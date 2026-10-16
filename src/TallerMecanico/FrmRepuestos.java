package TallerMecanico;

import javax.swing.JButton;
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
import java.util.ArrayList;
import java.util.List;

public class FrmRepuestos extends JFrame {

    JLabel lblTitulo = new JLabel("GESTIÓN DE REPUESTOS");
    JLabel lblNombre = new JLabel("Nombre:");
    JTextField txtNombre = new JTextField();
    JLabel lblDescripcion = new JLabel("Descripción:");
    JTextField txtDescripcion = new JTextField();
    JLabel lblPrecio = new JLabel("Precio (Q):");
    JTextField txtPrecio = new JTextField();
    JLabel lblExistencia = new JLabel("Existencia:");
    JTextField txtExistencia = new JTextField();

    JButton btnGuardar = new JButton("Guardar");
    JButton btnEditar = new JButton("Editar");
    JButton btnEliminar = new JButton("Eliminar");
    JButton btnLimpiar = new JButton("Limpiar");

    JLabel lblAbastecer = new JLabel("Abastecer:");
    JTextField txtCantidad = new JTextField();
    JButton btnAbastecer = new JButton("Abastecer");
    JButton btnAgotados = new JButton("Ver agotados");
    JButton btnImportar = new JButton("Importar CSV");

    String[] columnas = {"ID", "Nombre", "Descripción", "Precio", "Existencia", "Estado"};
    DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable tablaRepuestos = new JTable(modeloTabla);
    JScrollPane scrollTabla = new JScrollPane(tablaRepuestos);

    GestorRepuestos gestorRepuestos = new GestorRepuestos();
    GestorArchivos gestorArchivos = new GestorArchivos();
    List<Repuesto> listaRepuestos = new ArrayList<>();
    int idSeleccionado = -1;
    boolean soloAgotados = false;

