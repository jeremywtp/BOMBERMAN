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
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour le menu de sélection des thèmes du jeu Bomberman.
 * Gère la navigation par clavier entre les thèmes.
 */
public class ThemeMenuController implements Initializable {
    
    @FXML private Label themeTitle;
    @FXML private VBox themePreview;
    @FXML private Label currentThemeName;
    @FXML private ImageView themePreviewImage;
    @FXML private Button previousThemeButton;
    @FXML private Button nextThemeButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private Label instructionsLabel;
    
    // Références aux flèches de sélection
    @FXML private Label confirmArrow;
    @FXML private Label cancelArrow;
    
    // Référence vers le gestionnaire de thèmes
    private ThemeSelector themeSelector;
    private ThemeMenuCallback themeCallback;
    
    // Navigation simple
    private int selectedButtonIndex = 0; // 0 = Confirmer, 1 = Annuler
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupKeyboardNavigation();
        setupButtonSounds();
        initializeThemeSelector();
        updateThemeDisplay();
        updateButtons();
    }
    
    /**
     * Initialise le gestionnaire de thèmes
     */
    private void initializeThemeSelector() {
        themeSelector = new ThemeSelector();
        updateThemeDisplay();
    }
    
    /**
     * Configure la navigation clavier pour le menu des thèmes
     */
    private void setupKeyboardNavigation() {
        themePreview.setOnKeyPressed(this::handleKeyPressed);
        themePreview.setFocusTraversable(true);
        
        // Ajouter également les listeners sur chaque bouton
        previousThemeButton.setOnKeyPressed(this::handleKeyPressed);
        nextThemeButton.setOnKeyPressed(this::handleKeyPressed);
        confirmButton.setOnKeyPressed(this::handleKeyPressed);
        cancelButton.setOnKeyPressed(this::handleKeyPressed);
        
        themePreview.requestFocus();
    }
    
    /**
     * Configure les effets sonores des boutons
     */
    private void setupButtonSounds() {
        previousThemeButton.setOnMouseEntered(e -> playNavigationSound());
        nextThemeButton.setOnMouseEntered(e -> playNavigationSound());
        confirmButton.setOnMouseEntered(e -> playNavigationSound());
        cancelButton.setOnMouseEntered(e -> playNavigationSound());
    }
    
    /**
     * Gère les événements clavier pour la navigation dans le menu des thèmes
     */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        
        switch (keyCode) {
            case LEFT:
            case Q: // Support clavier AZERTY
                previousTheme();
                event.consume();
                break;
                
            case RIGHT:
            case D: // Support clavier AZERTY
                nextTheme();
                event.consume();
                break;
                
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
                cancelTheme();
                event.consume();
                break;
        }
    }
    
    /**
     * Navigation vers le bouton précédent
     */
    private void navigateUp() {
        selectedButtonIndex = (selectedButtonIndex - 1 + 2) % 2;
        updateButtons();
        playNavigationSound();
    }
    
    /**
     * Navigation vers le bouton suivant
     */
    private void navigateDown() {
        selectedButtonIndex = (selectedButtonIndex + 1) % 2;
        updateButtons();
        playNavigationSound();
    }
    
    /**
     * Met à jour la visibilité des flèches pour les boutons d'action
     */
    private void updateButtons() {
        // Réinitialiser toutes les flèches
        confirmArrow.setVisible(false);
        cancelArrow.setVisible(false);
        
        // Afficher la flèche pour le bouton sélectionné
        if (selectedButtonIndex == 0) {
            confirmArrow.setVisible(true);
            confirmButton.requestFocus();
        } else {
            cancelArrow.setVisible(true);
            cancelButton.requestFocus();
        }
    }
    
    /**
     * Exécute l'action du bouton sélectionné
     */
    private void executeSelectedAction() {
        if (selectedButtonIndex == 0) {
            confirmTheme();
        } else {
            cancelTheme();
        }
    }
    
    /**
     * Met à jour l'affichage du thème actuel
     */
    private void updateThemeDisplay() {
        if (themeSelector != null && currentThemeName != null) {
            Theme currentTheme = themeSelector.getCurrentTheme();
            currentThemeName.setText(currentTheme.name());
            updateThemePreviewImage(currentTheme);
        }
    }
    
    /**
     * Met à jour l'image d'aperçu du thème
     */
    private void updateThemePreviewImage(Theme theme) {
        if (themePreviewImage == null) return;
        
        try {
            String imagePath = getThemePreviewImagePath(theme);
            Image previewImage = new Image(getClass().getResourceAsStream(imagePath));
            themePreviewImage.setImage(previewImage);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image d'aperçu pour " + theme.name() + ": " + e.getMessage());
            // Image par défaut en cas d'erreur
            themePreviewImage.setImage(null);
        }
    }
    
    /**
     * Retourne le chemin de l'image d'aperçu pour un thème donné
     */
    private String getThemePreviewImagePath(Theme theme) {
        switch (theme) {
            case BOMBERMAN:
                return "/images/theme-previews/bomberman-preview.png";
            case POKEMON:
                return "/images/theme-previews/pokemon-preview.png";
            default:
                return "/images/theme-previews/bomberman-preview.png";
        }
    }
    

    
    /**
     * Joue le son de navigation
     */
    private void playNavigationSound() {
        try {
            SoundManager.playEffect("menu_cursor");
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du son de navigation: " + e.getMessage());
        }
    }
    
    /**
     * Joue le son de sélection
     */
    private void playSelectionSound() {
        try {
            SoundManager.playEffect("menu_select");
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du son de sélection: " + e.getMessage());
        }
    }
    
    /**
     * Définit le callback pour les actions du menu des thèmes
     */
    public void setThemeCallback(ThemeMenuCallback callback) {
        this.themeCallback = callback;
    }
    
    /**
     * Définit le gestionnaire de thèmes
     */
    public void setThemeSelector(ThemeSelector selector) {
        this.themeSelector = selector;
        updateThemeDisplay();
    }
    
    /**
     * Action pour passer au thème précédent
     */
    @FXML
    private void previousTheme() {
        if (themeSelector != null) {
            themeSelector.previousTheme();
            updateThemeDisplay();
            playNavigationSound();
        }
    }
    
    /**
     * Action pour passer au thème suivant
     */
    @FXML
    private void nextTheme() {
        if (themeSelector != null) {
            themeSelector.nextTheme();
            updateThemeDisplay();
            playNavigationSound();
        }
    }
    
    /**
     * Action pour confirmer la sélection du thème
     */
    @FXML
    private void confirmTheme() {
        playSelectionSound();
        if (themeCallback != null && themeSelector != null) {
            themeCallback.confirmThemeSelection(themeSelector.getCurrentTheme());
        }
    }
    
    /**
     * Action pour annuler la sélection du thème
     */
    @FXML
    private void cancelTheme() {
        playSelectionSound();
        if (themeCallback != null) {
            themeCallback.cancelThemeSelection();
        }
    }
    
    /**
     * Interface de callback pour communiquer avec le menu principal
     */
    public interface ThemeMenuCallback {
        void confirmThemeSelection(Theme selectedTheme);
        void cancelThemeSelection();
    }
} 