package bomberman.bomberman;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le menu de gestion des profils
 */
public class ProfileMenuController implements Initializable {
    
    // Interface de callback pour retourner au menu principal
    public interface ProfileMenuCallback {
        void onBackToMainMenu();
    }
    
    // Éléments FXML
    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private Label globalStatsLabel;
    @FXML private ImageView currentPlayerAvatar;
    @FXML private Label currentPlayerName;
    @FXML private Label currentPlayerStats;
    @FXML private ListView<PlayerProfile> profilesList;
    @FXML private Button selectProfileButton;
    @FXML private Button createProfileButton;
    @FXML private Button editProfileButton;
    @FXML private Button deleteProfileButton;
    @FXML private Button backButton;
    @FXML private Label instructionsLabel;
    
    // Dialog de création/modification
    @FXML private VBox profileDialog;
    @FXML private Label dialogTitle;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> avatarComboBox;
    @FXML private ImageView avatarPreview;
    @FXML private Button saveProfileButton;
    @FXML private Button cancelProfileButton;
    
    // Variables
    private ProfileManager profileManager;
    private ProfileMenuCallback callback;
    private PlayerProfile editingProfile; // Profil en cours de modification
    private List<String> availableAvatars;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        profileManager = ProfileManager.getInstance();
        setupAvatars();
        setupUI();
        refreshProfilesList();
        updateCurrentPlayerDisplay();
        updateGlobalStats();
    }
    
    /**
     * Configure la liste des avatars disponibles
     */
    private void setupAvatars() {
        availableAvatars = new ArrayList<>();
        availableAvatars.add("Pokemon");
        availableAvatars.add("Bomberman");
        
        // Remplir la ComboBox
        if (avatarComboBox != null) {
            avatarComboBox.setItems(FXCollections.observableArrayList(availableAvatars));
            avatarComboBox.getSelectionModel().selectFirst();
            
            // Listener pour mettre à jour l'aperçu
            avatarComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateAvatarPreview(newVal)
            );
        }
    }
    
    /**
     * Configure l'interface utilisateur
     */
    private void setupUI() {
        // Charger l'image de fond
        try {
            InputStream bgStream = getClass().getResourceAsStream("/images/backgrounds/menu_bg.png");
            if (bgStream != null) {
                backgroundImage.setImage(new Image(bgStream));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
        
        // Configuration de la liste des profils
        profilesList.setCellFactory(listView -> new ListCell<PlayerProfile>() {
            @Override
            protected void updateItem(PlayerProfile profile, boolean empty) {
                super.updateItem(profile, empty);
                if (empty || profile == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("%s - Parties: %d | Victoires: %d (%.1f%%)",
                            profile.getFullName(),
                            profile.getGamesPlayed(),
                            profile.getGamesWon(),
                            profile.getWinRate()));
                }
            }
        });
        
        // Listener pour la sélection dans la liste
        profilesList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> updateButtonStates()
        );
        
        updateButtonStates();
    }
    
    /**
     * Met à jour l'état des boutons selon la sélection
     */
    private void updateButtonStates() {
        boolean hasSelection = profilesList.getSelectionModel().getSelectedItem() != null;
        selectProfileButton.setDisable(!hasSelection);
        editProfileButton.setDisable(!hasSelection);
        deleteProfileButton.setDisable(!hasSelection);
    }
    
    /**
     * Rafraîchit la liste des profils
     */
    private void refreshProfilesList() {
        ObservableList<PlayerProfile> profiles = FXCollections.observableArrayList(
            profileManager.getAllProfiles()
        );
        profilesList.setItems(profiles);
    }
    
    /**
     * Met à jour l'affichage du joueur actuel
     */
    private void updateCurrentPlayerDisplay() {
        PlayerProfile currentPlayer = profileManager.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayerName.setText(currentPlayer.getFullName());
            currentPlayerStats.setText(String.format(
                "Parties: %d | Victoires: %d | Taux: %.1f%% | Meilleur score: %d",
                currentPlayer.getGamesPlayed(),
                currentPlayer.getGamesWon(),
                currentPlayer.getWinRate(),
                currentPlayer.getHighScore()
            ));
            
            // Charger l'avatar selon le choix
            String avatarChoice = currentPlayer.getAvatarPath();
            String avatarPath;
            if ("Pokemon".equals(avatarChoice)) {
                avatarPath = "enemy1_down_1.png";
            } else if ("Bomberman".equals(avatarChoice)) {
                avatarPath = "player1_down_1.png";
            } else {
                // Compatibilité avec l'ancien système (si c'est déjà un nom de fichier)
                avatarPath = avatarChoice.contains(".png") ? avatarChoice : "player1_down_1.png";
            }
            loadAvatar(avatarPath, currentPlayerAvatar);
        } else {
            currentPlayerName.setText("Aucun joueur sélectionné");
            currentPlayerStats.setText("");
            currentPlayerAvatar.setImage(null);
        }
    }
    
    /**
     * Met à jour les statistiques globales
     */
    private void updateGlobalStats() {
        globalStatsLabel.setText(profileManager.getGlobalStats());
    }
    
    /**
     * Charge un avatar depuis les ressources
     */
    private void loadAvatar(String avatarPath, ImageView imageView) {
        try {
            InputStream avatarStream = getClass().getResourceAsStream("/images/sprites/" + avatarPath);
            if (avatarStream != null) {
                imageView.setImage(new Image(avatarStream));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'avatar : " + e.getMessage());
        }
    }
    
    /**
     * Met à jour l'aperçu de l'avatar dans le dialog
     */
    private void updateAvatarPreview(String avatarChoice) {
        if (avatarChoice != null && avatarPreview != null) {
            String avatarPath;
            if ("Pokemon".equals(avatarChoice)) {
                avatarPath = "enemy1_down_1.png"; // Sprite Pokemon
            } else if ("Bomberman".equals(avatarChoice)) {
                avatarPath = "player1_down_1.png"; // Sprite Bomberman
            } else {
                avatarPath = "player1_down_1.png"; // Par défaut
            }
            loadAvatar(avatarPath, avatarPreview);
        }
    }
    
    // Actions FXML
    
    @FXML
    private void selectProfile() {
        PlayerProfile selected = profilesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            profileManager.setCurrentPlayer(selected);
            updateCurrentPlayerDisplay();
            SoundManager.playEffect("menu_select");
            showAlert("Profil sélectionné", "Le profil " + selected.getFullName() + " a été sélectionné.");
        }
    }
    
    @FXML
    private void createProfile() {
        editingProfile = null;
        dialogTitle.setText("CRÉER UN PROFIL");
        firstNameField.clear();
        lastNameField.clear();
        avatarComboBox.getSelectionModel().selectFirst();
        profileDialog.setVisible(true);
        SoundManager.playEffect("menu_select");
    }
    
    @FXML
    private void editProfile() {
        PlayerProfile selected = profilesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editingProfile = selected;
            dialogTitle.setText("MODIFIER LE PROFIL");
            firstNameField.setText(selected.getFirstName());
            lastNameField.setText(selected.getLastName());
            
            // Sélectionner l'avatar correct dans la ComboBox
            String avatarChoice = selected.getAvatarPath();
            if ("Pokemon".equals(avatarChoice) || avatarChoice.contains("enemy")) {
                avatarComboBox.getSelectionModel().select("Pokemon");
            } else {
                avatarComboBox.getSelectionModel().select("Bomberman");
            }
            
            profileDialog.setVisible(true);
            SoundManager.playEffect("menu_select");
        }
    }
    
    @FXML
    private void deleteProfile() {
        PlayerProfile selected = profilesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer le profil");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce profil ?");
            alert.setContentText("Profil : " + selected.getFullName() + "\nCette action est irréversible.");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                profileManager.removeProfile(selected);
                refreshProfilesList();
                updateCurrentPlayerDisplay();
                updateGlobalStats();
                SoundManager.playEffect("menu_select");
            }
        }
    }
    
    @FXML
    private void saveProfile() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String avatarPath = avatarComboBox.getSelectionModel().getSelectedItem();
        
        // Validation
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }
        
        if (editingProfile == null) {
            // Création d'un nouveau profil
            if (profileManager.profileExists(firstName, lastName)) {
                showAlert("Erreur", "Un profil avec ce nom existe déjà.");
                return;
            }
            
            PlayerProfile newProfile = new PlayerProfile(firstName, lastName, avatarPath);
            if (profileManager.addProfile(newProfile)) {
                refreshProfilesList();
                updateGlobalStats();
                profileDialog.setVisible(false);
                SoundManager.playEffect("menu_select");
                showAlert("Succès", "Profil créé avec succès !");
            }
        } else {
            // Modification d'un profil existant
            editingProfile.setFirstName(firstName);
            editingProfile.setLastName(lastName);
            editingProfile.setAvatarPath(avatarPath);
            profileManager.updateProfile(editingProfile);
            refreshProfilesList();
            updateCurrentPlayerDisplay();
            profileDialog.setVisible(false);
            SoundManager.playEffect("menu_select");
            showAlert("Succès", "Profil modifié avec succès !");
        }
    }
    
    @FXML
    private void cancelProfile() {
        profileDialog.setVisible(false);
        SoundManager.playEffect("menu_back");
    }
    
    @FXML
    private void goBack() {
        SoundManager.playEffect("menu_back");
        if (callback != null) {
            callback.onBackToMainMenu();
        }
    }
    
    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Définit le callback pour retourner au menu principal
     */
    public void setCallback(ProfileMenuCallback callback) {
        this.callback = callback;
    }
} 