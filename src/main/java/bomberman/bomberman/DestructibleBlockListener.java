package bomberman.bomberman;

/**
 * Interface pour recevoir les notifications de destruction de blocs destructibles.
 * Permet de synchroniser les animations avec la logique de jeu.
 */
public interface DestructibleBlockListener {
    
    /**
     * Appelé quand un bloc destructible est détruit
     * @param column Position en colonne (x) du bloc détruit
     * @param row Position en ligne (y) du bloc détruit
     */
    void onBlockDestroyed(int column, int row);
} 