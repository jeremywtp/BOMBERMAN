# Bomberman Base - Architecture Évolutive

## Description
Projet JavaFX 17.0.6 avec Java 23.0.2 implémentant une base évolutive pour un jeu Bomberman. Cette version inclut maintenant un joueur déplaçable avec contrôles clavier, **pose de bombes et explosions** 💣, **blocs destructibles** 🧱💥, **ennemis avec IA simple** 👹, **interface utilisateur avec système de mort** 💀, et **power-ups cachés** ✨💎.

## Architecture du Projet

Le projet suit une architecture MVC (Model-View-Controller) simplifiée avec une séparation claire des responsabilités :

### Structure des Classes

#### 1. `Launcher.java`
- **Rôle** : Classe principale de l'application
- **Responsabilités** :
  - Lance l'application JavaFX
  - Initialise la fenêtre (480×352 pixels)
  - Crée les instances du modèle (`Grid`), du joueur (`Player`), des ennemis (`Enemy`) et du renderer (`GridRenderer`)
  - Configure la scène JavaFX et gère les événements clavier
  - Gère l'`AnimationTimer` pour les bombes, explosions et ennemis
  - **Nouveau** : Désactive tous les inputs après la mort du joueur
- **Évolutions** : Timer de jeu, gestion complète des collisions, système de mort

#### 2. `Grid.java`
- **Rôle** : Modèle de données de la grille
- **Responsabilités** :
  - Stocke l'état logique de chaque case (EMPTY, SOLID, DESTRUCTIBLE)
  - Génère le pattern Bomberman avec blocs destructibles
  - Fournit des méthodes d'accès et de modification de la grille
  - Méthodes `destroyBlock()` et `isDestructible()` pour la destruction
- **Évolutions** : Gestion des blocs destructibles avec placement aléatoire (30%)

