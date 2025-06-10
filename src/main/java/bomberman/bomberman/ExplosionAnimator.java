package bomberman.bomberman;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe responsable de l'animation des explosions de bombes avec sprites.
 * Gère le chargement des sprites d'explosion et l'animation frame par frame.
 * 
 * Types de sprites supportés :
 * - explosion_milieu_* : Centre de l'explosion
 * - explosion_droite_* : Extrémité droite
 * - explosion_gauche_* : Extrémité gauche
 * - explosion_haut_* : Extrémité haute
 * - explosion_bas_* : Extrémité basse
 * - explosion_horizontale_* : Segments horizontaux
 * - explosion_verticale_* : Segments verticaux
 */
public class ExplosionAnimator {
    
    // Animation configuration
    private static final int ANIMATION_FRAMES = 5;     // 5 frames d'animation (1 à 5)
    private static final long FRAME_DURATION = 100;    // 100ms par frame
    private static final long TOTAL_DURATION = ANIMATION_FRAMES * FRAME_DURATION; // 500ms total
    
    // Constantes de rendu
    private static final int CELL_SIZE = 48;
    private static final int GRID_VERTICAL_OFFSET = 100;
    
    // Sprites statiques (chargés une seule fois)
    private static Image[][] explosionSprites;  // [type][frame]
    private static boolean spritesLoaded = false;
    
    // Types d'explosion
    public enum ExplosionType {
        MILIEU(0),       // Centre
        DROITE(1),       // Extrémité droite
        GAUCHE(2),       // Extrémité gauche
        HAUT(3),         // Extrémité haute
        BAS(4),          // Extrémité basse
        HORIZONTALE(5),  // Segment horizontal
        VERTICALE(6);    // Segment vertical
        
        private final int index;
        
        ExplosionType(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
    }
    
    // Instance d'animation
    private Timeline animationTimeline;
    private int currentFrame;
    private boolean isActive;
    private long startTime;
    private List<ExplosionSegment> segments;
    
    /**
     * Classe représentant un segment d'explosion avec sa position et son type
     */
    public static class ExplosionSegment {
        private final int x;
        private final int y;
        private final ExplosionType type;
        
        public ExplosionSegment(int x, int y, ExplosionType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public ExplosionType getType() { return type; }
    }
    
    /**
     * Constructeur
     */
    public ExplosionAnimator() {
        loadSprites();
        this.segments = new ArrayList<>();
        this.isActive = false;
        this.currentFrame = 0;
    }
    
    /**
     * Charge tous les sprites d'explosion de manière statique
     */
    private static void loadSprites() {
        if (spritesLoaded) {
            return;
        }
        
        System.out.println("Chargement des sprites d'explosion...");
        
        explosionSprites = new Image[7][ANIMATION_FRAMES];
        String[] typeNames = {"milieu", "droite", "gauche", "haut", "bas", "horizontale", "verticale"};
        
        for (int type = 0; type < 7; type++) {
            for (int frame = 0; frame < ANIMATION_FRAMES; frame++) {
                String filename = "/sprites/resized_explosion_" + typeNames[type] + "_" + (frame + 1) + ".png";
                try {
                    explosionSprites[type][frame] = new Image(ExplosionAnimator.class.getResourceAsStream(filename));
                    if (explosionSprites[type][frame] == null) {
                        System.err.println("Impossible de charger le sprite d'explosion : " + filename);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement du sprite d'explosion " + filename + " : " + e.getMessage());
                }
            }
        }
        
        spritesLoaded = true;
        System.out.println("Sprites d'explosion chargés avec succès !");
    }
    
    /**
     * Démarre l'animation d'explosion à partir des données d'une explosion
     * @param explosion L'explosion à animer
     * @param range La portée de l'explosion
     */
    public void startExplosion(Explosion explosion, int range) {
        if (isActive) {
            stop(); // Arrêter l'animation précédente
        }
        
        // Générer les segments d'explosion selon la géométrie
        generateExplosionGeometry(explosion.getCenterX(), explosion.getCenterY(), explosion.getAffectedCells());
        
        System.out.println("🎬 Animation explosion démarrée - " + segments.size() + " segments générés");
        
        // Initialiser l'animation
        this.isActive = true;
        this.currentFrame = 0;
        this.startTime = System.currentTimeMillis();
        
        // Créer et démarrer la timeline d'animation
        animationTimeline = new Timeline();
        animationTimeline.setCycleCount(ANIMATION_FRAMES);
        
        animationTimeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(FRAME_DURATION), e -> {
                currentFrame++;
                if (currentFrame >= ANIMATION_FRAMES) {
                    stop();
                }
            })
        );
        
        animationTimeline.play();
    }
    
