package twitterclone;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class TwitterDB {
	private static final Logger log = LoggerFactory.getLogger(JingisTwitterApplication.class);

	JdbcTemplate jt;
	static String userTableName = "twitterusers"; 
	static String followingTableName = "followings"; 
	static String tweetsTableName = "tweets"; 

	public TwitterDB(JdbcTemplate jt) {
		this.jt = jt;
	}

	public void setJDBCTemplate(JdbcTemplate jt) {
		this.jt = jt;
	}

	public void createTables() {
		log.info("Creating tables");
		jt.execute("DROP TABLE " + userTableName + " IF EXISTS");
		jt.execute("CREATE TABLE " + userTableName + "(" +
				"username VARCHAR(255), passwd VARCHAR(255), PRIMARY KEY (username))"); 

		jt.execute("DROP TABLE " + followingTableName + " IF EXISTS");
		jt.execute("CREATE TABLE " + followingTableName + "(" +
				"username VARCHAR(255), follows VARCHAR(255))"); 

		jt.execute("DROP TABLE " + tweetsTableName + " IF EXISTS");
		jt.execute("CREATE TABLE " + tweetsTableName + "(" +
				"username VARCHAR(255), timestamp TIMESTAMP, tweets TEXT)"); 
	}

	/* Register a user to the database */
	public void signUpUser(String username, String passwd) {
		log.info("save to DB user " + username); 
		try {
			jt.execute("INSERT INTO twitterusers(username, passwd) VALUES ('" + username 
					+ "','" + passwd + "');");
		} catch (Exception e) {
			log.error("error sign up user " + e);
		}
	}

	/* Save a tweet to the database */
	public void logTweets(String username, String mytweet) {
		log.info("save tweets to DB: user " + username); 
		try {
			jt.execute("INSERT INTO tweets(username, timestamp, tweets) VALUES ('" + username 
					+ "', CURRENT_TIMESTAMP ,'" + mytweet + "');");
		} catch (Exception e) {
			log.error("error save user tweets" + e);
		}
	}

	/* register a following INSERT INTO FOLLOWINGS values('A', 'B'); */ 
	public void addFollowing(String username, String myfollowing) {
		try {
			String sql = "INSERT INTO FOLLOWINGS values('" + username + "','" + myfollowing + "')";
			log.info("add following from user " + username + ": " + myfollowing);
			jt.execute(sql);			
		} catch (Exception e) {
			log.error("error delete record from DB " + e);
		}
	}

	/* remove a following DELETE from FOLLOWINGS where username='jinghong' and follows='B'; */
	public void removeFollowing(String username, String myfollowing) {
		try {
			String sql = "DELETE from FOLLOWINGS where username='" + username + "' and follows='" + myfollowing + "'";
			log.info("remove following from user " + username + ": " + myfollowing);
			jt.execute(sql);			
		} catch (Exception e) {
			log.error("error delete record from DB " + e);
		}
	}

	/** Find all tweets that belong to the input user, and all tweets from the persons he follows, in desc order
	 * SELECT * from TWEETS where username='jinghong'  OR username in (SELECT FOLLOWS From FOLLOWINGS WHERE username = 'jinghong') ORDER BY timestamp DESC;
	 *  */
	public List<MyTweets> findAllRelatedTweets(String user) {
		List<MyTweets> allTweets = new ArrayList<MyTweets>();

		/** For test only 
		MyTweets mt1 = new MyTweets("jingis", "2019", "I am hungry"); 
		MyTweets mt2 = new MyTweets("jingis", "2018", "I am sleepy"); 

		allTweets.add(mt1);
		allTweets.add(mt2);
		 */

		String sql = "SELECT * FROM " + this.tweetsTableName + " WHERE username='" + user 
				+ "' OR username in (SELECT FOLLOWS From FOLLOWINGS WHERE username='"+ user + "') ORDER BY timestamp DESC";

		try {
			List<Map<String, Object>> rows =  jt.queryForList(sql);
			for (Map row : rows) {
				MyTweets mt = new MyTweets();
				mt.setUsername((String) row.get("username"));
				Timestamp ts = (Timestamp) row.get("timestamp");
				mt.setTimestamp(ts.toString());
				mt.setTweets((String) row.get("tweets"));
				allTweets.add(mt);
			}

			return allTweets;
		} catch (Exception je) {
			log.info("user" + user + " has not tweeted yet " + je);
			return allTweets;
		}
	}

	/** Find all registered twitter accounts that I do not follow, replace then by select from database 
	 * SELECT username from TWITTERUSERS where username!='jinghong'  and username not in (SELECT FOLLOWS From FOLLOWINGS WHERE username = 'jinghong');
	 * */ 
	public List<User> findPossibleFollowings(String user) {
		List<User> possibleFollowings = new ArrayList<User>();
		/** For test use only
		User u1 = new User("A", "");
		User u2 = new User("B", "");

		possibleFollowings.add(u1);
		possibleFollowings.add(u2);
		 */
		String sql = "SELECT username FROM " + this.userTableName + " WHERE username!='" + user 
				+ "' and username not in (SELECT FOLLOWS From " + this.followingTableName + " WHERE username = '" + user + "')";

		/* The SQL will return a list of Array with Strings, "USERNAME" as attribute name */
		try {
			List<Map<String, Object>> rows =  jt.queryForList(sql);
			for (Map row : rows) {
				User usr = new User();
				usr.setUsername((String) row.get("username"));

				possibleFollowings.add(usr);
			}

			return possibleFollowings;
		} catch (Exception je) {
			log.info("user" + user + " has not followed yet " + je);
			return possibleFollowings;
		}
	}

	/** Find all my following, possible to unfollow them 
	 * select follows from followings where username='jinghong';
	 * */
	public List<User> findMyFollowings(String user) {
		List<User> myFollowings = new ArrayList<User>();
		/*** For test use only
		User u1 = new User("C", "");
		User u2 = new User("D", "");

		myFollowings.add(u1);
		myFollowings.add(u2);
		 **/	

		String sql = "SELECT follows FROM " + this.followingTableName + " WHERE username='" + user + "'";

		/* The SQL will return a list of Array with Strings, "USERNAME" as attribute name */
		try {
			List<Map<String, Object>> rows =  jt.queryForList(sql);
			for (Map row : rows) {
				User usr = new User();
				usr.setUsername((String) row.get("follows"));
				myFollowings.add(usr);
			}

			return myFollowings;
		} catch (Exception je) {
			log.info("user" + user + " has not followed anyone yet " + je);
			return myFollowings;
		}

	}

	/** Test only
	public boolean checkUser(String username) {
		try {
			String usr =  (String) jt.queryForObject("select username from " + userTableName + " where username=" 
					+ username, String.class);
			log.info("Found usr " + usr);
			return true;
		} catch (Exception e) {
			log.info("User not exists");
			return false;
		}
	} **/
}
