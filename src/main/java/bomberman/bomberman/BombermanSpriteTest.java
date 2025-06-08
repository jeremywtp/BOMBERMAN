package bomberman.bomberman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Classe de test pour démontrer l'utilisation de BombermanSprite
 * avec rendu pixel perfect et conservation des proportions originales.
 * 
 * Utilisation :
 * - Flèches directionnelles pour changer la direction
 * - Espace pour afficher les informations du sprite
 * - Echap pour quitter
 */
public class BombermanSpriteTest extends Application {
    
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int CELL_SIZE = 48;
    
    private BombermanSprite bombermanSprite;
    private GraphicsContext gc;
    
    @Override
    public void start(Stage primaryStage) {
        // Créer le canvas
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // Initialiser le sprite de Bomberman
        bombermanSprite = new BombermanSprite();
        
        // Positionner le sprite au centre de la fenêtre (case 4,4)
        bombermanSprite.setPosition(4, 4, 0, 0);
        
        // Rendu initial
        render();
        
        // Configuration de la scène
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
            render(); // Redessiner après chaque action
        });
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Test BombermanSprite - Pixel Perfect");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Donner le focus pour capturer les événements clavier
        scene.getRoot().requestFocus();
        
        System.out.println("=== TEST BOMBERMAN SPRITE ===");
        System.out.println("Flèches : Changer direction");
        System.out.println("Espace : Infos sprite");
        System.out.println("Echap : Quitter");
        System.out.println("Direction initiale : " + bombermanSprite.getCurrentDirection());
    }
    
    /**
     * Gère les événements clavier
     */
    private void handleKeyPressed(KeyCode keyCode) {
        switch (keyCode) {
            case UP:
                bombermanSprite.setDirection("haut");
                System.out.println("Direction changée : " + bombermanSprite.getCurrentDirection());
                break;
                
            case DOWN:
                bombermanSprite.setDirection("bas");
                System.out.println("Direction changée : " + bombermanSprite.getCurrentDirection());
                break;
                
            case LEFT:
                bombermanSprite.setDirection("gauche");
                System.out.println("Direction changée : " + bombermanSprite.getCurrentDirection());
                break;
                
            case RIGHT:
                bombermanSprite.setDirection("droite");
                System.out.println("Direction changée : " + bombermanSprite.getCurrentDirection());
                break;
                
            case SPACE:
                displaySpriteInfo();
                break;
                
            case ESCAPE:
                System.out.println("Fermeture du test...");
                System.exit(0);
                break;
        }
    }
    
    /**
     * Affiche les informations du sprite actuel
     */
    private void displaySpriteInfo() {
        System.out.println("=== INFORMATIONS SPRITE ===");
        System.out.println("Direction : " + bombermanSprite.getCurrentDirection());
        System.out.println("Largeur rendu : " + String.format("%.1f", bombermanSprite.getRenderWidth()) + "px");
        System.out.println("Hauteur rendu : " + String.format("%.1f", bombermanSprite.getRenderHeight()) + "px");
        System.out.println("Position X : " + String.format("%.1f", bombermanSprite.getRenderX()) + "px");
        System.out.println("Position Y : " + String.format("%.1f", bombermanSprite.getRenderY()) + "px");
        System.out.println("===========================");
    }
    
    /**
     * Effectue le rendu complet
     */
    private void render() {
        // Nettoyer le canvas
        gc.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Fond noir
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Dessiner une grille pour référence
        drawGrid();
        
        // Dessiner le sprite de Bomberman
        bombermanSprite.render(gc);
        
        // Dessiner un contour autour de la case du sprite pour montrer l'alignement
        drawSpriteCell();
        
        // Afficher la direction actuelle
        displayCurrentDirection();
    }
    
    /**
     * Dessine une grille de référence
     */
    private void drawGrid() {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        
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
     * Dessine un contour rouge autour de la case du sprite
     */
    private void drawSpriteCell() {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        
        // Case 4,4 (position du sprite)
        int cellX = 4 * CELL_SIZE;
        int cellY = 4 * CELL_SIZE;
        
        gc.strokeRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
    }
    
    /**
     * Affiche la direction actuelle à l'écran
     */
    private void displayCurrentDirection() {
        gc.setFill(Color.WHITE);
        gc.fillText("Direction: " + bombermanSprite.getCurrentDirection(), 10, 20);
        gc.fillText("Flèches = Changer | Espace = Infos | Echap = Quitter", 10, WINDOW_HEIGHT - 10);
    }
    
    /**
     * Point d'entrée du test
     */
    public static void main(String[] args) {
        launch(args);
    }
} 