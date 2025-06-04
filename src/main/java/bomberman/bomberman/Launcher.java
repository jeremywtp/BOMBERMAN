package bomberman.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale de l'application Bomberman.
 * Responsable du lancement de l'application JavaFX,
 * de l'initialisation de la fenêtre et de la scène.
 * Délègue le dessin à GridRenderer et la logique à Grid.
 * Gère maintenant les interactions clavier pour déplacer le joueur,
 * poser des bombes, gérer les explosions et les ennemis avec IA.
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
    
    // Composants du jeu
    private Grid grid;
    private Player player;
    private List<Enemy> enemies;
    private GridRenderer renderer;
    
    // Gestion des bombes et explosions
    private Bomb activeBomb;
    private Explosion activeExplosion;
    
    // Timer d'animation pour les mises à jour
    private AnimationTimer gameTimer;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialisation du modèle de données de la grille
        grid = new Grid(GRID_COLUMNS, GRID_ROWS);
        
        // Initialisation du joueur à une position de départ valide
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        
        // Initialisation des ennemis
        enemies = new ArrayList<>();
        createEnemies();
        
        // Création du canvas pour le dessin
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialisation du renderer pour dessiner tous les éléments
        renderer = new GridRenderer(canvas, grid);
        renderer.render(player, enemies, activeBomb, activeExplosion);  // Rendu initial avec UI
        
        // Configuration de la scène
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Gestion des événements clavier pour déplacer le joueur et poser des bombes
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Bomberman Base");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Fenêtre non redimensionnable pour garder les proportions
        primaryStage.show();
        
        // Donner le focus à la scène pour capturer les événements clavier
        scene.getRoot().requestFocus();
        
        // Démarrer le timer d'animation pour les mises à jour
        startGameTimer();
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
     * Met à jour l'état du jeu (bombes, explosions, ennemis, collisions)
     */
    private void updateGame() {
        boolean needsRedraw = false;
        
        // Mettre à jour les ennemis seulement si le joueur est vivant
        // (Optionnel : on peut continuer à faire bouger les ennemis même après la mort)
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
        }
        
        // Redessiner si nécessaire (toujours redessiner pour mettre à jour l'UI)
        if (needsRedraw || !player.isAlive()) {
            renderer.render(player, enemies, activeBomb, activeExplosion);
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
     * Crée une explosion à partir de la bombe active
     */
    private void createExplosion() {
        if (activeBomb != null) {
            activeExplosion = new Explosion(
                activeBomb.getX(), 
                activeBomb.getY(), 
                Bomb.getExplosionRange(), 
                grid
            );
        }
    }
    
    /**
     * Gère les événements de touches pressées
     * @param keyCode Le code de la touche pressée
     */
    private void handleKeyPressed(KeyCode keyCode) {
        // Empêcher toute action si le joueur est mort
        if (!player.isAlive()) {
            return; // Ignorer toutes les touches si le joueur est mort
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
            renderer.render(player, enemies, activeBomb, activeExplosion);
        }
    }
    
    /**
     * Tente de placer une bombe à la position actuelle du joueur
     * @return true si la bombe a été placée, false sinon
     */
    private boolean tryPlaceBomb() {
        // Vérifier si le joueur peut poser une bombe
        if (!player.hasActiveBomb() && activeBomb == null) {
            activeBomb = new Bomb(player.getX(), player.getY());
            player.setHasActiveBomb(true);
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