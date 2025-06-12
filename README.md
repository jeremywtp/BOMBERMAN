# 🎮 Super Bomberman - Édition Complète

## 📋 Description

Une version complète et moderne du jeu classique Bomberman développée en Java avec JavaFX. Le jeu propose des mouvements fluides pixel par pixel, plusieurs modes de jeu, un système de profils joueurs, des thèmes visuels, une IA intelligente, et de nombreuses fonctionnalités avancées.

## 🎯 Modes de Jeu

Le jeu propose plusieurs modes accessibles depuis le menu principal :

### 🎮 **NORMAL GAME (1 Joueur)**
- Mode solo classique avec progression de niveaux
- Éliminez tous les ennemis pour révéler la porte de sortie
- Difficulté progressive avec plus d'ennemis à chaque niveau
- Système de vies et respawn

### 🤝 **COOPERATION (2 Joueurs)**
- Mode coopératif local à deux joueurs
- Collaboration pour vaincre les ennemis ensemble
- Les deux joueurs doivent atteindre la porte de sortie
- Power-ups individuels pour chaque joueur

### ⚔️ **BATTLE MODE (2-4 Joueurs)**
- Mode combat multijoueur local
- Éliminez les autres joueurs pour gagner
- Bombes et explosions affectent tous les joueurs
- Dernier joueur en vie remporte la victoire

### 🤖 **VS MACHINE**
- Mode duel contre une IA intelligente
- IA avec pathfinding avancé et stratégies d'évasion
- Statistiques automatiquement enregistrées dans les profils
- Fin de partie immédiate dès qu'un joueur meurt

### 🎨 **THEMES**
- Sélection de thèmes visuels
- Thème par défaut et thème Pokemon
- Sprites et graphismes personnalisés
- Sauvegarde automatique du thème choisi

### 👤 **PROFILS**
- Système complet de gestion des profils joueurs
- Statistiques détaillées et progression
- Avatars personnalisables
- Sauvegarde persistante des données

## 🎮 Comment Jouer

### Objectif
L'objectif de chaque niveau est de tuer tous les ennemis présents dans l'arène. Une fois tous les ennemis éliminés, la porte de sortie, cachée sous un bloc destructible, devient active. Le(s) joueur(s) doi(ven)t alors la rejoindre pour passer au niveau suivant.

Un timer de 2 minutes et 30 secondes est actif pour chaque niveau. S'il atteint zéro, le joueur perd une vie.

### 🎮 Contrôles

#### Menu Principal
| Action | Touche |
|--------|--------|
| **Naviguer** | `↑` / `↓` |
| **Sélectionner** | `ENTRÉE` |

#### Jeu Solo (Joueur 1)
| Action | Touche |
|--------|--------|
| **Haut** | `↑` |
| **Bas** | `↓` |
| **Gauche** | `←` |
| **Droite** | `→` |
| **Poser Bombe** | `ESPACE` |
| **Pause** | `ÉCHAP` |

#### Mode Coopération/Battle (Joueur 2)
| Action | Touche |
|--------|--------|
| **Haut** | `Z` |
| **Bas** | `S` |
| **Gauche** | `Q` |
| **Droite** | `D` |
| **Poser Bombe** | `SHIFT` |

#### Mode Battle 4 Joueurs
| Action | Joueur 3 | Joueur 4 |
|--------|----------|----------|
| **Haut** | `T` | `I` |
| **Bas** | `G` | `K` |
| **Gauche** | `F` | `J` |
| **Droite** | `H` | `L` |
| **Poser Bombe** | `R` | `U` |

#### Menu Pause
| Action | Touche |
|--------|--------|
| **Naviguer** | `↑` / `↓` |
| **Sélectionner** | `ENTRÉE` |
| **Reprendre** | `ÉCHAP` |

## 🚀 Fonctionnalités Principales

### ✨ **Mouvement Fluide**
- Déplacements pixel par pixel pour tous les personnages
- Détection de collision précise et réaliste
- Animations fluides et naturelles
- Système de vitesse avec power-ups

### 💣 **Système de Bombes Avancé**
- Bombes multiples simultanées (améliorable)
- Réactions en chaîne dévastatrices
- Bombes traversables au placement
- Système Bomb Rain avec bombes automatiques
- Timing précis et effets visuels

### 🎁 **Power-Ups Complets**

#### Power-Ups Permanents
- **EXTRA BOMB** (Cyan) : +1 bombe maximum
- **RANGE UP** (Orange) : +1 portée d'explosion
- **SPEED UP** (Vert) : +0.5 vitesse de déplacement

#### Power-Ups Temporaires
- **SHIELD** (Bleu) : Protection 10s avec effets visuels
- **SPEED BURST** (Jaune) : Vitesse maximale 5s
- **BOMB RAIN** (Rouge) : 5 bombes automatiques

