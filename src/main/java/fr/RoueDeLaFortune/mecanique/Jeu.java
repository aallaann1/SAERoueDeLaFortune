package fr.RoueDeLaFortune.mecanique;

import fr.RoueDeLaFortune.IJeu;
import fr.RoueDeLaFortune.services.ChatService;
import fr.RoueDeLaFortune.services.LangueManager;
import fr.RoueDeLaFortune.services.LoggerService;
import fr.RoueDeLaFortune.vues.RoueDeLaFortuneIHM;
import fr.RoueDeLaFortune.vues.VueJoueur;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


import java.util.*;

public class Jeu implements IJeu {
    private List<Couleur> couleurs;
    private Joueur joueur;
    private List<Bot> listeBot = new ArrayList<>();
    private Couleur couleurGagnante;
    private notreRandom notreRandom;
    private Random random;
    private VueJoueur vueJoueur;
    // Provisoir : definir la mise minimale
    private SimpleDoubleProperty miseMinimaleProperty = new SimpleDoubleProperty(1.0);
    private SimpleObjectProperty<Couleur> couleurGagnanteProperty = new SimpleObjectProperty<>();
    private SimpleDoubleProperty soldeProperty;
    private SimpleBooleanProperty partieTermineeProperty = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty tourTermineProperty = new SimpleBooleanProperty(true); // Devient false quand la roue tourne.
    private static final boolean utilsierNotreRandom=false;

    public Jeu(Joueur joueur) {
        this.couleurs = Arrays.asList(Couleur.values());
        this.joueur = joueur;
        this.vueJoueur = new VueJoueur(this);
        if (utilsierNotreRandom){
            this.notreRandom = new notreRandom();
        }else{
            this.random = new Random();
        }
        this.soldeProperty = new SimpleDoubleProperty(this.joueur.getSolde().get());
        creerBot();

    }

    public void creerBot(){
        Bot botNumero1 = new Bot();
        Bot botNumero2 = new Bot();

        if (botNumero1.getPseudo().equals(botNumero2.getPseudo())){
            //ca evite le moment ou il y a 2 fois le meme pseudo
            botNumero2.changerPseudo(botNumero2.getPseudo());
        }

        listeBot.add(botNumero1);
        listeBot.add(botNumero2);

        for (Bot b : listeBot){
            String mesageEntree = b.getPseudo() + " vient de rejoindre la partie";
            ChatService.ajouterMessage(mesageEntree,true);
        }
    }

    @Override
    public void placerMise(Couleur couleur, double montant) {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        double miseMinimale = getMiseMinimaleProperty();
        double jetonMaximum = couleur.getJetonMaximum();
        double miseActuelle = joueur.getMiseTotalePourCouleur(couleur);
        double miseTotaleApresNouvelleMise = miseActuelle + montant;
        double miseRestante = jetonMaximum - miseActuelle;

        if (miseTotaleApresNouvelleMise > jetonMaximum) {
            if (miseActuelle > 0) {
                throw new IllegalArgumentException(
                        resourceBundle.getString("betImpossibleAlreadyBet") + " " +
                                formaterNombre(miseActuelle) + " " +
                                resourceBundle.getString("tokensOn") + " " +
                                couleur.getNomTraduit(resourceBundle) + ". " +
                                resourceBundle.getString("canOnlyBet") + " " +
                                formaterNombre(miseRestante) + " " +
                                resourceBundle.getString("additionalTokens")
                );
            } else {
                throw new IllegalArgumentException(
                        resourceBundle.getString("betExceedsMaximum") + " " +
                                formaterNombre(jetonMaximum) + " " +
                                resourceBundle.getString("tokensForColor") + " " +
                                couleur.getNomTraduit(resourceBundle) + "."
                );
            }
        }
        if (montant < miseMinimale) {
            throw new IllegalArgumentException(
                    resourceBundle.getString("betBelowMinimum") + " " +
                            formaterNombre(miseMinimale) + "."
            );
        }
        if (montant > joueur.getSolde().get()) {
            throw new IllegalArgumentException(resourceBundle.getString("insufficientBalanceForBet"));
        }
        joueur.miser(couleur, montant);
    }

