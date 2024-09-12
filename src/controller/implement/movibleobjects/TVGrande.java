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
public class TVGrande extends Movible{
            private boolean fijo;

    public TVGrande(){
                    this.fijo = true;

    // Base
    Box base = new Box(80, 20, 160);
    base.setMaterial(new PhongMaterial(Color.GRAY));
    base.setTranslateY(360);

    // Base1
    Box base1 = new Box(25, 50, 80);
    base1.setMaterial(new PhongMaterial(Color.GRAY));
    base1.setTranslateY(325);        

    // Pantalla
    Box pantalla = new Box(25, 600, 1200);
    pantalla.setMaterial(new PhongMaterial(Color.BLACK));


    // Grupo
    this.getChildren().addAll(base, base1, pantalla);
}
    private TVGrande createTVGrande(){
        return new TVGrande();
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