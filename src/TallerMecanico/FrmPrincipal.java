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
    JButton btnMecanicos = new JButton("Mecánicos");
    JButton btnSalir = new JButton("Salir");

    public FrmPrincipal() {
        setTitle("Taller El Último Arranque");
        setSize(420, 330);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 380, 30);
        add(lblTitulo);

        lblSubtitulo.setBounds(20, 50, 380, 20);
        add(lblSubtitulo);

        btnClientes.setBounds(110, 100, 200, 40);
        btnMecanicos.setBounds(110, 150, 200, 40);
        btnSalir.setBounds(110, 220, 200, 35);
        add(btnClientes);
        add(btnMecanicos);
        add(btnSalir);

        btnClientes.addActionListener(e -> new FrmClientes().setVisible(true));
        btnMecanicos.addActionListener(e -> new FrmMecanicos().setVisible(true));
        btnSalir.addActionListener(e -> System.exit(0));
    }
}