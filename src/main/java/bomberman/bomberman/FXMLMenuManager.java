package bomberman.bomberman;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PauseMenu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/css/menu-styles.css").toExternalForm());
            
            Scene pauseScene = new Scene(root, 816, 956);
            primaryStage.setScene(pauseScene);
            
            PauseMenuController controller = loader.getController();
            controller.setPauseCallback(this);
            controller.resetSelection();
            
            System.out.println("Menu de pause FXML affiché");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage du menu de pause : " + e.getMessage());
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
    
    public void returnToGame() {
        if (gameScene != null) {
            primaryStage.setScene(gameScene);
            System.out.println("Retour au jeu");
        }
    }
    
    // Setters
    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
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
        showCommandsScreen();
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
        showPauseMenu();
    }
} 