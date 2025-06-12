package bomberman.bomberman;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour le menu principal du jeu Bomberman.
 * Gère la navigation par clavier et les actions des boutons.
 * Supporte la navigation clavier traditionnelle et les clics de souris.
 */
public class MainMenuController implements Initializable {
    
    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private VBox menuContainer;
    @FXML private Button normalGameButton;
    @FXML private Button cooperationButton;
    @FXML private Button battleModeButton;
    @FXML private Button themesButton;
    @FXML private Button passwordButton;
    @FXML private Label instructionsLabel;
    
    // Références aux flèches de sélection
    @FXML private Label normalGameArrow;
    @FXML private Label cooperationArrow;
    @FXML private Label battleModeArrow;
    @FXML private Label themesArrow;
    @FXML private Label passwordArrow;
    
    // Liste des boutons pour la navigation clavier
    private List<Button> menuButtons;
    private List<Label> menuArrows;
    private int selectedIndex = 0;
    
    // Référence vers l'application principale
    private MenuNavigationCallback navigationCallback;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupMenuButtons();
        loadBackgroundImage();
        setupKeyboardNavigation();
        updateButtonStyles();
    }
    
    /**
     * Configure la liste des boutons et leur état initial
     */
    private void setupMenuButtons() {
        menuButtons = new ArrayList<>();
        menuButtons.add(normalGameButton);
        menuButtons.add(cooperationButton);
        menuButtons.add(battleModeButton);
        menuButtons.add(themesButton);
        menuButtons.add(passwordButton);
        
        menuArrows = new ArrayList<>();
        menuArrows.add(normalGameArrow);
        menuArrows.add(cooperationArrow);
        menuArrows.add(battleModeArrow);
        menuArrows.add(themesArrow);
        menuArrows.add(passwordArrow);
        
        // Désactiver le bouton PASSWORD par défaut
        passwordButton.setDisable(true);
        
        // Ajouter les effets sonores hover pour tous les boutons actifs
        for (Button button : menuButtons) {
            if (!button.isDisabled()) {
                button.setOnMouseEntered(e -> playNavigationSound());
            }
        }
    }
    
    /**
     * Charge l'image de fond d'intro
     */
    private void loadBackgroundImage() {
        try {
            // Charger l'image d'intro depuis les ressources
            Image intro = new Image(getClass().getResourceAsStream("/images/intro.png"));
            if (intro != null) {
                backgroundImage.setImage(intro);
                backgroundImage.setPreserveRatio(true);
                backgroundImage.setSmooth(true);
            }
        } catch (Exception e) {
            System.out.println("Impossible de charger l'image d'intro : " + e.getMessage());
        }
    }
    
    /**
     * Configure la navigation clavier pour le menu
     */
    private void setupKeyboardNavigation() {
        // Configurer les événements clavier sur le conteneur principal ET sur chaque bouton
        menuContainer.setOnKeyPressed(this::handleKeyPressed);
        menuContainer.setFocusTraversable(true);
        
        // Ajouter également les listeners sur chaque bouton pour s'assurer de capturer les événements
        for (Button button : menuButtons) {
            button.setOnKeyPressed(this::handleKeyPressed);
        }
        
        // S'assurer que le conteneur ait le focus initial
        menuContainer.requestFocus();
        
        // Également essayer de donner le focus au premier bouton
        if (!menuButtons.isEmpty()) {
            menuButtons.get(selectedIndex).requestFocus();
        }
    }
    
    /**
     * Gère les événements clavier pour la navigation dans le menu
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
                // Quitter l'application (ou retour si dans un sous-menu)
                if (navigationCallback != null) {
                    navigationCallback.exitApplication();
                }
                event.consume();
                break;
        }
    }
    
    /**
     * Navigue vers l'option précédente (haut)
     */
    private void navigateUp() {
        do {
            selectedIndex = (selectedIndex - 1 + menuButtons.size()) % menuButtons.size();
        } while (menuButtons.get(selectedIndex).isDisabled());
        
        updateButtonStyles();
        playNavigationSound();
        System.out.println("Navigation vers le haut - Option sélectionnée : " + selectedIndex);
    }
    
    /**
     * Navigue vers l'option suivante (bas)
     */
    private void navigateDown() {
        do {
            selectedIndex = (selectedIndex + 1) % menuButtons.size();
        } while (menuButtons.get(selectedIndex).isDisabled());
        
        updateButtonStyles();
        playNavigationSound();
        System.out.println("Navigation vers le bas - Option sélectionnée : " + selectedIndex);
    }
    
    /**
     * Met à jour les styles des boutons et la visibilité des flèches pour indiquer la sélection
     */
    private void updateButtonStyles() {
        for (int i = 0; i < menuButtons.size(); i++) {
            Button button = menuButtons.get(i);
            Label arrow = menuArrows.get(i);
            
            if (i == selectedIndex && !button.isDisabled()) {
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
        Button selectedButton = menuButtons.get(selectedIndex);
        if (!selectedButton.isDisabled()) {
            playSelectionSound();
            selectedButton.fire(); // Déclenche l'action du bouton
        }
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
     * Définit le callback pour la navigation entre les menus
     */
    public void setNavigationCallback(MenuNavigationCallback callback) {
        this.navigationCallback = callback;
    }
    
    // ===== ACTIONS DES BOUTONS =====
    
    @FXML
    private void startNormalGame() {
        playSelectionSound();
        System.out.println("Démarrage du jeu normal");
        if (navigationCallback != null) {
            navigationCallback.startNormalGame();
        }
    }
    
    @FXML
    private void startCooperation() {
        playSelectionSound();
        System.out.println("Démarrage du mode coopération");
        if (navigationCallback != null) {
            navigationCallback.startCooperationMode();
        }
    }
    
    @FXML
    private void startBattleMode() {
        playSelectionSound();
        System.out.println("Démarrage du mode bataille");
        if (navigationCallback != null) {
            navigationCallback.startBattleMode();
        }
    }
    
    @FXML
    private void showThemes() {
        playSelectionSound();
        System.out.println("Affichage du menu des thèmes");
        if (navigationCallback != null) {
            navigationCallback.showThemeMenu();
        }
    }
    
    @FXML
    private void showPassword() {
        System.out.println("Affichage du menu password (non implémenté)");
        // Fonctionnalité désactivée pour le moment
    }
    
    /**
     * Interface pour communiquer avec l'application principale
     */
    public interface MenuNavigationCallback {
        void startNormalGame();
        void startCooperationMode();
        void startBattleMode();
        void showThemeMenu();
        void exitApplication();
    }
}
