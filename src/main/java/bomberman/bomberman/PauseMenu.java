package bomberman.bomberman;

/**
 * Gestionnaire du menu pause du jeu Bomberman.
 * Affiche un menu semi-transparent avec options de navigation :
 * - Reprendre
 * - Recommencer 
 * - Retour au menu principal
 * 
 * Navigation via flèches haut/bas et validation par Entrée.
 * Le joueur peut aussi reprendre en appuyant sur ECHAP.
 */
public class PauseMenu {
    
    // Options du menu pause
    private static final String[] PAUSE_OPTIONS = {
        "REPRENDRE",
        "RECOMMENCER",
        "COMMANDES",
        "RETOUR AU MENU PRINCIPAL"
    };
    
    // Index de l'option actuellement sélectionnée
    private int selectedIndex = 0;
    
    /**
     * Constructeur du menu pause
     */
    public PauseMenu() {
        this.selectedIndex = 0;
    }
    
    /**
     * Navigue vers l'option précédente (haut)
     */
    public void navigateUp() {
        selectedIndex = (selectedIndex - 1 + PAUSE_OPTIONS.length) % PAUSE_OPTIONS.length;
        // Jouer le son de navigation si disponible
        SoundManager.playEffect("menu_cursor");
        System.out.println("Menu pause - Navigation vers le haut : " + PAUSE_OPTIONS[selectedIndex]);
    }
    
    /**
     * Navigue vers l'option suivante (bas)
     */
    public void navigateDown() {
        selectedIndex = (selectedIndex + 1) % PAUSE_OPTIONS.length;
        // Jouer le son de navigation si disponible
        SoundManager.playEffect("menu_cursor");
        System.out.println("Menu pause - Navigation vers le bas : " + PAUSE_OPTIONS[selectedIndex]);
    }
    
    /**
     * Retourne l'action correspondant à l'option sélectionnée
     * @return L'action à exécuter
     */
    public PauseAction getSelectedAction() {
        switch (selectedIndex) {
            case 0:
                return PauseAction.RESUME;
            case 1:
                return PauseAction.RESTART;
            case 2:
                return PauseAction.COMMANDS;
            case 3:
                return PauseAction.MAIN_MENU;
            default:
                return PauseAction.RESUME;
        }
    }
    
    /**
     * Retourne l'index de l'option sélectionnée
     * @return Index de l'option sélectionnée
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    /**
     * Retourne les options du menu
     * @return Tableau des options du menu
     */
    public String[] getOptions() {
        return PAUSE_OPTIONS.clone();
    }
    
    /**
     * Remet à zéro la sélection (première option)
     */
    public void reset() {
        selectedIndex = 0;
    }
    
    /**
     * Énumération des actions possibles du menu pause
     */
    public enum PauseAction {
        RESUME,     // Reprendre la partie
        RESTART,    // Recommencer la partie
        COMMANDS,   // Afficher les commandes
        MAIN_MENU   // Retour au menu principal
    }
} 