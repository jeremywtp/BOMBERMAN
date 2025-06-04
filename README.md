# Bomberman Base - Architecture Évolutive

## Description
Projet JavaFX 17.0.6 avec Java 23.0.2 implémentant une base évolutive pour un jeu Bomberman. Cette version inclut maintenant un joueur déplaçable avec contrôles clavier.

## Architecture du Projet

Le projet suit une architecture MVC (Model-View-Controller) simplifiée avec une séparation claire des responsabilités :

### Structure des Classes

#### 1. `Launcher.java`
- **Rôle** : Classe principale de l'application
- **Responsabilités** :
  - Lance l'application JavaFX
  - Initialise la fenêtre (480×352 pixels)
  - Crée les instances du modèle (`Grid`), du joueur (`Player`) et du renderer (`GridRenderer`)
  - Configure la scène JavaFX et gère les événements clavier
- **Nouveauté** : Gestion des déplacements du joueur avec les flèches directionnelles

#### 2. `Grid.java`
- **Rôle** : Modèle de données de la grille
- **Responsabilités** :
  - Stocke l'état logique de chaque case (SOLID ou EMPTY)
  - Génère le pattern Bomberman (bordures solides, alternance intérieure)
  - Fournit des méthodes d'accès aux données de la grille
- **Nouveauté** : Méthode `isAccessible()` pour vérifier si une case est traversable par le joueur
- **Évolutions futures** : Gérera les bombes, power-ups, et destruction de blocs

#### 3. `GridRenderer.java`
- **Rôle** : Rendu graphique de la grille et du joueur
- **Responsabilités** :
  - Dessine la grille sur un Canvas JavaFX
  - Gère les couleurs (gris #505050 pour solide, noir #000000 pour vide, bleu clair #00AAFF pour le joueur)
  - Taille des cellules : 32×32 pixels
- **Nouveauté** : Rendu du joueur légèrement plus petit (26×26 pixels) pour un meilleur aspect visuel
- **Évolutions futures** : Animations, sprites, effets visuels

#### 4. `Player.java` ✨ **NOUVEAU**
- **Rôle** : Représentation et logique du joueur
- **Responsabilités** :
  - Stocke la position du joueur (coordonnées logiques x, y)
  - Gère les déplacements dans les 4 directions avec validation des collisions
  - Empêche les déplacements vers les cases solides ou hors de la grille
- **Évolutions futures** : Gestion des bombes, vies, power-ups

## Installation et Exécution

### Prérequis
- Java 23.0.2
- Maven 3.x
- JavaFX 17.0.6 (géré automatiquement par Maven)

### Compilation
```bash
mvn clean compile
```

### Exécution
```bash
mvn clean javafx:run
```

## Caractéristiques Actuelles

- **Fenêtre** : 480×352 pixels, non redimensionnable
- **Grille** : 15×11 cases (32 pixels par case)
- **Pattern Bomberman** :
  - Contour entièrement en blocs solides
  - Alternance de blocs solides toutes les 2 cases (positions paires)
  - Cases vides pour le reste
- **Joueur** ✨ **NOUVEAU** :
  - Carré bleu clair (#00AAFF) de 26×26 pixels
  - Position de départ : case (1,1)
  - Déplaçable avec les flèches directionnelles
  - Collision avec les blocs solides et bordures

## Contrôles

- **Flèche Haut** : Déplacer le joueur vers le haut
- **Flèche Bas** : Déplacer le joueur vers le bas  
- **Flèche Gauche** : Déplacer le joueur vers la gauche
- **Flèche Droite** : Déplacer le joueur vers la droite

## Évolutions Prévues

### Phase 2 - Bombes et Explosions ⬅️ **PROCHAINE ÉTAPE**
- Ajout d'une classe `Bomb` pour la logique des bombes
- Ajout d'une classe `Explosion` pour gérer les effets d'explosion
- Extension de `Grid` pour gérer les blocs destructibles
- Contrôle : barre d'espace pour poser des bombes

### Phase 3 - Ennemis et IA
- Ajout d'une classe `Enemy` avec comportements simples
- Système de collision et de vies

### Phase 4 - Power-ups et Score
- Système de power-ups (portée de bombe, vitesse, etc.)
- Interface utilisateur pour le score et les vies

## Structure des Fichiers
```
src/main/java/bomberman/bomberman/
├── Launcher.java       # Point d'entrée de l'application
├── Grid.java          # Modèle de données de la grille
├── GridRenderer.java  # Rendu graphique
└── Player.java        # ✨ Logique et position du joueur
```

## Conventions de Code

- **Taille des cellules** : 32×32 pixels (constante dans `GridRenderer`)
- **Taille du joueur** : 26×26 pixels avec décalage de 3 pixels pour le centrage
- **Dimensions de grille** : Calculées automatiquement selon la fenêtre
- **Couleurs** : Définies comme constantes dans `GridRenderer`
- **Commentaires** : JavaDoc pour toutes les méthodes publiques

## Notes Techniques

- Le projet utilise un Canvas JavaFX pour le rendu (performance optimale)
- La grille est stockée comme tableau 2D d'énumérations
- L'architecture permet l'ajout facile de nouvelles fonctionnalités
- Séparation claire entre logique (Grid/Player) et affichage (GridRenderer)
- Gestion des événements clavier centralisée dans `Launcher`
- Validation des déplacements du joueur via `Grid.isAccessible()` 