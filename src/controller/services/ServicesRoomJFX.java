package controller.services;

import controller.implement.RoomJFX;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import model.Mysql_BD;
import view.RooMakingJFX3D;
import static view.RooMakingJFX3D.roomSeleccionada;

public class ServicesRoomJFX {
    private final String tabla_Diseño = "rooms";    
    Mysql_BD bd = new Mysql_BD();
    private static List<RoomJFX> roomsJFX = new ArrayList<>();

    public static List<RoomJFX> getRooms() {
        return roomsJFX;
    }

    public static void setRooms(RoomJFX room) {
        roomsJFX.add(room);
    }

    // Eliminar la conexión persistente
    // Connection con = bd.conectar();

    public void guardar_Room(String username, RoomJFX room) {
        try (Connection con = bd.conectar(); // Crear la conexión aquí
             PreparedStatement consulta = con.prepareStatement("INSERT INTO " + this.tabla_Diseño + "(usuario, nombre, hash) VALUES(?, ?, ?)")) {
             
            String nombreObjeto = room.getNombre();
            String hash = room.getHash();
            
            consulta.setString(1, username);
            consulta.setString(2, nombreObjeto);
            consulta.setString(3, hash);
            consulta.executeUpdate();
            
            System.out.println("Se guardaron los datos correctamente.");
        } catch (SQLException ex) {
            System.out.println("Hubo un error al momento de guardar los datos. " + ex.getMessage());
        }
    }

    public List<RoomJFX> getRoomsJFXByUsuario(String usuario) {
        List<RoomJFX> roomsDb = new ArrayList<>();
        String query = "SELECT nombre, hash FROM rooms WHERE usuario = ?";

        try (Connection conn = bd.conectar(); // Crear la conexión aquí
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String hash = rs.getString("hash");

                RoomJFX roomsito = new RoomJFX(nombre, hash);
                roomsDb.add(roomsito);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomsDb;
    }
    
    private void moverCamaraHaciaAdelante(PerspectiveCamera camera, double distance) {
        // Obtener la rotación actual en el eje Y (girar izquierda/derecha) y en el eje X (girar arriba/abajo)
        double rotationY = Math.toRadians(camera.getRotate());
        double rotationX = Math.toRadians(camera.getRotationAxis() == Rotate.X_AXIS ? camera.getRotate() : 0);

        // Calcular las componentes del movimiento en los ejes X, Y, Z
        double deltaX = -Math.sin(rotationY) * distance * Math.cos(rotationX);
        double deltaY = Math.sin(rotationX) * distance;
        double deltaZ = -Math.cos(rotationY) * distance * Math.cos(rotationX);

        // Actualizar la posición de la cámara
        camera.setTranslateX(camera.getTranslateX() + deltaX);
        camera.setTranslateY(camera.getTranslateY() - deltaY);  // Restar porque el eje Y suele ir hacia abajo
        camera.setTranslateZ(camera.getTranslateZ() + deltaZ);
    }

    private void moverCamaraHaciaAtras(PerspectiveCamera camera, double distance) {
        // Obtener la rotación actual en el eje Y (girar izquierda/derecha) y en el eje X (girar arriba/abajo)
        double rotationY = Math.toRadians(camera.getRotate());
        double rotationX = Math.toRadians(camera.getRotationAxis() == Rotate.X_AXIS ? camera.getRotate() : 0);

        // Calcular las componentes del movimiento en los ejes X, Y, Z
        double deltaX = Math.sin(rotationY) * distance * Math.cos(rotationX);
        double deltaY = -Math.sin(rotationX) * distance;
        double deltaZ = Math.cos(rotationY) * distance * Math.cos(rotationX);

        // Actualizar la posición de la cámara
        camera.setTranslateX(camera.getTranslateX() + deltaX);
        camera.setTranslateY(camera.getTranslateY() - deltaY);  // Restar porque el eje Y suele ir hacia abajo
        camera.setTranslateZ(camera.getTranslateZ() + deltaZ);
    }



    
    public void MoverCamara(SubScene scene, PerspectiveCamera cameraInterna) {
        Platform.runLater(() -> scene.requestFocus());
        scene.setOnKeyPressed(event -> {
            double movementSpeed = 10.0;
            if (scene.getCamera() == cameraInterna) {
                System.out.println("Me están leyendo");
                switch (event.getCode()) {
                    case W -> moverCamaraHaciaAdelante(cameraInterna, movementSpeed);
                    case S -> moverCamaraHaciaAtras(cameraInterna, movementSpeed);
                    case A -> cameraInterna.setTranslateX(cameraInterna.getTranslateX() - movementSpeed);
                    case D -> cameraInterna.setTranslateX(cameraInterna.getTranslateX() + movementSpeed);
                }
                limitarMovimientoCamara(cameraInterna); // Llamada para asegurarse de que la cámara permanece dentro de los límites
            }
        });
    }
    
    private void limitarMovimientoCamara(PerspectiveCamera camera) {
        double minX = (-roomSeleccionada.getBase())/2; // Limites de la habitación
        double maxX = (roomSeleccionada.getBase())/2;
        double minY = (-roomSeleccionada.getAltura())/2;
        double maxY = (roomSeleccionada.getAltura())/2;
        double minZ = (-roomSeleccionada.getProfundidad())/2;
        double maxZ = (roomSeleccionada.getProfundidad())/2;

        if (camera.getTranslateX() < minX) camera.setTranslateX(minX);
        if (camera.getTranslateX() > maxX) camera.setTranslateX(maxX);
        if (camera.getTranslateY() < minY) camera.setTranslateY(minY);
        if (camera.getTranslateY() > maxY) camera.setTranslateY(maxY);
        if (camera.getTranslateZ() < minZ) camera.setTranslateZ(minZ);
        if (camera.getTranslateZ() > maxZ) camera.setTranslateZ(maxZ);
    }
}

