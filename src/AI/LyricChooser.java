package AI;

import TwitterInteraction.Tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samuel on 08/04/15.
 */

public class LyricChooser {

    public static final int MIN_LINE_LENGTH = 5;

    private Tweet tweet;
    protected ArrayList<RhymeLine> rhymeLines = new ArrayList<RhymeLine>();
    protected HashMap<Integer,ArrayList<String>> rhymeList = new HashMap<Integer, ArrayList<String>>();

    public LyricChooser(Tweet tweet) {
        this.tweet = tweet;
        this.rhymeList = NaturalLanguage.getRhymes(tweet.getRhymeWord());
    }





    public void chooseLyrics() {
        File[] songs = new File("lyrics").listFiles();
        BufferedReader br = null;
        String line1=null, line2=null, lastWord1, lastWord2;
        String artist=null, songName=null, album=null;
        String[] words;

        for (File song : songs) {
            try { br = new BufferedReader(new FileReader(song)); }
            catch (IOException e) { e.printStackTrace(); }

            if (song.length()<500) continue; // ignore empty or nearly empty files

            //System.out.println(song.getAbsolutePath());

            try {
//                String line = br.readLine();
//                artist = br.readLine().split(":\\s*")[1];
//                album = br. readLine().split(":\\s*")[1];
//                songName = br.readLine().split(":\\s*")[1];
//                br.readLine(); // skipping last line (Typed by)
                line1=br.readLine();
                line2=br.readLine();
            } catch (IOException e) { e.printStackTrace(); }

            while(true) {
                try {

                    line1=line2;

                    if ((line2 = br.readLine()) == null) break;

                    if ( line1.length()<MIN_LINE_LENGTH || line2.length()<MIN_LINE_LENGTH) continue;
                    if (line1.contains("[") || line2.contains("[")) continue;
                    if (NaturalLanguage.getLastWord(line1) == null ||
                            NaturalLanguage.getLastWord(line2)==null) continue;
                    if ( (line1+line2).length() > 140 ) continue; // rhyme is too long to be tweeted


                    lastWord1=NaturalLanguage.getLastWord(line1);
                    lastWord2=NaturalLanguage.getLastWord(line2);

                    for (Integer key : rhymeList.keySet()) {
                        if ( rhymeList.get(key).contains(lastWord1) && rhymeList.get(key).contains(lastWord2) ) {
                            rhymeLines.add( new RhymeLine(line1,line2,artist,album,songName,key));
                            break;
                        }
                    }

                } catch (IOException e) { e.printStackTrace(); }
            }

        }
    }
    public static void main (String[] args) {
        Tweet tw = new Tweet("zaid");
        LyricChooser lc = new LyricChooser(tw);
//        lc.chooseLyrics();
//        for (RhymeLine line : lc.rhymeLines)
//            System.out.println(line.toString());
    }

}
