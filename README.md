# DUTCH

Sujet : LG1
Groupe : LG1A
Attention : le groupe LG1A et VR2D ont échangés dès le premier tp leurs groupe. Les étudiants aillant travaillés sur le projet sont [AUTEUR](./AUTHORS.md)

## Comment lancer le jeu

### Exécution

Pour installer et éxécuter DUTCH vous aurez besoin d'une installation de Java 22 minimum. Téléchargez la dernière version déployée sur le dépôt et ouvrez un terminal à l'endroit où vous avez installé le jeu. PLacez-vous à la racine du projet et éxécutez la commande suivante : 

#### Sur Linux/MacOS :

`nohup java -jar DUTCH.jar arg1 >/dev/null 2>&1 &`

#### Sur Windows :

`start javaw -jar DUTCH.jar arg1`

### Recompilation

Vous pouvez également compiler les fichiers sources vous-mêmes en vous placant à la racine du projet et en éxécutant les commandes suivantes :

#### Sur Linux/MacOS :

```bash
javac -encoding UTF-8 -d ./build $(find src -name "*.java")
jar cvfm DUTCH.jar MANIFEST.MF -C build/ . -C ./ README.md AUTHORS.md
nohup java -jar DUTCH.jar arg1 >/dev/null 2>&1 &
```

ou 

`./DUTCH.sh arg1`

#### Sur Windows :

```batch
powershell -Command "$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java'; & javac -encoding UTF-8 -d build $files.FullName;"
jar cvfm DUTCH.jar MANIFEST.MF -C build/ . -C ./ README.md AUTHORS.md
start javaw -jar DUTCH.jar arg1
```

ou

`.\DUTCH.bat arg1`

### Pour lancer un serveur en localhost

Vous pouvez enfin si vous le souhaitez ouvrir un serveur local en localhost (127.0.0.1) afin de jouer à plusieurs sur la même machine.
Pour cela lancer le jeu comme expliqué ci dessus mais avec l'argument "LOCAL" partout ou arg1 est indiqué.
Puis placez-vous à la racine du projet et éxécutez la commande suivante : 

#### Sur Linux/MacOS :

`./WEBSOCKETSERVER.sh`

#### Sur Windows :

`.\WEBSOCKETSERVER.bat`

### Serveur hébergé

Le serveur que nous avons utilisé pour héberger à la soutenance et après est présent à l'adresse 148.253.122.47, appartenant à Valentin. Si besoin de relancer le serveur vous pouvez lui envoyer un message ou utiliser ce [lien](https://discord.com/oauth2/authorize?client_id=1168593371837763634) d'invitation discord pour inviter un bot sur votre serveur, en envoyant la commande `!start`, il redémarera le serveur.

### En cas d'erreur

Si dans l'erreur sur un .sh on vous parle de \r, il faut utilise la commande `dos2unix fichier.sh` pour résoudre le problème.
