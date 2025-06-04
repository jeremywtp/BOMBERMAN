package bomberman.bomberman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Classe principale de l'application Bomberman.
 * Responsable du lancement de l'application JavaFX,
 * de l'initialisation de la fenêtre et de la scène.
 * Délègue le dessin à GridRenderer et la logique à Grid.
 * Gère maintenant les interactions clavier pour déplacer le joueur.
 */
public class Launcher extends Application {
    
    // Dimensions de la fenêtre de jeu
    private static final int WINDOW_WIDTH = 480;
    private static final int WINDOW_HEIGHT = 352;
    
    // Dimensions de la grille (nombre de cases)
    private static final int GRID_COLUMNS = 15;  // 480/32 = 15 cases en largeur
    private static final int GRID_ROWS = 11;     // 352/32 = 11 cases en hauteur
    
    // Position de départ du joueur (première case vide disponible)
    private static final int PLAYER_START_X = 1;
    private static final int PLAYER_START_Y = 1;
    
    // Composants du jeu
    private Grid grid;
    private Player player;
    private GridRenderer renderer;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialisation du modèle de données de la grille
        grid = new Grid(GRID_COLUMNS, GRID_ROWS);
        
        // Initialisation du joueur à une position de départ valide
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        
        // Création du canvas pour le dessin
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialisation du renderer pour dessiner la grille et le joueur
        renderer = new GridRenderer(canvas, grid);
        renderer.render(player);  // Rendu initial avec le joueur
        
        // Configuration de la scène
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier pour déplacer le joueur
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Bomberman Base");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Fenêtre non redimensionnable pour garder les proportions
        primaryStage.show();
        
        // Donner le focus à la scène pour capturer les événements clavier
        scene.getRoot().requestFocus();
    }
    
    /**
     * Gère les événements de touches pressées
     * @param keyCode Le code de la touche pressée
     */
    private void handleKeyPressed(KeyCode keyCode) {
        boolean playerMoved = false;
        
        // Traiter les déplacements selon les flèches directionnelles
        switch (keyCode) {
            case UP:
                playerMoved = player.moveUp(grid);
                break;
            case DOWN:
                playerMoved = player.moveDown(grid);
                break;
            case LEFT:
                playerMoved = player.moveLeft(grid);
                break;
            case RIGHT:
                playerMoved = player.moveRight(grid);
                break;
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Redessiner la scène si le joueur a bougé
        if (playerMoved) {
            renderer.render(player);
        }
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 