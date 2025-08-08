package bookrecommender;
import java.io.Serializable;
public class Utente implements Serializable {
	
	private final static long serialVersionUID=1L;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String email;
	private String userid;
	private String password;
	
	
	public Utente(String nome, String cognome, String codiceFiscale, String email, String userid, String password) {
	    this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.userid = userid;
        this.password = password;
		
	}
	
	public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getCodiceFiscale() { return codiceFiscale; }
    public String getEmail() { return email; }
    public String getUserid() { return userid; }
    public String getPassword() { return password; }
	
	 

}
