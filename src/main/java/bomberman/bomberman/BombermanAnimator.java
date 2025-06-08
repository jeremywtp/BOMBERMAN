package bomberman.bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

/**
 * Classe responsable de l'animation du personnage Bomberman avec ses sprites de marche.
 * Gère l'animation fluide avec 2 frames par direction, rendu pixel perfect et centrage automatique.
 * Étend les fonctionnalités de BombermanSprite en ajoutant l'animation de marche.
 */
public class BombermanAnimator {
    
    // Taille logique d'une case dans le jeu
    private static final int CELL_SIZE = 48;
    
    // Durée d'animation entre les frames (200ms = animation fluide)
    private static final double ANIMATION_DURATION_MS = 200.0;
    
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
    
    // État actuel
    private String currentDirection;
    private boolean isWalking;
    private int currentFrame; // 0 ou 1 pour alterner entre les 2 frames
    
    // Position en pixels pour le rendu
    private double renderX;
    private double renderY;
    
    // Animation
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
    public BombermanAnimator() {
        loadAllSprites();
        setDirection("bas"); // Direction par défaut
        this.isWalking = false;
        this.currentFrame = 0;
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
     * Initialise l'animation de marche avec Timeline
     */
    private void initializeWalkingAnimation() {
        walkingAnimation = new Timeline(
            new KeyFrame(Duration.millis(ANIMATION_DURATION_MS), e -> {
                // Alterner entre les 2 frames d'animation
                currentFrame = (currentFrame == 0) ? 1 : 0;
                updateCurrentSprite();
                needsRecalculation = true;
            })
        );
        walkingAnimation.setCycleCount(Timeline.INDEFINITE); // Animation en boucle
    }
    
    /**
     * Met à jour le sprite actuel selon la direction, l'état (marche/immobile) et la frame
     */
    private void updateCurrentSprite() {
        if (isWalking) {
            // Utiliser les sprites d'animation
            switch (currentDirection.toLowerCase()) {
                case "haut":
                    this.currentSprite = (currentFrame == 0) ? spriteMarcheHaut1 : spriteMarcheHaut2;
                    break;
                case "bas":
                    this.currentSprite = (currentFrame == 0) ? spriteMarcheBas1 : spriteMarcheBas2;
                    break;
                case "gauche":
                    this.currentSprite = (currentFrame == 0) ? spriteMarcheGauche1 : spriteMarcheGauche2;
                    break;
                case "droite":
                    this.currentSprite = (currentFrame == 0) ? spriteMarcheDroite1 : spriteMarcheDroite2;
                    break;
                default:
                    this.currentSprite = spriteMarcheBas1;
                    break;
            }
        } else {
            // Utiliser les sprites fixes
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
        if (!isWalking) {
            this.isWalking = true;
            this.currentFrame = 0;
            updateCurrentSprite();
            walkingAnimation.play();
            this.needsRecalculation = true;
            System.out.println("Animation de marche démarrée - Direction: " + currentDirection);
        }
    }
    
    /**
     * Arrête l'animation de marche et revient au sprite fixe
     */
    public void stopWalking() {
        if (isWalking) {
            this.isWalking = false;
            walkingAnimation.stop();
            updateCurrentSprite(); // Revenir au sprite fixe
            this.needsRecalculation = true;
            System.out.println("Animation de marche arrêtée - Retour au sprite fixe");
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
     * @return La frame actuelle de l'animation (0 ou 1)
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
     * Libère les ressources de l'animation
     */
    public void dispose() {
        if (walkingAnimation != null) {
            walkingAnimation.stop();
        }
    }
} 