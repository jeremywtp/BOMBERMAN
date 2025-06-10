package bomberman.bomberman;

/**
 * Classe représentant le joueur dans le jeu Bomberman.
 * Stocke la position actuelle du joueur sur la grille et gère ses déplacements.
 * Le joueur ne peut pas traverser les blocs solides ni sortir de la grille.
 * Gère également l'état des bombes posées par le joueur, son état de vie,
 * et les power-ups récupérés (bombes supplémentaires, portée, vitesse).
 */
public class Player {
    
    // Position actuelle du joueur sur la grille (coordonnées logiques)
    private int x;  // Colonne
    private int y;  // Ligne
    
    // Direction actuelle du joueur pour l'affichage des sprites
    private String currentDirection;
    
    // État de marche pour l'animation
    private boolean isWalking;
    private long lastMovementTime;
    
    // État des bombes du joueur
    private boolean hasActiveBomb;
    
    // Système de vies du joueur
    private int lives;
    private int maxLives;
    private boolean isInvincible;  // Invincibilité temporaire après respawn
    private long invincibilityStartTime;
    private static final long INVINCIBILITY_DURATION = 10000; // 10 secondes d'invincibilité
    
    // Système d'effets temporaires
    private boolean hasShield;
    private long shieldStartTime;
    private boolean hasSpeedBurst;
    private long speedBurstStartTime;
    private boolean isBombRainActive;
    
    // Système de vitesse visible avec cooldown
    private long lastMoveTime;
    private static final long BASE_MOVE_COOLDOWN = 200; // 200ms de base entre mouvements
    
    // Système de son de marche avec cooldown
    private long lastWalkSoundTime;
    private static final long WALK_SOUND_COOLDOWN = 300; // 300ms entre chaque son de marche
    
    // Score du joueur
    private int score;
    
    // Attributs des power-ups
    private int maxBombs;        // Nombre maximum de bombes actives simultanément
    private int currentBombs;    // Nombre de bombes actuellement actives
    private int range;           // Portée d'explosion des bombes
    private double speed;        // Vitesse de déplacement (pour futures évolutions)
    
    // Valeurs par défaut
    private static final int DEFAULT_MAX_BOMBS = 1;
    private static final int DEFAULT_RANGE = 1;
    private static final double DEFAULT_SPEED = 1.0;
    private static final int DEFAULT_MAX_LIVES = 6;
    
    /**
     * Constructeur du joueur
     * @param startX Position initiale en colonne
     * @param startY Position initiale en ligne
     */
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.currentDirection = "bas"; // Direction par défaut vers le bas
        this.isWalking = false;
        this.lastMovementTime = 0;
        this.hasActiveBomb = false;
        this.lives = DEFAULT_MAX_LIVES;
        this.maxLives = DEFAULT_MAX_LIVES;
        this.isInvincible = false;
        this.invincibilityStartTime = 0;
        this.score = 0;  // Score initial à 0
        
        // Initialiser les effets temporaires
        this.hasShield = false;
        this.shieldStartTime = 0;
        this.hasSpeedBurst = false;
        this.speedBurstStartTime = 0;
        this.isBombRainActive = false;
        
        // Initialiser le système de vitesse
        this.lastMoveTime = 0;
        this.lastWalkSoundTime = 0;
        
