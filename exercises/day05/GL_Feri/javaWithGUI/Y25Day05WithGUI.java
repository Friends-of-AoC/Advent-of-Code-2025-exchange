import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2025/day/05
 */
public class Y25Day05WithGUI {

	
	static Y25GUIOutput05 output;


	
	public static record InputData(Long from, Long to, Long id) {
		public boolean isRange() {
			return from != null && to != null;
		}
		public Range getRange() {
			if (!isRange()) {
				throw new RuntimeException("not a range");
			}
			return new Range(from, to);
		}
	}

	private static final String INPUT_RANGE_RX = "^([0-9]+)-([0-9]+)$";
	private static final String INPUT_ID_RX = "^([0-9]+)$";
	
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
			if (line.matches(INPUT_RANGE_RX)) {
				long from = Long.parseLong(line.replaceFirst(INPUT_RANGE_RX, "$1"));
				long to = Long.parseLong(line.replaceFirst(INPUT_RANGE_RX, "$2"));
				return new InputData(from, to, null);
			} if (line.matches(INPUT_ID_RX)) {
				long id = Long.parseLong(line.replaceFirst(INPUT_ID_RX, "$1"));
				return new InputData(null, null, id);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}

	
	public static class Range {
		public long from;
		public long to;
		public Range(long from, long to) {
			this.from = from;
			this.to = to;
		}
		public boolean contains(long id) {
			return id >= from && id <= to;
		}
		public boolean overlaps(Range other) {
			return this.from <= other.to && other.from <= this.to;
		}
		public Range merge(Range other) {
			return new Range(Math.min(this.from, other.from), Math.max(this.to, other.to));
		}
		public long size() {
			return to - from + 1;
		}
		@Override
		public String toString() {
			return "["+from+"-"+to+"]";
		}
	}

	public static class RangeRenderer {
		List<Long> sortedNumbers;
		public RangeRenderer(List<Range> ranges) {
			sortedNumbers = new ArrayList<>();
			for (Range range:ranges) {
				if (!sortedNumbers.contains(range.from)) {
					sortedNumbers.add(range.from);
				}
				if (!sortedNumbers.contains(range.to)) {
					sortedNumbers.add(range.to);
				}
			}
			sortedNumbers.sort(Long::compareTo);
		}
		public void addNumber(long number) {
			if (!sortedNumbers.contains(number)) {
				sortedNumbers.add(number);
				sortedNumbers.sort(Long::compareTo);
			}
		}
		public String createHeader() {
			int maxNumLen = sortedNumbers.get(sortedNumbers.size()-1).toString().length(); 
			String header = "";
			List<String> sortedNumberStrs = new ArrayList<>();
			for (Long number:sortedNumbers) {
				sortedNumberStrs.add(padl(number.toString(), maxNumLen, ' '));
			}
			StringBuilder result = new StringBuilder();
			for (int numPos=0; numPos<maxNumLen; numPos++) {
				for (String numberStr:sortedNumberStrs) {
					result.append(numberStr.charAt(numPos)).append(" ");
				}
				result.append("\n");
			}
			return result.toString();
		}
		public List<String> createFooter(List<Integer> idPositions) {
			int maxNumLen = sortedNumbers.get(sortedNumbers.size()-1).toString().length(); 
			String header = "";
			List<String> sortedNumberStrs = new ArrayList<>();
			for (Long number:sortedNumbers) {
				sortedNumberStrs.add(padl(number.toString(), maxNumLen, ' '));
			}
			Set<String> idStrs = new HashSet<>();
			for (int idPos:idPositions) {
				idStrs.add(sortedNumberStrs.get(idPos/2).toString());
			}
			List<String> result = new ArrayList<>();
			for (int numPos=0; numPos<maxNumLen; numPos++) {
				StringBuilder line = new StringBuilder();
				for (String numberStr:sortedNumberStrs) {
					if (idStrs.contains(numberStr)) {
						line.append(numberStr.charAt(numPos));
					} else {
						line.append(" ");
					}
					line.append(" ");
				}
				result.add(line.toString());
			}
			return result;
		}
		public String rangeAsString(Range range) {
			int startPos = sortedNumbers.indexOf(range.from) * 2;
			int endPos = sortedNumbers.indexOf(range.to) * 2;
			String result = padl("", startPos, ' ');
			if (endPos == startPos) { 
				result += "|";
			} else {
				result += "[" + padl("]", endPos - startPos, '-');
			}
			return result.toString();
		}
		public void outputRanges(List<Range> ranges) {
			StringBuilder result = new StringBuilder();
			result.append(createHeader()).append("\n");
			
			for (Range range:ranges) {
				result.append(rangeAsString(range)).append("\n");
			}
			output.addStep(result.toString());
		}
		
