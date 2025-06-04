# Bomberman Base - Architecture Évolutive

## Description
Projet JavaFX 17.0.6 avec Java 23.0.2 implémentant une base évolutive pour un jeu Bomberman. Cette version inclut maintenant un joueur déplaçable avec contrôles clavier, **pose de bombes et explosions** 💣, et **blocs destructibles** 🧱💥.

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
  - Stocke l'état logique de chaque case (EMPTY, SOLID, DESTRUCTIBLE)
  - Génère le pattern Bomberman avec blocs destructibles
  - Fournit des méthodes d'accès et de modification de la grille
  - **Nouveau** : Méthodes `destroyBlock()` et `isDestructible()` pour la destruction
- **Évolutions** : Gestion des blocs destructibles avec placement aléatoire (30%)

#### 3. `GridRenderer.java`
- **Rôle** : Rendu graphique complet
- **Responsabilités** :
  - Dessine la grille sur un Canvas JavaFX
  - Gère toutes les couleurs du jeu
  - **Nouveau** : Rendu des blocs destructibles (marron clair #A0522D)
  - Mise à jour dynamique de l'affichage après destruction
- **Évolutions** : Méthode `render(Player, Bomb, Explosion)` pour le rendu complet

#### 4. `Player.java`
- **Rôle** : Représentation et logique du joueur
- **Responsabilités** :
  - Stocke la position du joueur (coordonnées logiques x, y)
  - Gère les déplacements dans les 4 directions avec validation des collisions
  - Empêche les déplacements vers les cases solides ET destructibles
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
- **Rôle** : Gestion des explosions et destruction
- **Responsabilités** :
  - Calcule les cases affectées par l'explosion (forme de croix)
  - **Nouveau** : Détruit automatiquement les blocs destructibles touchés
  - S'arrête sur les blocs solides ET après destruction d'un bloc destructible
  - Gère la durée d'affichage des flammes (0.5 seconde)
- **Évolutions** : Destruction de blocs, dégâts aux ennemis

#### 7. `TileType.java` ✨ **NOUVEAU**
- **Rôle** : Énumération des types de cases
- **Valeurs** : `EMPTY`, `SOLID`, `DESTRUCTIBLE`
- **Méthodes utilitaires** :
  - `isTraversable()` : Si le joueur peut passer
  - `isDestructible()` : Si peut être détruit par explosion
  - `blocksExplosion()` : Si bloque la propagation des flammes

#### 8. `Enemy.java` ✨ **NOUVEAU**
- **Rôle** : Ennemis avec IA simple
- **Responsabilités** :
  - IA de déplacement autonome (mouvement toutes les 500ms)
  - Direction persistante jusqu'à rencontrer un obstacle
  - Changement de direction aléatoire quand bloqué
  - État `isAlive` et méthode `kill()` pour la gestion de la mort
- **Comportement** : Les ennemis ne traversent pas les blocs solides/destructibles
- **Énumération** : `Direction` (UP, DOWN, LEFT, RIGHT)

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
- **Types de blocs** :
  - **Solides** (gris #505050) : Bordures + alternance, indestructibles
  - **Destructibles** (marron #A0522D) : ~30% des cases vides, destructibles par explosions ✨
  - **Vides** (noir #000000) : Traversables par le joueur
- **Joueur** :
  - Carré bleu clair (#00AAFF) de 26×26 pixels
  - Position de départ : case (1,1) avec zone de sécurité 2×2
  - Déplaçable avec les flèches directionnelles
  - **Collision** : Bloqué par les blocs solides ET destructibles
- **Bombes** ✨ **NOUVEAU** :
  - Carré rouge foncé (#990000) de 28×28 pixels
  - Timer d'explosion : 2 secondes
  - Une seule bombe active par joueur
  - Posée avec la barre d'espace
- **Explosions** ✨ **NOUVEAU** :
  - Flammes orange (#FF8800) en forme de croix
  - Portée : 2 cases dans chaque direction
  - **S'arrête** sur les blocs solides
  - **Détruit** les blocs destructibles (puis s'arrête)
  - Durée d'affichage : 0.5 seconde
- **Ennemis** ✨ **NOUVEAU** :
  - Carrés rouge vif (#FF0000) de 26×26 pixels
  - **Nombre** : 3 ennemis par défaut
  - **Placement** : Aléatoire, hors zone de sécurité 3×3 du joueur
  - **IA** : Mouvement autonome toutes les 500ms
  - **Comportement** : Direction persistante, changement si bloqué
  - **Mort** : Par explosion uniquement

## Contrôles

- **Flèche Haut** : Déplacer le joueur vers le haut
- **Flèche Bas** : Déplacer le joueur vers le bas  
- **Flèche Gauche** : Déplacer le joueur vers la gauche
- **Flèche Droite** : Déplacer le joueur vers la droite
- **Barre d'espace** ✨ **NOUVEAU** : Poser une bombe

## Mécaniques de Jeu

### Système de Blocs Destructibles ✨ **NOUVEAU**
1. **Génération** : ~30% des cases vides deviennent destructibles au démarrage
2. **Zone de sécurité** : Aucun bloc destructible dans la zone 2×2 autour du joueur
3. **Collision** : Le joueur ne peut pas traverser les blocs destructibles
4. **Destruction** : Une explosion qui touche un bloc destructible le détruit
5. **Propagation** : L'explosion s'arrête après avoir détruit un bloc (ne le traverse pas)
6. **Transformation** : Bloc destructible → Case vide (traversable)

### Système de Bombes
1. **Pose** : Le joueur peut poser une bombe avec la barre d'espace
2. **Limitation** : Une seule bombe active à la fois (pas de spam)
3. **Timer** : La bombe explose automatiquement après 2 secondes
4. **Explosion** : Flammes en croix avec une portée de 2 cases
5. **Obstacles** : L'explosion s'arrête sur les blocs solides ET destructibles
6. **Affichage** : Les flammes sont visibles pendant 0.5 seconde

### Système d'Ennemis avec IA ✨ **NOUVEAU**
1. **Génération** : 3 ennemis placés aléatoirement hors zone de sécurité joueur
2. **IA Simple** : 
   - Mouvement autonome toutes les 500ms
   - Direction persistante (UP, DOWN, LEFT, RIGHT)
   - Changement de direction aléatoire quand bloqué
3. **Collision** : Ne traversent pas les blocs solides ou destructibles
4. **Mort** : Tués par les explosions uniquement
5. **Interaction** : Contact avec le joueur = mort du joueur

## Évolutions Prévues

### Phase 5 - Ennemis et IA ⬅️ **PROCHAINE ÉTAPE**
- Ajout d'une classe `Enemy` avec comportements simples
- Système de collision avec les explosions
- Vies du joueur et game over

### Phase 6 - Power-ups
- Power-ups cachés dans les blocs destructibles
- Amélioration de portée, vitesse, bombes multiples
- Interface utilisateur pour le score et les vies

### Phase 7 - Niveaux et Progression
- Plusieurs niveaux avec patterns différents
- Augmentation progressive de la difficulté
- Système de score

## Structure des Fichiers
```
src/main/java/bomberman/bomberman/
├── Launcher.java       # Point d'entrée de l'application
├── Grid.java          # Modèle de données de la grille
├── GridRenderer.java  # Rendu graphique
├── Player.java        # Logique et position du joueur
├── Bomb.java          # ✨ Logique des bombes
├── Explosion.java     # ✨ Gestion des explosions et destruction
├── TileType.java      # ✨ Énumération des types de cases
└── Enemy.java         # ✨ Ennemis avec IA simple
```

## Conventions de Code

- **Taille des cellules** : 32×32 pixels (constante dans `GridRenderer`)
- **Taille du joueur** : 26×26 pixels avec décalage de 3 pixels pour le centrage
- **Taille des bombes** : 28×28 pixels avec décalage de 2 pixels pour le centrage
- **Timers** : Gestion avec `System.currentTimeMillis()` et `AnimationTimer`
- **Types de cases** : Énumération `TileType` avec méthodes utilitaires
- **Placement des destructibles** : 30% des cases vides, zone de sécurité joueur
- **Couleurs** : Définies comme constantes dans `GridRenderer`
- **Commentaires** : JavaDoc pour toutes les méthodes publiques

## Notes Techniques

- Le projet utilise un Canvas JavaFX pour le rendu (performance optimale)
- La grille est stockée comme tableau 2D d'énumérations `TileType`
- `AnimationTimer` pour les mises à jour en temps réel (bombes/explosions)
- Séparation claire entre logique et affichage
- Gestion des événements clavier centralisée dans `Launcher`
- **Destruction dynamique** : `Grid.destroyBlock()` modifie la grille en temps réel
- **Validation des déplacements** : `TileType.isTraversable()` pour la logique de collision
- **Propagation d'explosion** : Arrêt sur destruction ET sur blocs solides
- Génération procédurale des blocs destructibles avec zone de sécurité
- **Nouveau** : Rendu des blocs destructibles (marron clair #A0522D)
- **Nouveau** : Mise à jour dynamique de l'affichage après destruction 