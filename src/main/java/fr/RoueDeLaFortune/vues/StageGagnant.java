package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.mecanique.Couleur;
import fr.RoueDeLaFortune.mecanique.Jeu;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;

import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

public class StageGagnant {

    private Couleur couleurGagnante;
    private String chemin;

    public StageGagnant(Couleur couleur){
        this.couleurGagnante=couleur;
        this.chemin = "/img/imageVictoire/";
        trouverChemin();
        affichageAnimation();
    }

    public void affichageAnimation(){
        //apeller uniquement si le joueur gagne

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
        pauseTransition.setOnFinished(pause -> {
            Image winningImage = new Image(chemin);
            ImageView imageView = new ImageView(winningImage);

            Stage animation = new Stage();
            Group group = new Group(imageView);
            Scene scene = new Scene(group);

            // Get the primary screen's dimensions
            Screen screen = Screen.getPrimary();
            double screenWidth = screen.getBounds().getWidth();
            double screenHeight = screen.getBounds().getHeight();



            scene.setFill(Color.rgb(0, 0, 0, 0.5));
            scene.setRoot(group);
            animation.setScene(scene);
            animation.initStyle(StageStyle.TRANSPARENT);
            animation.setWidth(screenWidth);
            animation.setHeight(screenHeight);

            Duration duration = Duration.seconds(4);
            double groupWidth = imageView.getBoundsInParent().getWidth();
            double groupHeight = imageView.getBoundsInParent().getHeight();
            group.setLayoutX((screenWidth - groupWidth) / 2);
            group.setLayoutY((screenHeight - groupHeight) / 2);

            animation.show();
            Timeline timeline = new Timeline(new KeyFrame(duration, event -> {
                animation.close();
            }));
            timeline.play();
        });
        pauseTransition.play();

    }

    public void trouverChemin(){
        switch (couleurGagnante) {
            case ORANGE -> chemin = chemin.concat("wins_1.png");
            case VERT -> chemin = chemin.concat("wins_3.png");
            case BLEU -> chemin = chemin.concat("wins_5.png");
            case ROUGE -> chemin = chemin.concat("wins_10.png");
            case VIOLET -> chemin = chemin.concat("wins_20.png");
            case BLANC, NOIR -> chemin = chemin.concat("wins_50.png");
        }
    }

}