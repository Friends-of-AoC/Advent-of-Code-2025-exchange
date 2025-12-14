package aoc;

import java.io.*;

public class Test {
    public static void main(String[] args) throws Exception {
        Dial d = new Dial(100, 50);
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Test.class.getResourceAsStream("test.txt")))) {
            while ( ( line = reader.readLine() ) != null ) {
                d.performMove(line);
            }
        }
        System.out.println(d.getCount());
    }
}
