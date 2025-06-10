package bomberman.bomberman;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.image.Image;

/**
 * Gestionnaire d'animation pour le bonus EXPLOSION_EXPANDER
 * Fait une boucle très rapide entre bonus_explosion_expander_1.png et bonus_explosion_expander_2.png
 */
public class ExplosionExpanderAnimator {
    
    // Images du bonus (2 frames d'animation)
    private static Image[] expanderSprites = new Image[2];
    private static boolean spritesLoaded = false;
    
    // État de l'animation
    private int currentFrame = 0;
    private Timeline animation;
    
    // Durée d'un frame (50ms pour une animation très rapide)
    private static final double FRAME_DURATION_MS = 50.0;
    
    /**
     * Constructeur - Charge les sprites et démarre l'animation
     */
    public ExplosionExpanderAnimator() {
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
            expanderSprites[0] = new Image(ExplosionExpanderAnimator.class.getResourceAsStream("/sprites/bonus_explosion_expander_1.png"));
            expanderSprites[1] = new Image(ExplosionExpanderAnimator.class.getResourceAsStream("/sprites/bonus_explosion_expander_2.png"));
            
            spritesLoaded = true;
            System.out.println("✅ Sprites EXPLOSION_EXPANDER chargés : bonus_explosion_expander_1.png, bonus_explosion_expander_2.png");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites EXPLOSION_EXPANDER : " + e.getMessage());
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
        if (!spritesLoaded || expanderSprites[currentFrame] == null) {
            return null;
        }
        return expanderSprites[currentFrame];
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
        return spritesLoaded && expanderSprites[0] != null && expanderSprites[1] != null;
    }
} 