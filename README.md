# CAR TP1 - Serveur FTP 
Jean-Serge Monbailly

Ecriture en Java d'un serveur FTP 
=================================


Fichiers présents : 
===================

+ README.md			: 	ce document
+ Répertoire \ 		: 	racine des fichiers sur le serveur FTP

Package src/server
------------------
+ Serveur.java		: 	classe contenant le main pour lancer le serveur FTP
+ FTPRequest.java	: 	classe (extends thread) représentant une connexion d'un client
						sur le serveur

Package src/client
------------------ 
+ Client.java		: 	(obsolète) code d'un client personnalisé pour le serveur FTP
			
Package src/util
----------------
+ Tools.java		: 	classe offrant plusieurs constantes utiles
+ Identifier.java 	: 	classe (singleton) permettant la gestion des identifications 
						sur le serveur	

Package test/server
------------------- 
+ Test_Request.java : classe de test pour les commandes gérées par le serveur FTP


Implémentation :
================
La connexion au serveur FTP se fait par le port 2000.
La connexion d'un client au serveur déclenche un Thread permettant de
gérer la connexion (autorise les connexions multiples).

L'utilisation du pattern singleton dans la classe Identifier permet de limiter à 1
le nombre d'instances permettant de gérer les identifications sur le serveur (malgré
de multiples Thread)

Les messages fonctionnels sont :
+ QUIT 	: 	ferme la connexion 
+ USER 	: 	indique au serveur le nom d'utilisateur pour la connexion
+ PASS 	: 	tentative de connexion par mot de passe pour l'utilisateur
+ STOR 	: 	envoi d'un fichier du client vers le serveur
+ RETR 	: 	retrait d'un fichier du serveur vers le client
+ LIST	:	envoie au client la liste des fichiers présent dans le répertoire courant
+ SYST 	: 	envoie au client des informations sur le système du serveur
+ EPRT 	: 	réception de la part du client des informations sur la connexion à ouvrir
			pour le transfert 
+ PORT 	: 	semblable à la commande EPRT mais spécifique au protocole IPv4


Dans le cas où la commande n'est pas reconnue, un message l'indiquant est
transmis. 

Les tests ne sont écrits que pour les commandes QUIT USER et PASS.


Notes :
=======

Général 
-------
+ La gestion des commandes reçues délègue leur exécution à une fonction tierce.
+ Les commandes demandant un ou plusieurs paramètres gérent le cas ou l'un 
d'eux est null
+ Les commandes relatives au système de fichiers ont pour racine le répertoire \ du projet.
L'initialisation de la liste des utilisateurs créé (s'ils n'existent pas déjà) les 
répertoires correspondant
+ Un utilisateur ne peut travailler que dans son propre répertoire, les commandes sont rejetées
si il tente d'accèder à un autre répertoire

USER 
----
+ La commande USER retourne un code d'erreur si l'utilisateur spécifié n'est pas
reconnu

PASS 
----
+ La commande PASS retourne un code d'erreur si le mot de passe précisé ne correspond
pas à celui attendu

QUIT
----
+ La commande QUIT ferme la connexion du coté serveur mais cela n'a apparemment pas 
d'effet chez le client (au client donc de fermer sa connexion ou de traiter les
erreurs résultant de l'utilisation de celle-ci)

EPRT & PORT
-----------
+ Les commandes PORT et EPRT supposent les paramètres valides

STOR
----
+ La commande STOR accepte un 2e arguments indiquant le chemin du fichier sur le serveur distant
Si ce chemin n'existe pas il est créé 
+ La commande STOR retourne au client un code d'erreur si le fichier existe déjà mais est un
répertoire
+ La commande STOR retourne un code d'erreur si le chemin demandé n'appartient pas au
répertoire attribué à l'utilisateur

RETR
----
+ La commande RETR retourne un code d'erreur si le fichier demandé n'existe pas ou ne fait pas partie
du répertoire attribué à l'utilisateur
+ La commande RETR retourne un code d'erreur si le fichier demandé est un répertoire


TODO :
======
+ Améliorer le  système permettant de vérifier si le client est toujours 
connecté
+ Utiliser un timeout pour éviter les connexions prolongées abusives 
+ Écrire les tests du serveur : 
	- LIST 
	- STOR
	- RETR
	- EPRT
	- PORT
+ Gérer des paramètres incorrects pour la commande EPRT
+ Écrire les commandes permettant de naviguer dans le système de fichiers