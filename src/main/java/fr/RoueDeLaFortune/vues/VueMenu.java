package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.ILangueUpdateable;
import fr.RoueDeLaFortune.services.LangueManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.layout.VBox;

public class VueMenu implements ILangueUpdateable {
    private VueRegles vueRegles;
    private Label rulesLabel, languageToggleLabel, soundToggleLabel;
    private Button boutonAfficherRegles;

    public VueMenu() {
        LangueManager.addLangueListener(this);
        initialiserLabels();
        boutonAfficherRegles = creerBoutonAfficherRegles();
        boutonAfficherRegles.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
    }

    private void initialiserLabels() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        rulesLabel = new Label(resourceBundle.getString("rulesLabel"));
        languageToggleLabel = new Label(resourceBundle.getString("languageToggleLabel"));
        soundToggleLabel = new Label(resourceBundle.getString("soundToggleLabel"));
        setLabelStyles();
    }

    private void setLabelStyles() {
        String style = "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;";
        rulesLabel.setStyle(style);
        languageToggleLabel.setStyle(style);
        soundToggleLabel.setStyle(style);
    }

    public void afficherMenu() {
        vueRegles = new VueRegles();

        // Effet néon
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(javafx.scene.paint.Color.rgb(252, 220, 18));
        neonEffect.setRadius(20);
        neonEffect.setSpread(0.4);

        // Création d'une fenêtre modale
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Menu");

        Button langueToggleButton = creerBoutonLangue();
        Button sonToggleButton = creerBoutonSon();

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(
                rulesLabel, boutonAfficherRegles,
                languageToggleLabel, langueToggleButton,
                soundToggleLabel, sonToggleButton
        );
        vbox.setStyle("-fx-background-color: #483D8B;");

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.sizeToScene();
        stage.showAndWait();
    }

    private Button creerBoutonAfficherRegles() {
        Button bouton = new Button();
        ImageView imageView = new ImageView(new Image("img/rules.png"));
        imageView.setFitHeight(70);
        imageView.setFitWidth(70);
        bouton.setGraphic(imageView);

        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(javafx.scene.paint.Color.rgb(252, 220, 18));
        neonEffect.setRadius(20);
        neonEffect.setSpread(0.4);
        bouton.setEffect(neonEffect);

        bouton.setOnAction(e -> {
            vueRegles.afficherRegles();
        });

        return bouton;
    }

    public Button creerBoutonLangue() {
        ImageView frIcon = new ImageView(new Image("img/eng.png"));
        frIcon.setFitWidth(70);
        frIcon.setFitHeight(50);

        ImageView engIcon = new ImageView(new Image("img/fr.png"));
        engIcon.setFitWidth(70);
        engIcon.setFitHeight(50);

        Button langueToggleButton = new Button("", engIcon); // defaut = fr
        langueToggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-border: none;");

        // Définir l'icône en fonction de la langue actuelle
        if (LangueManager.getLocale().equals(Locale.FRENCH)) {
            langueToggleButton.setGraphic(engIcon);
        } else {
            langueToggleButton.setGraphic(frIcon);
        }

        langueToggleButton.setOnAction(e -> {
            if (LangueManager.getLocale().equals(Locale.FRENCH)) {
                LangueManager.setLocale(Locale.ENGLISH);
                langueToggleButton.setGraphic(frIcon); // Afficher l'icône français
            } else {
                LangueManager.setLocale(Locale.FRENCH);
                langueToggleButton.setGraphic(engIcon); // Afficher l'icône anglais
            }
        });
        return langueToggleButton;
    }

    public Button creerBoutonSon() {
        ImageView sonOnIcon = new ImageView(new Image("img/sonOff.png"));
        sonOnIcon.setFitWidth(60);
        sonOnIcon.setFitHeight(60);

        ImageView sonOffIcon = new ImageView(new Image("img/sonOn.png"));
        sonOffIcon.setFitWidth(60);
        sonOffIcon.setFitHeight(60);

        Button sonToggleButton = new Button("", sonOffIcon);
        sonToggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-border: none;");

        // Définir l'icône en fonction de l'état actuel de la musique
        if (RoueDeLaFortuneIHM.estMusiqueEnCours()) {
            sonToggleButton.setGraphic(sonOffIcon);
        } else {
            sonToggleButton.setGraphic(sonOnIcon);
        }

        sonToggleButton.setOnAction(e -> {
            if (RoueDeLaFortuneIHM.estMusiqueEnCours()) {
                RoueDeLaFortuneIHM.stopMusique();
                sonToggleButton.setGraphic(sonOnIcon);
            } else {
                RoueDeLaFortuneIHM.playMusique();
                sonToggleButton.setGraphic(sonOffIcon);
            }
            RoueDeLaFortuneIHM.setMusiqueEnCours(!RoueDeLaFortuneIHM.estMusiqueEnCours());
        });

        return sonToggleButton;
    }

    @Override
    public void updateLangue() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        rulesLabel.setText(resourceBundle.getString("rulesLabel"));
        languageToggleLabel.setText(resourceBundle.getString("languageToggleLabel"));
        soundToggleLabel.setText(resourceBundle.getString("soundToggleLabel"));
    }
}
