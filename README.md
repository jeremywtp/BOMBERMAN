# Bomberman Base - Architecture Évolutive ✨ ÉDITION AGRANDIE x1.5

## Description
Projet JavaFX 17.0.6 avec Java 23.0.2 implémentant une base évolutive pour un jeu Bomberman. Cette version inclut maintenant un joueur déplaçable avec contrôles clavier, **pose de bombes multiples et explosions** 💣, **blocs destructibles** 🧱💥, **ennemis avec IA simple** 👹, **interface utilisateur avec système de vies multiples** 💖, **power-ups permanents et temporaires** ✨💎, **gestion complète des états de jeu** 🎮, et **affichage agrandi x1.5 avec interface optimisée** 🖥️.

## 🚀 NOUVELLES FONCTIONNALITÉS MAJEURES

### ⏱️ **Système de Timer Global** ✨ **NOUVEAU**
- **Durée limite** : 2 minutes 30 secondes (150 secondes) par vie
- **Perte automatique** : Le joueur perd 1 vie si le timer atteint zéro
- **Réinitialisation** : Timer remis à zéro à chaque début de niveau ou mort du joueur
- **Barre visuelle** : 15 segments colorés (1 segment = 10 secondes)
  - **Vert** : > 60 secondes restantes (plus de 6 segments)
  - **Orange** : 30-60 secondes restantes (3-6 segments)  
  - **Rouge** : < 30 secondes restantes (moins de 3 segments)
- **Affichage temps** : Format MM:SS sous la barre (ex: "⏱️ 2:30")
- **Gestion pause** : Timer suspendu pendant le menu pause
- **Positionnement** : Centré sous la ligne principale de l'ATH
- **Intégration parfaite** : 600px de large, centré dans la fenêtre 720px

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

### 🧱 **Bombes Bloquantes - Comportement Classique** ✨ **NOUVEAU**
- **Obstacle solide** : Une fois posée, la bombe devient un mur infranchissable
- **Exception temporaire** : Le joueur peut sortir de la bombe qu'il vient de poser
- **Blocage définitif** : Dès que le joueur quitte la case, impossible d'y revenir
- **Ennemis bloqués** : Les ennemis ne peuvent jamais traverser les bombes
- **Stratégie renforcée** : Placement tactique devient crucial
- **Fidélité classique** : Reproduit le comportement authentique Bomberman
- **Gestion intelligente** : Système BombCollisionChecker avec interfaces fonctionnelles

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
- **Vies multiples** : 6 vies réelles avec respawn et invincibilité temporaire (affichage 5/5 → 0/5 + dernière chance) ✨ **MODIFIÉ**
- **Timer de niveau** : Compte à rebours de 2min30s par vie avec barre visuelle
  - Perte automatique d'une vie à zéro
  - Réinitialisation à chaque début de niveau ou mort
  - Barre de 15 segments (blanc/noir) avec agencement vertical professionnel :
    - Zone ATH (50px) : LEVEL / SCORE / HIGHSCORE centré
    - Zone Timer (50px) : Barre 500x10px avec marges respiratoires
    - Zone Grille (528px) : Jeu décalé +100px sans superposition
    - Zone Notifications élargie : Fenêtre agrandie +80px (780→860px)
  - Affichage temps en format ⏰ MM:SS au centre de la barre
- **Système de vitesse** : Cooldown basé sur les bonus de vitesse
- **Notifications** : Système d'alertes amélioré avec zone élargie
  - Fenêtre agrandie de 780px à 860px (+80px)
  - Jusqu'à 10 notifications simultanées (vs 5 auparavant)
  - Police 14px avec espacement optimal (22px entre lignes)
  - Durée d'affichage : 4 secondes avec effet fade
- **États de jeu** : Menu → Jeu → Niveau terminé → Game Over

### 🧹 **Interface Épurée** ✨ **NOUVEAU**
- **Notifications ciblées** : Seuls les messages de power-ups s'affichent à l'écran
- **Suppression des distractions** : Plus de spam de messages système ou de porte
- **Focus gameplay** : Interface claire pour une expérience immersive
- **Messages pertinents** : Seules les réussites importantes sont mises en valeur
- **Feedback utile** : Information directement liée aux actions du joueur

