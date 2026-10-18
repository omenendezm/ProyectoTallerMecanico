package TallerMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorOrdenes {

    private static final String SQL_ORDENES =
          "SELECT o.ID_ORDEN, o.ID_VEHICULO, v.PLACA, v.MARCA || ' ' || v.MODELO AS VEHICULO, "
        + "c.NOMBRE || ' ' || c.APELLIDO AS CLIENTE, o.ID_MECANICO, m.NOMBRE || ' ' || m.APELLIDO AS MECANICO, "
        + "o.DESCRIPCION_PROBLEMA, o.ESTADO, o.FECHA_INGRESO, o.FECHA_FINALIZACION, o.TOTAL "
        + "FROM ORDEN_TRABAJO o "
        + "JOIN VEHICULO_TALLER v ON o.ID_VEHICULO = v.ID_VEHICULO "
        + "JOIN CLIENTE c ON v.ID_CLIENTE = c.ID_CLIENTE "
        + "JOIN MECANICO m ON o.ID_MECANICO = m.ID_MECANICO ";

    public int crear(OrdenTrabajo orden) throws SQLException {
        String sqlId = "SELECT seq_orden.NEXTVAL FROM DUAL";
        String sql = "INSERT INTO ORDEN_TRABAJO (ID_ORDEN, ID_VEHICULO, ID_MECANICO, DESCRIPCION_PROBLEMA, ESTADO, FECHA_INGRESO) "
                   + "VALUES (?, ?, ?, ?, 'PENDIENTE', SYSDATE)";

        try (Connection con = Conexion.conectar()) {
            int idNuevo;
            try (PreparedStatement ps = con.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                idNuevo = rs.getInt(1);
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idNuevo);
                ps.setInt(2, orden.getIdVehiculo());
                ps.setInt(3, orden.getIdMecanico());
                ps.setString(4, orden.getDescripcionProblema());
                ps.executeUpdate();
            }
            orden.setId(idNuevo);
            return idNuevo;
        }
    }

    public List<OrdenTrabajo> listar(String estado) throws SQLException {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = SQL_ORDENES;
        if (estado != null) {
            sql += "WHERE o.ESTADO = ? ";
        }
        sql += "ORDER BY o.ID_ORDEN DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (estado != null) {
                ps.setString(1, estado);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leerOrden(rs));
                }
            }
        }
        return lista;
    }

    public OrdenTrabajo buscar(int idOrden) throws SQLException {
        String sqlServicios = "SELECT os.ID_ORDEN_SERVICIO, os.ID_SERVICIO, s.DESCRIPCION, os.COSTO "
                            + "FROM ORDEN_SERVICIO os JOIN SERVICIO s ON os.ID_SERVICIO = s.ID_SERVICIO "
                            + "WHERE os.ID_ORDEN = ? ORDER BY os.ID_ORDEN_SERVICIO";
        String sqlRepuestos = "SELECT orp.ID_ORDEN_REPUESTO, orp.ID_REPUESTO, r.NOMBRE, orp.CANTIDAD, orp.PRECIO_UNITARIO "
                            + "FROM ORDEN_REPUESTO orp JOIN REPUESTO r ON orp.ID_REPUESTO = r.ID_REPUESTO "
                            + "WHERE orp.ID_ORDEN = ? ORDER BY orp.ID_ORDEN_REPUESTO";

        try (Connection con = Conexion.conectar()) {
            OrdenTrabajo orden = null;

            try (PreparedStatement ps = con.prepareStatement(SQL_ORDENES + "WHERE o.ID_ORDEN = ?")) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        orden = leerOrden(rs);
                    }
                }
            }
            if (orden == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sqlServicios)) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DetalleServicio d = new DetalleServicio(rs.getInt("ID_SERVICIO"),
                                rs.getString("DESCRIPCION"), rs.getDouble("COSTO"));
                        d.setId(rs.getInt("ID_ORDEN_SERVICIO"));
                        orden.agregarServicio(d);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlRepuestos)) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DetalleRepuesto d = new DetalleRepuesto(rs.getInt("ID_REPUESTO"), rs.getString("NOMBRE"),
                                rs.getInt("CANTIDAD"), rs.getDouble("PRECIO_UNITARIO"));
                        d.setId(rs.getInt("ID_ORDEN_REPUESTO"));
                        orden.agregarRepuesto(d);
                    }
                }
            }
            return orden;
        }
    }

    public void iniciar(int idOrden) throws SQLException, TallerException {
        cambiarEstado(idOrden, OrdenTrabajo.PENDIENTE, OrdenTrabajo.EN_PROCESO,
                "Solo se puede iniciar una orden PENDIENTE.");
    }

    public void agregarServicio(int idOrden, Servicio servicio) throws SQLException, TallerException {
        String sql = "INSERT INTO ORDEN_SERVICIO (ID_ORDEN_SERVICIO, ID_ORDEN, ID_SERVICIO, COSTO) "
                   + "VALUES (seq_orden_servicio.NEXTVAL, ?, ?, ?)";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                validarEnProceso(con, idOrden);

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, idOrden);
                    ps.setInt(2, servicio.getId());
                    ps.setDouble(3, servicio.getCostoManoObra());
                    ps.executeUpdate();
                }

                recalcularTotales(con, idOrden);
                con.commit();
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public void quitarServicio(int idOrden, int idOrdenServicio) throws SQLException, TallerException {
        String sql = "DELETE FROM ORDEN_SERVICIO WHERE ID_ORDEN_SERVICIO = ? AND ID_ORDEN = ?";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                validarEnProceso(con, idOrden);

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, idOrdenServicio);
                    ps.setInt(2, idOrden);
                    ps.executeUpdate();
                }

                recalcularTotales(con, idOrden);
                con.commit();
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }
        public void agregarRepuesto(int idOrden, int idRepuesto, int cantidad) throws SQLException, TallerException {
        String sqlRepuesto = "SELECT NOMBRE, PRECIO, EXISTENCIA FROM REPUESTO WHERE ID_REPUESTO = ? FOR UPDATE";
        String sqlDescontar = "UPDATE REPUESTO SET EXISTENCIA = EXISTENCIA - ? "
                            + "WHERE ID_REPUESTO = ? AND EXISTENCIA >= ?";
        String sqlDetalle = "INSERT INTO ORDEN_REPUESTO (ID_ORDEN_REPUESTO, ID_ORDEN, ID_REPUESTO, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                          + "VALUES (seq_orden_repuesto.NEXTVAL, ?, ?, ?, ?, ?)";

        if (cantidad <= 0) {
            throw new TallerException("La cantidad debe ser mayor que cero.");
        }

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                validarEnProceso(con, idOrden);

                String nombre;
                double precio;
                int existencia;
                try (PreparedStatement ps = con.prepareStatement(sqlRepuesto)) {
                    ps.setInt(1, idRepuesto);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new TallerException("El repuesto ya no existe.");
                        }
                        nombre = rs.getString("NOMBRE");
                        precio = rs.getDouble("PRECIO");
                        existencia = rs.getInt("EXISTENCIA");
                    }
                }

                if (cantidad > existencia) {
                    throw new InventarioInsuficienteException(nombre, existencia, cantidad);
                }

                try (PreparedStatement ps = con.prepareStatement(sqlDescontar)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, idRepuesto);
                    ps.setInt(3, cantidad);
                    if (ps.executeUpdate() == 0) {
                        throw new InventarioInsuficienteException(nombre, existencia, cantidad);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    ps.setInt(1, idOrden);
                    ps.setInt(2, idRepuesto);
                    ps.setInt(3, cantidad);
                    ps.setDouble(4, precio);
                    ps.setDouble(5, precio * cantidad);
                    ps.executeUpdate();
                }

                recalcularTotales(con, idOrden);
                con.commit();
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public void quitarRepuesto(int idOrden, int idOrdenRepuesto) throws SQLException, TallerException {
        String sqlDetalle = "SELECT ID_REPUESTO, CANTIDAD FROM ORDEN_REPUESTO WHERE ID_ORDEN_REPUESTO = ? AND ID_ORDEN = ?";
        String sqlDevolver = "UPDATE REPUESTO SET EXISTENCIA = EXISTENCIA + ? WHERE ID_REPUESTO = ?";
        String sqlBorrar = "DELETE FROM ORDEN_REPUESTO WHERE ID_ORDEN_REPUESTO = ?";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                validarEnProceso(con, idOrden);

                int idRepuesto;
                int cantidad;
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    ps.setInt(1, idOrdenRepuesto);
                    ps.setInt(2, idOrden);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new TallerException("El repuesto ya no está en la orden.");
                        }
                        idRepuesto = rs.getInt("ID_REPUESTO");
                        cantidad = rs.getInt("CANTIDAD");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlDevolver)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, idRepuesto);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlBorrar)) {
                    ps.setInt(1, idOrdenRepuesto);
                    ps.executeUpdate();
                }

                recalcularTotales(con, idOrden);
                con.commit();
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public void finalizar(int idOrden) throws SQLException, TallerException {
        String sqlServicios = "SELECT COUNT(*) FROM ORDEN_SERVICIO WHERE ID_ORDEN = ?";
        String sqlFinalizar = "UPDATE ORDEN_TRABAJO SET ESTADO = 'FINALIZADA', FECHA_FINALIZACION = SYSDATE "
                            + "WHERE ID_ORDEN = ?";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                validarEnProceso(con, idOrden);

                try (PreparedStatement ps = con.prepareStatement(sqlServicios)) {
                    ps.setInt(1, idOrden);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) == 0) {
                            throw new TallerException("No se puede finalizar una orden sin servicios registrados.");
                        }
                    }
                }

                recalcularTotales(con, idOrden);

                try (PreparedStatement ps = con.prepareStatement(sqlFinalizar)) {
                    ps.setInt(1, idOrden);
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public int cancelar(int idOrden) throws SQLException, TallerException {
        String sqlRepuestos = "SELECT ID_REPUESTO, CANTIDAD FROM ORDEN_REPUESTO WHERE ID_ORDEN = ?";
        String sqlDevolver = "UPDATE REPUESTO SET EXISTENCIA = EXISTENCIA + ? WHERE ID_REPUESTO = ?";
        String sqlCancelar = "UPDATE ORDEN_TRABAJO SET ESTADO = 'CANCELADA' WHERE ID_ORDEN = ?";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                String estado = obtenerEstado(con, idOrden);
                if (!OrdenTrabajo.PENDIENTE.equals(estado) && !OrdenTrabajo.EN_PROCESO.equals(estado)) {
                    throw new TallerException("Solo se pueden cancelar órdenes PENDIENTES o EN PROCESO.");
                }

                int unidades = 0;
                try (PreparedStatement psLeer = con.prepareStatement(sqlRepuestos);
                     PreparedStatement psDevolver = con.prepareStatement(sqlDevolver)) {
                    psLeer.setInt(1, idOrden);
                    try (ResultSet rs = psLeer.executeQuery()) {
                        while (rs.next()) {
                            psDevolver.setInt(1, rs.getInt("CANTIDAD"));
                            psDevolver.setInt(2, rs.getInt("ID_REPUESTO"));
                            psDevolver.executeUpdate();
                            unidades += rs.getInt("CANTIDAD");
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlCancelar)) {
                    ps.setInt(1, idOrden);
                    ps.executeUpdate();
                }

                con.commit();
                return unidades;
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private void cambiarEstado(int idOrden, String estadoActual, String estadoNuevo, String mensaje)
            throws SQLException, TallerException {
        String sql = "UPDATE ORDEN_TRABAJO SET ESTADO = ? WHERE ID_ORDEN = ?";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                if (!estadoActual.equals(obtenerEstado(con, idOrden))) {
                    throw new TallerException(mensaje);
                }
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, estadoNuevo);
                    ps.setInt(2, idOrden);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | TallerException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private String obtenerEstado(Connection con, int idOrden) throws SQLException, TallerException {
        String sql = "SELECT ESTADO FROM ORDEN_TRABAJO WHERE ID_ORDEN = ? FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new TallerException("La orden " + idOrden + " no existe.");
                }
                return rs.getString("ESTADO");
            }
        }
    }

    private void validarEnProceso(Connection con, int idOrden) throws SQLException, TallerException {
        String estado = obtenerEstado(con, idOrden);
        if (!OrdenTrabajo.EN_PROCESO.equals(estado)) {
            throw new TallerException("La orden está " + estado
                + ". Solo se pueden modificar órdenes EN PROCESO.");
        }
    }

    private void recalcularTotales(Connection con, int idOrden) throws SQLException {
        String sql = "UPDATE ORDEN_TRABAJO SET "
                   + "TOTAL_MANO_OBRA = (SELECT NVL(SUM(COSTO), 0) FROM ORDEN_SERVICIO WHERE ID_ORDEN = ?), "
                   + "TOTAL_REPUESTOS = (SELECT NVL(SUM(SUBTOTAL), 0) FROM ORDEN_REPUESTO WHERE ID_ORDEN = ?), "
                   + "TOTAL = (SELECT NVL(SUM(COSTO), 0) FROM ORDEN_SERVICIO WHERE ID_ORDEN = ?) "
                   + "+ (SELECT NVL(SUM(SUBTOTAL), 0) FROM ORDEN_REPUESTO WHERE ID_ORDEN = ?) "
                   + "WHERE ID_ORDEN = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                ps.setInt(i, idOrden);
            }
            ps.executeUpdate();
        }
    }

    private OrdenTrabajo leerOrden(ResultSet rs) throws SQLException {
        OrdenTrabajo o = new OrdenTrabajo();
        o.setId(rs.getInt("ID_ORDEN"));
        o.setIdVehiculo(rs.getInt("ID_VEHICULO"));
        o.setPlaca(rs.getString("PLACA"));
        o.setVehiculo(rs.getString("VEHICULO"));
        o.setNombreCliente(rs.getString("CLIENTE"));
        o.setIdMecanico(rs.getInt("ID_MECANICO"));
        o.setNombreMecanico(rs.getString("MECANICO"));
        o.setDescripcionProblema(rs.getString("DESCRIPCION_PROBLEMA"));
        o.setEstado(rs.getString("ESTADO"));
        o.setFechaIngreso(rs.getDate("FECHA_INGRESO"));
        o.setFechaFinalizacion(rs.getDate("FECHA_FINALIZACION"));
        o.setTotalRegistrado(rs.getDouble("TOTAL"));
        return o;
    }
}