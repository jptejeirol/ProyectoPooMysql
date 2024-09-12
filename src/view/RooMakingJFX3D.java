package view;

import controller.implement.CrearItem;
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
import controller.implement.movibleobjects.Movible;
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
import controller.implement.movibleobjects.Armario;
import controller.implement.movibleobjects.CamaDoble;
import controller.implement.movibleobjects.CamaSimple;
import controller.implement.movibleobjects.Cylinder3D;
import controller.implement.movibleobjects.ItemNuevo;
import controller.implement.movibleobjects.Mesa;
import controller.implement.movibleobjects.MesaDeNoche;
import controller.implement.movibleobjects.Mueble;
import controller.implement.movibleobjects.Silla;
import controller.implement.movibleobjects.Sphere3D;
import controller.implement.movibleobjects.TV;
import controller.implement.movibleobjects.TVGrande;
import controller.implement.movibleobjects.Ventana;
import controller.services.ServicesIluminacion;
import model.services.ServicesRoom;
import model.services.ServicesItem;
import model.services.ServicesRoomJFX;
import model.services.ServicesUsuario;
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
import javafx.stage.Modality;
import javafx.util.Pair;

/**
 *
 * @author Juan Pablo Tejeiro, Santiago Villareal, Juan José Hernandez, Sergio Nicolas Vanegas;
 * Grupo Roomade 
 * 
 */

public class RooMakingJFX3D{

    private double mouseX, mouseY;
    private double rotateX = 0, rotateY = 0;
    private double rotateSpeed = 0.2;
    private double zoomSpeed = 1.1;
    private double gravity = 5;
    private double floorY = 1000;
        
    private Shape3D selectedObject;
    private Group selectedGroup;
    private TextArea hashArea;
    private double objectMouseOffsetX, objectMouseOffsetY, objectMouseOffsetZ;
    
    private List<Movible> shapes = new ArrayList<>(); 
    private Movible selectedShape = null;
    private boolean positionsLocked = false;
    
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
    
    ServicesRoomJFX servRoomJFX = new ServicesRoomJFX();
    ServicesIluminacion servIlum = new ServicesIluminacion();

