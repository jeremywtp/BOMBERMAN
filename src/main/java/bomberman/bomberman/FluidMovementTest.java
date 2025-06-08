package bomberman.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Test du système de mouvement fluide de Bomberman
 * Démontre le mouvement pixel par pixel avec gestion continue des touches
 */
public class FluidMovementTest extends Application {
    
    private static final int WINDOW_WIDTH = 720;
    private static final int WINDOW_HEIGHT = 624;
    private static final int CELL_SIZE = 48;
    
    private FluidMovementPlayer player;
    private BombermanAnimator animator;
    private Grid testGrid;
    private AnimationTimer gameLoop;
    
    // Variables pour l'affichage debug
    private boolean showDebugInfo = false;
    private long frameCount = 0;
    private long lastFpsUpdate = System.currentTimeMillis();
    private double currentFps = 0;
    
    @Override
    public void start(Stage primaryStage) {
        // Créer une grille de test simple
        createTestGrid();
        
        // Créer le joueur avec mouvement fluide
        player = new FluidMovementPlayer(1, 1);
        
        // Créer l'animateur
        animator = new BombermanAnimator();
        
        // Créer le canvas
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Configuration de la scène
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier (pressé et relâché)
        scene.setOnKeyPressed(this::onKeyPressed);
        scene.setOnKeyReleased(this::onKeyReleased);
        
        // Démarrer la boucle de jeu
        startGameLoop(canvas);
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Test Mouvement Fluide Bomberman");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Donner le focus à la scène
        scene.getRoot().requestFocus();
        
        System.out.println("=== TEST MOUVEMENT FLUIDE ===");
        System.out.println("Flèches : Mouvement continu");
        System.out.println("D : Afficher/masquer debug");
        System.out.println("R : Reset position");
        System.out.println("Echap : Quitter");
    }
    
    /**
     * Crée une grille de test avec quelques obstacles
     */
    private void createTestGrid() {
        testGrid = new Grid(15, 13); // Même taille que le jeu principal
        
        // La grille est initialisée avec des blocs solides sur les bords
        // et quelques blocs destructibles. On peut l'utiliser telle quelle pour le test.
        System.out.println("Grille de test créée : " + testGrid.getColumns() + "x" + testGrid.getRows());
    }
    
    /**
     * Gestion des touches pressées
     */
    private void onKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        
        // Touches de mouvement
        player.onKeyPressed(keyCode);
        
        // Touches de debug/contrôle
        switch (keyCode) {
            case D:
                showDebugInfo = !showDebugInfo;
                System.out.println("Debug info : " + (showDebugInfo ? "ON" : "OFF"));
                break;
            case R:
                // Reset position
                player.setPixelPosition(FluidMovementPlayer.gridToPixel(1), FluidMovementPlayer.gridToPixel(1));
                System.out.println("Position reset");
                break;
            case ESCAPE:
                System.out.println("Fermeture du test...");
                System.exit(0);
                break;
        }
        
