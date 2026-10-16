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
import java.util.ArrayList;
import java.util.List;

public class FrmServicios extends JFrame {

    JLabel lblTitulo = new JLabel("GESTIÓN DE SERVICIOS");
    JLabel lblDescripcion = new JLabel("Descripción:");
    JTextField txtDescripcion = new JTextField();
    JLabel lblCosto = new JLabel("Mano de obra (Q):");
    JTextField txtCosto = new JTextField();

    JButton btnGuardar = new JButton("Guardar");
    JButton btnEditar = new JButton("Editar");
    JButton btnEliminar = new JButton("Eliminar");
    JButton btnLimpiar = new JButton("Limpiar");

    String[] columnas = {"ID", "Descripción", "Mano de obra"};
    DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable tablaServicios = new JTable(modeloTabla);
    JScrollPane scrollTabla = new JScrollPane(tablaServicios);

    GestorServicios gestorServicios = new GestorServicios();
    List<Servicio> listaServicios = new ArrayList<>();
    int idSeleccionado = -1;

    public FrmServicios() {
        setTitle("Servicios");
        setSize(620, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(30, 15, 300, 30);
        add(lblTitulo);

        lblDescripcion.setBounds(30, 60, 130, 26);
        txtDescripcion.setBounds(160, 60, 300, 26);
        lblCosto.setBounds(30, 100, 130, 26);
        txtCosto.setBounds(160, 100, 120, 26);
        add(lblDescripcion);
        add(txtDescripcion);
        add(lblCosto);
        add(txtCosto);

        btnGuardar.setBounds(30, 145, 120, 32);
        btnEditar.setBounds(160, 145, 120, 32);
        btnEliminar.setBounds(290, 145, 120, 32);
        btnLimpiar.setBounds(420, 145, 120, 32);
        add(btnGuardar);
        add(btnEditar);
        add(btnEliminar);
        add(btnLimpiar);

        scrollTabla.setBounds(30, 195, 540, 180);
        add(scrollTabla);

        int[] anchos = {50, 370, 120};
        for (int i = 0; i < anchos.length; i++) {
            tablaServicios.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaServicios.getSelectedRow() != -1) {
                Servicio s = listaServicios.get(tablaServicios.getSelectedRow());
                idSeleccionado = s.getId();
                txtDescripcion.setText(s.getDescripcion());
                txtCosto.setText(String.valueOf(s.getCostoManoObra()));
            }
        });

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        cargarDatos();
    }

    private boolean validarCampos() {
        if (txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria.");
            txtDescripcion.requestFocus();
            return false;
        }
        try {
            double costo = Double.parseDouble(txtCosto.getText().trim());
            if (costo < 0) {
                JOptionPane.showMessageDialog(this, "El costo no puede ser negativo.");
                txtCosto.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El costo debe ser un número.");
            txtCosto.requestFocus();
            return false;
        }
        return true;
    }

    private Servicio leerFormulario() {
        return new Servicio(
            txtDescripcion.getText().trim(),
            Double.parseDouble(txtCosto.getText().trim())
        );
    }

    private void guardar() {
        if (idSeleccionado != -1) {
            JOptionPane.showMessageDialog(this, "Ese servicio ya existe. Usa Editar o presiona Limpiar para registrar uno nuevo.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            gestorServicios.insertar(leerFormulario());
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Servicio registrado correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un servicio de la tabla.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            Servicio s = leerFormulario();
            s.setId(idSeleccionado);
            gestorServicios.actualizar(s);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Servicio actualizado correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un servicio de la tabla.");
            return;
        }
        try {
            if (gestorServicios.estaEnOrdenes(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el servicio se utilizó en órdenes de trabajo.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar este servicio?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            gestorServicios.eliminar(idSeleccionado);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Servicio eliminado.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            listaServicios = gestorServicios.listarTodos();
            modeloTabla.setRowCount(0);
            for (Servicio s : listaServicios) {
                modeloTabla.addRow(new Object[]{
                    s.getId(), s.getDescripcion(), String.format("%.2f", s.getCostoManoObra())
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los servicios: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtDescripcion.setText("");
        txtCosto.setText("");
        idSeleccionado = -1;
        tablaServicios.clearSelection();
        txtDescripcion.requestFocus();
    }
}