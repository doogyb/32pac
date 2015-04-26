import AI.NaturalLanguage;
import DataManagement.LyricsDB;
import TwitterInteraction.TwitterActions;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by doogy on 17/04/15.
 */

public class MainPac {
    private static final String dbURL = "http://ohhla.com/favorite.html";

    private static boolean updateLyrics() {
        System.out.println("Do you want to update your lyrics database ? (y/n) ");
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String input = br.readLine();
                if (input.equals("y") || input.equals("yes")) return true;
                else if (input.equals("n" )|| input.equals("no")) return false;
                else System.out.println("[-] Incorrect input, please enter y/n");
            } catch (IOException e ) {System.out.println("[-] Incorrect input.");return false;}
        }
    }

    public static void main (String[] args) throws IOException {
        if (!LyricsDB.haveLyrics() || updateLyrics()) {
            System.out.println("[+] Looking for lyrics files ... ");
            LyricsDB db = new LyricsDB(dbURL);
            db.downloadSongs();
        }
        TwitterActions TA = new TwitterActions();
        TA.authorization();
        TA.listener();
        TA.trendTweetListener();

    }
}
