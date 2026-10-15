package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorClientes {

    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO CLIENTE (ID_CLIENTE, DPI, NOMBRE, APELLIDO, TELEFONO, CORREO, DIRECCION) "
                   + "VALUES (seq_cliente.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getDpi());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getDireccion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE CLIENTE SET DPI = ?, NOMBRE = ?, APELLIDO = ?, TELEFONO = ?, CORREO = ?, DIRECCION = ? "
                   + "WHERE ID_CLIENTE = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getDpi());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getDireccion());
            ps.setInt(7, c.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idCliente) throws SQLException {
        String sql = "DELETE FROM CLIENTE WHERE ID_CLIENTE = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    public boolean tieneVehiculos(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM VEHICULO_TALLER WHERE ID_CLIENTE = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT ID_CLIENTE, DPI, NOMBRE, APELLIDO, TELEFONO, CORREO, DIRECCION "
                   + "FROM CLIENTE ORDER BY ID_CLIENTE";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("ID_CLIENTE"));
                c.setDpi(rs.getString("DPI"));
                c.setNombre(rs.getString("NOMBRE"));
                c.setApellido(rs.getString("APELLIDO"));
                c.setTelefono(rs.getString("TELEFONO"));
                c.setCorreo(rs.getString("CORREO"));
                c.setDireccion(rs.getString("DIRECCION"));
                lista.add(c);
            }
        }
        return lista;
    }
}