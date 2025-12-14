package day2;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DuplicateIdChecker implements IdChecker {

    public static final Logger LOGGER = Logger.getLogger(DuplicateIdChecker.class.getName());

    @Override
    public boolean idIsInvalid(long id) {
        var arr = Long.toString(id).toCharArray();
        var length = arr.length;
        if (arr.length % 2 == 1) {
            return false;
        }
        if (Arrays.equals(arr, 0, length >>1, arr, length >> 1, length)) {
            long invalidHalf = Long.parseLong(new String(arr, 0, length>>1));
            LOGGER.log(Level.WARNING, "found invalid half {0} from {1}" , new Object[]{invalidHalf, id});
            return true;
        }
        return false;
    }
}
