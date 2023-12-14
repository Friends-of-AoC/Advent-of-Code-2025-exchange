import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2023/day/14
 */
public class Y23Day14 {

	/*
	 * Example:
	 * 
	 * O....#....
	 * O.OO#....#
	 * .....##...
	 * OO.#O....O
	 * .O.....O#.
	 * O.#..O.#.#
	 * ..O..#O..O
	 * .......O..
	 * #....###..
	 * #OO..#....
	 * 
	 */

	private static final String INPUT_RX = "^([.#O]*)$";
	
	public static record InputData(String row) {
		@Override public String toString() {
			return row;
		}
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

	
	public static class World {
		char[][] field;
		List<String> rows;
		int maxX;
		int maxY;
		long ticks;
		public World() {
			rows = new ArrayList<>();
		}
		public void init() {
			maxY = rows.size();
			maxX = rows.get(0).length();
			field = new char[maxY][];
			ticks = 0;
			for (int y=0; y<maxY; y++) {
				field[y] = rows.get(y).toCharArray();
			}
		}
		public void addRow(String row) {
			rows.add(row);
		}
		public void tiltNorth() {
			tilt(0, -1);
		}
		public void tiltWest() {
			tilt(-1, 0);
		}
		public void tiltSouth() {
			tilt(0, +1);
		}
		public void tiltEast() {
			tilt(+1, 0);
		}
		public void tilt(int dx, int dy) {
			boolean changed = true;
			while (changed) {
				changed = false;
				for (int y=0; y<maxY; y++) {
					for (int x=0; x<maxX; x++) {
						if ((get(x, y)=='O') && (get(x+dx, y+dy) == '.')) {
							changed = true;
							set(x,y,'.');
							set(x+dx,y+dy,'O');
						}
					}
				}
			}
		}
		public int countLoad() {
			int result = 0;
			for (int y=0; y<maxY; y++) {
				for (int x=0; x<maxX; x++) {
					if (get(x,y)=='O') {
						result = result + maxY-y;
					}
				}
			}
			return result;
		}
		private void set(int x, int y, char c) {
			field[y][x]=c;
		}
		char get(int x, int y) {
			if ((x<0) || (x>=maxX) || (y<0) || (y>=maxY)) {
				return '#';
			}
			return field[y][x];
		}
		@Override public String toString() {
			StringBuilder result = new StringBuilder();
			for (int y=0; y<maxY; y++) {
				result.append(new String(field[y])).append('\n');
			}
			return result.toString();
		}
		public void tick() {
			ticks++;
			tiltNorth();
			tiltWest();
			tiltSouth();
			tiltEast();
		}
	}

	public static void mainPart1(String inputFile) {
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			world.addRow(data.row);
		}
		System.out.println("---");
		world.init();
		System.out.println(world);
		world.tiltNorth();
		System.out.println(world);
		System.out.println("LOAD: "+world.countLoad());
	}

	
	public static class CircleDetector {
		Map<String, Long> previousWorldsAtTick;
		long circleLength;
		long lastTick;
		public CircleDetector(World world) {
			this.previousWorldsAtTick = new LinkedHashMap<>();
			this.circleLength = 0;
			detectCircle(world);
		}
		public boolean circleDetected() {
			return circleLength != 0;
		}
		public boolean detectCircle(World world) {
			if (circleDetected()) {
				return true;
			}
			String worldString = world.toString();
			long tick = world.ticks;
			if (previousWorldsAtTick.containsKey(worldString)) {
				lastTick = tick;
				long oldTick = previousWorldsAtTick.get(worldString);
				circleLength = lastTick - oldTick;
				System.out.println("FOUND CIRCLE starting at tick "+oldTick+" with length "+circleLength);
				return true;
			}
			previousWorldsAtTick.put(worldString, tick);
			return false;
		}
	}

	
	public static void mainPart2(String inputFile) {
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			world.addRow(data.row);
		}
		System.out.println("---");
		world.init();
		CircleDetector circleDetector = new CircleDetector(world);
		while (!circleDetector.circleDetected()) {
			world.tick();
			circleDetector.detectCircle(world);
		}
		long targetTick = 1000000000L;
		long circleLength = circleDetector.circleLength;
		long targetIndex = targetTick % circleLength;
		while ((world.ticks%circleLength) != targetIndex) {
			world.tick();
		}
		System.out.println(world);
		System.out.println("LOAD: "+world.countLoad());
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day14/Feri/input-example.txt");
		mainPart1("exercises/day14/Feri/input.txt");               
		System.out.println("---------------");                           
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day14/Feri/input-example.txt");
		mainPart2("exercises/day14/Feri/input.txt");              
		System.out.println("---------------");    //
	}
	
}
