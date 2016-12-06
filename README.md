# Madjoh mini-projet

Ce projet consiste en 2 applications : une Androïd (API 15 minimum) et un serveur NodeJs.
L'apk pour installer le jeu Androïd est disponnible dans le dossier de l'application. Comme elle communique 
avec le serveur NodeJs, il faudrait renseigner dans la variable 'URL_IP' l'ip du serveur et dans 
'URL_PORT' le port du serveur.
Ce projet utilise l'API de traduction Yandex.Translate (http://translate.yandex.com/) que j'ai choisi pour
traduire mes mots (je doute un petit peu qu'en même de la justesse des traductions). Le
serveur utilise une base de données de Mot sur un SGBDR mysql (le script est dans le fichier .sql). Plus
l'utilisateur marque de point plus c'est difficile et moins il en marque plus c'est facile.le fichier 
package.json du dossier du serveur permettra de récuperer les dépendances de bibliothèques. Pour l'instant,
le serveur écoute sur le port 8080, pour changer cela il faut changer la dernière instruction 'app.listen(8080)'.
Les lignes 6 à 12 du fichier serveur 'backend.js' correspondent à la configuration de la connextion au serveur.
Il faudra changer les entrées pour adapter.

En lancant l'application Androïd, on arrive en premier sur une activité qui vous présente le jeu avec 
un bouton de lancement. Ce bouton lance une autre activité et commence la partie. Plusieurs cas sont gérés :
si le serveur est indisponible, si internet n'est pas disponible, changement d'orientation, si le serveur 
ne réussit pas à traduire etc...
Un retour à l'activité précédente est possible.

Un approfondissement de l'interface graphique de l'activité 'Game' aurait pu être fait. L'UX de 'lutilisateur
aurait aussi pu être mieux réfléchie en déplaçant par exemple le curseur après la première lettredu mot anglais
et en connectant le boutont 'ok' du clavier virtuel et la fonction de validation. Une possibilité de suavegarde
de la partie dans des fichiers préférences par exmeple aurait aussi pu être rajouter lorsqu'on arrête 
l'application Androïd et on la relance.

#Bon jeu :)
