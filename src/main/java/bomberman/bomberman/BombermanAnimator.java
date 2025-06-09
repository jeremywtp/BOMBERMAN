package bomberman.bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

/**
 * Classe responsable de l'animation du personnage Bomberman avec ses sprites de marche et de mort.
 * Gère l'animation fluide avec 5 frames par direction en alternant sprites fixes et marche,
 * plus l'animation de mort avec 8 frames non-bouclante.
 * 
 * Pattern d'animation de marche (5 frames) :
 * Frame 0: bomberman_fixe_direction.png
 * Frame 1: bomberman_marche_direction1.png  
 * Frame 2: bomberman_fixe_direction.png
 * Frame 3: bomberman_marche_direction2.png
 * Frame 4: bomberman_fixe_direction.png
 * 
 * Animation de mort (8 frames non-bouclante) :
 * Frames 0-7: Bomberman_dies_1.png à Bomberman_dies_8.png
 * 
 * États supportés : ALIVE_IDLE, ALIVE_WALKING, DYING
 */
public class BombermanAnimator {
    
    // Énumération des états d'animation
    public enum AnimationState {
        ALIVE_IDLE,     // Vivant et immobile (sprite fixe)
        ALIVE_WALKING,  // Vivant et en mouvement (animation de marche)
        DYING          // En train de mourir (animation de mort)
    }
    
    // Taille logique d'une case dans le jeu
    private static final int CELL_SIZE = 48;
    
    // Durées d'animation différenciées pour plus de naturel
    private static final double MARCHE_DURATION_MS = 150.0;  // Durée pour sprites de marche
    private static final double FIXE_DURATION_MS = 80.0;     // Durée pour sprites fixes (plus court)
    private static final double DEATH_DURATION_MS = 200.0;   // Durée pour chaque frame de mort
    
    // Images des sprites fixes selon la direction (état immobile)
    private static Image spriteFixeHaut;
    private static Image spriteFixeBas;
    private static Image spriteFixeGauche;
    private static Image spriteFixeDroite;
    
    // Images des sprites d'animation de marche (2 frames par direction)
    private static Image spriteMarcheHaut1, spriteMarcheHaut2;
    private static Image spriteMarcheBas1, spriteMarcheBas2;
    private static Image spriteMarcheGauche1, spriteMarcheGauche2;
    private static Image spriteMarcheDroite1, spriteMarcheDroite2;
    
    // Images des sprites d'animation de mort (8 frames)
    private static Image[] spritesDeath = new Image[8];
    
    // État actuel
    private AnimationState currentState;
    private String currentDirection;
    private boolean isWalking;
    private int currentFrame; // 0 à 4 pour marche, 0 à 7 pour mort
    
    // Position en pixels pour le rendu
    private double renderX;
    private double renderY;
    
    // Animation
    private Timeline walkingAnimation;
    private Timeline deathAnimation;
    
    // Cache pour éviter les recalculs répétés
    private Image currentSprite;
    private double spriteRenderX;
    private double spriteRenderY;
    private double spriteRenderWidth;
    private double spriteRenderHeight;
    private boolean needsRecalculation;
    
    // Callback pour la fin de l'animation de mort
    private Runnable onDeathAnimationComplete;
    
    /**
     * Constructeur qui charge les sprites et initialise l'animation
     */
    public BombermanAnimator() {
        loadAllSprites();
        this.currentState = AnimationState.ALIVE_IDLE;
        setDirection("bas"); // Direction par défaut
        this.isWalking = false;
        this.currentFrame = 0; // Commencer sur le sprite fixe
        this.needsRecalculation = true;
        
        // Initialiser l'animation de marche
        initializeWalkingAnimation();
    }
    
