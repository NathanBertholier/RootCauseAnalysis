# Root-Cause

## Installation

La manière la plus simple de lancer la solution est de cloner le repos et de lancer la commande `docker-compose up -d`
au niveau du fichier `docker-compose.yml`

## Variable d'environment
Les variables d'environnement sont accessible et modifiable dans le fichier [.env](.env) a la racine.

|  **_Nom de Variable_** |             **_Description_**            | **_Type_** | **_Valeur par défaut_** |
|:----------------------:|:----------------------------------------:|:----------:|:-----------------------:|
|     REPORT_EXPANDED    | Génération du rapport au format expanded |   Boolean  |          FALSE          |
|      REPORT_DELTA      |           Delta de temps en ms           |   Integer  |           7200          |
| REPORT_PROXIMITY_LIMIT |        Indice minimum de proximité       |   Integer  |            50           |
|   REPORT_NETWORK_SIZE  |             Taille du réseau             |   Integer  |            15           |
|      QUARKUS_PORT      |          Port du serveur Quarkus         |   Integer  |           8081          |
|         PROFILE        |        Type de log (AWS, standard)       |   String   |           AWS           |
## Docker

La solution utilise les images suivantes:

- `RabbitMQ` : Pour échanger des messages entre les différents composants et tenir une congestion lors des pics
- `TimeScale` : Pour Stocker les données de l'application, TimeScale est une surcouche de PostgreSQL qui optimise les
  requétes basé sur un timestamp
- `Grafana` : Pour visualiser les métriques de l'application
- `NGinx` : Pour permettre la connection en HTTPS
- `Rootcause` : Pour répondre aux appels API et effectuer la majorité des calculs
- `Tokenisation` : Pour insérer en batch les logs tokénisé

## Fonctionnement de la solution

### API

L'API RootCause propose 2 routes majeures et 3 routes destinées au démonstrateur Le Swagger est disponible sur la
route `/swagger` une fois la solution lancée

#### POST insertlog

Insertlog permet de faire ingérer des logs à l'API, la requéte HTTP doit soumettre les logs au format suivant :

```json
[
  {
    "log": "mon log"
  }
]
```

Le corps peut contenir plusieurs logs, la taille maximum d'envoi est de 10240K (elle peut étre augmenter en modifiant la propriété `quarkus.http.limits.max-body-size` [ici](core/src/main/resources/application.properties))

```json
[
  {
    "log": "mon 1er log"
  },
  {
    "log": "mon 2nd log"
  }
]
```

Pour que la solution puisse tokénisé les logs, les champs du log doivent étre séparé par des caractères d'espacement (
espace, tabulation, etc)

Format correct

```json
[
  {
    "log": "2021-10-15 12:49:50 CDGVD450 6542 83.251.190.180 GET serveur.net /wp-includes/css/dist/block-library/style.min.css.gzip 200"
  }
]
```

Format incorrect

```json
[
  {
    "log": "2021-10-15;12:49:50;CDGVD450;6542;83.251.190.180;GET;serveur.net;/wp-includes/css/dist/block-library/style.min.css.gzip;200"
  }
]
```

Une fois les logs soumis, la solution renvoi une liste d'id associant chaque log soumis avec un id unique

```json
[
  1,
  2,
  3,
  4
]
```

#### GET report

La route report permet de générer un rapport basé sur un log donné dans le chemin de la requéte API et indiquant une
root cause et un ensemble de logs liée au log pris en paramètre

Si on cherche à générer un rapport pour le log 31, ont envoi la requéte `/report/31` et on reçoit une réponse dans le
format suivant :

```json
{
  "root": {
    "id": "id du log root cause",
    "content": "corps du log",
    "datetime": "date time du log"
  },
  "target": {
    "id": "id du log target",
    "content": "corps du log",
    "datetime": "date time du log"
  },
  "tokens": [
    {
      "name": "type du token",
      "value": "tableau de valeur les plus citée du token",
      "count": "nombre de citation de la valeur"
    }
  ],
  "logs": [
    {
      "id": "id du log",
      "content": "corps du log",
      "datetime": "date time du log"
    }
  ]
}
```

Il existe 3 options pour créer un rapport qui seront transmis comme paramètre de la requéte :

- `delta` : valeur en seconde indiquant l'intervalle de recherche (Ex : si l'intervalle est de 3600 et que le datetime
  du log cible est 2020-01-01 12:00:00.000000, le système ne comparera que les logs ayant un datetime compris entre 11:
  00:00.000000 et 12:00:00.000000)
