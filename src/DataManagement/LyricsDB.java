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

    public static void main(String[] args) {

    }
}
