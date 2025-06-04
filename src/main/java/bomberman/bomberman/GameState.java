package bomberman.bomberman;

/**
 * Énumération des différents états du jeu Bomberman.
 * Contrôle le flux principal du jeu entre menu, partie en cours et game over.
 */
public enum GameState {
    
    /**
     * État du menu de démarrage
     * Affiche les instructions pour commencer le jeu
     */
    START_MENU,
    
    /**
     * État de jeu en cours
     * Le joueur peut se déplacer, poser des bombes, etc.
     */
    RUNNING,
    
    /**
     * État de fin de partie
     * Affiche le game over et permet de rejouer
     */
    GAME_OVER
} 