### 🎬 **Animations d'Introduction de Niveau** ✨ **NOUVEAU**
- **Overlay visuel immersif** : Affichage "LEVEL X" avec fond noir semi-transparent (70%)
- **Style arcade authentique** : Police Arial Bold 72px en jaune clair avec contour
- **Synchronisation audio** : Animation pendant toute la durée de `Level_Start.wav` (≈3s)
- **Transition fluide** : Disparition automatique au début du gameplay
- **Fond visible** : La carte reste visible derrière l'overlay (assombrie)
- **Expérience immersive** : Feedback visuel + audio élégant avant chaque niveau

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
  - **NOUVEAU** : Méthode `isBombBlockingMovement(x, y, isPlayer)` pour collision bombes
  - **NOUVEAU** : Synchronisation automatique des bombes avec `updateTraversability()`
  - **NOUVEAU** : Animation d'introduction `renderLevelStart()` avec overlay
  - **NOUVEAU** : Gestion de l'état `LEVEL_STARTING` dans `updateGame()`
  - **NOUVEAU** : Interface épurée avec notifications ciblées power-ups uniquement
  - **NOUVEAU** : Timer global 2min30s avec perte automatique de vie et barre visuelle
  - Crée les instances du modèle (`Grid`), du joueur (`Player`), des ennemis (`Enemy`) et du renderer (`GridRenderer`)
  - Configure la scène JavaFX et gère les événements clavier
  - Gère l'`AnimationTimer` pour les bombes, explosions et ennemis
  - Gestion des power-ups (collecte, révélation, application des effets)
  - Gestion complète des états du jeu (menu, partie, niveau terminé, game over)
  - Système de rejouabilité avec réinitialisation complète
