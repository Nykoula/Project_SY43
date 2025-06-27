# Project SY43
## Creation of an Android application : Vinted

In this project we decided to recreate the Vinted application in our own way.

__**Main features :**__
- Home page with personnal recommandation based on your last search / item click
- Search page with filter to ensure an efficient resultat
- Sell page with more possibilities to extend the personnalisation of an item
- Inbox page to discuss and negociate with clients / owner (seller) and propose a new price before purchasing
- Profile page where we can see our information and edit them with a total liberty (while maintaining security aspects)

This project has a such potential and we are doing our best to improve by taking pleasure in it.


## Depuis la soutenance voici les modifications :

__**Messagerie :**__
- La messagerie fonctionne : on peut reproposer un prix, le vendeur verra alors le Bouton "Accepter"
- Lorsqu'il accepte l'offre, un message pour dire que la proposition a été accepté est automatiquement envoyé
--> En revanche la conversation ne s'actualise pas, il faut donc relancer l'application pour voir les changements
- Désormais on peut voir l'icône du produit que l'on souhaite acheter dans notre Inbox
- On voit l'heure des messages, et il y'a une séparation concernant les jours
- Les conversations se créent automatiquement lorsqu'un message est envoyé
- Les conversations sont ordonnées par date
- On voit un aperçu du dernier message et de qui il a été envoyé
- On peut cliquer sur l'image pour voir de nouveau les détails de l'article

__**Achats :**__
- Une page a été rajouté pour finaliser l'achat en mettant sa carte, son nom et son adresse (les données ne sont
pas sauvegardées volontairement)
- Une API google maps a été mise et configurer pour n'être fonctionnel que avec les projets Android et plus
précisément la manière dont on s'en sert très précisemment : lorsqu'une adresse est mise dans le formulaire
la map se met à jour pour montrer le lieu

__**Navigation :**__
- Une transition en fondu a été mise par défaut dans le projet

__**Dressing :**__
- Il n'est plus possible de modifier ou de supprimer ses articles vendus

__**Photo de profil :**__ 
- Le soucis éphémère qui faisait que l'appli crashait quand on voulait en mettre une est résolue

__**Détails sur l'article :**__
- Le bouton "négocier" ne recréer pas une autre conversation si une existe déjà avec cette utilisateur sur ce produit

__**Scénario Test :**__
- Ajout d'un scénario test classique : inscription + connexion


## Pour se connecter

Si vous souhaitez utiliser déjà un compte existant, vous pouvez mettre :
utilisateur : f.t@gmail.com
password : 123456
