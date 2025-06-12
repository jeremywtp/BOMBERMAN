# 🎮 Super Bomberman - Édition Complète Ultime

## 📋 Description Générale

**Super Bomberman** est une version complète et moderne du jeu classique Bomberman développée en Java 23 avec JavaFX 17. Cette édition propose une expérience de jeu riche avec des mouvements fluides pixel par pixel, une IA intelligente, un système de profils complet, des thèmes visuels, et de nombreuses fonctionnalités avancées pour une expérience de jeu exceptionnelle.

### 🌟 Caractéristiques Principales
- **6 modes de jeu** différents avec mécaniques uniques
- **Système de profils joueurs** avec statistiques détaillées
- **IA avancée** avec pathfinding intelligent et stratégies d'évasion
- **2 thèmes visuels** complets (Bomberman classique et Pokemon)
- **Système audio** immersif avec musiques et effets sonores
- **Interface moderne** avec menus FXML et navigation intuitive
- **Power-ups avancés** permanents et temporaires
- **Mouvement fluide** pixel par pixel pour tous les personnages

## 🎯 Modes de Jeu Complets

### 🎮 **NORMAL GAME (1 Joueur)**
**Mode solo classique avec progression de niveaux**

#### Objectif
- Éliminer tous les ennemis pour révéler la porte de sortie
- Atteindre la porte pour passer au niveau suivant
- Survivre le plus longtemps possible avec un système de vies

#### Mécaniques Spécifiques
- **Timer global** : 2 minutes 30 secondes par niveau
- **Système de vies** : 6 vies maximum avec respawn automatique
- **Progression** : Difficulté croissante avec plus d'ennemis
- **Power-ups garantis** : 2x EXTRA_BOMB + 1x EXPLOSION_EXPANDER au niveau 1
- **Score** : Points pour ennemis tués, blocs détruits, power-ups collectés

#### Contrôles
- **Flèches directionnelles** : Déplacement fluide
- **Espace** : Poser une bombe
- **Échap** : Menu pause

### 🤝 **COOPERATION (2 Joueurs)**
**Mode coopératif local pour deux joueurs**

#### Objectif
- Collaboration pour éliminer tous les ennemis ensemble
- Les deux joueurs doivent atteindre la porte de sortie
- Partage de l'objectif mais power-ups individuels

#### Mécaniques Spécifiques
- **Power-ups individuels** : Chaque joueur garde ses bonus
- **Vies partagées** : Si un joueur meurt, l'autre peut continuer
- **Victoire commune** : Animation de victoire pour les deux joueurs
- **Scores séparés** : Chaque joueur accumule ses propres points
- **Respawn intelligent** : Repositionnement automatique après mort

#### Contrôles
- **Joueur 1** : Flèches directionnelles + Espace
- **Joueur 2** : ZQSD + Shift gauche

### ⚔️ **BATTLE MODE (2-4 Joueurs)**
**Mode combat multijoueur local**

#### Objectif
- Éliminer tous les autres joueurs pour gagner
- Dernier joueur en vie remporte la victoire
- Utiliser bombes et explosions tactiquement

#### Mécaniques Spécifiques
- **Grille spéciale** : Moins de blocs destructibles pour plus d'action
- **Mort immédiate** : Pas de système de vies, élimination directe
- **Bombes dangereuses** : Toutes les explosions affectent tous les joueurs
- **Power-ups stratégiques** : Collecte pour avantage tactique
- **Fin de partie** : Dès qu'un seul joueur reste en vie

#### Contrôles
- **Joueur 1** : Flèches directionnelles + Espace
- **Joueur 2** : ZQSD + Shift gauche
- **Joueur 3** : IJKL + U
- **Joueur 4** : Pavé numérique (8456) + 0

### 🤖 **VS MACHINE**
**Mode duel contre une IA intelligente**

#### Objectif
- Affronter une IA avancée en combat singulier
- Éliminer l'IA avant qu'elle ne vous élimine
- Tester vos compétences contre un adversaire intelligent

#### Mécaniques Spécifiques
- **IA avancée** : Pathfinding avec A*, évitement des bombes
- **Stratégies adaptatives** : L'IA change de comportement selon la situation
- **Sécurité renforcée** : L'IA ne se suicide jamais
- **Fin immédiate** : Partie terminée dès qu'un joueur meurt
- **Statistiques automatiques** : Victoires/défaites enregistrées dans le profil

