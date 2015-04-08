package AI;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by z00z on 06/04/15.
 */

public class RhymeGenerator {

    protected HashMap<String, String[]> getRhymes(String word) {
        Elements element = null;
        HashMap<String, String[]> rhymes = new HashMap<String, String[]>();

        try {
            element = Jsoup.connect("http://muse.dillfrog.com/sound/search?match_type=perfect_rhyme&group_by=syllable_count&word_type=&word=" + word + "&familiar_only=Y&defined_only=Y&pb=%21").get().select("div div");
            try {
                rhymes.put("1S", element.get(1).select("a").text().split(" "));
                rhymes.put("2S", element.get(3).select("a").text().split(" "));
                rhymes.put("3S", element.get(5).select("a").text().split(" "));
            } catch (IndexOutOfBoundsException e) {}
        }
        catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
        return rhymes;
    }

    public static void main(String[] args) {
        HashMap<String, String[]> rhymes = new RhymeGenerator().getRhymes("zaid");
        for (String key : rhymes.keySet()) {
            System.out.println("\n" + key + " rhymes: ");
            for (String word : rhymes.get(key)) System.out.println("\t" + word);
        }
        //System.out.println(e.text());
    }
}
