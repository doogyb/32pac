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
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
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
import AI.LyricChooser;
import AI.NaturalLanguage;
import AI.RhymeLine;

public class TwitterActions {
	/**
	 * Created by Fergus on 08/04/15.
	 */

	public  static final int MAX_TWEETS = 10;

	private static String CONSUMER_KEY = "", CONSUMER_KEY_SECRET = "", accessToken = "", accessTokenSecret = "";
	private static String ourUserNameMention = "@32_Pac";
	private static Twitter twitter = new TwitterFactory().getInstance();
	private TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	ArrayList<Tweet> currentTweets = new ArrayList<Tweet>();

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

	public void listener(){
		final UserStreamListener userStreamListener = new UserStreamListener() {
			@Override
			public void onDirectMessage(DirectMessage message) {
				System.out.println("Replying to direct message");
				respondToMention(message.getHashtagEntities(), message.getText(),
						message.getSender().getScreenName(), message.getSenderScreenName());
			}

			@Override
			public void onStatus(Status status) {
				if (status.getText().contains(ourUserNameMention)) {
					System.out.println("\n[+] Recieved tweet from " + status.getUser().getScreenName() +
							"saying " + status.getText());

					respondToMention(status.getHashtagEntities(), status.getText(),
							status.getUser().getScreenName(), status.getUser().getScreenName());
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
		StatusListener trendListener = new StatusListener() {
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
				ArrayList<String> hashTags = new ArrayList<String>();
				for (HashtagEntity hash : hashtagList) {hashTags.add(hash.getText()); }

				if (counter < MAX_TWEETS) {
					currentTweets.add(new Tweet(status.getText(), hashTags, status.getUser().getScreenName()));
					System.out.println("[+] GETTING STATUS:" + status.getText());
					System.out.println("[+] Using these hashTag words: " + currentTweets.get(counter).getHashtags());
					counter++;
				} else {
					System.out.println("\n [+] Quitting listener.");
					counter = 0;
					while (true){}
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
			twitterStream.addListener(trendListener);
			twitterStream.filter(fq);
			try { TimeUnit.SECONDS.sleep(10); }
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			twitterStream.shutdown();
			twitterStream.cleanUp();
			twitterStream.removeListener(trendListener);
			System.out.println("\n[+] Resuming.");
			handleTweets();
		}
	}

	private void respondToMention(HashtagEntity[] hashtagList, String tweet, String username, String toUsername) {
		ArrayList<String> hashTags = new ArrayList<String>();
		for (HashtagEntity hash : hashtagList) hashTags.add(hash.getText());

		RhymeLine tweetText = getTweetText(new Tweet(tweet, hashTags, username));
		if (tweetText != null) postTweet(tweetText.toString()+"\n@" + toUsername);
		else {System.out.println("[-] Could not rhyme with that tweet :(");}
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
				bestRhymeLine = currentRhymeLine;
				bestScore = currentScore;
				username = tweet.getUserName();
			}
		}
		currentTweets.clear();
		if (bestRhymeLine == null) return null;
		return NaturalLanguage.filter(bestRhymeLine.toString()) + "\n@" + username;
	}

	public static RhymeLine getTweetText(Tweet tw) {
		LyricChooser lc = new LyricChooser(tw);
		lc.chooseLyrics();
		return lc.selectBest();
	}

	public void postTweet(String text){
		try{
			System.out.println("\n[*] Posting " + text);
			//twitter.updateStatus(text);
			System.out.println("[+]Tweet Successful: '" + text + "'");
		} catch(Exception e) { System.out.println("Tweet Error!!!!!!!");}
	}
}
