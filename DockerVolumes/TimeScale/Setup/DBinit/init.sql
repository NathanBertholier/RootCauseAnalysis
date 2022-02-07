CREATE TABLE log(id bigint PRIMARY KEY,datetime timestamp NOT NULL);
CREATE TABLE rawlog(id bigint PRIMARY KEY,value text NOT NULL);
CREATE TABLE report(id bigint PRIMARY KEY,content text NOT NULL,proximity text NOT NULL);
CREATE TABLE token(id bigserial PRIMARY KEY,idlog bigint NOT NULL,idtokentype int NOT NULL,value text not null);
CREATE TABLE tokentype(id serial PRIMARY KEY,name varchar(20) NOT NULL);
INSERT INTO tokentype (name) values ('IPV4'),('IPV6'),('Statut'),('Datetime'),('EdgeResponse');

