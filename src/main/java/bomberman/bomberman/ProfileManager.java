package bomberman.bomberman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gestionnaire des profils joueurs - Sauvegarde et chargement
 */
public class ProfileManager {
    private static final String PROFILES_DIR = "profiles";
    private static final String PROFILES_FILE = "profiles/players.dat";
    private static ProfileManager instance;
    
    private List<PlayerProfile> profiles;
    private PlayerProfile currentPlayer;
    
    private ProfileManager() {
        this.profiles = new ArrayList<>();
        createProfilesDirectory();
        loadProfiles();
    }
    
    /**
     * Singleton - Récupère l'instance unique du gestionnaire
     */
    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }
    
    /**
     * Crée le répertoire des profils s'il n'existe pas
     */
    private void createProfilesDirectory() {
        try {
            Path profilesPath = Paths.get(PROFILES_DIR);
            if (!Files.exists(profilesPath)) {
                Files.createDirectories(profilesPath);
                System.out.println("Répertoire des profils créé : " + PROFILES_DIR);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du répertoire des profils : " + e.getMessage());
        }
    }
    
    /**
     * Charge tous les profils depuis le fichier
     */
    public void loadProfiles() {
        try {
            File file = new File(PROFILES_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    @SuppressWarnings("unchecked")
                    List<PlayerProfile> loadedProfiles = (List<PlayerProfile>) ois.readObject();
                    this.profiles = loadedProfiles;
                    System.out.println("Profils chargés : " + profiles.size() + " profil(s)");
                }
            } else {
                System.out.println("Aucun fichier de profils trouvé, création d'une liste vide");
                this.profiles = new ArrayList<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des profils : " + e.getMessage());
            this.profiles = new ArrayList<>();
        }
    }
    
    /**
     * Sauvegarde tous les profils dans le fichier
     */
    public void saveProfiles() {
        try {
            createProfilesDirectory(); // S'assurer que le répertoire existe
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROFILES_FILE))) {
                oos.writeObject(profiles);
                System.out.println("Profils sauvegardés : " + profiles.size() + " profil(s)");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des profils : " + e.getMessage());
        }
    }
    
    /**
     * Ajoute un nouveau profil
     */
    public boolean addProfile(PlayerProfile profile) {
        if (profiles.contains(profile)) {
            System.out.println("Un profil avec ce nom existe déjà : " + profile.getFullName());
            return false;
        }
        
        profiles.add(profile);
        saveProfiles();
        System.out.println("Nouveau profil ajouté : " + profile.getFullName());
        return true;
    }
    
    /**
     * Supprime un profil
     */
    public boolean removeProfile(PlayerProfile profile) {
        if (profiles.remove(profile)) {
            if (currentPlayer != null && currentPlayer.equals(profile)) {
                currentPlayer = null;
            }
            saveProfiles();
            System.out.println("Profil supprimé : " + profile.getFullName());
            return true;
        }
        return false;
    }
    
    /**
     * Met à jour un profil existant
     */
    public void updateProfile(PlayerProfile profile) {
        int index = profiles.indexOf(profile);
        if (index >= 0) {
            profiles.set(index, profile);
            saveProfiles();
            System.out.println("Profil mis à jour : " + profile.getFullName());
        }
    }
    
    /**
     * Trouve un profil par nom complet
     */
    public Optional<PlayerProfile> findProfile(String firstName, String lastName) {
        return profiles.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName) && 
                           p.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
    }
    
    /**
     * Récupère tous les profils
     */
    public List<PlayerProfile> getAllProfiles() {
        return new ArrayList<>(profiles);
    }
    
    /**
     * Définit le joueur actuel
     */
    public void setCurrentPlayer(PlayerProfile player) {
        this.currentPlayer = player;
        System.out.println("Joueur actuel défini : " + (player != null ? player.getFullName() : "Aucun"));
    }
    
    /**
     * Récupère le joueur actuel
     */
    public PlayerProfile getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Enregistre une partie pour le joueur actuel
     */
    public void recordGameForCurrentPlayer(boolean won, int score) {
        if (currentPlayer != null) {
            currentPlayer.recordGamePlayed(won, score);
            updateProfile(currentPlayer);
            System.out.println("Partie enregistrée pour " + currentPlayer.getFullName() + 
                             " - Victoire: " + won + ", Score: " + score);
        }
    }
    
    /**
     * Récupère le nombre total de profils
     */
    public int getProfileCount() {
        return profiles.size();
    }
    
    /**
     * Vérifie si un profil existe
     */
    public boolean profileExists(String firstName, String lastName) {
        return findProfile(firstName, lastName).isPresent();
    }
    
    /**
     * Récupère les statistiques globales
     */
    public String getGlobalStats() {
        int totalGames = profiles.stream().mapToInt(PlayerProfile::getGamesPlayed).sum();
        int totalWins = profiles.stream().mapToInt(PlayerProfile::getGamesWon).sum();
        int maxScore = profiles.stream().mapToInt(PlayerProfile::getHighScore).max().orElse(0);
        
        return String.format("Profils: %d | Parties totales: %d | Victoires: %d | Meilleur score: %d",
                profiles.size(), totalGames, totalWins, maxScore);
    }
} 