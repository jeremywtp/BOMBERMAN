package bomberman.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale de l'application Bomberman.
 * Responsable du lancement de l'application JavaFX,
 * de l'initialisation de la fenêtre et de la scène.
 * Délègue le dessin à GridRenderer et la logique à Grid.
 * Gère maintenant les interactions clavier pour déplacer le joueur,
 * poser des bombes, gérer les explosions et les ennemis avec IA.
 * Implémente un système d'états pour gérer le menu, le jeu et le game over.
 * Système de score avec high score persistant.
 */
public class Launcher extends Application {
    
    // Dimensions de la fenêtre de jeu
    private static final int WINDOW_WIDTH = 480;
    private static final int WINDOW_HEIGHT = 352;
    
    // Dimensions de la grille (nombre de cases)
    private static final int GRID_COLUMNS = 15;  // 480/32 = 15 cases en largeur
    private static final int GRID_ROWS = 11;     // 352/32 = 11 cases en hauteur
    
    // Position de départ du joueur (première case vide disponible)
    private static final int PLAYER_START_X = 1;
    private static final int PLAYER_START_Y = 1;
    
    // Nombre d'ennemis à créer
    private static final int ENEMY_COUNT = 3;
    
    // Fichier de sauvegarde du high score
    private static final String HIGHSCORE_FILE = "highscore.txt";
    
    // Points attribués pour les actions
    private static final int POINTS_ENEMY_KILLED = 100;
    private static final int POINTS_BLOCK_DESTROYED = 10;
    private static final int POINTS_POWERUP_COLLECTED = 50;
    
    // État du jeu
    private GameState currentState;
    private int gameCounter;  // Compteur de parties
    private int highScore;    // Meilleur score
    
    // Composants du jeu
    private Grid grid;
    private Player player;
    private List<Enemy> enemies;
    private GridRenderer renderer;
    
    // Gestion des bombes et explosions
    private Bomb activeBomb;
    private Explosion activeExplosion;
    
    // Gestion des power-ups
    private List<PowerUp> powerUps;
    
    // Timer d'animation pour les mises à jour
    private AnimationTimer gameTimer;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialisation de l'état du jeu
        currentState = GameState.START_MENU;
        gameCounter = 0;
        
        // Charger le high score
        loadHighScore();
        
        // Création du canvas pour le dessin
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialisation du renderer
        renderer = new GridRenderer(canvas, null);  // Pas de grille au début
        
        // Affichage initial du menu
        renderer.renderStartMenu();
        
        // Configuration de la scène
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Bomberman - Menu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Donner le focus à la scène pour capturer les événements clavier
        scene.getRoot().requestFocus();
        
