package controller.implement.movibleobjects;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;

/**
 *
 * @author sergiopro
 */
public class Movible extends Group{
    private boolean fijo = false;

        public boolean isFijo() {
            return fijo;
        }

        public void setFijo(boolean fijo) {
            this.fijo = fijo;
        }
}