package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TallerMecanico {

    public static void main(String[] args) {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM CLIENTE");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println("Conexion exitosa. Clientes registrados: " + rs.getInt(1));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}