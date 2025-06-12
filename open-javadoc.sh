#!/bin/bash

# Script pour ouvrir la documentation Javadoc dans le navigateur par défaut
# Fonctionne sur Linux, macOS et Windows (avec WSL)

JAVADOC_PATH="docs/javadoc/index.html"

# Vérifier que la Javadoc existe
if [ ! -f "$JAVADOC_PATH" ]; then
    echo "❌ Javadoc non trouvée à $JAVADOC_PATH"
    echo "🔧 Génération de la Javadoc..."
    
    # Générer la Javadoc
    mvn clean compile javadoc:javadoc
    
    # Copier vers docs/
    if [ -d "target/site/apidocs" ]; then
        mkdir -p docs
        cp -r target/site/apidocs docs/javadoc
        echo "✅ Javadoc copiée vers docs/javadoc/"
    else
        echo "❌ Erreur lors de la génération de la Javadoc"
        exit 1
    fi
fi

echo "🚀 Ouverture de la Javadoc..."

# Détecter l'OS et ouvrir avec la commande appropriée
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    xdg-open "$JAVADOC_PATH"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    open "$JAVADOC_PATH"
elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    # Windows
    start "$JAVADOC_PATH"
else
    echo "⚠️  OS non reconnu. Ouvrez manuellement : $JAVADOC_PATH"
    echo "📂 Chemin absolu : $(pwd)/$JAVADOC_PATH"
fi

echo "✅ Documentation Javadoc accessible à : $JAVADOC_PATH" 