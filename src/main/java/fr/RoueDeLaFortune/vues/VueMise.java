package fr.RoueDeLaFortune.vues;
import fr.RoueDeLaFortune.ILangueUpdateable;
import fr.RoueDeLaFortune.mecanique.Couleur;
import fr.RoueDeLaFortune.mecanique.Jeu;
import fr.RoueDeLaFortune.services.LangueManager;
import fr.RoueDeLaFortune.services.LoggerService;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class VueMise extends VBox implements ILangueUpdateable {
    private Jeu jeu;
    private VueJoueur vueJoueur;
    private final List<String> imagesBoutons = List.of(
            "/img/caseNoir.png",
            "/img/caseBlanc.png",
            "/img/caseBleu.png",
            "/img/caseRouge.png",
            "/img/caseViolet.png",
            "/img/caseOrange.png",
            "/img/caseVert.png"
    );

    public VueMise(Jeu jeu, VueJoueur vueJoueur) {
        super();
        this.jeu = jeu;
        this.vueJoueur = vueJoueur;
        LangueManager.addLangueListener(this);

        this.setAlignment(Pos.CENTER);
        this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        configurerFondEtBordure();
        creerEtAjouterBoutons();
    }

    private void configurerFondEtBordure() {
        Color backgroundColor = Color.web("#1b0430");
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(20), Insets.EMPTY);
        Background background = new Background(backgroundFill);

        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(Color.rgb(252, 220, 18));
        neonEffect.setRadius(40);
        neonEffect.setSpread(0.5);

        BorderStroke borderStroke = new BorderStroke(Color.YELLOW, BorderStrokeStyle.SOLID, new CornerRadii(10),
                new BorderWidths(10));
        Border border = new Border(borderStroke);

        this.setBackground(background);
        this.setBorder(border);
        this.setEffect(neonEffect);
    }

    private void creerEtAjouterBoutons() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.spacingProperty().bind(this.widthProperty().multiply(0.04));

        Button blackButton = creerBoutonAvecImage(imagesBoutons.get(0), Couleur.NOIR);
        Button blueButton = creerBoutonAvecImage(imagesBoutons.get(2), Couleur.BLEU);
        Button orangeButton = creerBoutonAvecImage(imagesBoutons.get(5), Couleur.ORANGE);
        Button whiteButton = creerBoutonAvecImage(imagesBoutons.get(1), Couleur.BLANC);
        Button redButton = creerBoutonAvecImage(imagesBoutons.get(3), Couleur.ROUGE);
        Button purpleButton = creerBoutonAvecImage(imagesBoutons.get(4), Couleur.VIOLET);
        Button greenButton = creerBoutonAvecImage(imagesBoutons.get(6), Couleur.VERT);

        row.getChildren().addAll(orangeButton,greenButton,blueButton,redButton,purpleButton,blackButton,whiteButton);

        this.getChildren().add(row);
        ajouterListenersTaille(row);
    }

    private Button creerBoutonAvecImage(String imagePath, Couleur couleur) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty().multiply(0.08));

        Button bouton = new Button();
        bouton.setGraphic(imageView);
        bouton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        bouton.setOnAction(e -> gererActionBouton(e, couleur));

        return bouton;
    }

    private void ajouterListenersTaille(HBox row) {
        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            row.prefHeight(newValue.doubleValue() * 0.8);
        });
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            row.prefWidth(newValue.doubleValue() * 0.8);
        });
    }

    private void gererActionBouton(ActionEvent e, Couleur couleurChoisie) {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        double jetonMaximum = couleurChoisie.getJetonMaximum();
        double miseActuelle = jeu.getJoueur().getMiseTotalePourCouleur(couleurChoisie);

        if (miseActuelle >= jetonMaximum) {
            showAlert(resourceBundle.getString("maxBetReached"),
                    MessageFormat.format(resourceBundle.getString("alreadyBetMax"),
                            jeu.formaterNombre(jetonMaximum),
                            couleurChoisie.getNomTraduit(resourceBundle)));
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(MessageFormat.format(resourceBundle.getString("betOnColor"), couleurChoisie.getNomTraduit(resourceBundle)));
        dialog.setHeaderText(MessageFormat.format(resourceBundle.getString("howMuchToBet"), couleurChoisie.getNomTraduit(resourceBundle)));
        dialog.setContentText(resourceBundle.getString("enterYourBet"));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(montant -> {
            try {
                double montantMise = Double.parseDouble(montant);
                jeu.placerMise(couleurChoisie, montantMise);
                LoggerService.log(MessageFormat.format(resourceBundle.getString("betPlaced"), montant, couleurChoisie.getNomTraduit(resourceBundle)));
                playJetSound();
            } catch (NumberFormatException nfe) {
                showAlert(resourceBundle.getString("formatError"), resourceBundle.getString("enterValidNumber"));
            } catch (IllegalArgumentException iae) {
                showAlert(resourceBundle.getString("betError"), iae.getMessage());
            }
        });
    }

    @Override
    public void updateLangue() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
    }

    private void playJetSound() {
        String jetSoundFile = "src/main/resources/son/jeton.mp3";
        AudioClip jetSound = new AudioClip(new File(jetSoundFile).toURI().toString());
        jetSound.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



