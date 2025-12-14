import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/05
 */
public class Y25Day05 {
	
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
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		List<Range> ranges = new ArrayList<>();
		int cntFresh = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			if (data.isRange()) {
				ranges.add(data.getRange());
			} else {
				long id = data.id();
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
	}


	public static void mainPart2(String inputFile) {
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
//		mainPart1("exercises/day05/Feri/input-example.txt");
		mainPart1("exercises/day05/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day05/Feri/input-example.txt");
		mainPart2("exercises/day05/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
