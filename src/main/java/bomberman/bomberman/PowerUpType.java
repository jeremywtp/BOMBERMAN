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
    RANGE_UP("Range Up", true, 0),
    
    /**
     * Power-up permanent : Augmente la vitesse de déplacement de +0.5
     */
    SPEED_UP("Speed Up", true, 0),
    
    /**
     * Power-up temporaire : Bouclier protégeant des explosions (10 secondes)
     */
    SHIELD("Shield", false, 10000),
    
    /**
     * Power-up temporaire : Double la vitesse de déplacement (5 secondes)
     */
    SPEED_BURST("Speed Burst", false, 5000),
    
    /**
     * Power-up temporaire : Pose automatiquement 5 bombes (effet instantané)
     */
    BOMB_RAIN("Bomb Rain", false, 0);
    
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
            case RANGE_UP:
                player.increaseRange();
                break;
            case SPEED_UP:
                player.increaseSpeed();
                break;
            case SHIELD:
                player.activateShield(duration);
                break;
            case SPEED_BURST:
                player.activateSpeedBurst(duration);
                break;
            case BOMB_RAIN:
                player.activateBombRain();
                break;
        }
        
        System.out.println("Power-up récupéré : " + displayName + " à la position (" + player.getX() + ", " + player.getY() + ")");
    }
} 