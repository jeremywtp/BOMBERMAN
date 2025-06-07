package bomberman.bomberman;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;

/**
 * Vue personnalisée pour afficher le sprite contours_map.png avec la grille de jeu parfaitement alignée.
 * 
 * Spécifications :
 * - Sprite : 272×176 px (taille originale, pas de redimensionnement)
 * - Map de jeu : 13 colonnes × 11 lignes, 16×16 px par tuile = 208×176 px
 * - Position de la map : x=32, y=0 (centrée dans la zone blanche du sprite)
 * - Architecture : StackPane avec ImageView en fond et grille par-dessus
 */
public class ContoursMapView extends StackPane {
    
    // 🎯 Spécifications exactes du sprite et de la grille
    private static final int SPRITE_WIDTH = 272;           // Largeur du sprite contours
    private static final int SPRITE_HEIGHT = 176;          // Hauteur du sprite contours
    private static final int GAME_COLUMNS = 13;            // Nombre de colonnes de la map
    private static final int GAME_ROWS = 11;               // Nombre de lignes de la map  
    private static final int TILE_SIZE = 16;               // Taille d'une tuile en pixels
    private static final int MAP_OFFSET_X = 32;            // Décalage de la map dans le sprite (bordure gauche)
    private static final int MAP_OFFSET_Y = 0;             // Décalage de la map dans le sprite (bordure haut)
    
    // 📐 Dimensions calculées
    private static final int MAP_WIDTH = GAME_COLUMNS * TILE_SIZE;   // 13 * 16 = 208px
    private static final int MAP_HEIGHT = GAME_ROWS * TILE_SIZE;     // 11 * 16 = 176px
    
    // 🎨 Couleurs pour les types de tuiles (temporaires pour visualisation)
    private static final Color EMPTY_COLOR = Color.TRANSPARENT;      // Transparent pour voir le sprite
    private static final Color SOLID_COLOR = Color.web("#505050");   // Gris pour les blocs solides
    private static final Color DESTRUCTIBLE_COLOR = Color.web("#A0522D"); // Marron pour les blocs destructibles
    
    // 🧩 Composants de la vue
    private ImageView contoursImageView;
    private Pane gameGridPane;
    private Rectangle[][] gridCells;
    
    /**
     * Constructeur principal - initialise la vue avec le sprite et la grille
     */
    public ContoursMapView() {
        initializeView();
    }
    
    /**
     * Initialise la vue complète : sprite + grille alignée
     */
    private void initializeView() {
        // 📏 Définir la taille exacte du conteneur
        setPrefSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        setMinSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        setMaxSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        
        // 🖼️ Charger et configurer le sprite de contours
        setupContoursSprite();
        
        // 🧱 Créer et positionner la grille de jeu
        setupGameGrid();
        
        // 📦 Ajouter les composants dans l'ordre (fond d'abord, grille par-dessus)
        getChildren().addAll(contoursImageView, gameGridPane);
        
        // 🎯 Centrage et alignement
        setAlignment(Pos.TOP_LEFT); // Alignement précis au pixel près
        
        System.out.println("✅ ContoursMapView initialisée - Sprite: " + SPRITE_WIDTH + "×" + SPRITE_HEIGHT + 
                          ", Map: " + MAP_WIDTH + "×" + MAP_HEIGHT + " à (" + MAP_OFFSET_X + "," + MAP_OFFSET_Y + ")");
    }
    
