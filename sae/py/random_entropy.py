import random
import subprocess




subprocess.run(["python", "monRandom.py"])
with open('doublemix_data.txt', 'r') as file:
    seed = file.read().strip()


# Votre liste de chiffres
ma_liste = [1,50,1,3,1,5,1,3,1,10,3,1,5,1,20,1,3,1,5,1,5,1,10,1,3,5,1,51,1,3,1,5,1,3,5,10,5,1,3,1,20,1,3,1,5,1,3,1,5,1,10,3]


# Convertir la seed en une valeur entière
seed_int = int(seed, 2)


# Utiliser la seed pour choisir un index
random.seed(seed_int)
index_choisi = random.randint(0, len(ma_liste) - 1)
# Accéder à l'élément correspondant dans la liste
chiffre_aleatoire = ma_liste[index_choisi]


with open('resultat.txt', 'w') as fichier:
    fichier.write(str(chiffre_aleatoire))



