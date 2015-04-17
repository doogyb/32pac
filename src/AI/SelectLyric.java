package AI;

import TwitterInteraction.Tweet;

import java.util.ArrayList;

/**
 * Created by samuel on 16/04/15.
 */
public class SelectLyric {
    private ArrayList<RhymeLine> rhymeLines;
    private Tweet tweet;
    public SelectLyric(ArrayList<RhymeLine> rhymeLines, Tweet tweet) {
        this.rhymeLines = rhymeLines;
        this.tweet = tweet;
    }

    public RhymeLine selectBest() {
        int maxScore = 0;
        RhymeLine bestLine=null;
        for (RhymeLine line : rhymeLines) {
            if (NaturalLanguage.numberOfSyllables(tweet.getRhymeWord())==line.syllables) {
                line.score+=10;
            }

            if (line.score>maxScore) {
                bestLine=line;
                maxScore=line.score;
            }

        }
        return bestLine;
    }
}
