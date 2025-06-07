package bomberman.bomberman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Application de test pour démontrer l'utilisation de ContoursMapView.
 * 
 * Cette application montre :
 * - Le chargement du sprite contours_map.png
 * - La grille 13×11 tuiles de 16px parfaitement alignée
 * - Des boutons pour tester différents patterns de grille
 * - Les dimensions exactes respectées (272×176 px)
 */
public class ContoursMapTestApp extends Application {
    
    private ContoursMapView contoursMapView;
    
    @Override
    public void start(Stage primaryStage) {
        // 🖼️ Créer la vue principale avec sprite + grille
        contoursMapView = new ContoursMapView();
        
        // 🎛️ Créer les contrôles de test
        VBox controls = createControlPanel();
        
        // 📊 Créer le panneau d'informations
        VBox infoPanel = createInfoPanel();
        
        // 📦 Organiser l'interface
        BorderPane root = new BorderPane();
        root.setCenter(contoursMapView);
        root.setRight(controls);
        root.setLeft(infoPanel);
        root.setPadding(new Insets(20));
        
        // 🖥️ Configurer la fenêtre
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setTitle("Test ContoursMapView - Sprite 272×176 + Grille 13×11×16px");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
        
        // 🧪 Charger un pattern de test initial
        contoursMapView.generateTestPattern();
        
        System.out.println("🚀 Application de test lancée");
        System.out.println("📏 Sprite: " + ContoursMapView.getSpriteWidth() + "×" + ContoursMapView.getSpriteHeight() + " px");
        System.out.println("🧱 Grille: " + ContoursMapView.getGameColumns() + "×" + ContoursMapView.getGameRows() + 
                          " tuiles de " + ContoursMapView.getTileSize() + "px");
        System.out.println("📍 Position: (" + ContoursMapView.getMapOffsetX() + "," + ContoursMapView.getMapOffsetY() + ")");
    }
    
    /**
     * 🎛️ Crée le panneau de contrôles pour tester la grille
     */
    private VBox createControlPanel() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("🎛️ Contrôles de Test");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Boutons de test
        Button btnTestPattern = new Button("🧪 Pattern de Test");
        btnTestPattern.setOnAction(e -> contoursMapView.generateTestPattern());
        
        Button btnClearGrid = new Button("🧹 Effacer Grille");
        btnClearGrid.setOnAction(e -> contoursMapView.clearGrid());
        
        Button btnBorderOnly = new Button("🔲 Bordures Seulement");
        btnBorderOnly.setOnAction(e -> generateBorderPattern());
        
        Button btnCheckerboard = new Button("♟️ Damier");
        btnCheckerboard.setOnAction(e -> generateCheckerboardPattern());
        
        Button btnRandomBlocks = new Button("🎲 Blocs Aléatoires");
        btnRandomBlocks.setOnAction(e -> generateRandomPattern());
        
        // Organiser les contrôles
        controls.getChildren().addAll(
            title,
            btnTestPattern,
            btnClearGrid,
            btnBorderOnly,
            btnCheckerboard,
            btnRandomBlocks
        );
        
        // Style des boutons
        controls.getChildren().stream()
            .filter(node -> node instanceof Button)
            .forEach(button -> {
                ((Button) button).setPrefWidth(150);
                ((Button) button).setStyle("-fx-font-size: 12px;");
            });
        
