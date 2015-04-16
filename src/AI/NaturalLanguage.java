package AI;

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
}
