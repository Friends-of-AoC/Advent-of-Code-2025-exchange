package aoc;

import java.io.IOException;

public class Test2 {
    public static void main(String[] args) throws IOException {
        var in = Test2.class.getResourceAsStream("./test.txt");
        var result = Impl2.parser(in);
        System.out.println(result);
        assert result == 43;
    }
}