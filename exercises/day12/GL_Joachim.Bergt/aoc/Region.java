package aoc;

import java.util.Arrays;
import java.util.BitSet;

public record Region(BitSet space, Point2D dimensions, int[] requirements, PresentShape[] presents, int dof) {
    static Region of(Point2D dimensions, int[] requirements, PresentShape[] presents) {
        var maximumSpace = dimensions.x() * dimensions.y();
        var presentSpace = 0;
        for (var presentIndex = 0; presentIndex<presents.length;++presentIndex) {
            presentSpace += presents[presentIndex].size() * requirements[presentIndex];
        }
        int dof = maximumSpace - presentSpace;
        return new Region(new BitSet(), dimensions, requirements, presents, dof);
    }
    /**
     * winfast: more presents withoutlap passible the the required sum!
     * @return winfast
     */
    boolean trivialCheckDimensionsLowerBound() {
        var x = Math.floorDiv(dimensions.x(), 3);
        var y = Math.floorDiv(dimensions.y(), 3);
        var trivialPresentAmount = x*y;
        var presentSum = Arrays.stream(requirements).sum();
        return trivialPresentAmount >= presentSum;
    }

    /**
     * failfast ... dimensions are smaller that minimal required present spaces, at best packing
     * @return ! failfast (true if still possible)
     */
    boolean trivialCheckDimensionUpperBound() {
        var maximumSpace = dimensions.x() * dimensions().y();
        var presentSpace = 0;
        for (var presentIndex = 0; presentIndex<presents.length;++presentIndex) {
            presentSpace += presents[presentIndex].size() * requirements[presentIndex];
        }
        if (presentSpace > maximumSpace) {
//            System.out.println("^ " + dimensions + " can contain at most " + maximumSpace + ", but the presents require " + presentSpace);
            return false;
        }
        return true;
    }
    boolean checkDegreesOfFreedom() {
        var minPossible = (Math.floorDiv(dimensions.x(),3) * Math.floorDiv(dimensions.y(),3));
        var required = Arrays.stream(requirements).sum();
        var diff = required - minPossible;
        System.out.println(
                "?" + dimensions + " DOF " + dof + " gits:" + required + " trivialSpace:" + minPossible + " delta " + diff
        );
        return true;
    }
}
