package aoc;

public class Test2 {
    public static void main(String[] args)  {
        var result = Impl2.worksheet(Test2.class.getResourceAsStream("./test.txt"));
        System.out.println(result);
        assert result == 3263827;
    }
}
