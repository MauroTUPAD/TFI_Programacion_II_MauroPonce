package service.impl;

import config.DatabaseConnection;
import dao.EnvioDAO;
import dao.PedidoDAO;
import dao.impl.EnvioDAOImpl;
import dao.impl.PedidoDAOImpl;
import entities.Envio;
import entities.Pedido;
import service.PedidoService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoServiceImpl implements PedidoService {

    private final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private final EnvioDAO envioDAO = new EnvioDAOImpl();

    @Override
    public void crearPedidoCompleto(Pedido a, Envio b) throws Exception {
        Connection conn = null;
        try {
            // Validaciones básicas
            if (a == null || b == null)
                throw new IllegalArgumentException("Pedido y Envio son obligatorios");
            if (a.getNumero() == null || a.getNumero().isBlank())
                throw new IllegalArgumentException("Número de pedido requerido");
            if (a.getFecha() == null)
                throw new IllegalArgumentException("Fecha de pedido requerida");
            if (a.getClienteNombre() == null || a.getClienteNombre().isBlank())
                throw new IllegalArgumentException("Cliente requerido");
            if (a.getTotal() == null)
                throw new IllegalArgumentException("Total requerido");

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 👈 acá va solo 'false'

            Integer idB = envioDAO.crear(b, conn);
            if (idB == null) throw new SQLException("No se pudo crear Envio");
            b.setId(idB.longValue());

            a.setEnvio(b);
            Integer idA = pedidoDAO.crear(a, conn);
            if (idA == null) throw new SQLException("No se pudo crear Pedido");

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            throw new Exception("Error en la transacción crearPedidoCompleto: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 👈 y acá solo 'true'
                    conn.close();
                } catch (SQLException ignore) {}
            }
        }
    }

    @Override
    public void actualizarPedido(Pedido a) throws Exception {
        Connection conn = null;
        try {
            if (a == null || a.getId() == null)
                throw new IllegalArgumentException("Pedido con ID requerido");

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            boolean ok = pedidoDAO.actualizar(a, conn);
            if (!ok) throw new SQLException("No se actualizó el pedido");

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            throw new Exception("Error actualizando pedido: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {}
            }
        }
    }

    @Override
    public void eliminarPedido(int idPedido) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            boolean ok = pedidoDAO.eliminarLogico(idPedido, conn);
            if (!ok) throw new SQLException("No se eliminó (lógico) el pedido");

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            throw new Exception("Error eliminando pedido: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {}
            }
        }
    }

    @Override
    public Pedido obtenerPedidoPorId(int idPedido) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pedidoDAO.buscarPorId(idPedido, conn);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> listarPedidos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pedidoDAO.listarTodos(conn);
        } catch (SQLException e) {
            throw new Exception("Error listando pedidos: " + e.getMessage(), e);
        }
    }

}