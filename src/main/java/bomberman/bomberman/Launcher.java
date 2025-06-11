package bomberman.bomberman;
//testpcportable
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

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
    
    // Dimensions de la fenêtre de jeu (agrandie x1.5 pour zoom + 96px pour sprite complet)
    private static final int WINDOW_WIDTH = 816;  // 720 + 96px (48px de chaque côté pour sprite complet)
    private static final int WINDOW_HEIGHT = 956; // 624 + 332px pour UI et zone notifications
    
    // Dimensions de la grille (nombre de cases) - Surface jouable: 13x11
    private static final int GRID_COLUMNS = 15;  // 720/48 = 15 cases en largeur (surface totale)
    private static final int GRID_ROWS = 13;     // 624/48 = 13 cases en hauteur (surface totale)
    
    // Hauteur de la grille de jeu (agrandie pour 13 lignes)
    private static final int GAME_AREA_HEIGHT = 624; // 13 * 48 = 624px (surface jouable: 13x11)
    
    // Position de départ du joueur (première case vide disponible)
    private static final int PLAYER_START_X = 1;
    private static final int PLAYER_START_Y = 1;
    private static final int PLAYER2_START_X = 13;  // Coin opposé pour le joueur 2
    private static final int PLAYER2_START_Y = 11;
    
    // Nombre d'ennemis à créer
    private static final int ENEMY_COUNT = 3;
    
    // Limite maximale d'ennemis autorisée par niveau
    private static final int MAX_ENEMIES = 8;
    
    // Fichier de sauvegarde du high score
    private static final String HIGHSCORE_FILE = "highscore.txt";
    
    // Points attribués pour les actions
    private static final int POINTS_ENEMY_KILLED = 100;
    private static final int POINTS_BLOCK_DESTROYED = 10;
    private static final int POINTS_POWERUP_COLLECTED = 50;
    
    // ⏱️ Timer global de 2min30s (150 000ms)
    private static final long GLOBAL_TIMER_DURATION = 150000; // 2 minutes 30 secondes en millisecondes
    private long globalTimerStartTime; // Temps de début du timer global
    private boolean globalTimerActive; // État du timer global
    private long pausedTimeRemaining; // Temps restant quand le timer est en pause
    
    // État du jeu
    private GameState currentState;
    private int gameCounter;  // Compteur de parties
    private int highScore;    // Meilleur score
    private int currentLevel; // Niveau actuel
    private boolean isLevelStarting; // True si la musique de niveau est en cours
    
    // État du menu interactif
    private int selectedMenuIndex = 0;  // Index de l'option sélectionnée (0-2)
    private static final String[] MENU_OPTIONS = {"NORMAL GAME", "COOPERATION", "PASSWORD"};
    private static final boolean[] MENU_OPTIONS_ENABLED = {true, true, false}; // NORMAL GAME et COOPERATION actifs
    
    // Mode de jeu
    private boolean isCooperationMode = false;  // true = mode coopération, false = mode normal
    
    // ✨ **NOUVEAU** : Suivi des animations de victoire en mode coopération
    private boolean player1WinAnimationTriggered = false;  // true si le joueur 1 a déclenché son animation de victoire
    private boolean player2WinAnimationTriggered = false;  // true si le joueur 2 a déclenché son animation de victoire
    
    // Composants du jeu
    private Grid grid;
    private FluidMovementPlayer player;   // ✨ Mouvement fluide pixel par pixel (Joueur 1)
    private FluidMovementPlayer player2;  // ✨ Mouvement fluide pixel par pixel (Joueur 2, uniquement en mode coopération)
    private List<Enemy> enemies;
    private GridRenderer renderer;
    private ExitDoor exitDoor;  // Porte de sortie pour terminer le niveau
    
    // Gestion des bombes et explosions
    private List<Bomb> activeBombs;        // Bombes du joueur
    private List<Bomb> rainBombs;          // Bombes de Bomb Rain (ne comptent pas dans la limite)
    private List<Explosion> activeExplosions;
    
    // Gestion des power-ups
    private List<PowerUp> powerUps;
    
    // Timer d'animation pour les mises à jour
    private AnimationTimer gameTimer;
    
    // Variables pour suivre les spawns d'ennemis programmés
    private List<Timeline> pendingEnemySpawns = new ArrayList<>();
    
    // Gestion du menu pause
    private PauseMenu pauseMenu;
    
    // État du bouton "Retour" dans le panneau des commandes
    private boolean isCommandsReturnButtonSelected = true;  // Sélectionné par défaut
    
    @Override
    public void start(Stage primaryStage) {
        // Initialisation de l'état du jeu
        currentState = GameState.START_MENU;
        gameCounter = 0;
        
        // Charger le high score
        loadHighScore();
        
        // Initialiser le menu pause
        pauseMenu = new PauseMenu();
        
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
        
        // Gestion des événements clavier (pressé et relâché pour mouvement fluide)
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        
        scene.setOnKeyReleased(event -> {
            handleKeyReleased(event.getCode());
        });
        
        // Configuration de l'icône d'application
        
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
        primaryStage.getIcons().add(icon);
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Super Bomberman");
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
            
            // Charger la musique de fond du niveau 1 (format WAV PCM)
            SoundManager.loadSound("theme_world_1", "/music/Theme_World_1.wav");
            
            // Charger le son de fin de niveau
            SoundManager.loadSound("level_clear", "/music/Level_Clear.wav");
            
            // Charger les effets sonores de menu
            SoundManager.loadSoundEffect("menu_cursor", "/music/Menu_Cursor.wav");
            SoundManager.loadSoundEffect("menu_select", "/music/Menu_Select.wav");
            
            // Charger le son de marche de Bomberman
            SoundManager.loadSoundEffect("walking", "/music/Walking.wav");
            
            // Charger le son de mort de Bomberman
            SoundManager.loadSoundEffect("dies", "/music/Dies.wav");
            
            // Charger les sons de bombes (effets optimisés pour latence minimale)
            SoundManager.loadSoundEffect("bomb_place", "/music/Bomb_Place.wav");
            SoundManager.loadSoundEffect("bomb_explode", "/music/Bomb_Explodes.wav");
            
            // Attendre un peu avant de lancer la musique pour permettre l'initialisation
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    // Démarrer la musique d'intro en boucle après délai
                    SoundManager.loop("intro");
                    System.out.println("Musique d'intro lancée avec délai");
                })
            );
            timeline.play();
            
            System.out.println("SoundManager initialisé - Chargement terminé (musique + effets menu/gameplay + musique niveau)");
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
        
        // Initialisation du modèle de données de la grille avec le niveau actuel et support mode coopération
        grid = new Grid(GRID_COLUMNS, GRID_ROWS, currentLevel, isCooperationMode, PLAYER2_START_X, PLAYER2_START_Y);
        
        // Mise à jour du renderer avec la nouvelle grille
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Initialisation du joueur à une position de départ valide (avec mouvement fluide)
        player = new FluidMovementPlayer(PLAYER_START_X, PLAYER_START_Y);
        player.resetScore();  // Reset du score à 0
        
        // Initialiser le joueur 2 en mode coopération uniquement
        if (isCooperationMode) {
            player2 = new FluidMovementPlayer(PLAYER2_START_X, PLAYER2_START_Y);
            player2.resetScore();  // Reset du score à 0
            System.out.println("Mode COOPÉRATION activé - Joueur 2 initialisé en position (" + PLAYER2_START_X + ", " + PLAYER2_START_Y + ")");
        } else {
            player2 = null;  // Pas de player2 en mode normal
        }
        
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
        
        // Générer la porte de sortie cachée dans un bloc destructible
        generateExitDoor();
        
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
                    
                    // Activer l'invincibilité de 10 secondes quand le(s) joueur(s) peut/peuvent bouger
                    player.respawn(player.getX(), player.getY());
                    if (isCooperationMode && player2 != null) {
                        player2.respawn(player2.getX(), player2.getY());
                    }
                    
                    // Démarrer la musique de fond du niveau
                    SoundManager.playLevelMusic(currentLevel);
                    
                    System.out.println("Musique de démarrage terminée - Niveau " + currentLevel + " démarré avec musique de fond !");
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
        
        // Régénérer une nouvelle grille pour le niveau suivant avec le niveau actuel et support mode coopération
        grid = new Grid(GRID_COLUMNS, GRID_ROWS, currentLevel, isCooperationMode, PLAYER2_START_X, PLAYER2_START_Y);
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Remettre le(s) joueur(s) à leur position de départ (mais conserver leurs attributs)
        player.setPixelPosition(FluidMovementPlayer.gridToPixel(PLAYER_START_X), FluidMovementPlayer.gridToPixel(PLAYER_START_Y));
        if (isCooperationMode && player2 != null) {
            player2.setPixelPosition(FluidMovementPlayer.gridToPixel(PLAYER2_START_X), FluidMovementPlayer.gridToPixel(PLAYER2_START_Y));
        }
        
        // Réinitialiser les variables de victoire coopération pour le nouveau niveau
        player1WinAnimationTriggered = false;
        player2WinAnimationTriggered = false;
        
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
        
        // ⏱️ Obtenir le temps restant du timer global
        long globalTimeRemaining = getGlobalTimeRemaining();
        
        // ✨ Choisir le rendu selon le mode
        if (isCooperationMode) {
            // Mode coopération : afficher les deux joueurs
            renderer.renderCooperation(player, player2, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel, exitDoor, globalTimeRemaining);
        } else {
            // Mode normal : afficher un seul joueur
        renderer.render(player, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel, exitDoor, globalTimeRemaining);
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Rendu spécial pour le démarrage de niveau avec affichage "LEVEL X" et overlay
     */
    private void renderLevelStart() {
        // Combiner toutes les bombes pour le rendu
        List<Bomb> allBombs = new ArrayList<>(activeBombs);
        allBombs.addAll(rainBombs);
        
        // ⏱️ Obtenir le temps restant du timer global
        long globalTimeRemaining = getGlobalTimeRemaining();
        
        // ✨ Choisir le rendu selon le mode (sans porte de sortie pendant le démarrage)
        if (isCooperationMode) {
            // Mode coopération : afficher les deux joueurs
            renderer.renderCooperation(player, player2, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel, null, globalTimeRemaining);
        } else {
            // Mode normal : afficher un seul joueur
        renderer.render(player, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel, null, globalTimeRemaining);
        }
        
        // ✨ **NOUVEAU** : Ajouter l'overlay d'introduction avec "LEVEL X"
        renderer.renderLevelIntroOverlay(currentLevel);
    }
    
    /**
     * Crée les ennemis pour le niveau actuel avec difficulté progressive
     */
    private void createEnemiesForLevel() {
        // Calculer le nombre d'ennemis en fonction du niveau (3 + 1 par niveau, max MAX_ENEMIES)
        int enemyCount = Math.min(ENEMY_COUNT + currentLevel - 1, MAX_ENEMIES);
        
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
                enemies.add(new FluidMovementEnemy(x, y));
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
        // ✨ **NOUVEAU** : Gestion spéciale pour l'intro de niveau
        if (currentState == GameState.LEVEL_STARTING) {
            // Continuer à afficher l'overlay d'intro
            renderLevelStart();
            return;
        }
        
        // ✨ **NOUVEAU** : Gestion de l'animation de mort
        if (currentState == GameState.PLAYER_DYING) {
            // En mode coopération, vérifier si au moins un joueur est vivant
            if (isCooperationMode && player2 != null && (player.isAlive() || player2.isAlive())) {
                // Au moins un joueur est vivant : continuer le jeu normalement
                // (l'animation de mort sera gérée par le GridRenderer)
            } else {
                // Mode normal OU les deux joueurs sont morts : geler le jeu
                renderGame();
                return;
            }
        }
        
        // ✨ **NOUVEAU** : Gestion de l'animation de victoire
        if (currentState == GameState.PLAYER_WINNING) {
            // Ne rien mettre à jour (geler le jeu), juste rendre la scène
            // L'animation de victoire est gérée par le GridRenderer
            renderGame();
            return;
        }
        
        // Ne mettre à jour que si le jeu est en cours (pas en pause)
        // En mode coopération avec un joueur mort, on continue si au moins un joueur est vivant
        if (currentState != GameState.RUNNING && 
            !(currentState == GameState.PLAYER_DYING && isCooperationMode && player2 != null && (player.isAlive() || player2.isAlive()))) {
            return;
        }
        
        // ⏱️ Vérifier l'expiration du timer global avant tout le reste
        if (checkGlobalTimerExpired()) {
            // Le timer global a expiré, forcer le rendu pour mettre à jour l'affichage
            renderGame();
            return;
        }
        
        boolean needsRedraw = false;
        
        // Mettre à jour l'invincibilité du joueur et le mouvement fluide
            player.updateInvincibility();
            player.updateTemporaryEffects();
        player.updateWalkingState(); // Mise à jour de l'état de marche pour l'animation
        
        // ✨ **MOUVEMENT FLUIDE** : Mise à jour continue de la position avec collision entre joueurs en mode coopération
        FluidMovementPlayer.PlayerCollisionChecker playerCollisionChecker = null;
        if (isCooperationMode && player2 != null) {
            playerCollisionChecker = this::isPlayerAt;
        }
        player.updateMovement(grid, this::isBombBlockingMovement, playerCollisionChecker);
            
            // Forcer le rendu si le joueur est invincible (pour le clignotement)
            if (player.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain
            if (player.isBombRainActive()) {
                handleBombRain();
                player.deactivateBombRain();
        }
        
        // ✨ **MODE COOPÉRATION** : Mettre à jour le joueur 2 de la même manière
        if (isCooperationMode && player2 != null) {
            player2.updateInvincibility();
            player2.updateTemporaryEffects();
            player2.updateWalkingState();
            
            // Mise à jour du mouvement du joueur 2 avec collision entre joueurs
            player2.updateMovement(grid, this::isBombBlockingMovement, playerCollisionChecker);
            
            // Forcer le rendu si le joueur 2 est invincible
            if (player2.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain du joueur 2
            if (player2.isBombRainActive()) {
                handleBombRain();
                player2.deactivateBombRain();
            }
        }
        
        // Mettre à jour les ennemis seulement si le joueur est vivant
        if (player.isAlive()) {
            for (Enemy enemy : enemies) {
                if (enemy.update(grid, this::isBombBlockingMovement, this::isEnemyAt)) {
                    needsRedraw = true;
                }
            }
        }
        
        // Mettre à jour les bombes actives du joueur
        for (int i = activeBombs.size() - 1; i >= 0; i--) {
            Bomb bomb = activeBombs.get(i);
            
            // ✨ **NOUVEAU** : Mettre à jour la traversabilité avec le propriétaire de la bombe
            FluidMovementPlayer activeBombOwner = bomb.getOwner();
            if (activeBombOwner != null) {
                bomb.updateTraversability(activeBombOwner);
            }
            
            if (bomb.update()) {
                // La bombe a explosé
                createExplosion(bomb);
                activeBombs.remove(i);  // Retirer la bombe de la liste
                
                // Décrémenter le compteur du bon joueur selon qui a posé la bombe
                FluidMovementPlayer explodedBombOwner = bomb.getOwner();
                if (explodedBombOwner != null) {
                    explodedBombOwner.decrementActiveBombs();
                    String playerName = (explodedBombOwner == player) ? "Joueur 1" : "Joueur 2";
                    System.out.println(playerName + " - Bombe explosée - Bombes restantes: " + explodedBombOwner.getCurrentBombs() + "/" + explodedBombOwner.getMaxBombs());
                } else {
                    System.out.println("Bombe sans propriétaire explosée (probablement rain/ennemi)");
                }
                
                needsRedraw = true;
            }
        }
        
        // Mettre à jour les bombes de Bomb Rain (ne comptent pas dans la limite)
        for (int i = rainBombs.size() - 1; i >= 0; i--) {
            Bomb bomb = rainBombs.get(i);
            
            // ✨ **NOUVEAU** : Mettre à jour la traversabilité avec le propriétaire de la bombe (ou joueur 1 pour bombes rain)
            FluidMovementPlayer rainBombOwner = bomb.getOwner();
            if (rainBombOwner != null) {
                bomb.updateTraversability(rainBombOwner);
            } else {
                // Pour les bombes rain, utiliser le joueur 1 par défaut
                bomb.updateTraversability(player);
            }
            
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
        
        // ✨ **NOUVEAU** : Nettoyer les animateurs d'explosion terminés
        renderer.cleanupExplosionAnimators();
        
        // ✨ **NOUVEAU** : Mettre à jour la portée d'explosion du renderer
        renderer.setExplosionRange(player.getRange());
        
        // Vérifier les collisions si au moins un joueur est vivant
        if (player.isAlive() || (isCooperationMode && player2 != null && player2.isAlive())) {
            checkCollisions();
            
            // Vérifier la collecte de power-ups
            if (checkPowerUpCollection()) {
                needsRedraw = true;
            }
            
            // Vérifier si le niveau est terminé (tous les ennemis morts)
            if (checkLevelCompleted()) {
                // En mode coopération, la logique de victoire est gérée dans checkLevelCompleted()
                // En mode normal, démarrer la séquence de victoire
                if (!isCooperationMode) {
                    handlePlayerWin();
                } else {
                    // Mode coopération : passer à l'écran de fin une fois que les deux animations sont déclenchées
                    handleCooperationWin();
                }
                return;
            }
        } else if (currentState == GameState.RUNNING) {
            // Le joueur vient de mourir complètement, passer à l'état GAME_OVER
            // Arrêter la musique de niveau
            SoundManager.stopLevelMusic();
            
            updateHighScore();  // Mettre à jour le high score avant de passer en game over
            currentState = GameState.GAME_OVER;
            renderer.renderGameOverScreen(player);
            System.out.println("=== GAME OVER ===");
            System.out.println("Score final : " + player.getScore());
            System.out.println("Passage à l'état : " + currentState);
            return;
        }
        
        // ⏱️ Forcer le rendu à chaque frame pour mettre à jour le timer visuel
        // même si aucune action du joueur n'a eu lieu
        renderGame();
    }
    
    /**
     * Vérifie toutes les collisions du jeu (mode coopération supporté)
     */
    private void checkCollisions() {
        // En mode normal : ne pas vérifier si le joueur est en train de mourir
        // En mode coopération : continuer à vérifier pour l'autre joueur
        if (!isCooperationMode && player.isDying()) {
            return;
        }
        
        // Vérifier s'il y a des collisions mortelles pour au moins un joueur
        boolean playerDeath = false;
        
        // === VÉRIFICATIONS POUR JOUEUR 1 ===
        if (player.isAlive() && !player.isInvincible() && !player.isDying()) {
            // Collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player, enemy)) {
                    playerDeath = true;
                    break;
                }
            }
            
            // Collision avec explosions (si pas déjà de collision avec ennemi)
            if (!playerDeath && !player.isProtectedFromExplosions() && isInExplosion(player.getX(), player.getY())) {
                playerDeath = true;
            } else if (player.isAlive() && player.hasShield() && isInExplosion(player.getX(), player.getY())) {
                System.out.println("EXPLOSION BLOQUÉE PAR LE BOUCLIER (Joueur 1) !");
            }
        }
        
        // === VÉRIFICATIONS POUR JOUEUR 2 (MODE COOPÉRATION) ===
        if (!playerDeath && isCooperationMode && player2 != null && player2.isAlive() && !player2.isInvincible() && !player2.isDying()) {
            // Collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player2, enemy)) {
                    playerDeath = true;
                    break;
                }
            }
            
            // Collision avec explosions (si pas déjà de collision avec ennemi)
            if (!playerDeath && !player2.isProtectedFromExplosions() && isInExplosion(player2.getX(), player2.getY())) {
                playerDeath = true;
            } else if (player2.isAlive() && player2.hasShield() && isInExplosion(player2.getX(), player2.getY())) {
                System.out.println("EXPLOSION BLOQUÉE PAR LE BOUCLIER (Joueur 2) !");
            }
        }
        
        // Si une mort de joueur est détectée, traiter
        if (playerDeath) {
            handlePlayerDeath();
            return; // Sortir pour ne pas traiter d'autres collisions
        }
        
        // === COLLISIONS AVEC EXPLOSIONS - AUTRES ÉLÉMENTS ===
        for (Explosion explosion : activeExplosions) {
            if (explosion.isActive()) {
                
                // Vérifier si des ennemis sont touchés par l'explosion (ignorer les ennemis invincibles)
                for (Enemy enemy : enemies) {
                    if (enemy.isAlive() && !enemy.isInvincible() && isInExplosion(enemy.getX(), enemy.getY())) {
                        enemy.kill();
                        player.addScore(POINTS_ENEMY_KILLED);  // +100 points pour ennemi tué
                        System.out.println("ENEMY DIED - Explosion at (" + enemy.getX() + ", " + enemy.getY() + ")");
                    } else if (enemy.isAlive() && enemy.isInvincible() && isInExplosion(enemy.getX(), enemy.getY())) {
                        System.out.println("EXPLOSION BLOQUÉE PAR L'INVINCIBILITÉ ENNEMI à (" + enemy.getX() + ", " + enemy.getY() + ")");
                    }
                }
                
                // ✨ **NOUVEAU** : Vérifier si des power-ups visibles sont détruits par l'explosion
                checkPowerUpDestruction();
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
     * ✨ **NOUVEAU** : Vérifie si des power-ups visibles sont détruits par l'explosion
     * Retire les power-ups qui sont touchés par l'explosion
     * IMPORTANT: Les power-ups révélés par l'explosion actuelle ne sont PAS détruits
     */
    private void checkPowerUpDestruction() {
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            
            // Seulement vérifier les power-ups visibles (révélés) ET existants depuis un certain temps
            if (powerUp.isVisible() && powerUp.canBeDestroyed() && isInExplosion(powerUp.getX(), powerUp.getY())) {
                powerUps.remove(i);
                System.out.println("💥 Power-up " + powerUp.getType() + " détruit par explosion à (" + 
                                 powerUp.getX() + ", " + powerUp.getY() + ")");
            }
        }
    }
    
    /**
     * Vérifie la collision entre le joueur et un ennemi avec détection pixel-perfect
     * @param player Le joueur
     * @param enemy L'ennemi
     * @return true s'il y a collision
     */
    private boolean isPlayerEnemyCollision(FluidMovementPlayer player, Enemy enemy) {
        // Si l'ennemi est fluide, utiliser la détection pixel-perfect
        if (enemy instanceof FluidMovementEnemy) {
            FluidMovementEnemy fluidEnemy = (FluidMovementEnemy) enemy;
            
            // Calculer la distance entre les centres
            double playerX = player.getPixelX();
            double playerY = player.getPixelY();
            double enemyX = fluidEnemy.getPixelX();
            double enemyY = fluidEnemy.getPixelY();
            
            double deltaX = Math.abs(playerX - enemyX);
            double deltaY = Math.abs(playerY - enemyY);
            
            // Seuil de collision (environ 3/4 de la taille d'une cellule)
            double collisionThreshold = FluidMovementPlayer.CELL_SIZE * 0.75;
            
            return (deltaX < collisionThreshold && deltaY < collisionThreshold);
        } else {
            // Fallback : collision par grille classique
            return (enemy.getX() == player.getX() && enemy.getY() == player.getY());
        }
    }
    
    /**
     * ✨ **MODIFIÉ** : Crée une explosion à partir d'une bombe et gère les réactions en chaîne
     * @param bomb La bombe à partir de laquelle l'explosion est créée
     */
    private void createExplosion(Bomb bomb) {
        // Première étape : vérifier si cette explosion va révéler la porte (avant destruction)
        boolean willRevealDoor = willExplosionRevealDoor(bomb.getX(), bomb.getY());
        
        // Deuxième étape : révéler les power-ups AVANT de créer l'explosion
        // (car l'explosion va détruire les blocs et nous perdrons l'information)
        revealPowerUpsBeforeExplosion(bomb.getX(), bomb.getY());
        
        // Troisième étape : créer l'explosion qui va détruire les blocs
        Explosion explosion = new Explosion(
            bomb.getX(), 
            bomb.getY(), 
            player.getRange(), // Utiliser la portée du joueur (modifiable par power-ups)
            grid,
            exitDoor  // Passer la porte de sortie pour bloquer l'explosion
        );
        activeExplosions.add(explosion);
        
        // Jouer le son d'explosion de bombe
        SoundManager.playBombExplodeSound();
        
        // ✨ **NOUVEAU** : Vérifier les réactions en chaîne avec d'autres bombes
        checkChainReactions(explosion);
        
        // Vérifier si l'explosion touche la porte de sortie et faire apparaître un ennemi
        // (seulement si cette explosion ne révèle pas la porte)
        if (!willRevealDoor) {
            checkExplosionOnExitDoor(explosion);
        }
    }
    
    /**
     * Vérifie si une explosion va révéler la porte de sortie (avant destruction des blocs)
     * @param bombX Position X de la bombe
     * @param bombY Position Y de la bombe
     * @return true si cette explosion va révéler la porte
     */
    private boolean willExplosionRevealDoor(int bombX, int bombY) {
        int range = player.getRange();
        
        // Vérifier le centre
        if (isExplosionHittingDoorInDestructibleBlock(bombX, bombY)) {
            return true;
        }
        
        // Vérifier vers le haut
        for (int i = 1; i <= range; i++) {
            int yUp = bombY - i;
            if (yUp < 0) break;
            
            TileType tileType = grid.getTileType(bombX, yUp);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                if (isExplosionHittingDoorInDestructibleBlock(bombX, yUp)) {
                    return true;
                }
                break;
            }
        }
        
        // Vérifier vers le bas
        for (int i = 1; i <= range; i++) {
            int yDown = bombY + i;
            if (yDown >= grid.getRows()) break;
            
            TileType tileType = grid.getTileType(bombX, yDown);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                if (isExplosionHittingDoorInDestructibleBlock(bombX, yDown)) {
                    return true;
                }
                break;
            }
        }
        
        // Vérifier vers la gauche
        for (int i = 1; i <= range; i++) {
            int xLeft = bombX - i;
            if (xLeft < 0) break;
            
            TileType tileType = grid.getTileType(xLeft, bombY);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                if (isExplosionHittingDoorInDestructibleBlock(xLeft, bombY)) {
                    return true;
                }
                break;
            }
        }
        
        // Vérifier vers la droite
        for (int i = 1; i <= range; i++) {
            int xRight = bombX + i;
            if (xRight >= grid.getColumns()) break;
            
            TileType tileType = grid.getTileType(xRight, bombY);
            if (tileType == TileType.SOLID) {
                break;
            } else if (tileType == TileType.DESTRUCTIBLE) {
                if (isExplosionHittingDoorInDestructibleBlock(xRight, bombY)) {
                    return true;
                }
                break;
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si une position donnée contient la porte dans un bloc destructible
     * @param x Position en colonne
     * @param y Position en ligne
     * @return true si la porte est à cette position dans un bloc destructible
     */
    private boolean isExplosionHittingDoorInDestructibleBlock(int x, int y) {
        return exitDoor.getX() == x && exitDoor.getY() == y && grid.isDestructible(x, y);
    }
    
    /**
     * ✨ **NOUVEAU** : Vérifie les réactions en chaîne d'une explosion avec d'autres bombes
     * Si l'explosion touche une autre bombe, celle-ci explose immédiatement
     * @param explosion L'explosion à vérifier pour les réactions en chaîne
     */
    private void checkChainReactions(Explosion explosion) {
        List<Bomb> bombsToExplode = new ArrayList<>();
        
        // Vérifier les bombes du joueur
        for (Bomb bomb : activeBombs) {
            if (bomb.isActive() && isBombInExplosion(bomb, explosion)) {
                bombsToExplode.add(bomb);
                System.out.println("🔗 Réaction en chaîne ! Bombe joueur à (" + bomb.getX() + ", " + bomb.getY() + ") touchée par explosion");
            }
        }
        
        // Vérifier les bombes de Bomb Rain
        for (Bomb bomb : rainBombs) {
            if (bomb.isActive() && isBombInExplosion(bomb, explosion)) {
                bombsToExplode.add(bomb);
                System.out.println("🔗 Réaction en chaîne ! Bombe Rain à (" + bomb.getX() + ", " + bomb.getY() + ") touchée par explosion");
            }
        }
        
        // Faire exploser toutes les bombes touchées immédiatement
        for (Bomb bomb : bombsToExplode) {
            explodeBombImmediately(bomb);
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Vérifie si une bombe est dans la zone d'effet d'une explosion
     * @param bomb La bombe à vérifier
     * @param explosion L'explosion à vérifier
     * @return true si la bombe est touchée par l'explosion
     */
    private boolean isBombInExplosion(Bomb bomb, Explosion explosion) {
        for (Explosion.ExplosionCell cell : explosion.getAffectedCells()) {
            if (cell.getX() == bomb.getX() && cell.getY() == bomb.getY()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * ✨ **NOUVEAU** : Fait exploser une bombe immédiatement (réaction en chaîne)
     * @param bomb La bombe à faire exploser
     */
    private void explodeBombImmediately(Bomb bomb) {
        // Retirer la bombe de sa liste respective
        if (activeBombs.contains(bomb)) {
            activeBombs.remove(bomb);
            
            // Décrémenter le compteur du bon joueur selon qui a posé la bombe
            FluidMovementPlayer bombOwner = bomb.getOwner();
            if (bombOwner != null) {
                bombOwner.decrementActiveBombs();
                String playerName = (bombOwner == player) ? "Joueur 1" : "Joueur 2";
                System.out.println("💥 " + playerName + " - Explosion immédiate - Bombes restantes: " + bombOwner.getCurrentBombs() + "/" + bombOwner.getMaxBombs());
            } else {
                System.out.println("💥 Explosion immédiate bombe sans propriétaire");
            }
        } else if (rainBombs.contains(bomb)) {
            rainBombs.remove(bomb);
            System.out.println("💥 Explosion immédiate bombe Rain");
        }
        
        // Créer l'explosion immédiatement (ceci peut déclencher d'autres réactions en chaîne)
        createExplosion(bomb);
    }

    /**
     * Vérifie si une explosion touche la porte de sortie et programme un spawn d'ennemi
     * (appelée seulement si l'explosion ne révèle pas la porte)
     * @param explosion L'explosion à vérifier
     */
    private void checkExplosionOnExitDoor(Explosion explosion) {
        // Vérifier si la porte est visible (révélée)
        if (!exitDoor.isVisible()) {
            return;
        }
        
        // Compter le nombre d'ennemis vivants actuels
        int aliveEnemiesCount = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                aliveEnemiesCount++;
            }
        }
        
        // Calculer la limite d'ennemis pour le niveau actuel (comme dans createEnemiesForLevel)
        int currentLevelMaxEnemies = Math.min(ENEMY_COUNT + currentLevel - 1, MAX_ENEMIES);
        
        // Vérifier si on a atteint la limite maximale d'ennemis pour ce niveau
        if (aliveEnemiesCount >= currentLevelMaxEnemies) {
            System.out.println("Limite d'ennemis atteinte pour le niveau " + currentLevel + " (" + aliveEnemiesCount + "/" + currentLevelMaxEnemies + ")");
            return;
        }
        
        // Vérifier si la porte est dans la zone d'explosion
        boolean doorInExplosion = false;
        for (Explosion.ExplosionCell cell : explosion.getAffectedCells()) {
            if (cell.getX() == exitDoor.getX() && cell.getY() == exitDoor.getY()) {
                doorInExplosion = true;
                break;
            }
        }
        
        // Si la porte est touchée par l'explosion, faire apparaître un ennemi APRÈS l'explosion
        if (doorInExplosion) {
            System.out.println("Explosion sur porte déjà révélée - Spawn d'ennemi programmé");
            // Programmer l'apparition de l'ennemi après que l'explosion soit terminée
            // pour éviter qu'il meure immédiatement
            Timeline delayedSpawn = new Timeline();
            delayedSpawn.getKeyFrames().add(
                new KeyFrame(Duration.millis(1000), e -> { // Augmenté de 600ms à 1000ms pour s'assurer qu'aucune explosion n'est active
                    // Vérifier à nouveau la limite au moment du spawn (au cas où d'autres ennemis seraient morts)
                    int currentAliveCount = 0;
                    for (Enemy enemy : enemies) {
                        if (enemy.isAlive()) {
                            currentAliveCount++;
                        }
                    }
                    
                    if (currentAliveCount < currentLevelMaxEnemies) {
                        // ✨ **SÉCURITÉ** : Vérifier qu'il n'y a pas d'explosion active sur la position de spawn
                        boolean isSpawnPositionSafe = true;
                        for (Explosion activeExplosion : activeExplosions) {
                            if (activeExplosion.isActive() && isInExplosion(exitDoor.getX(), exitDoor.getY())) {
                                isSpawnPositionSafe = false;
                                System.out.println("⚠️ SPAWN ANNULÉ - Explosion encore active sur la position de spawn");
                                break;
                            }
                        }
                        
                        if (isSpawnPositionSafe) {
                        Enemy newEnemy = new FluidMovementEnemy(exitDoor.getX(), exitDoor.getY(), true); // true = avec invincibilité
                        enemies.add(newEnemy);
                        
                        System.out.println("Ennemi spawn avec invincibilité (5s) à (" + exitDoor.getX() + ", " + exitDoor.getY() + ")");
                        }
                    }
                    
                    // Retirer cette Timeline de la liste des spawns en cours
                    pendingEnemySpawns.remove(delayedSpawn);
                    System.out.println("Spawn d'ennemi terminé - Spawns en cours: " + pendingEnemySpawns.size());
                })
            );
            
            // Ajouter la Timeline à la liste des spawns en cours
            pendingEnemySpawns.add(delayedSpawn);
            System.out.println("Spawn d'ennemi programmé - Spawns en cours: " + pendingEnemySpawns.size());
            
            delayedSpawn.play();
        }
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
        
        // Vérifier si la porte de sortie est à cette position et révéler si c'est le cas
        if (exitDoor.getX() == x && exitDoor.getY() == y) {
            exitDoor.reveal();
            System.out.println("Bloc contenant la porte détruit à (" + x + ", " + y + ")");
        }
    }
    
    /**
     * Vérifie si le joueur collecte un power-up et applique l'effet
     * @return true si un power-up a été collecté
     */
    private boolean checkPowerUpCollection() {
        if (isCooperationMode && player2 != null) {
            // Mode coopération : vérifier les deux joueurs et partager les bonus
            return checkPowerUpCollectionCooperation();
        } else {
            // Mode normal : seulement le joueur 1
            return checkPowerUpCollectionSolo();
        }
    }
    
    /**
     * Collecte de power-ups en mode solo (joueur 1 uniquement)
     * @return true si un power-up a été collecté
     */
    private boolean checkPowerUpCollectionSolo() {
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
     * ✨ **NOUVEAU** : Collecte de power-ups en mode coopération (bonus individuels)
     * @return true si un power-up a été collecté
     */
    private boolean checkPowerUpCollectionCooperation() {
        boolean collected = false;
        
        // Parcourir tous les power-ups visibles
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            
            if (!powerUp.isVisible()) {
                continue;
            }
            
            // Vérifier si l'un des deux joueurs collecte le power-up
            FluidMovementPlayer collector = null;
            String playerName = "";
            
            if (powerUp.isAtPosition(player.getX(), player.getY())) {
                collector = player;
                playerName = "Joueur 1";
            } else if (powerUp.isAtPosition(player2.getX(), player2.getY())) {
                collector = player2;
                playerName = "Joueur 2";
            }
            
            if (collector != null) {
                // Donner les points au joueur qui a collecté
                collector.addScore(POINTS_POWERUP_COLLECTED);
                
                // Ajouter notification selon le type de power-up avec le nom du collecteur
                String notificationMessage = getCooperationNotificationMessage(powerUp.getType(), playerName);
                renderer.addNotification(notificationMessage);
                
                // ✨ **INDIVIDUEL** : Appliquer l'effet uniquement au joueur qui a collecté
                powerUp.applyEffect(collector);
                
                System.out.println("🎯 " + playerName + " a collecté " + powerUp.getType() + " - Bonus individuel !");
                
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
            case EXPLOSION_EXPANDER:
                return "EXPLOSION EXPANDER récupéré ! (+1 portée)";
            default:
                return "Power-up récupéré !";
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Génère le message de notification pour un power-up collecté en mode coopération
     * @param type Type de power-up
     * @param playerName Nom du joueur qui a collecté
     * @return Message à afficher
     */
    private String getCooperationNotificationMessage(PowerUpType type, String playerName) {
        switch (type) {
            case EXTRA_BOMB:
                return playerName + " : EXTRA BOMB récupéré ! (+1 bombe max)";
            case EXPLOSION_EXPANDER:
                return playerName + " : EXPLOSION EXPANDER récupéré ! (+1 portée)";
            default:
                return playerName + " : Power-up récupéré !";
        }
    }
    
    /**
     * Vérifie si le niveau est terminé (tous les ennemis morts)
     * @return true si le niveau est terminé
     */
    private boolean checkLevelCompleted() {
        // Vérifier si tous les ennemis sont morts
        boolean allEnemiesDead = true;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                allEnemiesDead = false;
                break;
            }
        }
        
        // Vérifier s'il y a des spawns d'ennemis en cours (Timeline programmées)
        boolean hasPendingSpawns = !pendingEnemySpawns.isEmpty();
        
        // Si tous les ennemis sont morts ET qu'il n'y a pas de spawns programmés, activer la porte
        if (allEnemiesDead && !hasPendingSpawns && !exitDoor.isActivated()) {
            exitDoor.activate();
        }
        
        // Si il y a des spawns programmés, désactiver la porte (au cas où elle était activée)
        if (hasPendingSpawns && exitDoor.isActivated()) {
            exitDoor.deactivate();
            System.out.println("Porte désactivée - Spawn d'ennemi en cours");
        }
        
        // ✨ **MODE COOPÉRATION** : Chaque joueur déclenche son animation quand il atteint la porte
        if (isCooperationMode && player2 != null) {
            // Vérifier si le joueur 1 doit déclencher son animation de victoire
            if (!player1WinAnimationTriggered && player.isAlive() && exitDoor.canUseToExit(player.getX(), player.getY())) {
                player1WinAnimationTriggered = true;
                player.win(); // Déclencher l'animation de victoire du joueur 1
                System.out.println("🎉 Joueur 1 a atteint la porte ! Animation de victoire déclenchée.");
            }
            
            // Vérifier si le joueur 2 doit déclencher son animation de victoire
            if (!player2WinAnimationTriggered && player2.isAlive() && exitDoor.canUseToExit(player2.getX(), player2.getY())) {
                player2WinAnimationTriggered = true;
                player2.win(); // Déclencher l'animation de victoire du joueur 2
                System.out.println("🎉 Joueur 2 a atteint la porte ! Animation de victoire déclenchée.");
            }
            
            // Le niveau se termine seulement quand les DEUX animations sont déclenchées
            return player1WinAnimationTriggered && player2WinAnimationTriggered;
        } else {
            // Mode normal : seulement le joueur 1
        if (exitDoor.canUseToExit(player.getX(), player.getY())) {
            return true; // Le niveau est terminé
            }
        }
        
        // Le niveau n'est pas encore terminé
        return false;
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
            case PAUSED:
                handlePauseInput(keyCode);
                break;
            case COMMANDS_DISPLAY:
                handleCommandsInput(keyCode);
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
     * Gère les événements de touches relâchées selon l'état du jeu (pour mouvement fluide)
     * @param keyCode Le code de la touche relâchée
     */
    private void handleKeyReleased(KeyCode keyCode) {
        switch (currentState) {
            case RUNNING:
                handleGameKeyReleased(keyCode);
                break;
            default:
                // Les autres états n'ont pas besoin de gestion de relâchement
                break;
        }
    }
    
    /**
     * Gère les touches relâchées durant le jeu (mouvement fluide)
     * @param keyCode Le code de la touche relâchée
     */
    private void handleGameKeyReleased(KeyCode keyCode) {
        // Gestion des relâchements selon le mode
        switch (keyCode) {
            // ========== CONTRÔLES JOUEUR 1 (FLÈCHES) ==========
            case UP:
            case DOWN:
            case LEFT:
            case RIGHT:
                if (player.isAlive()) {
                    player.onKeyReleased(keyCode);
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 2 (Z/Q/S/D) - MODE COOPÉRATION UNIQUEMENT ==========
            case Z:
                // Joueur 2 : Relâchement Haut
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.UP);
                }
                break;
            case S:
                // Joueur 2 : Relâchement Bas
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.DOWN);
                }
                break;
            case Q:
                // Joueur 2 : Relâchement Gauche
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.LEFT);
                }
                break;
            case D:
                // Joueur 2 : Relâchement Droite
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.RIGHT);
                }
                break;
            default:
                // Ignorer les autres touches
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

                        // Désactiver le mode coopération
        isCooperationMode = false;
                
        // Réinitialiser les variables de victoire coopération
        player1WinAnimationTriggered = false;
        player2WinAnimationTriggered = false;
                
                // Arrêter la musique d'intro avant de lancer le jeu
                SoundManager.stop("intro");
                System.out.println("Musique d'intro arrêtée");
                
                initializeNewGame();
                break;
                
            case 1: // COOPERATION
                SoundManager.playEffect("menu_select");
                System.out.println("Démarrage du mode COOPERATION...");
                
                // Activer le mode coopération
                isCooperationMode = true;
                
                // Réinitialiser les variables de victoire coopération
                player1WinAnimationTriggered = false;
                player2WinAnimationTriggered = false;
                
                // Arrêter la musique d'intro avant de lancer le jeu
                SoundManager.stop("intro");
                System.out.println("Musique d'intro arrêtée");
                
                initializeNewGame();
                break;
                
            case 2: // PASSWORD
                SoundManager.playEffect("menu_select");
                System.out.println("PASSWORD non implémenté pour l'instant");
                break;
        }
    }
    
    /**
     * Gère les inputs durant le jeu (système de mouvement fluide)
     * @param keyCode Le code de la touche pressée
     */
    private void handleGameInput(KeyCode keyCode) {
        // Vérifier d'abord si le joueur veut mettre en pause
        if (keyCode == KeyCode.ESCAPE) {
            pauseGame();
            return;
        }
        
        // En mode normal : ignorer si le joueur est mort
        // En mode coopération : ignorer si les DEUX joueurs sont morts
        if (isCooperationMode) {
            if (!player.isAlive() && (player2 == null || !player2.isAlive())) {
                return;
            }
        } else {
        if (!player.isAlive()) {
            return;
            }
        }
        
        boolean needsRedraw = false;
        
        // Traiter les actions selon la touche
        switch (keyCode) {
            // ========== CONTRÔLES JOUEUR 1 (FLÈCHES + ESPACE) ==========
            case UP:
            case DOWN:
            case LEFT:
            case RIGHT:
                // ✨ Mouvement fluide : transmettre l'événement au joueur 1
                if (player.isAlive()) {
                    player.onKeyPressed(keyCode);
                }
                break;
            case SPACE:
                // Joueur 1 : Poser une bombe
                if (player.isAlive() && tryPlaceBombPlayer1()) {
                    needsRedraw = true;
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 2 (Z/Q/S/D + SHIFT) - MODE COOPÉRATION UNIQUEMENT ==========
            case Z:
                // Joueur 2 : Haut (uniquement en mode coopération)
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.UP);
                }
                break;
            case S:
                // Joueur 2 : Bas (uniquement en mode coopération)
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.DOWN);
                }
                break;
            case Q:
                // Joueur 2 : Gauche (uniquement en mode coopération)
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.LEFT);
                }
                break;
            case D:
                // Joueur 2 : Droite (uniquement en mode coopération)
                if (isCooperationMode && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.RIGHT);
                }
                break;
            case SHIFT:
                // Joueur 2 : Poser une bombe (uniquement en mode coopération)
                if (isCooperationMode && player2 != null && player2.isAlive() && tryPlaceBombPlayer2()) {
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
     * Gère les inputs dans le menu pause
     * @param keyCode Le code de la touche pressée
     */
    private void handlePauseInput(KeyCode keyCode) {
        boolean needsRedraw = false;
        
        switch (keyCode) {
            case UP:
                pauseMenu.navigateUp();
                needsRedraw = true;
                break;
                
            case DOWN:
                pauseMenu.navigateDown();
                needsRedraw = true;
                break;
                
            case ENTER:
                // Exécuter l'action sélectionnée
                handlePauseMenuSelection();
                break;
                
            case ESCAPE:
                // Reprendre la partie directement
                resumeGame();
                break;
                
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Redessiner le menu pause si nécessaire
        if (needsRedraw) {
            renderPauseMenu();
        }
    }
    
    /**
     * Gère les inputs dans l'écran de game over
     * @param keyCode Le code de la touche pressée
     */
    private void handleGameOverInput(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            System.out.println("Redémarrage du jeu...");
            
            // Arrêter toutes les musiques avant de redémarrer
            SoundManager.stopAllMusic();
            
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
     * Tente de placer une bombe à la position actuelle du joueur 1
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBombPlayer1() {
        // Vérifier si le joueur peut poser une bombe (nouveau système multi-bombes)
        if (player.canPlaceBomb() && !isBombAt(player.getX(), player.getY()) && !isVisibleExitDoorAt(player.getX(), player.getY())) {
            Bomb newBomb = new Bomb(player.getX(), player.getY(), player); // Bombe posée par le joueur 1
            activeBombs.add(newBomb);
            player.incrementActiveBombs();  // Incrémenter le compteur de bombes actives
            
            // Jouer le son de placement de bombe
            SoundManager.playBombPlaceSound();
            
            System.out.println("Joueur 1 - Bombe posée à (" + player.getX() + ", " + player.getY() + ") - Total: " + player.getCurrentBombs() + "/" + player.getMaxBombs());
            return true;
        }
        return false;
    }
    
    /**
     * Tente de placer une bombe à la position actuelle du joueur 2
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBombPlayer2() {
        // Mode coopération uniquement
        if (!isCooperationMode || player2 == null) {
            return false;
        }
        
        // Vérifier si le joueur 2 peut poser une bombe (nouveau système multi-bombes)
        if (player2.canPlaceBomb() && !isBombAt(player2.getX(), player2.getY()) && !isVisibleExitDoorAt(player2.getX(), player2.getY())) {
            Bomb newBomb = new Bomb(player2.getX(), player2.getY(), player2); // Bombe posée par le joueur 2
            activeBombs.add(newBomb);
            player2.incrementActiveBombs();  // Incrémenter le compteur de bombes actives
            
            // Jouer le son de placement de bombe
            SoundManager.playBombPlaceSound();
            
            System.out.println("Joueur 2 - Bombe posée à (" + player2.getX() + ", " + player2.getY() + ") - Total: " + player2.getCurrentBombs() + "/" + player2.getMaxBombs());
            return true;
        }
        return false;
    }
    
    /**
     * Vérifie si la porte de sortie visible est à la position donnée
     * @param x Position X
     * @param y Position Y
     * @return true si la porte de sortie visible est à cette position
     */
    private boolean isVisibleExitDoorAt(int x, int y) {
        return exitDoor != null && exitDoor.isVisible() && exitDoor.getX() == x && exitDoor.getY() == y;
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
                // Créer une bombe de "Bomb Rain", non-traversable par défaut
                Bomb rainBomb = new Bomb(x, y, false);
                rainBombs.add(rainBomb);
                bombsPlaced++;
                
                // Jouer le son de placement de bombe (avec un délai pour éviter la cacophonie)
                if (bombsPlaced == 1) {
                    SoundManager.playBombPlaceSound();
                }
                
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
        return isPlayerAt(x, y, null);
    }
    
    /**
     * Vérifie s'il y a un joueur à une position spécifique (pour les collisions entre joueurs)
     * @param x Position en colonne
     * @param y Position en ligne  
     * @param excludePlayer Le joueur à exclure de la vérification (pour éviter l'auto-collision)
     * @return true s'il y a un autre joueur à cette position, false sinon
     */
    private boolean isPlayerAt(int x, int y, FluidMovementPlayer excludePlayer) {
        // Vérifier le joueur 1 (s'il n'est pas exclu)
        if (player != null && player != excludePlayer && player.isAlive() && player.getX() == x && player.getY() == y) {
            return true;
        }
        
        // Vérifier le joueur 2 en mode coopération (s'il n'est pas exclu)
        if (isCooperationMode && player2 != null && player2 != excludePlayer && player2.isAlive() && player2.getX() == x && player2.getY() == y) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Génère une porte de sortie cachée dans un bloc destructible
     * Cherche une position aléatoire parmi les blocs destructibles disponibles
     */
    private void generateExitDoor() {
        // Liste de toutes les positions de blocs destructibles
        List<Point2D> destructiblePositions = new ArrayList<>();
        
        // Collecter toutes les positions de blocs destructibles
        for (int row = 1; row < grid.getRows() - 1; row++) {
            for (int col = 1; col < grid.getColumns() - 1; col++) {
                if (grid.isDestructible(col, row)) {
                    // Ne pas placer la porte trop près du joueur (minimum 3 cases)
                    int distanceX = Math.abs(col - player.getX());
                    int distanceY = Math.abs(row - player.getY());
                    if (distanceX + distanceY >= 3) {
                        destructiblePositions.add(new Point2D(col, row));
                    }
                }
            }
        }
        
        // S'il n'y a pas de blocs destructibles, placer la porte dans un coin éloigné
        if (destructiblePositions.isEmpty()) {
            int x = grid.getColumns() - 2;
            int y = grid.getRows() - 2;
            exitDoor = new ExitDoor(x, y);
            System.out.println("Porte de sortie placée en position de secours (" + x + ", " + y + ")");
            return;
        }
        
        // Choisir une position aléatoire parmi les blocs destructibles
        int randomIndex = (int) (Math.random() * destructiblePositions.size());
        Point2D selectedPosition = destructiblePositions.get(randomIndex);
        
        // Créer la porte de sortie
        exitDoor = new ExitDoor((int) selectedPosition.getX(), (int) selectedPosition.getY());
        System.out.println("Porte de sortie cachée en position (" + exitDoor.getX() + ", " + exitDoor.getY() + ")");
        
        // ⏱️ Démarre le timer global de 2min30s
        startGlobalTimer();
    }
    
    /**
     * ⏱️ Démarre le timer global de 2min30s
     */
    private void startGlobalTimer() {
        globalTimerStartTime = System.currentTimeMillis();
        globalTimerActive = true;
        pausedTimeRemaining = GLOBAL_TIMER_DURATION; // Initialiser le temps en pause
        System.out.println("⏱️ Timer global démarré - 2min30s avant perte de vie automatique");
    }
    
    /**
     * ⏱️ Réinitialise le timer global
     */
    private void resetGlobalTimer() {
        globalTimerStartTime = System.currentTimeMillis();
        globalTimerActive = true;
        pausedTimeRemaining = GLOBAL_TIMER_DURATION; // Réinitialiser le temps en pause
        System.out.println("⏱️ Timer global réinitialisé");
    }
    
    /**
     * ⏱️ Arrête le timer global
     */
    private void stopGlobalTimer() {
        globalTimerActive = false;
        System.out.println("⏱️ Timer global arrêté");
    }
    
    /**
     * ⏱️ Obtient le temps restant du timer global en millisecondes
     * @return temps restant (0 si expiré ou inactif)
     */
    private long getGlobalTimeRemaining() {
        if (!globalTimerActive) {
            // Si le timer est en pause, retourner le temps restant sauvegardé
            return pausedTimeRemaining;
        }
        
        long elapsed = System.currentTimeMillis() - globalTimerStartTime;
        long remaining = GLOBAL_TIMER_DURATION - elapsed;
        return Math.max(0, remaining);
    }
    
    /**
     * ⏱️ Vérifie si le timer global a expiré et gère la perte de vie automatique
     * @return true si le timer a expiré et une action a été prise
     */
    private boolean checkGlobalTimerExpired() {
        if (!globalTimerActive) {
            return false;
        }
        
        long remaining = getGlobalTimeRemaining();
        
        if (remaining <= 0) {
            // Timer expiré - le joueur perd une vie
            System.out.println("⏰ TIMER GLOBAL EXPIRÉ - Le joueur perd une vie automatiquement");
            
            if (player.isAlive() && !player.isDying()) {
                handlePlayerDeath();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * ✨ **NOUVEAU** : Vérifie si une position est bloquée par des bombes pour une entité donnée
     * @param x Position X à vérifier
     * @param y Position Y à vérifier  
     * @param isPlayer True si l'entité est le joueur, false pour les ennemis
     * @return true si la position est bloquée par une bombe, false sinon
     */
    private boolean isBombBlockingMovement(int x, int y, boolean isPlayer) {
        // Vérifier les bombes du joueur
        for (Bomb bomb : activeBombs) {
            if (bomb.isActive() && bomb.blocksMovementFor(x, y, isPlayer)) {
                return true;
            }
        }
        
        // Vérifier les bombes de Bomb Rain
        for (Bomb bomb : rainBombs) {
            if (bomb.isActive() && bomb.blocksMovementFor(x, y, isPlayer)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * ✨ **NOUVEAU** : Méthode centralisée pour détecter les collisions entre ennemis
     * Empêche qu'un ennemi se déplace sur une case déjà occupée par un autre ennemi vivant
     * @param x Position X à vérifier
     * @param y Position Y à vérifier
     * @param excludeEnemy L'ennemi à exclure de la vérification (celui qui veut se déplacer)
     * @return true si un autre ennemi vivant occupe cette position
     */
    private boolean isEnemyAt(int x, int y, Enemy excludeEnemy) {
        for (Enemy enemy : enemies) {
            // Ignorer l'ennemi qui fait la demande et les ennemis morts
            if (enemy == excludeEnemy || !enemy.isAlive()) {
                continue;
            }
            
            // Vérifier si cet ennemi occupe la position cible
            if (enemy.getX() == x && enemy.getY() == y) {
                return true; // Position occupée par un autre ennemi
            }
        }
        
        return false; // Position libre d'ennemis
    }
    
    /**
     * Met le jeu en pause et affiche le menu pause
     */
    private void pauseGame() {
        if (currentState == GameState.RUNNING) {
            currentState = GameState.PAUSED;
            
            // ⏱️ Sauvegarder le temps restant et arrêter le timer
            if (globalTimerActive) {
                pausedTimeRemaining = getGlobalTimeRemaining();
                globalTimerActive = false;
            }
            
            pauseMenu.reset();  // Remettre la sélection sur la première option
            renderPauseMenu();
            System.out.println("=== JEU MIS EN PAUSE ===");
        }
    }
    
    /**
     * Reprend le jeu depuis le menu pause
     */
    private void resumeGame() {
        if (currentState == GameState.PAUSED) {
            currentState = GameState.RUNNING;
            
            // ⏱️ Reprendre le timer global avec le temps restant sauvegardé
            if (!globalTimerActive && pausedTimeRemaining > 0) {
                globalTimerStartTime = System.currentTimeMillis() - (GLOBAL_TIMER_DURATION - pausedTimeRemaining);
                globalTimerActive = true;
            }
            
            renderGame();
            System.out.println("=== JEU REPRIS ===");
        }
    }
    
    /**
     * Affiche le menu pause par-dessus le jeu figé
     */
    private void renderPauseMenu() {
        // D'abord afficher le jeu figé en arrière-plan
        renderGame();
        
        // Puis afficher le menu pause par-dessus
        renderer.renderPauseMenu(pauseMenu);
    }
    
    /**
     * Gère les inputs dans le panneau des commandes
     * @param keyCode Le code de la touche pressée
     */
    private void handleCommandsInput(KeyCode keyCode) {
        boolean needsRedraw = false;
        
        switch (keyCode) {
            case UP:
            case DOWN:
                // Pour l'instant, un seul bouton donc pas de navigation
                // Mais on peut jouer un son pour le feedback
                SoundManager.playEffect("menu_cursor");
                break;
                
            case ENTER:
                // Sélectionner le bouton "Retour"
                if (isCommandsReturnButtonSelected) {
                    SoundManager.playEffect("menu_select");
                    hideCommands();
                }
                break;
                
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Pas besoin de redessiner car il n'y a qu'un bouton pour l'instant
    }
    
    /**
     * Affiche le panneau des commandes
     */
    private void showCommands() {
        currentState = GameState.COMMANDS_DISPLAY;
        isCommandsReturnButtonSelected = true;  // Bouton sélectionné par défaut
        renderer.renderCommandsPanel(isCommandsReturnButtonSelected);
        System.out.println("=== PANNEAU COMMANDES AFFICHÉ ===");
    }
    
    /**
     * Cache le panneau des commandes et retourne au menu pause
     */
    private void hideCommands() {
        currentState = GameState.PAUSED;
        renderPauseMenu();
        System.out.println("=== RETOUR AU MENU PAUSE ===");
    }
    
    /**
     * Traite la sélection d'une option du menu pause
     */
    private void handlePauseMenuSelection() {
        SoundManager.playEffect("menu_select");
        
        PauseMenu.PauseAction action = pauseMenu.getSelectedAction();
        System.out.println("Action sélectionnée dans le menu pause : " + action);
        
        switch (action) {
            case RESUME:
                resumeGame();
                break;
                
            case RESTART:
                System.out.println("Redémarrage de la partie...");
                
                // Arrêter toutes les musiques
                SoundManager.stopAllMusic();
                
                // Reinitialiser une nouvelle partie
                initializeNewGame();
                break;
                
            case COMMANDS:
                System.out.println("Affichage des commandes...");
                showCommands();
                break;
                
            case MAIN_MENU:
                System.out.println("Retour au menu principal...");
                
                // Arrêter toutes les musiques
                SoundManager.stopAllMusic();
                
                // Nettoyer les spawns d'ennemis en attente
                for (Timeline timeline : pendingEnemySpawns) {
                    timeline.stop();
                }
                pendingEnemySpawns.clear();
                
                // Arrêter le timer de jeu
                if (gameTimer != null) {
                    gameTimer.stop();
                    gameTimer = null; // <-- AJOUT DE CETTE LIGNE
                }
                
                // Retourner au menu principal
                currentState = GameState.START_MENU;
                selectedMenuIndex = 0;
                
                // Relancer la musique d'intro
                SoundManager.loop("intro");
                
                // Afficher le menu principal
                renderer.renderStartMenu(selectedMenuIndex, MENU_OPTIONS, MENU_OPTIONS_ENABLED);
                break;
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Gère la séquence de mort du joueur (avec support mode coopération)
     */
    private void handlePlayerDeath() {
        // Déterminer quel joueur est mort en vérifiant les collisions
        FluidMovementPlayer dyingPlayer = null;
        
        if (player.isAlive() && !player.isInvincible()) {
            // Vérifier si le joueur 1 doit mourir
            boolean player1ShouldDie = false;
            
            // Vérifier collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player, enemy)) {
                    player1ShouldDie = true;
                    break;
                }
            }
            
            // Vérifier collision avec explosions
            if (!player1ShouldDie && !player.isProtectedFromExplosions()) {
                if (isInExplosion(player.getX(), player.getY())) {
                    player1ShouldDie = true;
                }
            }
            
            if (player1ShouldDie) {
                dyingPlayer = player;
            }
        }
        
        // En mode coopération, vérifier aussi le joueur 2
        if (isCooperationMode && player2 != null && player2.isAlive() && !player2.isInvincible() && dyingPlayer == null) {
            boolean player2ShouldDie = false;
            
            // Vérifier collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player2, enemy)) {
                    player2ShouldDie = true;
                    break;
                }
            }
            
            // Vérifier collision avec explosions
            if (!player2ShouldDie && !player2.isProtectedFromExplosions()) {
                if (isInExplosion(player2.getX(), player2.getY())) {
                    player2ShouldDie = true;
                }
            }
            
            if (player2ShouldDie) {
                dyingPlayer = player2;
            }
        }
        
        // Si aucun joueur ne doit mourir, ne rien faire
        if (dyingPlayer == null) {
            return;
        }
        
        // 1. Initialiser la séquence de mort dans le joueur qui meurt
        dyingPlayer.kill(); // Ceci met isDying à true et joue le son
        
        // 2. En mode coopération, ne geler le jeu que si c'est le dernier joueur vivant qui meurt
        boolean shouldFreezeGame = false;
        if (!isCooperationMode) {
            // Mode normal : toujours geler quand le joueur meurt
            shouldFreezeGame = true;
        } else {
            // Mode coopération : geler seulement si l'autre joueur est aussi mort/mourant
            FluidMovementPlayer otherPlayer = (dyingPlayer == player) ? player2 : player;
            if (otherPlayer == null || !otherPlayer.isAlive() || otherPlayer.isDying()) {
                shouldFreezeGame = true;
            }
        }
        
        if (shouldFreezeGame) {
            currentState = GameState.PLAYER_DYING;
            System.out.println("CHANGEMENT D'ÉTAT -> PLAYER_DYING (" + (dyingPlayer == player ? "Joueur 1" : "Joueur 2") + ")");
        } else {
            System.out.println("MODE COOPÉRATION -> Un joueur meurt mais l'autre continue (" + (dyingPlayer == player ? "Joueur 1" : "Joueur 2") + " meurt)");
        }
        
        // 3. Référence finale pour le callback
        final FluidMovementPlayer finalDyingPlayer = dyingPlayer;
        
        // 4. Le GridRenderer va maintenant détecter cet état et démarrer l'animation
        renderer.setDeathAnimationCallback(() -> {
            // Ce code sera exécuté à la fin de l'animation de mort
            
            // 5. Terminer la séquence de mort (décrémenter la vie)
            finalDyingPlayer.completeDeathSequence();
            
            // 6. En mode coopération, vérifier si les DEUX joueurs sont morts
            if (isCooperationMode) {
                boolean bothPlayersDead = !player.isAlive() && (player2 == null || !player2.isAlive());
                
                if (bothPlayersDead) {
                    // Les deux joueurs sont morts : Game Over
                    SoundManager.stopLevelMusic();
                    updateHighScore();
                    currentState = GameState.GAME_OVER;
                    renderer.renderGameOverScreen(player); // Afficher le game over
                    System.out.println("=== GAME OVER COOPÉRATION - Les deux joueurs sont morts ===");
                } else {
                    // Au moins un joueur est encore vivant : continuer le jeu
                    if (finalDyingPlayer.isAlive()) {
                        // Le joueur qui est mort a encore des vies : respawn
                        int respawnX = (finalDyingPlayer == player) ? PLAYER_START_X : PLAYER2_START_X;
                        int respawnY = (finalDyingPlayer == player) ? PLAYER_START_Y : PLAYER2_START_Y;
                        finalDyingPlayer.respawn(respawnX, respawnY);
                        System.out.println((finalDyingPlayer == player ? "Joueur 1" : "Joueur 2") + " respawn avec " + finalDyingPlayer.getLives() + " vies restantes");
                    }
                    
                    currentState = GameState.RUNNING; // Reprendre le jeu
                    System.out.println("Mode coopération : le jeu continue avec au moins un joueur vivant");
                }
            } else {
                // Mode normal : gestion classique
                if (finalDyingPlayer.isAlive()) {
                    // Le joueur a encore des vies : respawn
                    finalDyingPlayer.respawn(PLAYER_START_X, PLAYER_START_Y);
                    resetGlobalTimer(); // Réinitialiser le timer
                    currentState = GameState.RUNNING; // Reprendre le jeu
                    System.out.println("Joueur respawn avec " + finalDyingPlayer.getLives() + " vies restantes - État -> RUNNING");
                } else {
                    // Le joueur n'a plus de vies : Game Over
                    SoundManager.stopLevelMusic();
                    updateHighScore();
                    currentState = GameState.GAME_OVER;
                    renderer.renderGameOverScreen(finalDyingPlayer);
                    System.out.println("=== GAME OVER ===");
                }
            }
        });
    }
    
    /**
     * ✨ **NOUVEAU** : Gère la fin de niveau en mode coopération (les deux animations sont déclenchées)
     */
    private void handleCooperationWin() {
        // 1. Changer l'état du jeu pour geler l'action pendant les animations
        currentState = GameState.PLAYER_WINNING;
        System.out.println("CHANGEMENT D'ÉTAT -> PLAYER_WINNING (MODE COOPÉRATION)");
        
        // 2. ✨ **NOUVEAU** : Arrêter la musique de niveau et jouer immédiatement Level_Clear.wav
        SoundManager.stopLevelMusic();
        SoundManager.playLevelClearSound();
        System.out.println("🎵 Musique Level_Clear.wav lancée pour la victoire coopération");
        
        // 3. Attendre que toutes les animations de victoire soient terminées avant d'afficher l'écran de fin
        // Le GridRenderer va gérer l'affichage des animations des deux joueurs
        // Une fois les deux animations terminées, on passe à l'écran de niveau terminé
        renderer.setWinAnimationCallback(() -> {
            // Ce code sera exécuté quand toutes les animations de victoire sont terminées
            
            // 4. Terminer les séquences de victoire pour les deux joueurs
            player.completeWinSequence();
            if (player2 != null) {
                player2.completeWinSequence();
            }
            
            // 5. Passer à l'écran de niveau terminé (la musique continue)
            currentState = GameState.LEVEL_COMPLETED;
            renderer.renderLevelCompletedScreen(currentLevel, player);
            System.out.println("=== NIVEAU " + currentLevel + " TERMINÉ (MODE COOPÉRATION) ===");
            System.out.println("Passage à l'état : " + currentState);
        });
    }
    
    /**
     * ✨ **NOUVEAU** : Gère la séquence de victoire du joueur (mode normal)
     */
    private void handlePlayerWin() {
        // 1. Initialiser la séquence de victoire dans le joueur
        if (player instanceof FluidMovementPlayer) {
            ((FluidMovementPlayer) player).win(); // Ceci met isWinning à true
        }
        
        // 2. Changer l'état du jeu pour geler l'action
        currentState = GameState.PLAYER_WINNING;
        System.out.println("CHANGEMENT D'ÉTAT -> PLAYER_WINNING");
        
        // 3. ✨ **NOUVEAU** : Arrêter la musique de niveau et jouer immédiatement Level_Clear.wav
        SoundManager.stopLevelMusic();
        SoundManager.playLevelClearSound();
        System.out.println("🎵 Musique Level_Clear.wav lancée au début de l'animation de victoire");
        
        // 4. Le GridRenderer va maintenant détecter cet état et démarrer l'animation
        // Nous devons lui dire quoi faire quand l'animation est terminée.
        renderer.setWinAnimationCallback(() -> {
            // Ce code sera exécuté à la fin de l'animation de victoire
            
            // 5. Terminer la séquence de victoire
            if (player instanceof FluidMovementPlayer) {
                ((FluidMovementPlayer) player).completeWinSequence();
            }
            
            // 6. Passer à l'écran de niveau terminé (la musique continue)
            currentState = GameState.LEVEL_COMPLETED;
            renderer.renderLevelCompletedScreen(currentLevel, player);
            System.out.println("=== NIVEAU " + currentLevel + " TERMINÉ ===");
            System.out.println("Passage à l'état : " + currentState);
        });
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 