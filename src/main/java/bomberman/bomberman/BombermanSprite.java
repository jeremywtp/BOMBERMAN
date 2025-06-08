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
     * Charge tous les sprites fixes de Bomberman depuis les ressources
     */
    private static void loadSprites() {
        if (spriteHaut == null) {
            try {
                spriteHaut = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_haut.png"));
                spriteBas = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_bas.png"));
                spriteGauche = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_gauche.png"));
                spriteDroite = new Image(BombermanSprite.class.getResourceAsStream("/sprites/perso/bomberman_fixe_droite.png"));
                
                System.out.println("Sprites Bomberman chargés avec succès :");
                System.out.println("- Haut: " + spriteHaut.getWidth() + "x" + spriteHaut.getHeight());
                System.out.println("- Bas: " + spriteBas.getWidth() + "x" + spriteBas.getHeight());
                System.out.println("- Gauche: " + spriteGauche.getWidth() + "x" + spriteGauche.getHeight());
                System.out.println("- Droite: " + spriteDroite.getWidth() + "x" + spriteDroite.getHeight());
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des sprites Bomberman : " + e.getMessage());
                e.printStackTrace();
            }
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
     * Calcule les paramètres de rendu pour centrer le sprite dans la case 48x48
     * sans déformation, en conservant les proportions originales
     */
    private void calculateRenderParameters() {
        if (currentSprite == null) {
            return;
        }
        
        double originalWidth = currentSprite.getWidth();
        double originalHeight = currentSprite.getHeight();
        
        // Calculer le facteur d'échelle pour que le sprite rentre dans une case 48x48
        // sans déformation (conserver le ratio d'aspect)
        double scaleX = CELL_SIZE / originalWidth;
        double scaleY = CELL_SIZE / originalHeight;
        double scale = Math.min(scaleX, scaleY); // Prendre le plus petit pour éviter le débordement
        
        // Dimensions finales du sprite à l'écran
        this.spriteRenderWidth = originalWidth * scale;
        this.spriteRenderHeight = originalHeight * scale;
        
        // Position pour centrer le sprite dans la case 48x48
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
     * Invalide le cache et force le recalcul des paramètres de rendu
     */
    public void invalidateCache() {
        this.needsRecalculation = true;
    }
} 