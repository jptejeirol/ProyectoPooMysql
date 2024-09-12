package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author sergiopro
 */
public class CamaDoble extends Movible{
    private boolean fijo;

    public CamaDoble(){
                    this.fijo = true;

    // base
    Box base = new Box(1200, 400, 1800);
    base.setMaterial(new PhongMaterial(Color.BLACK));

    // colchon
    Box colchon = new Box(1100, 200, 1900);
    colchon.setMaterial(new PhongMaterial(Color.BLUE));
    colchon.setTranslateY(-300);

    // Almohada
    Box almohada = new Box(500, 120, 300);
    almohada.setMaterial(new PhongMaterial(Color.WHITE));
    almohada.setTranslateY(-450);
    almohada.setTranslateZ(800);        

    // Grupo
    this.getChildren().addAll(base, colchon, almohada);

    // Posicionar el grupo en la escena
    this.setTranslateX(0);
    this.setTranslateY(0);
    this.setTranslateZ(0);

    }
        private CamaDoble createCamaDoble(){
        return new CamaDoble();
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