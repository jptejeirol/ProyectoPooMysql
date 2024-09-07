package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.geometry.Bounds;
import javafx.scene.SubScene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.geometry.Point3D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.TextArea;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import controller.implement.Item;
import controller.implement.Room;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Transform;

import controller.implement.RoomJFX;
import controller.services.ServicesRoom;
import controller.services.ServicesItem;
import controller.services.ServicesRoomJFX;
import controller.services.ServicesUsuario;
import java.util.Optional;
import java.util.Random;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Material;
import javafx.stage.Modality;
import javafx.util.Pair;

public class RooMakingJFX3D extends Application {

    private PerspectiveCamera cameraExterna, cameraInterna;
    private double mouseX, mouseY;
    private double rotateX = 0, rotateY = 0;
    private double rotateSpeed = 0.2;
    private double zoomSpeed = 1.1;
    private double gravity = 5;
    private double floorY = 1000;
    
    private Group root3D;
    private SubScene subScene;
    private Shape3D selectedObject;
    private Group selectedGroup;
    private TextArea hashArea;
    private double objectMouseOffsetX, objectMouseOffsetY, objectMouseOffsetZ;
    
    private List<Movible> shapes = new ArrayList<>(); //necesaria para fijar posiciones
    private Movible selectedShape = null;
    private boolean positionsLocked = false; // Variable para controlar el bloqueo de posiciones
    
    private Box floor;
    private Box wallBack;
    private Box wallLeft;
    private Movible newObjectItem;
    
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
            // Crear panel de control
    VBox controlPanel= createControlPanel(subScene, cameraInterna, cameraExterna);
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
        VBox controlPanel = createControlPanel(subScene, cameraInterna, cameraExterna);
        


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
        startPhysics();
        
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

    private VBox createControlPanel(SubScene scene, PerspectiveCamera cameraInternal, PerspectiveCamera cameraExternal) {
        VBox controlPanel = new VBox(10);       
        controlPanel.setPadding(new Insets(10));
        
        Button switchCameraButton = new Button("Cambiar cámara");
        switchCameraButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );        

        switchCameraButton.setOnAction(event -> {
            if (scene.getCamera() == cameraExternal) {
                scene.setCamera(cameraInternal);
                cameraInterna.setTranslateX(0); 
                cameraInterna.setTranslateY(0);
                cameraInterna.setTranslateZ(0);
                cameraInterna.setRotate(0);
                subScene.requestFocus();
                
            } else {
                scene.setCamera(cameraExternal);
                scene.setCursor(Cursor.DEFAULT);
                subScene.requestFocus();
            }
        });
        
        Button roomButton = new Button("Crear Room");
        roomButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );        
        roomButton.setOnAction(e -> showRoomDialog()) ; 
        
        Button itemButton = new Button("Crear Item");
        itemButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );
        itemButton.setOnAction(e -> showItemDialog() );
        
        /*Label titleLabel = new Label("Crear Item:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: rgb(210,180,140);"); // Color tierra        
        ComboBox<String> objectSelector = new ComboBox<>();
        objectSelector.getItems().addAll("Cubo", "Esfera", "Cilindro");
        objectSelector.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(245,245,220), rgb(162,217,206));" +  // Degradado de beige claro a verde claro
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        
        objectSelector.setOnAction(e -> showDimensionDialog(objectSelector.getValue())); 
        */
        Button itemsButton = new Button("Modelado Items");
        itemsButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );
        itemsButton.setOnAction(e -> abrirCrearItem());        
        
        Label groupLabel = new Label("Seleccionar Item:");
        groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: rgb(210,180,140);"); // Color tierra        
        ComboBox<String> groupSelector = new ComboBox<>();
        groupSelector.getItems().addAll("Cama Simple", "Cama Doble", "Mesa de Noche", "Armario", "Mueble", "Mesa", "Silla", "Televisor", "Televisor Grande");
        groupSelector.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(162,217,206), rgb(126,188,137));" +  // Degradado de verde claro a verde suave
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        
        groupSelector.setOnAction(e -> addGroup(groupSelector.getValue()));
        
        // Botón de iluminación
        Button iluminacionButton = new Button("Iluminación");
        iluminacionButton.setStyle(
            "-fx-background-color: rgb(245,245,220);" +  // Beige claro
            "-fx-text-fill: black;" +  // Texto en negro
            "-fx-font-weight: bold;" +
            "-fx-border-color: rgb(210,180,140);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        iluminacionButton.setOnAction(e -> opcionesIluminacion(wallLeft, wallBack));        
        
        Button saveButton = new Button("Guardar");
        saveButton.setStyle(
            "-fx-background-color: rgb(126,188,137);" +  // Verde suave
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: rgb(126,188,137);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );        
        saveButton.setOnAction(e -> {
            // Crear un cuadro de diálogo para solicitar el nombre del Room
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Guardar Room");
            dialog.setHeaderText("Ingrese el nombre para el Room:");
            dialog.setContentText("Nombre:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(roomName -> {
                // Llamar al método para obtener el JSON codificado
                String encodedJson = saveScene(); // Asegúrate de que saveScene() devuelva el JSON codificado como String

                // Crear un objeto para guardar el nombre del Room y el JSON codificado
                RoomJFX roomData = new RoomJFX(roomName, encodedJson);

                // Aquí puedes guardar roomData en una lista, base de datos, etc.
                servRoomJFX.guardar_diseño(ServicesUsuario.getUsuario(), roomData);
                ServicesRoomJFX.setRooms(roomData);           
            });
        });       

        Button loadButton = new Button("Cargar");
        loadButton.setStyle(
            "-fx-background-color: rgb(210,180,140);" +  // Color tierra
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: rgb(210,180,140);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );        
        loadButton.setOnAction(e -> showCargarDialog());        
        
        Button fijarobjetos = new Button("Fijar Objetos");
        fijarobjetos.setStyle(
            "-fx-background-color: rgb(162,217,206);" +  // Verde claro
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: rgb(162,217,206);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );        
        fijarobjetos.setOnAction(e -> {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Ha entrado al modo fijar objetos, a partir de ahora podrá "
                    + "posicionar los objetos donde quiere que se fijen.");
            alert.showAndWait();

            controlPanel.getChildren().removeAll(loadButton,saveButton);
            Button hecho = new Button("hecho");
                hecho.setStyle(
                    "-fx-background-color: rgb(126,188,137);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-border-color: rgb(126,188,137);" +
                    "-fx-border-radius: 5;" +
                    "-fx-background-radius: 5;"
                );            
            hecho.setOnAction(ev -> {
            togglePositionsLock();
  
            controlPanel.getChildren().remove(hecho);            
            controlPanel.getChildren().addAll(loadButton,saveButton);
            });   
            

            // Agregar elementos actualizados
            controlPanel.getChildren().add(hecho);
            controlPanel.requestLayout(); // Forzar actualización de la disposición
                });
         });
            Button agregarobjetos = new Button("agregar para ordenar");
            agregarobjetos.setStyle(
                "-fx-background-color: rgb(126,188,137);" +  // Verde suave
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: rgb(126,188,137);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
            );            
            agregarobjetos.setOnAction(e -> {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Ha entrado al modo ordenar objetos, a partir de ahora podrá "
                    + "agregar los objetos que desea que se ordenen.");
            alert.showAndWait();
            });
            Button ordenarbtn = new Button("Ordenar");
            ordenarbtn.setStyle(
                "-fx-background-color: rgb(162,217,206);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: rgb(162,217,206);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
            );            
            ordenarbtn.setOnAction(ev -> {
            OrdenarObjetos();
            });   
            

            // Agregar elementos actualizados
            controlPanel.getChildren().add(ordenarbtn);
            controlPanel.requestLayout(); // Forzar actualización de la disposición
            
            });
        controlPanel.setAlignment(Pos.CENTER);            
        controlPanel.getChildren().addAll(roomButton, itemButton, itemsButton, fijarobjetos, groupLabel, groupSelector, saveButton, loadButton, agregarobjetos, switchCameraButton, iluminacionButton);
        controlPanel.setStyle(
            "-fx-background-color: rgb(245,245,220);" +  // Fondo beige claro
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-width: 2px;" +  // Grosor del borde
            "-fx-border-radius: 10px;"  // Bordes redondeados
        );        
        return controlPanel;
    }

