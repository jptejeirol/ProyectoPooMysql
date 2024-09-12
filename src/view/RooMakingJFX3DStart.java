package view;

import controller.implement.CrearItem;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.TextArea;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.implement.Item;
import controller.implement.Room;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import model.services.ServicesRoomJFX;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class RooMakingJFX3DStart extends Application {
    
    RooMakingJFX3D jfx3D = new RooMakingJFX3D();

    private static PerspectiveCamera cameraExterna, cameraInterna;
    private double mouseX, mouseY;
    private double rotateX = 0, rotateY = 0;
    private double rotateSpeed = 0.2;
    private double zoomSpeed = 1.1;
    private double gravity = 5;
    private double floorY = 1000;
    
    private Group root3D;
    private static SubScene subScene;
    private Shape3D selectedObject;
    private Group selectedGroup;
    private TextArea hashArea;
    private double objectMouseOffsetX, objectMouseOffsetY, objectMouseOffsetZ;
    

    private boolean positionsLocked = false;
    
    private Box floor;
    private Box wallBack;
    private Box wallLeft;
    
    private boolean contieneLuzPared1;
    private boolean contieneLuzPared2;
    private boolean contieneLuzPared3;
    private boolean contieneLuzTecho;
    private boolean contieneVentana1;
    private boolean contieneVentana2;
    private boolean contieneVentana3;    
    
    private CrearItem crearItem;
    
    public static Room roomSeleccionada;
    public static Item itemSeleccionado;
    private List<Item> ItemSeleccionados= new ArrayList<>();

    public void setRoomSeleccionada(Room roomSeleccionada) {
        this.roomSeleccionada = roomSeleccionada;
    }
    
    public void setItemSeleccionado(Item itemSeleccionada) {
        this.itemSeleccionado = itemSeleccionada;
    }
    
    private ObjectMapper objectMapper = new ObjectMapper();

    VBox controlPanel= jfx3D.createControlPanel(subScene, cameraInterna, cameraExterna, root3D);
    
    ServicesRoomJFX servRoomJFX = new ServicesRoomJFX();    

    @Override
    public void start(Stage primaryStage) {
        // Crear la escena 3D
        root3D = new Group();
        subScene = new SubScene(root3D, 600, 400, true, javafx.scene.SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BEIGE);

        // Configurar la cámara 1
        cameraExterna = new PerspectiveCamera(true);
        cameraExterna.setTranslateZ(-2000);
        cameraExterna.setTranslateY(500);
        cameraExterna.setNearClip(1);
        cameraExterna.setFarClip(10000);
        cameraExterna.setFieldOfView(50);
        subScene.setCamera(cameraExterna);
        
        // Cámara interna
        cameraInterna = new PerspectiveCamera(true);
        cameraInterna.setNearClip(1);
        cameraInterna.setFarClip(10000);
        cameraInterna.setTranslateX(0); // Inicia dentro del cuarto
        cameraInterna.setTranslateY(0);
        cameraInterna.setTranslateZ(0);
        cameraInterna.setRotationAxis(Rotate.Y_AXIS);  // Eje de rotación
        cameraInterna.setRotate(0);  // Inicializa la rotación
        
        // Crear panel de control
        VBox controlPanel = jfx3D.createControlPanel(subScene, cameraInterna, cameraExterna, root3D);
        


        // Crear objetos iniciales
        //root3D.getChildren().addAll(floor, wallBack, wallLeft);

        // Configurar los eventos del mouse para la subescena
        subScene.setOnMousePressed(this::handleMousePressed);
        subScene.setOnMouseDragged(this::handleMouseDragged);
        subScene.setOnScroll(this::handleScroll);

        

        // Crear layout principal
        HBox mainLayout = new HBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(subScene, controlPanel);
        mainLayout.setPrefHeight(600);
        mainLayout.setPrefWidth(400);
        
        mainLayout.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, rgb(245,245,220), rgb(162,217,206)); " +  // Fondo degradado de beige a verde claro
            "-fx-border-color: rgb(210,180,140); " +  // Color del borde tierra
            "-fx-border-width: 2px; " +  // Grosor del borde
            "-fx-border-radius: 10px; " +  // Bordes redondeados
            "-fx-background-radius: 10px; " +  // Bordes redondeados para el fondo
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 0);"  // Efecto de sombra
        );        

        // Crear la escena principal
        Scene mainScene = new Scene(mainLayout, 800, 500);
       
        //metodo camara:
        servRoomJFX.MoverCamara(subScene, cameraInterna);


        // Solicitar el enfoque en el nodo raíz
        root3D.requestFocus();

        // Iniciar el sistema de física
        jfx3D.startPhysics(root3D);
        
        floor = new Box(0, 0, 0);
        wallBack = new Box(0, 0, 0);
        wallLeft = new Box(0, 0, 0);    
        
        subScene.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                System.out.println("La subescena tiene el enfoque.");
            } else {
                System.out.println("La subescena perdió el enfoque.");
            }
        });

    }
    
    private void handleMousePressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        if (selectedObject == null) {
            double deltaX = event.getSceneX() - mouseX;
            double deltaY = event.getSceneY() - mouseY;
            rotateGroup(deltaX, deltaY);
        }

        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }
    
    private void rotateGroup(double deltaX, double deltaY) {
        rotateX += deltaX * rotateSpeed;
        rotateY -= deltaY * rotateSpeed;
        rotateY = Math.max(-89, Math.min(89, rotateY));

        root3D.getTransforms().clear();
        root3D.getTransforms().addAll(
            new Rotate(rotateX, Rotate.Y_AXIS),
            new Rotate(rotateY, Rotate.X_AXIS)
        );
    }
    
    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        cameraExterna.setTranslateZ(cameraExterna.getTranslateZ() + delta * zoomSpeed);
    }
    
    public static PerspectiveCamera getCameraExterna() {
        return cameraExterna;
    }
    
    public static SubScene getSubScene() {
        return subScene;
    }
}