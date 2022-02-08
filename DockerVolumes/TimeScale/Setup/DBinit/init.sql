CREATE TABLE log(id bigint PRIMARY KEY,datetime timestamp NOT NULL);
CREATE TABLE rawlogt(id bigint PRIMARY KEY);
CREATE TABLE rawlog(id bigint PRIMARY KEY,value text NOT NULL);
CREATE TABLE monitoring(datetime timestamp PRIMARY KEY, ingressRate bigint NOT NULL, egressRate bigint NOT NULL,queuedMessages bigint NOT NULL,messagePoolBytes bigint NOT NULL);
CREATE TABLE report(id bigint PRIMARY KEY,content text NOT NULL,proximity text NOT NULL);
CREATE TABLE token(id bigserial PRIMARY KEY,idlog bigint NOT NULL,idtokentype int NOT NULL,value text not null);
CREATE TABLE tokentype(id serial PRIMARY KEY,name varchar(20) NOT NULL);

INSERT INTO tokentype (name) values ('IPv4'),('IPv6'),('Statut'),('Datetime'),('EdgeResponse');

