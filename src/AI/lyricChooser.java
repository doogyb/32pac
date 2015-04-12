package AI;

import TwitterInteraction.Tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by samuel on 08/04/15.
 */

public class lyricChooser {

    public static final int MIN_LINE_LENGTH = 5;

    private Tweet tweet;
    protected ArrayList<RhymeLine> rhymeLines = new ArrayList<RhymeLine>();
    protected HashMap<String,ArrayList<String>> rhymeList = new HashMap<String, ArrayList<String>>();

    public lyricChooser(Tweet tweet) {
        this.tweet = tweet;
        this.rhymeList = new RhymeGenerator().getRhymes(tweet.getRhymeWord());
    }

    private boolean isVowel(char c) {
        return (c == 'e' || c == 'a' || c == 'i' || c == 'o' || c == 'u' || c == 'y');
    }

    private int numberOfSyllables() {
        int syllablesCount = 0;
        char[] word = tweet.getRhymeWord().toCharArray();

        for (int i = 0; i < word.length-1; i++) {
            if (isVowel(word[i])) {
                syllablesCount++;
                while (isVowel(word[i])) i++;
            }
        }

        return syllablesCount;
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
                    if(line1.split(" ").length<1 || line2.split(" ").length<1) continue;


                    words = line1.split(" ");
                    lastWord1 = words[words.length - 1].toLowerCase();
                    words = line2.split(" ");
                    lastWord2 = words[words.length-1].toLowerCase();

                    for (String key : rhymeList.keySet()) {
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
        lyricChooser lc = new lyricChooser(tw);
        System.out.println(lc.numberOfSyllables());
//        lc.chooseLyrics();
//        for (RhymeLine line : lc.rhymeLines)
//            System.out.println(line.toString());
    }

}
