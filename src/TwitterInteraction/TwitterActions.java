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
import java.util.concurrent.TimeUnit;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import AI.LyricChooser;
import AI.NaturalLanguage;
import AI.RhymeLine;

public class TwitterActions {
	/**
	 * Created by Fergus on 08/04/15.
	 */

	public static final int MAX_TWEETS = 5, TREND_TIME = 2, STANDBY_TIME = 12;
	private static String CONSUMER_KEY = "", CONSUMER_KEY_SECRET = "", accessToken = "", accessTokenSecret = "";
	private static String ourUserNameMention = "@32_Pac";
	private static Twitter twitter = new TwitterFactory().getInstance();
	private TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	private static long statusId = 0;
	ArrayList<Tweet> currentTweets = new ArrayList<Tweet>();
	private int globalCounter = 0;

	//Sets the keys and tokens.
	public void authorization() {
		readKeys();
		readTokens();
		try {
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
			twitterStream.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
			AccessToken oauthAccessToken = new AccessToken(accessToken, accessTokenSecret);
			twitter.setOAuthAccessToken(oauthAccessToken);
			twitterStream.setOAuthAccessToken(oauthAccessToken);
		} catch (Exception e) { System.out.println("Authorization failed!!!"); }
	}

	//Obtain a new set of tokens.
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
		} catch (IOException e) { System.out.println("Error while saving file:" + e.getMessage()); }
	}

	//Read the keys from a file.
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
		}catch(Exception e){ System.out.println("Error reading file: " + e.getMessage()); }
		CONSUMER_KEY = secret[0];
		CONSUMER_KEY_SECRET = secret[1];
	}

	//Read the tokens from a file.
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
		}catch(Exception e){ System.out.println("Error reading file: " + e.getMessage()); }
		accessToken = secret[0];
		accessTokenSecret = secret[1];
	}

	//Listen to tweets containing trending hashtags.
	public void trendTweetListener(){
		final StatusListener trendListener = new StatusListener() {

			@Override
			public void onException(Exception arg0) {}
			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {}
			@Override
			public void onScrubGeo(long arg0, long arg1) {}
			@Override
			public void onStatus(Status status) {
				if (status.getText().contains(ourUserNameMention)){
					System.out.println("\n[+] Recieved tweet from " + status.getUser().getScreenName() +
							"saying " + status.getText());
					statusId = status.getId();
					respondToMention(status.getHashtagEntities(), status.getText(),
							status.getUser().getScreenName(), status.getUser().getScreenName());
				}

				if (globalCounter < MAX_TWEETS) {
					HashtagEntity[] hashtagList = status.getHashtagEntities();
					ArrayList<String> hashTags = new ArrayList<String>();
					for (HashtagEntity hash : hashtagList) {hashTags.add(hash.getText()); }
					currentTweets.add(new Tweet(status.getText(), hashTags, status.getUser().getScreenName(), status.getId()));
					System.out.println("\n[+] Getting status: " + status.getText());
					System.out.println("[+] Using these hashTag words: " + currentTweets.get(globalCounter).getHashtags());
					globalCounter++;
				}
			}
			@Override
			public void onTrackLimitationNotice(int arg0) {}
			@Override
			public void onStallWarning(StallWarning arg0) {}
		};

		while (true){
			FilterQuery fq = new FilterQuery();
			String[] queries = getTrends();
			queries[queries.length - 1] = ourUserNameMention;
			fq.track(queries);
			twitterStream.addListener(trendListener);
			twitterStream.filter(fq);
			try { TimeUnit.MINUTES.sleep(TREND_TIME); }
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			postTweet(handleTweets());
			twitterStream.shutdown();
			twitterStream.cleanUp();
			twitterStream.removeListener(trendListener);
			String name[] = {ourUserNameMention};
			fq.track(name);
			twitterStream.addListener(trendListener);
			twitterStream.filter(fq);
			try {
				System.out.println("\n[+] Listening to tweets directed at us.");
				TimeUnit.HOURS.sleep(STANDBY_TIME);
				globalCounter = 0;
			}
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			System.out.println("\n[+] Resuming.");
			twitterStream.shutdown();
			twitterStream.cleanUp();
			twitterStream.removeListener(trendListener);
		}
	}

	//Generate responses to users mentioning 32Pac.
	private void respondToMention(HashtagEntity[] hashtagList, String tweet, String username, String toUsername) {
		ArrayList<String> hashTags = new ArrayList<String>();
		for (HashtagEntity hash : hashtagList) hashTags.add(hash.getText());
		RhymeLine tweetText = getTweetText(new Tweet(tweet, hashTags, username, statusId));
		if (tweetText != null) {
			postTweet(tweetText.toString()+"\n@" + toUsername);
		}
		else {System.out.println("[-] Could not rhyme with that tweet :(");}
	}

	//Obtain trending hashtags.
	public String[] getTrends(){
		Trends trends = null;
		String[] out = new String[4];
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

	//Iterates through all obtained tweets and picks the best rhyme from them.
	public String handleTweets(){
		System.out.println("[+] Generating tweets, please wait ... ");
		int bestScore = 0, currentScore = 0;
		RhymeLine bestRhymeLine = null;
		String username = null;
		for (Tweet tweet : currentTweets) {
			RhymeLine currentRhymeLine = getTweetText(tweet);
			if (currentRhymeLine == null) continue;
			currentScore = currentRhymeLine.getScore();
			if (currentScore > bestScore){
				statusId = tweet.getStatusId();
				bestRhymeLine = currentRhymeLine;
				bestScore = currentScore;
				username = tweet.getUserName();
			}
		}
		currentTweets.clear();
		if (bestRhymeLine == null) return null;
		return NaturalLanguage.filter("@" + username + " " + bestRhymeLine.toString());
	}

	public static RhymeLine getTweetText(Tweet tw) {
		LyricChooser lc = new LyricChooser(tw);
		lc.chooseLyrics();
		return lc.selectBest();
	}

	//Post the generated text to Twitter.
	public void postTweet(String text) {
		try{
			System.out.println("\n[*] Posting " + text);
			if (statusId != 0){
				System.out.println("id: " + statusId);
				StatusUpdate stat = new StatusUpdate(text);
				stat.setInReplyToStatusId(statusId);
				twitter.updateStatus(stat);
			}
			else { twitter.updateStatus(text); }
			System.out.println("[+]Tweet Successful: '" + text + "'");
		} catch(Exception e) { System.out.println("Tweet Error!!!!!!!");}
	}
}
