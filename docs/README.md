# 📚 Documentation Super Bomberman

Ce répertoire contient toute la documentation technique du projet Super Bomberman.

## 📖 Javadoc API

La documentation complète de l'API est disponible dans le répertoire `javadoc/` :

- **Point d'entrée** : [javadoc/index.html](javadoc/index.html)
- **39 classes documentées** avec descriptions complètes
- **Index de recherche** et navigation intuitive
- **Relations entre classes** et hiérarchies

### Accès Local

```bash
# Ouvrir la documentation dans le navigateur
open docs/javadoc/index.html        # macOS
xdg-open docs/javadoc/index.html    # Linux
start docs/javadoc/index.html       # Windows
```

### Régénération

Pour mettre à jour la Javadoc après modifications du code :

```bash
# Générer la nouvelle Javadoc
mvn clean compile javadoc:javadoc

# Copier vers docs/
cp -r target/site/apidocs/* docs/javadoc/

# Commiter les changements
git add docs/javadoc/
git commit -m "docs: mise à jour Javadoc"
```

## 📋 Structure

```
docs/
├── README.md           # Ce fichier
└── javadoc/           # Documentation API complète
    ├── index.html     # Page d'accueil Javadoc
    ├── allclasses-index.html
    ├── index-all.html
    └── bomberman.bomberman/  # Package principal
```

## 🔗 Liens Utiles

- **README principal** : [../README.md](../README.md)
- **Code source** : [../src/main/java/](../src/main/java/)
- **Ressources** : [../src/main/resources/](../src/main/resources/) 