### 🤖 **IA Intelligente**
- Pathfinding avancé avec vérification de sécurité
- Stratégies d'évasion sophistiquées
- Placement de bombes tactique
- Évitement des situations suicidaires
- Adaptation au comportement du joueur

### 👤 **Système de Profils Joueurs**

#### Gestion des Profils
- **Création** : Nom, prénom, choix d'avatar
- **Modification** : Édition des informations
- **Suppression** : Avec confirmation de sécurité
- **Sélection** : Joueur actuel pour les parties

#### Statistiques Automatiques
- **Parties jouées** : Compteur total
- **Parties gagnées** : Nombre de victoires
- **Taux de victoire** : Pourcentage de réussite
- **Meilleur score** : Score maximum atteint
- **Dates** : Création et dernière partie

#### Avatars Simplifiés
- **"Pokemon"** : Style Pokemon avec sprites colorés
- **"Bomberman"** : Style classique Bomberman
- **Aperçu visuel** : Sprite affiché en temps réel
- **Compatibilité** : Support des anciens profils

### 🎨 **Système de Thèmes**
- **Thème DEFAULT** : Style classique Bomberman
- **Thème POKEMON** : Sprites et couleurs Pokemon
- **Sauvegarde automatique** : Thème conservé entre les sessions
- **Interface cohérente** : Tous les éléments adaptés

### 🎵 **Audio et Effets**
- **Musique de fond** : Thèmes pour chaque écran
- **Effets sonores** : Explosions, pas, collecte
- **Feedback audio** : Navigation dans les menus
- **Gestion centralisée** : SoundManager optimisé

### 📊 **Interface Utilisateur Moderne**
- **Zone de jeu** : 720×528px optimisée
- **Interface** : 720×252px dédiée aux informations
- **ATH complet** : Niveau, score, vies, power-ups
- **Notifications** : Système d'alertes empilées
- **Menus FXML** : Interface moderne et responsive

## 🏗️ Architecture Technique

### Classes Principales

#### Core Game
- **`Launcher.java`** : Cœur de l'application et boucle de jeu
- **`Grid.java`** : Modèle de données de la grille
- **`GridRenderer.java`** : Rendu graphique complet
- **`GameState.java`** : États du jeu (menu, jeu, pause, etc.)

#### Joueurs et Ennemis
- **`FluidMovementPlayer.java`** : Logique des joueurs
- **`FluidMovementEnemy.java`** : IA des ennemis
- **`Enemy.java`** : Classe de base des ennemis

#### Objets de Jeu
- **`Bomb.java`** : Logique des bombes
- **`Explosion.java`** : Gestion des explosions
- **`PowerUp.java`** : Power-ups et effets
- **`ExitDoor.java`** : Porte de sortie

#### Système de Profils
- **`PlayerProfile.java`** : Modèle de données profil
- **`ProfileManager.java`** : Gestionnaire singleton
- **`ProfileMenuController.java`** : Contrôleur interface

#### Utilitaires
- **`SoundManager.java`** : Gestion audio centralisée
- **`ThemeSelector.java`** : Sélection de thèmes
- **`FXMLMenuManager.java`** : Gestion des menus FXML

### Structure des Fichiers

```
BOMBERMAN/
├── src/main/java/bomberman/bomberman/     # Code source Java
├── src/main/resources/
│   ├── fxml/                              # Interfaces FXML
│   ├── images/                            # Sprites et graphismes
│   │   ├── sprites/                       # Sprites de jeu
│   │   ├── backgrounds/                   # Arrière-plans
│   │   └── themes/                        # Thèmes visuels
│   └── sounds/                            # Fichiers audio
├── profiles/                              # Profils joueurs
│   └── players.dat                        # Données sérialisées
├── target/                                # Compilation Maven
├── highscore.txt                          # Meilleur score
├── theme.txt                              # Thème sélectionné
└── README.md                              # Cette documentation
```

## 🛠️ Installation et Exécution

### Prérequis
- **Java 23.0.2** ou supérieur
- **Maven 3.x** pour la compilation
- **JavaFX 17.0.6** (géré automatiquement par Maven)

### Compilation
```bash
mvn clean compile
```

### Exécution
```bash
mvn clean javafx:run
```

### Création d'un JAR exécutable
```bash
mvn clean package
```

## 🎮 Caractéristiques Détaillées

### Fenêtre et Affichage
- **Résolution** : 816×956 pixels (non redimensionnable)
- **Grille** : 15×13 cases (48 pixels par case)
- **Zone de jeu** : 720×624 pixels
- **Interface** : 720×332 pixels pour les informations

