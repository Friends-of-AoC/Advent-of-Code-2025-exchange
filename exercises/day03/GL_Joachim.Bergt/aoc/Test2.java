package aoc;

public class Test2 {
    public static void main(String[] args) {
        var in = Test2.class.getResourceAsStream("./test.txt");
        var result = Impl2.list(in);
        System.out.println(result);
        assert result == 3121910778619L;
    }
}