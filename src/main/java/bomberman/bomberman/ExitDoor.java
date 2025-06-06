package bomberman.bomberman;

/**
 * Représente une porte de sortie pour terminer le niveau.
 * La porte est initialement cachée dans un bloc destructible.
 * Elle devient visible quand le bloc est détruit,
 * et utilisable seulement quand tous les ennemis sont morts.
 */
public class ExitDoor {
    private final int x;  // Position en colonne
    private final int y;  // Position en ligne
    private boolean visible;  // Si la porte est visible ou non
    private boolean activated;  // Si la porte est activée (utilisable)
    
    /**
     * Constructeur de la porte de sortie
     * @param x Position en colonne
     * @param y Position en ligne
     */
    public ExitDoor(int x, int y) {
        this.x = x;
        this.y = y;
        this.visible = false;  // Initialement cachée, sera révélée quand le bloc est détruit
        this.activated = false;  // Initialement désactivée (non utilisable)
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
     * @return true si la porte est visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Rend la porte visible (quand le bloc qui la contient est détruit)
     */
    public void reveal() {
        if (!visible) {
            visible = true;
            System.out.println("Porte de sortie révélée à la position (" + x + ", " + y + ")");
        }
    }
    
    /**
     * Active la porte (quand tous les ennemis sont morts)
     */
    public void activate() {
        if (!activated) {
            activated = true;
            System.out.println("Porte de sortie activée à la position (" + x + ", " + y + ") - Utilisable !");
        }
    }
    
    /**
     * Désactive la porte (par exemple, quand un ennemi va respawn)
     */
    public void deactivate() {
        if (activated) {
            activated = false;
            System.out.println("Porte de sortie désactivée à la position (" + x + ", " + y + ") - En attente...");
        }
    }
    
    /**
     * @return true si la porte est activée (utilisable)
     */
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Vérifie si le joueur est sur la porte
     * @param playerX Position X du joueur
     * @param playerY Position Y du joueur
     * @return true si le joueur est sur la porte
     */
    public boolean isPlayerOnDoor(int playerX, int playerY) {
        return playerX == x && playerY == y;
    }
    
    /**
     * Vérifie si le joueur peut utiliser la porte pour passer au niveau suivant
     * @param playerX Position X du joueur
     * @param playerY Position Y du joueur 
     * @return true si le joueur est sur la porte ET que la porte est activée
     */
    public boolean canUseToExit(int playerX, int playerY) {
        return activated && playerX == x && playerY == y;
    }
} 