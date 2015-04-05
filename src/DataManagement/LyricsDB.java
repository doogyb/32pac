package DataManagement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by z00z on 04/04/15.
 */
 
public class LyricsDB {
    private static final String dbURL = "http://ohhla.com/favorite.html";
    private String url;

    private LyricsDB(String inputURL) {
        url = inputURL;
    }

    private Elements getHtmlElement(String url ,String element_name) {
        Elements element = null;
        try {element = Jsoup.connect(url).get().select(element_name);}
        catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
        return element;
    }

    private void getSongs() {
        Elements top50Artists = getHtmlElement(url, "table td a[href]");
        for (Element artist : top50Artists) {
            Elements songs = getHtmlElement(artist.attr("abs:href"), "table table td a[href]");
            for (Element song : songs) {
                System.out.println(song.attr("abs:href"));
            }
        }
    }

    public static void main(String[] args) {
        LyricsDB db = new LyricsDB(dbURL);
        db.getSongs();

    }
}