    public FrmRepuestos() {
        setTitle("Repuestos");
        setSize(830, 530);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(30, 15, 300, 30);
        add(lblTitulo);

        agregarCampo(lblNombre, txtNombre, 30, 60);
        agregarCampo(lblDescripcion, txtDescripcion, 30, 100);
        agregarCampo(lblPrecio, txtPrecio, 390, 60);
        agregarCampo(lblExistencia, txtExistencia, 390, 100);

        btnGuardar.setBounds(30, 145, 120, 32);
        btnEditar.setBounds(160, 145, 120, 32);
        btnEliminar.setBounds(290, 145, 120, 32);
        btnLimpiar.setBounds(420, 145, 120, 32);
        add(btnGuardar);
        add(btnEditar);
        add(btnEliminar);
        add(btnLimpiar);

        lblAbastecer.setBounds(30, 195, 90, 28);
        txtCantidad.setBounds(120, 195, 80, 28);
        btnAbastecer.setBounds(210, 193, 120, 32);
        btnAgotados.setBounds(420, 193, 150, 32);
        btnImportar.setBounds(580, 193, 140, 32);
        add(lblAbastecer);
        add(txtCantidad);
        add(btnAbastecer);
        add(btnAgotados);
        add(btnImportar);

        scrollTabla.setBounds(30, 245, 760, 200);
        add(scrollTabla);

        int[] anchos = {50, 170, 270, 90, 80, 100};
        for (int i = 0; i < anchos.length; i++) {
            tablaRepuestos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        tablaRepuestos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaRepuestos.getSelectedRow() != -1) {
                Repuesto r = listaRepuestos.get(tablaRepuestos.getSelectedRow());
                idSeleccionado = r.getId();
                txtNombre.setText(r.getNombre());
                txtDescripcion.setText(r.getDescripcion() == null ? "" : r.getDescripcion());
                txtPrecio.setText(String.valueOf(r.getPrecio()));
                txtExistencia.setText(String.valueOf(r.getExistencia()));
                txtExistencia.setEditable(false);
            }
        });

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnAbastecer.addActionListener(e -> abastecer());
        btnAgotados.addActionListener(e -> cambiarFiltro());
        btnImportar.addActionListener(e -> importarCsv());

        cargarDatos();
    }

    private void agregarCampo(JLabel etiqueta, JTextField campo, int x, int y) {
        etiqueta.setBounds(x, y, 90, 26);
        campo.setBounds(x + 90, y, 230, 26);
        add(etiqueta);
        add(campo);
    }

    private boolean validarCampos(boolean esNuevo) {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio < 0) {
                JOptionPane.showMessageDialog(this, "El precio no puede ser negativo.");
                txtPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número.");
            txtPrecio.requestFocus();
            return false;
        }
        if (esNuevo) {
            try {
                int existencia = Integer.parseInt(txtExistencia.getText().trim());
                if (existencia < 0) {
                    JOptionPane.showMessageDialog(this, "La existencia no puede ser negativa.");
                    txtExistencia.requestFocus();
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La existencia debe ser un número entero.");
                txtExistencia.requestFocus();
                return false;
            }
        }
        return true;
    }

    private Repuesto leerFormulario(boolean esNuevo) {
        int existencia = esNuevo ? Integer.parseInt(txtExistencia.getText().trim()) : 0;
        return new Repuesto(
            txtNombre.getText().trim(),
            txtDescripcion.getText().trim(),
            Double.parseDouble(txtPrecio.getText().trim()),
            existencia
        );
    }

    private void guardar() {
        if (idSeleccionado != -1) {
            JOptionPane.showMessageDialog(this, "Ese repuesto ya existe. Usa Editar o presiona Limpiar para registrar uno nuevo.");
            return;
        }
        if (!validarCampos(true)) {
            return;
        }
        try {
            gestorRepuestos.insertar(leerFormulario(true));
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Repuesto registrado correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un repuesto de la tabla.");
            return;
        }
        if (!validarCampos(false)) {
            return;
        }
        try {
            Repuesto r = leerFormulario(false);
            r.setId(idSeleccionado);
            gestorRepuestos.actualizar(r);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Repuesto actualizado correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un repuesto de la tabla.");
            return;
        }
        try {
            if (gestorRepuestos.estaEnOrdenes(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el repuesto se utilizó en órdenes de trabajo.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar este repuesto?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            gestorRepuestos.eliminar(idSeleccionado);
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Repuesto eliminado.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }
        private void abastecer() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un repuesto de la tabla.");
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
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad a abastecer debe ser mayor que cero.");
            txtCantidad.requestFocus();
            return;
        }
        try {
            gestorRepuestos.abastecer(idSeleccionado, cantidad);
            txtCantidad.setText("");
            cargarDatos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Se agregaron " + cantidad + " unidades al inventario.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void cambiarFiltro() {
        soloAgotados = !soloAgotados;
        btnAgotados.setText(soloAgotados ? "Ver todos" : "Ver agotados");
        limpiarCampos();
        cargarDatos();
    }

    private void importarCsv() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Importar repuestos (nombre, descripción, precio, existencia)");
        selector.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        int importados = 0;
        List<String> errores = new ArrayList<>();
        try {
            List<String[]> filas = gestorArchivos.leerCsv(archivo);
            for (int i = 0; i < filas.size(); i++) {
                String[] campos = filas.get(i);
                int numeroLinea = i + 1;
                if (i == 0 && campos.length >= 3 && !esNumero(campos[2])) {
                    continue;
                }
                try {
                    if (campos.length < 4) {
                        throw new IllegalArgumentException("faltan columnas");
                    }
                    String nombre = campos[0];
                    double precio = Double.parseDouble(campos[2]);
                    int existencia = Integer.parseInt(campos[3]);
                    if (nombre.isEmpty() || precio < 0 || existencia < 0) {
                        throw new IllegalArgumentException("datos inválidos");
                    }
                    gestorRepuestos.insertar(new Repuesto(nombre, campos[1], precio, existencia));
                    importados++;
                } catch (IllegalArgumentException ex) {
                    errores.add("Línea " + numeroLinea + ": " + ex.getMessage());
                } catch (SQLException ex) {
                    errores.add("Línea " + numeroLinea + ": " + ex.getMessage());
                }
            }
            cargarDatos();
            String mensaje = "Repuestos importados: " + importados;
            if (!errores.isEmpty()) {
                mensaje += "\nLíneas con error: " + errores.size() + "\n" + String.join("\n", errores);
            }
            JOptionPane.showMessageDialog(this, mensaje);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el archivo: " + ex.getMessage());
        }
    }

    private boolean esNumero(String texto) {
        try {
            Double.parseDouble(texto);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void cargarDatos() {
        try {
            listaRepuestos = soloAgotados ? gestorRepuestos.listarAgotados() : gestorRepuestos.listarTodos();
            modeloTabla.setRowCount(0);
            for (Repuesto r : listaRepuestos) {
                modeloTabla.addRow(new Object[]{
                    r.getId(), r.getNombre(), r.getDescripcion(),
                    String.format("%.2f", r.getPrecio()), r.getExistencia(), r.getEstadoInventario()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los repuestos: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtExistencia.setText("");
        txtExistencia.setEditable(true);
        idSeleccionado = -1;
        tablaRepuestos.clearSelection();
        txtNombre.requestFocus();
    }
}