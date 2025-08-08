package bookrecommender;

public class DBConfigurazione {
	
	private String username;
    private String password;
    
    public DBConfigurazione(String username, String password) {
    	this.username=username;
    	this.password=password;
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public String getPassword() {
    	return this.password;
    }

}
