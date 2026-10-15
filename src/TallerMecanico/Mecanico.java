package TallerMecanico;

public class Mecanico extends Persona {

    private String especialidad;

    public Mecanico() {
    }

    public Mecanico(String dpi, String nombre, String apellido, String telefono, String especialidad) {
        super(dpi, nombre, apellido, telefono);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    @Override
    public String mostrarInformacion() {
        return "Mecánico: " + getNombreCompleto()
             + "\nDPI: " + getDpi()
             + "\nTeléfono: " + getTelefono()
             + "\nEspecialidad: " + especialidad;
    }
}
