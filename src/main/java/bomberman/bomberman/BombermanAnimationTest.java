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
 * Classe de test pour démontrer l'animation de marche de Bomberman
 * avec des contrôles interactifs et des informations d'état.
 * 
 * Utilisation :
 * - Flèches directionnelles pour changer la direction et animer
 * - Espace pour basculer l'animation manuellement
 * - TAB pour afficher les informations détaillées
 * - Echap pour quitter
 */
public class BombermanAnimationTest extends Application {
    
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;
    private static final int BOMBERMAN_X = 225; // Centré horizontalement
    private static final int BOMBERMAN_Y = 150; // Centré verticalement
    
    private BombermanAnimator animator;
    private boolean showInfo;
    private boolean manualAnimationControl;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialiser l'animateur de Bomberman
        animator = new BombermanAnimator();
        animator.setPosition(0, 0, BOMBERMAN_X, BOMBERMAN_Y);
        
        this.showInfo = false;
        this.manualAnimationControl = false;
        
        // Créer le canvas
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Configuration de la scène
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode(), canvas.getGraphicsContext2D());
        });
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Test Animation Bomberman - Direction et Marche");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Donner le focus à la scène pour capturer les événements clavier
        scene.getRoot().requestFocus();
        
        // Rendu initial
        render(canvas.getGraphicsContext2D());
        
        System.out.println("=== TEST ANIMATION BOMBERMAN ===");
        System.out.println("Utilisez les flèches pour changer direction et déclencher l'animation");
        System.out.println("Espace : basculer animation manuelle");
        System.out.println("TAB : afficher/masquer informations");
        System.out.println("Echap : quitter");
    }
    
    /**
     * Gère les événements clavier
     */
    private void handleKeyPressed(KeyCode keyCode, GraphicsContext gc) {
        boolean needsRedraw = false;
        
        switch (keyCode) {
            case UP:
                animator.setDirection("haut");
                if (!manualAnimationControl) {
                    startTemporaryWalk();
                }
                needsRedraw = true;
                System.out.println("Direction changée : HAUT");
                break;
                
            case DOWN:
                animator.setDirection("bas");
                if (!manualAnimationControl) {
                    startTemporaryWalk();
                }
                needsRedraw = true;
                System.out.println("Direction changée : BAS");
                break;
                
            case LEFT:
                animator.setDirection("gauche");
                if (!manualAnimationControl) {
                    startTemporaryWalk();
                }
                needsRedraw = true;
                System.out.println("Direction changée : GAUCHE");
                break;
                
            case RIGHT:
                animator.setDirection("droite");
                if (!manualAnimationControl) {
                    startTemporaryWalk();
                }
                needsRedraw = true;
                System.out.println("Direction changée : DROITE");
                break;
                
            case SPACE:
                // Basculer l'animation manuellement
                if (animator.isWalking()) {
                    animator.stopWalking();
                    System.out.println("Animation arrêtée manuellement");
                } else {
                    animator.startWalking();
                    System.out.println("Animation démarrée manuellement");
                }
                manualAnimationControl = true;
                needsRedraw = true;
                break;
                
            case TAB:
                // Basculer l'affichage des informations
                showInfo = !showInfo;
                needsRedraw = true;
                System.out.println("Affichage infos : " + (showInfo ? "ON" : "OFF"));
                break;
                
            case ESCAPE:
                // Quitter l'application
                System.out.println("Fermeture du test...");
                System.exit(0);
                break;
                
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Redessiner si nécessaire
        if (needsRedraw) {
            render(gc);
        }
    }
    
    /**
     * Démarre une animation temporaire de marche (simulate mouvement)
     */
    private void startTemporaryWalk() {
        if (manualAnimationControl) {
            return; // Ne pas interférer avec le contrôle manuel
        }
        
        animator.startWalking();
        
        // Arrêter l'animation après 600ms (simulate un mouvement court)
        new Thread(() -> {
            try {
                Thread.sleep(600);
                if (!manualAnimationControl) {
                    animator.stopWalking();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Effectue le rendu complet de la scène de test
     */
    private void render(GraphicsContext gc) {
        // Nettoyer le canvas
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Dessiner une grille de référence
        drawGrid(gc);
        
        // Dessiner le cadre de la case logique 48x48
        drawLogicalCellFrame(gc);
        
        // Dessiner Bomberman
        animator.render(gc);
        
        // Afficher les informations si activé
        if (showInfo) {
            drawInformation(gc);
        } else {
            drawBasicControls(gc);
        }
    }
    
    /**
     * Dessine une grille de référence
     */
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTBLUE);
        gc.setLineWidth(0.5);
        
        // Lignes verticales
        for (int x = 48; x < WINDOW_WIDTH; x += 48) {
            gc.strokeLine(x, 0, x, WINDOW_HEIGHT);
        }
        
        // Lignes horizontales
        for (int y = 48; y < WINDOW_HEIGHT; y += 48) {
            gc.strokeLine(0, y, WINDOW_WIDTH, y);
        }
    }
    
    /**
     * Dessine le cadre de la case logique 48x48 autour de Bomberman
     */
    private void drawLogicalCellFrame(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2.0);
        gc.strokeRect(BOMBERMAN_X, BOMBERMAN_Y, 48, 48);
        
        // Ajouter un label
        gc.setFill(Color.BLUE);
        gc.fillText("Case 48x48", BOMBERMAN_X, BOMBERMAN_Y - 5);
    }
    
    /**
     * Affiche les contrôles de base
     */
    private void drawBasicControls(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillText("Flèches: Direction + Animation", 10, 25);
        gc.fillText("Espace: Animation manuelle", 10, 45);
        gc.fillText("TAB: Informations détaillées", 10, 65);
        gc.fillText("Echap: Quitter", 10, 85);
    }
    
    /**
     * Affiche les informations détaillées sur l'état de l'animation
     */
    private void drawInformation(GraphicsContext gc) {
        // Fond semi-transparent pour les informations
        gc.setFill(Color.color(0, 0, 0, 0.8));
        gc.fillRect(10, 10, 300, 200);
        
        // Informations de l'état
        gc.setFill(Color.WHITE);
        int y = 30;
        int lineHeight = 18;
        
        gc.fillText("=== ÉTAT ANIMATION BOMBERMAN ===", 20, y);
        y += lineHeight * 1.5;
        
        gc.fillText("Direction actuelle: " + animator.getCurrentDirection(), 20, y);
        y += lineHeight;
        
        gc.fillText("En marche: " + (animator.isWalking() ? "OUI" : "NON"), 20, y);
        y += lineHeight;
        
        gc.fillText("Frame actuelle: " + animator.getCurrentFrame(), 20, y);
        y += lineHeight;
        
        gc.fillText("Contrôle manuel: " + (manualAnimationControl ? "OUI" : "NON"), 20, y);
        y += lineHeight * 1.5;
        
        gc.fillText("Dimensions de rendu:", 20, y);
        y += lineHeight;
        
        gc.fillText("  Largeur: " + String.format("%.1f", animator.getRenderWidth()) + "px", 20, y);
        y += lineHeight;
        
        gc.fillText("  Hauteur: " + String.format("%.1f", animator.getRenderHeight()) + "px", 20, y);
        y += lineHeight;
        
        gc.fillText("Position de rendu:", 20, y);
        y += lineHeight;
        
        gc.fillText("  X: " + String.format("%.1f", animator.getRenderX()) + "px", 20, y);
        y += lineHeight;
        
        gc.fillText("  Y: " + String.format("%.1f", animator.getRenderY()) + "px", 20, y);
        
        // Instructions en bas
        gc.setFill(Color.YELLOW);
        gc.fillText("TAB: Masquer ces informations", 20, WINDOW_HEIGHT - 20);
    }
    
    @Override
    public void stop() {
        // Libérer les ressources de l'animateur
        if (animator != null) {
            animator.dispose();
        }
        System.out.println("Ressources libérées - Test terminé");
    }
    
    /**
     * Point d'entrée du programme de test
     */
    public static void main(String[] args) {
        launch(args);
    }
} 