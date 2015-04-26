package AI;

import TwitterInteraction.Tweet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by samuel on 08/04/15.
 */

public class LyricChooser {
    public static final int MIN_LINE_LENGTH = 5;
    public static final int MAX_TWEET_LENGTH = 140;

    private Tweet tweet;
    public ArrayList<RhymeLine> rhymeLines = new ArrayList<RhymeLine>();
    protected HashMap<Integer,ArrayList<String>> rhymeList = new HashMap<Integer, ArrayList<String>>();

    public LyricChooser(Tweet tweet) {
        this.tweet = tweet;
        this.rhymeList = NaturalLanguage.getRhymes(tweet.getRhymeWord());
        System.out.println("[++] rhyming with " + tweet.toString());
    }

    public void chooseLyrics() {
        File[] songs = new File("lyrics").listFiles();
        BufferedReader br = null;
        String line1=null, line2=null, lastWord1, lastWord2;

        for (File song : songs) {
            try {
                br = new BufferedReader(new FileReader(song));
                if (song.length() < 500) continue; // ignore empty or nearly empty files

                try {
                    line2 = br.readLine();
                } catch (IOException e) {e.printStackTrace();}

                while (true) {
                    try {
                        line1 = line2;

                        if ((line2 = br.readLine()) == null) break;

                        if (line1.length() < MIN_LINE_LENGTH || line2.length() < MIN_LINE_LENGTH) continue;
                        if (line1.contains("[") || line2.contains("[")) continue;
                        if (NaturalLanguage.getLastWord(line1) == null ||
                                NaturalLanguage.getLastWord(line2) == null) continue;
                        if ((line1 + line2).length() > (MAX_TWEET_LENGTH + tweet.getUserName().length() + 1))
                            continue; // rhyme is too long to be tweeted

                        lastWord1 = NaturalLanguage.getLastWord(line1);
                        lastWord2 = NaturalLanguage.getLastWord(line2);

                        for (Integer key : rhymeList.keySet()) {
                            if (rhymeList.get(key).contains(lastWord1) && rhymeList.get(key).contains(lastWord2)) {
                                rhymeLines.add(new RhymeLine(line1, line2, key));
                                break;
                            }
                        }
                    } catch (IOException e) {e.printStackTrace();}
                }
            } catch (IOException e) { e.printStackTrace(); } finally {
                try {if (br != null)br.close();}
                catch (IOException ex) {ex.printStackTrace();}
            }
        }
    }

    public RhymeLine selectBest() {
        if (rhymeLines.size() == 0) return null;
        int maxScore = 0;
        RhymeLine bestLine = rhymeLines.get(0);
        for (RhymeLine line : rhymeLines) {
            String line1Comp = line.line1.toLowerCase();
            String line2Comp = line.line2.toLowerCase(); // for proper comparisons.
            String fullRhyme = line1Comp + "\n" + line2Comp;

            //check if last word and rhyme word have the same number of sels
            if (NaturalLanguage.numberOfSyllables(tweet.getRhymeWord())==line.syllables) line.score+=10;

            //check if last word is contained in line
            if (line1Comp.contains(tweet.getRhymeWord().toLowerCase())) line.score += 10;
            if (line2Comp.contains(tweet.getRhymeWord().toLowerCase())) line.score += 10;

            //checks if it contains you
            if (line1Comp.contains("you") || line1Comp.contains("your") || line1Comp.contains("you're")) line.score += 10;
            if (line2Comp.contains("you") || line2Comp.contains("your") || line2Comp.contains("you're")) line.score += 10;

            //checks if it contains a question
            if (line1Comp.contains("?")) line.score += 10;
            if (line2Comp.contains("?")) line.score += 10;

            if (tweet.getHashtags().size()>0) {
                for (String hashTag : tweet.getHashtags()) {
                    if (fullRhyme.contains(hashTag.toLowerCase())) line.score += 15;
                }
            }

            if (line.score>maxScore) {
                bestLine=line;
                maxScore=line.score;
            }

        }
        bestLine.setScore(maxScore);
        return bestLine;
    }
}
