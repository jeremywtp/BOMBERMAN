package bomberman.bomberman;

import javafx.animation.AnimationTimer;
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
 * Gère maintenant les interactions clavier pour déplacer le joueur,
 * poser des bombes et gérer les explosions.
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
    
    // Gestion des bombes et explosions
    private Bomb activeBomb;
    private Explosion activeExplosion;
    
    // Timer d'animation pour les mises à jour
    private AnimationTimer gameTimer;
    
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
        
        // Gestion des événements clavier pour déplacer le joueur et poser des bombes
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
        
        // Démarrer le timer d'animation pour les mises à jour
        startGameTimer();
    }
    
    /**
     * Démarre le timer d'animation pour les mises à jour du jeu
     */
    private void startGameTimer() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
            }
        };
        gameTimer.start();
    }
    
    /**
     * Met à jour l'état du jeu (bombes, explosions)
     */
    private void updateGame() {
        boolean needsRedraw = false;
        
        // Mettre à jour la bombe active
        if (activeBomb != null) {
            if (activeBomb.update()) {
                // La bombe a explosé
                createExplosion();
                activeBomb = null;
                player.setHasActiveBomb(false);
                needsRedraw = true;
            }
        }
        
        // Mettre à jour l'explosion active
        if (activeExplosion != null) {
            if (activeExplosion.update()) {
                // L'explosion est terminée
                activeExplosion = null;
                needsRedraw = true;
            }
        }
        
        // Redessiner si nécessaire
        if (needsRedraw) {
            renderer.render(player, activeBomb, activeExplosion);
        }
    }
    
    /**
     * Crée une explosion à partir de la bombe active
     */
    private void createExplosion() {
        if (activeBomb != null) {
            activeExplosion = new Explosion(
                activeBomb.getX(), 
                activeBomb.getY(), 
                Bomb.getExplosionRange(), 
                grid
            );
        }
    }
    
    /**
     * Gère les événements de touches pressées
     * @param keyCode Le code de la touche pressée
     */
    private void handleKeyPressed(KeyCode keyCode) {
        boolean needsRedraw = false;
        
        // Traiter les déplacements selon les flèches directionnelles
        switch (keyCode) {
            case UP:
                if (player.moveUp(grid)) {
                    needsRedraw = true;
                }
                break;
            case DOWN:
                if (player.moveDown(grid)) {
                    needsRedraw = true;
                }
                break;
            case LEFT:
                if (player.moveLeft(grid)) {
                    needsRedraw = true;
                }
                break;
            case RIGHT:
                if (player.moveRight(grid)) {
                    needsRedraw = true;
                }
                break;
            case SPACE:
                // Poser une bombe
                if (tryPlaceBomb()) {
                    needsRedraw = true;
                }
                break;
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Redessiner la scène si nécessaire
        if (needsRedraw) {
            renderer.render(player, activeBomb, activeExplosion);
        }
    }
    
    /**
     * Tente de placer une bombe à la position actuelle du joueur
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBomb() {
        // Vérifier si le joueur peut poser une bombe
        if (!player.hasActiveBomb() && activeBomb == null) {
            activeBomb = new Bomb(player.getX(), player.getY());
            player.setHasActiveBomb(true);
            return true;
        }
        return false;
    }
    
    /**
     * Arrête le timer d'animation
     */
    @Override
    public void stop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 