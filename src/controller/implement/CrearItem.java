package controller.implement;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import view.RooMakingJFX3D;


public class CrearItem extends Application {
    
    
    private PerspectiveCamera camera;
    private double mouseX, mouseY;
    private double rotateX = 0, rotateY = 0;
    private double rotateSpeed = 0.2;
    private double zoomSpeed = 1.1;
    private double floorY = 1000;

    private List<Shape3D> shapes = new ArrayList<>();
    private boolean distancesFixed = false;
    private Shape3D selectedShape = null;
    private List<double[]> distances = new ArrayList<>();
    private Shape3D unanchoredShape = null;

    private Group root3D;
    private Stage primaryStage;
    private SubScene subScene;
    private Shape3D selectedObject;
    private double objectMouseOffsetX, objectMouseOffsetY;
    
    private RooMakingJFX3D parentWindow;

    private Button fijarposicionesBtn;
    

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root3D = new Group();
        subScene = new SubScene(root3D, 600, 400, true, javafx.scene.SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BEIGE);

        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-2000);
        camera.setTranslateY(500);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        camera.setFieldOfView(50);
        subScene.setCamera(camera);


        subScene.setOnMousePressed(this::handleMousePressed);
        subScene.setOnMouseDragged(this::handleMouseDragged);
        subScene.setOnScroll(this::handleScroll);

        VBox controlPanel = createControlPanel();

        HBox mainLayout = new HBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(subScene, controlPanel);
        
        mainLayout.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, rgb(245,245,220), rgb(162,217,206)); " +  // Fondo degradado de beige a verde claro
            "-fx-border-color: rgb(210,180,140); " +  // Color del borde tierra
            "-fx-border-width: 2px; " +  // Grosor del borde
            "-fx-border-radius: 10px; " +  // Bordes redondeados
            "-fx-background-radius: 10px; " +  // Bordes redondeados para el fondo
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 0);"  // Efecto de sombra
        );        

        Scene mainScene = new Scene(mainLayout);

        primaryStage.setTitle("RoomDesigner3D");
        primaryStage.setScene(mainScene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(460);
        primaryStage.show();

        root3D.requestFocus();
    }
    
    public CrearItem(RooMakingJFX3D parentWindow) {
        this.parentWindow = parentWindow;
    }

    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));

        Button fixDistancesBtn = new Button("Fijar Distancias");
        fixDistancesBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );        
        fixDistancesBtn.setOnAction(event -> fixDistances()); 

        Label titleLabel = new Label("Seleccionar objeto:");
        ComboBox<String> objectSelector = new ComboBox<>();
        objectSelector.getItems().addAll("Esfera", "Cilindro", "Cubo", "Poliedro", "Tetraedro", "Piramide");
        objectSelector.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(162,217,206), rgb(126,188,137));" +  // Degradado de verde claro a verde suave
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-radius: 5;"  // Bordes redondeados
        );        
        objectSelector.setOnAction(e -> addObject(objectSelector.getValue()));
        
        Button roomMakingBtn = new Button("RooMaking");
        roomMakingBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );        
        
        roomMakingBtn.setOnAction(e -> handleRoomMaking());        

        controlPanel.setAlignment(Pos.CENTER);        
        controlPanel.getChildren().addAll(titleLabel, objectSelector, fixDistancesBtn, roomMakingBtn);
        controlPanel.setStyle(
            "-fx-background-color: rgb(245,245,220);" +  // Fondo beige claro
            "-fx-border-color: rgb(210,180,140);" +  // Borde color tierra
            "-fx-border-width: 2px;" +  // Grosor del borde
            "-fx-border-radius: 10px;"  // Bordes redondeados
        );        
        return controlPanel;
    }
    
    private void handleRoomMaking() {
        // Crear un nuevo Group con todos los objetos de la escena actual
        Group itemGroup = new Group(root3D.getChildren());
        
        // Enviar el Group a la ventana padre
        parentWindow.addItemToScene(itemGroup, root3D);
        
        // Cerrar la ventana actual
        primaryStage.close();
    }

    private void addObject(String objectType) {
        Stage dialog = new Stage();
        dialog.setTitle("Ingresar dimensiones para " + objectType);

        final Shape3D[] newObject = new Shape3D[1]; // Utiliza un array de un solo elemento para que sea efectivamente final
        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));

        Label widthLabel = new Label("Ancho:");
        TextField widthField = new TextField();
        Label heightLabel = new Label("Altura:");
        TextField heightField = new TextField();
        Label depthLabel = new Label("Profundidad:");
        TextField depthField = new TextField();
        Label colorLabel = new Label("Color (en formato HEX, ej: #FF5733):");
        TextField colorField = new TextField();

        // Configurar la visibilidad de los campos según el tipo de objeto
        switch (objectType) {
            case "Cilindro":
                depthLabel.setDisable(true);
                depthField.setDisable(true);
                break;
            case "Esfera":
                depthLabel.setDisable(true);
                depthField.setDisable(true);
                heightLabel.setDisable(true);
                heightField.setDisable(true);
                break;
            case "Cubo":
            case "Poliedro":
            case "Tetraedro":
            case "Piramide":
                // Todos los campos son necesarios
                break;
        }
        Button createButton = new Button("Crear");
        createButton.setOnAction(e -> {
            try {
                float width = Float.parseFloat(widthField.getText());
                float height = objectType.equals("Esfera") ? width : Float.parseFloat(heightField.getText());
                float depth = (objectType.equals("Esfera") || objectType.equals("Cilindro")) ? height : Float.parseFloat(depthField.getText());
                Color color = Color.web(colorField.getText());

                // Crear el objeto según el tipo y las propiedades
                switch (objectType) {
                    case "Cubo":
                        newObject[0] = new Box(width, height, depth);
                        newObject[0].setMaterial(new PhongMaterial(color));
                        break;
                    case "Esfera":
                        newObject[0] = new Esfera3D(width, color).getEsfera();
                        break;
                    case "Cilindro":
                        newObject[0] = new Cilindro3D(width, height, color).getCilindro();
                        break;
                    case "Poliedro":
                        newObject[0] = new PoliedroPersonalizado(width, height, depth, color);
                        break;
                    case "Tetraedro":
                        newObject[0] = new Tetraedro(width, height, depth, color);
                        break;
                    case "Piramide":
                        newObject[0] = new Piramide(width, height, depth, color);
                        break;
                }

                // Agregar el objeto a la escena
                if (newObject[0] != null) {
                    addObjectToScene(newObject[0]);
                }

                dialog.close();
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Entrada inválida");
                alert.setContentText("Por favor, ingrese dimensiones y un color válidos.");
                alert.showAndWait();
            }
        });

        dialogVBox.getChildren().addAll(widthLabel, widthField, heightLabel, heightField, depthLabel, depthField, colorLabel, colorField, createButton);
        Scene dialogScene = new Scene(dialogVBox, 300, 300);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    // Método para añadir el objeto a la escena
    private void addObjectToScene(Shape3D newObject) {
        newObject.setTranslateX(0);
        newObject.setTranslateY(0);
        newObject.setTranslateZ(0);
        newObject.setOnMousePressed(this::handleObjectPressed);
        newObject.setOnMouseDragged(this::handleObjectDragged);
        newObject.setOnMouseReleased(event -> selectedObject = null);
        newObject.setOnMouseClicked(this::handleObjectClicked);

        setupContextMenu(newObject);
        root3D.getChildren().add(newObject);
        shapes.add(newObject);
    }

    private void setupContextMenu(Shape3D object) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Eliminar");
        removeItem.setOnAction(e -> {
            root3D.getChildren().remove(object);
            shapes.remove(object);
        });
        contextMenu.getItems().add(removeItem);

        object.setOnContextMenuRequested(e -> contextMenu.show(object, e.getScreenX(), e.getScreenY()));
    }

    private void handleObjectPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            selectedObject = (Shape3D) event.getSource();
            System.out.println("Se presiono una figura");
            objectMouseOffsetX = selectedObject.getTranslateX() - event.getSceneX();
            objectMouseOffsetY = selectedObject.getTranslateY() - event.getSceneY();
        }
        event.consume();
    }

    private void handleObjectDragged(MouseEvent event) {
        if (selectedObject != null) {
            selectedObject.setTranslateX(event.getSceneX() + objectMouseOffsetX);
            selectedObject.setTranslateY(event.getSceneY() + objectMouseOffsetY);

            if (distancesFixed && selectedObject != unanchoredShape) {
                adjustPositions(selectedObject);
            }
        }
        event.consume();
    }

    private void handleObjectClicked(MouseEvent event) {
        Shape3D clickedShape = (Shape3D) event.getSource();

        if (distancesFixed) {
            unanchoredShape = clickedShape;
        } else {
            unanchoredShape = null;
        }

        selectedShape = clickedShape;
    }

    private void fixDistances() {
        distancesFixed = true;
        distances.clear();

        for (int i = 0; i < shapes.size(); i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                Shape3D shapeA = shapes.get(i);
                Shape3D shapeB = shapes.get(j);

                double distanceX = shapeB.getTranslateX() - shapeA.getTranslateX();
                double distanceY = shapeB.getTranslateY() - shapeA.getTranslateY();
                double distanceZ = shapeB.getTranslateZ() - shapeA.getTranslateZ();

                distances.add(new double[]{i, j, distanceX, distanceY, distanceZ});
            }
        }
    }

    private void adjustPositions(Shape3D movedShape) {
        int movedIndex = shapes.indexOf(movedShape);

        for (double[] distance : distances) {
            int indexA = (int) distance[0];
            int indexB = (int) distance[1];
            double distanceX = distance[2];
            double distanceY = distance[3];
            double distanceZ = distance[4];

            if (indexA == movedIndex || indexB == movedIndex) {
                Shape3D shapeA = shapes.get(indexA);
                Shape3D shapeB = shapes.get(indexB);

                if (indexA == movedIndex) {
                    updateShapePosition(shapeB, shapeA, distanceX, distanceY, distanceZ);
                } else {
                    updateShapePosition(shapeA, shapeB, -distanceX, -distanceY, -distanceZ);
                }
            }
        }
    }

    private void updateShapePosition(Shape3D shapeToMove, Shape3D referenceShape, double distanceX, double distanceY, double distanceZ) {
        shapeToMove.setTranslateX(referenceShape.getTranslateX() + distanceX);
        shapeToMove.setTranslateY(referenceShape.getTranslateY() + distanceY);
        shapeToMove.setTranslateZ(referenceShape.getTranslateZ() + distanceZ);
    }

    private void handleMousePressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        rotateX += (event.getSceneY() - mouseY) * rotateSpeed;
        rotateY += (event.getSceneX() - mouseX) * rotateSpeed;

        camera.setRotationAxis(new Point3D(1, 0, 0));
        camera.setRotate(rotateX);

        camera.setRotationAxis(new Point3D(0, 1, 0));
        camera.setRotate(rotateY);

        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    private void handleScroll(ScrollEvent event) {
        double zoomFactor = (event.getDeltaY() > 0) ? zoomSpeed : 1 / zoomSpeed;
        camera.setTranslateZ(camera.getTranslateZ() * zoomFactor);
    }

    private Shape3D createShapeFromKey(String key, float width, float height, float depth, Color color) {
        Shape3D newObject = null;

        if (key.contains("Box")) {
            newObject = new Box(width, height, depth);
            newObject.setMaterial(new PhongMaterial(color));
        } else if (key.contains("Sphere")) {
            newObject = new Esfera3D(width / 2, color).getEsfera(); // Aquí se asume que width es el diámetro
        } else if (key.contains("Cylinder")) {
            newObject = new Cilindro3D(width / 2, height, color).getCilindro(); // Aquí se asume que width es el diámetro
        } else if (key.contains("Tetrahedron")) {
            newObject = new Tetraedro(width, height, depth, color);
        } else if (key.contains("Pyramid")) {
            newObject = new Piramide(width, height, depth, color);
        } else {
            newObject = new Box(width, height, depth); // Default shape
            newObject.setMaterial(new PhongMaterial(color));
        }

        return newObject;
    }


    private TriangleMesh createTetrahedronMesh(float width, float height, float depth) {
        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
            0, 0, 0, // Vertex 0
            0, height, depth, // Vertex 1
            width, height, depth, // Vertex 2
            width / 2, 0, depth / 2 // Vertex 3
        );

        mesh.getFaces().addAll(
            0, 0, 1, 0, 3, 0,
            0, 0, 2, 0, 1, 0,
            1, 0, 2, 0, 3, 0,
            2, 0, 0, 0, 3, 0
        );

        return mesh;
    }

    private TriangleMesh createPyramidMesh(float width, float height, float depth) {
        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
            0, 0, 0, // Base vertex 0
            0, depth, 0, // Base vertex 1
            width, 0, 0, // Base vertex 2
            width, depth, 0, // Base vertex 3
            width / 2, depth / 2, height // Apex vertex
        );

        mesh.getFaces().addAll(
            0, 0, 1, 0, 4, 0,
            1, 0, 2, 0, 4, 0,
            2, 0, 3, 0, 4, 0,
            3, 0, 0, 0, 4, 0
        );

        return mesh;
    }
    
    class Esfera3D {
        private Sphere esfera;
        public Esfera3D(double radius, Color color) {
            esfera = new Sphere(radius);
            esfera.setMaterial(new PhongMaterial(color));
        }
        public Sphere getEsfera() {
            return esfera;
        }
    }

    class Cilindro3D {
        private Cylinder cilindro;
        public Cilindro3D(double radius, double height, Color color) {
            cilindro = new Cylinder(radius, height);
            cilindro.setMaterial(new PhongMaterial(color));
        }
        public Cylinder getCilindro() {
            return cilindro;
        }
    }

    public class PoliedroPersonalizado extends MeshView {
        public PoliedroPersonalizado(float width, float height, float depth, Color color) {
            // Definir los puntos y las caras del poliedro personalizado
            float[] puntos = {
                0, height / 2, 0,                // A
                -width / 2, 0, depth / 2,        // B
                width / 2, 0, depth / 2,         // C
                width / 2, 0, -depth / 2,        // D
                -width / 2, 0, -depth / 2,       // E
                0, -height / 2, 0                // F
            };
            int[] caras = {
                0, 0, 1, 0, 2, 0,  // ABC
                0, 0, 2, 0, 3, 0,  // ACD
                0, 0, 3, 0, 4, 0,  // ADE
                0, 0, 4, 0, 1, 0,  // AEB
                1, 0, 5, 0, 2, 0,  // BFC
                2, 0, 5, 0, 3, 0,  // CFD
                3, 0, 5, 0, 4, 0,  // DFE
                4, 0, 5, 0, 1, 0   // EFB
            };

            TriangleMesh mesh = new TriangleMesh();
            mesh.getPoints().addAll(puntos);
            mesh.getFaces().addAll(caras);

            float[] texCoords = {
                0.5f, 0, 0, 1, 1, 1
            };
            mesh.getTexCoords().addAll(texCoords);

            this.setMesh(mesh);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(color);
            this.setMaterial(material);
        }
    }

    public class Tetraedro extends MeshView {
        public Tetraedro(float width, float height, float depth, Color color) {
            float[] puntos = {
                0, height / 2, 0,                     // Vértice superior
                -width / 2, -height / 2, depth / 2,   // Vértice base 1
                width / 2, -height / 2, depth / 2,    // Vértice base 2
                width / 2, -height / 2, -depth / 2    // Vértice base 3
            };
            int[] caras = {
                0, 0, 1, 0, 2, 0,  // Cara 1
                0, 0, 2, 0, 3, 0,  // Cara 2
                0, 0, 3, 0, 1, 0,  // Cara 3
                1, 0, 2, 0, 3, 0   // Cara 4
            };

            TriangleMesh mesh = new TriangleMesh();
            mesh.getPoints().addAll(puntos);
            mesh.getFaces().addAll(caras);

            float[] texCoords = {
                0.5f, 0, 0, 1, 1, 1
            };
            mesh.getTexCoords().addAll(texCoords);

            this.setMesh(mesh);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(color);
            this.setMaterial(material);
        }
    }

    public class Piramide extends MeshView {
        public Piramide(float width, float height, float depth, Color color) {
            float[] puntos = {
                0, height / 2, 0,                     // Vértice superior
                -width / 2, -height / 2, depth / 2,   // Vértice base 1
                width / 2, -height / 2, depth / 2,    // Vértice base 2
                width / 2, -height / 2, -depth / 2,   // Vértice base 3
                -width / 2, -height / 2, -depth / 2   // Vértice base 4
            };
            int[] caras = {
                0, 0, 1, 0, 2, 0,  // Cara 1
                0, 0, 2, 0, 3, 0,  // Cara 2
                0, 0, 3, 0, 4, 0,  // Cara 3
                0, 0, 4, 0, 1, 0,  // Cara 4
                1, 0, 2, 0, 3, 0,  // Base 1
                1, 0, 3, 0, 4, 0   // Base 2
            };

            TriangleMesh mesh = new TriangleMesh();
            mesh.getPoints().addAll(puntos);
            mesh.getFaces().addAll(caras);

            float[] texCoords = {
                0.5f, 0, 0, 1, 1, 1
            };
            mesh.getTexCoords().addAll(texCoords);

            this.setMesh(mesh);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(color);
            this.setMaterial(material);
        }
    }

}