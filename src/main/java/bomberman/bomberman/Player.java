package bomberman.bomberman;

/**
 * Classe représentant le joueur dans le jeu Bomberman.
 * Stocke la position actuelle du joueur sur la grille et gère ses déplacements.
 * Le joueur ne peut pas traverser les blocs solides ni sortir de la grille.
 * Gère également l'état des bombes posées par le joueur et son état de vie.
 */
public class Player {
    
    // Position actuelle du joueur sur la grille (coordonnées logiques)
    private int x;  // Colonne
    private int y;  // Ligne
    
    // État des bombes du joueur
    private boolean hasActiveBomb;
    
    // État de vie du joueur
    private boolean isAlive;
    
    /**
     * Constructeur du joueur
     * @param startX Position initiale en colonne
     * @param startY Position initiale en ligne
     */
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.hasActiveBomb = false;
        this.isAlive = true;
    }
    
    /**
     * Tente de déplacer le joueur vers le haut
     * @param grid La grille pour vérifier les collisions
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveUp(Grid grid) {
        if (!isAlive) return false;
        return tryMove(x, y - 1, grid);
    }
    
    /**
     * Tente de déplacer le joueur vers le bas
     * @param grid La grille pour vérifier les collisions
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveDown(Grid grid) {
        if (!isAlive) return false;
        return tryMove(x, y + 1, grid);
    }
    
    /**
     * Tente de déplacer le joueur vers la gauche
     * @param grid La grille pour vérifier les collisions
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveLeft(Grid grid) {
        if (!isAlive) return false;
        return tryMove(x - 1, y, grid);
    }
    
    /**
     * Tente de déplacer le joueur vers la droite
     * @param grid La grille pour vérifier les collisions
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveRight(Grid grid) {
        if (!isAlive) return false;
        return tryMove(x + 1, y, grid);
    }
    
    /**
     * Méthode privée pour tenter un déplacement vers une position donnée
     * @param newX Nouvelle position en colonne
     * @param newY Nouvelle position en ligne
     * @param grid La grille pour vérifier les collisions
     * @return true si le déplacement a eu lieu, false sinon
     */
    private boolean tryMove(int newX, int newY, Grid grid) {
        // Vérifier si la nouvelle position est accessible
        if (grid.isAccessible(newX, newY)) {
            this.x = newX;
            this.y = newY;
            return true;
        }
        return false;
    }
    
    /**
     * Tue le joueur
     */
    public void kill() {
        this.isAlive = false;
    }
    
    /**
     * @return true si le joueur est vivant
     */
    public boolean isAlive() {
        return isAlive;
    }
    
    /**
     * @return Position actuelle en colonne (x)
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return Position actuelle en ligne (y)
     */
    public int getY() {
        return y;
    }
    
    /**
     * Définit une nouvelle position pour le joueur (sans vérification de collision)
     * @param x Nouvelle position en colonne
     * @param y Nouvelle position en ligne
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return true si le joueur a une bombe active
     */
    public boolean hasActiveBomb() {
        return hasActiveBomb;
    }
    
    /**
     * Définit l'état de la bombe active du joueur
     * @param hasActiveBomb true si le joueur a une bombe active
     */
    public void setHasActiveBomb(boolean hasActiveBomb) {
        this.hasActiveBomb = hasActiveBomb;
    }
} 