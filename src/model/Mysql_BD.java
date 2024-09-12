package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class Mysql_BD {
    
    private static Connection conn;
    private static final String driver ="com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/prueba1";
    private final String user = "root";
    private final String password = "";

    public  Connection conectar() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            if(conn!=null){
                System.out.println("Conexion establecida :D en: " + this.toString());
            }
            return conn;
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
            throw new RuntimeException(e);
            //return null;
        }
    }
}
