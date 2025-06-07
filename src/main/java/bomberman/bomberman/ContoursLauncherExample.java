package bomberman.bomberman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;

/**
 * Exemple d'intégration de ContoursMapView dans votre Launcher Bomberman existant.
 * 
 * Cet exemple montre comment :
 * - Remplacer le Canvas par ContoursMapView
 * - Conserver la gestion des touches
 * - Adapter la logique de jeu pour les nouvelles dimensions
 * - Intégrer avec votre système existant
 */
public class ContoursLauncherExample extends Application {
    
    // 🎯 Nouvelles dimensions basées sur ContoursMapView
    private static final int WINDOW_WIDTH = 400;   // Ajustée pour contenir le sprite 272×176
    private static final int WINDOW_HEIGHT = 300;
    
    // 🧩 Composants principaux
    private ContoursMapView contoursMapView;
    private ContoursGridAdapter gridAdapter;
    
    // 🎮 État du jeu simplifié
    private int playerCol = 1, playerRow = 1;  // Position du joueur en coordonnées grille
    
    @Override
    public void start(Stage primaryStage) {
        System.out.println("🚀 ContoursLauncherExample - Démarrage avec sprite 272×176");
        
        // 🖼️ Créer la vue principale avec sprite + grille
        contoursMapView = new ContoursMapView();
        gridAdapter = new ContoursGridAdapter(contoursMapView);
        
        // 🗺️ Générer une map de test
        generateGameMap();
        
        // 📦 Configuration de la scène (équivalent de votre Launcher actuel)
        StackPane root = new StackPane();
        root.getChildren().add(contoursMapView);
        root.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // ⌨️ Gestion des événements clavier (équivalent de votre handleKeyPressed)
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        
        // 🖥️ Configuration de la fenêtre (similaire à votre Launcher)
        primaryStage.setTitle("🧱 Bomberman ContoursMapView - Sprite 272×176 + Grille 13×11");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Donner le focus pour capturer les touches
        scene.getRoot().requestFocus();
        
        System.out.println("✅ Application lancée - Utilisez les flèches pour déplacer le 'joueur' (bloc bleu)");
        System.out.println("📐 Sprite: " + ContoursMapView.getSpriteWidth() + "×" + ContoursMapView.getSpriteHeight() + " px");
        System.out.println("🧱 Grille: " + ContoursMapView.getGameColumns() + "×" + ContoursMapView.getGameRows() + " tuiles de " + ContoursMapView.getTileSize() + "px");
    }
    
    /**
     * 🗺️ Génère une map de jeu simplifiée (équivalent de votre initializeLevel)
     */
    private void generateGameMap() {
        System.out.println("🗺️ Génération de la map de jeu...");
        
        // Bordures solides (comme dans votre Grid actuel)
        for (int col = 0; col < ContoursMapView.getGameColumns(); col++) {
            gridAdapter.setTile(col, 0, TileType.SOLID);                                    // Ligne du haut
            gridAdapter.setTile(col, ContoursMapView.getGameRows() - 1, TileType.SOLID);    // Ligne du bas
        }
        for (int row = 0; row < ContoursMapView.getGameRows(); row++) {
            gridAdapter.setTile(0, row, TileType.SOLID);                                    // Colonne de gauche
            gridAdapter.setTile(ContoursMapView.getGameColumns() - 1, row, TileType.SOLID); // Colonne de droite
        }
        
        // Quelques blocs destructibles (équivalent de vos blocs destructibles)
        gridAdapter.setTile(3, 2, TileType.DESTRUCTIBLE);
        gridAdapter.setTile(5, 4, TileType.DESTRUCTIBLE);
        gridAdapter.setTile(7, 3, TileType.DESTRUCTIBLE);
        gridAdapter.setTile(9, 6, TileType.DESTRUCTIBLE);
        gridAdapter.setTile(4, 8, TileType.DESTRUCTIBLE);
        
        // Position de départ du joueur (équivalent de PLAYER_START_X, PLAYER_START_Y)
        playerCol = 1;
        playerRow = 1;
        
        // Visualiser la position du joueur avec un bloc bleu
        gridAdapter.setTile(playerCol, playerRow, TileType.EMPTY); // S'assurer que c'est vide
        updatePlayerVisualization();
        
        System.out.println("✅ Map générée - Joueur en position (" + playerCol + "," + playerRow + ")");
    }
    
