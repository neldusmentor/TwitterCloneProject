package twitterclone;

public class User {
  
    private String userName, passWd;

    public User() {
    	this.userName = "";
        this.passWd = "";
    }
    
    public User(String userName, String passWd) {
        this.userName = userName;
        this.passWd = passWd;
    }

    public void setUsername(String userName) {
    	this.userName = userName;
    }
    
    public void setPasswd(String passwd) {
    	this.passWd = passwd;
    }
    
    
	public String getUsername() {
		return this.userName;
	}
	
    @Override
    public String toString() {
        return String.format(
                "User[username='%s', password='%s']",
                userName, passWd);
    }

    // getters & setters omitted for brevity
}

