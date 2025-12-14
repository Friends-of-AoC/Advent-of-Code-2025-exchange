package day2;

public class Test {
    public static void main(String[] args) throws Exception {
        String result = IdRangeScanner.scan(Test.class.getResourceAsStream("./test.txt"), new DuplicateIdChecker());
        System.out.println("Result : " + result);
        assert result.equals("1227775554");
    }
}
