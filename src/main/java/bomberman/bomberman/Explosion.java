package bomberman.bomberman;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une explosion dans le jeu Bomberman.
 * Gère le calcul des cases affectées par l'explosion en forme de croix
 * et la durée d'affichage des flammes.
 */
public class Explosion {
    
    // Position centrale de l'explosion
    private final int centerX;
    private final int centerY;
    
    // Cases affectées par l'explosion
    private final List<ExplosionCell> affectedCells;
    
    // État de l'explosion
    private boolean isActive;
    private final long startTime;
    
    // Durée d'affichage des flammes (en millisecondes)
    private static final long FLAME_DURATION = 500; // 0.5 seconde
    
    /**
     * Classe interne représentant une case affectée par l'explosion
     */
    public static class ExplosionCell {
        private final int x;
        private final int y;
        
        public ExplosionCell(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
    }
    
    /**
     * Constructeur de l'explosion
     * @param centerX Position centrale en colonne
     * @param centerY Position centrale en ligne
     * @param range Portée de l'explosion
     * @param grid Grille pour vérifier les obstacles
     */
    public Explosion(int centerX, int centerY, int range, Grid grid) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.affectedCells = new ArrayList<>();
        this.isActive = true;
        this.startTime = System.currentTimeMillis();
        
        calculateAffectedCells(range, grid);
    }
    
    /**
     * Calcule les cases affectées par l'explosion en forme de croix
     * @param range Portée de l'explosion
     * @param grid Grille pour vérifier les obstacles
     */
    private void calculateAffectedCells(int range, Grid grid) {
        // Ajouter le centre de l'explosion
        affectedCells.add(new ExplosionCell(centerX, centerY));
        
        // Explosion vers le haut
        for (int i = 1; i <= range; i++) {
            int y = centerY - i;
            if (y < 0 || grid.isSolid(centerX, y)) {
                break; // Arrêter si hors limites ou bloc solide
            }
            affectedCells.add(new ExplosionCell(centerX, y));
        }
        
        // Explosion vers le bas
        for (int i = 1; i <= range; i++) {
            int y = centerY + i;
            if (y >= grid.getRows() || grid.isSolid(centerX, y)) {
                break; // Arrêter si hors limites ou bloc solide
            }
            affectedCells.add(new ExplosionCell(centerX, y));
        }
        
        // Explosion vers la gauche
        for (int i = 1; i <= range; i++) {
            int x = centerX - i;
            if (x < 0 || grid.isSolid(x, centerY)) {
                break; // Arrêter si hors limites ou bloc solide
            }
            affectedCells.add(new ExplosionCell(x, centerY));
        }
        
        // Explosion vers la droite
        for (int i = 1; i <= range; i++) {
            int x = centerX + i;
            if (x >= grid.getColumns() || grid.isSolid(x, centerY)) {
                break; // Arrêter si hors limites ou bloc solide
            }
            affectedCells.add(new ExplosionCell(x, centerY));
        }
    }
    
    /**
     * Met à jour l'état de l'explosion
     * @return true si l'explosion est terminée, false sinon
     */
    public boolean update() {
        if (!isActive) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= FLAME_DURATION) {
            isActive = false;
            return true; // Explosion terminée
        }
        
        return false;
    }
    
    /**
     * @return true si l'explosion est encore active (flammes visibles)
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * @return Liste des cases affectées par l'explosion
     */
    public List<ExplosionCell> getAffectedCells() {
        return affectedCells;
    }
    
    /**
     * @return Position centrale en colonne
     */
    public int getCenterX() {
        return centerX;
    }
    
    /**
     * @return Position centrale en ligne
     */
    public int getCenterY() {
        return centerY;
    }
    
    /**
     * @return Durée d'affichage des flammes en millisecondes
     */
    public static long getFlameDuration() {
        return FLAME_DURATION;
    }
} 