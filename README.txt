CREATE TABLE Libri(
  Titolo VARCHAR(500) NOT NULL,
  Autore VARCHAR(500) NOT NULL,
  AnnoDiPubblicazione INTEGER
);

(Popolare la tabella eseguendo il file libri.sql presente nella cartella lib)

DELETE FROM Libri WHERE titolo in (SELECT titolo FROM Libri GROUP BY titolo HAVING count(*)>=2)

ALTER TABLE Libri
ADD CONSTRAINT pk_titolo PRIMARY KEY(titolo);

CREATE TABLE UtentiRegistrati(
  userid VARCHAR(50) PRIMARY KEY,
  nome VARCHAR(50) NOT NULL,
  cognome VARCHAR(50) NOT NULL,
  codice_fiscale CHAR(16) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL
);

CREATE TABLE Librerie (
  utente VARCHAR(50) NOT NULL,
  nomeLibreria VARCHAR(100) NOT NULL,
  titolo VARCHAR(500) NOT NULL,
  PRIMARY KEY (utente, nomeLibreria, titolo),
  FOREIGN KEY(utente) REFERENCES UtentiRegistrati(userid),
  FOREIGN KEY(titolo) REFERENCES Libri(titolo)
)

CREATE TABLE ValutazioniLibri(
  utente VARCHAR(50) NOT NULL,
  titolo VARCHAR(500) NOT NULL,
  stile INT CHECK (stile BETWEEN 1 AND 5),
  nota_stile VARCHAR(256),
  contenuto INT CHECK (contenuto BETWEEN 1 AND 5),
  nota_contenuto VARCHAR(256),
  gradevolezza INT CHECK (gradevolezza BETWEEN 1 AND 5),
  nota_gradevolezza VARCHAR(256),
  originalita INT CHECK (originalita BETWEEN 1 AND 5),
  nota_originalita VARCHAR(256),
  edizione INT CHECK (edizione BETWEEN 1 AND 5),
  nota_edizione VARCHAR(256),
  votoFinale DECIMAL(2,3),
  PRIMARY KEY(utente, titolo),
  FOREIGN KEY(utente) REFERENCES UtentiRegistrati(userid),
  FOREIGN KEY(titolo) REFERENCES Libri(Titolo)
);

CREATE TABLE ConsigliLibri(
  userid VARCHAR(50) NOT NULL,
  titoloLibro VARCHAR(500) NOT NULL,
  libroConsigliato VARCHAR(500) NOT NULL,
  PRIMARY KEY (userid, titoloLibro, libroConsigliato),
  FOREIGN KEY (userid) REFERENCES UtentiRegistrati(userid),
  FOREIGN KEY (titoloLibro) REFERENCES Libri(Titolo),
  FOREIGN KEY (libroConsigliato) REFERENCES Libri(Titolo)
);


COMANDI MAVEN PER AVVIARE IL PROGRAMMA:
1)Avviare il server: tasto destro sul progetto; run as->maven build; inserire il comando "exec:java@run-server";
2)Avviare il client: tasto destro sul progetto; run as->maven build; inserire il comando "exec:java@run-client";