package TwitterInteraction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
    protected static String ourUserNameMention = "@32_Pac";
    private static Twitter twitter;
    private static TwitterStream twitterStream;
    protected static long statusId = 0;
    ArrayList<Tweet> currentTweets = new ArrayList<Tweet>();
    private int globalCounter = 0;

    public TwitterActions() {
        twitter = new TwitterFactory().getInstance();
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new Listener());
        twitterStream.filter(new FilterQuery(0, 3003340575L));
    }





    // Listen to tweets containing trending hashtags.
//    public void trendTweetListener() {
//
//        while (true) {
//            FilterQuery fq = new FilterQuery();
//            String[] queries = getTrends();
//            queries[queries.length - 1] = ourUserNameMention;
//            fq.track(queries);
//            twitterStream.addListener(this);
//            twitterStream.filter(fq);
//            try {
//                TimeUnit.MINUTES.sleep(TREND_TIME);
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//            postTweet(handleTweets());
//            twitterStream.shutdown();
//            twitterStream.cleanUp();
//            twitterStream.removeListener(this);
//            String name[] = {ourUserNameMention};
//            fq.track(name);
//            twitterStream.addListener(this);
//            twitterStream.filter(fq);
//            try {
//                System.out.println("\n[+] Listening to tweets directed at us.");
//                TimeUnit.HOURS.sleep(STANDBY_TIME);
//                globalCounter = 0;
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//            System.out.println("\n[+] Resuming.");
//            twitterStream.shutdown();
//            twitterStream.cleanUp();
//            twitterStream.removeListener(this);
//        }
//    }
//
//    //Generate responses to users mentioning 32Pac.
//    private void respondToMention(HashtagEntity[] hashtagList, String tweet, String username, String toUsername) {
//        ArrayList<String> hashTags = new ArrayList<String>();
//        for (HashtagEntity hash : hashtagList) hashTags.add(hash.getText());
//        RhymeLine tweetText = getTweetText(new Tweet(tweet, hashTags, username, statusId));
//        if (tweetText != null) {
//            postTweet(tweetText.toString() + "\n@" + toUsername);
//        } else {
//            System.out.println("[-] Could not rhyme with that tweet :(");
//        }
//    }
//
//    //Obtain trending hashtags.
//    public String[] getTrends() {
//        Trends trends = null;
//        String[] out = new String[4];
//        try {
//            trends = twitter.getPlaceTrends(23424977); //us woeid
//        } catch (TwitterException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < 3; i++) {
//            out[i] = trends.getTrends()[i].getName();
//            System.out.println(out[i]);
//        }
//        return out;
//    }
//
//    //Iterates through all obtained tweets and picks the best rhyme from them.
//    public String handleTweets() {
//        System.out.println("[+] Generating tweets, please wait ... ");
//        int bestScore = 0, currentScore = 0;
//        RhymeLine bestRhymeLine = null;
//        String username = null;
//        for (Tweet tweet : currentTweets) {
//            RhymeLine currentRhymeLine = getTweetText(tweet);
//            if (currentRhymeLine == null) continue;
//            currentScore = currentRhymeLine.getScore();
//            if (currentScore > bestScore) {
//                statusId = tweet.getStatusId();
//                bestRhymeLine = currentRhymeLine;
//                bestScore = currentScore;
//                username = tweet.getUserName();
//            }
//        }
//        currentTweets.clear();
//        if (bestRhymeLine == null) return null;
//        return NaturalLanguage.filter("@" + username + " " + bestRhymeLine.toString());
//    }
//
//    public static RhymeLine getTweetText(Tweet tw) {
//        LyricChooser lc = new LyricChooser(tw);
//        lc.chooseLyrics();
//        return lc.selectBest();
//    }
//
//    //Post the generated text to Twitter.
//    public void postTweet(String text) {
//        Status status = null;
//        try {
//            status = twitter.updateStatus(text);
//        } catch (TwitterException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        twitter = TwitterFactory.getSingleton();
//        Status status = null;
//        try {
//            status = twitter.updateStatus("Microphone check");
//        } catch (TwitterException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Successfully updated the status to [" + status.getText() + "].");
////        TwitterActions TA = new TwitterActions();
////        TA.readCredentials();
////        System.out.println(TA.accessToken + TA.accessTokenSecret + TA.consumer_key + TA.consumer_key_secret);
//    }
//
//    @Override
//    public void onStatus(Status status) {
//        if (status.getText().contains(ourUserNameMention)) {
//            System.out.println("\n[+] Recieved tweet from " + status.getUser().getScreenName() +
//                    "saying " + status.getText());
//            statusId = status.getId();
//            respondToMention(status.getHashtagEntities(), status.getText(),
//                    status.getUser().getScreenName(), status.getUser().getScreenName());
//        }
//
//        if (globalCounter < MAX_TWEETS) {
//            HashtagEntity[] hashtagList = status.getHashtagEntities();
//            ArrayList<String> hashTags = new ArrayList<String>();
//            for (HashtagEntity hash : hashtagList) {
//                hashTags.add(hash.getText());
//            }
//            currentTweets.add(new Tweet(status.getText(), hashTags, status.getUser().getScreenName(), status.getId()));
//            System.out.println("\n[+] Getting status: " + status.getText());
//            System.out.println("[+] Using these hashTag words: " + currentTweets.get(globalCounter).getHashtags());
//            globalCounter++;
//        }
//    }
//
//    @Override
//    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
//
//    @Override
//    public void onTrackLimitationNotice(int i) {}
//
//    @Override
//    public void onScrubGeo(long l, long l1) {}
//
//    @Override
//    public void onStallWarning(StallWarning stallWarning) {}
//
//    @Override
//    public void onException(Exception e) {}
}
