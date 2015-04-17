package AI;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by samuel on 16/04/15.
 */
public class NaturalLanguage {

    public static int numberOfSyllables(String input) {
        int syllablesCount = 0;
        char[] word = input.toCharArray();

        for (int i = 0; i < word.length-1; i++) {
            if (isVowel(word[i])) {
                syllablesCount++;
                while (isVowel(word[i])) i++;
            }
        }

        return syllablesCount;
    }

    public static boolean isVowel(char c) {
        return (c == 'e' || c == 'a' || c == 'i' || c == 'o' || c == 'u' || c == 'y');
    }

    public static String getLastWord(String input) {
        String[] out = input.split(" ");
        if (out.length<1) return null;
        return out[out.length-1];
    }

    public static HashMap<Integer, ArrayList<String>> getRhymes(String word) {
        Elements element = null;
        HashMap<Integer, ArrayList<String>> rhymes = new HashMap<Integer, ArrayList<String>>();
        rhymes.put(1,new ArrayList<String>());
        rhymes.put(2,new ArrayList<String>());
        rhymes.put(3,new ArrayList<String>());

        try {
            element = Jsoup.connect("http://muse.dillfrog.com/sound/search?match_type=perfect_rhyme&group_by=syllable_count&word_type=&word=" + word + "&familiar_only=Y&defined_only=Y&pb=%21").get().select("div div");
            try {
                rhymes.get(1).addAll(Arrays.asList(element.get(1).select("a").text().split(" ")));
                rhymes.get(2).addAll(Arrays.asList(element.get(3).select("a").text().split(" ")));
                rhymes.get(3).addAll(Arrays.asList(element.get(5).select("a").text().split(" ")));
            } catch (IndexOutOfBoundsException e) {}
        }
        catch (IOException e) {System.err.println("Caught IOException: " + e.getMessage());}
        return rhymes;
    }
}
