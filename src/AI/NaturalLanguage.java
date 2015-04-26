package AI;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by samuel on 16/04/15.
 */
public class NaturalLanguage {
    public static String getLastTweetWord(String input) {
        input = input.substring(0, input.indexOf("#"));     //so we take the last word after #
        String inputArray[] = input.split(" ");
        int i;

        for (i = inputArray.length-1; i >= 0; i--) {
            int end;
            try {for (end = inputArray[i].length() - 1; !Character.isLetterOrDigit(inputArray[i].charAt(end)); end--);}
            catch (StringIndexOutOfBoundsException e) {continue;}
            inputArray[i] = inputArray[i].substring(0, end+1);
            Pattern wordPattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);

            if (inputArray[i].equals("http")) continue;
            if (!wordPattern.matcher(inputArray[i]).find()) break;
        } return inputArray[i];
    }

    public static int numberOfSyllables(String input) {
        int syllablesCount = 0;
        char[] word = input.toCharArray();

        for (int i = 0; i < word.length-1; i++) {
            if (isVowel(word[i])) {
                syllablesCount++;
                while (isVowel(word[i]) && i < word.length-1) i++;
            }
        } return syllablesCount;
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

    public static String filter(String rhyme){
        byte[][] words = {new byte[]{78, 105, 103, 103, 101, 114}, new byte[]{110, 105, 103, 103, 101, 114},
                new byte[]{78, 105, 103, 103, 97}, new byte[]{110, 105, 103, 103, 97},
                new byte[]{80, 117, 115, 115, 121}, new byte[]{112, 117, 115, 115, 121},
                new byte[]{70, 117, 99, 107}, new byte[]{102, 117, 99, 107},
                new byte[]{83, 104, 105, 116}, new byte[]{115, 104, 105, 116},
                new byte[]{68, 105, 99, 107}, new byte[]{100, 105, 99, 107}
        };

        byte[][] filter = {new byte[]{78, 105, 42, 42, 101, 114}, new byte[]{110, 105, 42, 42, 101, 114},
                new byte[]{78, 105, 42, 42, 97}, new byte[]{110, 105, 42, 42, 97},
                new byte[]{80, 117, 42, 42, 121}, new byte[]{112, 117, 42, 42, 121},
                new byte[]{70, 42, 42, 107}, new byte[]{102, 42, 42, 107},
                new byte[]{83, 42, 42, 116}, new byte[]{115, 42, 42, 116},
                new byte[]{68, 42, 99, 107}, new byte[]{100, 42, 99, 107}
        };
        for (int i = 0; i < 12; i++){
            String x = new String(words[i]);
            if (rhyme.contains(x)){
                rhyme = rhyme.replace(x, new String(filter[i]));
            }
        }
        return rhyme;
    }

    public static List splitHashtag(String input) {
        List<String> tags = new ArrayList<String> (Arrays.asList(input.split("(?=[A-Z])")));
        for (int i = 0; i < tags.size(); i++) if (tags.get(i).length() <= 2) tags.remove(i--);
        return  tags;
    }
}
