package aoc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Impl {
    public static void main(String[] args) {
        System.out.println(Impl.solve(System.in));
    }

    static long solve(InputStream in) {
        record Corner(long x, long y) {}
        record Rectangle(Corner a, Corner b, long area) {
            static Rectangle buiöd(Corner a, Corner b) {
                long length = Math.abs(a.x-b.x)+1;
                long height = Math.abs(a.y-b.y)+1;
                long area = length * height;
                return new Rectangle(a, b, area);
            }
        }
        var scanner = new Scanner(in);
        var corners = new ArrayList<Corner>();
        var rectangles = new ArrayList<Rectangle>();
        scanner.useLocale(Locale.US);
        scanner.useDelimiter(Pattern.compile("[ \\t\\n\\x0B\\f\\r,]+"));
        while (scanner.hasNextLong()) {
            var corner = new Corner(scanner.nextLong(), scanner.nextLong());
            var currentRectangles = corners.parallelStream()
                    .map(other ->Rectangle.buiöd(corner, other)).collect(Collectors.toList());
            rectangles.addAll(currentRectangles);
            corners.add(corner);
        }
        rectangles.sort(Comparator.comparing(Rectangle::area).reversed());
        return rectangles.get(0).area();

    }
}