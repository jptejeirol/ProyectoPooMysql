package controller.services;

import controller.implement.movibleobjects.Armario;
import controller.implement.movibleobjects.CamaDoble;
import controller.implement.movibleobjects.CamaSimple;
import controller.implement.movibleobjects.Mesa;
import controller.implement.movibleobjects.Movible;
import controller.implement.movibleobjects.Mueble;
import controller.implement.movibleobjects.Ventana;
import view.RooMakingJFX3D;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.util.Pair;


/**
 *
 * @author sergiopro
 */
public class ServicesIluminacion {
    private boolean contieneLuzPared1;
    private boolean contieneLuzPared2;
    private boolean contieneLuzPared3;
    private boolean contieneLuzTecho;
    private boolean contieneVentana1;
    private boolean contieneVentana2;
    private boolean contieneVentana3;  
    
    public boolean actualizarEstadoIluminacion(Group root3D) {
        contieneLuzPared1 = root3D.getChildren().stream().anyMatch(node -> "luzPared1".equals(node.getId()));
        contieneLuzPared2 = root3D.getChildren().stream().anyMatch(node -> "luzPared2".equals(node.getId()));
        contieneLuzPared3 = root3D.getChildren().stream().anyMatch(node -> "luzPared3".equals(node.getId()));
        contieneLuzTecho = root3D.getChildren().stream().anyMatch(node -> "luzTecho".equals(node.getId()));
        contieneVentana1 = root3D.getChildren().stream().anyMatch(node -> "newVentana1".equals(node.getId()));
        contieneVentana2 = root3D.getChildren().stream().anyMatch(node -> "newVentana2".equals(node.getId()));    
        contieneVentana3 = root3D.getChildren().stream().anyMatch(node -> "newVentana3".equals(node.getId()));
        
        boolean considerarIluminacion = contieneLuzPared1 || contieneLuzPared2 || contieneLuzPared3 || 
                                        contieneLuzTecho || contieneVentana1 || contieneVentana2 || contieneVentana3;
        return considerarIluminacion;
    }
    
