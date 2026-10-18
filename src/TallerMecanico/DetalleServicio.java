package TallerMecanico;

public class DetalleServicio extends Detalle {

    private int idServicio;
    private String descripcion;
    private double costo;

    public DetalleServicio() {
    }

    public DetalleServicio(int idServicio, String descripcion, double costo) {
        this.idServicio = idServicio;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    @Override
    public String getTipo() {
        return "Servicio";
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public int getCantidad() {
        return 1;
    }

    @Override
    public double getPrecioUnitario() {
        return costo;
    }
}