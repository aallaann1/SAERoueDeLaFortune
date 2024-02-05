package fr.RoueDeLaFortune.mecanique;

import java.util.ResourceBundle;

public enum Couleur {
    ORANGE("Orange", 1.0, 100),
    VERT("Vert", 3.0, 50),
    BLEU("Bleu", 5.0, 25),
    ROUGE("Rouge", 10.0, 12),
    VIOLET("Violet", 20.0, 10),
    BLANC("Blanc", 50.0, 5),
    NOIR("Noir", 50.0, 5);

    private final String nom;
    private final double facteurGain;
    private final double jetonMaximum;

    private Couleur(String nom, double facteurGain, double jetonMaximum) {
        this.nom = nom;
        this.facteurGain = facteurGain;
        this.jetonMaximum = jetonMaximum;
    }

    public String getNom() {
        return nom;
    }

    public double getFacteurGain() {
        return facteurGain;
    }

    public double getJetonMaximum() {
        return jetonMaximum;
    }

    public String getNomTraduit(ResourceBundle resourceBundle) {
        switch (this) {
            case ORANGE:
                return resourceBundle.getString("color.orange");
            case VERT:
                return resourceBundle.getString("color.green");
            case BLEU:
                return resourceBundle.getString("color.blue");
            case ROUGE:
                return resourceBundle.getString("color.red");
            case VIOLET:
                return resourceBundle.getString("color.violet");
            case BLANC:
                return resourceBundle.getString("color.white");
            case NOIR:
                return resourceBundle.getString("color.black");
            default:
                return this.nom;
        }
    }
}

