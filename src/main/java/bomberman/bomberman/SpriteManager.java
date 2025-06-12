package bomberman.bomberman;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire centralisé de tous les sprites du jeu organisés par thème.
 * Charge et fournit les sprites appropriés selon le thème sélectionné.
 * 
 * Structure des sprites par thème :
 * - BOMBERMAN : Sprites originaux actuels
 * - Autres thèmes : Variations des sprites (à implémenter)
 */
public class SpriteManager {
    
    // Instance singleton
    private static SpriteManager instance;
    
    // Thème actuellement actif
    private Theme currentTheme;
    
    // Cache des sprites chargés par thème
    private Map<Theme, ThemeSprites> spriteCache;
    
    /**
     * Classe interne contenant tous les sprites d'un thème
     */
    public static class ThemeSprites {
        // === SPRITES JOUEUR ===
        // Sprites fixes
        public Image playerFixeHaut;
        public Image playerFixeBas;
        public Image playerFixeGauche;
        public Image playerFixeDroite;
        
        // Sprites de marche
        public Image playerMarcheHaut1, playerMarcheHaut2;
        public Image playerMarcheBas1, playerMarcheBas2;
        public Image playerMarcheGauche1, playerMarcheGauche2;
        public Image playerMarcheDroite1, playerMarcheDroite2;
        
        // Sprites de mort (8 frames)
        public Image[] playerDeath = new Image[8];
        
        // Sprites de victoire (9 frames)
        public Image[] playerWin = new Image[9];
        
        // === SPRITES ENNEMIS ===
        // Sprites Puropen (4 frames par direction)
        public Image[] enemyHaut = new Image[4];
        public Image[] enemyBas = new Image[4];
        public Image[] enemyGauche = new Image[4];
        public Image[] enemyDroite = new Image[4];
        
        // === SPRITES ENVIRONNEMENT ===
        // Blocs et terrain
        public Image blocNonDestructible;
        public Image herbe;
        public Image herbeWithOmbreBlocNonDestructible;
        public Image herbeWithOmbreBlocDestructible;
        public Image contoursMap;
        
        // Blocs destructibles animés (4 frames, 2 versions)
        public Image[] blocDestructibleV1 = new Image[4];
        public Image[] blocDestructibleV2 = new Image[4];
        
        // === SPRITES BOMBES ===
        public Image bomb1, bomb2, bomb3;
        
        // === SPRITES EXPLOSIONS ===
        // 7 types x 5 frames
        public Image[][] explosions = new Image[7][5]; // [type][frame]
        
        // === SPRITES POWER-UPS ===
        public Image[] extraBomb = new Image[2];           // 2 frames d'animation
        public Image[] explosionExpander = new Image[2];   // 2 frames d'animation
        public Image[] accelerator = new Image[2];         // 2 frames d'animation
        
        // === SPRITES PORTE ===
        public Image[] door = new Image[2];                // 2 frames d'animation
        
        // === IMAGES INTERFACE ===
        public Image intro;
        public Image icon;
    }
    
    /**
     * Constructeur privé pour le pattern singleton
     */
    private SpriteManager() {
        this.spriteCache = new HashMap<>();
        this.currentTheme = Theme.BOMBERMAN; // Thème par défaut
    }
    
