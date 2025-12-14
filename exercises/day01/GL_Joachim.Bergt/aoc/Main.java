package aoc;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Dial d = new Dial(100, 50);
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while ( ( line = reader.readLine() ) != null ) {
                d.performMove(line);
            }
        }
        System.out.println(d.getCount());

    }

}
