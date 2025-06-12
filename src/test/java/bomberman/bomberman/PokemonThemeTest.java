package bomberman.bomberman;

/**
 * Test spécifique pour vérifier le fonctionnement du thème Pokemon
 */
public class PokemonThemeTest {
    
    public static void main(String[] args) {
        System.out.println("=== TEST THÈME POKEMON ===");
        
        // Obtenir l'instance du SpriteManager
        SpriteManager spriteManager = SpriteManager.getInstance();
        
        // Tester le changement vers le thème Pokemon
        System.out.println("Changement vers le thème Pokemon...");
        spriteManager.setTheme(Theme.POKEMON);
        
        // Vérifier que le thème est bien Pokemon
        if (spriteManager.getCurrentTheme() == Theme.POKEMON) {
            System.out.println("✅ Thème Pokemon activé avec succès");
        } else {
            System.err.println("❌ Erreur : thème actuel = " + spriteManager.getCurrentTheme().getDisplayName());
        }
        
        // Obtenir les sprites du thème Pokemon
        SpriteManager.ThemeSprites pokemonSprites = spriteManager.getCurrentSprites();
        
        // Vérifier que les sprites sont chargés
        boolean allSpritesLoaded = true;
        
        // Vérifier les sprites de base (doivent être identiques à Bomberman)
        if (pokemonSprites.playerFixeBas == null) {
            System.err.println("❌ Sprite playerFixeBas non chargé");
            allSpritesLoaded = false;
        } else {
            System.out.println("✅ Sprite playerFixeBas chargé : " + pokemonSprites.playerFixeBas.getWidth() + "x" + pokemonSprites.playerFixeBas.getHeight());
        }
        
        if (pokemonSprites.bomb1 == null) {
            System.err.println("❌ Sprite bomb1 non chargé");
            allSpritesLoaded = false;
        } else {
            System.out.println("✅ Sprite bomb1 chargé : " + pokemonSprites.bomb1.getWidth() + "x" + pokemonSprites.bomb1.getHeight());
        }
        
        // Vérifier le sprite spécifique Pokemon (contours)
        if (pokemonSprites.contoursMap == null) {
            System.err.println("❌ Sprite contoursMap Pokemon non chargé");
            allSpritesLoaded = false;
        } else {
            System.out.println("✅ Sprite contoursMap Pokemon chargé : " + pokemonSprites.contoursMap.getWidth() + "x" + pokemonSprites.contoursMap.getHeight());
        }
        
        // Comparer avec le thème Bomberman pour vérifier la différence
        System.out.println("\n=== COMPARAISON AVEC THÈME BOMBERMAN ===");
        spriteManager.setTheme(Theme.BOMBERMAN);
        SpriteManager.ThemeSprites bombermanSprites = spriteManager.getCurrentSprites();
        
        // Vérifier que les sprites de base sont identiques
        boolean samePlayerSprites = (pokemonSprites.playerFixeBas == bombermanSprites.playerFixeBas);
        boolean sameBombSprites = (pokemonSprites.bomb1 == bombermanSprites.bomb1);
        
        System.out.println("Sprites joueur identiques : " + (samePlayerSprites ? "✅ OUI" : "❌ NON"));
        System.out.println("Sprites bombes identiques : " + (sameBombSprites ? "✅ OUI" : "❌ NON"));
        
        // Vérifier que les contours sont différents (ou identiques si image temporaire)
        boolean sameContours = (pokemonSprites.contoursMap == bombermanSprites.contoursMap);
        System.out.println("Contours identiques : " + (sameContours ? "⚠️ OUI (image temporaire)" : "✅ NON (image Pokemon différente)"));
        
        // Retour au thème Pokemon
        spriteManager.setTheme(Theme.POKEMON);
        
        if (allSpritesLoaded) {
            System.out.println("\n✅ TOUS LES TESTS RÉUSSIS - Thème Pokemon fonctionne correctement !");
            System.out.println("🎮 Le thème Pokemon est prêt à être utilisé dans le jeu !");
            System.out.println("📝 Pour utiliser la vraie image Pokemon, remplacez :");
            System.out.println("   src/main/resources/sprites/themes/pokemon/contours_map_pokemon.png");
        } else {
            System.out.println("\n❌ CERTAINS TESTS ONT ÉCHOUÉ - Vérifier le chargement des sprites Pokemon");
        }
    }
} 