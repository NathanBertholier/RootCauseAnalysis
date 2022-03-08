# Root-Cause

## Installation

La manière la plus simple de lancer la solution est de cloner le repos et de lancer la commande `docker-compose up -d`
au niveau du fichier `docker-compose.yml`

## Variable d'environment

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

L'API RootCause propose 2 routes majeures et 3 routes destinées au démonstrateur
Le Swagger est disponible sur la route `/swagger` une fois la solution lancée
#### POST insertlog

Insertlog permet de faire ingérer des logs à l'API, la requéte HTTP doit soumettre les logs au format suivant :

```json
[
  {
    "log": "mon log"
  }
]
```

Le corps peut contenir plusieurs logs

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

#### POST tokens

#### GET tokentypes

#### GET link

### FrontEnd

## Divers

### Performances

| Spécification                            | Vitesse de tokénisation en log/seconde |
|------------------------------------------|----------------------------------------|
| 8Cœurs/16Threads à 3.8GHz, 16G DDR4@3600 | ~4000                                  |
| 4Cœurs/8Threads à 2.7GHz, 32G DDR4@2133  | ~2000                                  |
| 8vCœurs/8Threads à 2.6GHz, 8G DDR4@2133  | ~500                                   |

### Bugs identifiés