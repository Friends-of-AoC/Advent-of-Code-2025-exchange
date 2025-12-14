import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/02
 */
public class Y25Day02 {
	
	public static record InputData(String row) {
	}

	private static final String INPUT_RX = "^([-0-9,]+)$";
	
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

	
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		long sum_invalid = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			String[] ranges = data.row().split(",");
			for (String range:ranges) {
				String[] from_to = range.split("-");
				if (from_to[0].startsWith("0") || from_to[1].startsWith("0")) {
					throw new RuntimeException("invalid from '"+from_to[0]+"'");
				}
				long from = Long.parseLong(from_to[0]);
				long to = Long.parseLong(from_to[1]);
				System.out.println("  range: "+from+" - "+to);
				for (long i=from; i<=to; i++) {
					if (checkInvalid(i)) {
						System.out.println("    invalid number: "+i);
						sum_invalid += i;
					}
				}
			}
		}
		System.out.println("sum invalid: "+sum_invalid);
	}

	
	private static boolean checkInvalid(long n) {
		String s = Long.toString(n);
		if (s.length() % 2 != 0) {
			return false;
		}
		int half_len = s.length() / 2;
		String n_left = s.substring(0, half_len);
		String n_right = s.substring(half_len);
		return n_left.equals(n_right);
	}


	public static void mainPart2(String inputFile) {
		long sum_invalid = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			String[] ranges = data.row().split(",");
			for (String range:ranges) {
				String[] from_to = range.split("-");
				if (from_to[0].startsWith("0") || from_to[1].startsWith("0")) {
					throw new RuntimeException("invalid from '"+from_to[0]+"'");
				}
				long from = Long.parseLong(from_to[0]);
				long to = Long.parseLong(from_to[1]);
				System.out.println("  range: "+from+" - "+to);
				for (long i=from; i<=to; i++) {
					if (checkInvalid2(i)) {
						System.out.println("    invalid number: "+i);
						sum_invalid += i;
					}
				}
			}
		}
		System.out.println("sum invalid: "+sum_invalid);
	}

	private static boolean checkInvalid2(long n) {
		String s = Long.toString(n);
		for (int repeats=2; repeats<=s.length(); repeats++) {
			if (checkInvalid2repeats(n, repeats)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkInvalid2repeats(long n, int repeats) {
		String s = Long.toString(n);
		if (s.length() % repeats != 0) {
			return false;
		}
		int part_len = s.length() / repeats;
		String n_first = s.substring(0, part_len);
		String comp = "";
		for (int i=0; i<repeats; i++) {
			comp += n_first;
		}
		return s.equals(comp);
	}
	

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day02/Feri/input-example.txt");
		mainPart1("exercises/day02/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day02/Feri/input-example.txt");
		mainPart2("exercises/day02/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
