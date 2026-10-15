package TallerMecanico;

public class Cliente extends Persona {

    private String correo;
    private String direccion;

    public Cliente() {
    }

    public Cliente(String dpi, String nombre, String apellido, String telefono, String correo, String direccion) {
        super(dpi, nombre, apellido, telefono);
        this.correo = correo;
        this.direccion = direccion;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String mostrarInformacion() {
        return "Cliente: " + getNombreCompleto()
             + "\nDPI: " + getDpi()
             + "\nTeléfono: " + getTelefono()
             + "\nCorreo: " + correo
             + "\nDirección: " + direccion;
    }
}
