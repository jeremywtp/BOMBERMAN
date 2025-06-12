package bomberman.bomberman;

/**
 * Test simple pour vérifier le fonctionnement du SpriteManager
 */
public class SpriteManagerTest {
    
    public static void main(String[] args) {
        System.out.println("=== TEST SPRITEMANAGER ===");
        
        // Obtenir l'instance du SpriteManager
        SpriteManager spriteManager = SpriteManager.getInstance();
        
        // Tester le thème par défaut
        System.out.println("Thème par défaut : " + spriteManager.getCurrentTheme().getDisplayName());
        
        // Tester le chargement des sprites du thème BOMBERMAN
        SpriteManager.ThemeSprites sprites = spriteManager.getCurrentSprites();
        
        // Vérifier que quelques sprites sont chargés
        boolean spritesLoaded = true;
        
        if (sprites.playerFixeBas == null) {
            System.err.println("❌ Sprite playerFixeBas non chargé");
            spritesLoaded = false;
        } else {
            System.out.println("✅ Sprite playerFixeBas chargé : " + sprites.playerFixeBas.getWidth() + "x" + sprites.playerFixeBas.getHeight());
        }
        
        if (sprites.bomb1 == null) {
            System.err.println("❌ Sprite bomb1 non chargé");
            spritesLoaded = false;
        } else {
            System.out.println("✅ Sprite bomb1 chargé : " + sprites.bomb1.getWidth() + "x" + sprites.bomb1.getHeight());
        }
        
        if (sprites.herbe == null) {
            System.err.println("❌ Sprite herbe non chargé");
            spritesLoaded = false;
        } else {
            System.out.println("✅ Sprite herbe chargé : " + sprites.herbe.getWidth() + "x" + sprites.herbe.getHeight());
        }
        
        // Tester le changement de thème
        System.out.println("\n=== TEST CHANGEMENT DE THÈME ===");
        spriteManager.setTheme(Theme.CLASSIC);
        System.out.println("Thème changé vers : " + spriteManager.getCurrentTheme().getDisplayName());
        
        spriteManager.setTheme(Theme.DARK);
        System.out.println("Thème changé vers : " + spriteManager.getCurrentTheme().getDisplayName());
        
        spriteManager.setTheme(Theme.BOMBERMAN);
        System.out.println("Thème changé vers : " + spriteManager.getCurrentTheme().getDisplayName());
        
        if (spritesLoaded) {
            System.out.println("\n✅ TOUS LES TESTS RÉUSSIS - SpriteManager fonctionne correctement !");
        } else {
            System.out.println("\n❌ CERTAINS TESTS ONT ÉCHOUÉ - Vérifier le chargement des sprites");
        }
    }
} 