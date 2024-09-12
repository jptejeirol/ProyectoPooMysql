package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 *
 * @author sergiopro
 */
public class Sphere3D extends Movible {
    private Sphere sphere;
    private boolean fijo;
    public Sphere3D(double radius, Color color) {
        this.fijo = true;
        sphere = new Sphere(radius);
        sphere.setMaterial(new PhongMaterial(color));
        this.getChildren().add(sphere);
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