        event.consume();
    }
    
    /**
     * Gestion des touches relâchées
     */
    private void onKeyReleased(KeyEvent event) {
        player.onKeyReleased(event.getCode());
        event.consume();
    }
    
    /**
     * Démarre la boucle de jeu
     */
    private void startGameLoop(Canvas canvas) {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(canvas.getGraphicsContext2D());
                updateFpsCounter();
            }
        };
        gameLoop.start();
    }
    
    /**
     * Met à jour la logique du jeu
     */
    private void update() {
        // Mettre à jour le mouvement du joueur
        player.updateMovement(testGrid, this::mockBombCollisionChecker);
        
        // Mettre à jour l'état de marche
        player.updateWalkingState();
        
        // Synchroniser l'animation avec l'état du joueur
        if (player.isWalking() && !animator.isWalking()) {
            animator.startWalking();
        } else if (!player.isWalking() && animator.isWalking()) {
            animator.stopWalking();
        }
        
        // Mettre à jour la direction de l'animation
        animator.setDirection(player.getCurrentDirection());
        
        // Mettre à jour la position de l'animation (coordonnées flottantes)
        animator.setPixelPosition(player.getPixelX(), player.getPixelY(), 0, 0);
    }
    
    /**
     * Mock du checker de collision avec bombes (pas de bombes dans ce test)
     */
    private boolean mockBombCollisionChecker(int x, int y, boolean isPlayer) {
        return false; // Pas de bombes dans ce test
    }
    
    /**
     * Effectue le rendu
     */
    private void render(GraphicsContext gc) {
        // Nettoyer l'écran
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Dessiner la grille
        drawGrid(gc);
        
        // Dessiner les obstacles
        drawObstacles(gc);
        
        // Dessiner le joueur avec l'animation
        animator.render(gc);
        
        // Dessiner les informations de debug si activées
        if (showDebugInfo) {
            drawDebugInfo(gc);
        } else {
            drawBasicInfo(gc);
        }
    }
    
    /**
     * Dessine la grille de référence
     */
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);
        
        // Lignes verticales
        for (int x = 0; x <= WINDOW_WIDTH; x += CELL_SIZE) {
            gc.strokeLine(x, 0, x, WINDOW_HEIGHT);
        }
        
        // Lignes horizontales
        for (int y = 0; y <= WINDOW_HEIGHT; y += CELL_SIZE) {
            gc.strokeLine(0, y, WINDOW_WIDTH, y);
        }
    }
    
    /**
     * Dessine les obstacles de la grille
     */
    private void drawObstacles(GraphicsContext gc) {
        for (int row = 0; row < testGrid.getRows(); row++) {
            for (int col = 0; col < testGrid.getColumns(); col++) {
                TileType tileType = testGrid.getTileType(col, row);
                
                if (tileType == TileType.SOLID) {
                    // Blocs solides en bleu foncé
                    gc.setFill(Color.DARKBLUE);
                    gc.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (tileType == TileType.DESTRUCTIBLE) {
                    // Blocs destructibles en marron
                    gc.setFill(Color.BROWN);
                    gc.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
    
    /**
     * Dessine les informations de base
     */
    private void drawBasicInfo(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillText("Flèches: Mouvement fluide", 10, 20);
        gc.fillText("D: Debug  R: Reset  Echap: Quitter", 10, 40);
        gc.fillText("FPS: " + String.format("%.1f", currentFps), 10, 60);
    }
    
    /**
     * Dessine les informations de debug détaillées
     */
    private void drawDebugInfo(GraphicsContext gc) {
        // Fond semi-transparent
        gc.setFill(Color.color(0, 0, 0, 0.8));
        gc.fillRect(10, 10, 350, 200);
        
        // Informations détaillées
        gc.setFill(Color.WHITE);
        int y = 30;
        int lineHeight = 15;
        
        gc.fillText("=== DEBUG MOUVEMENT FLUIDE ===", 20, y);
        y += lineHeight * 1.5;
        
        gc.fillText("Position pixels: " + String.format("%.1f, %.1f", player.getPixelX(), player.getPixelY()), 20, y);
        y += lineHeight;
        
        gc.fillText("Position grille: " + player.getX() + ", " + player.getY(), 20, y);
        y += lineHeight;
        
        gc.fillText("Direction: " + player.getCurrentDirection(), 20, y);
        y += lineHeight;
        
        gc.fillText("En mouvement: " + (player.isMoving() ? "OUI" : "NON"), 20, y);
        y += lineHeight;
        
        gc.fillText("Animation marche: " + (animator.isWalking() ? "OUI" : "NON"), 20, y);
        y += lineHeight;
        
        gc.fillText("Frame animation: " + animator.getCurrentFrame(), 20, y);
        y += lineHeight;
        
        gc.fillText("Vitesse: " + String.format("%.1f px/s", player.getEffectiveSpeedPixelsPerSecond()), 20, y);
        y += lineHeight;
        
        gc.fillText("FPS: " + String.format("%.1f", currentFps), 20, y);
        y += lineHeight;
        
        // Instructions
        gc.setFill(Color.YELLOW);
        gc.fillText("D: Masquer debug", 20, 190);
    }
    
    /**
     * Met à jour le compteur FPS
     */
    private void updateFpsCounter() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastFpsUpdate >= 1000) {
            currentFps = frameCount * 1000.0 / (currentTime - lastFpsUpdate);
            frameCount = 0;
            lastFpsUpdate = currentTime;
        }
    }
    
    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (animator != null) {
            animator.dispose();
        }
        System.out.println("Test fermé - Ressources libérées");
    }
    
    /**
     * Point d'entrée du test
     */
    public static void main(String[] args) {
        launch(args);
    }
} 