package bomberman.bomberman;

import java.util.HashMap;
import java.util.Map;

/**
 * Modèle de données représentant la grille du jeu Bomberman.
 * Stocke les informations logiques sur chaque case (vide, solide, destructible).
 * Cette classe suit le pattern classique de Bomberman :
 * - Contour entièrement en blocs solides
 * - Alternance intérieure : blocs solides toutes les deux cases
 * - Blocs destructibles répartis dans les cases vides disponibles
 * - Power-ups cachés dans certains blocs destructibles
 */
public class Grid {
    
    private final int columns;
    private final int rows;
    private final TileType[][] cells;
    
    // Map pour stocker les power-ups cachés dans les blocs destructibles
    // Clé : "x,y" (position), Valeur : PowerUpType
    private final Map<String, PowerUpType> hiddenPowerUps;
    
    // Probabilité qu'un bloc destructible contienne un power-up (20% par défaut)
    private static final double POWER_UP_PROBABILITY = 0.2;
    
    /**
     * Constructeur de la grille
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     */
    public Grid(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new TileType[rows][columns];
        this.hiddenPowerUps = new HashMap<>();
        
        initializeGrid();
    }
    
    /**
     * Initialise la grille selon le pattern classique de Bomberman :
     * - Les bordures sont des blocs solides (géré par le sprite de contours)
     * - À l'intérieur, blocs non destructibles sur positions (x,y) où x et y sont impairs
     * - Ajout de blocs destructibles dans certaines cases vides
     * - Ajout de power-ups cachés dans certains blocs destructibles
     */
    private void initializeGrid() {
        // Initialiser toutes les cases comme vides
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                cells[row][col] = TileType.EMPTY;
            }
        }
        
        // 🧱 **NOUVEAU** : Placer les blocs non destructibles selon le pattern Bomberman
        placeIndestructibleBlocks();
        
        // Placer les bordures (pour la logique de collision)
        placeBorderBlocks();
        
        // Ajouter des blocs destructibles dans certaines cases vides
        addDestructibleBlocks();
        
        // Ajouter des power-ups cachés dans certains blocs destructibles
        addHiddenPowerUps();
        
        // Afficher le compte final des blocs non destructibles
        countIndestructibleBlocks();
    }
    
    /**
     * 🧱 **NOUVEAU** : Place les blocs non destructibles selon le pattern Bomberman
     * Positions : (x,y) où x % 2 == 1 && y % 2 == 1
     * Génère exactement 38 blocs dans une grille 13×11
     */
    private void placeIndestructibleBlocks() {
        int count = 0;
        for (int row = 1; row < rows - 1; row += 2) {  // Lignes impaires seulement
            for (int col = 1; col < columns - 1; col += 2) {  // Colonnes impaires seulement
                cells[row][col] = TileType.SOLID;
                count++;
            }
        }
        System.out.println("🧱 " + count + " blocs non destructibles placés selon le pattern Bomberman");
    }
    
    /**
     * 🧱 **NOUVEAU** : Place les bordures solides pour la logique de collision
     */
    private void placeBorderBlocks() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Bordures : toujours solides
                if (row == 0 || row == rows - 1 || col == 0 || col == columns - 1) {
                    cells[row][col] = TileType.SOLID;
                }
            }
        }
    }
    
    /**
     * 🧱 **NOUVEAU** : Compte et affiche le nombre total de blocs non destructibles
     */
    private void countIndestructibleBlocks() {
        int borderCount = 0;
        int interiorCount = 0;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (cells[row][col] == TileType.SOLID) {
                    if (row == 0 || row == rows - 1 || col == 0 || col == columns - 1) {
                        borderCount++;
                    } else {
                        interiorCount++;
                    }
                }
            }
        }
        
        System.out.println("📊 Blocs SOLID - Bordures: " + borderCount + ", Intérieurs: " + interiorCount + ", Total: " + (borderCount + interiorCount));
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
     * Ajoute des power-ups cachés dans certains blocs destructibles
     * Chaque bloc destructible a une chance de contenir un power-up aléatoire
     */
    private void addHiddenPowerUps() {
        PowerUpType[] powerUpTypes = PowerUpType.values();
        
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < columns - 1; col++) {
                // Si c'est un bloc destructible
                if (cells[row][col] == TileType.DESTRUCTIBLE) {
                    // Chance de contenir un power-up
                    if (Math.random() < POWER_UP_PROBABILITY) {
                        // Choisir un type de power-up aléatoire
                        PowerUpType randomType = powerUpTypes[(int) (Math.random() * powerUpTypes.length)];
                        String key = col + "," + row;
                        hiddenPowerUps.put(key, randomType);
                        
                        System.out.println("Power-up " + randomType + " caché à la position (" + col + ", " + row + ")");
                    }
                }
            }
        }
        
        System.out.println("Total de " + hiddenPowerUps.size() + " power-ups cachés générés");
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
     * @return Le type de power-up révélé (null si aucun), ou null si aucun bloc détruit
     */
    public PowerUpType destroyBlock(int column, int row) {
        // Vérifier si la position est valide
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return null;
        }
        
        // Vérifier si le bloc est destructible
        if (cells[row][column] == TileType.DESTRUCTIBLE) {
            cells[row][column] = TileType.EMPTY;
            
            // Vérifier s'il y avait un power-up caché
            String key = column + "," + row;
            PowerUpType powerUpType = hiddenPowerUps.remove(key);
            
            if (powerUpType != null) {
                System.out.println("Power-up " + powerUpType + " révélé à la position (" + column + ", " + row + ")");
            }
            
            return powerUpType;  // Peut être null si pas de power-up
        }
        
        return null;
    }
    
    /**
     * Vérifie si un bloc destructible à la position donnée contient un power-up caché
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return true si un power-up est caché à cette position
     */
    public boolean hasHiddenPowerUp(int column, int row) {
        String key = column + "," + row;
        return hiddenPowerUps.containsKey(key);
    }
    
    /**
     * @return Le nombre de power-ups encore cachés dans la grille
     */
    public int getHiddenPowerUpCount() {
        return hiddenPowerUps.size();
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
    
    /**
     * Obtient le type de power-up caché à la position donnée (sans le révéler)
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return Le type de power-up ou null si aucun
     */
    public PowerUpType getHiddenPowerUpType(int column, int row) {
        String key = column + "," + row;
        return hiddenPowerUps.get(key);
    }
    
    /**
     * Retire un power-up caché de la map (utilisé après révélation)
     * @param column Colonne (x)
     * @param row Ligne (y)
     * @return Le type de power-up retiré ou null si aucun
     */
    public PowerUpType removeHiddenPowerUp(int column, int row) {
        String key = column + "," + row;
        return hiddenPowerUps.remove(key);
    }
} 