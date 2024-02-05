package fr.RoueDeLaFortune.vues;

import fr.RoueDeLaFortune.mecanique.Grade;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.List;

public class VueChoixGrade extends VBox {
    private ObjectProperty<Grade> gradeChoisi = new SimpleObjectProperty<>();

    public VueChoixGrade(List<Grade> grades) {
        creerVue(grades);
    }

    private void creerVue(List<Grade> grades) {
        setAlignment(Pos.CENTER);
        setId("gradeChoice");
        setSpacing(50);

        // Texte "Choisissez votre grade" en néon
        Text headerText = new Text("Choisissez votre grade / Choose your grade");

        // Utilisation d'un effet DropShadow pour simuler l'effet néon
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(javafx.scene.paint.Color.rgb(252, 220, 18));
        neonEffect.setRadius(20);
        neonEffect.setSpread(0.4);

        headerText.setEffect(neonEffect);
        headerText.getStyleClass().add("headerText");
        getChildren().add(headerText);

        // HBox pour contenir les boutons
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(10);
        buttonContainer.getStyleClass().add("button-container");

        for (Grade grade : grades) {
            // Création d'un bouton pour le grade
            String gradeName = getDualLanguageGradeName(grade.getNom());
            Button gradeButton = new Button(gradeName);

            // Au clic, on enregistre le grade dans la propriété gradeChoisi
            gradeButton.setOnAction(event -> gradeChoisi.set(grade));

            // Création de l'effet DropShadow pour simuler l'effet néon
            gradeButton.getStyleClass().add("neon-button");
            gradeButton.setEffect(neonEffect);

            // Ajouter le bouton à la HBox
            buttonContainer.getChildren().add(gradeButton);
        }

        // Ajouter la HBox à la VBox
        getChildren().add(buttonContainer);
    }

    private String getDualLanguageGradeName(String gradeName) {
        switch (gradeName) {
            case "Visiteur":
                return "Visiteur / Guest";
            case "Inscrit":
                return "Inscrit / Member";
            default:
                return gradeName;
        }
    }

    public ObjectProperty<Grade> gradeChoisiProperty() {
        return gradeChoisi;
    }
}