    public Pair<Double, Double> determinarPosicionSegunIluminacion(Movible movible, double minX, double maxX, double minZ, double maxZ) {
        Random random = new Random();
        double x, z;

        if (movible instanceof Armario) {
            // Armario debe ir sobre una pared sin luz
            if (!contieneLuzPared1 && !contieneVentana1) {
                x = minX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else if (!contieneLuzPared2 && !contieneVentana2) {
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ;
            } else if (!contieneLuzPared3 && !contieneVentana3) {
                x = maxX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else {
                // Si todas las paredes tienen luz, colocar aleatoriamente
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ + (maxZ - minZ) * random.nextDouble();
            }
        } else if (movible instanceof Mueble) {
            // Mueble debe ir opuesto a una pared con luz o ventana
            if (contieneLuzPared1 || contieneVentana1) {
                x = maxX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else if (contieneLuzPared2 || contieneVentana2) {
                x = minX + (maxX - minX) * random.nextDouble();
                z = maxZ;
            } else if (contieneLuzPared3 || contieneVentana3) {
                x = minX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else {
                // Si no hay paredes con luz, colocar aleatoriamente
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ + (maxZ - minZ) * random.nextDouble();
            }
        } else if (movible instanceof CamaSimple || movible instanceof CamaDoble) {
            // Cama debe ir en una pared sin ventana preferiblemente
            if (!contieneVentana1) {
                x = minX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else if (!contieneVentana2) {
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ;
            } else if (!contieneVentana3) {
                x = maxX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else {
                // Si todas las paredes tienen ventana, colocar aleatoriamente
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ + (maxZ - minZ) * random.nextDouble();
            }
        } else if (movible instanceof Mesa) {
            // Mesa debe ir siempre sobre una pared con luz preferiblemente
            if (contieneLuzPared1 || contieneVentana1) {
                x = minX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else if (contieneLuzPared2 || contieneVentana2) {
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ;
            } else if (contieneLuzPared3 || contieneVentana3) {
                x = maxX;
                z = minZ + (maxZ - minZ) * random.nextDouble();
            } else {
                // Si no hay paredes con luz, colocar aleatoriamente
                x = minX + (maxX - minX) * random.nextDouble();
                z = minZ + (maxZ - minZ) * random.nextDouble();
            }
        } else {
            // Para otros objetos, colocar aleatoriamente
            x = minX + (maxX - minX) * random.nextDouble();
            z = minZ + (maxZ - minZ) * random.nextDouble();
        }

        return new Pair<>(x, z);
    }
    
    public void configuracionIluminacion(String pared1, String pared2, String pared3, String techo, Box wallLeft, Box wallBack, Group root3D) {
        // Limpia las configuraciones de luz existentes si es necesario
        root3D.getChildren().removeIf(node -> node instanceof PointLight || node instanceof Ventana);

        // Aplicar configuración para Pared 1
        if ("Bombillo".equals(pared1)) {
            PointLight luzPared1 = new PointLight(Color.WHITE);
            luzPared1.setTranslateX(wallBack.getTranslateX()); // Colocar la luz en la posición adecuada
            luzPared1.setTranslateY(wallBack.getTranslateY()*0.8);
            luzPared1.setTranslateZ(wallBack.getTranslateZ());
            root3D.getChildren().add(luzPared1);
        } else if ("Ventana".equals(pared1)) {
            Ventana newVentana1 = new Ventana();
            newVentana1.setTranslateX(wallBack.getTranslateX());
            newVentana1.setTranslateY(wallBack.getTranslateY());
            newVentana1.setTranslateZ(wallBack.getTranslateZ() - 5);
            RooMakingJFX3D.rotateObjectY(newVentana1);
            RooMakingJFX3D.togglePositionsLock(root3D);
            root3D.getChildren().add(newVentana1);
            PointLight luzPared1 = new PointLight(Color.WHITE);
            luzPared1.setTranslateX(wallBack.getTranslateX());
            luzPared1.setTranslateY(wallBack.getTranslateY());
            luzPared1.setTranslateZ(wallBack.getTranslateZ() - 5);
            root3D.getChildren().add(luzPared1);        

        }

        if ("Bombillo".equals(pared2)) {
            PointLight luzPared2 = new PointLight(Color.WHITE);
            luzPared2.setTranslateX(wallLeft.getTranslateX());
            luzPared2.setTranslateY(-wallLeft.getTranslateY()*0.8);
            luzPared2.setTranslateZ(wallLeft.getTranslateX());
            root3D.getChildren().add(luzPared2);
        } else if ("Ventana".equals(pared2)) {
            Ventana newVentana2 = new Ventana();
            newVentana2.setTranslateX(wallLeft.getTranslateX() + 5);
            newVentana2.setTranslateY(wallLeft.getTranslateY());
            newVentana2.setTranslateZ(wallLeft.getTranslateZ());
            RooMakingJFX3D.togglePositionsLock(root3D);
            root3D.getChildren().add(newVentana2);
            PointLight luzPared2 = new PointLight(Color.WHITE);
            luzPared2.setTranslateX(wallLeft.getTranslateX() + 5);
            luzPared2.setTranslateY(wallLeft.getTranslateY());
            luzPared2.setTranslateZ(wallLeft.getTranslateZ());
            root3D.getChildren().add(luzPared2);        

        }

        if ("Bombillo".equals(pared3)) {
            PointLight luzPared3 = new PointLight(Color.WHITE);
            luzPared3.setTranslateX(-wallLeft.getTranslateX());
            luzPared3.setTranslateY(-wallLeft.getTranslateY()*0.8);
            luzPared3.setTranslateZ(wallLeft.getTranslateZ());
            root3D.getChildren().add(luzPared3);
        } else if ("Ventana".equals(pared3)) {
            Ventana newVentana3 = new Ventana();
            newVentana3.setTranslateX(-wallLeft.getTranslateX() + 5);
            newVentana3.setTranslateY(wallLeft.getTranslateY());
            newVentana3.setTranslateZ(wallLeft.getTranslateZ());
            RooMakingJFX3D.rotateObjectY(newVentana3);
            RooMakingJFX3D.rotateObjectY(newVentana3);
            RooMakingJFX3D.togglePositionsLock(root3D);
            root3D.getChildren().add(newVentana3);
            PointLight luzPared3 = new PointLight(Color.WHITE);
            luzPared3.setTranslateX(-wallLeft.getTranslateX() + 5);
            luzPared3.setTranslateY(wallLeft.getTranslateY());
            luzPared3.setTranslateZ(wallLeft.getTranslateZ());
            root3D.getChildren().add(luzPared3);        

        }

        if ("Bombillo".equals(techo)) {
            PointLight luzTecho = new PointLight(Color.WHITE);
            luzTecho.setTranslateX(0);
            luzTecho.setTranslateY(-wallLeft.getTranslateY());
            luzTecho.setTranslateZ(0);
            root3D.getChildren().add(luzTecho);
        }
    }
    
    public void opcionesIluminacion(Box wallLeft, Box wallBack, Group root3D) {
        Stage secondaryStage = new Stage();
        
        Label pared1Label = new Label("Pared Trasera");        
        ComboBox<String> pared1ComboBox = new ComboBox<>();
        pared1ComboBox.getItems().addAll("Bombillo", "Ventana", "sin luz");
        pared1ComboBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(162,217,206), rgb(126,188,137));" +  // Degradado de verde claro a verde suave
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        

        Label pared2Label = new Label("Pared Izquierda");         
        ComboBox<String> pared2ComboBox = new ComboBox<>();
        pared2ComboBox.getItems().addAll("Bombillo", "Ventana", "sin luz");
        pared2ComboBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(162,217,206), rgb(126,188,137));" +  // Degradado de verde claro a verde suave
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        

        Label pared3Label = new Label("Pared Derecha");                 
        ComboBox<String> pared3ComboBox = new ComboBox<>();
        pared3ComboBox.getItems().addAll("Bombillo", "Ventana", "sin luz");
        pared3ComboBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(162,217,206), rgb(126,188,137));" +  // Degradado de verde claro a verde suave
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        

        Label techoLabel = new Label("Techo");        
        ComboBox<String> techoComboBox = new ComboBox<>();
        techoComboBox.getItems().addAll("Bombillo", "sin luz");
        techoComboBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(162,217,206), rgb(126,188,137));" +  // Degradado de verde claro a verde suave
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        

        Button aplicarButton = new Button("Aplicar");
        aplicarButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        ); 
        aplicarButton.setVisible(false); // Inicialmente oculto

        pared1ComboBox.setOnAction(e -> verificarSeleccion(pared1ComboBox, pared2ComboBox, pared3ComboBox, techoComboBox, aplicarButton));
        pared1ComboBox.setOnAction(e -> verificarSeleccion(pared1ComboBox, pared2ComboBox, pared3ComboBox, techoComboBox, aplicarButton));
        pared3ComboBox.setOnAction(e -> verificarSeleccion(pared1ComboBox, pared2ComboBox, pared3ComboBox, techoComboBox, aplicarButton));
        techoComboBox.setOnAction(e -> verificarSeleccion(pared1ComboBox, pared2ComboBox, pared3ComboBox, techoComboBox, aplicarButton));
        
        aplicarButton.setOnAction(e -> {
        configuracionIluminacion(pared1ComboBox.getValue(), pared2ComboBox.getValue(), pared3ComboBox.getValue(), techoComboBox.getValue(), wallLeft, wallBack, root3D);
        secondaryStage.close();
    });
         

        VBox secondaryLayout = new VBox();
        secondaryLayout.setAlignment(Pos.CENTER);
        secondaryLayout.getChildren().addAll(pared1Label, pared1ComboBox, pared2Label, pared2ComboBox, pared3Label, pared3ComboBox, techoLabel, techoComboBox, aplicarButton);
        secondaryLayout.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, rgb(245,245,220), rgb(162,217,206)); " +  // Fondo degradado de beige a verde claro
            "-fx-border-color: rgb(210,180,140); " +  // Color del borde tierra
            "-fx-border-width: 2px; " +  // Grosor del borde
            "-fx-border-radius: 10px; " +  // Bordes redondeados
            "-fx-background-radius: 10px; " +  // Bordes redondeados para el fondo
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 0);"  // Efecto de sombra
        );

        Scene iluminacionDialog = new Scene(secondaryLayout, 300, 300);
        secondaryStage.setScene(iluminacionDialog);
        secondaryStage.sizeToScene();
        secondaryStage.setTitle("Opciones de Iluminación");
        secondaryStage.show();
    }
    
    private void verificarSeleccion(ComboBox<String> pared1ComboBox, ComboBox<String> pared2ComboBox, ComboBox<String> pared3ComboBox, ComboBox<String> techoComboBox, Button aplicarButton) {
        if (pared1ComboBox.getValue() != null && 
            pared2ComboBox.getValue() != null && 
            pared3ComboBox.getValue() != null && 
            techoComboBox.getValue() != null) {
            aplicarButton.setVisible(true);
        } else {
            aplicarButton.setVisible(false);
        }    
    }
    
}
