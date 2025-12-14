package aoc;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Impl2 {
    public static void main(String[] args) {
        System.out.println(Impl2.solve(System.in));
    }

    static long solve(InputStream in) {
        record Corner(long x, long y) implements Comparable<Corner>{

            @Override
            public int compareTo(Corner o) {
                var cmpX = Long.compare(this.x, o.x);
                if (cmpX == 0) {
                    return Long.compare(this.y, o.y);
                }
                return cmpX;
            }
        }
        final var corners = new ArrayList<Corner>();

        enum Turn {

            CLOCKWISE,
            COUNTERCLOCKWISE,
            NEUTRAL,
            REVERSE
        }
        enum Direction {
            LEFT,
            RIGHT,
            UP,
            DOWN;

            public Turn getTurn(Direction next) {
                if (this == next) {
                    return Turn.NEUTRAL;
                }
                return switch (this) {
                    case LEFT -> switch (next) {
                        case LEFT -> Turn.NEUTRAL;
                        case RIGHT -> Turn.REVERSE;
                        case UP -> Turn.CLOCKWISE;
                        case DOWN -> Turn.COUNTERCLOCKWISE;
                    };
                    case RIGHT -> switch (next) {
                        case LEFT -> Turn.REVERSE;
                        case RIGHT -> Turn.NEUTRAL;
                        case UP -> Turn.COUNTERCLOCKWISE;
                        case DOWN -> Turn.CLOCKWISE;
                    };
                    case UP -> switch (next) {
                        case LEFT -> Turn.COUNTERCLOCKWISE;
                        case RIGHT -> Turn.CLOCKWISE;
                        case UP -> Turn.NEUTRAL;
                        case DOWN -> Turn.REVERSE;
                    };
                    case DOWN -> switch (next) {
                        case LEFT -> Turn.CLOCKWISE;
                        case RIGHT -> Turn.COUNTERCLOCKWISE;
                        case UP -> Turn.REVERSE;
                        case DOWN -> Turn.NEUTRAL;
                    };
                };
            }
        }
        record LineSegment(Corner start, Corner end, Direction dir) {
            public boolean contains(Corner check) {
                return switch (this.dir) {
                    case LEFT, RIGHT :
                        if (check.y != this.start.y) {
                            yield false;
                        }
                        yield contains(check.x);
                    case UP, DOWN :
                        if (check.x != this.start.x) {
                            yield false;
                        }
                        yield contains(check.y);
                };
            }
            public boolean contains(long otherDimension) {
                return switch (this.dir) {
                    case LEFT, RIGHT :
                        yield otherDimension >= Math.min(start.x, end.x)
                                && otherDimension <= Math.max(start.x, end.x);
                    case UP, DOWN :
                        yield otherDimension  >= Math.min(start.y, end.y)
                                && otherDimension <= Math.max(start.y, end.y);
                };

            }
            static LineSegment build(Corner start, Corner end) {
                if (start.x == end.x) {
                    if (start.y < end.y) {
                        return new LineSegment(start, end, Direction.DOWN);
                    }
                    return new LineSegment(start,end, Direction.UP);
                }
                if (start.x < end.x) {
                    return new LineSegment(start, end, Direction.RIGHT);
                }
                return new LineSegment(start,end, Direction.LEFT);
            }
        }
        record Rectangle(Corner a, Corner b, long area) {
            static Rectangle build(Corner a, Corner b) {
                long length = Math.abs(a.x-b.x)+1;
                long height = Math.abs(a.y-b.y)+1;
                long area = length * height;
                return new Rectangle(a, b, area);
            }
            public boolean cornerOn(Corner c) {
                var topY = Math.min(this.a.y, this.b.y);
                var bottomY = Math.max(this.a.y, this.b.y);

                var leftX = Math.min(this.a.x, this.b.x);
                var rightX = Math.max(this.a.x, this.b.x);
                if ( (c.x == leftX|| c.x == rightX) && (c.y == topY ||c.y == bottomY) ) return false; // corner
                return (c.x > leftX && c.x < rightX && c.y > topY && c.y < bottomY); //contained
            }
        }
        enum Tile {
            RED,
            GREEN,
            NONE
        }
        var scanner = new Scanner(in);
        var rectangles = new ArrayList<Rectangle>();
        scanner.useLocale(Locale.US);
        scanner.useDelimiter(Pattern.compile("[ \\t\\n\\x0B\\f\\r,]+"));
        var xEntries = new TreeSet<Long>();
        var yEntries = new TreeSet<Long>();
        final var linesSegments = new ArrayList<LineSegment>();
        {
            Corner firstCorner = null;
            Corner lastCorner = null;
            Corner prevCorner = null;
            while (scanner.hasNextLong()) {
                var corner = new Corner(scanner.nextLong(), scanner.nextLong());
                if (corner.y == 50117) {
                    System.out.println("debug");
                }
                var currentRectangles = corners.parallelStream()
                        .map(other -> Rectangle.build(corner, other)).collect(Collectors.toList());
                rectangles.addAll(currentRectangles);
                xEntries.add(corner.x);
                yEntries.add(corner.y);
                corners.add(corner);
                if (firstCorner == null) {
                    firstCorner = corner;
                }
                if (prevCorner == null) {
                    prevCorner = corner;
                } else {
                    var lineSegment = LineSegment.build(prevCorner, corner);
                    linesSegments.add(lineSegment);
                    prevCorner = corner;
                }
                lastCorner = corner;
            }
            if (firstCorner == null) {
                return 0L;
            }
            var lineSegment = LineSegment.build(prevCorner, firstCorner);
            linesSegments.add(lineSegment);
        }
        var xKeys = new ArrayList<Long>();
        xKeys.addAll(xEntries);
        var yKeys = new ArrayList<Long>();
        yKeys.addAll(yEntries);

        var tileArray = new Tile[xKeys.size()][yKeys.size()];
        {
            // set Corners
            for (var corner : corners) {
                var xIndex = xKeys.indexOf(corner.x);
                var yIndex = yKeys.indexOf(corner.y);
                if (xIndex <0 || yIndex < 0) {
                    System.out.println("Panic!");
                }
                tileArray[xIndex][yIndex] = Tile.RED;
            }
            // fill Lines!
            for (var lineSegment : linesSegments) {
                switch (lineSegment.dir) {
                    case LEFT, RIGHT -> {
                        var yIndex = yKeys.indexOf(lineSegment.start.y);
                        var min = Math.min(lineSegment.start.x, lineSegment.end.x);
                        var max = Math.max(lineSegment.start.x, lineSegment.end.x);
                        var minIndex = xKeys.indexOf(min);
                        var maxIndex = xKeys.indexOf(max);
                        for (var idx = minIndex+1; idx < maxIndex; ++idx) {
                            tileArray[idx][yIndex] = Tile.GREEN;
                        }
                    }
                    case UP, DOWN -> {
                        var xIndex = xKeys.indexOf(lineSegment.start.x);
                        var min = Math.min(lineSegment.start.y, lineSegment.end.y);
                        var max = Math.max(lineSegment.start.y, lineSegment.end.y);
                        var minIndex = yKeys.indexOf(min);
                        var maxIndex = yKeys.indexOf(max);
                        for (var idx = minIndex+1; idx < maxIndex; ++idx) {
                            tileArray[xIndex][idx] = Tile.GREEN;
                        }
                    }
                }
            }
            Supplier<String> debugArray = () -> {
                StringBuffer sb = new StringBuffer();
                for (var y = 0;y<yEntries.size();++y) {
                    for (var x = 0;x<xEntries.size();++x) {
                        var tile = tileArray[x][y];
                        if (tile == null) {
                            sb.append("?");
                            continue;
                        }
                        switch(tile) {
                            case GREEN -> sb.append("G");
                            case RED -> sb.append("R");
                            case NONE -> sb.append(".");
                        }
                    }
                    sb.append("\n");
                }
                return sb.toString();
            };
            // fill up first and last row
            for (var x : new int[]{0, xKeys.size()-1}) {
                for(int y= yKeys.size()-1;y>=0;--y) {
                    if (tileArray[x][y] == null) {
                        tileArray[x][y] = Tile.NONE;
                    }
                }
            }
            // fill up first and last column
            for (var y : new int[]{0, yKeys.size()-1}) {
                for(int x = xKeys.size()-1;x>=0;--x) {
                    if (tileArray[x][y] == null) {
                        tileArray[x][y] = Tile.NONE;
                    }
                }
            }
            {
                System.out.println(" before Fill fill");
                // debug
//                System.out.println(debugArray.get());
            }
            // flood fill L/R
            for (var y = 1; y<yKeys.size()-1;++y) {
                var left = tileArray[0][y];
                for (var x = 1; x<xKeys.size()-1;++x) {
                    var currentTile = tileArray[x][y];
                    if (currentTile == null ) {
                        tileArray[x][y] = left;
                    } else if (currentTile != left) {
                        break;
                    }
                }
            }
            // flood fill R/L
            for (var y = yKeys.size()-1; y>0;--y) {
                var right = tileArray[tileArray.length-1][y];
                System.out.println( right + " @ " + (tileArray.length-1) + "y" +y );
                for (var x = xKeys.size()-1; x>0;--x) {
                    var currentTile = tileArray[x][y];
                    if (currentTile == null) {
                        tileArray[x][y] = right;
                    } else if (currentTile != right)  {
                        System.out.println("Break on " + currentTile + "@x=" + x);
                        break;
                    }
                }
            }
            // flood fill T/B
            for (var x = 1; x<xKeys.size()-1;++x) {
                var top = tileArray[x][0];
                for (var y = 1; y<yKeys.size()-1;++y) {
                    var currentTile = tileArray[x][y];
                    if (currentTile == null) {
                        tileArray[x][y] = top;
                    } else if (currentTile != top) {
                        break;
                    }
                }
            }
            // flood fill B/T
            for (var x = xKeys.size()-1; x>0;--x) {
                System.out.println("x = " + x);
                var bottom = tileArray[x][yKeys.size()-1];
                for (var y = yKeys.size()-1; y>0;--y) {
                    var currentTile = tileArray[x][y];
                    if (currentTile == null ) {
                        tileArray[x][y] = bottom;
                    } else if (currentTile != bottom) {
                        break;
                    }
                }
            }
            {
                System.out.println(" after fill");
                // debug
                System.out.println(debugArray.get());
            }
        }
        {
            var rectIter = rectangles.iterator();
            while (rectIter.hasNext()) {
                var curRect = rectIter.next();
                // check if ANY Corner of the Line is inside the Rectangle
                boolean invalid = false;
                var left = Math.min(curRect.a.x, curRect.b.x);
                var right = Math.max(curRect.a.x, curRect.b.x);
                var top = Math.min(curRect.a.y, curRect.b.y);
                var bottom = Math.max(curRect.a.y, curRect.b.y);
                for (var corner : corners) {
                    if (corner.x > left && corner.x < right && corner.y > top && corner.y < bottom) {
                        System.out.println(corner + " is inside Rectangle " + curRect);
                        invalid = true;
                        break;
                    }
                }
                if (invalid) {
                    rectIter.remove();
                    continue;
                }
                // check counter corners
                if (curRect.a.x == curRect.b.x || curRect.a.y == curRect.b.y) {
                    System.out.println("singleFile file rect " + curRect );
                    continue;
                }

                invalid = false;
                // check whole Rectangle for Tile==NONE
                {
                    var xStart = xKeys.indexOf(Math.min(curRect.a.x, curRect. b.x));
                    var xEnd = xKeys.indexOf(Math.max(curRect.a.x, curRect. b.x));
                    var yStart = yKeys.indexOf(Math.min(curRect.a.y, curRect.b.y));
                    var yEnd = yKeys.indexOf(Math.max(curRect.a.y, curRect.b.y));
                    for (var x=xStart;(!invalid)&&x<=xEnd;++x) {
                        for (var y = yStart; (!invalid)&&y<=yEnd;++y) {
                            System.out.println("   rect " + curRect  + " with Outside @" + x + "," + y);
                            invalid = (tileArray[x][y] == Tile.NONE);
                        }
                    }
                }
                if (invalid) {
                    rectIter.remove();
                    continue;
                }
            }
        }
        rectangles.sort(Comparator.comparing(Rectangle::area).reversed());
        for (var r : rectangles) {
            System.out.println(r);
        }
        return rectangles.get(0).area();
    }
}