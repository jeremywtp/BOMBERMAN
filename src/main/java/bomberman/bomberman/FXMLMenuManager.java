package bomberman.bomberman;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestionnaire centralisé pour tous les menus FXML du jeu Bomberman.
 */
public class FXMLMenuManager implements 
    MainMenuController.MenuNavigationCallback,
    PauseMenuController.PauseMenuCallback,
    ThemeMenuController.ThemeMenuCallback,
    CommandsController.CommandsCallback {
    
    private Stage primaryStage;
    private Scene gameScene;
    private StackPane gameRoot;
    private Parent pauseMenuOverlay;
    private Parent commandsOverlay;
    private Launcher gameController;
    private ThemeSelector themeSelector;
    
    public FXMLMenuManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            Scene menuScene = new Scene(root, 816, 956);
            primaryStage.setScene(menuScene);
            
            MainMenuController controller = loader.getController();
            controller.setNavigationCallback(this);
            
            System.out.println("Menu principal FXML affiché");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage du menu principal : " + e.getMessage());
        }
    }
    
    public void showPauseMenu() {
        try {
            // Si on n'est pas dans la scène de jeu, utiliser l'ancienne méthode
            if (gameScene == null || gameRoot == null) {
                showPauseMenuFullScreen();
                return;
            }
            
            // Charger le menu pause comme overlay
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PauseMenu.fxml"));
            pauseMenuOverlay = loader.load();
            pauseMenuOverlay.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            // Configurer le contrôleur
            PauseMenuController controller = loader.getController();
            controller.setPauseCallback(this);
            controller.resetSelection();

            // Ajouter l'overlay à la scène de jeu existante
            gameRoot.getChildren().add(pauseMenuOverlay);

            // Demander le focus initial pour la navigation clavier
            // Ceci est crucial pour que les flèches fonctionnent sans avoir à cliquer d'abord
            controller.requestInitialFocus();

            System.out.println("Menu de pause FXML affiché en overlay");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage du menu de pause : " + e.getMessage());
        }
    }
    
    /**
     * Méthode de fallback pour afficher le menu pause en plein écran
     */
    private void showPauseMenuFullScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PauseMenu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            Scene pauseScene = new Scene(root, 816, 956);
            primaryStage.setScene(pauseScene);
            
            PauseMenuController controller = loader.getController();
            controller.setPauseCallback(this);
            controller.resetSelection();
            
            System.out.println("Menu de pause FXML affiché en plein écran");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage du menu de pause : " + e.getMessage());
        }
    }
    
    public void hidePauseMenu() {
        if (pauseMenuOverlay != null && gameRoot != null) {
            gameRoot.getChildren().remove(pauseMenuOverlay);
            pauseMenuOverlay = null;
            System.out.println("Menu de pause FXML masqué");
        }
    }
    
    public void showThemeMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ThemeMenu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            Scene themeScene = new Scene(root, 816, 956);
            primaryStage.setScene(themeScene);
            
            ThemeMenuController controller = loader.getController();
            controller.setThemeCallback(this);
            if (themeSelector != null) {
                controller.setThemeSelector(themeSelector);
            }
            
            System.out.println("Menu des thèmes FXML affiché");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage du menu des thèmes : " + e.getMessage());
        }
    }
    
    public void showCommandsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CommandsScreen.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            Scene commandsScene = new Scene(root, 816, 956);
            primaryStage.setScene(commandsScene);
            
            CommandsController controller = loader.getController();
            controller.setCommandsCallback(this);
            
            System.out.println("Écran des commandes FXML affiché");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage de l'écran des commandes : " + e.getMessage());
        }
    }
    
    public void showCommandsOverlay() {
        try {
            // Si on n'est pas dans la scène de jeu, utiliser l'ancienne méthode
            if (gameScene == null || gameRoot == null) {
                showCommandsScreen();
                return;
            }
            
            // Charger les commandes comme overlay
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CommandsScreen.fxml"));
            commandsOverlay = loader.load();
            commandsOverlay.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            // Configurer le contrôleur
            CommandsController controller = loader.getController();
            controller.setCommandsCallback(this);
            
            // Masquer temporairement le menu pause
            if (pauseMenuOverlay != null) {
                pauseMenuOverlay.setVisible(false);
            }
            
            // Ajouter l'overlay à la scène de jeu existante
            gameRoot.getChildren().add(commandsOverlay);
            
            // S'assurer que les commandes reçoivent le focus pour la navigation clavier
            commandsOverlay.setFocusTraversable(true);
            commandsOverlay.requestFocus();
            
            System.out.println("Commandes FXML affichées en overlay");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage des commandes en overlay : " + e.getMessage());
        }
    }
    
    public void hideCommandsOverlay() {
        if (commandsOverlay != null && gameRoot != null) {
            gameRoot.getChildren().remove(commandsOverlay);
            commandsOverlay = null;
            
            // Réafficher le menu pause
            if (pauseMenuOverlay != null) {
                pauseMenuOverlay.setVisible(true);
                pauseMenuOverlay.requestFocus();
            }
            
            System.out.println("Commandes FXML masquées");
        }
    }
    
    public void returnToGame() {
        // Masquer le menu pause s'il est affiché
        hidePauseMenu();
        
        if (gameScene != null) {
            primaryStage.setScene(gameScene);
            System.out.println("Retour au jeu");
        }
    }
    
    // Setters
    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
        // Extraire le StackPane root de la scène de jeu
        if (gameScene.getRoot() instanceof StackPane) {
            this.gameRoot = (StackPane) gameScene.getRoot();
        }
    }
    
    public void setGameController(Launcher gameController) {
        this.gameController = gameController;
    }
    
    public void setThemeSelector(ThemeSelector themeSelector) {
        this.themeSelector = themeSelector;
    }
    
    // MainMenuController callbacks
    @Override
    public void startNormalGame() {
        if (gameController != null) {
            gameController.startNormalGameFromFXML();
        }
    }
    
    @Override
    public void startCooperationMode() {
        if (gameController != null) {
            gameController.startCooperationModeFromFXML();
        }
    }
    
    @Override
    public void startBattleMode() {
        if (gameController != null) {
            gameController.startBattleModeFromFXML();
        }
    }
    
    @Override
    public void exitApplication() {
        System.exit(0);
    }
    
    // PauseMenuController callbacks
    @Override
    public void resumeGame() {
        if (gameController != null) {
            gameController.resumeGameFromFXML();
        }
    }
    
    @Override
    public void restartGame() {
        if (gameController != null) {
            gameController.restartGameFromFXML();
        }
    }
    
    @Override
    public void showCommands() {
        showCommandsOverlay();
    }
    
    @Override
    public void returnToMainMenu() {
        if (gameController != null) {
            gameController.returnToMainMenuFromFXML();
        }
    }
    
    // ThemeMenuController callbacks
    @Override
    public void confirmThemeSelection(Theme selectedTheme) {
        if (themeSelector != null && selectedTheme != null) {
            themeSelector.setTheme(selectedTheme);
            System.out.println("Thème appliqué : " + selectedTheme.getDisplayName());
        }
        showMainMenu();
    }
    
    @Override
    public void cancelThemeSelection() {
        showMainMenu();
    }
    
    // CommandsController callbacks
    @Override
    public void returnToPreviousMenu() {
        hideCommandsOverlay();
    }
} 