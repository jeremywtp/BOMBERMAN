package bomberman.bomberman;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
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
    
    // Taille d'une cellule en pixels
    private static final int CELL_SIZE = 32;
    
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
    
    // Taille du joueur (légèrement plus petit que la case pour un meilleur aspect visuel)
    private static final int PLAYER_SIZE = CELL_SIZE - 6;  // 26 pixels au lieu de 32
    private static final int PLAYER_OFFSET = 3;  // Décalage pour centrer le joueur dans la case
    
    // Taille de la bombe (légèrement plus petite que la case)
    private static final int BOMB_SIZE = CELL_SIZE - 4;  // 28 pixels au lieu de 32
    private static final int BOMB_OFFSET = 2;  // Décalage pour centrer la bombe dans la case
    
    // Taille des ennemis (même taille que le joueur pour la cohérence)
    private static final int ENEMY_SIZE = CELL_SIZE - 6;  // 26 pixels au lieu de 32
    private static final int ENEMY_OFFSET = 3;  // Décalage pour centrer l'ennemi dans la case
    
    // Taille des power-ups (même taille que le joueur pour la cohérence)
    private static final int POWER_UP_SIZE = CELL_SIZE - 6;              // 26 pixels au lieu de 32
    private static final int POWER_UP_OFFSET = 3;                        // Décalage pour centrer le power-up dans la case
    
    // Paramètres de l'interface utilisateur
    private static final int UI_MARGIN = 10;                             // Marge pour l'UI
    private static final int UI_FONT_SIZE = 16;                          // Taille de police pour l'UI
    private static final int GAME_OVER_FONT_SIZE = 48;                   // Taille de police pour GAME OVER
    private static final int GAME_AREA_HEIGHT = 352;                     // Hauteur de la zone de jeu (11*32)
    private static final int UI_AREA_HEIGHT = 168;                        // Hauteur de la zone d'interface en bas (encore plus grande)
    
    // Zone de notifications temporaires
    private static final int MAX_NOTIFICATIONS = 5; // Augmenté pour profiter de l'espace supplémentaire
    private List<String> recentNotifications = new ArrayList<>();
    private List<Long> notificationTimestamps = new ArrayList<>();
    private static final long NOTIFICATION_DURATION = 3000; // 3 secondes
    
    private final Canvas canvas;
    private final Grid grid;
    private final GraphicsContext gc;
    
    /**
     * Constructeur du renderer
     * @param canvas Le canvas JavaFX sur lequel dessiner
     * @param grid Le modèle de grille à afficher
     */
    public GridRenderer(Canvas canvas, Grid grid) {
        this.canvas = canvas;
        this.grid = grid;
        this.gc = canvas.getGraphicsContext2D();
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
     * Zone de jeu : 0-352px (grille + ligne du haut uniquement)
     * Zone d'interface : 352-480px (128px dédiés pour tout le reste)
     * 
     * Ligne 1 (haut) : LEVEL, SCORE, HIGHSCORE SEULEMENT
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
        
        // === LIGNE 1 (HAUT) : LEVEL, SCORE, HIGHSCORE SEULEMENT ===
        int topUiY = UI_MARGIN + UI_FONT_SIZE;
        double thirdWidth = canvas.getWidth() / 3; // 3 colonnes au lieu de 4
        
        // Afficher le niveau (colonne 1)
        gc.setTextAlign(TextAlignment.CENTER);
        String levelText = "LEVEL : " + currentLevel;
        gc.fillText(levelText, thirdWidth * 0.5, topUiY);
        
        // Afficher le score actuel (colonne 2)
        String scoreText = "SCORE : " + player.getScore();
        gc.fillText(scoreText, thirdWidth * 1.5, topUiY);
        
        // Afficher le high score (colonne 3)
        String highScoreText = "HIGHSCORE : " + highScore;
        gc.fillText(highScoreText, thirdWidth * 2.5, topUiY);
        
        // === ZONE DÉDIÉE EN BAS : TOUT LE RESTE ===
        renderDedicatedUIArea(player);
    }
    
    /**
     * Dessine la zone d'interface dédiée en bas (352-480px = 128px)
     * @param player Le joueur
     */
    private void renderDedicatedUIArea(Player player) {
        // Dessiner un fond légèrement différent pour la zone d'interface
        gc.setFill(Color.web("#111111")); // Fond sombre pour séparer visuellement
        gc.fillRect(0, GAME_AREA_HEIGHT, canvas.getWidth(), UI_AREA_HEIGHT);
        
        // Position de départ de la zone d'interface
        int uiStartY = GAME_AREA_HEIGHT + 15; // 15px de marge depuis le bas de la grille
        
        // === LIGNE 1 DE LA ZONE UI : BOMBES (centré) ===
        renderBombsCounter(player, uiStartY + 20);
        
        // === LIGNE 2 DE LA ZONE UI : INDICATEURS DE BONUS (4 colonnes fixes) ===
        renderBonusIndicatorsInDedicatedArea(player, uiStartY + 50);
        
        // === LIGNES 3+ DE LA ZONE UI : NOTIFICATIONS EMPILÉES ===
        renderNotificationsInDedicatedArea(uiStartY + 80);
    }
    
    /**
     * Dessine le compteur de bombes et vies dans la zone dédiée
     * @param player Le joueur
     * @param yPosition Position Y
     */
    private void renderBombsCounter(Player player, int yPosition) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE + 2)); // Police légèrement plus grande
        
        // Calculer les positions pour centrer les deux éléments
        double leftX = canvas.getWidth() * 0.25;  // 25% de la largeur
        double rightX = canvas.getWidth() * 0.75; // 75% de la largeur
        
        // Afficher les vies avec cœur rouge (à gauche)
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        String lifeText = "❤️ VIES : " + player.getLives() + "/" + player.getMaxLives();
        gc.fillText(lifeText, leftX, yPosition);
        
        // Afficher les bombes avec émoji bombe (à droite)
        gc.setFill(EXTRA_BOMB_COLOR); // Couleur cyan pour les bombes
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
        gc.setTextAlign(TextAlignment.CENTER); // Centrer tous les textes
        
        // Diviser l'espace en 4 colonnes strictement égales
        double columnWidth = canvas.getWidth() / 4.0;
        
        // Colonne 1 : Shield (position fixe)
        double col1X = columnWidth * 0.5; // Centre de la colonne 1
        if (player.hasShield()) {
            gc.setFill(SHIELD_COLOR);
            gc.fillText("🛡️ SHIELD", col1X, yPosition);
        } else {
            gc.setFill(Color.web("#666666")); // Gris quand inactif
            gc.fillText("🛡️ -----", col1X, yPosition);
        }
        
        // Colonne 2 : Speed Burst (position fixe)
        double col2X = columnWidth * 1.5; // Centre de la colonne 2
        if (player.hasSpeedBurst()) {
            gc.setFill(SPEED_BURST_COLOR);
            gc.fillText("⚡ SPEED BURST", col2X, yPosition);
        } else {
            gc.setFill(Color.web("#666666"));
            gc.fillText("⚡ -----", col2X, yPosition);
        }
        
        // Colonne 3 : Vitesse (position fixe, toujours affiché)
        double col3X = columnWidth * 2.5; // Centre de la colonne 3
        gc.setFill(SPEED_UP_COLOR);
        if (player.hasSpeedBurst()) {
            gc.fillText("→ VITESSE: MAX", col3X, yPosition);
        } else if (player.getSpeed() > 1.0) {
            gc.fillText("→ VITESSE: " + String.format("%.1f", player.getSpeed()), col3X, yPosition);
        } else {
            gc.fillText("→ VITESSE: 1.0", col3X, yPosition);
        }
        
        // Colonne 4 : Portée (position fixe, toujours affiché)
        double col4X = columnWidth * 3.5; // Centre de la colonne 4
        gc.setFill(RANGE_UP_COLOR);
        if (player.getRange() > 2) {
            gc.fillText("○ PORTÉE: " + player.getRange(), col4X, yPosition);
        } else {
            gc.fillText("○ PORTÉE: 2", col4X, yPosition);
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
            // Afficher un message par défaut
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, UI_FONT_SIZE - 4));
            gc.setFill(Color.web("#666666"));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Les notifications des power-ups apparaîtront ici...", canvas.getWidth() / 2, yPosition);
            gc.setFill(UI_TEXT_COLOR);
            gc.setTextAlign(TextAlignment.LEFT);
            return;
        }
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, UI_FONT_SIZE - 3));
        gc.setTextAlign(TextAlignment.LEFT);
        
        // Afficher les notifications empilées verticalement (les plus récentes en haut)
        for (int i = 0; i < recentNotifications.size(); i++) {
            String notification = recentNotifications.get(recentNotifications.size() - 1 - i); // Plus récente en premier
            long timestamp = notificationTimestamps.get(notificationTimestamps.size() - 1 - i);
            long age = System.currentTimeMillis() - timestamp;
            
            // Effet de fade
            double alpha = 1.0 - (double) age / NOTIFICATION_DURATION;
            alpha = Math.max(0.4, alpha);
            
            gc.setFill(Color.web("#00FF00", alpha));
            
            // Position verticale (empiler vers le bas)
            int notificationY = yPosition + (i * (UI_FONT_SIZE - 1)); // Espacement de 15px entre chaque notification
            
            // Centrer horizontalement mais laisser de la marge
            int notificationX = UI_MARGIN + 10;
            gc.fillText("→ " + notification, notificationX, notificationY);
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
        
        // Configurer la police pour le titre principal
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        gc.setFill(UI_TEXT_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Calculer les positions centrales
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Afficher le titre du jeu
        gc.fillText("BOMBERMAN", centerX, centerY - 40);
        
        // Configurer la police pour les instructions
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.fillText("Appuyez sur ENTRÉE pour commencer", centerX, centerY + 20);
        
        // Afficher les contrôles
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        gc.fillText("Flèches : Déplacement | Espace : Poser une bombe", centerX, centerY + 60);
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
        
        // Calculer les positions centrales
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Afficher le message GAME OVER
        gc.fillText("GAME OVER", centerX, centerY - 40);
        
        // Afficher le score final
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setFill(UI_TEXT_COLOR);
        gc.fillText("SCORE FINAL : " + player.getScore(), centerX, centerY);
        
        // Configurer la police pour les instructions de rejeu
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.fillText("Appuyez sur ENTRÉE pour rejouer", centerX, centerY + 40);
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
        
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        gc.fillText("GAME OVER", centerX, centerY - 20);
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.setFill(UI_TEXT_COLOR);
        gc.fillText("Appuyez sur ENTRÉE pour rejouer", centerX, centerY + 40);
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
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        gc.setFill(Color.LIGHTGREEN);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Calculer les positions centrales
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Afficher le message de niveau terminé
        gc.fillText("NIVEAU " + currentLevel + " TERMINÉ !", centerX, centerY - 60);
        
        // Afficher le score actuel
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.setFill(UI_TEXT_COLOR);
        gc.fillText("Score actuel : " + player.getScore(), centerX, centerY - 20);
        
        // Afficher les informations du niveau suivant
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.fillText("Niveau suivant : " + (currentLevel + 1), centerX, centerY + 20);
        
        // Afficher les instructions
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gc.fillText("Appuyez sur ENTRÉE pour continuer", centerX, centerY + 60);
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