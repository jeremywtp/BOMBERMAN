# Bomberman Base - Architecture Évolutive ✨ ÉDITION AGRANDIE x1.5

## Description
Projet JavaFX 17.0.6 avec Java 23.0.2 implémentant une base évolutive pour un jeu Bomberman. Cette version inclut maintenant un joueur déplaçable avec contrôles clavier, **pose de bombes multiples et explosions** 💣, **blocs destructibles** 🧱💥, **ennemis avec IA simple** 👹, **interface utilisateur avec système de vies multiples** 💖, **power-ups permanents et temporaires** ✨💎, **gestion complète des états de jeu** 🎮, et **affichage agrandi x1.5 avec interface optimisée** 🖥️.

## 🚀 NOUVELLES FONCTIONNALITÉS MAJEURES

### ✨ **Interface Agrandie x1.5 (Zoom 50%)**
- **Fenêtre** : 720×780 pixels (était 480×520px)
- **Zone de jeu** : 720×528 pixels (grille 15×11 agrandie)
- **Zone d'interface** : 720×252 pixels (interface dédiée)
- **Taille des cellules** : 48×48 pixels (était 32×32px)
- **Éléments graphiques** : Tous agrandis proportionnellement
- **Polices** : Tailles mises à l'échelle (24px, 27px, 36px, 48px, 72px)

### 🎯 **Interface Utilisateur Repensée**
- **ATH supérieur** : LEVEL, SCORE, HIGHSCORE parfaitement répartis
- **ATH inférieur** : Zone dédiée avec 3 lignes distinctes
  - Ligne 1 : ❤️ VIES et 💣 BOMBES
  - Ligne 2 : 🛡️ SHIELD, ⚡ SPEED BURST, → VITESSE, ○ PORTÉE
  - Ligne 3+ : Notifications empilées verticalement
- **Centrage optimal** : Alignement mathématique parfait
- **Espace dédié** : 252px d'interface pure sous la grille

### 💣 **Système Multi-Bombes Avancé**
- **Bombes multiples** : Jusqu'à 3+ bombes simultanées (selon power-ups)
- **Compteur dynamique** : "BOMBES : X/Y" en temps réel
- **Gestion séparée** : Bombes joueur vs bombes Bomb Rain
- **Limitation intelligente** : Système getAvailableBombs()

### ⚡ **Power-ups Temporaires**
- **SHIELD** : Protection 10 secondes avec effets visuels bleus
- **SPEED_BURST** : Vitesse maximale 5 secondes avec effets jaunes
- **BOMB_RAIN** : 5 bombes automatiques avec effets rouges
- **Animations** : Auras, glows, clignotements uniquement pour temporaires
- **Indicateurs visuels** : État en temps réel dans l'interface

### 📊 **Système de Score et High Score**
- **Score persistant** : Sauvegarde automatique dans highscore.txt
- **Points** : Ennemis (+100), blocs détruits (+10), power-ups (+50)
- **Affichage** : Score actuel et record en permanence
- **Niveaux progressifs** : Difficulté croissante par niveau

### 🎮 **Gameplay Amélioré**
- **Vies multiples** : 3 vies avec respawn et invincibilité temporaire
- **Système de vitesse** : Cooldown basé sur les bonus de vitesse
- **Notifications** : Système d'alertes avec fade pour power-ups
- **États de jeu** : Menu → Jeu → Niveau terminé → Game Over

### 🎬 **Menu d'accueil interactif**
- **Image d'arrière-plan** : `intro.png` de Super Bomberman SNES
  - Affichage plein écran centré sans déformation
  - Scaling automatique pour s'adapter à la fenêtre 720×780px
- **Musique d'intro** : `intro.wav` (converti depuis "01. Explosive Beginnings (Main Title).mp3")
  - ✅ **Lecture automatique en boucle infinie** au démarrage (20 secondes)
  - ✅ **Arrêt propre** lors du lancement d'une partie (touche ENTRÉE)
  - ✅ **Gestion complète** via `SoundManager` centralisé avec MediaPlayer
  - **Format WAV** : Compatibilité universelle JavaFX (MP3 non supporté sur certains systèmes)
- **Menu interactif** : 3 options sélectionnables dans une zone semi-transparente
  - `► NORMAL GAME` : Lance le jeu classique (option active)
  - `   BATTLE MODE` : Mode combat (option grisée, future fonctionnalité)
  - `   PASSWORD` : Système de codes (option grisée, future fonctionnalité)
