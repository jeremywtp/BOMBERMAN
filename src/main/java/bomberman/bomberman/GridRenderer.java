package bomberman.bomberman;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    
    // Taille du joueur (légèrement plus petit que la case pour un meilleur aspect visuel)
    private static final int PLAYER_SIZE = CELL_SIZE - 6;  // 26 pixels au lieu de 32
    private static final int PLAYER_OFFSET = 3;  // Décalage pour centrer le joueur dans la case
    
    // Taille de la bombe (légèrement plus petite que la case)
    private static final int BOMB_SIZE = CELL_SIZE - 4;  // 28 pixels au lieu de 32
    private static final int BOMB_OFFSET = 2;  // Décalage pour centrer la bombe dans la case
    
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
     * Dessine la grille puis le joueur par-dessus.
     * @param player Le joueur à afficher
     */
    public void render(Player player) {
        // Dessiner d'abord la grille
        render();
        
        // Puis dessiner le joueur par-dessus
        renderPlayer(player);
    }
    
    /**
     * Méthode de rendu complète avec joueur, bombe et explosion.
     * @param player Le joueur à afficher
     * @param bomb La bombe active (peut être null)
     * @param explosion L'explosion active (peut être null)
     */
    public void render(Player player, Bomb bomb, Explosion explosion) {
        // Dessiner d'abord la grille
        render();
        
        // Dessiner l'explosion en premier (sous les autres éléments)
        if (explosion != null && explosion.isActive()) {
            renderExplosion(explosion);
        }
        
        // Dessiner la bombe
        if (bomb != null && bomb.isActive()) {
            renderBomb(bomb);
        }
        
        // Dessiner le joueur en dernier (par-dessus tout)
        renderPlayer(player);
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
} 