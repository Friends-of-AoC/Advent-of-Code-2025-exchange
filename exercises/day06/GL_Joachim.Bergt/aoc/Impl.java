package aoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Impl {
    static long worksheet(InputStream in)  {
        interface Op {
            void operate(long v);
            long result();
        }
        class Add implements Op {
            long result = 0L;
            @Override
            public void operate(long v) {
                result += v;
            }
            @Override
            public long result() {
                return this.result;
            }
        }
        class Mul implements Op {
            long result = 1L;
            @Override
            public void operate(long v) {
                result *= v;
            }
            @Override
            public long result() {
                return this.result;
            }
        }
        var bin = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
        List<String[]> lines = bin
                .lines()
                .filter(l -> !l.isBlank())
                .map(l -> l.trim().split("\\s+"))
                .collect(Collectors.toList());
        Add totalSum = new Add();
        String[] operators = lines.remove(lines.size()-1);
        int valuesLength = lines.size();
        for (int idx = operators.length -1; idx >=0; --idx) {
            Op current = operators[idx].equals("+")?new Add():new Mul();
            for (int valIndex = 0; valIndex<valuesLength;++valIndex) {
                current.operate(Long.parseLong(lines.get(valIndex)[idx]));
            }
            totalSum.operate(current.result());
        }
        return totalSum.result;
    }
}
