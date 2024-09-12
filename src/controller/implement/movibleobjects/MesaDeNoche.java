package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author sergiopro
 */
public class MesaDeNoche extends Movible {
    private boolean fijo;

    public MesaDeNoche(){
                    this.fijo = true;

    // Base
    Box base = new Box(300, 700, 400);
    base.setMaterial(new PhongMaterial(Color.BROWN));

    // Cajón1
    Box cajon1 = new Box(10, 280, 350);
    cajon1.setMaterial(new PhongMaterial(Color.DARKGRAY));
    cajon1.setTranslateY(-160);
    cajon1.setTranslateX(160);  

    Box cajon2 = new Box(10, 280, 350);
    cajon2.setMaterial(new PhongMaterial(Color.DARKGRAY));
    cajon2.setTranslateY(150);
    cajon2.setTranslateX(160);

    Box chapa2 = new Box(15, 30, 80);
    chapa2.setMaterial(new PhongMaterial(Color.BROWN));
    chapa2.setTranslateY(150);
    chapa2.setTranslateX(180);

    Box chapa1 = new Box(15, 30, 80);
    chapa1.setMaterial(new PhongMaterial(Color.BROWN));
    chapa1.setTranslateY(-160);
    chapa1.setTranslateX(180);

    // Grupo
    this.getChildren().addAll(base, cajon1, cajon2, chapa1, chapa2);
    }
    private MesaDeNoche createMesaDeNoche(){
        return new MesaDeNoche();
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