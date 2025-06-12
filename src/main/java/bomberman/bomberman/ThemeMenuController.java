package bomberman.bomberman;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour le menu de sélection des thèmes du jeu Bomberman.
 * Gère la navigation par clavier et l'aperçu visuel des thèmes.
 */
public class ThemeMenuController implements Initializable {
    
    @FXML private Label themeTitle;
    @FXML private VBox themePreview;
    @FXML private Label currentThemeName;
    @FXML private Button previousThemeButton;
    @FXML private Button nextThemeButton;
    @FXML private Rectangle primaryColorBox;
    @FXML private Rectangle secondaryColorBox;
    @FXML private Rectangle accentColorBox;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private Label instructionsLabel;
    
    // Référence vers le gestionnaire de thèmes
    private ThemeSelector themeSelector;
    private ThemeMenuCallback themeCallback;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupKeyboardNavigation();
        initializeThemeSelector();
        updateThemePreview();
    }
    
    /**
     * Initialise le gestionnaire de thèmes
     */
    private void initializeThemeSelector() {
        themeSelector = new ThemeSelector();
        updateThemePreview();
    }
    
    /**
     * Configure la navigation clavier pour le menu des thèmes
     */
    private void setupKeyboardNavigation() {
        themePreview.setOnKeyPressed(this::handleKeyPressed);
        themePreview.setFocusTraversable(true);
        themePreview.requestFocus();
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
                
            case ENTER:
            case SPACE:
                confirmTheme();
                event.consume();
                break;
                
            case ESCAPE:
                cancelTheme();
                event.consume();
                break;
        }
    }
    
    /**
     * Met à jour l'aperçu visuel du thème actuel
     */
    private void updateThemePreview() {
        if (themeSelector == null) return;
        
        Theme currentTheme = themeSelector.getCurrentTheme();
        
        // Mettre à jour le nom du thème
        currentThemeName.setText(currentTheme.getDisplayName());
        
        // Mettre à jour les couleurs d'aperçu
        updateColorBoxes(currentTheme);
        
        System.out.println("Aperçu du thème mis à jour : " + currentTheme.getDisplayName());
    }
    
    /**
     * Met à jour les rectangles de couleur pour l'aperçu
     */
    private void updateColorBoxes(Theme theme) {
        try {
            // Couleur principale
            primaryColorBox.setFill(Color.web(theme.getPrimaryColor()));
            primaryColorBox.setStroke(Color.web("#333333"));
            
            // Couleur secondaire
            secondaryColorBox.setFill(Color.web(theme.getSecondaryColor()));
            secondaryColorBox.setStroke(Color.web("#333333"));
            
            // Couleur d'accent
            accentColorBox.setFill(Color.web(theme.getAccentColor()));
            accentColorBox.setStroke(Color.web("#333333"));
            
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour des couleurs : " + e.getMessage());
            // Couleurs par défaut en cas d'erreur
            primaryColorBox.setFill(Color.web("#FF6B35"));
            secondaryColorBox.setFill(Color.web("#004E89"));
            accentColorBox.setFill(Color.web("#FFD23F"));
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
     * Définit le callback pour les actions du menu des thèmes
     */
    public void setThemeCallback(ThemeMenuCallback callback) {
        this.themeCallback = callback;
    }
    
    /**
     * Définit le gestionnaire de thèmes à utiliser
     */
    public void setThemeSelector(ThemeSelector selector) {
        this.themeSelector = selector;
        updateThemePreview();
    }
    
    // ===== ACTIONS DES BOUTONS =====
    
    @FXML
    private void previousTheme() {
        if (themeSelector != null) {
            themeSelector.previousTheme();
            updateThemePreview();
            playNavigationSound();
            System.out.println("Thème précédent sélectionné");
        }
    }
    
    @FXML
    private void nextTheme() {
        if (themeSelector != null) {
            themeSelector.nextTheme();
            updateThemePreview();
            playNavigationSound();
            System.out.println("Thème suivant sélectionné");
        }
    }
    
    @FXML
    private void confirmTheme() {
        playSelectionSound();
        System.out.println("Confirmation du thème : " + 
            (themeSelector != null ? themeSelector.getCurrentTheme().getDisplayName() : "Aucun"));
        
        if (themeCallback != null) {
            themeCallback.confirmThemeSelection(themeSelector != null ? themeSelector.getCurrentTheme() : null);
        }
    }
    
    @FXML
    private void cancelTheme() {
        playSelectionSound();
        System.out.println("Annulation de la sélection de thème");
        
        if (themeCallback != null) {
            themeCallback.cancelThemeSelection();
        }
    }
    
    /**
     * Interface pour communiquer avec l'application principale
     */
    public interface ThemeMenuCallback {
        void confirmThemeSelection(Theme selectedTheme);
        void cancelThemeSelection();
    }
} 