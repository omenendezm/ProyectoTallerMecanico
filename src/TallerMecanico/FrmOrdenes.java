package TallerMecanico;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FrmOrdenes extends JFrame {

    JLabel lblTitulo = new JLabel("ÓRDENES DE TRABAJO");

    JLabel lblNueva = new JLabel("NUEVA ORDEN");
    JLabel lblCliente = new JLabel("Cliente:");
    JComboBox<Cliente> cmbCliente = new JComboBox<>();
    JButton btnNuevoCliente = new JButton("Nuevo cliente");
    JLabel lblVehiculo = new JLabel("Vehículo:");
    JComboBox<Vehiculo> cmbVehiculo = new JComboBox<>();
    JButton btnNuevoVehiculo = new JButton("Nuevo vehículo");
    JLabel lblMecanico = new JLabel("Mecánico:");
    JComboBox<Mecanico> cmbMecanico = new JComboBox<>();
    JLabel lblProblema = new JLabel("Problema:");
    JTextArea txtProblema = new JTextArea();
    JScrollPane scrollProblema = new JScrollPane(txtProblema);
    JButton btnCrear = new JButton("Crear orden");

    JLabel lblListado = new JLabel("ÓRDENES REGISTRADAS");
    JLabel lblFiltro = new JLabel("Estado:");
    JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{
        "TODAS", OrdenTrabajo.PENDIENTE, OrdenTrabajo.EN_PROCESO, OrdenTrabajo.FINALIZADA, OrdenTrabajo.CANCELADA
    });
    String[] columnasOrdenes = {"No.", "Placa", "Cliente", "Mecánico", "Estado", "Total"};
    DefaultTableModel modeloOrdenes = crearModelo(columnasOrdenes);
    JTable tablaOrdenes = new JTable(modeloOrdenes);
    JScrollPane scrollOrdenes = new JScrollPane(tablaOrdenes);

    JLabel lblOrden = new JLabel("Selecciona una orden de la tabla.");
    JButton btnIniciar = new JButton("Iniciar reparación");
    JButton btnFinalizar = new JButton("Finalizar");
    JButton btnCancelar = new JButton("Cancelar orden");
    JButton btnComprobante = new JButton("Ver comprobante");

    JLabel lblServicios = new JLabel("SERVICIOS");
    JComboBox<Servicio> cmbServicio = new JComboBox<>();
    JButton btnAgregarServicio = new JButton("Agregar");
    JButton btnQuitarServicio = new JButton("Quitar");
    String[] columnasServicios = {"Servicio", "Costo"};
    DefaultTableModel modeloServicios = crearModelo(columnasServicios);
    JTable tablaServicios = new JTable(modeloServicios);
    JScrollPane scrollServicios = new JScrollPane(tablaServicios);

    JLabel lblRepuestos = new JLabel("REPUESTOS");
    JComboBox<Repuesto> cmbRepuesto = new JComboBox<>();
    JLabel lblCantidad = new JLabel("Cant:");
    JTextField txtCantidad = new JTextField("1");
    JButton btnAgregarRepuesto = new JButton("Agregar");
    JButton btnQuitarRepuesto = new JButton("Quitar");
    String[] columnasRepuestos = {"Repuesto", "Cant.", "Precio", "Subtotal"};
    DefaultTableModel modeloRepuestos = crearModelo(columnasRepuestos);
    JTable tablaRepuestos = new JTable(modeloRepuestos);
    JScrollPane scrollRepuestos = new JScrollPane(tablaRepuestos);

    JLabel lblTotalManoObra = new JLabel("Mano de obra: Q 0.00");
    JLabel lblTotalRepuestos = new JLabel("Repuestos: Q 0.00");
    JLabel lblTotal = new JLabel("TOTAL: Q 0.00");

    GestorOrdenes gestorOrdenes = new GestorOrdenes();
    GestorClientes gestorClientes = new GestorClientes();
    GestorVehiculos gestorVehiculos = new GestorVehiculos();
    GestorMecanicos gestorMecanicos = new GestorMecanicos();
    GestorServicios gestorServicios = new GestorServicios();
    GestorRepuestos gestorRepuestos = new GestorRepuestos();

    List<OrdenTrabajo> listaOrdenes = new ArrayList<>();
    OrdenTrabajo ordenActual = null;
    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    public FrmOrdenes() {
        setTitle("Órdenes de trabajo");
        setSize(1005, 660);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(20, 10, 400, 28);
        add(lblTitulo);

        Font negrita = new Font("Arial", Font.BOLD, 13);

        lblNueva.setFont(negrita);
        lblNueva.setBounds(20, 45, 200, 22);
        lblCliente.setBounds(20, 75, 75, 26);
        cmbCliente.setBounds(95, 75, 230, 26);
        btnNuevoCliente.setBounds(330, 75, 140, 26);
        lblVehiculo.setBounds(20, 110, 75, 26);
        cmbVehiculo.setBounds(95, 110, 230, 26);
        btnNuevoVehiculo.setBounds(330, 110, 140, 26);
        lblMecanico.setBounds(20, 145, 75, 26);
        cmbMecanico.setBounds(95, 145, 230, 26);
        lblProblema.setBounds(20, 180, 75, 26);
        txtProblema.setLineWrap(true);
        txtProblema.setWrapStyleWord(true);
        scrollProblema.setBounds(95, 180, 375, 50);
        btnCrear.setBounds(95, 240, 150, 30);
        add(lblNueva);
        add(lblCliente);
        add(cmbCliente);
        add(btnNuevoCliente);
        add(lblVehiculo);
        add(cmbVehiculo);
        add(btnNuevoVehiculo);
        add(lblMecanico);
        add(cmbMecanico);
        add(lblProblema);
        add(scrollProblema);
        add(btnCrear);

        lblListado.setFont(negrita);
        lblListado.setBounds(490, 45, 250, 22);
        lblFiltro.setBounds(760, 45, 50, 24);
        cmbFiltro.setBounds(810, 45, 160, 24);
        scrollOrdenes.setBounds(490, 75, 480, 195);
        add(lblListado);
        add(lblFiltro);
        add(cmbFiltro);
        add(scrollOrdenes);
        ajustarAnchos(tablaOrdenes, new int[]{40, 75, 120, 110, 80, 55});

        lblOrden.setFont(negrita);
        lblOrden.setBounds(20, 283, 950, 24);
        btnIniciar.setBounds(20, 315, 170, 30);
        btnFinalizar.setBounds(200, 315, 130, 30);
        btnCancelar.setBounds(340, 315, 140, 30);
        btnComprobante.setBounds(490, 315, 150, 30);
        add(lblOrden);
        add(btnIniciar);
        add(btnFinalizar);
        add(btnCancelar);
        add(btnComprobante);

        lblServicios.setFont(negrita);
        lblServicios.setBounds(20, 358, 200, 22);
        cmbServicio.setBounds(20, 385, 230, 26);
        btnAgregarServicio.setBounds(255, 385, 95, 26);
        btnQuitarServicio.setBounds(355, 385, 95, 26);
        scrollServicios.setBounds(20, 420, 450, 150);
        add(lblServicios);
        add(cmbServicio);
        add(btnAgregarServicio);
        add(btnQuitarServicio);
        add(scrollServicios);
        ajustarAnchos(tablaServicios, new int[]{330, 120});

        lblRepuestos.setFont(negrita);
        lblRepuestos.setBounds(490, 358, 200, 22);
        cmbRepuesto.setBounds(490, 385, 200, 26);
        lblCantidad.setBounds(697, 385, 38, 26);
        txtCantidad.setBounds(735, 385, 40, 26);
        btnAgregarRepuesto.setBounds(780, 385, 95, 26);
        btnQuitarRepuesto.setBounds(880, 385, 90, 26);
        scrollRepuestos.setBounds(490, 420, 480, 150);
        add(lblRepuestos);
        add(cmbRepuesto);
        add(lblCantidad);
        add(txtCantidad);
        add(btnAgregarRepuesto);
        add(btnQuitarRepuesto);
        add(scrollRepuestos);
        ajustarAnchos(tablaRepuestos, new int[]{200, 70, 100, 110});

        lblTotalManoObra.setBounds(20, 582, 250, 24);
        lblTotalRepuestos.setBounds(280, 582, 250, 24);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setBounds(740, 580, 230, 26);
        add(lblTotalManoObra);
        add(lblTotalRepuestos);
        add(lblTotal);

        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaOrdenes.getSelectedRow() != -1) {
                mostrarOrden(listaOrdenes.get(tablaOrdenes.getSelectedRow()).getId());
            }
        });

        cmbCliente.addActionListener(e -> cargarVehiculos());
        cmbFiltro.addActionListener(e -> cargarOrdenes(-1));
        btnNuevoCliente.addActionListener(e -> abrirVentana(new FrmClientes()));
        btnNuevoVehiculo.addActionListener(e -> abrirVentana(new FrmVehiculos()));
        btnCrear.addActionListener(e -> crearOrden());
        btnIniciar.addActionListener(e -> iniciar());
        btnFinalizar.addActionListener(e -> finalizar());
        btnCancelar.addActionListener(e -> cancelar());
        btnComprobante.addActionListener(e -> verComprobante());
        btnAgregarServicio.addActionListener(e -> agregarServicio());
        btnQuitarServicio.addActionListener(e -> quitarServicio());
        btnAgregarRepuesto.addActionListener(e -> agregarRepuesto());
        btnQuitarRepuesto.addActionListener(e -> quitarRepuesto());

        cargarCombos();
        cargarOrdenes(-1);
    }
        private static DefaultTableModel crearModelo(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void ajustarAnchos(JTable tabla, int[] anchos) {
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private String dinero(double valor) {
        return String.format("Q %.2f", valor);
    }

    private void abrirVentana(JFrame ventana) {
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarCombos();
            }
        });
        ventana.setVisible(true);
    }

    private void cargarCombos() {
        try {
            Cliente clienteActual = (Cliente) cmbCliente.getSelectedItem();
            int idClienteActual = clienteActual == null ? -1 : clienteActual.getId();

            cmbCliente.removeAllItems();
            for (Cliente c : gestorClientes.listarTodos()) {
                cmbCliente.addItem(c);
                if (c.getId() == idClienteActual) {
                    cmbCliente.setSelectedItem(c);
                }
            }

            cmbMecanico.removeAllItems();
            for (Mecanico m : gestorMecanicos.listarTodos()) {
                cmbMecanico.addItem(m);
            }

            cmbServicio.removeAllItems();
            for (Servicio s : gestorServicios.listarTodos()) {
                cmbServicio.addItem(s);
            }

            cargarRepuestosDisponibles();
            cargarVehiculos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los datos: " + ex.getMessage());
        }
    }

    private void cargarRepuestosDisponibles() throws SQLException {
        cmbRepuesto.removeAllItems();
        for (Repuesto r : gestorRepuestos.listarDisponibles()) {
            cmbRepuesto.addItem(r);
        }
    }

    private void cargarVehiculos() {
        cmbVehiculo.removeAllItems();
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        if (cliente == null) {
            return;
        }
        try {
            for (Vehiculo v : gestorVehiculos.listarPorCliente(cliente.getId())) {
                cmbVehiculo.addItem(v);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los vehículos: " + ex.getMessage());
        }
    }

    private boolean cargarOrdenes(int idSeleccionar) {
        try {
            String filtro = (String) cmbFiltro.getSelectedItem();
            listaOrdenes = gestorOrdenes.listar("TODAS".equals(filtro) ? null : filtro);
            modeloOrdenes.setRowCount(0);
            int filaSeleccionar = -1;
            for (int i = 0; i < listaOrdenes.size(); i++) {
                OrdenTrabajo o = listaOrdenes.get(i);
                modeloOrdenes.addRow(new Object[]{
                    o.getId(), o.getPlaca(), o.getNombreCliente(), o.getNombreMecanico(),
                    o.getEstado(), String.format("%.2f", o.getTotalRegistrado())
                });
                if (o.getId() == idSeleccionar) {
                    filaSeleccionar = i;
                }
            }
            if (filaSeleccionar != -1) {
                tablaOrdenes.setRowSelectionInterval(filaSeleccionar, filaSeleccionar);
                tablaOrdenes.scrollRectToVisible(tablaOrdenes.getCellRect(filaSeleccionar, 0, true));
                return true;
            }
            limpiarDetalle();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las órdenes: " + ex.getMessage());
        }
        return false;
    }

    private void mostrarOrden(int idOrden) {
        try {
            ordenActual = gestorOrdenes.buscar(idOrden);
            if (ordenActual == null) {
                limpiarDetalle();
                return;
            }
            lblOrden.setText("Orden No. " + ordenActual.getId()
                + "  |  " + ordenActual.getPlaca() + " " + ordenActual.getVehiculo()
                + "  |  Ingreso: " + formatoFecha.format(ordenActual.getFechaIngreso())
                + "  |  Estado: " + ordenActual.getEstado()
                + "  |  " + ordenActual.getDescripcionProblema());

            modeloServicios.setRowCount(0);
            for (DetalleServicio s : ordenActual.getServicios()) {
                modeloServicios.addRow(new Object[]{s.getDescripcion(), String.format("%.2f", s.getSubtotal())});
            }

            modeloRepuestos.setRowCount(0);
            for (DetalleRepuesto r : ordenActual.getRepuestos()) {
                modeloRepuestos.addRow(new Object[]{
                    r.getNombre(), r.getCantidad(),
                    String.format("%.2f", r.getPrecioUnitario()), String.format("%.2f", r.getSubtotal())
                });
            }

            lblTotalManoObra.setText("Mano de obra: " + dinero(ordenActual.calcularTotalManoObra()));
            lblTotalRepuestos.setText("Repuestos: " + dinero(ordenActual.calcularTotalRepuestos()));
            lblTotal.setText("TOTAL: " + dinero(ordenActual.calcularTotal()));
            actualizarBotones();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la orden: " + ex.getMessage());
        }
    }

    private void limpiarDetalle() {
        ordenActual = null;
        tablaOrdenes.clearSelection();
        lblOrden.setText("Selecciona una orden de la tabla.");
        modeloServicios.setRowCount(0);
        modeloRepuestos.setRowCount(0);
        lblTotalManoObra.setText("Mano de obra: Q 0.00");
        lblTotalRepuestos.setText("Repuestos: Q 0.00");
        lblTotal.setText("TOTAL: Q 0.00");
        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean hayOrden = ordenActual != null;
        boolean enProceso = hayOrden && ordenActual.puedeModificarse();
        btnIniciar.setEnabled(hayOrden && OrdenTrabajo.PENDIENTE.equals(ordenActual.getEstado()));
        btnFinalizar.setEnabled(enProceso);
        btnCancelar.setEnabled(hayOrden && ordenActual.puedeCancelarse());
        btnComprobante.setEnabled(hayOrden && OrdenTrabajo.FINALIZADA.equals(ordenActual.getEstado()));
        btnAgregarServicio.setEnabled(enProceso);
        btnQuitarServicio.setEnabled(enProceso);
        btnAgregarRepuesto.setEnabled(enProceso);
        btnQuitarRepuesto.setEnabled(enProceso);
    }

    private void recargar() {
        int id = ordenActual == null ? -1 : ordenActual.getId();
        try {
            cargarRepuestosDisponibles();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los repuestos: " + ex.getMessage());
        }
        if (!cargarOrdenes(id) && id != -1) {
            mostrarOrden(id);
        }
    }
        private void crearOrden() {
        Vehiculo vehiculo = (Vehiculo) cmbVehiculo.getSelectedItem();
        Mecanico mecanico = (Mecanico) cmbMecanico.getSelectedItem();
        String problema = txtProblema.getText().trim();

        if (vehiculo == null) {
            JOptionPane.showMessageDialog(this, "El cliente no tiene vehículos. Usa \"Nuevo vehículo\" para registrarlo.");
            return;
        }
        if (mecanico == null) {
            JOptionPane.showMessageDialog(this, "Primero registra un mecánico.");
            return;
        }
        if (problema.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la descripción del problema.");
            txtProblema.requestFocus();
            return;
        }
        if (problema.length() > 400) {
            JOptionPane.showMessageDialog(this, "La descripción no puede pasar de 400 caracteres.");
            return;
        }
        try {
            OrdenTrabajo orden = new OrdenTrabajo(vehiculo.getId(), mecanico.getId(), problema);
            int idNuevo = gestorOrdenes.crear(orden);
            txtProblema.setText("");
            cmbFiltro.setSelectedIndex(0);
            cargarOrdenes(idNuevo);
            JOptionPane.showMessageDialog(this, "Orden No. " + idNuevo + " creada en estado PENDIENTE.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void iniciar() {
        try {
            gestorOrdenes.iniciar(ordenActual.getId());
            recargar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void agregarServicio() {
        Servicio servicio = (Servicio) cmbServicio.getSelectedItem();
        if (servicio == null) {
            JOptionPane.showMessageDialog(this, "No hay servicios registrados.");
            return;
        }
        try {
            gestorOrdenes.agregarServicio(ordenActual.getId(), servicio);
            recargar();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1) {
                JOptionPane.showMessageDialog(this, "Ese servicio ya está en la orden.");
            } else {
                JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
            }
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void quitarServicio() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona el servicio que deseas quitar.");
            return;
        }
        try {
            DetalleServicio detalle = ordenActual.getServicios().get(fila);
            gestorOrdenes.quitarServicio(ordenActual.getId(), detalle.getId());
            recargar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void agregarRepuesto() {
        Repuesto repuesto = (Repuesto) cmbRepuesto.getSelectedItem();
        if (repuesto == null) {
            JOptionPane.showMessageDialog(this, "No hay repuestos con existencia disponible.");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.");
            txtCantidad.requestFocus();
            return;
        }
        try {
            gestorOrdenes.agregarRepuesto(ordenActual.getId(), repuesto.getId(), cantidad);
            txtCantidad.setText("1");
            recargar();
        } catch (InventarioInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Inventario insuficiente", JOptionPane.WARNING_MESSAGE);
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void quitarRepuesto() {
        int fila = tablaRepuestos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona el repuesto que deseas quitar.");
            return;
        }
        DetalleRepuesto detalle = ordenActual.getRepuestos().get(fila);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Se devolverán " + detalle.getCantidad() + " unidades de " + detalle.getNombre() + " al inventario. ¿Continuar?",
                "Quitar repuesto", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            gestorOrdenes.quitarRepuesto(ordenActual.getId(), detalle.getId());
            recargar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void finalizar() {
        if (ordenActual.getServicios().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se puede finalizar una orden sin servicios registrados.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Total a pagar: " + dinero(ordenActual.calcularTotal()) + "\n¿Confirmas la finalización de la orden?",
                "Finalizar orden", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            gestorOrdenes.finalizar(ordenActual.getId());
            recargar();
            verComprobante();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void cancelar() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Se cancelará la orden No. " + ordenActual.getId()
                + " y se reintegrarán sus repuestos al inventario.\n¿Confirmas la cancelación?",
                "Cancelar orden", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int unidades = gestorOrdenes.cancelar(ordenActual.getId());
            recargar();
            JOptionPane.showMessageDialog(this, "Orden cancelada. Unidades reintegradas al inventario: " + unidades);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        } catch (TallerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void verComprobante() {
        if (ordenActual == null) {
            return;
        }
        JTextArea areaComprobante = new JTextArea(ordenActual.generarComprobante());
        areaComprobante.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaComprobante.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaComprobante);
        scroll.setPreferredSize(new Dimension(580, 420));
        JOptionPane.showMessageDialog(this, scroll, "Comprobante - Orden No. " + ordenActual.getId(),
                JOptionPane.PLAIN_MESSAGE);
    }
}