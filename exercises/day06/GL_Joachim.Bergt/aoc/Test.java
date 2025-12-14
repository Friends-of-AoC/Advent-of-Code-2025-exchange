package aoc;

public class Test {
    public static void main(String[] args)  {
        var result = Impl.worksheet(Test.class.getResourceAsStream("./test.txt"));
        System.out.println(result);
        assert result == 4277556;
    }
}
