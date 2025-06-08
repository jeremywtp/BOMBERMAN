package bomberman.bomberman;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/**
 * Extension de Player avec mouvement fluide pixel par pixel
 * Implémente les mécaniques de mouvement de Super Bomberman :
 * - Déplacement continu tant que la touche est maintenue
 * - Position en coordonnées flottantes (pixels)
 * - Vitesse en pixels par seconde
 * - Collisions en temps réel
 */
public class FluidMovementPlayer extends Player {
    
    // Constantes de mouvement fluide
    public static final int CELL_SIZE = 48; // Taille d'une case en pixels
    private static final double BASE_SPEED_PIXELS_PER_SECOND = 160.0; // Vitesse de base (3.33 cases/sec)
    
    // Position en pixels (coordonnées flottantes)
    private double pixelX;
    private double pixelY;
    
    // État des touches actuellement pressées
    private Set<KeyCode> pressedKeys;
    
    // Direction de mouvement actuelle (peut être différente de currentDirection pour les diagonales)
    private double moveDirectionX; // -1, 0, ou 1
    private double moveDirectionY; // -1, 0, ou 1
    
    // Temps de la dernière mise à jour pour calculer le delta
    private long lastUpdateTime;
    
    // Vitesse effective en pixels par seconde (modifiée par les power-ups)
    private double effectiveSpeedPixelsPerSecond;
    
    /**
     * Constructeur du joueur avec mouvement fluide
     * @param startX Position initiale en colonne (grille)
     * @param startY Position initiale en ligne (grille)
     */
    public FluidMovementPlayer(int startX, int startY) {
        super(startX, startY);
        
        // Initialiser l'état de mouvement AVANT setPixelPosition
        this.pressedKeys = new HashSet<>();
        this.moveDirectionX = 0;
        this.moveDirectionY = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        this.effectiveSpeedPixelsPerSecond = BASE_SPEED_PIXELS_PER_SECOND;
        
        // Initialiser la position en pixels (centré dans la case de départ)
        // ✨ **SÉCURITÉ** : Utiliser setPixelPosition pour vérifier les limites
        setPixelPosition(startX * CELL_SIZE + (CELL_SIZE / 2.0), startY * CELL_SIZE + (CELL_SIZE / 2.0));
    }
    
    /**
     * Met à jour la position du joueur selon les touches pressées et la vitesse
     * À appeler dans la boucle de jeu principale
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     */
    public void updateMovement(Grid grid, BombCollisionChecker bombCollisionChecker) {
        if (!isAlive()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0; // Delta en secondes
        this.lastUpdateTime = currentTime;
        
        // Calculer la direction de mouvement selon les touches pressées
        updateMovementDirection();
        
        // Calculer la vitesse effective (power-ups, effets temporaires)
        updateEffectiveSpeed();
        
        // Si le joueur ne bouge pas, arrêter l'animation
        if (moveDirectionX == 0 && moveDirectionY == 0) {
            if (isWalking()) {
                updateWalkingState(false);
            }
            return;
        }
        
        // Calculer le déplacement en pixels pour cette frame
        double pixelMovement = effectiveSpeedPixelsPerSecond * deltaTime;
        
        // 🛡️ **SÉCURITÉ** : Limiter le déplacement maximum par frame pour éviter de "sauter" par-dessus les collisions
        double maxMovementPerFrame = CELL_SIZE / 4.0; // Maximum 12 pixels par frame (1/4 de case)
        pixelMovement = Math.min(pixelMovement, maxMovementPerFrame);
        
        // Calculer la nouvelle position
        double newPixelX = pixelX + (moveDirectionX * pixelMovement);
        double newPixelY = pixelY + (moveDirectionY * pixelMovement);
        
        // Vérifier les collisions et ajuster la position avec auto-correction pour virages
        newPixelX = checkCollisionX(newPixelX, newPixelY, grid, bombCollisionChecker);
        newPixelY = checkCollisionY(newPixelX, newPixelY, grid, bombCollisionChecker);
        
        // Appliquer le mouvement si il y a eu un changement
        boolean hasMoved = (newPixelX != pixelX || newPixelY != pixelY);
        if (hasMoved) {
            this.pixelX = newPixelX;
            this.pixelY = newPixelY;
            
            // Mettre à jour les coordonnées de grille
            updateGridPosition();
            
            // Déclencher l'animation et les sons
            if (!isWalking()) {
                updateWalkingState(true);
                playMovementSound();
            }
            
            // Mettre à jour la direction du sprite
            updateSpriteDirection();
        }
    }
    
    /**
     * Met à jour la direction de mouvement selon les touches pressées
     */
    private void updateMovementDirection() {
        double newDirectionX = 0;
        double newDirectionY = 0;
        
        // Calculer la direction selon les touches pressées
        if (pressedKeys.contains(KeyCode.LEFT)) {
            newDirectionX -= 1;
        }
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            newDirectionX += 1;
        }
        if (pressedKeys.contains(KeyCode.UP)) {
            newDirectionY -= 1;
        }
        if (pressedKeys.contains(KeyCode.DOWN)) {
            newDirectionY += 1;
        }
        
        // Normaliser pour les mouvements diagonaux (même vitesse dans toutes les directions)
        if (newDirectionX != 0 && newDirectionY != 0) {
            double length = Math.sqrt(newDirectionX * newDirectionX + newDirectionY * newDirectionY);
            newDirectionX /= length;
            newDirectionY /= length;
        }
        
        this.moveDirectionX = newDirectionX;
        this.moveDirectionY = newDirectionY;
    }
    
