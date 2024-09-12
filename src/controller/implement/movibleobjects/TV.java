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
public class TV extends Movible {
            private boolean fijo;

    public TV(){
                    this.fijo = true;

    // Base
    Box base = new Box(80, 20, 160);
    base.setMaterial(new PhongMaterial(Color.GRAY));
    base.setTranslateY(210);

    // Base1
    Box base1 = new Box(25, 50, 80);
    base1.setMaterial(new PhongMaterial(Color.GRAY));
    base1.setTranslateY(175);        

    // Pantalla
    Box pantalla = new Box(25, 300, 600);
    pantalla.setMaterial(new PhongMaterial(Color.BLACK));


    // Grupo
    this.getChildren().addAll(base, base1, pantalla);
    }
    private TV createTV(){
        return new TV();
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