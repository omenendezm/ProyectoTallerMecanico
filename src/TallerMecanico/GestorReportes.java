package TallerMecanico;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GestorReportes {

    private static final String DESDE_ORDENES =
          "FROM ORDEN_TRABAJO o "
        + "JOIN VEHICULO_TALLER v ON o.ID_VEHICULO = v.ID_VEHICULO "
        + "JOIN CLIENTE c ON v.ID_CLIENTE = c.ID_CLIENTE "
        + "JOIN MECANICO m ON o.ID_MECANICO = m.ID_MECANICO ";

    public DefaultTableModel ordenesPorEstado(String estado) throws SQLException {
        String[] columnas = {"No. Orden", "Placa", "Cliente", "Mecánico", "Ingreso", "Estado", "Total"};
        String sql = "SELECT o.ID_ORDEN, v.PLACA, c.NOMBRE || ' ' || c.APELLIDO, m.NOMBRE || ' ' || m.APELLIDO, "
                   + "TO_CHAR(o.FECHA_INGRESO, 'DD/MM/YYYY'), o.ESTADO, TO_CHAR(o.TOTAL, 'FM999999990.00') "
                   + DESDE_ORDENES;
        if (estado != null) {
            sql += "WHERE o.ESTADO = ? ";
        }
        sql += "ORDER BY o.ESTADO, o.ID_ORDEN";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (estado != null) {
                ps.setString(1, estado);
            }
            return crearModelo(ps, columnas);
        }
    }

    public DefaultTableModel reparacionesPorVehiculo(String placa) throws SQLException {
        String[] columnas = {"No. Orden", "Placa", "Vehículo", "Ingreso", "Finalización", "Mecánico",
                             "Problema", "Estado", "Total"};
        String sql = "SELECT o.ID_ORDEN, v.PLACA, v.MARCA || ' ' || v.MODELO, TO_CHAR(o.FECHA_INGRESO, 'DD/MM/YYYY'), "
                   + "TO_CHAR(o.FECHA_FINALIZACION, 'DD/MM/YYYY'), m.NOMBRE || ' ' || m.APELLIDO, "
                   + "o.DESCRIPCION_PROBLEMA, o.ESTADO, TO_CHAR(o.TOTAL, 'FM999999990.00') "
                   + DESDE_ORDENES
                   + "WHERE UPPER(v.PLACA) = UPPER(?) ORDER BY o.FECHA_INGRESO DESC, o.ID_ORDEN DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);
            return crearModelo(ps, columnas);
        }
    }

    public DefaultTableModel repuestosBajoInventario() throws SQLException {
        String[] columnas = {"ID", "Repuesto", "Descripción", "Precio", "Existencia", "Situación"};
        String sql = "SELECT ID_REPUESTO, NOMBRE, DESCRIPCION, TO_CHAR(PRECIO, 'FM999999990.00'), EXISTENCIA, "
                   + "CASE WHEN EXISTENCIA = 0 THEN 'AGOTADO' ELSE 'BAJO' END "
                   + "FROM REPUESTO WHERE EXISTENCIA <= 5 ORDER BY EXISTENCIA, NOMBRE";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            return crearModelo(ps, columnas);
        }
    }

    public DefaultTableModel reparacionesPorMecanico(int idMecanico) throws SQLException {
        String[] columnas = {"Mecánico", "Especialidad", "No. Orden", "Placa", "Cliente", "Ingreso", "Estado", "Total"};
        String sql = "SELECT m.NOMBRE || ' ' || m.APELLIDO, m.ESPECIALIDAD, o.ID_ORDEN, v.PLACA, "
                   + "c.NOMBRE || ' ' || c.APELLIDO, TO_CHAR(o.FECHA_INGRESO, 'DD/MM/YYYY'), o.ESTADO, "
                   + "TO_CHAR(o.TOTAL, 'FM999999990.00') "
                   + DESDE_ORDENES;
        if (idMecanico != -1) {
            sql += "WHERE o.ID_MECANICO = ? ";
        }
        sql += "ORDER BY m.NOMBRE, m.APELLIDO, o.ID_ORDEN";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (idMecanico != -1) {
                ps.setInt(1, idMecanico);
            }
            return crearModelo(ps, columnas);
        }
    }

    public DefaultTableModel ingresosPorReparaciones(Date desde, Date hasta) throws SQLException {
        String[] columnas = {"No. Orden", "Finalización", "Placa", "Cliente", "Mano de obra", "Repuestos", "Total"};
        String sql = "SELECT o.ID_ORDEN, TO_CHAR(o.FECHA_FINALIZACION, 'DD/MM/YYYY'), v.PLACA, "
                   + "c.NOMBRE || ' ' || c.APELLIDO, TO_CHAR(o.TOTAL_MANO_OBRA, 'FM999999990.00'), "
                   + "TO_CHAR(o.TOTAL_REPUESTOS, 'FM999999990.00'), TO_CHAR(o.TOTAL, 'FM999999990.00') "
                   + DESDE_ORDENES
                   + "WHERE o.ESTADO = 'FINALIZADA' AND o.FECHA_FINALIZACION >= ? AND o.FECHA_FINALIZACION < ? "
                   + "ORDER BY o.FECHA_FINALIZACION, o.ID_ORDEN";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(desde.getTime()));
            ps.setDate(2, new java.sql.Date(diaSiguiente(hasta).getTime()));
            return crearModelo(ps, columnas);
        }
    }

    public double[] totalesIngresos(Date desde, Date hasta) throws SQLException {
        String sql = "SELECT NVL(SUM(TOTAL_MANO_OBRA), 0), NVL(SUM(TOTAL_REPUESTOS), 0), NVL(SUM(TOTAL), 0) "
                   + "FROM ORDEN_TRABAJO WHERE ESTADO = 'FINALIZADA' "
                   + "AND FECHA_FINALIZACION >= ? AND FECHA_FINALIZACION < ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(desde.getTime()));
            ps.setDate(2, new java.sql.Date(diaSiguiente(hasta).getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
            }
        }
    }

    private Date diaSiguiente(Date fecha) {
        return new Date(fecha.getTime() + 24L * 60 * 60 * 1000);
    }

    private DefaultTableModel crearModelo(PreparedStatement ps, String[] columnas) throws SQLException {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[columnas.length];
                for (int i = 0; i < columnas.length; i++) {
                    fila[i] = rs.getString(i + 1);
                }
                modelo.addRow(fila);
            }
        }
        return modelo;
    }
}