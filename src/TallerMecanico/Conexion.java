package TallerMecanico;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String HOST = "localhost";
    private static final String PUERTO = "1521";
    private static final String SID = "umg";
    private static final String URL = "jdbc:oracle:thin:@" + HOST + ":" + PUERTO + ":" + SID;

    private static final String USUARIO = "system";
    private static final String PASSWORD = "Umg$2026";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "No se encontró el driver de Oracle. Verifica que ojdbc.jar esté agregado a las Libraries del proyecto.",
                e
            );
        }
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
