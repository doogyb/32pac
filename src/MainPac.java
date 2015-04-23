import AI.NaturalLanguage;
import AI.RhymeLine;
import AI.LyricChooser;
import TwitterInteraction.Tweet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by doogy on 17/04/15.
 */

public class MainPac {

    public static void main (String[] args) {
        Tweet tw = new Tweet("my main hammer", new HashSet(), "fergyPoo");
        LyricChooser lc = new LyricChooser(tw);
        lc.chooseLyrics();
        String tweetMessage = lc.selectBest() + "\n@" + tw.getUserName();
        System.out.println(tweetMessage);

        //for (RhymeLine line : lc.rhymeLines) System.out.println(line.toString());
    }
}
