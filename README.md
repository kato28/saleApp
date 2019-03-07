# saleApp
protocole réseaux pour une application de vente 
	 	 	

Note : Les messages circulant sont indiqués entre crochets, les crochets ne faisant pas partie du message. Pour les messages d’erreurs, l’idée est d’avoir « un code par type d’erreur ». Tous les messages n’ont que 1024 bytes au maximum.

Les clients

Les caractéristiques d’un client sont les suivantes :
- un identifiant
- un mot de passe
- une adresse IP et un port TCP pour que les autres clients puissent le contacter
- un booléen pour indiquer si il est connecté ou non
- une ServerSocket pour communiquer avec les autres clients

Les serveurs

Les caractéristiques d’un serveur sont les suivantes :
- une liste de clients
- un port TCP pour accueillir les clients (2345)
- une HashMap (oui j’adore les hashmaps) <Key=Numéro d’annonce, Value=Client associé>


Architecture


L’architecture du système sera hybride. La communication entre les utilisateurs et le serveur principal se fera en suivant le paradigme Client / Serveur, alors que la communication entre les clients pour acheter le produit d’une annonce suivra une architecture Pair à Pair.


Interaction client-serveur

S’inscrire. 	
1) Le client envoie : [REGI id mdp+++]
2) Le serveur répond :
	- [OKOK+++] si tout s’est bien passé
	- [KOKO code+++] si il y a une erreur (codes 0 ou 1) et ferme la connexion

Se connecter. 	
1) Le client envoie : [CONE id mdp port+++]
2) Le serveur répond :
	- [OKOK+++] si tout s’est bien passé
	- [KOKO code+++] si il y a une erreur (codes 1, 2 ou 3) et ferme la connexion

Se déconnecter. 	
1) Le client envoie : [DECO+++]
2) Le serveur répond [GBYE+++] et il ferme la connexion

Poster une annonce. 	
1) Le client envoie : [POST code prix descriptif+++] (code est un integer allant de 1 à 6 indiquant le type de l’annonce – cf sujet, prix est au format _._, descriptif est un string décrivant l’annonce)
2) Le serveur répond :
	- [OKOK id+++] si tout s’est bien passé (id étant l’id unique de l’annonce généré par le serveur)
	- [KOKO code+++] si il y a une erreur (codes 1 ou 4)

Supprimer une annonce. 	
1) Le client envoie : [SUPR idAnnonce+++]
2) Le serveur répond :
	- [OKOK+++] si tout s’est bien passé
	- [KOKO code+++] si il y a une erreur (codes 1, 4, 5 ou 6)


Récupérer toutes les annonces 
1) Le client envoie : [LIST+++]
2) Le serveur répond :
	- [NBAN nb+++] nb est le nombre d’annonces
	- [ANNO id code prix descriptif+++] pour chaque annonce


Récupérer les informations sur une annonce 	
1) Le client envoie : [INTR idAnnonce+++]
2) Le serveur répond :
	- [CONT ip port+++] avec adresse ip et port les informations du propriétaire de l’annonce 
	- [NCON+++] si le propriétaire de l’annonce n’est pas connecté
	- [KOKO code+++] si il y a une erreur (codes 1 ou 5)


Interaction entre clients
La communication entre les clients se fera en utilisant TCP.

Initialiser l’échange et vérifier disponibilité
1) Le client envoie : [DISP idAnnonce+++] au propriétaire de l’annonce
2) Le client propriétaire répond :
	- [OKOK+++] si l’annonce est disponible
	- [KOKO code+++] si il y a une erreur (codes 1, ou 5)

Envoyer un message
1) Le client envoie : [MSSG texte+++] au propriétaire de l’annonce
2) Le client propriétaire répond : [MSSG texte+++]

Acheter le produit
1) Le client envoie : [ACHA+++] au propriétaire de l’annonce
2) Le client propriétaire répond :
	- [OKOK+++] si il est d’accord
	- [KOKO code+++] si il y a une erreur (codes 1 ou 8)	
La connexion est fermée après l’achat

Terminer l’échange
1) Le client envoie : [GBYE+++] au propriétaire de l’annonce
2) Le client propriétaire répond : [GBYE+++]
La connexion est fermée après l’envoi de ce message


Version sécurisée de la communication entre clients
La communication entre les clients se fait en utilisant des Secure Socket (Secure Socket Layer SSL) et les options par défaut.
