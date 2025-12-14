package aoc;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        var in = Test.class.getResourceAsStream("./test.txt");
        var result = Impl.parser(in);
        System.out.println(result);
        assert result == 13;
    }
}