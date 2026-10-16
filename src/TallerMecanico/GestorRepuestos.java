package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorRepuestos {

    public void insertar(Repuesto r) throws SQLException {
        String sqlId = "SELECT seq_repuesto.NEXTVAL FROM DUAL";
        String sqlRepuesto = "INSERT INTO REPUESTO (ID_REPUESTO, NOMBRE, DESCRIPCION, PRECIO, EXISTENCIA) "
                           + "VALUES (?, ?, ?, ?, ?)";
        String sqlAbastecimiento = "INSERT INTO ABASTECIMIENTO (ID_ABASTECIMIENTO, ID_REPUESTO, CANTIDAD, FECHA) "
                                 + "VALUES (seq_abastecimiento.NEXTVAL, ?, ?, SYSDATE)";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                int idNuevo;
                try (PreparedStatement ps = con.prepareStatement(sqlId);
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idNuevo = rs.getInt(1);
                }

                try (PreparedStatement ps = con.prepareStatement(sqlRepuesto)) {
                    ps.setInt(1, idNuevo);
                    ps.setString(2, r.getNombre());
                    ps.setString(3, r.getDescripcion());
                    ps.setDouble(4, r.getPrecio());
                    ps.setInt(5, r.getExistencia());
                    ps.executeUpdate();
                }

                if (r.getExistencia() > 0) {
                    try (PreparedStatement ps = con.prepareStatement(sqlAbastecimiento)) {
                        ps.setInt(1, idNuevo);
                        ps.setInt(2, r.getExistencia());
                        ps.executeUpdate();
                    }
                }

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public void actualizar(Repuesto r) throws SQLException {
        String sql = "UPDATE REPUESTO SET NOMBRE = ?, DESCRIPCION = ?, PRECIO = ? WHERE ID_REPUESTO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());
            ps.setDouble(3, r.getPrecio());
            ps.setInt(4, r.getId());
            ps.executeUpdate();
        }
    }

    public void abastecer(int idRepuesto, int cantidad) throws SQLException {
        String sqlExistencia = "UPDATE REPUESTO SET EXISTENCIA = EXISTENCIA + ? WHERE ID_REPUESTO = ?";
        String sqlAbastecimiento = "INSERT INTO ABASTECIMIENTO (ID_ABASTECIMIENTO, ID_REPUESTO, CANTIDAD, FECHA) "
                                 + "VALUES (seq_abastecimiento.NEXTVAL, ?, ?, SYSDATE)";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlExistencia)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, idRepuesto);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlAbastecimiento)) {
                    ps.setInt(1, idRepuesto);
                    ps.setInt(2, cantidad);
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public boolean estaEnOrdenes(int idRepuesto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ORDEN_REPUESTO WHERE ID_REPUESTO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRepuesto);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void eliminar(int idRepuesto) throws SQLException {
        String sqlAbastecimientos = "DELETE FROM ABASTECIMIENTO WHERE ID_REPUESTO = ?";
        String sqlRepuesto = "DELETE FROM REPUESTO WHERE ID_REPUESTO = ?";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlAbastecimientos)) {
                    ps.setInt(1, idRepuesto);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlRepuesto)) {
                    ps.setInt(1, idRepuesto);
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public List<Repuesto> listarTodos() throws SQLException {
        return listar("SELECT ID_REPUESTO, NOMBRE, DESCRIPCION, PRECIO, EXISTENCIA FROM REPUESTO ORDER BY ID_REPUESTO");
    }

    public List<Repuesto> listarAgotados() throws SQLException {
        return listar("SELECT ID_REPUESTO, NOMBRE, DESCRIPCION, PRECIO, EXISTENCIA FROM REPUESTO "
                    + "WHERE EXISTENCIA = 0 ORDER BY NOMBRE");
    }

    public List<Repuesto> listarDisponibles() throws SQLException {
        return listar("SELECT ID_REPUESTO, NOMBRE, DESCRIPCION, PRECIO, EXISTENCIA FROM REPUESTO "
                    + "WHERE EXISTENCIA > 0 ORDER BY NOMBRE");
    }

    private List<Repuesto> listar(String sql) throws SQLException {
        List<Repuesto> lista = new ArrayList<>();

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Repuesto r = new Repuesto();
                r.setId(rs.getInt("ID_REPUESTO"));
                r.setNombre(rs.getString("NOMBRE"));
                r.setDescripcion(rs.getString("DESCRIPCION"));
                r.setPrecio(rs.getDouble("PRECIO"));
                r.setExistencia(rs.getInt("EXISTENCIA"));
                lista.add(r);
            }
        }
        return lista;
    }
}