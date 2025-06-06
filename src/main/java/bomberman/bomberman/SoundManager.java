package bomberman.bomberman;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de sons et musiques pour le jeu Bomberman.
 * Permet de charger, jouer, arrêter et boucler des fichiers audio.
 * Gère une collection de MediaPlayer pour différents sons/musiques.
 */
public class SoundManager {
    
    // Map pour stocker les différents MediaPlayer (musiques longues)
    private static final Map<String, MediaPlayer> mediaPlayers = new HashMap<>();
    
    // Map pour stocker les différents AudioClip (effets courts - latence minimale)
    private static final Map<String, AudioClip> audioClips = new HashMap<>();
    
    // Pool d'AudioClip préchargés pour latence absolument nulle
    private static final Map<String, List<AudioClip>> audioClipPools = new HashMap<>();
    private static final Map<String, Integer> poolIndexes = new HashMap<>();
    

    
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
     * Charge un effet sonore court (AudioClip) et l'associe à un nom
     * Note: AudioClip offre une latence minimale pour les effets courts (fichiers PCM requis)
     * @param name Nom d'identification de l'effet sonore
     * @param resourcePath Chemin du fichier audio dans les ressources
     */
    public static void loadSoundEffect(String name, String resourcePath) {
        try {
            // Vérifier que le fichier existe
            if (SoundManager.class.getResource(resourcePath) == null) {
                System.err.println("Fichier audio non trouvé : " + resourcePath);
                return;
            }
            
            // Charger le fichier audio depuis les ressources
            String audioPath = SoundManager.class.getResource(resourcePath).toExternalForm();
            System.out.println("Tentative de chargement effet sonore AudioClip : " + audioPath);
            
            AudioClip audioClip = new AudioClip(audioPath);
            
            // Optimisations pour latence minimale
            double volume = "walking".equals(name) ? 1.0 : 
                           "dies".equals(name) ? 0.8 : 0.9; // Volume équilibré pour le son de mort
            audioClip.setVolume(volume);
            
            // Créer un pool de 3 instances pour éviter toute latence de recyclage
            List<AudioClip> clipPool = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                AudioClip poolClip = new AudioClip(audioPath);
                poolClip.setVolume(volume);
                
                // Précharger chaque instance
                poolClip.setVolume(0.0);
                poolClip.play();
                poolClip.setVolume(volume);
                
                clipPool.add(poolClip);
            }
            
            // Attendre un court instant pour le préchargement
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}
            
            // Stocker l'AudioClip principal et le pool
            audioClips.put(name, audioClip);
            audioClipPools.put(name, clipPool);
            poolIndexes.put(name, 0);
            
            System.out.println("Effet sonore AudioClip préchargé avec succès : " + name + " depuis " + resourcePath);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'effet sonore AudioClip " + name + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Joue un effet sonore court (AudioClip) avec latence absolument nulle
     * Utilise un pool tournant d'instances préchargées
     * @param name Nom de l'effet sonore à jouer
     */
    public static void playEffect(String name) {
        List<AudioClip> clipPool = audioClipPools.get(name);
        if (clipPool != null && !clipPool.isEmpty()) {
            try {
                // Utiliser la prochaine instance du pool (rotation)
                int currentIndex = poolIndexes.get(name);
                AudioClip audioClip = clipPool.get(currentIndex);
                

                
                // Lecture immédiate sans aucune latence
                audioClip.play();
                
                // Passer à l'instance suivante pour le prochain appel
                poolIndexes.put(name, (currentIndex + 1) % clipPool.size());
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture de l'effet sonore AudioClip " + name + " : " + e.getMessage());
            }
        } else {
            System.err.println("Pool d'effets sonores AudioClip non trouvé : " + name);
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
     * Joue un son une seule fois avec callback à la fin
     * @param name Nom du son à jouer
     * @param onEndCallback Action à exécuter à la fin de la lecture (peut être null)
     */
    public static void playOnce(String name, Runnable onEndCallback) {
        MediaPlayer mediaPlayer = mediaPlayers.get(name);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop(); // Arrêter si déjà en cours
                mediaPlayer.seek(Duration.ZERO); // Revenir au début
                mediaPlayer.setCycleCount(1); // Une seule lecture
                
                // Configurer le callback de fin
                if (onEndCallback != null) {
                    mediaPlayer.setOnEndOfMedia(() -> {
                        System.out.println("Fin de lecture pour : " + name + " - Exécution du callback");
                        onEndCallback.run();
                    });
                } else {
                    mediaPlayer.setOnEndOfMedia(() -> {
                        System.out.println("Fin de lecture pour : " + name);
                    });
                }
                
                mediaPlayer.play();
                System.out.println("Lecture unique du son : " + name);
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture unique du son " + name + " : " + e.getMessage());
                // En cas d'erreur, exécuter quand même le callback pour ne pas bloquer le jeu
                if (onEndCallback != null) {
                    onEndCallback.run();
                }
            }
        } else {
            System.err.println("Son non trouvé : " + name);
            // Si le son n'existe pas, exécuter quand même le callback pour ne pas bloquer le jeu
            if (onEndCallback != null) {
                onEndCallback.run();
            }
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
        // Libérer les MediaPlayer
        for (Map.Entry<String, MediaPlayer> entry : mediaPlayers.entrySet()) {
            try {
                entry.getValue().dispose();
                System.out.println("Ressources MediaPlayer libérées pour : " + entry.getKey());
            } catch (Exception e) {
                System.err.println("Erreur lors de la libération des ressources MediaPlayer pour " + entry.getKey() + " : " + e.getMessage());
            }
        }
        mediaPlayers.clear();
        
        // Les AudioClip n'ont pas besoin de dispose explicite, mais on nettoie les maps
        audioClips.clear();
        audioClipPools.clear();
        poolIndexes.clear();
        System.out.println("Ressources AudioClip et pools nettoyées");
        System.out.println("Toutes les ressources audio nettoyées");
    }
    
    /**
     * Joue la musique de fond d'un niveau spécifique en boucle
     * Arrête automatiquement toute autre musique en cours
     * @param levelNumber Numéro du niveau (1, 2, 3, etc.)
     */
    public static void playLevelMusic(int levelNumber) {
        // Déterminer le nom de la musique selon le niveau
        String musicName = null;
        switch (levelNumber) {
            case 1:
                musicName = "theme_world_1";
                break;
            // Ajouter d'autres niveaux ici dans le futur
            default:
                System.out.println("Aucune musique définie pour le niveau " + levelNumber);
                return;
        }
        
        // Arrêter toutes les autres musiques avant de commencer
        stopAllMusic();
        
        // Jouer la musique du niveau en boucle
        loop(musicName);
        System.out.println("Musique de niveau " + levelNumber + " démarrée : " + musicName);
    }
    
    /**
     * Arrête la musique de niveau en cours
     */
    public static void stopLevelMusic() {
        // Arrêter les musiques de niveau connues
        stop("theme_world_1");
        // Ajouter d'autres musiques de niveau ici dans le futur
        
        System.out.println("Musique de niveau arrêtée");
    }
    
    /**
     * Arrête toutes les musiques (intro, niveau, etc.) sauf les effets sonores
     */
    public static void stopAllMusic() {
        stop("intro");
        stop("level_start");
        stop("theme_world_1");
        // Ajouter d'autres musiques ici dans le futur
        
        System.out.println("Toutes les musiques arrêtées");
    }
} 