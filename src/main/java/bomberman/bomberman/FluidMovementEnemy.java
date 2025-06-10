package bomberman.bomberman;

/**
 * Extension d'Enemy avec mouvement fluide pixel par pixel
 * Implémente les mécaniques de mouvement de Super Bomberman pour les ennemis :
 * - Déplacement continu selon l'IA
 * - Position en coordonnées flottantes (pixels)
 * - Vitesse en pixels par seconde
 * - Collisions en temps réel
 * - Animation synchronisée avec le mouvement
 */
public class FluidMovementEnemy extends Enemy {
    
    // Constantes de mouvement fluide
    public static final int CELL_SIZE = 48; // Taille d'une case en pixels
    private static final double BASE_SPEED_PIXELS_PER_SECOND = 90.0; // Vitesse réduite de 25% (était 120, maintenant 90) - Plus lent et mieux équilibré
    
    // Position en pixels (coordonnées flottantes)
    private double pixelX;
    private double pixelY;
    
    // Direction de mouvement actuelle
    private double moveDirectionX; // -1, 0, ou 1
    private double moveDirectionY; // -1, 0, ou 1
    
    // Temps de la dernière mise à jour pour calculer le delta
    private long lastUpdateTime;
    
    // Vitesse effective en pixels par seconde
    private double effectiveSpeedPixelsPerSecond;
    
    // Système d'animation pour les sprites
    private EnemyAnimator animator;
    
    // Timer pour la fréquence de changement de direction
    private long lastDirectionChangeTime;
    private static final long DIRECTION_CHANGE_INTERVAL = 4000; // Change de direction toutes les 4 secondes (au lieu de 2)
    
    // Compteur pour les tentatives de déblocage
    private int blockedAttempts;
    private static final int MAX_BLOCKED_ATTEMPTS = 8; // Essayer 8 fois avant de changer de direction
    
    /**
     * Constructeur de l'ennemi avec mouvement fluide
     * @param startX Position initiale en colonne (grille)
     * @param startY Position initiale en ligne (grille)
     */
    public FluidMovementEnemy(int startX, int startY) {
        super(startX, startY);
        
        // Initialiser l'état de mouvement
        this.effectiveSpeedPixelsPerSecond = BASE_SPEED_PIXELS_PER_SECOND;
        this.lastUpdateTime = System.currentTimeMillis();
        this.lastDirectionChangeTime = System.currentTimeMillis();
        
        // Initialiser la position en pixels (centré dans la case de départ)
        setPixelPosition(startX * CELL_SIZE + (CELL_SIZE / 2.0), startY * CELL_SIZE + (CELL_SIZE / 2.0));
        
        // Initialiser l'animation
        this.animator = new EnemyAnimator();
        
        // Initialiser l'IA
        this.blockedAttempts = 0;
        chooseRandomDirection(); // Direction initiale aléatoire
    }
    
    /**
     * Constructeur avec invincibilité
     * @param startX Position initiale en colonne
     * @param startY Position initiale en ligne
     * @param withInvincibility true si l'ennemi doit avoir une invincibilité temporaire
     */
    public FluidMovementEnemy(int startX, int startY, boolean withInvincibility) {
        this(startX, startY);
        
        if (withInvincibility) {
            activateInvincibility();
        }
    }
    
    /**
     * Met à jour l'ennemi avec mouvement fluide
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @param enemyCollisionChecker Interface pour vérifier les collisions avec d'autres ennemis
     * @return true si l'ennemi a bougé
     */
    @Override
    public boolean update(Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        // Skip l'update si l'ennemi est mort
        if (!isAlive()) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Mettre à jour l'invincibilité (du parent)
        if (isInvincible()) {
            long invincibilityStartTime = getInvincibilityStartTime();
            if (currentTime - invincibilityStartTime >= getInvincibilityDuration()) {
                deactivateInvincibility();
            }
        }
        
        // Changer de direction périodiquement pour l'IA (seulement toutes les 4 secondes)
        if (currentTime - lastDirectionChangeTime >= DIRECTION_CHANGE_INTERVAL) {
            chooseRandomDirection();
            lastDirectionChangeTime = currentTime;
            blockedAttempts = 0; // Reset du compteur de blocage
        }
        
        // Mettre à jour le mouvement fluide
        return updateMovement(grid, bombCollisionChecker, enemyCollisionChecker);
    }
    
