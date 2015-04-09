package TwitterInteraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import twitter4j.DirectMessage;
import twitter4j.HashtagEntity;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterActions {
	/**
	 * Created by Fergus on 08/04/15.
	 */

	private static String CONSUMER_KEY = "", CONSUMER_KEY_SECRET = "", accessToken = "", accessTokenSecret = "";
	private Twitter twitter = new TwitterFactory().getInstance();
	private TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

	public void authorization() {
		try {        
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
			twitterStream.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
			AccessToken oauthAccessToken = new AccessToken(accessToken, accessTokenSecret);
			twitter.setOAuthAccessToken(oauthAccessToken);	
			twitterStream.setOAuthAccessToken(oauthAccessToken);
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

	public void getTokens() throws TwitterException, IOException {	//keys must be set before calling this
		String filename = "tokens.txt";
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		System.out.println("Authorization URL: \n" + requestToken.getAuthorizationURL());
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			try {
				System.out.print("Input PIN here: ");
				String pin = br.readLine();
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			} catch (TwitterException te) {
				System.out.println("Failed to get access token, caused by: " + te.getMessage());
				System.out.println("Retry input PIN");
			}
		}
		br.close();
		System.out.println("Access Token: " + accessToken.getToken());
		System.out.println("Access Token Secret: " + accessToken.getTokenSecret());
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
			writer.write(accessToken.getToken());
			writer.write(System.lineSeparator());
			writer.write(accessToken.getTokenSecret());
			writer.close();
		} catch (IOException e) {
			System.out.println("Error while saving file:" + e.getMessage());  
		}
	}

	public void readKeys() {
		String keys = "keys.txt", secret[] = new String[2]; 
		try{
			FileReader inputFile = new FileReader(keys);
			BufferedReader bufferReader = new BufferedReader(inputFile);
			for(int counter = 0; counter < 2; counter++) { 
				String line = bufferReader.readLine();
				secret[counter] = line;
			}
			bufferReader.close();
			inputFile.close();
		}catch(Exception e){
			System.out.println("Error reading file: " + e.getMessage());                      
		}
		CONSUMER_KEY = secret[0];
		CONSUMER_KEY_SECRET = secret[1];
	} 

	public void readTokens() {
		String tokens = "tokens.txt", secret[] = new String[2];
		try{
			FileReader inputFile = new FileReader(tokens);
			BufferedReader bufferReader = new BufferedReader(inputFile);
			for(int counter = 0; counter < 2; counter++) {
				String line = bufferReader.readLine();
				secret[counter] = line;
			}
			bufferReader.close();
			inputFile.close();
		}catch(Exception e){
			System.out.println("Error reading file: " + e.getMessage());                      
		}
		accessToken = secret[0];
		accessTokenSecret = secret[1];
	}

	public void Listener(){
		UserStreamListener userStreamListener = new UserStreamListener() {
			@Override
			public void onDirectMessage(DirectMessage message) {
				int counter = 0;
				HashtagEntity[] x = message.getHashtagEntities();
				String username = message.getSender().getScreenName(), text = message.getText(), lastword = text.substring(text.lastIndexOf(" ") + 1);
				String[] hashes = new String[46];	//max no of possible hashtags
				for (HashtagEntity hash : x){
					hashes[counter] = hash.getText();
					counter++;
				}
			}
			@Override
			public void onStatus(Status status) {
				int counter = 0;
				HashtagEntity[] x = status.getHashtagEntities();
				String username = status.getUser().getScreenName(), text = status.getText(), lastword = text.substring(text.lastIndexOf(" ") + 1);
				String[] hashes = new String[46];	//max no of possible hashtags
				for (HashtagEntity hash : x){
					hashes[counter] = hash.getText();
					counter++;
				}
			}
			@Override
			public void onException(Exception arg0) {}
			@Override
			public void onTrackLimitationNotice(int arg0) {}
			@Override
			public void onScrubGeo(long arg0, long arg1) {}
			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {}
			@Override
			public void onUserProfileUpdate(User arg0) {}
			@Override
			public void onUserListUpdate(User arg0, UserList arg1) {}
			@Override
			public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListSubscription(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListDeletion(User arg0, UserList arg1) {}
			@Override
			public void onUserListCreation(User arg0, UserList arg1) {}
			@Override
			public void onUnfavorite(User arg0, User arg1, Status arg2) {}
			@Override
			public void onUnblock(User arg0, User arg1) {}
			@Override
			public void onFriendList(long[] arg0) {}
			@Override
			public void onFollow(User arg0, User arg1) {}
			@Override
			public void onFavorite(User arg0, User arg1, Status arg2) {}
			@Override
			public void onDeletionNotice(long arg0, long arg1) {}
			@Override
			public void onBlock(User arg0, User arg1) {}
			@Override
			public void onStallWarning(StallWarning arg0) {}
			@Override
			public void onUnfollow(User arg0, User arg1) {}
		};
		twitterStream.addListener(userStreamListener);
		twitterStream.user();
	}

	public static void main(String[] args) throws Exception {
		TwitterActions client = new TwitterActions();
		client.readKeys();
		//client.getTokens();
		client.readTokens();
		client.authorization();
		client.Listener();
	}
}
