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

## Divers
### Performances

| Spécification                            | Vitesse de tokénisation en log/seconde |
|------------------------------------------|----------------------------------------|
| 8Cœurs/16Threads à 3.8GHz, 16G DDR4@3600 | ~4000                                  |
| 4Cœurs/8Threads à 2.7GHz, 32G DDR4@2133  | ~2000                                  |
| 8vCœurs/8Threads à 2.6GHz, 8G DDR4@2133  | ~500                                   |

### Bugs identifiés