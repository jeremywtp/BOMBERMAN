package bomberman.bomberman;

/**
 * Énumération représentant les différents types de cases dans la grille Bomberman.
 * Chaque type a des propriétés spécifiques concernant la traversabilité et la destructibilité.
 */
public enum TileType {
    
    /**
     * Case vide - Traversable par le joueur et les explosions
     */
    EMPTY,
    
    /**
     * Bloc solide - Indestructible, bloque les déplacements et les explosions
     */
    SOLID,
    
    /**
     * Bloc destructible - Bloque les déplacements mais peut être détruit par les explosions
     */
    DESTRUCTIBLE;
    
    /**
     * Vérifie si ce type de case est traversable par le joueur
     * @return true si le joueur peut traverser cette case
     */
    public boolean isTraversable() {
        return this == EMPTY;
    }
    
    /**
     * Vérifie si ce type de case est destructible par les explosions
     * @return true si cette case peut être détruite
     */
    public boolean isDestructible() {
        return this == DESTRUCTIBLE;
    }
    
    /**
     * Vérifie si ce type de case bloque les explosions
     * @return true si les explosions ne peuvent pas traverser cette case
     */
    public boolean blocksExplosion() {
        return this == SOLID || this == DESTRUCTIBLE;
    }
} 