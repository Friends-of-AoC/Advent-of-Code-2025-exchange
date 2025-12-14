package day2;

public class Main2 {
    static {
        java.util.logging.Logger.getGlobal().setLevel(java.util.logging.Level.OFF);
    }
    public static void main(String[] args) throws Exception {
        String result = IdRangeScanner.scan(Main2.class.getResourceAsStream("input.txt"), new FullScanner());
        System.out.println("Result : " + result);
    }
}
