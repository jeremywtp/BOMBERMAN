# Super Bomberman

## Description
Une version du jeu classique Bomberman développée en Java avec la bibliothèque JavaFX. Le jeu met en scène des mouvements fluides (pixel par pixel), un mode coopération à deux joueurs, des niveaux à difficulté progressive, des power-ups, et un système de high score persistant.

## Modes de Jeu
Le jeu propose deux modes accessibles depuis le menu principal :

*   **NORMAL GAME (1 Joueur)** : Le mode solo classique. Incarnez Bomberman, éliminez tous les ennemis pour révéler la porte de sortie et passez au niveau suivant.
*   **COOPERATION (2 Joueurs)** : Faites équipe avec un ami en local. Les deux joueurs partagent la même arène et doivent collaborer pour vaincre les ennemis. Pour finir un niveau, les deux joueurs doivent atteindre la porte de sortie une fois celle-ci activée.

## Comment Jouer

### Objectif
L'objectif de chaque niveau est de tuer tous les ennemis présents dans l'arène. Une fois tous les ennemis éliminés, la porte de sortie, cachée sous un bloc destructible, devient active. Le(s) joueur(s) doi(ven)t alors la rejoindre pour passer au niveau suivant.

Un timer de 2 minutes et 30 secondes est actif pour chaque niveau. S'il atteint zéro, le joueur perd une vie.

### Contrôles

| Action | Joueur 1 | Joueur 2 (Mode Coopération) |
| :--- | :--- | :--- |
| **Haut** | `Flèche Haut` | `Z` |
| **Bas** | `Flèche Bas` | `S` |
| **Gauche** | `Flèche Gauche` | `Q` |
| **Droite** | `Flèche Droite` | `D` |
| **Poser Bombe** | `Espace` | `Shift` |
| **Pause** | `Echap` | `Echap` |

## Fonctionnalités Clés

*   **Mouvement Fluide**: Les joueurs et les ennemis se déplacent au pixel près, offrant une expérience de jeu plus dynamique et précise que les déplacements par case. La détection des collisions entre le joueur et les ennemis est également gérée au pixel près.

*   **Bombes et Explosions**:
    *   Posez plusieurs bombes simultanément (nombre améliorable via power-ups).
    *   Les bombes posées deviennent des obstacles temporaires pour bloquer les ennemis ou vous protéger.
    *   Les explosions peuvent déclencher d'autres bombes, créant des **réactions en chaîne** dévastatrices.

*   **Power-Ups**: Améliorez les capacités de votre Bomberman en ramassant les power-ups cachés dans les blocs destructibles. En mode coopération, les bonus sont individuels.
    *   `EXTRA BOMB`: Augmente le nombre de bombes que vous pouvez poser.
    *   `EXPLOSION EXPANDER`: Augmente la portée des explosions.
    *   *Et d'autres à découvrir...*

*   **Niveaux Progressifs**: La difficulté augmente à chaque niveau, avec plus d'ennemis et des configurations de carte différentes.

*   **Système de Score**: Gagnez des points en détruisant des blocs (+10), en éliminant des ennemis (+100) ou en ramassant des power-ups (+50). Votre meilleur score est sauvegardé entre les parties.