    /**
     * ⌨️ Gestion des touches (équivalent de votre handleGameInput)
     * @param keyCode Code de la touche pressée
     */
    private void handleKeyPressed(KeyCode keyCode) {
        boolean moved = false;
        
        switch (keyCode) {
            case UP:
                if (tryMovePlayer(0, -1)) {
                    moved = true;
                    System.out.println("⬆️ Joueur déplacé vers le haut → (" + playerCol + "," + playerRow + ")");
                }
                break;
                
            case DOWN:
                if (tryMovePlayer(0, 1)) {
                    moved = true;
                    System.out.println("⬇️ Joueur déplacé vers le bas → (" + playerCol + "," + playerRow + ")");
                }
                break;
                
            case LEFT:
                if (tryMovePlayer(-1, 0)) {
                    moved = true;
                    System.out.println("⬅️ Joueur déplacé vers la gauche → (" + playerCol + "," + playerRow + ")");
                }
                break;
                
            case RIGHT:
                if (tryMovePlayer(1, 0)) {
                    moved = true;
                    System.out.println("➡️ Joueur déplacé vers la droite → (" + playerCol + "," + playerRow + ")");
                }
                break;
                
            case SPACE:
                // Placer une "bombe" (bloc destructible temporaire)
                System.out.println("💣 Bombe posée à (" + playerCol + "," + playerRow + ") !");
                break;
                
            case ESCAPE:
                System.out.println("⏸️ Touche Échap pressée (pause dans votre jeu)");
                break;
                
            default:
                // Ignorer les autres touches
                break;
        }
        
        if (moved) {
            updatePlayerVisualization();
        }
    }
    
    /**
     * 🚶 Tente de déplacer le joueur (équivalent de votre player.moveUp/Down/Left/Right)
     * @param deltaCol Changement de colonne (-1, 0, 1)
     * @param deltaRow Changement de ligne (-1, 0, 1)
     * @return true si le déplacement a réussi
     */
    private boolean tryMovePlayer(int deltaCol, int deltaRow) {
        int newCol = playerCol + deltaCol;
        int newRow = playerRow + deltaRow;
        
        // Vérifier si la nouvelle position est accessible (équivalent de grid.isAccessible)
        if (gridAdapter.isAccessible(newCol, newRow)) {
            // Effacer l'ancienne position
            clearPlayerVisualization();
            
            // Mettre à jour la position
            playerCol = newCol;
            playerRow = newRow;
            
            return true;
        } else {
            System.out.println("❌ Déplacement bloqué vers (" + newCol + "," + newRow + ") - Obstacle");
            return false;
        }
    }
    
    /**
     * 🎨 Met à jour la visualisation du joueur (bloc bleu)
     */
    private void updatePlayerVisualization() {
        // Dans une vraie implémentation, vous ajouteriez un ImageView ou Rectangle
        // pour représenter visuellement le joueur par-dessus la grille
        
        // Pour l'instant, on change temporairement la couleur de la cellule
        // (Dans votre vrai jeu, vous auriez un Player avec getX(), getY(), etc.)
        
        System.out.println("🎨 Visualisation joueur mise à jour : (" + playerCol + "," + playerRow + ")");
        
        // TODO: Ajouter ici le code pour positionner visuellement le joueur
        // Exemple : playerImageView.setTranslateX(playerCol * 16 + 32);
        //          playerImageView.setTranslateY(playerRow * 16);
    }
    
    /**
     * 🧹 Efface la visualisation du joueur de l'ancienne position
     */
    private void clearPlayerVisualization() {
        // Dans une vraie implémentation, vous retireriez la représentation visuelle
        // de l'ancienne position
        
        System.out.println("🧹 Ancienne position joueur effacée");
    }
    
    /**
     * 📊 Adaptateur simple pour connecter ContoursMapView à la logique de grille
     * (Équivalent simplifié de votre classe Grid)
     */
    private static class ContoursGridAdapter {
        private ContoursMapView view;
        private TileType[][] gridData;
        
        public ContoursGridAdapter(ContoursMapView view) {
            this.view = view;
            this.gridData = new TileType[ContoursMapView.getGameColumns()][ContoursMapView.getGameRows()];
            
            // Initialiser toutes les cellules comme vides
            for (int col = 0; col < ContoursMapView.getGameColumns(); col++) {
                for (int row = 0; row < ContoursMapView.getGameRows(); row++) {
                    gridData[col][row] = TileType.EMPTY;
                }
            }
        }
        
        /**
         * Définit le type d'une tuile (équivalent de grid.setTileType)
         */
        public void setTile(int col, int row, TileType type) {
            if (isValidPosition(col, row)) {
                gridData[col][row] = type;
                view.setCellType(col, row, type);
            }
        }
        
        /**
         * Obtient le type d'une tuile (équivalent de grid.getTileType)
         */
        public TileType getTile(int col, int row) {
            if (isValidPosition(col, row)) {
                return gridData[col][row];
            }
            return TileType.SOLID; // Hors limites = solide
        }
        
        /**
         * Vérifie si une position est accessible (équivalent de grid.isAccessible)
         */
        public boolean isAccessible(int col, int row) {
            return getTile(col, row) == TileType.EMPTY;
        }
        
        /**
         * Vérifie si une position est dans les limites de la grille
         */
        private boolean isValidPosition(int col, int row) {
            return col >= 0 && col < ContoursMapView.getGameColumns() && 
                   row >= 0 && row < ContoursMapView.getGameRows();
        }
    }
    
    /**
     * 🚀 Point d'entrée de l'exemple
     */
    public static void main(String[] args) {
        System.out.println("🎮 Lancement de l'exemple ContoursMapView pour Bomberman");
        System.out.println("📏 Dimensions: Sprite 272×176 px + Grille 13×11 tuiles de 16px");
        System.out.println("⌨️ Contrôles: Flèches = Déplacement, Espace = Bombe, Échap = Pause");
        launch(args);
    }
} 