- **Navigation clavier** :
  - **↑/↓** : Déplacer le curseur (`►`) entre les options + effet sonore `Menu_Cursor.wav`
  - **ENTRÉE** : Valider l'option sélectionnée + effet sonore `Menu_Select.wav`
  - Curseur dynamique avec couleurs distinctes :
    - **Option active sélectionnée** : Jaune/orange vif (`#FFCC00`)
    - **Option active non sélectionnée** : Blanc
    - **Options inactives** : Gris clair (`#AAAAAA`)
- **Feedback sonore** : Effets audio instantanés pour une expérience utilisateur immersive
  - **Navigation** : Son court à chaque changement de sélection
  - **Validation** : Son de confirmation lors de l'appui sur ENTRÉE
- **Architecture évolutive** : Prêt pour l'activation des modes BATTLE et PASSWORD
- **Ressources** : Gestion automatique des fichiers dans `src/main/resources/`

## Architecture du Projet

Le projet suit une architecture MVC (Model-View-Controller) simplifiée avec une séparation claire des responsabilités :

### Structure des Classes

#### 1. `Launcher.java` 🔧 **MAJORÉ**
- **Rôle** : Classe principale de l'application
- **Responsabilités** :
  - Lance l'application JavaFX
  - **NOUVEAU** : Initialise la fenêtre (720×780 pixels)
  - **NOUVEAU** : Gestion du système multi-bombes (activeBombs + rainBombs)
  - **NOUVEAU** : High score persistant avec sauvegarde automatique
  - **NOUVEAU** : Niveaux progressifs avec difficulté croissante
  - **NOUVEAU** : Gestion des power-ups temporaires (Shield, Speed Burst, Bomb Rain)
  - **NOUVEAU** : Système de vies multiples avec respawn
  - Crée les instances du modèle (`Grid`), du joueur (`Player`), des ennemis (`Enemy`) et du renderer (`GridRenderer`)
  - Configure la scène JavaFX et gère les événements clavier
  - Gère l'`AnimationTimer` pour les bombes, explosions et ennemis
  - Gestion des power-ups (collecte, révélation, application des effets)
  - Gestion complète des états du jeu (menu, partie, niveau terminé, game over)
  - Système de rejouabilité avec réinitialisation complète
- **Évolutions** : Multi-bombes, high score, niveaux, power-ups temporaires, vies multiples

#### 2. `Grid.java`
- **Rôle** : Modèle de données de la grille
- **Responsabilités** :
  - Stocke l'état logique de chaque case (EMPTY, SOLID, DESTRUCTIBLE)
  - Génère le pattern Bomberman avec blocs destructibles
  - **NOUVEAU** : Gestion des power-ups cachés avec Map<Position, PowerUpType>
  - Fournit des méthodes d'accès et de modification de la grille
  - Méthodes `destroyBlock()` et `isDestructible()` pour la destruction
- **Évolutions** : Gestion des blocs destructibles avec placement aléatoire (30%), power-ups cachés

