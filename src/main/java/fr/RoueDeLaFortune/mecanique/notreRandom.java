package fr.RoueDeLaFortune.mecanique;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class notreRandom {


    public void cmd(String commande) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", commande);

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                System.out.println(ligne);
            }

            int exitCode = process.waitFor();
            System.out.println("La commande s'est terminée avec le code de sortie : " + exitCode);
            //TODO si il y a un probleme enlever les //;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exe() {
        //String commande = "cd ../../py && (if exist random_entropy.py (python .\\random_entropy.py) else (echo Le fichier random_entropy.py n'existe pas))";
        String commande ="cd sae/py && python random_entropy.py";
        cmd(commande);
    }



    public String lire() {
        //String nomFichier = "..\\..\\py\\resultat.txt";
        String nomFichier = "sae\\py\\resultat.txt";
        try {
            FileReader fileReader = new FileReader(nomFichier);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String resultat = bufferedReader.readLine();
            bufferedReader.close();
            return resultat;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int trouverIndex(int valeur) {
        List<Integer> l = new ArrayList<>();
        l.add(1);
        l.add(3);
        l.add(5);
        l.add(10);
        l.add(20);
        l.add(50);
        l.add(51);

        for (int i = 0; i < l.size(); i++) {
            if (l.get(i) == valeur) {
                return i;
            }
        }

        return -1;
    }


    public int retourneRandom(){
        exe();
        String nbString = lire();
        return trouverIndex(Integer.parseInt(nbString));
    }


}
