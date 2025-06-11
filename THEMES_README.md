# 🎨 Système de Thèmes - Bomberman

## Vue d'ensemble

Le jeu Bomberman dispose maintenant d'un système de sélection de thèmes visuels qui permet aux joueurs de personnaliser l'apparence du jeu selon leurs préférences.

## Fonctionnalités

### 🎯 Thèmes Disponibles

1. **Classique** - Le thème original de Bomberman
   - Couleurs vives et contrastées
   - Style rétro authentique

2. **Sombre** - Thème moderne et élégant
   - Couleurs sombres avec accents lumineux
   - Parfait pour les sessions de jeu prolongées

3. **Océan** - Thème aquatique
   - Couleurs bleues et marines
   - Ambiance apaisante

4. **Forêt** - Thème naturel
   - Couleurs vertes et organiques
   - Ambiance forestière

5. **Désert** - Thème chaud
   - Couleurs sablonneuses et dorées
   - Ambiance aride et ensoleillée

### 🎮 Navigation dans le Menu des Thèmes

#### Accès au Menu
- Depuis le menu principal, sélectionnez **"THEMES"** avec les flèches ↑/↓
- Appuyez sur **ENTRÉE** pour ouvrir le sélecteur de thèmes

#### Contrôles dans le Sélecteur
- **← →** : Naviguer entre les thèmes disponibles
- **ENTRÉE** : Confirmer la sélection et retourner au menu principal
- **ÉCHAP** : Annuler et retourner au menu principal sans sauvegarder

### 💾 Sauvegarde Automatique

- Le thème sélectionné est automatiquement sauvegardé dans le fichier `theme.txt`
- Au prochain lancement du jeu, votre thème préféré sera automatiquement chargé
- Si le fichier de sauvegarde est corrompu, le thème **Classique** sera utilisé par défaut

### 🎨 Aperçu Visuel

Le menu de sélection des thèmes affiche :
- Le nom du thème actuel
- Un aperçu des trois couleurs principales du thème :
  - **Couleur principale** : Utilisée pour les éléments principaux
  - **Couleur secondaire** : Utilisée pour les éléments de fond
  - **Couleur d'accent** : Utilisée pour les éléments de mise en évidence

## Architecture Technique

### Classes Principales

#### `Theme.java`
- Énumération définissant tous les thèmes disponibles
- Chaque thème contient :
  - Un nom d'affichage
  - Trois couleurs (principale, secondaire, accent)
  - Méthodes de navigation (suivant/précédent)

#### `ThemeSelector.java`
- Gestionnaire de sélection et sauvegarde des thèmes
- Fonctionnalités :
  - Chargement automatique au démarrage
  - Sauvegarde automatique lors des changements
  - Navigation cyclique entre les thèmes

#### Intégration dans `Launcher.java`
- Nouvel état de jeu : `THEME_SELECTION`
- Gestion des inputs pour la navigation
- Intégration dans le menu principal

#### Rendu dans `GridRenderer.java`
- Méthode `renderThemeSelectionMenu()` pour l'affichage
- Interface utilisateur intuitive avec aperçu visuel

### Fichiers de Sauvegarde

- **`theme.txt`** : Stocke le thème sélectionné (nom de l'énumération)
- Créé automatiquement lors de la première sélection
- Lecture automatique au démarrage du jeu

## Utilisation

1. **Lancer le jeu**
2. **Naviguer vers "THEMES"** dans le menu principal
3. **Sélectionner "THEMES"** avec ENTRÉE
4. **Choisir un thème** avec ← →
5. **Confirmer** avec ENTRÉE
6. **Profiter** de votre nouveau thème !

## Notes de Développement

### Extensibilité
- Ajouter de nouveaux thèmes est simple : il suffit d'ajouter une entrée dans l'énumération `Theme`
- Les couleurs sont définies en format hexadécimal pour une compatibilité maximale
- Le système est conçu pour être facilement étendu avec des textures ou des sons thématiques

### Compatibilité
- Compatible avec tous les modes de jeu (Normal, Coopération, Battle)
- Aucun impact sur les performances de jeu
- Sauvegarde indépendante du score et des autres paramètres

---

*Développé pour améliorer l'expérience utilisateur et la personnalisation du jeu Bomberman.* 