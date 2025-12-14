package aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main_0x434C49434B {
    public static void main(String[] args) throws IOException {
        var d = new Dial_0x434C49434B(100, 50);
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while ( ( line = reader.readLine() ) != null ) {
                d.performMove(line);
            }
        }
        System.out.println(d.getCount());

    }

}
