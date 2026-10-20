package TallerMecanico;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FrmReportes extends JFrame {

    static final String[] REPORTES = {
        "01 - Órdenes por estado",
        "02 - Reparaciones por vehículo",
        "03 - Repuestos con bajo inventario",
        "04 - Reparaciones por mecánico",
        "05 - Ingresos por reparaciones"
    };

    JLabel lblTitulo = new JLabel("REPORTES");
    JLabel lblReporte = new JLabel("Reporte:");
    JComboBox<String> cmbReporte = new JComboBox<>(REPORTES);
    JButton btnGenerar = new JButton("Generar");
    JButton btnCsv = new JButton("Exportar CSV");
    JButton btnHtml = new JButton("Exportar HTML");

    JLabel lblEstado = new JLabel("Estado:");
    JComboBox<String> cmbEstado = new JComboBox<>(new String[]{
        "TODAS", OrdenTrabajo.PENDIENTE, OrdenTrabajo.EN_PROCESO, OrdenTrabajo.FINALIZADA, OrdenTrabajo.CANCELADA
    });
    JLabel lblPlaca = new JLabel("Placa:");
    JTextField txtPlaca = new JTextField();
    JLabel lblMecanico = new JLabel("Mecánico:");
    JComboBox<Mecanico> cmbMecanico = new JComboBox<>();
    JLabel lblDesde = new JLabel("Desde:");
    JTextField txtDesde = new JTextField();
    JLabel lblHasta = new JLabel("Hasta:");
    JTextField txtHasta = new JTextField();
    JLabel lblFormato = new JLabel("(dd/mm/aaaa)");

    JTable tablaReporte = new JTable(new DefaultTableModel());
    JScrollPane scrollTabla = new JScrollPane(tablaReporte);
    JLabel lblResumen = new JLabel(" ");

    GestorReportes gestorReportes = new GestorReportes();
    GestorMecanicos gestorMecanicos = new GestorMecanicos();
    GestorArchivos gestorArchivos = new GestorArchivos();
    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    String tituloGenerado = "";

    public FrmReportes() {
        setTitle("Reportes");
        setSize(900, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(30, 15, 300, 30);
        add(lblTitulo);

        lblReporte.setBounds(30, 60, 80, 26);
        cmbReporte.setBounds(110, 60, 330, 26);
        btnGenerar.setBounds(460, 58, 120, 30);
        btnCsv.setBounds(590, 58, 130, 30);
        btnHtml.setBounds(730, 58, 130, 30);
        add(lblReporte);
        add(cmbReporte);
        add(btnGenerar);
        add(btnCsv);
        add(btnHtml);

        lblEstado.setBounds(30, 100, 80, 26);
        cmbEstado.setBounds(110, 100, 200, 26);
        lblPlaca.setBounds(30, 100, 80, 26);
        txtPlaca.setBounds(110, 100, 150, 26);
        lblMecanico.setBounds(30, 100, 80, 26);
        cmbMecanico.setBounds(110, 100, 260, 26);
        lblDesde.setBounds(30, 100, 80, 26);
        txtDesde.setBounds(110, 100, 110, 26);
        lblHasta.setBounds(240, 100, 50, 26);
        txtHasta.setBounds(290, 100, 110, 26);
        lblFormato.setBounds(410, 100, 120, 26);
        add(lblEstado);
        add(cmbEstado);
        add(lblPlaca);
        add(txtPlaca);
        add(lblMecanico);
        add(cmbMecanico);
        add(lblDesde);
        add(txtDesde);
        add(lblHasta);
        add(txtHasta);
        add(lblFormato);

        scrollTabla.setBounds(30, 145, 830, 300);
        add(scrollTabla);

        lblResumen.setFont(new Font("Arial", Font.BOLD, 13));
        lblResumen.setBounds(30, 455, 830, 24);
        add(lblResumen);

        formatoFecha.setLenient(false);
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(Calendar.DAY_OF_MONTH, 1);
        txtDesde.setText(formatoFecha.format(inicioMes.getTime()));
        txtHasta.setText(formatoFecha.format(new Date()));

        cargarMecanicos();
        mostrarParametros();

        cmbReporte.addActionListener(e -> {
            mostrarParametros();
            tablaReporte.setModel(new DefaultTableModel());
            lblResumen.setText(" ");
        });
        btnGenerar.addActionListener(e -> generar());
        btnCsv.addActionListener(e -> exportar(true));
        btnHtml.addActionListener(e -> exportar(false));
    }

    private void cargarMecanicos() {
        try {
            Mecanico todos = new Mecanico();
            todos.setNombre("Todos los");
            todos.setApellido("mecánicos");
            cmbMecanico.addItem(todos);
            for (Mecanico m : gestorMecanicos.listarTodos()) {
                cmbMecanico.addItem(m);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los mecánicos: " + ex.getMessage());
        }
    }

    private void mostrarParametros() {
        int reporte = cmbReporte.getSelectedIndex();
        lblEstado.setVisible(reporte == 0);
        cmbEstado.setVisible(reporte == 0);
        lblPlaca.setVisible(reporte == 1);
        txtPlaca.setVisible(reporte == 1);
        lblMecanico.setVisible(reporte == 3);
        cmbMecanico.setVisible(reporte == 3);
        lblDesde.setVisible(reporte == 4);
        txtDesde.setVisible(reporte == 4);
        lblHasta.setVisible(reporte == 4);
        txtHasta.setVisible(reporte == 4);
        lblFormato.setVisible(reporte == 4);
    }
        private void generar() {
        int reporte = cmbReporte.getSelectedIndex();
        try {
            DefaultTableModel modelo;
            String resumen;
            switch (reporte) {
                case 0: {
                    String estado = (String) cmbEstado.getSelectedItem();
                    modelo = gestorReportes.ordenesPorEstado("TODAS".equals(estado) ? null : estado);
                    resumen = "Órdenes encontradas: " + modelo.getRowCount();
                    tituloGenerado = "Órdenes por estado - " + estado;
                    break;
                }
                case 1: {
                    String placa = txtPlaca.getText().trim().toUpperCase();
                    if (placa.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ingresa la placa del vehículo.");
                        txtPlaca.requestFocus();
                        return;
                    }
                    modelo = gestorReportes.reparacionesPorVehiculo(placa);
                    resumen = modelo.getRowCount() == 0
                            ? "No hay reparaciones registradas para la placa " + placa + "."
                            : "Reparaciones del vehículo " + placa + ": " + modelo.getRowCount();
                    tituloGenerado = "Reparaciones del vehículo " + placa;
                    break;
                }
                case 2: {
                    modelo = gestorReportes.repuestosBajoInventario();
                    resumen = "Repuestos con existencia menor o igual a 5: " + modelo.getRowCount();
                    tituloGenerado = "Repuestos con bajo inventario";
                    break;
                }
                case 3: {
                    Mecanico mecanico = (Mecanico) cmbMecanico.getSelectedItem();
                    int idMecanico = mecanico == null ? -1 : mecanico.getId();
                    modelo = gestorReportes.reparacionesPorMecanico(idMecanico);
                    resumen = "Órdenes asignadas: " + modelo.getRowCount();
                    tituloGenerado = "Reparaciones por mecánico - " + mecanico;
                    break;
                }
                default: {
                    Date desde;
                    Date hasta;
                    try {
                        desde = formatoFecha.parse(txtDesde.getText().trim());
                        hasta = formatoFecha.parse(txtHasta.getText().trim());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(this, "Las fechas deben tener el formato dd/mm/aaaa.");
                        return;
                    }
                    if (desde.after(hasta)) {
                        JOptionPane.showMessageDialog(this, "La fecha inicial no puede ser mayor que la final.");
                        return;
                    }
                    modelo = gestorReportes.ingresosPorReparaciones(desde, hasta);
                    double[] totales = gestorReportes.totalesIngresos(desde, hasta);
                    resumen = String.format("Órdenes finalizadas: %d   |   Mano de obra: Q %.2f   |   Repuestos: Q %.2f   |   TOTAL: Q %.2f",
                            modelo.getRowCount(), totales[0], totales[1], totales[2]);
                    tituloGenerado = "Ingresos por reparaciones del " + txtDesde.getText().trim()
                            + " al " + txtHasta.getText().trim();
                    break;
                }
            }
            tablaReporte.setModel(modelo);
            lblResumen.setText(resumen);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + ex.getMessage());
        }
    }

    private void exportar(boolean csv) {
        if (tablaReporte.getModel().getColumnCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero genera un reporte.");
            return;
        }
        String extension = csv ? "csv" : "html";
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte");
        selector.setFileFilter(new FileNameExtensionFilter(csv ? "Archivo CSV (Excel)" : "Página HTML", extension));
        selector.setSelectedFile(new File("reporte_" + (cmbReporte.getSelectedIndex() + 1) + "." + extension));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith("." + extension)) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + "." + extension);
        }
        try {
            if (csv) {
                gestorArchivos.exportarCsv(tablaReporte.getModel(), archivo);
            } else {
                gestorArchivos.exportarHtml(tablaReporte.getModel(), tituloGenerado, archivo);
            }
            JOptionPane.showMessageDialog(this, "Reporte exportado en:\n" + archivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el archivo: " + ex.getMessage());
        }
    }
}