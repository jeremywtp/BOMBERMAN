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
    
    // ✨ **NOUVEAU** : Listener pour les notifications de destruction de blocs
    private DestructibleBlockListener destructibleBlockListener;
    
    // ✨ **NOUVEAU** : Position de spawn du joueur 2 (mode coopération)
    private int player2SpawnX = -1;
    private int player2SpawnY = -1;
    
    /**
     * Constructeur de la grille
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     * @param currentLevel Niveau actuel pour adapter la génération des power-ups
     */
    public Grid(int columns, int rows, int currentLevel) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new TileType[rows][columns];
        this.hiddenPowerUps = new HashMap<>();
        
        initializeGrid(currentLevel);
    }
    
    /**
     * ✨ **NOUVEAU** : Constructeur de la grille avec support complet des modes de jeu
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     * @param currentLevel Niveau actuel pour adapter la génération des power-ups
     * @param isCooperationMode True si en mode coopération
     * @param isBattleMode True si en mode battle
     * @param player2SpawnX Position X de spawn du joueur 2 (ignoré si mode solo)
     * @param player2SpawnY Position Y de spawn du joueur 2 (ignoré si mode solo)
     */
    public Grid(int columns, int rows, int currentLevel, boolean isCooperationMode, boolean isBattleMode, int player2SpawnX, int player2SpawnY) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new TileType[rows][columns];
        this.hiddenPowerUps = new HashMap<>();
        
        // Enregistrer la position de spawn du joueur 2 en mode multijoueur
        if (isCooperationMode || isBattleMode) {
            this.player2SpawnX = player2SpawnX;
            this.player2SpawnY = player2SpawnY;
        }
        
        initializeGrid(currentLevel, isBattleMode);
    }
    
    /**
     * ✨ **RÉTROCOMPATIBILITÉ** : Constructeur de la grille avec support du mode coopération
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     * @param currentLevel Niveau actuel pour adapter la génération des power-ups
     * @param isMultiplayerMode True si en mode coopération ou battle
     * @param player2SpawnX Position X de spawn du joueur 2 (ignoré si pas en mode multijoueur)
     * @param player2SpawnY Position Y de spawn du joueur 2 (ignoré si pas en mode multijoueur)
     */
    public Grid(int columns, int rows, int currentLevel, boolean isMultiplayerMode, int player2SpawnX, int player2SpawnY) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new TileType[rows][columns];
        this.hiddenPowerUps = new HashMap<>();
        
        // Enregistrer la position de spawn du joueur 2 seulement en mode multijoueur
        if (isMultiplayerMode) {
            this.player2SpawnX = player2SpawnX;
            this.player2SpawnY = player2SpawnY;
        }
        
        initializeGrid(currentLevel, false); // Mode battle = false par défaut pour rétrocompatibilité
    }
    
    /**
     * Constructeur de la grille (rétrocompatibilité - niveau 1 par défaut)
     * @param columns Nombre de colonnes
     * @param rows Nombre de lignes
     */
    public Grid(int columns, int rows) {
        this(columns, rows, 1); // Niveau 1 par défaut
    }
    
    /**
     * Initialise la grille selon le pattern classique de Bomberman :
     * - Les bordures sont des blocs solides
     * - À l'intérieur, alternance de blocs solides toutes les deux cases
     * - Ajout de 8 blocs solides aléatoires supplémentaires (sauf en mode Battle)
     * - Ajout de blocs destructibles dans certaines cases vides
     * - Ajout de power-ups cachés dans certains blocs destructibles
     * @param currentLevel Niveau actuel pour adapter la génération des power-ups
     * @param isBattleMode True si en mode Battle (modifie la génération de blocs)
     */
    private void initializeGrid(int currentLevel, boolean isBattleMode) {
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
        
        // ✨ **MODE BATTLE** : Pas de blocs solides aléatoires en mode Battle
        if (!isBattleMode) {
            // Ajouter 8 blocs solides aléatoires supplémentaires (modes normal et coopération)
            addRandomSolidBlocks();
        } else {
            System.out.println("MODE BATTLE : Pas de blocs solides aléatoires ajoutés");
        }
        
        // Ajouter des blocs destructibles dans certaines cases vides
        addDestructibleBlocks(isBattleMode);
        
        // Ajouter des power-ups cachés dans certains blocs destructibles
        addHiddenPowerUps(currentLevel);
    }
    
    /**
     * ✨ **RÉTROCOMPATIBILITÉ** : Version sans paramètre Battle Mode
     * @param currentLevel Niveau actuel pour adapter la génération des power-ups
     */
    private void initializeGrid(int currentLevel) {
        initializeGrid(currentLevel, false); // Mode battle = false par défaut
    }
    
    /**
     * Ajoute 8 blocs solides aléatoires sur la grille
     * Ces blocs sont positionnés aléatoirement à chaque partie
     * Ils ne peuvent pas être placés sur les positions de départ des joueurs (1,1) et joueur 2 en mode coopération/battle
     */
    private void addRandomSolidBlocks() {
        int blocksToAdd = 8;
        int blocksAdded = 0;
        int attempts = 0;
        int maxAttempts = 100; // Éviter les boucles infinies
        
        while (blocksAdded < blocksToAdd && attempts < maxAttempts) {
            attempts++;
            
            // Générer une position aléatoire dans la zone intérieure
            int col = 1 + (int) (Math.random() * (columns - 2));
            int row = 1 + (int) (Math.random() * (rows - 2));
            
            // Vérifier que la position n'est pas la zone de départ du joueur 1 (2x2 autour de 1,1)
            if ((row == 1 || row == 2) && (col == 1 || col == 2)) {
                continue;
            }
            
            // ✨ **CORRECTION BATTLE MODE** : Éviter la zone de spawn du joueur 2 en mode coopération/battle
            if (player2SpawnX != -1 && player2SpawnY != -1) {
                // Laisser une zone 3x3 complètement vide autour du spawn du joueur 2
                if (Math.abs(row - player2SpawnY) <= 1 && Math.abs(col - player2SpawnX) <= 1) {
                    continue;
                }
            }
            
            // Vérifier que la case est actuellement vide
            if (cells[row][col] == TileType.EMPTY) {
                cells[row][col] = TileType.SOLID;
                blocksAdded++;
                System.out.println("Bloc solide aléatoire #" + blocksAdded + " ajouté à (" + col + ", " + row + ")");
            }
        }
        
        System.out.println("Total de " + blocksAdded + " blocs solides aléatoires ajoutés sur " + blocksToAdd + " demandés");
        if (player2SpawnX != -1 && player2SpawnY != -1) {
            System.out.println("Zone de spawn du joueur 2 protégée : 3x3 autour de (" + player2SpawnX + ", " + player2SpawnY + ")");
        }
    }
    
    /**
     * Ajoute des blocs destructibles dans la grille
     * - Mode normal/coopération : exactement 33 blocs destructibles
     * - Mode Battle : exactement 80 blocs destructibles
     * Les blocs sont placés aléatoirement dans les cases vides disponibles
     * @param isBattleMode True si en mode Battle (change le nombre de blocs)
     */
    private void addDestructibleBlocks(boolean isBattleMode) {
        final int TARGET_DESTRUCTIBLE_BLOCKS = isBattleMode ? 80 : 33;
        
        // Collecter toutes les positions vides disponibles (hors zone de départ)
        java.util.List<int[]> availablePositions = new java.util.ArrayList<>();
        
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < columns - 1; col++) {
                // Éviter la zone de départ du joueur 1 (2x2 autour de 1,1)
                if ((row == 1 || row == 2) && (col == 1 || col == 2)) {
                    continue;
                }
                
                // ✨ **NOUVEAU** : Éviter la zone de spawn du joueur 2 en mode coopération (2x2 autour de sa position)
                if (player2SpawnX != -1 && player2SpawnY != -1) {
                    // Laisser une zone 3x3 complètement vide autour du spawn du joueur 2
                    if (Math.abs(row - player2SpawnY) <= 1 && Math.abs(col - player2SpawnX) <= 1) {
                        continue;
                    }
                }
                
                // Si c'est une case vide, l'ajouter aux positions disponibles
                if (cells[row][col] == TileType.EMPTY) {
                    availablePositions.add(new int[]{col, row});
                }
            }
        }
        
        // Mélanger les positions pour avoir un placement aléatoire
        java.util.Collections.shuffle(availablePositions);
        
        // Placer exactement 33 blocs destructibles
        int blocksPlaced = 0;
        for (int i = 0; i < availablePositions.size() && blocksPlaced < TARGET_DESTRUCTIBLE_BLOCKS; i++) {
            int[] pos = availablePositions.get(i);
            int col = pos[0];
            int row = pos[1];
            
            cells[row][col] = TileType.DESTRUCTIBLE;
            blocksPlaced++;
        }
        
        String mode = isBattleMode ? "BATTLE" : "NORMAL/COOPÉRATION";
        System.out.println("MODE " + mode + " : " + blocksPlaced + " blocs destructibles placés sur " + TARGET_DESTRUCTIBLE_BLOCKS + " demandés");
        System.out.println("Positions disponibles trouvées : " + availablePositions.size());
    }
    
    /**
     * ✨ **RÉTROCOMPATIBILITÉ** : Version sans paramètre Battle Mode
     */
    private void addDestructibleBlocks() {
        addDestructibleBlocks(false); // Mode battle = false par défaut
    }
    
    /**
     * Ajoute des power-ups cachés dans certains blocs destructibles
     * Pour le niveau 1 : exactement 2x EXTRA_BOMB et 1x EXPLOSION_EXPANDER
     * Pour les autres niveaux : distribution aléatoire classique
     * @param currentLevel Niveau actuel pour adapter la génération
     */
    private void addHiddenPowerUps(int currentLevel) {
        if (currentLevel == 1) {
            // Niveau 1 : distribution fixe garantie
            addLevel1PowerUps();
        } else {
            // Autres niveaux : distribution aléatoire classique
            addRandomPowerUps();
        }
    }
    
    /**
     * Ajoute exactement 2x EXTRA_BOMB et 1x EXPLOSION_EXPANDER pour le niveau 1
     */
    private void addLevel1PowerUps() {
        // Collecter toutes les positions de blocs destructibles
        java.util.List<int[]> destructiblePositions = new java.util.ArrayList<>();
        
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < columns - 1; col++) {
                if (cells[row][col] == TileType.DESTRUCTIBLE) {
                    destructiblePositions.add(new int[]{col, row});
                }
            }
        }
        
        // Mélanger les positions pour avoir un placement aléatoire
        java.util.Collections.shuffle(destructiblePositions);
        
        // Placer les power-ups garantis
        int powerUpsPlaced = 0;
        
        // Placer 2x EXTRA_BOMB
        for (int i = 0; i < 2 && powerUpsPlaced < destructiblePositions.size(); i++) {
            int[] pos = destructiblePositions.get(powerUpsPlaced);
            String key = pos[0] + "," + pos[1];
            hiddenPowerUps.put(key, PowerUpType.EXTRA_BOMB);
            System.out.println("EXTRA_BOMB " + (i + 1) + " caché à la position (" + pos[0] + ", " + pos[1] + ")");
            powerUpsPlaced++;
        }
        
        // Placer 1x EXPLOSION_EXPANDER
        if (powerUpsPlaced < destructiblePositions.size()) {
            int[] pos = destructiblePositions.get(powerUpsPlaced);
            String key = pos[0] + "," + pos[1];
            hiddenPowerUps.put(key, PowerUpType.EXPLOSION_EXPANDER);
            System.out.println("EXPLOSION_EXPANDER caché à la position (" + pos[0] + ", " + pos[1] + ")");
            powerUpsPlaced++;
        }
        
        System.out.println("Niveau 1 : " + powerUpsPlaced + " power-ups garantis placés (2x EXTRA_BOMB + 1x EXPLOSION_EXPANDER)");
    }
    
    /**
     * Ajoute des power-ups cachés selon la distribution aléatoire classique
     */
    private void addRandomPowerUps() {
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
            
            // ✨ **NOUVEAU** : Notifier le listener de la destruction du bloc
            if (destructibleBlockListener != null) {
                destructibleBlockListener.onBlockDestroyed(column, row);
            }
            
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
    
    /**
     * ✨ **NOUVEAU** : Définit le listener pour les notifications de destruction de blocs
     * @param listener Le listener à notifier lors de la destruction de blocs
     */
    public void setDestructibleBlockListener(DestructibleBlockListener listener) {
        this.destructibleBlockListener = listener;
    }
} 