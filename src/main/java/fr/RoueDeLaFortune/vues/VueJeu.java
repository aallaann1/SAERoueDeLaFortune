package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.ILangueUpdateable;
import fr.RoueDeLaFortune.mecanique.Bot;
import fr.RoueDeLaFortune.mecanique.Couleur;
import fr.RoueDeLaFortune.mecanique.Joueur;
import fr.RoueDeLaFortune.services.ChatService;
import fr.RoueDeLaFortune.services.LangueManager;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import fr.RoueDeLaFortune.mecanique.Jeu;
import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;
import fr.RoueDeLaFortune.services.LoggerService;
import javafx.util.Duration;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;

public class VueJeu extends HBox implements ILangueUpdateable {
    private Jeu jeu;
    private VueJoueur vueJoueur;
    private VueMise vueMise;
    private VueRegles vueRegles;
    private VueRoue vueRoue;
    private VueMenu vueMenu;
    private Button boutonLancerRoue;
    private Button boutonAfficherRegles;
    private Button boutonArreterPartie;
    private ToggleButton logButton;
    private ToggleButton chatButton;
    private StackPane contentArea;
    private StackPane gauche;
    private StackPane droite;
    private Label miseMinLabel;
    private Label miseMaxParCouleurLabel;
    private Button boutons;
    private VBox vBoxParamQuit;
    private VBox misePossible;
    private VBox roueMisePlay;
    private VBox vboxGauche;
    private VBox leChat;
    private HBox boutonOnglet;
    private List<Label> labelsCouleurs = new ArrayList<>();


