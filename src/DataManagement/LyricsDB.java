package DataManagement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

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
            String artistName = artist.text();
            System.out.println(artistName);
            Elements songs = getHtmlElement(artist.attr("abs:href"), "table table td a[href]");
            for (Element song : songs) {
                String songURL = song.attr("abs:href");
                String songName = song.text();
                System.out.println("name " + songName + " artist = " + artistName + " URL = " + songURL);
            }
        }
    }

    private void download(String url, String fileName) {
        String text = getHtmlElement(url, "pre").text();
        try {
            FileWriter fw = new FileWriter("lyrics/" + fileName);
            fw.write(text);
            fw.close();
        } catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
    }

    public static void main(String[] args) {
        LyricsDB db = new LyricsDB(dbURL);
        //db.getSongs();
        db.download("http://ohhla.com/anonymous/treysong/ladies2/gonetill.tre.txt", "test2.txt");

    }
}
