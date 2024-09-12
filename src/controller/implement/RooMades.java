package controller.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.services.ServicesUsuario;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class RooMades{
    // Crea listas para almacenar los valores de cada Room
    private List<Double> bases = new ArrayList<>();
    private List<Double> alturas = new ArrayList<>();
    private List<Double> profundidades = new ArrayList<>();
    private List<Integer> numerosDeObjetos = new ArrayList<>();
    private List<String> nombres = new ArrayList<>();

    public RooMades(List<RoomJFX> roomJFX) {
        for (int i = 0; i < roomJFX.size(); i++) {  // Corregido: Empezar desde 0
            Room room = MedidasRoom(roomJFX.get(i).getHash());

            if (room != null) {
                nombres.add(roomJFX.get(i).getNombre());
                bases.add(room.getBase());
                alturas.add(room.getAltura());
                profundidades.add(room.getProfundidad());
                numerosDeObjetos.add(NumeroDeObjetosRoom(roomJFX.get(i).getHash()));
            } else {
                System.err.println("Room no encontrado para el hash: " + roomJFX.get(i).getHash());
                // Añade valores predeterminados si el room no es encontrado
                nombres.add("Desconocido");
                bases.add(0.0);
                alturas.add(0.0);
                profundidades.add(0.0);
                numerosDeObjetos.add(0);
            }
        }
    }

    public List<Double> getBases() {
        return bases;
    }

    public void setBases(List<Double> bases) {
        this.bases = bases;
    }

    public List<Double> getAlturas() {
        return alturas;
    }

    public void setAlturas(List<Double> alturas) {
        this.alturas = alturas;
    }

    public List<Double> getProfundidades() {
        return profundidades;
    }

    public void setProfundidades(List<Double> profundidades) {
        this.profundidades = profundidades;
    }

    public List<Integer> getNumerosDeObjetos() {
        return numerosDeObjetos;
    }

    public void setNumerosDeObjetos(List<Integer> numerosDeObjetos) {
        this.numerosDeObjetos = numerosDeObjetos;
    }

    public List<String> getNombres() {
        return nombres;
    }

    public void setNombres(List<String> nombres) {
        this.nombres = nombres;
    }

    private Room decodeObjectMedidasRoom(Map<String, Object> objectData) {
        String type = (String) objectData.get("type");
        Room room = null;
        if (type.equals("Box")){
            room = new Room(
                (String) ServicesUsuario.getUsuario(),
                (double) objectData.get("width"),
                (double) objectData.get("height"),
                (double) objectData.get("depth")
                );
            return room;
        }
        return room;
    }

    private int decodeObjectNumeroDeItems(Map<String, Object> objectData, int i) {
        String type = (String) objectData.get("type");
        Room room = null;
        switch (type) {
           
            case "ItemNuevo" -> {
                i++;
                return i;
            }

            case "Sphere" ->{
                i++;
                return i;
            }
            case "Cylinder" ->{
                i++;
                return i;
            }
            case "Group" -> {
                i++;
                return i;
            }
            case "MesaDeNoche" -> {
                i++;
                return i;
            }
            case "Mueble" -> {
                i++;
                return i;
            }
            case "Armario" -> {
                i++;
                return i;
            }
            case "Mesa" -> {
                i++;
                return i;
            }
            case "Silla" -> {
                i++;
                return i;
            }
            case "CamaSimple" -> {
                i++;
                return i;
            }
            case "CamaDoble" -> {
                i++;
                return i;
            }
            case "TV" -> {
                i++;
                return i;
            }
            case "TVGrande" -> {
                i++;
                return i;
            }            
        }
        return i;
    }
    
    private Room MedidasRoom(String encodedJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        Room room = null;
        Room backupRoom = null;  // Para almacenar un room que usaremos como backup para reemplazar
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedJson);
            String json = new String(decodedBytes);
            Map<String, Object> sceneData = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            if (sceneData == null || !sceneData.containsKey("objects")) {
                System.err.println("El JSON no contiene la clave 'objects'");
                return null;
            }

            List<Map<String, Object>> objectsData = (List<Map<String, Object>>) sceneData.get("objects");
            if (objectsData == null || objectsData.isEmpty()) {
                System.err.println("La lista 'objects' está vacía o es nula.");
                return null;
            }

            for (Map<String, Object> objectData : objectsData) {
                room = (Room) decodeObjectMedidasRoom(objectData);

                if (room != null) {
                    // Si encontramos un objeto con 10.0 en alguna medida, verificamos el siguiente objeto para reemplazar esa medida
                    if (room.getBase() == 10.0 || room.getAltura() == 10.0 || room.getProfundidad() == 10.0) {
                        backupRoom = findNextValidRoom(objectsData, room);  // Buscar otro Room para tomar la medida válida
                        if (backupRoom != null) {
                            // Reemplazar solo la medida que es 10.0 con la del siguiente objeto válido
                            if (room.getBase() == 10.0 && backupRoom.getBase() != 10.0) {
                                room.setBase(backupRoom.getBase());
                            }
                            if (room.getAltura() == 10.0 && backupRoom.getAltura()!= 10.0) {
                                room.setAltura(backupRoom.getAltura());
                            }
                            if (room.getProfundidad() == 10.0 && backupRoom.getProfundidad()!= 10.0) {
                                room.setProfundidad(backupRoom.getProfundidad());
                            }
                        }
                    }
                    // Cuando encontramos un room válido con las medidas corregidas, salimos del bucle
                    break;
                }
            }

        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return room;
    }

    private Room findNextValidRoom(List<Map<String, Object>> objectsData, Room currentRoom) {
        for (Map<String, Object> objectData : objectsData) {
            Room room = (Room) decodeObjectMedidasRoom(objectData);
            // Asegurarse de que el room siguiente tenga medidas válidas
            if (room != null && room.getAltura() != currentRoom.getAltura()) {
                return room;  // Retornar el siguiente room válido
            }
        }
        return null;  // Si no se encuentra otro room válido
    }    
    private int NumeroDeObjetosRoom(String encodedJson) {
        // Aquí deberías obtener el 'encodedJson' desde un archivo o entrada del usuario
        ObjectMapper objectMapper = new ObjectMapper();
        int i = 0;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedJson);
            String json = new String(decodedBytes);
            Map<String, Object> sceneData = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> objectsData = (List<Map<String, Object>>) sceneData.get("objects");
            for (Map<String, Object> objectData : objectsData) {
                i = (int) decodeObjectNumeroDeItems(objectData, i);
                
                
            }
            return i;
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return i;
    }

}
