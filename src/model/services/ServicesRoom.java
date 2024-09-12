package model.services;

import controller.implement.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Mysql_BD;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class ServicesRoom{
    private final String tabla_Diseño = "room";
    Mysql_BD bd = new Mysql_BD();
    private static List<Room> rooms= new ArrayList<>();
    
    public static List<Room> getRooms() {
        return rooms;
    }
    
    public static void setRooms(Room room) {
        rooms.add(room);
    }
    
    Connection con = bd.conectar();
    
    public void guardar_room(String username, Room room){
        try{
            PreparedStatement consulta;
            double Base = room.getBase(), Altura = room.getAltura(), Profundidad = room.getProfundidad();
            String nombreObjeto= room.getNombreObjeto();
            
            consulta = con.prepareStatement("INSERT INTO " + this.tabla_Diseño + "(usuario, nombre, base, altura, profundidad) VALUES(?, ?, ?, ?, ?)");
            consulta.setString(1, username);
            consulta.setString(2, nombreObjeto);
            consulta.setDouble(3, Base);
            consulta.setDouble(4, Altura);
            consulta.setDouble(5, Profundidad);
            consulta.executeUpdate();
            
            System.out.println("Se guardaron los datos correctamente. ");
        } 
        catch (SQLException ex){
            System.out.println("Hubo un error al momento de guardar los datos. "+ ex.getMessage());
        }
    }
    
    public void eliminar_room(String username, Room room) {
        try (Connection con = bd.conectar(); // Crear la conexión aquí
             PreparedStatement consulta = con.prepareStatement("DELETE FROM " + this.tabla_Diseño + " WHERE usuario = ? AND nombre = ?")) {

            String nombreObjeto = room.getNombreObjeto();

            // Asignar los valores a la consulta
            consulta.setString(1, username);
            consulta.setString(2, nombreObjeto);

            int filasAfectadas = consulta.executeUpdate();

            // Verificar si se eliminó algún registro
            if (filasAfectadas > 0) {
                System.out.println("El Room fue eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún Room para eliminar con esos parámetros.");
            }

        } catch (SQLException ex) {
            System.out.println("Hubo un error al eliminar el Room. " + ex.getMessage());
        }
    }
    
    public List<Room> getRoomsByUsuario(String usuario) {
        List<Room> roomsDb = new ArrayList<>();
        String query = "SELECT nombre, base, altura, profundidad FROM room WHERE usuario = ?";

        try (Connection conn = con;
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                float base = rs.getFloat("base");
                float altura = rs.getFloat("altura");
                float profundidad = rs.getFloat("profundidad");

                Room roomsito = new Room(nombre, base, altura, profundidad);
                roomsDb.add(roomsito);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomsDb;
    }
    
}