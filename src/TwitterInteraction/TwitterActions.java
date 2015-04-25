package TwitterInteraction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import AI.LyricChooser;
import AI.NaturalLanguage;
import AI.RhymeLine;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trends;
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
	private static String ourUserNameMention = "@32_Pac";
	private static Twitter twitter = new TwitterFactory().getInstance();
	private TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	ArrayList<Tweet> currentTweets = new ArrayList<Tweet>();

	public String handleTweets(){
		System.out.println("[+] Generating tweet ..");
		//System.out.println("Contents of cw: " + currentTweets);
		int bestScore = 0, currentScore = 0;
		RhymeLine bestRhymeLine = null;
		String username = null;
		for (Tweet tweet : currentTweets) {
			System.out.println("TWEET = " + getTweetText(tweet));
			RhymeLine currentRhymeLine = getTweetText(tweet);
			currentScore = currentRhymeLine.get_score();
			if (currentScore > bestScore){
				bestRhymeLine = currentRhymeLine;
				bestScore = currentScore;
				username = tweet.getUserName();
			}
		}
		return bestRhymeLine.toString() + "\n@" + username;
	}

	public static RhymeLine getTweetText(Tweet tw) {
		LyricChooser lc = new LyricChooser(tw);
		lc.chooseLyrics();
		return lc.selectBest();
	}

	public void authorization() {
		readKeys();
		readTokens();
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

	public void listener(){
		final UserStreamListener userStreamListener = new UserStreamListener() {
			@Override
			public void onDirectMessage(DirectMessage message) {
				System.out.println("Replying to direct message");
				HashtagEntity[] hashtagList = message.getHashtagEntities();
				Set<String> hashtags = new HashSet<String>();
				for (HashtagEntity hash : hashtagList) {
					hashtags.add(hash.getText());
				}
				String tweetText = getTweetText(new Tweet(message.getText(), hashtags, message.getSender().getScreenName())).toString();
				postTweet(tweetText+"\n@" +message.getSenderScreenName());
			}
			@Override
			public void onStatus(Status status) {
				if (status.getText().contains(ourUserNameMention)) {
					System.out.println("[+] Recieved tweet from " + status.getUser().getScreenName() +
							"saying " + status.getText());
					HashtagEntity[] hashtagList = status.getHashtagEntities();
					Set<String> hashtags = new HashSet<String>();
					for (HashtagEntity hash : hashtagList) {
						hashtags.add(hash.getText());
					}
					String text = status.getText();

					if (text.length() < 1) return;
					String tweetText = getTweetText(new Tweet(text, hashtags, status.getUser().getScreenName())).toString();
					postTweet(tweetText + "\n@" + status.getUser().getScreenName());
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

	public void trendTweetListener(){
		StatusListener listener = new StatusListener() {
			int counter = 0;
			@Override
			public void onException(Exception arg0) {}
			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {}
			@Override
			public void onScrubGeo(long arg0, long arg1) {}
			@Override
			public void onStatus(Status status) {
				HashtagEntity[] hashtagList = status.getHashtagEntities();
				Set<String> hashtags = new HashSet<String>();
				for (HashtagEntity hash : hashtagList){
					hashtags.add(hash.getText());
				}
				if (counter < 1){
					currentTweets.add(new Tweet(status.getText(), hashtags, status.getUser().getScreenName()));
					System.out.println("\n[+] GETTING STATUS:" + status.getText());
					counter++;
				}
				else {
					System.out.println("\n [+] Quitting listener.");
					twitterStream.shutdown();
				}
			}
			@Override
			public void onTrackLimitationNotice(int arg0) {}
			@Override
			public void onStallWarning(StallWarning arg0) {}
		};
		while (true){
			FilterQuery fq = new FilterQuery();
			fq.track(getTrends());
			twitterStream.addListener(listener);
			twitterStream.filter(fq);
			try {
				TimeUnit.SECONDS.sleep(10);          
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			System.out.println("Resuming.");
			//postTweet(handleTweets());
			System.out.println("[++] Final tweet is " + handleTweets());
			twitterStream.removeListener(listener);
		}
	}

	public String[] getTrends(){
		Trends trends = null;
		String[] out = new String[3];
		try {
			trends = twitter.getPlaceTrends(23424977); //us woeid
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 3; i++) {
			out[i] = trends.getTrends()[i].getName();
			System.out.println(out[i]);
		}
		return out;
	}
}
