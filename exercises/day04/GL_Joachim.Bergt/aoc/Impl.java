package aoc;

import java.io.*;
import java.util.BitSet;

public class Impl {
    public static int parser(InputStream in) throws IOException {
        int read = -1;
        class Lines {
            int length = -1;
            BitSet before2 = new BitSet();
            BitSet before = new BitSet();
            BitSet current = new BitSet();

            /**
             * swap the BitSet around, without re-allocating
             * @return
             */
            void swap() {
                var temp = this.before2;
                this.before2 = this.before;
                this.before = this.current;
                this.current = temp;
            }
            int countOptions() {
                int possibilities = 0;
                for (int idx = 0; idx <=length;++idx) {
                    if (this.before.get(idx)) {
                        // a roll, start Counting
                        int around = 0;
                        if (idx > 0) {
                            if (this.before2.get(idx-1)) {
                                ++around;
                            }
                            if (this.before.get(idx-1)) {
                                ++around;
                            }
                            if (this.current.get(idx-1)) {
                                ++around;
                            }
                        }
                        if (this.before2.get(idx)) {
                            ++around;
                        }
                        if (this.current.get(idx)) {
                            ++around;
                        }
                        if (idx < length) {
                            if (this.before2.get(idx+1)) {
                                ++around;
                            }
                            if (this.before.get(idx+1)) {
                                ++around;
                            }
                            if (this.current.get(idx+1)) {
                                ++around;
                            }
                        }
                        if (around < 4) {
                            ++possibilities;
                        }
                    }
                }
                return possibilities;
           }
        }
        var lines = new Lines();
        int idx = -1;
        boolean firstLine = true;
        int possibilities = 0;
        while ( (read = in.read()) != -1) {
            switch (read)  {
                case '.': { // Lift
                    ++idx;
                    lines.current.clear(idx);
                    break;
                }
                case '@': { // Paper
                    ++idx;
                    lines.current.set(idx);
                    break;
                }
                default : {
//                    System.out.println("read = " + read);
                    if (read != 10 && read != 13 && read != -1) {
                        System.out.println("Invalid." + read);
                    }
                    if (idx != -1) {
                        lines.length = idx;
                        idx = -1;
                        if (firstLine) {
                            firstLine = false;
                            lines.swap();
                        } else {
                            possibilities += lines.countOptions();
                            lines.swap();
                        }
                        lines.current.clear();
                    }
                }
            }
        }
        if (idx != -1) {
            lines.swap();
        }
        lines.current.clear();
        possibilities += lines.countOptions();
        return possibilities;
    }
}