- `expanded` : valeur boolean permettant de générer une liste d'adjacence des liens
- `network_size` : valeur numérique indiquant le nombre de logs disponible dans le rapport
- `proximity_limit` : valeurs de proximité basses prises en compte dans le rapport (seul les valeurs de proxitmité
  supérieur ou égale à la proximity limit sont prisent en comptes dans le rapport)

Si la valeur expanded est à true, le rapport a le format suivant :

```json
{
  "root": {
    "id": "id du log root cause",
    "content": "corps du log",
    "datetime": "date time du log"
  },
  "target": {
    "id": "id du log target",
    "content": "corps du log",
    "datetime": "date time du log"
  },
  "tokens": [
    {
      "name": "type du token",
      "value": "tableau de valeur les plus citée du token",
      "count": "nombre de citation de la valeur"
    }
  ],
  "logs": [
    {
      "id": "id du log",
      "content": "corps du log",
      "datetime": "date time du log"
    }
  ],
  "proximity": [
    {
      "id": "id du log parent",
      "links": [
        {
          "id": "id du log enfant",
          "proximity": "valeur de proximité entre le parent et l’enfant"
        }
      ]
    }
  ]
}
```
### Calculs de proximités

Les calculs entre deux tokens sont un score sur 100.
Ces différents calculs sont implémentés dans les différents TokenTypes.
Ils se trouvent [Ici](core/src/main/java/fr/uge/modules/linking/token/type)

Si un log contient plusieurs fois un token (plusieurs IP par exemple), nous effectuerons une moyenne sur les 
valeurs obtenues.

#### DateTime

Proximité de 0 à 100 entre deux tokens datetime.
Plus les datetime sont proches par rapport au delta plus la proximité est forte.
Calcul : (1 - ((datetime2 - datetime1) / delta)) x 100.

#### IPv4

Calcul en fonction des blocs identiques sur deux IP.

|      **_Nombre de blocs_**      |     **_Résultat_**      |
|:-------------------------------:|:-----------------------:|
|       1er bloc en commun        |           20            |
|    2 premiers blocs en commun   |           85            |
|    3 premiers blocs en commun   |           95            |
|           Identiques            |          100            |
|      Complètement différent     |            0            |

#### IPv6

En vue du format des IPv6 nous avons fait le choix de donner un score de 100 pour deux IPv6 identiques et O sinon.

#### Status

Si deux tokens Status sont égaux alors nous renverrons une proximité de 100.
Ensuite en fonction cela va dépendre du premier caractère du code de retour HTTP.
S'ils sont égaux, nous renverrons 95.
S'ils sont différents, mais correspondent tous deux à un code d'erreur (commençant par 4 ou 5) nous renverrons 90.
Puis si l'un des deux commencent par un 4 ou 5 alors nous renverrons 25, dans la mesure ou si l'un deux fait référence à une erreur,
nous ne pouvons lui donner un score nul.
Dans tous les autres cas, nous renverrons un score de 0.

#### EdgeResponse

L'EdgeResponse, champ apparent dans les logs AWS, peut avoir pour valeur des champs inclus dans un ensemble de mots-clés,
que nous avons séparés en tant que mots-clés d'erreur et de mots-clés neutre.
De là nous donnons les résultats suivants : 

|               **_Champs_**                 |     **_Résultat_**      |
|:------------------------------------------:|:-----------------------:|
|      Les deux champs sont identiques       |          100            |
| Les deux champs correspondent à une erreur |           95            |
|L'un des deux champs correspond à une erreur|           25            |
|   Les deux champs ne sont pas une erreur   |            0            |


#### Resource

Le token resource est la resource demandée dans le log web.
Nous comparons les deux resources et voici le calcul effectué :
(nombre d'éléments en commun / minimum(nombre d'éléments resource 1, nombre d'éléments resource 2)) * 100.

## Divers

### Performances

| Spécification                            | Vitesse de tokénisation en log/seconde |
|------------------------------------------|----------------------------------------|
| 8Cœurs/16Threads à 3.8GHz, 16G DDR4@3600 | ~4000                                  |
| 4Cœurs/8Threads à 2.7GHz, 32G DDR4@2133  | ~2000                                  |
| 8vCœurs/8Threads à 2.6GHz, 8G DDR4@2133  | ~500                                   |

### Bugs identifiés
- Sur Linux, le dossier DockerVolumes semble avoir des problèmes de droits, si on cherche à relancer les dockers apres 
qu'ils aient écrits dans les volumes ->  `sudo chown -R $USER DockerVolumes/` pour régler le problème