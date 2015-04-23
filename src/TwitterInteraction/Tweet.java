package TwitterInteraction;

import java.util.ArrayList;
import java.util.Set;

import AI.NaturalLanguage;

/**
 * Created by samuel on 08/04/15.
 */
public class Tweet {
    protected String rhymeWord, text, userName;
    protected ArrayList<String> hashTags;
    public Tweet (String text, Set<String> hashtags, String userName) {
        this.text = text;
        rhymeWord = NaturalLanguage.getLastWord(text);
        this.userName = userName;
        for (String hashtag : hashtags) {
        	hashTags.addAll(NaturalLanguage.splitString(hashtag));
        }
    }
    public String getRhymeWord() { return rhymeWord; }
    public String getUserName() { return userName; }
}
