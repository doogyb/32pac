import DataManagement.LyricsDB;
import TwitterInteraction.TwitterActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import util.IO;

/**
 * Created by doogy on 17/04/15.
 */

public class MainPac {
    private static final String dbURL = "http://ohhla.com/favorite.html";

    //Download new lyrics.
    private static boolean updateLyrics() throws IOException {
        return IO.YesNo("Do you wish to update the lyric database?");
    }

    public static void main(String[] args) throws IOException {
        if (!LyricsDB.haveLyrics() || updateLyrics()) {
            System.out.println("[+] Looking for lyrics files ... ");
            LyricsDB db = new LyricsDB(dbURL);
            db.downloadSongs();
        }
        TwitterActions TA = new TwitterActions();
    }
}