    public VueJeu(Jeu jeu) {
        this.jeu = jeu;
        LangueManager.addLangueListener(this);

        initialiserVue();
        Image image = new Image("/img/lastBackground.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(
                image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

        this.setBackground(new Background(backgroundImage));
        updateLangue();
    }

    private void initialiserVue() {
        initialiserComposants();
        configurerGauche();
        configurerDroite();
        configurerBindings();
        configurerLayout();
    }

    private void initialiserComposants() {
        vueJoueur = new VueJoueur(jeu);
        vueRoue = new VueRoue(jeu);
        vueMise = new VueMise(jeu, vueJoueur);
        vueRegles = new VueRegles();
        vueMenu = new VueMenu();
        boutons = creerBoutonsAction();
        vBoxParamQuit = boutonParamQuit();
    }

    private void configurerGauche() {
        gauche = new StackPane();
        boutonOnglet = creerBoutonsOnglets();
        leChat = new VBox(boutonOnglet, contentArea);
        leChat.setAlignment(Pos.BOTTOM_LEFT);

        misePossible = afficherMisesPossibles();
        misePossible.setAlignment(Pos.CENTER_LEFT);

        vueJoueur.setAlignment(Pos.CENTER);

        vboxGauche = new VBox(5, vueJoueur, misePossible, leChat);

        gauche.getChildren().addAll(vboxGauche);
    }


    public void tailleGauche(double hauteur, double largeur){
        double size;
        if (hauteur < largeur) {
            size = hauteur;
        } else {
            size = largeur;
        }


        double valeurLargeur = size * 0.75;
        vueJoueur.setMinWidth(valeurLargeur);
        vueJoueur.setPrefWidth(valeurLargeur);
        vueJoueur.setMaxWidth(valeurLargeur);

        double valeurHauteur = valeurLargeur * 0.7584269662921348;
        vueJoueur.setMinHeight(valeurHauteur);
        vueJoueur.setPrefHeight(valeurHauteur);
        vueJoueur.setMaxHeight(valeurHauteur);

        double scale = valeurLargeur / 356;
        vueJoueur.setScaleX(scale);
        vueJoueur.setScaleY(scale);

        double padding = hauteur - misePossible.getHeight() - vueJoueur.getHeight() - 220;
        leChat.setPadding(new Insets(padding,0,5,5));
        VBox.setMargin(misePossible, new Insets(30, 0, 0, 0)); // Ajout espace entre vueJoueur et misePossible
        this.setPadding(new Insets(15, 0, 0, 0)); // Ajout espace au dessus du logo pour pas depasser
    }

    private void configurerDroite() {
        droite = new StackPane();
        roueMisePlay = new VBox();
        HBox hBox = new HBox();

        roueMisePlay.getChildren().addAll(vueRoue, vueMise, boutons);
        hBox.getChildren().addAll(roueMisePlay, vBoxParamQuit);
        droite.getChildren().addAll(hBox);

        roueMisePlay.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        vueRoue.setAlignment(Pos.CENTER);
    }

    public void tailleRoue(double hauteur, double largeur) {
        double size;
        if (hauteur < largeur) {
            size = hauteur;
        } else {
            size = largeur;
        }

        double setTaille = size * 0.6;
        vueRoue.setMinHeight(setTaille);
        vueRoue.setPrefHeight(setTaille);
        vueRoue.setMaxHeight(setTaille);

        vueRoue.setMinWidth(setTaille);
        vueRoue.setPrefWidth(setTaille);
        vueRoue.setMaxWidth(setTaille);


        double facteurTaille = setTaille / 532;
        double modif;
        if (facteurTaille >= 1) {
            modif = facteurTaille * 1.2;
        } else {
            modif = facteurTaille * 1;
        }

        vueRoue.setScaleX(modif);
        vueRoue.setScaleY(modif);

        double paddingRoueMisePlay = (largeur - setTaille - vBoxParamQuit.getWidth()) * 0.5;

        roueMisePlay.setPadding(new Insets(0,paddingRoueMisePlay,0,0));
    }

    public void mettreAjour() {


        droite.heightProperty().addListener((observable, oldValue, newValue) -> {
            vueMise.setPrefHeight(newValue.doubleValue() * 0.1);
            tailleRoue(newValue.doubleValue(), droite.getWidth());
        });

        droite.widthProperty().addListener((observable, oldValue, newValue) -> {
            vueMise.setPrefWidth(newValue.doubleValue() * 0.33);
            tailleRoue(droite.getHeight(), newValue.doubleValue());
        });

        gauche.heightProperty().addListener((observable, oldValue, newValue) -> {
            tailleGauche(newValue.doubleValue(), gauche.getWidth());
        });

        gauche.widthProperty().addListener((observable, oldValue, newValue) -> {
            tailleGauche(gauche.getHeight(), newValue.doubleValue());
        });

    }

    private void configurerBindings() {
        LoggerService.getLogTextArea().prefWidthProperty().bind(LoggerService.getLogTextArea().widthProperty().multiply(1));
    }

    private void configurerLayout() {
        getChildren().addAll(gauche, droite);
        VBox.setMargin(gauche, new Insets(20, 0, 20, 20));
        VBox.setMargin(droite, new Insets(20, 0, 20, 20));
    }


    public VBox boutonParamQuit() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();

        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(Color.rgb(252, 220, 18));
        neonEffect.setRadius(10);
        neonEffect.setSpread(0.2);

        boutonArreterPartie = new Button();
        configurerBouton(boutonArreterPartie, resourceBundle.getString("quit"), neonEffect);
        boutonArreterPartie.setOnAction(e -> jeu.terminerLaPartie());

        boutonAfficherRegles = new Button();
        configurerBouton(boutonAfficherRegles, resourceBundle.getString("rule"), neonEffect);
        boutonAfficherRegles.setOnAction(e -> vueMenu.afficherMenu());

        return new VBox(10, boutonArreterPartie, boutonAfficherRegles);
    }

    private void configurerBouton(Button bouton, String text, DropShadow effect) {
        Label label = new Label(text);
        label.setPrefHeight(50);
        label.setPrefWidth(60);
        label.setStyle("-fx-border-color: #FCDC12; -fx-border-width: 3.5; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
        label.setEffect(effect);

        bouton.setGraphic(label);
        bouton.setStyle("-fx-background-color: transparent;");
        bouton.setEffect(effect);
    }

    @Override
    public void updateLangue() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        logButton.setText(resourceBundle.getString("infoGame"));
        chatButton.setText(resourceBundle.getString("chat"));
        miseMinLabel.setText(resourceBundle.getString("minimumBet") + jeu.getMiseMinimalePropertyInt());
        miseMaxParCouleurLabel.setText(resourceBundle.getString("maximumBetPerColor"));

        Label labelDansBouton = (Label) boutonLancerRoue.getGraphic();
        labelDansBouton.setText(resourceBundle.getString("play"));

        ObservableList<Node> labelVBox = vBoxParamQuit.getChildren();
        Button boutonQuitter = (Button) labelVBox.get(0);
        Button boutonRegle = (Button) labelVBox.get(1);
        Label labelBoutonQuitter = (Label) boutonQuitter.getGraphic();
        Label labelBoutonRegle = (Label) boutonRegle.getGraphic();
        labelBoutonQuitter.setText(resourceBundle.getString("quit"));
        labelBoutonRegle.setText("Menu");

        ToggleButton misePossibleText = (ToggleButton) misePossible.getChildren().get(0);
        misePossibleText.setText(resourceBundle.getString("betMask"));

        for (Label labelCouleur : labelsCouleurs) {
            Couleur couleur = (Couleur) labelCouleur.getUserData();
            labelCouleur.setText(couleur.getNomTraduit(resourceBundle) + " : " + jeu.getMiseMaximalePourCouleurInt(couleur));
        }
    }

