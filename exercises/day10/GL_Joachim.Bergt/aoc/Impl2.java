package aoc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Impl2 {
    public static void main(String[] arg) throws IOException {
        var i = new Impl2();
        System.out.println(i.scan(System.in));
    }
    static final AtomicInteger runningTotal = new AtomicInteger(0);
    public int scan(InputStream in) {
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII))
                .lines()
                .mapToInt(this::handleLine).sum();
    }

    int handleLine(String line) {
        var parts = line.split(" ");
        var switchBoard = new SwitchBoard();
        List<Joltage> desiredJoltages = new ArrayList<>();
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
                desiredJoltages.add(new Joltage("J" + desiredJoltages.size(), Integer.parseInt(part.substring(idx, nextIdx))));
                if (end) {
                    idx = -1;
                } else {
                    idx = nextIdx+1;
                }
            } while (idx != -1);
        }
        var desired = new Joltage[desiredJoltages.size()];

        for (var j=0;j<desired.length;++j) {
            desired[j] = desiredJoltages.get(j);
        };
        var dynamicNumbers = new ArrayList<SwitchGroup>();
        for (int i= parts.length-2; i>0; --i) {
            var part = parts[i];
            var idx = 1;
            var minValue = Integer.MAX_VALUE;
            var dyn = new SwitchGroup(part);
            dynamicNumbers.add(0, dyn);
            do {
                var nextIdx = part.indexOf(',', idx);
                boolean end = false;
                if (nextIdx == -1) {
                    nextIdx = part.length()-1;
                    end = true;
                }
                var digitNumber = Integer.parseInt(part.substring(idx, nextIdx));
                dyn.dependentVariables.add(desired[digitNumber]);
                desired[digitNumber].sum.addPositiveTerm(dyn);


                if (end) {
                    idx = -1;
                } else {
                    idx = nextIdx+1;
                }
            } while (idx != -1);
        }
        System.out.println(" start " + line);
        int result = 0;
        try {
            result = switchBoard.solve(desired, dynamicNumbers);
        } catch (Throwable t) {
            System.out.println("Failed.. fallback to old impl");
            result = new Impl2Old().handleLine(line);
        }
        System.out.println("result = " + result + "; total = " + runningTotal.addAndGet(result));
        return result;


    }

    static interface NumericValue {
        int getValue();
    }

    static final class ComputedSum implements NumericValue {
        final List<NumericValue> positive = new ArrayList<>();
        final List<NumericValue> negative = new ArrayList<>();

        record PosNeg(String id, List<NumericValue> provider, Consumer<NumericValue> posConsumer, Consumer<NumericValue> negConsumer) {}
        final PosNeg[] actions = new PosNeg[]{
                new ComputedSum.PosNeg("+",this.positive, this::addPositiveTerm, this::addNegativeTerm),
                new ComputedSum.PosNeg("-",this.negative, this::addNegativeTerm, this::addPositiveTerm)
        };

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Sum{");
            if (!positive.isEmpty()) {
                sb.append("+").append(positive);
            }
            if (!negative.isEmpty()) {
                sb.append("-").append(negative);
            }
            sb.append("}");
            return sb.toString();
       }

        @Override
        public int getValue() {
            return positive.stream().mapToInt(NumericValue::getValue).sum() - negative.stream().mapToInt(NumericValue::getValue).sum();
        }

        private void addNumericValue(final int value) {
            int result = value;
            for (var iter = positive.iterator(); iter.hasNext();) {
                var next = iter.next();
                if (next instanceof StaticValue pos) {
                    iter.remove();
                    result += pos.value;
                }
            }
            for (var iter = negative.iterator(); iter.hasNext();) {
                var next = iter.next();
                if (next instanceof StaticValue neg) {
                    iter.remove();
                    result -= neg.value;
                }
            }
            if (result == 0) return;
            if (result > 0) {
                positive.add(new StaticValue(result));
            } else {
                negative.add(new StaticValue(-result));
            }
        }
        public void addPositiveTerm(NumericValue value) {
            if (negative.remove(value)) {
                return;
            }
            if (value instanceof StaticValue s) {
                addNumericValue(s.value);
                return;
            }
            if (value instanceof SwitchGroup dyn && dyn.valueProvider instanceof ComputedSum inner){
                addPositiveTerm(inner);
                return;
            }
            if (value instanceof ComputedSum d) {
                d.positive.forEach(this::addPositiveTerm);
                d.negative.forEach(this::addNegativeTerm);
                return;
            }
            this.positive.add(value);
        }

        public void addNegativeTerm(NumericValue value) {

            if (positive.remove(value)) {
                return;
            }
            if (value instanceof StaticValue neg) {
                addNumericValue(- neg.value);
                return;
            }
            if (value instanceof SwitchGroup dyn && dyn.valueProvider instanceof ComputedSum inner){
                addNegativeTerm(inner);
                return;
            }
            if (value instanceof ComputedSum d) {
                d.positive.forEach(this::addNegativeTerm);
                d.negative.forEach(this::addPositiveTerm);
                return;
            }
            this.negative.add(value);
        }

        public Stream<NumericValue> stream() {
            return Stream.concat(positive.stream(), negative.stream());
        }

        public boolean isEmpty() {
            if (!positive.isEmpty()) {
                return false;
            }
            return negative.isEmpty();
        }
        public int size() {
            return positive.size() + negative.size();
        }

        public void clear() {
            this.positive.clear();
            this.negative.clear();
        }

        public boolean filterDoesNotContainDeep(NumericValue other) {
            if (positive.contains(other)) {
                return false;
            }
            if (negative.contains(other)) {
                return false;
            }
            if (other instanceof SwitchGroup dyn && dyn.valueProvider instanceof ComputedSum comp) {
                return filterDoesNotContainDeep(comp);
            }
            return true;
        }

        public NumericValue reduceWhenPurelyStatic() {
            if (this.isEmpty()) {
                return new StaticValue(0);
            }
            if (this.size() == 1) {
                if (this.negative.isEmpty()) {
                    return this.positive.get(0);
                }
                var neg = this.negative.get(0);
                if (neg instanceof StaticValue negs) {
                    return new StaticValue(- negs.value);
                }
            }
            return this;
        }

        public NumericValue reduce() {
            for (var action : this.actions) {
                for (var term : action.provider) {
                    boolean removed = false;
                    if (term instanceof SwitchGroup dyn) {
                        var valueProvider = dyn.valueProvider;
                        if (valueProvider == null) continue;
                        if (valueProvider instanceof StaticValue s) {
                            action.provider.remove(term);
                            action.posConsumer.accept(s);
                            return reduce();
                        }
                        if (valueProvider instanceof ComputedSum c) {
                            removed = action.provider.remove(term);
                            term = c;
                        }
                    }
                    if (term instanceof ComputedSum comp) {
                        if (! removed)
                            action.provider.remove(term);
                        for (var inner : comp.positive) {
                            action.posConsumer.accept(inner);
                        }
                        for (var inner : comp.negative) {
                            action.negConsumer.accept(inner);
                        }
                        return reduce();
                    }
                }
            }
            return reduceWhenPurelyStatic();
        }
    }

    static class Joltage extends Variable {
        final ComputedSum sum = new ComputedSum();
        boolean reduced = false;

        public Joltage(String id, int value) {
            super(id, new StaticValue(value));
        }

        @Override
        public String toStringExtra() {
            return " = " + sum;
        }
        public int remainingJoltage() {
            return valueProvider.getValue() - sum.positive.stream().mapToInt(s -> s instanceof StaticValue st ? st.getValue() : 0).sum();
        }

        static boolean isUnreducedJoltage(Variable other) {
            if (other instanceof Joltage j ) {
                return !j.reduced;
            }
            return false;
        }
    }

    static final class StaticValue implements NumericValue {
        final int value;

        public StaticValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public String toString() {
            return Integer.toString(value);
        }
    }

    static class SwitchBoard {
        int solve(Joltage[] desiredJoltages, List<SwitchGroup> switchGroups) {
            // check for static numbers;

            boolean somethingChanged;
            do {
                testGiven(desiredJoltages, switchGroups);
                switchGroups.stream().forEach(SwitchGroup::propagateReductions);
                testGiven(desiredJoltages, switchGroups);
                for (var j : desiredJoltages) {
                    j.sum.reduce();
                }
                testGiven(desiredJoltages, switchGroups);
                somethingChanged = findNullJoltages(desiredJoltages, switchGroups);
                if (! somethingChanged) {
    //                somethingChanged = findNakedSingles(desiredJoltages);
                }
                if (! somethingChanged) {
                    somethingChanged = findDanglingVariable(desiredJoltages, switchGroups);
                }
                if (! somethingChanged) {
                    somethingChanged = findReducibleVariable(desiredJoltages, switchGroups);
                }

            } while (somethingChanged);
            var dynamics = new ArrayList<SwitchGroup>();
            for (var dynamic : switchGroups) {
                if (dynamic.valueProvider == null) {
                    dynamics.add(dynamic);
                }
            }
            if (dynamics.isEmpty()) {
                System.out.println("sanity-check");
                return check(desiredJoltages, switchGroups);
            }
            int minValue = Integer.MAX_VALUE;

            for (var d : dynamics) {
                var range = new TestValue(0, d.getMaxValue());
                System.out.println("Set " + d.id + " to 0.." + range.max);
                d.valueProvider = range;
            }

            while (true) {

                var currentCheckValue = check(desiredJoltages, switchGroups);
                if (currentCheckValue < minValue) {
                    System.out.println("tempMin = " + currentCheckValue);
                    minValue = currentCheckValue;
                }

                int partialSum = 0;
                boolean carry = false;
                int setIndex = 0;
                do {
                    carry = false;
                    if (setIndex == dynamics.size()) {
                        return minValue;
                    }
                    var number = dynamics.get(setIndex);
                    if (((TestValue)number.valueProvider).increment()) {
                        carry = true;
                        ++setIndex;
                    }
                } while (carry);
            }
        }

        private static void testGiven(Joltage[] desiredJoltages, List<SwitchGroup> switchGroups) {
    /*        var givens = new StaticValue[] {
                    new StaticValue(10),
                    new StaticValue(0),
                    new StaticValue(6),
                    new StaticValue(16),
                    new StaticValue(5),
                    new StaticValue(1),
                    new StaticValue(18),
                    new StaticValue(1),
                    new StaticValue(17)
            };
            var backup = new NumericValue[givens.length];

            for (int i=0;i<backup.length; ++i) {
                var sg = switchGroups.get(i);
                backup[i] = sg.valueProvider;
                if (sg.valueProvider == null) {
                    sg.valueProvider = givens[i];
                }
            }
            var result = check(desiredJoltages, switchGroups);
            if (result != 74) {
                System.out.println(" !!!!!!!!!! CHECKS FAILED !!!!!!!!!!!");
                for (int sgi = 0;sgi<backup.length;sgi++) {
                    var sg = switchGroups.get(sgi);
                    if (givens[sgi].getValue() != sg.getValue()) {
                        System.out.println(givens[sgi] + " vs " + sg.getValue() + " for " + switchGroups.get(sgi));
                    }
                }
                for (var debug :switchGroups) {
                    System.out.println("S = " + debug);
                }
            }

            for (int i=0;i<backup.length; ++i) {
                var sg = switchGroups.get(i);
                sg.valueProvider = backup[i];
            }*/
        }

        private static boolean findNullJoltages(Joltage[] desiredJoltages, List<SwitchGroup> switchGroups) {
            boolean somethingChanged = false;
            for (var digit: desiredJoltages) {
                if (digit.getValue() == 0 && !digit.sum.isEmpty()) {
                    final StaticValue NULL = new StaticValue(0);
                    digit.sum.stream().forEach(d -> {
                        if (d instanceof Variable v) {
                            v.valueProvider = NULL;
                        }
                    });
                    digit.sum.clear();
                    somethingChanged = true;
                }
            }
            return somethingChanged;
        }
        private static boolean findNakedSingles(Joltage[] desiredJoltages) {
            boolean somethingChanged = false;
            for (var digit: desiredJoltages) {
                if (digit.reduced) continue;
                final var sumElements = digit.sum
                        .stream()
                        .filter(SwitchGroup::filterForDynamicNumberWithoutValue)
                        .toList();
                for (var otherDigit : desiredJoltages) {
                    if (digit.reduced) continue;
                    if (digit == otherDigit) continue;
                    final var otherSumElements = otherDigit.sum.stream().filter(SwitchGroup::filterForDynamicNumberWithoutValue).toList();
                    if (otherSumElements.containsAll(sumElements) && otherSumElements.size() == sumElements.size() + 1) {
                        // found a static digit!
                        var set = new HashSet<NumericValue>();
                        set.addAll(otherSumElements);
                        set.removeAll(sumElements);
                        if (! set.isEmpty()) {
                            var staticValue = (SwitchGroup) set.stream().findFirst().get();
                            System.out.println(">>>>>>Naked Single " + staticValue + "of " + otherDigit + " - " + digit);
                            staticValue.resolveDependentJoltage(otherDigit, digit);
                            System.out.println("<<<<<<Naked Single " + staticValue + "of " + otherDigit + " - " + digit);
                            somethingChanged = true;
                            break;
                        }
                    }
                }
            }
            return somethingChanged;
        }
        private static boolean findDanglingVariable(Joltage[] desiredJoltages, List<SwitchGroup> switchGroups) {
            // find dangeling Dynamic Number
            for (var number : switchGroups) {
                var unresolvedDependent = number.dependentVariables.stream().filter(SwitchGroup::filterForDynamicNumberWithoutValue).toList();
                for (var dep : unresolvedDependent) {
                    var dependent = (SwitchGroup) dep;
                    var potentialJoltage = dependent.dependentVariables
                            .stream()
                            .filter(Joltage::isUnreducedJoltage)
                            .filter(j -> ((Joltage)j).sum.stream().filter(d -> d == dependent).count() == 1)
                            .sorted((j1, j2) -> Integer.compare(j2.getValue(), j1.getValue()))
                            .findFirst();
                    if (potentialJoltage.isPresent()) {
                        var joltage = (Joltage) potentialJoltage.get();
                        if (joltage.id.equals("J5") && dependent.id.equals("(1,3,4,5,6)")) {
                            System.out.println("Error here!");
                        }
                        System.out.println(">>>>>>Reduce Dangling " + dependent + " by " + joltage);
                        dependent.resolveBy(joltage);
                        testGiven(desiredJoltages, switchGroups);
                        System.out.println("======Reduce Dangling " + dependent);
                        dependent.propagateReductions();
                        testGiven(desiredJoltages, switchGroups);
                        System.out.println("<<<<<<Reduce Dangling " + dependent);
                        return true;
                    }
                }
            }
            return false;

        }
        private static boolean findReducibleVariable(Joltage[] desiredJoltages, List<SwitchGroup> switchGroups) {
            var options = switchGroups.stream().filter(SwitchGroup::filterForDynamicNumberWithoutValue).sorted(
                            (d1, d2) -> Integer.compare(d2.getMaxValue(), d1.getMaxValue()))
                    .toList();
            for (var dyn : options) {
                boolean isDependendOn = switchGroups.stream().anyMatch(check -> check.dependentVariables.contains(dyn));
                if (isDependendOn) continue;
                var potentialJoltage = dyn.dependentVariables
                        .stream()
                        .filter(Joltage::isUnreducedJoltage)
                        .sorted((j1, j2) -> Integer.compare(j2.getValue(), j1.getValue()))
                        .findFirst();
                if (potentialJoltage.isPresent()) {
                    var joltage = (Joltage) potentialJoltage.get();
                    System.out.println(">>>>Reduce fresh " + dyn + " by " + joltage);
                    dyn.resolveBy(joltage);
                    testGiven(desiredJoltages, switchGroups);
                    System.out.println("====Reduce fresh " + dyn);
                    dyn.propagateReductions();
                    testGiven(desiredJoltages, switchGroups);
                    System.out.println("<<<<Reduce fresh " + dyn);
                    return true;
                }
            }
            return false;
        }
        static int check(Joltage[] desiredJoltages, List<SwitchGroup> switchGroups) {
            for (var j: desiredJoltages) {
                if (j.getValue() != j.sum.getValue()) return Integer.MAX_VALUE;
            }
            var sum = 0;
            for (var s : switchGroups) {
                var v = s.getValue();
                if (v < 0) return Integer.MAX_VALUE;
                sum += v;
            }
            return sum;
        }

    }

    static class SwitchGroup extends Variable {
        public final HashSet<Variable> dependentVariables = new HashSet<>();
        public SwitchGroup(String id) {
            super(id, null);
        }

        @Override
        protected String toStringExtra() {
            StringBuilder sb = new StringBuilder();
            if (valueProvider == null) {
                sb.append("?");
            }else if (! (valueProvider instanceof StaticValue)){
                sb.append("=").append(valueProvider);
            }
    /*        var deps = this.dependentVariables.stream().filter(v -> v instanceof SwitchGroup).map(v -> v.id).toList();
            if (! deps.isEmpty()) {
                sb.append(" deps:").append(deps);
            }*/
            return sb.toString();
        }

        public int getMaxValue() {
            return dependentVariables.stream().mapToInt(
                    d -> {
                        var value = Integer.MAX_VALUE;
                        if (d instanceof Joltage j) {
                            value = j.getValue();
//                            value = j.remainingJoltage();
//                            if (value < 0)
                        }
                        return value;
                    }
            ).min().orElse(0);
        }

        void propagateReductions() {
            if (this.valueProvider instanceof ComputedSum comp) {
    //            System.out.println(">pr" + this);
                this.valueProvider = comp.reduce();
    //            System.out.println("<pr" + this);
            }

        }
        void resolveBy(Joltage j) {
            resolveDependentJoltage(j, null);
        }
        void resolveDependentJoltage(Joltage primary, Joltage secondary) {
            var calculated = new ComputedSum();
            if (secondary == null) {
                primary.reduced = true;
            }
            var action = calculated.actions[primary.sum.positive.contains(this) ? 0 : 1];
            action.posConsumer().accept(primary.valueProvider);
            for (var pos : primary.sum.positive) {
                if (pos == this) continue;
                action.negConsumer().accept(pos);
            }
            for (var neg : primary.sum.negative) {
                if (neg == this) continue;
                action.posConsumer().accept(neg);
            }
            if (secondary != null) {
                action.negConsumer().accept(secondary.valueProvider);
                for (var pos : secondary.sum.positive) {
                    action.posConsumer().accept(pos);
                }
                for (var neg : secondary.sum.negative) {
                    action.negConsumer().accept(neg);
                }
            }
            if (calculated.isEmpty()) {
                this.valueProvider = new StaticValue(0);
                return;
            }

            calculated.stream().filter( v-> v instanceof SwitchGroup).map(d -> (Variable)d).forEach(this.dependentVariables::add);
            this.valueProvider =  calculated.reduceWhenPurelyStatic();
            calculated.stream().filter( v-> v instanceof SwitchGroup).map(d -> (Variable)d).forEach(this.dependentVariables::add);
        }

        static boolean filterForDynamicNumberWithoutValue(NumericValue d) {
            return d instanceof SwitchGroup dyn && dyn.valueProvider == null;
        }

    }

    public static class TestValue implements NumericValue {
        final int min;
        final int max;
        int current;

        public TestValue(int min, int max) {
            this.min = min;
            this.max = max;
            this.current = this.min;
        }
        @Override
        public int getValue() {
            return this.current;
        }

        /**
         *
         * @return true if carry
         */
        public boolean increment() {
            if (++current > max) {
                current = min;
                return true;
            }
            return false;
        }
    }

    abstract static class Variable implements Comparable<Variable>, NumericValue {

        final String id;
        NumericValue valueProvider;

        Variable(String id, NumericValue valueProvider) {
            this.id = id;
            this.valueProvider = valueProvider;
        }

        @Override
        public int getValue() {
            return valueProvider.getValue();
        }

        @Override
        public int compareTo(Variable o) {
            return this.id.compareTo(o.id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Variable other && this.id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{ ").append(id);
            if (valueProvider instanceof StaticValue s)
                sb.append(" == ").append(s.value);
            var extra = toStringExtra();
            if (extra != null) {
                sb.append(" ").append(extra);
            }
            sb.append("}");
            return sb.toString();
         }
         protected String toStringExtra() {
            return null;
         }
    }
}

