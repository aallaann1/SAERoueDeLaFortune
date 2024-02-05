package fr.RoueDeLaFortune.mecanique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        /*
        Joueur j = creationJoueurPourTest();
        Jeu jeu = creationJeuPourTest(j);
        for (int i=0; i<10; i++){
            jeu.jouer(testerMiser(jeu,j));
        }
        */
    }


    public static Joueur creationJoueurPourTest(){
        Grade g = new Grade("normal",1.2);
        return new Joueur(100.0,g);
    }

    public static void testerNotreRandom(){
        notreRandom a = new notreRandom();
        System.out.printf(String.valueOf(a.retourneRandom()));
    }

    /*public static Jeu creationJeuPourTest(Joueur jo){
        return new Jeu(Jeu.creationCouleurs(),jo);
    }*/

    public static Map<Integer,Double> testerMiser(Jeu jeu, Joueur j){
        Map<Integer,Double> laMise = new HashMap<>();
        laMise.put(0, 0.0);
        laMise.put(1,5.0);
        laMise.put(2,0.0);
        laMise.put(3,0.0);
        laMise.put(4,0.0);
        laMise.put(5,0.0);
        laMise.put(6,0.0);
        return laMise;
    }
}

