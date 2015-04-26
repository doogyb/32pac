package DataManagement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

/**
 * Created by z00z on 04/04/15.
 */

public class LyricsDB {
    private String url;
    private static final int MAX_ATTEMPTS = 25;

    public LyricsDB(String inputURL) {
        url = inputURL;
    }

    private Elements getHtmlElement(String url ,String element_name) {
        Elements element = null;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {element = Jsoup.connect(url).get().select(element_name);
                if (i>0) System.out.println("Attempt number " +i);
            }
            catch (IOException e) {continue;}
            break;
        } return element;
    }

    public void downloadSongs() {
        if (!(new File("lyrics").exists())) new File("lyrics").mkdir();
        Elements top50Artists = getHtmlElement(url, "table td a[href]");
        for (Element artist : top50Artists) {
            String artistName = artist.text();
            System.out.println("\n[+] Downloading songs by " + artistName);
            Elements songs;

            if ((songs = getHtmlElement(artist.attr("abs:href"), "table table td a[href]"))==null) {
                System.out.println("[-] Skipping songs by " + artist);
                continue;
            }

            for (Element song : songs) {
                String songURL = song.attr("abs:href");
                String songFileName = "lyrics/" + song.text().replace("/","") + ".txt";
                if (!(new File(songFileName).exists())) {
                    System.out.println("\t[+] Downloading " + songURL);
                    download(songURL, songFileName);
                    //try {Thread.sleep(500);}
                    //catch (InterruptedException e) {System.err.println("Caught IOException: " + e.getMessage());}
                }
            }
        }
    }

    private boolean download(String url, String fileName) {
        Elements element = getHtmlElement(url, "pre");

        if (element == null) System.out.println("[-] Download failed (Incorrect URL ?)");
        else {
            String text = getHtmlElement(url, "pre").text();
            try {
                FileWriter fw = new FileWriter(fileName);
                fw.write(text);
                fw.close();
                return true;
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        } return false;
    }

    public static boolean haveLyrics() {
        File lyricsFile = new File("lyrics/");
        return ( (lyricsFile.exists()) && ((lyricsFile.list().length) > 0) );
    }
}
