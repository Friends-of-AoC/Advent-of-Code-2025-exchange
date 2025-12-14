import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/03
 */
public class Y25Day03 {
	
	public static record InputData(String row) {
	}

	private static final String INPUT_RX = "^([0-9]+)$";
	
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
		long sum_joltage = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			int joltage = calcJoltage(data.row());
			sum_joltage += joltage;
			System.out.println(data.row+"  joltage: "+joltage);
		}
		System.out.println("sum joltage: "+sum_joltage);
	}

	
	private static int calcJoltage(String nums) {
		String nums1 = nums.substring(0, nums.length()-1);
		int pos1 = -1;
		for (int i=9; i>=0; i--) {
			String search = Integer.toString(i);
			int pos = nums1.indexOf(search);
			if (pos >= 0) {
				pos1 = pos;
				break;
			}
		}
		int pos2 = -1;
		for (int i=9; i>=0; i--) {
			String search = Integer.toString(i);
			int pos = nums.indexOf(search, pos1+1);
			if (pos >= 0) {
				pos2 = pos;
				break;
			}
		}
		int d1 = nums.charAt(pos1) - '0';
		int d2 = nums.charAt(pos2) - '0';
		return d1*10+d2;
	}


	public static void mainPart2(String inputFile) {
		long sum_joltage = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			long joltage = calcJoltage2(data.row());
			sum_joltage += joltage;
			System.out.println(data.row+"  joltage: "+joltage);
		}
		System.out.println("sum joltage: "+sum_joltage);
	}

	
	private static long calcJoltage2(String nums) {
		int currentPos = -1;
		long result = 0;
		for (int b=12; b>0; b--) {
			String numsB = nums.substring(0, nums.length()-b+1);
			int posB = -1;
			for (int i=9; i>=0; i--) {
				String search = Integer.toString(i);
				int pos = numsB.indexOf(search, currentPos+1);
				if (pos >= 0) {
					posB = pos;
					break;
				}
			}
			long d = nums.charAt(posB) - '0';
			result = result * 10 + d;
			currentPos = posB;
		}
		return result;
	}



	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day03/Feri/input-example.txt");
		mainPart1("exercises/day03/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day03/Feri/input-example.txt");
		mainPart2("exercises/day03/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
