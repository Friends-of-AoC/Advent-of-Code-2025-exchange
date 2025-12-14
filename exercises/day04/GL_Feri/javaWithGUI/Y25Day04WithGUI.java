import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/04
 */
public class Y25Day04WithGUI {

	
	static Y25GUIOutput04 output;


	
	public static record InputData(String row) {
	}

	private static final String INPUT_RX = "^([.@]+)$";
	
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

	public record RollPos(int row, int col) {}
	
	public static class World {
		List<String> grid;
		int numRows;
		int numCol;
		public World() {
			this.grid = new ArrayList<>();
			this.numRows = 0;
			this.numCol = 0;
		}
		public void addRow(String row) {
			grid.add(row);
			numRows++;
			numCol = row.length();
		}
		public char get(int row, int col) {
			if (row<0 || row>=numRows || col<0 || col>=numCol) {
				return '.';
			}
			return grid.get(row).charAt(col);
		}
		public void set(int row, int col, char value) {
			String rowString = grid.get(row);
			rowString = rowString.substring(0, col) + value + rowString.substring(col+1);
			grid.set(row, rowString);
		}
		@Override public String toString() {
			StringBuilder result = new StringBuilder();
			for (String row:grid) {
				result.append(row).append("\n");
			}
			return result.toString();
		}
		public void displayInGUI() {
			StringBuilder sb = new StringBuilder(); 
			for (int row=0; row<numRows; row++) {
				String lastColor = "°c0;";
				for (int col=0; col<numCol; col++) {
					char c = get(row, col);
					String color;
					if (c == '@') {
						color = "°bor;";
					} else if (c == 'x') {
						color = "°bye;";
					} else {
						color = "°c0;";
					}
					if (!color.equals(lastColor)) {
						sb.append(color);
						lastColor = color;
					}
					sb.append(c);
				}
				sb.append("°c0;\n");
			}
			output.addStep(sb.toString());
		}
		public int removeLiftableRolls() {
			List<RollPos> liftableRolls = getLiftableRolls();
			for (RollPos pos:liftableRolls) {
				set(pos.row, pos.col, 'x');
			}
			return liftableRolls.size();
		}
		public List<RollPos> getLiftableRolls() {
			List<RollPos> result = new ArrayList<>();
			for (int row=0; row<numRows; row++) {
				for (int col=0; col<numCol; col++) {
					if (isRoll(row,col) && isLiftable(row, col)) {
						result.add(new RollPos(row, col));
					}
				}
			}
			return result;
		}
		public int calcLiftableRolls() {
			int result = 0;
			for (int row=0; row<numRows; row++) {
				for (int col=0; col<numCol; col++) {
					if (isRoll(row,col) && isLiftable(row, col)) {
						result++;
					}
				}
			}
			return result;
		}
		private boolean isLiftable(int row, int col) {
			return countNeighbourRolls(row, col) < 4;
		}
		private int countNeighbourRolls(int row, int col) {
			int neighbourRolls = 0;
			for (int deltaRow=-1; deltaRow<=1; deltaRow+=1) {
				for (int deltaCol=-1; deltaCol<=1; deltaCol+=1) {
					if (deltaRow == 0 && deltaCol == 0) {
						continue;
					}
					if (isRoll(row+deltaRow, col+deltaCol)) {
						neighbourRolls++;
					}
					// process cell at (row,col) with direction (deltaRow, deltaCol)
				}
			}
			return neighbourRolls;
		}
		private boolean isRoll(int row, int col) {
			return get(row, col) == '@';
		}
		public void removeX() {
			for (int row=0; row<numRows; row++) {
				for (int col=0; col<numCol; col++) {
					if (get(row, col) == 'x') {
						set(row, col, '.');
					}
				}
			}
		}
	}
	
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {

		output = new Y25GUIOutput04("2025 Day 04 Part 1", true);
		
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
			world.addRow(data.row);
		}
		System.out.println(world);
		System.out.println("Liftable rolls: " + world.calcLiftableRolls());
	}


	public static void mainPart2(String inputFile) {
		
		output = new Y25GUIOutput04("2025 Day 04 Part 2", true);
		
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
			world.addRow(data.row);
		}
		world.displayInGUI();
		int removedRolls = 0;
		int cnt = -1;
		while (cnt != 0) {
			cnt = world.removeLiftableRolls();
			removedRolls += cnt;
			world.displayInGUI();
			world.removeX();
		}
		System.out.println(world);
		System.out.println("Removed rolls: " + removedRolls);
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day04/Feri/input-example.txt");
//		mainPart1("exercises/day04/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
		mainPart2("exercises/day04/Feri/input-example.txt");
//		mainPart2("exercises/day04/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
