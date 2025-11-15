package entities;

import java.time.LocalDate;

public class Envio {
    private Long id;
    private boolean eliminado;
    private String tracking; // UNIQUE, máx. 40
    private String empresa;  // ANDREANI | OCA | CORREO_ARG
    private String tipo;     // ESTANDAR | EXPRES
    private Double costo;    // (10,2)
    private LocalDate fechaDespacho;
    private LocalDate fechaEstimada;
    private String estado;   // EN_PREPARACION | EN_TRANSITO | ENTREGADO

    public Envio() {}

    public Envio(Long id, boolean eliminado, String tracking, String empresa, String tipo,
                 Double costo, LocalDate fechaDespacho, LocalDate fechaEstimada, String estado) {
        this.id = id;
        this.eliminado = eliminado;
        this.tracking = tracking;
        this.empresa = empresa;
        this.tipo = tipo;
        this.costo = costo;
        this.fechaDespacho = fechaDespacho;
        this.fechaEstimada = fechaEstimada;
        this.estado = estado;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public LocalDate getFechaDespacho() { return fechaDespacho; }
    public void setFechaDespacho(LocalDate fechaDespacho) { this.fechaDespacho = fechaDespacho; }

    public LocalDate getFechaEstimada() { return fechaEstimada; }
    public void setFechaEstimada(LocalDate fechaEstimada) { this.fechaEstimada = fechaEstimada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Envio{id=" + id + ", tracking='" + tracking + "', empresa='" + empresa + "', tipo='" + tipo +
                "', costo=" + costo + ", fechaDespacho=" + fechaDespacho + ", fechaEstimada=" + fechaEstimada +
                ", estado='" + estado + "', eliminado=" + eliminado + "}";
    }
}
