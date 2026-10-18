package TallerMecanico;

public class DetalleRepuesto extends Detalle {

    private int idRepuesto;
    private String nombre;
    private int cantidad;
    private double precioUnitario;

    public DetalleRepuesto() {
    }

    public DetalleRepuesto(int idRepuesto, String nombre, int cantidad, double precioUnitario) {
        this.idRepuesto = idRepuesto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdRepuesto() { return idRepuesto; }
    public void setIdRepuesto(int idRepuesto) { this.idRepuesto = idRepuesto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    @Override
    public String getTipo() {
        return "Repuesto";
    }

    @Override
    public String getDescripcion() {
        return nombre;
    }

    @Override
    public int getCantidad() {
        return cantidad;
    }

    @Override
    public double getPrecioUnitario() {
        return precioUnitario;
    }
}