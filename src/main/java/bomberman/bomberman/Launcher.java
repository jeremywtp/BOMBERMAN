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
    private static final int PLAYER3_START_X = 1;   // Coin inférieur gauche pour le joueur 3 (mode Battle 4 joueurs)
    private static final int PLAYER3_START_Y = 11;
    private static final int PLAYER4_START_X = 13;  // Coin supérieur droit pour le joueur 4 (mode Battle 4 joueurs)
    private static final int PLAYER4_START_Y = 1;
    
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
    private int selectedMenuIndex = 0;  // Index de l'option sélectionnée (0-4)
    private static final String[] MENU_OPTIONS = {"NORMAL GAME", "COOPERATION", "BATTLE MODE", "THEMES", "PASSWORD"};
    private static final boolean[] MENU_OPTIONS_ENABLED = {true, true, true, true, false}; // NORMAL GAME, COOPERATION, BATTLE MODE et THEMES actifs
    
    // Mode de jeu
    private boolean isCooperationMode = false;  // true = mode coopération, false = mode normal
    private boolean isBattleMode = false;       // true = mode battle, false = autre mode
    
    // ✨ **NOUVEAU** : Suivi des animations de victoire en mode coopération/battle
    private boolean player1WinAnimationTriggered = false;  // true si le joueur 1 a déclenché son animation de victoire
    private boolean player2WinAnimationTriggered = false;  // true si le joueur 2 a déclenché son animation de victoire
    private boolean player3WinAnimationTriggered = false;  // true si le joueur 3 a déclenché son animation de victoire (mode Battle 4 joueurs)
    private boolean player4WinAnimationTriggered = false;  // true si le joueur 4 a déclenché son animation de victoire (mode Battle 4 joueurs)
    
    // Composants du jeu
    private Grid grid;
    private FluidMovementPlayer player;   // ✨ Mouvement fluide pixel par pixel (Joueur 1)
    private FluidMovementPlayer player2;  // ✨ Mouvement fluide pixel par pixel (Joueur 2, uniquement en mode coopération/battle)
    private FluidMovementPlayer player3;  // ✨ Mouvement fluide pixel par pixel (Joueur 3, uniquement en mode Battle 4 joueurs)
    private FluidMovementPlayer player4;  // ✨ Mouvement fluide pixel par pixel (Joueur 4, uniquement en mode Battle 4 joueurs)
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
    
    // Gestionnaire de thèmes
    private ThemeSelector themeSelector;
    
    // État du bouton "Retour" dans le panneau des commandes
    private boolean isCommandsReturnButtonSelected = true;  // Sélectionné par défaut
    
    // Gestionnaire de menus FXML
    private FXMLMenuManager fxmlMenuManager;
    private boolean useFXMLMenus = true;  // Switch pour utiliser FXML ou Canvas
    
    @Override
    public void start(Stage primaryStage) {
        // Initialisation de l'état du jeu
        currentState = GameState.START_MENU;
        gameCounter = 0;
        
        // Charger le high score
        loadHighScore();
        
        // Initialiser le menu pause
        pauseMenu = new PauseMenu();
        
        // Initialiser le gestionnaire de thèmes
        themeSelector = new ThemeSelector();
        
        // Initialiser le gestionnaire de menus FXML
        fxmlMenuManager = new FXMLMenuManager(primaryStage);
        fxmlMenuManager.setGameController(this);
        fxmlMenuManager.setThemeSelector(themeSelector);
        
        // Initialiser le gestionnaire de sons
        initializeSoundManager();
        
        // Création du canvas pour le dessin
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialisation du renderer
        renderer = new GridRenderer(canvas, null);  // Pas de grille au début
        
        // Affichage initial du menu
        renderer.renderStartMenu(selectedMenuIndex, MENU_OPTIONS, MENU_OPTIONS_ENABLED);
        
        // Configuration de la scène de jeu (Canvas)
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene gameScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier (pressé et relâché pour mouvement fluide)
        gameScene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        
        gameScene.setOnKeyReleased(event -> {
            handleKeyReleased(event.getCode());
        });
        
        // Configurer le gestionnaire FXML avec la scène de jeu
        fxmlMenuManager.setGameScene(gameScene);
        
        // Configuration de l'icône d'application
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
        primaryStage.getIcons().add(icon);
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Super Bomberman");
        primaryStage.setResizable(false);
        
        // Afficher le menu FXML ou Canvas selon la préférence
        if (useFXMLMenus) {
            fxmlMenuManager.showMainMenu();
        } else {
            primaryStage.setScene(gameScene);
            // Donner le focus à la scène pour capturer les événements clavier
            gameScene.getRoot().requestFocus();
        }
        
        primaryStage.show();
        
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
        
        // Initialisation du modèle de données de la grille avec le niveau actuel et support mode coopération/battle
        grid = new Grid(GRID_COLUMNS, GRID_ROWS, currentLevel, isCooperationMode, isBattleMode, PLAYER2_START_X, PLAYER2_START_Y);
        
        // Mise à jour du renderer avec la nouvelle grille
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Initialisation du joueur à une position de départ valide (avec mouvement fluide)
        player = new FluidMovementPlayer(PLAYER_START_X, PLAYER_START_Y);
        player.resetScore();  // Reset du score à 0
        
        // Initialiser le joueur 2 en mode coopération ou battle
        if (isCooperationMode || isBattleMode) {
            player2 = new FluidMovementPlayer(PLAYER2_START_X, PLAYER2_START_Y);
            player2.resetScore();  // Reset du score à 0
            if (isCooperationMode) {
                System.out.println("Mode COOPÉRATION activé - Joueur 2 initialisé en position (" + PLAYER2_START_X + ", " + PLAYER2_START_Y + ")");
            } else if (isBattleMode) {
                System.out.println("Mode BATTLE 4 JOUEURS activé - Joueur 2 initialisé en position (" + PLAYER2_START_X + ", " + PLAYER2_START_Y + ")");
            }
        } else {
            player2 = null;  // Pas de player2 en mode normal
        }
        
        // Initialiser les joueurs 3 et 4 uniquement en mode Battle
        if (isBattleMode) {
            player3 = new FluidMovementPlayer(PLAYER3_START_X, PLAYER3_START_Y);
            player3.resetScore();  // Reset du score à 0
            player4 = new FluidMovementPlayer(PLAYER4_START_X, PLAYER4_START_Y);
            player4.resetScore();  // Reset du score à 0
            System.out.println("Mode BATTLE 4 JOUEURS - Joueur 3 initialisé en position (" + PLAYER3_START_X + ", " + PLAYER3_START_Y + ")");
            System.out.println("Mode BATTLE 4 JOUEURS - Joueur 4 initialisé en position (" + PLAYER4_START_X + ", " + PLAYER4_START_Y + ")");
        } else {
            player3 = null;  // Pas de player3 en mode normal/coopération
            player4 = null;  // Pas de player4 en mode normal/coopération
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
                    if ((isCooperationMode || isBattleMode) && player2 != null) {
                        player2.respawn(player2.getX(), player2.getY());
                    }
                    // ✨ **CORRECTION** : Ajouter l'invincibilité pour les joueurs 3 et 4 en mode Battle
                    if (isBattleMode && player3 != null) {
                        player3.respawn(player3.getX(), player3.getY());
                    }
                    if (isBattleMode && player4 != null) {
                        player4.respawn(player4.getX(), player4.getY());
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
        
        // Régénérer une nouvelle grille pour le niveau suivant avec le niveau actuel et support mode coopération/battle
        grid = new Grid(GRID_COLUMNS, GRID_ROWS, currentLevel, isCooperationMode, isBattleMode, PLAYER2_START_X, PLAYER2_START_Y);
        renderer = new GridRenderer(renderer.getCanvas(), grid);
        
        // Remettre le(s) joueur(s) à leur position de départ (mais conserver leurs attributs)
        player.setPixelPosition(FluidMovementPlayer.gridToPixel(PLAYER_START_X), FluidMovementPlayer.gridToPixel(PLAYER_START_Y));
        if ((isCooperationMode || isBattleMode) && player2 != null) {
            player2.setPixelPosition(FluidMovementPlayer.gridToPixel(PLAYER2_START_X), FluidMovementPlayer.gridToPixel(PLAYER2_START_Y));
        }
        if (isBattleMode && player3 != null) {
            player3.setPixelPosition(FluidMovementPlayer.gridToPixel(PLAYER3_START_X), FluidMovementPlayer.gridToPixel(PLAYER3_START_Y));
        }
        if (isBattleMode && player4 != null) {
            player4.setPixelPosition(FluidMovementPlayer.gridToPixel(PLAYER4_START_X), FluidMovementPlayer.gridToPixel(PLAYER4_START_Y));
        }
        
        // Réinitialiser les variables de victoire coopération/battle pour le nouveau niveau
        player1WinAnimationTriggered = false;
        player2WinAnimationTriggered = false;
        player3WinAnimationTriggered = false;
        player4WinAnimationTriggered = false;
        
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
        } else if (isBattleMode) {
            // Mode battle : afficher les quatre joueurs en mode battle
            renderer.renderBattle(player, player2, player3, player4, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel, exitDoor, globalTimeRemaining);
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
        } else if (isBattleMode) {
            // Mode battle : afficher les quatre joueurs en mode battle
            renderer.renderBattle(player, player2, player3, player4, enemies, allBombs, activeExplosions, powerUps, highScore, currentLevel, null, globalTimeRemaining);
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
        // ✨ **BATTLE MODE** : Pas d'ennemis en mode battle (1v1 pur)
        if (isBattleMode) {
            System.out.println("Mode BATTLE : Aucun ennemi créé (mode 1v1 pur)");
            return;
        }
        
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
            // En mode battle, le jeu continue toujours même si un joueur meurt
            if ((isCooperationMode && player2 != null && (player.isAlive() || player2.isAlive())) || isBattleMode) {
                // Au moins un joueur est vivant : continuer le jeu normalement
                // (l'animation de mort sera gérée par le GridRenderer)
            } else {
                // Mode normal OU les deux joueurs sont morts en coopération : geler le jeu
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
        // En mode battle, on continue même si un joueur est mort
        if (currentState != GameState.RUNNING && 
            !(currentState == GameState.PLAYER_DYING && isCooperationMode && player2 != null && (player.isAlive() || player2.isAlive())) &&
            !(currentState == GameState.PLAYER_DYING && isBattleMode)) {
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
        
        // ✨ **MOUVEMENT FLUIDE** : Mise à jour continue de la position avec collision entre joueurs en mode coopération/battle
        FluidMovementPlayer.PlayerCollisionChecker playerCollisionChecker = null;
        if ((isCooperationMode || isBattleMode) && player2 != null) {
            playerCollisionChecker = this::isPlayerAt;
        }
        if (!player.isDying()) {
            player.updateMovement(grid, this::isBombBlockingMovement, playerCollisionChecker);
        }
            
            // Forcer le rendu si le joueur est invincible (pour le clignotement)
            if (player.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain
            if (player.isBombRainActive()) {
                handleBombRain();
                player.deactivateBombRain();
        }
        
        // ✨ **MODE COOPÉRATION/BATTLE** : Mettre à jour le joueur 2 de la même manière
        if ((isCooperationMode || isBattleMode) && player2 != null) {
            player2.updateInvincibility();
            player2.updateTemporaryEffects();
            player2.updateWalkingState(); // Mise à jour de l'état de marche pour l'animation
            
            // ✨ **MOUVEMENT FLUIDE** : Mise à jour continue de la position avec collision entre joueurs
            if (!player2.isDying()) {
                player2.updateMovement(grid, this::isBombBlockingMovement, playerCollisionChecker);
            }
            
            // Forcer le rendu si le joueur 2 est invincible (pour le clignotement)
            if (player2.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain pour le joueur 2
            if (player2.isBombRainActive()) {
                handleBombRain(); // Utiliser la même méthode pour l'instant
                player2.deactivateBombRain();
            }
        }
        
        // ✨ **MODE BATTLE 4 JOUEURS** : Mettre à jour le joueur 3
        if (isBattleMode && player3 != null) {
            player3.updateInvincibility();
            player3.updateTemporaryEffects();
            player3.updateWalkingState(); // Mise à jour de l'état de marche pour l'animation
            
            // ✨ **MOUVEMENT FLUIDE** : Mise à jour continue de la position avec collision entre joueurs
            if (!player3.isDying()) {
                player3.updateMovement(grid, this::isBombBlockingMovement, playerCollisionChecker);
            }
            
            // Forcer le rendu si le joueur 3 est invincible (pour le clignotement)
            if (player3.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain pour le joueur 3
            if (player3.isBombRainActive()) {
                handleBombRain(); // Utiliser la même méthode pour l'instant
                player3.deactivateBombRain();
            }
        }
        
        // ✨ **MODE BATTLE 4 JOUEURS** : Mettre à jour le joueur 4
        if (isBattleMode && player4 != null) {
            player4.updateInvincibility();
            player4.updateTemporaryEffects();
            player4.updateWalkingState(); // Mise à jour de l'état de marche pour l'animation
            
            // ✨ **MOUVEMENT FLUIDE** : Mise à jour continue de la position avec collision entre joueurs
            if (!player4.isDying()) {
                player4.updateMovement(grid, this::isBombBlockingMovement, playerCollisionChecker);
            }
            
            // Forcer le rendu si le joueur 4 est invincible (pour le clignotement)
            if (player4.isInvincible()) {
                needsRedraw = true;
            }
            
            // Vérifier et traiter l'effet Bomb Rain pour le joueur 4
            if (player4.isBombRainActive()) {
                handleBombRain(); // Utiliser la même méthode pour l'instant
                player4.deactivateBombRain();
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
        if (!activeBombs.isEmpty()) {
            System.out.println("🔄 DEBUG: Updating " + activeBombs.size() + " active bombs");
        }
        
        for (int i = activeBombs.size() - 1; i >= 0; i--) {
            Bomb bomb = activeBombs.get(i);
            
            // ✨ **CORRECTION** : Nettoyer les bombes mortes avant tout
            if (!bomb.isActive()) {
                activeBombs.remove(i);
                continue;
            }
            
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
        if (player.isAlive() || ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive())) {
            checkCollisions();
            
            // Vérifier la collecte de power-ups
            if (checkPowerUpCollection()) {
                needsRedraw = true;
            }
            
            // Vérifier si le niveau est terminé (tous les ennemis morts)
            if (checkLevelCompleted()) {
                // En mode coopération, la logique de victoire est gérée dans checkLevelCompleted()
                // En mode battle, vérifier si un seul joueur reste en vie
                if (!isCooperationMode && !isBattleMode) {
                    handlePlayerWin();
                } else if (isCooperationMode) {
                    // Mode coopération : passer à l'écran de fin une fois que les deux animations sont déclenchées
                    handleCooperationWin();
                } else if (isBattleMode) {
                    // Mode battle : gérer la victoire en mode battle
                    handleBattleWin();
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
     * Vérifie toutes les collisions du jeu (mode coopération/battle supporté)
     */
    private void checkCollisions() {
        // En mode normal : ne pas vérifier si le joueur est en train de mourir
        // En mode coopération/battle : continuer à vérifier pour l'autre joueur
        if (!isCooperationMode && !isBattleMode && player.isDying()) {
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
            
            // === BATTLE MODE : Vérifier collision avec bombes du joueur 2 ===
            if (!playerDeath && isBattleMode && player2 != null) {
                for (Explosion explosion : activeExplosions) {
                    if (explosion.isActive() && isBombFromPlayer(explosion, player2) && isInExplosion(player.getX(), player.getY())) {
                        playerDeath = true;
                        System.out.println("BATTLE MODE: Joueur 1 touché par une bombe du Joueur 2 !");
                        break;
                    }
                }
            }
        }
        
        // === VÉRIFICATIONS POUR JOUEUR 2 (MODE COOPÉRATION/BATTLE) ===
        if (!playerDeath && (isCooperationMode || isBattleMode) && player2 != null && player2.isAlive() && !player2.isInvincible() && !player2.isDying()) {
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
            
            // === BATTLE MODE : Vérifier collision avec bombes du joueur 1 ===
            if (!playerDeath && isBattleMode) {
                for (Explosion explosion : activeExplosions) {
                    if (explosion.isActive() && isBombFromPlayer(explosion, player) && isInExplosion(player2.getX(), player2.getY())) {
                        playerDeath = true;
                        System.out.println("BATTLE MODE: Joueur 2 touché par une bombe du Joueur 1 !");
                        break;
                    }
                }
            }
        }
        
        // === ✨ **CORRECTION** : VÉRIFICATIONS POUR JOUEUR 3 (MODE BATTLE 4 JOUEURS) ===
        if (!playerDeath && isBattleMode && player3 != null && player3.isAlive() && !player3.isInvincible() && !player3.isDying()) {
            // Collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player3, enemy)) {
                    playerDeath = true;
                    break;
                }
            }
        
            // Collision avec explosions (si pas déjà de collision avec ennemi)
            if (!playerDeath && !player3.isProtectedFromExplosions() && isInExplosion(player3.getX(), player3.getY())) {
                playerDeath = true;
            } else if (player3.isAlive() && player3.hasShield() && isInExplosion(player3.getX(), player3.getY())) {
                System.out.println("EXPLOSION BLOQUÉE PAR LE BOUCLIER (Joueur 3) !");
            }
            
            // === BATTLE MODE : Vérifier collision avec bombes des autres joueurs ===
            if (!playerDeath) {
                for (Explosion explosion : activeExplosions) {
                    if (explosion.isActive() && isInExplosion(player3.getX(), player3.getY())) {
                        playerDeath = true;
                        System.out.println("BATTLE MODE: Joueur 3 touché par une explosion !");
                        break;
                    }
                }
            }
        }
        
        // === ✨ **CORRECTION** : VÉRIFICATIONS POUR JOUEUR 4 (MODE BATTLE 4 JOUEURS) ===
        if (!playerDeath && isBattleMode && player4 != null && player4.isAlive() && !player4.isInvincible() && !player4.isDying()) {
            // Collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player4, enemy)) {
                    playerDeath = true;
                    break;
                }
            }
        
            // Collision avec explosions (si pas déjà de collision avec ennemi)
            if (!playerDeath && !player4.isProtectedFromExplosions() && isInExplosion(player4.getX(), player4.getY())) {
                playerDeath = true;
            } else if (player4.isAlive() && player4.hasShield() && isInExplosion(player4.getX(), player4.getY())) {
                System.out.println("EXPLOSION BLOQUÉE PAR LE BOUCLIER (Joueur 4) !");
            }
            
            // === BATTLE MODE : Vérifier collision avec bombes des autres joueurs ===
            if (!playerDeath) {
                for (Explosion explosion : activeExplosions) {
                    if (explosion.isActive() && isInExplosion(player4.getX(), player4.getY())) {
                        playerDeath = true;
                        System.out.println("BATTLE MODE: Joueur 4 touché par une explosion !");
                        break;
                    }
                }
            }
        }
        
        // Si au moins un joueur doit mourir, déclencher la séquence de mort
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
     * ✨ **BATTLE MODE** : Vérifie si une explosion provient d'une bombe d'un joueur spécifique
     * @param explosion L'explosion à vérifier
     * @param player Le joueur dont on veut vérifier la bombe
     * @return true si l'explosion provient d'une bombe de ce joueur
     */
    private boolean isBombFromPlayer(Explosion explosion, FluidMovementPlayer player) {
        // Pour l'instant, on considère que toutes les explosions peuvent tuer en mode battle
        // Cette méthode pourrait être étendue pour traquer l'origine des bombes
        return true;
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
        
        // ✨ **CORRECTION** : Utiliser la portée du propriétaire de la bombe
        int range = player.getRange(); // Par défaut
        FluidMovementPlayer bombOwner = bomb.getOwner();
        if (bombOwner != null) {
            range = bombOwner.getRange();
        }
        
        // Troisième étape : créer l'explosion qui va détruire les blocs
        Explosion explosion = new Explosion(
            bomb.getX(), 
            bomb.getY(), 
            range, // Utiliser la portée correcte
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
        // ✨ **BATTLE MODE** : Pas de porte en mode battle, donc aucune révélation possible
        if (isBattleMode || exitDoor == null) {
            return false;
        }
        
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
        // ✨ **BATTLE MODE** : Pas de porte en mode battle
        if (isBattleMode || exitDoor == null) {
            return false;
        }
        
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
        // ✨ **BATTLE MODE** : Pas de porte en mode battle
        if (isBattleMode || exitDoor == null) {
            return;
        }
        
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
        if (exitDoor != null && exitDoor.getX() == x && exitDoor.getY() == y) {
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
        // ✨ **BATTLE MODE** : Pas de condition de niveau terminé classique
        // La victoire se fait uniquement par élimination de l'autre joueur
        if (isBattleMode) {
            // En mode battle, on ne vérifie pas les ennemis ni la porte
            // La victoire est gérée directement dans handlePlayerDeath()
            return false;
        }
        
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
        if (exitDoor != null && allEnemiesDead && !hasPendingSpawns && !exitDoor.isActivated()) {
            exitDoor.activate();
        }
        
        // Si il y a des spawns programmés, désactiver la porte (au cas où elle était activée)
        if (exitDoor != null && hasPendingSpawns && exitDoor.isActivated()) {
            exitDoor.deactivate();
            System.out.println("Porte désactivée - Spawn d'ennemi en cours");
        }
        
        // ✨ **MODE COOPÉRATION** : Chaque joueur déclenche son animation quand il atteint la porte
        if (isCooperationMode && player2 != null) {
            // Vérifier si le joueur 1 doit déclencher son animation de victoire
            if (exitDoor != null && !player1WinAnimationTriggered && player.isAlive() && exitDoor.canUseToExit(player.getX(), player.getY())) {
                player1WinAnimationTriggered = true;
                player.win(); // Déclencher l'animation de victoire du joueur 1
                System.out.println("🎉 Joueur 1 a atteint la porte ! Animation de victoire déclenchée.");
            }
            
            // Vérifier si le joueur 2 doit déclencher son animation de victoire
            if (exitDoor != null && !player2WinAnimationTriggered && player2.isAlive() && exitDoor.canUseToExit(player2.getX(), player2.getY())) {
                player2WinAnimationTriggered = true;
                player2.win(); // Déclencher l'animation de victoire du joueur 2
                System.out.println("🎉 Joueur 2 a atteint la porte ! Animation de victoire déclenchée.");
            }
            
            // Le niveau se termine seulement quand les DEUX animations sont déclenchées
            return player1WinAnimationTriggered && player2WinAnimationTriggered;
        } else {
            // Mode normal : seulement le joueur 1
        if (exitDoor != null && exitDoor.canUseToExit(player.getX(), player.getY())) {
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
            case THEME_SELECTION:
                handleThemeSelectionInput(keyCode);
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
                if (player.isAlive() && !player.isDying()) {
                    player.onKeyReleased(keyCode);
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 2 (Z/Q/S/D) - MODE COOPÉRATION/BATTLE UNIQUEMENT ==========
            case Z:
                // Joueur 2 : Relâchement Haut
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive() && !player2.isDying()) {
                    player2.onKeyReleased(KeyCode.UP);
                }
                break;
            case S:
                // Joueur 2 : Relâchement Bas
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.DOWN);
                }
                break;
            case Q:
                // Joueur 2 : Relâchement Gauche
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.LEFT);
                }
                break;
            case D:
                // Joueur 2 : Relâchement Droite
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyReleased(KeyCode.RIGHT);
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 3 (Y/G/H/J) - MODE BATTLE UNIQUEMENT ==========
            case Y:
                // Joueur 3 : Relâchement Haut
                if (isBattleMode && player3 != null && player3.isAlive() && !player3.isDying()) {
                    player3.onKeyReleased(KeyCode.UP);
                }
                break;
            case H:
                // Joueur 3 : Relâchement Bas
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyReleased(KeyCode.DOWN);
                }
                break;
            case G:
                // Joueur 3 : Relâchement Gauche
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyReleased(KeyCode.LEFT);
                }
                break;
            case J:
                // Joueur 3 : Relâchement Droite
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyReleased(KeyCode.RIGHT);
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 4 (O/K/L/M) - MODE BATTLE UNIQUEMENT ==========
            case O:
                // Joueur 4 : Relâchement Haut
                if (isBattleMode && player4 != null && player4.isAlive() && !player4.isDying()) {
                    player4.onKeyReleased(KeyCode.UP);
                }
                break;
            case L:
                // Joueur 4 : Relâchement Bas
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyReleased(KeyCode.DOWN);
                }
                break;
            case K:
                // Joueur 4 : Relâchement Gauche
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyReleased(KeyCode.LEFT);
                }
                break;
            case M:
                // Joueur 4 : Relâchement Droite
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyReleased(KeyCode.RIGHT);
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

                // Désactiver tous les modes spéciaux
                isCooperationMode = false;
                isBattleMode = false;
                
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
                
                // Activer le mode coopération et désactiver les autres
                isCooperationMode = true;
                isBattleMode = false;
                
                // Réinitialiser les variables de victoire coopération
                player1WinAnimationTriggered = false;
                player2WinAnimationTriggered = false;
                
                // Arrêter la musique d'intro avant de lancer le jeu
                SoundManager.stop("intro");
                System.out.println("Musique d'intro arrêtée");
                
                initializeNewGame();
                break;
                
            case 2: // BATTLE MODE
                SoundManager.playEffect("menu_select");
                System.out.println("Démarrage du mode BATTLE MODE...");
                
                // Activer le mode battle et désactiver les autres
                isBattleMode = true;
                isCooperationMode = false;
                
                // Réinitialiser les variables de victoire coopération
                player1WinAnimationTriggered = false;
                player2WinAnimationTriggered = false;
                
                // Arrêter la musique d'intro avant de lancer le jeu
                SoundManager.stop("intro");
                System.out.println("Musique d'intro arrêtée");
                
                initializeNewGame();
                break;
                
            case 3: // THEMES
                SoundManager.playEffect("menu_select");
                System.out.println("Ouverture du menu de sélection des thèmes...");
                showThemeSelection();
                break;
                
            case 4: // PASSWORD
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
            // Utiliser FXML ou Canvas selon la préférence
            if (useFXMLMenus) {
                showPauseMenuFXML();
            } else {
                pauseGame();
            }
            return;
        }
        
        // En mode normal : ignorer si le joueur est mort
        // En mode coopération/battle : ignorer si les DEUX joueurs sont morts
        if (isCooperationMode || isBattleMode) {
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
                if (player.isAlive() && !player.isDying()) {
                    player.onKeyPressed(keyCode);
                }
                break;
            case SPACE:
                // Joueur 1 : Poser une bombe
                if (player.isAlive() && tryPlaceBombPlayer1()) {
                    needsRedraw = true;
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 2 (Z/Q/S/D + SHIFT) - MODE COOPÉRATION/BATTLE UNIQUEMENT ==========
            case Z:
                // Joueur 2 : Haut (uniquement en mode coopération/battle)
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.UP);
                }
                break;
            case S:
                // Joueur 2 : Bas (uniquement en mode coopération/battle)
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.DOWN);
                }
                break;
            case Q:
                // Joueur 2 : Gauche (uniquement en mode coopération/battle)
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.LEFT);
                }
                break;
            case D:
                // Joueur 2 : Droite (uniquement en mode coopération/battle)
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive()) {
                    player2.onKeyPressed(KeyCode.RIGHT);
                }
                break;
            case SHIFT:
                // Joueur 2 : Poser une bombe (uniquement en mode coopération/battle)
                if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive() && tryPlaceBombPlayer2()) {
                    needsRedraw = true;
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 3 (Y/G/H/J + ;) - MODE BATTLE UNIQUEMENT ==========
            case Y:
                // Joueur 3 : Haut (uniquement en mode battle)
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyPressed(KeyCode.UP);
                }
                break;
            case H:
                // Joueur 3 : Bas (uniquement en mode battle)
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyPressed(KeyCode.DOWN);
                }
                break;
            case G:
                // Joueur 3 : Gauche (uniquement en mode battle)
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyPressed(KeyCode.LEFT);
                }
                break;
            case J:
                // Joueur 3 : Droite (uniquement en mode battle)
                if (isBattleMode && player3 != null && player3.isAlive()) {
                    player3.onKeyPressed(KeyCode.RIGHT);
                }
                break;
            case SEMICOLON:
                // Joueur 3 : Poser une bombe (uniquement en mode battle)
                if (isBattleMode && player3 != null && player3.isAlive() && tryPlaceBombPlayer3()) {
                    needsRedraw = true;
                }
                break;
                
            // ========== CONTRÔLES JOUEUR 4 (O/K/L/M + ENTER) - MODE BATTLE UNIQUEMENT ==========
            case O:
                // Joueur 4 : Haut (uniquement en mode battle)
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyPressed(KeyCode.UP);
                }
                break;
            case L:
                // Joueur 4 : Bas (uniquement en mode battle)
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyPressed(KeyCode.DOWN);
                }
                break;
            case K:
                // Joueur 4 : Gauche (uniquement en mode battle)
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyPressed(KeyCode.LEFT);
                }
                break;
            case M:
                // Joueur 4 : Droite (uniquement en mode battle)
                if (isBattleMode && player4 != null && player4.isAlive()) {
                    player4.onKeyPressed(KeyCode.RIGHT);
                }
                break;
            case ENTER:
                // Joueur 4 : Poser une bombe (uniquement en mode battle)
                if (isBattleMode && player4 != null && player4.isAlive() && tryPlaceBombPlayer4()) {
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
        System.out.println("🔍 DEBUG: tryPlaceBombPlayer1() - Player1 can place: " + player.canPlaceBomb() + ", Position: (" + player.getX() + ", " + player.getY() + ")");
        
        // Vérifier si le joueur peut poser une bombe (nouveau système multi-bombes)
        if (player.canPlaceBomb() && !isBombAt(player.getX(), player.getY()) && !isVisibleExitDoorAt(player.getX(), player.getY())) {
            Bomb newBomb = new Bomb(player.getX(), player.getY(), player); // Bombe posée par le joueur 1
            activeBombs.add(newBomb);
            player.incrementActiveBombs();  // Incrémenter le compteur de bombes actives
            
            // Jouer le son de placement de bombe
            SoundManager.playBombPlaceSound();
            
            System.out.println("✅ Joueur 1 - Bombe posée à (" + player.getX() + ", " + player.getY() + ") - Total: " + player.getCurrentBombs() + "/" + player.getMaxBombs() + " - Bombe active: " + newBomb.isActive());
            System.out.println("🔍 DEBUG: activeBombs.size() = " + activeBombs.size());
            return true;
        }
        System.out.println("❌ Joueur 1 - Impossible de poser la bombe");
        return false;
    }
    
    /**
     * Tente de placer une bombe à la position actuelle du joueur 2
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBombPlayer2() {
        // Mode coopération ou battle uniquement
        if ((!isCooperationMode && !isBattleMode) || player2 == null) {
            return false;
        }
        
        System.out.println("🔍 DEBUG: tryPlaceBombPlayer2() - Player2 can place: " + player2.canPlaceBomb() + ", Position: (" + player2.getX() + ", " + player2.getY() + ")");
        
        // Vérifier si le joueur 2 peut poser une bombe (nouveau système multi-bombes)
        if (player2.canPlaceBomb() && !isBombAt(player2.getX(), player2.getY()) && !isVisibleExitDoorAt(player2.getX(), player2.getY())) {
            Bomb newBomb = new Bomb(player2.getX(), player2.getY(), player2); // Bombe posée par le joueur 2
            activeBombs.add(newBomb);
            player2.incrementActiveBombs();  // Incrémenter le compteur de bombes actives
            
            // Jouer le son de placement de bombe
            SoundManager.playBombPlaceSound();
            
            System.out.println("✅ Joueur 2 - Bombe posée à (" + player2.getX() + ", " + player2.getY() + ") - Total: " + player2.getCurrentBombs() + "/" + player2.getMaxBombs() + " - Bombe active: " + newBomb.isActive());
            System.out.println("🔍 DEBUG: activeBombs.size() = " + activeBombs.size());
            return true;
        }
        System.out.println("❌ Joueur 2 - Impossible de poser la bombe");
        return false;
    }
    
    /**
     * ✨ **NOUVEAU** : Tentative de placement d'une bombe par le joueur 3 (mode Battle 4 joueurs)
     * @return true si la bombe a été placée avec succès
     */
    private boolean tryPlaceBombPlayer3() {
        System.out.println("🔍 DEBUG: tryPlaceBombPlayer3() - Player3 can place: " + player3.canPlaceBomb() + ", Position: (" + player3.getX() + ", " + player3.getY() + ")");
        
        if (player3.canPlaceBomb() && !isBombAt(player3.getX(), player3.getY()) && !isVisibleExitDoorAt(player3.getX(), player3.getY())) {
            // Créer et ajouter la bombe à la position du joueur 3
            Bomb newBomb = new Bomb(player3.getX(), player3.getY(), player3);
            activeBombs.add(newBomb);
            player3.incrementActiveBombs();
            
            // Jouer le son de placement de bombe
            SoundManager.playBombPlaceSound();
            
            System.out.println("✅ Joueur 3 - Bombe posée à (" + player3.getX() + ", " + player3.getY() + ") - Total: " + player3.getCurrentBombs() + "/" + player3.getMaxBombs() + " - Bombe active: " + newBomb.isActive());
            return true;
        }
        
        System.out.println("❌ Joueur 3 - Impossible de poser la bombe");
        return false;
    }
    
    /**
     * ✨ **NOUVEAU** : Tentative de placement d'une bombe par le joueur 4 (mode Battle 4 joueurs)
     * @return true si la bombe a été placée avec succès
     */
    private boolean tryPlaceBombPlayer4() {
        System.out.println("🔍 DEBUG: tryPlaceBombPlayer4() - Player4 can place: " + player4.canPlaceBomb() + ", Position: (" + player4.getX() + ", " + player4.getY() + ")");
        
        if (player4.canPlaceBomb() && !isBombAt(player4.getX(), player4.getY()) && !isVisibleExitDoorAt(player4.getX(), player4.getY())) {
            // Créer et ajouter la bombe à la position du joueur 4
            Bomb newBomb = new Bomb(player4.getX(), player4.getY(), player4);
            activeBombs.add(newBomb);
            player4.incrementActiveBombs();
            
            // Jouer le son de placement de bombe
            SoundManager.playBombPlaceSound();
            
            System.out.println("✅ Joueur 4 - Bombe posée à (" + player4.getX() + ", " + player4.getY() + ") - Total: " + player4.getCurrentBombs() + "/" + player4.getMaxBombs() + " - Bombe active: " + newBomb.isActive());
            return true;
        }
        
        System.out.println("❌ Joueur 4 - Impossible de poser la bombe");
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
        
        // Vérifier le joueur 2 en mode coopération OU battle (s'il n'est pas exclu)
        if ((isCooperationMode || isBattleMode) && player2 != null && player2 != excludePlayer && player2.isAlive() && player2.getX() == x && player2.getY() == y) {
            return true;
        }
        
        // Vérifier le joueur 3 en mode battle 4 joueurs (s'il n'est pas exclu)
        if (isBattleMode && player3 != null && player3 != excludePlayer && player3.isAlive() && player3.getX() == x && player3.getY() == y) {
            return true;
        }
        
        // Vérifier le joueur 4 en mode battle 4 joueurs (s'il n'est pas exclu)
        if (isBattleMode && player4 != null && player4 != excludePlayer && player4.isAlive() && player4.getX() == x && player4.getY() == y) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Génère une porte de sortie cachée dans un bloc destructible
     * Cherche une position aléatoire parmi les blocs destructibles disponibles
     */
    private void generateExitDoor() {
        // ✨ **BATTLE MODE** : Pas de porte de sortie en mode battle
        // La victoire se fait uniquement par élimination de l'autre joueur
        if (isBattleMode) {
            exitDoor = null;
            System.out.println("Mode BATTLE : Aucune porte de sortie créée (victoire par élimination)");
            // Démarrer quand même le timer global pour limiter la durée du combat
            startGlobalTimer();
            return;
        }
        
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
            // ⏱️ Démarre le timer global de 2min30s
            startGlobalTimer();
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
     * Affiche le menu de sélection des thèmes
     */
    private void showThemeSelection() {
        currentState = GameState.THEME_SELECTION;
        renderer.renderThemeSelectionMenu(themeSelector);
        System.out.println("=== MENU SÉLECTION THÈMES AFFICHÉ ===");
    }
    
    /**
     * Cache le menu de sélection des thèmes et retourne au menu principal
     */
    private void hideThemeSelection() {
        currentState = GameState.START_MENU;
        renderer.renderStartMenu(selectedMenuIndex, MENU_OPTIONS, MENU_OPTIONS_ENABLED);
        System.out.println("=== RETOUR AU MENU PRINCIPAL ===");
    }
    
    /**
     * Gère les inputs dans le menu de sélection des thèmes
     * @param keyCode Le code de la touche pressée
     */
    private void handleThemeSelectionInput(KeyCode keyCode) {
        boolean needsRedraw = false;
        
        switch (keyCode) {
            case LEFT:
                // Thème précédent
                themeSelector.previousTheme();
                needsRedraw = true;
                SoundManager.playEffect("menu_cursor");
                break;
                
            case RIGHT:
                // Thème suivant
                themeSelector.nextTheme();
                needsRedraw = true;
                SoundManager.playEffect("menu_cursor");
                break;
                
            case ENTER:
                // Confirmer la sélection et retourner au menu principal
                SoundManager.playEffect("menu_select");
                hideThemeSelection();
                break;
                
            case ESCAPE:
                // Retourner au menu principal sans sauvegarder
                hideThemeSelection();
                break;
                
            default:
                // Ignorer les autres touches
                break;
        }
        
        // Redessiner le menu des thèmes si nécessaire
        if (needsRedraw) {
            renderer.renderThemeSelectionMenu(themeSelector);
        }
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
        // ✨ **CORRIGÉ** : Détecter TOUS les joueurs qui doivent mourir, pas juste le premier
        List<FluidMovementPlayer> dyingPlayers = new ArrayList<>();
        
        if (player.isAlive() && !player.isInvincible() && !player.isDying()) {
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
                dyingPlayers.add(player);
            }
        }
        
        // En mode coopération OU battle, vérifier aussi le joueur 2
        if ((isCooperationMode || isBattleMode) && player2 != null && player2.isAlive() && !player2.isInvincible() && !player2.isDying()) {
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
                dyingPlayers.add(player2);
            }
        }
        
        // ✨ **CORRECTION** : En mode battle 4 joueurs, vérifier aussi le joueur 3
        if (isBattleMode && player3 != null && player3.isAlive() && !player3.isInvincible() && !player3.isDying()) {
            boolean player3ShouldDie = false;
            
            // Vérifier collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player3, enemy)) {
                    player3ShouldDie = true;
                    break;
                }
            }
            
            // Vérifier collision avec explosions
            if (!player3ShouldDie && !player3.isProtectedFromExplosions()) {
                if (isInExplosion(player3.getX(), player3.getY())) {
                    player3ShouldDie = true;
                }
            }
            
            if (player3ShouldDie) {
                dyingPlayers.add(player3);
            }
        }
        
        // ✨ **CORRECTION** : En mode battle 4 joueurs, vérifier aussi le joueur 4
        if (isBattleMode && player4 != null && player4.isAlive() && !player4.isInvincible() && !player4.isDying()) {
            boolean player4ShouldDie = false;
            
            // Vérifier collision avec ennemis
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && isPlayerEnemyCollision(player4, enemy)) {
                    player4ShouldDie = true;
                    break;
                }
            }
            
            // Vérifier collision avec explosions
            if (!player4ShouldDie && !player4.isProtectedFromExplosions()) {
                if (isInExplosion(player4.getX(), player4.getY())) {
                    player4ShouldDie = true;
                }
            }
            
            if (player4ShouldDie) {
                dyingPlayers.add(player4);
            }
        }
        
        // Si aucun joueur ne doit mourir, ne rien faire
        if (dyingPlayers.isEmpty()) {
            return;
        }
        
        // ✨ **CORRIGÉ** : Traiter TOUS les joueurs mourants simultanément
        System.out.println("🚨 " + dyingPlayers.size() + " joueur(s) mourant(s) détecté(s) simultanément");
        
        for (FluidMovementPlayer dyingPlayer : dyingPlayers) {
            handleSinglePlayerDeath(dyingPlayer);
        }
        
        // ✨ **NOUVEAU** : Vérifier si le jeu doit être gelé après toutes les morts
        checkGameStateAfterDeaths(dyingPlayers);
    }
    
    /**
     * ✨ **NOUVEAU** : Gère la mort d'un joueur spécifique
     * @param dyingPlayer Le joueur qui meurt
     */
    private void handleSinglePlayerDeath(FluidMovementPlayer dyingPlayer) {
        // 1. Initialiser la séquence de mort dans le joueur qui meurt
        dyingPlayer.kill(); // Ceci met isDying à true et joue le son
        String playerName = "Joueur Inconnu";
        if (dyingPlayer == player) playerName = "Joueur 1";
        else if (dyingPlayer == player2) playerName = "Joueur 2";
        else if (dyingPlayer == player3) playerName = "Joueur 3";
        else if (dyingPlayer == player4) playerName = "Joueur 4";
        System.out.println("💀 Initialisation de la mort pour " + playerName);
        
        // 2. Référence finale pour le callback (capturée dans lambda)
        final FluidMovementPlayer finalDyingPlayer = dyingPlayer;
        
        // 3. Le GridRenderer va empiler ce callback ; il sera exécuté à la fin de l'animation correspondante
        renderer.setDeathAnimationCallback(() -> {
            // Ce code sera exécuté à la fin de l'animation de mort
            
            // 4. Terminer la séquence de mort (décrémenter la vie)
            finalDyingPlayer.completeDeathSequence();
            String finalPlayerName = "Joueur Inconnu";
            if (finalDyingPlayer == player) finalPlayerName = "Joueur 1";
            else if (finalDyingPlayer == player2) finalPlayerName = "Joueur 2";
            else if (finalDyingPlayer == player3) finalPlayerName = "Joueur 3";
            else if (finalDyingPlayer == player4) finalPlayerName = "Joueur 4";
            System.out.println("✅ Séquence de mort terminée pour " + finalPlayerName);
            
            // 5. En mode coopération, vérifier si les DEUX joueurs sont morts
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
            } else if (isBattleMode) {
                // ✨ **CORRECTION** : Mode battle 4 joueurs - vérifier combien de joueurs sont encore en vie
                int alivePlayers = 0;
                FluidMovementPlayer lastAlivePlayer = null;
                
                if (player != null && player.isAlive()) {
                    alivePlayers++;
                    lastAlivePlayer = player;
                }
                if (player2 != null && player2.isAlive()) {
                    alivePlayers++;
                    lastAlivePlayer = player2;
                }
                if (player3 != null && player3.isAlive()) {
                    alivePlayers++;
                    lastAlivePlayer = player3;
                }
                if (player4 != null && player4.isAlive()) {
                    alivePlayers++;
                    lastAlivePlayer = player4;
                }
                
                if (alivePlayers == 1) {
                    // Un seul joueur en vie : il gagne
                    String winnerName = "Joueur Inconnu";
                    if (lastAlivePlayer == player) winnerName = "JOUEUR 1";
                    else if (lastAlivePlayer == player2) winnerName = "JOUEUR 2";
                    else if (lastAlivePlayer == player3) winnerName = "JOUEUR 3";
                    else if (lastAlivePlayer == player4) winnerName = "JOUEUR 4";
                    
                    System.out.println("=== BATTLE MODE - " + winnerName + " GAGNE ===");
                    handleBattleWin();
                } else if (alivePlayers == 0) {
                    // Tous les joueurs sont morts : match nul -> Game Over
                    SoundManager.stopLevelMusic();
                    updateHighScore();
                    currentState = GameState.GAME_OVER;
                    renderer.renderGameOverScreen(player);
                    System.out.println("=== GAME OVER BATTLE - MATCH NUL ===");
                } else {
                    // Plus d'un joueur en vie : le jeu continue
                    currentState = GameState.RUNNING;
                    System.out.println("Mode battle : le jeu continue (" + alivePlayers + " joueurs en vie)");
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
     * ✨ **NOUVEAU** : Vérifie l'état du jeu après les morts et gèle si nécessaire
     * @param dyingPlayers Liste des joueurs qui meurent
     */
    private void checkGameStateAfterDeaths(List<FluidMovementPlayer> dyingPlayers) {
        // Vérifier si le jeu doit être gelé
        boolean shouldFreezeGame = false;
        
        if (!isCooperationMode && !isBattleMode) {
            // Mode normal : toujours geler quand un joueur meurt
            shouldFreezeGame = true;
        } else if (isCooperationMode) {
            // Mode coopération : geler seulement si tous les joueurs vivants meurent
            boolean allPlayersWillBeDead = true;
            
            // Vérifier s'il restera des joueurs vivants après les morts
            if (player != null && player.isAlive() && !dyingPlayers.contains(player)) {
                allPlayersWillBeDead = false;
            }
            
            if (player2 != null && player2.isAlive() && !dyingPlayers.contains(player2)) {
                allPlayersWillBeDead = false;
            }
            
            shouldFreezeGame = allPlayersWillBeDead;
        } else if (isBattleMode) {
            // Mode battle : toujours geler quand un joueur meurt (pour l'animation de mort)
            shouldFreezeGame = true;
        }
        
        if (shouldFreezeGame) {
            currentState = GameState.PLAYER_DYING;
            String playerNames = dyingPlayers.stream()
                .map(p -> {
                    if (p == player) return "Joueur 1";
                    else if (p == player2) return "Joueur 2";
                    else if (p == player3) return "Joueur 3";
                    else if (p == player4) return "Joueur 4";
                    else return "Joueur Inconnu";
                })
                .reduce((a, b) -> a + " + " + b)
                .orElse("Aucun");
            System.out.println("CHANGEMENT D'ÉTAT -> PLAYER_DYING (" + playerNames + ")");
        } else {
            String playerNames = dyingPlayers.stream()
                .map(p -> {
                    if (p == player) return "Joueur 1";
                    else if (p == player2) return "Joueur 2";
                    else if (p == player3) return "Joueur 3";
                    else if (p == player4) return "Joueur 4";
                    else return "Joueur Inconnu";
                })
                .reduce((a, b) -> a + " + " + b)
                .orElse("Aucun");
            System.out.println("MODE COOPÉRATION -> " + playerNames + " meurt/meurent mais le jeu continue");
        }
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
     * ✨ **NOUVEAU** : Gère la fin de niveau en mode battle (victoire quand un joueur élimine l'autre)
     */
    private void handleBattleWin() {
        // En mode battle, la victoire se produit quand:
        // 1. Tous les ennemis sont morts ET un seul joueur reste en vie
        // 2. OU un joueur élimine l'autre joueur
        
        final FluidMovementPlayer winner;
        if (player.isAlive() && (player2 == null || !player2.isAlive())) {
            winner = player;
        } else if (player2 != null && player2.isAlive() && !player.isAlive()) {
            winner = player2;
        } else {
            winner = null;
        }
        
        if (winner != null) {
            // 1. Initialiser la séquence de victoire dans le joueur gagnant
            winner.win();
            
            // 2. Changer l'état du jeu pour geler l'action pendant l'animation
            currentState = GameState.PLAYER_WINNING;
            System.out.println("CHANGEMENT D'ÉTAT -> PLAYER_WINNING (MODE BATTLE)");
            System.out.println("GAGNANT: " + (winner == player ? "Joueur 1" : "Joueur 2"));
            
            // 3. Arrêter la musique de niveau et jouer immédiatement Level_Clear.wav
            SoundManager.stopLevelMusic();
            SoundManager.playLevelClearSound();
            System.out.println("🎵 Musique Level_Clear.wav lancée pour la victoire battle");
            
            // 4. Le GridRenderer va gérer l'affichage de l'animation du gagnant
            renderer.setWinAnimationCallback(() -> {
                // Ce code sera exécuté quand l'animation de victoire est terminée
                
                // 5. Terminer la séquence de victoire
                winner.completeWinSequence();
                
                // 6. Passer à l'écran de niveau terminé (la musique continue)
                currentState = GameState.LEVEL_COMPLETED;
                renderer.renderLevelCompletedScreen(currentLevel, winner);
                System.out.println("=== NIVEAU " + currentLevel + " TERMINÉ (MODE BATTLE) ===");
                System.out.println("GAGNANT: " + (winner == player ? "Joueur 1" : "Joueur 2"));
                System.out.println("Passage à l'état : " + currentState);
            });
        } else {
            // Aucun gagnant clair, continuer le jeu
            System.out.println("Mode battle : aucun gagnant déterminé, le jeu continue");
        }
    }
    
        // ===== MÉTHODES CALLBACK POUR FXML =====
    
    /**
     * Démarre le jeu normal depuis FXML
     */
    public void startNormalGameFromFXML() {
        isCooperationMode = false;
        isBattleMode = false;
        player1WinAnimationTriggered = false;
        player2WinAnimationTriggered = false;
        
        SoundManager.stop("intro");
        initializeNewGame();
        
        // Retourner à la scène de jeu Canvas
        fxmlMenuManager.returnToGame();
    }
    
    /**
     * Démarre le mode coopération depuis FXML
     */
    public void startCooperationModeFromFXML() {
        isCooperationMode = true;
        isBattleMode = false;
        player1WinAnimationTriggered = false;
        player2WinAnimationTriggered = false;
        
        SoundManager.stop("intro");
        initializeNewGame();
        
        // Retourner à la scène de jeu Canvas
        fxmlMenuManager.returnToGame();
    }
    
    /**
     * Démarre le mode battle depuis FXML
     */
    public void startBattleModeFromFXML() {
        isCooperationMode = false;
        isBattleMode = true;
        player1WinAnimationTriggered = false;
        player2WinAnimationTriggered = false;
        player3WinAnimationTriggered = false;
        player4WinAnimationTriggered = false;
        
        SoundManager.stop("intro");
        initializeNewGame();
        
        // Retourner à la scène de jeu Canvas
        fxmlMenuManager.returnToGame();
    }
    
    /**
     * Reprend le jeu depuis le menu FXML
     */
    public void resumeGameFromFXML() {
        resumeGame();
        fxmlMenuManager.returnToGame();
    }
    
    /**
     * Redémarre le jeu depuis le menu FXML
     */
    public void restartGameFromFXML() {
        SoundManager.stopAllMusic();
        initializeNewGame();
        
        // Retourner à la scène de jeu Canvas
        fxmlMenuManager.returnToGame();
    }
    
    /**
     * Retourne au menu principal depuis FXML
     */
    public void returnToMainMenuFromFXML() {
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
            gameTimer = null;
        }
        
        // Retourner au menu principal
        currentState = GameState.START_MENU;
        selectedMenuIndex = 0;
        
        // Relancer la musique d'intro
        SoundManager.loop("intro");
        
        // Afficher le menu FXML
        fxmlMenuManager.showMainMenu();
    }
    
    /**
     * Affiche le menu de pause FXML
     */
    public void showPauseMenuFXML() {
        if (currentState == GameState.RUNNING) {
            // Sauvegarder l'état du jeu
            pauseGame();
            
            // Afficher le menu FXML
            fxmlMenuManager.showPauseMenu();
        }
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 