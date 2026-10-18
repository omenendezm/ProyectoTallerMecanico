package TallerMecanico;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdenTrabajo {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String EN_PROCESO = "EN PROCESO";
    public static final String FINALIZADA = "FINALIZADA";
    public static final String CANCELADA = "CANCELADA";

    private int id = -1;
    private int idVehiculo;
    private String placa;
    private String vehiculo;
    private String nombreCliente;
    private int idMecanico;
    private String nombreMecanico;
    private String descripcionProblema;
    private String estado = PENDIENTE;
    private Date fechaIngreso;
    private Date fechaFinalizacion;
    private double totalRegistrado;

    private final ArrayList<DetalleServicio> servicios = new ArrayList<>();
    private final ArrayList<DetalleRepuesto> repuestos = new ArrayList<>();

    public OrdenTrabajo() {
    }

    public OrdenTrabajo(int idVehiculo, int idMecanico, String descripcionProblema) {
        this.idVehiculo = idVehiculo;
        this.idMecanico = idMecanico;
        this.descripcionProblema = descripcionProblema;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getVehiculo() { return vehiculo; }
    public void setVehiculo(String vehiculo) { this.vehiculo = vehiculo; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public int getIdMecanico() { return idMecanico; }
    public void setIdMecanico(int idMecanico) { this.idMecanico = idMecanico; }

    public String getNombreMecanico() { return nombreMecanico; }
    public void setNombreMecanico(String nombreMecanico) { this.nombreMecanico = nombreMecanico; }

    public String getDescripcionProblema() { return descripcionProblema; }
    public void setDescripcionProblema(String descripcionProblema) { this.descripcionProblema = descripcionProblema; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Date getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(Date fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public double getTotalRegistrado() { return totalRegistrado; }
    public void setTotalRegistrado(double totalRegistrado) { this.totalRegistrado = totalRegistrado; }

    public List<DetalleServicio> getServicios() { return servicios; }
    public List<DetalleRepuesto> getRepuestos() { return repuestos; }

    public void agregarServicio(DetalleServicio detalle) {
        servicios.add(detalle);
    }

    public void agregarRepuesto(DetalleRepuesto detalle) {
        repuestos.add(detalle);
    }

    public List<Detalle> getDetalles() {
        List<Detalle> detalles = new ArrayList<>();
        detalles.addAll(servicios);
        detalles.addAll(repuestos);
        return detalles;
    }

    public boolean puedeModificarse() {
        return EN_PROCESO.equals(estado);
    }

    public boolean puedeCancelarse() {
        return PENDIENTE.equals(estado) || EN_PROCESO.equals(estado);
    }

    public double calcularTotalManoObra() {
        double total = 0;
        for (DetalleServicio s : servicios) {
            total += s.getSubtotal();
        }
        return total;
    }

    public double calcularTotalRepuestos() {
        double total = 0;
        for (DetalleRepuesto r : repuestos) {
            total += r.getSubtotal();
        }
        return total;
    }

    public double calcularTotal() {
        return calcularTotalManoObra() + calcularTotalRepuestos();
    }

    public String generarComprobante() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String separador = linea('=', 62);
        String division = linea('-', 62);
        StringBuilder sb = new StringBuilder();

        sb.append(separador).append("\n");
        sb.append(centrar("TALLER EL ÚLTIMO ARRANQUE", 62)).append("\n");
        sb.append(centrar("COMPROBANTE DE REPARACIÓN", 62)).append("\n");
        sb.append(separador).append("\n");
        sb.append("Orden No.     : ").append(id).append("\n");
        sb.append("Fecha ingreso : ").append(fechaIngreso == null ? "" : formato.format(fechaIngreso)).append("\n");
        sb.append("Finalización  : ").append(fechaFinalizacion == null ? "" : formato.format(fechaFinalizacion)).append("\n");
        sb.append("Cliente       : ").append(nombreCliente).append("\n");
        sb.append("Vehículo      : ").append(placa).append(" - ").append(vehiculo).append("\n");
        sb.append("Mecánico      : ").append(nombreMecanico).append("\n");
        sb.append("Problema      : ").append(descripcionProblema).append("\n");
        sb.append("Estado        : ").append(estado).append("\n");
        sb.append(division).append("\n");
        sb.append(String.format("%-9s %-26s %5s %9s %9s%n", "Tipo", "Descripción", "Cant.", "Precio", "Subtotal"));
        sb.append(division).append("\n");
        for (Detalle d : getDetalles()) {
            sb.append(String.format("%-9s %-26s %5d %9.2f %9.2f%n",
                    d.getTipo(), recortar(d.getDescripcion(), 26), d.getCantidad(),
                    d.getPrecioUnitario(), d.getSubtotal()));
        }
        sb.append(division).append("\n");
        sb.append(String.format("%-46s Q %13.2f%n", "Total mano de obra:", calcularTotalManoObra()));
        sb.append(String.format("%-46s Q %13.2f%n", "Total repuestos:", calcularTotalRepuestos()));
        sb.append(separador).append("\n");
        sb.append(String.format("%-46s Q %13.2f%n", "TOTAL A PAGAR:", calcularTotal()));
        sb.append(separador).append("\n");
        return sb.toString();
    }

    private String linea(char caracter, int largo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < largo; i++) {
            sb.append(caracter);
        }
        return sb.toString();
    }

    private String centrar(String texto, int largo) {
        int espacios = Math.max(0, (largo - texto.length()) / 2);
        return linea(' ', espacios) + texto;
    }

    private String recortar(String texto, int largo) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= largo ? texto : texto.substring(0, largo - 1) + ".";
    }
}