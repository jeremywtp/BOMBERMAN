package bomberman.bomberman;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente un profil de joueur avec ses statistiques
 */
public class PlayerProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String firstName;
    private String lastName;
    private String avatarPath;
    private int gamesPlayed;
    private int gamesWon;
    private int highScore;
    private LocalDateTime creationDate;
    private LocalDateTime lastPlayedDate;
    
    /**
     * Constructeur pour créer un nouveau profil
     */
    public PlayerProfile(String firstName, String lastName, String avatarPath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarPath = avatarPath;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.highScore = 0;
        this.creationDate = LocalDateTime.now();
        this.lastPlayedDate = LocalDateTime.now();
    }
    
    /**
     * Constructeur par défaut pour la désérialisation
     */
    public PlayerProfile() {
        this.creationDate = LocalDateTime.now();
        this.lastPlayedDate = LocalDateTime.now();
    }
    
    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getAvatarPath() { return avatarPath; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() { return gamesWon; }
    public int getGamesLost() { return gamesPlayed - gamesWon; }
    public int getHighScore() { return highScore; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public LocalDateTime getLastPlayedDate() { return lastPlayedDate; }
    
    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
    public void setHighScore(int highScore) { this.highScore = highScore; }
    
    /**
     * Calcule le pourcentage de victoires
     */
    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) gamesWon / gamesPlayed * 100.0;
    }
    
    /**
     * Enregistre une partie jouée
     */
    public void recordGamePlayed(boolean won, int score) {
        gamesPlayed++;
        if (won) {
            gamesWon++;
        }
        if (score > highScore) {
            highScore = score;
        }
        lastPlayedDate = LocalDateTime.now();
    }
    
    /**
     * Formate la date de création
     */
    public String getFormattedCreationDate() {
        return creationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    /**
     * Formate la date de dernière partie
     */
    public String getFormattedLastPlayedDate() {
        return lastPlayedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    @Override
    public String toString() {
        return String.format("%s %s (Parties: %d, Victoires: %d, Taux: %.1f%%)", 
                firstName, lastName, gamesPlayed, gamesWon, getWinRate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerProfile profile = (PlayerProfile) obj;
        return firstName.equals(profile.firstName) && lastName.equals(profile.lastName);
    }
    
    @Override
    public int hashCode() {
        return (firstName + lastName).hashCode();
    }
} 