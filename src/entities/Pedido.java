package entities;

import java.time.LocalDate;

public class Pedido {
    private Long id;
    private boolean eliminado;
    private String numero;     // UNIQUE, NOT NULL
    private LocalDate fecha;   // NOT NULL
    private String clienteNombre; // NOT NULL
    private Double total;      // NOT NULL (12,2)
    private String estado;     // NUEVO | FACTURADO | ENVIADO
    private Envio envio;       // Referencia 1→1 a B

    public Pedido() {}

    public Pedido(Long id, boolean eliminado, String numero, LocalDate fecha, String clienteNombre,
                  Double total, String estado, Envio envio) {
        this.id = id;
        this.eliminado = eliminado;
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = estado;
        this.envio = envio;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }

    @Override
    public String toString() {
        return "Pedido{id=" + id + ", numero='" + numero + "', fecha=" + fecha + ", clienteNombre='" + clienteNombre +
                "', total=" + total + ", estado='" + estado + "', envioId=" + (envio != null ? envio.getId() : null) +
                ", eliminado=" + eliminado + "}";
    }
}
