package bomberman.bomberman;

import javafx.application.Platform;
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
    
    // ===== ÉLÉMENTS DU MENU PRINCIPAL =====
    @FXML private VBox mainPauseMenu;
    @FXML private Label pauseTitle;
    @FXML private VBox optionsContainer;
    @FXML private Button resumeButton;
    @FXML private Button restartButton;
    @FXML private Button optionsMenuButton;
    @FXML private Button commandsButton;
    @FXML private Button mainMenuButton;
    
    // Références aux flèches de sélection du menu principal
    @FXML private Label resumeArrow;
    @FXML private Label restartArrow;
    @FXML private Label optionsMenuArrow;
    @FXML private Label commandsArrow;
    @FXML private Label mainMenuArrow;
    
    // ===== ÉLÉMENTS DU MENU D'OPTIONS =====
    @FXML private VBox optionsMenuPanel;
    @FXML private Label optionsTitle;
    @FXML private VBox optionsSettingsContainer;
    
    // Contrôles de volume musique
    @FXML private Button musicVolumeDownButton;
    @FXML private Button musicVolumeUpButton;
    @FXML private Label musicVolumeLabel;
    
    // Contrôles de volume effets
    @FXML private Button effectsVolumeDownButton;
    @FXML private Button effectsVolumeUpButton;
    @FXML private Label effectsVolumeLabel;
    
    // Bouton retour du menu d'options
    @FXML private Button optionsBackButton;
    @FXML private Label optionsBackArrow;
    
    // ===== VARIABLES DE NAVIGATION =====
    private List<Button> mainMenuButtons;
    private List<Label> mainMenuArrows;
    private List<Button> optionsButtons;
    private List<Label> optionsArrows;
    
    private int selectedIndex = 0;
    private boolean inOptionsMenu = false;
    
    // ===== VARIABLES DE CONFIGURATION =====
    private int musicVolume = SoundManager.getMusicVolume();
    private int effectsVolume = SoundManager.getEffectsVolume();
    
    // Protection contre les sons répétés
    private long lastNavigationSoundTime = 0;
    private static final long NAVIGATION_SOUND_COOLDOWN = 100; // 100ms entre les sons
    
    // Référence vers l'application principale
    private PauseMenuCallback pauseCallback;
    
    private static final double SELECTED_SCALE = 1.03;
    private static final double DEFAULT_SCALE = 1.0;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtons();
        setupKeyboardNavigation();
        updateButtonStyles();
        updateDisplays();
        
        // Configuration des événements clavier directement sur les conteneurs
        mainPauseMenu.setOnKeyPressed(this::handleKeyPressed);
        optionsMenuPanel.setOnKeyPressed(this::handleKeyPressed);
        
        // S'assurer que les conteneurs peuvent recevoir le focus
        mainPauseMenu.setFocusTraversable(true);
        optionsMenuPanel.setFocusTraversable(true);
    }
    
    /**
     * Demande le focus pour le premier bouton du menu.
     * Doit être appelée après que le menu soit visible à l'écran.
     */
    public void requestInitialFocus() {
        // Platform.runLater s'assure que la demande de focus est traitée au bon moment
        // par le thread de l'interface graphique.
        Platform.runLater(() -> {
            if (inOptionsMenu) {
                optionsMenuPanel.requestFocus();
                System.out.println("Focus initial demandé pour le menu d'options");
            } else {
                mainPauseMenu.requestFocus();
                System.out.println("Focus initial demandé pour le menu principal");
            }
        });
    }
    
    /**
     * Configure les listes de boutons pour la navigation
     */
    private void setupButtons() {
        // Configuration du menu principal
        mainMenuButtons = new ArrayList<>();
        mainMenuButtons.add(resumeButton);
        mainMenuButtons.add(restartButton);
        mainMenuButtons.add(optionsMenuButton);
        mainMenuButtons.add(commandsButton);
        mainMenuButtons.add(mainMenuButton);
        
        mainMenuArrows = new ArrayList<>();
        mainMenuArrows.add(resumeArrow);
        mainMenuArrows.add(restartArrow);
        mainMenuArrows.add(optionsMenuArrow);
        mainMenuArrows.add(commandsArrow);
        mainMenuArrows.add(mainMenuArrow);
        
        // Configuration du menu d'options (simplifié)
        optionsButtons = new ArrayList<>();
        optionsButtons.add(musicVolumeDownButton);
        optionsButtons.add(effectsVolumeDownButton);
        optionsButtons.add(optionsBackButton);
        
        optionsArrows = new ArrayList<>();
        optionsArrows.add(null); // Pas de flèche pour les boutons de volume
        optionsArrows.add(null); // Pas de flèche pour les boutons de volume
        optionsArrows.add(optionsBackArrow);
        
        // Ajouter les effets sonores hover et navigation souris pour le menu principal
        for (int i = 0; i < mainMenuButtons.size(); i++) {
            final int index = i;
            Button button = mainMenuButtons.get(i);
            
            button.setOnMouseEntered(e -> {
                if (!inOptionsMenu) {
                    selectedIndex = index;
                    updateButtonStyles();
                    playNavigationSound();
                }
            });
            
            button.setOnMouseExited(e -> {
                // Optionnel : comportement au survol sortant
            });
        }
        
        // Ajouter les effets sonores hover et navigation souris pour le menu d'options
        for (int i = 0; i < optionsButtons.size(); i++) {
            final int index = i;
            Button button = optionsButtons.get(i);
            
            button.setOnMouseEntered(e -> {
                if (inOptionsMenu) {
                    selectedIndex = index;
                    updateButtonStyles();
                    playNavigationSound();
                }
            });
            
            button.setOnMouseExited(e -> {
                // Optionnel : comportement au survol sortant
            });
        }
        
        // Ajouter les événements souris pour les boutons d'augmentation de volume (non navigables mais avec son)
        musicVolumeUpButton.setOnMouseEntered(e -> playNavigationSound());
        effectsVolumeUpButton.setOnMouseEntered(e -> playNavigationSound());
    }
    
    /**
     * Configure la navigation clavier
     */
    private void setupKeyboardNavigation() {
        // La navigation clavier est gérée globalement sur le panneau
        // lorsque le menu a le focus. Les événements sont attrapés par onKeyPressed
        // dans le FXML (<VBox ... onKeyPressed="#handleKeyPressed">)
    }
    
    /**
     * Gère les événements clavier pour la navigation
     */
    @FXML
    public void handleKeyPressed(KeyEvent event) {
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
                
            case LEFT:
            case Q: // Support clavier AZERTY
                if (inOptionsMenu) {
                    handleLeftInOptions();
                }
                event.consume();
                break;
                
            case RIGHT:
            case D: // Support clavier AZERTY
                if (inOptionsMenu) {
                    handleRightInOptions();
                }
                event.consume();
                break;
                
            case ENTER:
            case SPACE:
                executeSelectedAction();
                event.consume();
                break;
                
            case ESCAPE:
                if (inOptionsMenu) {
                    backToMainPauseMenu();
                } else {
                    resumeGame();
                }
                event.consume();
                break;
        }
    }
    
    private void handleLeftInOptions() {
        if (selectedIndex == 0) { // Volume musique
            decreaseMusicVolume();
        } else if (selectedIndex == 1) { // Volume effets
            decreaseEffectsVolume();
        }
    }
    
    private void handleRightInOptions() {
        if (selectedIndex == 0) { // Volume musique
            increaseMusicVolume();
        } else if (selectedIndex == 1) { // Volume effets
            increaseEffectsVolume();
        }
    }
    
    /**
     * Navigue vers l'option précédente (haut)
     */
    private void navigateUp() {
        List<Button> currentButtons = inOptionsMenu ? optionsButtons : mainMenuButtons;
        selectedIndex = (selectedIndex - 1 + currentButtons.size()) % currentButtons.size();
        updateButtonStyles();
        playNavigationSound();
        System.out.println((inOptionsMenu ? "Menu options" : "Menu pause") + " - Navigation vers le haut : " + selectedIndex);
    }
    
    /**
     * Navigue vers l'option suivante (bas)
     */
    private void navigateDown() {
        List<Button> currentButtons = inOptionsMenu ? optionsButtons : mainMenuButtons;
        selectedIndex = (selectedIndex + 1) % currentButtons.size();
        updateButtonStyles();
        playNavigationSound();
        System.out.println((inOptionsMenu ? "Menu options" : "Menu pause") + " - Navigation vers le bas : " + selectedIndex);
    }
    
    /**
     * Met à jour les styles des boutons et la visibilité des flèches
     */
    private void updateButtonStyles() {
        List<Button> currentButtons = inOptionsMenu ? optionsButtons : mainMenuButtons;
        List<Label> currentArrows = inOptionsMenu ? optionsArrows : mainMenuArrows;
        
        for (int i = 0; i < currentButtons.size(); i++) {
            Button button = currentButtons.get(i);
            Label arrow = currentArrows.get(i);
            
            if (i == selectedIndex) {
                // Visuel sélectionné (échelle + couleur focus en ajoutant pseudo classe manually)
                button.setScaleX(SELECTED_SCALE);
                button.setScaleY(SELECTED_SCALE);
                button.getStyleClass().remove("selected-keyboard");
                button.getStyleClass().add("selected-keyboard");
                if (arrow != null) {
                    arrow.setVisible(true);
                }
            } else {
                button.setScaleX(DEFAULT_SCALE);
                button.setScaleY(DEFAULT_SCALE);
                button.getStyleClass().remove("selected-keyboard");
                if (arrow != null) {
                    arrow.setVisible(false);
                }
            }
        }
    }
    
    /**
     * Met à jour les affichages des valeurs
     */
    private void updateDisplays() {
        musicVolumeLabel.setText(musicVolume + "%");
        effectsVolumeLabel.setText(effectsVolume + "%");
    }
    
    /**
     * Exécute l'action correspondant à l'option sélectionnée
     */
    private void executeSelectedAction() {
        if (inOptionsMenu) {
            Button selectedButton = optionsButtons.get(selectedIndex);
            playSelectionSound();
            selectedButton.fire();
        } else {
            Button selectedButton = mainMenuButtons.get(selectedIndex);
            playSelectionSound();
            selectedButton.fire();
        }
    }
    
    /**
     * Joue le son de navigation si disponible
     */
    private void playNavigationSound() {
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastNavigationSoundTime > NAVIGATION_SOUND_COOLDOWN) {
                SoundManager.playEffect("menu_cursor");
                lastNavigationSoundTime = currentTime;
            }
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
        inOptionsMenu = false;
        mainPauseMenu.setVisible(true);
        optionsMenuPanel.setVisible(false);
        
        // Synchroniser les valeurs avec SoundManager
        musicVolume = SoundManager.getMusicVolume();
        effectsVolume = SoundManager.getEffectsVolume();
        
        updateButtonStyles();
        updateDisplays();
        
        // S'assurer que le focus est sur le conteneur principal
        Platform.runLater(() -> mainPauseMenu.requestFocus());
    }
    
    // ===== ACTIONS DU MENU PRINCIPAL =====
    
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
    private void showOptionsMenu() {
        playSelectionSound();
        System.out.println("Affichage du menu d'options");
        inOptionsMenu = true;
        selectedIndex = 0;
        mainPauseMenu.setVisible(false);
        optionsMenuPanel.setVisible(true);
        updateButtonStyles();
        updateDisplays();
        Platform.runLater(() -> optionsMenuPanel.requestFocus());
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
    
    // ===== ACTIONS DU MENU D'OPTIONS =====
    
    @FXML
    private void decreaseMusicVolume() {
        if (musicVolume > 0) {
            musicVolume = Math.max(0, musicVolume - 10);
            SoundManager.setMusicVolume(musicVolume);
            updateDisplays();
            playNavigationSound();
            System.out.println("Volume musique : " + musicVolume + "%");
        }
    }
    
    @FXML
    private void increaseMusicVolume() {
        if (musicVolume < 100) {
            musicVolume = Math.min(100, musicVolume + 10);
            SoundManager.setMusicVolume(musicVolume);
            updateDisplays();
            playNavigationSound();
            System.out.println("Volume musique : " + musicVolume + "%");
        }
    }
    
    @FXML
    private void decreaseEffectsVolume() {
        if (effectsVolume > 0) {
            effectsVolume = Math.max(0, effectsVolume - 10);
            SoundManager.setEffectsVolume(effectsVolume);
            updateDisplays();
            playNavigationSound();
            System.out.println("Volume effets : " + effectsVolume + "%");
        }
    }
    
    @FXML
    private void increaseEffectsVolume() {
        if (effectsVolume < 100) {
            effectsVolume = Math.min(100, effectsVolume + 10);
            SoundManager.setEffectsVolume(effectsVolume);
            updateDisplays();
            playNavigationSound();
            System.out.println("Volume effets : " + effectsVolume + "%");
        }
    }
    
    @FXML
    private void backToMainPauseMenu() {
        playSelectionSound();
        System.out.println("Retour au menu pause principal");
        inOptionsMenu = false;
        selectedIndex = 2; // Retourner sur le bouton "OPTIONS"
        mainPauseMenu.setVisible(true);
        optionsMenuPanel.setVisible(false);
        updateButtonStyles();
        Platform.runLater(() -> mainPauseMenu.requestFocus());
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