    /**
     * Met à jour la position de l'ennemi selon sa direction et sa vitesse
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @param enemyCollisionChecker Interface pour vérifier les collisions avec d'autres ennemis
     * @return true si l'ennemi a bougé
     */
    private boolean updateMovement(Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0; // Delta en secondes
        this.lastUpdateTime = currentTime;
        
        // Si pas de direction de mouvement, arrêter l'animation
        if (moveDirectionX == 0 && moveDirectionY == 0) {
            if (animator.isWalking()) {
                animator.stopWalking();
            }
            return false;
        }
        
        // Calculer le déplacement en pixels pour cette frame
        double pixelMovement = effectiveSpeedPixelsPerSecond * deltaTime;
        
        // Limiter le déplacement maximum par frame pour éviter de "sauter" par-dessus les collisions
        double maxMovementPerFrame = CELL_SIZE / 4.0; // Maximum 12 pixels par frame
        pixelMovement = Math.min(pixelMovement, maxMovementPerFrame);
        
        // Calculer la nouvelle position
        double newPixelX = pixelX + (moveDirectionX * pixelMovement);
        double newPixelY = pixelY + (moveDirectionY * pixelMovement);
        
        // Vérifier les collisions et ajuster la position
        newPixelX = checkCollisionX(newPixelX, newPixelY, grid, bombCollisionChecker, enemyCollisionChecker);
        newPixelY = checkCollisionY(newPixelX, newPixelY, grid, bombCollisionChecker, enemyCollisionChecker);
        
        // Vérifier si le mouvement est bloqué
        boolean isBlockedX = (newPixelX == pixelX && moveDirectionX != 0);
        boolean isBlockedY = (newPixelY == pixelY && moveDirectionY != 0);
        
        if (isBlockedX || isBlockedY) {
            // Incrémenter le compteur de tentatives bloquées
            blockedAttempts++;
            
            // Ne changer de direction qu'après plusieurs tentatives
            if (blockedAttempts >= MAX_BLOCKED_ATTEMPTS) {
                // Changer de direction si bloqué trop longtemps
                chooseRandomDirection();
                blockedAttempts = 0; // Reset du compteur
                lastDirectionChangeTime = currentTime; // Reset du timer pour éviter un changement immédiat
            }
            
            // Arrêter l'animation quand bloqué
            if (animator.isWalking()) {
                animator.stopWalking();
            }
            return false;
        }
        
        // Reset du compteur si le mouvement n'est pas bloqué
        blockedAttempts = 0;
        
        // Appliquer le mouvement si il y a eu un changement
        boolean hasMoved = (newPixelX != pixelX || newPixelY != pixelY);
        if (hasMoved) {
            this.pixelX = newPixelX;
            this.pixelY = newPixelY;
            
            // Mettre à jour les coordonnées de grille du parent
            updateGridPosition();
            
            // Déclencher l'animation
            if (!animator.isWalking()) {
                animator.startWalking();
            }
            
            // Mettre à jour la direction du sprite
            updateSpriteDirection();
        }
        
        return hasMoved;
    }
    
    /**
     * Vérifie la collision horizontale et retourne la position X ajustée
     */
    private double checkCollisionX(double newPixelX, double pixelY, Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        // Calculer les limites de la hitbox
        double hitboxSize = CELL_SIZE * 0.92; // Hitbox légèrement agrandie pour un meilleur centrage visuel
        double halfHitbox = hitboxSize / 2.0;
        
        double leftEdge = newPixelX - halfHitbox;
        double rightEdge = newPixelX + halfHitbox;
        double topEdge = pixelY - halfHitbox;
        double bottomEdge = pixelY + halfHitbox;
        
        // Convertir en coordonnées de grille
        int leftCell = pixelToGrid(leftEdge);
        int rightCell = pixelToGrid(rightEdge);
        int topCell = pixelToGrid(topEdge);
        int bottomCell = pixelToGrid(bottomEdge);
        
        // Vérifier toutes les cellules touchées par la hitbox
        for (int cellY = topCell; cellY <= bottomCell; cellY++) {
            for (int cellX = leftCell; cellX <= rightCell; cellX++) {
                if (!isValidPosition(cellX, cellY, grid, bombCollisionChecker, enemyCollisionChecker)) {
                    return pixelX; // Mouvement bloqué, garder la position actuelle
                }
            }
        }
        
        return newPixelX; // Mouvement autorisé
    }
    
    /**
     * Vérifie la collision verticale et retourne la position Y ajustée
     */
    private double checkCollisionY(double pixelX, double newPixelY, Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        // Calculer les limites de la hitbox
        double hitboxSize = CELL_SIZE * 0.92; // Hitbox légèrement agrandie pour un meilleur centrage visuel
        double halfHitbox = hitboxSize / 2.0;
        
        double leftEdge = pixelX - halfHitbox;
        double rightEdge = pixelX + halfHitbox;
        double topEdge = newPixelY - halfHitbox;
        double bottomEdge = newPixelY + halfHitbox;
        
        // Convertir en coordonnées de grille
        int leftCell = pixelToGrid(leftEdge);
        int rightCell = pixelToGrid(rightEdge);
        int topCell = pixelToGrid(topEdge);
        int bottomCell = pixelToGrid(bottomEdge);
        
        // Vérifier toutes les cellules touchées par la hitbox
        for (int cellY = topCell; cellY <= bottomCell; cellY++) {
            for (int cellX = leftCell; cellX <= rightCell; cellX++) {
                if (!isValidPosition(cellX, cellY, grid, bombCollisionChecker, enemyCollisionChecker)) {
                    return pixelY; // Mouvement bloqué, garder la position actuelle
                }
            }
        }
        
        return newPixelY; // Mouvement autorisé
    }
    
