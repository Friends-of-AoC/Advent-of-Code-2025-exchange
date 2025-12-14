import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/03
 */
public class Y25Day03WithGUI {

	
	static Y25GUIOutput03 output;

	
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


	public static class ScrollingOutput {
		List<String> lines;
		int maxLines = 20;
		String prefix;
		public ScrollingOutput(int maxLine, String prefix) {
			this.lines = new ArrayList<>();
			this.maxLines = maxLine;
			this.prefix = prefix;
		}

		public void addLine(String text) {
			lines.add(text);
			if (lines.size() > maxLines) {
				lines.remove(0);
			}
		}
		
		public void replaceLine(String text) {
			lines.set(lines.size()-1, text);
		}
		
		public void show() {
			show(-1);
		}
		public void show(int lastPos) {
			show(lastPos, false);
		}
		public void show(int lastPos, boolean noPrefixLastLine) {
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<lines.size(); i++) {
				String line = lines.get(i);
				if (i == lines.size()-1 && lastPos != -1) {
					String rest = line.length()<=lastPos+1 ? "" : line.substring(lastPos+1);
					String cursor = line.length()<=lastPos ? "" : "°bor;" + line.charAt(lastPos) + "°c0;";
					line = line.substring(0, lastPos) + cursor +  rest;
				}
				if (i != lines.size()-1 || !noPrefixLastLine) {
					sb.append(prefix);
				}
				sb.append(line).append("\n");
			}
			output.addStep(sb.toString());
		}
	}
	
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		
		output = new Y25GUIOutput03("2025 Day 03 Part 1", true);
		
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


	public static void mainPart2(String inputFile, int numBats) {
		
		output = new Y25GUIOutput03("2025 Day 03 Part 2", true);
		
		ScrollingOutput scrollingOutput = new ScrollingOutput(12, " ");
		scrollingOutput.show();
		
		long sum_joltage = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			long joltage = calcJoltage2(data.row(), numBats);
			sum_joltage += joltage;
			showJoltage(scrollingOutput, data.row(), joltage);
			System.out.println(data.row+"  joltage: "+joltage);
		}
		scrollingOutput.addLine("===============".substring(0, numBats));
		scrollingOutput.show();
		scrollingOutput.addLine(Long.toString(sum_joltage));
		scrollingOutput.show(-1, true);
		System.out.println("sum joltage: "+sum_joltage);
	}

	
	private static void showJoltage(ScrollingOutput scrollingOutput, String nums, long joltage) {
		scrollingOutput.addLine(nums);
		String joltageStr = Long.toString(joltage);
		String target = nums;;
		for (int i=0; i<joltageStr.length(); i++) {
			scrollingOutput.show(i);
			char d = joltageStr.charAt(i);
			int pos = target.indexOf(d, i);
			for (int j=i; j<pos; j++) {
				target = target.substring(0, i) + target.substring(i+1);
				scrollingOutput.replaceLine(target);
				scrollingOutput.show(i);
			}
		}
		while (target.length() > joltageStr.length()) {
			target = target.substring(0, joltageStr.length()) + target.substring(joltageStr.length()+1);
			scrollingOutput.replaceLine(target);
			scrollingOutput.show(joltageStr.length());
		}
	}


	private static long calcJoltage2(String nums, int len) {
		int currentPos = -1;
		long result = 0;
		for (int b=len; b>0; b--) {
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
//		mainPart1("exercises/day03/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day03/Feri/input-example.txt", 12);
		mainPart2("exercises/day03/Feri/input-example.txt", 6);
//		mainPart2("exercises/day03/Feri/input-example2.txt", 12);
//		mainPart2("exercises/day03/Feri/input.txt", 12);    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
