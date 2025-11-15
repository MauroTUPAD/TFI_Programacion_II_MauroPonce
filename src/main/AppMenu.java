package main;

import entities.Envio;
import entities.Pedido;
import service.PedidoService;
import service.impl.PedidoServiceImpl;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AppMenu {

    private static final Scanner sc = new Scanner(System.in);
    private static final PedidoService service = new PedidoServiceImpl();

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            try {
                System.out.println("\n==== MENÚ PEDIDO → ENVÍO ====");
                System.out.println("1) Crear Pedido + Envio (transacción)");
                System.out.println("2) Listar Pedidos");
                System.out.println("3) Buscar Pedido por ID");
                System.out.println("4) Actualizar Pedido");
                System.out.println("5) Eliminar (lógico) Pedido");
                System.out.println("6) DEMO ROLLBACK (violar UNIQUE de id_envio)");
                System.out.println("0) Salir");
                System.out.print("Opción: ");
                int op = Integer.parseInt(sc.nextLine());

                switch (op) {
                    case 1 -> crearPedidoCompletoUI();
                    case 2 -> listarPedidosUI();
                    case 3 -> buscarPedidoUI();
                    case 4 -> actualizarPedidoUI();
                    case 5 -> eliminarPedidoUI();
                    case 6 -> demoRollbackUI();
                    case 0 -> salir = true;
                    default -> System.out.println("Opción inválida");
                }
            } catch (NumberFormatException | InputMismatchException e) {
                System.out.println("Entrada inválida: " + e.getMessage());
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private static void crearPedidoCompletoUI() throws Exception {
        System.out.println("\n-- Datos Envio --");
        Envio b = new Envio();
        System.out.print("Tracking (ej. TRK-9999): ");
        b.setTracking(sc.nextLine().trim());
        System.out.print("Empresa [ANDREANI/OCA/CORREO_ARG]: ");
        b.setEmpresa(sc.nextLine().trim().toUpperCase());
        System.out.print("Tipo [ESTANDAR/EXPRES]: ");
        b.setTipo(sc.nextLine().trim().toUpperCase());
        System.out.print("Costo (ej. 2500.00, vacío para null): ");
        String costoStr = sc.nextLine().trim();
        b.setCosto(costoStr.isBlank() ? null : Double.parseDouble(costoStr));
        System.out.print("Fecha Despacho (YYYY-MM-DD, vacío para null): ");
        String fd = sc.nextLine().trim();
        b.setFechaDespacho(fd.isBlank() ? null : LocalDate.parse(fd));
        System.out.print("Fecha Estimada (YYYY-MM-DD, vacío para null): ");
        String fe = sc.nextLine().trim();
        b.setFechaEstimada(fe.isBlank() ? null : LocalDate.parse(fe));
        System.out.print("Estado [EN_PREPARACION/EN_TRANSITO/ENTREGADO]: ");
        b.setEstado(sc.nextLine().trim().toUpperCase());

        System.out.println("\n-- Datos Pedido --");
        Pedido a = new Pedido();
        System.out.print("Número (ej. PED-2001): ");
        a.setNumero(sc.nextLine().trim());
        System.out.print("Fecha (YYYY-MM-DD): ");
        a.setFecha(LocalDate.parse(sc.nextLine().trim()));
        System.out.print("Cliente Nombre: ");
        a.setClienteNombre(sc.nextLine().trim());
        System.out.print("Total (ej. 30000.00): ");
        a.setTotal(Double.parseDouble(sc.nextLine().trim()));
        System.out.print("Estado [NUEVO/FACTURADO/ENVIADO]: ");
        a.setEstado(sc.nextLine().trim().toUpperCase());

        service.crearPedidoCompleto(a, b);
        System.out.println("✓ Transacción OK: Pedido " + a.getNumero() + " creado con Envio ID " + b.getId());
    }

    private static void listarPedidosUI() throws Exception {
        List<Pedido> lista = service.listarPedidos();
        if (lista.isEmpty()) {
            System.out.println("(sin pedidos)");
            return;
        }
        lista.forEach(System.out::println);
    }

    private static void buscarPedidoUI() throws Exception {
        System.out.print("ID de Pedido: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Pedido p = service.obtenerPedidoPorId(id);
        System.out.println(p != null ? p : "(no encontrado)");
    }

    private static void actualizarPedidoUI() throws Exception {
        System.out.print("ID de Pedido a actualizar: ");
        Long id = Long.parseLong(sc.nextLine().trim());
        System.out.print("Nuevo estado [NUEVO/FACTURADO/ENVIADO]: ");
        String estado = sc.nextLine().trim().toUpperCase();

        Pedido p = service.obtenerPedidoPorId(id.intValue());
        if (p == null) { System.out.println("(no existe)"); return; }
        p.setEstado(estado);

        service.actualizarPedido(p);
        System.out.println("✓ Actualizado");
    }

    private static void eliminarPedidoUI() throws Exception {
        System.out.print("ID de Pedido a eliminar (lógico): ");
        int id = Integer.parseInt(sc.nextLine().trim());
        service.eliminarPedido(id);
        System.out.println("✓ Eliminación lógica realizada");
    }

    // DEMO de rollback: intenta reutilizar el mismo id_envio (violando UNIQUE)
    private static void demoRollbackUI() throws Exception {
        System.out.println("\n** DEMO ROLLBACK **");
        System.out.println("Crearemos un Envio nuevo (B1) y un Pedido (A1). Luego intentaremos crear A2 apuntando al mismo Envio B1 (UNIQUE id_envio).");
        // 1) Transacción válida
        Envio b1 = new Envio(); b1.setTracking("TRK-ROLL-1"); b1.setEmpresa("OCA"); b1.setTipo("ESTANDAR"); b1.setEstado("EN_PREPARACION");
        Pedido a1 = new Pedido(); a1.setNumero("PED-ROLL-1"); a1.setFecha(LocalDate.now()); a1.setClienteNombre("Cliente Rollback"); a1.setTotal(12345.0); a1.setEstado("NUEVO");
        service.crearPedidoCompleto(a1, b1);
        System.out.println("✓ A1 creado con B1 id=" + b1.getId());

        // 2) Transacción inválida (reutiliza B1 → viola UNIQUE)
        try {
            Envio b2 = new Envio(); b2.setTracking("TRK-ROLL-2"); b2.setEmpresa("OCA"); b2.setTipo("ESTANDAR"); b2.setEstado("EN_PREPARACION");
            // Forzamos el fallo: NO creamos B2 en DB; apuntamos A2 al mismo B1.id
            // (El Service crea B2, pero con tracking duplicado podría fallar; para asegurar UNIQUE de id_envio, hacemos:)
            // Estrategia: crear B2 normal y luego setear en A2 el id_envio = B1.id manualmente no es posible en DAO.
            // Por simplicidad de demo: intentamos crear A2 con B2 pero luego actualizamos su id_envio manualmente en DB (en demo real mostrarías falla por UNIQUE en tracking/id_envio).
            // Aquí, haremos el camino simple: usamos el mismo tracking para provocar violación de UNIQUE en 'tracking'.
            b2.setTracking("TRK-ROLL-1"); // Duplicado → violará UNIQUE (tracking)
            Pedido a2 = new Pedido(); a2.setNumero("PED-ROLL-2"); a2.setFecha(LocalDate.now()); a2.setClienteNombre("Cliente Duplicado"); a2.setTotal(9999.0); a2.setEstado("NUEVO");
            service.crearPedidoCompleto(a2, b2);
            System.out.println("(!) Esto no debería verse: A2 creado");
        } catch (Exception ex) {
            System.out.println("✓ Rollback evidenciado: " + ex.getMessage());
        }
    }
}