    /**
     * Met à jour la vitesse effective selon les power-ups et effets temporaires
     */
    private void updateEffectiveSpeed() {
        double speedMultiplier = getSpeed(); // Speed des power-ups (1.0 + 0.5 par SPEED_UP)
        
        // Speed Burst : vitesse maximale
        if (hasSpeedBurst()) {
            speedMultiplier = 3.0; // Vitesse triplee
        }
        
        this.effectiveSpeedPixelsPerSecond = BASE_SPEED_PIXELS_PER_SECOND * speedMultiplier;
    }
    
    /**
     * Met à jour la direction du sprite selon le mouvement
     */
    private void updateSpriteDirection() {
        // Priorité aux mouvements cardinaux pour les sprites
        if (Math.abs(moveDirectionY) > Math.abs(moveDirectionX)) {
            // Mouvement principalement vertical
            if (moveDirectionY < 0) {
                updateCurrentDirection("haut");
            } else {
                updateCurrentDirection("bas");
            }
        } else if (moveDirectionX != 0) {
            // Mouvement principalement horizontal
            if (moveDirectionX < 0) {
                updateCurrentDirection("gauche");
            } else {
                updateCurrentDirection("droite");
            }
        }
    }
    
    /**
     * Vérifie les collisions horizontales et retourne la position X ajustée
     * ✨ **NOUVEAU** : Inclut l'auto-correction pour les virages fluides
     */
    private double checkCollisionX(double newPixelX, double pixelY, Grid grid, BombCollisionChecker bombCollisionChecker) {
        // ✨ **SÉCURITÉ** : Vérifier d'abord les limites strictes de la grille
        double minPixelX = (CELL_SIZE / 2.0); // Centre de la première case
        double maxPixelX = (grid.getColumns() - 1) * CELL_SIZE + (CELL_SIZE / 2.0); // Centre de la dernière case
        
        if (newPixelX < minPixelX) {
            return minPixelX; // Bloquer aux limites gauches
        }
        if (newPixelX > maxPixelX) {
            return maxPixelX; // Bloquer aux limites droites
        }
        
        // Calculer les cases que le joueur occuperait (hitbox authentique Bomberman)
        double hitboxRadius = (CELL_SIZE / 2.0) - 4; // Bomberman plus grand, rayon de 20px (au lieu de 22px)
        int leftCell = (int) ((newPixelX - hitboxRadius) / CELL_SIZE);
        int rightCell = (int) ((newPixelX + hitboxRadius) / CELL_SIZE);
        int topCell = (int) ((pixelY - hitboxRadius) / CELL_SIZE);
        int bottomCell = (int) ((pixelY + hitboxRadius) / CELL_SIZE);
        
        // Sécuriser les indices
        leftCell = Math.max(0, leftCell);
        rightCell = Math.min(grid.getColumns() - 1, rightCell);
        topCell = Math.max(0, topCell);
        bottomCell = Math.min(grid.getRows() - 1, bottomCell);
        
        // Vérifier toutes les cases que le joueur toucherait
        for (int cellY = topCell; cellY <= bottomCell; cellY++) {
            for (int cellX = leftCell; cellX <= rightCell; cellX++) {
                if (!isValidPosition(cellX, cellY, grid, bombCollisionChecker)) {
                    // 🛡️ **COLLISION DÉTECTÉE** : Rester à la position actuelle
                    // Auto-correction supprimée car trop permissive (permettait de passer dans les blocs)
                    System.out.println("Collision X détectée à (" + cellX + ", " + cellY + ") - Position maintenue : " + String.format("%.1f", pixelX));
                    return pixelX;
                }
            }
        }
        
        return newPixelX; // Pas de collision
    }
    
