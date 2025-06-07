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
public class GridRenderer {
    
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
    
    // Couleurs pour les power-ups permanents
    private static final Color EXTRA_BOMB_COLOR = Color.CYAN;             // Cyan pour EXTRA_BOMB
    private static final Color RANGE_UP_COLOR = Color.ORANGE;             // Orange pour RANGE_UP
    private static final Color SPEED_UP_COLOR = Color.LIGHTGREEN;         // Vert clair pour SPEED_UP
    
    // Couleurs pour les power-ups temporaires
    private static final Color SHIELD_COLOR = Color.DODGERBLUE;           // Bleu pour SHIELD
    private static final Color SPEED_BURST_COLOR = Color.YELLOW;          // Jaune pour SPEED_BURST
    private static final Color BOMB_RAIN_COLOR = Color.CRIMSON;           // Rouge foncé pour BOMB_RAIN
    
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
    private static final int GAME_AREA_HEIGHT = 528;                     // Hauteur de la grille seule (11 * 48 = 528px)
    private static final int UI_AREA_HEIGHT = 362;                        // était 282, maintenant 362 (+80px pour zone notifications élargie)
    
    // ⏱️ Paramètres d'agencement vertical amélioré
    private static final int ATH_HEIGHT = 50;                           // Espace pour l'ATH (LEVEL/SCORE/HIGHSCORE)
    private static final int TIMER_ZONE_HEIGHT = 50;                    // Zone dédiée au timer avec marges
    private static final int TOTAL_HEADER_HEIGHT = ATH_HEIGHT + TIMER_ZONE_HEIGHT; // 100px total pour header + timer
    private static final int GRID_VERTICAL_OFFSET = TOTAL_HEADER_HEIGHT; // Décalage de la grille vers le bas
    private static final int GRID_HORIZONTAL_OFFSET = 60;               // Décalage horizontal pour centrer la grille (840-720)/2 = 60px
    
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
    
    // ✨ **NOUVEAU** : Image de contours de la map (bordures)
    private static Image contoursMapImage;
    
    // 🧱 **NOUVEAU** : Image des blocs non destructibles (16x16 px)
    private static Image blocNonDestructibleImage;
    
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
        
        // ✨ **NOUVEAU** : Charger l'image de contours de map
        loadContoursMapImage();
        