    VBox createControlPanel(SubScene scene, PerspectiveCamera cameraInternal, PerspectiveCamera cameraExternal, Group root3D) {
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
                cameraInternal.setTranslateX(0); 
                cameraInternal.setTranslateY(0);
                cameraInternal.setTranslateZ(0);
                cameraInternal.setRotate(0);
                scene.requestFocus();
                
            } else {
                scene.setCamera(cameraExternal);
                scene.setCursor(Cursor.DEFAULT);
                scene.requestFocus();
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
        roomButton.setOnAction(e -> showRoomDialog(root3D)) ; 
        
        Button itemButton = new Button("Crear Item");
        itemButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgb(126,188,137), rgb(162,217,206));" + // Degradado de verde
            "-fx-text-fill: white;" +  // Texto en blanco
            "-fx-font-weight: bold;" +  // Texto en negrita
            "-fx-border-color: rgb(126,188,137);" +  // Borde del botón
            "-fx-border-radius: 5;" +  // Bordes redondeados
            "-fx-background-radius: 5;"  // Bordes redondeados para el fondo
        );
        itemButton.setOnAction(e -> showItemDialog(root3D) );
        
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
        groupSelector.setOnAction(e -> addGroup(groupSelector.getValue(), root3D));
        
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
        iluminacionButton.setOnAction(e -> servIlum.opcionesIluminacion(wallLeft, wallBack, root3D));        
        
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
                String encodedJson = saveScene(root3D); // Asegúrate de que saveScene() devuelva el JSON codificado como String

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
        loadButton.setOnAction(e -> showCargarDialog(root3D));        
        
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
            togglePositionsLock(root3D);
  
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
            OrdenarObjetos(root3D);
            //serItems.OrdenarObjetos(root3D, wallBack, wallLeft, contieneLuzPared1, contieneLuzPared2, contieneLuzPared3, contieneLuzTecho, contieneVentana1, contieneVentana2, contieneVentana3, positionsLocked);
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

    private void OrdenarObjetos(Group root3D) {
        Random random = new Random();
        double rangoMinX = -wallBack.getWidth() / 2;
        double rangoMaxX = wallBack.getWidth() / 2;
        double rangoMinZ = -wallLeft.getDepth() / 2;
        double rangoMaxZ = wallLeft.getDepth() / 2;
        double margenSeguridad = 50;
        List<Movible> colocados = new ArrayList<>();
        List<Pair<Double, Double>> espaciosOcupadosX = new ArrayList<>();
        List<Pair<Double, Double>> espaciosOcupadosZ = new ArrayList<>();
        
        boolean considerarIluminacion = servIlum.actualizarEstadoIluminacion(root3D);

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
                        System.out.println("No se encontró una posición adecuada para el objeto tras " + maxIntentos + " intentos.");
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

                    double nuevaX, nuevaZ;
                    int rotacion;

                    if (considerarIluminacion) {
                        Pair<Double, Double> posicion = servIlum.determinarPosicionSegunIluminacion(movible, efectivoMinX, efectivoMaxX, efectivoMinZ, efectivoMaxZ);
                        nuevaX = posicion.getKey();
                        nuevaZ = posicion.getValue();
                        rotacion = determinarRotacion(nuevaX, nuevaZ, efectivoMinX, efectivoMaxX, efectivoMinZ, efectivoMaxZ);
                    } else {
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

    public int determinarRotacion(double x, double z, double minX, double maxX, double minZ, double maxZ) {
        if (x == minX) return 0;
        if (x == maxX) return 180;
        if (z == minZ) return 270;
        if (z == maxZ) return 90;
        return 0; // Default rotation
    }

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

    private void showDimensionDialog(String objectType, Group root3D) {
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
                addObject(objectType, width, height, depth, root3D);
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
        
    private void showRoomDialog(Group root3D) {
        Stage dialog = new Stage();
        dialog.setTitle("Elija la room sobre la cual desea trabajar");

        TableView<Room> tableView = new TableView<>();

        TableColumn<Room, String> column1 = new TableColumn<>("Room");
        column1.setCellValueFactory(new PropertyValueFactory<>("nombreObjeto"));

        TableColumn<Room, Double> column2 = new TableColumn<>("Base");
        column2.setCellValueFactory(new PropertyValueFactory<>("base"));

        TableColumn<Room, Double> column3 = new TableColumn<>("Altura");
        column3.setCellValueFactory(new PropertyValueFactory<>("altura"));

        TableColumn<Room, Double> column4 = new TableColumn<>("Profundidad");
        column4.setCellValueFactory(new PropertyValueFactory<>("profundidad"));

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);

        tableView.setPrefHeight(200);
        tableView.setPrefWidth(500);

        for (Room room : ServicesRoom.getRooms()) {
            tableView.getItems().add(room);
        }
            
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setRoomSeleccionada(newSelection);
            }
        });

        Button createButton = new Button("Crear");
        createButton.setOnAction(e -> {
            try {
                double width = roomSeleccionada.getBase();
                double height = roomSeleccionada.getAltura();
                double depth = roomSeleccionada.getProfundidad();

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
            } 
            catch (NumberFormatException ex) {
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
        
    private void showItemDialog(Group root3D) {
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
                setupContextMenu(newObjectItem, root3D); // Configurar menú contextual

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
    
    public static void togglePositionsLock(Group root3D) {
        for (javafx.scene.Node node : root3D.getChildren()) {
            if (node instanceof Movible) {
                Movible movible = (Movible) node;
                movible.setFijo(!movible.isFijo()); // Alternar el estado de "fijo"
            }
        }
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
    
    private void addObject(String objectType, double width, double height, double depth, Group root3D) {
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
            setupContextMenu(newObject, root3D); // Configurar menú contextual
            root3D.getChildren().add(newObject);
        }
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
    
    private void addGroup(String groupType, Group root3D) {
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
            setupContextMenu(newObject, root3D); // Configurar menú contextual
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

    private void setupContextMenu(Shape3D object, Group root3D) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Eliminar");
        removeItem.setOnAction(e -> root3D.getChildren().remove(object));
        
        MenuItem rotateItem = new MenuItem("Rotar");
        rotateItem.setOnAction(e -> rotateObjectY(object));
        
        contextMenu.getItems().addAll(removeItem, rotateItem);

        object.setOnContextMenuRequested(e -> contextMenu.show(object, e.getScreenX(), e.getScreenY()));
    }
    
    private void setupContextMenu(Group group, Group root3D) {
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
    
    public static void rotateObjectY(Shape3D object) {
        Rotate rotate = new Rotate(90, Rotate.Y_AXIS);
        object.getTransforms().add(rotate);
    }    
    
    public static void rotateObjectY(Group group) {
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

    private Point3D unproject(double sceneX, double sceneY, double z) {
        double subSceneX = sceneX - RooMakingJFX3DStart.getSubScene().getLayoutX();
        double subSceneY = sceneY - RooMakingJFX3DStart.getSubScene().getLayoutY();

        double normalizedX = (subSceneX / RooMakingJFX3DStart.getSubScene().getWidth()) * 2 - 1;
        double normalizedY = -((subSceneY / RooMakingJFX3DStart.getSubScene().getHeight()) * 2 - 1);

        Point3D clipPoint = new Point3D(normalizedX, normalizedY, -1);

        Point3D viewPoint = RooMakingJFX3DStart.getCameraExterna().localToScene(clipPoint);
        viewPoint = RooMakingJFX3DStart.getCameraExterna().sceneToLocal(viewPoint);

        Point3D worldPoint = new Point3D(viewPoint.getX() * -RooMakingJFX3DStart.getCameraExterna().getTranslateZ(), 
                                         viewPoint.getY() * RooMakingJFX3DStart.getCameraExterna().getTranslateZ(), 
                                         z);

        Rotate rxInverse = new Rotate(-rotateY, Rotate.X_AXIS);
        Rotate ryInverse = new Rotate(-rotateX, Rotate.Y_AXIS);
        worldPoint = rxInverse.transform(worldPoint);
        worldPoint = ryInverse.transform(worldPoint);

        return worldPoint;
    }

    void startPhysics(Group root3D) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            for (javafx.scene.Node node : root3D.getChildren()) {
                if ((node instanceof Shape3D || node instanceof Group) && node != selectedObject) {
                    if (node instanceof Ventana){
                        detectCollisions(node, root3D);                    
                    } else {
                        applyGravity(node);
                        detectCollisions(node, root3D);                    
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

    private void detectCollisions(javafx.scene.Node node, Group root3D) {
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

    private String saveScene(Group root3D) {
        Map<String, Object> sceneData = new HashMap<>();
        List<Map<String, Object>> objectsData = new ArrayList<>();

        for (javafx.scene.Node node : root3D.getChildren()) {
            if (node instanceof Shape3D || node instanceof Group || node instanceof Movible || node instanceof PointLight) {
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

    private void loadScene(String encodedJson, Group root3D) {
        // Aquí deberías obtener el 'encodedJson' desde un archivo o entrada del usuario
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedJson);
            String json = new String(decodedBytes);
            Map<String, Object> sceneData = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            root3D.getChildren().clear();
            List<Map<String, Object>> objectsData = (List<Map<String, Object>>) sceneData.get("objects");
            for (Map<String, Object> objectData : objectsData) {
                javafx.scene.Node node = decodeObject(objectData, root3D);
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

        if (node instanceof Shape3D shape) {
            encodeShape3D(shape, objectData);
        } else if (node instanceof Group group) {
            encodeGroup(group, objectData);
        } else if (node instanceof PointLight light) {
            encodePointLight(light, objectData);
        }

        encodeTransforms(node, objectData);

        return objectData;
    }

    private void encodeShape3D(Shape3D shape, Map<String, Object> objectData) {
        PhongMaterial material = (PhongMaterial) shape.getMaterial();
        if (material != null) {
            Color color = material.getDiffuseColor();
            objectData.put("color", String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255)));
        }

        if (shape instanceof Box box) {
            objectData.put("width", box.getWidth());
            objectData.put("height", box.getHeight());
            objectData.put("depth", box.getDepth());
        } else if (shape instanceof Sphere sphere) {
            objectData.put("radius", sphere.getRadius());
        } else if (shape instanceof Cylinder cylinder) {
            objectData.put("radius", cylinder.getRadius());
            objectData.put("height", cylinder.getHeight());
        }
    }

    private void encodeGroup(Group group, Map<String, Object> objectData) {
        List<Map<String, Object>> childrenData = new ArrayList<>();
        for (javafx.scene.Node child : group.getChildren()) {
            childrenData.add(encodeObject(child));
        }
        objectData.put("children", childrenData);
    }

    private void encodePointLight(PointLight light, Map<String, Object> objectData) {
        Color color = light.getColor();
        objectData.put("color", String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)));
    }

    private void encodeTransforms(javafx.scene.Node node, Map<String, Object> objectData) {
        List<Map<String, Object>> transformsData = new ArrayList<>();
        for (Transform transform : node.getTransforms()) {
            if (transform instanceof Rotate rotate) {
                Map<String, Object> rotateData = new HashMap<>();
                rotateData.put("type", "Rotate");
                rotateData.put("angle", rotate.getAngle());
                rotateData.put("pivotX", rotate.getPivotX());
                rotateData.put("pivotY", rotate.getPivotY());
                rotateData.put("pivotZ", rotate.getPivotZ());
                Point3D axis = rotate.getAxis();
                rotateData.put("axisX", axis.getX());
                rotateData.put("axisY", axis.getY());
                rotateData.put("axisZ", axis.getZ());
                transformsData.add(rotateData);
            }
        }
        if (!transformsData.isEmpty()) {
            objectData.put("transforms", transformsData);
        }
    }
    
    private javafx.scene.Node decodeObject(Map<String, Object> objectData, Group root3D) {
        String type = (String) objectData.get("type");
        javafx.scene.Node node = null;

        switch (type) {
            case "Box", "Sphere", "Cylinder" -> node = createShape3D(type, objectData, root3D);
            case "Group", "ItemNuevo", "MesaDeNoche", "Mueble", "Armario", "Mesa", "Silla", "CamaSimple", "CamaDoble", "TV", "TVGrande", "Ventana" -> node = createGroupNode(type, objectData, root3D);
            case "PointLight" -> node = createPointLight(objectData);
        }

        if (node != null) {
            setCommonProperties(node, objectData);
        }

        return node;
    }

    private Shape3D createShape3D(String type, Map<String, Object> objectData, Group root3D) {
        Shape3D shape = switch (type) {
            case "Box" -> new Box(
                (double) objectData.get("width"),
                (double) objectData.get("height"),
                (double) objectData.get("depth")
            );
            case "Sphere" -> new Sphere((double) objectData.get("radius"));
            case "Cylinder" -> new Cylinder(
                (double) objectData.get("radius"),
                (double) objectData.get("height")
            );
            default -> throw new IllegalArgumentException("Unknown shape type: " + type);
        };

        setColorForShape3D(shape, objectData);
        setupContextMenu(shape, root3D);
        return shape;
    }

    private void setColorForShape3D(Shape3D shape, Map<String, Object> objectData) {
        String colorStr = (String) objectData.get("color");
        if (colorStr != null && !colorStr.isEmpty()) {
            try {
                Color color = Color.web(colorStr);
                shape.setMaterial(new PhongMaterial(color));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid color string for Shape3D: " + colorStr);
            }
        }
    }

    private Group createGroupNode(String type, Map<String, Object> objectData, Group root3D) {
        Group group = switch (type) {
            case "Group" -> new Group();
            case "ItemNuevo" -> new ItemNuevo(
                getDoubleOrDefault(objectData, "width", 100.0),
                getDoubleOrDefault(objectData, "height", 100.0),
                getDoubleOrDefault(objectData, "depth", 100.0)
            );
            case "MesaDeNoche" -> new MesaDeNoche();
            case "Mueble" -> new Mueble();
            case "Armario" -> new Armario();
            case "Mesa" -> new Mesa();
            case "Silla" -> new Silla();
            case "CamaSimple" -> new CamaSimple();
            case "CamaDoble" -> new CamaDoble();
            case "TV" -> new TV();
            case "TVGrande" -> new TVGrande();
            case "Ventana" -> new Ventana();
            default -> throw new IllegalArgumentException("Unknown group type: " + type);
        };

        List<Map<String, Object>> childrenData = (List<Map<String, Object>>) objectData.get("children");
        if (childrenData != null) {
            for (Map<String, Object> childData : childrenData) {
                javafx.scene.Node child = decodeObject(childData, root3D);
                if (child != null) {
                    group.getChildren().add(child);
                }
            }
        }

        if (group instanceof ItemNuevo itemNuevo) {
            itemNuevo.setTranslateX(0);
            itemNuevo.setTranslateY(-500);
            itemNuevo.setTranslateZ(0);
            itemNuevo.setOnMousePressed(this::handleGroupPressed);
            itemNuevo.setOnMouseDragged(this::handleGroupDragged);
        }

        setupContextMenu(group, root3D);
        return group;
    }

    private PointLight createPointLight(Map<String, Object> objectData) {
        PointLight light = new PointLight();
        String colorStr = (String) objectData.get("color");
        if (colorStr != null && !colorStr.isEmpty()) {
            try {
                light.setColor(Color.web(colorStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid color string for PointLight: " + colorStr);
            }
        }
        return light;
    }

    private void setCommonProperties(javafx.scene.Node node, Map<String, Object> objectData) {
        node.setTranslateX((double) objectData.getOrDefault("translateX", 0.0));
        node.setTranslateY((double) objectData.getOrDefault("translateY", 0.0));
        node.setTranslateZ((double) objectData.getOrDefault("translateZ", 0.0));

        List<Map<String, Object>> transformsData = (List<Map<String, Object>>) objectData.get("transforms");
        if (transformsData != null) {
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
                    node.getTransforms().add(new Rotate(angle, pivotX, pivotY, pivotZ, axis));
                }
            }
        }
    }

    private double getDoubleOrDefault(Map<String, Object> data, String key, double defaultValue) {
        Object value = data.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : defaultValue;
    }
    
    private void showCargarDialog(Group root3D) {
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
                    loadScene(elemento, root3D);
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

    public void addItemToScene(Group itemGroup, Group root3D) {
        itemGroup.setTranslateX(0);
        itemGroup.setTranslateY(0);
        itemGroup.setTranslateZ(0);
        itemGroup.setOnMousePressed(this::handleGroupPressed);
        itemGroup.setOnMouseDragged(this::handleGroupPressed);
        setupContextMenu(itemGroup, root3D);
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
}