package aoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Impl2 {
    public static long list(InputStream in)  {
        return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.summingLong(Impl2::parsePack));
    }
    static long parsePack(String line) {
        var bytes = line.getBytes();
        var pack = new byte[12];
        System.arraycopy(bytes, 0, pack, 0,pack.length);
        var length = bytes.length;
        for (var idx = pack.length; idx < length; ++idx) {
            var jolt = bytes[idx];
            // bubble
            boolean swapped = false;
            for (int i=1; (!swapped) && i<pack.length;++i) {
                if (pack[i] > pack[i-1]) {
                    swapped = true;
                    System.arraycopy(pack, i, pack, i-1, pack.length-i);

                }
            }
            if ((swapped) || pack[pack.length-1] < jolt) {
                pack[pack.length-1] = jolt;
            }
        }
        long result = Long.parseLong(new String(pack));
        return result;
    }
}