#### Fonctionnalités IA
- **Pathfinding sécurisé** : Vérification des voies d'évasion avant placement de bombe
- **Timing précis** : Calculs de 350ms par case + marge de sécurité
- **Évitement multi-bombes** : Prise en compte de toutes les menaces simultanées
- **Distance de sécurité** : Maintien de Range + 1 cases minimum
- **Détection de blocage** : Évitement des situations sans issue

#### Contrôles
- **Joueur humain** : Flèches directionnelles + Espace
- **IA** : Contrôlée automatiquement

### 🎨 **THEMES**
**Sélection et personnalisation des thèmes visuels**

#### Thèmes Disponibles

##### Thème BOMBERMAN (Classique)
- **Style** : Bomberman original authentique
- **Couleurs** : Palette traditionnelle verte/marron/dorée
- **Sprites** : Design rétro fidèle au jeu original
- **Ambiance** : Nostalgique et classique

##### Thème POKEMON
- **Style** : Univers Pokemon coloré
- **Couleurs** : Rouge/bleu/jaune Pokemon
- **Sprites** : Personnages et éléments Pokemon
- **Ambiance** : Moderne et dynamique

#### Fonctionnalités
- **Aperçu en temps réel** : Visualisation avant application
- **Sauvegarde automatique** : Thème conservé entre les sessions
- **Application immédiate** : Changement instantané dans le jeu
- **Interface cohérente** : Tous les éléments visuels adaptés

### 👤 **PROFILS**
**Système complet de gestion des profils joueurs**

#### Gestion des Profils
- **Création** : Nom, prénom, choix d'avatar simplifié
- **Modification** : Édition de toutes les informations
- **Suppression** : Avec confirmation de sécurité
- **Sélection** : Joueur actuel pour les parties

#### Statistiques Automatiques
- **Parties jouées** : Compteur total de toutes les parties
- **Parties gagnées** : Nombre de victoires
- **Taux de victoire** : Pourcentage de réussite calculé automatiquement
- **Meilleur score** : Score maximum atteint
- **Dates** : Création du profil et dernière partie jouée

#### Avatars Simplifiés
- **"Pokemon"** : Style Pokemon avec sprites colorés
- **"Bomberman"** : Style classique Bomberman
- **Aperçu visuel** : Sprite affiché en temps réel dans l'interface
- **Compatibilité** : Support des anciens profils avec noms de fichiers

## 🎮 Système de Jeu Avancé

### 🏃‍♂️ **Mouvement Fluide Pixel par Pixel**

#### Caractéristiques
- **Mouvement continu** : Déplacement fluide sans grille rigide
- **Collision précise** : Détection pixel-perfect avec l'environnement
- **Vitesse variable** : Affectée par les power-ups de vitesse
- **Animation synchronisée** : Sprites animés selon le mouvement

#### Mécaniques
- **Vitesse de base** : 120 pixels/seconde
- **Accélération** : +0.5 par power-up SPEED_UP
- **SPEED_BURST** : Vitesse triplée temporairement
- **Collision intelligente** : Glissement le long des obstacles

### 💣 **Système de Bombes Avancé**

#### Placement et Explosion
- **Timer fixe** : 2 secondes avant explosion
- **Animation** : Clignotement accéléré avant explosion
- **Traversabilité** : Le poseur peut traverser sa bombe initialement
- **Réactions en chaîne** : Les explosions déclenchent d'autres bombes

#### Mécaniques Avancées
- **Portée variable** : Affectée par les power-ups EXPLOSION_EXPANDER
- **Propagation** : En forme de croix, arrêtée par les obstacles
- **Destruction** : Blocs destructibles détruits, power-ups révélés
- **Dégâts** : Joueurs et ennemis éliminés instantanément

### 🎁 **Power-Ups Complets**

#### Power-Ups Permanents
- **EXTRA_BOMB** : +1 bombe maximum simultanée
- **EXPLOSION_EXPANDER** : +1 portée d'explosion

#### Power-Ups Temporaires (Futurs)
- **SHIELD** : Protection 10 secondes avec effets visuels
- **SPEED_BURST** : Vitesse maximale 5 secondes
- **BOMB_RAIN** : 5 bombes automatiques

