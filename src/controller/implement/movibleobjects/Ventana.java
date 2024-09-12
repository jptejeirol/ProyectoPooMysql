/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author sergiopro
 */
public class Ventana extends Movible {
    private boolean fijo;

    public Ventana(){

    this.fijo = true;

    Box marco = new Box(5, 800, 1000);
    marco.setMaterial(new PhongMaterial(Color.BLACK));

    Box ventana = new Box(5, 780, 980);
    ventana.setMaterial(new PhongMaterial(Color.WHITE));
    ventana.setTranslateX(5);  

    this.getChildren().addAll(marco, ventana);
    }
    private Ventana createVentana(){
        return new Ventana();
    }
                            @Override
    public boolean isFijo() {
        return fijo;
    }

        @Override
    public void setFijo(boolean fijo) {
        this.fijo = fijo;
    }
}
