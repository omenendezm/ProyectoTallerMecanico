package TallerMecanico;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class FrmPrincipal extends JFrame {

    JLabel lblTitulo = new JLabel("TALLER EL ÚLTIMO ARRANQUE", SwingConstants.CENTER);
    JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Órdenes de Trabajo", SwingConstants.CENTER);

    JButton btnClientes = new JButton("Clientes");
    JButton btnVehiculos = new JButton("Vehículos");
    JButton btnMecanicos = new JButton("Mecánicos");
    JButton btnServicios = new JButton("Servicios");
    JButton btnRepuestos = new JButton("Repuestos");
    JButton btnOrdenes = new JButton("Órdenes de trabajo");
    JButton btnSalir = new JButton("Salir");

    public FrmPrincipal() {
        setTitle("Taller El Último Arranque");
        setSize(520, 410);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBounds(20, 20, 480, 30);
        add(lblTitulo);

        lblSubtitulo.setBounds(20, 52, 480, 20);
        add(lblSubtitulo);

        btnClientes.setBounds(60, 100, 190, 40);
        add(btnClientes);
        btnVehiculos.setBounds(270, 100, 190, 40);
        add(btnVehiculos);
        btnMecanicos.setBounds(60, 150, 190, 40);
        add(btnMecanicos);
        btnServicios.setBounds(270, 150, 190, 40);
        add(btnServicios);
        btnRepuestos.setBounds(60, 200, 190, 40);
        add(btnRepuestos);
        btnOrdenes.setBounds(270, 200, 190, 40);
        add(btnOrdenes);
        btnSalir.setBounds(160, 315, 200, 35);
        add(btnSalir);

        btnClientes.addActionListener(e -> new FrmClientes().setVisible(true));
        btnVehiculos.addActionListener(e -> new FrmVehiculos().setVisible(true));
        btnMecanicos.addActionListener(e -> new FrmMecanicos().setVisible(true));
        btnServicios.addActionListener(e -> new FrmServicios().setVisible(true));
        btnRepuestos.addActionListener(e -> new FrmRepuestos().setVisible(true));
        btnOrdenes.addActionListener(e -> new FrmOrdenes().setVisible(true));
        btnSalir.addActionListener(e -> System.exit(0));
    }
}