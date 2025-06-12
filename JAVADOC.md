# 📚 Documentation API Javadoc - Super Bomberman

## 🎯 Vue d'ensemble

La documentation API Javadoc du projet Super Bomberman fournit une référence technique complète de toutes les classes, méthodes, interfaces et énumérations du jeu. Cette documentation est générée automatiquement à partir des commentaires dans le code source Java et constitue la référence officielle pour les développeurs.

## 🚀 Génération de la Javadoc

### Prérequis
- Java 23.0.2 ou supérieur
- Maven 3.x
- Projet compilé avec succès

### Commandes de Génération

#### Génération Standard
```bash
mvn clean compile javadoc:javadoc
```

#### Génération avec JAR
```bash
mvn javadoc:jar
```

### Scripts Automatiques

#### Linux/macOS
```bash
./open-javadoc.sh
```

#### Windows
```batch
open-javadoc.bat
```

Ces scripts vérifient automatiquement l'existence de la Javadoc, la génèrent si nécessaire, et l'ouvrent dans le navigateur par défaut.

## 📁 Structure de la Documentation

### Arborescence Complète
```
target/site/apidocs/
├── index.html                           # Page d'accueil principale
├── allclasses-index.html                # Index alphabétique de toutes les classes
├── allpackages-index.html               # Index de tous les packages
├── overview-tree.html                   # Arbre hiérarchique des classes
├── constant-values.html                 # Valeurs des constantes
├── serialized-form.html                 # Formulaires de sérialisation
├── search.html                          # Page de recherche avancée
├── index-all.html                       # Index général complet
├── bomberman.bomberman/                 # Package principal
│   └── bomberman/bomberman/             # Sous-package des classes
│       ├── package-summary.html         # Résumé du package
│       ├── package-tree.html            # Arbre du package
│       ├── [Classe].html                # Documentation de chaque classe
│       └── [Interface].html             # Documentation de chaque interface
└── resource-files/                      # Ressources CSS/JS
```

## 🏗️ Classes et Interfaces Documentées

### Core Game Engine (Moteur de Jeu)

#### `Launcher`
- **Rôle** : Classe principale du jeu, point d'entrée
- **Responsabilités** : 
  - Boucle de jeu principale (AnimationTimer)
  - Gestion des états du jeu (GameState)
  - Coordination entre tous les composants
  - Gestion des entrées clavier
  - IA des ennemis et logique de combat
- **Méthodes clés** : start(), handle(), updateGame(), handleInput()

#### `Grid`
- **Rôle** : Modèle de données de la grille de jeu
- **Responsabilités** :
  - Structure de la grille 15×13
  - Gestion des types de tuiles (TileType)
  - Placement des power-ups cachés
  - Logique de collision avec la grille
- **Méthodes clés** : getTile(), setTile(), isWalkable(), generatePowerUps()

#### `GridRenderer`
- **Rôle** : Moteur de rendu graphique complet
- **Responsabilités** :
  - Rendu de la grille et des tuiles
  - Affichage des joueurs et ennemis
  - Rendu des bombes et explosions
  - Interface utilisateur (HUD, menus)
  - Gestion des thèmes visuels
- **Méthodes clés** : render(), drawGrid(), drawPlayers(), drawUI()

#### `GameState`
- **Rôle** : Énumération des états du jeu
- **Valeurs** : MENU, RUNNING, PAUSED, GAME_OVER, LEVEL_COMPLETE
- **Utilité** : Contrôle du flux de jeu et des transitions

### Joueurs et Personnages

#### `FluidMovementPlayer`
- **Rôle** : Logique complète des joueurs
- **Responsabilités** :
  - Mouvement fluide pixel par pixel
  - Gestion des power-ups et effets
  - Système de vies et respawn
  - Collision avec environnement et ennemis
  - Placement et gestion des bombes
- **Méthodes clés** : update(), move(), placeBomb(), takeDamage()
- **Classes internes** : PlayerCollisionChecker, BombCollisionChecker

#### `Player`
- **Rôle** : Classe de base pour les joueurs
- **Responsabilités** :
  - Propriétés de base (position, score, vies)
  - Interface commune pour tous les types de joueurs
- **Méthodes clés** : getX(), getY(), getScore(), getLives()

#### `FluidMovementEnemy`
- **Rôle** : Intelligence artificielle des ennemis
- **Responsabilités** :
  - Pathfinding intelligent avec A*
  - Évitement des bombes et explosions
  - Comportement adaptatif selon la situation
  - Collision avec joueurs et environnement
- **Méthodes clés** : updateAI(), findPath(), avoidDanger(), canEscapeFromPosition()

