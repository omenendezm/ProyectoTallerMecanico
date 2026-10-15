package TallerMecanico;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

public class FrmMecanicos extends JFrame {

    JLabel lblTitulo = new JLabel("GESTIÓN DE MECÁNICOS");
    JLabel lblDpi = new JLabel("DPI:");
    JTextField txtDpi = new JTextField();
    JLabel lblNombre = new JLabel("Nombre:");
    JTextField txtNombre = new JTextField();
    JLabel lblApellido = new JLabel("Apellido:");
    JTextField txtApellido = new JTextField();
    JLabel lblTelefono = new JLabel("Teléfono:");
    JTextField txtTelefono = new JTextField();
    JLabel lblEspecialidad = new JLabel("Especialidad:");
    JTextField txtEspecialidad = new JTextField();

    JButton btnGuardar = new JButton("Guardar");
    JButton btnEditar = new JButton("Editar");
    JButton btnEliminar = new JButton("Eliminar");
    JButton btnLimpiar = new JButton("Limpiar");

    String[] columnas = {"ID", "DPI", "Nombre", "Apellido", "Teléfono", "Especialidad"};
    DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable tablaMecanicos = new JTable(modeloTabla);
    JScrollPane scrollTabla = new JScrollPane(tablaMecanicos);

    GestorMecanicos gestorMecanicos = new GestorMecanicos();
    int idSeleccionado = -1;

    public FrmMecanicos() {
        setTitle("Mecánicos");
        setSize(830, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(30, 15, 300, 30);
        add(lblTitulo);

        agregarCampo(lblDpi, txtDpi, 30, 60);
        agregarCampo(lblNombre, txtNombre, 30, 100);
        agregarCampo(lblApellido, txtApellido, 30, 140);
        agregarCampo(lblTelefono, txtTelefono, 390, 60);
        agregarCampo(lblEspecialidad, txtEspecialidad, 390, 100);

        btnGuardar.setBounds(30, 190, 120, 32);
        btnEditar.setBounds(160, 190, 120, 32);
        btnEliminar.setBounds(290, 190, 120, 32);
        btnLimpiar.setBounds(420, 190, 120, 32);
        add(btnGuardar);
        add(btnEditar);
        add(btnEliminar);
        add(btnLimpiar);

        scrollTabla.setBounds(30, 240, 760, 200);
        add(scrollTabla);

        int[] anchos = {50, 120, 140, 140, 110, 200};
        for (int i = 0; i < anchos.length; i++) {
            tablaMecanicos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        tablaMecanicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaMecanicos.getSelectedRow() != -1) {
                int fila = tablaMecanicos.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtDpi.setText(texto(modeloTabla.getValueAt(fila, 1)));
                txtNombre.setText(texto(modeloTabla.getValueAt(fila, 2)));
                txtApellido.setText(texto(modeloTabla.getValueAt(fila, 3)));
                txtTelefono.setText(texto(modeloTabla.getValueAt(fila, 4)));
                txtEspecialidad.setText(texto(modeloTabla.getValueAt(fila, 5)));
            }
        });

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        cargarDatos();
    }

    private void agregarCampo(JLabel etiqueta, JTextField campo, int x, int y) {
        etiqueta.setBounds(x, y, 90, 26);
        campo.setBounds(x + 90, y, 230, 26);
        add(etiqueta);
        add(campo);
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private boolean validarCampos() {
        if (!txtDpi.getText().trim().matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this, "El DPI debe tener 13 dígitos.");
            txtDpi.requestFocus();
            return false;
        }
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y el apellido son obligatorios.");
            txtNombre.requestFocus();
            return false;
        }
        return true;
    }

    private Mecanico leerFormulario() {
        return new Mecanico(
            txtDpi.getText().trim(),
            txtNombre.getText().trim(),
            txtApellido.getText().trim(),
            txtTelefono.getText().trim(),
            txtEspecialidad.getText().trim()
        );
    }

    private void guardar() {
        if (idSeleccionado != -1) {
            JOptionPane.showMessageDialog(this, "Ese mecánico ya existe. Usa Editar o presiona Limpiar para registrar uno nuevo.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            gestorMecanicos.insertar(leerFormulario());
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Mecánico registrado correctamente.");
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un mecánico de la tabla.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            Mecanico m = leerFormulario();
            m.setId(idSeleccionado);
            gestorMecanicos.actualizar(m);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Mecánico actualizado correctamente.");
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un mecánico de la tabla.");
            return;
        }
        try {
            if (gestorMecanicos.tieneOrdenes(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el mecánico tiene órdenes de trabajo registradas.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar este mecánico?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            gestorMecanicos.eliminar(idSeleccionado);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Mecánico eliminado.");
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(SQLException ex) {
        if (ex.getErrorCode() == 1) {
            JOptionPane.showMessageDialog(this, "Ya existe un mecánico con ese DPI.");
        } else {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            List<Mecanico> lista = gestorMecanicos.listarTodos();
            modeloTabla.setRowCount(0);
            for (Mecanico m : lista) {
                modeloTabla.addRow(new Object[]{
                    m.getId(), m.getDpi(), m.getNombre(), m.getApellido(),
                    m.getTelefono(), m.getEspecialidad()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los mecánicos: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtDpi.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtEspecialidad.setText("");
        idSeleccionado = -1;
        tablaMecanicos.clearSelection();
        txtDpi.requestFocus();
    }
}