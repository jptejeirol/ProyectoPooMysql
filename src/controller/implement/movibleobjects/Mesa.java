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
public class Mesa extends Movible {
            private boolean fijo;

    public Mesa(){
                    this.fijo = true;

    // Base
    Box base = new Box(800, 100, 800);
    base.setMaterial(new PhongMaterial(Color.BROWN));
    base.setTranslateY(-350);

    // pata1
    Box pata1 = new Box(100, 600, 100);
    pata1.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata1.setTranslateX(350);
    pata1.setTranslateZ(350);

    // pata2
    Box pata2 = new Box(100, 600, 100);
    pata2.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata2.setTranslateX(350);
    pata2.setTranslateZ(-350);

    // pata3
    Box pata3 = new Box(100, 600, 100);
    pata3.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata3.setTranslateX(-350);
    pata3.setTranslateZ(-350);

    // pata4
    Box pata4 = new Box(100, 600, 100);
    pata4.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata4.setTranslateX(-350);
    pata4.setTranslateZ(350);

    // Grupo
    this.getChildren().addAll(base, pata1, pata2, pata3, pata4);
    }
    private Mesa createMesa(){
        return new Mesa();
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