*   **Porte de Sortie Interactive**: La porte est cachée sous un bloc. Une fois révélée, si une bombe explose dessus, un nouvel ennemi apparaît (dans la limite du nombre maximum d'ennemis par niveau) avec une invincibilité temporaire.

*   **Animations et Sons**:
    *   Des animations de début de niveau, de victoire et de mort des joueurs pour une meilleure immersion.
    *   Une bande-son et des effets sonores (musique, explosions, pas, etc.) qui rythment le jeu.

*   **Menu Pause Complet**: Mettez le jeu en pause à tout moment pour accéder aux options : reprendre, recommencer le niveau, voir les commandes ou retourner au menu principal.

## Architecture du Projet
Le projet est structuré autour de plusieurs classes clés pour séparer les responsabilités :

*   `Launcher.java`: Le cœur de l'application. Il gère la fenêtre JavaFX, la boucle de jeu principale (`AnimationTimer`), les états du jeu (menu, en jeu, pause, etc.), les entrées clavier et l'initialisation de tous les composants.
*   `Grid.java`: Le modèle de données de la grille de jeu. Il contient la logique des tuiles (murs solides, blocs destructibles) et l'emplacement des power-ups cachés.
*   `GridRenderer.java`: La classe responsable de tout le rendu graphique sur le `Canvas` JavaFX. Elle dessine la grille, les joueurs, les ennemis, les bombes, les explosions et toute l'interface utilisateur (scores, vies, timer, etc.).
*   `FluidMovementPlayer.java`: Gère la logique du joueur, y compris ses déplacements fluides, son état (vies, score, power-ups), et ses actions comme poser une bombe.
*   `FluidMovementEnemy.java`: Gère la logique des ennemis, y compris leur IA de déplacement fluide, leurs collisions et leur état.
*   `Bomb.java`: Représente une bombe posée par un joueur, avec son timer avant explosion.
*   `Explosion.java`: Gère la logique d'une explosion, y compris sa propagation, sa durée et les dégâts qu'elle inflige aux blocs, ennemis et joueurs.
*   `SoundManager.java`: Un gestionnaire centralisé pour charger et jouer la musique de fond et les effets sonores.

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
4. **Anti-chevauchement** ✨ **NOUVEAU** : 
   - **Prévention** : Aucun ennemi ne peut se déplacer sur une case déjà occupée par un autre ennemi vivant
   - **Méthode centralisée** : `isEnemyAt(x, y, excludeEnemy)` dans Launcher pour détecter les positions
   - **Interface fonctionnelle** : `EnemyCollisionChecker` pour découplage propre
   - **Exclusion intelligente** : L'ennemi qui se déplace ne se bloque pas lui-même
   - **Ennemis morts ignorés** : Seuls les ennemis vivants comptent comme obstacle
   - **Intégration IA** : Traité comme un obstacle normal, change de direction si bloqué
   - **Restriction** : S'applique uniquement aux ennemis (pas au joueur)
5. **Mort** : Tués par les explosions uniquement (+100 points)
6. **Interaction** : Contact avec le joueur = mort du joueur (si non protégé)
7. **Invincibilité temporaire** ✨ **NOUVEAU** : Les ennemis qui réapparaissent (via explosion sur porte) bénéficient de **5 secondes d'invincibilité** avec effet visuel de clignotement rouge (200ms), les rendant insensibles aux explosions pendant cette durée

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
enemy.update(grid, this::isBombBlockingMovement, this::isEnemyAt)
```

### États de Traversabilité
1. **canPlayerTraverse** : `true` → Le joueur peut encore passer

## 🧱 Détails Techniques : Anti-Chevauchement des Ennemis ✨ **NOUVEAU**

### Architecture du Système
```java
// Interface fonctionnelle pour découplage
@FunctionalInterface
public interface EnemyCollisionChecker {
    /**
     * Vérifie s'il y a un ennemi vivant à la position donnée (excluant l'ennemi qui demande)
     * @param x Position X à vérifier
     * @param y Position Y à vérifier  
     * @param excludeEnemy L'ennemi à exclure de la vérification (celui qui se déplace)
     * @return true si un autre ennemi vivant occupe cette position
     */
    boolean isEnemyAt(int x, int y, Enemy excludeEnemy);
}

// Utilisation dans Enemy.update()
enemy.update(grid, this::isBombBlockingMovement, this::isEnemyAt)

// Implémentation centralisée dans Launcher
private boolean isEnemyAt(int x, int y, Enemy excludeEnemy) {
    for (Enemy enemy : enemies) {
        if (enemy == excludeEnemy || !enemy.isAlive()) {
            continue; // Ignorer l'ennemi demandeur et les ennemis morts
        }
        if (enemy.getX() == x && enemy.getY() == y) {
            return true; // Position occupée
        }
    }
    return false; // Position libre
}
```

### Logique de Vérification
1. **Exclusion intelligente** : L'ennemi qui demande la vérification est ignoré
2. **Ennemis morts ignorés** : Seuls les ennemis vivants (`isAlive() == true`) comptent
3. **Vérification position exacte** : Comparaison directe des coordonnées (x,y)
4. **Intégration IA** : Traité comme obstacle normal, déclenche changement de direction
5. **Performance optimisée** : Parcours simple de la liste des ennemis, arrêt au premier trouvé
6. **Restriction ciblée** : Ne s'applique qu'aux ennemis (le joueur peut passer sur les ennemis)
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