    /**
     * Vérifie les collisions verticales et retourne la position Y ajustée
     */
    private double checkCollisionY(double pixelX, double newPixelY, Grid grid, BombCollisionChecker bombCollisionChecker) {
        // ✨ **SÉCURITÉ** : Vérifier d'abord les limites strictes de la grille
        double minPixelY = (CELL_SIZE / 2.0); // Centre de la première case
        double maxPixelY = (grid.getRows() - 1) * CELL_SIZE + (CELL_SIZE / 2.0); // Centre de la dernière case
        
        if (newPixelY < minPixelY) {
            return minPixelY; // Bloquer aux limites hautes
        }
        if (newPixelY > maxPixelY) {
            return maxPixelY; // Bloquer aux limites basses
        }
        
        // Calculer les cases que le joueur occuperait (hitbox authentique Bomberman)
        double hitboxRadius = (CELL_SIZE / 2.0) - 4; // Bomberman plus grand, rayon de 20px (au lieu de 22px)
        int leftCell = (int) ((pixelX - hitboxRadius) / CELL_SIZE);
        int rightCell = (int) ((pixelX + hitboxRadius) / CELL_SIZE);
        int topCell = (int) ((newPixelY - hitboxRadius) / CELL_SIZE);
        int bottomCell = (int) ((newPixelY + hitboxRadius) / CELL_SIZE);
        
        // Sécuriser les indices
        leftCell = Math.max(0, leftCell);
        rightCell = Math.min(grid.getColumns() - 1, rightCell);
        topCell = Math.max(0, topCell);
        bottomCell = Math.min(grid.getRows() - 1, bottomCell);
        
        // Vérifier toutes les cases que le joueur toucherait
        for (int cellY = topCell; cellY <= bottomCell; cellY++) {
            for (int cellX = leftCell; cellX <= rightCell; cellX++) {
                if (!isValidPosition(cellX, cellY, grid, bombCollisionChecker)) {
                    // 🛡️ **COLLISION DÉTECTÉE** : Rester à la position actuelle
                    // Auto-correction supprimée car trop permissive (permettait de passer dans les blocs)
                    System.out.println("Collision Y détectée à (" + cellX + ", " + cellY + ") - Position maintenue : " + String.format("%.1f", pixelY));
                    return pixelY;
                }
            }
        }
        
        return newPixelY; // Pas de collision
    }
    
    /**
     * ✨ **MODIFIÉ** : Vérifie si la position de destination est valide (pas un mur ou une bombe solide)
     * Autorise le joueur à quitter une case contenant une bombe qu'il vient de poser.
     */
    private boolean isValidPosition(int cellX, int cellY, Grid grid, BombCollisionChecker bombCollisionChecker) {
        // La case est-elle accessible dans la grille (pas un mur) ?
        if (!grid.isAccessible(cellX, cellY)) {
            return false;
        }

        // Y a-t-il une bombe qui bloque le mouvement à cette position ?
        if (bombCollisionChecker.isBombBlockingMovement(cellX, cellY, true)) {
            // Le joueur est-il sur le point de quitter la case de cette bombe ?
            boolean isLeavingBomb = (getX() == cellX && getY() == cellY);

            // Si le joueur n'est PAS en train de quitter la case de la bombe, alors c'est une collision.
            // S'il est en train de la quitter, on l'autorise (return true plus bas).
            if (!isLeavingBomb) {
                return false; // Collision avec une bombe solide
            }
        }

        // La position est valide
        return true;
    }
    
