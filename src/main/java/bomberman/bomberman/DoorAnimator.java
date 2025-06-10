package bomberman.bomberman;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.image.Image;

/**
 * Gestionnaire d'animation pour la porte de sortie
 * Fait une boucle rapide entre porte_1.png et porte_2.png
 */
public class DoorAnimator {
    
    // Images de la porte (2 frames d'animation)
    private static Image[] doorSprites = new Image[2];
    private static boolean spritesLoaded = false;
    
    // État de l'animation
    private int currentFrame = 0;
    private Timeline animation;
    
    // Durée d'un frame (80ms pour une animation très rapide)
    private static final double FRAME_DURATION_MS = 80.0;
    
    /**
     * Constructeur - Charge les sprites et démarre l'animation
     */
    public DoorAnimator() {
        loadSprites();
        startAnimation();
    }
    
    /**
     * Charge les sprites de la porte depuis les ressources
     */
    private static void loadSprites() {
        if (spritesLoaded) return;
        
        try {
            // Charger les 2 images de la porte
            doorSprites[0] = new Image(DoorAnimator.class.getResourceAsStream("/sprites/porte_1.png"));
            doorSprites[1] = new Image(DoorAnimator.class.getResourceAsStream("/sprites/porte_2.png"));
            
            spritesLoaded = true;
            System.out.println("✅ Sprites de porte chargés : porte_1.png, porte_2.png");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites de porte : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Démarre l'animation cyclique de la porte
     */
    private void startAnimation() {
        if (animation != null) {
            animation.stop();
        }
        
        animation = new Timeline(
            new KeyFrame(Duration.millis(FRAME_DURATION_MS), e -> nextFrame())
        );
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }
    
    /**
     * Passe au frame suivant (0 → 1 → 0 → 1...)
     */
    private void nextFrame() {
        currentFrame = (currentFrame + 1) % 2; // Boucle entre 0 et 1
    }
    
    /**
     * Retourne le sprite actuel de la porte
     * @return L'image du frame actuel
     */
    public Image getCurrentSprite() {
        if (!spritesLoaded || doorSprites[currentFrame] == null) {
            return null;
        }
        return doorSprites[currentFrame];
    }
    
    /**
     * Arrête l'animation et libère les ressources
     */
    public void dispose() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }
    
    /**
     * Vérifie si les sprites sont correctement chargés
     * @return true si tous les sprites sont chargés
     */
    public boolean isReady() {
        return spritesLoaded && doorSprites[0] != null && doorSprites[1] != null;
    }
} 