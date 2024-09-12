/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

/**
 *
 * @author sergiopro
 */
public class Cylinder3D extends Movible {
    private Cylinder cylinder;
    private boolean fijo;

    public Cylinder3D(double radius, double height, Color color) {
        cylinder = new Cylinder(radius, height);
        cylinder.setMaterial(new PhongMaterial(color));
        this.getChildren().add(cylinder);
                    this.fijo = true;
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