    /**
     * Met à jour les coordonnées de grille basées sur la position en pixels
     */
    private void updateGridPosition() {
        int newGridX = (int) (pixelX / CELL_SIZE);
        int newGridY = (int) (pixelY / CELL_SIZE);
        
        // Mettre à jour les coordonnées de grille héritées
        setPosition(newGridX, newGridY);
    }
    
    /**
     * Gère l'appui d'une touche de mouvement
     * @param keyCode La touche pressée
     */
    public void onKeyPressed(KeyCode keyCode) {
        if (isMovementKey(keyCode)) {
            pressedKeys.add(keyCode);
        }
    }
    
    /**
     * Gère le relâchement d'une touche de mouvement
     * @param keyCode La touche relâchée
     */
    public void onKeyReleased(KeyCode keyCode) {
        if (isMovementKey(keyCode)) {
            pressedKeys.remove(keyCode);
        }
    }
    
    /**
     * Vérifie si une touche est une touche de mouvement
     */
    private boolean isMovementKey(KeyCode keyCode) {
        return keyCode == KeyCode.UP || keyCode == KeyCode.DOWN || 
               keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT;
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
     * @return Position X de rendu (coin supérieur gauche de la hitbox)
     */
    public double getRenderX() {
        return pixelX - (CELL_SIZE / 2.0);
    }
    
    /**
     * @return Position Y de rendu (coin supérieur gauche de la hitbox)
     */
    public double getRenderY() {
        return pixelY - (CELL_SIZE / 2.0);
    }
    
    /**
     * @return True si le joueur est en mouvement
     */
    public boolean isMoving() {
        return moveDirectionX != 0 || moveDirectionY != 0;
    }
    
    /**
     * @return Vitesse effective actuelle en pixels par seconde
     */
    public double getEffectiveSpeedPixelsPerSecond() {
        return effectiveSpeedPixelsPerSecond;
    }
    
    /**
     * Définit la position en pixels (pour téléportation, respawn, etc.)
     * ✨ **SÉCURITÉ** : Vérifie les limites avant de définir la position
     */
    public void setPixelPosition(double pixelX, double pixelY) {
        // Limites de sécurité (basées sur CELL_SIZE = 48 et grille standard)
        double minX = CELL_SIZE / 2.0;
        double maxX = 14 * CELL_SIZE + (CELL_SIZE / 2.0); // Case 14 (15 colonnes au total)
        double minY = CELL_SIZE / 2.0;
        double maxY = 12 * CELL_SIZE + (CELL_SIZE / 2.0); // Case 12 (13 lignes au total)
        
        // Clamp dans les limites
        this.pixelX = Math.max(minX, Math.min(maxX, pixelX));
        this.pixelY = Math.max(minY, Math.min(maxY, pixelY));
        
        updateGridPosition();
        
        System.out.println("Position sécurisée : (" + String.format("%.1f", this.pixelX) + ", " + String.format("%.1f", this.pixelY) + ")");
    }
    
    /**
     * Méthode utilitaire pour convertir les coordonnées de grille en pixels (centre de la case)
     */
    public static double gridToPixel(int gridCoord) {
        return gridCoord * CELL_SIZE + (CELL_SIZE / 2.0);
    }
    
    /**
     * Méthode utilitaire pour convertir les coordonnées de pixels en grille
     */
    public static int pixelToGrid(double pixelCoord) {
        return (int) (pixelCoord / CELL_SIZE);
    }
    
    /**
     * Override du respawn pour repositionner en pixels
     */
    @Override
    public void respawn(int startX, int startY) {
        super.respawn(startX, startY);
        setPixelPosition(gridToPixel(startX), gridToPixel(startY));
        
        // Arrêter le mouvement pendant l'invincibilité
        pressedKeys.clear();
        moveDirectionX = 0;
        moveDirectionY = 0;
        updateWalkingState(false);
    }
    
    /**
     * Met à jour la direction du sprite (utilise les méthodes protégées de Player)
     */
    private void updateCurrentDirection(String direction) {
        setCurrentDirection(direction);
    }
    
    /**
     * Met à jour l'état de marche (utilise les méthodes protégées de Player)
     */
    private void updateWalkingState(boolean walking) {
        setWalkingState(walking);
    }
    
    /**
     * Joue le son de marche (utilise les méthodes protégées de Player)
     */
    private void playMovementSound() {
        playWalkingSound();
    }
} 