# BombermanSprite - Système de sprites pour Bomberman

## 🎯 Objectif

La classe `BombermanSprite` permet d'afficher le personnage Bomberman avec ses sprites directionnels de manière **pixel perfect**, en conservant les proportions originales et en centrant automatiquement le sprite dans une case de 48×48 pixels.

## ✨ Fonctionnalités

- **Rendu pixel perfect** : Désactivation du lissage pour un affichage net
- **Conservation des proportions** : Les sprites ne sont jamais déformés
- **Centrage automatique** : Chaque sprite est centré dans la case 48×48
- **Support multi-directions** : 4 directions (haut, bas, gauche, droite)
- **Cache optimisé** : Recalcul uniquement quand nécessaire
- **Effets visuels** : Support pour invincibilité, transparence, etc.

## 📐 Dimensions des sprites

Les sprites ont des dimensions différentes (conservées intactes) :
- **Haut** : 15×22 pixels
- **Bas** : 15×24 pixels  
- **Gauche** : 16×24 pixels
- **Droite** : 16×24 pixels

Tous sont automatiquement centrés dans une case de 48×48 pixels.

## 🚀 Utilisation de base

```java
// 1. Créer une instance
BombermanSprite bombermanSprite = new BombermanSprite();

// 2. Définir la direction
bombermanSprite.setDirection("bas"); // "haut", "bas", "gauche", "droite"

// 3. Positionner le sprite (coordonnées de grille)
bombermanSprite.setPosition(
    cellX,           // Position logique en colonne
    cellY,           // Position logique en ligne  
    offsetX,         // Décalage horizontal (centrage fenêtre)
    offsetY          // Décalage vertical (interface)
);

// 4. Dessiner le sprite
bombermanSprite.render(gc); // gc = GraphicsContext
```

## 🎨 Rendu avec effets

```java
// Rendu avec effets d'invincibilité et transparence
bombermanSprite.renderWithEffects(
    gc,              // GraphicsContext
    isInvincible,    // true = effet clignotant
    alpha            // Transparence (0.0 à 1.0)
);
```

## 📊 Informations de rendu

```java
// Obtenir les informations après rendu
String direction = bombermanSprite.getCurrentDirection();
double width = bombermanSprite.getRenderWidth();      // Largeur à l'écran
double height = bombermanSprite.getRenderHeight();    // Hauteur à l'écran
double x = bombermanSprite.getRenderX();              // Position X écran
double y = bombermanSprite.getRenderY();              // Position Y écran
```

## 🔧 Intégration dans le jeu

### Dans GridRenderer

```java
public class GridRenderer {
    private BombermanSprite bombermanSprite;
    
    public GridRenderer(Canvas canvas, Grid grid) {
        // ... autres initialisations ...
        bombermanSprite = new BombermanSprite();
    }
    
    private void renderPlayer(Player player) {
        // Calculer les décalages pour centrer dans la fenêtre
        double horizontalOffset = (canvas.getWidth() - 720) / 2.0;
        
        // Mettre à jour la direction du sprite
        bombermanSprite.setDirection(player.getCurrentDirection());
        
        // Mettre à jour la position du sprite
        bombermanSprite.setPosition(
            player.getX(), 
            player.getY(), 
            horizontalOffset, 
            GRID_VERTICAL_OFFSET
        );
        
        // Dessiner le sprite avec effets
        bombermanSprite.renderWithEffects(gc, player.isInvincible(), 1.0);
    }
}
```

### Dans Player

```java
public class Player {
    private String currentDirection = "bas"; // Direction par défaut
    
    public boolean moveUp(Grid grid, BombCollisionChecker bombCollisionChecker) {
        if (!isAlive()) return false;
        this.currentDirection = "haut"; // Mettre à jour la direction
        return tryMove(x, y - 1, grid, bombCollisionChecker);
    }
    
    // ... autres méthodes de mouvement similaires ...
    
    public String getCurrentDirection() {
        return currentDirection;
    }
}
```

## 🧪 Test standalone

Un programme de test `BombermanSpriteTest` est disponible pour tester la classe de manière isolée :

```bash
# Compiler le test
mvn clean compile

# Lancer le test (modification manuelle du main dans le pom.xml nécessaire)
# Ou lancer directement depuis l'IDE : BombermanSpriteTest.main()
```

### Contrôles du test
- **Flèches directionnelles** : Changer la direction du sprite
- **Espace** : Afficher les informations de rendu dans la console
- **Échap** : Quitter le test

## 🔍 Avantages techniques

1. **Performance** : Cache intelligent, recalcul uniquement si nécessaire
2. **Qualité visuelle** : Rendu pixel perfect sans lissage
3. **Flexibilité** : Support des effets visuels (clignotement, transparence)
4. **Simplicité** : API claire et facile à utiliser
5. **Robustesse** : Gestion d'erreur pour les sprites manquants

## 📁 Fichiers du sprite

Les sprites doivent être placés dans `/src/main/resources/sprites/perso/` :
- `bomberman_fixe_haut.png`
- `bomberman_fixe_bas.png`
- `bomberman_fixe_gauche.png`
- `bomberman_fixe_droite.png`

## 🎮 Intégration complète

Cette classe remplace complètement l'ancien système de rendu du joueur basé sur `fillRect()`. Le personnage Bomberman est maintenant affiché avec ses vrais sprites, tout en conservant tous les effets visuels existants (invincibilité, bouclier, etc.).

Le système s'intègre parfaitement dans l'architecture existante du jeu sans modification majeure du code de logique métier. 