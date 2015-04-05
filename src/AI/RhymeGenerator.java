package AI;
import org.jsoup.Jsoup;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by z00z on 06/04/15.
 */

public class RhymeGenerator {
    private RhymeGenerator() {}

    private Elements getRhymes(String word) {
        Elements element = null;
        try {
            element = Jsoup.connect("http://muse.dillfrog.com/sound/search?match_type=perfect_rhyme&group_by=syllable_count&word_type=&word=" + word + "&familiar_only=Y&defined_only=Y&pb=%21").get().select("div div");
            try {
                System.out.println(element.get(1).select("a").text());
                System.out.println(element.get(3).select("a").text());
                System.out.println(element.get(5).select("a").text());
            } catch (IndexOutOfBoundsException e) {}
        }
        catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
        return element;
    }
    
    public static void main(String[] args) {
        RhymeGenerator r = new RhymeGenerator();
        Elements e = r.getRhymes("zaid");
    }
}
