#!/bin/bash

# Script pour mettre à jour la Javadoc dans le dépôt Git
# Usage: ./update-javadoc.sh [message]

echo "🔧 Mise à jour de la Javadoc..."

# Vérifier que nous sommes dans un dépôt Git
if [ ! -d ".git" ]; then
    echo "❌ Erreur: Ce script doit être exécuté dans un dépôt Git"
    exit 1
fi

# Nettoyer et générer la nouvelle Javadoc
echo "📝 Génération de la nouvelle Javadoc..."
mvn clean compile javadoc:javadoc

# Vérifier que la génération a réussi
if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la génération de la Javadoc"
    exit 1
fi

# Vérifier que le répertoire target/site/apidocs existe
if [ ! -d "target/site/apidocs" ]; then
    echo "❌ Erreur: Répertoire target/site/apidocs non trouvé"
    exit 1
fi

# Créer le répertoire docs s'il n'existe pas
mkdir -p docs

# Supprimer l'ancienne Javadoc et copier la nouvelle
echo "📂 Mise à jour du répertoire docs/javadoc/..."
rm -rf docs/javadoc
cp -r target/site/apidocs docs/javadoc

# Vérifier que la copie a réussi
if [ ! -f "docs/javadoc/index.html" ]; then
    echo "❌ Erreur lors de la copie de la Javadoc"
    exit 1
fi

# Ajouter les fichiers au staging Git
echo "📋 Ajout des fichiers au staging Git..."
git add docs/javadoc/

# Vérifier s'il y a des changements
if git diff --staged --quiet; then
    echo "ℹ️  Aucun changement détecté dans la Javadoc"
    exit 0
fi

# Message de commit par défaut ou fourni en paramètre
COMMIT_MESSAGE="${1:-docs: mise à jour automatique de la Javadoc}"

# Créer le commit
echo "💾 Création du commit..."
git commit -m "$COMMIT_MESSAGE"

if [ $? -eq 0 ]; then
    echo "✅ Javadoc mise à jour avec succès !"
    echo "📖 Accessible à : docs/javadoc/index.html"
    echo "🚀 Pour pousser vers le dépôt distant : git push"
else
    echo "❌ Erreur lors de la création du commit"
    exit 1
fi 