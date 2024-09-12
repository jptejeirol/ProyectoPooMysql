package controller.implement.movibleobjects;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author sergiopro
 */
public class ItemNuevo extends Movible{
    private boolean fijo;

    public ItemNuevo(double width, double height, double depth){
        this.fijo = true;
        Box item = new Box(width, height, depth);
        item.setMaterial(new PhongMaterial(Color.WHITE));
        this.setTranslateX(0);
        this.setTranslateY(0);
        this.setTranslateZ(0);
        this.getChildren().addAll(item);




    }

    private ItemNuevo crearItemNuevo(double width, double height, double depth){
        return new ItemNuevo( width,  height,  depth);
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