        System.out.println("=== BOMBERMAN DÉMARRÉ ===");
        System.out.println("État initial : " + currentState);
        System.out.println("High Score : " + highScore);
    }
    
    /**
     * Charge le high score depuis le fichier de sauvegarde
     */
    private void loadHighScore() {
        try {
            if (Files.exists(Paths.get(HIGHSCORE_FILE))) {
                String content = Files.readString(Paths.get(HIGHSCORE_FILE));
                highScore = Integer.parseInt(content.trim());
                System.out.println("High Score chargé : " + highScore);
            } else {
                highScore = 0;
                System.out.println("Aucun fichier de high score trouvé, initialisation à 0");
            }
        } catch (Exception e) {
            highScore = 0;
            System.out.println("Erreur lors du chargement du high score : " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde le high score dans le fichier
     */
    private void saveHighScore() {
        try {
            Files.writeString(Paths.get(HIGHSCORE_FILE), String.valueOf(highScore));
            System.out.println("High Score sauvegardé : " + highScore);
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde du high score : " + e.getMessage());
        }
    }
    
    /**
     * Met à jour le high score si nécessaire
     */
    private void updateHighScore() {
        if (player.getScore() > highScore) {
            highScore = player.getScore();
            saveHighScore();
            System.out.println("NOUVEAU HIGH SCORE : " + highScore + " !");
        }
    }
    
    /**
     * Initialise une nouvelle partie
     */
    private void initializeNewGame() {
        gameCounter++;
        System.out.println("\n=== PARTIE " + gameCounter + " ===");
        
        // Initialisation du modèle de données de la grille
        grid = new Grid(GRID_COLUMNS, GRID_ROWS);
        
        // Mise à jour du renderer avec la nouvelle grille
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Initialisation du joueur à une position de départ valide
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.resetScore();  // Reset du score à 0
        
        // Initialisation des ennemis
        enemies = new ArrayList<>();
        createEnemies();
        
        // Initialisation des power-ups
        powerUps = new ArrayList<>();
        
        // Réinitialisation des bombes et explosions
        activeBomb = null;
        activeExplosion = null;
        
        // Changer l'état du jeu
        currentState = GameState.RUNNING;
        
        // Rendu initial avec high score
        renderGame();
        
        // Démarrer le timer d'animation si pas déjà démarré
        if (gameTimer == null) {
            startGameTimer();
        }
        
        System.out.println("Nouvelle partie initialisée !");
        System.out.println("Score initial : " + player.getScore());
    }
    
    /**
     * Méthode utilitaire pour le rendu complet du jeu avec high score
     */
    private void renderGame() {
        // Dessiner d'abord la grille avec tous les éléments et le high score
        renderer.render(player, enemies, activeBomb, activeExplosion, powerUps, highScore);
    }
    
    /**
     * Crée les ennemis et les place sur la grille
     */
    private void createEnemies() {
        int created = 0;
        int attempts = 0;
        int maxAttempts = 100; // Éviter les boucles infinies
        
        while (created < ENEMY_COUNT && attempts < maxAttempts) {
            attempts++;
            
            // Générer une position aléatoire
            int x = 1 + (int) (Math.random() * (GRID_COLUMNS - 2));
            int y = 1 + (int) (Math.random() * (GRID_ROWS - 2));
            
            // Vérifier que la position est valide
            if (isValidEnemyPosition(x, y)) {
                enemies.add(new Enemy(x, y));
                created++;
                System.out.println("Enemy " + created + " created at position (" + x + ", " + y + ")");
            }
        }
        
        System.out.println("Created " + created + " enemies out of " + ENEMY_COUNT + " requested");
    }
    
    /**
     * Vérifie si une position est valide pour placer un ennemi
     * @param x Position en colonne
     * @param y Position en ligne
     * @return true si la position est valide
     */
    private boolean isValidEnemyPosition(int x, int y) {
        // Vérifier que la case est accessible
        if (!grid.isAccessible(x, y)) {
            return false;
        }
        
        // Vérifier qu'on est assez loin du joueur (zone 3x3 autour du joueur)
        int playerX = player.getX();
        int playerY = player.getY();
        if (Math.abs(x - playerX) <= 1 && Math.abs(y - playerY) <= 1) {
            return false;
        }
        
        // Vérifier qu'il n'y a pas déjà un ennemi à cette position
        for (Enemy enemy : enemies) {
            if (enemy.getX() == x && enemy.getY() == y) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Démarre le timer d'animation pour les mises à jour du jeu
     */
    private void startGameTimer() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
            }
        };
        gameTimer.start();
    }
    
    /**
     * Met à jour l'état du jeu selon l'état actuel
     */
    private void updateGame() {
        // Ne mettre à jour que si le jeu est en cours
        if (currentState != GameState.RUNNING) {
            return;
        }
        
        boolean needsRedraw = false;
        
        // Mettre à jour les ennemis seulement si le joueur est vivant
        if (player.isAlive()) {
            for (Enemy enemy : enemies) {
                if (enemy.update(grid)) {
                    needsRedraw = true;
                }
            }
        }
        
        // Mettre à jour la bombe active
        if (activeBomb != null) {
            if (activeBomb.update()) {
                // La bombe a explosé
                createExplosion();
                activeBomb = null;
                player.setHasActiveBomb(false);
                player.decrementActiveBombs();
                needsRedraw = true;
            }
        }
        
        // Mettre à jour l'explosion active
        if (activeExplosion != null) {
            if (activeExplosion.update()) {
                // L'explosion est terminée
                activeExplosion = null;
                needsRedraw = true;
            }
        }
        
        // Vérifier les collisions seulement si le joueur est vivant
        if (player.isAlive()) {
            checkCollisions();
            
            // Vérifier la collecte de power-ups
            if (checkPowerUpCollection()) {
                needsRedraw = true;
            }
        } else if (currentState == GameState.RUNNING) {
            // Le joueur vient de mourir, passer à l'état GAME_OVER
            updateHighScore();  // Mettre à jour le high score avant de passer en game over
            currentState = GameState.GAME_OVER;
            renderer.renderGameOverScreen(player);
            System.out.println("=== GAME OVER ===");
            System.out.println("Score final : " + player.getScore());
            System.out.println("Passage à l'état : " + currentState);
            return;
        }
        
        // Redessiner la scène si nécessaire
        if (needsRedraw) {
            renderGame();
        }
    }
    
    /**
     * Vérifie toutes les collisions du jeu
     */
    private void checkCollisions() {
        // Vérifier collision joueur/ennemi
        if (player.isAlive()) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && 
                    enemy.getX() == player.getX() && 
                    enemy.getY() == player.getY()) {
                    player.kill();
                    System.out.println("PLAYER DIED - Contact with enemy");
                    break;
                }
            }
        }
        
        // Vérifier collision avec explosion
        if (activeExplosion != null && activeExplosion.isActive()) {
            // Vérifier si le joueur est touché par l'explosion
            if (player.isAlive() && isInExplosion(player.getX(), player.getY())) {
                player.kill();
                System.out.println("PLAYER DIED - Explosion");
            }
            
            // Vérifier si des ennemis sont touchés par l'explosion
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isInExplosion(enemy.getX(), enemy.getY())) {
                    enemy.kill();
                    player.addScore(POINTS_ENEMY_KILLED);  // +100 points pour ennemi tué
                    System.out.println("ENEMY DIED - Explosion at (" + enemy.getX() + ", " + enemy.getY() + ")");
                }
            }
        }
    }
    
    /**
     * Vérifie si une position donnée est dans la zone d'explosion
     * @param x Position en colonne
     * @param y Position en ligne
     * @return true si la position est affectée par l'explosion
     */
    private boolean isInExplosion(int x, int y) {
        if (activeExplosion == null || !activeExplosion.isActive()) {
            return false;
        }
        
        for (Explosion.ExplosionCell cell : activeExplosion.getAffectedCells()) {
            if (cell.getX() == x && cell.getY() == y) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Crée une explosion à partir de la bombe active et révèle les power-ups
     */
    private void createExplosion() {
        if (activeBomb != null) {
            // Première étape : révéler les power-ups AVANT de créer l'explosion
            // (car l'explosion va détruire les blocs et nous perdrons l'information)
            revealPowerUpsBeforeExplosion();
            
            // Deuxième étape : créer l'explosion qui va détruire les blocs
            activeExplosion = new Explosion(
                activeBomb.getX(), 
                activeBomb.getY(), 
                player.getRange(), // Utiliser la portée du joueur (modifiable par power-ups)
                grid
            );
        }
    }
    
    /**
     * Révèle les power-ups des blocs destructibles qui vont être détruits par l'explosion
     * Cette méthode doit être appelée AVANT la création de l'explosion
     */
    private void revealPowerUpsBeforeExplosion() {
        if (activeBomb == null) return;
        
        int centerX = activeBomb.getX();
        int centerY = activeBomb.getY();
        int range = player.getRange();
        
        // Vérifier le centre
        checkAndRevealPowerUp(centerX, centerY);
        
        // Vérifier vers le haut
        for (int i = 1; i <= range; i++) {
            int y = centerY - i;
            if (y < 0) break;
            
            TileType tileType = grid.getTileType(centerX, y);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(centerX, y);
                break;
            }
        }
        
        // Vérifier vers le bas
        for (int i = 1; i <= range; i++) {
            int y = centerY + i;
            if (y >= grid.getRows()) break;
            
            TileType tileType = grid.getTileType(centerX, y);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(centerX, y);
                break;
            }
        }
        
        // Vérifier vers la gauche
        for (int i = 1; i <= range; i++) {
            int x = centerX - i;
            if (x < 0) break;
            
            TileType tileType = grid.getTileType(x, centerY);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(x, centerY);
                break;
            }
        }
        
        // Vérifier vers la droite
        for (int i = 1; i <= range; i++) {
            int x = centerX + i;
            if (x >= grid.getColumns()) break;
            
            TileType tileType = grid.getTileType(x, centerY);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(x, centerY);
                break;
            }
        }
    }
    
    /**
     * Vérifie s'il y a un power-up caché à la position donnée et le révèle
     * @param x Position en colonne
     * @param y Position en ligne
     */
    private void checkAndRevealPowerUp(int x, int y) {
        if (grid.hasHiddenPowerUp(x, y) && grid.isDestructible(x, y)) {
            // +10 points pour bloc destructible détruit
            player.addScore(POINTS_BLOCK_DESTROYED);
            
            // Récupérer le type de power-up caché
            PowerUpType powerUpType = grid.getHiddenPowerUpType(x, y);
            
            if (powerUpType != null) {
                // Retirer le power-up de la map des cachés
                grid.removeHiddenPowerUp(x, y);
                
                // Créer et révéler le power-up
                PowerUp newPowerUp = new PowerUp(x, y, powerUpType);
                newPowerUp.reveal();
                powerUps.add(newPowerUp);
                
                System.out.println("Power-up " + powerUpType + " pré-révélé à (" + x + ", " + y + ")");
            }
        } else if (grid.isDestructible(x, y)) {
            // Bloc destructible sans power-up, donner quand même des points
            player.addScore(POINTS_BLOCK_DESTROYED);
        }
    }
    
    /**
     * Vérifie si le joueur collecte un power-up et applique l'effet
     * @return true si un power-up a été collecté
     */
    private boolean checkPowerUpCollection() {
        boolean collected = false;
        
        // Parcourir tous les power-ups visibles
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            
            if (powerUp.isVisible() && 
                powerUp.isAtPosition(player.getX(), player.getY())) {
                
                // +50 points pour power-up collecté
                player.addScore(POINTS_POWERUP_COLLECTED);
                
                // Appliquer l'effet du power-up
                powerUp.applyEffect(player);
                
                // Retirer le power-up de la liste
                powerUps.remove(i);
                collected = true;
            }
        }
        
        return collected;
    }
    
    /**
     * Gère les événements de touches pressées selon l'état du jeu
     * @param keyCode Le code de la touche pressée
     */
    private void handleKeyPressed(KeyCode keyCode) {
        switch (currentState) {
            case START_MENU:
                handleMenuInput(keyCode);
                break;
            case RUNNING:
                handleGameInput(keyCode);
                break;
            case GAME_OVER:
                handleGameOverInput(keyCode);
                break;
        }
    }
    
    /**
     * Gère les inputs dans le menu de démarrage
     * @param keyCode Le code de la touche pressée
     */
    private void handleMenuInput(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            System.out.println("Démarrage d'une nouvelle partie...");
            initializeNewGame();
        }
    }
    
    /**
     * Gère les inputs durant le jeu
     * @param keyCode Le code de la touche pressée
     */
    private void handleGameInput(KeyCode keyCode) {
        // Ignorer toutes les touches si le joueur est mort
        if (!player.isAlive()) {
            return;
        }
        
        boolean needsRedraw = false;
        
        // Traiter les déplacements selon les flèches directionnelles
        switch (keyCode) {
            case UP:
                if (player.moveUp(grid)) {
                    needsRedraw = true;
                }
                break;
            case DOWN:
                if (player.moveDown(grid)) {
                    needsRedraw = true;
                }
                break;
            case LEFT:
                if (player.moveLeft(grid)) {
                    needsRedraw = true;
                }
                break;
            case RIGHT:
                if (player.moveRight(grid)) {
                    needsRedraw = true;
                }
                break;
            case SPACE:
                // Poser une bombe
                if (tryPlaceBomb()) {
                    needsRedraw = true;
                }
                break;
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Redessiner la scène si nécessaire
        if (needsRedraw) {
            renderGame();
        }
    }
    
    /**
     * Gère les inputs dans l'écran de game over
     * @param keyCode Le code de la touche pressée
     */
    private void handleGameOverInput(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            System.out.println("Redémarrage du jeu...");
            initializeNewGame();
        }
    }
    
    /**
     * Tente de placer une bombe à la position actuelle du joueur
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBomb() {
        // Vérifier si le joueur peut poser une bombe (nouveau système multi-bombes)
        if (player.canPlaceBomb() && activeBomb == null) {
            activeBomb = new Bomb(player.getX(), player.getY());
            player.setHasActiveBomb(true);
            player.incrementActiveBombs();  // Incrémenter le compteur de bombes actives
            return true;
        }
        return false;
    }
    
    /**
     * Arrête le timer d'animation
     */
    @Override
    public void stop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 