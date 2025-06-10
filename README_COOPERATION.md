# 🎮 Mode Coopération Bomberman (2 Joueurs)

## 📋 Aperçu

Le **Mode Coopération** permet à deux joueurs de jouer ensemble simultanément sur le même écran dans votre jeu Bomberman Java. Ce mode duplique entièrement le gameplay du mode Normal tout en ajoutant un deuxième joueur contrôlable.

---

## 🚀 Fonctionnalités Implémentées

### ✅ **Menu Principal**
- Nouveau choix **"COOPERATION"** dans le menu principal
- Navigation avec les flèches et validation avec Entrée
- **NORMAL GAME** et **COOPERATION** activés

### ✅ **Deux Joueurs Simultanés**
- **Joueur 1** : Position de départ (1, 1) - coin supérieur gauche
- **Joueur 2** : Position de départ (13, 11) - coin inférieur droit
- Sprites identiques pour les deux joueurs
- Animations et effets visuels partagés

### ✅ **Contrôles Séparés**
| Joueur | Mouvement | Bombe |
|--------|-----------|--------|
| **Joueur 1** | Flèches ↑↓←→ | Espace |
| **Joueur 2** | Z S Q D | Alt |

### ✅ **Interface Utilisateur Adaptée**
- Affichage des scores des deux joueurs (P1 et P2)
- Informations détaillées pour chaque joueur :
  - Vie (VIE: 1/0)
  - Bombes actives (BOMBES: x/y)
  - Portée d'explosion (PORTÉE: x)
  - Bonus actifs (INVULNÉRABLE, VITESSE +)
- High Score centralisé

### ✅ **Gameplay Coopératif**
- **Power-ups partagés** : Les deux joueurs peuvent collecter des bonus
- **Collisions indépendantes** : Chaque joueur peut mourir séparément
- **Bombes personnelles** : Chaque joueur a ses propres bombes
- **Survie conditionnelle** : Si un joueur meurt, l'autre continue
- **Victoire partagée** : N'importe quel joueur peut terminer le niveau

---

## 🎯 Mécaniques de Jeu

### **Placement de Bombes**
- Chaque joueur a son propre compteur de bombes (1 max par défaut)
- Les bombes peuvent être traversées par leur poseur après placement
- Explosions affectent les deux joueurs et les ennemis

### **Collecte de Power-ups**
- **EXTRA_BOMB** : +1 bombe maximale pour le joueur qui la collecte
- **EXPLOSION_EXPANDER** : +1 portée d'explosion pour le joueur qui la collecte
- Notifications indiquent quel joueur a collecté quoi

### **Conditions de Mort**
- **Un joueur meurt** → L'autre continue seul
- **Les deux joueurs morts** → Game Over
- **Aucun respawn** en mode coopération (comme le mode normal)

### **Victoire**
- Un seul joueur doit atteindre la porte de sortie
- Les deux joueurs gagnent ensemble le niveau

---

## 🛠️ Implémentation Technique

### **Fichiers Modifiés**

#### **Launcher.java**
- Ajout du mode coopération (`isCooperationMode`)
- Gestion du `player2` (FluidMovementPlayer)
- Contrôles clavier étendus (ZQSD + Alt)
- Méthodes de bombes séparées (`tryPlaceBombPlayer1/2`)
- Collisions et mise à jour des deux joueurs
- Interface de rendu adaptée

#### **GridRenderer.java**
- Nouvelle méthode `renderCooperation()`
- Interface utilisateur spécialisée `renderUICooperation()`
- Affichage des informations des deux joueurs
- Rendu simultané des deux sprites de joueurs

#### **Système de Power-ups**
- Méthode `applyPowerUpToPlayer()` pour application ciblée
- Collecte par n'importe quel joueur
- Notifications avec identification du joueur

### **Positions de Départ**
```java
PLAYER_START_X = 1, PLAYER_START_Y = 1      // Joueur 1
PLAYER2_START_X = 13, PLAYER2_START_Y = 11  // Joueur 2
```

### **Validation Ennemis**
- Les ennemis ne peuvent pas apparaître près d'aucun des deux joueurs
- Zone de sécurité 3x3 autour de chaque joueur

---

## 🎮 Comment Jouer

### **Démarrage**
1. Lancer le jeu
2. Naviguer vers **"COOPERATION"** avec ↑/↓
3. Appuyer sur **Entrée** pour commencer

### **Contrôles**
```
🎮 JOUEUR 1        🎮 JOUEUR 2
↑ ↓ ← →  Mouvement   Z S Q D  Mouvement
Espace   Bombe       Alt      Bombe
```

### **Stratégies**
- **Coopération** : Coordonnez-vous pour éliminer les ennemis
- **Partage** : Les power-ups bénéficient individuellement
- **Protection** : Protégez-vous mutuellement des explosions
- **Efficacité** : Un seul joueur doit atteindre la sortie

---

## 🔧 Architecture

### **Structures de Données**
```java
private boolean isCooperationMode = false;
private FluidMovementPlayer player;   // Joueur 1
private FluidMovementPlayer player2;  // Joueur 2 (coopération uniquement)
```

### **Boucle de Jeu**
1. **Input** : Traitement des touches des deux joueurs
2. **Update** : Mise à jour des positions, collisions, power-ups
3. **Render** : Affichage des deux joueurs et interface adaptée

### **Gestion des États**
- Mode détecté au démarrage (`isCooperationMode`)
- Rendu conditionnel selon le mode
- Logique de mort et victoire adaptée

---

## 📊 Compatibilité

### ✅ **Fonctionnalités Supportées**
- Toutes les mécaniques du mode Normal
- Power-ups animés (EXTRA_BOMB, EXPLOSION_EXPANDER)
- Système de timer global
- Animations de porte
- Effets sonores
- Menu pause
- High Score

### ⚠️ **Limitations Actuelles**
- Pas de respawn individuel
- Identification des bombes par proximité (pas d'attribut owner)
- Interface utilisateur optimisée pour écran simple

---

## 🎯 Extensions Futures Possibles

1. **Respawn Individuel** : Permettre à un joueur de revenir après mort
2. **Sprites Différenciés** : Couleurs ou designs distincts par joueur
3. **Mode Compétitif** : Joueurs contre joueurs
4. **Power-ups Coopératifs** : Bonus qui affectent les deux joueurs
5. **Écran Scindé** : Visualisation séparée pour chaque joueur

---

## 🏆 Résultat Final

Le **Mode Coopération** offre une expérience de jeu **complète et fluide** permettant à deux joueurs de profiter ensemble du jeu Bomberman. Toutes les mécaniques sont fonctionnelles, l'interface est adaptée, et le gameplay conserve l'esprit du jeu original tout en ajoutant la dimension coopérative.

**Status : ✅ COMPLET ET OPÉRATIONNEL** 