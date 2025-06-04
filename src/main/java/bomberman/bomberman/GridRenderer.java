package bomberman.bomberman;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.List;

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
    
    // Couleurs pour les power-ups
    private static final Color EXTRA_BOMB_COLOR = Color.CYAN;             // Cyan pour EXTRA_BOMB
    private static final Color RANGE_UP_COLOR = Color.ORANGE;             // Orange pour RANGE_UP
    private static final Color SPEED_UP_COLOR = Color.LIGHTGREEN;         // Vert clair pour SPEED_UP
    
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
     * Dessine l'intégralité de la grille sur le canvas.
     */
    public void render() {
        // Effacer le canvas (fond noir par défaut)
        gc.setFill(EMPTY_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
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
        render(player, null, bomb, explosion, null);
    }
    
    /**
     * Méthode de rendu complète avec joueur, ennemis, bombe et explosion (sans power-ups)
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     */
    public void render(Player player, List<Enemy> enemies, Bomb bomb, Explosion explosion) {
        render(player, enemies, bomb, explosion, null);
    }
    
    /**
     * Méthode de rendu complète avec tous les éléments du jeu et l'interface utilisateur (avec high score)
     * @param player Le joueur à afficher
     * @param enemies Liste des ennemis à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     * @param powerUps Liste des power-ups visibles à afficher
     * @param highScore Le meilleur score enregistré
     */
    public void render(Player player, List<Enemy> enemies, Bomb bomb, Explosion explosion, List<PowerUp> powerUps, int highScore) {
        // Dessiner d'abord la grille
        render();
        
        // Dessiner l'explosion en premier (sous les autres éléments)
        if (explosion != null && explosion.isActive()) {
            renderExplosion(explosion);
        }
        
        // Dessiner les power-ups visibles
        if (powerUps != null) {
            renderPowerUps(powerUps);
        }
        
        // Dessiner la bombe
        if (bomb != null && bomb.isActive()) {
            renderBomb(bomb);
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
        
        // Dessiner l'interface utilisateur par-dessus tout (avec high score)
        renderUI(player, highScore);
        
        // Note: Le message GAME OVER est géré par renderGameOverScreen() appelé depuis Launcher
        // Pas de double appel ici pour éviter les doublons
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
        render(player, enemies, bomb, explosion, powerUps, 0);  // High score par défaut à 0
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
     * Dessine le joueur à sa position actuelle
     * @param player Le joueur à dessiner
     */
    private void renderPlayer(Player player) {
        // Calculer la position en pixels
        int x = player.getX() * CELL_SIZE + PLAYER_OFFSET;
        int y = player.getY() * CELL_SIZE + PLAYER_OFFSET;
        
        // Dessiner le joueur
        gc.setFill(PLAYER_COLOR);
        gc.fillRect(x, y, PLAYER_SIZE, PLAYER_SIZE);
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
     * Dessine l'interface utilisateur (vie, score, high score sur une ligne)
     * @param player Le joueur pour afficher ses informations
     * @param highScore Le meilleur score enregistré
     */
    private void renderUI(Player player, int highScore) {
        // Configurer la police pour l'UI
        gc.setFont(Font.font("Arial", FontWeight.BOLD, UI_FONT_SIZE));
        gc.setFill(UI_TEXT_COLOR);
        
        // Position verticale constante pour toute l'UI (au-dessus de la grille)
        int uiY = UI_MARGIN + UI_FONT_SIZE;
        
        // Afficher la vie du joueur (à gauche)
        gc.setTextAlign(TextAlignment.LEFT);
        String lifeText = "VIE : " + (player.isAlive() ? "1" : "0");
        gc.fillText(lifeText, UI_MARGIN, uiY);
        
        // Afficher le score actuel (au centre)
        gc.setTextAlign(TextAlignment.CENTER);
        String scoreText = "SCORE : " + player.getScore();
        gc.fillText(scoreText, canvas.getWidth() / 2, uiY);
        
        // Afficher le high score (à droite)
        gc.setTextAlign(TextAlignment.RIGHT);
        String highScoreText = "HIGHSCORE : " + highScore;
        gc.fillText(highScoreText, canvas.getWidth() - UI_MARGIN, uiY);
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
     * Dessine un power-up individuel à sa position
     * @param powerUp Le power-up à dessiner
     */
    private void renderPowerUp(PowerUp powerUp) {
        // Calculer la position en pixels
        int x = powerUp.getX() * CELL_SIZE + POWER_UP_OFFSET;
        int y = powerUp.getY() * CELL_SIZE + POWER_UP_OFFSET;
        
        // Déterminer la couleur selon le type de power-up
        Color powerUpColor = getPowerUpColor(powerUp.getType());
        
        // Dessiner le power-up
        gc.setFill(powerUpColor);
        gc.fillRect(x, y, POWER_UP_SIZE, POWER_UP_SIZE);
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
            default:
                return Color.WHITE; // Couleur par défaut en cas d'erreur
        }
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
} 