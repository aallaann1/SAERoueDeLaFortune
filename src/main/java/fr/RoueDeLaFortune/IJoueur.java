package fr.RoueDeLaFortune;
import fr.RoueDeLaFortune.mecanique.Couleur;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableMap;


public interface IJoueur {
    SimpleDoubleProperty getSolde();
    void setSolde(double solde);
    void afficherGrade();
    ObservableMap<Couleur, Double> getMise();
}
