package TallerMecanico;

public class Vehiculo {

    private int id = -1;
    private String placa;
    private int idCliente;
    private String nombreCliente;
    private String marca;
    private String modelo;
    private int anio;
    private String color;

    public Vehiculo() {
    }

    public Vehiculo(String placa, int idCliente, String marca, String modelo, int anio, String color) {
        this.placa = placa;
        this.idCliente = idCliente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return placa + " - " + marca + " " + modelo;
    }
}