#### Mécaniques
- **Révélation** : Cachés dans les blocs destructibles
- **Collection** : Contact direct pour activation
- **Effets visuels** : Notifications et animations
- **Persistance** : Power-ups permanents conservés entre niveaux

### 👾 **Ennemis et IA**

#### Types d'Ennemis
- **Puropen** : Ennemi de base avec mouvement aléatoire
- **IA Bot** : Adversaire intelligent en mode VS Machine

#### Comportements
- **Mouvement** : Déplacement fluide avec animation
- **Collision** : Détection avec joueurs et environnement
- **Invincibilité** : Période temporaire après spawn
- **Élimination** : Par explosion ou contact selon le mode

## 🎵 Système Audio Immersif

### 🎼 **Musiques**

#### Musiques de Fond
- **Intro** : Musique du menu principal en boucle
- **Level_Start** : Jingle de début de niveau
- **Theme_World_1** : Musique de gameplay du niveau 1
- **Level_Clear** : Fanfare de victoire

#### Gestion Audio
- **Transitions fluides** : Changement automatique selon l'état
- **Volume séparé** : Contrôle indépendant musique/effets
- **Boucles intelligentes** : Répétition sans coupure
- **Arrêt propre** : Fade-out lors des transitions

### 🔊 **Effets Sonores**

#### Effets de Gameplay
- **Bomb_Place** : Son de placement de bombe (latence minimale)
- **Bomb_Explodes** : Son d'explosion puissant
- **Walking** : Pas de Bomberman avec cooldown
- **Dies** : Son de mort dramatique

#### Effets d'Interface
- **Menu_Cursor** : Navigation dans les menus
- **Menu_Select** : Validation des choix

#### Optimisations
- **Pool d'AudioClip** : 3 instances préchargées par effet
- **Latence zéro** : Lecture instantanée des effets critiques
- **Volume adaptatif** : Ajustement selon le type d'effet
- **Cooldown intelligent** : Évitement de l'empilement sonore

## 🖥️ Interface Utilisateur Moderne

### 📱 **Menus FXML**

#### Menu Principal
- **Navigation clavier/souris** : Support complet des deux modes
- **Effets visuels** : Animations et transitions fluides
- **Feedback audio** : Sons de navigation et sélection
- **Design moderne** : Interface épurée et intuitive

#### Menu Pause
- **Options complètes** : Reprendre, redémarrer, options, commandes, menu principal
- **Contrôles audio** : Volume musique et effets séparés
- **Navigation intuitive** : Flèches + Entrée ou souris
- **Sauvegarde automatique** : Préférences conservées

#### Menu Thèmes
- **Aperçu en temps réel** : Visualisation des personnages et gameplay
- **Navigation simple** : Flèches gauche/droite pour changer
- **Application immédiate** : Changement instantané
- **Descriptions détaillées** : Informations sur chaque thème

#### Menu Profils
- **CRUD complet** : Créer, lire, modifier, supprimer
- **Validation** : Vérification des données saisies
- **Aperçu avatar** : Sprite affiché en temps réel
- **Statistiques** : Affichage des performances

### 🎮 **Interface de Jeu**

#### Zone de Jeu
- **Résolution** : 720×528 pixels optimisée
- **Centrage automatique** : Adaptation à la fenêtre
- **Rendu fluide** : 60 FPS constant
- **Effets visuels** : Explosions, animations, particules

#### Interface Utilisateur (HUD)
- **Zone dédiée** : 720×252 pixels pour les informations
- **Informations complètes** :
  - Niveau actuel et progression
  - Score en temps réel
  - Vies restantes avec icônes
  - Power-ups actifs avec compteurs
  - Timer global avec alerte
  - Bombes disponibles

#### Système de Notifications
- **Notifications empilées** : Messages superposés
- **Durée adaptative** : Affichage selon l'importance
- **Couleurs contextuelles** : Rouge pour danger, vert pour bonus
- **Animation fluide** : Apparition et disparition en fondu

## 🎯 Contrôles Complets

### 🎮 **Contrôles de Jeu**

#### Joueur 1 (Principal)
- **↑ ↓ ← →** : Déplacement fluide dans toutes les directions
- **Espace** : Poser une bombe
- **Échap** : Ouvrir le menu pause

