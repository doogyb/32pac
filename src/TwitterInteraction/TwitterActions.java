package TwitterInteraction;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterActions {
	/**
 * Created by Fergus on 08/04/15.
 */
	
	private final static String CONSUMER_KEY = "";
	private final static String CONSUMER_KEY_SECRET = "";
	
	private Twitter twitter = new TwitterFactory().getInstance();

	 public void authorization() {
	        try {        
	         twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
	          
	         String accessToken = getSavedAccessToken();
	         String accessTokenSecret = getSavedAccessTokenSecret();
	 
	         AccessToken oauthAccessToken = new AccessToken(accessToken, accessTokenSecret);
	          
	         twitter.setOAuthAccessToken(oauthAccessToken);
	        } catch (Exception e) {
	            System.out.println("Authorization failed!!!");
	        }
	    }
	     
	    public void postTweet(String text){
	         try{
	             twitter.updateStatus(text);
	             System.out.println("Tweet Successful: '" + text + "'");
	         }
	         catch(Exception e){
	             System.out.println("Tweet Error!!!!!!!");
	         }
	    }
	     
	    public void getTimeLine() throws TwitterException{
	        ResponseList<Status> list = twitter.getHomeTimeline();
	        for(Status each: list){
	            System.out.println("Sent By: @" + each.getUser().getScreenName() + " - " + " " + each.getUser().getName() + "\n" + each.getText() + "\n");
	        }   
	    }
	     
	    private String getSavedAccessToken(){
	        return "";
	    }
	     
	    private String getSavedAccessTokenSecret(){
	        return "";
	         
	    }   
	     
}
