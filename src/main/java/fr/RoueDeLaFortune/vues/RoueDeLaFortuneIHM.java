package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.mecanique.Grade;
import fr.RoueDeLaFortune.mecanique.Jeu;
import fr.RoueDeLaFortune.mecanique.Joueur;
import fr.RoueDeLaFortune.services.LangueManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import java.util.*;

public class RoueDeLaFortuneIHM extends Application {
    private Jeu jeu;
    private Joueur joueur;
    private VueJeu vueJeu;
    private Stage primaryStage;
    private static MediaPlayer ambiancePlayer;
    private static boolean musiqueEnCours = true;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        choisirGrade();
    }

    public void choisirGrade() {
        final double[] solde = new double[1];

        List<Grade> grades = Arrays.asList(
                new Grade("Visiteur", 0),
                new Grade("Inscrit", 100),
                new Grade("VIP", 200)
        );

        // Création de la vue du jeu
        VueChoixGrade vueChoixGrade = new VueChoixGrade(grades);

        Scene scene = new Scene(vueChoixGrade, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


        // Configuration de la taille de la fenêtre
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        // Calcul de la taille de la fenêtre en fonction d'un pourcentage de la taille de l'écran
        double width = screenBounds.getWidth(); // 95% de la largeur de l'écran
        double height = screenBounds.getHeight(); // 95% de la hauteur de l'écran

        primaryStage.setWidth(width); // largeur
        primaryStage.setHeight(height); // hauteur


        // Configuration de la scène et affichage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Choix du grade");
        primaryStage.centerOnScreen();
        primaryStage.show();

        vueChoixGrade.gradeChoisiProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue.getNom()) {
                    case "Visiteur" -> solde[0] = 100.0;
                    case "Inscrit" -> solde[0] = 300.0;
                    case "VIP" -> solde[0] = 500.0;
                    default -> solde[0] = 100.0;
                }
                Platform.runLater(() -> debuterJeu(newValue, solde[0]));
            }
        });
    }

    public void debuterJeu(Grade grade, double solde) {
        playMusique();

        joueur = new Joueur(solde, grade);
        jeu = new Jeu(joueur);

        // Création de la vue du jeu
        vueJeu = new VueJeu(jeu);

        Scene scene = new Scene(vueJeu);
        String cssFile = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(cssFile);

        // Configuration de la scène et affichage
        primaryStage.setScene(scene);
        primaryStage.setTitle("La Roue de la Fortune");
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> arreterJeu());
        primaryStage.show();

        Scene s = primaryStage.getScene();

        if (s != null) {
            s.widthProperty().addListener((observable, oldValue, newValue) -> {
                double newWidthGauche = newValue.doubleValue()*0.3;
                double newWidthDroite = newValue.doubleValue()*0.7;
                vueJeu.getGauche().setPrefWidth(newWidthGauche);
                vueJeu.getDroite().setPrefWidth(newWidthDroite);
                updateLayout();
                System.out.println("largeur : " + newValue);
            });

            s.heightProperty().addListener((observable, oldValue, newValue) -> {
                double newHeight = newValue.doubleValue() * 0.95;
                vueJeu.getDroite().setPrefHeight(newHeight);
                vueJeu.getGauche().setPrefHeight(newHeight);
                updateLayout();
                System.out.println("hauteur : " + newValue);
            });
        }

        Windows();
        double hauteur = s.getHeight();
        double largeur = s.getWidth();
        vueJeu.tailleGauche(hauteur, largeur);
        vueJeu.tailleRoue(hauteur, largeur);

        jeu.partieTerminee().addListener((observable, oldValue, newValue) -> {//quand on appuie sur la flèche retour
            if (newValue) {
                Platform.runLater(this::confirmationQuitter);
            }
        });

        setupCloseConfirmation();

        jeu.getTourTermineProperty().addListener((obs, oldVal, newVal) -> {//quand le solde arrive a 0
            if (newVal) {
                if (joueur.getSolde().get() <= 0) {
                    demanderContinuerOuQuitter();
                }
            }
        });

    }

    public void updateLayout(){
        vueJeu.mettreAjour();
    }

    private void setupCloseConfirmation() { // Quand on clique sur la croix rouge
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(resourceBundle.getString("closeConfirmationTitle"));
            alert.setHeaderText(resourceBundle.getString("closeConfirmationHeader"));
            alert.setContentText(resourceBundle.getString("closeConfirmationContent"));

            ButtonType buttonTypeOK = new ButtonType(resourceBundle.getString("okButton"));
            ButtonType buttonTypeCancel = new ButtonType(resourceBundle.getString("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeOK) {
                    primaryStage.close();
                }
            });
        });
    }

    public void demanderContinuerOuQuitter() { // Quand plus de jetons
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(resourceBundle.getString("gameOverTitle"));
            alert.setHeaderText(resourceBundle.getString("balanceDepletedHeader"));
            alert.setContentText(resourceBundle.getString("restartGamePrompt"));

            ButtonType btnRelancer = new ButtonType(resourceBundle.getString("restartButton"));
            ButtonType btnQuitter = new ButtonType(resourceBundle.getString("quitButton"), ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(btnRelancer, btnQuitter);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == btnRelancer) {
                choisirGrade();
            } else {
                arreterJeu();
            }
        });
    }

    public void confirmationQuitter() { // Quand on clique sur la fleche pour finir la partie
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("quitConfirmationTitle"));
        alert.setContentText(resourceBundle.getString("quitConfirmationPrompt"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            arreterJeu();
        }else {
            debuterJeu(jeu.getJoueur().getGrade(), jeu.getJoueur().getSolde().get());
        }
    }

    public static boolean estMusiqueEnCours() {
        return musiqueEnCours;
    }

    public static void setMusiqueEnCours(boolean enCours) {
        musiqueEnCours = enCours;
    }

    public static void playMusique() {
        if (ambiancePlayer == null) {
            String ambianceFilePath = "src/main/resources/son/ambiance.mp3";
            Media ambianceMedia = new Media(new File(ambianceFilePath).toURI().toString());
            ambiancePlayer = new MediaPlayer(ambianceMedia);
            ambiancePlayer.setVolume(0.3);
            ambiancePlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        ambiancePlayer.play();
    }

    public static void stopMusique() {
        if (ambiancePlayer != null) {
            ambiancePlayer.stop();
        }
    }


    public void Windows() {
        // Obtention des dimensions de l'écran
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calcul de la taille de la fenêtre en fonction d'un pourcentage de la taille de l'écran
        double widthmin = screenBounds.getWidth() * 0.40; // 95% de la largeur de l'écran
        double heightmin = screenBounds.getHeight() * 0.60; // 95% de la hauteur de l'écran

        // Configuration de la taille de la fenêtre
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);


        // Définir la taille minimale de la fenêtre
        primaryStage.setMinWidth(widthmin);
        primaryStage.setMinHeight(heightmin);
    }

    public void arreterJeu() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }


}