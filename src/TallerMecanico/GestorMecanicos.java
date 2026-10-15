package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorMecanicos {

    public void insertar(Mecanico m) throws SQLException {
        String sql = "INSERT INTO MECANICO (ID_MECANICO, DPI, NOMBRE, APELLIDO, TELEFONO, ESPECIALIDAD) "
                   + "VALUES (seq_mecanico.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getDpi());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getApellido());
            ps.setString(4, m.getTelefono());
            ps.setString(5, m.getEspecialidad());
            ps.executeUpdate();
        }
    }

    public void actualizar(Mecanico m) throws SQLException {
        String sql = "UPDATE MECANICO SET DPI = ?, NOMBRE = ?, APELLIDO = ?, TELEFONO = ?, ESPECIALIDAD = ? "
                   + "WHERE ID_MECANICO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getDpi());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getApellido());
            ps.setString(4, m.getTelefono());
            ps.setString(5, m.getEspecialidad());
            ps.setInt(6, m.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idMecanico) throws SQLException {
        String sql = "DELETE FROM MECANICO WHERE ID_MECANICO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMecanico);
            ps.executeUpdate();
        }
    }

    public boolean tieneOrdenes(int idMecanico) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ORDEN_TRABAJO WHERE ID_MECANICO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMecanico);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Mecanico> listarTodos() throws SQLException {
        List<Mecanico> lista = new ArrayList<>();
        String sql = "SELECT ID_MECANICO, DPI, NOMBRE, APELLIDO, TELEFONO, ESPECIALIDAD "
                   + "FROM MECANICO ORDER BY ID_MECANICO";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mecanico m = new Mecanico();
                m.setId(rs.getInt("ID_MECANICO"));
                m.setDpi(rs.getString("DPI"));
                m.setNombre(rs.getString("NOMBRE"));
                m.setApellido(rs.getString("APELLIDO"));
                m.setTelefono(rs.getString("TELEFONO"));
                m.setEspecialidad(rs.getString("ESPECIALIDAD"));
                lista.add(m);
            }
        }
        return lista;
    }
}