private void OrdenarObjetos() {
    Random random = new Random();
    double rangoMinX = -wallBack.getWidth() / 2;
    double rangoMaxX = wallBack.getWidth() / 2;
    double rangoMinZ = -wallLeft.getDepth() / 2;
    double rangoMaxZ = wallLeft.getDepth() / 2;
    double margenSeguridad = 50; // Margen de seguridad para evitar que los objetos toquen las paredes
    List<Movible> colocados = new ArrayList<>();
    List<Pair<Double, Double>> espaciosOcupadosX = new ArrayList<>();
    List<Pair<Double, Double>> espaciosOcupadosZ = new ArrayList<>();

    // Verificación de la presencia de luces y ventanas
    boolean contieneLuzPared1 = root3D.getChildren().stream().anyMatch(node -> "luzPared1".equals(node.getId()));
    boolean contieneLuzPared2 = root3D.getChildren().stream().anyMatch(node -> "luzPared2".equals(node.getId()));
    boolean contieneLuzPared3 = root3D.getChildren().stream().anyMatch(node -> "luzPared3".equals(node.getId()));
    boolean contieneLuzTecho = root3D.getChildren().stream().anyMatch(node -> "luzTecho".equals(node.getId()));
    boolean contieneVentana1 = root3D.getChildren().stream().anyMatch(node -> "newVentana1".equals(node.getId()));
    boolean contieneVentana2 = root3D.getChildren().stream().anyMatch(node -> "newVentana2".equals(node.getId()));
    boolean contieneVentana3 = root3D.getChildren().stream().anyMatch(node -> "newVentana3".equals(node.getId()));

    for (javafx.scene.Node node : root3D.getChildren()) {
        if (node instanceof Movible) {
            Movible movible = (Movible) node;
            if (movible instanceof Ventana) {
                movible.setFijo(positionsLocked);
            }            
            if (!movible.isFijo()) {
                continue;
            }

            boolean solapado;
            int intentos = 0;
            int maxIntentos = 100;

            do {
                solapado = false;
                intentos++;
                if (intentos > maxIntentos) {
                    System.out.println("No se encontró una posición adecuada tras " + maxIntentos + " intentos.");
                    break;
                }

                Bounds dimensiones = movible.getBoundsInParent();
                double anchura = dimensiones.getWidth();
                double profundidad = dimensiones.getDepth();
                double altura = dimensiones.getHeight();

                double efectivoMinX = rangoMinX + anchura / 2 + margenSeguridad;
                double efectivoMaxX = rangoMaxX - anchura / 2 - margenSeguridad;
                double efectivoMinZ = rangoMinZ + profundidad / 2 + margenSeguridad;
                double efectivoMaxZ = rangoMaxZ - profundidad / 2 - margenSeguridad;

                double nuevaX = 0, nuevaZ = 0;
                int rotacion = 0;

                // Restricciones basadas en el tipo de objeto y la iluminación/ventanas
                if (movible instanceof Armario) {
                    // Armario: debe ir en una pared sin luz
                    if (!contieneLuzPared1) {
                        nuevaX = rangoMinX;
                        rotacion = 0;
                    } else if (!contieneLuzPared2) {
                        nuevaZ = rangoMinZ;
                        rotacion = 270;
                    } else {
                        nuevaX = rangoMaxX;
                        rotacion = 180;
                    }
                } else if (movible instanceof Mueble) {
                    // Mueble: debe ir en una pared opuesta a una pared con luz o ventana
                    if (contieneLuzPared1 || contieneVentana1) {
                        nuevaX = rangoMaxX;
                        rotacion = 180;
                    } else if (contieneLuzPared2 || contieneVentana2) {
                        nuevaZ = rangoMaxZ;
                        rotacion = 90;
                    } else {
                        nuevaX = rangoMinX;
                        rotacion = 0;
                    }
                } else if (movible instanceof CamaSimple || movible instanceof CamaDoble) {
                    // Cama: preferentemente en una pared sin ventana
                    if (!contieneVentana1) {
                        nuevaX = rangoMinX;
                        rotacion = 0;
                    } else if (!contieneVentana2) {
                        nuevaZ = rangoMinZ;
                        rotacion = 270;
                    } else {
                        nuevaX = rangoMaxX;
                        rotacion = 180;
                    }
                } else if (movible instanceof Mesa) {
                    // Mesa: preferentemente en una pared con luz
                    if (contieneLuzPared1) {
                        nuevaX = rangoMinX;
                        rotacion = 0;
                    } else if (contieneLuzPared2) {
                        nuevaZ = rangoMinZ;
                        rotacion = 270;
                    } else {
                        nuevaX = rangoMaxX;
                        rotacion = 180;
                    }
                } else {
                    // Otros objetos sin restricciones específicas
                    if (random.nextBoolean()) {
                        nuevaX = random.nextBoolean() ? efectivoMinX : efectivoMaxX;
                        nuevaZ = efectivoMinZ + (efectivoMaxZ - efectivoMinZ) * random.nextDouble();
                        rotacion = (nuevaX == efectivoMinX) ? 0 : 180;
                    } else {
                        nuevaZ = random.nextBoolean() ? efectivoMinZ : efectivoMaxZ;
                        nuevaX = efectivoMinX + (efectivoMaxX - efectivoMinX) * random.nextDouble();
                        rotacion = (nuevaZ == efectivoMinZ) ? 270 : 90;
                    }
                }

                movible.setRotationAxis(Rotate.Y_AXIS);
                movible.setRotate(rotacion);

                // Verificar solapamiento
                for (Movible colocado : colocados) {
                    if (isOverlapping(movible, colocado)) {
                        solapado = true;
                        break;
                    }
                }

                if (!solapado) {
                    movible.setTranslateX(nuevaX);
                    movible.setTranslateZ(nuevaZ);
                    movible.setTranslateY(wallBack.getHeight() / 2 - altura / 2);
                    colocados.add(movible);
                    espaciosOcupadosX.add(new Pair<>(nuevaX - anchura / 2, nuevaX + anchura / 2));
                    espaciosOcupadosZ.add(new Pair<>(nuevaZ - profundidad / 2, nuevaZ + profundidad / 2));
                }
            } while (solapado);
        }
    }
}
    
