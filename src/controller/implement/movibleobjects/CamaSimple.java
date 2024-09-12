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
public class CamaSimple extends Movible {
    private boolean fijo;

    public CamaSimple() {
        this.fijo = true;

        // base
        Box base = new Box(800, 400, 1800);
        base.setMaterial(new PhongMaterial(Color.BLACK));

        // colchon
        Box colchon = new Box(700, 200, 1700);
        colchon.setMaterial(new PhongMaterial(Color.RED));
        colchon.setTranslateY(-300);

        // Almohada
        Box almohada = new Box(500, 120, 300);
        almohada.setMaterial(new PhongMaterial(Color.WHITE));
        almohada.setTranslateY(-450);
        almohada.setTranslateZ(700);

        // Añadir todos los elementos al grupo
        this.getChildren().addAll(base, colchon, almohada);

        // Posicionar el grupo en la escena (opcional)
        this.setTranslateX(0);
        this.setTranslateY(0);
        this.setTranslateZ(0);
    }

    private CamaSimple createCamaSimple() {
        return new CamaSimple();
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