@echo off
REM Script pour ouvrir la Javadoc du projet Bomberman
REM Usage: open-javadoc.bat

echo 🚀 Ouverture de la Javadoc Super Bomberman...

REM Chemin vers le fichier index.html de la Javadoc
set JAVADOC_PATH=target\site\apidocs\index.html

REM Vérifier si la Javadoc existe
if not exist "%JAVADOC_PATH%" (
    echo ❌ Javadoc non trouvée. Génération en cours...
    mvn clean compile javadoc:javadoc
    
    if errorlevel 1 (
        echo ❌ Erreur lors de la génération de la Javadoc
        pause
        exit /b 1
    )
)

REM Obtenir le chemin absolu
for %%i in ("%JAVADOC_PATH%") do set ABSOLUTE_PATH=%%~fi

echo 📖 Ouverture de la documentation API...
echo 📁 Chemin: file:///%ABSOLUTE_PATH%

REM Ouvrir dans le navigateur par défaut
start "" "file:///%ABSOLUTE_PATH%"

echo ✅ Documentation API ouverte avec succès !
pause 