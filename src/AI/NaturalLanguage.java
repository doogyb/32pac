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
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
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
			while ((line=br.readLine())!=null)
				if (line.length() > 3) dict.add(line);
		} catch (IOException e) { e.printStackTrace(); }
        return dict;
	}


    public static String getLastTweetWord(String input) {
        String inputArray[] = input.split(" ");
        int i;

        for (i = inputArray.length-1; i >= 0; i--) {
            String currentWord = inputArray[i].substring(0, inputArray[i].length()-1);
            Pattern wordPattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
            Matcher m = wordPattern.matcher(currentWord);

            if (!m.find()) break;
        }
        return inputArray[i];
    }

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


}
