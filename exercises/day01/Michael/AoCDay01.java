import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AoCDay01 {

    static Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) {

        List<String> stringList = readTextFile(args[0]);

        // Part 1:
        System.out.println("Part 1");
        long start = System.currentTimeMillis();

        int sum = 0;
        for (String line : stringList) {
            line = line.replaceAll("\\D+", "");
            sum += Integer.parseInt(""+line.charAt(0))*10 + Integer.parseInt(""+line.charAt(line.length()-1));
        }
        System.out.println(sum);

        long finish = System.currentTimeMillis();
        System.out.println("runtime: "+(finish-start));
        System.out.println();


        // Part 2:
        System.out.println("Part 2");
        start = System.currentTimeMillis();

        sum = 0;
        MinMax positions;
        for (String line : stringList) {
            positions = new MinMax();
            getMinMax(line, positions, "1", 1);
            getMinMax(line, positions, "2", 2);
            getMinMax(line, positions, "3", 3);
            getMinMax(line, positions, "4", 4);
            getMinMax(line, positions, "5", 5);
            getMinMax(line, positions, "6", 6);
            getMinMax(line, positions, "7", 7);
            getMinMax(line, positions, "8", 8);
            getMinMax(line, positions, "9", 9);
            getMinMax(line, positions, "one", 1);
            getMinMax(line, positions, "two", 2);
            getMinMax(line, positions, "three", 3);
            getMinMax(line, positions, "four", 4);
            getMinMax(line, positions, "five", 5);
            getMinMax(line, positions, "six", 6);
            getMinMax(line, positions, "seven", 7);
            getMinMax(line, positions, "eight", 8);
            getMinMax(line, positions, "nine", 9);

            sum += positions.getMinVal()*10 + positions.getMaxVal();

            System.out.println(line);
            System.out.println(positions.getMinVal());
            System.out.println(positions.getMaxVal());
        }
        System.out.println(sum);

        finish = System.currentTimeMillis();
        System.out.println("runtime: "+(finish-start));
    }

    private static void getMinMax(String line, MinMax positions, String number, int val) {
        int pos;
        pos = line.indexOf(number);
        if (pos >= 0) {
            if (pos < positions.getMin()) {
                positions.setMin(pos);
                positions.setMinVal(val);
            }
        }
        pos = line.lastIndexOf(number);
        if (pos >= 0) {
            if (pos > positions.getMax()) {
                positions.setMax(pos);
                positions.setMaxVal(val);
            }
        }
    }

    private static List<String> readTextFile(String sourceFile) {
        String line;
        List<String> resultList = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(sourceFile));
            while ((line = in.readLine()) != null)
                resultList.add(line);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return resultList;
    }

    private static class MinMax {
        int min, max;
        int minVal, maxVal;

        public MinMax() {
            this.min = Integer.MAX_VALUE;
            this.max = Integer.MIN_VALUE;
            minVal = -1;
            maxVal = -1;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMinVal() {
            return minVal;
        }

        public void setMinVal(int minVal) {
            this.minVal = minVal;
        }

        public int getMaxVal() {
            return maxVal;
        }

        public void setMaxVal(int maxVal) {
            this.maxVal = maxVal;
        }
    }
}
