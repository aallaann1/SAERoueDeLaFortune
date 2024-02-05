package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.mecanique.Couleur;
import fr.RoueDeLaFortune.mecanique.Jeu;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.animation.*;
import java.io.File;
import java.util.*;

public class VueRoue extends VBox {
    private Jeu jeu;
    private ImageView imageViewRoue;
    private ImageView imageViewContour;
    private Map<Couleur,List<Float>> mapCouleurAngle;
    private Integer nbTour;
    private float revenirBase;
    private StackPane stackPane;

    public VueRoue(Jeu jeu) {
        super();
        this.jeu = jeu;
        this.nbTour = 0;
        this.mapCouleurAngle = mapDesAngles();

        initialiserComposants();
        configurerEffets();
        ajouterComposants();
    }

    private void initialiserComposants() {
        imageViewRoue = new ImageView(new Image(getClass().getResourceAsStream("/img/roue.png")));
        imageViewContour = new ImageView(new Image(getClass().getResourceAsStream("/img/contourRoue.png")));
    }

    private void configurerEffets() {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(Color.rgb(252, 220, 18));
        neonEffect.setRadius(40);
        neonEffect.setSpread(0.5);

        imageViewRoue.setEffect(neonEffect);
        imageViewContour.setEffect(neonEffect);
    }

    private void ajouterComposants() {
        stackPane = new StackPane(imageViewRoue, imageViewContour);
        StackPane.setAlignment(imageViewContour, Pos.BOTTOM_CENTER);
        this.getChildren().add(stackPane);
        setPadding(new Insets(0, 0, 20, 0));
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Map<Couleur, List<Float>> mapDesAngles(){
        float indice = (float) 360 /52;
        Map<Couleur,List<Float>> notreMap = new HashMap<>();
        List<Float> angleBlanc = new ArrayList<>();
        List<Float> angleNoir = new ArrayList<>();
        List<Float> angleBleu = new ArrayList<>();
        List<Float> angleRouge = new ArrayList<>();
        List<Float> angleViolet = new ArrayList<>();
        List<Float> angleVert = new ArrayList<>();
        List<Float> angleOrange = new ArrayList<>();

        angleBlanc.add(indice*52);
        angleNoir.add((indice*26));
        angleRouge.addAll(Arrays.asList(indice*3,indice*18,indice*31,indice*43));
        angleViolet.addAll(Arrays.asList(indice*13,indice*39));
        angleBleu.addAll(Arrays.asList(indice*5,indice*9, indice*17,indice*19,indice*22,indice*28,indice*33,indice*35,indice*41,indice*48));
        angleVert.addAll(Arrays.asList(indice*2,indice*7,indice*11,indice*15,indice*20,indice*24,indice*29,indice*37,indice*43,indice*46,indice*50));
        angleOrange.addAll(Arrays.asList(indice,indice*4,indice*6,indice*8,indice*10,indice*12,indice*14,indice*16,indice*21,indice*23,indice*25,indice*27,indice*30,indice*32,indice*34,indice*36,indice*38,indice*40,indice*42,indice*45,indice*47,indice*49,indice*51));

        notreMap.put(Couleur.BLANC,angleBlanc);
        notreMap.put(Couleur.NOIR,angleNoir);
        notreMap.put(Couleur.VIOLET, angleViolet);
        notreMap.put(Couleur.ROUGE,angleRouge);
        notreMap.put(Couleur.BLEU,angleBleu);
        notreMap.put(Couleur.VERT,angleVert);
        notreMap.put(Couleur.ORANGE,angleOrange);

        return notreMap;
    }

    public void tournerRoue(){
        jeu.demarrerTour();
        Couleur couleur = jeu.getCouleurGagnante();
        List<Float> possibilite = mapCouleurAngle.get(couleur);
        //RotateTransition revenirTransition = new RotateTransition(Duration.seconds(0.1), imageViewRoue);
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(9),imageViewRoue);
        rotateTransition.setInterpolator(Interpolator.EASE_OUT);

        if (possibilite != null && !possibilite.isEmpty()) {

            Random random = new Random();
            int indexAleatoire = random.nextInt(possibilite.size());
            float angleChoisi = possibilite.get(indexAleatoire);
            float angleTour = 360*5 + angleChoisi;

            if (nbTour!=0){
                angleTour+=this.revenirBase;
            }

            rotateTransition.setByAngle(angleTour);

            this.revenirBase = 0;

            if (angleChoisi < 360) {
                this.revenirBase = 360 - angleChoisi;
            }

            rotateTransition.play();


            if ((couleur == Couleur.NOIR || couleur == Couleur.BLANC) && jeu.getJoueur().getCouleurMise().contains(couleur)) {
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), ev -> {
                    playJackpot();
                }));
                timeline.play();
            }

            rotateTransition.setOnFinished(event -> {
                if (jeu.getJoueur().getCouleurMise().contains(couleur)){
                    StageGagnant stageGagnant = new StageGagnant(couleur);
                    if (!(couleur == Couleur.NOIR && couleur == Couleur.BLANC)) {
                        playGagnant(); // Jouer le son gagnant si la couleur correspond
                    }
                } else {
                    playPerdant(); // Jouer le son perdant si la couleur ne correspond pas
                }
                jeu.terminerTour();
            });

            this.nbTour++;
        }
    }

    private void playJackpot() {
        String jetSoundFile = "src/main/resources/son/jackpot.mp3";
        AudioClip jetSound = new AudioClip(new File(jetSoundFile).toURI().toString());
        jetSound.play();
    }

    private void playPerdant() {
        String perdantSoundFile = "src/main/resources/son/DuolingoFail.mp3";
        AudioClip perdantSound = new AudioClip(new File(perdantSoundFile).toURI().toString());
        perdantSound.play();

        // Trouver l'instance de VueJeu dans la hiérarchie parentale
        Node parent = getParent();
        while (parent != null && !(parent instanceof VueJeu)) {
            parent = parent.getParent();
        }

        // Appliquer l'effet de tremblement sur le VueJeu trouvé
        if (parent instanceof VueJeu) {
            ((VueJeu) parent).appliquerEffetTremblement();
        }
    }

    private void playGagnant() {
        String gagnantSoundFile = "src/main/resources/son/victoire-no-copyright.mp3";
        AudioClip gagnantSound = new AudioClip(new File(gagnantSoundFile).toURI().toString());
        gagnantSound.play();
    }

}
