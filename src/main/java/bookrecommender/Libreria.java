package bookrecommender;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Libreria implements Serializable{
	private final static long serialVersionUID=1L;
    private String nomeLibreria;
    private List<String> titoli;

    public Libreria(String nomeLibreria) {
        this.nomeLibreria = nomeLibreria;
        this.titoli = new ArrayList<>();
    }

    public String getNomeLibreria() {
        return nomeLibreria;
    }

    public List<String> getTitoli() {
        return titoli;
    }

    public void addTitolo(String titolo) {
        titoli.add(titolo);
    }
}