#### 3. `GridRenderer.java`
- **Rôle** : Rendu graphique complet avec interface utilisateur
- **Responsabilités** :
  - Dessine la grille sur un Canvas JavaFX
  - Gère toutes les couleurs du jeu
  - Rendu des blocs destructibles (marron clair #A0522D)
  - **Nouveau** : Interface utilisateur avec affichage de la vie
  - **Nouveau** : Écran de game over avec overlay sombre
  - **Nouveau** : Messages textuels dynamiques
- **Évolutions** : Méthodes `renderUI()`, `renderGameOver()`, `renderDeathOverlay()`

#### 4. `Player.java`
- **Rôle** : Représentation et logique du joueur
- **Responsabilités** :
  - Stocke la position du joueur (coordonnées logiques x, y)
  - Gère les déplacements dans les 4 directions avec validation des collisions
  - Empêche les déplacements vers les cases solides ET destructibles
  - Gestion de l'état `hasActiveBomb` pour éviter le spam de bombes
  - **État de vie** : `isAlive()` et `kill()` pour le système de mort
- **Évolutions** : Système de vie simple (vivant/mort)

#### 5. `Bomb.java`
- **Rôle** : Logique et état des bombes
- **Responsabilités** :
  - Stocke la position de la bombe (x, y)
  - Gère le timer d'explosion (2 secondes)
  - Fournit l'état de la bombe (active, explosée)
  - Portée d'explosion : 2 cases dans chaque direction
- **Évolutions futures** : Bombes multiples, portée variable

#### 6. `Explosion.java`
- **Rôle** : Gestion des explosions et destruction
- **Responsabilités** :
  - Calcule les cases affectées par l'explosion (forme de croix)
  - Détruit automatiquement les blocs destructibles touchés
  - S'arrête sur les blocs solides ET après destruction d'un bloc destructible
  - Gère la durée d'affichage des flammes (0.5 seconde)
  - **Dégâts** : Tue le joueur et les ennemis touchés
- **Évolutions** : Destruction de blocs, dégâts aux entités

#### 7. `TileType.java`
- **Rôle** : Énumération des types de cases
- **Valeurs** : `EMPTY`, `SOLID`, `DESTRUCTIBLE`
- **Méthodes utilitaires** :
  - `isTraversable()` : Si le joueur peut passer
  - `isDestructible()` : Si peut être détruit par explosion
  - `blocksExplosion()` : Si bloque la propagation des flammes

#### 8. `Enemy.java`
- **Rôle** : Ennemis avec IA simple et système de mort
- **Responsabilités** :
  - IA de déplacement autonome (mouvement toutes les 500ms)
  - Direction persistante jusqu'à rencontrer un obstacle
  - Changement de direction aléatoire quand bloqué
  - État `isAlive()` et méthode `kill()` pour la gestion de la mort
  - **Collision mortelle** : Contact avec le joueur tue le joueur
- **Comportement** : Les ennemis ne traversent pas les blocs solides/destructibles
- **Énumération** : `Direction` (UP, DOWN, LEFT, RIGHT)

#### 9. `PowerUpType.java` ✨ **NOUVEAU**
- **Rôle** : Énumération des types de power-ups
- **Types disponibles** :
  - `EXTRA_BOMB` : Permet de poser une bombe supplémentaire
  - `RANGE_UP` : Augmente la portée d'explosion de +1
  - `SPEED_UP` : Augmente la vitesse de déplacement
- **Méthodes** : `applyEffect(Player)` pour appliquer l'effet au joueur

#### 10. `PowerUp.java` ✨ **NOUVEAU**
- **Rôle** : Représentation d'un power-up dans le jeu
- **Responsabilités** :
  - Stocke la position (x, y) et le type de power-up
  - Gère l'état de visibilité (caché/visible/collecté)
  - Applique l'effet au joueur lors de la collecte
  - Méthodes : `reveal()`, `applyEffect()`, `isVisible()`
- **Cycle de vie** : Caché → Révélé → Collecté → Supprimé

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
  - **Destructibles** (marron #A0522D) : ~30% des cases vides, destructibles par explosions
  - **Vides** (noir #000000) : Traversables par le joueur
- **Joueur** :
  - Carré bleu clair (#00AAFF) de 26×26 pixels
  - Position de départ : case (1,1) avec zone de sécurité 2×2
  - Déplaçable avec les flèches directionnelles
  - **Collision** : Bloqué par les blocs solides ET destructibles
  - **Système de vie** : Vie unique, meurt au contact des ennemis ou explosions
- **Bombes** :
  - Carré rouge foncé (#990000) de 28×28 pixels
  - Timer d'explosion : 2 secondes
  - Une seule bombe active par joueur
  - Posée avec la barre d'espace
- **Explosions** :
  - Flammes orange (#FF8800) en forme de croix
  - Portée : 2 cases dans chaque direction
  - **S'arrête** sur les blocs solides
  - **Détruit** les blocs destructibles (puis s'arrête)
  - **Dégâts** : Tue le joueur et les ennemis touchés
  - Durée d'affichage : 0.5 seconde
- **Ennemis** :
  - Carrés rouge vif (#FF0000) de 26×26 pixels
  - **Nombre** : 3 ennemis par défaut
  - **Placement** : Aléatoire, hors zone de sécurité 3×3 du joueur
  - **IA** : Mouvement autonome toutes les 500ms
  - **Comportement** : Direction persistante, changement si bloqué
  - **Collision** : Contact avec le joueur = mort du joueur
  - **Mort** : Par explosion uniquement
- **Interface Utilisateur** ✨ **NOUVEAU** :
  - **Affichage de la vie** : "VIE : 1" en haut à gauche (blanc #FFFFFF)
  - **Game Over** : Message "GAME OVER" rouge vif au centre (police 48px)
  - **Overlay de mort** : Écran semi-transparent noir à la mort
  - **Blocage des inputs** : Aucune action possible après la mort
- **Power-ups** ✨ **NOUVEAU** :
  - **Génération** : 20% des blocs destructibles contiennent un power-up caché
  - **Révélation** : Apparaissent quand le bloc destructible est détruit
  - **Collecte** : Automatique au passage du joueur
  - **Types et couleurs** :
    - **EXTRA_BOMB** (cyan #00FFFF) : +1 bombe maximum
    - **RANGE_UP** (orange #FFA500) : +1 portée d'explosion
    - **SPEED_UP** (vert clair #90EE90) : +0.5 vitesse
  - **Effets** : Permanents jusqu'à la fin de la partie

## Contrôles

- **Flèche Haut** : Déplacer le joueur vers le haut
- **Flèche Bas** : Déplacer le joueur vers le bas  
- **Flèche Gauche** : Déplacer le joueur vers la gauche
- **Flèche Droite** : Déplacer le joueur vers la droite
- **Barre d'espace** : Poser une bombe
- **⚠️ Après la mort** : Toutes les touches sont désactivées

## Mécaniques de Jeu

### Système de Blocs Destructibles
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

### Système d'Ennemis avec IA
1. **Génération** : 3 ennemis placés aléatoirement hors zone de sécurité joueur
2. **IA Simple** : 
   - Mouvement autonome toutes les 500ms
   - Direction persistante (UP, DOWN, LEFT, RIGHT)
   - Changement de direction aléatoire quand bloqué
3. **Collision** : Ne traversent pas les blocs solides ou destructibles
4. **Mort** : Tués par les explosions uniquement
5. **Interaction** : Contact avec le joueur = mort du joueur

### Système de Mort et Game Over ✨ **NOUVEAU**
1. **Causes de mort** :
   - Contact direct avec un ennemi
   - Être pris dans une explosion (propre bombe ou autre)
2. **Conséquences** :
   - Affichage "VIE : 0" dans l'interface
   - Overlay noir semi-transparent sur l'écran
   - Message "GAME OVER" rouge vif au centre
   - Désactivation de tous les contrôles clavier
   - Arrêt des mouvements d'ennemis (optionnel)
3. **Affichage** :
   - Police Arial Bold pour tous les textes UI
   - Texte de vie en blanc (16px) en haut à gauche
   - Message game over en rouge (48px) centré
4. **Comportement** : Aucune possibilité de redémarrage (pour l'instant)

### Système de Power-ups ✨ **NOUVEAU**
1. **Génération** :
   - 20% des blocs destructibles contiennent un power-up caché
   - Type de power-up déterminé aléatoirement à la génération
   - Répartition équitable entre les 3 types
2. **Révélation** :
   - Power-up devient visible quand le bloc destructible est détruit
   - Apparaît instantanément à la position du bloc détruit
   - Reste visible jusqu'à collecte par le joueur
3. **Collecte** :
   - Automatique : le joueur n'a qu'à passer sur le power-up
   - Effet appliqué immédiatement
   - Power-up disparaît après collecte
4. **Effets permanents** :
   - **EXTRA_BOMB** : +1 bombe simultanée maximum (cumulative)
   - **RANGE_UP** : +1 case de portée d'explosion (cumulative)
   - **SPEED_UP** : +0.5 vitesse de déplacement (cumulative)
5. **Affichage** :
   - Carrés colorés de 26×26 pixels (même taille que le joueur)
   - Positionnés au centre des cases comme les autres entités

## Évolutions Prévues

### Phase 6 - Power-ups
- Power-ups cachés dans les blocs destructibles
- Amélioration de portée, vitesse, bombes multiples
- Interface utilisateur pour le score et les power-ups

### Phase 7 - Système de Vies et Redémarrage
- Vies multiples pour le joueur
- Possibilité de redémarrer le jeu après game over
- Menu principal et écran de fin

### Phase 8 - Niveaux et Progression
- Plusieurs niveaux avec patterns différents
- Augmentation progressive de la difficulté
- Système de score et classement

## Structure des Fichiers
```
src/main/java/bomberman/bomberman/
├── Launcher.java       # Point d'entrée avec boucle de jeu et gestion des inputs
├── Grid.java          # Modèle de données de la grille + power-ups cachés
├── GridRenderer.java  # Rendu graphique + interface utilisateur + power-ups
├── Player.java        # Logique et position du joueur + système de vie + power-ups
├── Bomb.java          # Logique des bombes
├── Explosion.java     # Gestion des explosions et destruction
├── TileType.java      # Énumération des types de cases
├── Enemy.java         # Ennemis avec IA simple + système de mort
├── PowerUpType.java   # ✨ Énumération des types de power-ups
└── PowerUp.java       # ✨ Classe des power-ups (position, visibilité, effets)
```

## Conventions de Code

- **Taille des cellules** : 32×32 pixels (constante dans `GridRenderer`)
- **Taille du joueur** : 26×26 pixels avec décalage de 3 pixels pour le centrage
- **Taille des bombes** : 28×28 pixels avec décalage de 2 pixels pour le centrage
- **Taille des ennemis** : 26×26 pixels avec décalage de 3 pixels pour le centrage
- **Timers** : Gestion avec `System.currentTimeMillis()` et `AnimationTimer`
- **Types de cases** : Énumération `TileType` avec méthodes utilitaires
- **Placement des destructibles** : 30% des cases vides, zone de sécurité joueur
- **Couleurs** : Définies comme constantes dans `GridRenderer`
- **Interface utilisateur** : Rendu via `GraphicsContext.fillText()` avec polices configurées
- **Système de mort** : État boolean `isAlive()` dans Player et Enemy
- **Commentaires** : JavaDoc pour toutes les méthodes publiques

## Notes Techniques

- Le projet utilise un Canvas JavaFX pour le rendu (performance optimale)
- La grille est stockée comme tableau 2D d'énumérations `TileType`
- `AnimationTimer` pour les mises à jour en temps réel (bombes/explosions/ennemis)
- Séparation claire entre logique et affichage
- Gestion des événements clavier centralisée dans `Launcher`
- **Destruction dynamique** : `Grid.destroyBlock()` modifie la grille en temps réel
- **Validation des déplacements** : `TileType.isTraversable()` pour la logique de collision
- **Propagation d'explosion** : Arrêt sur destruction ET sur blocs solides
- Génération procédurale des blocs destructibles avec zone de sécurité
- **Interface utilisateur** : Texte rendu directement sur Canvas avec `GraphicsContext`
- **Gestion de la mort** : Vérification systématique de `isAlive()` avant actions
- **Overlay visuel** : Couche semi-transparente pour feedback visuel de la mort
- **Rendu en couches** : Grille → Entités → Overlay → UI → Messages 