    /**
     * 🖼️ Configure le sprite de contours en arrière-plan
     */
    private void setupContoursSprite() {
        try {
            // Charger l'image depuis les ressources
            Image contoursImage = new Image(getClass().getResourceAsStream("/sprites/contours_map.png"));
            
            if (contoursImage.isError()) {
                throw new RuntimeException("Impossible de charger /sprites/contours_map.png");
            }
            
            // Créer l'ImageView avec taille exacte (pas de redimensionnement)
            contoursImageView = new ImageView(contoursImage);
            contoursImageView.setFitWidth(SPRITE_WIDTH);
            contoursImageView.setFitHeight(SPRITE_HEIGHT);
            
            // 🎨 Qualité pixel-perfect : pas de lissage flou
            contoursImageView.setSmooth(false);
            contoursImageView.setPreserveRatio(false);
            
            // 📍 Position exacte (pas de décalage)
            contoursImageView.setTranslateX(0);
            contoursImageView.setTranslateY(0);
            
            System.out.println("🖼️ Sprite contours chargé : " + (int)contoursImage.getWidth() + "×" + (int)contoursImage.getHeight() + 
                              " → affiché à " + SPRITE_WIDTH + "×" + SPRITE_HEIGHT);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du sprite contours : " + e.getMessage());
            e.printStackTrace();
            
            // 🛡️ Fallback : rectangle de couleur pour débugger
            contoursImageView = new ImageView();
            contoursImageView.setFitWidth(SPRITE_WIDTH);
            contoursImageView.setFitHeight(SPRITE_HEIGHT);
        }
    }
    
    /**
     * 🧱 Configure la grille de jeu positionnée dans le sprite
     */
    private void setupGameGrid() {
        // Créer le conteneur pour la grille
        gameGridPane = new Pane();
        gameGridPane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        
        // 📍 Positionner la grille dans le sprite (x=32, y=0)
        gameGridPane.setTranslateX(MAP_OFFSET_X);
        gameGridPane.setTranslateY(MAP_OFFSET_Y);
        
        // 🏗️ Créer le tableau de cellules
        gridCells = new Rectangle[GAME_COLUMNS][GAME_ROWS];
        
        // 🔨 Générer toutes les cellules de la grille
        for (int col = 0; col < GAME_COLUMNS; col++) {
            for (int row = 0; row < GAME_ROWS; row++) {
                createGridCell(col, row);
            }
        }
        
        System.out.println("🧱 Grille de jeu créée : " + GAME_COLUMNS + "×" + GAME_ROWS + 
                          " tuiles de " + TILE_SIZE + "px à (" + MAP_OFFSET_X + "," + MAP_OFFSET_Y + ")");
    }
    
    /**
     * 🔨 Crée une cellule individuelle de la grille
     * @param col Colonne (0 à GAME_COLUMNS-1)
     * @param row Ligne (0 à GAME_ROWS-1)
     */
    private void createGridCell(int col, int row) {
        // Calculer la position en pixels dans la grille
        double x = col * TILE_SIZE;
        double y = row * TILE_SIZE;
        
        // Créer la cellule rectangulaire
        Rectangle cell = new Rectangle(TILE_SIZE, TILE_SIZE);
        cell.setX(x);
        cell.setY(y);
        
        // 🎨 Couleur par défaut : transparent pour voir le sprite
        cell.setFill(EMPTY_COLOR);
        cell.setStroke(null); // Pas de contour par défaut
        
        // 📦 Ajouter au conteneur et mémoriser
        gameGridPane.getChildren().add(cell);
        gridCells[col][row] = cell;
    }
    
    /**
     * 🎨 Définit le type d'une cellule (et sa couleur)
     * @param col Colonne (0 à GAME_COLUMNS-1)
     * @param row Ligne (0 à GAME_ROWS-1) 
     * @param tileType Type de tuile : EMPTY, SOLID, DESTRUCTIBLE
     */
    public void setCellType(int col, int row, TileType tileType) {
        if (col < 0 || col >= GAME_COLUMNS || row < 0 || row >= GAME_ROWS) {
            return; // Position invalide
        }
        
        Rectangle cell = gridCells[col][row];
        
        switch (tileType) {
            case EMPTY:
                cell.setFill(EMPTY_COLOR); // Transparent : sprite visible
                break;
            case SOLID:
                cell.setFill(SOLID_COLOR); // Gris : bloc indestructible
                break;
            case DESTRUCTIBLE:
                cell.setFill(DESTRUCTIBLE_COLOR); // Marron : bloc destructible
                break;
        }
    }
    
