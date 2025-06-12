# 🎮 Système de Profils Joueurs - Bomberman

## 📋 **Vue d'ensemble**

Le jeu Bomberman dispose maintenant d'un système complet de gestion des profils joueurs qui permet de :
- **Créer et gérer des profils** avec nom, prénom et avatar
- **Enregistrer automatiquement les statistiques** de jeu
- **Consulter les performances** de chaque joueur
- **Suivre la progression** au fil du temps

## 🚀 **Fonctionnalités**

### ✨ **Gestion des Profils**
- **Création de profils** : Nom, prénom, choix d'avatar
- **Modification de profils** : Édition des informations existantes
- **Suppression de profils** : Avec confirmation de sécurité
- **Sélection du joueur actuel** : Pour les parties

### 📊 **Statistiques Automatiques**
- **Parties jouées** : Compteur total des parties
- **Parties gagnées** : Nombre de victoires
- **Taux de victoire** : Pourcentage de réussite
- **Meilleur score** : Score le plus élevé atteint
- **Dates** : Création du profil et dernière partie

### 💾 **Sauvegarde Persistante**
- **Fichier de données** : `profiles/players.dat`
- **Sauvegarde automatique** : À chaque modification
- **Chargement au démarrage** : Récupération des données

## 🎯 **Utilisation**

### 1. **Accéder au Menu des Profils**
1. Lancer le jeu Bomberman
2. Dans le menu principal, naviguer vers **"PROFILS"**
3. Appuyer sur **ENTRÉE** ou cliquer sur le bouton

### 2. **Créer un Nouveau Profil**
1. Dans le menu des profils, cliquer sur **"CRÉER PROFIL"**
2. Remplir les champs :
   - **Prénom** : Votre prénom
   - **Nom** : Votre nom de famille
   - **Avatar** : Choisir parmi les sprites disponibles
3. Cliquer sur **"SAUVEGARDER"**

### 3. **Sélectionner un Profil**
1. Choisir un profil dans la liste
2. Cliquer sur **"SÉLECTIONNER"**
3. Le profil devient le **joueur actuel**

### 4. **Jouer et Enregistrer les Statistiques**
1. Sélectionner un profil avant de jouer
2. Lancer une partie en **mode "VS MACHINE"**
3. Les statistiques sont **automatiquement enregistrées** :
   - ✅ **Victoire** si vous battez l'IA
   - ❌ **Défaite** si l'IA vous bat
   - 📈 **Score** de la partie

## 🔧 **Architecture Technique**

### **Classes Principales**

#### `PlayerProfile`
- Modèle de données pour un profil joueur
- Méthodes pour calculer les statistiques
- Sérialisation pour la sauvegarde

#### `ProfileManager`
- Gestionnaire singleton des profils
- Sauvegarde/chargement des données
- Gestion du joueur actuel

#### `ProfileMenuController`
- Contrôleur FXML pour l'interface
- Gestion des interactions utilisateur
- Validation des données

#### `ProfileMenu.fxml`
- Interface utilisateur moderne
- Liste des profils avec statistiques
- Formulaires de création/modification

### **Intégration avec le Jeu**
- **Enregistrement automatique** dans `handleBattleWin()`
- **Mode VS Machine uniquement** pour les statistiques
- **Sauvegarde immédiate** après chaque partie

## 📁 **Structure des Fichiers**

```
BOMBERMAN/
├── profiles/                          # Répertoire des profils
│   └── players.dat                    # Fichier de données sérialisées
├── src/main/java/bomberman/bomberman/
│   ├── PlayerProfile.java             # Modèle de profil
│   ├── ProfileManager.java            # Gestionnaire des profils
│   └── ProfileMenuController.java     # Contrôleur de l'interface
├── src/main/resources/fxml/
│   └── ProfileMenu.fxml               # Interface utilisateur
└── PROFILS_README.md                  # Cette documentation
```

## 🎮 **Modes de Jeu Supportés**

### ✅ **Mode VS Machine**
- **Enregistrement automatique** des statistiques
- **Joueur humain vs IA**
- **Fin de partie immédiate** dès qu'un joueur meurt

### ❌ **Autres Modes**
- **Normal Game** : Pas d'enregistrement (mode solo)
- **Cooperation** : Pas d'enregistrement (mode coopératif)
- **Battle Mode** : Pas d'enregistrement (mode multijoueur)

## 📈 **Statistiques Affichées**

### **Profil Individuel**
- **Nom complet** : Prénom + Nom
- **Parties jouées** : Nombre total
- **Victoires** : Nombre de parties gagnées
- **Défaites** : Parties perdues (calculé)
- **Taux de victoire** : Pourcentage (%)
- **Meilleur score** : Score maximum atteint
- **Dates** : Création et dernière partie

### **Statistiques Globales**
- **Nombre total de profils**
- **Parties totales** de tous les joueurs
- **Victoires totales** de tous les joueurs
- **Meilleur score global**

## 🔒 **Sécurité et Validation**

### **Validation des Données**
- **Champs obligatoires** : Prénom et nom requis
- **Unicité** : Pas de doublons (même nom + prénom)
- **Longueur** : Validation des chaînes de caractères

### **Gestion des Erreurs**
- **Fichier corrompu** : Création d'une liste vide
- **Erreurs d'E/S** : Messages d'erreur informatifs
- **Profil inexistant** : Gestion gracieuse

## 🎨 **Interface Utilisateur**

### **Design Moderne**
- **Style cohérent** avec le reste du jeu
- **Navigation intuitive** avec clavier et souris
- **Feedback visuel** pour les actions

### **Fonctionnalités UX**
- **Aperçu d'avatar** en temps réel
- **Confirmation** pour les suppressions
- **Messages d'état** informatifs
- **Retour au menu** facile

## 🚀 **Évolutions Futures**

### **Fonctionnalités Possibles**
- 🏆 **Classements** et leaderboards
- 📊 **Graphiques** de progression
- 🎖️ **Achievements** et trophées
- 📸 **Avatars personnalisés**
- 🌐 **Synchronisation cloud**
- 📱 **Export des statistiques**

### **Améliorations Techniques**
- 🔐 **Chiffrement** des données
- 🗄️ **Base de données** SQLite
- 🔄 **Sauvegarde automatique** périodique
- 📝 **Logs** détaillés des parties

---

## 🎯 **Conclusion**

Le système de profils ajoute une dimension personnalisée et compétitive au jeu Bomberman, permettant aux joueurs de suivre leur progression et de s'améliorer au fil du temps. L'intégration transparente avec le mode "VS Machine" offre une expérience de jeu enrichie et motivante.

**Amusez-vous bien et que le meilleur joueur gagne ! 🎮💣** 