    /**
     * Charge tous les sprites (fixes et animés) depuis les ressources
     */
    private static void loadAllSprites() {
        if (spriteFixeHaut == null) {
            try {
                // Sprites fixes (état immobile)
                spriteFixeHaut = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_fixe_haut.png"));
                spriteFixeBas = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_fixe_bas.png"));
                spriteFixeGauche = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_fixe_gauche.png"));
                spriteFixeDroite = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_fixe_droite.png"));
                
                // Sprites d'animation de marche - Haut
                spriteMarcheHaut1 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_haut1.png"));
                spriteMarcheHaut2 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_haut2.png"));
                
                // Sprites d'animation de marche - Bas
                spriteMarcheBas1 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_bas1.png"));
                spriteMarcheBas2 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_bas2.png"));
                
                // Sprites d'animation de marche - Gauche
                spriteMarcheGauche1 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_gauche1.png"));
                spriteMarcheGauche2 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_gauche2.png"));
                
                // Sprites d'animation de marche - Droite
                spriteMarcheDroite1 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_droite1.png"));
                spriteMarcheDroite2 = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/bomberman_marche_droite2.png"));
                
                // Sprites de mort
                for (int i = 0; i < 8; i++) {
                    spritesDeath[i] = new Image(BombermanAnimator.class.getResourceAsStream("/sprites/perso/Bomberman_dies_" + (i + 1) + ".png"));
                }
                
                System.out.println("Sprites Bomberman (fixes + animation) chargés avec succès :");
                System.out.println("- Fixe Haut: " + spriteFixeHaut.getWidth() + "x" + spriteFixeHaut.getHeight());
                System.out.println("- Fixe Bas: " + spriteFixeBas.getWidth() + "x" + spriteFixeBas.getHeight());
                System.out.println("- Marche Bas1: " + spriteMarcheBas1.getWidth() + "x" + spriteMarcheBas1.getHeight());
                System.out.println("- Marche Bas2: " + spriteMarcheBas2.getWidth() + "x" + spriteMarcheBas2.getHeight());
                System.out.println("Animation chargée pour toutes les directions (2 frames chacune)");
                
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des sprites Bomberman : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Initialise l'animation de marche avec Timeline à durée variable
     */
    private void initializeWalkingAnimation() {
        // On démarre avec la première frame qui est un sprite fixe
        scheduleNextFrame();
    }
    
    /**
     * Programme la prochaine frame avec la durée appropriée (seulement pour l'animation de marche)
     */
    private void scheduleNextFrame() {
        if (walkingAnimation != null) {
            walkingAnimation.stop();
        }
        
        // Ne programmer la prochaine frame que si on est en état de marche
        if (currentState != AnimationState.ALIVE_WALKING) {
            return;
        }
        
        // Déterminer la durée selon le type de frame actuel
        double duration = isFixeFrame(currentFrame) ? FIXE_DURATION_MS : MARCHE_DURATION_MS;
        
        walkingAnimation = new Timeline(
            new KeyFrame(Duration.millis(duration), e -> {
                // Passer à la frame suivante seulement si on est toujours en marche
                if (currentState == AnimationState.ALIVE_WALKING) {
                    currentFrame = (currentFrame + 1) % 5;
                    updateCurrentSprite();
                    needsRecalculation = true;
                    
                    // Programmer la prochaine frame si on est toujours en train de marcher
                    if (isWalking && currentState == AnimationState.ALIVE_WALKING) {
                        scheduleNextFrame();
                    }
                }
            })
        );
        
        if (isWalking && currentState == AnimationState.ALIVE_WALKING) {
            walkingAnimation.play();
        }
    }
    
    /**
     * Vérifie si une frame donnée correspond à un sprite fixe
     * @param frame Index de la frame (0-4)
     * @return true si c'est un sprite fixe
     */
    private boolean isFixeFrame(int frame) {
        return frame == 0 || frame == 2 || frame == 4;
    }
    
    /**
     * Met à jour le sprite actuel selon l'état, la direction et la frame
     */
    private void updateCurrentSprite() {
        switch (currentState) {
            case ALIVE_IDLE:
                // Utiliser le sprite fixe selon la direction
                switch (currentDirection.toLowerCase()) {
                    case "haut":
                        this.currentSprite = spriteFixeHaut;
                        break;
                    case "bas":
                        this.currentSprite = spriteFixeBas;
                        break;
                    case "gauche":
                        this.currentSprite = spriteFixeGauche;
                        break;
                    case "droite":
                        this.currentSprite = spriteFixeDroite;
                        break;
                    default:
                        this.currentSprite = spriteFixeBas;
                        break;
                }
                break;
                
            case ALIVE_WALKING:
                // Utiliser le pattern avec sprites fixes intercalés
                switch (currentDirection.toLowerCase()) {
                    case "haut":
                        this.currentSprite = getSpriteForFrame(
                            spriteFixeHaut, spriteMarcheHaut1, spriteFixeHaut, spriteMarcheHaut2, spriteFixeHaut
                        );
                        break;
                    case "bas":
                        this.currentSprite = getSpriteForFrame(
                            spriteFixeBas, spriteMarcheBas1, spriteFixeBas, spriteMarcheBas2, spriteFixeBas
                        );
                        break;
                    case "gauche":
                        this.currentSprite = getSpriteForFrame(
                            spriteFixeGauche, spriteMarcheGauche1, spriteFixeGauche, spriteMarcheGauche2, spriteFixeGauche
                        );
                        break;
                    case "droite":
                        this.currentSprite = getSpriteForFrame(
                            spriteFixeDroite, spriteMarcheDroite1, spriteFixeDroite, spriteMarcheDroite2, spriteFixeDroite
                        );
                        break;
                    default:
                        this.currentSprite = spriteFixeBas;
                        break;
                }
                break;
                
            case DYING:
                // Utiliser les sprites de mort (frames 0-7)
                if (currentFrame >= 0 && currentFrame < spritesDeath.length) {
                    this.currentSprite = spritesDeath[currentFrame];
                } else {
                    // Si on dépasse, rester sur la dernière frame
                    this.currentSprite = spritesDeath[spritesDeath.length - 1];
                }
                break;
        }
    }
    
    /**
     * Sélectionne le sprite approprié selon la frame courante (0-4)
     * @param frame0 Sprite pour frame 0 (fixe)
     * @param frame1 Sprite pour frame 1 (marche1)
     * @param frame2 Sprite pour frame 2 (fixe)
     * @param frame3 Sprite pour frame 3 (marche2)
     * @param frame4 Sprite pour frame 4 (fixe)
     * @return Le sprite correspondant à currentFrame
     */
    private Image getSpriteForFrame(Image frame0, Image frame1, Image frame2, Image frame3, Image frame4) {
        switch (currentFrame) {
            case 0: return frame0; // fixe
            case 1: return frame1; // marche1
            case 2: return frame2; // fixe
            case 3: return frame3; // marche2
            case 4: return frame4; // fixe
            default: return frame0; // fallback
        }
    }
    
    /**
     * Définit la direction actuelle de Bomberman
     * @param direction Direction ("haut", "bas", "gauche", "droite")
     */
    public void setDirection(String direction) {
        if (!direction.equals(this.currentDirection)) {
            this.currentDirection = direction;
            this.currentFrame = 0; // Remettre à la première frame
            updateCurrentSprite();
            this.needsRecalculation = true;
        }
    }
    
    /**
     * Démarre l'animation de marche
     */
    public void startWalking() {
        if (currentState == AnimationState.ALIVE_IDLE) {
            this.currentState = AnimationState.ALIVE_WALKING;
            this.isWalking = true;
            this.currentFrame = 0; // Commencer par le sprite fixe
            updateCurrentSprite();
            scheduleNextFrame(); // Démarrer le cycle avec durées variables
            this.needsRecalculation = true;
        }
    }
    
    /**
     * Arrête l'animation de marche et revient au sprite fixe
     */
    public void stopWalking() {
        if (currentState == AnimationState.ALIVE_WALKING) {
            this.currentState = AnimationState.ALIVE_IDLE;
            this.isWalking = false;
            this.currentFrame = 0; // Revenir au sprite fixe
            if (walkingAnimation != null) {
                walkingAnimation.stop();
            }
            updateCurrentSprite(); // Revenir au sprite fixe
            this.needsRecalculation = true;
        }
    }
    
    /**
     * Met à jour la position de rendu du sprite (coordonnées de la case)
     * @param cellX Position logique en colonne (coordonnées de grille)
     * @param cellY Position logique en ligne (coordonnées de grille)
     * @param offsetX Décalage horizontal en pixels (pour centrage dans la fenêtre)
     * @param offsetY Décalage vertical en pixels (pour l'interface)
     */
    public void setPosition(int cellX, int cellY, double offsetX, double offsetY) {
        double newRenderX = cellX * CELL_SIZE + offsetX;
        double newRenderY = cellY * CELL_SIZE + offsetY;
        
        if (this.renderX != newRenderX || this.renderY != newRenderY) {
            this.renderX = newRenderX;
            this.renderY = newRenderY;
            this.needsRecalculation = true;
        }
    }
    
    /**
     * Met à jour la position de rendu du sprite (coordonnées en pixels flottantes)
     * @param pixelX Position en pixels (coordonnées flottantes)
     * @param pixelY Position en pixels (coordonnées flottantes)
     * @param offsetX Décalage horizontal en pixels (pour centrage dans la fenêtre)
     * @param offsetY Décalage vertical en pixels (pour l'interface)
     */
    public void setPixelPosition(double pixelX, double pixelY, double offsetX, double offsetY) {
        // Pour le mouvement fluide, la position est déjà en pixels, on ajoute juste les décalages
        double newRenderX = pixelX - (CELL_SIZE / 2.0) + offsetX; // Centrer le sprite
        double newRenderY = pixelY - (CELL_SIZE / 2.0) + offsetY;
        
        if (this.renderX != newRenderX || this.renderY != newRenderY) {
            this.renderX = newRenderX;
            this.renderY = newRenderY;
            this.needsRecalculation = true;
        }
    }
    
    /**
     * Calcule les paramètres de rendu pour afficher le sprite plus grand que sa case
     * sans déformation, en conservant les proportions originales
     * ✨ Sprite agrandi pour un rendu plus fidèle au vrai jeu Bomberman
     */
    private void calculateRenderParameters() {
        if (currentSprite == null) {
            return;
        }
        
        double originalWidth = currentSprite.getWidth();
        double originalHeight = currentSprite.getHeight();
        
        // Calculer le facteur d'échelle de base pour que le sprite rentre dans une case 48x48
        double scaleX = CELL_SIZE / originalWidth;
        double scaleY = CELL_SIZE / originalHeight;
        double baseScale = Math.min(scaleX, scaleY); // Prendre le plus petit pour éviter le débordement
        
        // ✨ **AGRANDISSEMENT AUTHENTIQUE** : Multiplier par 1.5 pour un sprite plus imposant
        // comme dans le vrai jeu Bomberman où l'ombre au sol fait la taille d'une case
        double scale = baseScale * 1.5;
        
        // Dimensions finales du sprite à l'écran (maintenant plus grandes que 48x48)
        this.spriteRenderWidth = originalWidth * scale;
        this.spriteRenderHeight = originalHeight * scale;
        
        // Position pour centrer le sprite agrandi dans la case 48x48
        // Le sprite peut dépasser de la case, ce qui est authentique
        this.spriteRenderX = renderX + (CELL_SIZE - spriteRenderWidth) / 2.0;
        this.spriteRenderY = renderY + (CELL_SIZE - spriteRenderHeight) / 2.0;
        
        this.needsRecalculation = false;
    }
    
    /**
     * Dessine le sprite de Bomberman sur le contexte graphique donné
     * @param gc Contexte graphique JavaFX
     */
    public void render(GraphicsContext gc) {
        if (currentSprite == null) {
            System.err.println("Aucun sprite chargé pour la direction : " + currentDirection);
            return;
        }
        
        // Recalculer les paramètres si nécessaire
        if (needsRecalculation) {
            calculateRenderParameters();
        }
        
        // Sauvegarder l'état du contexte graphique
        gc.save();
        
        // Désactiver le lissage pour un rendu pixel perfect
        gc.setImageSmoothing(false);
        
        // Dessiner le sprite centré dans la case
        gc.drawImage(
            currentSprite,
            spriteRenderX,
            spriteRenderY,
            spriteRenderWidth,
            spriteRenderHeight
        );
        
        // Restaurer l'état du contexte graphique
        gc.restore();
    }
    
    /**
     * Dessine le sprite avec des effets visuels additionnels
     * @param gc Contexte graphique JavaFX
     * @param isInvincible True si le joueur est invincible (effet clignotant)
     * @param alpha Transparence du sprite (0.0 à 1.0)
     */
    public void renderWithEffects(GraphicsContext gc, boolean isInvincible, double alpha) {
        if (currentSprite == null) {
            return;
        }
        
        // Effet de clignotement pour l'invincibilité
        if (isInvincible) {
            long currentTime = System.currentTimeMillis();
            boolean shouldRender = (currentTime / 33) % 2 == 0; // Clignotement ultra rapide
            if (!shouldRender) {
                return; // Ne pas dessiner (effet de clignotement)
            }
        }
        
        // Recalculer les paramètres si nécessaire
        if (needsRecalculation) {
            calculateRenderParameters();
        }
        
        // Sauvegarder l'état du contexte graphique
        gc.save();
        
        // Désactiver le lissage pour un rendu pixel perfect
        gc.setImageSmoothing(false);
        
        // Appliquer la transparence si nécessaire
        if (alpha < 1.0) {
            gc.setGlobalAlpha(alpha);
        }
        
        // Dessiner le sprite centré dans la case
        gc.drawImage(
            currentSprite,
            spriteRenderX,
            spriteRenderY,
            spriteRenderWidth,
            spriteRenderHeight
        );
        
        // Restaurer l'état du contexte graphique
        gc.restore();
    }
    
    /**
     * @return La direction actuelle du personnage
     */
    public String getCurrentDirection() {
        return currentDirection;
    }
    
    /**
     * @return True si le personnage est en train de marcher (animation active)
     */
    public boolean isWalking() {
        return isWalking;
    }
    
    /**
     * @return La frame actuelle de l'animation (0 à 4)
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
    
    /**
     * @return La largeur de rendu actuelle du sprite (après mise à l'échelle)
     */
    public double getRenderWidth() {
        if (needsRecalculation) {
            calculateRenderParameters();
        }
        return spriteRenderWidth;
    }
    
    /**
     * @return La hauteur de rendu actuelle du sprite (après mise à l'échelle)
     */
    public double getRenderHeight() {
        if (needsRecalculation) {
            calculateRenderParameters();
        }
        return spriteRenderHeight;
    }
    
    /**
     * @return La position X de rendu du sprite (coin supérieur gauche)
     */
    public double getRenderX() {
        if (needsRecalculation) {
            calculateRenderParameters();
        }
        return spriteRenderX;
    }
    
    /**
     * @return La position Y de rendu du sprite (coin supérieur gauche)
     */
    public double getRenderY() {
        if (needsRecalculation) {
            calculateRenderParameters();
        }
        return spriteRenderY;
    }
    
    /**
     * Invalide le cache et force le recalcul des paramètres de rendu
     */
    public void invalidateCache() {
        this.needsRecalculation = true;
    }
    
    /**
     * Libère les ressources d'animation
     */
    public void dispose() {
        if (walkingAnimation != null) {
            walkingAnimation.stop();
            walkingAnimation = null;
        }
        if (deathAnimation != null) {
            deathAnimation.stop();
            deathAnimation = null;
        }
        onDeathAnimationComplete = null;
    }
    
    /**
     * Démarre l'animation de mort (non-bouclante)
     * @param onComplete Callback appelé à la fin de l'animation (optionnel)
     */
    public void startDeathAnimation(Runnable onComplete) {
        // Arrêter toute animation en cours
        if (walkingAnimation != null) {
            walkingAnimation.stop();
        }
        if (deathAnimation != null) {
            deathAnimation.stop();
        }
        
        // Changer d'état et réinitialiser
        this.currentState = AnimationState.DYING;
        this.isWalking = false;
        this.currentFrame = 0;
        this.onDeathAnimationComplete = onComplete;
        updateCurrentSprite();
        this.needsRecalculation = true;
        
        // Créer et démarrer l'animation de mort
        initializeDeathAnimation();
        
        System.out.println("🎬 Animation de mort démarrée (8 frames, 200ms chacune)");
    }
    
    /**
     * Initialise l'animation de mort avec 8 frames non-bouclantes
     */
    private void initializeDeathAnimation() {
        if (deathAnimation != null) {
            deathAnimation.stop();
        }

        deathAnimation = new Timeline();
        // L'animation ne doit se jouer qu'une seule fois
        deathAnimation.setCycleCount(1);

        // Ajouter une KeyFrame pour chaque sprite de l'animation de mort.
        // Chaque KeyFrame met à jour le sprite à un moment précis.
        for (int i = 0; i < spritesDeath.length; i++) {
            final int frameIndex = i;
            KeyFrame kf = new KeyFrame(Duration.millis(DEATH_DURATION_MS * i), e -> {
                this.currentFrame = frameIndex;
                updateCurrentSprite();
                needsRecalculation = true;
            });
            deathAnimation.getKeyFrames().add(kf);
        }

        // Ajouter une KeyFrame finale vide pour garantir que la dernière image reste visible
        // pendant la durée souhaitée. La durée totale sera 8 * 200ms = 1600ms.
        deathAnimation.getKeyFrames().add(
            new KeyFrame(Duration.millis(DEATH_DURATION_MS * spritesDeath.length))
        );

        // Définir une action à exécuter lorsque l'animation est complètement terminée
        deathAnimation.setOnFinished(e -> {
            System.out.println("💀 Animation de mort terminée (onFinished)");

            // Exécuter le callback pour notifier la fin de la séquence de mort
            if (onDeathAnimationComplete != null) {
                onDeathAnimationComplete.run();
                onDeathAnimationComplete = null; // N'exécuter qu'une seule fois
            }
        });

        // Lancer l'animation
        deathAnimation.play();
    }
    
    /**
     * Vérifie si l'animation de mort est en cours
     * @return true si l'animation de mort est active
     */
    public boolean isDeathAnimationPlaying() {
        return currentState == AnimationState.DYING && 
               deathAnimation != null && 
               deathAnimation.getStatus() == Timeline.Status.RUNNING;
    }
    
    /**
     * Vérifie si le personnage est mort (animation terminée ou en cours)
     * @return true si le personnage est dans l'état de mort
     */
    public boolean isDead() {
        return currentState == AnimationState.DYING;
    }
    
    /**
     * Remet le personnage en vie (retour à l'état ALIVE_IDLE)
     * Utilisé lors du respawn
     */
    public void revive() {
        // Arrêter toute animation en cours
        if (walkingAnimation != null) {
            walkingAnimation.stop();
        }
        if (deathAnimation != null) {
            deathAnimation.stop();
        }
        
        // Remettre en état vivant
        this.currentState = AnimationState.ALIVE_IDLE;
        this.isWalking = false;
        this.currentFrame = 0;
        this.onDeathAnimationComplete = null;
        updateCurrentSprite();
        this.needsRecalculation = true;
        
        System.out.println("✨ Personnage remis en vie - État: ALIVE_IDLE");
    }
} 