package day2;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        Logger.getGlobal().setLevel(Level.OFF);
        String result = IdRangeScanner.scan(Main.class.getResourceAsStream("/day2/input.txt"), new DuplicateIdChecker());
        System.out.println("Result : " + result);
    }
}
