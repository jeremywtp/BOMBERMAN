package bomberman.bomberman;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
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
    
    // Dimensions de la fenêtre de jeu (agrandie x1.5 pour zoom)
    private static final int WINDOW_WIDTH = 720;  // 480 * 1.5
    private static final int WINDOW_HEIGHT = 780; // 520 * 1.5
    
    // Dimensions de la grille (nombre de cases)
    private static final int GRID_COLUMNS = 15;  // 720/48 = 15 cases en largeur
    private static final int GRID_ROWS = 11;     // 528/48 = 11 cases en hauteur (grille agrandie)
    
    // Hauteur de la grille de jeu (agrandie x1.5)
    private static final int GAME_AREA_HEIGHT = 528; // 11 * 48 = 528px (était 352px)
    
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
    private int currentLevel; // Niveau actuel
    private boolean isLevelStarting; // True si la musique de niveau est en cours
    
    // État du menu interactif
    private int selectedMenuIndex = 0;  // Index de l'option sélectionnée (0-2)
    private static final String[] MENU_OPTIONS = {"NORMAL GAME", "BATTLE MODE", "PASSWORD"};
    private static final boolean[] MENU_OPTIONS_ENABLED = {true, false, false}; // Seul NORMAL GAME est actif
    
    // Composants du jeu
    private Grid grid;
    private Player player;
    private List<Enemy> enemies;
    private GridRenderer renderer;
    
    // Gestion des bombes et explosions
    private List<Bomb> activeBombs;        // Bombes du joueur
    private List<Bomb> rainBombs;          // Bombes de Bomb Rain (ne comptent pas dans la limite)
    private List<Explosion> activeExplosions;
    
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
        
        // Initialiser le gestionnaire de sons
        initializeSoundManager();
        
        // Création du canvas pour le dessin
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialisation du renderer
        renderer = new GridRenderer(canvas, null);  // Pas de grille au début
        
        // Affichage initial du menu
        renderer.renderStartMenu(selectedMenuIndex, MENU_OPTIONS, MENU_OPTIONS_ENABLED);
        
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
     * Initialise le gestionnaire de sons et charge la musique d'intro et les effets de menu
     */
    private void initializeSoundManager() {
        try {
            // Charger la musique d'intro (format WAV pour compatibilité)
            SoundManager.loadSound("intro", "/music/intro.wav");
            
            // Charger la musique de démarrage de niveau
            SoundManager.loadSound("level_start", "/music/Level_Start.wav");
            
            // Charger les effets sonores de menu
            SoundManager.loadSoundEffect("menu_cursor", "/music/Menu_Cursor.wav");
            SoundManager.loadSoundEffect("menu_select", "/music/Menu_Select.wav");
            
            // Attendre un peu avant de lancer la musique pour permettre l'initialisation
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    // Démarrer la musique d'intro en boucle après délai
                    SoundManager.loop("intro");
                    System.out.println("Musique d'intro lancée avec délai");
                })
            );
            timeline.play();
            
            System.out.println("SoundManager initialisé - Chargement terminé (musique + effets menu)");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du SoundManager : " + e.getMessage());
            e.printStackTrace();
        }
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
        currentLevel = 1;  // Commencer au niveau 1
        System.out.println("\n=== PARTIE " + gameCounter + " ===");
        System.out.println("=== NIVEAU " + currentLevel + " ===");
        
        // Initialisation du modèle de données de la grille
        grid = new Grid(GRID_COLUMNS, GRID_ROWS);
        
        // Mise à jour du renderer avec la nouvelle grille
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Initialisation du joueur à une position de départ valide
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.resetScore();  // Reset du score à 0
        
        // Initialisation du niveau
        initializeLevel();
        
        // Démarrer le timer d'animation si pas déjà démarré
        if (gameTimer == null) {
            startGameTimer();
        }
        
        System.out.println("Nouvelle partie initialisée !");
        System.out.println("Score initial : " + player.getScore());
    }
    
    /**
     * Initialise un niveau spécifique avec difficulté progressive
     */
    private void initializeLevel() {
        // Initialisation des ennemis (plus d'ennemis aux niveaux supérieurs)
        enemies = new ArrayList<>();
        createEnemiesForLevel();
        
        // Initialisation des power-ups
        powerUps = new ArrayList<>();
        
        // Réinitialisation des bombes et explosions
        activeBombs = new ArrayList<>();
        rainBombs = new ArrayList<>();
        activeExplosions = new ArrayList<>();
        
        // État temporaire : niveau en cours de démarrage
        isLevelStarting = true;
        currentState = GameState.LEVEL_STARTING;
        
        // Attendre un court délai pour que l'effet menu se termine, puis jouer la musique de démarrage
        Timeline delayTimeline = new Timeline(
            new KeyFrame(Duration.millis(600), e -> {
                // Jouer la musique de démarrage de niveau et attendre sa fin
                SoundManager.playOnce("level_start", () -> {
                    // Callback exécuté à la fin de la musique
                    isLevelStarting = false;
                    currentState = GameState.RUNNING;
                    
                    // Activer l'invincibilité de 10 secondes quand le joueur peut bouger
                    player.respawn(player.getX(), player.getY());
                    
                    System.out.println("Musique de démarrage terminée - Niveau " + currentLevel + " démarré !");
                });
                System.out.println("Délai d'attente terminé - Lancement de Level_Start.wav");
            })
        );
        delayTimeline.play();
        
        // Rendu initial avec affichage "NIVEAU X" en surimpression
        renderLevelStart();
        
        System.out.println("Niveau " + currentLevel + " initialisé avec musique de démarrage !");
        System.out.println("Nombre d'ennemis : " + enemies.size());
    }
    
    /**
     * Passe au niveau suivant en conservant l'état du joueur
     */
    private void nextLevel() {
        currentLevel++;
        System.out.println("\n=== NIVEAU " + currentLevel + " ===");
        
        // Régénérer une nouvelle grille pour le niveau suivant
        grid = new Grid(GRID_COLUMNS, GRID_ROWS);
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Remettre le joueur à la position de départ (mais conserver ses attributs)
        player.setPosition(PLAYER_START_X, PLAYER_START_Y);
        
        // Initialiser le nouveau niveau
        initializeLevel();
        
        System.out.println("Passage au niveau " + currentLevel + " terminé !");
        System.out.println("Score actuel conservé : " + player.getScore());
    }
    
    /**
     * Méthode utilitaire pour le rendu complet du jeu avec high score et niveau
     */
    private void renderGame() {
        // Combiner toutes les bombes pour le rendu
        List<Bomb> allBombs = new ArrayList<>(activeBombs);
        allBombs.addAll(rainBombs);
        
        // Dessiner d'abord la grille avec tous les éléments, le high score et le niveau
        renderer.render(player, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel);
    }
    
    /**
     * Rendu spécial pour le démarrage de niveau avec affichage "NIVEAU X"
     */
    private void renderLevelStart() {
        // Combiner toutes les bombes pour le rendu
        List<Bomb> allBombs = new ArrayList<>(activeBombs);
        allBombs.addAll(rainBombs);
        
        // Dessiner la grille normalement avec surimpression "NIVEAU X"
        renderer.render(player, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel);
        
        // TODO: Ajouter une surimpression "NIVEAU X" si nécessaire dans GridRenderer
        // Pour l'instant, on utilise le rendu normal
    }
    
    /**
     * Crée les ennemis pour le niveau actuel avec difficulté progressive
     */
    private void createEnemiesForLevel() {
        // Calculer le nombre d'ennemis en fonction du niveau (3 + 1 par niveau, max 8)
        int enemyCount = Math.min(ENEMY_COUNT + currentLevel - 1, 8);
        
        int created = 0;
        int attempts = 0;
        int maxAttempts = 100; // Éviter les boucles infinies
        
        while (created < enemyCount && attempts < maxAttempts) {
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
        
        System.out.println("Created " + created + " enemies out of " + enemyCount + " requested for level " + currentLevel);
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
        
        // Mettre à jour l'invincibilité du joueur
        if (player.isAlive()) {
            player.updateInvincibility();
            player.updateTemporaryEffects();
            
            // Forcer le rendu si le joueur est invincible (pour le clignotement)
            if (player.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain
            if (player.isBombRainActive()) {
                handleBombRain();
                player.deactivateBombRain();
            }
        }
        
        // Mettre à jour les ennemis seulement si le joueur est vivant
        if (player.isAlive()) {
            for (Enemy enemy : enemies) {
                if (enemy.update(grid)) {
                    needsRedraw = true;
                }
            }
        }
        
        // Mettre à jour les bombes actives du joueur
        for (int i = activeBombs.size() - 1; i >= 0; i--) {
            Bomb bomb = activeBombs.get(i);
            if (bomb.update()) {
                // La bombe du joueur a explosé
                createExplosion(bomb);
                activeBombs.remove(i);  // Retirer la bombe de la liste
                player.decrementActiveBombs();  // Décrémenter le compteur du joueur
                needsRedraw = true;
                System.out.println("Bombe joueur explosée - Bombes restantes: " + player.getCurrentBombs() + "/" + player.getMaxBombs());
            }
        }
        
        // Mettre à jour les bombes de Bomb Rain (ne comptent pas dans la limite)
        for (int i = rainBombs.size() - 1; i >= 0; i--) {
            Bomb bomb = rainBombs.get(i);
            if (bomb.update()) {
                // Une bombe de Bomb Rain a explosé
                createExplosion(bomb);
                rainBombs.remove(i);  // Retirer la bombe de la liste
                needsRedraw = true;
                System.out.println("Bombe Rain explosée (ne compte pas dans la limite joueur)");
            }
        }
        
        // Mettre à jour les explosions actives et nettoyer les terminées
        for (int i = activeExplosions.size() - 1; i >= 0; i--) {
            Explosion explosion = activeExplosions.get(i);
            if (explosion.update()) {
                // L'explosion est terminée
                activeExplosions.remove(i);  // Retirer l'explosion de la liste
                needsRedraw = true;
                System.out.println("Explosion terminée");
            }
        }
        
        // Vérifier les collisions seulement si le joueur est vivant
        if (player.isAlive()) {
            checkCollisions();
            
            // Vérifier la collecte de power-ups
            if (checkPowerUpCollection()) {
                needsRedraw = true;
            }
            
            // Vérifier si le niveau est terminé (tous les ennemis morts)
            if (checkLevelCompleted()) {
                currentState = GameState.LEVEL_COMPLETED;
                renderer.renderLevelCompletedScreen(currentLevel, player);
                System.out.println("=== NIVEAU " + currentLevel + " TERMINÉ ===");
                System.out.println("Passage à l'état : " + currentState);
                return;
            }
        } else if (currentState == GameState.RUNNING) {
            // Le joueur vient de mourir complètement, passer à l'état GAME_OVER
            updateHighScore();  // Mettre à jour le high score avant de passer en game over
            currentState = GameState.GAME_OVER;
            renderer.renderGameOverScreen(player);
            System.out.println("=== GAME OVER ===");
            System.out.println("Score final : " + player.getScore());
            System.out.println("Passage à l'état : " + currentState);
            return;
        }
        
        // Redessiner si nécessaire
        if (needsRedraw) {
            renderGame();
        }
    }
    
    /**
     * Vérifie toutes les collisions du jeu
     */
    private void checkCollisions() {
        // Vérifier collision joueur/ennemi (seulement si pas invincible)
        if (player.isAlive() && !player.isInvincible()) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && 
                    enemy.getX() == player.getX() && 
                    enemy.getY() == player.getY()) {
                    
                    int livesBeforeDamage = player.getLives();
                    player.kill();
                    System.out.println("PLAYER HIT BY ENEMY - Contact with enemy");
                    
                    // Si le joueur a encore des vies, le faire respawn
                    if (player.isAlive()) {
                        player.respawn(PLAYER_START_X, PLAYER_START_Y);
                        System.out.println("Joueur respawn avec " + player.getLives() + " vies restantes");
                    }
                    break;
                }
            }
        }
        
        // Vérifier collision avec explosion (seulement si pas protégé)
        for (Explosion explosion : activeExplosions) {
            if (explosion.isActive()) {
                // Vérifier si le joueur est touché par l'explosion (avec protection du bouclier)
                if (player.isAlive() && !player.isProtectedFromExplosions() && isInExplosion(player.getX(), player.getY())) {
                    int livesBeforeDamage = player.getLives();
                    player.kill();
                    System.out.println("PLAYER HIT BY EXPLOSION - Explosion damage");
                    
                    // Si le joueur a encore des vies, le faire respawn
                    if (player.isAlive()) {
                        player.respawn(PLAYER_START_X, PLAYER_START_Y);
                        System.out.println("Joueur respawn avec " + player.getLives() + " vies restantes");
                    }
                } else if (player.isAlive() && player.hasShield() && isInExplosion(player.getX(), player.getY())) {
                    System.out.println("EXPLOSION BLOQUÉE PAR LE BOUCLIER !");
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
    }
    
    /**
     * Vérifie si une position donnée est dans la zone d'explosion
     * @param x Position en colonne
     * @param y Position en ligne
     * @return true si la position est affectée par l'explosion
     */
    private boolean isInExplosion(int x, int y) {
        for (Explosion explosion : activeExplosions) {
            if (explosion.isActive()) {
                for (Explosion.ExplosionCell cell : explosion.getAffectedCells()) {
                    if (cell.getX() == x && cell.getY() == y) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Crée une explosion à partir d'une bombe et révèle les power-ups
     * @param bomb La bombe à partir de laquelle l'explosion est créée
     */
    private void createExplosion(Bomb bomb) {
        // Première étape : révéler les power-ups AVANT de créer l'explosion
        // (car l'explosion va détruire les blocs et nous perdrons l'information)
        revealPowerUpsBeforeExplosion(bomb.getX(), bomb.getY());
        
        // Deuxième étape : créer l'explosion qui va détruire les blocs
        Explosion explosion = new Explosion(
            bomb.getX(), 
            bomb.getY(), 
            player.getRange(), // Utiliser la portée du joueur (modifiable par power-ups)
            grid
        );
        activeExplosions.add(explosion);
    }
    
    /**
     * Révèle les power-ups des blocs destructibles qui vont être détruits par l'explosion
     * Cette méthode doit être appelée AVANT la création de l'explosion
     * @param x Position X de la bombe
     * @param y Position Y de la bombe
     */
    private void revealPowerUpsBeforeExplosion(int x, int y) {
        int range = player.getRange();
        
        // Vérifier le centre
        checkAndRevealPowerUp(x, y);
        
        // Vérifier vers le haut
        for (int i = 1; i <= range; i++) {
            int yUp = y - i;
            if (yUp < 0) break;
            
            TileType tileType = grid.getTileType(x, yUp);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(x, yUp);
                break;
            }
        }
        
        // Vérifier vers le bas
        for (int i = 1; i <= range; i++) {
            int yDown = y + i;
            if (yDown >= grid.getRows()) break;
            
            TileType tileType = grid.getTileType(x, yDown);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(x, yDown);
                break;
            }
        }
        
        // Vérifier vers la gauche
        for (int i = 1; i <= range; i++) {
            int xLeft = x - i;
            if (xLeft < 0) break;
            
            TileType tileType = grid.getTileType(xLeft, y);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(xLeft, y);
                break;
            }
        }
        
        // Vérifier vers la droite
        for (int i = 1; i <= range; i++) {
            int xRight = x + i;
            if (xRight >= grid.getColumns()) break;
            
            TileType tileType = grid.getTileType(xRight, y);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                checkAndRevealPowerUp(xRight, y);
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
                
                // Ajouter notification selon le type de power-up
                String notificationMessage = getNotificationMessage(powerUp.getType());
                renderer.addNotification(notificationMessage);
                
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
     * Génère le message de notification pour un power-up collecté
     * @param type Type de power-up
     * @return Message à afficher
     */
    private String getNotificationMessage(PowerUpType type) {
        switch (type) {
            case EXTRA_BOMB:
                return "EXTRA BOMB récupéré ! (+1 bombe max)";
            case RANGE_UP:
                return "RANGE UP récupéré ! (+1 portée)";
            case SPEED_UP:
                return "SPEED UP récupéré ! (+0.5 vitesse)";
            case SHIELD:
                return "SHIELD récupéré ! (10s protection)";
            case SPEED_BURST:
                return "SPEED BURST récupéré ! (5s vitesse max)";
            case BOMB_RAIN:
                return "BOMB RAIN récupéré ! (5 bombes automatiques)";
            default:
                return "Power-up récupéré !";
        }
    }
    
    /**
     * Vérifie si le niveau est terminé (tous les ennemis morts)
     * @return true si le niveau est terminé
     */
    private boolean checkLevelCompleted() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }
        return true;
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
            case LEVEL_STARTING:
                // Ignorer tous les inputs pendant le démarrage de niveau
                System.out.println("Input ignoré pendant le démarrage de niveau : " + keyCode);
                break;
            case RUNNING:
                handleGameInput(keyCode);
                break;
            case LEVEL_COMPLETED:
                handleLevelCompletedInput(keyCode);
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
        boolean needsRedraw = false;
        
        switch (keyCode) {
            case UP:
                // Naviguer vers le haut
                selectedMenuIndex = (selectedMenuIndex - 1 + MENU_OPTIONS.length) % MENU_OPTIONS.length;
                needsRedraw = true;
                SoundManager.playEffect("menu_cursor");
                System.out.println("Menu navigation - Option sélectionnée : " + MENU_OPTIONS[selectedMenuIndex]);
                break;
                
            case DOWN:
                // Naviguer vers le bas
                selectedMenuIndex = (selectedMenuIndex + 1) % MENU_OPTIONS.length;
                needsRedraw = true;
                SoundManager.playEffect("menu_cursor");
                System.out.println("Menu navigation - Option sélectionnée : " + MENU_OPTIONS[selectedMenuIndex]);
                break;
                
            case ENTER:
                // Valider la sélection
                handleMenuSelection();
                break;
        }
        
        // Redessiner le menu si nécessaire
        if (needsRedraw) {
            renderer.renderStartMenu(selectedMenuIndex, MENU_OPTIONS, MENU_OPTIONS_ENABLED);
        }
    }
    
    /**
     * Gère la sélection d'une option du menu
     */
    private void handleMenuSelection() {
        if (!MENU_OPTIONS_ENABLED[selectedMenuIndex]) {
            System.out.println("Option désactivée : " + MENU_OPTIONS[selectedMenuIndex]);
            return;
        }
        
        switch (selectedMenuIndex) {
            case 0: // NORMAL GAME
                SoundManager.playEffect("menu_select");
                System.out.println("Démarrage d'une nouvelle partie...");
                
                // Arrêter la musique d'intro avant de lancer le jeu
                SoundManager.stop("intro");
                System.out.println("Musique d'intro arrêtée");
                
                initializeNewGame();
                break;
                
            case 1: // BATTLE MODE
                SoundManager.playEffect("menu_select");
                System.out.println("BATTLE MODE non implémenté pour l'instant");
                break;
                
            case 2: // PASSWORD
                SoundManager.playEffect("menu_select");
                System.out.println("PASSWORD non implémenté pour l'instant");
                break;
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
            
            // Arrêter la musique d'intro si elle joue encore (normalement elle ne devrait pas)
            SoundManager.stop("intro");
            
            initializeNewGame();
        }
    }
    
    /**
     * Gère les inputs dans l'écran de niveau terminé
     * @param keyCode Le code de la touche pressée
     */
    private void handleLevelCompletedInput(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            System.out.println("Passage au niveau suivant...");
            nextLevel();
        }
    }
    
    /**
     * Tente de placer une bombe à la position actuelle du joueur
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBomb() {
        // Vérifier si le joueur peut poser une bombe (nouveau système multi-bombes)
        if (player.canPlaceBomb() && !isBombAt(player.getX(), player.getY())) {
            Bomb newBomb = new Bomb(player.getX(), player.getY());
            activeBombs.add(newBomb);
            player.incrementActiveBombs();  // Incrémenter le compteur de bombes actives
            System.out.println("Bombe posée à (" + player.getX() + ", " + player.getY() + ") - Total: " + player.getCurrentBombs() + "/" + player.getMaxBombs());
            return true;
        }
        return false;
    }
    
    /**
     * Vérifie s'il y a une bombe à la position donnée (joueur ou rain)
     * @param x Position X
     * @param y Position Y
     * @return true s'il y a une bombe
     */
    private boolean isBombAt(int x, int y) {
        // Vérifier les bombes du joueur
        for (Bomb bomb : activeBombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                return true;
            }
        }
        
        // Vérifier les bombes de Bomb Rain
        for (Bomb bomb : rainBombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Arrête le timer d'animation et libère les ressources audio
     */
    @Override
    public void stop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // Libérer les ressources audio
        SoundManager.dispose();
        System.out.println("Application fermée - Ressources libérées");
    }
    
    /**
     * Gère l'effet Bomb Rain - pose automatiquement plusieurs bombes
     * Note: Les bombes de Bomb Rain ne comptent PAS dans la limite du joueur
     */
    private void handleBombRain() {
        System.out.println("=== BOMB RAIN EN COURS ===");
        System.out.println("Bombes du joueur avant Bomb Rain : " + player.getCurrentBombs() + "/" + player.getMaxBombs());
        
        // Poser jusqu'à 5 bombes dans des positions aléatoires accessibles
        int bombsToPlace = 5;
        int bombsPlaced = 0;
        int attempts = 0;
        int maxAttempts = 50; // Éviter les boucles infinies
        
        while (bombsPlaced < bombsToPlace && attempts < maxAttempts) {
            attempts++;
            
            // Générer une position aléatoire accessible
            int x = 1 + (int) (Math.random() * (GRID_COLUMNS - 2));
            int y = 1 + (int) (Math.random() * (GRID_ROWS - 2));
            
            // Vérifier que la case est accessible et libre
            if (grid.isAccessible(x, y) && !isBombAt(x, y) && !isPlayerAt(x, y)) {
                // Créer une vraie bombe avec timer (explosera après 2 secondes)
                Bomb rainBomb = new Bomb(x, y);
                rainBombs.add(rainBomb);
                bombsPlaced++;
                
                System.out.println("Bomb Rain - Bombe " + bombsPlaced + " placée à (" + x + ", " + y + ") - Explosion dans 2s");
            }
        }
        
        System.out.println("=== BOMB RAIN TERMINÉ - " + bombsPlaced + " bombes posées avec timers ===");
        System.out.println("Bombes du joueur après Bomb Rain : " + player.getCurrentBombs() + "/" + player.getMaxBombs() + " (inchangé)");
    }
    
    /**
     * Vérifie si le joueur est à la position donnée
     * @param x Position X
     * @param y Position Y
     * @return true si le joueur est à cette position
     */
    private boolean isPlayerAt(int x, int y) {
        return player.getX() == x && player.getY() == y;
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 