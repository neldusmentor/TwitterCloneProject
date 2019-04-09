package twitterclone;

public class MyTweets {
	String username = "";
	String timestamp = "";
	String tweets = "";
	
	
	public MyTweets() {
		this.username = "";
		this.timestamp = "";
		this.tweets = "";
	}
	
	public MyTweets(String username, String timestamp, String tweets) {
		this.username = username;
		this.timestamp = timestamp;
		this.tweets = tweets;
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setTweets(String tweets) {
		this.tweets = tweets;
	}
	
	
	public String getUsername() {
		return username;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getTweets() {
		return tweets;
	}
}
