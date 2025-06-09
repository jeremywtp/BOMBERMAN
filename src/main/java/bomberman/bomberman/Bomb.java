package bomberman.bomberman;

/**
 * Classe représentant une bombe dans le jeu Bomberman.
 * Gère la position, le timer d'explosion et l'état de la bombe.
 * Une bombe explose après 2 secondes et sa portée est de 1 case dans chaque direction.
 * 
 * ✨ **NOUVEAU** : Système de blocage intelligent :
 * - Une bombe bloque les mouvements comme un mur solide
 * - Exception : le joueur qui vient de la poser peut la traverser temporairement
 * - Dès que le joueur sort de la case, la bombe devient solide pour tout le monde
 */
public class Bomb {
    
    // Position de la bombe sur la grille
    private final int x;  // Colonne
    private final int y;  // Ligne
    
    // État de la bombe
    private boolean isActive;
    private boolean hasExploded;
    
    // Timer pour l'explosion (en millisecondes)
    private static final long EXPLOSION_DELAY = 2000; // 2 secondes
    private final long startTime;
    
    // Portée de l'explosion
    private static final int EXPLOSION_RANGE = 1;
    
    // ✨ **NOUVEAU** : Système de traversabilité temporaire
    private boolean isPlayerStillOnBomb;  // True si le joueur qui l'a posée est encore dessus
    private boolean canPlayerTraverse;    // True si le joueur peut encore la traverser
    
    /**
     * Constructeur de la bombe
     * @param x Position en colonne
     * @param y Position en ligne
     * @param placedByPlayer True si la bombe est posée par le joueur (et donc initialement traversable)
     */
    public Bomb(int x, int y, boolean placedByPlayer) {
        this.x = x;
        this.y = y;
        this.isActive = true;
        this.hasExploded = false;
        this.startTime = System.currentTimeMillis();
        
        // La logique de traversabilité ne s'applique qu'aux bombes du joueur
        if (placedByPlayer) {
        this.isPlayerStillOnBomb = true;
        this.canPlayerTraverse = true;
        } else {
            this.isPlayerStillOnBomb = false;
            this.canPlayerTraverse = false; // Les autres bombes (ex: Bomb Rain) sont solides immédiatement
        }
    }
    
    /**
     * Constructeur par défaut pour les bombes posées par le joueur.
     */
    public Bomb(int x, int y) {
        this(x, y, true);
    }
    
    /**
     * Met à jour l'état de la bombe
     * @return true si la bombe a explosé ce tick, false sinon
     */
    public boolean update() {
        if (!isActive || hasExploded) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= EXPLOSION_DELAY) {
            hasExploded = true;
            return true; // Explosion déclenchée
        }
        
        return false;
    }
    
    /**
     * ✨ **MODIFIÉ** : Met à jour l'état de traversabilité en se basant sur la position pixel-perfect du joueur.
     * La bombe devient solide uniquement quand la hitbox du joueur a complètement quitté la case.
     * @param player Le joueur qui a potentiellement posé la bombe.
     */
    public void updateTraversability(FluidMovementPlayer player) {
        // Cette logique ne s'applique que si le joueur n'a pas encore quitté la bombe.
        if (isPlayerStillOnBomb) {
            // Définir la hitbox du joueur
            double hitboxRadius = (FluidMovementPlayer.CELL_SIZE / 2.0) - 4; // Rayon de 20px
            double playerLeft = player.getPixelX() - hitboxRadius;
            double playerRight = player.getPixelX() + hitboxRadius;
            double playerTop = player.getPixelY() - hitboxRadius;
            double playerBottom = player.getPixelY() + hitboxRadius;

            // Définir les limites de la case de la bombe
            double bombTileLeft = this.x * FluidMovementPlayer.CELL_SIZE;
            double bombTileRight = (this.x + 1) * FluidMovementPlayer.CELL_SIZE;
            double bombTileTop = this.y * FluidMovementPlayer.CELL_SIZE;
            double bombTileBottom = (this.y + 1) * FluidMovementPlayer.CELL_SIZE;

            // Vérifier si la hitbox du joueur et la case de la bombe ne se chevauchent plus
            boolean noOverlap = playerRight <= bombTileLeft || playerLeft >= bombTileRight ||
                                playerBottom <= bombTileTop || playerTop >= bombTileBottom;
            
            if (noOverlap) {
            isPlayerStillOnBomb = false;
            canPlayerTraverse = false;
                System.out.println("Bombe à (" + x + ", " + y + ") devient solide - Hitbox du joueur a quitté la case.");
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Vérifie si cette bombe bloque le mouvement pour une entité donnée
     * @param entityX Position X de l'entité qui veut se déplacer
     * @param entityY Position Y de l'entité qui veut se déplacer
     * @param isPlayer True si l'entité est le joueur, false sinon
     * @return true si la bombe bloque le mouvement, false si elle est traversable
     */
    public boolean blocksMovementFor(int entityX, int entityY, boolean isPlayer) {
        // Si l'entité n'est pas sur cette bombe, pas de blocage
        if (entityX != x || entityY != y) {
            return false;
        }
        
        // Si c'est le joueur et qu'il peut encore traverser, pas de blocage
        if (isPlayer && canPlayerTraverse) {
            return false;
        }
        
        // Sinon, la bombe bloque le mouvement
        return true;
    }
    
    /**
     * Désactive la bombe (fin de vie)
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * @return true si la bombe est encore active (visible)
     */
    public boolean isActive() {
        return isActive && !hasExploded;
    }
    
    /**
     * @return true si la bombe a explosé
     */
    public boolean hasExploded() {
        return hasExploded;
    }
    
    /**
     * ✨ **NOUVEAU** : Vérifie si le joueur peut encore traverser cette bombe
     * @return true si le joueur peut traverser, false sinon
     */
    public boolean canPlayerTraverse() {
        return canPlayerTraverse;
    }
    
    /**
     * ✨ **NOUVEAU** : Vérifie si le joueur est encore considéré comme étant sur cette bombe
     * @return true si le joueur est encore sur la bombe, false sinon
     */
    public boolean isPlayerStillOnBomb() {
        return isPlayerStillOnBomb;
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
     * @return La portée de l'explosion
     */
    public static int getExplosionRange() {
        return EXPLOSION_RANGE;
    }
    
    /**
     * @return Le délai avant explosion en millisecondes
     */
    public static long getExplosionDelay() {
        return EXPLOSION_DELAY;
    }
} 