    /**
     * Vérifie si une position est valide (pas de mur, bombe, ou autre ennemi)
     */
    private boolean isValidPosition(int cellX, int cellY, Grid grid, BombCollisionChecker bombCollisionChecker, EnemyCollisionChecker enemyCollisionChecker) {
        // Vérifier les limites de la grille
        if (cellX < 0 || cellY < 0 || cellX >= grid.getColumns() || cellY >= grid.getRows()) {
            return false;
        }
        
        // Vérifier les murs et blocs
        if (!grid.isAccessible(cellX, cellY)) {
            return false;
        }
        
        // Vérifier les bombes
        if (bombCollisionChecker.isBombBlockingMovement(cellX, cellY, false)) {
            return false;
        }
        
        // Vérifier les autres ennemis
        if (enemyCollisionChecker.isEnemyAt(cellX, cellY, this)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Met à jour les coordonnées de grille selon la position en pixels
     */
    private void updateGridPosition() {
        int newGridX = pixelToGrid(pixelX);
        int newGridY = pixelToGrid(pixelY);
        setPosition(newGridX, newGridY);
    }
    
    /**
     * Choisit une direction aléatoire pour l'IA
     */
    private void chooseRandomDirection() {
        Direction[] directions = Direction.values();
        Direction newDirection = directions[(int) (Math.random() * directions.length)];
        setDirection(newDirection);
    }
    
    /**
     * Définit la direction de mouvement
     * @param direction La nouvelle direction
     */
    private void setDirection(Direction direction) {
        switch (direction) {
            case UP:
                moveDirectionX = 0;
                moveDirectionY = -1;
                break;
            case DOWN:
                moveDirectionX = 0;
                moveDirectionY = 1;
                break;
            case LEFT:
                moveDirectionX = -1;
                moveDirectionY = 0;
                break;
            case RIGHT:
                moveDirectionX = 1;
                moveDirectionY = 0;
                break;
        }
        
        // Mettre à jour la direction actuelle dans le parent
        setCurrentDirection(direction);
    }
    
    /**
     * Met à jour la direction du sprite selon le mouvement
     */
    private void updateSpriteDirection() {
        String spriteDirection;
        
        if (moveDirectionY < 0) {
            spriteDirection = "haut";
        } else if (moveDirectionY > 0) {
            spriteDirection = "bas";
        } else if (moveDirectionX < 0) {
            spriteDirection = "gauche";
        } else if (moveDirectionX > 0) {
            spriteDirection = "droite";
        } else {
            spriteDirection = "bas"; // Direction par défaut
        }
        
        animator.setDirection(spriteDirection);
    }
    
    /**
     * @return Position X en pixels
     */
    public double getPixelX() {
        return pixelX;
    }
    
    /**
     * @return Position Y en pixels
     */
    public double getPixelY() {
        return pixelY;
    }
    
    /**
     * @return Position X pour le rendu (centrée)
     */
    public double getRenderX() {
        return pixelX;
    }
    
    /**
     * @return Position Y pour le rendu (centrée)
     */
    public double getRenderY() {
        return pixelY;
    }
    
    /**
     * @return true si l'ennemi est en mouvement
     */
    public boolean isMoving() {
        return moveDirectionX != 0 || moveDirectionY != 0;
    }
    
    /**
     * @return L'animateur de sprites
     */
    public EnemyAnimator getAnimator() {
        return animator;
    }
    
    /**
     * Définit la position en pixels
     * @param pixelX Position X en pixels
     * @param pixelY Position Y en pixels
     */
    public void setPixelPosition(double pixelX, double pixelY) {
        // Vérifier les limites de la grille
        double maxX = (CELL_SIZE * 15) - CELL_SIZE; // 15 colonnes
        double maxY = (CELL_SIZE * 13) - CELL_SIZE; // 13 lignes
        
        this.pixelX = Math.max(CELL_SIZE / 2.0, Math.min(pixelX, maxX));
        this.pixelY = Math.max(CELL_SIZE / 2.0, Math.min(pixelY, maxY));
        
        // Mettre à jour les coordonnées de grille
        updateGridPosition();
        
        // Mettre à jour la position de l'animateur
        if (animator != null) {
            animator.setPixelPosition(this.pixelX, this.pixelY, 0, 0);
        }
    }
    
    /**
     * Convertit une coordonnée de grille en pixels (centre de la cellule)
     * @param gridCoord Coordonnée de grille
     * @return Position en pixels
     */
    public static double gridToPixel(int gridCoord) {
        return gridCoord * CELL_SIZE + (CELL_SIZE / 2.0);
    }
    
    /**
     * Convertit une coordonnée en pixels en coordonnée de grille
     * @param pixelCoord Coordonnée en pixels
     * @return Coordonnée de grille
     */
    public static int pixelToGrid(double pixelCoord) {
        return (int) (pixelCoord / CELL_SIZE);
    }
    
    @Override
    public void kill() {
        super.kill();
        if (animator != null) {
            animator.stopWalking();
            animator.dispose();
        }
    }
    
    // Les méthodes pour accéder aux propriétés du parent sont maintenant disponibles via les méthodes protégées
}