    @Override
    public void lancerRoue() {
        //int index = random.nextInt(couleurs.size());
        for (Bot b : listeBot){
            Random r = new Random();
            int i=r.nextInt(8);
            b.creerFausseMiseBot(i);
            b.afficherMiseBot();
        }
        if (utilsierNotreRandom){
            int index = notreRandom.retourneRandom();
            couleurGagnante = couleurs.get(index);
            couleurGagnanteProperty.set(couleurGagnante);
            couleurGagnanteProperty().set(couleurGagnante);
        }else{
            int[] ma_liste = {1, 50, 1, 3, 1, 5, 1, 3, 1, 10, 3, 1, 5, 1, 20, 1, 3, 1, 5, 1, 5, 1, 10, 1, 3, 5, 1, 51, 1, 3, 1, 5, 1, 3, 5, 10, 5, 1, 3, 1, 20, 1, 3, 1, 5, 1, 3, 1, 5, 1, 10, 3};
            int randomIndex = random.nextInt(ma_liste.length);
            int randomValue = ma_liste[randomIndex];
            int[] listeIndex = {1,3,5,10,20,50,51};
            int i=0;
            for (int valeur : listeIndex){
                if (randomValue==listeIndex[i]){
                    couleurGagnante = couleurs.get(i);
                }
                i++;
            }
        }
        couleurGagnanteProperty().set(couleurGagnante);
    }

    public void demarrerTour() {
        tourTermineProperty.set(false);
    }

    public void terminerTour() {
        for (Bot b : listeBot){
            b.mettreAjourSolde(couleurGagnante);
            b.clearMises();
        }
        calculerGain(joueur.getMise());
        joueur.clearMises();
        tourTermineProperty.set(true);
    }

    @Override
    public double calculerGain(ObservableMap<Couleur, Double> laMise) {
        double gain = 0;
        double totalMises = 0;
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();

        for (Map.Entry<Couleur, Double> entry : laMise.entrySet()) {
            Couleur couleur = entry.getKey();
            Double mise = entry.getValue();
            totalMises += mise;

            if (couleur == couleurGagnante){
                gain += mise * couleur.getFacteurGain();
            }
        }
        if (gain > 0){
            joueur.mettreAJourSolde(gain);
        }

        String nomCouleurGagnante = getCouleurGagnante().getNomTraduit(resourceBundle);
        double facteurGain = getCouleurGagnante().getFacteurGain();
        String gainFormate = formaterNombre(gain);
        String totalMisesFormate = formaterNombre(totalMises);

        System.out.println("Résultat du tour: " + nomCouleurGagnante + ". Facteur gain : " + facteurGain + " Vos gains: " + gainFormate);

        LoggerService.log(resourceBundle.getString("totalBet") + totalMisesFormate);
        LoggerService.log("-----------------------");
        LoggerService.log(resourceBundle.getString("winningColor") + nomCouleurGagnante);
        LoggerService.log(resourceBundle.getString("youWon") + gainFormate + " " +
                resourceBundle.getString("tokens"));
        LoggerService.log("=================");
        if (gain == 0) {
            Random random = new Random();
            int messageIndex = random.nextInt(4) + 1; // Génère un des 4 loserMessage
            String loserKey = "loserMessage" + messageIndex;
            ChatService.ajouterMessage(resourceBundle.getString(loserKey),true);
            ChatService.ajouterMessage("",false);
        } else {
            Random random = new Random();
            int messageIndex = random.nextInt(4) + 1; // // Génère un des 4 congratulationsMessage
            String congratulationsKey = "congratulationsMessage" + messageIndex;
            ChatService.ajouterMessage(resourceBundle.getString(congratulationsKey),true);
            ChatService.ajouterMessage("",false);
        }
        couleurGagnanteProperty().set(null);
        return gain;
    }

    public String formaterNombre(double nombre) {
        return (nombre % 1 == 0) ? String.format("%d", (int) nombre) : String.format("%s", nombre);
    }

    public List<Bot> getListeBot() {
        return listeBot;
    }

    public Joueur getJoueur() {
        return this.joueur;
    }

    @Override
    public double getMiseMinimaleProperty() {
        return miseMinimaleProperty.get();
    }

    @Override
    public int getMiseMinimalePropertyInt() {
        return (int) miseMinimaleProperty.get();
    }

    @Override
    public double getMiseMaximalePourCouleur(Couleur couleur) {
        return couleur.getJetonMaximum();
    }

    @Override
    public int getMiseMaximalePourCouleurInt(Couleur couleur) {
        return (int) couleur.getJetonMaximum();
    }

    @Override
    public Couleur getCouleurGagnante() {
        return couleurGagnanteProperty.get();
    }

    public SimpleObjectProperty<Couleur> couleurGagnanteProperty() {
        return couleurGagnanteProperty;
    }

    SimpleDoubleProperty getSoldeProperty() {
        return soldeProperty;
    }

    public SimpleBooleanProperty partieTerminee() {
        return partieTermineeProperty;
    }

    public void terminerLaPartie() {
        partieTermineeProperty.set(true);
    }

    public SimpleBooleanProperty getTourTermineProperty() {
        return tourTermineProperty;
    }

}
