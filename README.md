# Projet E4 en Android

## Objectif:
Créer une application android récupérant des jokes du site https://api.chucknorris.io/ et les afficher.

## Langage:
Kotlin

## Fonctionnalités:
* Scroll de l'écran vers le haut charge de nouvelles jokes
* Swipe-to-Refresh renouvele la totalité des jokes
* Bouton star pour sauvegarder des jokes; lorsque l'on relance l'application, elles sont affichées en premier
* Bouton share qui permet de partager la joke
* Déplacer les jokes
* Supprimer une joke en swipant sur le coté

## Élements d'implémentation:
* Utilisation d'un recycler view et d'un adapteur.
* On passe pour chaque joke que l'on souhaite afficher un model de la View souhaitée
* Sauvegarde des jokes pour la rotation par le Bundle
* Sauvegarde des jokes starées lors du redémarrage de l'application par un fichier SharedPreferences