#### 3. `GridRenderer.java` 🎨 **TRANSFORMÉ**
- **Rôle** : Rendu graphique complet avec interface utilisateur agrandie
- **Responsabilités** :
  - **NOUVEAU** : Rendu agrandi x1.5 (CELL_SIZE = 48px)
  - **NOUVEAU** : Interface utilisateur repensée avec zone dédiée
  - **NOUVEAU** : Centrage mathématique parfait de tous les éléments
  - **NOUVEAU** : Système de notifications avec empilement vertical
  - **NOUVEAU** : Effets visuels pour power-ups temporaires
  - **NOUVEAU** : Multi-bomb rendering avec gestion des listes
  - Dessine la grille sur un Canvas JavaFX
  - Gère toutes les couleurs du jeu
  - Rendu des blocs destructibles (marron clair #A0522D)
  - Interface utilisateur avec affichage des vies multiples
  - Écrans de menu, game over, niveau terminé
  - Messages textuels dynamiques
- **Évolutions** : Zone UI dédiée, centrage optimal, effets visuels, multi-rendu

#### 4. `Player.java` 👤 **ENRICHI**
- **Rôle** : Représentation et logique du joueur avec capacités étendues
- **Responsabilités** :
  - Stocke la position du joueur (coordonnées logiques x, y)
  - **NOUVEAU** : Système de vies multiples (3 vies par défaut)
  - **NOUVEAU** : Power-ups permanents (maxBombs, range, speed) cumulatifs
  - **NOUVEAU** : Power-ups temporaires avec timers (Shield 10s, Speed Burst 5s)
  - **NOUVEAU** : Système de score avec accumulation de points
  - **NOUVEAU** : Gestion multi-bombes avec compteurs (currentBombs/maxBombs)
  - **NOUVEAU** : Système de cooldown de mouvement basé sur la vitesse
  - **NOUVEAU** : Protection combinée (Shield + invincibilité)
  - Gère les déplacements dans les 4 directions avec validation des collisions
  - Empêche les déplacements vers les cases solides ET destructibles
  - Respawn avec invincibilité temporaire après mort
- **Évolutions** : Vies multiples, power-ups temporaires, multi-bombes, système de vitesse

#### 5. `Bomb.java`
- **Rôle** : Logique et état des bombes (inchangé mais utilisé en multi-instances)
- **Responsabilités** :
  - Stocke la position de la bombe (x, y)
  - Gère le timer d'explosion (2 secondes)
  - Fournit l'état de la bombe (active, explosée)
  - Portée d'explosion : Variable selon le joueur (2+ cases dans chaque direction)
- **Utilisation** : Maintenant géré en List<Bomb> dans Launcher

#### 6. `Explosion.java`
- **Rôle** : Gestion des explosions et destruction (inchangé)
- **Responsabilités** :
  - Calcule les cases affectées par l'explosion (forme de croix)
  - Détruit automatiquement les blocs destructibles touchés
  - S'arrête sur les blocs solides ET après destruction d'un bloc destructible
  - Gère la durée d'affichage des flammes (0.5 seconde)
  - Dégâts : Tue le joueur (si non protégé) et les ennemis touchés
- **Utilisation** : Maintenant géré en List<Explosion> dans Launcher

#### 7. `TileType.java`
- **Rôle** : Énumération des types de cases (inchangé)
- **Valeurs** : `EMPTY`, `SOLID`, `DESTRUCTIBLE`
- **Méthodes utilitaires** :
  - `isTraversable()` : Si le joueur peut passer
  - `isDestructible()` : Si peut être détruit par explosion
  - `blocksExplosion()` : Si bloque la propagation des flammes

#### 8. `Enemy.java`
- **Rôle** : Ennemis avec IA simple et système de mort (inchangé)
- **Responsabilités** :
  - IA de déplacement autonome (mouvement toutes les 500ms)
  - Direction persistante jusqu'à rencontrer un obstacle
  - Changement de direction aléatoire quand bloqué
  - État `isAlive()` et méthode `kill()` pour la gestion de la mort
  - Collision mortelle : Contact avec le joueur tue le joueur (si non protégé)
- **Comportement** : Les ennemis ne traversent pas les blocs solides/destructibles
- **Énumération** : `Direction` (UP, DOWN, LEFT, RIGHT)

#### 9. `PowerUpType.java` ✨ **ÉTENDU**
- **Rôle** : Énumération des types de power-ups
- **Types permanents** :
  - `EXTRA_BOMB` : Permet de poser une bombe supplémentaire
  - `RANGE_UP` : Augmente la portée d'explosion de +1
  - `SPEED_UP` : Augmente la vitesse de déplacement de +0.5
- **Types temporaires** ✨ **NOUVEAU** :
  - `SHIELD` : Protection contre explosions (10 secondes)
  - `SPEED_BURST` : Vitesse maximale instantanée (5 secondes)
  - `BOMB_RAIN` : Pose 5 bombes automatiques (effet instantané)
- **Méthodes** : `applyEffect(Player)`, `isPermanent()`, effets différenciés

#### 10. `PowerUp.java` ✨ **AMÉLIORÉ**
- **Rôle** : Représentation d'un power-up dans le jeu
- **Responsabilités** :
  - Stocke la position (x, y) et le type de power-up
  - Gère l'état de visibilité (caché/visible/collecté)
  - **NOUVEAU** : Effets visuels différenciés (permanents vs temporaires)
  - **NOUVEAU** : Animations de pulsation pour power-ups temporaires
  - Applique l'effet au joueur lors de la collecte
  - Méthodes : `reveal()`, `applyEffect()`, `isVisible()`
- **Cycle de vie** : Caché → Révélé → Collecté → Supprimé
- **Affichage** : Auras clignotantes pour temporaires, statiques pour permanents

#### 11. `GameState.java`
- **Rôle** : Énumération des états du jeu
- **États disponibles** :
  - `START_MENU` : Menu de démarrage avec instructions
  - `RUNNING` : Partie en cours (gameplay normal)
  - **NOUVEAU** : `LEVEL_COMPLETED` : Écran de transition entre niveaux
  - `GAME_OVER` : Écran de fin avec option de rejeu
- **Utilisation** : Contrôle du flux principal et des inputs selon l'état

#### 12. `SoundManager.java` 🎵 **ÉTENDU**
- **Rôle** : Gestionnaire centralisé de sons et musiques
- **Responsabilités** :
  - Chargement des fichiers audio depuis les ressources
  - **NOUVEAU** : Support dual MediaPlayer (musiques longues) + AudioClip (effets courts latence minimale)
  - Lecture simple (`play()`) et en boucle infinie (`loop()`)
  - **NOUVEAU** : Lecture unique avec callback (`playOnce(name, callback)`) pour démarrage de niveau ✨
  - **NOUVEAU** : Lecture d'effets sonores instantanés (`playEffect()`) sans latence
  - Arrêt sélectif (`stop()`) et général (`stopAll()`)
  - Contrôle du volume et vérification d'état de lecture
  - Libération propre des ressources (`dispose()`)
- **Utilisation principale** :
  - **Musique d'intro** : `loadSound("intro", "/music/intro.wav")` (format WAV PCM)
  - **Musique de niveau** : `loadSound("level_start", "/music/Level_Start.wav")` (format WAV PCM) ✨ **NOUVEAU**
  - **Effets de menu** : `loadSoundEffect("menu_cursor", "/music/Menu_Cursor.wav")` (format WAV PCM)
  - **Effets de menu** : `loadSoundEffect("menu_select", "/music/Menu_Select.wav")` (format WAV PCM)
  - Lecture automatique : `loop("intro")` au démarrage avec délai d'initialisation
  - **Démarrage de niveau** : `playOnce("level_start", callback)` automatique à chaque nouveau niveau ✨ **NOUVEAU**
  - **Feedback interactif** : `playEffect("menu_cursor")` pour navigation ↑/↓ (AudioClip instantané)
  - **Validation** : `playEffect("menu_select")` pour confirmation ENTRÉE (AudioClip instantané)
  - Arrêt propre : `stop("intro")` lors du lancement de partie
  - **Optimisation latence** : AudioClip pour effets courts (≈0ms), MediaPlayer pour musiques longues
  - **Compatibilité audio** : Tous les fichiers convertis en PCM non compressé pour JavaFX
  - **Gestion d'erreurs** : Listeners détaillés pour diagnostiquer les problèmes audio
- **Architecture** : Classe statique pour accès global simplifié

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

- **Fenêtre** : 720×780 pixels (agrandie x1.5), non redimensionnable
- **Grille** : 15×11 cases (48 pixels par case, agrandie x1.5)
- **Types de blocs** :
  - **Solides** (gris #505050) : Bordures + alternance, indestructibles
  - **Destructibles** (marron #A0522D) : ~30% des cases vides, destructibles par explosions
  - **Vides** (noir #000000) : Traversables par le joueur
- **Joueur** :
  - Carré bleu clair (#00AAFF) de 39×39 pixels (agrandi x1.5)
  - Position de départ : case (1,1) avec zone de sécurité 2×2
  - **NOUVEAU** : 3 vies avec respawn et invincibilité temporaire
  - **NOUVEAU** : Système de vitesse avec cooldown adaptatif
  - **NOUVEAU** : Protection combinée (Shield + invincibilité)
  - Déplaçable avec les flèches directionnelles
  - Collision : Bloqué par les blocs solides ET destructibles
- **Bombes** :
  - Carré rouge foncé (#990000) de 42×42 pixels (agrandie x1.5)
  - Timer d'explosion : 2 secondes
  - **NOUVEAU** : Système multi-bombes (1-3+ selon power-ups)
  - **NOUVEAU** : Bombes Bomb Rain séparées (ne comptent pas dans la limite)
  - Posée avec la barre d'espace
- **Explosions** :
  - Flammes orange (#FF8800) en forme de croix
  - Portée : 2+ cases dans chaque direction (selon power-ups)
  - S'arrête sur les blocs solides
  - Détruit les blocs destructibles (puis s'arrête)
  - Dégâts : Tue le joueur (si non protégé) et les ennemis touchés
  - Durée d'affichage : 0.5 seconde
- **Ennemis** :
  - Carrés rouge vif (#FF0000) de 39×39 pixels (agrandis x1.5)
  - **Nombre** : 3+ ennemis (augmente avec les niveaux)
  - Placement : Aléatoire, hors zone de sécurité 3×3 du joueur
  - IA : Mouvement autonome toutes les 500ms
  - Comportement : Direction persistante, changement si bloqué
  - Collision : Contact avec le joueur = mort du joueur (si non protégé)
  - Mort : Par explosion uniquement
- **Interface Utilisateur** ✨ **TRANSFORMÉE** :
  - **Zone de jeu** : 720×528px (grille agrandie)
  - **Zone d'interface** : 720×252px (dédiée)
  - **ATH supérieur** : LEVEL, SCORE, HIGHSCORE (polices 24px)
  - **ATH inférieur** :
    - **Ligne 1** : ❤️ VIES : X/Y + 💣 BOMBES : X/Y (polices 27px)
    - **Ligne 2** : 🛡️ SHIELD, ⚡ SPEED BURST, → VITESSE, ○ PORTÉE (polices 23px)
    - **Ligne 3+** : Notifications empilées avec fade 3s (polices 20px)
  - **Centrage parfait** : Alignement mathématique optimal
  - **Menu de démarrage** : Titre "BOMBERMAN" + instructions (polices 48px/27px/21px)
  - **Écran game over** : Message + score final + rejeu (polices 72px/36px/27px)
  - **Écran niveau terminé** : Transition avec score conservé (polices 48px/30px/27px/24px)
- **Power-ups** :
  - **Génération** : 20% des blocs destructibles contiennent un power-up caché
  - **Révélation** : Apparaissent quand le bloc destructible est détruit
  - **Collecte** : Automatique au passage du joueur (+50 points)
  - **Types permanents et couleurs** :
    - **EXTRA_BOMB** (cyan #00FFFF) : +1 bombe maximum
    - **RANGE_UP** (orange #FFA500) : +1 portée d'explosion
    - **SPEED_UP** (vert clair #90EE90) : +0.5 vitesse
  - **Types temporaires et couleurs** ✨ **NOUVEAU** :
    - **SHIELD** (bleu #1E90FF) : Protection 10s + effets visuels bleus
    - **SPEED_BURST** (jaune #FFFF00) : Vitesse max 5s + effets visuels jaunes
    - **BOMB_RAIN** (rouge foncé #DC143C) : 5 bombes auto + effets visuels rouges
  - **Effets visuels** : Auras clignotantes et animations pour temporaires uniquement
  - **Effets** : Permanents jusqu'à la fin de la partie / Temporaires avec timers
- **Système de Score** ✨ **NOUVEAU** :
  - **Points** : Ennemis tués (+100), blocs détruits (+10), power-ups collectés (+50)
  - **High Score** : Sauvegarde automatique dans highscore.txt
  - **Affichage** : Score actuel et record en permanence dans l'ATH
  - **Conservation** : Score conservé entre les niveaux

## Contrôles

### Menu de démarrage :
- **ENTRÉE** : Lancer une nouvelle partie

### Pendant le jeu :
- **Flèche Haut** : Déplacer le joueur vers le haut
- **Flèche Bas** : Déplacer le joueur vers le bas  
- **Flèche Gauche** : Déplacer le joueur vers la gauche
- **Flèche Droite** : Déplacer le joueur vers la droite
- **Barre d'espace** : Poser une bombe (limite selon power-ups EXTRA_BOMB)

### Écran Niveau Terminé :
- **ENTRÉE** : Passer au niveau suivant

### Écran Game Over :
- **ENTRÉE** : Rejouer (réinitialisation complète)

⚠️ **Tous les autres inputs sont ignorés selon l'état du jeu**

## Mécaniques de Jeu

### Système Multi-Bombes ✨ **NOUVEAU**
1. **Limitation** : 1 bombe par défaut, augmentable avec power-ups EXTRA_BOMB
2. **Gestion** : List<Bomb> activeBombs pour bombes du joueur
3. **Compteur** : "BOMBES : X/Y" affiché en temps réel
4. **Bomb Rain** : List<Bomb> rainBombs séparée (ne compte pas dans la limite)
5. **Placement** : Vérification isBombAt() sur les deux listes
6. **Explosion** : Gestion simultanée de multiples explosions

### Système de Power-ups Temporaires ✨ **NOUVEAU**
1. **SHIELD** :
   - Durée : 10 secondes
   - Effet : Protection totale contre explosions
   - Visuels : Aura bleue, contour, changement de couleur joueur
   - Combinaison : Fonctionne avec invincibilité de respawn
2. **SPEED_BURST** :
   - Durée : 5 secondes  
   - Effet : Bypass total du cooldown de mouvement
   - Visuels : Aura jaune clignotante, changement de couleur joueur
   - Indication : "VITESSE: MAX" dans l'interface
3. **BOMB_RAIN** :
   - Effet : Instantané (pose 5 bombes automatiques)
   - Placement : Positions aléatoires accessibles
   - Timer : 2 secondes comme bombes normales
   - Visuels : Effets rouges pendant l'activation
4. **Gestion** : Timers automatiques, mise à jour dans updateTemporaryEffects()

### Système de Vies Multiples ✨ **NOUVEAU**
1. **Vies** : 3 vies par défaut (affichage "VIES : X/3")
2. **Mort** : Contact ennemi ou explosion (si non protégé)
3. **Respawn** : Position (1,1) avec invincibilité temporaire
4. **Invincibilité** : Clignotement visuel + protection complète
5. **Game Over** : Quand vies = 0
6. **Interface** : Mise à jour en temps réel de l'affichage

### Système de Démarrage de Niveau ✨ **NOUVEAU**
1. **Musique d'intro** : `Level_Start.wav` joué automatiquement à chaque nouveau niveau
2. **Durée** : Une seule lecture (non-loopée) avant démarrage effectif du niveau
3. **Blocage** : Aucun input joueur ni mouvement ennemi pendant la musique
4. **Callback** : Démarrage automatique du gameplay à la fin de la musique
5. **Gestion d'erreurs** : Le niveau démarre même si la musique ne peut pas être lue
6. **Interface** : Affichage normal avec "NIVEAU X" visible pendant l'intro

### Système de Vitesse et Cooldown ✨ **NOUVEAU**
1. **Cooldown de base** : 200ms entre mouvements
2. **Calcul** : cooldown réduit selon speed (speed 1.5 = 133ms)
3. **Speed Burst** : Bypass complet du cooldown
4. **Affichage** : "VITESSE: X.X" ou "VITESSE: MAX"
5. **Power-ups** : SPEED_UP cumulative (+0.5 par power-up)

### Système de Score et High Score ✨ **NOUVEAU**
1. **Points** :
   - Ennemi tué : +100 points
   - Bloc destructible détruit : +10 points
   - Power-up collecté : +50 points
2. **High Score** :
   - Sauvegarde automatique dans highscore.txt
   - Chargement au démarrage
   - Mise à jour si dépassement
3. **Affichage** : Score actuel et record en permanence
4. **Conservation** : Score conservé entre niveaux

### Système de Niveaux Progressifs ✨ **NOUVEAU**
1. **Difficulté** : Nombre d'ennemis = 3 + (niveau - 1), maximum 8
2. **Transition** : Écran "NIVEAU X TERMINÉ !" avec score conservé
3. **Progression** : Grille régénérée, ennemis repositionnés
4. **Interface** : Affichage du niveau actuel en permanence
5. **Completion** : Tous les ennemis morts = niveau terminé

### Système de Blocs Destructibles
1. **Génération** : ~30% des cases vides deviennent destructibles au démarrage
2. **Zone de sécurité** : Aucun bloc destructible dans la zone 2×2 autour du joueur
3. **Collision** : Le joueur ne peut pas traverser les blocs destructibles
4. **Destruction** : Une explosion qui touche un bloc destructible le détruit (+10 points)
5. **Propagation** : L'explosion s'arrête après avoir détruit un bloc (ne le traverse pas)
6. **Transformation** : Bloc destructible → Case vide (traversable)

### Système de Bombes
1. **Pose** : Le joueur peut poser des bombes avec la barre d'espace
2. **Limitation** : Nombre maximum selon power-ups EXTRA_BOMB
3. **Timer** : Les bombes explosent automatiquement après 2 secondes
4. **Explosion** : Flammes en croix avec portée variable (selon power-ups RANGE_UP)
5. **Obstacles** : L'explosion s'arrête sur les blocs solides ET destructibles
6. **Affichage** : Les flammes sont visibles pendant 0.5 seconde

### Système d'Ennemis avec IA
1. **Génération** : 3+ ennemis placés aléatoirement hors zone de sécurité joueur
2. **IA Simple** : 
   - Mouvement autonome toutes les 500ms
   - Direction persistante (UP, DOWN, LEFT, RIGHT)
   - Changement de direction aléatoire quand bloqué
3. **Collision** : Ne traversent pas les blocs solides ou destructibles
4. **Mort** : Tués par les explosions uniquement (+100 points)
5. **Interaction** : Contact avec le joueur = mort du joueur (si non protégé)

### Système de Mort et Game Over
1. **Causes de mort** :
   - Contact direct avec un ennemi (si non protégé)
   - Être pris dans une explosion (si non protégé par Shield)
2. **Protection** : Combinaison Shield + invincibilité de respawn
3. **Conséquences** :
   - Décrémentation des vies
   - Respawn à (1,1) si vies > 0
   - Game Over si vies = 0
4. **Affichage** :
   - Interface : "VIES : X/3" mise à jour
   - Game Over : Overlay + message + score final
   - Rejeu : ENTRÉE pour redémarrer complètement

### Système de Power-ups
1. **Génération** :
   - 20% des blocs destructibles contiennent un power-up caché
   - Types déterminés aléatoirement (permanents + temporaires)
   - Répartition équitable entre tous les types
2. **Révélation** :
   - Power-up devient visible quand le bloc destructible est détruit
   - Apparaît instantanément à la position du bloc détruit
   - Reste visible jusqu'à collecte par le joueur
3. **Collecte** :
   - Automatique : le joueur n'a qu'à passer sur le power-up
   - Effet appliqué immédiatement (+50 points)
   - Power-up disparaît après collecte
   - Notification affichée avec fade
4. **Effets permanents** :
   - **EXTRA_BOMB** : +1 bombe simultanée maximum (cumulative)
   - **RANGE_UP** : +1 case de portée d'explosion (cumulative)
   - **SPEED_UP** : +0.5 vitesse de déplacement (cumulative)
5. **Effets temporaires** ✨ **NOUVEAU** :
   - **SHIELD** : Protection 10s avec effets visuels
   - **SPEED_BURST** : Vitesse max 5s avec effets visuels
   - **BOMB_RAIN** : 5 bombes automatiques avec effets visuels
6. **Affichage** :
   - Carrés colorés de 39×39 pixels (agrandis x1.5)
   - Effets visuels différenciés (auras pour temporaires)
   - Positionnés au centre des cases comme les autres entités

### Système de Gestion des États
1. **États du jeu** :
   - **START_MENU** : Affichage du menu principal avec titre et instructions
   - **RUNNING** : Partie en cours avec gameplay complet
   - **LEVEL_COMPLETED** ✨ **NOUVEAU** : Écran de transition entre niveaux
   - **GAME_OVER** : Écran de fin avec possibilité de rejouer
2. **Transitions d'états** :
   - `START_MENU` → `RUNNING` : Touche ENTRÉE (nouvelle partie)
   - `RUNNING` → `LEVEL_COMPLETED` : Tous les ennemis morts
   - `LEVEL_COMPLETED` → `RUNNING` : Touche ENTRÉE (niveau suivant)
   - `RUNNING` → `GAME_OVER` : Mort du joueur (vies = 0)
   - `GAME_OVER` → `RUNNING` : Touche ENTRÉE (rejeu avec réinitialisation)
3. **Gestion des inputs** :
   - Inputs de jeu (flèches, espace) actifs uniquement en état `RUNNING`
   - Touche ENTRÉE active selon l'état pour transitions
   - Filtrage automatique selon l'état actuel
4. **Réinitialisation** :
   - Nouvelle grille avec blocs destructibles aléatoires
   - Nouveaux power-ups cachés (génération différente)
   - Nouveaux ennemis placés aléatoirement (nombre selon niveau)
   - Reset du joueur à la position de départ
   - **Conservation** : Score et high score conservés
5. **Affichage** :
   - Menu : Titre centré + instructions + contrôles (agrandis)
   - Niveau terminé : Message + score + niveau suivant
   - Game over : Message rouge + overlay + instructions de rejeu
   - Transitions fluides sans changement de fenêtre

## Évolutions Prévues

### Phase 12 - Optimisations Visuelles
- Sprites personnalisés remplaçant les carrés colorés
- Animations fluides pour déplacements et explosions
- Effets de particules pour power-ups et explosions
- Thèmes visuels multiples

### Phase 13 - Fonctionnalités Avancées
- Sons et musiques d'ambiance
- Sauvegarde de progression
- Système de achievements/succès
- Mode multijoueur local (2-4 joueurs)

### Phase 14 - Niveaux et Progression
- Patterns de niveaux prédéfinis
- Boss de fin de niveau avec mécaniques spéciales
- Histoire et campagne narrative
- Éditeur de niveaux personnalisés

## Structure des Fichiers
```
src/main/java/bomberman/bomberman/
├── Launcher.java       # Point d'entrée avec boucle de jeu et gestion des états ✨ MAJORÉ
├── Grid.java          # Modèle de données de la grille + power-ups cachés
├── GridRenderer.java  # Rendu graphique + interface utilisateur + menus ✨ TRANSFORMÉ
├── Player.java        # Logique et position du joueur + système de vies + power-ups ✨ ENRICHI
├── Bomb.java          # Logique des bombes
├── Explosion.java     # Gestion des explosions et destruction
├── TileType.java      # Énumération des types de cases
├── Enemy.java         # Ennemis avec IA simple + système de mort
├── PowerUpType.java   # Énumération des types de power-ups ✨ ÉTENDU
├── PowerUp.java       # Classe des power-ups (position, visibilité, effets) ✨ AMÉLIORÉ
└── GameState.java     # Énumération des états du jeu (menu, partie, niveau terminé, game over)
```

## Conventions de Code

- **Taille des cellules** : 48×48 pixels (agrandie x1.5, constante dans `GridRenderer`)
- **Taille du joueur** : 39×39 pixels avec décalage de 4 pixels pour le centrage
- **Taille des bombes** : 42×42 pixels avec décalage de 3 pixels pour le centrage
- **Taille des ennemis** : 39×39 pixels avec décalage de 4 pixels pour le centrage
- **Taille des power-ups** : 39×39 pixels avec décalage de 4 pixels pour le centrage
- **Polices** : Toutes mises à l'échelle x1.5 (24px, 27px, 36px, 48px, 72px)
- **Interface** : Zone dédiée 720×252px sous la grille
- **Timers** : Gestion avec `System.currentTimeMillis()` et `AnimationTimer`
- **Types de cases** : Énumération `TileType` avec méthodes utilitaires
- **Placement des destructibles** : 30% des cases vides, zone de sécurité joueur
- **Couleurs** : Définies comme constantes dans `GridRenderer`
- **Interface utilisateur** : Rendu via `GraphicsContext.fillText()` avec polices configurées
- **Système de mort** : État boolean `isAlive()` dans Player et Enemy
- **Multi-bombes** : Gestion via List<Bomb> pour joueur et Bomb Rain
- **Commentaires** : JavaDoc pour toutes les méthodes publiques

## Notes Techniques

- Le projet utilise un Canvas JavaFX pour le rendu (performance optimale)
- **Fenêtre agrandie** : 720×780 pixels avec interface repensée
- La grille est stockée comme tableau 2D d'énumérations `TileType`
- **Multi-listes** : activeBombs (joueur) + rainBombs (Bomb Rain) + activeExplosions
- `AnimationTimer` pour les mises à jour en temps réel (bombes/explosions/ennemis)
- Séparation claire entre logique et affichage
- Gestion des événements clavier centralisée dans `Launcher`
- **Destruction dynamique** : `Grid.destroyBlock()` modifie la grille en temps réel
- **Validation des déplacements** : `TileType.isTraversable()` pour la logique de collision
- **Propagation d'explosion** : Arrêt sur destruction ET sur blocs solides
- Génération procédurale des blocs destructibles avec zone de sécurité
- **Interface utilisateur** : Zone dédiée avec centrage mathématique parfait
- **Gestion de la mort** : Vérification systématique de `isAlive()` avant actions
- **High Score persistant** : Sauvegarde automatique dans highscore.txt
- **Système de protection** : Combinaison Shield + invincibilité de respawn
- **Effets temporaires** : Timers automatiques avec updateTemporaryEffects()
- **Système de vitesse** : Cooldown adaptatif basé sur les bonus
- **Overlay visuel** : Couche semi-transparente pour feedback visuel
- **Rendu en couches** : Grille → Explosions → Power-ups → Bombes → Ennemis → Joueur → Overlay → UI → Messages
- **Notifications** : Système d'empilement vertical avec fade automatique 