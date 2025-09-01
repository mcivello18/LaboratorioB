ISTRUZIONI PER L’INSTALLAZIONE:

1) Aprire pgAdmin (o altro strumento PostgreSQL) e creare un nuovo database chiamato "BookRecommender", utilizzando l’utente "postgres".

2) Aprire il prompt dei comandi, posizionarsi nella cartella principale del progetto (dove si trova il file pom.xml) ed eseguire il seguente comando:

"mvn clean compile -Ddb.user=postgres -Ddb.pass=Password".

(Sostituire "Password" con la propria password dell'utente "postgres", quella usata per accedere a pgAdmin.

Questo comando creerà e popolerà automaticamente le tabelle del database.)

ISTRUZIONI PER L’ESECUZIONE:

3) Aprire la cartella "bin" del progetto e fare doppio clic prima sul file:

_start-server.bat

e poi su:

_start-client.bat


REQUISITI DI SISTEMA:

- Java Development Kit (JDK) 21
- PostgreSQL installato (con database "BookRecommender" e utente "postgres")
- Connessione internet per scaricare le dipendenze al primo avvio (solo il primo mvn)

---

NOTE:

- Le librerie JavaFX necessarie sono già incluse nella cartella "lib", non serve installarle manualmente.
- I file .jar sono già compilati e presenti nella cartella "bin": non è necessario eseguire mvn package.