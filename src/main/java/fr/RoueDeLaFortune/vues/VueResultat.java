package fr.RoueDeLaFortune.vues;
import fr.RoueDeLaFortune.mecanique.Couleur;
import fr.RoueDeLaFortune.services.LangueManager;

import java.util.ResourceBundle;

public class VueResultat {
    public void afficherResultat(Couleur couleurGagnante) {
        ResourceBundle resourceBundle = LangueManager.getResourceBundle();
        String nomCouleurTraduit = couleurGagnante.getNomTraduit(resourceBundle);
        String messageGagnant = resourceBundle.getString("winningColor") + nomCouleurTraduit;
        System.out.println(messageGagnant);
    }
}