    /**
     * Génère la géométrie d'explosion avec les types appropriés pour chaque segment
     * @param centerX Position centrale X
     * @param centerY Position centrale Y
     * @param affectedCells Liste des cellules affectées
     */
    private void generateExplosionGeometry(int centerX, int centerY, List<Explosion.ExplosionCell> affectedCells) {
        segments.clear();
        
        // Ajouter le centre
        segments.add(new ExplosionSegment(centerX, centerY, ExplosionType.MILIEU));
        
        // Analyser chaque cellule pour déterminer son type
        for (Explosion.ExplosionCell cell : affectedCells) {
            int x = cell.getX();
            int y = cell.getY();
            
            // Ignorer le centre (déjà ajouté)
            if (x == centerX && y == centerY) {
                continue;
            }
            
            // Déterminer le type selon la position relative au centre
            ExplosionType type = determineSegmentType(x, y, centerX, centerY, affectedCells);
            segments.add(new ExplosionSegment(x, y, type));
        }
    }
    
    /**
     * Détermine le type d'un segment d'explosion selon sa position
     * @param x Position X du segment
     * @param y Position Y du segment
     * @param centerX Position centrale X
     * @param centerY Position centrale Y
     * @param allCells Toutes les cellules affectées
     * @return Le type approprié pour ce segment
     */
    private ExplosionType determineSegmentType(int x, int y, int centerX, int centerY, List<Explosion.ExplosionCell> allCells) {
        boolean isHorizontal = (y == centerY);
        boolean isVertical = (x == centerX);
        
        if (isHorizontal) {
            // Segment horizontal
            if (x > centerX) {
                // Côté droit - vérifier si c'est une extrémité
                return isEndOfExplosion(x + 1, y, allCells) ? ExplosionType.DROITE : ExplosionType.HORIZONTALE;
            } else {
                // Côté gauche - vérifier si c'est une extrémité
                return isEndOfExplosion(x - 1, y, allCells) ? ExplosionType.GAUCHE : ExplosionType.HORIZONTALE;
            }
        } else if (isVertical) {
            // Segment vertical
            if (y > centerY) {
                // Côté bas - vérifier si c'est une extrémité
                return isEndOfExplosion(x, y + 1, allCells) ? ExplosionType.BAS : ExplosionType.VERTICALE;
            } else {
                // Côté haut - vérifier si c'est une extrémité
                return isEndOfExplosion(x, y - 1, allCells) ? ExplosionType.HAUT : ExplosionType.VERTICALE;
            }
        }
        
        // Ne devrait pas arriver dans une explosion en croix
        return ExplosionType.MILIEU;
    }
    
    /**
     * Vérifie si une position marque la fin d'une branche d'explosion
     * @param x Position X à vérifier
     * @param y Position Y à vérifier
     * @param allCells Toutes les cellules affectées
     * @return true si cette position n'est pas dans l'explosion (donc fin de branche)
     */
    private boolean isEndOfExplosion(int x, int y, List<Explosion.ExplosionCell> allCells) {
        for (Explosion.ExplosionCell cell : allCells) {
            if (cell.getX() == x && cell.getY() == y) {
                return false; // Position trouvée dans l'explosion
            }
        }
        return true; // Position pas dans l'explosion = fin de branche
    }
    
    /**
     * Rend l'animation d'explosion sur le canvas
     * @param gc Le contexte graphique
     * @param canvas Le canvas pour calculer les offsets
     */
    public void render(GraphicsContext gc, javafx.scene.canvas.Canvas canvas) {
        if (!isActive || segments.isEmpty()) {
            System.out.println("⚠️ Render explosion skipped - Active: " + isActive + ", Segments: " + segments.size());
            return;
        }
        
        // Calculer l'offset horizontal pour centrer la grille
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        
        System.out.println("🎬 Rendering explosion frame " + currentFrame + " with " + segments.size() + " segments");
        
        // Dessiner chaque segment avec le sprite approprié
        for (ExplosionSegment segment : segments) {
            Image sprite = getExplosionSprite(segment.getType(), currentFrame);
            if (sprite != null) {
                int x = (int) (segment.getX() * CELL_SIZE + horizontalOffset);
                int y = segment.getY() * CELL_SIZE + GRID_VERTICAL_OFFSET;
                gc.drawImage(sprite, x, y, CELL_SIZE, CELL_SIZE);
            } else {
                System.out.println("⚠️ Sprite null pour type " + segment.getType() + " frame " + currentFrame);
            }
        }
    }
    
    /**
     * Retourne le sprite approprié pour un type et une frame donnés
     * @param type Type d'explosion
     * @param frame Frame actuelle (0-4)
     * @return L'image du sprite ou null si non trouvé
     */
    private Image getExplosionSprite(ExplosionType type, int frame) {
        if (frame < 0 || frame >= ANIMATION_FRAMES) {
            return null;
        }
        
        return explosionSprites[type.getIndex()][frame];
    }
    
    /**
     * Arrête l'animation
     */
    public void stop() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        isActive = false;
        segments.clear();
    }
    
    /**
     * @return true si l'animation est active
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Met à jour l'état de l'animation
     * @return true si l'animation est terminée
     */
    public boolean update() {
        if (!isActive) {
            return true;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= TOTAL_DURATION) {
            stop();
            return true;
        }
        
        return false;
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        stop();
    }
} 