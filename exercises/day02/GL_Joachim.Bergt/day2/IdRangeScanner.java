package day2;

import java.io.*;
import java.util.logging.*;


class Id {
    public static final Logger LOGGER = Logger.getLogger(Id.class.getName());
    private char[] buffer = new char[8];
    int pointer = 0;
    public Id() {
    }
    void addDigit(char digit) {
        if (this.pointer == this.buffer.length) {
            char[] newBuffer = new char[this.pointer<<1];
            System.arraycopy(this.buffer, 0, newBuffer, 0, pointer);
            this.buffer = newBuffer;
        }
        this.buffer[this.pointer++] = digit;
    }
    long getId() {
        String s = new String(this.buffer, 0, this.pointer);
        LOGGER.log(Level.FINER, "ParsedId-String {0}", s);
        long id = Long.parseUnsignedLong(s);
        LOGGER.log(Level.FINER, "ParsedId-Long {0}", id);
        return id;
    }
    void clear() {
        this.pointer = 0;
    }

}
class IdRange {
    Id start = new Id();
    Id end = new Id();

    long getInvalidIds(IdChecker checker) {
        long invStart = start.getId();
        long invEnd = end.getId();
        long sum = 0L;
        for (long id = invStart; id <= invEnd; ++id) {
            if (checker.idIsInvalid(id)) {
                sum += id;
            }
        }
        return sum;
    }
}
public class IdRangeScanner {

    public static final Logger LOGGER = Logger.getLogger(IdRangeScanner.class.getName());

    public static String scan(InputStream in, IdChecker checker) throws Exception {
        long cummulatedId = 0L;
        int read = -1;
        var currentRange = new IdRange();
        var currentId = currentRange.start;
        while (true) {
            read = in.read();
            switch (read) {
                case '-': {
                    LOGGER.log(Level.INFO, "Switch to second digit");
                    currentId = currentRange.end;
                    break;
                }
                case ',': {
                    LOGGER.log(Level.INFO, "Emd Id-Pair");
                }
                case -1: {
                    cummulatedId += currentRange.getInvalidIds(checker);
                    currentRange.start.clear();
                    currentRange.end.clear();
                    currentId = currentRange.start;
                    break;
                }
                case 10 : {
                }
                case 13 : {
                    break;
                }
                default: {
                    currentId.addDigit((char) read);
                }
            }
            if (read == -1) {
                return Long.toString(cummulatedId);
            }
        }
    }
}
