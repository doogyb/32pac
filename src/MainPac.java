import AI.RhymeLine;
import AI.LyricChooser;
import TwitterInteraction.Tweet;

/**
 * Created by doogy on 17/04/15.
 */

public class MainPac {

    public static void main (String[] args) {
        //I shat myself
        Tweet tw = new Tweet("myself");
        LyricChooser lc = new LyricChooser(tw);
        lc.chooseLyrics();
        System.out.println(lc.selectBest());
        //for (RhymeLine line : lc.rhymeLines) System.out.println(line.toString());
    }
}
