package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author sergiopro
 */
public class Silla extends Movible{
            private boolean fijo;

    public Silla(){
                    this.fijo = true;

    // Base
    Box base = new Box(400, 80, 400);
    base.setMaterial(new PhongMaterial(Color.BROWN));
    base.setTranslateY(-220);

    // Espalda
    Box espalda = new Box(70, 550, 400);
    espalda.setMaterial(new PhongMaterial(Color.BROWN));
    espalda.setTranslateX(-165);
    espalda.setTranslateY(-530);

    // pata1
    Box pata1 = new Box(50, 360, 50);
    pata1.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata1.setTranslateX(175);
    pata1.setTranslateZ(175);

    // pata2
    Box pata2 = new Box(50, 360, 50);
    pata2.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata2.setTranslateX(175);
    pata2.setTranslateZ(-175);

    // pata3
    Box pata3 = new Box(50, 360, 50);
    pata3.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata3.setTranslateX(-175);
    pata3.setTranslateZ(-175);

    // pata4
    Box pata4 = new Box(50, 360, 50);
    pata4.setMaterial(new PhongMaterial(Color.DARKGRAY));
    pata4.setTranslateX(-175);
    pata4.setTranslateZ(175);

    // Grupo
    this.getChildren().addAll(base, espalda, pata1, pata2, pata3, pata4);
    }
    private Silla createSilla(){
        return new Silla();
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