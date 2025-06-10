# Mode Coopération Bomberman - Copie Exacte du Jeu

## 🎮 Approche Utilisée

Comme demandé, j'ai créé une **copie exacte** du jeu normal avec simplement l'ajout d'un deuxième joueur. Cette approche est beaucoup plus simple et propre que les modifications conditionnelles.

## 📂 Fichiers Créés

### Nouveaux Fichiers
- **`LauncherCooperation.java`** - Copie exacte de `Launcher.java` avec Player 2 ajouté
- **`GridRendererCooperation.java`** - Copie exacte de `GridRenderer.java` avec méthodes pour 2 joueurs

### Modifications dans les Fichiers Existants
- **`Launcher.java`** - Menu modifié pour lancer le mode coopération
  - "BATTLE MODE" → "COOPERATION" 
  - Option activée et fonctionnelle

## 🎯 Fonctionnalités du Mode Coopération

### Joueurs
- **Joueur 1** : Position (1,1) - Flèches + Espace
- **Joueur 2** : Position (13,11) - Z/Q/S/D + Alt

### Interface
- **Score Total** : Addition des scores des deux joueurs
- **Interface séparée** : Statistiques distinctes pour chaque joueur
  - Joueur 1 (Cyan) : Score, Bombes, Portée, Statut
  - Joueur 2 (Jaune) : Score, Bombes, Portée, Statut

### Gameplay
- **Coopération** : Les deux joueurs jouent ensemble contre les ennemis
- **Objectif commun** : N'importe lequel des joueurs peut atteindre la porte
- **Game Over** : Seulement si les DEUX joueurs meurent
- **Power-ups** : Collecte individuelle avec effets séparés

## 🚀 Comment Jouer

1. Lancer le jeu normal (`mvn exec:java`)
2. Naviguer vers "COOPERATION" dans le menu
3. Appuyer sur Entrée pour lancer le mode coopération
4. Une nouvelle fenêtre s'ouvre avec le mode 2 joueurs

## 🎮 Contrôles

### Joueur 1 (Bomberman Bleu)
- **Déplacement** : ↑ ↓ ← →
- **Bombe** : Espace

### Joueur 2 (Bomberman Jaune)  
- **Déplacement** : Z (haut), S (bas), Q (gauche), D (droite)
- **Bombe** : Alt

## ✨ Avantages de cette Approche

- **Simplicité** : Copie exacte du jeu existant
- **Maintenance** : Code séparé, pas d'interférence
- **Stabilité** : Le mode normal reste inchangé
- **Extensibilité** : Facile d'ajouter des fonctionnalités spécifiques au mode coop

## 🔧 Architecture

```
Launcher.java (Mode Normal)
├── GridRenderer.java
└── [Jeu normal inchangé]

LauncherCooperation.java (Mode Coopération)
├── GridRendererCooperation.java
└── [Même logique + Player 2]
```

Cette approche respecte parfaitement votre demande d'une "copie exacte du jeu" avec simplement l'ajout d'un deuxième joueur ! 