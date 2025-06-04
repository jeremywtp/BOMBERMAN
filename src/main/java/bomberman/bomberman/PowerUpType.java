package bomberman.bomberman;

/**
 * Énumération des différents types de power-ups disponibles dans le jeu.
 * Chaque type de power-up confère un bonus spécifique au joueur.
 */
public enum PowerUpType {
    
    /**
     * Power-up qui permet au joueur de poser une bombe supplémentaire
     */
    EXTRA_BOMB("Extra Bomb", "Permet de poser une bombe supplémentaire"),
    
    /**
     * Power-up qui augmente la portée d'explosion des bombes de +1
     */
    RANGE_UP("Range Up", "Augmente la portée d'explosion de +1"),
    
    /**
     * Power-up qui augmente la vitesse de déplacement du joueur
     */
    SPEED_UP("Speed Up", "Augmente la vitesse de déplacement");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructeur de l'énumération
     * @param displayName Nom affiché du power-up
     * @param description Description de l'effet du power-up
     */
    PowerUpType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Applique l'effet du power-up au joueur
     * @param player Le joueur qui récupère le power-up
     */
    public void applyEffect(Player player) {
        switch (this) {
            case EXTRA_BOMB:
                player.increaseMaxBombs();
                break;
            case RANGE_UP:
                player.increaseRange();
                break;
            case SPEED_UP:
                player.increaseSpeed();
                break;
        }
    }
    
    /**
     * @return Le nom affiché du power-up
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @return La description de l'effet du power-up
     */
    public String getDescription() {
        return description;
    }
} 