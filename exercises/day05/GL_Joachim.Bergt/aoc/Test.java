package aoc;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        var result = Impl.parser(Test.class.getResourceAsStream("./test.txt"));
        System.out.println(result);
        assert result == 3;
    }
}
