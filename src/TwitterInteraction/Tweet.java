package TwitterInteraction;

import java.util.ArrayList;

import AI.NaturalLanguage;

/**
 * Created by samuel on 08/04/15.
 */
public class Tweet {
    private String rhymeWord, text, userName;
    private long statusId;
    private ArrayList<String> hashTags = new ArrayList<String>();

    public Tweet(String text, ArrayList<String> inputHashTags, String userName, long statusId) {
        this.text = text;
        rhymeWord = NaturalLanguage.getLastTweetWord(text);
        this.userName = userName;
        this.statusId = statusId;
        for (String hashTag : inputHashTags)
            this.hashTags.addAll(NaturalLanguage.splitHashtag(hashTag));
    }

    public String getRhymeWord() {
        return rhymeWord;
    }

    public String getUserName() {
        return userName;
    }

    public long getStatusId() {
        return statusId;
    }

    public ArrayList<String> getHashtags() {
        return hashTags;
    }

    public String toString() {
        return text + "\nRhymeword: " + rhymeWord + "\nuserName: " + userName;
    }
}
