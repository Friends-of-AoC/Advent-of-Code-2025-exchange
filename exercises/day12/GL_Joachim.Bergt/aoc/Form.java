package aoc;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

record Form(BitSet filled, Point2D bottomRight) {
    static Form initialize(int width, int height) {
        return new Form(new BitSet(), new Point2D(width, height));
    }

    static Form initialize(Point2D d) {
        return new Form(new BitSet(), d);
    }

    boolean canFit(Form other) {
        var firstBit = (filled.nextSetBit(0));
        return true;
    }

    /**
     * Calculate the bitindex.
     * Bitstream is formed as lines
     * (x,0) will always return just x;
     * otherwise increment x by (x times form-width)
     * @param point coordinates to transfer
     * @return bitIndex of the internal Bitstream for the given point, based on the Dimensions of this form
     */
    int coordinatesAsIndex(Point2D point) {
        return coordinatesAsIndex(point.x(), point.y());
    }
    int coordinatesAsIndex(int x, int y) {
        return this.bottomRight.x() * y + x;
    }

    Point2D indexAsCoordinates(int idx) {
        return new Point2D(idx % this.bottomRight.x(), Math.floorDiv(idx, this.bottomRight.x()));
    }

    boolean isSet(int index) {
        return this.filled.get(index);
    }
    boolean isSet(Point2D point) {
        return this.filled.get(coordinatesAsIndex(point));
    }

    void set(int index) {
        this.filled.set(index);
    }
    void set(Point2D point) {
        this.filled.set(coordinatesAsIndex(point));
    }

    void clear(int index) {
        this.filled.set(index, false);
    }
    void clear(Point2D point) {
        this.filled.set(coordinatesAsIndex(point), false);
    }

    boolean canInsert(Point2D topLeft, Form other) {
        if (topLeft.x() >= this.bottomRight.x()|| topLeft.y() >= this.bottomRight.y()) return false;
        return false;
    }

    int filledSpaces() {
        return filled.cardinality();
    }

    public String toString() {
        var maxSize = bottomRight.x() * bottomRight.y();
        StringBuilder builder = new StringBuilder(maxSize + bottomRight.y());
        for (int i = 0; i < maxSize; ++i) {
            if (i % bottomRight.x() == 0 && i > 0) {
                builder.append('\n');
            }
            if (this.filled.get(i)) {
                builder.append('#');
            } else {
                builder.append('.');
            }
        }
        return builder.toString();
    }

    Form flipX() {
        var result = new Form(new BitSet(), this.bottomRight);
        var index = -1;
        while ((index = filled.nextSetBit(index + 1)) != -1) {
            var getPoint = this.indexAsCoordinates(index);
            var setPoint = new Point2D(this.bottomRight.x() - getPoint.x() - 1, getPoint.y());
            result.filled.set(coordinatesAsIndex(setPoint));
        }
        return result;
    }

    Form flipY() {
        var result = new Form(new BitSet(), this.bottomRight);
        var index = -1;
        while ((index = filled.nextSetBit(index + 1)) != -1) {
            var getPoint = this.indexAsCoordinates(index);
            var setPoint = new Point2D(getPoint.x(), this.bottomRight.y() - getPoint.y() - 1);
            result.filled.set(coordinatesAsIndex(setPoint));
        }
        return result;
    }

    Form rot90() {
        var result = new Form(new BitSet(), bottomRight.y() == bottomRight.x() ? this.bottomRight : new Point2D(this.bottomRight.y(), this.bottomRight.x()));
        var index = -1;
        while ((index = filled.nextSetBit(index + 1)) != -1) {
            var getPoint = this.indexAsCoordinates(index);
            var setPoint = new Point2D(result.bottomRight.x() - getPoint.y() - 1, getPoint.x());
            result.filled.set(result.coordinatesAsIndex(setPoint));
        }
        return result;
    }