### Types de Blocs
- **Solides** : Bordures et alternance, indestructibles
- **Destructibles** : ~30% des cases, destructibles par explosions
- **Vides** : Traversables par tous les personnages

### Système de Vies
- **5 vies** par joueur avec affichage 5/5 → 0/5
- **Respawn** avec invincibilité temporaire
- **Zone de sécurité** 2×2 au spawn
- **Game Over** quand toutes les vies sont perdues

### Power-Ups Détaillés
- **Génération** : 20% des blocs destructibles
- **Révélation** : Apparition à la destruction du bloc
- **Collecte** : Automatique au passage (+50 points)
- **Effets visuels** : Auras et animations pour les temporaires

### Système de Score
- **Ennemis tués** : +100 points
- **Blocs détruits** : +10 points
- **Power-ups collectés** : +50 points
- **High Score** : Sauvegarde automatique
- **Conservation** : Score maintenu entre les niveaux

## 🎯 Mécaniques de Jeu Avancées

### Menu Pause Complet
- **Activation** : Touche ÉCHAP pendant le jeu
- **Effet** : Jeu complètement figé
- **Options** : Reprendre, Recommencer, Commandes, Menu principal
- **Interface** : Fond semi-transparent et navigation fluide

### Système de Timer
- **Durée** : 2 minutes 30 secondes par niveau
- **Affichage** : Temps restant en temps réel
- **Expiration** : Perte d'une vie si temps écoulé
- **Pause** : Timer suspendu pendant la pause

### Réactions en Chaîne
- **Propagation** : Les explosions déclenchent d'autres bombes
- **Timing** : Explosion immédiate des bombes touchées
- **Stratégie** : Possibilité de créer des combos dévastateurs

### Porte de Sortie Interactive
- **Cachée** : Sous un bloc destructible aléatoire
- **Révélation** : Apparition à la destruction du bloc
- **Protection** : Agit comme un mur solide contre les explosions
- **Respawn d'ennemis** : Si touchée par une explosion

## 📊 Système de Profils - Guide Complet

### Utilisation du Système

#### 1. Accès au Menu des Profils
1. Lancer le jeu Bomberman
2. Naviguer vers **"PROFILS"** dans le menu principal
3. Appuyer sur **ENTRÉE** pour accéder

#### 2. Création d'un Profil
1. Cliquer sur **"CRÉER PROFIL"**
2. Remplir les champs :
   - **Prénom** : Votre prénom
   - **Nom** : Votre nom de famille
   - **Avatar** : "Pokemon" ou "Bomberman"
3. Cliquer sur **"SAUVEGARDER"**

#### 3. Gestion des Profils
- **Sélectionner** : Choisir le joueur actuel
- **Modifier** : Éditer les informations
- **Supprimer** : Avec confirmation de sécurité

#### 4. Statistiques Automatiques
- **Mode VS Machine** : Enregistrement automatique
- **Victoires et défaites** : Toutes enregistrées
- **Scores** : Toujours sauvegardés
- **Calculs automatiques** : Taux de victoire, etc.

### Fonctionnalités Avancées

#### Validation et Sécurité
- **Champs obligatoires** : Prénom et nom requis
- **Unicité** : Pas de doublons autorisés
- **Gestion d'erreurs** : Messages informatifs
- **Sauvegarde sécurisée** : Fichiers protégés

#### Interface Intuitive
- **Aperçu d'avatar** : Sprite affiché en temps réel
- **Navigation fluide** : Clavier et souris
- **Feedback visuel** : Confirmations et alertes
- **Design cohérent** : Style uniforme avec le jeu

#### Compatibilité
- **Anciens profils** : Support des versions précédentes
- **Migration automatique** : Conversion des formats
- **Sauvegarde robuste** : Récupération en cas d'erreur

## 🎨 Système de Thèmes - Guide Complet

### Thèmes Disponibles

#### Thème DEFAULT
- **Style** : Bomberman classique
- **Couleurs** : Palette traditionnelle
- **Sprites** : Design original du jeu

#### Thème POKEMON
- **Style** : Univers Pokemon coloré
- **Sprites** : Personnages Pokemon
- **Effets** : Couleurs vives et contrastées

### Utilisation
1. Menu principal → **"THEMES"**
2. Sélectionner le thème désiré
3. Sauvegarde automatique du choix
4. Application immédiate dans le jeu

## 🤖 IA Avancée - Fonctionnalités

### Algorithmes Intelligents
- **Pathfinding sécurisé** : Vérification des voies d'évasion
- **Timing optimisé** : Calculs précis pour éviter les bombes
- **Gestion multi-bombes** : Prise en compte de toutes les menaces
- **Stratégies adaptatives** : Comportement selon la situation

### Sécurité Renforcée
- **Distance minimale** : Range + 1 cases garanties
- **Vérification temporelle** : 350ms par case + marge
- **Échappement garanti** : Pas de placement suicidaire
- **Détection de blocage** : Évitement des situations critiques

