package bookrecommender;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
public class ServerSlave extends Thread {
	
	Socket s;
	ObjectOutputStream out;
	ObjectInputStream in;
	BRDatabaseManager b;
	
	public ServerSlave(Socket socket, BRDatabaseManager databasemanager) throws IOException{
		s=socket;
		out=new ObjectOutputStream(s.getOutputStream());
		in=new ObjectInputStream(s.getInputStream());
		b=databasemanager;
		start();
	}
	
	public void exec(String cmd) throws Exception{
		   switch (cmd) {
	        case "login": {
	            Utente creds = (Utente) in.readObject();
	            boolean ok = b.login(creds.getUserid(), creds.getPassword());
	            out.writeObject(ok);
	            break;
	        }
	        case "registrazione": {
	            Utente u = (Utente) in.readObject();
	            boolean ok = b.registraUtente(u);
	            out.writeObject(ok);
	            break;
	        }
	        case "creaLibreria": {
	            String userid = (String) in.readObject();
	            String nomeLibreria = (String) in.readObject();
	            @SuppressWarnings("unchecked")
	            List<String> titoli = (List<String>) in.readObject();

	            boolean success = b.creaLibreria(userid, nomeLibreria, titoli);
	            out.writeObject(success);  // true = creazione ok, false = libreria già esistente
	            break;
	        }
	        case "ricercaLibri": {
	            String keyword = (String) in.readObject();
	            int offset = (Integer) in.readObject(); 
	            int limit = (Integer) in.readObject();  

	            List<String[]> risultati = b.ricercaLibri(keyword, offset, limit);
	            out.writeObject(risultati);
	            break;
	        }
	        case "getLibrerieConLibriDiUtente":{
	        	   String userid = (String) in.readObject();  // leggi userid dal client
	        	    List<Libreria> librerieList = b.getLibrerieConLibriDiUtente(userid);
	        	    out.writeObject(librerieList);             // invia la lista di librerie + libri al client
	        	    out.flush();
	        		break;
	        }
	        
	        case "aggiungiValutazione": {
	            String utente = (String) in.readObject();
	            String titolo = (String) in.readObject();

	            int stile = (Integer) in.readObject();
	            String notaStile = (String) in.readObject();

	            int contenuto = (Integer) in.readObject();
	            String notaContenuto = (String) in.readObject();

	            int gradevolezza = (Integer) in.readObject();
	            String notaGradevolezza = (String) in.readObject();

	            int originalita = (Integer) in.readObject();
	            String notaOriginalita = (String) in.readObject();

	            int edizione = (Integer) in.readObject();
	            String notaEdizione = (String) in.readObject();

	            boolean successo = b.aggiungiValutazione(
	                utente, titolo,
	                stile, notaStile,
	                contenuto, notaContenuto,
	                gradevolezza, notaGradevolezza,
	                originalita, notaOriginalita,
	                edizione, notaEdizione
	            );

	            out.writeObject(successo);
	            break;
	        }
	        
	        case "consigliaLibri": {
	            String userid = (String) in.readObject();
	            String titoloPrincipale = (String) in.readObject();
	            @SuppressWarnings("unchecked")
	            List<String> libriConsigliati = (List<String>) in.readObject();

	            boolean esito = b.consigliaLibri(userid, titoloPrincipale, libriConsigliati);
	            out.writeObject(esito);
	            break;
	        }
	        
	        case "ricercaLibriPerAutore":{
	        	String autore = (String) in.readObject();
	            int offset = (Integer) in.readObject();
	            int limit = (Integer) in.readObject();

	            List<String[]> risultati = b.ricercaLibriPerAutore(autore, offset, limit);
	            out.writeObject(risultati);
	            break;
	        }
	        
	        case "ricercaLibriPerAutoreEAnno": {
	            String autore = (String) in.readObject();
	            int anno = (Integer) in.readObject();
	            int offset = (Integer) in.readObject();
	            int limit = (Integer) in.readObject();

	            List<String[]> risultati = b.ricercaLibriPerAutoreEAnno(autore, anno, offset, limit);
	            out.writeObject(risultati);
	            break;
	        }

	        case "getValutazioniLibro": {
	            String titolo = (String) in.readObject();
	            List<String[]> valutazioni = b.getValutazioniLibro(titolo);
	            out.writeObject(valutazioni);
	            break;
	        }

	        case "getUtentiCheConsigliano": {
	            String titolo = (String) in.readObject();
	            List<String> utenti = b.getUtentiCheConsigliano(titolo);
	            out.writeObject(utenti);
	            break;
	        }

	        case "getLibriConsigliatiPerLibro": {
	            String titolo = (String) in.readObject();
	            List<String[]> libriConsigliati = b.getLibriConsigliatiPerLibro(titolo);
	            out.writeObject(libriConsigliati);
	            break;
	        }

	        case "getCommentiLibro": {
	            String titolo = (String) in.readObject();
	            List<String[]> commenti = b.getCommentiLibro(titolo);
	            out.writeObject(commenti);
	            break;
	        }

	        case "getStatisticheAggregate": {
	            String titolo = (String) in.readObject();
	            String[] statistiche = b.getStatisticheAggregate(titolo);
	            out.writeObject(statistiche);
	            break;
	        }
	        	
	        default:
	            out.writeObject("Comando non riconosciuto");
	    }	  
		//tutti i metodi di BRDatabaseManager
	}
	
	public void run() {
		try {
			String cmd="";
			boolean finito=false;
			while(finito==false) {
				cmd=(String)in.readObject();
				if(cmd.equals("END")) {
					finito=true;
				}else {
					try {
						exec(cmd);
					}catch(Exception e) {
						e.printStackTrace();
						out.writeObject("Errore durante l'esecuzione del comando: " + e.getMessage());
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				s.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
