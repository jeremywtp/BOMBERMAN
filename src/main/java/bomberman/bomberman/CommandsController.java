package bomberman.bomberman;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour l'écran des commandes du jeu Bomberman.
 * Affiche les contrôles pour tous les joueurs et gère le retour au menu précédent.
 */
public class CommandsController implements Initializable {
    
    @FXML private Label commandsTitle;
    @FXML private VBox commandsList;
    @FXML private Button returnButton;
    
    // Référence vers l'application principale
    private CommandsCallback commandsCallback;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupKeyboardNavigation();
        setupButtonSounds();
        // Le bouton retour est automatiquement focusé
        returnButton.requestFocus();
    }
    
    /**
     * Configure la navigation clavier pour l'écran des commandes
     */
    private void setupKeyboardNavigation() {
        returnButton.setOnKeyPressed(this::handleKeyPressed);
        returnButton.setFocusTraversable(true);
        
        // S'assurer que le bouton a bien le focus pour capturer les événements clavier
        returnButton.requestFocus();
    }
    
    /**
     * Configure les effets sonores des boutons
     */
    private void setupButtonSounds() {
        returnButton.setOnMouseEntered(e -> playNavigationSound());
    }
    
    /**
     * Joue le son de navigation si disponible
     */
    private void playNavigationSound() {
        try {
            SoundManager.playEffect("menu_cursor");
        } catch (Exception e) {
            // Son non disponible, continuer sans erreur
        }
    }
    
    /**
     * Gère les événements clavier pour l'écran des commandes
     */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        
        switch (keyCode) {
            case ENTER:
            case SPACE:
            case ESCAPE:
                returnToPreviousMenu();
                event.consume();
                break;
        }
    }
    
    /**
     * Joue le son de sélection si disponible
     */
    private void playSelectionSound() {
        try {
            SoundManager.playEffect("menu_select");
        } catch (Exception e) {
            // Son non disponible, continuer sans erreur
        }
    }
    
    /**
     * Définit le callback pour les actions de l'écran des commandes
     */
    public void setCommandsCallback(CommandsCallback callback) {
        this.commandsCallback = callback;
    }
    
    // ===== ACTIONS DU BOUTON =====
    
    @FXML
    private void returnToPreviousMenu() {
        playSelectionSound();
        System.out.println("Retour au menu précédent depuis l'écran des commandes");
        
        if (commandsCallback != null) {
            commandsCallback.returnToPreviousMenu();
        }
    }
    
    /**
     * Interface pour communiquer avec l'application principale
     */
    public interface CommandsCallback {
        void returnToPreviousMenu();
    }
} 