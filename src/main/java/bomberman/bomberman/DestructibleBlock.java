package bomberman.bomberman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * Classe gérant l'animation des blocs destructibles.
 * Supporte deux versions :
 * - Version 1 : bloc destructible sans bloc non destructible au-dessus
 * - Version 2 : bloc destructible avec bloc non destructible au-dessus
 * 
 * Chaque version possède 4 frames d'animation qui s'enchaînent en boucle continue.
 * Les sprites sont automatiquement redimensionnés de 16x16 à 48x48 pixels.
 */
public class DestructibleBlock {
    
    // Constantes d'animation
    private static final int FRAME_COUNT = 4;
    private static final double FRAME_DURATION_MS = 100.0; // 100ms par frame
    
    // Images des animations (statiques pour éviter de recharger)
    private static Image[] v1Frames = new Image[FRAME_COUNT];
    private static Image[] v2Frames = new Image[FRAME_COUNT];
    private static boolean framesLoaded = false;
    
    // État de l'animation
    private final String version;
    private Timeline animationTimeline;
    private int currentFrameIndex = 0;
    private Image currentFrame;
    
    /**
     * Constructeur du bloc destructible animé
     * @param version "v1" pour version sans bloc au-dessus, "v2" pour version avec bloc au-dessus
     */
    public DestructibleBlock(String version) {
        if (!version.equals("v1") && !version.equals("v2")) {
            throw new IllegalArgumentException("Version doit être 'v1' ou 'v2'");
        }
        
        this.version = version;
        
        // Charger les images si ce n'est pas déjà fait
        loadFrames();
        
        // Initialiser l'animation
        initializeAnimation();
        
        // Démarrer l'animation
        startAnimation();
    }
    
    /**
     * Charge tous les frames d'animation pour les deux versions depuis le SpriteManager (thème actuel)
     */
    private static void loadFrames() {
        if (framesLoaded) return;
        
        try {
            // Utiliser le SpriteManager pour obtenir les blocs destructibles du thème actuel
            SpriteManager spriteManager = SpriteManager.getInstance();
            SpriteManager.ThemeSprites currentSprites = spriteManager.getCurrentSprites();
            
            if (currentSprites != null) {
                // Copier les frames depuis le SpriteManager
                for (int i = 0; i < FRAME_COUNT; i++) {
                    v1Frames[i] = currentSprites.blocDestructibleV1[i];
                    v2Frames[i] = currentSprites.blocDestructibleV2[i];
                }
                System.out.println("Frames de blocs destructibles chargées depuis le thème : " + spriteManager.getCurrentTheme().getDisplayName());
            } else {
                // Fallback vers les images par défaut si le SpriteManager n'est pas disponible
                loadFramesFallback();
            }
            
            framesLoaded = true;
            System.out.println("Toutes les frames de blocs destructibles chargées avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des frames depuis le SpriteManager : " + e.getMessage());
            // Fallback final
            loadFramesFallback();
        }
    }
    
    /**
     * Méthode de fallback pour charger les frames par défaut
     */
    private static void loadFramesFallback() {
        try {
            // Charger les frames de la version 1
            for (int i = 0; i < FRAME_COUNT; i++) {
                String imagePath = "/sprites/bloc_destructible_v1_" + (i + 1) + "_48x48.png";
                v1Frames[i] = new Image(DestructibleBlock.class.getResourceAsStream(imagePath));
                System.out.println("Frame v1_" + (i + 1) + " chargée en fallback : " + imagePath);
            }
            
            // Charger les frames de la version 2
            for (int i = 0; i < FRAME_COUNT; i++) {
                String imagePath = "/sprites/bloc_destructible_v2_" + (i + 1) + "_48x48.png";
                v2Frames[i] = new Image(DestructibleBlock.class.getResourceAsStream(imagePath));
                System.out.println("Frame v2_" + (i + 1) + " chargée en fallback : " + imagePath);
            }
            
            framesLoaded = true;
            
        } catch (Exception fallbackException) {
            System.err.println("Erreur critique lors du chargement des frames en fallback : " + fallbackException.getMessage());
            fallbackException.printStackTrace();
        }
    }
    
    /**
     * Initialise l'animation Timeline
     */
    private void initializeAnimation() {
        // Définir la frame initiale
        updateCurrentFrame();
        
        // Créer la timeline pour l'animation
        animationTimeline = new Timeline(
            new KeyFrame(Duration.millis(FRAME_DURATION_MS), e -> {
                // Passer à la frame suivante
                currentFrameIndex = (currentFrameIndex + 1) % FRAME_COUNT;
                updateCurrentFrame();
            })
        );
        
        // Configuration de l'animation en boucle infinie
        animationTimeline.setCycleCount(Animation.INDEFINITE);
    }
    
    /**
     * Met à jour la frame courante selon la version et l'index
     */
    private void updateCurrentFrame() {
        if (version.equals("v1")) {
            currentFrame = v1Frames[currentFrameIndex];
        } else {
            currentFrame = v2Frames[currentFrameIndex];
        }
    }
    
    /**
     * Démarre l'animation
     */
    public void startAnimation() {
        if (animationTimeline != null) {
            animationTimeline.play();
        }
    }
    
    /**
     * Arrête l'animation
     */
    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
    }
    
    /**
     * Met en pause l'animation
     */
    public void pauseAnimation() {
        if (animationTimeline != null) {
            animationTimeline.pause();
        }
    }
    
    /**
     * Reprend l'animation si elle était en pause
     */
    public void resumeAnimation() {
        if (animationTimeline != null) {
            animationTimeline.play();
        }
    }
    
    /**
     * Obtient l'image de la frame courante
     * @return Image de la frame courante à afficher
     */
    public Image getCurrentFrame() {
        return currentFrame;
    }
    
    /**
     * Obtient la version du bloc
     * @return "v1" ou "v2"
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Vérifie si l'animation est en cours
     * @return true si l'animation joue, false sinon
     */
    public boolean isAnimationRunning() {
        return animationTimeline != null && animationTimeline.getStatus() == Animation.Status.RUNNING;
    }
    
    /**
     * Libère les ressources de l'animation
     */
    public void dispose() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
    }
    
    /**
     * ✨ **NOUVEAU** : Force le rechargement des frames (utile lors du changement de thème)
     */
    public static void reloadFrames() {
        framesLoaded = false; // Forcer le rechargement
        loadFrames();
        System.out.println("Frames de blocs destructibles rechargées pour le nouveau thème");
    }
    
    /**
     * Méthode statique pour créer un bloc selon le contexte
     * @param hasNonDestructibleAbove true s'il y a un bloc non destructible au-dessus
     * @return DestructibleBlock configuré avec la bonne version
     */
    public static DestructibleBlock createForContext(boolean hasNonDestructibleAbove) {
        return new DestructibleBlock(hasNonDestructibleAbove ? "v2" : "v1");
    }
} 