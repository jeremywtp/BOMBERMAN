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
    private static final int GAME_AREA_HEIGHT = 528;                     // était 352, maintenant 528
    private static final int UI_AREA_HEIGHT = 252;                        // était 168, maintenant 252
    
    // Zone de notifications temporaires
    private static final int MAX_NOTIFICATIONS = 5; // Augmenté pour profiter de l'espace supplémentaire
    private List<String> recentNotifications = new ArrayList<>();
    private List<Long> notificationTimestamps = new ArrayList<>();
    private static final long NOTIFICATION_DURATION = 3000; // 3 secondes
    
    private final Canvas canvas;
    private final Grid grid;
    private final GraphicsContext gc;
    
    // Image d'intro pour l'écran de démarrage
    private static Image introImage;
    
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
     * Méthode principale de rendu.
     * Dessine l'intégralité de la grille sur le canvas (dans la zone de jeu uniquement).
     */
    public void render() {
        // Effacer uniquement la zone de jeu (pas la zone d'interface)
        gc.setFill(EMPTY_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), GAME_AREA_HEIGHT);
        
        // Parcourir toute la grille et dessiner chaque cellule
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                renderCell(col, row);
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
     */
    public void render(Player player, List<Enemy> enemies, List<Bomb> bombs, List<Explosion> explosions, List<PowerUp> powerUps, int highScore, int currentLevel) {
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
        
        // Dessiner les power-ups visibles
        if (powerUps != null) {
            renderPowerUps(powerUps);
        }
        
        // Dessiner les bombes
        if (bombs != null) {
            for (Bomb bomb : bombs) {
                if (bomb.isActive()) {
                    renderBomb(bomb);
                }
            }
        }
        
        // Dessiner les ennemis vivants
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
        
        // Dessiner l'interface utilisateur par-dessus tout (avec high score et niveau)
        renderUI(player, highScore, currentLevel);
        
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
     * Dessine une cellule individuelle de la grille
     * @param column Position en colonne (x)
     * @param row Position en ligne (y)
     */
    private void renderCell(int column, int row) {
        // Calculer la position en pixels
        int x = column * CELL_SIZE;
        int y = row * CELL_SIZE;
        
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
     * Dessine le joueur à sa position actuelle avec effets visuels
     * @param player Le joueur à dessiner
     */
    private void renderPlayer(Player player) {
        // Si le joueur est invincible, effet de clignotement
        if (player.isInvincible()) {
            // Clignotement basé sur le temps (4 clignotements par seconde)
            long currentTime = System.currentTimeMillis();
            boolean shouldRender = (currentTime / 125) % 2 == 0; // Alterne toutes les 125ms
            if (!shouldRender) {
                return; // Ne pas dessiner le joueur (effet de clignotement)
            }
        }
        
        // Calculer la position en pixels
        int x = player.getX() * CELL_SIZE + PLAYER_OFFSET;
        int y = player.getY() * CELL_SIZE + PLAYER_OFFSET;
        
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
        // Calculer la position en pixels
        int x = bomb.getX() * CELL_SIZE + BOMB_OFFSET;
        int y = bomb.getY() * CELL_SIZE + BOMB_OFFSET;
        
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
        
        // Dessiner chaque case affectée par l'explosion
        for (Explosion.ExplosionCell cell : explosion.getAffectedCells()) {
            int x = cell.getX() * CELL_SIZE;
            int y = cell.getY() * CELL_SIZE;
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
    }
    
    /**
     * Dessine un ennemi à sa position actuelle
     * @param enemy L'ennemi à dessiner
     */
    private void renderEnemy(Enemy enemy) {
        // Calculer la position en pixels
        int x = enemy.getX() * CELL_SIZE + ENEMY_OFFSET;
        int y = enemy.getY() * CELL_SIZE + ENEMY_OFFSET;
        
        // Dessiner l'ennemi
        gc.setFill(ENEMY_COLOR);
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
        
        // === LIGNE 1 (HAUT) : LEVEL, SCORE, HIGHSCORE MIEUX RÉPARTIS ===
        int topUiY = UI_MARGIN + UI_FONT_SIZE;
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
        gc.fillRect(0, GAME_AREA_HEIGHT, canvas.getWidth(), UI_AREA_HEIGHT);
        
        // Position de départ de la zone d'interface (ajustée pour les nouvelles dimensions)
        int uiStartY = GAME_AREA_HEIGHT + 22; // était 15, maintenant 22 (proportionnel x1.5)
        
        // === LIGNE 1 DE LA ZONE UI : BOMBES (centré) ===
        renderBombsCounter(player, uiStartY + 30); // était +20, maintenant +30
        
        // === LIGNE 2 DE LA ZONE UI : INDICATEURS DE BONUS (4 colonnes fixes) ===
        renderBonusIndicatorsInDedicatedArea(player, uiStartY + 75); // était +50, maintenant +75
        
        // === LIGNES 3+ DE LA ZONE UI : NOTIFICATIONS EMPILÉES ===
        renderNotificationsInDedicatedArea(uiStartY + 120); // était +80, maintenant +120
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
        String lifeText = "❤️ VIES : " + player.getLives() + "/" + player.getMaxLives();
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
        if (player.getRange() > 2) {
            gc.fillText("○ PORTÉE: " + player.getRange(), porteeX, yPosition);
        } else {
            gc.fillText("○ PORTÉE: 2", porteeX, yPosition);
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
            // Afficher un message par défaut parfaitement centré
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, UI_FONT_SIZE - 6));
            gc.setFill(Color.web("#666666"));
            gc.setTextAlign(TextAlignment.CENTER);
            
            double canvasCenterX = canvas.getWidth() / 2.0; // 360px - centre parfait du canvas
            gc.fillText("Les notifications des power-ups apparaîtront ici...", canvasCenterX, yPosition);
            
            gc.setFill(UI_TEXT_COLOR);
            gc.setTextAlign(TextAlignment.LEFT);
            return;
        }
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, UI_FONT_SIZE - 4));
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
            
            // Position verticale (empiler vers le bas)
            int notificationY = yPosition + (i * (UI_FONT_SIZE - 1));
            
            // Centrer parfaitement chaque notification
            gc.fillText("→ " + notification, canvasCenterX, notificationY);
        }
        
        // Reset
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.LEFT);
    }
    
    /**
     * Dessine l'écran de menu de démarrage
     */
    public void renderStartMenu() {
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
            
            System.out.println("Image d'intro affichée - Échelle: " + scale + ", Position: (" + x + ", " + y + ")");
        }
        
        // Ajouter un overlay semi-transparent pour améliorer la lisibilité du texte
        gc.setFill(Color.web("#000000", 0.4));
        gc.fillRect(0, canvas.getHeight() - 150, canvas.getWidth(), 150);
        
        // Configurer la police pour les instructions
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 27));
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Calculer les positions centrales pour le texte en bas
        double canvasCenterX = canvas.getWidth() / 2.0;
        double textAreaY = canvas.getHeight() - 100; // Zone de texte en bas
        
        // Afficher les instructions
        gc.fillText("Appuyez sur ENTRÉE pour commencer", canvasCenterX, textAreaY);
        
        // Afficher les contrôles
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.fillText("Flèches : Déplacement | Espace : Poser une bombe", canvasCenterX, textAreaY + 35);
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
        // Calculer la position en pixels
        int x = powerUp.getX() * CELL_SIZE + POWER_UP_OFFSET;
        int y = powerUp.getY() * CELL_SIZE + POWER_UP_OFFSET;
        
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
} 