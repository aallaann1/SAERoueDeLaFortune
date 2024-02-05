package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.services.LangueManager;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.Locale;

public class VueRegles {

    public void afficherRegles() {
        Stage stage = new Stage();
        stage.setTitle("Règles du jeu");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);

        String cheminImage = LangueManager.getLocale().equals(Locale.ENGLISH) ? "/img/reglesEn.png" : "/img/reglesFr.png";
        Image imageRegles = new Image(getClass().getResourceAsStream(cheminImage));

        ImageView imageViewRegles = new ImageView(imageRegles);
        imageViewRegles.setPreserveRatio(true);

        imageViewRegles.fitWidthProperty().bind(stage.widthProperty());

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> stage.close());

        VBox vbox = new VBox(10, imageViewRegles, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double initialWidth = Math.min(900, screenBounds.getWidth() * 0.6);
        double initialHeight = Math.min(900 * (imageRegles.getHeight() / imageRegles.getWidth()), screenBounds.getHeight() * 0.6);

        Scene scene = new Scene(vbox, initialWidth, initialHeight);
        stage.setScene(scene);

        stage.showAndWait();
    }
}
