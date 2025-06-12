package bomberman.bomberman;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour le menu de pause du jeu Bomberman.
 * Gère la navigation par clavier et les actions du menu pause.
 */
public class PauseMenuController implements Initializable {
    
    @FXML private Label pauseTitle;
    @FXML private VBox optionsContainer;
    @FXML private Button resumeButton;
    @FXML private Button restartButton;
    @FXML private Button commandsButton;
    @FXML private Button mainMenuButton;
    
    // Références aux flèches de sélection
    @FXML private Label resumeArrow;
    @FXML private Label restartArrow;
    @FXML private Label commandsArrow;
    @FXML private Label mainMenuArrow;
    
    // Liste des boutons pour la navigation clavier
    private List<Button> pauseButtons;
    private List<Label> pauseArrows;
    private int selectedIndex = 0;
    
    // Référence vers l'application principale
    private PauseMenuCallback pauseCallback;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPauseButtons();
        setupKeyboardNavigation();
        updateButtonStyles();
    }
    
    /**
     * Configure la liste des boutons du menu pause
     */
    private void setupPauseButtons() {
        pauseButtons = new ArrayList<>();
        pauseButtons.add(resumeButton);
        pauseButtons.add(restartButton);
        pauseButtons.add(commandsButton);
        pauseButtons.add(mainMenuButton);
        
        pauseArrows = new ArrayList<>();
        pauseArrows.add(resumeArrow);
        pauseArrows.add(restartArrow);
        pauseArrows.add(commandsArrow);
        pauseArrows.add(mainMenuArrow);
        
        // Ajouter les effets sonores hover pour tous les boutons
        for (Button button : pauseButtons) {
            button.setOnMouseEntered(e -> playNavigationSound());
        }
    }
    
    /**
     * Configure la navigation clavier pour le menu pause
     */
    private void setupKeyboardNavigation() {
        optionsContainer.setOnKeyPressed(this::handleKeyPressed);
        optionsContainer.setFocusTraversable(true);
        
        // Ajouter également les listeners sur chaque bouton pour s'assurer de capturer les événements
        for (Button button : pauseButtons) {
            button.setOnKeyPressed(this::handleKeyPressed);
        }
        
        optionsContainer.requestFocus();
        
        // Également essayer de donner le focus au premier bouton
        if (!pauseButtons.isEmpty()) {
            pauseButtons.get(selectedIndex).requestFocus();
        }
    }
    
    /**
     * Gère les événements clavier pour la navigation dans le menu pause
     */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        
        switch (keyCode) {
            case UP:
            case Z: // Support clavier AZERTY
                navigateUp();
                event.consume();
                break;
                
            case DOWN:
            case S: // Support clavier AZERTY
                navigateDown();
                event.consume();
                break;
                
            case ENTER:
            case SPACE:
                executeSelectedAction();
                event.consume();
                break;
                
            case ESCAPE:
                // Reprendre le jeu (équivalent à "REPRENDRE")
                resumeGame();
                event.consume();
                break;
        }
    }
    
    /**
     * Navigue vers l'option précédente (haut)
     */
    private void navigateUp() {
        selectedIndex = (selectedIndex - 1 + pauseButtons.size()) % pauseButtons.size();
        updateButtonStyles();
        playNavigationSound();
        System.out.println("Menu pause - Navigation vers le haut : " + selectedIndex);
    }
    
    /**
     * Navigue vers l'option suivante (bas)
     */
    private void navigateDown() {
        selectedIndex = (selectedIndex + 1) % pauseButtons.size();
        updateButtonStyles();
        playNavigationSound();
        System.out.println("Menu pause - Navigation vers le bas : " + selectedIndex);
    }
    
    /**
     * Met à jour les styles des boutons et la visibilité des flèches pour indiquer la sélection
     */
    private void updateButtonStyles() {
        for (int i = 0; i < pauseButtons.size(); i++) {
            Button button = pauseButtons.get(i);
            Label arrow = pauseArrows.get(i);
            
            if (i == selectedIndex) {
                button.requestFocus();
                arrow.setVisible(true);
                // Le style focus est géré par CSS
            } else {
                arrow.setVisible(false);
            }
        }
    }
    
    /**
     * Exécute l'action correspondant à l'option sélectionnée
     */
    private void executeSelectedAction() {
        Button selectedButton = pauseButtons.get(selectedIndex);
        playSelectionSound();
        selectedButton.fire(); // Déclenche l'action du bouton
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
     * Définit le callback pour les actions du menu pause
     */
    public void setPauseCallback(PauseMenuCallback callback) {
        this.pauseCallback = callback;
    }
    
    /**
     * Remet la sélection à zéro (première option)
     */
    public void resetSelection() {
        selectedIndex = 0;
        updateButtonStyles();
    }
    
    // ===== ACTIONS DES BOUTONS =====
    
    @FXML
    private void resumeGame() {
        playSelectionSound();
        System.out.println("Reprise du jeu");
        if (pauseCallback != null) {
            pauseCallback.resumeGame();
        }
    }
    
    @FXML
    private void restartGame() {
        playSelectionSound();
        System.out.println("Redémarrage du jeu");
        if (pauseCallback != null) {
            pauseCallback.restartGame();
        }
    }
    
    @FXML
    private void showCommands() {
        playSelectionSound();
        System.out.println("Affichage des commandes");
        if (pauseCallback != null) {
            pauseCallback.showCommands();
        }
    }
    
    @FXML
    private void returnToMainMenu() {
        playSelectionSound();
        System.out.println("Retour au menu principal");
        if (pauseCallback != null) {
            pauseCallback.returnToMainMenu();
        }
    }
    
    /**
     * Interface pour communiquer avec l'application principale
     */
    public interface PauseMenuCallback {
        void resumeGame();
        void restartGame();
        void showCommands();
        void returnToMainMenu();
    }
} 