package controller;

import java.io.FileNotFoundException;
import model.services.ServicesItem;
import model.services.ServicesRoom;
import model.services.ServicesUsuario;
import view.Login;
/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */
public class MenuNotView {
    public static void main(String[] args) throws FileNotFoundException {
        ServicesUsuario servUsuario = new ServicesUsuario();
        ServicesRoom servRoom = new ServicesRoom();
        ServicesItem servItem = new ServicesItem();
        Login Logeo = new Login();
        Logeo.setVisible(true);
        Logeo.setLocationRelativeTo(null);
        
    }
}
