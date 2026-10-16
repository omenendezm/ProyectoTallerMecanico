package TallerMecanico;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FrmVehiculos extends JFrame {

    JLabel lblTitulo = new JLabel("GESTIÓN DE VEHÍCULOS");
    JLabel lblPlaca = new JLabel("Placa:");
    JTextField txtPlaca = new JTextField();
    JLabel lblCliente = new JLabel("Cliente:");
    JComboBox<Cliente> cmbCliente = new JComboBox<>();
    JLabel lblAnio = new JLabel("Año:");
    JTextField txtAnio = new JTextField();
    JLabel lblMarca = new JLabel("Marca:");
    JTextField txtMarca = new JTextField();
    JLabel lblModelo = new JLabel("Modelo:");
    JTextField txtModelo = new JTextField();
    JLabel lblColor = new JLabel("Color:");
    JTextField txtColor = new JTextField();

    JButton btnGuardar = new JButton("Guardar");
    JButton btnEditar = new JButton("Editar");
    JButton btnEliminar = new JButton("Eliminar");
    JButton btnLimpiar = new JButton("Limpiar");
    JButton btnHistorial = new JButton("Ver historial");

    JLabel lblFiltro = new JLabel("Ver vehículos de:");
    JComboBox<Cliente> cmbFiltro = new JComboBox<>();

    String[] columnas = {"ID", "Placa", "Cliente", "Marca", "Modelo", "Año", "Color"};
    DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable tablaVehiculos = new JTable(modeloTabla);
    JScrollPane scrollTabla = new JScrollPane(tablaVehiculos);

    GestorVehiculos gestorVehiculos = new GestorVehiculos();
    GestorClientes gestorClientes = new GestorClientes();
    List<Vehiculo> listaVehiculos = new ArrayList<>();
    int idSeleccionado = -1;

    public FrmVehiculos() {
        setTitle("Vehículos");
        setSize(830, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(30, 15, 300, 30);
        add(lblTitulo);

        agregarCampo(lblPlaca, txtPlaca, 30, 60);
        lblCliente.setBounds(30, 100, 90, 26);
        cmbCliente.setBounds(120, 100, 230, 26);
        add(lblCliente);
        add(cmbCliente);
        agregarCampo(lblAnio, txtAnio, 30, 140);
        agregarCampo(lblMarca, txtMarca, 390, 60);
        agregarCampo(lblModelo, txtModelo, 390, 100);
        agregarCampo(lblColor, txtColor, 390, 140);

        btnGuardar.setBounds(30, 190, 120, 32);
        btnEditar.setBounds(160, 190, 120, 32);
        btnEliminar.setBounds(290, 190, 120, 32);
        btnLimpiar.setBounds(420, 190, 120, 32);
        btnHistorial.setBounds(550, 190, 140, 32);
        add(btnGuardar);
        add(btnEditar);
        add(btnEliminar);
        add(btnLimpiar);
        add(btnHistorial);

        lblFiltro.setBounds(30, 240, 120, 26);
        cmbFiltro.setBounds(150, 240, 260, 26);
        add(lblFiltro);
        add(cmbFiltro);

        scrollTabla.setBounds(30, 275, 760, 190);
        add(scrollTabla);

        int[] anchos = {50, 90, 200, 110, 110, 70, 130};
        for (int i = 0; i < anchos.length; i++) {
            tablaVehiculos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        tablaVehiculos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaVehiculos.getSelectedRow() != -1) {
                Vehiculo v = listaVehiculos.get(tablaVehiculos.getSelectedRow());
                idSeleccionado = v.getId();
                txtPlaca.setText(v.getPlaca());
                seleccionarCliente(v.getIdCliente());
                txtAnio.setText(String.valueOf(v.getAnio()));
                txtMarca.setText(v.getMarca());
                txtModelo.setText(v.getModelo());
                txtColor.setText(v.getColor() == null ? "" : v.getColor());
            }
        });

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnHistorial.addActionListener(e -> verHistorial());

        cargarClientes();
        cmbFiltro.addActionListener(e -> cargarDatos());
        cargarDatos();
    }

    private void agregarCampo(JLabel etiqueta, JTextField campo, int x, int y) {
        etiqueta.setBounds(x, y, 90, 26);
        campo.setBounds(x + 90, y, 230, 26);
        add(etiqueta);
        add(campo);
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = gestorClientes.listarTodos();
            Cliente todos = new Cliente();
            todos.setNombre("Todos los");
            todos.setApellido("clientes");
            cmbFiltro.addItem(todos);
            for (Cliente c : clientes) {
                cmbCliente.addItem(c);
                cmbFiltro.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los clientes: " + ex.getMessage());
        }
    }

    private void seleccionarCliente(int idCliente) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).getId() == idCliente) {
                cmbCliente.setSelectedIndex(i);
                return;
            }
        }
    }

    private Vehiculo leerFormulario() {
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        return new Vehiculo(
            txtPlaca.getText().trim().toUpperCase(),
            cliente.getId(),
            txtMarca.getText().trim(),
            txtModelo.getText().trim(),
            Integer.parseInt(txtAnio.getText().trim()),
            txtColor.getText().trim()
        );
    }

    private boolean validarCampos() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        if (!placa.matches("[A-Z]{1,3}[0-9]{3}[A-Z]{3}")) {
            JOptionPane.showMessageDialog(this, "La placa debe tener un formato como P123ABC.");
            txtPlaca.requestFocus();
            return false;
        }
        if (cmbCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Primero registra un cliente.");
            return false;
        }
        if (txtMarca.getText().trim().isEmpty() || txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La marca y el modelo son obligatorios.");
            txtMarca.requestFocus();
            return false;
        }
        try {
            int anio = Integer.parseInt(txtAnio.getText().trim());
            if (anio < 1950 || anio > 2100) {
                JOptionPane.showMessageDialog(this, "El año debe estar entre 1950 y 2100.");
                txtAnio.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El año debe ser un número.");
            txtAnio.requestFocus();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (idSeleccionado != -1) {
            JOptionPane.showMessageDialog(this, "Ese vehículo ya existe. Usa Editar o presiona Limpiar para registrar uno nuevo.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            gestorVehiculos.insertar(leerFormulario());
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Vehículo registrado correctamente.");
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un vehículo de la tabla.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            Vehiculo v = leerFormulario();
            v.setId(idSeleccionado);
            gestorVehiculos.actualizar(v);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Vehículo actualizado correctamente.");
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un vehículo de la tabla.");
            return;
        }
        try {
            if (gestorVehiculos.tieneOrdenes(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el vehículo tiene órdenes de trabajo registradas.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar este vehículo?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            gestorVehiculos.eliminar(idSeleccionado);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Vehículo eliminado.");
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void verHistorial() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un vehículo de la tabla.");
            return;
        }
        try {
            List<Object[]> filas = gestorVehiculos.historialOrdenes(idSeleccionado);
            if (filas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El vehículo " + txtPlaca.getText() + " no tiene órdenes de trabajo.");
                return;
            }
            String[] titulos = {"No. Orden", "Ingreso", "Mecánico", "Problema", "Estado", "Total"};
            DefaultTableModel modelo = new DefaultTableModel(titulos, 0);
            for (Object[] fila : filas) {
                modelo.addRow(fila);
            }
            JTable tabla = new JTable(modelo);
            tabla.setEnabled(false);
            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setPreferredSize(new Dimension(700, 200));
            JOptionPane.showMessageDialog(this, scroll, "Historial de órdenes - " + txtPlaca.getText(),
                    JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(SQLException ex) {
        if (ex.getErrorCode() == 1) {
            JOptionPane.showMessageDialog(this, "Ya existe un vehículo con esa placa.");
        } else {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            Cliente filtro = (Cliente) cmbFiltro.getSelectedItem();
            if (filtro == null || filtro.getId() == -1) {
                listaVehiculos = gestorVehiculos.listarTodos();
            } else {
                listaVehiculos = gestorVehiculos.listarPorCliente(filtro.getId());
            }
            modeloTabla.setRowCount(0);
            for (Vehiculo v : listaVehiculos) {
                modeloTabla.addRow(new Object[]{
                    v.getId(), v.getPlaca(), v.getNombreCliente(), v.getMarca(),
                    v.getModelo(), v.getAnio(), v.getColor()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los vehículos: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtPlaca.setText("");
        txtAnio.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtColor.setText("");
        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }
        idSeleccionado = -1;
        tablaVehiculos.clearSelection();
        txtPlaca.requestFocus();
    }
}