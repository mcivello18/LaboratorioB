package bookrecommender;
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class serverBR {
	
	public final static int PORT=8080;
	
	public void exec() throws IOException{
		ServerSocket server=new ServerSocket(PORT);
		System.out.println("Server avviato. In ascolto sulla porta " + PORT);
		Scanner sc=new Scanner(System.in);
		System.out.println("user: ");
		String user=sc.nextLine();
		System.out.println("password: ");
		String password=sc.nextLine();
		DBConfigurazione d=new DBConfigurazione(user, password);
		BRDatabaseManager dbManager =null;
		
		try {
			dbManager=new BRDatabaseManager(d);
		}catch(Exception e) {
			System.out.println("Errore: impossibile connettersi al database. Verifica le credenziali.");
			
			return;
		}
		
		
		try {
			
			while(true) {
				Socket s=server.accept();
				try {
					new ServerSlave(s, dbManager);
				}catch(IOException e) {
					s.close();
				}
			}
		}finally {
			sc.close();
			server.close();
		}
	}

	public static void main(String[] args) {
		try {
			new serverBR().exec();
		}catch(IOException e) {
			e.printStackTrace();
		}
    }
		// TODO Auto-generated method stub

	

}
