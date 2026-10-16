package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorServicios {

    public void insertar(Servicio s) throws SQLException {
        String sql = "INSERT INTO SERVICIO (ID_SERVICIO, DESCRIPCION, COSTO_MANO_OBRA) "
                   + "VALUES (seq_servicio.NEXTVAL, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getDescripcion());
            ps.setDouble(2, s.getCostoManoObra());
            ps.executeUpdate();
        }
    }

    public void actualizar(Servicio s) throws SQLException {
        String sql = "UPDATE SERVICIO SET DESCRIPCION = ?, COSTO_MANO_OBRA = ? WHERE ID_SERVICIO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getDescripcion());
            ps.setDouble(2, s.getCostoManoObra());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idServicio) throws SQLException {
        String sql = "DELETE FROM SERVICIO WHERE ID_SERVICIO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idServicio);
            ps.executeUpdate();
        }
    }

    public boolean estaEnOrdenes(int idServicio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ORDEN_SERVICIO WHERE ID_SERVICIO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Servicio> listarTodos() throws SQLException {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT ID_SERVICIO, DESCRIPCION, COSTO_MANO_OBRA FROM SERVICIO ORDER BY ID_SERVICIO";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio();
                s.setId(rs.getInt("ID_SERVICIO"));
                s.setDescripcion(rs.getString("DESCRIPCION"));
                s.setCostoManoObra(rs.getDouble("COSTO_MANO_OBRA"));
                lista.add(s);
            }
        }
        return lista;
    }
}