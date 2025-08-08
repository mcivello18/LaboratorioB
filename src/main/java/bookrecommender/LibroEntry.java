package bookrecommender;
import javafx.beans.property.*;
public class LibroEntry {
	   private final StringProperty titolo;
	    private final StringProperty autore;
	    private final StringProperty anno;
	    private final BooleanProperty selected;

	    public LibroEntry(String titolo, String autore, String anno) {
	        this.titolo = new SimpleStringProperty(titolo);
	        this.autore = new SimpleStringProperty(autore);
	        this.anno = new SimpleStringProperty(anno);
	        this.selected = new SimpleBooleanProperty(false);
	    }

	    public String getTitolo() { return titolo.get(); }
	    public StringProperty titoloProperty() { return titolo; }

	    public String getAutore() { return autore.get(); }
	    public StringProperty autoreProperty() { return autore; }

	    public String getAnno() { return anno.get(); }
	    public StringProperty annoProperty() { return anno; }

	    public boolean isSelected() { return selected.get(); }
	    public void setSelected(boolean value) { selected.set(value); }
	    public BooleanProperty selectedProperty() { return selected; }

}