#### Joueur 2 (Coopération/Battle)
- **Z Q S D** : Déplacement (Haut, Gauche, Bas, Droite)
- **Shift Gauche** : Poser une bombe

#### Joueur 3 (Battle Mode)
- **I J K L** : Déplacement (Haut, Gauche, Bas, Droite)
- **U** : Poser une bombe

#### Joueur 4 (Battle Mode)
- **8 4 5 6** (Pavé numérique) : Déplacement
- **0** (Pavé numérique) : Poser une bombe

### 🖱️ **Contrôles de Menu**

#### Navigation Universelle
- **↑ ↓** : Navigation verticale dans les menus
- **← →** : Navigation horizontale (thèmes, options)
- **Entrée** : Valider la sélection
- **Échap** : Retour/Annuler

#### Souris
- **Clic** : Sélection directe des boutons
- **Survol** : Mise en surbrillance avec son
- **Molette** : Navigation dans certains menus

## 🛠️ Architecture Technique

### 🏗️ **Structure du Code**

#### Classes Principales
- **`Launcher`** : Contrôleur principal et boucle de jeu
- **`Grid`** : Modèle de données de la grille
- **`GridRenderer`** : Moteur de rendu graphique
- **`FluidMovementPlayer`** : Logique des joueurs avec mouvement fluide
- **`FluidMovementEnemy`** : IA des ennemis avec pathfinding

#### Gestionnaires
- **`SoundManager`** : Gestionnaire audio centralisé
- **`SpriteManager`** : Gestionnaire des sprites par thème
- **`ProfileManager`** : Gestionnaire des profils (Singleton)
- **`FXMLMenuManager`** : Gestionnaire des menus FXML
- **`ThemeSelector`** : Sélecteur et applicateur de thèmes

#### Objets de Jeu
- **`Bomb`** : Logique des bombes et explosions
- **`Explosion`** : Gestion des explosions et propagation
- **`PowerUp`** : Power-ups avec effets et animations
- **`ExitDoor`** : Porte de sortie avec animations

### 🎨 **Système de Thèmes**

#### Architecture
- **`Theme`** : Énumération des thèmes disponibles
- **`SpriteManager.ThemeSprites`** : Container des sprites par thème
- **Chargement dynamique** : Sprites chargés selon le thème actif
- **Cache intelligent** : Sprites mis en cache pour performance

#### Sprites Supportés
- **Joueur** : Sprites fixes (4 directions), marche (8 sprites), mort (8 frames), victoire (9 frames)
- **Ennemis** : Animations complètes par direction
- **Environnement** : Herbe, blocs, contours de carte
- **Objets** : Bombes, explosions, power-ups, porte
- **Interface** : Images d'intro, icônes, aperçus

### 💾 **Système de Sauvegarde**

#### Profils Joueurs
- **Format** : Sérialisation Java native
- **Emplacement** : Dossier `profiles/` dans le projet
- **Sauvegarde automatique** : Après chaque partie
- **Récupération d'erreur** : Gestion des fichiers corrompus

#### Préférences
- **Thème** : Sauvegardé dans `theme.txt`
- **High Score** : Sauvegardé dans `highscore.txt`
- **Volumes audio** : Conservés dans les préférences système

## 🧪 Tests et Validation

### 🔍 **Tests Recommandés**

#### Système de Profils
1. **Créer un profil** avec avatar "Pokemon"
2. **Jouer en VS Machine** et perdre volontairement
3. **Vérifier** l'enregistrement de la défaite dans les statistiques
4. **Rejouer et gagner** pour tester l'enregistrement des victoires
5. **Modifier le profil** pour tester l'édition complète
6. **Supprimer et recréer** pour tester la persistance

#### IA et Gameplay
1. **Observer l'IA** en mode VS Machine pendant plusieurs parties
2. **Vérifier** qu'elle ne se suicide plus avec les bombes
3. **Tester** les réactions en chaîne d'explosions
4. **Valider** la collecte et les effets des power-ups
5. **Confirmer** la sauvegarde automatique des thèmes

#### Modes Multijoueurs
1. **Tester** le mode Coopération à 2 joueurs complet
2. **Valider** le mode Battle à 4 joueurs simultanés
3. **Vérifier** tous les contrôles de chaque joueur
4. **Tester** les animations de victoire en coopération
5. **Valider** les statistiques individuelles

