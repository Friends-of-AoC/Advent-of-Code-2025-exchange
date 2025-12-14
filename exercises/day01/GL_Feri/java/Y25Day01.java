import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/01
 */
public class Y25Day01 {
	
	public static record InputData(String direction, int count) {
		public int getAddCount() {
			return direction.equals("L") ? -count : count;
		}
	}

	private static final String INPUT_RX = "^([LR])([0-9]+)$";
	
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
				String direction = line.replaceFirst(INPUT_RX, "$1");
				int count = Integer.parseInt(line.replaceFirst(INPUT_RX, "$2"));
				return new InputData(direction, count);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}

	
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		int position = 50;
		int cnt0 = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			if (position == 0) {
				cnt0++;
			}
			position = (position + data.getAddCount() + 100) % 100;
		}
		System.out.println("count 0: "+cnt0);
	}

	
	public static void mainPart2(String inputFile) {
		int position = 50;
		int cnt0 = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			// System.out.println("position: "+position+", add: "+data.getAddCount());
			int oldPosition = position;
			position = position + data.getAddCount();
			if (position == 0) {
				cnt0++;
			}
			else if (position < 0) {
				while (position < 0) {
					position += 100;
					cnt0++;
				}
				if (position == 0) {
					cnt0++;
				}
				if (oldPosition == 0) {
					cnt0--;
				}
			}
			else if (position >= 100) {
				while (position >= 100) {
					position -= 100;
					cnt0++;
				}
			}
			// System.out.println("  newpos: "+position+", cnt0: "+cnt0);
		}
		System.out.println("count 0: "+cnt0);
	}

	

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day01/Feri/input-example.txt");
		mainPart1("exercises/day01/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day01/Feri/input-example.txt");
		mainPart2("exercises/day01/Feri/input.txt");    // > 6003
		System.out.println("---------------");    // 
	}
	
}
