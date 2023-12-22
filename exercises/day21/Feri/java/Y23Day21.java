import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2023/day/21
 */
public class Y23Day21 {

	static Y23GUIOutput21 output;

	/*
	 * Example:
	 * 
	 * ...........
	 * .....###.#.
	 * .###.##..#.
	 * ..#.#...#..
	 * ....#.#....
	 * .##..S####.
	 * .##..#...#.
	 * .......##..
	 * .##.#.####.
	 * .##..##.##.
	 * ...........
	 * 
	 */

	private static final String INPUT_RX = "^([.#S]+)$";
	
	public static record InputData(String row) {
		@Override public String toString() { return row; }
	}
	
	public static class InputProcessor implements Iterable<InputData>, Iterator<InputData> {
		private Scanner scanner;
		public InputProcessor(String inputFile) {
			try {
				scanner = new Scanner(new File(inputFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		@Override public Iterator<InputData> iterator() { return this; }
		@Override public boolean hasNext() { return scanner.hasNext(); }
		@Override public InputData next() {
			String line = scanner.nextLine().trim();
			while (line.length() == 0) {
				line = scanner.nextLine();
			}
			if (line.matches(INPUT_RX)) {
				String row = line.replaceFirst(INPUT_RX, "$1");
				return new InputData(row);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}

	static String DIRS            = ">v<^";
	static int[]  DIR_ADD_X 	  = { 1,   0,  -1,   0};
	static int[]  DIR_ADD_Y 	  = { 0,   1,   0,  -1};
	
	static int DIR_EAST = 0;
	static int DIR_SOUTH = 1;
	static int DIR_WEST = 2;
	static int DIR_NORTH = 3;
	
	static int DIR_ROT_LEFT = 3;
	static int DIR_ROT_RIGHT = 1;

	static int rot(int dir, int rot) { return (dir+rot+4)%4; }


	static record Pos(int x, int y) {
		Pos move(int dir) {
			return new Pos(x+DIR_ADD_X[dir], y+DIR_ADD_Y[dir]);
		}		
		Pos move(int dir, int steps) {
			return new Pos(x+steps*DIR_ADD_X[dir], y+steps*DIR_ADD_Y[dir]);
		}		
		public Pos min(Pos other) {
			if ((x<=other.x) && (y<=other.y)) {
				return this;
			}
			if ((other.x<=x) && (other.y<=y)) {
				return other;
			}
			return new Pos(Math.min(x,  other.x), Math.min(y,  other.y));
		}
		public Pos max(Pos other) {
			if ((x>=other.x) && (y>=other.y)) {
				return this;
			}
			if ((other.x>=x) && (other.y>=y)) {
				return other;
			}
			return new Pos(Math.max(x,  other.x), Math.max(y,  other.y));
		}
		@Override public String toString() { return "("+x+","+y+")"; }
		public List<Pos> getNeighbours() {
			List<Pos> result = new ArrayList<>();
			result.add(move(DIR_EAST));
			result.add(move(DIR_SOUTH));
			result.add(move(DIR_WEST));
			result.add(move(DIR_NORTH));
			return result;
		}
	}

	
	public static class World {
		List<String> rows;
		char[][] field;
		int maxX;
		int maxY;
		Pos startPos;
		Set<Pos> currentPositions;
		Set<Pos> evenHistoryPositions;
		Set<Pos> oddHistoryPositions;
		int ticks;
		boolean infinite;
		public World(boolean infinite) {
			this.rows = new ArrayList<>();
			this.infinite = infinite;
		}
		public void addRow(String row) {
			rows.add(row);
		}
		public void init() {
			ticks = 0;
			maxY = rows.size();
			maxX = rows.get(0).length();
			field = new char[maxY][maxX];
			for (int y=0; y<maxY; y++) {
				for (int x=0; x<maxX; x++) {
					char c = rows.get(y).charAt(x);
					if (c=='S') {
						c='.';
						startPos = new Pos(x,y);
					}
					field[y][x] = c;
				}
			}
			this.currentPositions = new LinkedHashSet<>();
			this.evenHistoryPositions = new LinkedHashSet<>();
			this.oddHistoryPositions = new LinkedHashSet<>();
			currentPositions.add(startPos);
		}
		private boolean isEvenTick() {
			return ticks%2 == 0;
		}
		private boolean isOddTick() {
			return ticks%2 == 1;
		}
		private char get(Pos pos) {
			return get(pos.x, pos.y);
		}		
		private char get(int x, int y) {
			if (infinite) {
				x = Math.floorMod(x, maxX);
				y = Math.floorMod(y, maxY);
			}
			else {
				if ((x<0) || (y<0) || (x>=maxX) || (y>=maxY)) {
					return '?';
				}
			}
			return field[y][x];
		}		
		public void tick() {
			Set<Pos> checkNextHistoryPositions;
			if (isEvenTick()) {
				evenHistoryPositions.addAll(currentPositions);
				checkNextHistoryPositions = oddHistoryPositions; 
			}
			else {
				oddHistoryPositions.addAll(currentPositions);
				checkNextHistoryPositions = evenHistoryPositions; 
			}
			Set<Pos> nextPositions = new LinkedHashSet<>();
			for (Pos pos:currentPositions) {
				for (Pos neighbour:pos.getNeighbours()) {
					if (get(neighbour)=='.') {
						if (!checkNextHistoryPositions.contains(neighbour)) {
							nextPositions.add(neighbour);
						}
					}
				}
			}
			currentPositions = nextPositions;
			ticks++;
		}
		@Override public String toString() {
			Set<Pos> historyPositions = isEvenTick() ? evenHistoryPositions : oddHistoryPositions;
			StringBuilder result = new StringBuilder();
			for (int y=0; y<maxY; y++) {
				for (int x=0; x<maxX; x++) {
					char c = get(x,y);
					if (currentPositions.contains(new Pos(x,y)) || historyPositions.contains(new Pos(x,y))) {
						c = 'O';
					}
					result.append(c);
				}
				result.append("\n");
			}
			return result.toString();
		}
		public String toString(int size) {
			Set<Pos> historyPositions = isEvenTick() ? evenHistoryPositions : oddHistoryPositions;
			StringBuilder result = new StringBuilder();
			for (int y=-maxY*(size-1); y<maxY*size; y++) {
				for (int x=-maxX*(size-1); x<maxX*size; x++) {
					char c = get(x,y);
					if (currentPositions.contains(new Pos(x,y)) || historyPositions.contains(new Pos(x,y))) {
						c = 'O';
					}
					result.append(c);
				}
				result.append("\n");
			}
			return result.toString();
		}
		public void show(int size) {
			output.addStep("TICKS: "+ticks+"\n"+toString(size));
		}
		public int getSize() {
			int result = currentPositions.size();
			if (isEvenTick()) {
				result += evenHistoryPositions.size();
			}
			else {
				result += oddHistoryPositions.size();
			}
			return result;
		}
	}

	public static void mainPart1(String inputFile) {
		output = new Y23GUIOutput21("2023 day 21 Part I", true);
		World world = new World(true);
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addRow(data.row);
		}
		world.init();
//		System.out.println(world);
		for (int i=0; i<5000; i++) {
			world.tick();
			System.out.println(world.ticks+" : "+ world.getSize());
		}

		for (int i=0; i<130+131; i++) {
			world.tick();
			world.show(2);
		}
		System.out.println("TICK: "+world.ticks);
		System.out.println(world);
		System.out.println("#POSITIONS: "+world.getSize());
	}

	public static void mainPart2gui(String inputFile) {
		output = new Y23GUIOutput21("2023 day 21 Part II", true);
		World world = new World(true);
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addRow(data.row);
		}
		world.init();
		world.show(2);
		
		for (int i=0; i<5*world.maxX; i++) {
			world.tick();
			world.show(5);
		}
	}

	
	public static void mainPart2(String inputFile, long targetTick, int startIterations) {
//		output = new Y23GUIOutput21("2023 day 21 Part II", true);
		World world = new World(true);
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addRow(data.row);
		}
		world.init();

		
//		for (int i=0; i<5000; i++) {
//			world.tick();
//			System.out.println(world.ticks+" : "+ world.getSize());
//		}
//		
//		if (true) return;
		
		
		
		
		
		
		
		
		
		
		long iterations = targetTick / world.maxX;
		long offset = targetTick - iterations*world.maxX;
		
		System.out.println("start with offset "+offset+" for target tick "+targetTick+" ("+iterations+" iterations of "+world.maxX+")");
		for (int i=0; i<offset; i++) {
			world.tick();
		}
		System.out.println("offset "+offset+": "+world.getSize());
		
//		System.out.println(world);
		for (int n=0; n<startIterations; n++) {
			for (int i=0; i<world.maxX; i++) {
				world.tick();
			}
			System.out.println("iteration "+(n+1)+" (tick "+world.ticks+"): "+world.getSize());
		}
		
		long x1 = startIterations;
		long x2 = startIterations+1;
		long x3 = startIterations+2;
		long x4 = startIterations+3;
		
		System.out.println();
		System.out.println("f(x) = a*x^2+b*x+c");
		System.out.println("f'(x)  = 2*a*x+b");
		System.out.println("f''(x)  = 2*a");
		System.out.println("------------------");

		long y1 = world.getSize();
		System.out.println("f("+x1+")="+y1);

		for (int i=0; i<1*world.maxX; i++) {
			world.tick();
		}
		long y2 = world.getSize();
		System.out.println("f("+x2+")="+y2);

		for (int i=0; i<1*world.maxX; i++) {
			world.tick();
		}
		long y3 = world.getSize();
		System.out.println("f("+x3+")="+y3);

		for (int i=0; i<1*world.maxX; i++) {
			world.tick();
		}
		long y4 = world.getSize();
		System.out.println("f("+x4+")="+y4+"      TICK="+world.ticks);
		
		long yd2 = y2-y1;
		long yd3 = y3-y2;
		long yd4 = y4-y3;

		long ydd3 = yd3-yd2;
		long ydd4 = yd4-yd3;

		System.out.println();
		
		long a = ydd3/2; 
		long aTest = ydd4/2;
		System.out.println("a = f''/2 = "+a);
		if ((a != aTest) || (2*a != ydd3)) {
			throw new RuntimeException("inconsistent result for a: "+a+" != "+aTest);
		}
		
		long bxc4 = y4 - a*x4*x4; 
		long bxc3 = y3 - a*x3*x3; 
		long bxc2 = y2 - a*x2*x2;
		
		long b = bxc4-bxc3;
		long bTest = bxc3-bxc2;
		System.out.println("b = (f(x)-2*a*x^2) - (f(x+1)-2*a*x^2)  = "+b);
		if ((b != bTest)) {
			throw new RuntimeException("inconsistent result for b: "+b+" != "+bTest);
		}
		
		long c = y4-a*x4*x4-b*x4; 
		long cTest = y3-a*x3*x3-b*x3; 
		System.out.println("c = (f(x)-2*a*x^2-b*x) - (f(x+1)-2*a*(x+1)^2-b*(x+1)) = "+c);
		if ((c != cTest)) {
			throw new RuntimeException("inconsistent result for c: "+c+" != "+cTest);
		}
		
		System.out.println();
		System.out.println("derived formula: f(x) = "+a+"*x^2+"+b+"*x+"+c);
		System.out.println("f("+iterations+") = "+(a*iterations*iterations+b*iterations+c));

	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day21/Feri/input-example.txt");
//		mainPart1("exercises/day21/Feri/input.txt");               
		System.out.println("---------------");                           
		System.out.println("--- PART II ---");
//		mainPart2gui("exercises/day21/Feri/input-example.txt");
//		mainPart2gui("exercises/day21/Feri/input.txt");
//		mainPart2("exercises/day21/Feri/input-example.txt", 5000, 3);
//		StopWatch12.run("Day 21 Part II", () -> {
//		mainPart2("exercises/day21/Feri/input.txt", 26501365,0);
//		});
		mainPart2("exercises/day21/Feri/input.txt", 3000,0);
		System.out.println("---------------");    
	}
	
}
