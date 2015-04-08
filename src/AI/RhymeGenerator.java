package AI;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Created by z00z on 06/04/15.
 */

public class RhymeGenerator {

    protected ArrayList<String> getRhymes(String word) {
        Elements element = null;
        ArrayList<String> rhymes = new ArrayList<String>();

        try {
            element = Jsoup.connect("http://muse.dillfrog.com/sound/search?match_type=perfect_rhyme&group_by=syllable_count&word_type=&word=" + word + "&familiar_only=Y&defined_only=Y&pb=%21").get().select("div div");
            try {
                rhymes.addAll(Arrays.asList(element.get(1).select("a").text().split(" ")));
                rhymes.addAll(Arrays.asList(element.get(3).select("a").text().split(" ")));
                rhymes.addAll(Arrays.asList(element.get(5).select("a").text().split(" ")));
            } catch (IndexOutOfBoundsException e) {}
        }
        catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
        return rhymes;
    }

    public static void main(String[] args) {
        ArrayList<String> rhymes = new RhymeGenerator().getRhymes("zaid");
        for (String rhyme : rhymes) {
            System.out.println(rhymes);
        }
        //System.out.println(e.text());
    }
}
