# 🧩 Guide d'Intégration ContoursMapView

## 📋 Vue d'ensemble

J'ai créé **`ContoursMapView`** : une classe JavaFX sur mesure qui respecte exactement vos spécifications :

- ✅ **Sprite 272×176 px** (taille originale, pas de redimensionnement)
- ✅ **Grille 13×11 tuiles de 16×16 px** (208×176 px au total)
- ✅ **Position exacte** : grille à (x=32, y=0) dans le sprite
- ✅ **Architecture StackPane** : ImageView en fond + grille par-dessus
- ✅ **Qualité pixel-perfect** : `setSmooth(false)` pour éviter le flou

## 🚀 Test Rapide

```bash
# Tester l'application de démonstration
mvn compile exec:java -Dexec.mainClass="bomberman.bomberman.ContoursMapTestApp"
```

Cette application montre :
- Votre sprite `contours_map.png` en arrière-plan
- La grille 13×11 parfaitement alignée
- Différents patterns de test (bordures, damier, aléatoire)
- Toutes les dimensions techniques

## 🧩 Intégration dans votre Launcher.java

### Option 1 : Remplacement de GridRenderer (Recommandé)

Modifiez votre méthode `start()` dans `Launcher.java` :

```java
@Override
public void start(Stage primaryStage) {
    // ... code existant jusqu'à la création du canvas ...
    
    // ✨ **NOUVEAU** : Remplacer le canvas par ContoursMapView
    ContoursMapView contoursMapView = new ContoursMapView();
    
    // 🧪 Générer un pattern de test pour vérifier l'alignement
    contoursMapView.generateTestPattern();
    
    // Configuration de la scène avec la nouvelle vue
    StackPane root = new StackPane();
    root.getChildren().add(contoursMapView);
    root.setAlignment(Pos.CENTER); // Centrer la vue dans la fenêtre
    
    Scene scene = new Scene(root, 800, 400); // Ajuster selon vos besoins
    
    // ... reste du code existant ...
}
```

### Option 2 : Intégration hybride (Coexistence)

Si vous voulez garder votre système existant :

```java
@Override
public void start(Stage primaryStage) {
    // Créer les deux systèmes
    Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
    ContoursMapView contoursMapView = new ContoursMapView();
    
    // Conteneur avec onglets ou boutons pour basculer
    TabPane tabPane = new TabPane();
    
    Tab tabOriginal = new Tab("Système Original", canvas);
    Tab tabContours = new Tab("Nouveau Contours", contoursMapView);
    
    tabPane.getTabs().addAll(tabOriginal, tabContours);
    
    Scene scene = new Scene(tabPane, WINDOW_WIDTH, WINDOW_HEIGHT);
    // ... reste du code ...
}
```

## 🎮 Adaptation pour la Logique de Jeu

### 1. Créer un adaptateur pour Grid

```java
public class ContoursGridAdapter {
    private ContoursMapView view;
    private TileType[][] gridData;
    
    public ContoursGridAdapter(ContoursMapView view) {
        this.view = view;
        this.gridData = new TileType[ContoursMapView.getGameColumns()][ContoursMapView.getGameRows()];
        
        // Initialiser avec des cellules vides
        for (int col = 0; col < ContoursMapView.getGameColumns(); col++) {
            for (int row = 0; row < ContoursMapView.getGameRows(); row++) {
                gridData[col][row] = TileType.EMPTY;
            }
        }
    }
    
    public void setTile(int col, int row, TileType type) {
        if (col >= 0 && col < ContoursMapView.getGameColumns() && 
            row >= 0 && row < ContoursMapView.getGameRows()) {
            gridData[col][row] = type;
            view.setCellType(col, row, type);
        }
    }
    
    public TileType getTile(int col, int row) {
        if (col >= 0 && col < ContoursMapView.getGameColumns() && 
            row >= 0 && row < ContoursMapView.getGameRows()) {
            return gridData[col][row];
        }
        return TileType.SOLID; // Hors limites = solide
    }
    
    public boolean isAccessible(int col, int row) {
        return getTile(col, row) == TileType.EMPTY;
    }
}
```

### 2. Adapter les entités (Player, Enemy, Bomb)

