package bomberman.bomberman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Gestionnaire de sélection et de sauvegarde des thèmes du jeu Bomberman.
 * Permet de changer de thème, sauvegarder la préférence et la charger au démarrage.
 */
public class ThemeSelector {
    
    // Fichier de sauvegarde du thème sélectionné
    private static final String THEME_FILE = "theme.txt";
    
    // Thème actuellement sélectionné
    private Theme currentTheme;
    
    /**
     * Constructeur du sélecteur de thèmes
     * Charge automatiquement le thème sauvegardé ou utilise le thème par défaut
     */
    public ThemeSelector() {
        loadTheme();
        // Synchroniser le SpriteManager avec le thème chargé
        SpriteManager.getInstance().setTheme(currentTheme);
    }
    
    /**
     * Charge le thème depuis le fichier de sauvegarde
     * Si le fichier n'existe pas ou est corrompu, utilise le thème CLASSIC par défaut
     */
    private void loadTheme() {
        try {
            if (Files.exists(Paths.get(THEME_FILE))) {
                String themeContent = Files.readString(Paths.get(THEME_FILE)).trim();
                currentTheme = Theme.valueOf(themeContent);
                System.out.println("Thème chargé : " + currentTheme.getDisplayName());
            } else {
                currentTheme = Theme.BOMBERMAN;
                System.out.println("Aucun fichier de thème trouvé, utilisation du thème par défaut : " + currentTheme.getDisplayName());
            }
        } catch (Exception e) {
            currentTheme = Theme.BOMBERMAN;
            System.out.println("Erreur lors du chargement du thème : " + e.getMessage() + ", utilisation du thème par défaut");
        }
    }
    
    /**
     * Sauvegarde le thème actuel dans le fichier
     */
    private void saveTheme() {
        try {
            Files.writeString(Paths.get(THEME_FILE), currentTheme.name());
            System.out.println("Thème sauvegardé : " + currentTheme.getDisplayName());
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde du thème : " + e.getMessage());
        }
    }
    
    /**
     * @return Le thème actuellement sélectionné
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Passe au thème suivant et sauvegarde la préférence
     */
    public void nextTheme() {
        currentTheme = currentTheme.getNext();
        saveTheme();
        // Synchroniser le SpriteManager avec le nouveau thème
        SpriteManager.getInstance().setTheme(currentTheme);
        System.out.println("Thème changé vers : " + currentTheme.getDisplayName());
    }
    
    /**
     * Passe au thème précédent et sauvegarde la préférence
     */
    public void previousTheme() {
        currentTheme = currentTheme.getPrevious();
        saveTheme();
        // Synchroniser le SpriteManager avec le nouveau thème
        SpriteManager.getInstance().setTheme(currentTheme);
        System.out.println("Thème changé vers : " + currentTheme.getDisplayName());
    }
    
    /**
     * Définit un thème spécifique et sauvegarde la préférence
     * @param theme Le thème à définir
     */
    public void setTheme(Theme theme) {
        if (theme != null && theme != currentTheme) {
            currentTheme = theme;
            saveTheme();
            // Synchroniser le SpriteManager avec le nouveau thème
            SpriteManager.getInstance().setTheme(currentTheme);
            System.out.println("Thème défini : " + currentTheme.getDisplayName());
        }
    }
    
    /**
     * @return Le nom d'affichage du thème actuel
     */
    public String getCurrentThemeDisplayName() {
        return currentTheme.getDisplayName();
    }
} 