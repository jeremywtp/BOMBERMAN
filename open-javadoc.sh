#!/bin/bash

# Script pour ouvrir la Javadoc du projet Bomberman
# Usage: ./open-javadoc.sh

echo "🚀 Ouverture de la Javadoc Super Bomberman..."

# Chemin vers le fichier index.html de la Javadoc
JAVADOC_PATH="target/site/apidocs/index.html"

# Vérifier si la Javadoc existe
if [ ! -f "$JAVADOC_PATH" ]; then
    echo "❌ Javadoc non trouvée. Génération en cours..."
    mvn clean compile javadoc:javadoc
    
    if [ $? -ne 0 ]; then
        echo "❌ Erreur lors de la génération de la Javadoc"
        exit 1
    fi
fi

# Obtenir le chemin absolu
ABSOLUTE_PATH=$(realpath "$JAVADOC_PATH")

echo "📖 Ouverture de la documentation API..."
echo "📁 Chemin: file://$ABSOLUTE_PATH"

# Ouvrir dans le navigateur par défaut
if command -v xdg-open > /dev/null; then
    xdg-open "file://$ABSOLUTE_PATH"
elif command -v open > /dev/null; then
    open "file://$ABSOLUTE_PATH"
elif command -v start > /dev/null; then
    start "file://$ABSOLUTE_PATH"
else
    echo "🌐 Ouvrez manuellement ce lien dans votre navigateur:"
    echo "file://$ABSOLUTE_PATH"
fi

echo "✅ Documentation API ouverte avec succès !" 