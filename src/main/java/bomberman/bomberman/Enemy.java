package bomberman.bomberman;

/**
 * Classe représentant un ennemi dans le jeu Bomberman.
 * L'ennemi se déplace automatiquement selon une IA simple :
 * - Mouvement toutes les 500ms
 * - Direction persistante jusqu'à rencontrer un obstacle
 * - Changement de direction aléatoire quand bloqué
 */
public class Enemy {
    
    // Position de l'ennemi sur la grille
    private int x;
    private int y;
    
    // Direction actuelle de déplacement
    private Direction currentDirection;
    
    // État de l'ennemi
    private boolean isAlive;
    
    // Système d'invincibilité temporaire (pour les ennemis qui viennent de respawn)
    private boolean isInvincible;
    private long invincibilityStartTime;
    private static final long INVINCIBILITY_DURATION = 5000; // 5 secondes d'invincibilité
    
    // Timer pour les déplacements (500ms entre chaque mouvement)
    private static final long MOVE_INTERVAL = 500; // millisecondes
    private long lastMoveTime;
    
    /**
     * Énumération des directions possibles
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    
    /**
     * Constructeur de l'ennemi
     * @param startX Position initiale en colonne
     * @param startY Position initiale en ligne
     */
    public Enemy(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.isAlive = true;
        this.currentDirection = getRandomDirection();
        this.lastMoveTime = System.currentTimeMillis();
        
        // Pas d'invincibilité par défaut (pour les ennemis créés normalement au début du niveau)
        this.isInvincible = false;
        this.invincibilityStartTime = 0;
    }
    
    /**
     * Constructeur de l'ennemi avec invincibilité
     * @param startX Position initiale en colonne
     * @param startY Position initiale en ligne
     * @param withInvincibility true si l'ennemi doit avoir une invincibilité temporaire
     */
    public Enemy(int startX, int startY, boolean withInvincibility) {
        this(startX, startY); // Appeler le constructeur principal
        
        if (withInvincibility) {
            activateInvincibility();
        }
    }
    
    /**
     * Active l'invincibilité temporaire de l'ennemi
     */
    public void activateInvincibility() {
        this.isInvincible = true;
        this.invincibilityStartTime = System.currentTimeMillis();
        System.out.println("Ennemi spawn avec invincibilité (5s) à (" + x + ", " + y + ")");
    }
    
    /**
     * Met à jour l'ennemi (déplacement selon le timer et gestion de l'invincibilité)
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @param enemyCollisionChecker Interface pour vérifier les collisions avec d'autres ennemis
     * @return true si l'ennemi a bougé, false sinon
     */
    public boolean update(Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        if (!isAlive) {
            return false;
        }
        
        // Mettre à jour l'invincibilité
        updateInvincibility();
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime >= MOVE_INTERVAL) {
            lastMoveTime = currentTime;
            return move(grid, bombCollisionChecker, enemyCollisionChecker);
        }
        
        return false;
    }
    
    /**
     * Met à jour l'état d'invincibilité de l'ennemi
     */
    private void updateInvincibility() {
        if (isInvincible) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - invincibilityStartTime >= INVINCIBILITY_DURATION) {
                isInvincible = false;
                System.out.println("Invincibilité terminée pour l'ennemi à (" + x + ", " + y + ")");
            }
        }
    }
    
    /**
     * Tente de déplacer l'ennemi dans sa direction actuelle
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @param enemyCollisionChecker Interface pour vérifier les collisions avec d'autres ennemis
     * @return true si l'ennemi a bougé, false sinon
     */
    private boolean move(Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        int newX = x;
        int newY = y;
        
        // Calculer la nouvelle position selon la direction
        switch (currentDirection) {
            case UP:
                newY = y - 1;
                break;
            case DOWN:
                newY = y + 1;
                break;
            case LEFT:
                newX = x - 1;
                break;
            case RIGHT:
                newX = x + 1;
                break;
        }
        
        // Vérifier si la nouvelle position est accessible ET pas bloquée par une bombe OU un autre ennemi
        if (grid.isAccessible(newX, newY) && 
            !bombCollisionChecker.isBombBlockingMovement(newX, newY, false) &&
            !enemyCollisionChecker.isEnemyAt(newX, newY, this)) {
            this.x = newX;
            this.y = newY;
            return true;
        } else {
            // Changer de direction si bloqué
            updateDirection();
            return false;
        }
    }
    
    /**
     * Change la direction actuelle de l'ennemi
     * Utilise une approche simple : direction aléatoire
     */
    private void updateDirection() {
        currentDirection = getRandomDirection();
    }
    
    /**
     * Génère une direction aléatoire
     * @return Une direction aléatoire
     */
    private Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        int randomIndex = (int) (Math.random() * directions.length);
        return directions[randomIndex];
    }
    
    /**
     * Tue l'ennemi
     */
    public void kill() {
        this.isAlive = false;
    }
    
    /**
     * @return true si l'ennemi est vivant
     */
    public boolean isAlive() {
        return isAlive;
    }
    
    /**
     * @return Position actuelle en colonne (x)
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return Position actuelle en ligne (y)
     */
    public int getY() {
        return y;
    }
    
    /**
     * @return Direction actuelle de l'ennemi
     */
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    /**
     * Définit une nouvelle position pour l'ennemi
     * @param x Nouvelle position en colonne
     * @param y Nouvelle position en ligne
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return L'intervalle de temps entre les mouvements en ms
     */
    public static long getMoveInterval() {
        return MOVE_INTERVAL;
    }
    
    /**
     * @return true si l'ennemi est invincible (temporairement)
     */
    public boolean isInvincible() {
        return isInvincible;
    }
    
    /**
     * ✨ **NOUVEAU** : Interface fonctionnelle pour vérifier les collisions avec les bombes
     * Permet à Enemy de vérifier les bombes sans dépendre directement de Launcher
     */
    @FunctionalInterface
    public interface BombCollisionChecker {
        boolean isBombBlockingMovement(int x, int y, boolean isPlayer);
    }
    
    /**
     * ✨ **NOUVEAU** : Interface fonctionnelle pour vérifier les collisions avec d'autres ennemis
     * Empêche le chevauchement entre ennemis - cette vérification ne s'applique qu'aux ennemis (pas au joueur)
     */
    @FunctionalInterface
    public interface EnemyCollisionChecker {
        /**
         * Vérifie s'il y a un ennemi vivant à la position donnée (excluant l'ennemi qui demande)
         * @param x Position X à vérifier
         * @param y Position Y à vérifier  
         * @param excludeEnemy L'ennemi à exclure de la vérification (celui qui se déplace)
         * @return true si un autre ennemi vivant occupe cette position
         */
        boolean isEnemyAt(int x, int y, Enemy excludeEnemy);
    }
} 