        // 🧱 **NOUVEAU** : Charger l'image des blocs non destructibles
        loadBlocNonDestructibleImage();
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
     * ✨ **NOUVEAU** : Charge l'image de contours de map depuis les ressources
     */
    private static void loadContoursMapImage() {
        if (contoursMapImage == null) {
            try {
                String imagePath = "/sprites/contours_map.png";
                contoursMapImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image de contours de map chargée : " + imagePath + 
                                  " (dimensions: " + (int)contoursMapImage.getWidth() + "x" + (int)contoursMapImage.getHeight() + ")");
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image de contours de map : " + e.getMessage());
                e.printStackTrace();
                contoursMapImage = null;
            }
        }
    }
    
    /**
     * 🧱 **NOUVEAU** : Charge l'image des blocs non destructibles depuis les ressources
     */
    private static void loadBlocNonDestructibleImage() {
        if (blocNonDestructibleImage == null) {
            try {
                String imagePath = "/sprites/bloc_non_destructible.png";
                blocNonDestructibleImage = new Image(GridRenderer.class.getResourceAsStream(imagePath));
                System.out.println("Image des blocs non destructibles chargée : " + imagePath + 
                                  " (dimensions: " + (int)blocNonDestructibleImage.getWidth() + "x" + (int)blocNonDestructibleImage.getHeight() + ")");
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image des blocs non destructibles : " + e.getMessage());
                e.printStackTrace();
                blocNonDestructibleImage = null;
            }
        }
    }
    
    /**
     * Méthode principale de rendu.
     * Dessine l'intégralité de la grille sur le canvas (dans la zone de jeu uniquement).
     */
    public void render() {
        // Effacer TOUT le canvas pour éviter les doublons d'ATH
        gc.setFill(EMPTY_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // ✨ **NOUVEAU** : Dessiner l'image de contours de map en arrière-plan (si chargée)
        renderContoursMapBackground();
        
        // Parcourir toute la grille et dessiner chaque cellule
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                renderCell(col, row);
            }
        }
    }
    
        /**
     * ✨ **NOUVEAU** : Dessine l'image de contours de map en arrière-plan
     * ADAPTÉ À LA GRILLE ACTUELLE : Redimensionne le sprite 272×176 vers la taille de la grille 720×528
     */
    private void renderContoursMapBackground() {
        if (contoursMapImage != null) {
            // 🎯 CORRECTION PROPORTIONS : Respect du ratio original du sprite
            // - Sprite original : 272×176 px conçu pour grille 13×11 
            // - Grille actuelle : 15×11 tuiles de 48×48 px = 720×528 px
            // - PROBLÈME : 13 colonnes → 15 colonnes = débordement sur les bords
            // - SOLUTION : Ajuster les dimensions pour centrer et respecter les proportions
            
            // Calculer les dimensions proportionnelles basées sur le sprite original
            double originalSpriteWidth = 272;
            double originalSpriteHeight = 176;
            double originalGameColumns = 13;  // Le sprite était conçu pour 13 colonnes
            double currentGameColumns = grid.getColumns();  // 15 colonnes actuelles
            
                         // Calculer le ratio de mise à l'échelle pour préserver les proportions
             // ✨ Augmentation légère du facteur d'échelle pour couvrir mieux la zone de jeu
             double scaleRatio = (currentGameColumns * CELL_SIZE) / originalSpriteWidth;
             double enhancedScaleRatio = scaleRatio * 1.15;  // Augmentation de 15% pour meilleure couverture
             double scaledSpriteWidth = originalSpriteWidth * enhancedScaleRatio;    // ~828px
             double scaledSpriteHeight = originalSpriteHeight * enhancedScaleRatio;  // ~546px
            
                         // Centrer le sprite sur la zone de jeu élargie pour éviter les débordements
             double gameAreaWidth = grid.getColumns() * CELL_SIZE;   // 720px (zone de jeu)
             double gameAreaHeight = grid.getRows() * CELL_SIZE;     // 528px
             double canvasWidth = canvas.getWidth();                 // 780px (nouvelle largeur de canvas)
             
             // Centrer horizontalement dans toute la largeur du canvas (pas seulement la grille)
             double spriteX = (canvasWidth - scaledSpriteWidth) / 2;         // Centrer dans toute la largeur (780px)
             double spriteY = GRID_VERTICAL_OFFSET + (gameAreaHeight - scaledSpriteHeight) / 2;  // Centrer verticalement
            
            gc.drawImage(contoursMapImage, 
                        spriteX, spriteY,                           // Position centrée
                        scaledSpriteWidth, scaledSpriteHeight       // Dimensions proportionnelles
            );
            
                         // 📊 Debug désactivé (redimensionnement 272×176 → 720×528 px fonctionnel)
             // System.out.println("🖼️ Sprite contours adapté : " + (int)contoursMapImage.getWidth() + "×" + (int)contoursMapImage.getHeight() + 
             //                   " → " + (int)gameAreaWidth + "×" + (int)gameAreaHeight + " px (grille " + grid.getColumns() + "×" + grid.getRows() + ")");
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
        
        // Dessiner le joueur en dernier (par-dessus tout, seulement s'il est vivant)
        if (player.isAlive()) {
            renderPlayer(player);
        }
        
        // Dessiner l'overlay de mort si le joueur est mort
        if (!player.isAlive()) {
            renderDeathOverlay();
        }
        
        // Note: L'interface utilisateur est maintenant gérée par la méthode render avec timer
        // Cette méthode ne dessine que les éléments de jeu, pas l'UI
        
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
        // Calculer la position en pixels avec décalages horizontal et vertical
        int x = column * CELL_SIZE + GRID_HORIZONTAL_OFFSET;  // Centrer horizontalement dans la fenêtre 780px
        int y = row * CELL_SIZE + GRID_VERTICAL_OFFSET;
        
        // Déterminer la couleur selon le type de cellule
        TileType tileType = grid.getTileType(column, row);
        
        // ✨ **NOUVEAU** : Gestion intelligente des blocs SOLID selon leur position
        switch (tileType) {
            case SOLID:
                // Différencier les bordures des blocs intérieurs
                if (isBorderCell(column, row)) {
                    // Bordures : ne pas dessiner si le sprite de contours est présent
                    if (contoursMapImage == null) {
                        gc.setFill(SOLID_COLOR);
                        gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    // Blocs intérieurs : utiliser le sprite des blocs non destructibles
                    renderIndestructibleBlock(x, y);
                }
                break;
            case DESTRUCTIBLE:
                gc.setFill(DESTRUCTIBLE_COLOR);
                gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                break;
            case EMPTY:
            default:
                // Ne rien dessiner pour les cellules vides : laisse l'image de contours visible
                break;
        }
    }
    
    /**
     * Dessine le joueur à sa position actuelle avec effets visuels
     * @param player Le joueur à dessiner
     */
    private void renderPlayer(Player player) {
        // Si le joueur est invincible, effet de clignotement ultra rapide
        if (player.isInvincible()) {
            // Clignotement ultra rapide (15 clignotements par seconde)
            long currentTime = System.currentTimeMillis();
            boolean shouldRender = (currentTime / 33) % 2 == 0; // Alterne toutes les 33ms
            if (!shouldRender) {
                return; // Ne pas dessiner le joueur (effet de clignotement)
            }
        }
        
        // Calculer la position en pixels avec décalages horizontal et vertical
        int x = player.getX() * CELL_SIZE + PLAYER_OFFSET + GRID_HORIZONTAL_OFFSET;
        int y = player.getY() * CELL_SIZE + PLAYER_OFFSET + GRID_VERTICAL_OFFSET;
        
        // Dessiner les effets de fond (auras, glows) avant le joueur
        renderPlayerEffects(player, x, y);
        
        // Couleur du joueur selon les effets actifs
        Color playerColor = getPlayerColor(player);
        
        // Dessiner le joueur principal
        gc.setFill(playerColor);
        gc.fillRect(x, y, PLAYER_SIZE, PLAYER_SIZE);
        
        // Dessiner les effets de premier plan après le joueur
        renderPlayerOverlayEffects(player, x, y);
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
     * Dessine une bombe à sa position
     * @param bomb La bombe à dessiner
     */
    private void renderBomb(Bomb bomb) {
        // Calculer la position en pixels avec décalages horizontal et vertical
        int x = bomb.getX() * CELL_SIZE + BOMB_OFFSET + GRID_HORIZONTAL_OFFSET;
        int y = bomb.getY() * CELL_SIZE + BOMB_OFFSET + GRID_VERTICAL_OFFSET;
        
        // Dessiner la bombe
        gc.setFill(BOMB_COLOR);
        gc.fillRect(x, y, BOMB_SIZE, BOMB_SIZE);
    }
    
    /**
     * Dessine une explosion (flammes sur toutes les cases affectées)
     * @param explosion L'explosion à dessiner
     */
    private void renderExplosion(Explosion explosion) {
        gc.setFill(EXPLOSION_COLOR);
        
        // Dessiner chaque case affectée par l'explosion avec décalages horizontal et vertical
        for (Explosion.ExplosionCell cell : explosion.getAffectedCells()) {
            int x = cell.getX() * CELL_SIZE + GRID_HORIZONTAL_OFFSET;
            int y = cell.getY() * CELL_SIZE + GRID_VERTICAL_OFFSET;
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
    }
    
    /**
     * Dessine un ennemi à sa position actuelle avec effet visuel d'invincibilité
     * @param enemy L'ennemi à dessiner
     */
    private void renderEnemy(Enemy enemy) {
        // Calculer la position en pixels avec décalages horizontal et vertical
        int x = enemy.getX() * CELL_SIZE + ENEMY_OFFSET + GRID_HORIZONTAL_OFFSET;
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
     * 🧱 **NOUVEAU** : Vérifie si une cellule est sur les bordures de la grille
     * @param column Position en colonne (x)
     * @param row Position en ligne (y)
     * @return true si la cellule est sur une bordure
     */
    private boolean isBorderCell(int column, int row) {
        return row == 0 || row == grid.getRows() - 1 || column == 0 || column == grid.getColumns() - 1;
    }
    
    /**
     * 🧱 **NOUVEAU** : Dessine un bloc non destructible avec le sprite
     * @param x Position X en pixels
     * @param y Position Y en pixels
     */
    private void renderIndestructibleBlock(int x, int y) {
        if (blocNonDestructibleImage != null) {
            // Redimensionner le sprite 16x16 vers la taille d'une cellule (48x48)
            gc.drawImage(blocNonDestructibleImage, x, y, CELL_SIZE, CELL_SIZE);
        } else {
            // Fallback : dessiner un bloc gris si le sprite ne se charge pas
            gc.setFill(SOLID_COLOR);
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
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
        double shieldX = leftEdge;                // 60px - aligné avec VIES
        double speedBurstX = 200;                 // 200px - plus d'espace pour SHIELD
        double vitesseX = 380;                    // 380px - plus d'espace pour SPEED BURST
        double porteeX = rightEdge;               // 660px - aligné avec BOMBES
        
        // Colonne 1 : Shield (aligné à gauche comme VIES)
        gc.setTextAlign(TextAlignment.LEFT);
        if (player.hasShield()) {
            gc.setFill(SHIELD_COLOR);
            gc.fillText("🛡️ SHIELD", shieldX, yPosition);
        } else {
            gc.setFill(Color.web("#666666"));
            gc.fillText("🛡️ -----", shieldX, yPosition);
        }
        
        // Colonne 2 : Speed Burst (centré)
        gc.setTextAlign(TextAlignment.CENTER);
        if (player.hasSpeedBurst()) {
            gc.setFill(SPEED_BURST_COLOR);
            gc.fillText("⚡ SPEED BURST", speedBurstX, yPosition);
        } else {
            gc.setFill(Color.web("#666666"));
            gc.fillText("⚡ -----", speedBurstX, yPosition);
        }
        
        // Colonne 3 : Vitesse (centré, plus d'espace avant PORTÉE)
        gc.setFill(SPEED_UP_COLOR);
        if (player.hasSpeedBurst()) {
            gc.fillText("→ VITESSE: MAX", vitesseX, yPosition);
        } else if (player.getSpeed() > 1.0) {
            gc.fillText("→ VITESSE: " + String.format("%.1f", player.getSpeed()), vitesseX, yPosition);
        } else {
            gc.fillText("→ VITESSE: 1.0", vitesseX, yPosition);
        }
        
        // Colonne 4 : Portée (aligné à droite comme BOMBES)
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFill(RANGE_UP_COLOR);
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
        // Calculer la position en pixels avec décalage vertical
        int x = powerUp.getX() * CELL_SIZE + POWER_UP_OFFSET + GRID_HORIZONTAL_OFFSET;
        int y = powerUp.getY() * CELL_SIZE + POWER_UP_OFFSET + GRID_VERTICAL_OFFSET;
        
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
     * Détermine la couleur d'affichage selon le type de power-up
     * @param type Type du power-up
     * @return Couleur correspondante
     */
    private Color getPowerUpColor(PowerUpType type) {
        switch (type) {
            case EXTRA_BOMB:
                return EXTRA_BOMB_COLOR;
            case RANGE_UP:
                return RANGE_UP_COLOR;
            case SPEED_UP:
                return SPEED_UP_COLOR;
            case SHIELD:
                return SHIELD_COLOR;
            case SPEED_BURST:
                return SPEED_BURST_COLOR;
            case BOMB_RAIN:
                return BOMB_RAIN_COLOR;
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
     * Dessine la porte de sortie à sa position
     * @param exitDoor La porte de sortie à dessiner
     */
    private void renderExitDoor(ExitDoor exitDoor) {
        // Calculer la position en pixels avec décalage vertical
        int x = exitDoor.getX() * CELL_SIZE + POWER_UP_OFFSET + GRID_HORIZONTAL_OFFSET;
        int y = exitDoor.getY() * CELL_SIZE + POWER_UP_OFFSET + GRID_VERTICAL_OFFSET;
        
        // Effet pulsatoire plus prononcé si la porte est activée
        long currentTime = System.currentTimeMillis();
        double pulseFrequency = exitDoor.isActivated() ? 200.0 : 500.0; // Plus rapide si activée
        double pulseAmplitude = exitDoor.isActivated() ? 0.1 : 0.05; // Plus forte si activée
        double pulseScale = 1.0 + pulseAmplitude * Math.sin(currentTime / pulseFrequency);
        
        // Fond plus clair pour effet de brillance (uniquement si activée)
        if (exitDoor.isActivated()) {
            gc.setFill(Color.web("#FFFACD")); // Jaune très clair
            double glowSize = POWER_UP_SIZE * pulseScale * 1.2;
            double glowOffset = (CELL_SIZE - glowSize) / 2;
            gc.fillRect(exitDoor.getX() * CELL_SIZE + glowOffset, 
                       exitDoor.getY() * CELL_SIZE + glowOffset + GRID_VERTICAL_OFFSET, 
                       glowSize, glowSize);
        }
        
        // Porte elle-même (couleur différente selon l'état)
        gc.setFill(exitDoor.isActivated() ? EXIT_DOOR_COLOR : EXIT_DOOR_INACTIVE_COLOR);
        double doorSize = POWER_UP_SIZE * pulseScale;
        double doorOffset = (CELL_SIZE - doorSize) / 2;
        gc.fillRect(exitDoor.getX() * CELL_SIZE + doorOffset,
                   exitDoor.getY() * CELL_SIZE + doorOffset + GRID_VERTICAL_OFFSET,
                   doorSize, doorSize);
        
        // Dessiner le contour de porte
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(exitDoor.getX() * CELL_SIZE + doorOffset,
                     exitDoor.getY() * CELL_SIZE + doorOffset + GRID_VERTICAL_OFFSET,
                     doorSize, doorSize);
        
        // Dessiner le symbole de porte (poignée)
        gc.setFill(Color.BLACK);
        double handleSize = doorSize / 5;
        double handleX = exitDoor.getX() * CELL_SIZE + doorOffset + doorSize * 0.7;
        double handleY = exitDoor.getY() * CELL_SIZE + doorOffset + doorSize / 2 - handleSize / 2 + GRID_VERTICAL_OFFSET;
        gc.fillOval(handleX, handleY, handleSize, handleSize);
        
        // Texte "EXIT" sur la porte activée
        if (exitDoor.isActivated()) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            gc.fillText("EXIT", 
                      exitDoor.getX() * CELL_SIZE + doorOffset + 5, 
                      exitDoor.getY() * CELL_SIZE + doorOffset + doorSize / 2 + 3 + GRID_VERTICAL_OFFSET);
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
        
        // Dessiner le joueur en dernier (par-dessus tout, seulement s'il est vivant)
        if (player.isAlive()) {
            renderPlayer(player);
        }
        
        // Dessiner l'overlay de mort si le joueur est mort
        if (!player.isAlive()) {
            renderDeathOverlay();
        }
        
        // Dessiner l'interface utilisateur par-dessus tout (avec high score, niveau et timer)
        renderUIWithTimer(player, highScore, currentLevel, globalTimeRemaining);
        
        // Note: Le message GAME OVER est géré par renderGameOverScreen() appelé depuis Launcher
        // Pas de double appel ici pour éviter les doublons
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
} 