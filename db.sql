DROP DATABASE IF EXISTS MiniProjet;
CREATE DATABASE MiniProjet DEFAULT CHARACTER SET utf8mb4;
USE MiniProjet
CREATE TABLE Mot(
  Mot VARCHAR(50) not null
) DEFAULT CHARSET=utf8;

ALTER TABLE Mot
  ADD CONSTRAINT Uk_Mot UNIQUE(Mot)
;

LOAD DATA LOCAL INFILE 'C:/Users/guydo/Documents/Madjoh/verbe.txt' INTO TABLE Mot
LINES TERMINATED BY '\r\n';