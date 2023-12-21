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
		int ticks;
		public World() {
			this.rows = new ArrayList<>();
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
			currentPositions.add(startPos);
		}
		private char get(Pos pos) {
			return get(pos.x, pos.y);
		}		
		private char get(int x, int y) {
			if ((x<0) || (y<0) || (x>=maxX) || (y>=maxY)) {
				return '?';
			}
			return field[y][x];
		}		
		public void tick() {
			ticks++;
			Set<Pos> nextPositions = new LinkedHashSet<>();
			for (Pos pos:currentPositions) {
				for (Pos neighbour:pos.getNeighbours()) {
					if (get(neighbour)=='.') {
						nextPositions.add(neighbour);
					}
				}
			}
			currentPositions = nextPositions;
		}
		@Override public String toString() {
			StringBuilder result = new StringBuilder();
			for (int y=0; y<maxY; y++) {
				for (int x=0; x<maxX; x++) {
					char c = get(x,y);
					if (currentPositions.contains(new Pos(x,y))) {
						c = 'O';
					}
					result.append(c);
				}
				result.append("\n");
			}
			return result.toString();
		}
		public void show() {
			StringBuilder result = new StringBuilder();
			String lastColor = "b0";
			for (int y=-1; y<=maxY; y++) {
				for (int x=-1; x<=maxX; x++) {
					char c = (char) ('0'+get(x, y));
					String color = "b0";
//						color = "byellow";
//						color = "bred";
					if (!lastColor.equals(color)) {
						lastColor = color;
						result.append(output.style(color));
					}
					result.append(c);
				}
				result.append("\n");
			}
			output.addStep(result.toString());
		}
	}

	public static void mainPart1(String inputFile) {
//		output = new Y23GUIOutput21("2023 day 21 Part I", true);
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addRow(data.row);
		}
		world.init();
		System.out.println(world);
		for (int i=0; i<64; i++) {
			world.tick();
		}
		System.out.println("TICK: "+world.ticks);
		System.out.println(world);
		System.out.println("#POSITIONS: "+world.currentPositions.size());
	}

	public static void mainPart2(String inputFile) {
//		output = new Y23GUIOutput21("2023 day 21 Part I", true);
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addRow(data.row);
		}
		world.init();
		System.out.println(world);
		for (int i=0; i<64; i++) {
			world.tick();
		}
		System.out.println("TICK: "+world.ticks);
		System.out.println(world);
		System.out.println("#POSITIONS: "+world.currentPositions.size());
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day21/Feri/input-example.txt");
		mainPart1("exercises/day21/Feri/input.txt");               
		System.out.println("---------------");                           
		System.out.println("--- PART II ---");
		mainPart2("exercises/day21/Feri/input-example.txt");
//		mainPart2("exercises/day21/Feri/input.txt");
		System.out.println("---------------");    
	}
	
}
