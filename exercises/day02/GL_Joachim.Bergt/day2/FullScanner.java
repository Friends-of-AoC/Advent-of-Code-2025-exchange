package day2;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FullScanner implements IdChecker {

    public static final Logger LOGGER = Logger.getLogger(FullScanner.class.getName());

    @Override
    public boolean idIsInvalid(long id) {
        var arr = Long.toString(id).toCharArray();
        var length = arr.length;
        if (length < 2) return false;
        for (int checkDigits = length >> 1; checkDigits >=1 ; --checkDigits) {
            // Check from half-length down to single digit;
            if (length % checkDigits == 0) {
                // range is divisible;
                int numberOfChecks = Math.floorDiv(length, checkDigits);
                boolean intermediate = true;
                for (int i = 1; intermediate && i<numberOfChecks; ++i) {
                    int start = i*checkDigits;
                    int end = start + checkDigits;
                    intermediate &= (Arrays.equals(arr, 0, checkDigits, arr, start, end));
                }
                if (intermediate) {
                    LOGGER.log(Level.WARNING, "IsInvalid : {0}", id);
                    return true;
                }
            }
        }
        return false;
    }
}
