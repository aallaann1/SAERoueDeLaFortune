package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.ILangueUpdateable;
import fr.RoueDeLaFortune.mecanique.Jeu;
import fr.RoueDeLaFortune.mecanique.Joueur;
import fr.RoueDeLaFortune.services.LangueManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ResourceBundle;

public class VueJoueur extends VBox implements ILangueUpdateable {
    private Joueur joueur;
    private Label soldeLabel;
    private Label gradeLabel;
    private ImageView logoJeu;
    private DropShadow neonEffect;

    public VueJoueur(Jeu jeu) {
        this.joueur = jeu.getJoueur();
        initialiserVue();
        LangueManager.addLangueListener(this);
    }

    private void initialiserVue() {
        neonEffect = new DropShadow();
        neonEffect.setColor(Color.rgb(252, 220, 18));
        neonEffect.setRadius(40);
        neonEffect.setSpread(0.5);

        creerEtConfigurerLogoJeu();
        creerEtConfigurerLabels();

        this.getChildren().addAll(logoJeu, soldeLabel, gradeLabel);
        updateLangue();
    }

    private void creerEtConfigurerLogoJeu() {
        logoJeu = new ImageView(new Image("/img/logoJeu.png"));
        logoJeu.setEffect(neonEffect);
        logoJeu.setFitHeight(200);
        logoJeu.setPreserveRatio(true);
    }

    private void creerEtConfigurerLabels() {
        String labelStyle = "-fx-background-color: #F0F0F0; -fx-border-color: #001D3D; -fx-border-width: 4px; " +
                "-fx-font-weight: bold; -fx-border-radius: 20 20 20 20; -fx-background-radius: 20 20 20 20; " +
                "-fx-text-fill: BLACK; -fx-padding: 5 10px;";
        BackgroundFill backgroundFill = new BackgroundFill(Color.WHITE, new CornerRadii(20), Insets.EMPTY);

        soldeLabel = new Label();
        configurerLabel(soldeLabel, labelStyle, backgroundFill);

        gradeLabel = new Label();
        configurerLabel(gradeLabel, labelStyle, backgroundFill);

        updateSoldeLabel();

        this.setSpacing(10);
    }

    private void configurerLabel(Label label, String style, BackgroundFill backgroundFill) {
        label.setFont(Font.font("Helvetica", 18));
        label.setStyle(style);
        label.setEffect(neonEffect);
        label.setBackground(new Background(backgroundFill));
    }

    private void updateSoldeLabel() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        StringBinding soldeBinding = Bindings.createStringBinding(() -> {
            double solde = joueur.getSolde().get();
            return resourceBundle.getString("playerBalance") + " : " +
                    (solde % 1.0 == 0 ? String.format("%.0f", solde) : String.format("%.2f", solde));
        }, joueur.getSolde());
        soldeLabel.textProperty().bind(soldeBinding);
    }

    @Override
    public void updateLangue() {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        updateSoldeLabel();
        String gradeKey = "grade." + joueur.getGrade().getNom().toLowerCase();
        gradeLabel.setText(resourceBundle.getString("playerGrade") + " : " + resourceBundle.getString(gradeKey));
    }
}

