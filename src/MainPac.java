import AI.NaturalLanguage;
import TwitterInteraction.TwitterActions;

/**
 * Created by doogy on 17/04/15.
 */

public class MainPac {

    public static void main (String[] args) {
        TwitterActions TA = new TwitterActions();
        TA.authorization();
        TA.listener();
        TA.trendTweetListener();

    }
}
