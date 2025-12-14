import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2025/day/11
 */
public class Y25Day11 {
	
	public static record InputData(String input, List<String> outputs) {}

	private static final String INPUT_RX = "^([a-z]+)[:] ([a-z ]+)$";
	
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
			String rawLine = scanner.nextLine();
			String line = rawLine.trim();
			while (line.length() == 0) {
				line = scanner.nextLine();
			}
			if (line.matches(INPUT_RX)) {
				String input = line.replaceFirst(INPUT_RX, "$1");
				String[] outputsArray = line.replaceFirst(INPUT_RX, "$2").split(" ");
				List<String> outputs = new ArrayList<>();
				for (String output : outputsArray) {
					outputs.add(output);
				}
				return new InputData(input, outputs);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}
	
	
	
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		Map<String, List<String>> connections = new HashMap<>();
		for (InputData data:new InputProcessor(inputFile)) {
			connections.put(data.input, data.outputs);
		}
		System.out.println(connections);
		
		String start = "you";
		List<String> initialPaths = new ArrayList<>();
		initialPaths.add(start);
		
		List<List<String>> paths = new ArrayList<>();
		paths.add(initialPaths);
		List<List<String>> finalPaths = new ArrayList<>();
		while (paths.size()>0) {
			List<List<String>> nextPaths = new ArrayList<>();
			for (List<String> path:paths) {
				String lastNode = path.get(path.size()-1);
				if (lastNode.equals("out")) {
					finalPaths.add(path);
				} else {
					List<String> nextNodes = connections.get(lastNode);
					for (String nextNode:nextNodes) {
						List<String> newPath = new ArrayList<>(path);
						newPath.add(nextNode);
						nextPaths.add(newPath);
					}
				}
			}
			paths = nextPaths;
			nextPaths = new ArrayList<>();
		}
		System.out.println("FINAL PATHS: "+finalPaths);
		System.out.println("FINAL PATHS: "+finalPaths.size());
	}


	public static void mainPart2(String inputFile) {
		Map<String, List<String>> connections = new HashMap<>();
		for (InputData data:new InputProcessor(inputFile)) {
			connections.put(data.input, data.outputs);
		}
		System.out.println(connections);
		
		String start = "svr";
		Map<String, Long> cache = new HashMap<>();
		long count = recursiveCountPaths(connections, cache, start, false, false);
		System.out.println("TOTAL PATHS: "+count);
	}

	
	public static record PathState(String node, boolean hasDAC, boolean hasFFT) {}
	
	private static long recursiveCountPaths(Map<String, List<String>> connections, Map<String, Long> cache, String node, boolean hasDAC, boolean hasFFT) {
		if (node.equals("out")) {
			if (hasDAC && hasFFT) {
				return 1;
			} else {
				return 0;
			}
		}
		PathState ps = new PathState(node, hasDAC, hasFFT);
		if (cache.containsKey(ps.toString())) {
			return cache.get(ps.toString());
		}
		long sum = 0;
		List<String> nextNodes = connections.get(node);
		if (nextNodes != null) {
			for (String nextNode:nextNodes) {
				long count = recursiveCountPaths(connections, cache, nextNode, hasDAC || nextNode.equals("dac"), hasFFT || nextNode.equals("fft"));
				sum += count;
			}
		}		
		cache.put(ps.toString(), sum); 
		return sum;
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day11/Feri/input-example.txt");
		mainPart1("exercises/day11/Feri/input.txt");  
		System.out.println("---------------");
//		System.out.println("--- PART II ---");
//		mainPart2("exercises/day11/Feri/input-example.txt");
//		mainPart2("exercises/day11/Feri/input-example-2.txt");
		mainPart2("exercises/day11/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}
	
}