- **Évolutions** : Multi-bombes, high score, niveaux, power-ups temporaires, vies multiples, bombes bloquantes, intro niveau, interface épurée

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
  - **NOUVEAU** : Système de notifications épuré (power-ups uniquement)
  - **NOUVEAU** : Effets visuels pour power-ups temporaires
  - **NOUVEAU** : Multi-bomb rendering avec gestion des listes
  - **NOUVEAU** : Animation d'introduction `renderLevelIntroOverlay(currentLevel)`
  - **NOUVEAU** : Overlay semi-transparent avec texte "LEVEL X" centré
  - **NOUVEAU** : Barre de timer global avec 15 segments colorés et affichage temps
  - Dessine la grille sur un Canvas JavaFX
  - Gère toutes les couleurs du jeu
  - Rendu des blocs destructibles (marron clair #A0522D)
  - Interface utilisateur avec affichage des vies multiples
  - Écrans de menu, game over, niveau terminé
  - Messages textuels dynamiques
- **Évolutions** : Zone UI dédiée, centrage optimal, effets visuels, multi-rendu, intro niveau, notifications épurées

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
  - **NOUVEAU** : Interface BombCollisionChecker pour vérification de bombes bloquantes
  - Gère les déplacements dans les 4 directions avec validation des collisions
  - Empêche les déplacements vers les cases solides, destructibles ET bombes bloquantes
  - Respawn avec invincibilité temporaire après mort
- **Évolutions** : Vies multiples, power-ups temporaires, multi-bombes, système de vitesse, collision bombes

#### 5. `Bomb.java` 🧱 **TRANSFORMÉ**
- **Rôle** : Logique et état des bombes avec système de blocage intelligent
- **Responsabilités** :
  - Stocke la position de la bombe (x, y)
  - Gère le timer d'explosion (2 secondes)
  - Fournit l'état de la bombe (active, explosée)
  - **NOUVEAU** : Système de traversabilité temporaire (`canPlayerTraverse`, `isPlayerStillOnBomb`)
  - **NOUVEAU** : Méthode `updateTraversability(playerX, playerY)` pour synchronisation
  - **NOUVEAU** : Méthode `blocksMovementFor(x, y, isPlayer)` pour collision intelligente
  - **NOUVEAU** : Interface BombCollisionChecker pour découplement
  - Portée d'explosion : Variable selon le joueur (1+ cases dans chaque direction)
- **Évolutions** : Bombes bloquantes avec exception temporaire pour le joueur
- **Utilisation** : Géré en List<Bomb> dans Launcher avec vérification de collision

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

#### 8. `Enemy.java` 🧱 **AMÉLIORÉ**
- **Rôle** : Ennemis avec IA simple et respect des bombes bloquantes
- **Responsabilités** :
  - IA de déplacement autonome (mouvement toutes les 500ms)
  - Direction persistante jusqu'à rencontrer un obstacle
  - Changement de direction aléatoire quand bloqué
  - État `isAlive()` et méthode `kill()` pour la gestion de la mort
  - Collision mortelle : Contact avec le joueur tue le joueur (si non protégé)
  - **NOUVEAU** : Interface BombCollisionChecker pour vérification de bombes
  - **NOUVEAU** : Respect des bombes comme obstacles infranchissables
- **Comportement** : Les ennemis ne traversent pas les blocs solides, destructibles ET bombes
- **Énumération** : `Direction` (UP, DOWN, LEFT, RIGHT)
- **Évolutions** : Intégration collision bombes avec logique simplifiée (isPlayer=false)

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

#### 11. `ExitDoor.java` ✨ **NOUVEAU**
- **Rôle** : Représentation de la porte de sortie pour terminer le niveau
- **Responsabilités** :
  - Stocke la position (x, y), visibilité et état d'activation
  - Toujours visible mais avec deux états distincts
  - S'active uniquement quand tous les ennemis sont morts
  - Permet de passer au niveau suivant quand activée + joueur dessus
  - Méthodes : `isActivated()`, `activate()`, `canUseToExit()`
- **Cycle de vie** : Inactive → Activée → Utilisée
- **Apparence** : 
  - Inactive : Marron doré, pulsation lente
  - Activée : Or brillant, texte "EXIT", pulsation rapide
- **Positionnement** : Aléatoire parmi les blocs destructibles, minimum 3 cases du joueur

#### 12. `GameState.java` ✨ **ÉTENDU**
- **Rôle** : Énumération des états du jeu
- **États disponibles** :
  - `START_MENU` : Menu de démarrage avec instructions
  - **NOUVEAU** : `LEVEL_STARTING` : Démarrage de niveau (pendant Level_Start.wav, inputs bloqués) ✨
  - `RUNNING` : Partie en cours (gameplay normal)
  - `LEVEL_COMPLETED` : Écran de transition entre niveaux
  - `GAME_OVER` : Écran de fin avec option de rejeu
- **Utilisation** : Contrôle du flux principal et des inputs selon l'état
- **Sécurité** : Empêche les inputs non désirés pendant les transitions ✨ **NOUVEAU**

#### 13. `SoundManager.java` 🎵 **ÉTENDU**
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

### 🎵 Musiques et Effets Sonores ✨ **NOUVEAU**
1. **Musique d'intro** :
   - 📁 `intro.wav` — Musique de fond du menu principal 
   - 🔁 Boucle infinie active dès le lancement
   - ⏹️ Arrêt automatique lors du démarrage de partie
2. **Musique de démarrage de niveau** :
   - 📁 `Level_Start.wav` — Musique de transition niveau
   - ▶️ Lecture unique (non-loopée) au début de chaque niveau
   - 📅 Callback automatique pour démarrer le gameplay après la fin
3. **Musique de fond de niveau** ✨ **NOUVEAU** :
   - 📁 `Theme_World_1.wav` — Musique de fond du niveau 1
   - 🔁 Boucle infinie pendant toute la durée du niveau 1
   - ⏹️ Arrêt automatique à la fin du niveau (victoire ou défaite)
4. **Effets sonores de menu** :
   - 📁 `Menu_Cursor.wav` — Son de navigation (↑/↓)
   - 📁 `Menu_Select.wav` — Son de sélection (ENTRÉE)
   - ⚡ Latence absolument nulle (AudioClip pooling)
5. **Effets sonores de gameplay** ✨ **NOUVEAU** :
   - 📁 `Walking.wav` — Son de marche du joueur (format WAV PCM)
   - 🚶 Joué automatiquement à chaque déplacement de case en case
   - ⏱️ Cooldown de 300ms pour éviter l'empilement de sons
   - 🛡️ N'est PAS joué si le joueur est mort (mais fonctionne pendant l'invincibilité)
   - ⚡ Compatible avec tous les modes de vitesse (normal, SPEED_UP, SPEED_BURST)
   - ⚡ Latence absolument nulle (AudioClip pooling)
   - 📁 `Dies.wav` — Son de mort du joueur (format WAV PCM) ✨ **NOUVEAU**
   - ⚰️ Joué instantanément quand le joueur perd une vie (collision explosion/ennemi)
   - 🚫 Ne se joue PAS si le joueur est invincible (protection active)
   - 🔊 Volume équilibré (0.8) pour éviter la saturation audio
   - ⚡ Une seule fois par mort (pas de répétition en cascade)
   - ⚡ Latence absolument nulle (AudioClip pooling)
   - 📁 `Bomb_Place.wav` — Son de placement de bombe ✨ **NOUVEAU**
   - 💣 Joué instantanément à chaque placement de bombe (barre d'espace)
   - 🎯 Compatible avec système multi-bombes et Bomb Rain
   - ⚡ Latence absolument nulle (AudioClip pooling optimisé)
   - 🔊 Volume équilibré (0.9) pour retour audio immédiat
   - 📁 `Bomb_Explodes.wav` — Son d'explosion de bombe ✨ **NOUVEAU**
   - 💥 Joué instantanément à chaque explosion (après 2 secondes de timer)
   - 🌟 Compatible avec explosions multiples simultanées
   - ⚡ Latence absolument nulle (AudioClip pooling optimisé)
   - 🔊 Volume équilibré (0.9) pour impact sonore maximal
   - 📁 `Level_Clear.wav` — Son de fin de niveau ✨ **NOUVEAU**
   - 🏆 Joué une seule fois lorsque le joueur termine un niveau avec succès
   - 🚪 Se déclenche après utilisation de la porte de sortie (tous ennemis morts)
   - 🎵 Son complet non interrompu avant transition vers niveau suivant
   - 🔊 Volume équilibré pour célébrer la victoire du niveau
6. **Stockage** : 
   - 📂 Tous les fichiers dans `src/main/resources/music/`
   - 🎵 WAV PCM pour toutes les musiques et effets (compatibilité JavaFX)
7. **Gestion technique** :
   - MediaPlayer pour musiques longues (intro, niveau)
   - AudioClip pooling pour effets instantanés
   - Arrêt propre selon les transitions d'état du jeu

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
  - **NOUVEAU** : 5 vies avec respawn et invincibilité temporaire (affichage 5/5 → 0/5) ✨ **MODIFIÉ**
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
  - **RESTRICTION** ✨ **NOUVEAU** : Impossible de poser une bombe sur la porte de sortie visible
- **Explosions** :
  - Flammes orange (#FF8800) en forme de croix
  - Portée : 1+ cases dans chaque direction (selon power-ups) ✨ **MODIFIÉ**
  - S'arrête sur les blocs solides
  - S'arrête sur la porte de sortie visible (agit comme un mur solide) ✨ **NOUVEAU**
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
    - **Ligne 1** : ❤️ VIES : X/5 (5→0) + 💣 BOMBES : X/Y (polices 27px) ✨ **MODIFIÉ**
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
- **Flèche Haut/Bas** : Naviguer entre les options
- **ENTRÉE** : Lancer une nouvelle partie (seule option active)

### Menu pause :
- **Flèche Haut/Bas** : Naviguer entre les options
- **ENTRÉE** : Sélectionner l'option (Reprendre/Recommencer/Retour au menu)
- **ÉCHAP** : Reprendre directement la partie

### Pendant le jeu :
- **Flèche Haut** : Déplacer le joueur vers le haut
- **Flèche Bas** : Déplacer le joueur vers le bas  
- **Flèche Gauche** : Déplacer le joueur vers la gauche
- **Flèche Droite** : Déplacer le joueur vers la droite
- **Barre d'espace** : Poser une bombe (limite selon power-ups EXTRA_BOMB)
- **ÉCHAP** : Ouvrir/fermer le menu pause

### Écran Niveau Terminé :
- **ENTRÉE** : Passer au niveau suivant

### Écran Game Over :
- **ENTRÉE** : Rejouer (réinitialisation complète)

⚠️ **Tous les autres inputs sont ignorés selon l'état du jeu**

## Mécaniques de Jeu ✨ **ENRICHIES**

### Système de Menu Pause ✨ **NOUVEAU**
1. **Activation** : Touche ÉCHAP pendant le jeu (état RUNNING uniquement)
2. **Effet** : Jeu complètement figé (bombes, ennemis, animations, timers)
3. **Interface** :
   - Fond semi-transparent noir (60% d'opacité) sur tout l'écran
   - Menu centré **par rapport à la zone de jeu uniquement** (pas l'ATH)
   - Fond sombre (80% d'opacité) et bordure blanche
   - Titre "PAUSE" en police Arial Bold 36px
4. **Options disponibles** :
   - **REPRENDRE** : Retourne au jeu (ÉCHAP fonctionne aussi)
   - **RECOMMENCER** : Redémarre le niveau actuel
   - **COMMANDES** : Affiche le panneau d'aide avec toutes les touches
   - **RETOUR AU MENU PRINCIPAL** : Retourne à l'écran d'accueil
5. **Navigation** :
   - **↑/↓** : Naviguer entre les options avec feedback sonore (Menu_Cursor.wav)
   - **ENTRÉE** : Valider la sélection avec feedback sonore (Menu_Select.wav)
   - **ÉCHAP** : Reprendre directement sans passer par le menu
6. **Effets visuels** :
   - Option sélectionnée : Surbrillance jaune avec symboles "► ◄"
   - Fond de sélection : Rectangle doré semi-transparent
   - Interface épurée : Pas d'instructions visibles (remplacées par l'option COMMANDES)
   - Centrage précis : Basé sur les dimensions réelles de la grille (720×528px)
7. **Panneau des commandes** ✨ **AMÉLIORÉ** :
   - Accessible via l'option "COMMANDES" du menu pause
   - Affichage modal centré sur la zone de jeu (450×300px)
   - Fond blanc cassé avec bordure sombre pour contraster
   - Contenu épuré : Uniquement les touches essentielles (↑↓←→, ESPACE, ÉCHAP, ENTRÉE)
   - Bouton "← RETOUR" navigable et sélectionnable avec feedback visuel
   - Navigation : ↑/↓ pour sélectionner, ENTRÉE pour valider
   - États : PAUSED → COMMANDS_DISPLAY → PAUSED
8. **Persistance** : Le jeu reprend exactement où il s'était arrêté
9. **Sécurité** : Impossible d'accéder au menu pause depuis d'autres états

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

### Système de Vies Multiples ✨ **MODIFIÉ**
1. **Vies** : 6 vies réelles (affichage "VIES : 5/5" → "4/5" → "3/5" → "2/5" → "1/5" → "0/5") ✨ **NOUVEAU**
2. **Dernière chance** : À "0/5", le joueur a encore une vie pour jouer ✨ **NOUVEAU**
3. **Mort** : Contact ennemi ou explosion (si non protégé)
4. **Respawn** : Position (1,1) avec invincibilité temporaire
5. **Invincibilité** : Clignotement visuel + protection complète
6. **Game Over** : Seulement après être mort pendant "0/5" ✨ **MODIFIÉ**
7. **Interface** : Mise à jour en temps réel de l'affichage

### Système de Démarrage de Niveau ✨ **NOUVEAU**
1. **Musique d'intro** : `Level_Start.wav` joué automatiquement à chaque nouveau niveau
2. **Durée** : Une seule lecture (non-loopée) avant démarrage effectif du niveau
3. **Blocage sécurisé** : État `LEVEL_STARTING` bloque tous les inputs pendant la musique ✨ **MODIFIÉ**
4. **Callback** : Démarrage automatique du gameplay à la fin de la musique
5. **Gestion d'erreurs** : Le niveau démarre même si la musique ne peut pas être lue
6. **Interface** : Affichage normal avec "NIVEAU X" visible pendant l'intro
7. **Robustesse** : Impossible de revenir au menu par accident pendant la transition ✨ **NOUVEAU**

### Système de Porte de Sortie ✨ **CORRIGÉ & AMÉLIORÉ**
1. **Génération** : Une porte est placée dans un bloc destructible au lancement du niveau
2. **Visibilité** : La porte devient visible après destruction du bloc qui la contient
3. **États** : Inactive (marron doré) → Activée (or brillant avec "EXIT") ✨ **MODIFIÉ**
4. **Activation** : La porte devient utilisable uniquement quand tous les ennemis sont morts
5. **Feedback** : 
   - Message "🚪 PORTE DE SORTIE DÉCOUVERTE !" lors de la révélation
   - Message "🚪 PORTE DE SORTIE ACTIVÉE !" quand la porte s'active
   - Message "❌ Tuez tous les ennemis pour activer la porte !" si tentative prématurée
6. **Mécanique** : Marcher sur la porte active = passage au niveau suivant
7. **Visual cues** : Pulsation lente (inactive) → Pulsation rapide + brillance (active)
8. **Emplacement** : Position aléatoire éloignée du joueur (min. 3 cases de distance)
9. **💣 Comportement avec les bombes** ✨ **NOUVEAU** :
   - **Placement BLOQUÉ** : Impossible de poser une bombe sur la case de la porte visible
   - **Explosion BLOQUÉE** : La porte visible agit comme un **mur solide** pour les explosions
   - **Propagation arrêtée** : L'explosion s'arrête en atteignant la porte (comme un bloc solide)
10. **🚶 Traversabilité** : Le joueur et les ennemis peuvent marcher normalement sur la porte
11. **🔄 Respawn d'Ennemis** ✨ **RESTAURÉ & SÉCURISÉ** :
    - **Déclencheur** : Si une bombe explose et que **sa zone d'explosion inclut la case de la porte révélée**
    - **Conditions** : Porte visible + nombre d'ennemis < maximum pour le niveau
    - **Effet** : Un ennemi apparaît **directement sur la porte** avec **5 secondes d'invincibilité**
    - **Portée** : Fonctionne même si la bombe est **posée à distance** (tant que sa portée atteint la porte)
    - **Limite** : Niveau 1 = 3 ennemis max, Niveau 2 = 4, etc. (plafonné à 8)
    - **Feedback** : Message "⚠️ Un ennemi est sorti de la porte !" + délai de 600ms après explosion
    - **Correction technique** : La porte est incluse dans les cellules affectées même si elle bloque la propagation
    - **🔒 Sécurité anti-exploit** ✨ **NOUVEAU** : 
      - La porte se **désactive automatiquement** pendant qu'un spawn d'ennemi est programmé
      - Impossible de terminer le niveau tant qu'un ennemi va apparaître
      - Message spécial : "❌ Un ennemi va apparaître ! Attendez..."
12. **🔧 Correction de Bug** ✨ **NOUVEAU** :
    - **Problème résolu** : Bloc destructible ne restait plus actif sous une porte révélée
    - **Solution** : Destruction du bloc garantie AVANT vérification de blocage d'explosion
    - **Résultat** : Case contenant la porte toujours accessible après révélation

### Système d'Invincibilité Prolongée ✨ **MODIFIÉ**
1. **Durée** : 10 secondes (augmentée de 2 secondes → 10 secondes) ✨ **NOUVEAU**
2. **Activation automatique** :
   - **Au début de chaque niveau** : Quand le joueur peut bouger (après Level_Start.wav) ✨ **MODIFIÉ**
   - **Après chaque respawn** : Lorsque le joueur perd une vie
3. **Protection complète** : Aucun dégât d'explosion ou de contact ennemi
4. **Effets visuels** : Clignotement ultra rapide (15x/seconde, 33ms) + rendu continu ✨ **MODIFIÉ**
5. **Logs informatifs** : Messages "Invincibilité (10s)" au début et "Invincibilité terminée (10s écoulées)" à la fin
6. **Combinaison** : Compatible avec power-up SHIELD pour protection prolongée
7. **Timing optimisé** : Commence exactement quand le contrôle est rendu au joueur ✨ **NOUVEAU**
8. **But** : Laisse plus de temps au joueur pour s'orienter et planifier sa stratégie

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
5. **Completion** : Tous les ennemis morts → porte de sortie révélée → passage au niveau suivant en marchant dessus ✨ **MODIFIÉ**

### Système de Blocs Destructibles
1. **Génération** : ~30% des cases vides deviennent destructibles au démarrage
2. **Zone de sécurité** : Aucun bloc destructible dans la zone 2×2 autour du joueur
3. **Collision** : Le joueur ne peut pas traverser les blocs destructibles
4. **Destruction** : Une explosion qui touche un bloc destructible le détruit (+10 points)
5. **Propagation** : L'explosion s'arrête après avoir détruit un bloc (ne le traverse pas)
6. **Transformation** : Bloc destructible → Case vide (traversable)

### Système de Bombes ✨ **ENRICHI**
1. **Pose** : Le joueur peut poser des bombes avec la barre d'espace
2. **Limitation** : Nombre maximum selon power-ups EXTRA_BOMB
3. **Timer** : Les bombes explosent automatiquement après 2 secondes
4. **Explosion** : Flammes en croix avec portée variable (selon power-ups RANGE_UP)
5. **Obstacles** : L'explosion s'arrête sur les blocs solides ET destructibles
6. **Affichage** : Les flammes sont visibles pendant 0.5 seconde
7. **Comportement bloquant** ✨ **NOUVEAU** :
   - Les bombes deviennent des **obstacles solides** immédiatement après pose
   - **Exception temporaire** : Le joueur peut sortir de la bombe qu'il vient de poser
   - **Blocage définitif** : Dès que le joueur quitte la case, impossible d'y revenir
   - **Ennemis bloqués** : Les ennemis ne peuvent **jamais** traverser les bombes
   - **Synchronisation** : État mis à jour automatiquement via `updateTraversability()`
   - **Stratégie** : Placement de bombes devient tactiquement crucial

### Événements Déclenchés par les Explosions ✨ **NOUVEAU**
1. **Destruction de blocs** : Les explosions détruisent les blocs destructibles (+10 points)
2. **Révélation de power-ups** : Les power-ups cachés dans les blocs détruits apparaissent
3. **Élimination d'ennemis** : Les ennemis pris dans l'explosion meurent (+100 points)
4. **Dégâts au joueur** : Le joueur perd une vie si pris dans l'explosion (sauf si protégé)
5. **Réapparition d'ennemi** ✨ **NOUVEAU** : Si une bombe explose sur la **porte de sortie déjà révélée** (pas lors de la révélation initiale), 1 ennemi apparaît sur celle-ci **après 600ms** (pour éviter qu'il meure dans l'explosion) **avec 5 secondes d'invincibilité** ✨ **MODIFIÉ**, à condition que la limite d'ennemis du niveau (niveau 1: 3, niveau 2: 4, etc.) ne soit pas atteinte
6. **Révélation de porte** : Si un bloc contenant la porte est détruit, celle-ci est révélée

### Système d'Ennemis avec IA
1. **Génération** : 3+ ennemis placés aléatoirement hors zone de sécurité joueur
2. **IA Simple** : 
   - Mouvement autonome toutes les 500ms
   - Direction persistante (UP, DOWN, LEFT, RIGHT)
   - Changement de direction aléatoire quand bloqué
3. **Collision** : Ne traversent pas les blocs solides, destructibles **ou bombes** ✨ **NOUVEAU**
4. **Mort** : Tués par les explosions uniquement (+100 points)
5. **Interaction** : Contact avec le joueur = mort du joueur (si non protégé)
6. **Invincibilité temporaire** ✨ **NOUVEAU** : Les ennemis qui réapparaissent (via explosion sur porte) bénéficient de **5 secondes d'invincibilité** avec effet visuel de clignotement rouge (200ms), les rendant insensibles aux explosions pendant cette durée

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

### Système de Gestion des États ✨ **ENRICHI**
1. **États du jeu** :
   - **START_MENU** : Affichage du menu principal avec titre et instructions
   - **LEVEL_STARTING** ✨ **NOUVEAU** : Animation d'introduction avec "LEVEL X" et musique
   - **RUNNING** : Partie en cours avec gameplay complet
   - **LEVEL_COMPLETED** : Écran de transition entre niveaux
   - **GAME_OVER** : Écran de fin avec possibilité de rejouer
2. **Transitions d'états** :
   - `START_MENU` → `LEVEL_STARTING` : Touche ENTRÉE (nouvelle partie)
   - `LEVEL_STARTING` → `RUNNING` : Fin automatique de `Level_Start.wav` (≈3s) ✨ **NOUVEAU**
   - `RUNNING` → `LEVEL_COMPLETED` : Tous les ennemis morts
   - `LEVEL_COMPLETED` → `LEVEL_STARTING` : Touche ENTRÉE (niveau suivant) ✨ **MODIFIÉ**
   - `RUNNING` → `GAME_OVER` : Mort du joueur (vies = 0)
   - `GAME_OVER` → `LEVEL_STARTING` : Touche ENTRÉE (rejeu avec réinitialisation) ✨ **MODIFIÉ**
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

### Système de Rendu & Visibilité ✨ **NOUVEAU**
1. **Ordre de rendu optimisé** :
   - **Grille** : Arrière-plan (sol, blocs solides, destructibles)
   - **Explosions** : Flammes d'explosion (sous les entités)
   - **Porte de sortie** : **Dessinée AVANT** les bombes et ennemis ✅
   - **Power-ups** : Articles collectibles visibles
   - **Bombes** : **Par-dessus la porte** (priorité visuelle) ✅
   - **Ennemis** : **Par-dessus la porte** (priorité visuelle) ✅
   - **Joueur** : Toujours visible (priorité maximale)
   - **Overlay** : Effets de mort, transitions
   - **Interface** : UI, score, vies (premier plan)
   - **Messages** : Notifications temporaires (avant-plan)
   - **Menu pause** : Overlay semi-transparent par-dessus tout (priorité absolue)
   - **Panneau commandes** : Modal par-dessus le menu pause (priorité maximale)

2. **Priorité visuelle** :
   - Les **bombes** apparaissent toujours au-dessus de la porte
   - Les **ennemis** apparaissent toujours au-dessus de la porte
   - Meilleure lisibilité du gameplay
   - Feedback visuel clair pour les interactions

3. **Correction appliquée** :
   - Porte dessinée en 3ème position (après explosions, avant tout le reste)
   - Bombes et ennemis désormais visibles par-dessus la porte
   - Ordre de rendu cohérent avec les priorités de gameplay

## 🧱 Détails Techniques : Bombes Bloquantes

### Architecture du Système
```java
// Interface fonctionnelle pour découplage
@FunctionalInterface
public interface BombCollisionChecker {
    boolean isBombBlockingMovement(int x, int y, boolean isPlayer);
}

// Utilisation dans Player.moveUp()
player.moveUp(grid, this::isBombBlockingMovement)

// Utilisation dans Enemy.update()
enemy.update(grid, this::isBombBlockingMovement)
```

### États de Traversabilité
1. **canPlayerTraverse** : `true` → Le joueur peut encore passer
2. **isPlayerStillOnBomb** : `true` → Le joueur n'a pas encore quitté la bombe
3. **Transition** : Dès que le joueur part, les deux deviennent `false` définitivement

### Logique de Collision
```java
// Dans Bomb.blocksMovementFor(entityX, entityY, isPlayer)
if (entityX != x || entityY != y) return false;  // Pas sur cette bombe
if (isPlayer && canPlayerTraverse) return false; // Joueur autorisé
return true; // Bombe bloque le mouvement
```

### Synchronisation Automatique
- Appelée à chaque frame dans `updateGame()`
- Met à jour TOUTES les bombes actives (joueur + Bomb Rain)
- Détection automatique de sortie du joueur
- État persistant jusqu'à explosion

### Comportement par Entité
- **Joueur** : Exception temporaire puis blocage
- **Ennemis** : Blocage immédiat et permanent
- **Explosions** : Ne sont PAS bloquées par les bombes
- **Power-ups** : Placement non affecté

## 🎬 Détails Techniques : Animation d'Introduction

### Architecture du Système
```java
// Dans Launcher.java - initializeLevel()
currentState = GameState.LEVEL_STARTING;
SoundManager.playOnce("level_start", () -> {
    currentState = GameState.RUNNING;
    // Fin de l'animation
});

// Dans updateGame() - Gestion continue
if (currentState == GameState.LEVEL_STARTING) {
    renderLevelStart(); // Affichage continu
    return;
}
```

### Composants de l'Overlay
1. **Fond semi-transparent** : `Color.web("#000000", 0.7)` (70% opacité)
2. **Texte centré** : `Font.font("Arial", FontWeight.BOLD, 72)`
3. **Couleur principale** : `#FFFF88` (jaune clair)
4. **Contour** : `#FFCC00` (jaune/orange, 3px)
5. **Position** : Centre exact de la fenêtre 720×780

### Synchronisation Audio-Visuelle
- **Durée** : Exactement la durée de `Level_Start.wav` (≈3 secondes)
- **Continuité** : Rendu en boucle via `AnimationTimer`
- **Transition** : Disparition automatique avec callback audio
- **États** : `LEVEL_STARTING` → `RUNNING` seamless

### Effets Visuels
- **Lisibilité optimale** : Contour pour contraste
- **Style arcade** : Police et couleurs rétro
- **Non-intrusif** : Fond visible et assombri
- **Centrage parfait** : Calcul mathématique précis

## 🧹 Détails Techniques : Interface Épurée

### Notifications Supprimées
```java
// AVANT - Messages distrayants supprimés :
renderer.addNotification("⚠️ Un ennemi est sorti de la porte !");
renderer.addNotification("🚪 PORTE DE SORTIE DÉCOUVERTE !");
renderer.addNotification("🚪 PORTE DE SORTIE ACTIVÉE !");
renderer.addNotification("❌ Un ennemi va apparaître ! Attendez...");
renderer.addNotification("❌ Tuez tous les ennemis pour activer la porte !");

// APRÈS - Seuls les logs en console restent :
System.out.println("Bloc contenant la porte détruit à (" + x + ", " + y + ")");
```

### Notifications Conservées
```java
// Messages de power-ups CONSERVÉS pour feedback utilisateur :
renderer.addNotification("EXTRA BOMB récupéré ! (+1 bombe max)");
renderer.addNotification("SHIELD récupéré ! (10s protection)");
renderer.addNotification("SPEED BURST récupéré ! (5s vitesse max)");
// ... autres power-ups
```

### Philosophie de Design
1. **Feedback ciblé** : Seules les actions positives du joueur sont notifiées
2. **Interface claire** : Suppression du spam informatif non-critique
3. **Focus gameplay** : L'attention reste sur les mécaniques de jeu
4. **Logs conservés** : Debug et informations techniques en console uniquement

### Impact UX
- **Réduction du bruit visuel** de 83% (5/6 types de messages supprimés)
- **Amélioration de la lisibilité** de l'interface
- **Focus renforcé** sur les récompenses et progressions

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
├── ExitDoor.java      # Classe de la porte de sortie pour terminer le niveau ✨ NOUVEAU
├── PauseMenu.java     # Gestionnaire du menu pause avec navigation clavier ✨ NOUVEAU
└── GameState.java     # Énumération des états du jeu (menu, partie, niveau terminé, game over, pause, commandes) ✨ ENRICHI
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
- **Rendu en couches** : Grille → Explosions → **Porte** → Power-ups → Bombes → Ennemis → Joueur → Overlay → UI → Messages
- **Notifications** : Système d'empilement vertical avec fade automatique 