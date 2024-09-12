package controller.implement;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class RoomJFX {
    
    private String nombre;
    private String hash;
    
    public RoomJFX(String nombre, String hash){
        this.nombre = nombre;
        this.hash = hash;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public String getHash(){
        return hash;
    }    
    
}
