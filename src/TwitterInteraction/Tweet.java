package TwitterInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import AI.NaturalLanguage;

/**
 * Created by samuel on 08/04/15.
 */
public class Tweet {
    private String rhymeWord, text, userName;
    private ArrayList<String> hashTags = new ArrayList<String>();

    public Tweet (String text, ArrayList<String> inputHashTags, String userName) {
        this.text = text;
        rhymeWord = NaturalLanguage.getLastTweetWord(text);
        this.userName = userName;
        for (String hashTag : inputHashTags) {
            // this.hashTags.addAll(NaturalLanguage.splitString(hashTag));
            this.hashTags.addAll(Arrays.asList(NaturalLanguage.splitHashtag(hashTag)));
        }
    }

    public String getRhymeWord() {return rhymeWord;}

    public String getUserName() { return userName; }
    
    public ArrayList<String> getHashtags() { return hashTags; }
    
    public boolean hasHashtags(){ return !(hashTags == null); }

    public String toString() {
        return text + "\n\nRhymeword: " + rhymeWord + "\n\n userName: " + userName;
    }
}