## 🧪 Tests et Validation

### Tests Recommandés

#### Système de Profils
1. **Créer un profil** avec avatar "Pokemon"
2. **Jouer en VS Machine** et perdre volontairement
3. **Vérifier** l'enregistrement de la défaite
4. **Rejouer et gagner** pour tester les victoires
5. **Modifier le profil** pour tester l'édition

#### IA et Gameplay
1. **Observer l'IA** en mode VS Machine
2. **Vérifier** qu'elle ne se suicide plus
3. **Tester** les réactions en chaîne
4. **Valider** les power-ups temporaires
5. **Confirmer** la sauvegarde des thèmes

#### Modes Multijoueurs
1. **Tester** le mode Coopération à 2 joueurs
2. **Valider** le mode Battle à 4 joueurs
3. **Vérifier** les contrôles de chaque joueur
4. **Confirmer** les collisions entre joueurs

## 🚀 Évolutions Futures

### Fonctionnalités Possibles
- 🏆 **Classements** et leaderboards globaux
- 📊 **Graphiques** de progression détaillés
- 🎖️ **Achievements** et système de trophées
- 📸 **Avatars personnalisés** uploadables
- 🌐 **Multijoueur en ligne** avec serveur
- 📱 **Export des statistiques** en CSV/JSON
- 🎮 **Modes de jeu** supplémentaires
- 🎨 **Éditeur de niveaux** intégré

### Améliorations Techniques
- 🔐 **Chiffrement** des données de profils
- 🗄️ **Base de données** SQLite pour les stats
- 🔄 **Sauvegarde cloud** automatique
- 📝 **Logs détaillés** des parties
- ⚡ **Optimisations** de performance
- 🎯 **IA encore plus intelligente**

## 🎯 Conclusion

Super Bomberman - Édition Complète est une version moderne et enrichie du jeu classique, offrant :

- **Gameplay fluide** avec mouvements pixel par pixel
- **Modes variés** pour tous les types de joueurs
- **Système de profils** complet avec statistiques
- **IA intelligente** pour des défis stimulants
- **Thèmes visuels** pour personnaliser l'expérience
- **Interface moderne** avec menus FXML
- **Audio immersif** avec musiques et effets

Le jeu combine parfaitement nostalgie et modernité, offrant une expérience de jeu riche et engageante pour les joueurs de tous niveaux.

**Amusez-vous bien et que le meilleur joueur gagne ! 🎮💣**

---

## 📞 Support et Contribution

Pour toute question, suggestion ou contribution au projet :
- Consultez le code source dans le dossier `src/`
- Vérifiez les logs en cas de problème
- Les profils sont sauvegardés dans `profiles/players.dat`
- Le thème sélectionné est dans `theme.txt`
- Le high score est dans `highscore.txt`

## 📚 Documentation API (Javadoc)

### 🚀 **Génération de la Javadoc**

Le projet inclut une documentation API complète générée automatiquement :

```bash
# Génération de la Javadoc
mvn clean compile javadoc:javadoc

# Scripts automatiques
./open-javadoc.sh        # Linux/macOS
open-javadoc.bat         # Windows
```

### 📁 **Accès à la Documentation**

- **Emplacement** : `target/site/apidocs/index.html`
- **39 classes documentées** avec toutes les méthodes publiques
- **Recherche intégrée** et navigation par packages
- **Relations entre classes** et hiérarchie complète

### 🏗️ **Classes Principales Documentées**

#### Core Game
- **`Launcher`** : Classe principale et boucle de jeu
- **`Grid`** : Modèle de données de la grille
- **`GridRenderer`** : Rendu graphique complet
- **`GameState`** : États du jeu

#### Joueurs et IA
- **`FluidMovementPlayer`** : Logique des joueurs
- **`FluidMovementEnemy`** : IA des ennemis
- **`BombermanAnimator`** : Animations du joueur

#### Système de Profils
- **`PlayerProfile`** : Modèle de données
- **`ProfileManager`** : Gestionnaire singleton
- **`ProfileMenuController`** : Interface FXML

#### Utilitaires
- **`SoundManager`** : Gestion audio
- **`SpriteManager`** : Gestion des sprites
- **`ThemeSelector`** : Système de thèmes

### 🔍 **Navigation Recommandée**

1. **index.html** : Vue d'ensemble du projet
2. **allclasses-index.html** : Liste alphabétique
3. **package-summary.html** : Classes par package
4. **Recherche** : Barre de recherche intégrée

---

**Version** : Édition Complète 2024  
**Développé avec** : Java 23 + JavaFX 17 + Maven  
**Compatibilité** : Windows, macOS, Linux 