package aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Impl {
    public static void main(String[] arg) throws IOException {
        var i = new Impl();
        System.out.println(i.scan(System.in));
    }
    public int scan(InputStream in) {
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII))
                .lines()
                .mapToInt(this::handleLine).sum();
    }
    static class SwitchBoard {
        record SolveAttempt(BitSet startPosition, List<Long> performedSwitches, Set<Long> stateBuffer) {
            Collection<SolveAttempt> next(SwitchBoard sb) {
                var array = startPosition.toLongArray();
                var currentState = array.length == 0 ? 0L : array[0];
                if (stateBuffer.contains(currentState)) {
//                    System.out.println("State " + stateBuffer + " already known, skip");
                    return Collections.EMPTY_LIST;
                }
                var newStateBuffer = new TreeSet<Long>();
                newStateBuffer.addAll(stateBuffer);
                newStateBuffer.add(currentState);

                BitSet clone = BitSet.valueOf(array);
                clone.xor(sb.desiredState);
                return clone.stream()
                        .mapToObj(i -> {
                            var result = sb.groupedToogles.get(i);
//                            System.out.println("Bit " + i + " togles " + result);
                            return result;
                        })
                        .flatMap(l -> l == null ? null : l.stream())
                        .map(toogles -> {
//                            System.out.println(" try  " + stateBuffer + "^" + toogles + " " + performedSwitches);
                            return toogles.toLongArray()[0];
                        })
                        .distinct()
                        .map(longToggle -> {
                            var start = BitSet.valueOf(array);
                            start.xor(BitSet.valueOf(new long[]{longToggle}));
                            var performed = new ArrayList<Long>();
                            performed.addAll(performedSwitches);
                            performed.add(longToggle);
                            return new SolveAttempt(start, performed, newStateBuffer);
                        }).collect(Collectors.toList());
            }

        }
        BitSet desiredState = new BitSet();
        Map<Integer, java.util.List<BitSet>> groupedToogles = new TreeMap<>();

        void addToogleGroup(BitSet switches) {
            System.out.println("store toggles = " + switches);
            switches.stream().forEach(idx -> groupedToogles.compute(idx, (i, list) -> {
                if (list == null) {
                    list = new ArrayList<>();
                }
                System.out.println("i = " + i);
                list.add(switches);
                return  list;
            }));
        }
        int solve() {
            List<SolveAttempt> solveAttempts = new ArrayList<SolveAttempt>();
            var solutions = new ArrayList<SolveAttempt>();
            var init = new SolveAttempt(new BitSet(), Collections.EMPTY_LIST, Collections.EMPTY_SET);
            solveAttempts.add(init);
//            SolveAttempt bestSolve = null;
            AtomicInteger ai = new AtomicInteger(Integer.MAX_VALUE);
/*            while ((! solveAttempts.isEmpty()) &&  solveAttempts.size() > 5) {
                var solveAttempt = solveAttempts.remove(0);
                if (solveAttempt.startPosition.equals(this.desiredState)) {
                    solutions.add(solveAttempt);
                    if (bestSolve == null) {
                        bestSolve = solveAttempt;
                        ai.set(bestSolve.performedSwitches.size());
                    } else if (bestSolve.performedSwitches().size() > solveAttempt.performedSwitches.size()) {
                        bestSolve = solveAttempt;
                        ai.set(bestSolve.performedSwitches.size());
                    }
                } else if (bestSolve != null && solveAttempt.performedSwitches.size() >= bestSolve.performedSwitches.size()) {
                } else {
                    solveAttempts.addAll(solveAttempt.next(this));
                }
            }*/
            while (!solveAttempts.isEmpty()) {
                solveAttempts = solveAttempts
                        .parallelStream()
                        .filter( attempt -> {
                            final var len = attempt.performedSwitches.size();
                            if (attempt.startPosition.equals(this.desiredState)) {
                                System.out.println("found  solution " + attempt + " @ "  + Thread.currentThread().getName());

                                ai.getAndUpdate(old -> {
                                    if (len < old) {
                                        System.out.println("found best solution " + attempt + " @ "  + Thread.currentThread().getName());
                                        return len;
                                    }
                                    return old;
                                });
                                return false;
                            }
                            if (len < ai.get()) {
                                return true;
                            }
//                            System.out.println("Skip " + attempt +" because > " + ai.get() + " @ " + Thread.currentThread().getName());
                            return false;
                        })
                        .flatMap( solveAttempt -> solveAttempt.next(this).stream())
                        .collect(Collectors.toList());
            }
            return ai.get();
        }
    }
    int handleLine(String line) {
        var parts = line.split(" ");
        var switchBoard = new SwitchBoard();
        {
            var desiredState = parts[0];
            var idx = 0;
            while ( ( idx = desiredState.indexOf("#", idx+1)) != -1) {
                switchBoard.desiredState.set(idx-1);
            }
        }
        for (int i= parts.length-2; i>0; --i) {
            var part = parts[i];
            var idx = 1;
            BitSet toggles = new BitSet();
            do {
                var nextIdx = part.indexOf(',', idx);
                boolean end = false;
                if (nextIdx == -1) {
                    nextIdx = part.length()-1;
                    end = true;
                }
                toggles.set(Integer.parseInt(part.substring(idx, nextIdx)));
                if (end) {
                    idx = -1;
                } else {
                    idx = nextIdx+1;
                }
            } while (idx != -1);
            switchBoard.addToogleGroup(toggles);

        }
        System.out.println(" start " + line);
        var result = switchBoard.solve();
        System.out.println("result = " + result);
        return result;


    }
}
