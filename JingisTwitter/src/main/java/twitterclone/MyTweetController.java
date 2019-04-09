package twitterclone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller	
public class MyTweetController {
	private static final Logger log = LoggerFactory.getLogger(JingisTwitterApplication.class);
	@Autowired	
	JdbcTemplate jdbcTemplate;

	TwitterDB tdb; 

	@PostConstruct
	public void init() {
		// Inititalizing 

		try {
			log.info("Initialize Database... ");
			tdb = new TwitterDB(jdbcTemplate);
		} catch (Exception e) {
			log.error("error inializing database ");
			throw e;
		}
	}

	@RequestMapping("/")
	public String startPage() {
		log.info("start page");
		return "index";
	}

	/* When user logs in, it will map to this method. The parameters were posted from <form> in index.html */
	@PostMapping("/login")
	public String login(@RequestParam(name="username") String username, @RequestParam(name="passwd") String passwd, Model model) {
		model.addAttribute("username", username);
		/** Check if the user already exists in the database. If not, ask user to "sign up" */
		log.info("Logging user " + username);

		/** if the user is found, get his tweets and tweets he follows, and present them to his page */


		@SuppressWarnings("unchecked")
		List<MyTweets> allTweets = (List<MyTweets>) tdb.findAllRelatedTweets(username);
		List myFollowings = (List<User>) tdb.findMyFollowings(username);
		List<User> possibleFollowings = (List<User>) tdb.findPossibleFollowings(username);

		/* twitterCollection is a thymeleaf variable in welcome.html under resources. Here the return variable will be a list of tweets */
		model.addAttribute("twitterCollections", allTweets);
		model.addAttribute("myFollowingCollections", myFollowings);
		model.addAttribute("possibleFollowingCollections", possibleFollowings);

		log.info("redirect to welcome.html");
		return "welcome";
	}

	/** User clicks Update in the welcome.html page, it will be mapped to this method */
	@PostMapping("/update")
	public String updateStatus(@RequestParam(name="username") String username, @RequestParam(name="mytweet") String mytweet, Model model) {
		log.info("Update user status " + username + ": " + mytweet);

		/** Save tweet to the database, update the tweets shown in the welcome page */
		tdb.logTweets(username, mytweet);

		List<MyTweets> allTweets = (List<MyTweets>) tdb.findAllRelatedTweets(username);
		List<User> myFollowings = (List<User>) tdb.findMyFollowings(username);
		List<User> possibleFollowings = (List<User>) tdb.findPossibleFollowings(username);

		/* twitterCollection is a thymeleaf variable in welcome.html under resources. Here the return variable will be a list of tweets */
		model.addAttribute("twitterCollections", allTweets);
		model.addAttribute("myFollowingCollections", myFollowings);
		model.addAttribute("possibleFollowingCollections", possibleFollowings);

		model.addAttribute("username", username);
		return "welcome";
	}

	/** User clicks Unfollow in the welcome.html page, it will be mapped to this method */
	@RequestMapping(value="/unfollow", method=RequestMethod.POST) 
	public String unfollowUser(@RequestParam(name="username") String username, @RequestParam(name="myfollowing") String myfollowing, Model model) {
		log.info(username + " will unfollow user " + myfollowing);
		model.addAttribute("username", username);
		
		tdb.removeFollowing(username, myfollowing);
		
		List<MyTweets> allTweets = (List<MyTweets>) tdb.findAllRelatedTweets(username);
		List<User> myFollowings = (List<User>) tdb.findMyFollowings(username);
		List<User> possibleFollowings = (List<User>) tdb.findPossibleFollowings(username);
		model.addAttribute("twitterCollections", allTweets);
		model.addAttribute("myFollowingCollections", myFollowings);
		model.addAttribute("possibleFollowingCollections", possibleFollowings);
		return "welcome";
	}

	/** User clicks Following in the welcome.html page, it will be mapped to this method */
	@RequestMapping(value="/following", method=RequestMethod.POST) 
	public String followingUser(@RequestParam(name="username") String username, @RequestParam(name="potentials") String pfollowing, Model model) {
		log.info(username + " will follow user " + pfollowing);
		
		tdb.addFollowing(username, pfollowing);
		model.addAttribute("username", username);
		List<MyTweets> allTweets = (List<MyTweets>) tdb.findAllRelatedTweets(username);
		List<User> myFollowings = (List<User>) tdb.findMyFollowings(username);
		List<User> possibleFollowings = (List<User>) tdb.findPossibleFollowings(username);
		model.addAttribute("twitterCollections", allTweets);
		model.addAttribute("myFollowingCollections", myFollowings);
		model.addAttribute("possibleFollowingCollections", possibleFollowings);
		return "welcome";
	}

	/** User clicks Signup in the index.html page, it will be mapped to this method */
	@PostMapping("/signup")	
	public String signup(@RequestParam(name="username") String username, @RequestParam(name="passwd") String passwd, Model model) {
		log.info("sign up user " + username);
		try {
			tdb.signUpUser(username, passwd);
			model.addAttribute("username", username);
			model.addAttribute("signedUpMessage", username + ", you have been signed up, please login");
			return "index";
		} catch (Exception e) {
			log.error("Can't put user " + username + " to the database" + e.toString());
			return "error";  // implements later need to add error.html
		}
	}

	/** Logout and redirected to index.html as init state */
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (Model model) {

		return "redirect:/";
	}

	/** Test methods below, for test purpose only.
	@RequestMapping(value="/createTable", method=RequestMethod.GET)
	public String createTable() { 
		tdb.createTables();
		return "index";
	}

	@RequestMapping(value="/check", method=RequestMethod.GET) 
	public void checkUser(@RequestParam(name="username") String username) {
		tdb.checkUser(username);
	} */
}