#### `Enemy`
- **Rôle** : Classe de base des ennemis
- **Responsabilités** :
  - Propriétés communes (position, direction, état)
  - Interface pour différents types d'ennemis
- **Énumérations** : Direction (UP, DOWN, LEFT, RIGHT)
- **Classes internes** : EnemyCollisionChecker, BombCollisionChecker

### Objets de Jeu

#### `Bomb`
- **Rôle** : Logique des bombes et explosions
- **Responsabilités** :
  - Timer d'explosion (2 secondes)
  - Animation de clignotement
  - Déclenchement des explosions
  - Gestion de la traversabilité
- **Méthodes clés** : update(), shouldExplode(), canPlayerTraverse()
- **Constantes** : EXPLOSION_DELAY, EXPLOSION_RANGE

#### `Explosion`
- **Rôle** : Gestion des explosions et dégâts
- **Responsabilités** :
  - Propagation en forme de croix
  - Destruction des blocs destructibles
  - Dégâts aux joueurs et ennemis
  - Déclenchement des réactions en chaîne
- **Méthodes clés** : update(), isActive(), getDamageCells()
- **Classes internes** : ExplosionCell

#### `PowerUp`
- **Rôle** : Power-ups et bonus collectibles
- **Responsabilités** :
  - Effets permanents et temporaires
  - Animation et rendu visuel
  - Application des bonus aux joueurs
- **Types** : EXTRA_BOMB, RANGE_UP, SPEED_UP, SHIELD, SPEED_BURST, BOMB_RAIN
- **Méthodes clés** : applyEffect(), isTemporary(), getDuration()

#### `ExitDoor`
- **Rôle** : Porte de sortie et progression de niveau
- **Responsabilités** :
  - Apparition après élimination des ennemis
  - Animation d'ouverture/fermeture
  - Détection de passage des joueurs
  - Respawn d'ennemis si touchée par explosion
- **Méthodes clés** : activate(), checkPlayerEntry(), onExplosionHit()

### Système de Profils

#### `PlayerProfile`
- **Rôle** : Modèle de données des profils joueurs
- **Responsabilités** :
  - Stockage des informations personnelles
  - Statistiques de jeu (parties, victoires, scores)
  - Calculs automatiques (taux de victoire)
  - Sérialisation pour sauvegarde
- **Méthodes clés** : recordGame(), getWinRate(), updateHighScore()
- **Implémente** : Serializable

#### `ProfileManager`
- **Rôle** : Gestionnaire singleton des profils
- **Responsabilités** :
  - Sauvegarde/chargement des profils
  - Gestion du joueur actuel
  - CRUD des profils (Create, Read, Update, Delete)
  - Statistiques globales
- **Méthodes clés** : saveProfiles(), loadProfiles(), setCurrentPlayer()
- **Pattern** : Singleton

#### `ProfileMenuController`
- **Rôle** : Contrôleur FXML pour l'interface des profils
- **Responsabilités** :
  - Gestion des interactions utilisateur
  - Validation des données de profil
  - Navigation entre les écrans
  - Mise à jour de l'affichage
- **Méthodes clés** : createProfile(), editProfile(), deleteProfile()
- **Interfaces** : ProfileMenuCallback

### Interface Utilisateur et Menus

#### `MainMenuController`
- **Rôle** : Contrôleur du menu principal
- **Responsabilités** :
  - Navigation entre les modes de jeu
  - Gestion des boutons et interactions
  - Transitions vers les sous-menus
- **Méthodes clés** : showNormalGame(), showCooperation(), showBattle()
- **Interfaces** : MenuNavigationCallback

#### `PauseMenuController`
- **Rôle** : Contrôleur du menu pause
- **Responsabilités** :
  - Suspension/reprise du jeu
  - Options de redémarrage
  - Affichage des commandes
  - Retour au menu principal
- **Méthodes clés** : resumeGame(), restartLevel(), showCommands()
- **Énumérations** : PauseAction

#### `ThemeMenuController`
- **Rôle** : Contrôleur de sélection des thèmes
- **Responsabilités** :
  - Sélection entre thèmes disponibles
  - Aperçu des thèmes
  - Application et sauvegarde du choix
- **Méthodes clés** : selectTheme(), applyTheme(), previewTheme()

#### `FXMLMenuManager`
- **Rôle** : Gestionnaire centralisé des menus FXML
- **Responsabilités** :
  - Chargement des fichiers FXML
  - Gestion des transitions entre menus
  - Coordination des contrôleurs
- **Méthodes clés** : loadMenu(), showMenu(), switchToMenu()

### Utilitaires et Gestionnaires

