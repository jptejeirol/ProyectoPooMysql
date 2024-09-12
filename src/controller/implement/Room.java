package controller.implement;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class Room extends Molde{
    
    public Room(String NombreObjeto, double base, double altura, double profundidad) {
        super(NombreObjeto, base, altura, profundidad);
    }

    @Override
    public void getArea(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