/*private void OrdenarObjetos() {
    Random random = new Random();
    double rangoMinX = -wallBack.getWidth() / 2;
    double rangoMaxX = wallBack.getWidth() / 2;
    double rangoMinZ = -wallLeft.getDepth() / 2;
    double rangoMaxZ = wallLeft.getDepth() / 2;
    double margenSeguridad = 50; // Margen de seguridad para evitar que los objetos toquen las paredes
    List<Movible> colocados = new ArrayList<>();
    List<Pair<Double, Double>> espaciosOcupadosX = new ArrayList<>();
    List<Pair<Double, Double>> espaciosOcupadosZ = new ArrayList<>();
    
    boolean contieneLuzPared1 = root3D.getChildren().stream()
    .anyMatch(node -> "luzPared1".equals(node.getId()));
    boolean contieneLuzPared2 = root3D.getChildren().stream()
    .anyMatch(node -> "luzPared2".equals(node.getId()));
    boolean contieneLuzPared3 = root3D.getChildren().stream()
    .anyMatch(node -> "luzPared3".equals(node.getId()));
    boolean contieneLuzTecho = root3D.getChildren().stream()
    .anyMatch(node -> "luzTecho".equals(node.getId()));
    boolean contieneVentana1 = root3D.getChildren().stream()
    .anyMatch(node -> "newVentana1".equals(node.getId()));
    boolean contieneVentana2 = root3D.getChildren().stream()
    .anyMatch(node -> "newVentana3".equals(node.getId()));    
    boolean contieneVentana3 = root3D.getChildren().stream()
    .anyMatch(node -> "newVentana3".equals(node.getId()));
    
    for (javafx.scene.Node node : root3D.getChildren()) {
        if (node instanceof Movible) {
            Movible movible = (Movible) node;
            if (movible instanceof Ventana) {
                movible.setFijo(positionsLocked);
            }
            if (!movible.isFijo()) {
                continue;
            }

            boolean solapado;
            int intentos = 0; // Contador de intentos
            int maxIntentos = 100; // Número máximo de intentos

            do {
                solapado = false;
                intentos++; // Incrementar el contador en cada intento

                if (intentos > maxIntentos) {
                    System.out.println("No se encontró una posición adecuada para el objeto tras " + maxIntentos + " intentos.");
                    break; // Romper el ciclo si se exceden los intentos permitidos
                }

                Bounds dimensiones = movible.getBoundsInParent();
                double anchura = dimensiones.getWidth();
                double profundidad = dimensiones.getDepth();
                double altura = dimensiones.getHeight();

                // Calcular límites efectivos considerando las dimensiones del objeto
                double efectivoMinX = rangoMinX + anchura / 2 + margenSeguridad;
                double efectivoMaxX = rangoMaxX - anchura / 2 - margenSeguridad;
                double efectivoMinZ = rangoMinZ + profundidad / 2 + margenSeguridad;
                double efectivoMaxZ = rangoMaxZ - profundidad / 2 - margenSeguridad;

                double nuevaX, nuevaZ;
                int rotacion;

                // Determinar posición y rotación
                if (random.nextBoolean()) {
                    nuevaX = random.nextBoolean() ? efectivoMinX : efectivoMaxX;
                    nuevaZ = efectivoMinZ + (efectivoMaxZ - efectivoMinZ) * random.nextDouble();
                    rotacion = (nuevaX == efectivoMinX) ? 0 : 180;
                } else {
                    nuevaZ = random.nextBoolean() ? efectivoMinZ : efectivoMaxZ;
                    nuevaX = efectivoMinX + (efectivoMaxX - efectivoMinX) * random.nextDouble();
                    rotacion = (nuevaZ == efectivoMinZ) ? 270 : 90;
                }

                movible.setRotationAxis(Rotate.Y_AXIS);
                movible.setRotate(rotacion);

                // Verificar solapamiento
                for (Movible colocado : colocados) {
                    if (isOverlapping(movible, colocado)) {
                        solapado = true;
                        break;
                    }
                }

                if (!solapado) {
                    movible.setTranslateX(nuevaX);
                    movible.setTranslateZ(nuevaZ);
                    movible.setTranslateY(wallBack.getHeight() / 2 - altura / 2);
                    colocados.add(movible);

                    // Actualizar espacios ocupados
                    espaciosOcupadosX.add(new Pair<>(nuevaX - anchura / 2, nuevaX + anchura / 2));
                    espaciosOcupadosZ.add(new Pair<>(nuevaZ - profundidad / 2, nuevaZ + profundidad / 2));
                }
            } while (solapado);
        }
    }
}*/


