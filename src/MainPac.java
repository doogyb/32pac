import AI.NaturalLanguage;
import AI.RhymeLine;
import AI.LyricChooser;
import TwitterInteraction.Tweet;
import TwitterInteraction.TwitterActions;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by doogy on 17/04/15.
 */

public class MainPac {

    public static void main (String[] args) {
//        TwitterActions TA = new TwitterActions();
//        TA.authorization();
//        TA.listener();

        System.out.println(NaturalLanguage.removeLastWords("hello my good frined th!@ere.?! @zaid"));
    }
}
