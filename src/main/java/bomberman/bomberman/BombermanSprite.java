package bomberman.bomberman;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

/**
 * Classe responsable de l'affichage du personnage Bomberman avec ses sprites directionnels.
 * Gère le rendu pixel perfect avec conservation des proportions originales.
 * Chaque sprite est centré dans une case de 48x48 pixels sans déformation.
 */
public class BombermanSprite {
    
    // Taille logique d'une case dans le jeu
    private static final int CELL_SIZE = 48;
    
    // Images des sprites fixes selon la direction
    private static Image spriteHaut;
    private static Image spriteBas;
    private static Image spriteGauche;
    private static Image spriteDroite;
    
    // Direction actuelle du personnage
    private String currentDirection;
    
    // Position en pixels pour le rendu
    private double renderX;
    private double renderY;
    
    // Cache pour éviter les recalculs répétés
    private Image currentSprite;
    private double spriteRenderX;
    private double spriteRenderY;
    private double spriteRenderWidth;
    private double spriteRenderHeight;
    private boolean needsRecalculation;
    
    /**
     * Constructeur qui charge les sprites et initialise la direction par défaut
     */
    public BombermanSprite() {
        loadSprites();
        setDirection("bas"); // Direction par défaut
        this.needsRecalculation = true;
    }
    
    /**
     * Charge tous les sprites fixes depuis le SpriteManager (thème actuel)
     */
    private static void loadSprites() {
        if (spriteHaut == null) {
            try {
                // ✨ **NOUVEAU** : Utiliser le SpriteManager pour obtenir les sprites du thème actuel
                SpriteManager spriteManager = SpriteManager.getInstance();
                SpriteManager.ThemeSprites currentSprites = spriteManager.getCurrentSprites();
                
                if (currentSprites != null) {
                    spriteHaut = currentSprites.playerFixeHaut;
                    spriteBas = currentSprites.playerFixeBas;
                    spriteGauche = currentSprites.playerFixeGauche;
                    spriteDroite = currentSprites.playerFixeDroite;
                
                    System.out.println("Sprites joueur fixes chargés depuis le thème : " + spriteManager.getCurrentTheme().getDisplayName());
                System.out.println("- Haut: " + spriteHaut.getWidth() + "x" + spriteHaut.getHeight());
                System.out.println("- Bas: " + spriteBas.getWidth() + "x" + spriteBas.getHeight());
                System.out.println("- Gauche: " + spriteGauche.getWidth() + "x" + spriteGauche.getHeight());
                System.out.println("- Droite: " + spriteDroite.getWidth() + "x" + spriteDroite.getHeight());
                } else {
                    // Fallback vers le chargement direct si le SpriteManager n'est pas disponible
                    loadSpritesFallback();
                }
                
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des sprites depuis le SpriteManager : " + e.getMessage());
                // Fallback final
                loadSpritesFallback();
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Méthode de fallback pour charger les sprites par défaut
     */
    private static void loadSpritesFallback() {
        try {
            spriteHaut = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_haut.png"));
            spriteBas = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_bas.png"));
            spriteGauche = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_gauche.png"));
            spriteDroite = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_droite.png"));
            
            System.out.println("Sprites joueur fixes chargés en fallback (Bomberman par défaut)");
            
        } catch (Exception fallbackException) {
            System.err.println("Erreur critique lors du chargement des sprites en fallback : " + fallbackException.getMessage());
            fallbackException.printStackTrace();
        }
    }
    
    /**
     * Définit la direction actuelle de Bomberman et met à jour le sprite correspondant
     * @param direction Direction ("haut", "bas", "gauche", "droite")
     */
    public void setDirection(String direction) {
        if (!direction.equals(this.currentDirection)) {
            this.currentDirection = direction;
            this.needsRecalculation = true;
            
            // Sélectionner le sprite correspondant à la direction
            switch (direction.toLowerCase()) {
                case "haut":
                    this.currentSprite = spriteHaut;
                    break;
                case "bas":
                    this.currentSprite = spriteBas;
                    break;
                case "gauche":
                    this.currentSprite = spriteGauche;
                    break;
                case "droite":
                    this.currentSprite = spriteDroite;
                    break;
                default:
                    System.err.println("Direction inconnue : " + direction + ". Direction 'bas' utilisée par défaut.");
                    this.currentSprite = spriteBas;
                    this.currentDirection = "bas";
                    break;
            }
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
     * ✨ **NOUVEAU** : Force le rechargement des sprites (utile lors du changement de thème)
     */
    public static void reloadSprites() {
        // Réinitialiser les sprites pour forcer le rechargement
        spriteHaut = null;
        spriteBas = null;
        spriteGauche = null;
        spriteDroite = null;
        
        // Recharger tous les sprites
        loadSprites();
        System.out.println("Sprites joueur fixes rechargés pour le nouveau thème");
    }
    
    /**
     * Invalide le cache et force le recalcul des paramètres de rendu
     */
    public void invalidateCache() {
        this.needsRecalculation = true;
    }
} 