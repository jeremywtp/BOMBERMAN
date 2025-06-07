# 🖼️ Intégration du Sprite de Contours de Map

## 📋 Vue d'ensemble

Ce document explique comment intégrer votre sprite `contours_map.png` comme arrière-plan des bordures de votre jeu Bomberman. Deux approches sont disponibles selon vos besoins.

## 🎯 Approche 1 : Modification GridRenderer (Recommandée)

### ✅ Avantages
- **Simple à utiliser** : Integration transparente dans l'architecture existante
- **Performance optimale** : Un seul canvas, pas de composants supplémentaires
- **Contrôle précis** : L'image est dessinée exactement à la position et taille de la grille

### 📍 Localisation
- **Fichier modifié** : `src/main/java/bomberman/bomberman/GridRenderer.java`
- **Méthodes ajoutées** :
  - `loadContoursMapImage()` : Charge l'image depuis `/sprites/contours_map.png`
  - `renderContoursMapBackground()` : Dessine l'image en arrière-plan

### 🛠️ Configuration actuelle
```java
// L'image est automatiquement redimensionnée pour correspondre à la grille
double gameAreaWidth = grid.getColumns() * CELL_SIZE;   // 15 * 48 = 720px
double gameAreaHeight = grid.getRows() * CELL_SIZE;     // 11 * 48 = 528px

// Position après l'ATH et le timer
double gameAreaY = GRID_VERTICAL_OFFSET;  // 100px du haut
```

### 📊 État actuel
- ✅ **ACTIVE** : Cette approche est actuellement utilisée
- ✅ **Testée** : Image chargée avec succès (dimensions détectées : 272x176)
- ✅ **Fonctionnelle** : L'image s'affiche automatiquement derrière la grille

---

## 🎯 Approche 2 : Architecture StackPane (Avancée)

### ✅ Avantages
- **Flexibilité maximale** : Contrôle précis du positionnement avec `translateX/Y`
- **Séparation claire** : ImageView dédié pour l'arrière-plan, Canvas pour le jeu
- **Facilité de modification** : Ajustements visuels sans recompilation du rendu

### 📍 Localisation
- **Fichier modifié** : `src/main/java/bomberman/bomberman/Launcher.java`
- **Méthode ajoutée** : `setupStackPaneApproach(Canvas canvas)`

### 🛠️ Activation de l'Approche 2

Pour activer cette approche, modifiez dans `Launcher.java` :

```java
// 🎯 **APPROCHE 1** : Canvas simple (active par défaut)
// StackPane root = new StackPane();
// root.getChildren().add(canvas);

// 🎯 **APPROCHE 2** : StackPane avec ImageView (décommentez cette ligne)
root = setupStackPaneApproach(canvas);
```

### 📊 Configuration StackPane
```java
// Position précise de l'ImageView
contoursMapImageView.setTranslateY(50); // Décalage pour alignement avec grille

// Redimensionnement exact
contoursMapImageView.setFitWidth(720);  // 15 * 48 px
contoursMapImageView.setFitHeight(528); // 11 * 48 px

// Qualité pixel-perfect
contoursMapImageView.setSmooth(false);
contoursMapImageView.setPreserveRatio(false);
```

---

## 🔧 Spécifications Techniques

### 📐 Dimensions de la Grille
- **Colonnes** : 15 tuiles × 48px = **720px de largeur**
- **Lignes** : 11 tuiles × 48px = **528px de hauteur**
- **Position Y** : 100px depuis le haut (après ATH + timer)

### 🖼️ Votre Sprite
- **Chemin** : `/sprites/contours_map.png`
- **Dimensions détectées** : 272×176 pixels
- **Redimensionnement** : Automatique vers 720×528 px pour correspondre à la grille

### 🎨 Qualité Visuelle
- **Pas de lissage** : `setSmooth(false)` pour préserver les pixels
- **Étirement contrôlé** : `setPreserveRatio(false)` pour correspondance exacte
- **Rendu pixel-perfect** : Image affichée avant la grille pour un rendu net

---

## 🚀 Utilisation Recommandée

### Pour la plupart des cas : **Approche 1**
```bash
# Aucune configuration nécessaire - déjà active !
mvn compile exec:java -Dexec.mainClass="bomberman.bomberman.Launcher"
```

### Pour un contrôle avancé : **Approche 2**
1. Décommentez la ligne dans `Launcher.java` :
   ```java
   root = setupStackPaneApproach(canvas);
   ```
2. Compilez et lancez :
   ```bash
   mvn compile exec:java -Dexec.mainClass="bomberman.bomberman.Launcher"
   ```

---

## 🐛 Dépannage

### L'image ne s'affiche pas
```bash
# Vérifiez que le fichier existe
ls -la src/main/resources/sprites/contours_map.png

# Vérifiez les logs de chargement
mvn compile exec:java -Dexec.mainClass="bomberman.bomberman.Launcher" | grep -i contours
```

### L'image est floue ou étirée
- ✅ **Approche 1** : L'image est automatiquement redimensionnée avec `drawImage()`
- ✅ **Approche 2** : `setSmooth(false)` désactive le lissage pour un rendu net

### Problèmes de positionnement
- **Approche 1** : Modifiez `gameAreaY` dans `renderContoursMapBackground()`
- **Approche 2** : Ajustez `setTranslateY()` dans `setupStackPaneApproach()`

---

## 📝 Résumé

🎯 **Approche 1 (Active)** : Votre sprite `contours_map.png` est automatiquement chargé et affiché comme arrière-plan de votre grille Bomberman. L'image est redimensionnée de 272×176 vers 720×528 pixels pour correspondre parfaitement à votre grille de 15×11 tuiles.

🎯 **Approche 2 (Optionnelle)** : Architecture StackPane disponible pour un contrôle plus fin du positionnement et des effets visuels.

**Résultat** : Votre sprite de contours s'affiche pixel par pixel comme bordure visuelle, exactement comme souhaité ! 🎮✨ 