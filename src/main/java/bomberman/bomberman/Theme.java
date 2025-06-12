package bomberman.bomberman;

/**
 * Énumération des thèmes visuels disponibles dans le jeu Bomberman.
 * Chaque thème définit un ensemble cohérent de couleurs, textures et musiques.
 */
public enum Theme {
    
    /**
     * Thème Bomberman - Style Bomberman original avec sprites authentiques
     * Couleurs vives et contrastées
     */
    BOMBERMAN("Bomberman", "#228B22", "#8B4513", "#FFD700"),
    
    /**
     * Thème Pokemon - Style Pokemon avec contours spéciaux
     * Couleurs rouge et bleu Pokemon
     */
    POKEMON("Pokemon", "#FF0000", "#0000FF", "#FFFF00"),
    
    /**
     * Thème classique - Style Bomberman original
     * Couleurs vives et contrastées
     */
    CLASSIC("Classique", "#228B22", "#8B4513", "#FFD700"),
    
    /**
     * Thème sombre - Style moderne et élégant
     * Couleurs sombres avec accents lumineux
     */
    DARK("Sombre", "#2F2F2F", "#1A1A1A", "#FF6B6B"),
    
    /**
     * Thème océan - Couleurs bleues et aquatiques
     * Ambiance marine et apaisante
     */
    OCEAN("Océan", "#4682B4", "#191970", "#00CED1"),
    
    /**
     * Thème forêt - Couleurs vertes et naturelles
     * Ambiance forestière et organique
     */
    FOREST("Forêt", "#228B22", "#006400", "#32CD32"),
    
    /**
     * Thème désert - Couleurs chaudes et sablonneuses
     * Ambiance aride et ensoleillée
     */
    DESERT("Désert", "#DEB887", "#8B4513", "#FFD700");
    
    private final String displayName;
    private final String primaryColor;
    private final String secondaryColor;
    private final String accentColor;
    
    /**
     * Constructeur d'un thème
     * @param displayName Nom affiché dans le menu
     * @param primaryColor Couleur principale (hex)
     * @param secondaryColor Couleur secondaire (hex)
     * @param accentColor Couleur d'accent (hex)
     */
    Theme(String displayName, String primaryColor, String secondaryColor, String accentColor) {
        this.displayName = displayName;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.accentColor = accentColor;
    }
    
    /**
     * @return Le nom d'affichage du thème
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @return La couleur principale du thème
     */
    public String getPrimaryColor() {
        return primaryColor;
    }
    
    /**
     * @return La couleur secondaire du thème
     */
    public String getSecondaryColor() {
        return secondaryColor;
    }
    
    /**
     * @return La couleur d'accent du thème
     */
    public String getAccentColor() {
        return accentColor;
    }
    
    /**
     * @return Le thème suivant dans la liste (cyclique)
     */
    public Theme getNext() {
        Theme[] themes = Theme.values();
        int currentIndex = this.ordinal();
        return themes[(currentIndex + 1) % themes.length];
    }
    
    /**
     * @return Le thème précédent dans la liste (cyclique)
     */
    public Theme getPrevious() {
        Theme[] themes = Theme.values();
        int currentIndex = this.ordinal();
        return themes[(currentIndex - 1 + themes.length) % themes.length];
    }
} 