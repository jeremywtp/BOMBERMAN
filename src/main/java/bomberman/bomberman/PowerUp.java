package bomberman.bomberman;

/**
 * Classe représentant un power-up dans le jeu Bomberman.
 * Les power-ups sont cachés dans les blocs destructibles et deviennent visibles
 * lorsque le bloc est détruit. Le joueur peut les ramasser pour obtenir des bonus.
 */
public class PowerUp {
    
    // Position du power-up sur la grille
    private final int x;
    private final int y;
    
    // Type de power-up (détermine l'effet)
    private final PowerUpType type;
    
    // État de visibilité du power-up
    private boolean visible;
    
    // État de récupération du power-up
    private boolean collected;
    
    /**
     * Constructeur du power-up
     * @param x Position en colonne
     * @param y Position en ligne
     * @param type Type de power-up
     */
    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.visible = false;  // Caché par défaut
        this.collected = false;
    }
    
    /**
     * Révèle le power-up (le rend visible)
     * Appelé lorsque le bloc destructible qui le contenait est détruit
     */
    public void reveal() {
        this.visible = true;
    }
    
    /**
     * Applique l'effet du power-up au joueur et marque le power-up comme récupéré
     * @param player Le joueur qui récupère le power-up
     */
    public void applyEffect(Player player) {
        if (!collected && visible) {
            type.applyEffect(player);
            collected = true;
            visible = false;  // Le power-up disparaît après récupération
            
            System.out.println("Power-up récupéré : " + type.getDisplayName() + 
                              " à la position (" + x + ", " + y + ")");
        }
    }
    
    /**
     * Vérifie si le power-up est visible et peut être récupéré
     * @return true si le power-up est visible et non récupéré
     */
    public boolean isVisible() {
        return visible && !collected;
    }
    
    /**
     * Vérifie si le power-up a été récupéré
     * @return true si le power-up a été récupéré
     */
    public boolean isCollected() {
        return collected;
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
     * @return Type du power-up
     */
    public PowerUpType getType() {
        return type;
    }
    
    /**
     * Vérifie si le power-up se trouve à la position donnée
     * @param checkX Position en colonne à vérifier
     * @param checkY Position en ligne à vérifier
     * @return true si le power-up est à cette position
     */
    public boolean isAtPosition(int checkX, int checkY) {
        return this.x == checkX && this.y == checkY;
    }
    
    /**
     * @return Une représentation textuelle du power-up pour le debug
     */
    @Override
    public String toString() {
        return "PowerUp{" +
                "type=" + type +
                ", position=(" + x + ", " + y + ")" +
                ", visible=" + visible +
                ", collected=" + collected +
                '}';
    }
} 