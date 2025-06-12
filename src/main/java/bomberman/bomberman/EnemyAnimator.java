package bomberman.bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

/**
 * Classe responsable de l'animation des ennemis avec leurs sprites de marche.
 * Gère l'animation fluide avec 4 frames par direction en cycle continu.
 * 
 * Pattern d'animation de marche (4 frames cycliques) :
 * Frame 0: Ennemis1_world1_direction_1.png
 * Frame 1: Ennemis1_world1_direction_2.png  
 * Frame 2: Ennemis1_world1_direction_3.png
 * Frame 3: Ennemis1_world1_direction_4.png
 * (puis retour à frame 0)
 * 
 * États supportés : IDLE, WALKING
 */
public class EnemyAnimator {
    
    // Énumération des états d'animation
    public enum AnimationState {
        IDLE,       // Immobile (utilise frame 0)
        WALKING     // En mouvement (animation cyclique)
    }
    
    // Taille logique d'une case dans le jeu
    private static final int CELL_SIZE = 48;
    
    // Durée d'animation pour chaque frame (vitesse plus douce et fluide)
    private static final double FRAME_DURATION_MS = 60.0;  // 120ms par frame (animation fluide et agréable)
    
    // Facteur d'agrandissement des sprites (x3 comme Bomberman)
    private static final double SPRITE_SCALE_FACTOR = 3.0;
    
    // Décalage visuel vers le haut (même que Bomberman)
    private static final int VISUAL_Y_OFFSET = -12; // Décalage de 12 pixels vers le haut
    
    // Images des sprites d'animation selon la direction (4 frames par direction)
    private static Image[] spritesHaut = new Image[4];
    private static Image[] spritesBas = new Image[4];
    private static Image[] spritesGauche = new Image[4];
    private static Image[] spritesDroite = new Image[4];
    
    // État actuel
    private AnimationState currentState;
    private String currentDirection;
    private boolean isWalking;
    private int currentFrame; // 0 à 3 pour les 4 frames d'animation
    
    // Position en pixels pour le rendu
    private double renderX;
    private double renderY;
    
    // Animation - Timeline simple et robuste
    private Timeline walkingAnimation;
    
    // Cache pour éviter les recalculs répétés
    private Image currentSprite;
    private double spriteRenderX;
    private double spriteRenderY;
    private double spriteRenderWidth;
    private double spriteRenderHeight;
    private boolean needsRecalculation;
    
    /**
     * Constructeur qui charge les sprites et initialise l'animation
     */
    public EnemyAnimator() {
        loadAllSprites();
        this.currentState = AnimationState.IDLE;
        setDirection("bas"); // Direction par défaut
        this.isWalking = false;
        this.currentFrame = 0; // Commencer sur la première frame
        this.needsRecalculation = true;
        
        // Initialiser l'animation de marche
        initializeWalkingAnimation();
    }
    
    /**
     * Charge tous les sprites (4 frames par direction) depuis les ressources
     */
    private static void loadAllSprites() {
        if (spritesHaut[0] == null) {
            try {
                // ✨ **NOUVEAU** : Utiliser le SpriteManager pour obtenir les sprites du thème actuel
                SpriteManager spriteManager = SpriteManager.getInstance();
                SpriteManager.ThemeSprites currentSprites = spriteManager.getCurrentSprites();
                
                if (currentSprites != null) {
                    // Charger les sprites ennemis depuis le thème actuel
                    for (int i = 0; i < 4; i++) {
                        spritesHaut[i] = currentSprites.enemyHaut[i];
                        spritesBas[i] = currentSprites.enemyBas[i];
                        spritesGauche[i] = currentSprites.enemyGauche[i];
                        spritesDroite[i] = currentSprites.enemyDroite[i];
                    }
                    
                    System.out.println("Sprites ennemis chargés depuis le thème : " + spriteManager.getCurrentTheme().getDisplayName());
                    System.out.println("- Haut: " + spritesHaut[0].getWidth() + "x" + spritesHaut[0].getHeight() + " (4 frames)");
                    System.out.println("- Bas: " + spritesBas[0].getWidth() + "x" + spritesBas[0].getHeight() + " (4 frames)");
                    System.out.println("- Gauche: " + spritesGauche[0].getWidth() + "x" + spritesGauche[0].getHeight() + " (4 frames)");
                    System.out.println("- Droite: " + spritesDroite[0].getWidth() + "x" + spritesDroite[0].getHeight() + " (4 frames)");
                } else {
                    // Fallback vers les sprites Puropen si le SpriteManager échoue
                    loadAllSpritesFallback();
                }
                
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des sprites d'ennemis depuis le SpriteManager : " + e.getMessage());
                // Fallback vers les sprites Puropen
                loadAllSpritesFallback();
            }
        }
    }
    
