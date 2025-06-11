package bomberman.bomberman;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe responsable du rendu graphique de la grille.
 * Prend en paramètre le modèle logique (Grid) et un Canvas JavaFX.
 * Dessine la grille selon les données du modèle avec :
 * - Blocs solides en gris (#505050)
 * - Cases vides en noir (#000000)
 * - Blocs destructibles en marron clair (#A0522D)
 * - Joueur en bleu clair (#00AAFF)
 * - Bombes en rouge foncé (#990000)
 * - Explosions en orange (#FF8800)
 * - Ennemis en rouge vif (#FF0000)
 * - Interface utilisateur avec vie et messages
 */
public class GridRenderer implements DestructibleBlockListener {
    
    // Taille d'une cellule en pixels (agrandie x1.5)
    private static final int CELL_SIZE = 48;  // était 32
    
    // Couleurs utilisées pour le rendu
    private static final Color SOLID_COLOR = Color.web("#505050");        // Gris pour les blocs solides
    private static final Color EMPTY_COLOR = Color.web("#000000");        // Noir pour les cases vides
    private static final Color DESTRUCTIBLE_COLOR = Color.web("#A0522D");  // Marron clair pour les blocs destructibles
    private static final Color EXIT_DOOR_COLOR = Color.web("#FFD700");    // Or pour la porte de sortie active
    private static final Color EXIT_DOOR_INACTIVE_COLOR = Color.web("#CD853F");  // Marron doré pour la porte inactive
    private static final Color PLAYER_COLOR = Color.web("#00AAFF");       // Bleu clair pour le joueur
    private static final Color BOMB_COLOR = Color.web("#990000");         // Rouge foncé pour les bombes
    private static final Color EXPLOSION_COLOR = Color.web("#FF8800");    // Orange pour les explosions
    private static final Color ENEMY_COLOR = Color.web("#FF0000");        // Rouge vif pour les ennemis
    
    // Couleurs pour l'interface utilisateur
    private static final Color UI_TEXT_COLOR = Color.WHITE;               // Blanc pour le texte de l'UI
    private static final Color GAME_OVER_COLOR = Color.RED;               // Rouge pour le message GAME OVER
    private static final Color DEATH_OVERLAY_COLOR = Color.web("#000000", 0.5); // Noir semi-transparent pour l'overlay de mort
    
    // Couleurs pour les power-ups
    private static final Color EXTRA_BOMB_COLOR = Color.CYAN;             // Cyan pour EXTRA_BOMB
    private static final Color EXPLOSION_EXPANDER_COLOR = Color.ORANGE;   // Orange pour EXPLOSION_EXPANDER
    
    // Taille du joueur (agrandie x1.5, légèrement plus petit que la case)
    private static final int PLAYER_SIZE = CELL_SIZE - 9;  // 39 pixels au lieu de 26 (était 32-6)
    private static final int PLAYER_OFFSET = 4;  // était 3, maintenant 4 pour centrer
    
    // Taille de la bombe (agrandie x1.5, légèrement plus petite que la case)
    private static final int BOMB_SIZE = CELL_SIZE - 6;  // 42 pixels au lieu de 28 (était 32-4)
    private static final int BOMB_OFFSET = 3;  // était 2, maintenant 3 pour centrer
    
    // Taille des ennemis (agrandie x1.5, même taille que le joueur)
    private static final int ENEMY_SIZE = CELL_SIZE - 9;  // 39 pixels au lieu de 26
    private static final int ENEMY_OFFSET = 4;  // était 3, maintenant 4 pour centrer
    
    // Taille des power-ups (agrandie x1.5, même taille que le joueur)
    private static final int POWER_UP_SIZE = CELL_SIZE - 9;              // 39 pixels au lieu de 26
    private static final int POWER_UP_OFFSET = 4;                        // était 3, maintenant 4 pour centrer
    
    // Paramètres de l'interface utilisateur (agrandis x1.5)
    private static final int UI_MARGIN = 15;                             // était 10, maintenant 15
    private static final int UI_FONT_SIZE = 24;                          // était 16, maintenant 24
    private static final int GAME_OVER_FONT_SIZE = 72;                   // était 48, maintenant 72
    private static final int GAME_AREA_HEIGHT = 624;                     // Hauteur de la grille seule (13 * 48 = 624px)
    private static final int UI_AREA_HEIGHT = 362;                        // était 282, maintenant 362 (+80px pour zone notifications élargie)
    
    // ⏱️ Paramètres d'agencement vertical amélioré
    private static final int ATH_HEIGHT = 50;                           // Espace pour l'ATH (LEVEL/SCORE/HIGHSCORE)
    private static final int TIMER_ZONE_HEIGHT = 50;                    // Zone dédiée au timer avec marges
    private static final int TOTAL_HEADER_HEIGHT = ATH_HEIGHT + TIMER_ZONE_HEIGHT; // 100px total pour header + timer
    private static final int GRID_VERTICAL_OFFSET = TOTAL_HEADER_HEIGHT; // Décalage de la grille vers le bas
    
    // Zone de notifications temporaires
    private static final int MAX_NOTIFICATIONS = 10; // Augmenté pour profiter de l'espace supplémentaire (+80px)
    private List<String> recentNotifications = new ArrayList<>();
    private List<Long> notificationTimestamps = new ArrayList<>();
    private static final long NOTIFICATION_DURATION = 4000; // 4 secondes pour profiter de l'espace
    
    private final Canvas canvas;
    private final Grid grid;
    private final GraphicsContext gc;
    
    // Image d'intro pour l'écran de démarrage
    private static Image introImage;
    
    // ✨ **NOUVEAU** : Image des contours personnalisés
    private static Image contoursMapImage;
    
    // ✨ **NOUVEAU** : Image des blocs non destructibles
    private static Image blocNonDestructibleImage;
    
    // ✨ **NOUVEAU** : Images des sprites d'herbe
    private static Image herbeImage;                              // Herbe classique
    private static Image herbeWithOmbreBlocNonDestructibleImage;  // Herbe avec ombre bloc non destructible
    private static Image herbeWithOmbreBlocDestructibleImage;     // Herbe avec ombre bloc destructible
    
    // ✨ **NOUVEAU** : Images des sprites de bombes
    private static Image bomb1Image;                              // Sprite bombe 1
    private static Image bomb2Image;                              // Sprite bombe 2
    private static Image bomb3Image;                              // Sprite bombe 3
    
    // ✨ **NOUVEAU** : Gestion des blocs destructibles animés
    private DestructibleBlock[][] destructibleBlocks;            // Tableau des blocs destructibles animés
    
    // ✨ **NOUVEAU** : Gestion de l'animation Bomberman
    private BombermanAnimator bombermanAnimator;          // Animateur pour joueur 1
    private BombermanAnimator bombermanAnimator2;         // Animateur pour joueur 2 (mode coopération)
    private Runnable onDeathAnimationCompleteCallback;
    private Runnable onWinAnimationCompleteCallback;
    
    // ✨ **NOUVEAU** : Gestion de l'animation d'explosion
    private List<ExplosionAnimator> explosionAnimators;
    private List<Explosion> trackedExplosions;
    private int currentExplosionRange = 1; // Portée par défaut
    
    // ✨ **NOUVEAU** : Animateur de porte
    private DoorAnimator doorAnimator;
    
    // ✨ **NOUVEAU** : Animateur du bonus EXPLOSION_EXPANDER
    private ExplosionExpanderAnimator explosionExpanderAnimator;
    
    // ✨ **NOUVEAU** : Animateur du bonus EXTRA_BOMB
    private ExtraBombAnimator extraBombAnimator;
    
    // Liste des callbacks de mort pour gérer plusieurs joueurs mourant en même temps
    private java.util.Queue<Runnable> deathAnimationCallbacks = new java.util.ArrayDeque<>();
    
    /**
     * Constructeur du renderer
     * @param canvas Le canvas JavaFX sur lequel dessiner
     * @param grid Le modèle de grille à afficher
     */
    public GridRenderer(Canvas canvas, Grid grid) {
        this.canvas = canvas;
        this.grid = grid;
        this.gc = canvas.getGraphicsContext2D();
        
        // Charger l'image d'intro si pas déjà fait
        loadIntroImage();
        
        // Charger l'image des contours personnalisés
        loadContoursMapImage();
        
        // Charger l'image des blocs non destructibles
        loadBlocNonDestructibleImage();
        
        // Charger les images des sprites d'herbe
        loadHerbeImages();
        
        // Charger les images des sprites de bombes
        loadBombImages();
        
        // Initialiser les blocs destructibles animés
        initializeDestructibleBlocks();
        
        // Initialiser les animateurs de Bomberman
        bombermanAnimator = new BombermanAnimator();     // Joueur 1
        bombermanAnimator2 = new BombermanAnimator();    // Joueur 2 (mode coopération)
        onDeathAnimationCompleteCallback = null;
        onWinAnimationCompleteCallback = null;
        
        // Initialiser l'animateur de porte
        doorAnimator = new DoorAnimator();
        
        // Initialiser l'animateur du bonus EXPLOSION_EXPANDER
        explosionExpanderAnimator = new ExplosionExpanderAnimator();
        
        // Initialiser l'animateur du bonus EXTRA_BOMB
        extraBombAnimator = new ExtraBombAnimator();
        
        // Initialiser les animateurs d'explosion
        explosionAnimators = new ArrayList<>();
        trackedExplosions = new ArrayList<>();
        
        // Configurer le listener pour recevoir les notifications de destruction de blocs
        if (grid != null) {
            grid.setDestructibleBlockListener(this);
        }
        
        System.out.println("GridRenderer initialisé");
    }
    
    /**
     * Charge l'image d'intro depuis les ressources
     */
    private static void loadIntroImage() {
        if (introImage == null) {
            try {
                String imagePath = "/images/intro.png";
                introImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image d'intro chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image d'intro : " + e.getMessage());
                introImage = null;
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Charge l'image des contours personnalisés depuis les ressources
     */
    private static void loadContoursMapImage() {
        if (contoursMapImage == null) {
            try {
                String imagePath = "/sprites/contours_map_816x624.png";
                contoursMapImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image des contours personnalisés chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image des contours : " + e.getMessage());
                contoursMapImage = null;
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Charge l'image des blocs non destructibles depuis les ressources
     */
    private static void loadBlocNonDestructibleImage() {
        if (blocNonDestructibleImage == null) {
            try {
                String imagePath = "/sprites/bloc_non_destructible_48x48.png";
                blocNonDestructibleImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image des blocs non destructibles chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image des blocs non destructibles : " + e.getMessage());
                blocNonDestructibleImage = null;
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Charge les images des sprites d'herbe depuis les ressources
     */
    private static void loadHerbeImages() {
        // Herbe classique
        if (herbeImage == null) {
            try {
                String imagePath = "/sprites/herbe_48x48.png";
                herbeImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image d'herbe classique chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image d'herbe classique : " + e.getMessage());
                herbeImage = null;
            }
        }
        
        // Herbe avec ombre bloc non destructible
        if (herbeWithOmbreBlocNonDestructibleImage == null) {
            try {
                String imagePath = "/sprites/herbe_with_ombre_bloc_non_destructible_48x48.png";
                herbeWithOmbreBlocNonDestructibleImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image d'herbe avec ombre bloc non destructible chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image d'herbe avec ombre bloc non destructible : " + e.getMessage());
                herbeWithOmbreBlocNonDestructibleImage = null;
            }
        }
        
        // Herbe avec ombre bloc destructible
        if (herbeWithOmbreBlocDestructibleImage == null) {
            try {
                String imagePath = "/sprites/herbe_with_ombre_bloc_destructible_48x48.png";
                herbeWithOmbreBlocDestructibleImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image d'herbe avec ombre bloc destructible chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image d'herbe avec ombre bloc destructible : " + e.getMessage());
                herbeWithOmbreBlocDestructibleImage = null;
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Charge les images des sprites de bombes depuis les ressources
     */
    private static void loadBombImages() {
        // Sprite bombe 1
        if (bomb1Image == null) {
            try {
                String imagePath = "/sprites/bomb_1_48x48.png";
                bomb1Image = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image sprite bombe 1 chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du sprite bombe 1 : " + e.getMessage());
                bomb1Image = null;
            }
        }
        
        // Sprite bombe 2
        if (bomb2Image == null) {
            try {
                String imagePath = "/sprites/bomb_2_48x48.png";
                bomb2Image = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image sprite bombe 2 chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du sprite bombe 2 : " + e.getMessage());
                bomb2Image = null;
            }
        }
        
        // Sprite bombe 3
        if (bomb3Image == null) {
            try {
                String imagePath = "/sprites/bomb_3_48x48.png";
                bomb3Image = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image sprite bombe 3 chargée : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du sprite bombe 3 : " + e.getMessage());
                bomb3Image = null;
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Initialise les blocs destructibles animés en fonction de la grille
     */
    private void initializeDestructibleBlocks() {
        if (grid == null) {
            return; // Pas de grille, pas d'initialisation
        }
        
        // Créer le tableau 2D pour stocker les blocs destructibles
        destructibleBlocks = new DestructibleBlock[grid.getRows()][grid.getColumns()];
        
        // Parcourir la grille et créer des blocs animés pour les cases destructibles
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                if (grid.getTileType(col, row) == TileType.DESTRUCTIBLE) {
                    // Vérifier s'il y a un bloc non destructible au-dessus
                    boolean hasNonDestructibleAbove = row > 0 && grid.getTileType(col, row - 1) == TileType.SOLID;
                    
                    // Créer le bloc destructible avec la bonne version
                    destructibleBlocks[row][col] = DestructibleBlock.createForContext(hasNonDestructibleAbove);
                    
                    System.out.println("Bloc destructible animé créé à (" + col + ", " + row + ") - Version: " + 
                                     destructibleBlocks[row][col].getVersion());
                }
            }
        }
        
        System.out.println("Initialisation des blocs destructibles animés terminée");
    }
    
    /**
     * ✨ **NOUVEAU** : Détruit un bloc destructible animé à une position donnée
     * @param column Position en colonne (x)
     * @param row Position en ligne (y)
     */
    public void destroyDestructibleBlock(int column, int row) {
        if (destructibleBlocks != null && 
            row >= 0 && row < destructibleBlocks.length && 
            column >= 0 && column < destructibleBlocks[row].length && 
            destructibleBlocks[row][column] != null) {
            
            // Arrêter l'animation du bloc
            destructibleBlocks[row][column].dispose();
            destructibleBlocks[row][column] = null;
            
            System.out.println("Bloc destructible animé détruit à (" + column + ", " + row + ")");
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Libère toutes les ressources des blocs destructibles animés
     */
    public void disposeAllDestructibleBlocks() {
        if (destructibleBlocks != null) {
            for (int row = 0; row < destructibleBlocks.length; row++) {
                for (int col = 0; col < destructibleBlocks[row].length; col++) {
                    if (destructibleBlocks[row][col] != null) {
                        destructibleBlocks[row][col].dispose();
                        destructibleBlocks[row][col] = null;
                    }
                }
            }
            System.out.println("Toutes les animations de blocs destructibles libérées");
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Implémentation de DestructibleBlockListener
     * Appelé automatiquement quand un bloc destructible est détruit dans la grille
     */
    @Override
    public void onBlockDestroyed(int column, int row) {
        destroyDestructibleBlock(column, row);
    }
    
    /**
     * Méthode principale de rendu.
     * Dessine l'intégralité de la grille sur le canvas (dans la zone de jeu uniquement).
     */
    public void render() {
        // Effacer TOUT le canvas pour éviter les doublons d'ATH
        gc.setFill(EMPTY_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // ✨ **NOUVEAU** : Afficher l'image des contours personnalisés en premier
        if (contoursMapImage != null) {
            // Dessiner l'image des contours complète à partir de x=0 (pleine largeur de 816px)
            gc.drawImage(contoursMapImage, 0, GRID_VERTICAL_OFFSET);
        } else {
            // Fallback : dessiner les cellules individuellement si l'image n'est pas chargée
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                renderCell(col, row);
                }
            }
        }
        
        // Dessiner seulement les cellules intérieures (non-bordures) par-dessus l'image des contours
        if (contoursMapImage != null) {
            for (int row = 1; row < grid.getRows() - 1; row++) {
                for (int col = 1; col < grid.getColumns() - 1; col++) {
                    renderInteriorCell(col, row);
                }
            }
        }
    }
    
    /**
     * Méthode de rendu avec joueur.
     * Dessine la grille puis le joueur par-dessus avec l'interface utilisateur.
     * @param player Le joueur à afficher
     */
    public void render(Player player) {
        render(player, null, null, null, null);
    }
    
    /**
     * Méthode de rendu complète avec joueur, bombe et explosion avec interface utilisateur.
     * @param player Le joueur à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     */
    public void render(Player player, Bomb bomb, Explosion explosion) {
        List<Bomb> bombs = bomb != null ? List.of(bomb) : new ArrayList<>();
        List<Explosion> explosions = explosion != null ? List.of(explosion) : new ArrayList<>();
        render(player, null, bombs, explosions, null, 0, 1);
    }
    
    /**
     * Méthode de rendu complète avec joueur, ennemis, bombe et explosion (sans power-ups)
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     */
    public void render(Player player, List<Enemy> enemies, Bomb bomb, Explosion explosion) {
        List<Bomb> bombs = bomb != null ? List.of(bomb) : new ArrayList<>();
        List<Explosion> explosions = explosion != null ? List.of(explosion) : new ArrayList<>();
        render(player, enemies, bombs, explosions, null, 0, 1);
    }
    
    /**
     * Méthode de rendu complète avec tous les éléments du jeu et l'interface utilisateur (avec high score et niveau)
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bombs Liste des bombes actives (peut être null ou vide)
     * @param explosions Liste des explosions actives (peut être null ou vide)
     * @param powerUps Liste des power-ups visibles à afficher
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     * @param exitDoor La porte de sortie (peut être null)
     */
    public void render(Player player, List<Enemy> enemies, List<Bomb> bombs, List<Explosion> explosions, List<PowerUp> powerUps, int highScore, int currentLevel, ExitDoor exitDoor) {
        // Dessiner d'abord la grille
        render();
        
        // Dessiner les explosions en premier (sous les autres éléments)
        if (explosions != null) {
            for (Explosion explosion : explosions) {
                if (explosion.isActive()) {
                    renderExplosion(explosion);
                }
            }
        }
        
        // Dessiner la porte de sortie en deuxième (sous les bombes/ennemis/joueur)
        if (exitDoor != null && exitDoor.isVisible()) {
            renderExitDoor(exitDoor);
        }
        
        // Dessiner les power-ups visibles
        if (powerUps != null) {
            renderPowerUps(powerUps);
        }
        
        // Dessiner les bombes (par-dessus la porte)
        if (bombs != null) {
            for (Bomb bomb : bombs) {
                if (bomb.isActive()) {
                    renderBomb(bomb);
                }
            }
        }
        
        // Dessiner les ennemis vivants (par-dessus la porte)
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    renderEnemy(enemy);
                }
            }
        }
        
        // ✨ **MODIFIÉ** : Dessiner le joueur en dernier (par-dessus tout, vivant OU mort pour l'animation)
        renderPlayer(player, exitDoor);
        
        // Dessiner l'interface utilisateur par-dessus tout (avec high score, niveau et timer)
        renderUIWithTimer(player, highScore, currentLevel, 0);
        
        // Note: Le message GAME OVER est géré par renderGameOverScreen() appelé depuis Launcher
        // Pas de double appel ici pour éviter les doublons
    }
    
    /**
     * Méthode de rendu complète avec tous les éléments du jeu et l'interface utilisateur (version avec high score)
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     * @param powerUps Liste des power-ups visibles à afficher
     * @param highScore Le meilleur score enregistré
     */
    public void render(Player player, List<Enemy> enemies, Bomb bomb, Explosion explosion, List<PowerUp> powerUps, int highScore) {
        List<Bomb> bombs = bomb != null ? List.of(bomb) : new ArrayList<>();
        List<Explosion> explosions = explosion != null ? List.of(explosion) : new ArrayList<>();
        render(player, enemies, bombs, explosions, powerUps, highScore, 1);  // Niveau par défaut à 1
    }
    
    /**
     * Méthode de rendu complète avec tous les éléments du jeu et l'interface utilisateur (version simplifiée)
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     * @param powerUps Liste des power-ups visibles à afficher
     */
    public void render(Player player, List<Enemy> enemies, Bomb bomb, Explosion explosion, List<PowerUp> powerUps) {
        List<Bomb> bombs = bomb != null ? List.of(bomb) : new ArrayList<>();
        List<Explosion> explosions = explosion != null ? List.of(explosion) : new ArrayList<>();
        render(player, enemies, bombs, explosions, powerUps, 0, 1);  // High score et niveau par défaut
    }
    
    /**
     * Méthode de rendu complète avec tous les éléments du jeu et l'interface utilisateur (avec high score et niveau)
     * Surcharge pour compatibilité avec l'ancienne signature
     */
    public void render(Player player, List<Enemy> enemies, List<Bomb> bombs, List<Explosion> explosions, List<PowerUp> powerUps, int highScore, int currentLevel) {
        // Appel avec porte null
        render(player, enemies, bombs, explosions, powerUps, highScore, currentLevel, null);
    }
    
    /**
     * Dessine une cellule individuelle de la grille
     * @param column Position en colonne (x)
     * @param row Position en ligne (y)
     */
    private void renderCell(int column, int row) {
        // Calculer la position en pixels avec décalage horizontal et vertical
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        int x = (int) (column * CELL_SIZE + horizontalOffset);
        int y = row * CELL_SIZE + GRID_VERTICAL_OFFSET;
        
        // Déterminer la couleur selon le type de cellule
        TileType tileType = grid.getTileType(column, row);
        Color cellColor;
        
        switch (tileType) {
            case SOLID:
                cellColor = SOLID_COLOR;
                break;
            case DESTRUCTIBLE:
                cellColor = DESTRUCTIBLE_COLOR;
                break;
            case EMPTY:
            default:
                cellColor = EMPTY_COLOR;
                break;
        }
        
        // Dessiner la cellule
        gc.setFill(cellColor);
        gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
    }
    
    /**
     * ✨ **NOUVEAU** : Dessine seulement les cellules intérieures (non-bordures) par-dessus l'image des contours
     * @param column Position en colonne (x)
     * @param row Position en ligne (y)
     */
    private void renderInteriorCell(int column, int row) {
        // Calculer la position en pixels avec décalage horizontal et vertical
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        int x = (int) (column * CELL_SIZE + horizontalOffset);
        int y = row * CELL_SIZE + GRID_VERTICAL_OFFSET;
        
        // Déterminer le type de cellule et choisir l'herbe appropriée
        TileType tileType = grid.getTileType(column, row);
        
        // ✨ **NOUVEAU** : Dessiner d'abord l'herbe appropriée selon le contexte
        renderHerbeBackground(column, row, x, y);
        
        // Ensuite dessiner les éléments par-dessus selon le type de cellule
        switch (tileType) {
            case DESTRUCTIBLE:
                // ✨ **NOUVEAU** : Utiliser le sprite animé pour les blocs destructibles
                if (destructibleBlocks != null && destructibleBlocks[row][column] != null) {
                    Image currentFrame = destructibleBlocks[row][column].getCurrentFrame();
                    if (currentFrame != null) {
                        gc.drawImage(currentFrame, x, y);
                    } else {
                        // Fallback : couleur unie si le sprite animé n'est pas disponible
                        gc.setFill(DESTRUCTIBLE_COLOR);
                        gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    // Fallback : couleur unie si pas d'animation
                    gc.setFill(DESTRUCTIBLE_COLOR);
                    gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                break;
            case EMPTY:
                // Rien d'autre à dessiner, l'herbe classique est déjà affichée
                break;
            case SOLID:
                // ✨ **NOUVEAU** : Utiliser le sprite pour les blocs solides intérieurs (piliers)
                if (blocNonDestructibleImage != null) {
                    gc.drawImage(blocNonDestructibleImage, x, y);
                } else {
                    // Fallback : couleur unie si le sprite n'est pas chargé
                    gc.setFill(SOLID_COLOR);
                    gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                break;
            default:
                // Rien d'autre à dessiner pour les autres cas
                break;
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Dessine l'herbe appropriée selon le contexte (case voisine)
     * @param column Position en colonne de la case à rendre
     * @param row Position en ligne de la case à rendre
     * @param x Position X en pixels
     * @param y Position Y en pixels
     */
    private void renderHerbeBackground(int column, int row, int x, int y) {
        // Vérifier s'il y a un bloc non destructible directement au-dessus
        boolean hasNonDestructibleAbove = row > 0 && grid.getTileType(column, row - 1) == TileType.SOLID;
        
        // Vérifier s'il y a un bloc destructible directement au-dessus
        boolean hasDestructibleAbove = row > 0 && grid.getTileType(column, row - 1) == TileType.DESTRUCTIBLE;
        
        // Choisir le sprite d'herbe approprié
        Image herbeToUse = null;
        if (hasNonDestructibleAbove && herbeWithOmbreBlocNonDestructibleImage != null) {
            herbeToUse = herbeWithOmbreBlocNonDestructibleImage;
        } else if (hasDestructibleAbove && herbeWithOmbreBlocDestructibleImage != null) {
            herbeToUse = herbeWithOmbreBlocDestructibleImage;
        } else if (herbeImage != null) {
            herbeToUse = herbeImage;
        }
        
        // Dessiner l'herbe si disponible, sinon utiliser la couleur noire par défaut
        if (herbeToUse != null) {
            gc.drawImage(herbeToUse, x, y);
        } else {
            // Fallback : couleur noire si aucun sprite d'herbe n'est chargé
            gc.setFill(EMPTY_COLOR);
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
    }
    
    /**
     * Dessine le joueur à sa position actuelle avec effets visuels
     * @param player Le joueur à dessiner
     */
    private void renderPlayer(Player player) {
        renderPlayer(player, null);
    }
    
    /**
     * Dessine le joueur à sa position actuelle avec effets visuels
     * @param player Le joueur à dessiner
     * @param exitDoor La porte de sortie (pour l'animation de victoire)
     */
    private void renderPlayer(Player player, ExitDoor exitDoor) {
        // Calculer les décalages pour centrer dans la fenêtre
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;

        // CAS 1 : Le joueur est dans sa séquence de mort.
        if (player.isDying()) {
            // Si l'animateur n'est pas déjà en train de jouer l'animation de mort, on la démarre.
            // Cela ne se produit qu'une seule fois.
            if (!bombermanAnimator.isDead()) {
                bombermanAnimator.startDeathAnimation(() -> {
                    System.out.println("💀 Animation de mort terminée pour le joueur (callback GridRenderer)");
                    // ✨ **CORRIGÉ** : Utiliser la queue des callbacks pour la cohérence avec le mode coopération
                    runNextDeathCallback();
                });
            }

            // Pendant toute la durée de la mort, on affiche l'animation correspondante.
            renderDeadPlayer(player, horizontalOffset);
            return; // On ne fait rien d'autre.
        }
        
        // CAS 1.5 : Le joueur est dans sa séquence de victoire.
        if (player.isWinning()) {
            // Si l'animateur n'est pas déjà en train de jouer l'animation de victoire, on la démarre.
            if (!bombermanAnimator.isWinning()) {
                bombermanAnimator.startWinAnimation(() -> {
                    System.out.println("🎉 Animation de victoire terminée pour le joueur (callback GridRenderer)");
                    if (onWinAnimationCompleteCallback != null) {
                        onWinAnimationCompleteCallback.run();
                    }
                });
            }

            // Pendant toute la durée de la victoire, on affiche l'animation correspondante.
            renderWinningPlayer(player, horizontalOffset, exitDoor);
            return; // On ne fait rien d'autre.
        }

        // CAS 2 : Le joueur est vivant.
        // Il se peut que l'animateur soit encore dans l'état "mort" (juste après un respawn).
        // Si c'est le cas, on le remet en vie.
        if (bombermanAnimator.isDead()) {
            bombermanAnimator.revive();
        }

        // --- Logique de rendu normale pour un joueur vivant ---

        // Mettre à jour la direction de l'animateur
        bombermanAnimator.setDirection(player.getCurrentDirection());
        
        // Gérer l'animation de marche selon l'état du joueur
        if (player.isWalking() && !bombermanAnimator.isWalking()) {
            bombermanAnimator.startWalking();
        } else if (!player.isWalking() && bombermanAnimator.isWalking()) {
            bombermanAnimator.stopWalking();
        }
        
        // Mettre à jour la position de l'animateur
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            bombermanAnimator.setPixelPosition(
                fluidPlayer.getPixelX(),
                fluidPlayer.getPixelY(),
                horizontalOffset,
                GRID_VERTICAL_OFFSET
            );
        } else {
            bombermanAnimator.setPosition(
                player.getX(), 
                player.getY(), 
                horizontalOffset, 
                GRID_VERTICAL_OFFSET
            );
        }
        
        // Calculer la position pour les effets visuels
        int effectX, effectY;
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            effectX = (int) (fluidPlayer.getRenderX() + horizontalOffset);
            effectY = (int) (fluidPlayer.getRenderY() + GRID_VERTICAL_OFFSET);
        } else {
            effectX = (int) (player.getX() * CELL_SIZE + PLAYER_OFFSET + horizontalOffset);
            effectY = player.getY() * CELL_SIZE + PLAYER_OFFSET + GRID_VERTICAL_OFFSET;
        }
        
        // Dessiner les effets et le joueur
        renderPlayerEffects(player, effectX, effectY);
        bombermanAnimator.renderWithEffects(gc, player.isInvincible(), 1.0);
        renderPlayerOverlayEffects(player, effectX, effectY);
    }
    
    /**
     * ✨ **NOUVEAU** : Dessine le joueur mort avec l'animation de mort
     * @param player Le joueur mort
     * @param horizontalOffset Décalage horizontal pour centrage
     */
    private void renderDeadPlayer(Player player, double horizontalOffset) {
        // Mettre à jour la position de l'animateur à la dernière position connue
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            bombermanAnimator.setPixelPosition(
                fluidPlayer.getPixelX(),
                fluidPlayer.getPixelY(),
                horizontalOffset,
                GRID_VERTICAL_OFFSET
            );
        } else {
            // Fallback pour Player classique
            bombermanAnimator.setPosition(
                player.getX(), 
                player.getY(), 
                horizontalOffset, 
                GRID_VERTICAL_OFFSET
            );
        }
        
        // Dessiner uniquement l'animation de mort (sans effets de joueur vivant)
        bombermanAnimator.renderWithEffects(gc, false, 1.0);
    }
    
    /**
     * Détermine la couleur du joueur selon ses effets actifs
     * @param player Le joueur
     * @return Couleur appropriée
     */
    private Color getPlayerColor(Player player) {
        if (player.isInvincible()) {
            return Color.LIGHTBLUE; // Bleu clair pour l'invincibilité
        } else if (player.hasShield()) {
            return Color.LIGHTCYAN; // Cyan clair pour le bouclier
        } else if (player.hasSpeedBurst()) {
            return Color.LIGHTYELLOW; // Jaune clair pour la vitesse
        } else {
            return PLAYER_COLOR; // Couleur normale
        }
    }
    
    /**
     * Dessine les effets de fond du joueur (auras, glows)
     * @param player Le joueur
     * @param x Position X en pixels
     * @param y Position Y en pixels
     */
    private void renderPlayerEffects(Player player, int x, int y) {
        // Effet de bouclier : aura bleue
        if (player.hasShield()) {
            gc.setFill(Color.web("#0080FF", 0.3)); // Bleu semi-transparent
            gc.fillOval(x - 4, y - 4, PLAYER_SIZE + 8, PLAYER_SIZE + 8);
        }
        
        // Effet de speed burst : aura jaune clignotante
        if (player.hasSpeedBurst()) {
            long currentTime = System.currentTimeMillis();
            boolean shouldGlow = (currentTime / 100) % 2 == 0; // Clignote plus vite
            if (shouldGlow) {
                gc.setFill(Color.web("#FFFF00", 0.4)); // Jaune semi-transparent
                gc.fillOval(x - 2, y - 2, PLAYER_SIZE + 4, PLAYER_SIZE + 4);
            }
        }
    }
    
    /**
     * Dessine les effets de premier plan du joueur (contours, particules)
     * @param player Le joueur
     * @param x Position X en pixels
     * @param y Position Y en pixels
     */
    private void renderPlayerOverlayEffects(Player player, int x, int y) {
        // Effet de bouclier : contour bleu
        if (player.hasShield()) {
            gc.setStroke(Color.DODGERBLUE);
            gc.setLineWidth(2);
            gc.strokeRect(x - 1, y - 1, PLAYER_SIZE + 2, PLAYER_SIZE + 2);
        }
        
        // Reset stroke
        gc.setLineWidth(1);
    }
    
    /**
     * ✨ **MODIFIÉ** : Dessine une bombe animée à sa position
     * Animation suit le pattern : 2 -> 3 -> 2 -> 1 -> 2 -> 3 -> 2 -> 1 -> BOOM
     * @param bomb La bombe à dessiner
     */
    private void renderBomb(Bomb bomb) {
        // Calculer la position en pixels avec décalage horizontal et vertical
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        int x = (int) (bomb.getX() * CELL_SIZE + BOMB_OFFSET + horizontalOffset);
        int y = bomb.getY() * CELL_SIZE + BOMB_OFFSET + GRID_VERTICAL_OFFSET;
        
        // Obtenir le sprite à afficher selon l'animation de la bombe
        int spriteNumber = bomb.getCurrentSpriteNumber();
        Image bombSprite = getBombSprite(spriteNumber);
        
        if (bombSprite != null) {
            // Dessiner le sprite de la bombe
            gc.drawImage(bombSprite, x, y, BOMB_SIZE, BOMB_SIZE);
        } else {
            // Fallback : dessiner un rectangle coloré si les sprites ne sont pas disponibles
        gc.setFill(BOMB_COLOR);
        gc.fillRect(x, y, BOMB_SIZE, BOMB_SIZE);
    }
    }
    
    /**
     * ✨ **NOUVEAU** : Retourne le sprite correspondant au numéro donné
     * @param spriteNumber Numéro du sprite (1, 2, ou 3)
     * @return L'image du sprite correspondant, ou null si non trouvé
     */
    private Image getBombSprite(int spriteNumber) {
        switch (spriteNumber) {
            case 1:
                return bomb1Image;
            case 2:
                return bomb2Image;
            case 3:
                return bomb3Image;
            default:
                System.err.println("Numéro de sprite de bombe invalide : " + spriteNumber);
                return bomb2Image; // Fallback sur le sprite 2
        }
    }
    
    /**
     * ✨ **MODIFIÉ** : Dessine une explosion avec sprites animés sophistiqués
     * Utilise l'ExplosionAnimator pour rendu contextuel des différents types de segments
     * @param explosion L'explosion à dessiner
     */
    private void renderExplosion(Explosion explosion) {
        // Chercher si cette explosion est déjà suivie
        ExplosionAnimator animator = findAnimatorForExplosion(explosion);
        
        // Si pas d'animateur pour cette explosion, en créer un nouveau
        if (animator == null && explosion.isActive()) {
            System.out.println("🔥 Création nouvelle animation explosion à (" + explosion.getCenterX() + ", " + explosion.getCenterY() + ") - Portée: " + currentExplosionRange);
            animator = new ExplosionAnimator();
            animator.startExplosion(explosion, currentExplosionRange); // Utiliser la vraie portée
            explosionAnimators.add(animator);
            trackedExplosions.add(explosion);
        }
        
        // Rendre l'animation d'explosion si elle existe et est active
        if (animator != null && animator.isActive()) {
            animator.render(gc, canvas);
        }
        // ✨ **MODIFIÉ** : NE PAS utiliser le fallback si l'animateur s'est terminé normalement
        // Cela évite de voir l'ancien rendu rectangulaire après l'animation des sprites
    }
    
    /**
     * ✨ **NOUVEAU** : Rendu de fallback pour les explosions si les sprites ne sont pas disponibles
     * @param explosion L'explosion à dessiner en mode basique
     */
    private void renderExplosionFallback(Explosion explosion) {
        gc.setFill(EXPLOSION_COLOR);
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        
        for (Explosion.ExplosionCell cell : explosion.getAffectedCells()) {
            int x = (int) (cell.getX() * CELL_SIZE + horizontalOffset);
            int y = cell.getY() * CELL_SIZE + GRID_VERTICAL_OFFSET;
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
    }
    
    /**
     * ✨ **MODIFIÉ** : Trouve l'animateur associé à une explosion donnée par position
     * @param explosion L'explosion à rechercher
     * @return L'animateur correspondant ou null si non trouvé
     */
    private ExplosionAnimator findAnimatorForExplosion(Explosion explosion) {
        for (int i = 0; i < trackedExplosions.size(); i++) {
            Explosion tracked = trackedExplosions.get(i);
            // Comparer par position plutôt que par référence d'objet
            if (tracked.getCenterX() == explosion.getCenterX() && 
                tracked.getCenterY() == explosion.getCenterY()) {
                return explosionAnimators.get(i);
            }
        }
        return null;
    }
    
         /**
      * ✨ **MODIFIÉ** : Nettoie les animateurs d'explosion terminés
      * À appeler régulièrement pour éviter les fuites mémoire et le fallback
      */
     public void cleanupExplosionAnimators() {
         for (int i = explosionAnimators.size() - 1; i >= 0; i--) {
             ExplosionAnimator animator = explosionAnimators.get(i);
             Explosion explosion = trackedExplosions.get(i);
             
             // Supprimer immédiatement les animateurs terminés pour éviter le fallback
             if (!animator.isActive()) {
                 System.out.println("🧹 Nettoyage animateur d'explosion terminé à (" + explosion.getCenterX() + ", " + explosion.getCenterY() + ")");
                 animator.dispose();
                 explosionAnimators.remove(i);
                 trackedExplosions.remove(i);
             }
             // Ou supprimer si l'explosion logique est terminée
             else if (!explosion.isActive()) {
                 System.out.println("🧹 Nettoyage explosion logique terminée à (" + explosion.getCenterX() + ", " + explosion.getCenterY() + ")");
                 animator.dispose();
                 explosionAnimators.remove(i);
                 trackedExplosions.remove(i);
             }
         }
     }
     
     /**
      * ✨ **NOUVEAU** : Met à jour la portée d'explosion pour les futures explosions
      * @param range La nouvelle portée d'explosion
      */
     public void setExplosionRange(int range) {
         this.currentExplosionRange = range;
    }
    
    /**
     * Dessine un ennemi à sa position actuelle avec effet visuel d'invincibilité
     * @param enemy L'ennemi à dessiner
     */
    private void renderEnemy(Enemy enemy) {
        if (!enemy.isAlive()) {
            return;
        }
        
        // Calculer le décalage horizontal pour centrer la grille
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        
        // Vérifier si c'est un ennemi avec mouvement fluide
        if (enemy instanceof FluidMovementEnemy) {
            FluidMovementEnemy fluidEnemy = (FluidMovementEnemy) enemy;
            
            // Utiliser les coordonnées pixel de l'ennemi fluide
            double pixelX = fluidEnemy.getPixelX() + horizontalOffset;
            double pixelY = fluidEnemy.getPixelY() + GRID_VERTICAL_OFFSET;
            
            // Mettre à jour la position de l'animateur
            EnemyAnimator animator = fluidEnemy.getAnimator();
            if (animator != null) {
                animator.setPixelPosition(pixelX, pixelY, 0, 0);
                
                // Rendre avec effets d'invincibilité si nécessaire
                animator.renderWithEffects(gc, enemy.isInvincible(), 1.0);
            }
        } else {
            // Mode de rendu classique pour les ennemis non-fluides (fallback)
            int x = (int) (enemy.getX() * CELL_SIZE + ENEMY_OFFSET + horizontalOffset);
        int y = enemy.getY() * CELL_SIZE + ENEMY_OFFSET + GRID_VERTICAL_OFFSET;
        
        // Choisir la couleur selon l'état d'invincibilité
        if (enemy.isInvincible()) {
            // Couleur plus claire pour les ennemis invincibles (effet de clignotement)
            long currentTime = System.currentTimeMillis();
            boolean shouldBlink = (currentTime / 200) % 2 == 0; // Clignotement toutes les 200ms
            
            if (shouldBlink) {
                gc.setFill(Color.web("#FF6666")); // Rouge plus clair
            } else {
                gc.setFill(Color.web("#FFAAAA")); // Rouge très clair
            }
        } else {
            gc.setFill(ENEMY_COLOR); // Couleur normale
        }
        
        gc.fillRect(x, y, ENEMY_SIZE, ENEMY_SIZE);
        }
    }
    
    /**
     * Méthode utilitaire pour redessiner une zone spécifique
     * (Utile pour les futures évolutions avec animations)
     * @param column Position en colonne
     * @param row Position en ligne
     */
    public void renderCellAt(int column, int row) {
        renderCell(column, row);
    }
    
    /**
     * @return La taille d'une cellule en pixels
     */
    public static int getCellSize() {
        return CELL_SIZE;
    }
    
    /**
     * @return Le canvas utilisé pour le rendu
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * Dessine l'interface utilisateur avec zone dédiée en bas
     * Zone de jeu : 0-528px (grille + ligne du haut uniquement)
     * Zone d'interface : 528-780px (252px dédiés pour tout le reste)
     * 
     * Ligne 1 (haut) : LEVEL, SCORE, HIGHSCORE MIEUX RÉPARTIS
     * Zone bas dédiée : BOMBES + indicateurs de bonus + notifications (3 lignes)
     * 
     * @param player Le joueur pour afficher ses informations
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     */
    private void renderUI(Player player, int highScore, int currentLevel) {
        // Nettoyer les notifications expirées
        cleanExpiredNotifications();
        
        // Configurer la police pour l'UI
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE));
        gc.setFill(UI_TEXT_COLOR);
        
        // === LIGNE 1 (HAUT) : LEVEL, SCORE, HIGHSCORE DANS SA ZONE DÉDIÉE ===
        // Position verticale centrée dans la zone ATH (50px)
        int topUiY = ATH_HEIGHT / 2 + UI_FONT_SIZE / 2;
        double canvasWidth = canvas.getWidth(); // 720px
        
        gc.setTextAlign(TextAlignment.LEFT); // Alignement à gauche pour LEVEL
        
        // Répartition optimisée sur toute la largeur avec marges appropriées
        double levelX = 30;                           // 30px du bord gauche (plus tôt)
        double scoreX = canvasWidth / 2.0;            // 360px - centre parfait (inchangé)
        double highScoreX = canvasWidth - 30;         // 690px - 30px du bord droit (plus loin)
        
        // Afficher le niveau (commence plus tôt)
        String levelText = "LEVEL : " + currentLevel;
        gc.fillText(levelText, levelX, topUiY);
        
        // Afficher le score actuel (centré)
        gc.setTextAlign(TextAlignment.CENTER);
        String scoreText = "SCORE : " + player.getScore();
        gc.fillText(scoreText, scoreX, topUiY);
        
        // Afficher le high score (aligné à droite, plus loin du bord)
        gc.setTextAlign(TextAlignment.RIGHT);
        String highScoreText = "HIGHSCORE : " + highScore;
        gc.fillText(highScoreText, highScoreX, topUiY);
        
        // === ZONE DÉDIÉE EN BAS : TOUT LE RESTE ===
        renderDedicatedUIArea(player);
    }
    
    /**
     * Dessine la zone d'interface dédiée en bas (528-780px = 252px)
     * @param player Le joueur
     */
    private void renderDedicatedUIArea(Player player) {
        // Dessiner un fond légèrement différent pour la zone d'interface
        gc.setFill(Color.web("#111111")); // Fond sombre pour séparer visuellement
        gc.fillRect(0, GRID_VERTICAL_OFFSET + GAME_AREA_HEIGHT, canvas.getWidth(), UI_AREA_HEIGHT);
        
        // Position de départ de la zone d'interface (après header + grille)
        int uiStartY = GRID_VERTICAL_OFFSET + GAME_AREA_HEIGHT + 22;
        
        // === LIGNE 1 DE LA ZONE UI : BOMBES (centré) ===
        renderBombsCounter(player, uiStartY + 30); // était +20, maintenant +30
        
        // === LIGNE 2 DE LA ZONE UI : INDICATEURS DE BONUS (4 colonnes fixes) ===
        renderBonusIndicatorsInDedicatedArea(player, uiStartY + 75); // était +50, maintenant +75
        
        // === LIGNES 3+ DE LA ZONE UI : NOTIFICATIONS EMPILÉES (ZONE TRÈS ÉLARGIE) ===
        renderNotificationsInDedicatedArea(uiStartY + 120); // Position optimisée avec 80px d'espace supplémentaire
    }
    
    /**
     * Dessine le compteur de bombes et vies dans la zone dédiée
     * @param player Le joueur
     * @param yPosition Position Y
     */
    private void renderBombsCounter(Player player, int yPosition) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE + 3));
        
        double canvasWidth = canvas.getWidth(); // 720px
        
        // Répartition améliorée pour VIES et BOMBES avec plus d'espace
        double leftX = 60;                        // 60px du bord gauche (plus tôt)
        double rightX = canvasWidth - 60;         // 660px - 60px du bord droit (plus loin)
        
        // Afficher les vies avec cœur rouge (aligné à gauche)
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.RED);
        String lifeText = "❤️ VIES : " + player.getDisplayLives() + "/5";
        gc.fillText(lifeText, leftX, yPosition);
        
        // Afficher les bombes avec émoji bombe (aligné à droite)
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFill(EXTRA_BOMB_COLOR);
        String bombText = "💣 BOMBES : " + player.getAvailableBombs() + "/" + player.getMaxBombs();
        gc.fillText(bombText, rightX, yPosition);
        
        // Reset
        gc.setFill(UI_TEXT_COLOR);
    }
    
    /**
     * Dessine les indicateurs de bonus dans la zone dédiée avec positions fixes
     * @param player Le joueur
     * @param yPosition Position Y
     */
    private void renderBonusIndicatorsInDedicatedArea(Player player, int yPosition) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE - 1));
        
        double canvasWidth = canvas.getWidth(); // 720px
        
        // Alignement avec la ligne VIES/BOMBES du dessus, mais avec plus d'espacement
        double leftEdge = 60;                     // Même position que VIES (60px)
        double rightEdge = canvasWidth - 60;      // Même position que BOMBES (660px)
        
        // Positions ajustées pour éviter les chevauchements
        double vitesseX = 200;                    // Position pour vitesse
        double porteeX = rightEdge;               // Aligné à droite comme BOMBES
        
        // Colonne 1 : Vitesse (centrée)
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(UI_TEXT_COLOR);
        if (player.getSpeed() > 1.0) {
            gc.fillText("→ VITESSE: " + String.format("%.1f", player.getSpeed()), vitesseX, yPosition);
        } else {
            gc.fillText("→ VITESSE: 1.0", vitesseX, yPosition);
        }
        
        // Colonne 2 : Portée (aligné à droite comme BOMBES)
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFill(EXPLOSION_EXPANDER_COLOR);
        if (player.getRange() > 1) {
            gc.fillText("○ PORTÉE: " + player.getRange(), porteeX, yPosition);
        } else {
            gc.fillText("○ PORTÉE: 1", porteeX, yPosition);
        }
        
        // Reset
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.LEFT);
    }
    
    /**
     * Dessine les notifications dans la zone dédiée (empilées verticalement)
     * @param yPosition Position Y de base
     */
    private void renderNotificationsInDedicatedArea(int yPosition) {
        if (recentNotifications.isEmpty()) {
            // Afficher un message par défaut simple et élégant
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            gc.setFill(Color.web("#999999", 0.6)); // Gris clair avec opacité 60%
            gc.setTextAlign(TextAlignment.CENTER);
            
            double canvasCenterX = canvas.getWidth() / 2.0; // 360px - centre parfait du canvas
            gc.fillText("Aucun événement récent.", canvasCenterX, yPosition);
            
            gc.setFill(UI_TEXT_COLOR);
            gc.setTextAlign(TextAlignment.LEFT);
            return;
        }
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14)); // Police fixe 14px pour meilleure lisibilité
        gc.setTextAlign(TextAlignment.CENTER); // Centrer les notifications aussi
        
        double canvasCenterX = canvas.getWidth() / 2.0; // 360px - centre parfait du canvas
        
        // Afficher les notifications empilées verticalement (les plus récentes en haut)
        for (int i = 0; i < recentNotifications.size(); i++) {
            String notification = recentNotifications.get(recentNotifications.size() - 1 - i);
            long timestamp = notificationTimestamps.get(notificationTimestamps.size() - 1 - i);
            long age = System.currentTimeMillis() - timestamp;
            
            // Effet de fade
            double alpha = 1.0 - (double) age / NOTIFICATION_DURATION;
            alpha = Math.max(0.4, alpha);
            
            gc.setFill(Color.web("#00FF00", alpha));
            
            // Position verticale (empiler vers le bas avec espacement optimal)
            int notificationY = yPosition + (i * 22); // Espacement fixe 22px pour lisibilité parfaite
            
            // Centrer parfaitement chaque notification
            gc.fillText("→ " + notification, canvasCenterX, notificationY);
        }
        
        // Reset
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.LEFT);
    }
    
    /**
     * Dessine l'écran de menu de démarrage (version simple pour compatibilité)
     */
    public void renderStartMenu() {
        renderStartMenu(0, new String[]{"NORMAL GAME", "BATTLE MODE", "PASSWORD"}, new boolean[]{true, false, false});
    }
    
    /**
     * Dessine l'écran de menu de démarrage interactif
     * @param selectedIndex Index de l'option sélectionnée
     * @param options Tableau des options du menu
     * @param enabledOptions Tableau indiquant quelles options sont actives
     */
    public void renderStartMenu(int selectedIndex, String[] options, boolean[] enabledOptions) {
        // Effacer l'écran avec un fond noir
        gc.setFill(EMPTY_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Afficher l'image d'intro si elle est chargée
        if (introImage != null) {
            // Calculer les dimensions pour centrer l'image sans l'étirer
            double imageWidth = introImage.getWidth();
            double imageHeight = introImage.getHeight();
            double canvasWidth = canvas.getWidth();
            double canvasHeight = canvas.getHeight();
            
            // Calculer le facteur d'échelle pour ajuster l'image à la fenêtre
            double scaleX = canvasWidth / imageWidth;
            double scaleY = canvasHeight / imageHeight;
            double scale = Math.min(scaleX, scaleY); // Prendre le plus petit pour ne pas étirer
            
            // Calculer les nouvelles dimensions et la position de centrage
            double scaledWidth = imageWidth * scale;
            double scaledHeight = imageHeight * scale;
            double x = (canvasWidth - scaledWidth) / 2.0;
            double y = (canvasHeight - scaledHeight) / 2.0;
            
            // Dessiner l'image centrée et mise à l'échelle
            gc.drawImage(introImage, x, y, scaledWidth, scaledHeight);
        }
        
        // Ajouter un overlay semi-transparent pour améliorer la lisibilité du texte
        gc.setFill(Color.web("#000000", 0.6));
        gc.fillRect(0, canvas.getHeight() - 200, canvas.getWidth(), 200);
        
        // Calculer les positions centrales
        double canvasCenterX = canvas.getWidth() / 2.0;
        
        // Afficher les options du menu
        renderMenuOptions(canvasCenterX, selectedIndex, options, enabledOptions);
        
        // Afficher les instructions de navigation en bas
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("↑/↓ : Naviguer | ENTRÉE : Sélectionner", canvasCenterX, canvas.getHeight() - 20);
    }
    
    /**
     * Dessine les options du menu interactif
     * @param centerX Position horizontale centrale
     * @param selectedIndex Index de l'option sélectionnée
     * @param options Tableau des options du menu
     * @param enabledOptions Tableau indiquant quelles options sont actives
     */
    private void renderMenuOptions(double centerX, int selectedIndex, String[] options, boolean[] enabledOptions) {
        // Configurer la police pour les options
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Position de départ des options (centrées dans la zone de texte)
        double startY = canvas.getHeight() - 150;
        double lineHeight = 35;
        
        for (int i = 0; i < options.length; i++) {
            double optionY = startY + (i * lineHeight);
            
            // Déterminer la couleur selon l'état de l'option
            Color textColor;
            if (!enabledOptions[i]) {
                // Option désactivée - gris clair
                textColor = Color.web("#AAAAAA");
            } else if (i == selectedIndex) {
                // Option sélectionnée - jaune/orange vif
                textColor = Color.web("#FFCC00");
            } else {
                // Option active non sélectionnée - blanc
                textColor = UI_TEXT_COLOR;
            }
            
            gc.setFill(textColor);
            
            // Afficher le curseur pour l'option sélectionnée
            if (i == selectedIndex) {
                // Dessiner le curseur à gauche
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.fillText("►", centerX - 80, optionY);
                
                // Repositionner pour le texte
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText(options[i], centerX - 70, optionY);
            } else {
                // Pas de curseur, texte centré
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(options[i], centerX, optionY);
            }
        }
        
        // Réinitialiser l'alignement
        gc.setTextAlign(TextAlignment.CENTER);
    }
    
    /**
     * Dessine l'écran de game over avec option de rejeu et score final
     * @param player Le joueur pour afficher son score final
     */
    public void renderGameOverScreen(Player player) {
        // Dessiner d'abord l'overlay de mort
        renderDeathOverlay();
        
        // Configurer la police pour le message principal
        gc.setFont(Font.font("Arial", FontWeight.BOLD, GAME_OVER_FONT_SIZE));
        gc.setFill(GAME_OVER_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Calculer les positions centrales parfaites
        double canvasCenterX = canvas.getWidth() / 2.0;  // 360px - centre parfait horizontal
        double canvasCenterY = canvas.getHeight() / 2.0; // 390px - centre parfait vertical
        
        // Afficher le message GAME OVER (parfaitement centré)
        gc.fillText("GAME OVER", canvasCenterX, canvasCenterY - 40);
        
        // Afficher le score final
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 36)); // était 24, maintenant 36
        gc.setFill(UI_TEXT_COLOR);
        gc.fillText("SCORE FINAL : " + player.getScore(), canvasCenterX, canvasCenterY);
        
        // Configurer la police pour les instructions de rejeu
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 27)); // était 18, maintenant 27
        gc.fillText("Appuyez sur ENTRÉE pour rejouer", canvasCenterX, canvasCenterY + 40);
    }
    
    /**
     * Dessine l'écran de game over avec option de rejeu (version simplifiée)
     */
    public void renderGameOverScreen() {
        // Version simplifiée sans score (pour compatibilité)
        renderDeathOverlay();
        
        gc.setFont(Font.font("Arial", FontWeight.BOLD, GAME_OVER_FONT_SIZE));
        gc.setFill(GAME_OVER_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        
        double canvasCenterX = canvas.getWidth() / 2.0;  // 360px - centre parfait horizontal
        double canvasCenterY = canvas.getHeight() / 2.0; // 390px - centre parfait vertical
        
        gc.fillText("GAME OVER", canvasCenterX, canvasCenterY - 20);
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 27)); // était 18, maintenant 27
        gc.setFill(UI_TEXT_COLOR);
        gc.fillText("Appuyez sur ENTRÉE pour rejouer", canvasCenterX, canvasCenterY + 40);
    }
    
    /**
     * Dessine l'écran de niveau terminé avec transition vers le niveau suivant
     * @param currentLevel Le niveau qui vient d'être terminé
     * @param player Le joueur pour afficher son score actuel
     */
    public void renderLevelCompletedScreen(int currentLevel, Player player) {
        // Dessiner un fond semi-transparent (mais moins sombre que le game over)
        gc.setFill(Color.web("#000000", 0.3));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Configurer la police pour le message principal
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48)); // était 32, maintenant 48
        gc.setFill(Color.LIGHTGREEN);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Calculer les positions centrales parfaites
        double canvasCenterX = canvas.getWidth() / 2.0;  // 360px - centre parfait horizontal
        double canvasCenterY = canvas.getHeight() / 2.0; // 390px - centre parfait vertical
        
        // Afficher le message de niveau terminé (parfaitement centré)
        gc.fillText("NIVEAU " + currentLevel + " TERMINÉ !", canvasCenterX, canvasCenterY - 60);
        
        // Afficher le score actuel
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30)); // était 20, maintenant 30
        gc.setFill(UI_TEXT_COLOR);
        gc.fillText("Score actuel : " + player.getScore(), canvasCenterX, canvasCenterY - 20);
        
        // Afficher les informations du niveau suivant
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 27)); // était 18, maintenant 27
        gc.fillText("Niveau suivant : " + (currentLevel + 1), canvasCenterX, canvasCenterY + 20);
        
        // Afficher les instructions
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 24)); // était 16, maintenant 24
        gc.fillText("Appuyez sur ENTRÉE pour continuer", canvasCenterX, canvasCenterY + 60);
    }
    
    /**
     * Dessine un overlay semi-transparent pour assombrir l'écran à la mort
     */
    private void renderDeathOverlay() {
        gc.setFill(DEATH_OVERLAY_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    /**
     * Dessine tous les power-ups visibles
     * @param powerUps Liste des power-ups à dessiner
     */
    private void renderPowerUps(List<PowerUp> powerUps) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isVisible()) {
                renderPowerUp(powerUp);
            }
        }
    }
    
    /**
     * Dessine un power-up individuel à sa position avec effet de pulsation
     * @param powerUp Le power-up à dessiner
     */
    private void renderPowerUp(PowerUp powerUp) {
        // Calculer la position en pixels avec décalage horizontal et vertical
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        int x = (int) (powerUp.getX() * CELL_SIZE + POWER_UP_OFFSET + horizontalOffset);
        int y = powerUp.getY() * CELL_SIZE + POWER_UP_OFFSET + GRID_VERTICAL_OFFSET;
        
        // Cas spéciaux pour les power-ups animés : utiliser les sprites animés
        if (powerUp.getType() == PowerUpType.EXPLOSION_EXPANDER) {
            renderExplosionExpanderSprite(x, y);
            return;
        } else if (powerUp.getType() == PowerUpType.EXTRA_BOMB) {
            renderExtraBombSprite(x, y);
            return;
        }
        
        // Rendu classique pour les autres power-ups (fallback)
        renderStandardPowerUp(powerUp, x, y);
    }
    
    /**
     * ✨ **NOUVEAU** : Rendu spécial pour EXPLOSION_EXPANDER avec sprites animés
     */
    private void renderExplosionExpanderSprite(int x, int y) {
        // Obtenir le sprite actuel de l'EXPLOSION_EXPANDER
        Image currentSprite = explosionExpanderAnimator.getCurrentSprite();
        
        if (currentSprite != null && explosionExpanderAnimator.isReady()) {
            // Dessiner uniquement le sprite animé (taille du power-up) sans effets supplémentaires
            // pour éviter le chevauchement avec l'ancien design
            gc.drawImage(currentSprite, x, y, POWER_UP_SIZE, POWER_UP_SIZE);
            
        } else {
            // Fallback vers l'ancien rendu si les sprites ne sont pas disponibles
            renderStandardPowerUpFallback(PowerUpType.EXPLOSION_EXPANDER, x, y);
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Rendu spécial pour EXTRA_BOMB avec sprites animés
     */
    private void renderExtraBombSprite(int x, int y) {
        // Obtenir le sprite actuel de l'EXTRA_BOMB
        Image currentSprite = extraBombAnimator.getCurrentSprite();
        
        if (currentSprite != null && extraBombAnimator.isReady()) {
            // Dessiner uniquement le sprite animé (taille du power-up) sans effets supplémentaires
            // pour éviter le chevauchement avec l'ancien design
            gc.drawImage(currentSprite, x, y, POWER_UP_SIZE, POWER_UP_SIZE);
            
        } else {
            // Fallback vers l'ancien rendu si les sprites ne sont pas disponibles
            renderStandardPowerUpFallback(PowerUpType.EXTRA_BOMB, x, y);
        }
    }
    
    /**
     * Rendu classique pour les power-ups standards (EXTRA_BOMB)
     */
    private void renderStandardPowerUp(PowerUp powerUp, int x, int y) {
        // Effet de pulsation pour attirer l'attention
        long currentTime = System.currentTimeMillis();
        double pulseFactor = 0.8 + 0.2 * Math.sin(currentTime * 0.01); // Pulsation entre 0.8 et 1.0
        
        int pulsedSize = (int) (POWER_UP_SIZE * pulseFactor);
        int pulsedOffset = (POWER_UP_SIZE - pulsedSize) / 2;
        
        // Déterminer la couleur selon le type de power-up
        Color powerUpColor = getPowerUpColor(powerUp.getType());
        
        // Dessiner l'aura/glow pour les power-ups temporaires
        if (!powerUp.getType().isPermanent()) {
            // Aura clignotante pour les power-ups temporaires
            boolean shouldGlow = (currentTime / 200) % 2 == 0;
            if (shouldGlow) {
                gc.setFill(Color.web(powerUpColor.toString(), 0.3));
                gc.fillOval(x - 3, y - 3, POWER_UP_SIZE + 6, POWER_UP_SIZE + 6);
            }
        }
        
        // Dessiner le power-up principal avec pulsation
        gc.setFill(powerUpColor);
        gc.fillRect(x + pulsedOffset, y + pulsedOffset, pulsedSize, pulsedSize);
        
        // Contour brillant pour les power-ups temporaires
        if (!powerUp.getType().isPermanent()) {
            gc.setStroke(powerUpColor.brighter());
            gc.setLineWidth(1.5);
            gc.strokeRect(x, y, POWER_UP_SIZE, POWER_UP_SIZE);
            gc.setLineWidth(1); // Reset
        }
    }
    
    /**
     * Rendu de secours pour les power-ups si les sprites ne sont pas disponibles
     */
    private void renderStandardPowerUpFallback(PowerUpType type, int x, int y) {
        Color powerUpColor = getPowerUpColor(type);
        gc.setFill(powerUpColor);
        gc.fillRect(x, y, POWER_UP_SIZE, POWER_UP_SIZE);
        
        // Contour simple
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, POWER_UP_SIZE, POWER_UP_SIZE);
    }
    
    /**
     * Détermine la couleur d'affichage selon le type de power-up
     * @param type Type du power-up
     * @return Couleur correspondante
     */
    private Color getPowerUpColor(PowerUpType type) {
        switch (type) {
            case EXTRA_BOMB:
                return EXTRA_BOMB_COLOR;
            case EXPLOSION_EXPANDER:
                return EXPLOSION_EXPANDER_COLOR;
            default:
                return Color.WHITE; // Couleur par défaut en cas d'erreur
        }
    }
    
    /**
     * Ajoute une notification temporaire (ex: power-up collecté)
     * @param message Message à afficher
     */
    public void addNotification(String message) {
        recentNotifications.add(message);
        notificationTimestamps.add(System.currentTimeMillis());
        
        // Limiter le nombre de notifications
        while (recentNotifications.size() > MAX_NOTIFICATIONS) {
            recentNotifications.remove(0);
            notificationTimestamps.remove(0);
        }
        
        System.out.println("NOTIFICATION: " + message);
    }
    
    /**
     * Nettoie les notifications expirées
     */
    private void cleanExpiredNotifications() {
        long currentTime = System.currentTimeMillis();
        
        for (int i = notificationTimestamps.size() - 1; i >= 0; i--) {
            if (currentTime - notificationTimestamps.get(i) > NOTIFICATION_DURATION) {
                recentNotifications.remove(i);
                notificationTimestamps.remove(i);
            }
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Dessine la porte de sortie animée avec les sprites porte_1.png et porte_2.png
     * @param exitDoor La porte de sortie à dessiner
     */
    private void renderExitDoor(ExitDoor exitDoor) {
        // Calculer la position en pixels avec décalage horizontal et vertical
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        int x = exitDoor.getX() * CELL_SIZE + (int) horizontalOffset;
        int y = exitDoor.getY() * CELL_SIZE + GRID_VERTICAL_OFFSET;
        
        // Obtenir le sprite actuel de la porte
        Image currentSprite = doorAnimator.getCurrentSprite();
        
        if (currentSprite != null && doorAnimator.isReady()) {
            // Effet de brillance pour la porte activée
            if (exitDoor.isActivated()) {
        long currentTime = System.currentTimeMillis();
                double glowIntensity = 0.3 + 0.2 * Math.sin(currentTime / 300.0); // Glow pulsant
                
                // Fond doré brillant
                gc.setFill(Color.web("#FFD700", glowIntensity));
                gc.fillRect(x - 3, y - 3, CELL_SIZE + 6, CELL_SIZE + 6);
            }
            
            // Dessiner le sprite de la porte (taille de la cellule complète)
            gc.drawImage(currentSprite, x, y, CELL_SIZE, CELL_SIZE);
        
            // Ajout d'un indicateur visuel si la porte est activée
        if (exitDoor.isActivated()) {
                // Contour doré brillant
                gc.setStroke(Color.web("#FFD700"));
                gc.setLineWidth(2);
                gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
                
                // Particules brillantes (effet optionnel)
                renderDoorActivatedEffect(x, y);
            }
            
        } else {
            // Fallback vers l'ancien rendu si les sprites ne sont pas disponibles
            renderExitDoorFallback(exitDoor, x, y);
        }
    }
    
    /**
     * Rendu de secours pour la porte si les sprites ne sont pas disponibles
     */
    private void renderExitDoorFallback(ExitDoor exitDoor, int x, int y) {
        // Porte elle-même (couleur différente selon l'état)
        gc.setFill(exitDoor.isActivated() ? EXIT_DOOR_COLOR : EXIT_DOOR_INACTIVE_COLOR);
        double doorSize = POWER_UP_SIZE;
        double doorOffset = (CELL_SIZE - doorSize) / 2;
        gc.fillRect(x + doorOffset, y + doorOffset, doorSize, doorSize);
        
        // Dessiner le contour de porte
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x + doorOffset, y + doorOffset, doorSize, doorSize);
        
        // Texte "EXIT" sur la porte activée
        if (exitDoor.isActivated()) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            gc.fillText("EXIT", x + doorOffset + 5, y + doorOffset + doorSize / 2 + 3);
        }
    }
    
    /**
     * Effet visuel pour la porte activée (particules brillantes)
     */
    private void renderDoorActivatedEffect(int x, int y) {
        long currentTime = System.currentTimeMillis();
        
        // Créer quelques "particules" brillantes autour de la porte
        for (int i = 0; i < 4; i++) {
            double angle = (currentTime / 1000.0 + i * Math.PI / 2) % (2 * Math.PI);
            double radius = 10 + 5 * Math.sin(currentTime / 400.0 + i);
            
            double particleX = x + CELL_SIZE / 2 + radius * Math.cos(angle);
            double particleY = y + CELL_SIZE / 2 + radius * Math.sin(angle);
            
            gc.setFill(Color.web("#FFD700", 0.7));
            gc.fillOval(particleX - 2, particleY - 2, 4, 4);
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Affiche l'overlay d'introduction de niveau avec "LEVEL X" et fond noir semi-transparent
     * @param currentLevel Le numéro du niveau actuel
     */
    public void renderLevelIntroOverlay(int currentLevel) {
        // Fond noir semi-transparent sur toute la fenêtre
        gc.setFill(Color.web("#000000", 0.7)); // 70% d'opacité
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Texte "LEVEL X" au centre
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 72)); // Police large et lisible
        gc.setFill(Color.web("#FFFF88")); // Jaune clair pour visibilité
        
        // Calculer la position centrale
        double centerX = canvas.getWidth() / 2.0;
        double centerY = canvas.getHeight() / 2.0;
        
        String levelText = "LEVEL " + currentLevel;
        gc.fillText(levelText, centerX, centerY);
        
        // Effet de contour pour améliorer la lisibilité
        gc.setStroke(Color.web("#FFCC00")); // Contour jaune/orange
        gc.setLineWidth(3);
        gc.strokeText(levelText, centerX, centerY);
        
        // Reset des propriétés graphiques
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.LEFT);
    }
    
    /**
     * Affiche le menu pause par-dessus le jeu figé, centré sur la zone de jeu uniquement
     * @param pauseMenu Le menu pause avec l'état actuel
     */
    public void renderPauseMenu(PauseMenu pauseMenu) {
        // Fond semi-transparent noir sur tout l'écran
        gc.setFill(Color.web("#000000", 0.6));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Calcul des dimensions de la zone de jeu (grille uniquement, sans l'ATH)
        double gameAreaWidth = grid.getColumns() * CELL_SIZE;   // 15 * 48 = 720px
        double gameAreaHeight = grid.getRows() * CELL_SIZE;     // 11 * 48 = 528px
        double gameAreaX = 0;  // La grille commence à x=0
        double gameAreaY = 0;  // La grille commence à y=0
        
        // Zone du menu pause (centrée par rapport à la zone de jeu uniquement)
        double menuWidth = 400;
        double menuHeight = 300;
        double menuX = gameAreaX + (gameAreaWidth - menuWidth) / 2;
        double menuY = gameAreaY + (gameAreaHeight - menuHeight) / 2;
        
        // Fond du menu (légèrement plus opaque)
        gc.setFill(Color.web("#000000", 0.8));
        gc.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 15, 15);
        
        // Bordure du menu
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(menuX, menuY, menuWidth, menuHeight, 15, 15);
        
        // Titre "PAUSE" (centré par rapport à la zone de jeu)
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gc.setTextAlign(TextAlignment.CENTER);
        double gameAreaCenterX = gameAreaX + gameAreaWidth / 2;
        gc.fillText("PAUSE", gameAreaCenterX, menuY + 60);
        
        // Options du menu
        String[] options = pauseMenu.getOptions();
        int selectedIndex = pauseMenu.getSelectedIndex();
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        double optionStartY = menuY + 120;
        double optionSpacing = 50;
        
        for (int i = 0; i < options.length; i++) {
            double optionY = optionStartY + (i * optionSpacing);
            
            // Mettre en surbrillance l'option sélectionnée
            if (i == selectedIndex) {
                // Fond de sélection
                gc.setFill(Color.web("#FFD700", 0.3));  // Or semi-transparent
                gc.fillRoundRect(menuX + 20, optionY - 25, menuWidth - 40, 40, 8, 8);
                
                // Texte en jaune (centré par rapport à la zone de jeu)
                gc.setFill(Color.YELLOW);
                gc.fillText("► " + options[i] + " ◄", gameAreaCenterX, optionY);
            } else {
                // Texte normal en blanc (centré par rapport à la zone de jeu)
                gc.setFill(Color.WHITE);
                gc.fillText(options[i], gameAreaCenterX, optionY);
            }
        }
        
        // Pas d'instructions en bas du menu (remplacées par l'option COMMANDES)
        
        // Reset des propriétés graphiques
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.LEFT);
        
        System.out.println("Menu pause affiché (centré sur zone de jeu) - Option sélectionnée : " + options[selectedIndex]);
    }
    
    /**
     * Affiche le panneau des commandes par-dessus le menu pause
     * @param isReturnButtonSelected Si le bouton "Retour" est sélectionné
     */
    public void renderCommandsPanel(boolean isReturnButtonSelected) {
        // Fond semi-transparent plus sombre pour masquer le menu pause
        gc.setFill(Color.web("#000000", 0.8));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Calcul des dimensions de la zone de jeu
        double gameAreaWidth = grid.getColumns() * CELL_SIZE;   // 720px
        double gameAreaHeight = grid.getRows() * CELL_SIZE;     // 528px
        double gameAreaX = 0;
        double gameAreaY = 0;
        
        // Zone du panneau des commandes (ajustée pour le contenu simplifié)
        double panelWidth = 450;
        double panelHeight = 300;
        double panelX = gameAreaX + (gameAreaWidth - panelWidth) / 2;
        double panelY = gameAreaY + (gameAreaHeight - panelHeight) / 2;
        
        // Fond du panneau (blanc cassé pour contraster)
        gc.setFill(Color.web("#F5F5F5"));
        gc.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Bordure du panneau
        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(3);
        gc.strokeRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Titre "COMMANDES"
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        gc.setTextAlign(TextAlignment.CENTER);
        double gameAreaCenterX = gameAreaX + gameAreaWidth / 2;
        gc.fillText("COMMANDES", gameAreaCenterX, panelY + 50);
        
        // Ligne de séparation sous le titre
        gc.setStroke(Color.web("#666666"));
        gc.setLineWidth(2);
        gc.strokeLine(panelX + 50, panelY + 70, panelX + panelWidth - 50, panelY + 70);
        
        // Contenu des commandes (simplifié)
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.setTextAlign(TextAlignment.LEFT);
        double commandsStartY = panelY + 110;
        double lineSpacing = 35;
        double leftMargin = panelX + 40;
        
        String[] commands = {
            "↑ ↓ ← → : Déplacement du joueur",
            "ESPACE : Poser une bombe",
            "ÉCHAP : Ouvrir/fermer le menu pause",
            "ENTRÉE : Valider une sélection"
        };
        
        gc.setFill(Color.web("#333333"));
        for (int i = 0; i < commands.length; i++) {
            double lineY = commandsStartY + (i * lineSpacing);
            gc.fillText(commands[i], leftMargin, lineY);
        }
        
        // Bouton "← RETOUR" (navigable)
        double buttonWidth = 150;
        double buttonHeight = 40;
        double buttonX = gameAreaCenterX - buttonWidth / 2;
        double buttonY = panelY + panelHeight - 70;
        
        // Fond du bouton selon l'état de sélection
        if (isReturnButtonSelected) {
            // Bouton sélectionné : fond doré comme dans le menu pause
            gc.setFill(Color.web("#FFD700", 0.3));
            gc.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            
            // Texte en jaune
            gc.setFill(Color.web("#FF8C00"));
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        } else {
            // Bouton non sélectionné : fond neutre
            gc.setFill(Color.web("#E0E0E0"));
            gc.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            
            // Texte en couleur normale
            gc.setFill(Color.web("#333333"));
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        }
        
        // Bordure du bouton
        gc.setStroke(Color.web("#666666"));
        gc.setLineWidth(1);
        gc.strokeRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
        
        // Texte du bouton (centré)
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("← RETOUR", gameAreaCenterX, buttonY + buttonHeight / 2 + 6);
        
        // Reset des propriétés graphiques
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.LEFT);
        
        System.out.println("Panneau des commandes affiché");
    }
    
    /**
     * ⏱️ Méthode de rendu complète avec timer global
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bombs Liste des bombes actives
     * @param explosions Liste des explosions actives
     * @param powerUps Liste des power-ups visibles à afficher
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     * @param exitDoor La porte de sortie (peut être null)
     * @param globalTimeRemaining Temps restant du timer global en millisecondes
     */
    public void render(Player player, List<Enemy> enemies, List<Bomb> bombs, List<Explosion> explosions, List<PowerUp> powerUps, int highScore, int currentLevel, ExitDoor exitDoor, long globalTimeRemaining) {
        // Dessiner d'abord la grille
        render();
        
        // Dessiner les explosions en premier (sous les autres éléments)
        if (explosions != null) {
            for (Explosion explosion : explosions) {
                if (explosion.isActive()) {
                    renderExplosion(explosion);
                }
            }
        }
        
        // Dessiner la porte de sortie en deuxième (sous les bombes/ennemis/joueur)
        if (exitDoor != null && exitDoor.isVisible()) {
            renderExitDoor(exitDoor);
        }
        
        // Dessiner les power-ups visibles
        if (powerUps != null) {
            renderPowerUps(powerUps);
        }
        
        // Dessiner les bombes (par-dessus la porte)
        if (bombs != null) {
            for (Bomb bomb : bombs) {
                if (bomb.isActive()) {
                    renderBomb(bomb);
                }
            }
        }
        
        // Dessiner les ennemis vivants (par-dessus la porte)
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    renderEnemy(enemy);
                }
            }
        }
        
        // ✨ **MODIFIÉ** : Dessiner le joueur en dernier (par-dessus tout, vivant OU mort pour l'animation)
        renderPlayer(player, exitDoor);
        
        // Dessiner l'interface utilisateur par-dessus tout (avec high score, niveau et timer)
        renderUIWithTimer(player, highScore, currentLevel, globalTimeRemaining);
        
        // Note: Le message GAME OVER est géré par renderGameOverScreen() appelé depuis Launcher
        // Pas de double appel ici pour éviter les doublons
    }
    
    /**
     * ✨ **MODE COOPÉRATION** : Rendu complet avec deux joueurs
     * @param player1 Le joueur 1
     * @param player2 Le joueur 2 (peut être null)
     * @param enemies Liste des ennemis vivants à afficher
     * @param bombs Liste des bombes actives à afficher
     * @param explosions Liste des explosions actives à afficher
     * @param powerUps Liste des power-ups visibles à afficher
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     * @param exitDoor La porte de sortie (peut être null)
     * @param globalTimeRemaining Temps restant du timer global en millisecondes
     */
    public void renderCooperation(Player player1, Player player2, List<Enemy> enemies, List<Bomb> bombs, List<Explosion> explosions, List<PowerUp> powerUps, int highScore, int currentLevel, ExitDoor exitDoor, long globalTimeRemaining) {
        // Dessiner d'abord la grille
        render();
        
        // Dessiner les explosions en premier (sous les autres éléments)
        if (explosions != null) {
            for (Explosion explosion : explosions) {
                if (explosion.isActive()) {
                    renderExplosion(explosion);
                }
            }
        }
        
        // Dessiner la porte de sortie en deuxième (sous les bombes/ennemis/joueur)
        if (exitDoor != null && exitDoor.isVisible()) {
            renderExitDoor(exitDoor);
        }
        
        // Dessiner les power-ups visibles
        if (powerUps != null) {
            renderPowerUps(powerUps);
        }
        
        // Dessiner les bombes (par-dessus la porte)
        if (bombs != null) {
            for (Bomb bomb : bombs) {
                if (bomb.isActive()) {
                    renderBomb(bomb);
                }
            }
        }
        
        // Dessiner les ennemis vivants (par-dessus la porte)
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    renderEnemy(enemy);
                }
            }
        }
        
        // ✨ **MODE COOPÉRATION** : Dessiner les deux joueurs avec leurs animateurs dédiés
        if (player1 != null) {
            renderPlayerCooperation(player1, bombermanAnimator, true, exitDoor);
        }
        if (player2 != null) {
            renderPlayerCooperation(player2, bombermanAnimator2, false, exitDoor);
        }
        
        // Dessiner l'interface utilisateur MODE COOPÉRATION par-dessus tout
        renderUICooperation(player1, player2, highScore, currentLevel, globalTimeRemaining);
    }
    
    /**
     * ✨ **MODE COOPÉRATION** : Interface utilisateur avec affichage des deux joueurs
     * @param player1 Le joueur 1
     * @param player2 Le joueur 2 (peut être null)
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     * @param globalTimeRemaining Temps restant du timer global en millisecondes
     */
    private void renderUICooperation(Player player1, Player player2, int highScore, int currentLevel, long globalTimeRemaining) {
        // Nettoyer les notifications expirées
        cleanExpiredNotifications();
        
        // Configurer la police pour l'UI
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE));
        gc.setFill(UI_TEXT_COLOR);
        
        // === LIGNE 1 (HAUT) : LEVEL ET HIGHSCORE ===
        int topUiY = ATH_HEIGHT / 2 + UI_FONT_SIZE / 2;
        double canvasWidth = canvas.getWidth(); // 720px
        
        gc.setTextAlign(TextAlignment.LEFT);
        String levelText = "LEVEL : " + currentLevel;
        gc.fillText(levelText, 30, topUiY);
        
        gc.setTextAlign(TextAlignment.RIGHT);
        String highScoreText = "HIGHSCORE : " + highScore;
        gc.fillText(highScoreText, canvasWidth - 30, topUiY);
        
        // === SCORES DES JOUEURS AU CENTRE ===
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Calculer le score total
        int totalScore = 0;
        if (player1 != null) totalScore += player1.getScore();
        if (player2 != null) totalScore += player2.getScore();
        
        String scoreText = "SCORE TOTAL : " + totalScore;
        gc.fillText(scoreText, canvasWidth / 2.0, topUiY);
        
        // ⏱️ Dessiner la barre de timer global
        int timerY = ATH_HEIGHT + (TIMER_ZONE_HEIGHT / 2) - 4;
        renderGlobalTimerBar(globalTimeRemaining, timerY);
        
        // === ZONE DÉDIÉE EN BAS : INFOS DES DEUX JOUEURS ===
        renderDedicatedUIAreaCooperation(player1, player2);
    }
    
    /**
     * ✨ **MODE COOPÉRATION** : Zone dédiée pour les infos des deux joueurs
     * @param player1 Le joueur 1
     * @param player2 Le joueur 2 (peut être null)
     */
    private void renderDedicatedUIAreaCooperation(Player player1, Player player2) {
        double canvasWidth = canvas.getWidth();
        int uiStartY = GAME_AREA_HEIGHT + TOTAL_HEADER_HEIGHT + 20; // Position de base avec marge
        
        // Colonnes pour les deux joueurs
        double player1X = canvasWidth * 0.25; // 25% de la largeur
        double player2X = canvasWidth * 0.75; // 75% de la largeur
        
        // === JOUEUR 1 ===
        if (player1 != null) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setFill(Color.CYAN);
            gc.fillText("JOUEUR 1", player1X, uiStartY);
            
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(UI_TEXT_COLOR);
            gc.fillText("Score: " + player1.getScore(), player1X, uiStartY + 25);
            gc.fillText("Bombes: " + player1.getCurrentBombs() + "/" + player1.getMaxBombs(), player1X, uiStartY + 45);
            gc.fillText("Portée: " + player1.getRange(), player1X, uiStartY + 65);
            
            // Statut vivant/mort
            if (player1.isAlive()) {
                gc.setFill(Color.GREEN);
                gc.fillText("VIVANT", player1X, uiStartY + 85);
            } else {
                gc.setFill(Color.RED);
                gc.fillText("MORT", player1X, uiStartY + 85);
            }
        }
        
        // === JOUEUR 2 ===
        if (player2 != null) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setFill(Color.YELLOW);
            gc.fillText("JOUEUR 2", player2X, uiStartY);
            
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(UI_TEXT_COLOR);
            gc.fillText("Score: " + player2.getScore(), player2X, uiStartY + 25);
            gc.fillText("Bombes: " + player2.getCurrentBombs() + "/" + player2.getMaxBombs(), player2X, uiStartY + 45);
            gc.fillText("Portée: " + player2.getRange(), player2X, uiStartY + 65);
            
            // Statut vivant/mort
            if (player2.isAlive()) {
                gc.setFill(Color.GREEN);
                gc.fillText("VIVANT", player2X, uiStartY + 85);
            } else {
                gc.setFill(Color.RED);
                gc.fillText("MORT", player2X, uiStartY + 85);
            }
        }
        
        // Afficher les notifications au centre
        gc.setTextAlign(TextAlignment.CENTER);
        renderNotificationsInDedicatedArea(uiStartY + 120);
        
        // Reset alignment
        gc.setTextAlign(TextAlignment.LEFT);
    }
    
    /**
     * ⏱️ Dessine l'interface utilisateur avec timer global
     * @param player Le joueur
     * @param highScore Le meilleur score
     * @param currentLevel Le niveau actuel
     * @param globalTimeRemaining Temps restant du timer global en millisecondes
     */
    private void renderUIWithTimer(Player player, int highScore, int currentLevel, long globalTimeRemaining) {
        // Nettoyer les notifications expirées
        cleanExpiredNotifications();
        
        // Configurer la police pour l'UI
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE));
        gc.setFill(UI_TEXT_COLOR);
        
        // === LIGNE 1 (HAUT) : LEVEL, SCORE, HIGHSCORE DANS SA ZONE DÉDIÉE ===
        // Position verticale centrée dans la zone ATH (50px)
        int topUiY = ATH_HEIGHT / 2 + UI_FONT_SIZE / 2;
        double canvasWidth = canvas.getWidth(); // 720px
        
        gc.setTextAlign(TextAlignment.LEFT); // Alignement à gauche pour LEVEL
        
        // Répartition optimisée sur toute la largeur avec marges appropriées
        double levelX = 30;                           // 30px du bord gauche (plus tôt)
        double scoreX = canvasWidth / 2.0;            // 360px - centre parfait (inchangé)
        double highScoreX = canvasWidth - 30;         // 690px - 30px du bord droit (plus loin)
        
        // Afficher le niveau (commence plus tôt)
        String levelText = "LEVEL : " + currentLevel;
        gc.fillText(levelText, levelX, topUiY);
        
        // Afficher le score actuel (centré)
        gc.setTextAlign(TextAlignment.CENTER);
        String scoreText = "SCORE : " + player.getScore();
        gc.fillText(scoreText, scoreX, topUiY);
        
        // Afficher le high score (aligné à droite, plus loin du bord)
        gc.setTextAlign(TextAlignment.RIGHT);
        String highScoreText = "HIGHSCORE : " + highScore;
        gc.fillText(highScoreText, highScoreX, topUiY);
        
        // ⏱️ Dessiner la barre de timer global entre l'ATH et la grille
        // Position dans la zone timer (50px à 100px) avec marges de 10px
        int timerY = ATH_HEIGHT + (TIMER_ZONE_HEIGHT / 2) - 4; // Centré dans la zone timer
        renderGlobalTimerBar(globalTimeRemaining, timerY);
        
        // === ZONE DÉDIÉE EN BAS : TOUT LE RESTE ===
        renderDedicatedUIArea(player);
    }
    
    /**
     * ⏱️ Dessine la barre de timer global avec nouveau design
     * @param globalTimeRemaining Temps restant en millisecondes
     * @param yPosition Position Y de la barre
     */
    private void renderGlobalTimerBar(long globalTimeRemaining, int yPosition) {
        double canvasWidth = canvas.getWidth(); // 720px
        
        // Configuration de la barre : 15 segments de 10 secondes chacun = 150 secondes
        int totalSegments = 15;
        long segmentDuration = 10000; // 10 secondes par segment en millisecondes
        
        // Nouvelles dimensions de la barre (redesign avec espacement amélioré)
        double barWidth = 500; // 500px de large (plus d'espace respiratoire)
        double barHeight = 10; // Épaisseur ajustée : 10px pour meilleure visibilité
        double segmentWidth = barWidth / totalSegments; // ~33px par segment
        
        // Position centrée
        double barX = (canvasWidth - barWidth) / 2.0; // 60px de marge de chaque côté
        
        // Calculer le nombre de segments restants
        int remainingSegments = (int) Math.ceil((double) globalTimeRemaining / segmentDuration);
        remainingSegments = Math.max(0, Math.min(totalSegments, remainingSegments));
        
        // Dessiner les segments vides (fond noir)
        gc.setFill(Color.BLACK);
        for (int i = 0; i < totalSegments; i++) {
            double segmentX = barX + (i * segmentWidth);
            gc.fillRect(segmentX, yPosition, segmentWidth - 1, barHeight); // -1 pour l'espacement
        }
        
        // Dessiner les segments remplis (blanc)
        gc.setFill(Color.WHITE);
        for (int i = 0; i < remainingSegments; i++) {
            double segmentX = barX + (i * segmentWidth);
            gc.fillRect(segmentX, yPosition, segmentWidth - 1, barHeight); // -1 pour l'espacement
        }
        
        // Dessiner le contour de la barre entière
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(barX - 1, yPosition - 1, barWidth + 2, barHeight + 2);
        
        // Afficher le temps restant au centre de la barre avec espacement optimal
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        
        long totalSeconds = globalTimeRemaining / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String timeText = String.format("⏰ %d:%02d", minutes, seconds);
        
        // Positionner le texte au centre de la barre avec ajustement vertical
        gc.fillText(timeText, canvasWidth / 2.0, yPosition + barHeight / 2 + 5);
        
        // Reset
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setLineWidth(1);
    }
    
    /**
     * ✨ **NOUVEAU** : Définit le callback à exécuter à la fin de l'animation de mort
     * @param callback Le code à exécuter
     */
    public void setDeathAnimationCallback(Runnable callback) {
        if (callback != null) {
            deathAnimationCallbacks.add(callback);
            System.out.println("📋 Callback de mort ajouté. Total en file: " + deathAnimationCallbacks.size());
        }
        // ✨ **CORRIGÉ** : Ne plus écraser onDeathAnimationCompleteCallback 
        // pour éviter de perdre les callbacks lors de morts quasi-simultanées
        // this.onDeathAnimationCompleteCallback = callback;
    }
    
    /**
     * ✨ **NOUVEAU** : Définit le callback à exécuter à la fin de l'animation de victoire
     * @param callback Le code à exécuter
     */
    public void setWinAnimationCallback(Runnable callback) {
        this.onWinAnimationCompleteCallback = callback;
    }
    
    /**
     * ✨ **NOUVEAU** : Rendu du joueur en animation de victoire
     * @param player Le joueur gagnant
     * @param horizontalOffset Décalage horizontal
     * @param exitDoor La porte de sortie où doit se jouer l'animation
     */
    private void renderWinningPlayer(Player player, double horizontalOffset, ExitDoor exitDoor) {
        // Mettre à jour la position de l'animateur à la position de la porte de sortie
        if (exitDoor != null) {
            bombermanAnimator.setPosition(
                exitDoor.getX(), 
                exitDoor.getY(), 
                horizontalOffset, 
                GRID_VERTICAL_OFFSET
            );
        } else {
            // Fallback : utiliser la position actuelle du joueur si pas de porte
            if (player instanceof FluidMovementPlayer) {
                FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
                bombermanAnimator.setPixelPosition(
                    fluidPlayer.getPixelX(),
                    fluidPlayer.getPixelY(),
                    horizontalOffset,
                    GRID_VERTICAL_OFFSET
                );
            } else {
                bombermanAnimator.setPosition(
                    player.getX(), 
                    player.getY(), 
                    horizontalOffset, 
                    GRID_VERTICAL_OFFSET
                );
            }
        }
        
        // Dessiner uniquement l'animation de victoire (sans effets de joueur vivant)
        bombermanAnimator.renderWithEffects(gc, false, 1.0);
    }

    /**
     * ✨ **NOUVEAU** : Dessine un joueur spécifique en mode coopération avec son animateur dédié
     * @param player Le joueur à dessiner (1 ou 2)
     * @param animator L'animateur correspondant à ce joueur
     * @param isPlayer1 true si c'est le joueur 1, false si c'est le joueur 2
     * @param exitDoor La porte de sortie
     */
    private void renderPlayerCooperation(Player player, BombermanAnimator animator, boolean isPlayer1, ExitDoor exitDoor) {
        // Calculer les décalages pour centrer dans la fenêtre
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;

        // CAS 1 : Le joueur est dans sa séquence de mort.
        if (player.isDying()) {
            // Si l'animateur n'est pas déjà en train de jouer l'animation de mort, on la démarre.
            if (!animator.isDead()) {
                animator.startDeathAnimation(() -> {
                    System.out.println("💀 Animation de mort terminée pour le " + (isPlayer1 ? "Joueur 1" : "Joueur 2") + " (callback GridRenderer)");
                    // ✨ **CORRIGÉ** : Utiliser la queue des callbacks pour éviter les pertes lors de morts simultanées
                    runNextDeathCallback();
                });
            }

            // Pendant toute la durée de la mort, on affiche l'animation correspondante.
            renderDeadPlayerCooperation(player, animator, horizontalOffset);
            return;
        }
        
        // CAS 1.5 : Le joueur est dans sa séquence de victoire.
        if (player.isWinning()) {
            // Si l'animateur n'est pas déjà en train de jouer l'animation de victoire, on la démarre.
            if (!animator.isWinning()) {
                animator.startWinAnimation(() -> {
                    System.out.println("🎉 Animation de victoire terminée pour le " + (isPlayer1 ? "Joueur 1" : "Joueur 2") + " (callback GridRenderer)");
                    if (onWinAnimationCompleteCallback != null) {
                        onWinAnimationCompleteCallback.run();
                    }
                });
            }

            // Pendant toute la durée de la victoire, on affiche l'animation correspondante.
            renderWinningPlayerCooperation(player, animator, horizontalOffset, exitDoor);
            return;
        }

        // CAS 2 : Le joueur est vivant.
        // Il se peut que l'animateur soit encore dans l'état "mort" (juste après un respawn).
        if (animator.isDead()) {
            animator.revive();
        }

        // --- Logique de rendu normale pour un joueur vivant ---

        // Mettre à jour la direction de l'animateur
        animator.setDirection(player.getCurrentDirection());
        
        // Gérer l'animation de marche selon l'état du joueur
        if (player.isWalking() && !animator.isWalking()) {
            animator.startWalking();
        } else if (!player.isWalking() && animator.isWalking()) {
            animator.stopWalking();
        }
        
        // Mettre à jour la position de l'animateur
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            animator.setPixelPosition(
                fluidPlayer.getPixelX(),
                fluidPlayer.getPixelY(),
                horizontalOffset,
                GRID_VERTICAL_OFFSET
            );
        } else {
            animator.setPosition(
                player.getX(), 
                player.getY(), 
                horizontalOffset, 
                GRID_VERTICAL_OFFSET
            );
        }
        
        // Calculer la position pour les effets visuels
        int effectX, effectY;
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            effectX = (int) (fluidPlayer.getRenderX() + horizontalOffset);
            effectY = (int) (fluidPlayer.getRenderY() + GRID_VERTICAL_OFFSET);
        } else {
            effectX = (int) (player.getX() * CELL_SIZE + PLAYER_OFFSET + horizontalOffset);
            effectY = player.getY() * CELL_SIZE + PLAYER_OFFSET + GRID_VERTICAL_OFFSET;
        }
        
        // Dessiner les effets et le joueur avec une couleur différente selon le joueur
        renderPlayerEffects(player, effectX, effectY);
        
        // Choisir l'opacité ou des effets visuels pour différencier les joueurs
        double playerOpacity = 1.0;
        if (!isPlayer1) {
            // Appliquer un léger effet visuel pour le joueur 2 (optionnel)
            // playerOpacity = 0.95; // Légèrement plus transparent
        }
        
        animator.renderWithEffects(gc, player.isInvincible(), playerOpacity);
        renderPlayerOverlayEffects(player, effectX, effectY);
    }

    /**
     * ✨ **NOUVEAU** : Dessine le joueur mort avec l'animation de mort (mode coopération)
     * @param player Le joueur mort
     * @param animator L'animateur correspondant à ce joueur
     * @param horizontalOffset Décalage horizontal pour centrage
     */
    private void renderDeadPlayerCooperation(Player player, BombermanAnimator animator, double horizontalOffset) {
        // Mettre à jour la position de l'animateur à la dernière position connue
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            animator.setPixelPosition(
                fluidPlayer.getPixelX(),
                fluidPlayer.getPixelY(),
                horizontalOffset,
                GRID_VERTICAL_OFFSET
            );
        } else {
            animator.setPosition(
                player.getX(), 
                player.getY(), 
                horizontalOffset, 
                GRID_VERTICAL_OFFSET
            );
        }
        
        // Dessiner uniquement l'animation de mort (sans effets de joueur vivant)
        animator.renderWithEffects(gc, false, 1.0);
    }

    /**
     * ✨ **NOUVEAU** : Dessine le joueur en train de gagner (mode coopération)
     * @param player Le joueur qui gagne
     * @param animator L'animateur correspondant à ce joueur
     * @param horizontalOffset Décalage horizontal pour centrage
     * @param exitDoor La porte de sortie
     */
    private void renderWinningPlayerCooperation(Player player, BombermanAnimator animator, double horizontalOffset, ExitDoor exitDoor) {
        // Mettre à jour la position de l'animateur
        if (player instanceof FluidMovementPlayer) {
            FluidMovementPlayer fluidPlayer = (FluidMovementPlayer) player;
            animator.setPixelPosition(
                fluidPlayer.getPixelX(),
                fluidPlayer.getPixelY(),
                horizontalOffset,
                GRID_VERTICAL_OFFSET
            );
        } else {
            animator.setPosition(
                player.getX(), 
                player.getY(), 
                horizontalOffset, 
                GRID_VERTICAL_OFFSET
            );
        }
        
        // Dessiner l'animation de victoire
        animator.renderWithEffects(gc, false, 1.0);
    }

    /**
     * ✨ **CORRIGÉ** : Récupère et exécute le prochain callback de mort, s'il existe.
     * Amélioration pour le debug des morts quasi-simultanées.
     */
    private void runNextDeathCallback() {
        Runnable cb = deathAnimationCallbacks.poll();
        if (cb != null) {
            System.out.println("🎯 Exécution du callback de mort. Callbacks restants: " + deathAnimationCallbacks.size());
            try {
            cb.run();
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'exécution du callback de mort: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Aucun callback de mort en attente dans la queue");
        }
    }

    /**
     * ✨ **MODE BATTLE** : Rendu complet avec deux joueurs en mode battle
     * @param player1 Le joueur 1
     * @param player2 Le joueur 2 (peut être null)
     * @param enemies Liste des ennemis vivants à afficher
     * @param bombs Liste des bombes actives à afficher
     * @param explosions Liste des explosions actives à afficher
     * @param powerUps Liste des power-ups visibles à afficher
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     * @param exitDoor La porte de sortie (peut être null)
     * @param globalTimeRemaining Temps restant du timer global en millisecondes
     */
    public void renderBattle(Player player1, Player player2, List<Enemy> enemies, List<Bomb> bombs, List<Explosion> explosions, List<PowerUp> powerUps, int highScore, int currentLevel, ExitDoor exitDoor, long globalTimeRemaining) {
        // Dessiner d'abord la grille
        render();
        
        // Dessiner les explosions en premier (sous les autres éléments)
        if (explosions != null) {
            for (Explosion explosion : explosions) {
                if (explosion.isActive()) {
                    renderExplosion(explosion);
                }
            }
        }
        
        // Dessiner la porte de sortie en deuxième (sous les bombes/ennemis/joueur)
        if (exitDoor != null && exitDoor.isVisible()) {
            renderExitDoor(exitDoor);
        }
        
        // Dessiner les power-ups visibles
        if (powerUps != null) {
            renderPowerUps(powerUps);
        }
        
        // Dessiner les bombes (par-dessus la porte)
        if (bombs != null) {
            for (Bomb bomb : bombs) {
                if (bomb.isActive()) {
                    renderBomb(bomb);
                }
            }
        }
        
        // Dessiner les ennemis vivants (par-dessus la porte)
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    renderEnemy(enemy);
                }
            }
        }
        
        // ✨ **MODE BATTLE** : Dessiner les deux joueurs avec leurs animateurs dédiés
        if (player1 != null) {
            renderPlayerCooperation(player1, bombermanAnimator, true, exitDoor);
        }
        if (player2 != null) {
            renderPlayerCooperation(player2, bombermanAnimator2, false, exitDoor);
        }
        
        // Dessiner l'interface utilisateur MODE BATTLE par-dessus tout
        renderUIBattle(player1, player2, highScore, currentLevel, globalTimeRemaining);
    }

    /**
     * ✨ **MODE BATTLE** : Interface utilisateur avec affichage des deux joueurs en mode battle
     * @param player1 Le joueur 1
     * @param player2 Le joueur 2 (peut être null)
     * @param highScore Le meilleur score enregistré
     * @param currentLevel Le niveau actuel
     * @param globalTimeRemaining Temps restant du timer global en millisecondes
     */
    private void renderUIBattle(Player player1, Player player2, int highScore, int currentLevel, long globalTimeRemaining) {
        // Nettoyer les notifications expirées
        cleanExpiredNotifications();
        
        // Configurer la police pour l'UI
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE));
        gc.setFill(UI_TEXT_COLOR);
        
        // === LIGNE 1 (HAUT) : LEVEL ET HIGHSCORE ===
        int topUiY = ATH_HEIGHT / 2 + UI_FONT_SIZE / 2;
        double canvasWidth = canvas.getWidth(); // 720px
        
        gc.setTextAlign(TextAlignment.LEFT);
        String levelText = "LEVEL : " + currentLevel;
        gc.fillText(levelText, 30, topUiY);
        
        gc.setTextAlign(TextAlignment.RIGHT);
        String highScoreText = "HIGHSCORE : " + highScore;
        gc.fillText(highScoreText, canvasWidth - 30, topUiY);
        
        // === MODE BATTLE AU CENTRE ===
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.RED);
        String battleText = "BATTLE MODE";
        gc.fillText(battleText, canvasWidth / 2.0, topUiY);
        gc.setFill(UI_TEXT_COLOR);
        
        // ⏱️ Dessiner la barre de timer global
        int timerY = ATH_HEIGHT + (TIMER_ZONE_HEIGHT / 2) - 4;
        renderGlobalTimerBar(globalTimeRemaining, timerY);
        
        // === ZONE DÉDIÉE EN BAS : INFOS DES DEUX JOUEURS EN MODE BATTLE ===
        renderDedicatedUIAreaBattle(player1, player2);
    }

    /**
     * ✨ **MODE BATTLE** : Zone dédiée pour les infos des deux joueurs en mode battle
     * @param player1 Le joueur 1
     * @param player2 Le joueur 2 (peut être null)
     */
    private void renderDedicatedUIAreaBattle(Player player1, Player player2) {
        double canvasWidth = canvas.getWidth();
        int uiStartY = GAME_AREA_HEIGHT + TOTAL_HEADER_HEIGHT + 20; // Position de base avec marge
        
        // Colonnes pour les deux joueurs
        double player1X = canvasWidth * 0.25; // 25% de la largeur
        double player2X = canvasWidth * 0.75; // 75% de la largeur
        
        // === JOUEUR 1 ===
        if (player1 != null) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setFill(Color.CYAN);
            gc.fillText("JOUEUR 1", player1X, uiStartY);
            
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(UI_TEXT_COLOR);
            gc.fillText("Score: " + player1.getScore(), player1X, uiStartY + 25);
            gc.fillText("Bombes: " + player1.getCurrentBombs() + "/" + player1.getMaxBombs(), player1X, uiStartY + 45);
            gc.fillText("Portée: " + player1.getRange(), player1X, uiStartY + 65);
            
            // Statut vivant/mort avec couleur différente pour le mode battle
            if (player1.isAlive()) {
                gc.setFill(Color.LIME);
                gc.fillText("EN VIE", player1X, uiStartY + 85);
            } else {
                gc.setFill(Color.DARKRED);
                gc.fillText("ÉLIMINÉ", player1X, uiStartY + 85);
            }
        }
        
        // === JOUEUR 2 ===
        if (player2 != null) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setFill(Color.YELLOW);
            gc.fillText("JOUEUR 2", player2X, uiStartY);
            
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(UI_TEXT_COLOR);
            gc.fillText("Score: " + player2.getScore(), player2X, uiStartY + 25);
            gc.fillText("Bombes: " + player2.getCurrentBombs() + "/" + player2.getMaxBombs(), player2X, uiStartY + 45);
            gc.fillText("Portée: " + player2.getRange(), player2X, uiStartY + 65);
            
            // Statut vivant/mort avec couleur différente pour le mode battle
            if (player2.isAlive()) {
                gc.setFill(Color.LIME);
                gc.fillText("EN VIE", player2X, uiStartY + 85);
            } else {
                gc.setFill(Color.DARKRED);
                gc.fillText("ÉLIMINÉ", player2X, uiStartY + 85);
            }
        }
        
        // Afficher les notifications au centre
        gc.setTextAlign(TextAlignment.CENTER);
        renderNotificationsInDedicatedArea(uiStartY + 120);
        
        // Reset alignment
        gc.setTextAlign(TextAlignment.LEFT);
    }
} 