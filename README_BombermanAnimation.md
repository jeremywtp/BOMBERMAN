# BombermanAnimator - Système d'animation pour Bomberman

## 🎯 Objectif

La classe `BombermanAnimator` permet d'animer le personnage Bomberman avec des sprites directionnels de marche en **pixel perfect**, conservant les proportions originales et gérant automatiquement le centrage dans une case de 48×48 pixels.

## ✨ Fonctionnalités avancées

- **Animation fluide** : 2 frames par direction avec Timeline JavaFX
- **Rendu pixel perfect** : Désactivation du lissage pour un affichage net
- **Conservation des proportions** : Les sprites ne sont jamais déformés
- **Centrage automatique** : Chaque sprite est centré dans la case 48×48
- **Support multi-directions** : 4 directions avec sprites fixes et animés
- **Gestion automatique** : Démarrage/arrêt selon les mouvements du joueur
- **Cache optimisé** : Recalcul uniquement quand nécessaire
- **Effets visuels** : Support pour invincibilité, transparence, etc.

## 📐 Sprites supportés

### Sprites fixes (état immobile)
- `bomberman_fixe_haut.png`
- `bomberman_fixe_bas.png` 
- `bomberman_fixe_gauche.png`
- `bomberman_fixe_droite.png`

### Sprites d'animation (2 frames par direction)
- **Haut** : `bomberman_marche_haut1.png`, `bomberman_marche_haut2.png`
- **Bas** : `bomberman_marche_bas1.png`, `bomberman_marche_bas2.png`
- **Gauche** : `bomberman_marche_gauche1.png`, `bomberman_marche_gauche2.png`
- **Droite** : `bomberman_marche_droite1.png`, `bomberman_marche_droite2.png`

### Dimensions variables supportées
Les sprites ont des dimensions différentes (ex: 15×22, 15×24, 16×24) mais sont tous affichés correctement dans une case logique de 48×48 pixels sans déformation.

## 🔧 Utilisation

### Création et initialisation
```java
// Créer l'animateur
BombermanAnimator animator = new BombermanAnimator();

// Définir la position (cellule logique + décalage)
animator.setPosition(cellX, cellY, offsetX, offsetY);

// Définir la direction
animator.setDirection("bas"); // "haut", "bas", "gauche", "droite"
```

### Gestion de l'animation
```java
// Démarrer l'animation de marche
animator.startWalking();

// Arrêter l'animation (retour au sprite fixe)
animator.stopWalking();

// Vérifier l'état
boolean isWalking = animator.isWalking();
```

### Rendu
```java
// Rendu simple
animator.render(graphicsContext);

// Rendu avec effets (invincibilité, transparence)
animator.renderWithEffects(graphicsContext, isInvincible, alpha);
```

### Intégration avec Player
```java
// Dans la boucle de jeu
player.updateWalkingState(); // Met à jour l'état de marche

// Dans le renderer
if (player.isWalking() && !animator.isWalking()) {
    animator.startWalking();
} else if (!player.isWalking() && animator.isWalking()) {
    animator.stopWalking();
}
```

## ⚙️ Configuration de l'animation

### Durée des frames
- **Intervalle** : 200ms entre les frames (configurable via `ANIMATION_DURATION_MS`)
- **Fluidité** : Animation en boucle avec Timeline JavaFX
- **Performance** : Cache intelligent pour éviter les recalculs

### Temporisation de marche
- **Démarrage** : Immédiat lors du mouvement du joueur
- **Arrêt** : 400ms après le dernier mouvement (configurable)
- **States** : Sprites fixes quand immobile, sprites animés quand en marche

## 🛠️ Classes principales

### BombermanAnimator
- **Responsabilité** : Gestion complète de l'animation
- **Méthodes clés** : `startWalking()`, `stopWalking()`, `setDirection()`, `render()`
- **Cache** : Optimisation des calculs de rendu

### Player (modifications)
- **Nouveaux champs** : `isWalking`, `lastMovementTime`
- **Nouvelles méthodes** : `updateWalkingState()`, `isWalking()`
- **Intégration** : Déclenchement automatique lors des mouvements

### GridRenderer (modifications)
- **Remplacement** : `BombermanSprite` → `BombermanAnimator`
- **Logique** : Synchronisation animation/état du joueur
- **Rendu** : Appel des méthodes d'animation appropriées

## 🧪 Tests et démonstration

### Programme de test complet
```bash
# Lancer le test d'animation isolé
java bomberman.bomberman.BombermanAnimationTest
```

**Contrôles du test :**
- **Flèches** : Changer direction + déclencher animation temporaire
- **Espace** : Basculer animation manuelle (marche continue)
- **TAB** : Afficher informations détaillées (frame, position, etc.)
- **Echap** : Quitter

### Test dans le jeu complet
```bash
# Lancer le jeu complet avec animation
mvn javafx:run
```

L'animation se déclenche automatiquement lors des déplacements dans le jeu principal.

## 📊 Informations techniques

### Performance
- **Chargement** : Sprites chargés une seule fois de manière statique
- **Animation** : Timeline JavaFX optimisée
- **Rendu** : Cache des paramètres de positionnement

### Compatibilité
- **JavaFX** : Toutes versions avec Canvas et Timeline
- **Formats** : PNG avec transparence
- **Pixel Art** : Rendu sans lissage preservé

### Logs de debug
```
Sprites Bomberman (fixes + animation) chargés avec succès :
- Fixe Haut: 15.0x22.0
- Fixe Bas: 15.0x24.0
- Marche Bas1: 15.0x24.0
- Marche Bas2: 15.0x24.0
Animation chargée pour toutes les directions (2 frames chacune)

Animation de marche démarrée - Direction: bas
Animation de marche arrêtée - Retour au sprite fixe
```

## 🔄 Évolutions possibles

### Animations supplémentaires
- Animation de mort avec plusieurs frames
- Animation d'idle avec clignotement occasionnel
- Animation de pose de bombe

### Effets visuels
- Animation de dash/vitesse
- Particules lors de la marche
- Ombres dynamiques

### Performance
- Atlas de sprites pour optimiser le chargement
- Pool d'objets pour les animations temporaires
- Culling des animations hors écran

## 🎮 Résultat

Le système produit une **animation fluide et authentique** qui :
- ✅ Respecte le style pixel art original de Bomberman
- ✅ S'active automatiquement selon les mouvements du joueur
- ✅ Conserve les proportions de tous les sprites
- ✅ Offre un rendu pixel perfect sans flou
- ✅ Fonctionne parfaitement dans le moteur de jeu existant

L'animation apporte une **amélioration significative** de l'expérience visuelle tout en conservant la performance et la simplicité d'utilisation. 