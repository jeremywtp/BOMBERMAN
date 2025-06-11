module bomberman.bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // ✨ **CORRIGÉ** : Suppression des dépendances non utilisées qui causaient
    // des erreurs de "build failed" dans l'IDE
    // requires javafx.web;
    // requires org.controlsfx.controls;
    // requires com.dlsc.formsfx;
    // requires net.synedra.validatorfx;
    // requires org.kordamp.ikonli.javafx;
    // requires org.kordamp.bootstrapfx.core;
    // requires eu.hansolo.tilesfx;
    // requires com.almasb.fxgl.all;

    opens bomberman.bomberman to javafx.fxml;
    exports bomberman.bomberman;
}