# Root-Cause
## Installation
La manière la plus simple de lancer la solution est de cloner le repos et de lancer la commande `docker-compose up -d` au
niveau du fichier `docker-compose.yml`

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
- RabbitMQ : Pour échanger des messages entre les différents composants et tenir une congestion lors des pics
- TimeScale : Pour Stocker les données de l'application, TimeScale est une surcouche de PostgreSQL qui optimise les requétes basé sur un timestamp
- Grafana : Pour visualiser les métriques de l'application
- NGinx : Pour permettre la connection en HTTPS
- Rootcause : Pour répondre aux appels API et effectuer la majorité des calculs
## Fonctionnement de la solution

### Calculs de proximités

Les calculs entre deux tokens sont un score sur 100.
Ces différents calculs sont implémentés dans les différents TokenTypes.
Ils se trouvent à l'adresse suivante : rootcause/core/src/main/java/fr.uge.modules/linking/token.type
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