package TallerMecanico;

public abstract class Detalle {

    private int id = -1;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public abstract String getTipo();
    public abstract String getDescripcion();
    public abstract int getCantidad();
    public abstract double getPrecioUnitario();

    public double getSubtotal() {
        return getCantidad() * getPrecioUnitario();
    }
}