        return controls;
    }
    
    /**
     * 📊 Crée le panneau d'informations techniques
     */
    private VBox createInfoPanel() {
        VBox info = new VBox(8);
        info.setPadding(new Insets(10));
        info.setAlignment(Pos.TOP_LEFT);
        
        Label title = new Label("📊 Spécifications");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Informations techniques
        Label spriteInfo = new Label("🖼️ Sprite: " + ContoursMapView.getSpriteWidth() + "×" + ContoursMapView.getSpriteHeight() + " px");
        Label gridInfo = new Label("🧱 Grille: " + ContoursMapView.getGameColumns() + "×" + ContoursMapView.getGameRows() + " tuiles");
        Label tileInfo = new Label("📐 Tuile: " + ContoursMapView.getTileSize() + "×" + ContoursMapView.getTileSize() + " px");
        Label positionInfo = new Label("📍 Position: (" + ContoursMapView.getMapOffsetX() + "," + ContoursMapView.getMapOffsetY() + ")");
        Label mapSizeInfo = new Label("📏 Zone jeu: " + (ContoursMapView.getGameColumns() * ContoursMapView.getTileSize()) + 
                                     "×" + (ContoursMapView.getGameRows() * ContoursMapView.getTileSize()) + " px");
        
        // Légende des couleurs
        Label legend = new Label("🎨 Légende:");
        legend.setStyle("-fx-font-weight: bold; -fx-margin-top: 10px;");
        Label emptyInfo = new Label("   🔳 Vide = Transparent");
        Label solidInfo = new Label("   🟫 Solide = Gris");
        Label destructibleInfo = new Label("   🟤 Destructible = Marron");
        
        info.getChildren().addAll(
            title,
            spriteInfo,
            gridInfo,
            tileInfo,
            positionInfo,
            mapSizeInfo,
            new Label(""), // Espacement
            legend,
            emptyInfo,
            solidInfo,
            destructibleInfo
        );
        
        // Style du texte
        info.getChildren().stream()
            .filter(node -> node instanceof Label && node != title && node != legend)
            .forEach(label -> ((Label) label).setStyle("-fx-font-size: 11px; -fx-font-family: monospace;"));
        
        return info;
    }
    
    /**
     * 🔲 Génère un pattern avec seulement les bordures
     */
    private void generateBorderPattern() {
        contoursMapView.clearGrid();
        
        // Bordures : blocs solides
        for (int col = 0; col < ContoursMapView.getGameColumns(); col++) {
            contoursMapView.setCellType(col, 0, TileType.SOLID);                    // Ligne du haut
            contoursMapView.setCellType(col, ContoursMapView.getGameRows() - 1, TileType.SOLID);  // Ligne du bas
        }
        for (int row = 0; row < ContoursMapView.getGameRows(); row++) {
            contoursMapView.setCellType(0, row, TileType.SOLID);                    // Colonne de gauche
            contoursMapView.setCellType(ContoursMapView.getGameColumns() - 1, row, TileType.SOLID); // Colonne de droite
        }
        
        System.out.println("🔲 Pattern bordures appliqué");
    }
    
    /**
     * ♟️ Génère un pattern en damier
     */
    private void generateCheckerboardPattern() {
        contoursMapView.clearGrid();
        
        for (int col = 0; col < ContoursMapView.getGameColumns(); col++) {
            for (int row = 0; row < ContoursMapView.getGameRows(); row++) {
                if ((col + row) % 2 == 0) {
                    contoursMapView.setCellType(col, row, TileType.SOLID);
                } else {
                    contoursMapView.setCellType(col, row, TileType.DESTRUCTIBLE);
                }
            }
        }
        
        System.out.println("♟️ Pattern damier appliqué");
    }
    
    /**
     * 🎲 Génère un pattern avec des blocs aléatoires
     */
    private void generateRandomPattern() {
        contoursMapView.clearGrid();
        
        // Bordures fixes
        generateBorderPattern();
        
        // Remplissage aléatoire du centre
        for (int col = 1; col < ContoursMapView.getGameColumns() - 1; col++) {
            for (int row = 1; row < ContoursMapView.getGameRows() - 1; row++) {
                double random = Math.random();
                if (random < 0.3) {
                    contoursMapView.setCellType(col, row, TileType.SOLID);
                } else if (random < 0.6) {
                    contoursMapView.setCellType(col, row, TileType.DESTRUCTIBLE);
                }
                // Sinon reste vide (transparent)
            }
        }
        
        System.out.println("🎲 Pattern aléatoire appliqué");
    }
    
    /**
     * 🚀 Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 