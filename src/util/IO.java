package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IO {

    public static boolean YesNo(String prompt) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        boolean return_value;
        System.out.println(prompt);
        while (!isStringYesNo(input)) {
            input = br.readLine();
        }
        br.close();
        return input.toLowerCase().contains("y");
    }

    public static boolean isStringYesNo(String input) {
        input = input.toLowerCase();
        return (input.equals("y") || input.equals("yes") || input.equals("n") || input.equals("no"));
    }
}
