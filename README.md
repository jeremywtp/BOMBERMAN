# Bomberman Base - Architecture Évolutive

## Description
Projet JavaFX 17.0.6 avec Java 23.0.2 implémentant une base évolutive pour un jeu Bomberman. Cette version inclut maintenant un joueur déplaçable avec contrôles clavier, **pose de bombes et explosions** 💣.

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
  - **Nouveau** : Gère l'`AnimationTimer` pour les bombes et explosions
- **Évolutions** : Timer de jeu, gestion des bombes actives et explosions

#### 2. `Grid.java`
- **Rôle** : Modèle de données de la grille
- **Responsabilités** :
  - Stocke l'état logique de chaque case (SOLID ou EMPTY)
  - Génère le pattern Bomberman (bordures solides, alternance intérieure)
  - Fournit des méthodes d'accès aux données de la grille
- **Fonctionnalité** : Méthode `isAccessible()` pour vérifier si une case est traversable par le joueur
- **Évolutions futures** : Gérera les blocs destructibles et power-ups

#### 3. `GridRenderer.java`
- **Rôle** : Rendu graphique de la grille, joueur, bombes et explosions
- **Responsabilités** :
  - Dessine la grille sur un Canvas JavaFX
  - Gère les couleurs (gris #505050 pour solide, noir #000000 pour vide, bleu clair #00AAFF pour le joueur)
  - **Nouveau** : Rendu des bombes (rouge foncé #990000) et explosions (orange #FF8800)
  - Taille des cellules : 32×32 pixels
- **Évolutions** : Méthode `render(Player, Bomb, Explosion)` pour le rendu complet

#### 4. `Player.java`
- **Rôle** : Représentation et logique du joueur
- **Responsabilités** :
  - Stocke la position du joueur (coordonnées logiques x, y)
  - Gère les déplacements dans les 4 directions avec validation des collisions
  - Empêche les déplacements vers les cases solides ou hors de la grille
  - **Nouveau** : Gestion de l'état `hasActiveBomb` pour éviter le spam de bombes
- **Évolutions futures** : Gestion des vies, power-ups

#### 5. `Bomb.java` ✨ **NOUVEAU**
- **Rôle** : Logique et état des bombes
- **Responsabilités** :
  - Stocke la position de la bombe (x, y)
  - Gère le timer d'explosion (2 secondes)
  - Fournit l'état de la bombe (active, explosée)
  - Portée d'explosion : 2 cases dans chaque direction
- **Évolutions futures** : Bombes multiples, portée variable

#### 6. `Explosion.java` ✨ **NOUVEAU**
- **Rôle** : Gestion des explosions en croix
- **Responsabilités** :
  - Calcule les cases affectées par l'explosion (forme de croix)
  - S'arrête sur les blocs solides
  - Gère la durée d'affichage des flammes (0.5 seconde)
  - Classe interne `ExplosionCell` pour les coordonnées des flammes
- **Évolutions futures** : Destruction de blocs, dégâts aux ennemis

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
- **Joueur** :
  - Carré bleu clair (#00AAFF) de 26×26 pixels
  - Position de départ : case (1,1)
  - Déplaçable avec les flèches directionnelles
  - Collision avec les blocs solides et bordures
- **Bombes** ✨ **NOUVEAU** :
  - Carré rouge foncé (#990000) de 28×28 pixels
  - Timer d'explosion : 2 secondes
  - Une seule bombe active par joueur
  - Posée avec la barre d'espace
- **Explosions** ✨ **NOUVEAU** :
  - Flammes orange (#FF8800) en forme de croix
  - Portée : 2 cases dans chaque direction
  - S'arrête sur les blocs solides
  - Durée d'affichage : 0.5 seconde

## Contrôles

- **Flèche Haut** : Déplacer le joueur vers le haut
- **Flèche Bas** : Déplacer le joueur vers le bas  
- **Flèche Gauche** : Déplacer le joueur vers la gauche
- **Flèche Droite** : Déplacer le joueur vers la droite
- **Barre d'espace** ✨ **NOUVEAU** : Poser une bombe

## Mécaniques de Jeu

### Système de Bombes
1. **Pose** : Le joueur peut poser une bombe avec la barre d'espace
2. **Limitation** : Une seule bombe active à la fois (pas de spam)
3. **Timer** : La bombe explose automatiquement après 2 secondes
4. **Explosion** : Flammes en croix avec une portée de 2 cases
5. **Obstacles** : L'explosion s'arrête sur les blocs solides
6. **Affichage** : Les flammes sont visibles pendant 0.5 seconde

## Évolutions Prévues

### Phase 3 - Ennemis et IA ⬅️ **PROCHAINE ÉTAPE**
- Ajout d'une classe `Enemy` avec comportements simples
- Système de collision avec les explosions
- Vies du joueur et game over

### Phase 4 - Blocs Destructibles
- Blocs destructibles par les explosions
- Power-ups cachés dans les blocs

### Phase 5 - Power-ups et Score
- Système de power-ups (portée de bombe, vitesse, bombes multiples)
- Interface utilisateur pour le score et les vies

## Structure des Fichiers
```
src/main/java/bomberman/bomberman/
├── Launcher.java       # Point d'entrée de l'application
├── Grid.java          # Modèle de données de la grille
├── GridRenderer.java  # Rendu graphique
├── Player.java        # Logique et position du joueur
├── Bomb.java          # ✨ Logique des bombes
└── Explosion.java     # ✨ Gestion des explosions
```

## Conventions de Code

- **Taille des cellules** : 32×32 pixels (constante dans `GridRenderer`)
- **Taille du joueur** : 26×26 pixels avec décalage de 3 pixels pour le centrage
- **Taille des bombes** : 28×28 pixels avec décalage de 2 pixels pour le centrage
- **Timers** : Gestion avec `System.currentTimeMillis()` et `AnimationTimer`
- **Dimensions de grille** : Calculées automatiquement selon la fenêtre
- **Couleurs** : Définies comme constantes dans `GridRenderer`
- **Commentaires** : JavaDoc pour toutes les méthodes publiques

## Notes Techniques

- Le projet utilise un Canvas JavaFX pour le rendu (performance optimale)
- La grille est stockée comme tableau 2D d'énumérations
- `AnimationTimer` pour les mises à jour en temps réel (bombes/explosions)
- Séparation claire entre logique (Grid/Player/Bomb/Explosion) et affichage (GridRenderer)
- Gestion des événements clavier centralisée dans `Launcher`
- Validation des déplacements du joueur via `Grid.isAccessible()`
- Système de bombes avec état `hasActiveBomb` pour éviter le spam
- Calcul d'explosion en croix avec arrêt sur obstacles 