package TallerMecanico;

public class Repuesto {

    private int id = -1;
    private String nombre;
    private String descripcion;
    private double precio;
    private int existencia;

    public Repuesto() {
    }

    public Repuesto(String nombre, String descripcion, double precio, int existencia) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.existencia = existencia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }

    public boolean estaAgotado() {
        return existencia == 0;
    }

    public String getEstadoInventario() {
        if (existencia == 0) {
            return "AGOTADO";
        }
        if (existencia <= 5) {
            return "BAJO";
        }
        return "DISPONIBLE";
    }

    @Override
    public String toString() {
        return nombre + " (disp. " + existencia + ")";
    }
}