package aoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Impl2 {
    static long worksheet(InputStream in)  {
        class CephalopodNumber {
            char[] buffer;
            int pointer;
            public CephalopodNumber() {
                this.buffer = new char[8];
                this.pointer = 0;
            }
            void addDigit(char digit) {
                if (this.pointer == this.buffer.length) {
                    char[] newBuffer = new char[this.pointer<<1];
                    System.arraycopy(this.buffer, 0, newBuffer, 0, pointer);
                    this.buffer = newBuffer;
                }
                this.buffer[this.pointer++] = digit;
            }
            long getValue() {
                String s = new String(this.buffer, 0, this.pointer);
                return Long.parseUnsignedLong(s);
            }
            void clear() {
                this.pointer = 0;
            }
        }

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

        List<char[]> lines = bin
                .lines()
                .filter(l -> !l.isBlank())
                .map(l -> l.toCharArray())
                .collect(Collectors.toList());
        var maxLineLength = lines.stream().mapToInt(a ->a.length).max().getAsInt();
        Add totalSum = new Add();
        char[] operators = lines.remove(lines.size()-1);
        int valuesLength = lines.size();
        Op currentOperation = null;
        CephalopodNumber currentNumber = new CephalopodNumber();
        for (int idx = 0; idx <maxLineLength; ++idx) {
            currentNumber.clear();
            switch (idx >= operators.length ? ' ': operators[idx]) {
                case '+' : {
                    if (currentOperation != null) {
                        totalSum.operate(currentOperation.result());
                    }
                    currentOperation = new Add();
                    break;
                }
                case '*' : {
                    if (currentOperation != null) {
                        totalSum.operate(currentOperation.result());
                    }
                    currentOperation = new Mul();
                    break;
                }
                default: {}
            }
            for (int valIndex = 0; valIndex<valuesLength;++valIndex) {
                var line = lines.get(valIndex);
                char digit = idx >= line.length ? ' ':line[idx];
                if (Character.isDigit(digit)) {
                    currentNumber.addDigit(digit);
                }
            }
            if (currentNumber.pointer > 0) {
                currentOperation.operate(currentNumber.getValue());
            }
        }
        totalSum.operate(currentOperation.result());
        return totalSum.result;
    }
}