#### Audio et Interface
1. **Tester** tous les effets sonores et musiques
2. **Valider** les contrôles de volume séparés
3. **Vérifier** les transitions audio fluides
4. **Tester** la navigation clavier/souris dans tous les menus
5. **Valider** les notifications et feedback visuels

### 🐛 **Problèmes Résolus**

#### Corrections Majeures
- **IA suicidaire** : Système de sécurité renforcé pour éviter l'auto-destruction
- **Statistiques manquantes** : Enregistrement automatique des victoires/défaites
- **Profils complexes** : Simplification des avatars en "Pokemon"/"Bomberman"
- **Interface incohérente** : Unification des menus FXML et Canvas
- **Audio désynchronisé** : Optimisation avec pools d'AudioClip

#### Améliorations
- **Performance** : Optimisation du rendu et des collisions
- **Compatibilité** : Support des anciens profils et migration automatique
- **Ergonomie** : Navigation intuitive et feedback utilisateur
- **Stabilité** : Gestion d'erreur robuste et récupération automatique

## 📊 Statistiques et Progression

### 📈 **Système de Score**

#### Points par Action
- **Ennemi éliminé** : +100 points
- **Bloc destructible détruit** : +10 points
- **Power-up collecté** : +50 points
- **Niveau terminé** : Bonus selon le temps restant

#### Calculs Avancés
- **Multiplicateurs** : Selon la difficulté et le mode
- **Bonus temporels** : Plus de points si niveau terminé rapidement
- **Pénalités** : Réduction pour morts multiples
- **High Score** : Meilleur score global sauvegardé

### 📊 **Statistiques Détaillées**

#### Par Profil
- **Parties totales** : Compteur de toutes les parties jouées
- **Victoires** : Nombre de parties gagnées
- **Défaites** : Nombre de parties perdues
- **Taux de victoire** : Pourcentage calculé automatiquement
- **Meilleur score** : Score maximum atteint
- **Temps de jeu** : Durée totale de jeu (futur)

#### Globales
- **Progression** : Niveau maximum atteint
- **Achievements** : Succès débloqués (futur)
- **Préférences** : Thème favori, contrôles personnalisés

## 🔧 Installation et Configuration

### 💻 **Prérequis Système**

#### Logiciels Requis
- **Java** : Version 23.0.2 ou supérieure
- **JavaFX** : Version 17 ou supérieure
- **Maven** : Version 3.x pour la compilation
- **Système** : Windows, macOS, ou Linux

#### Ressources Système
- **RAM** : 512 MB minimum, 1 GB recommandé
- **Stockage** : 100 MB d'espace libre
- **Processeur** : Dual-core 1.5 GHz minimum
- **Graphiques** : Support OpenGL 2.0

### 🚀 **Compilation et Lancement**

#### Compilation Maven
```bash
# Compilation complète
mvn clean compile

# Compilation avec tests
mvn clean compile test

# Package JAR
mvn clean package
```

#### Lancement Direct
```bash
# Avec Maven
mvn javafx:run

# Avec Java (après compilation)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.media -cp target/classes bomberman.bomberman.Launcher
```

#### Scripts de Lancement
- **Windows** : `launch.bat` (à créer)
- **Linux/macOS** : `launch.sh` (à créer)

### ⚙️ **Configuration Avancée**

#### Paramètres de Performance
- **Résolution** : Modifiable dans `Launcher.java`
- **FPS** : Limité à 60 FPS par défaut
- **Qualité audio** : Ajustable via les menus
- **Cache sprites** : Préchargement automatique

#### Personnalisation
- **Thèmes** : Ajout de nouveaux thèmes possible
- **Sprites** : Remplacement des images dans `/resources/sprites/`
- **Sons** : Ajout de nouveaux effets dans `/resources/music/`
- **Niveaux** : Modification de la génération dans `Grid.java`

## 📚 Documentation Technique

### 📖 **Javadoc**

#### Génération
```bash
# Générer la documentation API
mvn javadoc:javadoc

# Ouvrir la documentation
./open-javadoc.sh    # Linux/macOS
open-javadoc.bat     # Windows
```

#### Contenu
- **39 classes** entièrement documentées
- **Toutes les méthodes** avec descriptions complètes
- **Exemples d'utilisation** et cas particuliers
- **Relations entre classes** et diagrammes
- **Index de recherche** et navigation intuitive

