package bomberman.bomberman;

/**
 * Modèle de données représentant la grille du jeu Bomberman.
 * Stocke les informations logiques sur chaque case (vide, solide, destructible).
 * Cette classe suit le pattern classique de Bomberman :
 * - Contour entièrement en blocs solides
 * - Alternance intérieure : blocs solides toutes les deux cases
 * - Blocs destructibles répartis dans les cases vides disponibles
 */
public class Grid {
    
    private final int columns;
    private final int rows;
    private final TileType[][] cells;
    
    /**
     * Constructeur de la grille
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     */
    public Grid(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new TileType[rows][columns];
        
        initializeGrid();
    }
    
    /**
     * Initialise la grille selon le pattern classique de Bomberman :
     * - Les bordures sont des blocs solides
     * - À l'intérieur, alternance de blocs solides toutes les deux cases
     * - Ajout de blocs destructibles dans certaines cases vides
     */
    private void initializeGrid() {
        // Initialiser le pattern de base
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Bordures : toujours solides
                if (row == 0 || row == rows - 1 || col == 0 || col == columns - 1) {
                    cells[row][col] = TileType.SOLID;
                }
                // Pattern intérieur : blocs solides sur positions paires
                else if (row % 2 == 0 && col % 2 == 0) {
                    cells[row][col] = TileType.SOLID;
                }
                // Sinon, case vide
                else {
                    cells[row][col] = TileType.EMPTY;
                }
            }
        }
        
        // Ajouter des blocs destructibles dans certaines cases vides
        addDestructibleBlocks();
    }
    
    /**
     * Ajoute des blocs destructibles dans la grille
     * Place environ 30% des cases vides disponibles en blocs destructibles
     */
    private void addDestructibleBlocks() {
        // Préserver les cases de départ du joueur (1,1) et ses voisins immédiats
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < columns - 1; col++) {
                // Éviter la zone de départ du joueur (2x2 autour de 1,1)
                if ((row == 1 || row == 2) && (col == 1 || col == 2)) {
                    continue;
                }
                
                // Si c'est une case vide, 30% de chance de devenir destructible
                if (cells[row][col] == TileType.EMPTY && Math.random() < 0.3) {
                    cells[row][col] = TileType.DESTRUCTIBLE;
                }
            }
        }
    }
    
    /**
     * Obtient le type de case à une position donnée
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return Le type de case à cette position
     */
    public TileType getTileType(int column, int row) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IllegalArgumentException("Position hors limites : [" + column + ", " + row + "]");
        }
        return cells[row][column];
    }
    
    /**
     * Vérifie si une case est solide (indestructible)
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si la case est solide, false sinon
     */
    public boolean isSolid(int column, int row) {
        return getTileType(column, row) == TileType.SOLID;
    }
    
    /**
     * Vérifie si une case est destructible
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si la case est destructible, false sinon
     */
    public boolean isDestructible(int column, int row) {
        return getTileType(column, row) == TileType.DESTRUCTIBLE;
    }
    
    /**
     * Vérifie si une case est accessible pour le joueur
     * Une case est accessible si elle est dans les limites de la grille et traversable
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si la case est accessible, false sinon
     */
    public boolean isAccessible(int column, int row) {
        // Vérifier si la position est dans les limites de la grille
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return false;
        }
        
        // Vérifier si la case est traversable
        return getTileType(column, row).isTraversable();
    }
    
    /**
     * Détruit un bloc à la position donnée s'il est destructible
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si un bloc a été détruit, false sinon
     */
    public boolean destroyBlock(int column, int row) {
        // Vérifier si la position est valide
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return false;
        }
        
        // Vérifier si le bloc est destructible
        if (cells[row][column] == TileType.DESTRUCTIBLE) {
            cells[row][column] = TileType.EMPTY;
            return true;
        }
        
        return false;
    }
    
    /**
     * Vérifie si une explosion peut traverser une case
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si l'explosion peut traverser, false sinon
     */
    public boolean canExplosionTraverse(int column, int row) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return false;
        }
        
        return !getTileType(column, row).blocksExplosion();
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