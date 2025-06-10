package bomberman.bomberman;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.image.Image;

/**
 * Gestionnaire d'animation pour le bonus EXTRA_BOMB
 * Fait une boucle très rapide entre bonus_extra_bomb_1.png et bonus_extra_bomb_2.png
 */
public class ExtraBombAnimator {
    
    // Images du bonus (2 frames d'animation)
    private static Image[] extraBombSprites = new Image[2];
    private static boolean spritesLoaded = false;
    
    // État de l'animation
    private int currentFrame = 0;
    private Timeline animation;
    
    // Durée d'un frame (50ms pour une animation très rapide)
    private static final double FRAME_DURATION_MS = 50.0;
    
    /**
     * Constructeur - Charge les sprites et démarre l'animation
     */
    public ExtraBombAnimator() {
        loadSprites();
        startAnimation();
    }
    
    /**
     * Charge les sprites du bonus depuis les ressources
     */
    private static void loadSprites() {
        if (spritesLoaded) return;
        
        try {
            // Charger les 2 images du bonus
            extraBombSprites[0] = new Image(ExtraBombAnimator.class.getResourceAsStream("/sprites/bonus_extra_bomb_1.png"));
            extraBombSprites[1] = new Image(ExtraBombAnimator.class.getResourceAsStream("/sprites/bonus_extra_bomb_2.png"));
            
            spritesLoaded = true;
            System.out.println("✅ Sprites EXTRA_BOMB chargés : bonus_extra_bomb_1.png, bonus_extra_bomb_2.png");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites EXTRA_BOMB : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Démarre l'animation cyclique du bonus
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
     * Retourne le sprite actuel du bonus
     * @return L'image du frame actuel
     */
    public Image getCurrentSprite() {
        if (!spritesLoaded || extraBombSprites[currentFrame] == null) {
            return null;
        }
        return extraBombSprites[currentFrame];
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
        return spritesLoaded && extraBombSprites[0] != null && extraBombSprites[1] != null;
    }
} 