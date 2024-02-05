package fr.RoueDeLaFortune.mecanique;

import fr.RoueDeLaFortune.IJoueur;
import fr.RoueDeLaFortune.services.LangueManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Joueur implements IJoueur {
    private SimpleDoubleProperty solde;
    private Grade grade;
    private ObservableMap<Couleur, Double> mise = FXCollections.observableHashMap();

    public Joueur(double soldeInitial, Grade grade) {
        this.solde = new SimpleDoubleProperty(soldeInitial);
        this.grade = grade;
        this.mise = FXCollections.observableHashMap();
    }

    public double getMiseTotalePourCouleur(Couleur couleur) {
        return mise.getOrDefault(couleur, 0.0);
    }

    public void miser(Couleur couleur, double montant) {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        if (montant > getSolde().get()) {
            throw new IllegalArgumentException(resourceBundle.getString("insufficientBalanceForBet"));
        }
        mise.put(couleur, mise.getOrDefault(couleur, 0.0) + montant);
        setSolde(getSolde().get() - montant);
    }

    @Override
    public ObservableMap<Couleur, Double> getMise() {
        return this.mise;
    }

    public Double getSoldeDouble(){
        return solde.get();

    }

    public void clearMises() {
        mise.clear();
    }

    public void mettreAJourSolde(double montant) {
        solde.set(solde.get() + montant);
    }

    @Override
    public SimpleDoubleProperty getSolde() {
        return solde;
    }

    @Override
    public void setSolde(double nouveauSolde) {
        solde.set(nouveauSolde);
    }

    @Override
    public void afficherGrade() {
        System.out.println("Grade: " + grade.getNom());
    }

    public Grade getGrade() {
        return this.grade;
    }

    public List<Couleur> getCouleurMise() {
        return List.copyOf(mise.keySet());
    }


    public void setMise(ObservableMap<Couleur, Double> mise) {
        this.mise = mise;
    }
}
