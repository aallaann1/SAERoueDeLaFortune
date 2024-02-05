package fr.RoueDeLaFortune.mecanique;

import fr.RoueDeLaFortune.ILangueUpdateable;
import fr.RoueDeLaFortune.services.ChatService;
import fr.RoueDeLaFortune.services.LangueManager;
import fr.RoueDeLaFortune.services.LoggerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Bot extends Joueur implements ILangueUpdateable {

    private String pseudo;
    private ResourceBundle resourceBundle = LangueManager.getResourceBundle();
    public Bot() {
        super(150.0, new Grade("Inscrit",150.0));
        this.pseudo = pseudoBot(null);
        LangueManager.addLangueListener(this);
    }

    public String pseudoBot(String pseudoAEviter) {
        List<String> listePrenom = new ArrayList<>();
        listePrenom.add("Faker");
        listePrenom.add("Xx_DarkSasuke_xX");
        listePrenom.add("SiphanoFanAccount16");
        listePrenom.add("Clive_Rosefield");
        listePrenom.add("Pink_Pie_amg");
        listePrenom.add("OmegaGEEK");
        listePrenom.add("Xx_Naruto_Raptor_xX");
        listePrenom.add("THe_French_Monster");
        listePrenom.add("DoctorWarzz");
        listePrenom.add("NovaCovid91");
        listePrenom.add("C_Stan_Account");
        listePrenom.add("JavaLearner14");


        if (pseudoAEviter == null || pseudoAEviter.isEmpty()) {
            Random random = new Random();
            int indiceAleatoire = random.nextInt(listePrenom.size());
            return listePrenom.get(indiceAleatoire);
        }

        String pseudoAleatoire = pseudoAEviter;
        while (listePrenom.contains(pseudoAleatoire)) {
            Random random = new Random();
            int indiceAleatoire = random.nextInt(listePrenom.size());
            pseudoAleatoire = listePrenom.get(indiceAleatoire);
        }

        return pseudoAleatoire;
    }

    public void creerFausseMiseBot(int i){
        Double solde = getSolde().get();
        Random r = new Random();

        if (solde<1){
            //todo plus tard
        }

        if (solde<5) {
            int a = r.nextInt(2);
            if (a == 0) {
                miser(Couleur.NOIR, 1.0);
            } else {
                miser(Couleur.BLANC, 1.0);
            }

        }else{
            //int i = r.nextInt(8);
            switch (i) {
                case 0 -> {
                    miser(Couleur.VERT, 5.0);
                }
                case 1 -> {
                    if (solde > 14) {
                        miser(Couleur.BLANC, 1.0);
                        miser(Couleur.VERT, 9.0);
                    }
                    miser(Couleur.ROUGE, 4.0);
                    miser(Couleur.ORANGE, 1.0);
                }
                case 2 -> {
                    if (solde > 24) {
                        miser(Couleur.VIOLET, 14.0);
                        miser(Couleur.ROUGE, 6.0);
                    }
                    miser(Couleur.ORANGE, 4.0);
                    miser(Couleur.NOIR, 1.0);
                }
                case 3 -> {
                    if (solde > 17) {
                        miser(Couleur.ORANGE, 8.0);
                        miser(Couleur.ROUGE, 9.0);
                    }
                    miser(Couleur.VERT, 4.0);
                    miser(Couleur.VIOLET, 1.0);
                }
                case 4 -> {
                    if (solde > 8) {
                        miser(Couleur.BLANC, 3.0);
                    }
                    miser(Couleur.ORANGE, 2.0);
                    miser(Couleur.ROUGE, 3.0);
                }
                case 5 -> {
                    if (solde > 14) {
                        miser(Couleur.ROUGE, 4.0);
                        miser(Couleur.BLEU, 5.0);
                    }
                    miser(Couleur.VIOLET, 2.0);
                    miser(Couleur.NOIR, 3.0);
                }
                case 6 -> {
                    if (solde > 9) {
                        miser(Couleur.BLEU, 4.0);
                    }
                    miser(Couleur.ROUGE, 4.0);
                    miser(Couleur.VERT, 1.0);
                }
                case 7 -> {
                    if (solde > 28) {
                        miser(Couleur.BLEU, 20.0);
                        miser(Couleur.BLANC, 3.0);
                    }
                    miser(Couleur.NOIR, 3.0);
                    miser(Couleur.ORANGE, 1.0);
                }
            }
        }

    }

    public void afficherMiseBot() {
        String resultat = getPseudo() + " " + resourceBundle.getString("parier");
        int n = 0;
        for (Couleur c : getMise().keySet()) {
            double valeur = getMise().get(c);
            int nombreEntier = (int) valeur;
            resultat += " " + nombreEntier + " ";
            String couleurTraduite = c.getNomTraduit(resourceBundle);
            resultat += resourceBundle.getString("jetons") + " " + couleurTraduite;
            if (n != getMise().size() - 1) {
                resultat += ", ";
            }
            n++;
        }
        ChatService.ajouterMessage(resultat, false);
        ChatService.ajouterMessage("", false);
    }

    public void mettreAjourSolde(Couleur c){
        if (getMise().containsKey(c)){
            double somme = getMise().get(c)*c.getFacteurGain();
            setSolde(getSolde().get() + somme);
            possibiliteMessageWin(somme);
        }else{
            possibiliteMessageLoose();
        }
    }


    public void possibiliteMessageWin(double sommeGagnee) {
        Random r = new Random();
        int nb = r.nextInt(2);
        int somme = (int) sommeGagnee;
        if (nb == 0) {
            ChatService.ajouterMessage(getPseudo() + " : siiuuuuu + " + somme + " " + resourceBundle.getString("tokens"), false);
        } else {
            ChatService.ajouterMessage(getPseudo() + " : lets go + " + somme + " " + resourceBundle.getString("tokens"), false);
        }
        ChatService.ajouterMessage("", false);
    }



    public void possibiliteMessageLoose() {
        Random r = new Random();
        int nb = r.nextInt(2);
        if (nb == 0) {
            ChatService.ajouterMessage(getPseudo() + " : " + resourceBundle.getString("loseMessage1"), false);
        } else {
            ChatService.ajouterMessage(getPseudo() + " : " + resourceBundle.getString("loseMessage2"), false);
        }
        ChatService.ajouterMessage("", false);
    }

    public String getPseudo() {
        return pseudo;
    }

    public void changerPseudo(String ancienPseudo) {
        this.pseudo= pseudoBot(ancienPseudo);
    }

    @Override
    public void updateLangue() {
        resourceBundle = LangueManager.getResourceBundle();
    }
}