    public HBox creerBoutonsOnglets() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();

        logButton = new ToggleButton(resourceBundle.getString("infoGame"));
        chatButton = new ToggleButton(resourceBundle.getString("chat"));
        logButton.getStyleClass().add("toggle-button");
        chatButton.getStyleClass().add("toggle-button");

        ScrollPane logScrollPane = new ScrollPane(LoggerService.getLogTextArea());
        logScrollPane.setFitToWidth(true);
        logScrollPane.getStyleClass().add("scroll-pane");
        LoggerService.getLogTextArea().getStyleClass().add("log-text-area");

        VBox logScrollVbox = new VBox(logScrollPane);

        ScrollPane chatScrollPane = new ScrollPane(ChatService.getChatTextArea());
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.getStyleClass().add("scroll-pane");
        ChatService.getChatTextArea().getStyleClass().add("log-text-area");

        VBox vboxChat = new VBox(5, chatScrollPane, creerTextField());

        contentArea = new StackPane(logScrollVbox);

        logButton.setOnAction(e -> updateContentArea(logButton, chatButton, logScrollVbox));
        chatButton.setOnAction(e -> updateContentArea(chatButton, logButton, vboxChat));

        logButton.setSelected(true);

        return new HBox(logButton, chatButton);
    }

    public TextField creerTextField() {
        TextField textField = new TextField();
        textField.setStyle("-fx-padding: 0 0 20 0; -fx-border-radius: 50");
        textField.setOnAction(e -> handleEnterPressed(textField));
        return textField;
    }

    public void handleEnterPressed(TextField textField) {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        if (textField.getText().equals("/classement")) {
            List<Bot> listeBot = jeu.getListeBot();
            List<Joueur> classementJoueur = new ArrayList<>();

            classementJoueur.add(jeu.getJoueur());
            classementJoueur.addAll(listeBot);
            classementJoueur.sort(Comparator.comparingDouble(Joueur::getSoldeDouble));

            Collections.reverse(classementJoueur);
            ChatService.ajouterMessage(resourceBundle.getString("rankingHeader"), true);
            for (int i = 0; i < classementJoueur.size(); i++) {
                int classement = i + 1;
                Joueur joueur = classementJoueur.get(i);
                String message;
                if (joueur instanceof Bot) {
                    message = MessageFormat.format(resourceBundle.getString("botRanking"),
                            classement,
                            ((Bot) joueur).getPseudo(),
                            joueur.getSolde().get());
                } else {
                    message = MessageFormat.format(resourceBundle.getString("playerRanking"),
                            classement,
                            joueur.getSolde().get());
                }
                ChatService.ajouterMessage(message, false);
            }
        } else {
            String message = resourceBundle.getString("you") + " : " + textField.getText();
            ChatService.ajouterMessage(message, false);
        }
        textField.clear();
    }

    private void updateContentArea(ToggleButton selectedButton, ToggleButton otherButton, Node content) {
        if (!selectedButton.isSelected() || otherButton.isSelected()) {
            selectedButton.setSelected(true);
            otherButton.setSelected(false);
            contentArea.getChildren().setAll(content);
        }
    }

    public Button creerBoutonsAction() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();

        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(Color.rgb(252, 220, 18));
        neonEffect.setRadius(20);
        neonEffect.setSpread(0.2);

        boutonLancerRoue = new Button();
        Label play = new Label(resourceBundle.getString("play"));
        play.setPrefHeight(50);
        play.setPrefWidth(60);
        play.setStyle("-fx-border-color: #FCDC12; -fx-border-width: 3.5; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
        play.setEffect(neonEffect);

        boutonLancerRoue.setGraphic(play);
        boutonLancerRoue.setStyle("-fx-background-color: transparent;");
        boutonLancerRoue.setEffect(neonEffect);
        boutonLancerRoue.setAlignment(Pos.CENTER);
        boutonLancerRoue.disableProperty().bind(
                Bindings.isEmpty(jeu.getJoueur().getMise()).or(jeu.getTourTermineProperty().not())
        );
        boutonLancerRoue.setPadding(new Insets(0,0,-100,0));

        AudioClip audioClip = new AudioClip(new File("src/main/resources/son/spin2.mp3").toURI().toString());
        boutonLancerRoue.setOnAction(e -> {
            jeu.lancerRoue();
            vueRoue.tournerRoue();
            audioClip.play();
        });

        return boutonLancerRoue;
    }

    public VBox afficherMisesPossibles() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        VBox MisesLimitesContainer = new VBox(5);
        MisesLimitesContainer.setPadding(new Insets(0, 0, 20, 0));

        miseMinLabel = new Label(resourceBundle.getString("minimumBet") + jeu.getMiseMinimalePropertyInt());
        miseMinLabel.setStyle(createNeonEffect());
        MisesLimitesContainer.getChildren().add(miseMinLabel);

        miseMaxParCouleurLabel = new Label(resourceBundle.getString("maximumBetPerColor"));
        miseMaxParCouleurLabel.setStyle(createNeonEffect());
        MisesLimitesContainer.getChildren().add(miseMaxParCouleurLabel);

        HBox premiereLigneCouleurs = new HBox(10);
        HBox deuxiemeLigneCouleurs = new HBox(10);
        Couleur[] couleurs = Couleur.values();
        for (int i = 0; i < couleurs.length; i++) {
            Label labelCouleur = new Label();
            labelCouleur.setUserData(couleurs[i]);
            labelCouleur.setStyle(createNeonEffect());
            labelsCouleurs.add(labelCouleur);
            if (i < couleurs.length / 2) {
                premiereLigneCouleurs.getChildren().add(labelCouleur);
            } else {
                deuxiemeLigneCouleurs.getChildren().add(labelCouleur);
            }
        }
        MisesLimitesContainer.getChildren().addAll(premiereLigneCouleurs, deuxiemeLigneCouleurs);

        ToggleButton boutonMises = new ToggleButton(resourceBundle.getString("betMask"));
        boutonMises.setOnAction(event -> {
            if (boutonMises.isSelected()) {

                MisesLimitesContainer.setVisible(true);
            } else {

                MisesLimitesContainer.setVisible(false);
            }
        });

        MisesLimitesContainer.setVisible(false);

        return new VBox(10, boutonMises, MisesLimitesContainer);
    }


    private String createNeonEffect() {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(Color.rgb(252, 220, 18));
        neonEffect.setRadius(20);
        neonEffect.setSpread(0.4);

        return
                "-fx-background-color: #FFFFFF; " +
                        "-fx-border-color: #001D3D; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: BLACK; " +
                        "-fx-effect: " +
                        "dropshadow(" +
                        "gaussian, rgba(252, 220, 18, 1.0), " +
                        neonEffect.getRadius() + ", " +
                        neonEffect.getSpread() + ", " +
                        neonEffect.getOffsetX() + ", " +
                        neonEffect.getOffsetY() +
                        ")";
    }

    public void appliquerEffetFlou() {
        BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(5);
        boxBlur.setHeight(5);
        boxBlur.setIterations(3);
        this.setEffect(boxBlur);
    }

    public void retirerEffetFlou() {
        this.setEffect(null);
    }

    public void appliquerEffetTremblement() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(70), this);

        translateTransition.setByX(5);
        translateTransition.setByY(5);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.setCycleCount(15);
        translateTransition.setAutoReverse(true);
        translateTransition.setOnFinished(event -> retirerEffetTremblement());

        translateTransition.playFromStart();
    }

    private void retirerEffetTremblement() {
        this.setTranslateX(0);
        this.setTranslateY(0);
    }


    public StackPane getGauche() {
        return gauche;
    }

    public StackPane getDroite() {
        return droite;
    }

}