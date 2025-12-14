package aoc;

import java.io.IOException;

public class Test2 {
    public static void main(String[] args) throws IOException {
        var result = Impl2.parser(Test2.class.getResourceAsStream("./test.txt"));
        System.out.println(result);
        assert result == 14;
    }
}
