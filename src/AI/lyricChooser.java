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

/**
 * Created by samuel on 08/04/15.
 */

public class lyricChooser {

    private Tweet tweet;
    protected ArrayList<RhymeLine> rhymeLines = new ArrayList<RhymeLine>();
    protected HashMap<String, String[]> rhymeList = new HashMap<String, String[]>();

    public lyricChooser(Tweet tweet) {
        this.tweet = tweet;
        this.rhymeList = new RhymeGenerator().getRhymes(tweet.getRhymeWord());
    }

    public void chooseLyrics() {
        File[] songs = new File("lyrics").listFiles();
        BufferedReader br = null;
        String line1, line2, lastWord1, lastWord2;
        String artist=null, songName=null, album=null;
        String[] words;

        for (String[] rhymes : rhymeList.values()) {
            System.out.println("Rhyming words: " + (Arrays.asList(rhymes)));
        }

        for (File song : songs) {
            try { br = new BufferedReader(new FileReader(song)); }
            catch (IOException e) { e.printStackTrace(); }

            try {
                artist = br.readLine().split(":")[1];
                album = br. readLine().split(":")[1];
                songName = br.readLine().split(":")[1];
                br.readLine(); // skipping last line (Typed by)
            } catch (IOException e) { e.printStackTrace(); }

            while(true) {
                try {
                    if ((line1 = br.readLine()) == null) break;
                    if ((line2 = br.readLine()) == null) break;

                    if ( line1.length()<6 || line2.length()<6) continue;
                    if (line1.contains("[") || line2.contains("[")) continue;

                    words = line1.split(" ");
                    lastWord1 = words[words.length-1].toLowerCase();
                    words = line2.split(" ");
                    lastWord2 = words[words.length-1].toLowerCase();

                    System.out.println("lastword1 : " + lastWord1 + "lastword2 : " + lastWord2);

                    for (String[] rhymes : rhymeList.values()) {
                        if (Arrays.asList(rhymes).contains(lastWord1) && Arrays.asList(rhymes).contains(lastWord2))
                            rhymeLines.add(new RhymeLine(line1, line2, artist, album, songName));
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }

        }
    }
    public static void main (String[] args) {
        Tweet tw = new Tweet("sam");
        lyricChooser lc = new lyricChooser(tw);
        lc.chooseLyrics();
        for (RhymeLine line : lc.rhymeLines)
            System.out.println(line.toString());
    }

}
