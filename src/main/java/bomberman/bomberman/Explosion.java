package bomberman.bomberman;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une explosion dans le jeu Bomberman.
 * Gère le calcul des cases affectées par l'explosion en forme de croix,
 * la destruction des blocs destructibles et la durée d'affichage des flammes.
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
     * @param grid Grille pour vérifier les obstacles et détruire les blocs
     * @param exitDoor Porte de sortie pour vérifier si l'explosion doit s'arrêter
     */
    public Explosion(int centerX, int centerY, int range, Grid grid, ExitDoor exitDoor) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.affectedCells = new ArrayList<>();
        this.isActive = true;
        this.startTime = System.currentTimeMillis();
        
        calculateAffectedCells(range, grid, exitDoor);
    }
    
    /**
     * Calcule les cases affectées par l'explosion en forme de croix
     * et détruit les blocs destructibles touchés
     * @param range Portée de l'explosion
     * @param grid Grille pour vérifier les obstacles et détruire les blocs
     * @param exitDoor Porte de sortie pour vérifier si l'explosion doit s'arrêter
     */
    private void calculateAffectedCells(int range, Grid grid, ExitDoor exitDoor) {
        // Ajouter le centre de l'explosion
        affectedCells.add(new ExplosionCell(centerX, centerY));
        
        // Explosion vers le haut
        for (int i = 1; i <= range; i++) {
            int y = centerY - i;
            if (y < 0) {
                break; // Arrêter si hors limites
            }
            
            // Vérifier le type de case
            TileType tileType = grid.getTileType(centerX, y);
            
            if (tileType == TileType.SOLID) {
                break; // Arrêter sur un bloc solide
            } else if (tileType == TileType.DESTRUCTIBLE) {
                // Détruire le bloc destructible et ajouter la case à l'explosion
                grid.destroyBlock(centerX, y);
                affectedCells.add(new ExplosionCell(centerX, y));
                break; // Arrêter après avoir détruit un bloc
            } else {
                // Case vide, vérifier si la porte de sortie visible bloque l'explosion
                if (isVisibleExitDoorAt(centerX, y, exitDoor)) {
                    // Ajouter la case de la porte aux cellules affectées pour le respawn d'ennemis
                    affectedCells.add(new ExplosionCell(centerX, y));
                    break; // Arrêter sur la porte de sortie visible
                }
                // Case vide, continuer l'explosion
                affectedCells.add(new ExplosionCell(centerX, y));
            }
        }
        
        // Explosion vers le bas
        for (int i = 1; i <= range; i++) {
            int y = centerY + i;
            if (y >= grid.getRows()) {
                break; // Arrêter si hors limites
            }
            
            // Vérifier le type de case
            TileType tileType = grid.getTileType(centerX, y);
            
            if (tileType == TileType.SOLID) {
                break; // Arrêter sur un bloc solide
            } else if (tileType == TileType.DESTRUCTIBLE) {
                // Détruire le bloc destructible et ajouter la case à l'explosion
                grid.destroyBlock(centerX, y);
                affectedCells.add(new ExplosionCell(centerX, y));
                break; // Arrêter après avoir détruit un bloc
            } else {
                // Case vide, vérifier si la porte de sortie visible bloque l'explosion
                if (isVisibleExitDoorAt(centerX, y, exitDoor)) {
                    // Ajouter la case de la porte aux cellules affectées pour le respawn d'ennemis
                    affectedCells.add(new ExplosionCell(centerX, y));
                    break; // Arrêter sur la porte de sortie visible
                }
                // Case vide, continuer l'explosion
                affectedCells.add(new ExplosionCell(centerX, y));
            }
        }
        
        // Explosion vers la gauche
        for (int i = 1; i <= range; i++) {
            int x = centerX - i;
            if (x < 0) {
                break; // Arrêter si hors limites
            }
            
            // Vérifier le type de case
            TileType tileType = grid.getTileType(x, centerY);
            
            if (tileType == TileType.SOLID) {
                break; // Arrêter sur un bloc solide
            } else if (tileType == TileType.DESTRUCTIBLE) {
                // Détruire le bloc destructible et ajouter la case à l'explosion
                grid.destroyBlock(x, centerY);
                affectedCells.add(new ExplosionCell(x, centerY));
                break; // Arrêter après avoir détruit un bloc
            } else {
                // Case vide, vérifier si la porte de sortie visible bloque l'explosion
                if (isVisibleExitDoorAt(x, centerY, exitDoor)) {
                    // Ajouter la case de la porte aux cellules affectées pour le respawn d'ennemis
                    affectedCells.add(new ExplosionCell(x, centerY));
                    break; // Arrêter sur la porte de sortie visible
                }
                // Case vide, continuer l'explosion
                affectedCells.add(new ExplosionCell(x, centerY));
            }
        }
        
        // Explosion vers la droite
        for (int i = 1; i <= range; i++) {
            int x = centerX + i;
            if (x >= grid.getColumns()) {
                break; // Arrêter si hors limites
            }
            
            // Vérifier le type de case
            TileType tileType = grid.getTileType(x, centerY);
            
            if (tileType == TileType.SOLID) {
                break; // Arrêter sur un bloc solide
            } else if (tileType == TileType.DESTRUCTIBLE) {
                // Détruire le bloc destructible et ajouter la case à l'explosion
                grid.destroyBlock(x, centerY);
                affectedCells.add(new ExplosionCell(x, centerY));
                break; // Arrêter après avoir détruit un bloc
            } else {
                // Case vide, vérifier si la porte de sortie visible bloque l'explosion
                if (isVisibleExitDoorAt(x, centerY, exitDoor)) {
                    // Ajouter la case de la porte aux cellules affectées pour le respawn d'ennemis
                    affectedCells.add(new ExplosionCell(x, centerY));
                    break; // Arrêter sur la porte de sortie visible
                }
                // Case vide, continuer l'explosion
                affectedCells.add(new ExplosionCell(x, centerY));
            }
        }
    }
    
    /**
     * Vérifie si la porte de sortie visible est à la position donnée
     * @param x Position X
     * @param y Position Y
     * @param exitDoor Porte de sortie à vérifier
     * @return true si la porte de sortie visible est à cette position
     */
    private boolean isVisibleExitDoorAt(int x, int y, ExitDoor exitDoor) {
        return exitDoor != null && exitDoor.isVisible() && exitDoor.getX() == x && exitDoor.getY() == y;
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