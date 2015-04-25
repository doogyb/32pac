package AI;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by samuel on 16/04/15.
 */
public class NaturalLanguage {
    static Set<String> dict = genDict();

    public static HashSet<String> genDict() {
        HashSet<String> dict = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("unix.dict"));
            String line;
            while ((line=br.readLine())!=null){
                if (line.length() > 3) dict.add(line);
            }
            br.close();
        } catch (IOException e) { e.printStackTrace(); }
        return dict;
    }

    public static String getLastTweetWord(String input) {
        String inputArray[] = input.split(" ");
        int i;

        for (i = inputArray.length-1; i >= 0; i--) {
            int end;
            try {for (end = inputArray[i].length() - 1; !Character.isLetterOrDigit(inputArray[i].charAt(end)); end--);}
            catch (StringIndexOutOfBoundsException e) {continue;}
            inputArray[i] = inputArray[i].substring(0, end+1);
            Pattern wordPattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);

            if (!wordPattern.matcher(inputArray[i]).find() && !inputArray[i].equals("http")) break;
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

    public static ArrayList<String> splitString(String input) {
        ArrayList<String> out = new ArrayList<String>();
        int pos = 0, matched = 0;
        String match = "";
        if (input.length() < 3) return null;
        if (dict.contains(input)) return null;
        while (input.length() != 0){
            for (int i = 1; i <= input.length(); i++) {
                String split = input.substring(0, i);
                if (dict.contains(split)) {
                    match = split;
                    pos = i;
                    matched = 1;
                }
            }
            if (matched == 1) {
                input = input.substring(pos);
                matched = 0;
                //System.out.println(match); //testing
                out.add(match);
            }
            else {
                input = input.substring(1);
            }
        }
        return out;
    }
    
     public static String[] splitHashtag(String input) {
        return input.split("(?=[A-Z])");
    }

}