    Form rot180() {
        var result = new Form(new BitSet(), this.bottomRight);
        var index = -1;
        while ((index = filled.nextSetBit(index + 1)) != -1) {
            var getPoint = this.indexAsCoordinates(index);
            var setPoint = new Point2D(this.bottomRight.x() - getPoint.x() - 1, this.bottomRight.y() - getPoint.y() - 1);
            result.filled.set(coordinatesAsIndex(setPoint));
        }
        return result;
    }

    Form rot270() {
        var result = new Form(new BitSet(), bottomRight.y() == bottomRight.x() ? this.bottomRight : new Point2D(this.bottomRight.y(), this.bottomRight.x()));
        var index = -1;
        while ((index = filled.nextSetBit(index + 1)) != -1) {
            var getPoint = this.indexAsCoordinates(index);
            var setPoint = new Point2D(getPoint.y(), result.bottomRight.y() - getPoint.x() - 1);
            result.filled.set(result.coordinatesAsIndex(setPoint));
        }
        return result;
    }

    Set<Form> allIterations() {
        var intermediate = new HashSet<Form>();
        var truncated = this.truncate();
        intermediate.add(truncated);
        intermediate.add(truncated.flipX());
        intermediate.add(truncated.flipY());
        var result = new HashSet<Form>();
        for (var form : intermediate) {
            result.add(form);
            result.add(form.rot90());
            result.add(form.rot180());
            result.add(form.rot270());
        }
        return result;
    }

    /**
     * if there are any surroundng whitespaces, return now form without these
     *
     * @return truncated form, or self
     */
    Form truncate() {
        // sanity check
        if (this.filled.cardinality() == 0) {
            return new Form(new BitSet(), new Point2D(0,0));
        }
        // check trailing spaces
        int minX = this.bottomRight.x()-1;
        int maxX = 0;
        int minY = this.bottomRight().y()-1;
        int maxY = 0;
        for (int index = this.coordinatesAsIndex(this.bottomRight)-1; index >= 0; --index) {
            if (filled.get(index)) {
                var coord = this.indexAsCoordinates(index);
                minX = Math.min(minX, coord.x());
                maxX = Math.max(maxX, coord.x());
                minY = Math.min(minY, coord.y());
                maxY = Math.max(maxY, coord.y());
            }
        }
        if (minX == 0 && minY == 0 && maxX == this.bottomRight.x()-1 && maxY == this.bottomRight.y()-1) {
            return this;
        }
        var result = new Form(new BitSet(), new Point2D(maxX - minX + 1, maxY-minY + 1));
        var resultIndex = 0;
        var startIndex = coordinatesAsIndex(new Point2D(minX, minY));
        for (var y = minY ; y<=maxY ; ++y) {
            for (var x = minX; x<=maxX; ++x) {
                var getIndex = startIndex + x - minX;
                boolean value = this.filled.get(getIndex);
                result.filled.set(resultIndex, value);
                ++resultIndex;
            }
            startIndex += this.bottomRight.x();
        }
        return result;
    }
    int bitsWastedInCorner(boolean topToBottom, boolean leftToRight) {

        var result = 0;
        for (
                var y = topToBottom ? 0 : (this.bottomRight.y()-1 );
                topToBottom ? y<this.bottomRight.y() : y>=0;
                y += (topToBottom ? 1 : -1)
        ) {
            var lineResult = 0;
            for (
                var x = leftToRight ? 0 : (this.bottomRight.x() - 1) ;
                leftToRight ? x<this.bottomRight.y() : x>=0;
                x += (leftToRight ? 1 : -1)
        ) {
                var index = this.coordinatesAsIndex(x,y);
                if (! this.filled.get(index)) {
                    ++lineResult;
                } else {
                    break;
                }
            }
            if (lineResult > 0) {
                result += lineResult;
            } else {
                break;
            }
        }
        return result;
    }
    int bitsWastedInTopLeftCorner() {
        return bitsWastedInCorner(true, true);
    }
    int bitsWastedInTopRightCorner() {
        return bitsWastedInCorner(true, false);
    }
    int bitsWastedInBottomLeftCorner() {
        return bitsWastedInCorner(false, true);
    }
    int bitsWastedBottomRightCorner() {
        return bitsWastedInCorner(false, false);
    }

}
