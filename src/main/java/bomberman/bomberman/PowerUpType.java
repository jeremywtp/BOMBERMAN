package bomberman.bomberman;

/**
 * Énumération des différents types de power-ups disponibles dans le jeu.
 * Inclut des power-ups permanents et temporaires avec leurs effets respectifs.
 */
public enum PowerUpType {
    
    /**
     * Power-up permanent : Augmente le nombre maximum de bombes de +1
     */
    EXTRA_BOMB("Extra Bomb", true, 0),
    
    /**
     * Power-up permanent : Augmente la portée d'explosion de +1
     */
    EXPLOSION_EXPANDER("Explosion Expander", true, 0);
    
    private final String displayName;
    private final boolean isPermanent;
    private final long duration; // Durée en millisecondes (0 pour permanent ou instantané)
    
    /**
     * Constructeur de l'énumération
     * @param displayName Nom affiché du power-up
     * @param isPermanent true si le power-up est permanent
     * @param duration Durée en millisecondes (0 pour permanent ou instantané)
     */
    PowerUpType(String displayName, boolean isPermanent, long duration) {
        this.displayName = displayName;
        this.isPermanent = isPermanent;
        this.duration = duration;
    }
    
    /**
     * @return Le nom affiché du power-up
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @return true si l'effet est permanent
     */
    public boolean isPermanent() {
        return isPermanent;
    }
    
    /**
     * @return Durée de l'effet en millisecondes (0 si permanent ou instantané)
     */
    public long getDuration() {
        return duration;
    }
    
    /**
     * Applique l'effet du power-up au joueur
     * @param player Le joueur qui reçoit l'effet
     */
    public void applyEffect(Player player) {
        switch (this) {
            case EXTRA_BOMB:
                player.increaseMaxBombs();
                break;
            case EXPLOSION_EXPANDER:
                player.increaseRange();
                break;
        }
        
        System.out.println("Power-up récupéré : " + displayName + " à la position (" + player.getX() + ", " + player.getY() + ")");
    }
} 