package controller.implement;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class Item extends Molde{
    //private String diseñoId;}
    private String room;
    
    public Item(String nombreObjeto, double base, double altura, double profundidad) {
        super(nombreObjeto, base, altura, profundidad);
    }

    public String getUroom() {
        return room;
    }

    public void setUroom(String usuario) {
        this.room = usuario;
    }
    
    
    
    @Override
    public void getArea(double base, double altura) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String toString() {
        return "Ambiente{" + super.toString() + "nombre=" + '}';
    }
}