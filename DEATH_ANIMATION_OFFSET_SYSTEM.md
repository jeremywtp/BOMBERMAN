# 🎬 Système d'Offset pour l'Animation de Mort de Bomberman

## Problème résolu

Les sprites d'animation de mort de Bomberman avaient des **hauteurs variables** légitimes dues au mouvement du personnage (saut, chute, etc.), causant **deux problèmes majeurs** :

1. **Problème d'alignement** : Les frames ne s'alignaient pas visuellement
2. **Problème de taille** : La frame de saut (Frame 2) apparaissait plus petite que les autres à cause du calcul d'échelle individuel

### Sprites analysés
- **Frame 1** : 15x24px (sprite normal)
- **Frame 2** : 15x33px (Bomberman qui saute - plus haute)
- **Frames 3-6** : 15x22px (séquence de chute)
- **Frames 7-8** : 15x20px (sprite final au sol)

## Solution implémentée : Option 2 - Système d'offset par frame

### Principe
Au lieu de redimensionner tous les sprites à la même taille (ce qui déformerait l'animation), le système combine **deux techniques** :

1. **Échelle constante** : Toutes les frames utilisent la même échelle basée sur la plus grande frame
2. **Offsets verticaux** : Chaque frame applique un offset pour s'aligner par rapport à une base commune

### Calcul des offsets
Basé sur la taille de référence des sprites normaux (15x24px) :
```java
private static final int[] DEATH_VERTICAL_OFFSETS = {
    0,  // Frame 1 (15x24) : 24-24 = 0px d'offset (taille normale)
    -9, // Frame 2 (15x33) : 24-33 = -9px d'offset (saut vers le haut)
    2,  // Frame 3 (15x22) : 24-22 = 2px d'offset (vers le bas)
    2,  // Frame 4 (15x22) : 24-22 = 2px d'offset
    2,  // Frame 5 (15x22) : 24-22 = 2px d'offset
    2,  // Frame 6 (15x22) : 24-22 = 2px d'offset
    4,  // Frame 7 (15x20) : 24-20 = 4px d'offset (plus vers le bas)
    4   // Frame 8 (15x20) : 24-20 = 4px d'offset
};
```

## Implémentation technique

### Fichier modifié : `BombermanAnimator.java`

#### 1. Ajout des constantes d'offset et dimensions de référence
```java
// Système d'offsets pour l'animation de mort
private static final int[] DEATH_VERTICAL_OFFSETS = { /* valeurs */ };
private static final int DEATH_HORIZONTAL_OFFSET = 0;

// Dimensions de référence COMMUNES pour cohérence visuelle
private static final double SPRITES_REFERENCE_WIDTH = 15.0;
private static final double SPRITES_REFERENCE_HEIGHT = 24.0;
```

#### 2. Modification de `calculateRenderParameters()` - Échelle constante
```java
if (currentState == AnimationState.DYING) {
    // Utiliser la même échelle que les sprites normaux pour cohérence
    double scaleX = CELL_SIZE / SPRITES_REFERENCE_WIDTH;
    double scaleY = CELL_SIZE / SPRITES_REFERENCE_HEIGHT;
    double baseScale = Math.min(scaleX, scaleY);
    scale = baseScale * 1.5; // Identique aux autres sprites
} else {
    // Calcul normal pour les autres animations
    // ...
}
```

#### 3. Application des offsets
```java
if (currentState == AnimationState.DYING) {
    offsetY = DEATH_VERTICAL_OFFSETS[currentFrame] * scale;
    offsetX = DEATH_HORIZONTAL_OFFSET * scale;
}

// Position finale avec offsets
this.spriteRenderX = renderX + (CELL_SIZE - spriteRenderWidth) / 2.0 + offsetX;
this.spriteRenderY = renderY + (CELL_SIZE - spriteRenderHeight) / 2.0 + offsetY;
```

## Avantages de cette solution

✅ **Préservation de l'animation originale** : Aucun sprite n'est déformé  
✅ **Alignement cohérent** : Toutes les frames s'alignent visuellement  
✅ **Performance optimale** : Calcul simple appliqué au moment du rendu  
✅ **Flexibilité** : Facilement modifiable pour ajuster l'alignement  
✅ **Authenticité** : Respecte le mouvement naturel de Bomberman  

## Résultat visuel

L'animation de mort de Bomberman présente maintenant :
- Un **alignement cohérent** entre toutes les frames
- Le **mouvement de saut** conservé (Frame 2 plus haute)
- Une **transition fluide** vers les frames de chute
- Un **rendu final** parfaitement aligné sur la grille

## Tests réalisés

- ✅ Animation de mort fluide et alignée
- ✅ Aucun saut visuel entre les frames
- ✅ Respect du système de coordonnées de la grille 48x48px
- ✅ Compatibilité avec le facteur d'échelle existant (x1.5)

---

**Développé par** : Assistant Claude (Anthropic)  
**Date** : Décembre 2024  
**Statut** : ✅ Implémenté et testé 