package DataManagement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private void downloadSongs() {
        if (!(new File("lyrics").exists())) new File("lyrics").mkdir();
        Elements top50Artists = getHtmlElement(url, "table td a[href]");
        for (Element artist : top50Artists) {
            String artistName = artist.text();
            System.out.println("\n[+] Downloading songs by " + artistName);
            Elements songs = getHtmlElement(artist.attr("abs:href"), "table table td a[href]");
            for (Element song : songs) {
                String songURL = song.attr("abs:href");
                String songFileName = "lyrics/" + song.text() + ".txt";
                if (!(new File(songFileName).exists())) {
                    System.out.println("\t[+] Downloading + " + songURL);
                    download(songURL, songFileName);
                    try {Thread.sleep(500);}
                    catch (InterruptedException e) {System.err.println("Caught IOException: " + e.getMessage());}
                }
            }
        }
    }

    private boolean download(String url, String fileName) {
        String text = getHtmlElement(url, "pre").text();
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(text);
            fw.close();
            return true;
        } catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
        return false;
    }

    private boolean haveLyrics() {
        return ((new File("lyrics/").list().length) > 0);
    }

    public static void main(String[] args) {
        LyricsDB db = new LyricsDB(dbURL);
        //db.downloadSongs();
        //db.download("http://ohhla.com/anonymous/treysong/ladies2/gonetill.tre.txt", "test2.txt");
        System.out.println(db.haveLyrics());

    }
}
