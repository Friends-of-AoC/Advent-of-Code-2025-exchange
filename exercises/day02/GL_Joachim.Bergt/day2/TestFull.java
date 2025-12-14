package day2;


import java.util.logging.Level;
import java.util.logging.Logger;

public class TestFull {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getGlobal();
        logger.setLevel(Level.FINEST);
        String result = IdRangeScanner.scan(TestFull.class.getResourceAsStream("/day2/test.txt"), new FullScanner());
        System.out.println("Result : " + result);
    }
}
