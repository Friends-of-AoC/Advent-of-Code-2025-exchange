package aoc;

public class Test {
    public static void main(String[] args) {
        var in = Test.class.getResourceAsStream("./test.txt");
        var result = Impl.list(in);
        System.out.println(result);
        assert result == 357;
    }
}