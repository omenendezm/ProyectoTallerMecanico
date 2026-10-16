package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GestorVehiculos {

    public void insertar(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO VEHICULO_TALLER (ID_VEHICULO, PLACA, ID_CLIENTE, MARCA, MODELO, ANIO, COLOR) "
                   + "VALUES (seq_vehiculo.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getPlaca());
            ps.setInt(2, v.getIdCliente());
            ps.setString(3, v.getMarca());
            ps.setString(4, v.getModelo());
            ps.setInt(5, v.getAnio());
            ps.setString(6, v.getColor());
            ps.executeUpdate();
        }
    }

    public void actualizar(Vehiculo v) throws SQLException {
        String sql = "UPDATE VEHICULO_TALLER SET PLACA = ?, ID_CLIENTE = ?, MARCA = ?, MODELO = ?, ANIO = ?, COLOR = ? "
                   + "WHERE ID_VEHICULO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getPlaca());
            ps.setInt(2, v.getIdCliente());
            ps.setString(3, v.getMarca());
            ps.setString(4, v.getModelo());
            ps.setInt(5, v.getAnio());
            ps.setString(6, v.getColor());
            ps.setInt(7, v.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idVehiculo) throws SQLException {
        String sql = "DELETE FROM VEHICULO_TALLER WHERE ID_VEHICULO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            ps.executeUpdate();
        }
    }

    public boolean tieneOrdenes(int idVehiculo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ORDEN_TRABAJO WHERE ID_VEHICULO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Vehiculo> listarTodos() throws SQLException {
        return listar(-1);
    }

    public List<Vehiculo> listarPorCliente(int idCliente) throws SQLException {
        return listar(idCliente);
    }

    private List<Vehiculo> listar(int idCliente) throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT v.ID_VEHICULO, v.PLACA, v.ID_CLIENTE, c.NOMBRE || ' ' || c.APELLIDO AS CLIENTE, "
                   + "v.MARCA, v.MODELO, v.ANIO, v.COLOR "
                   + "FROM VEHICULO_TALLER v JOIN CLIENTE c ON v.ID_CLIENTE = c.ID_CLIENTE ";
        if (idCliente != -1) {
            sql += "WHERE v.ID_CLIENTE = ? ";
        }
        sql += "ORDER BY v.PLACA";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (idCliente != -1) {
                ps.setInt(1, idCliente);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setId(rs.getInt("ID_VEHICULO"));
                    v.setPlaca(rs.getString("PLACA"));
                    v.setIdCliente(rs.getInt("ID_CLIENTE"));
                    v.setNombreCliente(rs.getString("CLIENTE"));
                    v.setMarca(rs.getString("MARCA"));
                    v.setModelo(rs.getString("MODELO"));
                    v.setAnio(rs.getInt("ANIO"));
                    v.setColor(rs.getString("COLOR"));
                    lista.add(v);
                }
            }
        }
        return lista;
    }

    public List<Object[]> historialOrdenes(int idVehiculo) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String sql = "SELECT o.ID_ORDEN, o.FECHA_INGRESO, m.NOMBRE || ' ' || m.APELLIDO AS MECANICO, "
                   + "o.DESCRIPCION_PROBLEMA, o.ESTADO, o.TOTAL "
                   + "FROM ORDEN_TRABAJO o JOIN MECANICO m ON o.ID_MECANICO = m.ID_MECANICO "
                   + "WHERE o.ID_VEHICULO = ? ORDER BY o.FECHA_INGRESO DESC, o.ID_ORDEN DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getInt("ID_ORDEN"),
                        formato.format(rs.getDate("FECHA_INGRESO")),
                        rs.getString("MECANICO"),
                        rs.getString("DESCRIPCION_PROBLEMA"),
                        rs.getString("ESTADO"),
                        String.format("%.2f", rs.getDouble("TOTAL"))
                    });
                }
            }
        }
        return filas;
    }
}