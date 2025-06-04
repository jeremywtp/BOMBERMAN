package bomberman.bomberman;

/**
 * Modèle de données représentant la grille du jeu Bomberman.
 * Stocke les informations logiques sur chaque case (solide ou vide).
 * Cette classe suit le pattern classique de Bomberman :
 * - Contour entièrement en blocs solides
 * - Alternance intérieure : blocs solides toutes les deux cases
 */
public class Grid {
    
    // Types de cellules possibles
    public enum CellType {
        EMPTY,  // Case vide (traversable)
        SOLID   // Bloc solide (indestructible)
    }
    
    private final int columns;
    private final int rows;
    private final CellType[][] cells;
    
    /**
     * Constructeur de la grille
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     */
    public Grid(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new CellType[rows][columns];
        
        initializeGrid();
    }
    
    /**
     * Initialise la grille selon le pattern classique de Bomberman :
     * - Les bordures sont des blocs solides
     * - À l'intérieur, alternance de blocs solides toutes les deux cases
     *   (lignes et colonnes impaires)
     */
    private void initializeGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Bordures : toujours solides
                if (row == 0 || row == rows - 1 || col == 0 || col == columns - 1) {
                    cells[row][col] = CellType.SOLID;
                }
                // Pattern intérieur : blocs solides sur positions impaires
                else if (row % 2 == 0 && col % 2 == 0) {
                    cells[row][col] = CellType.SOLID;
                }
                // Sinon, case vide
                else {
                    cells[row][col] = CellType.EMPTY;
                }
            }
        }
    }
    
    /**
     * Obtient le type de cellule à une position donnée
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return Le type de cellule à cette position
     */
    public CellType getCellType(int column, int row) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IllegalArgumentException("Position hors limites : [" + column + ", " + row + "]");
        }
        return cells[row][column];
    }
    
    /**
     * Vérifie si une case est solide
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si la case est solide, false sinon
     */
    public boolean isSolid(int column, int row) {
        return getCellType(column, row) == CellType.SOLID;
    }
    
    /**
     * Vérifie si une case est accessible pour le joueur
     * Une case est accessible si elle est dans les limites de la grille et n'est pas solide
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si la case est accessible, false sinon
     */
    public boolean isAccessible(int column, int row) {
        // Vérifier si la position est dans les limites de la grille
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return false;
        }
        
        // Vérifier si la case n'est pas solide
        return getCellType(column, row) == CellType.EMPTY;
    }
    
    /**
     * @return Nombre de colonnes de la grille
     */
    public int getColumns() {
        return columns;
    }
    
    /**
     * @return Nombre de lignes de la grille
     */
    public int getRows() {
        return rows;
    }
} 