		private String padl(String text, int maxNumLen, char c) {
			if (text.length() >= maxNumLen) {
				return text;
			}
			String result = Character.toString(c).repeat(maxNumLen - text.length()) + text;
			return result;
		}
		private String padr(String text, int maxNumLen, char c) {
			if (text.length() >= maxNumLen) {
				return text;
			}
			String result = text + Character.toString(c).repeat(maxNumLen - text.length());
			return result;
		}
		
		public void outputIDDrops(List<Range> ranges, List<Long> ids) {
			for (Long id:ids) {
				addNumber(id);
			}
			int lineLength = sortedNumbers.size() * 2;
			List<Integer> idPos = new ArrayList<>();
			for (Long id:ids) {
				idPos.add(sortedNumbers.indexOf(id) * 2);
			}
			
			String header = createHeader();
			
			List<String> rangeLines = new ArrayList<>();
			rangeLines.add(padr("", lineLength, ' '));
			rangeLines.add(padr("", lineLength, ' '));
			for (Range range:ranges) {
				rangeLines.add(padr(rangeAsString(range), lineLength, ' '));
			}
			rangeLines.add(padr("", lineLength, ' '));
			rangeLines.add(padr("", lineLength, ' '));

			List<String> temp = new ArrayList<>(); 
			for (int i=0; i<rangeLines.size(); i++) {
				temp.add(rangeLines.get(i));
				output(header, temp);
			}
			
			for (int i=0; i<rangeLines.size(); i++) {
				String line = rangeLines.get(i);
				StringBuilder lineWithDrops = new StringBuilder(line);
				
				for (Integer pos:new ArrayList<>(idPos)) {
					if (lineWithDrops.charAt(pos) == ' ') {
						lineWithDrops.setCharAt(pos, '|');
					}
					else {
						lineWithDrops.setCharAt(pos, '+');
						idPos.remove(pos);
					}
				}
				rangeLines.set(i, lineWithDrops.toString());
				output(header, rangeLines);
			}
			
			rangeLines.add("");
			output(header, rangeLines);
			
			List<String> footer = createFooter(idPos);
			for (String line:footer) {
				rangeLines.add(line);
				output(header, rangeLines);
			}
			
		}
		
		private void output(String header, List<String> lines) {
			StringBuilder result = new StringBuilder();
			result.append(header).append("\n");
			for (String line:lines) {
				result.append(line).append("\n");
			}
			output.addStep(result.toString());
		}
		
	}
	
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		
		output = new Y25GUIOutput05("2025 Day 05 Part 1", true);

		List<Range> ranges = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		int cntFresh = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			if (data.isRange()) {
				ranges.add(data.getRange());
			} else {
				long id = data.id();
				ids.add(id);
				for (Range range:ranges) {
					if (range.contains(id)) {
						System.out.println(id + " is fresh: " + range);
						cntFresh++;
						break;
					}
				}
			}
		}
		System.out.println("fresh IDs: " + cntFresh);
		
		RangeRenderer renderer = new RangeRenderer(ranges);
		renderer.outputIDDrops(ranges, ids);
	}


	public static void mainPart2(String inputFile) {
		
		output = new Y25GUIOutput05("2025 Day 05 Part 2", true);

		List<Range> ranges = new ArrayList<>();
		int cntFresh = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			if (data.isRange()) {
				ranges.add(data.getRange());
			}
		}
		List<Range> mergedRanges = new ArrayList<>();
		for (Range newRange:ranges) {
			int mergedIdx = 0;
			while (mergedIdx < mergedRanges.size()) {
				Range mergedRange = mergedRanges.get(mergedIdx);
				if (mergedRange.overlaps(newRange)) {
					mergedRanges.remove(mergedIdx);
					newRange = newRange.merge(mergedRange);
				} else {
					mergedIdx++;
				}
			}
			mergedRanges.add(newRange);
		}
		long sum = 0;
		for (Range range:mergedRanges) {
			sum += range.size();
		}
		System.out.println("sum fresh IDs: " + sum);
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
		mainPart1("exercises/day05/Feri/input-example.txt");
//		mainPart1("exercises/day05/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day05/Feri/input-example.txt");
//		mainPart2("exercises/day05/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
	
}