        // Initialiser les attributs des power-ups aux valeurs par défaut
        this.maxBombs = DEFAULT_MAX_BOMBS;
        this.currentBombs = 0;
        this.range = DEFAULT_RANGE;
        this.speed = DEFAULT_SPEED;
    }
    
    /**
     * Tente de déplacer le joueur vers le haut
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveUp(Grid grid, BombCollisionChecker bombCollisionChecker) {
        if (!isAlive()) return false;  // Mouvement autorisé même si invincible
        this.currentDirection = "haut"; // Mettre à jour la direction
        return tryMove(x, y - 1, grid, bombCollisionChecker);
    }
    
    /**
     * Tente de déplacer le joueur vers le bas
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveDown(Grid grid, BombCollisionChecker bombCollisionChecker) {
        if (!isAlive()) return false;  // Mouvement autorisé même si invincible
        this.currentDirection = "bas"; // Mettre à jour la direction
        return tryMove(x, y + 1, grid, bombCollisionChecker);
    }
    
    /**
     * Tente de déplacer le joueur vers la gauche
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveLeft(Grid grid, BombCollisionChecker bombCollisionChecker) {
        if (!isAlive()) return false;  // Mouvement autorisé même si invincible
        this.currentDirection = "gauche"; // Mettre à jour la direction
        return tryMove(x - 1, y, grid, bombCollisionChecker);
    }
    
    /**
     * Tente de déplacer le joueur vers la droite
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @return true si le déplacement a eu lieu, false sinon
     */
    public boolean moveRight(Grid grid, BombCollisionChecker bombCollisionChecker) {
        if (!isAlive()) return false;  // Mouvement autorisé même si invincible
        this.currentDirection = "droite"; // Mettre à jour la direction
        return tryMove(x + 1, y, grid, bombCollisionChecker);
    }
    
    /**
     * Méthode privée pour tenter un déplacement vers une position donnée
     * @param newX Nouvelle position en colonne
     * @param newY Nouvelle position en ligne
     * @param grid La grille pour vérifier les collisions
     * @param bombCollisionChecker Interface pour vérifier les collisions avec les bombes
     * @return true si le déplacement a eu lieu, false sinon
     */
    private boolean tryMove(int newX, int newY, Grid grid, BombCollisionChecker bombCollisionChecker) {
        // SPEED_BURST = mouvement instantané, ignore tous les cooldowns
        if (hasSpeedBurst) {
            // Vérifier si la nouvelle position est accessible
            if (grid.isAccessible(newX, newY) && !bombCollisionChecker.isBombBlockingMovement(newX, newY, true)) {
                this.x = newX;
                this.y = newY;
                // Pas de mise à jour de lastMoveTime pour SPEED_BURST
                
                // Déclencher l'animation de marche
                this.isWalking = true;
                this.lastMovementTime = System.currentTimeMillis();
                
                // Jouer le son de marche (avec son propre cooldown)
                playWalkingSound();
                
                return true;
            }
            return false;
        }
        
        // Système normal avec cooldown pour mouvements normaux et SPEED_UP
        long currentTime = System.currentTimeMillis();
        long cooldown = calculateMoveCooldown();
        
        if (currentTime - lastMoveTime < cooldown) {
            return false; // Trop tôt pour bouger
        }
        
        // Vérifier si la nouvelle position est accessible et pas bloquée par une bombe
        if (grid.isAccessible(newX, newY) && !bombCollisionChecker.isBombBlockingMovement(newX, newY, true)) {
            this.x = newX;
            this.y = newY;
            this.lastMoveTime = currentTime;
            
            // Déclencher l'animation de marche
            this.isWalking = true;
            this.lastMovementTime = currentTime;
            
            // Jouer le son de marche
            playWalkingSound();
            
            return true;
        }
        return false;
    }
    
    /**
     * Calcule le cooldown de mouvement selon les effets actifs (hors SPEED_BURST)
     * @return Cooldown en millisecondes
     */
    private long calculateMoveCooldown() {
        long cooldown = BASE_MOVE_COOLDOWN;
        
        // Speed Up permanent : -25ms par niveau de vitesse
        double speedMultiplier = speed - DEFAULT_SPEED; // 0.5 par SPEED_UP
        cooldown -= (long) (speedMultiplier * 50); // -25ms par 0.5 de vitesse
        
        // NOTE: SPEED_BURST est géré directement dans tryMove() 
        // pour un mouvement complètement instantané
        
        // Cooldown minimum de 50ms pour éviter les mouvements trop rapides
        return Math.max(cooldown, 50);
    }
    
    /**
     * Joue le son de marche avec cooldown pour éviter l'empilement
     * Ne joue pas si le joueur est mort
     */
    protected void playWalkingSound() {
        // Ne pas jouer le son si le joueur est mort
        if (!isAlive()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWalkSoundTime >= WALK_SOUND_COOLDOWN) {
            try {
                bomberman.bomberman.SoundManager.playEffect("walking");
                lastWalkSoundTime = currentTime;
            } catch (Exception e) {
                // Ignorer les erreurs audio pour ne pas affecter le gameplay
                System.err.println("Erreur lors de la lecture du son de marche : " + e.getMessage());
            }
        }
    }
    
    /**
     * Tue le joueur
     */
    public void kill() {
        if (lives > 0) {
            lives--;
            
            // Jouer le son de mort immédiatement (une seule fois par mort)
            try {
                bomberman.bomberman.SoundManager.playEffect("dies");
            } catch (Exception e) {
                // Ignorer les erreurs audio pour ne pas affecter le gameplay
                System.err.println("Erreur lors de la lecture du son de mort : " + e.getMessage());
            }
            
            isInvincible = true;
            invincibilityStartTime = System.currentTimeMillis();
            System.out.println("Joueur tué, vies restantes: " + lives + " | Invincibilité (10s)");
        } else {
            System.out.println("Joueur mort, partie terminée");
        }
    }
    
    /**
     * @return true si le joueur est vivant
     */
    public boolean isAlive() {
        return lives > 0;
    }
    
    /**
     * @return Nombre de vies actuelles
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * @return Nombre maximum de vies
     */
    public int getMaxLives() {
        return maxLives;
    }
    
    /**
     * @return Nombre de vies pour l'affichage (vies réelles - 1)
     * Permet d'afficher 5/5 → 4/5 → ... → 0/5 pendant qu'il reste une dernière chance
     */
    public int getDisplayLives() {
        return Math.max(0, lives - 1);
    }
    
    /**
     * @return true si le joueur est invincible (après respawn)
     */
    public boolean isInvincible() {
        return isInvincible;
    }
    
    /**
     * Met à jour l'état d'invincibilité (à appeler dans la boucle de jeu)
     */
    public void updateInvincibility() {
        if (isInvincible) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - invincibilityStartTime >= INVINCIBILITY_DURATION) {
                isInvincible = false;
                System.out.println("Invincibilité terminée (10s écoulées)");
            }
        }
    }
    
    /**
     * Met à jour l'état de marche pour l'animation
     * Arrête l'animation si le joueur n'a pas bougé récemment
     */
    public void updateWalkingState() {
        if (isWalking) {
            long currentTime = System.currentTimeMillis();
            // Arrêter l'animation 400ms après le dernier mouvement
            if (currentTime - lastMovementTime >= 400) {
                isWalking = false;
            }
        }
    }
    
    /**
     * Fait respawn le joueur à la position de départ avec invincibilité
     * @param startX Position de respawn en X
     * @param startY Position de respawn en Y
     */
    public void respawn(int startX, int startY) {
        if (lives > 0) {
            setPosition(startX, startY);
            isInvincible = true;
            invincibilityStartTime = System.currentTimeMillis();
            System.out.println("Respawn du joueur à (" + startX + ", " + startY + ") | Invincibilité (10s)");
        }
    }
    
    /**
     * Ajoute une vie supplémentaire (power-up futur)
     */
    public void addLife() {
        if (lives < maxLives) {
            lives++;
            System.out.println("Vie supplémentaire ! Vies actuelles : " + lives);
        }
    }
    
    /**
     * Remet les vies au maximum (nouvelle partie)
     */
    public void resetLives() {
        lives = maxLives;
        isInvincible = false;
        invincibilityStartTime = 0;
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
     * Définit une nouvelle position pour le joueur (sans vérification de collision)
     * @param x Nouvelle position en colonne
     * @param y Nouvelle position en ligne
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return true si le joueur a une bombe active
     */
    public boolean hasActiveBomb() {
        return hasActiveBomb;
    }
    
    /**
     * Définit l'état de la bombe active du joueur
     * @param hasActiveBomb true si le joueur a une bombe active
     */
    public void setHasActiveBomb(boolean hasActiveBomb) {
        this.hasActiveBomb = hasActiveBomb;
    }
    
    /**
     * Augmente le nombre maximum de bombes que le joueur peut poser
     * Effect du power-up EXTRA_BOMB
     */
    public void increaseMaxBombs() {
        this.maxBombs++;
        System.out.println("Bombes maximum augmentées : " + maxBombs);
    }
    
    /**
     * Augmente la portée d'explosion des bombes du joueur
     * Effect du power-up RANGE_UP
     */
    public void increaseRange() {
        this.range++;
        System.out.println("Portée d'explosion augmentée : " + range);
    }
    
    /**
     * Augmente la vitesse de déplacement du joueur
     * Effect du power-up SPEED_UP
     */
    public void increaseSpeed() {
        this.speed += 0.5;
        System.out.println("Vitesse augmentée : " + speed);
    }
    
    /**
     * Vérifie si le joueur peut poser une bombe supplémentaire
     * @return true si le joueur peut poser une bombe
     */
    public boolean canPlaceBomb() {
        return currentBombs < maxBombs;
    }
    
    /**
     * Incrémente le nombre de bombes actives
     */
    public void incrementActiveBombs() {
        if (currentBombs < maxBombs) {
            currentBombs++;
        }
    }
    
    /**
     * Décrémente le nombre de bombes actives (quand une bombe explose)
     */
    public void decrementActiveBombs() {
        if (currentBombs > 0) {
            currentBombs--;
        }
    }
    
    /**
     * @return Nombre maximum de bombes
     */
    public int getMaxBombs() {
        return maxBombs;
    }
    
    /**
     * @return Nombre de bombes actuellement actives
     */
    public int getCurrentBombs() {
        return currentBombs;
    }
    
    /**
     * @return Portée d'explosion des bombes
     */
    public int getRange() {
        return range;
    }
    
    /**
     * @return Vitesse de déplacement
     */
    public double getSpeed() {
        return speed;
    }
    
    /**
     * Ajoute des points au score du joueur
     * @param amount Nombre de points à ajouter
     */
    public void addScore(int amount) {
        this.score += amount;
        System.out.println("Score +=" + amount + " | Total: " + this.score);
    }
    
    /**
     * @return Score actuel du joueur
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Remet le score à zéro (utilisé lors d'une nouvelle partie)
     */
    public void resetScore() {
        this.score = 0;
        resetLives();
        resetTemporaryEffects();
    }
    
    // ========== SYSTÈME D'EFFETS TEMPORAIRES ==========
    
    /**
     * Active le bouclier protecteur contre les explosions
     * @param duration Durée du bouclier en millisecondes
     */
    public void activateShield(long duration) {
        this.hasShield = true;
        this.shieldStartTime = System.currentTimeMillis();
        System.out.println("SHIELD ACTIVÉ pour " + (duration / 1000) + " secondes !");
    }
    
    /**
     * Active l'effet de vitesse accrue
     * @param duration Durée de l'effet en millisecondes
     */
    public void activateSpeedBurst(long duration) {
        this.hasSpeedBurst = true;
        this.speedBurstStartTime = System.currentTimeMillis();
        System.out.println("SPEED BURST ACTIVÉ pour " + (duration / 1000) + " secondes - VITESSE INSTANTANÉE !");
    }
    
    /**
     * Active l'effet de pluie de bombes (instantané)
     */
    public void activateBombRain() {
        this.isBombRainActive = true;
        System.out.println("BOMB RAIN ACTIVÉ ! Préparez-vous à l'explosion massive !");
    }
    
    /**
     * Met à jour tous les effets temporaires (à appeler dans la boucle de jeu)
     */
    public void updateTemporaryEffects() {
        long currentTime = System.currentTimeMillis();
        
        // Mettre à jour le bouclier
        if (hasShield) {
            if (currentTime - shieldStartTime >= 10000) { // 10 secondes
                hasShield = false;
                System.out.println("Bouclier désactivé");
            }
        }
        
        // Mettre à jour le speed burst
        if (hasSpeedBurst) {
            if (currentTime - speedBurstStartTime >= 5000) { // 5 secondes
                hasSpeedBurst = false;
                System.out.println("Speed Burst désactivé");
            }
        }
    }
    
    /**
     * @return true si le joueur a un bouclier actif
     */
    public boolean hasShield() {
        return hasShield;
    }
    
    /**
     * @return true si le joueur a un speed burst actif
     */
    public boolean hasSpeedBurst() {
        return hasSpeedBurst;
    }
    
    /**
     * @return true si l'effet bomb rain est actif (doit être traité puis désactivé)
     */
    public boolean isBombRainActive() {
        return isBombRainActive;
    }
    
    /**
     * Désactive l'effet bomb rain (après traitement)
     */
    public void deactivateBombRain() {
        this.isBombRainActive = false;
    }
    
    /**
     * Calcule la vitesse effective du joueur en tenant compte des effets temporaires
     * @return Vitesse effective (999 pour SPEED_BURST = vitesse instantanée)
     */
    public double getEffectiveSpeed() {
        if (hasSpeedBurst) {
            return 999.0; // Vitesse "infinie" pour SPEED_BURST
        }
        return this.speed; // Vitesse normale ou augmentée par SPEED_UP
    }
    
    /**
     * Vérifie si le joueur est protégé des explosions
     * @return true si protégé (bouclier ou invincibilité)
     */
    public boolean isProtectedFromExplosions() {
        return hasShield || isInvincible;
    }
    
    /**
     * Reset tous les effets temporaires (nouvelle partie)
     */
    public void resetTemporaryEffects() {
        hasShield = false;
        shieldStartTime = 0;
        hasSpeedBurst = false;
        speedBurstStartTime = 0;
        isBombRainActive = false;
    }
    
    /**
     * @return Nombre de bombes disponibles (non utilisées)
     */
    public int getAvailableBombs() {
        return maxBombs - currentBombs;
    }
    
    /**
     * @return Direction actuelle du joueur pour l'affichage des sprites
     */
    public String getCurrentDirection() {
        return currentDirection;
    }
    
    /**
     * @return True si le joueur est en train de marcher (pour l'animation)
     */
    public boolean isWalking() {
        return isWalking;
    }
    
    /**
     * Définit l'état de marche (pour l'animation)
     * @param walking True si en marche, false sinon
     */
    protected void setWalkingState(boolean walking) {
        this.isWalking = walking;
        if (walking) {
            this.lastMovementTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Définit la direction actuelle du sprite
     * @param direction Direction ("haut", "bas", "gauche", "droite")
     */
    protected void setCurrentDirection(String direction) {
        this.currentDirection = direction;
    }
    
    /**
     * ✨ **NOUVEAU** : Interface fonctionnelle pour vérifier les collisions avec les bombes
     * Permet au Player de vérifier les bombes sans dépendre directement de Launcher
     */
    @FunctionalInterface
    public interface BombCollisionChecker {
        boolean isBombBlockingMovement(int x, int y, boolean isPlayer);
    }
    
    /**
     * @return true si le joueur est dans l'animation de mort
     */
    public boolean isDying() {
        return false; // Par défaut, non géré dans la classe de base
    }
    
    /**
     * @return true si le joueur est dans l'animation de victoire
     */
    public boolean isWinning() {
        return false; // Par défaut, non géré dans la classe de base
    }
    
    /**
     * Termine la séquence de mort après l'animation.
     * C'est ici que le joueur perd une vie.
     */
    public void completeDeathSequence() {
        // Non implémenté dans la classe de base
    }
    
    /**
     * Décrémente la vie du joueur.
     * Protégé pour être accessible par les classes enfants.
     */
    protected void decrementLife() {
        if (lives > 0) {
            lives--;
        }
    }
} 