    /**
     * 🧪 Méthode de test pour visualiser la grille avec quelques blocs
     */
    public void generateTestPattern() {
        // Bordures : blocs solides
        for (int col = 0; col < GAME_COLUMNS; col++) {
            setCellType(col, 0, TileType.SOLID);                    // Ligne du haut
            setCellType(col, GAME_ROWS - 1, TileType.SOLID);        // Ligne du bas
        }
        for (int row = 0; row < GAME_ROWS; row++) {
            setCellType(0, row, TileType.SOLID);                    // Colonne de gauche
            setCellType(GAME_COLUMNS - 1, row, TileType.SOLID);     // Colonne de droite
        }
        
        // Quelques blocs destructibles au centre
        setCellType(2, 2, TileType.DESTRUCTIBLE);
        setCellType(4, 3, TileType.DESTRUCTIBLE);
        setCellType(6, 5, TileType.DESTRUCTIBLE);
        setCellType(8, 7, TileType.DESTRUCTIBLE);
        setCellType(10, 9, TileType.DESTRUCTIBLE);
        
        System.out.println("🧪 Pattern de test appliqué à la grille");
    }
    
    /**
     * 🔄 Méthode pour redimensionner si nécessaire (pour évolutions futures)
     * @param newSpriteWidth Nouvelle largeur du sprite
     * @param newSpriteHeight Nouvelle hauteur du sprite
     * @param newTileSize Nouvelle taille des tuiles
     * @param newMapOffsetX Nouveau décalage X de la map
     * @param newMapOffsetY Nouveau décalage Y de la map
     */
    public void resize(int newSpriteWidth, int newSpriteHeight, int newTileSize, int newMapOffsetX, int newMapOffsetY) {
        // Redimensionner le sprite
        contoursImageView.setFitWidth(newSpriteWidth);
        contoursImageView.setFitHeight(newSpriteHeight);
        
        // Redimensionner le conteneur
        setPrefSize(newSpriteWidth, newSpriteHeight);
        setMinSize(newSpriteWidth, newSpriteHeight);
        setMaxSize(newSpriteWidth, newSpriteHeight);
        
        // Repositionner et redimensionner la grille
        gameGridPane.setTranslateX(newMapOffsetX);
        gameGridPane.setTranslateY(newMapOffsetY);
        
        // Redimensionner les cellules
        for (int col = 0; col < GAME_COLUMNS; col++) {
            for (int row = 0; row < GAME_ROWS; row++) {
                Rectangle cell = gridCells[col][row];
                cell.setWidth(newTileSize);
                cell.setHeight(newTileSize);
                cell.setX(col * newTileSize);
                cell.setY(row * newTileSize);
            }
        }
        
        System.out.println("🔄 Vue redimensionnée : " + newSpriteWidth + "×" + newSpriteHeight + 
                          ", tuiles " + newTileSize + "px, décalage (" + newMapOffsetX + "," + newMapOffsetY + ")");
    }
    
    /**
     * 📏 Getters pour les dimensions
     */
    public static int getSpriteWidth() { return SPRITE_WIDTH; }
    public static int getSpriteHeight() { return SPRITE_HEIGHT; }
    public static int getGameColumns() { return GAME_COLUMNS; }
    public static int getGameRows() { return GAME_ROWS; }
    public static int getTileSize() { return TILE_SIZE; }
    public static int getMapOffsetX() { return MAP_OFFSET_X; }
    public static int getMapOffsetY() { return MAP_OFFSET_Y; }
    
    /**
     * 🎯 Obtient une cellule de la grille pour manipulation directe
     * @param col Colonne
     * @param row Ligne
     * @return Rectangle de la cellule ou null si position invalide
     */
    public Rectangle getCell(int col, int row) {
        if (col < 0 || col >= GAME_COLUMNS || row < 0 || row >= GAME_ROWS) {
            return null;
        }
        return gridCells[col][row];
    }
    
    /**
     * 🧹 Efface toute la grille (toutes les cellules deviennent transparentes)
     */
    public void clearGrid() {
        for (int col = 0; col < GAME_COLUMNS; col++) {
            for (int row = 0; row < GAME_ROWS; row++) {
                setCellType(col, row, TileType.EMPTY);
            }
        }
        System.out.println("🧹 Grille effacée");
    }
} 