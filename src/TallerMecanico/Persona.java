package TallerMecanico;

public abstract class Persona {

    private int id = -1;
    private String dpi;
    private String nombre;
    private String apellido;
    private String telefono;

    public Persona() {
    }

    public Persona(String dpi, String nombre, String apellido, String telefono) {
        this.dpi = dpi;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public abstract String mostrarInformacion();

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}