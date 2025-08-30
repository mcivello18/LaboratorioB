package bookrecommender;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
public class BRDatabaseManager {
	   private Connection conn;

	    public BRDatabaseManager(DBConfigurazione config) throws SQLException, ClassNotFoundException {
	        
	        
	        String url = "jdbc:postgresql://localhost:5432/BookRecommender";
	        conn = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
	    }

	    
	    public synchronized boolean existsUserid(String userid) throws SQLException {
	        String sql = "SELECT 1 FROM UtentiRegistrati WHERE userid = ?";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, userid);
	            try (ResultSet rs = pst.executeQuery()) {
	                return rs.next();
	            }
	        }
	    }

	    
	    public synchronized boolean existsEmail(String email) throws SQLException {
	        String sql = "SELECT 1 FROM UtentiRegistrati WHERE email = ?";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, email);
	            try (ResultSet rs = pst.executeQuery()) {
	                return rs.next();
	            }
	        }
	    }

	    
	    public synchronized boolean registraUtente(Utente u) throws SQLException {
	        if (existsUserid(u.getUserid()) || existsEmail(u.getEmail())) {
	            return false; // già presente userid o email
	        }
	        String sql = "INSERT INTO UtentiRegistrati (nome, cognome, codice_fiscale, email, userid, password) VALUES (?, ?, ?, ?, ?, ?)";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, u.getNome());
	            pst.setString(2, u.getCognome());
	            pst.setString(3, u.getCodiceFiscale());
	            pst.setString(4, u.getEmail());
	            pst.setString(5, u.getUserid());
	            pst.setString(6, u.getPassword());
	            int rows = pst.executeUpdate();
	            return rows == 1;
	        }
	    }

	    
	    public synchronized boolean login(String userid, String password) throws SQLException {
	        String sql = "SELECT password FROM UtentiRegistrati WHERE userid = ?";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, userid);
	            try (ResultSet rs = pst.executeQuery()) {
	                if (rs.next()) {
	                    String pwdFromDb = rs.getString("password");
	                    return pwdFromDb.equals(password);  
	                } else {
	                    return false; // userid non trovato
	                }
	            }
	        }
	    }

	    // Chiude la connessione (da chiamare alla fine)
	    public void close() throws SQLException {
	        if (conn != null && !conn.isClosed()) {
	            conn.close();
	        }
	    }
	    
	    public synchronized List<String[]> ricercaLibri(String keyword, int offset, int limit) throws SQLException {
	        List<String[]> risultati = new ArrayList<>();
	        String sql = "SELECT titolo, autore, annoDiPubblicazione " +
	                     "FROM Libri WHERE LOWER(titolo) LIKE ? " +
	                     "ORDER BY titolo " +
	                     "OFFSET ? LIMIT ?";
	        
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, "%" + keyword.toLowerCase() + "%");
	            pst.setInt(2, offset);
	            pst.setInt(3, limit);

	            try (ResultSet rs = pst.executeQuery()) {
	                while (rs.next()) {
	                    String[] libro = new String[3];
	                    libro[0] = rs.getString("titolo");
	                    libro[1] = rs.getString("autore");
	                    libro[2] = rs.getString("annoDiPubblicazione");
	                    risultati.add(libro);
	                }
	            }
	        }
	        return risultati;
	    }
	    
	    public synchronized boolean creaLibreria(String userid, String nomeLibreria, List<String> titoli) throws SQLException {
	        if (esisteLibreria(userid, nomeLibreria)) {
	            return false;  
	        }

	        String sql = "INSERT INTO Librerie (utente, nomeLibreria, titolo) VALUES (?, ?, ?)";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            for (String titolo : titoli) {
	                pst.setString(1, userid);
	                pst.setString(2, nomeLibreria);
	                pst.setString(3, titolo);
	                pst.addBatch();
	            }
	            pst.executeBatch();
	            return true;
	        }
	    }
	    
	    public synchronized boolean esisteLibreria(String userid, String nomeLibreria) throws SQLException {
	        String sql = "SELECT 1 FROM Librerie WHERE utente = ? AND nomeLibreria = ? LIMIT 1";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, userid);
	            pst.setString(2, nomeLibreria);
	            try (ResultSet rs = pst.executeQuery()) {
	                return rs.next();  // true se esiste almeno una riga
	            }
	        }
	    }
	    
	    public synchronized List<Libreria> getLibrerieConLibriDiUtente(String userid) throws SQLException {
	        String query = "SELECT nomeLibreria, titolo FROM Librerie WHERE utente = ?";
	        List<Libreria> librerieList = new ArrayList<>();

	        try (PreparedStatement ps = conn.prepareStatement(query)) {
	            ps.setString(1, userid);
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    String nomeLibreria = rs.getString("nomeLibreria");
	                    String titolo = rs.getString("titolo");

	                    // Cerca se la libreria è già nella lista
	                    Libreria libreria = null;
	                    for (Libreria l : librerieList) {
	                        if (l.getNomeLibreria().equals(nomeLibreria)) {
	                            libreria = l;
	                            break;
	                        }
	                    }

	                    // Se non c'è, la creo e la aggiungo
	                    if (libreria == null) {
	                        libreria = new Libreria(nomeLibreria);
	                        librerieList.add(libreria);
	                    }

	                    // Aggiungo il titolo
	                    libreria.addTitolo(titolo);
	                }
	            }
	        }

	        return librerieList;
	    }
	    
	    public synchronized boolean aggiungiValutazione(
	    	    String utente, String titolo,
	    	    int stile, String notaStile,
	    	    int contenuto, String notaContenuto,
	    	    int gradevolezza, String notaGradevolezza,
	    	    int originalita, String notaOriginalita,
	    	    int edizione, String notaEdizione
	    	) throws SQLException {

	    	    if (valutazioneEsistente(utente, titolo)) return false;

	    	    if (!isValutazioneValida(stile, contenuto, gradevolezza, originalita, edizione)) return false;

	    	    if (!isNotaValida(notaStile) || !isNotaValida(notaContenuto) ||
	    	        !isNotaValida(notaGradevolezza) || !isNotaValida(notaOriginalita) ||
	    	        !isNotaValida(notaEdizione)) {
	    	        return false;
	    	    }

	    	    double votoFinale = (stile + contenuto + gradevolezza + originalita + edizione) / 5.0;

	    	    String sql = "INSERT INTO ValutazioniLibri (utente, titolo, stile, nota_stile, contenuto, nota_contenuto, " +
	    	                 "gradevolezza, nota_gradevolezza, originalita, nota_originalita, edizione, nota_edizione, votoFinale) " +
	    	                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    	    try (PreparedStatement pst = conn.prepareStatement(sql)) {
	    	        pst.setString(1, utente);
	    	        pst.setString(2, titolo);

	    	        pst.setInt(3, stile);
	    	        pst.setString(4, notaStile);

	    	        pst.setInt(5, contenuto);
	    	        pst.setString(6, notaContenuto);

	    	        pst.setInt(7, gradevolezza);
	    	        pst.setString(8, notaGradevolezza);

	    	        pst.setInt(9, originalita);
	    	        pst.setString(10, notaOriginalita);

	    	        pst.setInt(11, edizione);
	    	        pst.setString(12, notaEdizione);

	    	        pst.setDouble(13, votoFinale);

	    	        return pst.executeUpdate() == 1;
	    	    }
	    	}
	    
	    public synchronized boolean valutazioneEsistente(String utente, String titolo) throws SQLException {
	        String sql = "SELECT 1 FROM ValutazioniLibri WHERE utente = ? AND titolo = ?";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, utente);
	            pst.setString(2, titolo);
	            try (ResultSet rs = pst.executeQuery()) {
	                return rs.next();  
	            }
	        }
	    }
	    
	    public boolean isValutazioneValida(int stile, int contenuto, int gradevolezza, int originalita, int edizione) {
	        return isVotoValido(stile) &&
	               isVotoValido(contenuto) &&
	               isVotoValido(gradevolezza) &&
	               isVotoValido(originalita) &&
	               isVotoValido(edizione);
	    }

	    private boolean isVotoValido(int voto) {
	        return voto >= 1 && voto <= 5;
	    }
	    
	    private boolean isNotaValida(String nota) {
	        return nota == null || nota.length() <= 256;
	    }
	    
	    public synchronized boolean consigliaLibri(String userid, String titoloPrincipale, List<String> consigliati) throws SQLException {
	        // Verifica se l’utente ha già consigliato libri per questo titolo
	        String checkSql = "SELECT 1 FROM ConsigliLibri WHERE userid = ? AND titoloLibro = ? LIMIT 1";
	        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
	            check.setString(1, userid);
	            check.setString(2, titoloPrincipale);
	            try (ResultSet rs = check.executeQuery()) {
	                if (rs.next()) {
	                    return false;  // già esiste un consiglio per questo libro da parte di questo utente
	                }
	            }
	        }

	        String insertSql = "INSERT INTO ConsigliLibri (userid, titoloLibro, libroConsigliato) VALUES (?, ?, ?)";
	        try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
	            for (String titolo : consigliati) {
	                pst.setString(1, userid);
	                pst.setString(2, titoloPrincipale);
	                pst.setString(3, titolo);
	                pst.addBatch();
	            }
	            pst.executeBatch();
	        }

	        return true;
	    }
	    
	    public synchronized List<String[]> ricercaLibriPerAutore(String autore, int offset, int limit) throws SQLException {
	        List<String[]> risultati = new ArrayList<>();
	        String sql = "SELECT titolo, autore, annoDiPubblicazione " +
	                     "FROM Libri WHERE LOWER(autore) LIKE ? " +
	                     "ORDER BY titolo " +
	                     "OFFSET ? LIMIT ?";

	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, "%" + autore.toLowerCase() + "%");
	            pst.setInt(2, offset);
	            pst.setInt(3, limit);

	            try (ResultSet rs = pst.executeQuery()) {
	                while (rs.next()) {
	                    String[] libro = new String[3];
	                    libro[0] = rs.getString("titolo");
	                    libro[1] = rs.getString("autore");
	                    libro[2] = rs.getString("annoDiPubblicazione");
	                    risultati.add(libro);
	                }
	            }
	        }
	        return risultati;
	    }
	    
	    public synchronized List<String[]> ricercaLibriPerAutoreEAnno(String autore, int anno, int offset, int limit) throws SQLException {
	        List<String[]> risultati = new ArrayList<>();
	        String sql = "SELECT titolo, autore, annoDiPubblicazione " +
	                     "FROM Libri WHERE LOWER(autore) LIKE ? AND annoDiPubblicazione = ? " +
	                     "ORDER BY titolo " +
	                     "OFFSET ? LIMIT ?";

	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, "%" + autore.toLowerCase() + "%");
	            pst.setInt(2, anno);
	            pst.setInt(3, offset);
	            pst.setInt(4, limit);

	            try (ResultSet rs = pst.executeQuery()) {
	                while (rs.next()) {
	                    String[] libro = new String[3];
	                    libro[0] = rs.getString("titolo");
	                    libro[1] = rs.getString("autore");
	                    libro[2] = rs.getString("annoDiPubblicazione");
	                    risultati.add(libro);
	                }
	            }
	        }
	        return risultati;
	    }

	    
	    public synchronized List<String[]> getValutazioniLibro(String titolo) throws SQLException {
	        List<String[]> results = new ArrayList<>();
	        String sql = "SELECT utente, stile, nota_stile, contenuto, nota_contenuto, gradevolezza, nota_gradevolezza, " +
	                     "originalita, nota_originalita, edizione, nota_edizione, votoFinale " +
	                     "FROM ValutazioniLibri WHERE titolo = ?";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, titolo);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                String[] row = new String[12];
	                row[0] = rs.getString("utente");
	                row[1] = String.valueOf(rs.getInt("stile"));
	                row[2] = rs.getString("nota_stile");
	                row[3] = String.valueOf(rs.getInt("contenuto"));
	                row[4] = rs.getString("nota_contenuto");
	                row[5] = String.valueOf(rs.getInt("gradevolezza"));
	                row[6] = rs.getString("nota_gradevolezza");
	                row[7] = String.valueOf(rs.getInt("originalita"));
	                row[8] = rs.getString("nota_originalita");
	                row[9] = String.valueOf(rs.getInt("edizione"));
	                row[10] = rs.getString("nota_edizione");
	                row[11] = rs.getString("votoFinale");
	                results.add(row);
	            }
	        }
	        return results;
	    }

	    
	    public synchronized List<String> getUtentiCheConsigliano(String titoloLibro) throws SQLException {
	        List<String> results = new ArrayList<>();
	        String sql = "SELECT DISTINCT userid FROM ConsigliLibri WHERE libroConsigliato = ?";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, titoloLibro);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                results.add(rs.getString("userid"));
	            }
	        }
	        return results;
	    }

	    
	    public synchronized List<String[]> getLibriConsigliatiPerLibro(String titoloLibro) throws SQLException {
	        List<String[]> results = new ArrayList<>();
	        String sql = "SELECT userid, libroConsigliato FROM ConsigliLibri WHERE titoloLibro = ?";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, titoloLibro);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                String[] row = new String[2];
	                row[0] = rs.getString("userid");
	                row[1] = rs.getString("libroConsigliato");
	                results.add(row);
	            }
	        }
	        return results;
	    }

	   
	    public synchronized List<String[]> getCommentiLibro(String titoloLibro) throws SQLException {
	        List<String[]> results = new ArrayList<>();
	        String sql = "SELECT utente, nota_stile, nota_contenuto, nota_gradevolezza, nota_originalita, nota_edizione " +
	                     "FROM ValutazioniLibri WHERE titolo = ?";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, titoloLibro);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                String userid = rs.getString("utente");

	                String ns = rs.getString("nota_stile");
	                if (ns != null && !ns.trim().isEmpty()) results.add(new String[]{userid, "stile", ns});

	                String nc = rs.getString("nota_contenuto");
	                if (nc != null && !nc.trim().isEmpty()) results.add(new String[]{userid, "contenuto", nc});

	                String ng = rs.getString("nota_gradevolezza");
	                if (ng != null && !ng.trim().isEmpty()) results.add(new String[]{userid, "gradevolezza", ng});

	                String no = rs.getString("nota_originalita");
	                if (no != null && !no.trim().isEmpty()) results.add(new String[]{userid, "originalita", no});

	                String ne = rs.getString("nota_edizione");
	                if (ne != null && !ne.trim().isEmpty()) results.add(new String[]{userid, "edizione", ne});
	            }
	        }
	        return results;
	    }

	    
	    public synchronized String[] getStatisticheAggregate(String titoloLibro) throws SQLException {
	        String sqlValutazioni = "SELECT COUNT(*) as numValutazioni, " +
	                                "AVG(stile) as media_stile, AVG(contenuto) as media_contenuto, " +
	                                "AVG(gradevolezza) as media_gradevolezza, AVG(originalita) as media_originalita, " +
	                                "AVG(edizione) as media_edizione " +
	                                "FROM ValutazioniLibri WHERE titolo = ?";

	        String sqlUtentiConsigliano = "SELECT COUNT(DISTINCT userid) as numUtenti FROM ConsigliLibri WHERE libroConsigliato = ?";

	        String sqlNote = "SELECT COUNT(*) as numNote FROM ValutazioniLibri WHERE titolo = ? AND " +
	                         "(" +
	                         "nota_stile IS NOT NULL AND TRIM(nota_stile) <> '' OR " +
	                         "nota_contenuto IS NOT NULL AND TRIM(nota_contenuto) <> '' OR " +
	                         "nota_gradevolezza IS NOT NULL AND TRIM(nota_gradevolezza) <> '' OR " +
	                         "nota_originalita IS NOT NULL AND TRIM(nota_originalita) <> '' OR " +
	                         "nota_edizione IS NOT NULL AND TRIM(nota_edizione) <> ''" +
	                         ")";

	        int numValutazioni = 0;
	        double media_stile = 0, media_contenuto = 0, media_gradevolezza = 0, media_originalita = 0, media_edizione = 0;
	        int numUtentiConsigliano = 0;
	        int numNote = 0;

	        try (PreparedStatement stmtVal = conn.prepareStatement(sqlValutazioni)) {
	            stmtVal.setString(1, titoloLibro);
	            ResultSet rsVal = stmtVal.executeQuery();
	            if (rsVal.next()) {
	                numValutazioni = rsVal.getInt("numValutazioni");
	                media_stile = rsVal.getDouble("media_stile");
	                media_contenuto = rsVal.getDouble("media_contenuto");
	                media_gradevolezza = rsVal.getDouble("media_gradevolezza");
	                media_originalita = rsVal.getDouble("media_originalita");
	                media_edizione = rsVal.getDouble("media_edizione");
	            }
	        }

	        try (PreparedStatement stmtCons = conn.prepareStatement(sqlUtentiConsigliano)) {
	            stmtCons.setString(1, titoloLibro);
	            ResultSet rsCons = stmtCons.executeQuery();
	            if (rsCons.next()) {
	                numUtentiConsigliano = rsCons.getInt("numUtenti");
	            }
	        }

	        try (PreparedStatement stmtNote = conn.prepareStatement(sqlNote)) {
	            stmtNote.setString(1, titoloLibro);
	            ResultSet rsNote = stmtNote.executeQuery();
	            if (rsNote.next()) {
	                numNote = rsNote.getInt("numNote");
	            }
	        }

	        return new String[]{
	            String.valueOf(numValutazioni),
	            String.format("%.2f", media_stile),
	            String.format("%.2f", media_contenuto),
	            String.format("%.2f", media_gradevolezza),
	            String.format("%.2f", media_originalita),
	            String.format("%.2f", media_edizione),
	            String.valueOf(numUtentiConsigliano),
	            String.valueOf(numNote)
	        };
	    }
	    
	    public synchronized boolean modificaValutazione(
	            String utente, String titolo,
	            int stile, String notaStile,
	            int contenuto, String notaContenuto,
	            int gradevolezza, String notaGradevolezza,
	            int originalita, String notaOriginalita,
	            int edizione, String notaEdizione
	    ) throws SQLException {
	        // Verifica se la valutazione esiste già
	        if (!valutazioneEsistente(utente, titolo)) {
	            return false;  // Non esiste → non si può modificare
	        }

	        // Controlla validità dei voti e delle note
	        if (!isValutazioneValida(stile, contenuto, gradevolezza, originalita, edizione)) {
	            return false;
	        }
	        if (!isNotaValida(notaStile) || !isNotaValida(notaContenuto) ||
	            !isNotaValida(notaGradevolezza) || !isNotaValida(notaOriginalita) ||
	            !isNotaValida(notaEdizione)) {
	            return false;
	        }

	        double votoFinale = (stile + contenuto + gradevolezza + originalita + edizione) / 5.0;

	        String sql = "UPDATE ValutazioniLibri SET " +
	                     "stile = ?, nota_stile = ?, " +
	                     "contenuto = ?, nota_contenuto = ?, " +
	                     "gradevolezza = ?, nota_gradevolezza = ?, " +
	                     "originalita = ?, nota_originalita = ?, " +
	                     "edizione = ?, nota_edizione = ?, " +
	                     "votoFinale = ? " +
	                     "WHERE utente = ? AND titolo = ?";

	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setInt(1, stile);
	            pst.setString(2, notaStile);

	            pst.setInt(3, contenuto);
	            pst.setString(4, notaContenuto);

	            pst.setInt(5, gradevolezza);
	            pst.setString(6, notaGradevolezza);

	            pst.setInt(7, originalita);
	            pst.setString(8, notaOriginalita);

	            pst.setInt(9, edizione);
	            pst.setString(10, notaEdizione);

	            pst.setDouble(11, votoFinale);

	            pst.setString(12, utente);
	            pst.setString(13, titolo);

	            return pst.executeUpdate() == 1;
	        }
	    }
	    
	    
	    public synchronized boolean modificaConsigliLibri(String userid, String titoloPrincipale, List<String> nuoviConsigliati) throws SQLException {
	        // Verifica che esistano consigli precedenti per questo libro da parte dell’utente
	        String checkSql = "SELECT 1 FROM ConsigliLibri WHERE userid = ? AND titoloLibro = ? LIMIT 1";
	        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
	            check.setString(1, userid);
	            check.setString(2, titoloPrincipale);
	            try (ResultSet rs = check.executeQuery()) {
	                if (!rs.next()) {
	                    return false;  // Nessun consiglio da modificare
	                }
	            }
	        }

	        // Elimina consigli precedenti
	        //Elimina consigli precedenti
	        String deleteSql = "DELETE FROM ConsigliLibri WHERE userid = ? AND titoloLibro = ?";
	        try (PreparedStatement delete = conn.prepareStatement(deleteSql)) {
	            delete.setString(1, userid);
	            delete.setString(2, titoloPrincipale);
	            delete.executeUpdate();
	        }

	        // Inserisce i nuovi consigli
	        String insertSql = "INSERT INTO ConsigliLibri (userid, titoloLibro, libroConsigliato) VALUES (?, ?, ?)";
	        try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
	            for (String titolo : nuoviConsigliati) {
	                pst.setString(1, userid);
	                pst.setString(2, titoloPrincipale);
	                pst.setString(3, titolo);
	                pst.addBatch();
	            }
	            pst.executeBatch();
	        }

	        return true;
	    }
	
	
}
