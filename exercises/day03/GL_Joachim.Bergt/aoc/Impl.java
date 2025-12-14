package aoc;

import javax.swing.text.Position;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Impl {
    public static int list(InputStream in)  {
        return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.summingInt(Impl::parsePack));
    }
    static int parsePack(String line) {
        var bytes = line.getBytes();
        var length = bytes.length;
        var first = bytes[0];
        var second = bytes[1];
        for (var idx = 2; idx < length; ++idx) {
            var jolt = bytes[idx];
            if (second > first) {
                first = second;
                second = jolt;
            } else if (jolt >= second) {
                if (second >= first) {
                    first = second;
                }
                second  = jolt;
            }
        }
        int result =  ((int)(first-'0'))*10 + (second - '0');
        return result;
    }
}