    /**
     * Méthode de fallback pour charger les sprites Puropen directement
     */
    private static void loadAllSpritesFallback() {
        try {
                // Sprites pour direction HAUT
                for (int i = 0; i < 4; i++) {
                    spritesHaut[i] = new Image(EnemyAnimator.class.getResourceAsStream("/sprites/ennemis/Puropen_haut_" + (i + 1) + ".png"));
                }
                
                // Sprites pour direction BAS
                for (int i = 0; i < 4; i++) {
                    spritesBas[i] = new Image(EnemyAnimator.class.getResourceAsStream("/sprites/ennemis/Puropen_bas_" + (i + 1) + ".png"));
                }
                
                // Sprites pour direction GAUCHE
                for (int i = 0; i < 4; i++) {
                    spritesGauche[i] = new Image(EnemyAnimator.class.getResourceAsStream("/sprites/ennemis/Puropen_gauche_" + (i + 1) + ".png"));
                }
                
                // Sprites pour direction DROITE
                for (int i = 0; i < 4; i++) {
                    spritesDroite[i] = new Image(EnemyAnimator.class.getResourceAsStream("/sprites/ennemis/Puropen_droite_" + (i + 1) + ".png"));
                }
                
            System.out.println("Sprites ennemis chargés en fallback (Puropen par défaut)");
                System.out.println("- Haut: " + spritesHaut[0].getWidth() + "x" + spritesHaut[0].getHeight() + " (4 frames)");
                System.out.println("- Bas: " + spritesBas[0].getWidth() + "x" + spritesBas[0].getHeight() + " (4 frames)");
                System.out.println("- Gauche: " + spritesGauche[0].getWidth() + "x" + spritesGauche[0].getHeight() + " (4 frames)");
                System.out.println("- Droite: " + spritesDroite[0].getWidth() + "x" + spritesDroite[0].getHeight() + " (4 frames)");
                
        } catch (Exception fallbackException) {
            System.err.println("Erreur critique lors du chargement des sprites ennemis en fallback : " + fallbackException.getMessage());
            fallbackException.printStackTrace();
            }
        }
    
    /**
     * Recharge les sprites depuis le SpriteManager (appelé lors du changement de thème)
     */
    public static void reloadSprites() {
        // Réinitialiser les sprites pour forcer le rechargement
        spritesHaut = new Image[4];
        spritesBas = new Image[4];
        spritesGauche = new Image[4];
        spritesDroite = new Image[4];
        
        // Recharger tous les sprites
        loadAllSprites();
        System.out.println("Sprites ennemis rechargés pour le nouveau thème");
    }
    
    /**
     * Initialise l'animation de marche avec Timeline simple et robuste
     */
    private void initializeWalkingAnimation() {
        // Créer une Timeline simple qui s'exécute indéfiniment
        KeyFrame animationFrame = new KeyFrame(
            Duration.millis(FRAME_DURATION_MS),
            e -> {
                // Passer à la frame suivante (cycle 0->1->2->3->0)
                currentFrame = (currentFrame + 1) % 4;
                updateCurrentSprite();
            }
        );
        
        walkingAnimation = new Timeline(animationFrame);
        walkingAnimation.setCycleCount(Timeline.INDEFINITE);
    }
    
    /**
     * Met à jour le sprite actuel selon la direction et la frame
     */
    private void updateCurrentSprite() {
        Image[] currentDirectionSprites;
        
        switch (currentDirection) {
            case "haut":
                currentDirectionSprites = spritesHaut;
                break;
            case "bas":
                currentDirectionSprites = spritesBas;
                break;
            case "gauche":
                currentDirectionSprites = spritesGauche;
                break;
            case "droite":
                currentDirectionSprites = spritesDroite;
                break;
            default:
                currentDirectionSprites = spritesBas; // Direction par défaut
                break;
        }
        
        this.currentSprite = currentDirectionSprites[currentFrame];
        this.needsRecalculation = true;
    }
    
    /**
     * Change la direction d'animation
     * @param direction "haut", "bas", "gauche", ou "droite"
     */
    public void setDirection(String direction) {
        if (!direction.equals(this.currentDirection)) {
            this.currentDirection = direction;
            updateCurrentSprite();
        }
    }
    
    /**
     * Démarre l'animation de marche
     */
    public void startWalking() {
        if (!isWalking) {
            this.isWalking = true;
            this.currentState = AnimationState.WALKING;
            updateCurrentSprite();
            
            if (walkingAnimation != null) {
                walkingAnimation.play();
            }
        }
    }
    
    /**
     * Arrête l'animation de marche et revient à l'état idle
     */
    public void stopWalking() {
        if (isWalking) {
            this.isWalking = false;
            this.currentState = AnimationState.IDLE;
            this.currentFrame = 0; // Revenir à la première frame (idle)
            
            if (walkingAnimation != null) {
                walkingAnimation.stop();
            }
            
            updateCurrentSprite();
        }
    }
    
