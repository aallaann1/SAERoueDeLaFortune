package fr.RoueDeLaFortune;

import fr.RoueDeLaFortune.mecanique.Couleur;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableMap;

import java.util.Map;

public interface IJeu {
    void lancerRoue();
    void placerMise(Couleur couleur, double montant);
    Couleur getCouleurGagnante();
    double calculerGain(ObservableMap<Couleur, Double> laMise);
    double getMiseMinimaleProperty();
    int getMiseMinimalePropertyInt();
    double getMiseMaximalePourCouleur(Couleur couleur);
    int getMiseMaximalePourCouleurInt(Couleur couleur);

}