#### Accès
- **Local** : `target/site/apidocs/index.html`
- **Navigation** : Index alphabétique, arbre hiérarchique
- **Recherche** : Fonction de recherche intégrée

### 🏗️ **Architecture Détaillée**

#### Patterns Utilisés
- **Singleton** : `ProfileManager`, `SoundManager`, `SpriteManager`
- **Observer** : Système de notifications et callbacks
- **Strategy** : Différents comportements d'IA
- **Factory** : Création d'objets de jeu
- **MVC** : Séparation modèle/vue/contrôleur

#### Flux de Données
1. **Input** : Capture des événements clavier/souris
2. **Logic** : Traitement dans `Launcher` et classes métier
3. **Render** : Affichage via `GridRenderer` et FXML
4. **Audio** : Gestion via `SoundManager`
5. **Persistence** : Sauvegarde via `ProfileManager`

## 🚀 Fonctionnalités Avancées

### 🎮 **Système de Jeu Intelligent**

#### Timer Global
- **Durée** : 2 minutes 30 secondes par niveau
- **Affichage** : Compte à rebours visible dans l'interface
- **Alerte** : Changement de couleur quand temps critique
- **Pause** : Timer suspendu pendant les menus
- **Reset** : Réinitialisation après respawn

#### Gestion des États
- **États multiples** : Menu, jeu, pause, game over, victoire
- **Transitions fluides** : Changements d'état sans coupure
- **Sauvegarde d'état** : Reprise possible après pause
- **Gestion d'erreur** : Récupération automatique en cas de problème

### 🎨 **Effets Visuels Avancés**

#### Animations
- **Joueurs** : Marche, mort, victoire avec timing précis
- **Ennemis** : Mouvement fluide et animations directionnelles
- **Bombes** : Clignotement accéléré avant explosion
- **Explosions** : Propagation en croix avec effets visuels
- **Power-ups** : Pulsation et effets de brillance

#### Interface Dynamique
- **Notifications** : Système d'alertes empilées
- **Feedback visuel** : Réactions aux actions du joueur
- **Transitions** : Fondu entre les écrans
- **Mise en surbrillance** : Éléments interactifs clairement identifiés

### 🔊 **Audio Avancé**

#### Optimisations
- **Latence zéro** : Pool d'AudioClip préchargés
- **Gestion mémoire** : Libération automatique des ressources
- **Volume adaptatif** : Ajustement selon le contexte
- **Qualité** : Support des formats WAV haute qualité

#### Immersion
- **Audio spatial** : Effets positionnés selon l'action
- **Transitions musicales** : Changements fluides entre thèmes
- **Feedback audio** : Confirmation sonore de chaque action
- **Ambiance** : Musiques adaptées à chaque situation

## 🎯 Conclusion

**Super Bomberman - Édition Complète Ultime** représente une version moderne et complète du jeu classique, offrant :

### ✨ **Expérience de Jeu Exceptionnelle**
- **6 modes de jeu** variés pour tous les goûts
- **Multijoueur local** jusqu'à 4 joueurs simultanés
- **IA intelligente** pour des défis stimulants
- **Progression** avec système de profils et statistiques

### 🎨 **Qualité Technique**
- **Architecture robuste** avec patterns éprouvés
- **Performance optimisée** pour un gameplay fluide
- **Interface moderne** avec menus FXML intuitifs
- **Audio immersif** avec effets et musiques de qualité

### 🔧 **Facilité d'Utilisation**
- **Installation simple** avec Maven
- **Documentation complète** avec Javadoc
- **Configuration flexible** et personnalisation
- **Support multiplateforme** Windows/macOS/Linux

### 🚀 **Évolutivité**
- **Code modulaire** pour ajouts futurs
- **Système de thèmes** extensible
- **Architecture ouverte** pour nouvelles fonctionnalités
- **Base solide** pour développements ultérieurs

---

**🎮 Prêt à jouer ? Lancez le jeu et découvrez toutes ces fonctionnalités !**

**📚 Documentation API complète disponible via :** `./open-javadoc.sh` ou `target/site/apidocs/index.html`

**🔧 Développé avec Java 23 + JavaFX 17 + Maven** 