    /**
     * Définit la position en coordonnées de grille avec offset en pixels
     * @param cellX Position en colonne
     * @param cellY Position en ligne  
     * @param offsetX Décalage horizontal en pixels
     * @param offsetY Décalage vertical en pixels
     */
    public void setPosition(int cellX, int cellY, double offsetX, double offsetY) {
        this.renderX = cellX * CELL_SIZE + offsetX;
        this.renderY = cellY * CELL_SIZE + offsetY;
        this.needsRecalculation = true;
    }
    
    /**
     * Définit la position en coordonnées pixel directement
     * @param pixelX Position X en pixels
     * @param pixelY Position Y en pixels
     * @param offsetX Décalage horizontal supplémentaire en pixels
     * @param offsetY Décalage vertical supplémentaire en pixels
     */
    public void setPixelPosition(double pixelX, double pixelY, double offsetX, double offsetY) {
        this.renderX = pixelX + offsetX;
        this.renderY = pixelY + offsetY;
        this.needsRecalculation = true;
    }
    
    /**
     * Calcule les paramètres de rendu si nécessaire
     * Utilise un facteur d'agrandissement x3 comme Bomberman
     */
    private void calculateRenderParameters() {
        if (!needsRecalculation || currentSprite == null) {
            return;
        }
        
        // Taille du sprite original
        double originalWidth = currentSprite.getWidth();
        double originalHeight = currentSprite.getHeight();
        
        // Appliquer le facteur d'agrandissement x3 (comme Bomberman)
        this.spriteRenderWidth = originalWidth * SPRITE_SCALE_FACTOR;
        this.spriteRenderHeight = originalHeight * SPRITE_SCALE_FACTOR;
        
        // Centrer le sprite dans sa position avec décalage visuel
        this.spriteRenderX = renderX - (spriteRenderWidth / 2.0);
        this.spriteRenderY = renderY - (spriteRenderHeight / 2.0) + VISUAL_Y_OFFSET;
        
        this.needsRecalculation = false;
    }
    
    /**
     * Dessine l'ennemi sur le canvas
     * @param gc Le contexte graphique
     */
    public void render(GraphicsContext gc) {
        renderWithEffects(gc, false, 1.0);
    }
    
    /**
     * Dessine l'ennemi avec effets visuels
     * @param gc Le contexte graphique
     * @param isInvincible Si l'ennemi est invincible (effet de clignotement)
     * @param alpha Transparence (0.0 à 1.0)
     */
    public void renderWithEffects(GraphicsContext gc, boolean isInvincible, double alpha) {
        if (currentSprite == null) {
            updateCurrentSprite();
            if (currentSprite == null) {
                return; // Pas de sprite à afficher
            }
        }
        
        calculateRenderParameters();
        
        // Sauvegarder l'état du contexte graphique
        gc.save();
        
        try {
            // Appliquer l'effet d'invincibilité (clignotement)
            if (isInvincible) {
                long currentTime = System.currentTimeMillis();
                boolean shouldBlink = (currentTime / 200) % 2 == 0; // Clignotement toutes les 200ms
                
                if (shouldBlink) {
                    alpha *= 0.5; // Réduire l'opacité quand il clignote
                }
            }
            
            // Appliquer la transparence
            gc.setGlobalAlpha(alpha);
            
            // Dessiner le sprite
            gc.drawImage(currentSprite, 
                        spriteRenderX, 
                        spriteRenderY, 
                        spriteRenderWidth, 
                        spriteRenderHeight);
            
        } finally {
            // Restaurer l'état du contexte graphique
            gc.restore();
        }
    }
    
    /**
     * @return La direction actuelle
     */
    public String getCurrentDirection() {
        return currentDirection;
    }
    
    /**
     * @return true si l'ennemi est en train de marcher
     */
    public boolean isWalking() {
        return isWalking;
    }
    
    /**
     * @return La frame actuelle (0-3)
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
    
    /**
     * @return La largeur de rendu
     */
    public double getRenderWidth() {
        calculateRenderParameters();
        return spriteRenderWidth;
    }
    
    /**
     * @return La hauteur de rendu
     */
    public double getRenderHeight() {
        calculateRenderParameters();
        return spriteRenderHeight;
    }
    
    /**
     * @return La position X de rendu
     */
    public double getRenderX() {
        calculateRenderParameters();
        return spriteRenderX;
    }
    
    /**
     * @return La position Y de rendu
     */
    public double getRenderY() {
        calculateRenderParameters();
        return spriteRenderY;
    }
    
    /**
     * Invalide le cache et force un recalcul
     */
    public void invalidateCache() {
        this.needsRecalculation = true;
    }
    
    /**
     * Nettoie les ressources d'animation
     */
    public void dispose() {
        if (walkingAnimation != null) {
            walkingAnimation.stop();
            walkingAnimation = null;
        }
    }
}