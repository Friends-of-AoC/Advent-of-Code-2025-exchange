package aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Impl2Old {
    public static void main(String[] arg) throws IOException {
        var i = new Impl2Old();
        System.out.println(i.scan(System.in));
    }
    public int scan(InputStream in) {
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII))
                .lines().parallel()
                .mapToInt(this::handleLine).sum();
    }
    static class DynamicNumber implements  Comparable<DynamicNumber> {
        enum State {
            Undefined,
            Final,
            Set,
            Calculated,
            Calculating
        };
        final int maxValue;
        final String info;
        State state = State.Undefined;
        int currentValue = 0;
        Set dynamicallyDependentOn = new HashSet<DynamicNumber>();

        public DynamicNumber(int maxValue, String info) {
            this.maxValue = maxValue;
            this.info = info;
        }
        public String toString() {
            if (state == State.Final) {
                return info + " == " + currentValue;
            } else if (state == State.Set) {
                    return info + " =? " + currentValue + " <= " + maxValue;
            } else if (state == State.Calculated) {
                return info + " =& " + currentValue + " <= " + maxValue;
            } else if (state == State. Calculating) {
                return info + " =$ " + currentValue + " <= " + maxValue;
            } else {
                return info + " <? " + maxValue;
            }
        }

        @Override
        public int hashCode() {
            return info.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof DynamicNumber other) ? this.info.equals(other.info) : false;
        }

        @Override
        public int compareTo(DynamicNumber o) {
            return this.info.compareTo(o.info);
        }
    }

    record CalculatedResult(AtomicInteger value, Set<DynamicNumber> sum) {};
    static class SwitchBoard {
        int solve(CalculatedResult[] desiredJoltages, Set<DynamicNumber> dynamicNumbers) {
            // check for static numbers;

            boolean somethingChanged;
            do {
                somethingChanged = false;
                for (var digit : desiredJoltages) {
                    for (var otherDigit : desiredJoltages) {
                        if (digit == otherDigit) continue;
                        if (otherDigit.sum().containsAll(digit.sum) && otherDigit.sum.size() == digit.sum().size() + 1) {
                            // found a static digit!
                            var set = new HashSet<DynamicNumber>();
                            set.addAll(otherDigit.sum);
                            set.removeAll(digit.sum());
                            var staticValue = set.stream().findFirst().get();
                            staticValue.currentValue = otherDigit.value.get() - digit.value.get();
                            staticValue.state = DynamicNumber.State.Final;
                            somethingChanged = true;
                        }
                    }
                }
                if (somethingChanged) {
                    for (var digit : desiredJoltages) {
                        var iter = digit.sum.iterator();
                        while (iter.hasNext()) {
                            var value = iter.next();
                            if (value.state == DynamicNumber.State.Final) {
                                digit.value.addAndGet(-value.currentValue);
                                iter.remove();
                            }
                        }
                    }
                }
            } while (somethingChanged);
            var fullyDynamicNumbers = new HashSet<DynamicNumber>();
            for (var dynamic: dynamicNumbers) {
                if (dynamic.state != DynamicNumber.State.Final) {
                    fullyDynamicNumbers.add(dynamic);
                }
            }
            if (fullyDynamicNumbers.isEmpty())  {
                return dynamicNumbers.stream().mapToInt(d -> d.currentValue).sum();
            }
            int minValue = Integer.MAX_VALUE;
            var dynamics = new ArrayList<DynamicNumber>();
            findBestDynamicNumbers(desiredJoltages, dynamics::add);
            while (true) {
//                System.out.println("dynamics = " + dynamics);
                resetDynamicCalculation(dynamicNumbers);
                var calulating = false;
                var clicks = 0;
                do {
                    calulating = false;
                    for (var joltage : desiredJoltages) {
                        var result = joltage.sum().stream().filter(d -> d.state == DynamicNumber.State.Calculating).toArray();
                        if (result.length == 1) {
                            var digit = (DynamicNumber) result[0];
                            var sum = joltage.value.get() - joltage.sum().stream().filter(d -> d.state != DynamicNumber.State.Calculating).mapToInt(d -> d.currentValue).sum();
                            if (sum >= 0 && sum <= digit.maxValue) {
                                digit.currentValue = sum;
                                digit.state = DynamicNumber.State.Calculated;
                                calulating = true;
                                if (dynamicNumbers.stream().mapToInt(d -> d.currentValue).sum() > minValue) {
//                                    System.out.println("Calculation too big, abort");
                                    calulating = false;
                                    clicks = -1;
                                    break;
                                }
//                                System.out.println(" Set Calculated Diget " + sum);
                            } else {
//                                System.out.println("Invalid sum = " + sum + " for digit " + digit);
                                calulating = false;
                                clicks = -1;
                                break;
                            }
                        }
                    }
                } while (calulating);
                if (clicks == 0) {
                    var m =check(desiredJoltages, dynamicNumbers);
                    if (m < minValue) {
                        System.out.println("Potentially lowest number = " + m);
                        minValue = m;
                    }
                }
                boolean carry = false;
                int setIndex = 0;
                do {
                    carry = false;
                    if (setIndex == dynamics.size()) {
                        return minValue;
                    }
                    var number = dynamics.get(setIndex);
                    if (number.currentValue == 0) {
                        number.currentValue = number.maxValue;
                        carry = true;
                        ++setIndex;
                    } else {
                        --number.currentValue;
                    }
                } while (carry);
            }
        }

        private int check(CalculatedResult[] desiredJoltages, Set<DynamicNumber> dynamicNumbers) {
                for (var j: desiredJoltages) {
                    if (j.value().get() != j.sum().stream().mapToInt(s-> s.currentValue).sum()) return Integer.MAX_VALUE;
                }
                var sum = 0;
                for (var s : dynamicNumbers) {
                    var v = s.currentValue;
                    if (v < 0) return Integer.MAX_VALUE;
                    sum += v;
                }
                return sum;
        }

        void resetDynamicCalculation(Set<DynamicNumber> dynamicNumbers) {
            for (var dyn : dynamicNumbers) {
                if (dyn.state == DynamicNumber.State.Calculated) {
                    dyn.state = DynamicNumber.State.Calculating;
                    dyn.currentValue = 0;
                }
            }
        }
        void findBestDynamicNumbers(CalculatedResult[] joltage, Consumer<DynamicNumber> callback) {
            int lowestCount = Integer.MAX_VALUE;
            Long lowestMultiplier = Long.MAX_VALUE;
            DynamicNumber dyn = null;
            for (var jolt : joltage) {
                var list = jolt.sum()
                        .stream()
                        .filter(d -> d.state == DynamicNumber.State.Undefined)
                        .sorted( (dyn1, dyn2) -> Integer.compare(dyn1.maxValue, dyn2.maxValue)).toList();
                if (! list.isEmpty() && list.size() <= lowestCount) {
                    var leng = 1L;
                    for (int i=Math.max(list.size()-2, 0);i>=0;--i) {
                        leng *= list.get(i).maxValue;
                    }
                    if (list.size() < lowestCount || leng < lowestMultiplier) {
                        lowestCount = list.size();
                        lowestMultiplier = leng;
                        dyn = list.get(0);
                    }
                }
            }
            if (dyn == null) {
                return;
            }
            dyn.state = DynamicNumber.State.Set;
            dyn.currentValue = dyn.maxValue;
            callback.accept(dyn);
            boolean somethingChanged;
            do {
                somethingChanged = false;
                for (var jolt : joltage) {
                    var list = jolt.sum.stream().filter(d -> d.state == DynamicNumber.State.Undefined).toList();
                    if (list.size() == 1) {
                        somethingChanged = true;
                        var digit = list.get(0);
                        digit.state = DynamicNumber.State.Calculating;
                    }
                }
            } while (somethingChanged);
            findBestDynamicNumbers(joltage, callback);
        }
    }
    public int handleLine(String line) {
        var parts = line.split(" ");
        var switchBoard = new SwitchBoard();
        List<CalculatedResult> desiredJoltages = new ArrayList<>();
        {
            var part = parts[parts.length-1];
            var idx = 1;
            do {
                var nextIdx = part.indexOf(',', idx);
                boolean end = false;
                if (nextIdx == -1) {
                    nextIdx = part.length()-1;
                    end = true;
                }
                desiredJoltages.add(new CalculatedResult(new AtomicInteger(Integer.parseInt(part.substring(idx, nextIdx))), new TreeSet<>()));
                if (end) {
                    idx = -1;
                } else {
                    idx = nextIdx+1;
                }
            } while (idx != -1);
        }
        var desired = new CalculatedResult[desiredJoltages.size()];

        for (var j=0;j<desired.length;++j) {
            desired[j] = desiredJoltages.get(j);
        };
        var dynamicNumbers = new HashSet<DynamicNumber>();
        for (int i= parts.length-2; i>0; --i) {
            var part = parts[i];
            var idx = 1;
            var minValue = Integer.MAX_VALUE;
            BitSet toggles = new BitSet();
            do {
                var nextIdx = part.indexOf(',', idx);
                boolean end = false;
                if (nextIdx == -1) {
                    nextIdx = part.length()-1;
                    end = true;
                }
                var digitNumber = Integer.parseInt(part.substring(idx, nextIdx));
                toggles.set(digitNumber);
                minValue = Math.min(minValue, desired[digitNumber].value.get());

                if (end) {
                    idx = -1;
                } else {
                    idx = nextIdx+1;
                }
            } while (idx != -1);
            var dyn = new DynamicNumber(minValue, part);
            toggles.stream().forEach( digit -> desired[digit].sum().add(dyn));
            dynamicNumbers.add(dyn);
        }
        System.out.println(" start " + line);
        var result = switchBoard.solve(desired, dynamicNumbers);
        System.out.println("result = " + result);
        return result;


    }
}
