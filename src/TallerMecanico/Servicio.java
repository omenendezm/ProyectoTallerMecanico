package TallerMecanico;

public class Servicio {

    private int id = -1;
    private String descripcion;
    private double costoManoObra;

    public Servicio() {
    }

    public Servicio(String descripcion, double costoManoObra) {
        this.descripcion = descripcion;
        this.costoManoObra = costoManoObra;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getCostoManoObra() { return costoManoObra; }
    public void setCostoManoObra(double costoManoObra) { this.costoManoObra = costoManoObra; }

    @Override
    public String toString() {
        return descripcion + " - Q" + String.format("%.2f", costoManoObra);
    }
}