private boolean isOverlapping(Movible obj1, Movible obj2) {
    Bounds bounds1 = obj1.getBoundsInParent();
    Bounds bounds2 = obj2.getBoundsInParent();
    return bounds1.intersects(bounds2);
}

  class Resultado {
    private double limiteinferior;
    private double limitesuperior;

    public Resultado(double limiteinferior, double limitesuperior) {
        this.limiteinferior = limiteinferior;
        this.limitesuperior = limitesuperior;
    }

    public double limiteinferior() {
        return limiteinferior;
    }

    public double limitesuperior() {
        return limitesuperior;
    }
}
    public Resultado realizarCalculos(double a, double anchoobjeto) {
    double limiteinferior =-50+a-anchoobjeto/2; // Primer cálculo, por ejemplo, suma
    double limitesuperior = 50+a+anchoobjeto/2;  // Segundo cálculo, por ejemplo, multiplicación
    return new Resultado(limiteinferior, limitesuperior);
}

    public boolean isOverlapping(Node objeto1, Node objeto2) {
    Bounds bounds1 = objeto1.getBoundsInParent();
    Bounds bounds2 = objeto2.getBoundsInParent();

    return bounds1.intersects(bounds2);
}    
    
        public abstract class Movible extends Group {
        private boolean fijo = false;

        public boolean isFijo() {
            return fijo;
        }

        public void setFijo(boolean fijo) {
            this.fijo = fijo;
        }
    }
    
   class Sphere3D extends Movible {
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

    // Cylinder implementation
    class Cylinder3D extends Movible {
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
    
    class CamaSimple extends Movible {
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
    
    class ItemNuevo extends Movible{
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
    
    class CamaDoble extends Movible{
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
    
    class MesaDeNoche extends Movible {
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
    
    class Armario extends Movible {
                private boolean fijo;

        public Armario(){
                        this.fijo = true;

        // Base
        Box base = new Box(400, 2000, 1200);
        base.setMaterial(new PhongMaterial(Color.BROWN));

        // puerta1
        Box puerta1 = new Box(10, 1800, 530);
        puerta1.setMaterial(new PhongMaterial(Color.DARKGRAY));
        puerta1.setTranslateX(210);
        puerta1.setTranslateZ(280);
        
        // puerta2
        Box puerta2 = new Box(10, 1800, 530);
        puerta2.setMaterial(new PhongMaterial(Color.DARKGRAY));
        puerta2.setTranslateX(210);
        puerta2.setTranslateZ(-280);
        
        Box chapa2 = new Box(15, 80, 30);
        chapa2.setMaterial(new PhongMaterial(Color.BROWN));
        chapa2.setTranslateX(220);
        chapa2.setTranslateZ(-80);
        
        Box chapa1 = new Box(15, 80, 30);
        chapa1.setMaterial(new PhongMaterial(Color.BROWN));
        chapa1.setTranslateX(220);
        chapa1.setTranslateZ(80);

        // Grupo
        this.getChildren().addAll(base, puerta1, puerta2, chapa1, chapa2);

    }
        private Armario createArmario(){
            return new Armario();
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
    
    class Mueble extends Movible{
                private boolean fijo;

        public Mueble(){
                        this.fijo = true;

        // Base
        Box base = new Box(300, 800, 1200);
        base.setMaterial(new PhongMaterial(Color.BROWN));

        // puerta1
        Box puerta1 = new Box(10, 600, 530);
        puerta1.setMaterial(new PhongMaterial(Color.DARKGRAY));
        puerta1.setTranslateX(160);
        puerta1.setTranslateZ(300);
        
        // puerta2
        Box puerta2 = new Box(10, 600, 530);
        puerta2.setMaterial(new PhongMaterial(Color.DARKGRAY));
        puerta2.setTranslateX(160);
        puerta2.setTranslateZ(-300);
        
        Box chapa2 = new Box(15, 30, 100);
        chapa2.setMaterial(new PhongMaterial(Color.BROWN));
        chapa2.setTranslateX(170);
        chapa2.setTranslateZ(-300);
        
        Box chapa1 = new Box(15, 30, 100);
        chapa1.setMaterial(new PhongMaterial(Color.BROWN));
        chapa1.setTranslateX(170);
        chapa1.setTranslateZ(300);

        // Grupo
        this.getChildren().addAll(base, puerta1, puerta2, chapa1, chapa2);
    }
        private Mueble createMueble(){
            return new Mueble();
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
    
    class Mesa extends Movible {
                private boolean fijo;

        public Mesa(){
                        this.fijo = true;

        // Base
        Box base = new Box(800, 100, 800);
        base.setMaterial(new PhongMaterial(Color.BROWN));
        base.setTranslateY(-350);

        // pata1
        Box pata1 = new Box(100, 600, 100);
        pata1.setMaterial(new PhongMaterial(Color.DARKGRAY));
        pata1.setTranslateX(350);
        pata1.setTranslateZ(350);
        
        // pata2
        Box pata2 = new Box(100, 600, 100);
        pata2.setMaterial(new PhongMaterial(Color.DARKGRAY));
        pata2.setTranslateX(350);
        pata2.setTranslateZ(-350);
        
        // pata3
        Box pata3 = new Box(100, 600, 100);
        pata3.setMaterial(new PhongMaterial(Color.DARKGRAY));
        pata3.setTranslateX(-350);
        pata3.setTranslateZ(-350);
        
        // pata4
        Box pata4 = new Box(100, 600, 100);
        pata4.setMaterial(new PhongMaterial(Color.DARKGRAY));
        pata4.setTranslateX(-350);
        pata4.setTranslateZ(350);

        // Grupo
        this.getChildren().addAll(base, pata1, pata2, pata3, pata4);
        }
        private Mesa createMesa(){
            return new Mesa();
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
    
    class Silla extends Movible{
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
    
    class TV extends Movible {
                private boolean fijo;

        public TV(){
                        this.fijo = true;

        // Base
        Box base = new Box(80, 20, 160);
        base.setMaterial(new PhongMaterial(Color.GRAY));
        base.setTranslateY(210);
        
        // Base1
        Box base1 = new Box(25, 50, 80);
        base1.setMaterial(new PhongMaterial(Color.GRAY));
        base1.setTranslateY(175);        
        
        // Pantalla
        Box pantalla = new Box(25, 300, 600);
        pantalla.setMaterial(new PhongMaterial(Color.BLACK));
               

        // Grupo
        this.getChildren().addAll(base, base1, pantalla);
        }
        private TV createTV(){
            return new TV();
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
    
    class TVGrande extends Movible{
                private boolean fijo;

        public TVGrande(){
                        this.fijo = true;

        // Base
        Box base = new Box(80, 20, 160);
        base.setMaterial(new PhongMaterial(Color.GRAY));
        base.setTranslateY(360);
        
        // Base1
        Box base1 = new Box(25, 50, 80);
        base1.setMaterial(new PhongMaterial(Color.GRAY));
        base1.setTranslateY(325);        
        
        // Pantalla
        Box pantalla = new Box(25, 600, 1200);
        pantalla.setMaterial(new PhongMaterial(Color.BLACK));
               

        // Grupo
        this.getChildren().addAll(base, base1, pantalla);
    }
        private TVGrande createTVGrande(){
            return new TVGrande();
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


    /*private void addObject(String objectType) {
        Shape3D newObject = null;
        switch (objectType) {
            case "Prisma":
                newObject = new Box(100, 100, 100);
                break;
            case "Esfera":
                newObject = new Sphere(100);
                break;
            case "Cilindro":
                newObject = new Cylinder(100, 100);
                break;                
        }
        
        if (newObject != null) {
            newObject.setMaterial(new PhongMaterial(Color.color(Math.random(), Math.random(), Math.random())));
            newObject.setTranslateX(0);
            newObject.setTranslateY(-500);
            newObject.setTranslateZ(0);
            newObject.setOnMousePressed(this::handleObjectPressed);
            newObject.setOnMouseDragged(this::handleObjectDragged);
            newObject.setOnMouseReleased(event -> selectedObject = null);
            setupContextMenu(newObject); // Configurar menú contextual
            root3D.getChildren().add(newObject);
        }
    }
    */
    
        private void showDimensionDialog(String objectType) {
        Stage dialog = new Stage();
        dialog.setTitle("Ingresar dimensiones para " + objectType);

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));
        
        Label widthLabel = new Label("Ancho:");
        TextField widthField = new TextField();
        Label heightLabel = new Label("Altura:");
        TextField heightField = new TextField();
        Label depthLabel = new Label("Profundidad:");
        TextField depthField = new TextField();
        
        if (objectType.equals("Cilindro")) {
            depthLabel.setDisable(true);
            depthField.setDisable(true);
            
        } else if(objectType.equals("Esfera")) {
            depthLabel.setDisable(true);
            depthField.setDisable(true);
            heightLabel.setDisable(true);
            heightField.setDisable(true);
        } else if(objectType.equals("Cubo")){
            
        }
        
        Button createButton = new Button("Crear");
        createButton.setOnAction(e -> {
            try {
                double width = Double.parseDouble(widthField.getText());
                double height = objectType.equals("Esfera") ? width : Double.parseDouble(heightField.getText());
                double depth = objectType.equals("Esfera") || objectType.equals("Cilindro") ? height : Double.parseDouble(depthField.getText());
                addObject(objectType, width, height, depth);
                dialog.close();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Dimensiones inválidas");
                alert.setContentText("Por favor ingrese dimensiones válidas.");
                alert.showAndWait();
            }
        });

        dialogVBox.getChildren().addAll(widthLabel, widthField, heightLabel, heightField, depthLabel, depthField, createButton);

        Scene dialogScene = new Scene(dialogVBox, 300, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }
        

        private void showRoomDialog() {
            Stage dialog = new Stage();
            dialog.setTitle("Elija la room sobre la cual desea trabajar");

            // Crear una tabla con el tipo Room
            TableView<Room> tableView = new TableView<>();

            // Crear las columnas y configurar cómo se llenan

            // Columna para el nombre de la Room
            TableColumn<Room, String> column1 = new TableColumn<>("Room");
            column1.setCellValueFactory(new PropertyValueFactory<>("nombreObjeto"));

            // Columna para la Base
            TableColumn<Room, Double> column2 = new TableColumn<>("Base");
            column2.setCellValueFactory(new PropertyValueFactory<>("base"));

            // Columna para la Altura
            TableColumn<Room, Double> column3 = new TableColumn<>("Altura");
            column3.setCellValueFactory(new PropertyValueFactory<>("altura"));

            // Columna para la Profundidad
            TableColumn<Room, Double> column4 = new TableColumn<>("Profundidad");
            column4.setCellValueFactory(new PropertyValueFactory<>("profundidad"));

            // Agregar las columnas a la tabla
            tableView.getColumns().add(column1);
            tableView.getColumns().add(column2);
            tableView.getColumns().add(column3);
            tableView.getColumns().add(column4);

            // Configurar un tamaño mínimo para la tabla si es necesario
            tableView.setPrefHeight(200);
            tableView.setPrefWidth(500);

            // Añadir las rooms a la tabla
            for (Room room : ServicesRoom.getRooms()) {
                tableView.getItems().add(room);
            }
            
            // Añadir un listener para detectar la selección de la fila
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    setRoomSeleccionada(newSelection);
                }
            });

            // Añadir la tabla a un VBox
            

            Button createButton = new Button("Crear");
            createButton.setOnAction(e -> {
                try {
                    double width = roomSeleccionada.getBase();
                    double height = roomSeleccionada.getAltura();
                    double depth = roomSeleccionada.getProfundidad();

                // Verificar si ya existen los objetos y eliminarlos si es necesario
                if (floor != null) {
                    root3D.getChildren().remove(floor);
                }
                if (wallBack != null) {
                    root3D.getChildren().remove(wallBack);
                }
                if (wallLeft != null) {
                    root3D.getChildren().remove(wallLeft);
                }            

                floor = createFloor(width, height, depth);
                wallBack = createWallBack(width, height, depth);
                wallLeft = createWallLeft(width, height, depth);

                root3D.getChildren().addAll(floor, wallBack, wallLeft);


                    dialog.close();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Dimensiones inválidas");
                    alert.setContentText("Por favor ingrese dimensiones válidas.");
                    alert.showAndWait();
                }
            });
            
            VBox vbox = new VBox(tableView, createButton);
            Scene dialogScene = new Scene(vbox, 300, 250);
            dialog.setScene(dialogScene);
            dialog.show();
            
        }
        
    private void showItemDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Item's Creados");

        // Crear una tabla con el tipo Item
        TableView<Item> tableView = new TableView<>();

        // Crear las columnas y configurar cómo se llenan
        TableColumn<Item, String> column1 = new TableColumn<>("Item");
        column1.setCellValueFactory(new PropertyValueFactory<>("nombreObjeto"));

        TableColumn<Item, Double> column2 = new TableColumn<>("Base");
        column2.setCellValueFactory(new PropertyValueFactory<>("base"));

        TableColumn<Item, Double> column3 = new TableColumn<>("Altura");
        column3.setCellValueFactory(new PropertyValueFactory<>("altura"));

        TableColumn<Item, Double> column4 = new TableColumn<>("Profundidad");
        column4.setCellValueFactory(new PropertyValueFactory<>("profundidad"));

        tableView.getColumns().addAll(column1, column2, column3, column4);

        // Configurar un tamaño mínimo para la tabla si es necesario
        tableView.setPrefHeight(200);
        tableView.setPrefWidth(500);

        // Añadir los items a la tabla
        for (Item item : ServicesItem.getItems()) {
            tableView.getItems().add(item);
        }

        // Añadir un listener para detectar la selección de la fila
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setItemSeleccionado(newSelection);
            }
        });

        Button createButton = new Button("Crear");
        createButton.setOnAction(e -> {
            try {
                double width = itemSeleccionado.getBase();
                double height = itemSeleccionado.getAltura();
                double depth = itemSeleccionado.getProfundidad();

                // Verificar si ya existen los objetos y eliminarlos si es necesario
                if (newObjectItem != null) {
                    root3D.getChildren().remove(newObjectItem);
                }

                // Crear el nuevo objeto
                newObjectItem = new ItemNuevo(width, height, depth);
                
                root3D.getChildren().add(newObjectItem);

                // Configurar la posición y eventos del objeto
                newObjectItem.setTranslateX(0);
                newObjectItem.setTranslateY(-500);
                newObjectItem.setTranslateZ(0);
                newObjectItem.setOnMousePressed(this::handleGroupPressed);
                newObjectItem.setOnMouseDragged(this::handleGroupDragged);
                setupContextMenu(newObjectItem); // Configurar menú contextual

                dialog.close();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Dimensiones inválidas");
                alert.setContentText("Por favor ingrese dimensiones válidas.");
                alert.showAndWait();
            }
        });

        VBox vbox = new VBox(tableView, createButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private Box createFloor(double width, double height, double depth) {
        Box floor = new Box(width, 10, depth);
        floor.setMaterial(new PhongMaterial(Color.GRAY));
        floor.setTranslateY(floorY);
        return floor;
    }

    private Box createWallBack(double width, double height, double depth) {
        Box wallBack = new Box(width, height, 10);
        wallBack.setMaterial(new PhongMaterial(Color.GRAY));
        wallBack.setTranslateZ(depth/2);
        return wallBack;
    }

    private Box createWallLeft(double width, double height, double depth) {
        Box wallLeft = new Box(10, height, depth);
        wallLeft.setMaterial(new PhongMaterial(Color.GRAY));
        wallLeft.setTranslateX(-width/2);
        return wallLeft;
    }        
    private ItemNuevo createItemNuevo(double width, double height, double depth){
        ItemNuevo itemNuevo = new ItemNuevo(width, height, depth);
        return itemNuevo;
    }
    
    
    private void addObject(String objectType, double width, double height, double depth) {
        Movible newObject = null;
        switch (objectType) {
            case "Prisma":
                //newObject = new Box(width, height, depth);
                break;
            case "Esfera":
                newObject = new Sphere3D(width, Color.RED);
                break;
            case "Cilindro":
                newObject = new Cylinder3D(width/2, height, Color.GREEN);
                break;                
        }
        
        if (newObject != null) {
            //newObject.setMaterial(new PhongMaterial(Color.color(Math.random(), Math.random(), Math.random())));
            newObject.setTranslateX(0);
            newObject.setTranslateY(-500);
            newObject.setTranslateZ(0);
            newObject.setOnMousePressed(this::handleObjectPressed);
            newObject.setOnMouseDragged(this::handleObjectDragged);
            newObject.setOnMouseReleased(event -> selectedObject = null);
            setupContextMenu(newObject); // Configurar menú contextual
            root3D.getChildren().add(newObject);
        }
    }       
    
    private void addGroup(String groupType) {
        Movible newObject = null;
        switch (groupType) {
            case "Cama Simple":
                newObject = new CamaSimple();
                break;
            case "Cama Doble":
                newObject = new CamaDoble();
                break;
            case "Mesa de Noche":
                newObject = new MesaDeNoche();
                break;
            case "Armario":
                newObject = new Armario();
                break;
            case "Mueble":
                newObject = new Mueble();
                break;
            case "Mesa":
                newObject = new Mesa();
                break;
            case "Silla":
                newObject = new Silla();
                break;
            case "Televisor":
                newObject = new TV();
                break;
            case "Televisor Grande":
                newObject = new TVGrande();
                break;               
        }

        if (newObject != null) {
            newObject.setTranslateX(0);
            newObject.setTranslateY(-500);
            newObject.setTranslateZ(0);
            newObject.setOnMousePressed(this::handleGroupPressed);
            newObject.setOnMouseDragged(this::handleGroupDragged);
            setupContextMenu(newObject); // Configurar menú contextual
            root3D.getChildren().add(newObject);
        }
    }

    private Group cloneGroup(Group original) {
        Group clone = new Group();
        for (javafx.scene.Node node : original.getChildren()) {
            if (node instanceof Box) {
                Box originalBox = (Box) node;
                Box boxClone = new Box(originalBox.getWidth(), originalBox.getHeight(), originalBox.getDepth());
                boxClone.setMaterial(originalBox.getMaterial());
                boxClone.setTranslateX(originalBox.getTranslateX());
                boxClone.setTranslateY(originalBox.getTranslateY());
                boxClone.setTranslateZ(originalBox.getTranslateZ());
                clone.getChildren().add(boxClone);
            }
        }
        return clone;
    }


    private void setupContextMenu(Shape3D object) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Eliminar");
        removeItem.setOnAction(e -> root3D.getChildren().remove(object));
        
        MenuItem rotateItem = new MenuItem("Rotar");
        rotateItem.setOnAction(e -> rotateObjectY(object));
        
        contextMenu.getItems().addAll(removeItem, rotateItem);

        object.setOnContextMenuRequested(e -> contextMenu.show(object, e.getScreenX(), e.getScreenY()));
    }
    
    private void setupContextMenu(Group group) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Eliminar");
        deleteItem.setOnAction(event -> root3D.getChildren().remove(group));
        
        MenuItem rotateItem = new MenuItem("Rotar");
        rotateItem.setOnAction(e -> rotateObjectY(group));
    
        contextMenu.getItems().addAll(deleteItem, rotateItem);
        
        group.setOnContextMenuRequested(event -> 
            contextMenu.show(group, event.getScreenX(), event.getScreenY())
        );
    }
    
    private void rotateObjectY(Shape3D object) {
        Rotate rotate = new Rotate(90, Rotate.Y_AXIS);
        object.getTransforms().add(rotate);
    }    
    
    private void rotateObjectY(Group group) {
        Rotate rotate = new Rotate(90, Rotate.Y_AXIS);
        group.getTransforms().add(rotate);
    }

    private void handleObjectPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            selectedShape = (Movible) event.getSource();
            objectMouseOffsetX = selectedObject.getTranslateX() - event.getSceneX();
            objectMouseOffsetY = selectedObject.getTranslateY() - event.getSceneY();
            
            if (event.isShiftDown()) {
                objectMouseOffsetZ = selectedObject.getTranslateZ() - event.getSceneY();
            }            
        }
        event.consume();
    }

    private void handleObjectDragged(MouseEvent event) {
        if (selectedShape != null && !positionsLocked) {
            Point3D newPosition = unproject(event.getSceneX(), event.getSceneY(), selectedShape.getTranslateZ());
            selectedShape.setTranslateX(newPosition.getX());
            selectedShape.setTranslateY(newPosition.getY());
            
            if (event.isShiftDown()) {
                double newZ = selectedShape.getTranslateZ() + (event.getSceneY() - (selectedShape.getTranslateY() - objectMouseOffsetY));
                selectedShape.setTranslateZ(newZ);
            }            
            
        }
        event.consume();
    }
    
    private void handleGroupPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getSource() instanceof Movible) {
            selectedShape = (Movible) event.getSource();

            objectMouseOffsetX = selectedShape.getTranslateX() - event.getSceneX();
            objectMouseOffsetY = selectedShape.getTranslateY() - event.getSceneY();

            if (event.isShiftDown()) {
                objectMouseOffsetZ = selectedShape.getTranslateZ() - event.getSceneY();
            }
        }
        event.consume();
    }
    
    private void handleGroupDragged(MouseEvent event) {
        if (selectedShape != null && !positionsLocked && selectedShape.isFijo()==true) {
            // Calcular la nueva posición en X e Y
            Point3D newPosition = unproject(event.getSceneX(), event.getSceneY(), selectedShape.getTranslateZ());
            selectedShape.setTranslateX(newPosition.getX());
            selectedShape.setTranslateY(newPosition.getY());

            // Si se está presionando Shift, permitir mover en el eje Z
            if (event.isShiftDown()) {
                double newZ = selectedShape.getTranslateZ() + (event.getSceneY() - (selectedShape.getTranslateY() - objectMouseOffsetY));
                selectedShape.setTranslateZ(newZ);
            }
        }
        event.consume();
    }        

    private void togglePositionsLock() {
        for (javafx.scene.Node node : root3D.getChildren()) {
            if (node instanceof Movible) {
                Movible movible = (Movible) node;
                movible.setFijo(!movible.isFijo()); // Alternar el estado de "fijo"
            }
        }
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

    private Point3D unproject(double sceneX, double sceneY, double z) {
        double subSceneX = sceneX - subScene.getLayoutX();
        double subSceneY = sceneY - subScene.getLayoutY();

        double normalizedX = (subSceneX / subScene.getWidth()) * 2 - 1;
        double normalizedY = -((subSceneY / subScene.getHeight()) * 2 - 1);

        Point3D clipPoint = new Point3D(normalizedX, normalizedY, -1);

        Point3D viewPoint = cameraExterna.localToScene(clipPoint);
        viewPoint = cameraExterna.sceneToLocal(viewPoint);

        Point3D worldPoint = new Point3D(viewPoint.getX() * -cameraExterna.getTranslateZ(), 
                                         viewPoint.getY() * cameraExterna.getTranslateZ(), 
                                         z);

        Rotate rxInverse = new Rotate(-rotateY, Rotate.X_AXIS);
        Rotate ryInverse = new Rotate(-rotateX, Rotate.Y_AXIS);
        worldPoint = rxInverse.transform(worldPoint);
        worldPoint = ryInverse.transform(worldPoint);

        return worldPoint;
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        cameraExterna.setTranslateZ(cameraExterna.getTranslateZ() + delta * zoomSpeed);
    }

private void startPhysics() {
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
        for (javafx.scene.Node node : root3D.getChildren()) {
            if ((node instanceof Shape3D || node instanceof Group) && node != selectedObject) {
                if (node instanceof Ventana){
                    detectCollisions(node);                    
                } else {
                    applyGravity(node);
                    detectCollisions(node);                    
                }
            }
        }
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}

private void applyGravity(javafx.scene.Node node) {
    if (node instanceof Group) {
        for (javafx.scene.Node child : ((Group) node).getChildren()) {
            double bottomY = child.getTranslateY() + getObjectHeight(child) / 2 + node.getTranslateY();
            if (bottomY < floorY) {
                node.setTranslateY(node.getTranslateY() + gravity);
            } else {
                node.setTranslateY(floorY - getObjectHeight(child) / 2);
            }
        }
    } else {
        double bottomY = node.getTranslateY() + getObjectHeight(node) / 2;
        if (bottomY < floorY) {
            node.setTranslateY(node.getTranslateY() + gravity);
        } else {
            node.setTranslateY(floorY - getObjectHeight(node) / 2);
        }
    }
}

private double getObjectHeight(javafx.scene.Node node) {
    if (node instanceof Box) {
        return ((Box) node).getHeight();
    } else if (node instanceof Sphere) {
        return ((Sphere) node).getRadius() * 2;
    } else if (node instanceof Cylinder) {
        return ((Cylinder) node).getHeight();
    } else if (node instanceof Group) {
        double maxHeight = 0;
        double grHeight = 0;
        for (javafx.scene.Node child : ((Group) node).getChildren()) {
            if (child instanceof Box) {
                grHeight = ((Box) node).getHeight() + node.getTranslateY();
                return grHeight;
            } else if (child instanceof Sphere) {
                grHeight = ((Sphere) node).getRadius() * 2 + node.getTranslateY();
                return grHeight;
            } else if (child instanceof Cylinder) {
                grHeight = ((Cylinder) node).getHeight() + node.getTranslateY();
                return grHeight;
            }
            maxHeight = Math.max(maxHeight, grHeight);            
        }
        return maxHeight;
    }
    return 0;
}

private void detectCollisions(javafx.scene.Node node) {
    for (javafx.scene.Node otherNode : root3D.getChildren()) {
        if (node != otherNode && (otherNode instanceof Shape3D || otherNode instanceof Group)) {
            if (isColliding(node, otherNode)) {
                resolveCollision(node, otherNode);
            }
        }
    }
}

private boolean isColliding(javafx.scene.Node node1, javafx.scene.Node node2) {
    // Obtener las Bounding Boxes de los nodos
    Bounds bounds1 = node1.getBoundsInParent();
    Bounds bounds2 = node2.getBoundsInParent();

    // Comprobar si las Bounding Boxes se intersectan
    return bounds1.intersects(bounds2);
}


    private void resolveCollision(javafx.scene.Node node1, javafx.scene.Node node2) {
        Bounds bounds1 = node1.getBoundsInParent();
        Bounds bounds2 = node2.getBoundsInParent();

        // Calcular las diferencias en las posiciones de los nodos
        double diffX = bounds1.getCenterX() - bounds2.getCenterX();
        double diffY = bounds1.getCenterY() - bounds2.getCenterY();
        double diffZ = bounds1.getCenterZ() - bounds2.getCenterZ();

        // Calcular la superposición en cada eje
        double overlapX = (bounds1.getWidth() + bounds2.getWidth()) / 2 - Math.abs(diffX);
        double overlapY = (bounds1.getHeight() + bounds2.getHeight()) / 2 - Math.abs(diffY);
        double overlapZ = (bounds1.getDepth() + bounds2.getDepth()) / 2 - Math.abs(diffZ);

        if (((node1 == floor || node2 == floor) || (node1 == wallBack || node2 == wallBack) || (node1 == wallLeft || node2 == wallLeft)) 
                && ((floor != null) && (wallBack != null) && (wallLeft != null))) {
            node1.setTranslateY(node1.getTranslateY() - 1);
            node2.setTranslateY(node2.getTranslateY() + 1);
        } else {    
            // Asegurarse de que hay una colisión en algún eje
            if (overlapX > 0 && overlapY > 0 && overlapZ > 0) {
                // Encuentra el eje con la menor superposición para mover el nodo en esa dirección
                if (overlapX < overlapY && overlapX < overlapZ) {
                    double adjustment = overlapX / 2;
                    if (diffX > 0) {
                        node1.setTranslateX(node1.getTranslateX() + adjustment);
                        node2.setTranslateX(node2.getTranslateX() - adjustment);
                    } else {
                        node1.setTranslateX(node1.getTranslateX() - adjustment);
                        node2.setTranslateX(node2.getTranslateX() + adjustment);
                    }
                } else if (overlapY < overlapX && overlapY < overlapZ) {
                    double adjustment = overlapY / 2;
                    if (diffY > 0) {
                        node1.setTranslateY(node1.getTranslateY() + adjustment);
                        node2.setTranslateY(node2.getTranslateY() - adjustment);
                    } else {
                        node1.setTranslateY(node1.getTranslateY() - adjustment);
                        node2.setTranslateY(node2.getTranslateY() + adjustment);
                    }
                } else {
                    double adjustment = overlapZ / 2;
                    if (diffZ > 0) {
                        node1.setTranslateZ(node1.getTranslateZ() + adjustment);
                        node2.setTranslateZ(node2.getTranslateZ() - adjustment);
                    } else {
                        node1.setTranslateZ(node1.getTranslateZ() - adjustment);
                        node2.setTranslateZ(node2.getTranslateZ() + adjustment);
                    }
                }
            }
        }
    }    

    private String saveScene() {
        Map<String, Object> sceneData = new HashMap<>();
        List<Map<String, Object>> objectsData = new ArrayList<>();

        for (javafx.scene.Node node : root3D.getChildren()) {
            if (node instanceof Shape3D || node instanceof Group || node instanceof Movible) {
                objectsData.add(encodeObject(node));
            }
        }

        sceneData.put("objects", objectsData);

        try {
            String json = objectMapper.writeValueAsString(sceneData);
            String encodedJson = Base64.getEncoder().encodeToString(json.getBytes());
            
            // Aquí puedes guardar 'encodedJson' en un archivo o mostrarlo al usuario
            System.out.println("Escena guardada: " + encodedJson);
            return encodedJson;
        } catch (JsonProcessingException e) { 
            return null;
        }
        
    }

    private void loadScene(String encodedJson) {
        // Aquí deberías obtener el 'encodedJson' desde un archivo o entrada del usuario
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedJson);
            String json = new String(decodedBytes);
            Map<String, Object> sceneData = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            root3D.getChildren().clear();
            List<Map<String, Object>> objectsData = (List<Map<String, Object>>) sceneData.get("objects");
            for (Map<String, Object> objectData : objectsData) {
                javafx.scene.Node node = decodeObject(objectData);
                if (node != null) {
                    root3D.getChildren().add(node);
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
    }

    private Map<String, Object> encodeObject(javafx.scene.Node node) {
        Map<String, Object> objectData = new HashMap<>();
        objectData.put("type", node.getClass().getSimpleName());
        objectData.put("translateX", node.getTranslateX());
        objectData.put("translateY", node.getTranslateY());
        objectData.put("translateZ", node.getTranslateZ());

        switch (node) {
            case Shape3D shape -> {
                PhongMaterial material = (PhongMaterial) shape.getMaterial();
                Color color = (Color) material.getDiffuseColor();
                objectData.put("color", String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255)));
                
                switch (shape) {
                    case Box box -> {
                        objectData.put("width", box.getWidth());
                        objectData.put("height", box.getHeight());
                        objectData.put("depth", box.getDepth());
                    }
                    case Sphere sphere -> objectData.put("radius", sphere.getRadius());
                    case Cylinder cylinder -> {
                        objectData.put("radius", cylinder.getRadius());
                        objectData.put("height", cylinder.getHeight());
                    }
                    
                    default -> {
                    }
                }
            }
            case Group group -> {
                List<Map<String, Object>> childrenData = new ArrayList<>();
                for (javafx.scene.Node child : group.getChildren()) {
                    childrenData.add(encodeObject(child));
                }
                objectData.put("children", childrenData);
            }
            
            default -> {
            }
        }

        List<Map<String, Object>> transformsData = new ArrayList<>();
        for (Transform transform : node.getTransforms()) {
            Map<String, Object> transformData = new HashMap<>();
            transformData.put("type", transform.getClass().getSimpleName());
            if (transform instanceof Rotate rotate) {
                transformData.put("angle", rotate.getAngle());
                transformData.put("pivotX", rotate.getPivotX());
                transformData.put("pivotY", rotate.getPivotY());
                transformData.put("pivotZ", rotate.getPivotZ());
                Point3D axis = rotate.getAxis();
                transformData.put("axisX", axis.getX());
                transformData.put("axisY", axis.getY());
                transformData.put("axisZ", axis.getZ());
            }
            transformsData.add(transformData);
        }
        objectData.put("transforms", transformsData);

        return objectData;
    }



    private javafx.scene.Node decodeObject(Map<String, Object> objectData) {
        String type = (String) objectData.get("type");
        javafx.scene.Node node = null;

        switch (type) {
            case "Box" -> node = new Box(
                    (double) objectData.get("width"),
                    (double) objectData.get("height"),
                    (double) objectData.get("depth")
                );
            case "ItemNuevo" -> {
                // Obtener las dimensiones con verificación de nulidad y casting adecuado
                Double width = objectData.get("width") instanceof Number ? ((Number) objectData.get("width")).doubleValue() : 100.0;
                Double height = objectData.get("height") instanceof Number ? ((Number) objectData.get("height")).doubleValue() : 100.0;
                Double depth = objectData.get("depth") instanceof Number ? ((Number) objectData.get("depth")).doubleValue() : 100.0;

                // Imprimir las dimensiones para depuración
                System.out.println("Dimensiones para ItemNuevo: width=" + width + ", height=" + height + ", depth=" + depth);

                // Crear el objeto ItemNuevo con dimensiones válidas
                ItemNuevo itemNuevo = new ItemNuevo(width, height, depth);

                // Procesar los hijos
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                if (childrenData != null) {
                    for (Map<String, Object> childData : childrenData) {
                        javafx.scene.Node child = decodeObject(childData);
                        if (child != null) {
                            itemNuevo.setTranslateX(0);
                            itemNuevo.setTranslateY(-500);
                            itemNuevo.setTranslateZ(0);
                            itemNuevo.setOnMousePressed(this::handleGroupPressed);
                            itemNuevo.setOnMouseDragged(this::handleGroupDragged);
                            setupContextMenu(itemNuevo);
                            itemNuevo.getChildren().add(child);
                        } else {
                            System.err.println("Child node could not be decoded: " + childData);
                        }
                    }
                } else {
                    System.out.println("No children data for ItemNuevo");
                }

                node = itemNuevo;
            }

            case "Sphere" -> node = new Sphere((double) objectData.get("radius"));
            case "Cylinder" -> node = new Cylinder(
                    (double) objectData.get("radius"),
                    (double) objectData.get("height")
                );
            case "Group" -> {
                Group group = new Group();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        group.getChildren().add(child);
                    }
                }
                node = group;
            }
            case "MesaDeNoche" -> {
                MesaDeNoche mesaDeNoche = new MesaDeNoche();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        mesaDeNoche.getChildren().add(child);
                    }
                }
                node = mesaDeNoche;
            }
            case "Mueble" -> {
                Mueble mueble = new Mueble();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        mueble.getChildren().add(child);
                    }
                }
                node = mueble;
            }
            case "Armario" -> {
                Armario armario = new Armario();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        armario.getChildren().add(child);
                    }
                }
                node = armario;
            }
            case "Mesa" -> {
                Mesa mesa = new Mesa();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        mesa.getChildren().add(child);
                    }
                }
                node = mesa;
            }
            case "Silla" -> {
                Silla silla = new Silla();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        silla.getChildren().add(child);
                    }
                }
                node = silla;
            }
            case "CamaSimple" -> {
                CamaSimple camaSimple = new CamaSimple();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        camaSimple.getChildren().add(child);
                    }
                }
                node = camaSimple;
            }
            case "CamaDoble" -> {
                CamaDoble camaDoble = new CamaDoble();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        camaDoble.getChildren().add(child);
                    }
                }
                node = camaDoble;
            }
            case "TV" -> {
                TV tv = new TV();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        tv.getChildren().add(child);
                    }
                }
                node = tv;
            }
            case "TVGrande" -> {
                TVGrande tvGrande = new TVGrande();
                List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
                for (Map<String, Object> childData : childrenData) {
                    javafx.scene.Node child = decodeObject(childData);
                    if (child != null) {
                        tvGrande.getChildren().add(child);
                    }
                }
                node = tvGrande;
            }             
        }

        if (node != null) {
            node.setTranslateX((double) objectData.get("translateX"));
            node.setTranslateY((double) objectData.get("translateY"));
            node.setTranslateZ((double) objectData.get("translateZ"));

            if (node instanceof Shape3D shape) {
                String colorStr = (String) objectData.get("color");
                Color color = Color.web(colorStr);
                shape.setMaterial(new PhongMaterial(color));
            }

           List<Map<String, Object>> transformsData = (List<Map<String, Object>>) objectData.get("transforms");
            for (Map<String, Object> transformData : transformsData) {
                String transformType = (String) transformData.get("type");
                if ("Rotate".equals(transformType)) {
                    double angle = (double) transformData.get("angle");
                    double pivotX = (double) transformData.get("pivotX");
                    double pivotY = (double) transformData.get("pivotY");
                    double pivotZ = (double) transformData.get("pivotZ");
                    double axisX = (double) transformData.get("axisX");
                    double axisY = (double) transformData.get("axisY");
                    double axisZ = (double) transformData.get("axisZ");
                    Point3D axis = new Point3D(axisX, axisY, axisZ);
                    Rotate rotate = new Rotate(angle, pivotX, pivotY, pivotZ, axis);
                    node.getTransforms().add(rotate);
                }
            }

            switch (node) {
                case Shape3D shape3D -> setupContextMenu(shape3D);
                case Group group -> setupContextMenu(group);
                default -> {
                }
            }
        }

        return node;
    }
    
    
    
    private void showCargarDialog() {
        Stage secondaryStage = new Stage();

        Map<String, List<String>> resultado = cargarOpcionesDesdeBD(); // Método para cargar desde la BD
        List<String> opciones = resultado.get("opciones");
        List<String> hashes = resultado.get("hashes");

        ComboBox<String> roomComboBox = new ComboBox<>();
        roomComboBox.getItems().addAll(opciones);
        roomComboBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(245,245,245), rgb(210,210,210));" + // Degradado de gris claro
            "-fx-border-color: rgb(200,200,200);" +  // Borde gris claro
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );
        roomComboBox.setPromptText("Selecciona una opción");

        Button cargarRoomButton = new Button("Cargar Room");
        cargarRoomButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado verde
            "-fx-text-fill: white;" +  // Texto blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde verde
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );
        cargarRoomButton.setVisible(false); // Inicialmente oculto

        roomComboBox.setOnAction(e -> {
            // Muestra el botón cuando se selecciona una opción
            if (roomComboBox.getValue() != null) {
                cargarRoomButton.setVisible(true);
            }
        });

        roomComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // Verifica si el nuevo valor no es null y es válido
            if (newValue.intValue() != -1) {
                int indiceSeleccionado = newValue.intValue();
                // Obtener el hash correspondiente al índice seleccionado
                String elemento = hashes.get(indiceSeleccionado);
                cargarRoomButton.setOnAction(e -> {
                    loadScene(elemento);
                    secondaryStage.close();        
                });
            }
        });   

        VBox secondaryLayout = new VBox(10);
        secondaryLayout.setPadding(new Insets(15));
        secondaryLayout.setStyle(
            "-fx-background-color: rgb(245,245,220);" +  
            "-fx-padding: 15;" +  // Espaciado interno
            "-fx-border-color: rgb(200,200,200);" +  // Borde gris claro
            "-fx-border-width: 2px;" +  // Grosor del borde
            "-fx-border-radius: 10px;"  // Bordes redondeados
        );
        secondaryLayout.getChildren().addAll(
            new Label("Rooms Guardadas:") {{
                setStyle(
                    "-fx-font-size: 16px;" +  // Tamaño de fuente
                    "-fx-font-weight: bold;" +  // Negrita
                    "-fx-text-fill: rgb(0,0,0);"  // Color del texto
                );
            }},
            roomComboBox,
            cargarRoomButton
        );

        Scene secondaryScene = new Scene(secondaryLayout, 300, 200);
        secondaryStage.setScene(secondaryScene);
        secondaryStage.setTitle("Cargar Room");
        secondaryStage.initModality(Modality.APPLICATION_MODAL); // Bloquea interacción con otras ventanas mientras está abierto
        secondaryStage.show();
    }

    // Método ficticio para cargar opciones desde la base de datos
    private Map<String, List<String>> cargarOpcionesDesdeBD() {
        List<RoomJFX> rooms = servRoomJFX.getRoomsJFXByUsuario(ServicesUsuario.getUsuario());

        // Verifica si la lista de habitaciones está vacía
        if (rooms.isEmpty()) {
            System.out.println("No se encontraron habitaciones para el usuario.");
        } else {
            // Iterar sobre las habitaciones obtenidas
            for (RoomJFX room : rooms) {
                ServicesRoomJFX.setRooms(room);
                System.out.println("Habitación: " + room.getNombre());
            }
        }

        List<String> opciones = new ArrayList<>();
        List<String> hashes = new ArrayList<>();

        // Verifica si la lista de habitaciones en ServicesRoomJFX está vacía
        List<RoomJFX> savedRooms = ServicesRoomJFX.getRooms();
        if (savedRooms.isEmpty()) {
            System.out.println("No se encontraron habitaciones guardadas.");
        } else {
            // Iterar sobre las habitaciones guardadas
            for (RoomJFX room : savedRooms) {
                opciones.add(room.getNombre());
                hashes.add(room.getHash());
            }
        }

        // Crear el resultado y añadir las listas
        Map<String, List<String>> resultado = new HashMap<>();
        resultado.put("opciones", opciones);
        resultado.put("hashes", hashes);

        return resultado;
    }
    
        class Ventana extends Movible {
                private boolean fijo;

        public Ventana(){
                        this.fijo = true;

        // Base
        Box marco = new Box(5, 800, 1000);
        marco.setMaterial(new PhongMaterial(Color.BLACK));

        // Cajón1
        Box ventana = new Box(5, 780, 980);
        ventana.setMaterial(new PhongMaterial(Color.WHITE));
        ventana.setTranslateX(5);  

        // Grupo
        this.getChildren().addAll(marco, ventana);
    }
        private Ventana createVentana(){
            return new Ventana();
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

    private void opcionesIluminacion(Box wallLeft, Box wallBack) {
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
        configuracionIluminacion(pared1ComboBox.getValue(), pared2ComboBox.getValue(), pared3ComboBox.getValue(), techoComboBox.getValue(), wallLeft, wallBack);
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

public void configuracionIluminacion(String pared1, String pared2, String pared3, String techo, Box wallLeft, Box wallBack) {
    // Limpia las configuraciones de luz existentes si es necesario
    root3D.getChildren().removeIf(node -> node instanceof PointLight);

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
        rotateObjectY(newVentana1);
        togglePositionsLock();
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
        togglePositionsLock();
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
        rotateObjectY(newVentana3);
        rotateObjectY(newVentana3);
        togglePositionsLock();
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

public void addItemToScene(Group itemGroup) {
    itemGroup.setTranslateX(0);
    itemGroup.setTranslateY(0);
    itemGroup.setTranslateZ(0);
    itemGroup.setOnMousePressed(this::handleGroupPressed);
    itemGroup.setOnMouseDragged(this::handleGroupPressed);
    setupContextMenu(itemGroup);
    root3D.getChildren().add(itemGroup);
}

    private void abrirCrearItem() {
        CrearItem crearItem = new CrearItem(this);
        Stage crearItemStage = new Stage();
        crearItem.start(crearItemStage);

        try {
            crearItem.start(crearItemStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        crearItemStage.show();
    }

    

    public static void main(String[] args) {
        launch(args);
    }

    public SubScene getSubScene() {
        return subScene;
    }

}