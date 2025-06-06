package bomberman.bomberman;

/**
 * Énumération des différents états du jeu Bomberman.
 * Contrôle le flux principal du jeu entre menu, partie en cours, niveau terminé et game over.
 */
public enum GameState {
    
    /**
     * État du menu de démarrage
     * Affiche les instructions pour commencer le jeu
     */
    START_MENU,
    
    /**
     * État de démarrage de niveau
     * Pendant la musique Level_Start.wav, aucun input n'est traité
     */
    LEVEL_STARTING,
    
    /**
     * État de jeu en cours
     * Le joueur peut se déplacer, poser des bombes, etc.
     */
    RUNNING,
    
    /**
     * État de jeu en pause
     * Le jeu est figé, menu pause affiché avec navigation clavier
     */
    PAUSED,
    
    /**
     * État d'affichage des commandes
     * Panneau des commandes affiché par-dessus le menu pause
     */
    COMMANDS_DISPLAY,
    
    /**
     * État de niveau terminé
     * Affiche l'écran de transition vers le niveau suivant
     */
    LEVEL_COMPLETED,
    
    /**
     * État de fin de partie
     * Affiche le game over et permet de rejouer
     */
    GAME_OVER
} 