#### `SoundManager`
- **Rôle** : Gestionnaire audio centralisé
- **Responsabilités** :
  - Chargement des fichiers audio
  - Lecture de la musique de fond
  - Gestion des effets sonores
  - Contrôle du volume
- **Méthodes clés** : playSound(), playMusic(), stopAll(), setVolume()
- **Pattern** : Singleton

#### `SpriteManager`
- **Rôle** : Gestionnaire des sprites et images
- **Responsabilités** :
  - Chargement des sprites par thème
  - Cache des images en mémoire
  - Gestion des animations
  - Support multi-thèmes
- **Méthodes clés** : loadSprite(), getSprite(), loadTheme()
- **Classes internes** : ThemeSprites

#### `ThemeSelector`
- **Rôle** : Sélecteur et applicateur de thèmes
- **Responsabilités** :
  - Détection des thèmes disponibles
  - Application des thèmes visuels
  - Sauvegarde des préférences
- **Méthodes clés** : applyTheme(), getAvailableThemes(), saveThemePreference()

### Animations et Effets Visuels

#### `BombermanAnimator`
- **Rôle** : Gestionnaire d'animations du joueur principal
- **Responsabilités** :
  - Animations de marche (4 directions)
  - Animations de mort (8 frames)
  - Animations de victoire (9 frames)
  - Gestion des états d'animation
- **Méthodes clés** : startWalkingAnimation(), startDeathAnimation(), startWinAnimation()
- **Énumérations** : AnimationState

#### `EnemyAnimator`
- **Rôle** : Gestionnaire d'animations des ennemis
- **Responsabilités** :
  - Animations de déplacement
  - Animations de mort
  - Synchronisation avec l'IA
- **Méthodes clés** : updateAnimation(), setDirection(), playDeathAnimation()

#### `ExplosionAnimator`
- **Rôle** : Gestionnaire d'animations des explosions
- **Responsabilités** :
  - Animations des flammes d'explosion
  - Gestion des segments d'explosion
  - Effets visuels temporisés
- **Méthodes clés** : startExplosion(), updateExplosion(), isExplosionActive()
- **Classes internes** : ExplosionSegment, ExplosionType

### Énumérations et Types

#### `TileType`
- **Valeurs** : EMPTY, SOLID_BLOCK, DESTRUCTIBLE_BLOCK, EXIT_DOOR
- **Utilité** : Classification des tuiles de la grille

#### `PowerUpType`
- **Valeurs** : EXTRA_BOMB, RANGE_UP, SPEED_UP, SHIELD, SPEED_BURST, BOMB_RAIN
- **Utilité** : Types de power-ups disponibles

#### `Theme`
- **Valeurs** : DEFAULT, POKEMON
- **Utilité** : Thèmes visuels disponibles

## 🔍 Navigation et Recherche

### Fonctionnalités de Navigation

#### Barre de Recherche
- **Emplacement** : En haut à droite de chaque page
- **Fonctionnalités** :
  - Recherche en temps réel
  - Filtrage automatique
  - Suggestions contextuelles
  - Recherche par classe, méthode, package

#### Index Alphabétique
- **All Classes** : Liste complète par ordre alphabétique
- **All Packages** : Vue organisée par packages
- **Constant Values** : Toutes les constantes du projet
- **Serialized Form** : Formulaires de sérialisation

#### Arbre Hiérarchique
- **Class Hierarchy** : Relations d'héritage
- **Interface Hierarchy** : Hiérarchie des interfaces
- **Package Tree** : Organisation par packages

### Liens et Références Croisées

#### Dans chaque classe
- **Direct Known Subclasses** : Classes filles
- **All Implemented Interfaces** : Interfaces implémentées
- **Enclosing Class** : Classe englobante (pour les classes internes)

#### Pour chaque méthode
- **Overrides** : Méthodes surchargées
- **Specified by** : Interface ou classe parente
- **See Also** : Références connexes

## 🛠️ Configuration Technique

### Plugin Maven Javadoc

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.6.3</version>
    <configuration>
        <source>24</source>
        <target>24</target>
        <encoding>UTF-8</encoding>
        <docencoding>UTF-8</docencoding>
        <charset>UTF-8</charset>
        <windowTitle>Super Bomberman - Documentation API</windowTitle>
        <doctitle>Super Bomberman - Documentation API</doctitle>
        <header>&lt;b&gt;Super Bomberman&lt;/b&gt;</header>
        <bottom>Copyright © 2024 Super Bomberman Project. Tous droits réservés.</bottom>
        <show>private</show>
        <nohelp>true</nohelp>
        <failOnError>false</failOnError>
        <additionalJOptions>
            <additionalJOption>-J-Duser.language=fr</additionalJOption>
            <additionalJOption>-J-Duser.country=FR</additionalJOption>
        </additionalJOptions>
    </configuration>
