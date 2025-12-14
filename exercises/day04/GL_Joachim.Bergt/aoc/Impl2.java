package aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;

public class Impl2 {
    public static int parser(InputStream in) throws IOException {
        int read = -1;
        class Lines {
            int length = -1;
            BitSet current = new BitSet();
            ArrayList<BitSet> bitsets = new ArrayList<>();
            /**
             * swap the BitSet around, without re-allocating
             * @return
             */
            void swap() {
                bitsets.add(this.current);
                this.current = new BitSet(this.length);
            }
            int countOptions() {
                final int countLines = bitsets.size();
                int removed = 0;
                boolean somethingemoved;
                final var emptyBitset = new BitSet();
                do {
                    somethingemoved = false;

                    for (int lineIndex = 0; lineIndex<countLines;++lineIndex) {
                        final var currentCheckRow = bitsets.get(lineIndex);
                        if (currentCheckRow.isEmpty()) {
                        }
                        final var rowBefore = lineIndex > 0 ? bitsets.get(lineIndex - 1) : emptyBitset;
                        final var rowAfter = lineIndex < countLines - 1 ? bitsets.get(lineIndex + 1) : emptyBitset;
/*                        debugLine(rowBefore);
                        debugLine(currentCheckRow);
                        debugLine(rowAfter);*/
                        if ((rowBefore.cardinality() + rowAfter.cardinality()) <=2) {
//                            System.out.println("Surrounding Rows of " + (lineIndex + 1) + "are nearly empty");
                            continue;
                        }

                        for (int columnIndex = 0; columnIndex <= length; ++columnIndex) {
                            if (currentCheckRow.get(columnIndex)) {
                                int around = 0;
                                // a roll, start Counting
                                if (columnIndex > 0) {
                                    if (rowBefore.get(columnIndex - 1)) {
                                        ++around;
                                    }
                                    if (currentCheckRow.get(columnIndex - 1)) {
                                        ++around;
                                    }
                                    if (rowAfter.get(columnIndex - 1)) {
                                        ++around;
                                    }
                                }
                                if (rowBefore.get(columnIndex)) {
                                    ++around;
                                }
                                if (rowAfter.get(columnIndex)) {
                                    ++around;
                                }
                                if (columnIndex < length) {
                                    if (rowBefore.get(columnIndex + 1)) {
                                        ++around;
                                    }
                                    if (currentCheckRow.get(columnIndex + 1)) {
                                        ++around;
                                    }
                                    if (rowAfter.get(columnIndex + 1)) {
                                        ++around;
                                    }
                                }
                                if (around < 4) {
                                    ++removed;
                                    currentCheckRow.clear(columnIndex);
                                    somethingemoved = true;
                                }
                            }
                        }
                    }
                }while (somethingemoved);
/*                for (var line : bitsets) {
                    debugLine(line);
                }*/
                return removed;
           }
/*           void debugLine(BitSet line) {
               for (int idx = 0; idx <= length; ++idx) {
                   if (line.get(idx)) {
                       System.out.print("@");
                   } else {
                       System.out.print(".");
                   }
               }
               System.out.println(" : " + line.cardinality());

           }*/
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
        possibilities = lines.countOptions();
        return possibilities;
    }
}
