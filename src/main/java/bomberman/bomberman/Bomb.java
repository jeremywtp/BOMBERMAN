package bomberman.bomberman;

/**
 * Classe représentant une bombe dans le jeu Bomberman.
 * Gère la position, le timer d'explosion et l'état de la bombe.
 * Une bombe explose après 2 secondes et sa portée est de 2 cases dans chaque direction.
 */
public class Bomb {
    
    // Position de la bombe sur la grille
    private final int x;  // Colonne
    private final int y;  // Ligne
    
    // État de la bombe
    private boolean isActive;
    private boolean hasExploded;
    
    // Timer pour l'explosion (en millisecondes)
    private static final long EXPLOSION_DELAY = 2000; // 2 secondes
    private final long startTime;
    
    // Portée de l'explosion
    private static final int EXPLOSION_RANGE = 2;
    
    /**
     * Constructeur de la bombe
     * @param x Position en colonne
     * @param y Position en ligne
     */
    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.isActive = true;
        this.hasExploded = false;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Met à jour l'état de la bombe
     * @return true si la bombe a explosé ce tick, false sinon
     */
    public boolean update() {
        if (!isActive || hasExploded) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= EXPLOSION_DELAY) {
            hasExploded = true;
            return true; // Explosion déclenchée
        }
        
        return false;
    }
    
    /**
     * Désactive la bombe (fin de vie)
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * @return true si la bombe est encore active (visible)
     */
    public boolean isActive() {
        return isActive && !hasExploded;
    }
    
    /**
     * @return true si la bombe a explosé
     */
    public boolean hasExploded() {
        return hasExploded;
    }
    
    /**
     * @return Position en colonne (x)
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return Position en ligne (y)
     */
    public int getY() {
        return y;
    }
    
    /**
     * @return La portée de l'explosion
     */
    public static int getExplosionRange() {
        return EXPLOSION_RANGE;
    }
    
    /**
     * @return Le délai avant explosion en millisecondes
     */
    public static long getExplosionDelay() {
        return EXPLOSION_DELAY;
    }
} 