</plugin>
```

### Paramètres de Configuration

#### Encodage
- **Source** : UTF-8 pour support des caractères français
- **Documentation** : UTF-8 pour l'affichage correct
- **Charset** : UTF-8 pour la compatibilité web

#### Affichage
- **windowTitle** : Titre de la fenêtre du navigateur
- **doctitle** : Titre principal de la documentation
- **header/footer** : En-têtes et pieds de page personnalisés

#### Niveau de Documentation
- **show=private** : Inclut les méthodes et champs privés
- **failOnError=false** : Continue malgré les avertissements
- **nohelp=true** : Supprime le lien d'aide par défaut

## 📊 Statistiques de Documentation

### Couverture Complète
- **39 classes** documentées intégralement
- **Toutes les méthodes publiques** avec descriptions
- **Méthodes privées** incluses pour référence complète
- **Interfaces et énumérations** entièrement documentées

### Types de Documentation
- **Classes principales** : 25 classes core
- **Classes utilitaires** : 8 classes helper
- **Interfaces** : 6 interfaces définies
- **Énumérations** : 5 enums typées
- **Classes internes** : 12 classes imbriquées

### Éléments Documentés
- **Constructeurs** : Tous avec paramètres expliqués
- **Méthodes** : Signature, paramètres, retours, exceptions
- **Champs** : Variables d'instance et de classe
- **Constantes** : Valeurs et utilisation
- **Relations** : Héritage, implémentation, composition

## 🌐 Accès et Utilisation

### Accès Local

#### Scripts Automatiques
```bash
# Linux/macOS
./open-javadoc.sh

# Windows
open-javadoc.bat
```

#### Méthode Directe
```bash
# Ouvrir directement dans le navigateur
firefox target/site/apidocs/index.html
chrome target/site/apidocs/index.html
```

### Navigation Recommandée

#### Pour les Nouveaux Développeurs
1. **index.html** : Vue d'ensemble du projet
2. **package-summary.html** : Classes principales
3. **Launcher.html** : Point d'entrée du jeu
4. **GridRenderer.html** : Système de rendu

#### Pour la Maintenance
1. **allclasses-index.html** : Recherche rapide
2. **overview-tree.html** : Relations entre classes
3. **Class Use** : Impact des modifications
4. **Constant Values** : Valeurs configurables

#### Pour l'Extension
1. **Interface documentation** : Points d'extension
2. **Abstract classes** : Classes à étendre
3. **Package organization** : Structure modulaire
4. **Design patterns** : Patterns utilisés

## 🔧 Maintenance de la Documentation

### Génération Automatique

#### Intégration Continue
```bash
# Dans le pipeline CI/CD
mvn clean compile javadoc:javadoc
```

#### Hooks Git
```bash
# Pre-commit hook pour vérifier la documentation
#!/bin/bash
mvn javadoc:javadoc -q
if [ $? -ne 0 ]; then
    echo "Erreur dans la génération Javadoc"
    exit 1
fi
```

### Bonnes Pratiques

#### Commentaires de Code
- **@param** : Description claire de chaque paramètre
- **@return** : Explication de la valeur de retour
- **@throws** : Conditions d'exception
- **@see** : Références vers classes/méthodes liées
- **@since** : Version d'introduction
- **@deprecated** : Éléments obsolètes avec alternatives

#### Structure des Commentaires
```java
/**
 * Description courte de la méthode.
 * 
 * Description détaillée avec exemples d'utilisation
 * et cas particuliers à considérer.
 * 
 * @param parametre Description du paramètre
 * @return Description de la valeur retournée
 * @throws Exception Conditions d'exception
 * @see ClasseRelated#methodeRelated()
 * @since 1.0
 */
```

## 🎯 Conclusion

La documentation Javadoc du projet Super Bomberman constitue une référence technique complète et professionnelle, essentielle pour :

### **Développement**
- Compréhension rapide de l'architecture
- Intégration de nouvelles fonctionnalités
- Maintenance et débogage efficaces

### **Collaboration**
- Onboarding des nouveaux développeurs
- Standardisation des pratiques de code
- Communication technique claire

### **Qualité**
- Documentation synchronisée avec le code
- Couverture complète des APIs
- Navigation intuitive et recherche efficace

**La Javadoc est accessible via les scripts `./open-javadoc.sh` (Linux/macOS) ou `open-javadoc.bat` (Windows), ou directement à `target/site/apidocs/index.html`.**

---

**Documentation générée automatiquement avec Maven Javadoc Plugin 3.6.3**  
**Projet Super Bomberman - Édition Complète 2024**  
**Compatible Java 23+ avec JavaFX 17**
