DROP SCHEMA IF EXISTS public;
CREATE SCHEMA IF NOT EXISTS public;

CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE log(id bigserial PRIMARY KEY, datetime timestamp NOT NULL);
CREATE TABLE rawlog(id bigserial PRIMARY KEY, log text NOT NULL);
CREATE TABLE monitoring(datetime timestamp PRIMARY KEY, deliver bigint NOT NULL, publish bigint NOT NULL, avg_rate float NOT NULL);
CREATE TABLE report(id bigserial PRIMARY KEY, content text NOT NULL, proximity text NOT NULL);
CREATE TABLE token(id bigserial PRIMARY KEY, idlog bigint NOT NULL, idtokentype int NOT NULL, value text not null);
CREATE TABLE tokentype(id serial PRIMARY KEY, name varchar(20) NOT NULL);

INSERT INTO tokentype (name) values ('IPv4'),('IPv6'),('Statut'),('Datetime'),('EdgeResponse');