    /**
     * Obtient l'instance singleton du SpriteManager
     * @return L'instance unique du SpriteManager
     */
    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }
    
    /**
     * Définit le thème actuel et charge les sprites correspondants
     * @param theme Le thème à activer
     */
    public void setTheme(Theme theme) {
        if (theme != currentTheme) {
            currentTheme = theme;
            loadThemeSprites(theme);
            
            // Notifier le GridRenderer et DestructibleBlock pour qu'ils rechargent leurs sprites
            try {
                GridRenderer.reloadContoursMapImage();
                GridRenderer.reloadBombImages();
                GridRenderer.reloadBlocNonDestructibleImage();
                DestructibleBlock.reloadFrames();
            } catch (Exception e) {
                System.err.println("Erreur lors du rechargement des sprites : " + e.getMessage());
            }
            
            System.out.println("SpriteManager - Thème changé vers : " + theme.getDisplayName());
        }
    }
    
    /**
     * Obtient le thème actuellement actif
     * @return Le thème actuel
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Obtient les sprites du thème actuel
     * @return Les sprites du thème actuel
     */
    public ThemeSprites getCurrentSprites() {
        return getSprites(currentTheme);
    }
    
    /**
     * Obtient les sprites d'un thème spécifique
     * @param theme Le thème dont on veut les sprites
     * @return Les sprites du thème demandé
     */
    public ThemeSprites getSprites(Theme theme) {
        if (!spriteCache.containsKey(theme)) {
            loadThemeSprites(theme);
        }
        return spriteCache.get(theme);
    }
    
    /**
     * Charge tous les sprites d'un thème spécifique
     * @param theme Le thème à charger
     */
    private void loadThemeSprites(Theme theme) {
        System.out.println("Chargement des sprites pour le thème : " + theme.getDisplayName());
        
        ThemeSprites sprites = new ThemeSprites();
        
        switch (theme) {
            case BOMBERMAN:
                loadBombermanThemeSprites(sprites);
                break;
            case POKEMON:
                loadPokemonThemeSprites(sprites);
                break;
            case CLASSIC:
                loadClassicThemeSprites(sprites);
                break;
            case DARK:
                loadDarkThemeSprites(sprites);
                break;
            case OCEAN:
                loadOceanThemeSprites(sprites);
                break;
            case FOREST:
                loadForestThemeSprites(sprites);
                break;
            case DESERT:
                loadDesertThemeSprites(sprites);
                break;
            default:
                // Fallback vers le thème Bomberman
                loadBombermanThemeSprites(sprites);
                break;
        }
        
        spriteCache.put(theme, sprites);
        System.out.println("Sprites du thème " + theme.getDisplayName() + " chargés avec succès");
    }
    
    /**
     * Charge les sprites du thème BOMBERMAN (sprites actuels)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadBombermanThemeSprites(ThemeSprites sprites) {
        try {
            // === SPRITES JOUEUR ===
            // Sprites fixes
            sprites.playerFixeHaut = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_haut.png"));
            sprites.playerFixeBas = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_bas.png"));
            sprites.playerFixeGauche = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_gauche.png"));
            sprites.playerFixeDroite = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_droite.png"));
            
            // Sprites de marche
            sprites.playerMarcheHaut1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_haut1.png"));
            sprites.playerMarcheHaut2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_haut2.png"));
            sprites.playerMarcheBas1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_bas1.png"));
            sprites.playerMarcheBas2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_bas2.png"));
            sprites.playerMarcheGauche1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_gauche1.png"));
            sprites.playerMarcheGauche2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_gauche2.png"));
            sprites.playerMarcheDroite1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_droite1.png"));
            sprites.playerMarcheDroite2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_droite2.png"));
            
            // Sprites de mort
            for (int i = 0; i < 8; i++) {
                sprites.playerDeath[i] = new Image(getClass().getResourceAsStream("/sprites/perso/Bomberman_dies_" + (i + 1) + ".png"));
            }
            
            // Sprites de victoire
            for (int i = 0; i < 9; i++) {
                sprites.playerWin[i] = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_win_" + (i + 1) + ".png"));
            }
            
            // === SPRITES ENNEMIS ===
            for (int i = 0; i < 4; i++) {
                sprites.enemyHaut[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_haut_" + (i + 1) + ".png"));
                sprites.enemyBas[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_bas_" + (i + 1) + ".png"));
                sprites.enemyGauche[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_gauche_" + (i + 1) + ".png"));
                sprites.enemyDroite[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_droite_" + (i + 1) + ".png"));
            }
            
            // === SPRITES ENVIRONNEMENT ===
            sprites.blocNonDestructible = new Image(getClass().getResourceAsStream("/sprites/bloc_non_destructible_48x48.png"));
            sprites.herbe = new Image(getClass().getResourceAsStream("/sprites/herbe_48x48.png"));
            sprites.herbeWithOmbreBlocNonDestructible = new Image(getClass().getResourceAsStream("/sprites/herbe_with_ombre_bloc_non_destructible_48x48.png"));
            sprites.herbeWithOmbreBlocDestructible = new Image(getClass().getResourceAsStream("/sprites/herbe_with_ombre_bloc_destructible_48x48.png"));
            sprites.contoursMap = new Image(getClass().getResourceAsStream("/sprites/contours_map_816x624.png"));
            
            // Blocs destructibles
            for (int i = 0; i < 4; i++) {
                sprites.blocDestructibleV1[i] = new Image(getClass().getResourceAsStream("/sprites/bloc_destructible_v1_" + (i + 1) + "_48x48.png"));
                sprites.blocDestructibleV2[i] = new Image(getClass().getResourceAsStream("/sprites/bloc_destructible_v2_" + (i + 1) + "_48x48.png"));
            }
            
            // === SPRITES BOMBES ===
            sprites.bomb1 = new Image(getClass().getResourceAsStream("/sprites/bomb_1_48x48.png"));
            sprites.bomb2 = new Image(getClass().getResourceAsStream("/sprites/bomb_2_48x48.png"));
            sprites.bomb3 = new Image(getClass().getResourceAsStream("/sprites/bomb_3_48x48.png"));
            
            // === SPRITES EXPLOSIONS ===
            String[] explosionTypes = {"milieu", "droite", "gauche", "haut", "bas", "horizontale", "verticale"};
            for (int type = 0; type < 7; type++) {
                for (int frame = 0; frame < 5; frame++) {
                    sprites.explosions[type][frame] = new Image(getClass().getResourceAsStream("/sprites/resized_explosion_" + explosionTypes[type] + "_" + (frame + 1) + ".png"));
                }
            }
            
            // === SPRITES POWER-UPS ===
            sprites.extraBomb[0] = new Image(getClass().getResourceAsStream("/sprites/bonus_extra_bomb_1.png"));
            sprites.extraBomb[1] = new Image(getClass().getResourceAsStream("/sprites/bonus_extra_bomb_2.png"));
            sprites.explosionExpander[0] = new Image(getClass().getResourceAsStream("/sprites/bonus_explosion_expander_1.png"));
            sprites.explosionExpander[1] = new Image(getClass().getResourceAsStream("/sprites/bonus_explosion_expander_2.png"));
            sprites.accelerator[0] = new Image(getClass().getResourceAsStream("/sprites/bonus_accelerator_1.png"));
            sprites.accelerator[1] = new Image(getClass().getResourceAsStream("/sprites/bonus_accelerator_2.png"));
            
            // === SPRITES PORTE ===
            sprites.door[0] = new Image(getClass().getResourceAsStream("/sprites/porte_1.png"));
            sprites.door[1] = new Image(getClass().getResourceAsStream("/sprites/porte_2.png"));
            
            // === IMAGES INTERFACE ===
            sprites.intro = new Image(getClass().getResourceAsStream("/images/intro.png"));
            sprites.icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
            
            System.out.println("✅ Tous les sprites du thème BOMBERMAN chargés avec succès");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites BOMBERMAN : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge les sprites du thème POKEMON (identique à BOMBERMAN sauf contours_map)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadPokemonThemeSprites(ThemeSprites sprites) {
        try {
            // === SPRITES JOUEUR ===
            // Sprites fixes
            sprites.playerFixeHaut = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_haut.png"));
            sprites.playerFixeBas = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_bas.png"));
            sprites.playerFixeGauche = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_gauche.png"));
            sprites.playerFixeDroite = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_fixe_droite.png"));
            
            // Sprites de marche
            sprites.playerMarcheHaut1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_haut1.png"));
            sprites.playerMarcheHaut2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_haut2.png"));
            sprites.playerMarcheBas1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_bas1.png"));
            sprites.playerMarcheBas2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_bas2.png"));
            sprites.playerMarcheGauche1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_gauche1.png"));
            sprites.playerMarcheGauche2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_gauche2.png"));
            sprites.playerMarcheDroite1 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_droite1.png"));
            sprites.playerMarcheDroite2 = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_marche_droite2.png"));
            
            // Sprites de mort
            for (int i = 0; i < 8; i++) {
                sprites.playerDeath[i] = new Image(getClass().getResourceAsStream("/sprites/perso/Bomberman_dies_" + (i + 1) + ".png"));
            }
            
            // Sprites de victoire
            for (int i = 0; i < 9; i++) {
                sprites.playerWin[i] = new Image(getClass().getResourceAsStream("/sprites/perso/bomberman_win_" + (i + 1) + ".png"));
            }
            
            // === SPRITES ENNEMIS ===
            for (int i = 0; i < 4; i++) {
                sprites.enemyHaut[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_haut_" + (i + 1) + ".png"));
                sprites.enemyBas[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_bas_" + (i + 1) + ".png"));
                sprites.enemyGauche[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_gauche_" + (i + 1) + ".png"));
                sprites.enemyDroite[i] = new Image(getClass().getResourceAsStream("/sprites/ennemis/Puropen_droite_" + (i + 1) + ".png"));
            }
            
            // ✨ **DIFFÉRENCE POKEMON** : Environnement spécial Pokemon
            try {
                sprites.blocNonDestructible = new Image(getClass().getResourceAsStream("/sprites/pokemon/bloc_non_destructible_pokemon.png"));
                System.out.println("✅ Bloc non destructible Pokemon chargé depuis /sprites/pokemon/");
            } catch (Exception e) {
                sprites.blocNonDestructible = new Image(getClass().getResourceAsStream("/sprites/bloc_non_destructible_48x48.png"));
                System.out.println("⚠️ Bloc non destructible Pokemon non trouvé, utilisation du bloc Bomberman");
                e.printStackTrace();
            }
            
            // Herbe (identique pour l'instant)
            sprites.herbe = new Image(getClass().getResourceAsStream("/sprites/herbe_48x48.png"));
            sprites.herbeWithOmbreBlocNonDestructible = new Image(getClass().getResourceAsStream("/sprites/herbe_with_ombre_bloc_non_destructible_48x48.png"));
            sprites.herbeWithOmbreBlocDestructible = new Image(getClass().getResourceAsStream("/sprites/herbe_with_ombre_bloc_destructible_48x48.png"));
            
            // ✨ **DIFFÉRENCE POKEMON** : Contours spéciaux Pokemon
            try {
                sprites.contoursMap = new Image(getClass().getResourceAsStream("/sprites/pokemon/contours_map_pokemon.png"));
                System.out.println("✅ Contours Pokemon chargés avec succès depuis /sprites/pokemon/");
            } catch (Exception e) {
                // Fallback vers les contours Bomberman si l'image Pokemon n'est pas trouvée
                sprites.contoursMap = new Image(getClass().getResourceAsStream("/sprites/contours_map_816x624.png"));
                System.out.println("⚠️ Contours Pokemon non trouvés, utilisation des contours Bomberman");
                e.printStackTrace();
            }
            
            // ✨ **DIFFÉRENCE POKEMON** : Blocs destructibles spéciaux Pokemon (sprite unique au lieu d'animation)
            try {
                Image blocDestructiblePokemon = new Image(getClass().getResourceAsStream("/sprites/pokemon/bloc_destructible_pokemon.png"));
                // Utiliser le même sprite Pokemon pour tous les frames d'animation (effet statique)
                for (int i = 0; i < 4; i++) {
                    sprites.blocDestructibleV1[i] = blocDestructiblePokemon;
                    sprites.blocDestructibleV2[i] = blocDestructiblePokemon;
                }
                System.out.println("✅ Blocs destructibles Pokemon chargés (sprite unique) depuis /sprites/pokemon/");
            } catch (Exception e) {
                // Fallback vers les blocs Bomberman animés
                for (int i = 0; i < 4; i++) {
                    sprites.blocDestructibleV1[i] = new Image(getClass().getResourceAsStream("/sprites/bloc_destructible_v1_" + (i + 1) + "_48x48.png"));
                    sprites.blocDestructibleV2[i] = new Image(getClass().getResourceAsStream("/sprites/bloc_destructible_v2_" + (i + 1) + "_48x48.png"));
                }
                System.out.println("⚠️ Blocs destructibles Pokemon non trouvés, utilisation des blocs Bomberman animés");
                e.printStackTrace();
            }
            
            // ✨ **DIFFÉRENCE POKEMON** : Bombes spéciales Pokemon
            try {
                sprites.bomb1 = new Image(getClass().getResourceAsStream("/sprites/pokemon/bomb_pokemon_1.png"));
                sprites.bomb2 = new Image(getClass().getResourceAsStream("/sprites/pokemon/bomb_pokemon_2.png"));
                sprites.bomb3 = new Image(getClass().getResourceAsStream("/sprites/pokemon/bomb_pokemon_3.png"));
                System.out.println("✅ Bombes Pokemon chargées avec succès depuis /sprites/pokemon/");
            } catch (Exception e) {
                // Fallback vers les bombes Bomberman si les images Pokemon ne sont pas trouvées
                sprites.bomb1 = new Image(getClass().getResourceAsStream("/sprites/bomb_1_48x48.png"));
                sprites.bomb2 = new Image(getClass().getResourceAsStream("/sprites/bomb_2_48x48.png"));
                sprites.bomb3 = new Image(getClass().getResourceAsStream("/sprites/bomb_3_48x48.png"));
                System.out.println("⚠️ Bombes Pokemon non trouvées, utilisation des bombes Bomberman");
                e.printStackTrace();
            }
            
            // === SPRITES EXPLOSIONS ===
            String[] explosionTypes = {"milieu", "droite", "gauche", "haut", "bas", "horizontale", "verticale"};
            for (int type = 0; type < 7; type++) {
                for (int frame = 0; frame < 5; frame++) {
                    sprites.explosions[type][frame] = new Image(getClass().getResourceAsStream("/sprites/resized_explosion_" + explosionTypes[type] + "_" + (frame + 1) + ".png"));
                }
            }
            
            // === SPRITES POWER-UPS ===
            sprites.extraBomb[0] = new Image(getClass().getResourceAsStream("/sprites/bonus_extra_bomb_1.png"));
            sprites.extraBomb[1] = new Image(getClass().getResourceAsStream("/sprites/bonus_extra_bomb_2.png"));
            sprites.explosionExpander[0] = new Image(getClass().getResourceAsStream("/sprites/bonus_explosion_expander_1.png"));
            sprites.explosionExpander[1] = new Image(getClass().getResourceAsStream("/sprites/bonus_explosion_expander_2.png"));
            sprites.accelerator[0] = new Image(getClass().getResourceAsStream("/sprites/bonus_accelerator_1.png"));
            sprites.accelerator[1] = new Image(getClass().getResourceAsStream("/sprites/bonus_accelerator_2.png"));
            
            // === SPRITES PORTE ===
            sprites.door[0] = new Image(getClass().getResourceAsStream("/sprites/porte_1.png"));
            sprites.door[1] = new Image(getClass().getResourceAsStream("/sprites/porte_2.png"));
            
            // === IMAGES INTERFACE ===
            sprites.intro = new Image(getClass().getResourceAsStream("/images/intro.png"));
            sprites.icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
            
            System.out.println("✅ Tous les sprites du thème POKEMON chargés avec succès");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites POKEMON : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge les sprites du thème CLASSIC (pour l'instant, copie du thème BOMBERMAN)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadClassicThemeSprites(ThemeSprites sprites) {
        // Pour l'instant, utiliser les mêmes sprites que BOMBERMAN
        // TODO: Implémenter des sprites spécifiques au thème CLASSIC
        loadBombermanThemeSprites(sprites);
        System.out.println("⚠️ Thème CLASSIC utilise temporairement les sprites BOMBERMAN");
    }
    
    /**
     * Charge les sprites du thème DARK (pour l'instant, copie du thème BOMBERMAN)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadDarkThemeSprites(ThemeSprites sprites) {
        // Pour l'instant, utiliser les mêmes sprites que BOMBERMAN
        // TODO: Implémenter des sprites spécifiques au thème DARK
        loadBombermanThemeSprites(sprites);
        System.out.println("⚠️ Thème DARK utilise temporairement les sprites BOMBERMAN");
    }
    
    /**
     * Charge les sprites du thème OCEAN (pour l'instant, copie du thème BOMBERMAN)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadOceanThemeSprites(ThemeSprites sprites) {
        // Pour l'instant, utiliser les mêmes sprites que BOMBERMAN
        // TODO: Implémenter des sprites spécifiques au thème OCEAN
        loadBombermanThemeSprites(sprites);
        System.out.println("⚠️ Thème OCEAN utilise temporairement les sprites BOMBERMAN");
    }
    
    /**
     * Charge les sprites du thème FOREST (pour l'instant, copie du thème BOMBERMAN)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadForestThemeSprites(ThemeSprites sprites) {
        // Pour l'instant, utiliser les mêmes sprites que BOMBERMAN
        // TODO: Implémenter des sprites spécifiques au thème FOREST
        loadBombermanThemeSprites(sprites);
        System.out.println("⚠️ Thème FOREST utilise temporairement les sprites BOMBERMAN");
    }
    
    /**
     * Charge les sprites du thème DESERT (pour l'instant, copie du thème BOMBERMAN)
     * @param sprites L'objet ThemeSprites à remplir
     */
    private void loadDesertThemeSprites(ThemeSprites sprites) {
        // Pour l'instant, utiliser les mêmes sprites que BOMBERMAN
        // TODO: Implémenter des sprites spécifiques au thème DESERT
        loadBombermanThemeSprites(sprites);
        System.out.println("⚠️ Thème DESERT utilise temporairement les sprites BOMBERMAN");
    }
    
    /**
     * Libère tous les sprites du cache
     */
    public void clearCache() {
        spriteCache.clear();
        System.out.println("Cache des sprites vidé");
    }
    
    /**
     * Précharge tous les sprites de tous les thèmes
     */
    public void preloadAllThemes() {
        System.out.println("Préchargement de tous les thèmes...");
        for (Theme theme : Theme.values()) {
            loadThemeSprites(theme);
        }
        System.out.println("Préchargement terminé");
    }
} 