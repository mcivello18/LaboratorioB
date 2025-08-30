package bookrecommender;
import javafx.application.Application;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import java.util.Set;
import java.util.HashSet;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
public class clientBR extends Application {
	
	private Stage primaryStage;
    private Scene menuScene, loginScene, registrationScene;
    private String useridLoggato;  
    private final List<LibroEntry> libriSelezionati = new ArrayList<>();

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private double savedWidth = 800;
    private double savedHeight = 600;
    private double savedX = -1;
    private double savedY = -1;
    private boolean wasMaximized = false;
	
	public void start(Stage stage) throws Exception {
		   socket = new Socket("localhost", 8080);
	        out = new ObjectOutputStream(socket.getOutputStream());
	        in = new ObjectInputStream(socket.getInputStream());

	        primaryStage = stage;
	        primaryStage.setTitle("Book Recommender");

	        menuScene = createMenuScene();
	        loginScene = createLoginScene();
	        registrationScene = createRegistrationScene();

	        primaryStage.setScene(menuScene);
	        restoreWindowState();
	        primaryStage.show();
	    }

	private Scene createMenuScene() {
        VBox menuLayout = new VBox(15);
        menuLayout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri3.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        menuLayout.setBackground(new Background(backgroundImage));

        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Registrazione");
        Button btnViewBook = new Button("Visualizza libro"); 

        
        
        
        
        
        
        btnLogin.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(loginScene);
            restoreWindowState();
        });

        btnRegister.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(registrationScene);
            restoreWindowState();
        });

        btnViewBook.setOnAction(e -> {
            saveWindowState();
            visualizzaLibriUI(primaryStage);  
            restoreWindowState();
        });

        menuLayout.getChildren().addAll(btnLogin, btnRegister, btnViewBook); // AGGIUNTO
        return new Scene(menuLayout, 300, 180);

       
    }
    
    

    private Scene createLoginScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri3.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));

        Label lblUserid = new Label("Userid:");
        TextField tfUserid = new TextField();
        Label lblUseridError = new Label(); lblUseridError.setStyle("-fx-text-fill: red;");

        Label lblPassword = new Label("Password:");
        PasswordField pfPassword = new PasswordField();
        Label lblPasswordError = new Label(); lblPasswordError.setStyle("-fx-text-fill: red;");

        Label lblLoginMessage = new Label();

        Button btnSubmit = new Button("Login");
        Button btnBack = new Button("Torna al menù");

        btnSubmit.setOnAction(e -> {
            lblUseridError.setText("");
            lblPasswordError.setText("");
            lblLoginMessage.setText("");

            String userid = tfUserid.getText().trim();
            String password = pfPassword.getText().trim();
            boolean valid = true;

            if (userid.isEmpty()) {
                lblUseridError.setText("Inserisci userid");
                valid = false;
            }
            if (password.isEmpty()) {
                lblPasswordError.setText("Inserisci password");
                valid = false;
            }

            if (valid) {
                try {
                    out.writeObject("login");
                    out.writeObject(new Utente("", "", "", "", userid, password));
                    boolean success = (Boolean) in.readObject();
                    if (success) {
                    	 
                    	 
                    	useridLoggato = userid;
                       
                        
                    	saveWindowState();
                    	Scene postLoginScene = createPostLoginMenu();
                    	primaryStage.setScene(postLoginScene);
                    	restoreWindowState();
                    } else {
                        lblLoginMessage.setStyle("-fx-text-fill: red;");
                        lblLoginMessage.setText("Userid o password errati.");
                    }
                } catch (Exception ex) {
                    lblLoginMessage.setStyle("-fx-text-fill: red;");
                    lblLoginMessage.setText("Errore di comunicazione col server.");
                    ex.printStackTrace();
                }
            }
        });

        
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(menuScene);
            restoreWindowState();
        });

        layout.getChildren().addAll(
                lblUserid, tfUserid, lblUseridError,
                lblPassword, pfPassword, lblPasswordError,
                btnSubmit, lblLoginMessage,
                btnBack
        );

        return new Scene(layout, 350, 300);
    }
    
    

    private Scene createRegistrationScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri3.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));

        Label lblNome = new Label("Nome:");
        TextField tfNome = new TextField();
        Label lblNomeError = new Label(); lblNomeError.setStyle("-fx-text-fill: red;");

        Label lblCognome = new Label("Cognome:");
        TextField tfCognome = new TextField();
        Label lblCognomeError = new Label(); lblCognomeError.setStyle("-fx-text-fill: red;");

        Label lblCodiceFiscale = new Label("Codice Fiscale:");
        TextField tfCodiceFiscale = new TextField();
        Label lblCodiceFiscaleError = new Label(); lblCodiceFiscaleError.setStyle("-fx-text-fill: red;");

        Label lblEmail = new Label("Email:");
        TextField tfEmail = new TextField();
        Label lblEmailError = new Label(); lblEmailError.setStyle("-fx-text-fill: red;");

        Label lblUserid = new Label("Userid:");
        TextField tfUserid = new TextField();
        Label lblUseridError = new Label(); lblUseridError.setStyle("-fx-text-fill: red;");

        Label lblPassword = new Label("Password:");
        PasswordField pfPassword = new PasswordField();
        Label lblPasswordError = new Label(); lblPasswordError.setStyle("-fx-text-fill: red;");

        Label lblRegistrationMessage = new Label();

        Button btnSubmit = new Button("Registrati");
        Button btnBack = new Button("Torna al menù");

        btnSubmit.setOnAction(e -> {
            lblNomeError.setText("");
            lblCognomeError.setText("");
            lblCodiceFiscaleError.setText("");
            lblEmailError.setText("");
            lblUseridError.setText("");
            lblPasswordError.setText("");
            lblRegistrationMessage.setText("");

            String nome = tfNome.getText().trim();
            String cognome = tfCognome.getText().trim();
            String cf = tfCodiceFiscale.getText().trim();
            String email = tfEmail.getText().trim();
            String userid = tfUserid.getText().trim();
            String password = pfPassword.getText().trim();

            boolean valid = true;

            if (!controlloNome(nome)) {
                lblNomeError.setText("Nome non valido");
                valid = false;
            }
            if (!controlloCognome(cognome)) {
                lblCognomeError.setText("Cognome non valido");
                valid = false;
            }
            if (!controlloCodiceFiscale(cf)) {
                lblCodiceFiscaleError.setText("Codice fiscale non valido");
                valid = false;
            }
            if (email.isEmpty()) {
                lblEmailError.setText("Email obbligatoria");
                valid = false;
            }
            if (userid.isEmpty()) {
                lblUseridError.setText("Userid obbligatorio");
                valid = false;
            }
            if (!controlloPassword(password)) {
                lblPasswordError.setText("Password non valida (deve contenere almeno 8 caratteri tra cui una lettera maiuscola, almeno una lettera minuscola, almeno un numero e almeno un carattere alfanumerico)");
                valid = false;
            }

            if (valid) {
                try {
                    Utente newUser = new Utente(nome, cognome, cf, email, userid, password);
                    out.writeObject("registrazione");
                    out.writeObject(newUser);
                    boolean ok = (Boolean) in.readObject();
                    if (ok) {
                        lblRegistrationMessage.setStyle("-fx-text-fill: green;");
                        lblRegistrationMessage.setText(
                                "Registrazione avvenuta con successo;\n" +
                                        "torna al menù principale per eseguire il login"
                        );
                    } else {
                        lblRegistrationMessage.setStyle("-fx-text-fill: red;");
                        lblRegistrationMessage.setText("Userid o email già esistenti");
                    }
                } catch (Exception ex) {
                    lblRegistrationMessage.setStyle("-fx-text-fill: red;");
                    lblRegistrationMessage.setText("Errore di comunicazione col server.");
                    ex.printStackTrace();
                }
            }
        });

        //btnBack.setOnAction(e -> primaryStage.setScene(menuScene));
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(menuScene);
            restoreWindowState();
        });

        layout.getChildren().addAll(
                lblNome, tfNome, lblNomeError,
                lblCognome, tfCognome, lblCognomeError,
                lblCodiceFiscale, tfCodiceFiscale, lblCodiceFiscaleError,
                lblEmail, tfEmail, lblEmailError,
                lblUserid, tfUserid, lblUseridError,
                lblPassword, pfPassword, lblPasswordError,
                btnSubmit, lblRegistrationMessage,
                btnBack
        );

        return new Scene(layout, 400, 650);
    }

    
    private static boolean controlloNome(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') return false;
        }
        return !s.isEmpty();
    }
    
    

    private static boolean controlloCognome(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') return false;
        }
        return !s.isEmpty();
    }
    
    

    private static boolean controlloCodiceFiscale(String cf) {
        if (cf == null || cf.length() != 16) return false;
        int[] digitPositions = {6, 7, 9, 10, 12, 13, 14};

        for (int i = 0; i < cf.length(); i++) {
            char c = cf.charAt(i);
            if (contains(digitPositions, i)) {
                if (!Character.isDigit(c)) return false;
            } else {
                if (!Character.isUpperCase(c) || !Character.isLetter(c)) return false;
            }
        }
        return true;
    }
    
    

    private static boolean contains(int[] arr, int key) {
        for (int value : arr) {
            if (value == key) return true;
        }
        return false;
    }
    
    

    private static boolean controlloPassword(String pw) {
        if (pw == null || pw.length() < 8) return false;

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (char c : pw.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSymbol = true;
        }

        return hasLower && hasUpper && hasDigit && hasSymbol;
    }
    
    
    private Scene createPostLoginMenu() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri13.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));

        Label lblWelcome = new Label("Benvenuto! Scegli un'opzione:");

        Button btnCreateLibrary = new Button("Crea libreria");
        Button btnViewLibrary = new Button("Visualizza libreria");
        Button btnRateBooks = new Button("Valuta libri");
        Button btnRecommendBooks = new Button("Consiglia libri");
        Button btnViewBook=new Button("Visualizza libro");
        Button btnBack = new Button("Logout");

        
        
            
        
        btnCreateLibrary.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(createCreateLibraryScene());
            restoreWindowState();
        });
        btnViewLibrary.setOnAction(e -> {
        	   try {
        	        out.writeObject("getLibrerieConLibriDiUtente");
        	        out.writeObject(useridLoggato);

        	        @SuppressWarnings("unchecked")
        	        List<Libreria> librerie = (List<Libreria>) in.readObject();
        	        saveWindowState();

        	        primaryStage.setScene(createViewLibrariesScene(librerie));
        	        restoreWindowState();

        	    } catch (Exception ex) {
        	        ex.printStackTrace();
        	        Alert alert = new Alert(Alert.AlertType.ERROR);
        	        alert.setHeaderText("Errore");
        	        alert.setContentText("Impossibile caricare le librerie.");
        	        alert.showAndWait();
        	    }	
        });
        btnRateBooks.setOnAction(e -> {
        	try {
        		out.writeObject("getLibrerieConLibriDiUtente");
        		out.writeObject(useridLoggato);
        		@SuppressWarnings("unchecked")
        		List<Libreria> librerie = (List<Libreria>) in.readObject();
        		saveWindowState();
        		primaryStage.setScene(createChooseBookToRateScene(librerie));
        		restoreWindowState();

            } catch (Exception ex) {
                ex.printStackTrace();
                
            }
        });
        
        btnRecommendBooks.setOnAction(e -> {
            saveWindowState();
            mostraFinestraConsigli(); 
            restoreWindowState();
        });
        	        
        
        btnViewBook.setOnAction(e -> {
            saveWindowState();
            visualizzaLibriUI1(primaryStage); // 
            restoreWindowState();
        });
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(menuScene);
            restoreWindowState();
        });

        layout.getChildren().addAll(
            lblWelcome,
            btnCreateLibrary,
            btnViewLibrary,
            btnRateBooks,
            btnRecommendBooks,
            btnViewBook,
            btnBack
        );

        return new Scene(layout, 400, 300);
    }
    
   
    
    private Scene createCreateLibraryScene() {
    	libriSelezionati.clear();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri13.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));
        
        Label lblNomeLibreria = new Label("Nome libreria:");
        TextField tfNomeLibreria = new TextField();
        Label lblNomeErrore = new Label();
        lblNomeErrore.setStyle("-fx-text-fill: red;");
        
        Label lblCercaLibro = new Label("Cerca libri (titolo):");
        TextField tfCercaLibro = new TextField();
        
        Button btnCerca = new Button("Cerca");
        
        TableView<LibroEntry> tableLibri = new TableView<>();
        tableLibri.setPrefHeight(300);
        
        // Definizione colonne
        TableColumn<LibroEntry, Boolean> colSeleziona = new TableColumn<>("Seleziona");
        colSeleziona.setCellValueFactory(param -> param.getValue().selectedProperty());
        colSeleziona.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleziona));
        colSeleziona.setEditable(true);
        
        TableColumn<LibroEntry, String> colTitolo = new TableColumn<>("Titolo");
        colTitolo.setCellValueFactory(param -> param.getValue().titoloProperty());
        
        TableColumn<LibroEntry, String> colAutore = new TableColumn<>("Autore");
        colAutore.setCellValueFactory(param -> param.getValue().autoreProperty());
        
        TableColumn<LibroEntry, String> colAnno = new TableColumn<>("Anno");
        colAnno.setCellValueFactory(param -> param.getValue().annoProperty());
        
        tableLibri.getColumns().addAll(colSeleziona, colTitolo, colAutore, colAnno);
        tableLibri.setEditable(true);
        
        Label lblMessaggio = new Label();
        
        // Paginazione (offset e limit)
        final int limit = 50;
        final int[] offset = {0};
        
        Button btnPrev = new Button("<< Indietro");
        Button btnNext = new Button("Avanti >>");
        btnPrev.setDisable(true);
        btnNext.setDisable(true);
        
        HBox paginazione = new HBox(10, btnPrev, btnNext);
        
        // Cerca libri e aggiorna tabella
        btnCerca.setOnAction(e -> {
            offset[0] = 0;  // reset offset
            caricaLibri(tfCercaLibro.getText().trim(), offset[0], limit, tableLibri, btnPrev, btnNext, lblMessaggio);
        });
        
        btnPrev.setOnAction(e -> {
            if (offset[0] - limit >= 0) {
                offset[0] -= limit;
                caricaLibri(tfCercaLibro.getText().trim(), offset[0], limit, tableLibri, btnPrev, btnNext, lblMessaggio);
            }
        });
        
        btnNext.setOnAction(e -> {
            offset[0] += limit;
            caricaLibri(tfCercaLibro.getText().trim(), offset[0], limit, tableLibri, btnPrev, btnNext, lblMessaggio);
        });
        
        Button btnCrea = new Button("Crea Libreria");
        Button btnBack = new Button("Torna al menù");
        
        btnCrea.setOnAction(e -> {
            lblNomeErrore.setText("");
            lblMessaggio.setText("");
            
            String nomeLibreria = tfNomeLibreria.getText().trim();
            if (nomeLibreria.isEmpty()) {
                lblNomeErrore.setText("Inserisci un nome per la libreria");
                return;
            }
            
            // Raccogli libri selezionati
            List<String> titoliSelezionati = new ArrayList<>();
            for (LibroEntry libro : libriSelezionati) {
                titoliSelezionati.add(libro.getTitolo());
            }
            
            if (titoliSelezionati.isEmpty()) {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Seleziona almeno un libro.");
                return;
            }
            
            try {
                out.writeObject("creaLibreria");
                out.writeObject(useridLoggato);
                out.writeObject(nomeLibreria);
                out.writeObject(titoliSelezionati);
                
                boolean success = (Boolean) in.readObject();
                if (success) {
                    lblMessaggio.setStyle("-fx-text-fill: green;");
                    lblMessaggio.setText("Libreria creata con successo!");
                } else {
                    lblMessaggio.setStyle("-fx-text-fill: red;");
                    lblMessaggio.setText("Libreria già esistente con questo nome.");
                }
            } catch (Exception ex) {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Errore di comunicazione col server.");
                ex.printStackTrace();
            }
        });
        
        //btnBack.setOnAction(e -> primaryStage.setScene(createPostLoginMenu()));
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(createPostLoginMenu());
            restoreWindowState();
        });
        
        layout.getChildren().addAll(
            lblNomeLibreria, tfNomeLibreria, lblNomeErrore,
            lblCercaLibro, tfCercaLibro, btnCerca,
            tableLibri, paginazione,
            btnCrea, lblMessaggio,
            btnBack
        );
        
        return new Scene(layout, 600, 650);
    }
    
    
    public void stop() throws Exception {
        out.writeObject("END");
        in.close();
        out.close();
        socket.close();
        super.stop();
    }
    
    
     
    private void caricaLibri(String keyword, int offset, int limit, TableView<LibroEntry> table, Button btnPrev, Button btnNext, Label lblMessaggio) {
        try {
            out.writeObject("ricercaLibri");
            out.writeObject(keyword);
            out.writeObject(offset);
            out.writeObject(limit);

            @SuppressWarnings("unchecked")
            List<String[]> risultati = (List<String[]>) in.readObject();

            table.getItems().clear();

            for (String[] libro : risultati) {
                LibroEntry entry = new LibroEntry(libro[0], libro[1], libro[2]);

                // Se questo libro è già stato selezionato, seleziona la checkbox
                for (LibroEntry selezionato : libriSelezionati) {
                    if (selezionato.getTitolo().equals(entry.getTitolo())) {
                        entry.setSelected(true);
                        break;
                    }
                }

                // Aggiungi listener per aggiornare la lista globale di selezionati
                entry.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        // Aggiungi solo se non è già presente
                        if (libriSelezionati.stream().noneMatch(l -> l.getTitolo().equals(entry.getTitolo()))) {
                            libriSelezionati.add(entry);
                        }
                    } else {
                        // Rimuovi dalla lista globale
                        libriSelezionati.removeIf(l -> l.getTitolo().equals(entry.getTitolo()));
                    }
                });

                table.getItems().add(entry);
            }

            btnPrev.setDisable(offset == 0);
            btnNext.setDisable(risultati.size() < limit);
            lblMessaggio.setText("");

        } catch (Exception e) {
            lblMessaggio.setStyle("-fx-text-fill: red;");
            lblMessaggio.setText("Errore nel caricamento dei libri.");
            e.printStackTrace();
        }
    }
    
    
    private Scene createViewLibrariesScene(List<Libreria> librerie) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri9.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));

        Label lblTitle = new Label("Le tue librerie");
        
        TreeItem<String> rootItem = new TreeItem<>("Librerie");
        rootItem.setExpanded(true);

        for (Libreria lib : librerie) {
            TreeItem<String> libreriaItem = new TreeItem<>(lib.getNomeLibreria());
            for (String titolo : lib.getTitoli()) {
                TreeItem<String> libroItem = new TreeItem<>(titolo);
                libreriaItem.getChildren().add(libroItem);
            }
            rootItem.getChildren().add(libreriaItem);
        }

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);

        Button btnBack = new Button("Torna al menu");
        //btnBack.setOnAction(ev -> primaryStage.setScene(createPostLoginMenu()));
        btnBack.setOnAction(ev -> {
            saveWindowState();
            primaryStage.setScene(createPostLoginMenu());
            restoreWindowState();
        });

        layout.getChildren().addAll(lblTitle, treeView, btnBack);

        return new Scene(layout, 400, 500);
    }
    
    
    private Scene createChooseBookToRateScene(List<Libreria> librerie) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri20.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));
        Label lbl = new Label("Scegli il libro da valutare:");

        // Una TreeView per librerie e titoli
        TreeItem<String> root = new TreeItem<>("Librerie");
        root.setExpanded(true);
        for (Libreria lib : librerie) {
            TreeItem<String> libItem = new TreeItem<>(lib.getNomeLibreria());
            for (String titolo : lib.getTitoli()) {
                libItem.getChildren().add(new TreeItem<>(titolo));
            }
            root.getChildren().add(libItem);
        }
        TreeView<String> tree = new TreeView<>(root);
        tree.setShowRoot(false);

        Button btnNext = new Button("Valuta libro selezionato");
        Label lblMsg = new Label();
        Button btnBack = new Button("Torna al menu");

        btnNext.setOnAction(e -> {
            TreeItem<String> sel = tree.getSelectionModel().getSelectedItem();
            if (sel == null || sel.getParent() == root) {
                lblMsg.setText("Seleziona un libro (non una libreria).");
                lblMsg.setStyle("-fx-text-fill: red;");
                return;
            }
            String titolo = sel.getValue();
            
            saveWindowState();
            primaryStage.setScene(createRatingBookScene(titolo));
            restoreWindowState();
        });

        
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(createPostLoginMenu());
            restoreWindowState();
        });

        layout.getChildren().addAll(lbl, tree, btnNext, lblMsg, btnBack);
        return new Scene(layout, 450, 500);
    }
    
    
    
    private Scene createRatingBookScene(String titolo) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri20.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));

        Label lblTitle = new Label("Valuta il libro:\n" + titolo);
        TextField tfStile = new TextField(); tfStile.setPromptText("Stile (1‑5)");
        TextField tfCont = new TextField(); tfCont.setPromptText("Contenuto (1‑5)");
        TextField tfGrad = new TextField(); tfGrad.setPromptText("Gradevolezza (1‑5)");
        TextField tfOrig = new TextField(); tfOrig.setPromptText("Originalità (1‑5)");
        TextField tfEdiz = new TextField(); tfEdiz.setPromptText("Edizione (1‑5)");

        TextArea taStile = new TextArea(); taStile.setPromptText("Nota stile"); taStile.setPrefRowCount(2);
        TextArea taCont = new TextArea(); taCont.setPromptText("Nota contenuto"); taCont.setPrefRowCount(2);
        TextArea taGrad = new TextArea(); taGrad.setPromptText("Nota gradevolezza"); taGrad.setPrefRowCount(2);
        TextArea taOrig = new TextArea(); taOrig.setPromptText("Nota originalità"); taOrig.setPrefRowCount(2);
        TextArea taEdiz = new TextArea(); taEdiz.setPromptText("Nota edizione"); taEdiz.setPrefRowCount(2);

        Label lblMsg = new Label();
        Button btnInvia = new Button("Invia valutazione");
        Button btnBack = new Button("Torna al menu");

        btnInvia.setOnAction(e -> {
            lblMsg.setText("");
            try {
                int s = Integer.parseInt(tfStile.getText());
                int c = Integer.parseInt(tfCont.getText());
                int g = Integer.parseInt(tfGrad.getText());
                int o = Integer.parseInt(tfOrig.getText());
                int ed = Integer.parseInt(tfEdiz.getText());
                double media = (s + c + g + o + ed) / 5.0;
                if (!validRange(s)||!validRange(c)||!validRange(g)||!validRange(o)||!validRange(ed)) {
                    throw new NumberFormatException();
                }

                out.writeObject("aggiungiValutazione");
                out.writeObject(useridLoggato);
                out.writeObject(titolo);
                out.writeObject(s); out.writeObject(taStile.getText());
                out.writeObject(c); out.writeObject(taCont.getText());
                out.writeObject(g); out.writeObject(taGrad.getText());
                out.writeObject(o); out.writeObject(taOrig.getText());
                out.writeObject(ed); out.writeObject(taEdiz.getText());

                boolean success = (Boolean) in.readObject();
                if (success) {
                    lblMsg.setStyle("-fx-text-fill: green;");
                    lblMsg.setText("Valutazione inviata! Voto medio: "+media);
                } else {
                    
                    
                	
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Valutazione già esistente");
                    alert.setHeaderText(null);
                    alert.setContentText("Hai già valutato questo libro. Vuoi modificare la valutazione?");

                    ButtonType buttonSi = new ButtonType("Sì");
                    ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonSi, buttonNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonSi) {
                        
                        out.writeObject("modificaValutazione");
                        out.writeObject(useridLoggato);
                        out.writeObject(titolo);
                        out.writeObject(s); out.writeObject(taStile.getText());
                        out.writeObject(c); out.writeObject(taCont.getText());
                        out.writeObject(g); out.writeObject(taGrad.getText());
                        out.writeObject(o); out.writeObject(taOrig.getText());
                        out.writeObject(ed); out.writeObject(taEdiz.getText());

                        boolean modifySuccess = (Boolean) in.readObject();

                        if (modifySuccess) {
                            lblMsg.setStyle("-fx-text-fill: green;");
                            lblMsg.setText("Valutazione modificata con successo! Voto medio: " + media);
                        } else {
                            lblMsg.setStyle("-fx-text-fill: red;");
                            lblMsg.setText("Errore nella modifica della valutazione.");
                        }
                    } else {
                        lblMsg.setStyle("-fx-text-fill: orange;");
                        lblMsg.setText("Valutazione non modificata; tornare al menu per altre operazioni.");
                    }
                
                }
            } catch (NumberFormatException ex) {
                lblMsg.setStyle("-fx-text-fill: red;");
                lblMsg.setText("Inserisci numeri validi da 1 a 5 per tutti i voti.");
            } catch (Exception ex) {
                lblMsg.setStyle("-fx-text-fill: red;");
                lblMsg.setText("Errore comunicazione con il server.");
                ex.printStackTrace();
            }
        });

        //btnBack.setOnAction(e -> primaryStage.setScene(createPostLoginMenu()));
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(createPostLoginMenu());
            restoreWindowState();
        });

        layout.getChildren().addAll(lblTitle,
            new Label("Stile:"), tfStile, taStile,
            new Label("Contenuto:"), tfCont, taCont,
            new Label("Gradevolezza:"), tfGrad, taGrad,
            new Label("Originalità:"), tfOrig, taOrig,
            new Label("Edizione:"), tfEdiz, taEdiz,
            btnInvia, lblMsg, btnBack);

        return new Scene(layout, 500, 800);
    }
    
    
    private static boolean validRange(int v) { return v>=1 && v<=5; }
    
    
    private void mostraFinestraConsigli() {
        try {
            // Chiede al server le librerie con libri dell'utente
            out.writeObject("getLibrerieConLibriDiUtente");
            out.writeObject(useridLoggato);
            @SuppressWarnings("unchecked")
            List<Libreria> librerie = (List<Libreria>) in.readObject();

            // Prima scena: scegliere libro per cui consigliare
            Scene scenaSelezionaLibro = createChooseBookToRecommendScene(librerie);
            saveWindowState();
            primaryStage.setScene(scenaSelezionaLibro);
            restoreWindowState();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    private Scene createChooseBookToRecommendScene(List<Libreria> librerie) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri20.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));
        Label lbl = new Label("Scegli il libro per cui vuoi consigliare:");

        TreeItem<String> root = new TreeItem<>("Librerie");
        root.setExpanded(true);
        for (Libreria lib : librerie) {
            TreeItem<String> libItem = new TreeItem<>(lib.getNomeLibreria());
            for (String titolo : lib.getTitoli()) {
                libItem.getChildren().add(new TreeItem<>(titolo));
            }
            root.getChildren().add(libItem);
        }

        TreeView<String> tree = new TreeView<>(root);
        tree.setShowRoot(false);

        Button btnNext = new Button("Avanti");
        Label lblMsg = new Label();
        Button btnBack = new Button("Torna al menu");

        btnNext.setOnAction(e -> {
            TreeItem<String> sel = tree.getSelectionModel().getSelectedItem();
            if (sel == null || sel.getParent() == root) {
                lblMsg.setText("Seleziona un libro (non una libreria).");
                lblMsg.setStyle("-fx-text-fill: red;");
                return;
            }
            String titoloSelezionato = sel.getValue();
            saveWindowState();
            primaryStage.setScene(createRecommendBooksSelectionScene(librerie, titoloSelezionato));
            restoreWindowState();
        });

        //btnBack.setOnAction(e -> primaryStage.setScene(createPostLoginMenu()));
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(createPostLoginMenu());
            restoreWindowState();
        });

        layout.getChildren().addAll(lbl, tree, btnNext, lblMsg, btnBack);
        return new Scene(layout, 450, 500);
    }

    
    
    
    
    
    
    
    private Scene createRecommendBooksSelectionScene(List<Libreria> librerie, String libroDaConsigliare) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri20.jpg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        layout.setBackground(new Background(backgroundImage));

        Label lblInfo = new Label("Libro da consigliare: " + libroDaConsigliare);
        Label lblIstruzioni = new Label("Seleziona fino a 3 libri da consigliare:");

        // Lista per tenere traccia dei libri selezionati
        List<LibroEntry> libriConsigliatiSelezionati = new ArrayList<>();
        Set<String> titoliSelezionati=new HashSet<>();

        // Container per le librerie e i loro libri con checkbox
        VBox librerieBox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(librerieBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        Label lblMsgErrore = new Label();

        // Costruisco la UI: per ogni libreria, nome + libri con checkbox (escludo libroDaConsigliare)
        for (Libreria lib : librerie) {
            VBox libBox = new VBox(5);
            Label lblLibName = new Label(lib.getNomeLibreria());
            lblLibName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            libBox.getChildren().add(lblLibName);

            for (String titolo : lib.getTitoli()) {
                if (titolo.equals(libroDaConsigliare)) continue; // non selezionabile

                CheckBox cb = new CheckBox(titolo);

                cb.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!cb.isSelected()) {
                        // Tentativo di selezione
                        if (libriConsigliatiSelezionati.size() >= 3) {
                            event.consume(); // blocca la selezione
                            lblMsgErrore.setText("Puoi selezionare al massimo 3 libri da consigliare.");
                            lblMsgErrore.setStyle("-fx-text-fill: red;");
                        } else if (titoliSelezionati.contains(titolo)) {
                            event.consume(); // blocca la selezione
                            lblMsgErrore.setText("Non puoi mettere 2 volte lo stesso libro tra i libri consigliati.");
                            lblMsgErrore.setStyle("-fx-text-fill: red;");
                        } else {
                            
                            cb.setSelected(true);
                            libriConsigliatiSelezionati.add(new LibroEntry(titolo, "", ""));
                            titoliSelezionati.add(titolo);
                            lblMsgErrore.setText("");
                            event.consume(); 
                        }
                    } else {
                        // Deselezione
                        cb.setSelected(false);
                        libriConsigliatiSelezionati.removeIf(l -> l.getTitolo().equals(titolo));
                        titoliSelezionati.remove(titolo);
                        lblMsgErrore.setText("");
                        event.consume(); 
                    }
                });

                libBox.getChildren().add(cb);
            }

            librerieBox.getChildren().add(libBox);
            librerieBox.getChildren().add(new Separator());
        }

        Button btnInvia = new Button("Invia consigli");
        Button btnBack = new Button("Torna indietro");
        Label lblMsgRisposta = new Label();

        btnInvia.setOnAction(e -> {
            if (libriConsigliatiSelezionati.isEmpty()) {
                lblMsgRisposta.setText("Seleziona almeno un libro da consigliare.");
                lblMsgRisposta.setStyle("-fx-text-fill: red;");
                return;
            }
            try {
                out.writeObject("consigliaLibri");
                out.writeObject(useridLoggato);
                out.writeObject(libroDaConsigliare);

                // Invia lista titoli consigliati
                List<String> titoliDaConsigliare = new ArrayList<>();
                for (LibroEntry le : libriConsigliatiSelezionati) {
                    titoliDaConsigliare.add(le.getTitolo());
                }
                out.writeObject(titoliDaConsigliare);

                boolean success = (Boolean) in.readObject();
                if (success) {
                    lblMsgRisposta.setText("Consigli inviati con successo!");
                    lblMsgRisposta.setStyle("-fx-text-fill: green;");
                } else {
                    
                    
                	
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Consigli già esistenti");
                    alert.setHeaderText(null);
                    alert.setContentText("Hai già consigliato libri per questo titolo. Vuoi modificarli?");

                    ButtonType buttonSi = new ButtonType("Sì");
                    ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonSi, buttonNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonSi) {
                        out.writeObject("modificaConsigliLibri");
                        out.writeObject(useridLoggato);
                        out.writeObject(libroDaConsigliare);
                        out.writeObject(titoliDaConsigliare);

                        boolean modificati = (Boolean) in.readObject();

                        if (modificati) {
                            lblMsgRisposta.setText("Consigli modificati con successo!");
                            lblMsgRisposta.setStyle("-fx-text-fill: green;");
                        } else {
                            lblMsgRisposta.setText("Errore nella modifica dei consigli.");
                            lblMsgRisposta.setStyle("-fx-text-fill: red;");
                        }
                    } else {
                        lblMsgRisposta.setText("Modifica annullata.");
                        lblMsgRisposta.setStyle("-fx-text-fill: orange;");
                    }
                }

            } catch (Exception ex) {
                lblMsgRisposta.setText("Errore di comunicazione col server.");
                lblMsgRisposta.setStyle("-fx-text-fill: red;");
                ex.printStackTrace();
            }
        });

        
            
            
        
        
        btnBack.setOnAction(e -> {
            saveWindowState();
            primaryStage.setScene(createChooseBookToRecommendScene(librerie));
            restoreWindowState();
        });

        layout.getChildren().addAll(lblInfo, lblIstruzioni, scrollPane, lblMsgErrore, btnInvia, lblMsgRisposta, btnBack);
        return new Scene(layout, 500, 600);
    }
    
    
    
    
   
    
    
    
      
    private void visualizzaLibriUI1(Stage primaryStage) {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri2.jpeg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        mainLayout.setBackground(new Background(backgroundImage));


        Label titleLabel = new Label("Visualizza Libri");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnTitolo = new Button("Cerca libro per titolo");
        Button btnAutore = new Button("Cerca libro per autore");
        Button btnAutoreAnno = new Button("Cerca libro per autore e anno");

        HBox searchButtons = new HBox(10, btnTitolo, btnAutore, btnAutoreAnno);

        TextField input1 = new TextField();
        TextField input2 = new TextField();
        input1.setPromptText("Inserisci valore...");
        input2.setPromptText("Inserisci anno...");
        Button btnCerca = new Button("Cerca");
        HBox searchInput = new HBox(10, input1, input2, btnCerca);
        searchInput.setVisible(false);

        TableView<String[]> risultatiTable = new TableView<>();
        risultatiTable.setPlaceholder(new Label("Nessun risultato"));
        String[] colNames = {"Titolo", "Autore", "Anno"};
        for (int i = 0; i < colNames.length; i++) {
            final int colIndex = i;
            TableColumn<String[], String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[colIndex]));
            risultatiTable.getColumns().add(col);
        }

        Button btnDettagli = new Button("Mostra Dettagli");
        Button btnAggregati = new Button("Visualizza dettagli in forma aggregata");
        Button btnIndietro = new Button("Torna al menù");
        HBox bottomButtons = new HBox(10, btnDettagli, btnAggregati, btnIndietro);

        VBox dettaglioBox = new VBox(5);
        dettaglioBox.setPadding(new Insets(10));

        Button btnAvanti = new Button("Avanti");
        Button btnIndietroPagina = new Button("Indietro");
        HBox pagination = new HBox(10, btnIndietroPagina, btnAvanti);
        pagination.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(dettaglioBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVisible(false);
        scrollPane.setPrefHeight(250);

        mainLayout.getChildren().addAll(titleLabel, searchButtons, searchInput, risultatiTable, pagination, bottomButtons, scrollPane);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Visualizza Libri");
        primaryStage.show();

        final StringProperty tipoRicerca = new SimpleStringProperty();
        final IntegerProperty offset = new SimpleIntegerProperty(0);
        final int LIMIT = 10;

        Runnable performSearch = () -> {
            risultatiTable.getItems().clear();
            dettaglioBox.setVisible(false);
            scrollPane.setVisible(false);

            String val1 = input1.getText().trim();
            String val2 = input2.getText().trim();

            try {
                String comando = switch (tipoRicerca.get()) {
                    case "titolo" -> "ricercaLibri";
                    case "autore" -> "ricercaLibriPerAutore";
                    case "autoreAnno" -> "ricercaLibriPerAutoreEAnno";
                    default -> "";
                };

                if (comando.isEmpty()) return;

                out.writeObject(comando);
                out.writeObject(val1);
                if ("autoreAnno".equals(tipoRicerca.get())) {
                    out.writeObject(Integer.parseInt(val2));
                }
                out.writeObject(offset.get());
                out.writeObject(LIMIT);

                @SuppressWarnings("unchecked")
                List<String[]> risultati = (List<String[]>) in.readObject();
                risultatiTable.getItems().addAll(risultati);

                btnAvanti.setDisable(risultati.size() < LIMIT);
                btnIndietroPagina.setDisable(offset.get() == 0);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        btnTitolo.setOnAction(e -> {
            tipoRicerca.set("titolo");
            input1.setPromptText("Inserisci titolo");
            input2.setVisible(false);
            searchInput.setVisible(true);
            offset.set(0);
        });

        btnAutore.setOnAction(e -> {
            tipoRicerca.set("autore");
            input1.setPromptText("Inserisci autore");
            input2.setVisible(false);
            searchInput.setVisible(true);
            offset.set(0);
        });

        btnAutoreAnno.setOnAction(e -> {
            tipoRicerca.set("autoreAnno");
            input1.setPromptText("Inserisci autore");
            input2.setPromptText("Inserisci anno");
            input2.setVisible(true);
            searchInput.setVisible(true);
            offset.set(0);
        });

        btnCerca.setOnAction(e -> {
            offset.set(0);
            performSearch.run();
        });

        btnAvanti.setOnAction(e -> {
            offset.set(offset.get() + LIMIT);
            performSearch.run();
        });

        btnIndietroPagina.setOnAction(e -> {
            if (offset.get() >= LIMIT) {
                offset.set(offset.get() - LIMIT);
                performSearch.run();
            }
        });

        btnDettagli.setOnAction(e -> {
            String[] selezionato = risultatiTable.getSelectionModel().getSelectedItem();
            if (selezionato == null) return;

            String titolo = selezionato[0];
            dettaglioBox.getChildren().clear();

            try {
                out.writeObject("getValutazioniLibro");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String[]> valutazioni = (List<String[]>) in.readObject();

                if (valutazioni.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessuna valutazione disponibile per questo libro."));
                } else {
                    for (String[] v : valutazioni) {
                        double media = (
                                Integer.parseInt(v[1]) +
                                Integer.parseInt(v[3]) +
                                Integer.parseInt(v[5]) +
                                Integer.parseInt(v[7]) +
                                Integer.parseInt(v[9])
                        ) / 5.0;

                        dettaglioBox.getChildren().add(new Label("L'utente " + v[0] + " ha valutato questo libro (media: " + String.format("%.2f", media) + "):"));
                        dettaglioBox.getChildren().add(new Label(" - Stile: " + v[1] + (v[2].isEmpty() ? "" : " - " + v[2])));
                        dettaglioBox.getChildren().add(new Label(" - Contenuto: " + v[3] + (v[4].isEmpty() ? "" : " - " + v[4])));
                        dettaglioBox.getChildren().add(new Label(" - Gradevolezza: " + v[5] + (v[6].isEmpty() ? "" : " - " + v[6])));
                        dettaglioBox.getChildren().add(new Label(" - Originalità: " + v[7] + (v[8].isEmpty() ? "" : " - " + v[8])));
                        dettaglioBox.getChildren().add(new Label(" - Edizione: " + v[9] + (v[10].isEmpty() ? "" : " - " + v[10])));
                        dettaglioBox.getChildren().add(new Label(" - Voto Finale: " + v[11]));
                        dettaglioBox.getChildren().add(new Label(""));
                    }
                }

                out.writeObject("getUtentiCheConsigliano");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String> consigliano = (List<String>) in.readObject();

                if (consigliano.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessun utente ha consigliato questo libro."));
                } else {
                    
                	
                	
                	
                	
                	VBox utentiBox = new VBox(2); // Spaziatura verticale tra gli elementi
                	Label header = new Label("Utenti che hanno consigliato questo libro:");
                	utentiBox.getChildren().add(header);

                	for (int i = 0; i < consigliano.size(); i++) {
                	    String utente = consigliano.get(i);
                	    Label utenteLabel = new Label((i + 1) + ". " + utente);
                	    utentiBox.getChildren().add(utenteLabel);
                	}

                	dettaglioBox.getChildren().add(utentiBox);
                	
                }

                out.writeObject("getLibriConsigliatiPerLibro");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String[]> consigliati = (List<String[]>) in.readObject();

                Map<String, List<String>> mapConsigli = new HashMap<>();
                for (String[] c : consigliati) {
                    mapConsigli.computeIfAbsent(c[0], k -> new ArrayList<>()).add(c[1]);
                }

                if (mapConsigli.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessun consiglio disponibile per questo libro."));
                } else {
                    for (Map.Entry<String, List<String>> entry : mapConsigli.entrySet()) {
                        
                    	VBox consigliUtenteBox = new VBox(2); // margine tra le righe
                    	Label utenteLabel = new Label("L'utente " + entry.getKey() + " consiglia i seguenti libri:");
                    	consigliUtenteBox.getChildren().add(utenteLabel);

                    	List<String> libri = entry.getValue();
                    	for (int i = 0; i < libri.size(); i++) {
                    	    String libro = libri.get(i);
                    	    Label libroLabel = new Label((i + 1) + ". " + libro);
                    	    consigliUtenteBox.getChildren().add(libroLabel);
                    	}

                    	dettaglioBox.getChildren().add(consigliUtenteBox);
                    }
                }

                out.writeObject("getCommentiLibro");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String[]> commenti = (List<String[]>) in.readObject();

                Map<String, List<String>> mapCommenti = new HashMap<>();
                for (String[] c : commenti) {
                    mapCommenti.computeIfAbsent(c[0], k -> new ArrayList<>()).add(c[2]);
                }

                if (mapCommenti.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessun commento per questo libro."));
                } else {
                    for (Map.Entry<String, List<String>> entry : mapCommenti.entrySet()) {
                        dettaglioBox.getChildren().add(new Label("Commenti dell'utente " + entry.getKey() + ":"));
                        for (String commento : entry.getValue()) {
                            dettaglioBox.getChildren().add(new Label("   - " + commento));
                        }
                    }
                }

                scrollPane.setVisible(true);
                dettaglioBox.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                dettaglioBox.getChildren().add(new Label("Errore durante il caricamento dei dettagli."));
                scrollPane.setVisible(true);
            }
        });

        btnAggregati.setOnAction(ev -> {
            String[] selezionato2 = risultatiTable.getSelectionModel().getSelectedItem();
            if (selezionato2 == null) return;

            String titolo2 = selezionato2[0];
            dettaglioBox.getChildren().clear();

            try {
                out.writeObject("getStatisticheAggregate");
                out.writeObject(titolo2);
                String[] stats = (String[]) in.readObject();

                dettaglioBox.getChildren().add(new Label("Statistiche aggregate per il libro \"" + titolo2 + "\":"));
                dettaglioBox.getChildren().add(new Label("Numero valutazioni: " + stats[0]));
                dettaglioBox.getChildren().add(new Label("Media Stile: " + stats[1]));
                dettaglioBox.getChildren().add(new Label("Media Contenuto: " + stats[2]));
                dettaglioBox.getChildren().add(new Label("Media Gradevolezza: " + stats[3]));
                dettaglioBox.getChildren().add(new Label("Media Originalità: " + stats[4]));
                dettaglioBox.getChildren().add(new Label("Media Edizione: " + stats[5]));
                dettaglioBox.getChildren().add(new Label("Numero utenti che consigliano: " + stats[6]));
                dettaglioBox.getChildren().add(new Label("Numero persone che hanno aggiunto note: " + stats[7]));

                scrollPane.setVisible(true);
                dettaglioBox.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                dettaglioBox.getChildren().add(new Label("Errore durante il caricamento delle statistiche aggregate."));
                scrollPane.setVisible(true);
            }
        });

        //btnIndietro.setOnAction(ev -> primaryStage.setScene(createPostLoginMenu()));
        btnIndietro.setOnAction(ev -> {
            saveWindowState();
            primaryStage.setScene(createPostLoginMenu());
            restoreWindowState();
        });
    }
    
    
    private void visualizzaLibriUI(Stage primaryStage) {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        //BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false,false,false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/ImmagineLibri2.jpeg").toExternalForm(), 3000, 2000, false,true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
            
        );
        mainLayout.setBackground(new Background(backgroundImage));

        Label titleLabel = new Label("Visualizza Libri");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnTitolo = new Button("Cerca libro per titolo");
        Button btnAutore = new Button("Cerca libro per autore");
        Button btnAutoreAnno = new Button("Cerca libro per autore e anno");

        HBox searchButtons = new HBox(10, btnTitolo, btnAutore, btnAutoreAnno);

        TextField input1 = new TextField();
        TextField input2 = new TextField();
        input1.setPromptText("Inserisci valore...");
        input2.setPromptText("Inserisci anno...");
        Button btnCerca = new Button("Cerca");
        HBox searchInput = new HBox(10, input1, input2, btnCerca);
        searchInput.setVisible(false);

        TableView<String[]> risultatiTable = new TableView<>();
        risultatiTable.setPlaceholder(new Label("Nessun risultato"));
        String[] colNames = {"Titolo", "Autore", "Anno"};
        for (int i = 0; i < colNames.length; i++) {
            final int colIndex = i;
            TableColumn<String[], String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[colIndex]));
            risultatiTable.getColumns().add(col);
        }

        Button btnDettagli = new Button("Mostra Dettagli");
        Button btnAggregati = new Button("Visualizza dettagli in forma aggregata");
        Button btnIndietro = new Button("Torna al menù");
        HBox bottomButtons = new HBox(10, btnDettagli, btnAggregati, btnIndietro);

        VBox dettaglioBox = new VBox(5);
        dettaglioBox.setPadding(new Insets(10));

        Button btnAvanti = new Button("Avanti");
        Button btnIndietroPagina = new Button("Indietro");
        HBox pagination = new HBox(10, btnIndietroPagina, btnAvanti);
        pagination.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(dettaglioBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVisible(false);
        scrollPane.setPrefHeight(250);

        mainLayout.getChildren().addAll(titleLabel, searchButtons, searchInput, risultatiTable, pagination, bottomButtons, scrollPane);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Visualizza Libri");
        primaryStage.show();

        final StringProperty tipoRicerca = new SimpleStringProperty();
        final IntegerProperty offset = new SimpleIntegerProperty(0);
        final int LIMIT = 10;

        Runnable performSearch = () -> {
            risultatiTable.getItems().clear();
            dettaglioBox.setVisible(false);
            scrollPane.setVisible(false);

            String val1 = input1.getText().trim();
            String val2 = input2.getText().trim();

            try {
                String comando = switch (tipoRicerca.get()) {
                    case "titolo" -> "ricercaLibri";
                    case "autore" -> "ricercaLibriPerAutore";
                    case "autoreAnno" -> "ricercaLibriPerAutoreEAnno";
                    default -> "";
                };

                if (comando.isEmpty()) return;

                out.writeObject(comando);
                out.writeObject(val1);
                if ("autoreAnno".equals(tipoRicerca.get())) {
                    out.writeObject(Integer.parseInt(val2));
                }
                out.writeObject(offset.get());
                out.writeObject(LIMIT);

                @SuppressWarnings("unchecked")
                List<String[]> risultati = (List<String[]>) in.readObject();
                risultatiTable.getItems().addAll(risultati);

                btnAvanti.setDisable(risultati.size() < LIMIT);
                btnIndietroPagina.setDisable(offset.get() == 0);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        btnTitolo.setOnAction(e -> {
            tipoRicerca.set("titolo");
            input1.setPromptText("Inserisci titolo");
            input2.setVisible(false);
            searchInput.setVisible(true);
            offset.set(0);
        });

        btnAutore.setOnAction(e -> {
            tipoRicerca.set("autore");
            input1.setPromptText("Inserisci autore");
            input2.setVisible(false);
            searchInput.setVisible(true);
            offset.set(0);
        });

        btnAutoreAnno.setOnAction(e -> {
            tipoRicerca.set("autoreAnno");
            input1.setPromptText("Inserisci autore");
            input2.setPromptText("Inserisci anno");
            input2.setVisible(true);
            searchInput.setVisible(true);
            offset.set(0);
        });

        btnCerca.setOnAction(e -> {
            offset.set(0);
            performSearch.run();
        });

        btnAvanti.setOnAction(e -> {
            offset.set(offset.get() + LIMIT);
            performSearch.run();
        });

        btnIndietroPagina.setOnAction(e -> {
            if (offset.get() >= LIMIT) {
                offset.set(offset.get() - LIMIT);
                performSearch.run();
            }
        });

        btnDettagli.setOnAction(e -> {
            String[] selezionato = risultatiTable.getSelectionModel().getSelectedItem();
            if (selezionato == null) return;

            String titolo = selezionato[0];
            dettaglioBox.getChildren().clear();

            try {
                out.writeObject("getValutazioniLibro");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String[]> valutazioni = (List<String[]>) in.readObject();

                if (valutazioni.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessuna valutazione disponibile per questo libro."));
                } else {
                    for (String[] v : valutazioni) {
                        double media = (
                                Integer.parseInt(v[1]) +
                                Integer.parseInt(v[3]) +
                                Integer.parseInt(v[5]) +
                                Integer.parseInt(v[7]) +
                                Integer.parseInt(v[9])
                        ) / 5.0;

                        dettaglioBox.getChildren().add(new Label("L'utente " + v[0] + " ha valutato questo libro (media: " + String.format("%.2f", media) + "):"));
                        dettaglioBox.getChildren().add(new Label(" - Stile: " + v[1] + (v[2].isEmpty() ? "" : " - " + v[2])));
                        dettaglioBox.getChildren().add(new Label(" - Contenuto: " + v[3] + (v[4].isEmpty() ? "" : " - " + v[4])));
                        dettaglioBox.getChildren().add(new Label(" - Gradevolezza: " + v[5] + (v[6].isEmpty() ? "" : " - " + v[6])));
                        dettaglioBox.getChildren().add(new Label(" - Originalità: " + v[7] + (v[8].isEmpty() ? "" : " - " + v[8])));
                        dettaglioBox.getChildren().add(new Label(" - Edizione: " + v[9] + (v[10].isEmpty() ? "" : " - " + v[10])));
                        dettaglioBox.getChildren().add(new Label(" - Voto Finale: " + v[11]));
                        dettaglioBox.getChildren().add(new Label(""));
                    }
                }

                out.writeObject("getUtentiCheConsigliano");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String> consigliano = (List<String>) in.readObject();

                if (consigliano.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessun utente ha consigliato questo libro."));
                } else {
                    
                	VBox utentiBox = new VBox(2); 
                	Label header = new Label("Utenti che hanno consigliato questo libro:");
                	utentiBox.getChildren().add(header);

                	for (int i = 0; i < consigliano.size(); i++) {
                	    String utente = consigliano.get(i);
                	    Label utenteLabel = new Label((i + 1) + ". " + utente);
                	    utentiBox.getChildren().add(utenteLabel);
                	}

                	dettaglioBox.getChildren().add(utentiBox);
                }

                out.writeObject("getLibriConsigliatiPerLibro");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String[]> consigliati = (List<String[]>) in.readObject();

                Map<String, List<String>> mapConsigli = new HashMap<>();
                for (String[] c : consigliati) {
                    mapConsigli.computeIfAbsent(c[0], k -> new ArrayList<>()).add(c[1]);
                }

                if (mapConsigli.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessun consiglio disponibile per questo libro."));
                } else {
                    for (Map.Entry<String, List<String>> entry : mapConsigli.entrySet()) {
                        
                    	VBox consigliUtenteBox = new VBox(2); // margine tra le righe
                    	Label utenteLabel = new Label("L'utente " + entry.getKey() + " consiglia i seguenti libri:");
                    	consigliUtenteBox.getChildren().add(utenteLabel);

                    	List<String> libri = entry.getValue();
                    	for (int i = 0; i < libri.size(); i++) {
                    	    String libro = libri.get(i);
                    	    Label libroLabel = new Label((i + 1) + ". " + libro);
                    	    consigliUtenteBox.getChildren().add(libroLabel);
                    	}

                    	dettaglioBox.getChildren().add(consigliUtenteBox);
                    }
                }

                out.writeObject("getCommentiLibro");
                out.writeObject(titolo);
                @SuppressWarnings("unchecked")
                List<String[]> commenti = (List<String[]>) in.readObject();

                Map<String, List<String>> mapCommenti = new HashMap<>();
                for (String[] c : commenti) {
                    mapCommenti.computeIfAbsent(c[0], k -> new ArrayList<>()).add(c[2]);
                }

                if (mapCommenti.isEmpty()) {
                    dettaglioBox.getChildren().add(new Label("Nessun commento per questo libro."));
                } else {
                    for (Map.Entry<String, List<String>> entry : mapCommenti.entrySet()) {
                        dettaglioBox.getChildren().add(new Label("Commenti dell'utente " + entry.getKey() + ":"));
                        for (String commento : entry.getValue()) {
                            dettaglioBox.getChildren().add(new Label("   - " + commento));
                        }
                    }
                }

                scrollPane.setVisible(true);
                dettaglioBox.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                dettaglioBox.getChildren().add(new Label("Errore durante il caricamento dei dettagli."));
                scrollPane.setVisible(true);
            }
        });

        btnAggregati.setOnAction(ev -> {
            String[] selezionato2 = risultatiTable.getSelectionModel().getSelectedItem();
            if (selezionato2 == null) return;

            String titolo2 = selezionato2[0];
            dettaglioBox.getChildren().clear();

            try {
                out.writeObject("getStatisticheAggregate");
                out.writeObject(titolo2);
                String[] stats = (String[]) in.readObject();

                dettaglioBox.getChildren().add(new Label("Statistiche aggregate per il libro \"" + titolo2 + "\":"));
                dettaglioBox.getChildren().add(new Label("Numero valutazioni: " + stats[0]));
                dettaglioBox.getChildren().add(new Label("Media Stile: " + stats[1]));
                dettaglioBox.getChildren().add(new Label("Media Contenuto: " + stats[2]));
                dettaglioBox.getChildren().add(new Label("Media Gradevolezza: " + stats[3]));
                dettaglioBox.getChildren().add(new Label("Media Originalità: " + stats[4]));
                dettaglioBox.getChildren().add(new Label("Media Edizione: " + stats[5]));
                dettaglioBox.getChildren().add(new Label("Numero utenti che consigliano: " + stats[6]));
                dettaglioBox.getChildren().add(new Label("Numero persone che hanno aggiunto note: " + stats[7]));

                scrollPane.setVisible(true);
                dettaglioBox.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                dettaglioBox.getChildren().add(new Label("Errore durante il caricamento delle statistiche aggregate."));
                scrollPane.setVisible(true);
            }
        });

        btnIndietro.setOnAction(ev -> {
            
        	saveWindowState();
            primaryStage.setScene(menuScene);
            restoreWindowState();
            
            
        });
    }
    
    
    private void saveWindowState() {
        savedWidth = primaryStage.getWidth();
        savedHeight = primaryStage.getHeight();
        savedX = primaryStage.getX();
        savedY = primaryStage.getY();
        wasMaximized = primaryStage.isMaximized();
    }
    
    
    private void restoreWindowState() {
        if (wasMaximized) {
            primaryStage.setMaximized(true);
        } else {
            primaryStage.setWidth(savedWidth);
            primaryStage.setHeight(savedHeight);
            if (savedX >= 0 && savedY >= 0) {
                primaryStage.setX(savedX);
                primaryStage.setY(savedY);
            }
        }
    }
	    
	    
	    

	public static void main(String[] args) {
		
	    launch(args);
		// TODO Auto-generated method stub

	}

}
