# Root-Cause
## Documentation des outils
### Git
#### Branches
Le git est organisé sous la forme suivante

![Git](https://www.bitbull.it/blog/git-flow-come-funziona/gitflow-1.png)

La branche `Main` (Master) est la branche la plus stable, elle sert de référence pour les versions (Tag) entièrement
fonctionnel et prêt à être déployer chez le client

Les Tags de la branche Main auront le format suivant (`1.0`,`2.0`,`3.0`, etc)

La branche `Dev` (Develop) a pour objectif de contenir les versions en cours de développement
mais doit rester stable, c'est la branche qui réunira toutes les features développé par l'équipe

Si une version de Dev est suffisamment avancée, il sera possible de définir un tag intermédiaire
pour tester le déploiement, ces tags auront la format suivant (`1.1`,`1.2`,`1.3`,`2.1`,`2.2`, etc)

Les branches `Features` auront pour objectif de développer les fonctionnalités

Quand une tache vous sera attribuée, vous pourrez créer une branche de feature à partir de dev

Le nom de la branche doit contenir vos initiales et le propos de la branche (Ex : `NB-React-Quarkus-Integration`)

Une fois sur votre branche features, vous avez tous les droits (possibilité de créer des sous-branches, etc)

Pour plus de sécurité, les branches `Main` et `Dev` sont protégés, il est donc impossible de push ou de force push, le seul
moyen d'ajouter du code est de passer par une merge request

#### Merge Request
Une fois que votre branche de feature est terminée, vous pouvez déclarer une merge request sur votre branche, cette merge
request doit être validé par au moins __2__ personnes avant d'etre merge avec `Dev`

####  Les commandes Git de base:

### Git fetch:
Git fetch permet de rechercher et afficher les changements sur un remote passé en argument, qui ne sont pas présent en local, sans aucun transfert de fichiers.
Exemple: 
La commande  git fetch dev  permet de chercher les nouveaux changement sur le remote dev qui ne sont pas présent en local.

### Git pull:
Git pull permet de récupérer tous les changements sur la branche distant.
Elle prend en paramètre la branche source et la branche ciblé.
Exemple:
La commande  git pull dev master  nous permet de récupérer les changement de la branche remote dev dans la branche master du remote origin.

### Git rebase:
Git rebase permet de transférer les changements d’une branche à une autre, elle prend en argument la branche avec laquelle nous voulons rebaser.
Exemple:
La commande  git rebase -i dev  permet de rebaser les commits de notre branche courant avec la branche remote dev.

### Git add: 
Git add permet d’ajouter les changements que nous avons fait dans nos fichiers sur la branche courante.
les deux arguments les plus importants de cette commande sont:
     git add .  : permet d’ajouter tous les changements que nous avons fait.
     git add -u  : permet d’ajouter les modifications sur les fichier déjà connus par git (les nouveaux fichiers ne seront pas ajoutés).

### Git commit:
Git commit permet commiter les modifications que nous avons en local sur la branche courante.
Exemples:
     git commit -m “Message de commit”  : permet de commiter les changements sur la branche courante, avec un message “Message de commit”.
     git commit –amend  : permet de commiter les nouveaux changements sur la branche courante dans la dernière commit que nous avons créé sur la branche.

### Git reset:
Git reset permet de supprimer tous les changements que nous avons fait sur la branche courante.
Exemples:
     git reset –hard HEAD  : permet de retourner vers la version que nous avons sur la branche master.
     git commit –amend  : permet de commiter les nouveaux changements sur la branche courante dans la dernière commit que nous avons créé sur la branche.

### Git push:
Git push permet d’envoyer les modifications sur un remote.
Exemple:
     git push origin branch-A  : permet d’envoyer les modifications que nous avons fait sur la branche branch-A vers notre repos origin.

### Git status:
     git status  : permet d’afficher toutes les modifications non commités sur la branche courante.

#### CI/CD
Lors d'une merge request, un pipeline lancera les tests maven et l'analyse SonarQube, si le pipeline échoue, la merge request
sera refusé
Si une branche est taggé ou publier sur `Main`, un pipeline déploiera la version sur le serveur
### SonarQube
L'interface de SonarQube est disponible à l'adresse suivante : http://192.168.4.102:9000

```
login: admin
password: rootcause
```

Pour faciliter l'intégration, vous pouvez utiliser le plugin intellij SonarLint

```
Adresse: 192.168.4.102:9000
Token: 367c44558b159bfb47ba8d0fb543ac076ee0c41c
```
### YouTrack
La gestion des taches et des tickets sont délégué à la plateforme YouTrack

Pour faciliter l'intégration, vous pouvez utiliser le plugin intellij YouTrack

```
Adresse: https://rootcause.myjetbrains.com
```
Pour le token : https://www.jetbrains.com/help/youtrack/incloud/Manage-Permanent-Token.html

### Architecture de dossier

```
.
├── DockerVolumes                   // Stockage des données relatives aux images docker
│   └── TimeScale
│       └── Setup
├── rootcausecore
│   ├── backend                     // Quarkus
│   ├── db                          // Insertion BDD
│   │   ├── dbinsertlogtoken        // Insertion des logs et tokens dans la BDD
│   │   └── dbinsertmonitoring      // Insertion des débits RabbitMQ dans la BDD
│   ├── modules                     // Modules du coeur de RootCause
│   │   ├── linking_synthetization  // Liaison-Synthétisation
│   │   └── tokenization            // Tokénisation
│   └── tools                       // Scripts permettant de tester la solution
│       └── benchmark               // Outils de benchmark de la solution
└── rootcausefront                  // Démonstrateur
```