```java
public class ContoursGameManager {
    private ContoursMapView mapView;
    private ContoursGridAdapter gridAdapter;
    
    // Position du joueur en coordonnées de grille (0-12, 0-10)
    private int playerCol = 1, playerRow = 1;
    
    public ContoursGameManager() {
        mapView = new ContoursMapView();
        gridAdapter = new ContoursGridAdapter(mapView);
        
        // Générer la map initiale
        generateGameMap();
        
        // Ajouter un rectangle pour visualiser le joueur
        addPlayerVisual();
    }
    
    private void generateGameMap() {
        // Bordures solides (comme dans vos spécifications)
        for (int col = 0; col < ContoursMapView.getGameColumns(); col++) {
            gridAdapter.setTile(col, 0, TileType.SOLID);
            gridAdapter.setTile(col, ContoursMapView.getGameRows() - 1, TileType.SOLID);
        }
        for (int row = 0; row < ContoursMapView.getGameRows(); row++) {
            gridAdapter.setTile(0, row, TileType.SOLID);
            gridAdapter.setTile(ContoursMapView.getGameColumns() - 1, row, TileType.SOLID);
        }
        
        // Quelques blocs destructibles
        gridAdapter.setTile(3, 3, TileType.DESTRUCTIBLE);
        gridAdapter.setTile(5, 5, TileType.DESTRUCTIBLE);
        // ... etc
    }
    
    private void addPlayerVisual() {
        // Ajouter un rectangle bleu pour le joueur
        Rectangle playerRect = new Rectangle(16, 16, Color.LIGHTBLUE);
        updatePlayerPosition();
        
        // L'ajouter à la vue
        mapView.getChildren().add(playerRect);
    }
    
    public void movePlayer(int deltaCol, int deltaRow) {
        int newCol = playerCol + deltaCol;
        int newRow = playerRow + deltaRow;
        
        if (gridAdapter.isAccessible(newCol, newRow)) {
            playerCol = newCol;
            playerRow = newRow;
            updatePlayerPosition();
        }
    }
    
    private void updatePlayerPosition() {
        // Convertir coordonnées grille en pixels
        double pixelX = ContoursMapView.getMapOffsetX() + (playerCol * ContoursMapView.getTileSize());
        double pixelY = ContoursMapView.getMapOffsetY() + (playerRow * ContoursMapView.getTileSize());
        
        // Mettre à jour la position du rectangle joueur
        // (code pour récupérer et positionner le rectangle)
    }
    
    public ContoursMapView getMapView() {
        return mapView;
    }
}
```

## 📁 Structure des Fichiers

Ajoutez à votre projet :

```
src/main/java/bomberman/bomberman/
├── ContoursMapView.java          # ✨ Vue principale (sprite + grille)
├── ContoursMapTestApp.java       # 🧪 Application de test
├── ContoursGridAdapter.java      # 🔧 Adaptateur pour votre logique Grid
└── ContoursGameManager.java      # 🎮 Gestionnaire de jeu adapté
```

## 🎯 Dimensions de Conversion

### Correspondance avec votre système actuel :

| Élément | Votre nouveau système | Système actuel |
|---------|----------------------|----------------|
| **Taille sprite** | 272×176 px | 720×528 px |
| **Nombre colonnes** | 13 | 15 |
| **Nombre lignes** | 11 | 11 |
| **Taille tuile** | 16×16 px | 48×48 px |
| **Position grille** | x=32, y=0 | x=0, y=100 |

### Formules de conversion :

```java
// Conversion coordonnées actuelles → nouveau système
int newCol = (int) ((currentX - 0) / 48.0 * 13.0 / 15.0);
int newRow = (int) ((currentY - 100) / 48.0);

// Conversion nouveau système → coordonnées actuelles  
int currentX = (int) (newCol * 15.0 / 13.0 * 48.0);
int currentY = newRow * 48 + 100;
```

## 🎨 Personnalisation

### Changer les couleurs des blocs :

```java
// Dans ContoursMapView.java, modifiez les constantes :
private static final Color SOLID_COLOR = Color.web("#YOUR_COLOR");
private static final Color DESTRUCTIBLE_COLOR = Color.web("#YOUR_COLOR");
```

### Ajouter des sprites personnalisés :

```java
// Remplacer les Rectangle par des ImageView
ImageView blockSprite = new ImageView(new Image("/sprites/block.png"));
blockSprite.setFitWidth(16);
blockSprite.setFitHeight(16);
blockSprite.setX(col * 16);
blockSprite.setY(row * 16);
gameGridPane.getChildren().add(blockSprite);
```

## 🔧 Redimensionnement Futur

Si vous changez de sprite plus tard :

```java
// Utiliser la méthode resize() prévue
contoursMapView.resize(
    newSpriteWidth,    // Nouvelle largeur sprite
    newSpriteHeight,   // Nouvelle hauteur sprite  
    newTileSize,       // Nouvelle taille tuile
    newMapOffsetX,     // Nouveau décalage X
    newMapOffsetY      // Nouveau décalage Y
);
```

## ✅ Résultat Final

Avec cette intégration, vous obtenez :

- 🖼️ **Votre sprite `contours_map.png`** affiché pixel par pixel
- 🧱 **Grille 13×11** parfaitement alignée dans la zone blanche
- 🎮 **Architecture propre** facilement extensible
- 📐 **Dimensions exactes** respectées (272×176 px)
- 🚀 **Performance optimale** avec JavaFX natif

Votre clone Bomberman aura maintenant un rendu visuel fidèle à votre sprite original ! 🎮✨ 