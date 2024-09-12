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
public class Mueble extends Movible{
    private boolean fijo;

    public Mueble(){
                    this.fijo = true;

    // Base
    Box base = new Box(300, 800, 1200);
    base.setMaterial(new PhongMaterial(Color.BROWN));

    // puerta1
    Box puerta1 = new Box(10, 600, 530);
    puerta1.setMaterial(new PhongMaterial(Color.DARKGRAY));
    puerta1.setTranslateX(160);
    puerta1.setTranslateZ(300);

    // puerta2
    Box puerta2 = new Box(10, 600, 530);
    puerta2.setMaterial(new PhongMaterial(Color.DARKGRAY));
    puerta2.setTranslateX(160);
    puerta2.setTranslateZ(-300);

    Box chapa2 = new Box(15, 30, 100);
    chapa2.setMaterial(new PhongMaterial(Color.BROWN));
    chapa2.setTranslateX(170);
    chapa2.setTranslateZ(-300);

    Box chapa1 = new Box(15, 30, 100);
    chapa1.setMaterial(new PhongMaterial(Color.BROWN));
    chapa1.setTranslateX(170);
    chapa1.setTranslateZ(300);

    // Grupo
    this.getChildren().addAll(base, puerta1, puerta2, chapa1, chapa2);
    }
    private Mueble createMueble(){
        return new Mueble();
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