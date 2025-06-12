# 🎮 Sprites du Thème BOMBERMAN

Ce fichier documente tous les sprites utilisés dans le thème BOMBERMAN du jeu.

## 📁 Structure des Sprites

### 🧑‍🎮 Sprites Joueur (`/sprites/perso/`)
- **Sprites fixes (4 directions)** :
  - `bomberman_fixe_haut.png` - Bomberman immobile regardant vers le haut
  - `bomberman_fixe_bas.png` - Bomberman immobile regardant vers le bas
  - `bomberman_fixe_gauche.png` - Bomberman immobile regardant vers la gauche
  - `bomberman_fixe_droite.png` - Bomberman immobile regardant vers la droite

- **Sprites de marche (2 frames par direction)** :
  - `bomberman_marche_haut1.png` / `bomberman_marche_haut2.png` - Animation marche vers le haut
  - `bomberman_marche_bas1.png` / `bomberman_marche_bas2.png` - Animation marche vers le bas
  - `bomberman_marche_gauche1.png` / `bomberman_marche_gauche2.png` - Animation marche vers la gauche
  - `bomberman_marche_droite1.png` / `bomberman_marche_droite2.png` - Animation marche vers la droite

- **Sprites de mort (8 frames)** :
  - `Bomberman_dies_1.png` à `Bomberman_dies_8.png` - Animation de mort

- **Sprites de victoire (9 frames)** :
  - `bomberman_win_1.png` à `bomberman_win_9.png` - Animation de victoire

### 👾 Sprites Ennemis (`/sprites/ennemis/`)
- **Puropen (4 frames par direction)** :
  - `Puropen_haut_1.png` à `Puropen_haut_4.png` - Animation marche vers le haut
  - `Puropen_bas_1.png` à `Puropen_bas_4.png` - Animation marche vers le bas
  - `Puropen_gauche_1.png` à `Puropen_gauche_4.png` - Animation marche vers la gauche
  - `Puropen_droite_1.png` à `Puropen_droite_4.png` - Animation marche vers la droite

### 🌍 Sprites Environnement (`/sprites/`)
- **Terrain** :
  - `herbe_48x48.png` - Herbe de base
  - `herbe_with_ombre_bloc_non_destructible_48x48.png` - Herbe avec ombre de bloc solide
  - `herbe_with_ombre_bloc_destructible_48x48.png` - Herbe avec ombre de bloc destructible
  - `contours_map_816x624.png` - Contours de la carte

- **Blocs** :
  - `bloc_non_destructible_48x48.png` - Bloc solide indestructible
  - `bloc_destructible_v1_1.png` à `bloc_destructible_v1_4.png` - Animation bloc destructible version 1
  - `bloc_destructible_v2_1.png` à `bloc_destructible_v2_4.png` - Animation bloc destructible version 2

### 💣 Sprites Bombes (`/sprites/`)
- **Animation bombes (3 frames)** :
  - `bomb_1_48x48.png` - Frame 1 de l'animation bombe
  - `bomb_2_48x48.png` - Frame 2 de l'animation bombe
  - `bomb_3_48x48.png` - Frame 3 de l'animation bombe

### 💥 Sprites Explosions (`/sprites/`)
- **7 types d'explosion, 5 frames chacun** :
  - `resized_explosion_milieu_1.png` à `resized_explosion_milieu_5.png` - Centre d'explosion
  - `resized_explosion_droite_1.png` à `resized_explosion_droite_5.png` - Extrémité droite
  - `resized_explosion_gauche_1.png` à `resized_explosion_gauche_5.png` - Extrémité gauche
  - `resized_explosion_haut_1.png` à `resized_explosion_haut_5.png` - Extrémité haute
  - `resized_explosion_bas_1.png` à `resized_explosion_bas_5.png` - Extrémité basse
  - `resized_explosion_horizontale_1.png` à `resized_explosion_horizontale_5.png` - Segment horizontal
  - `resized_explosion_verticale_1.png` à `resized_explosion_verticale_5.png` - Segment vertical

### 🎁 Sprites Power-ups (`/sprites/`)
- **Extra Bomb (2 frames d'animation)** :
  - `bonus_extra_bomb_1.png` / `bonus_extra_bomb_2.png`

- **Explosion Expander (2 frames d'animation)** :
  - `bonus_explosion_expander_1.png` / `bonus_explosion_expander_2.png`

- **Accelerator (2 frames d'animation)** :
  - `bonus_accelerator_1.png` / `bonus_accelerator_2.png`

### 🚪 Sprites Porte (`/sprites/`)
- **Porte de sortie (2 frames d'animation)** :
  - `porte_1.png` / `porte_2.png`

### 🖼️ Images Interface (`/images/`)
- **Interface** :
  - `intro.png` - Image d'introduction du jeu
  - `icon.png` - Icône de l'application

## 📊 Statistiques
- **Total sprites joueur** : 30 fichiers (4 fixes + 8 marche + 8 mort + 9 victoire)
- **Total sprites ennemis** : 16 fichiers (4 directions × 4 frames)
- **Total sprites environnement** : 11 fichiers (terrain + blocs)
- **Total sprites bombes** : 3 fichiers
- **Total sprites explosions** : 35 fichiers (7 types × 5 frames)
- **Total sprites power-ups** : 6 fichiers (3 types × 2 frames)
- **Total sprites porte** : 2 fichiers
- **Total images interface** : 2 fichiers

**TOTAL GÉNÉRAL** : 105 fichiers graphiques

## 🔧 Utilisation
Ces sprites constituent le thème BOMBERMAN de base. Pour créer un nouveau thème :
1. Copier cette structure de dossiers
2. Remplacer les sprites par des versions adaptées au nouveau thème
3. Conserver les mêmes noms de fichiers et dimensions
4. Ajouter le nouveau thème dans l'énumération `Theme.java`
5. Implémenter la méthode de chargement correspondante dans `SpriteManager.java` 