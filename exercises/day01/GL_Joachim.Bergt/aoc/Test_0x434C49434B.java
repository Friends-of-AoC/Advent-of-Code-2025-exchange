package aoc;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test_0x434C49434B {
    public static void main(String[] args) throws Exception {
        var d = new Dial_0x434C49434B(100, 50);
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Test_0x434C49434B.class.getResourceAsStream("./test.txt")))) {
            while ( ( line = reader.readLine() ) != null ) {
                d.performMove(line);
            }
        }
        System.out.println(d.getCount());
    }
}
