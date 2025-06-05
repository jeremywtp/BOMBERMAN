package bomberman.bomberman;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de sons et musiques pour le jeu Bomberman.
 * Permet de charger, jouer, arrêter et boucler des fichiers audio.
 * Gère une collection de MediaPlayer pour différents sons/musiques.
 */
public class SoundManager {
    
    // Map pour stocker les différents MediaPlayer
    private static final Map<String, MediaPlayer> mediaPlayers = new HashMap<>();
    
    /**
     * Charge un fichier audio et l'associe à un nom
     * @param name Nom d'identification du son
     * @param resourcePath Chemin du fichier audio dans les ressources
     */
    public static void loadSound(String name, String resourcePath) {
        try {
            // Vérifier que le fichier existe
            if (SoundManager.class.getResource(resourcePath) == null) {
                System.err.println("Fichier audio non trouvé : " + resourcePath);
                return;
            }
            
            // Charger le fichier audio depuis les ressources
            String audioPath = SoundManager.class.getResource(resourcePath).toExternalForm();
            System.out.println("Tentative de chargement : " + audioPath);
            
            Media media = new Media(audioPath);
            
            // Ajouter des listeners pour le média
            media.setOnError(() -> {
                System.err.println("Erreur Media pour " + name + " : " + media.getError());
            });
            
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            
            // Ajouter des listeners pour le lecteur
            mediaPlayer.setOnError(() -> {
                System.err.println("Erreur MediaPlayer pour " + name + " : " + mediaPlayer.getError());
            });
            
            mediaPlayer.setOnReady(() -> {
                System.out.println("MediaPlayer prêt pour : " + name);
                System.out.println("Durée : " + mediaPlayer.getTotalDuration());
            });
            
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Fin de lecture pour : " + name);
            });
            
            // Définir un volume par défaut
            mediaPlayer.setVolume(0.7); // 70% du volume
            
            // Stocker le MediaPlayer dans la map
            mediaPlayers.put(name, mediaPlayer);
            
            System.out.println("Son chargé avec succès : " + name + " depuis " + resourcePath);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du son " + name + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Joue un son une seule fois
     * @param name Nom du son à jouer
     */
    public static void play(String name) {
        MediaPlayer mediaPlayer = mediaPlayers.get(name);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop(); // Arrêter si déjà en cours
                mediaPlayer.seek(Duration.ZERO); // Revenir au début
                mediaPlayer.play();
                System.out.println("Lecture du son : " + name);
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture du son " + name + " : " + e.getMessage());
            }
        } else {
            System.err.println("Son non trouvé : " + name);
        }
    }
    
    /**
     * Joue un son en boucle infinie
     * @param name Nom du son à jouer en boucle
     */
    public static void loop(String name) {
        MediaPlayer mediaPlayer = mediaPlayers.get(name);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop(); // Arrêter si déjà en cours
                mediaPlayer.seek(Duration.ZERO); // Revenir au début
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Boucle infinie
                mediaPlayer.play();
                System.out.println("Lecture en boucle du son : " + name);
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture en boucle du son " + name + " : " + e.getMessage());
            }
        } else {
            System.err.println("Son non trouvé : " + name);
        }
    }
    
    /**
     * Arrête un son en cours de lecture
     * @param name Nom du son à arrêter
     */
    public static void stop(String name) {
        MediaPlayer mediaPlayer = mediaPlayers.get(name);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                System.out.println("Arrêt du son : " + name);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'arrêt du son " + name + " : " + e.getMessage());
            }
        } else {
            System.err.println("Son non trouvé : " + name);
        }
    }
    
    /**
     * Arrête tous les sons en cours de lecture
     */
    public static void stopAll() {
        for (Map.Entry<String, MediaPlayer> entry : mediaPlayers.entrySet()) {
            try {
                entry.getValue().stop();
                System.out.println("Arrêt du son : " + entry.getKey());
            } catch (Exception e) {
                System.err.println("Erreur lors de l'arrêt du son " + entry.getKey() + " : " + e.getMessage());
            }
        }
    }
    
    /**
     * Vérifie si un son est en cours de lecture
     * @param name Nom du son à vérifier
     * @return true si le son est en cours de lecture
     */
    public static boolean isPlaying(String name) {
        MediaPlayer mediaPlayer = mediaPlayers.get(name);
        if (mediaPlayer != null) {
            return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
        }
        return false;
    }
    
    /**
     * Définit le volume d'un son
     * @param name Nom du son
     * @param volume Volume (0.0 à 1.0)
     */
    public static void setVolume(String name, double volume) {
        MediaPlayer mediaPlayer = mediaPlayers.get(name);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume))); // Clamp entre 0 et 1
                System.out.println("Volume du son " + name + " défini à : " + volume);
            } catch (Exception e) {
                System.err.println("Erreur lors de la définition du volume du son " + name + " : " + e.getMessage());
            }
        } else {
            System.err.println("Son non trouvé : " + name);
        }
    }
    
    /**
     * Libère les ressources audio
     */
    public static void dispose() {
        for (Map.Entry<String, MediaPlayer> entry : mediaPlayers.entrySet()) {
            try {
                entry.getValue().dispose();
                System.out.println("Ressources libérées pour : " + entry.getKey());
            } catch (Exception e) {
                System.err.println("Erreur lors de la libération des ressources pour " + entry.getKey() + " : " + e.getMessage());
            }
        }
        mediaPlayers.clear();
    }
} 