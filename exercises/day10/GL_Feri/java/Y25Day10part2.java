import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2025/day/10
 */
public class Y25Day10part2 {

	
	
	public static record InputData(String lights, List<List<Integer>> buttons, List<Integer> nums) {}

	private static final String INPUT_RX = "^\\[([.#]+)\\] [(]([()0-9, ]+)[)] [{]([0-9,]+)[}]$";
	
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
				String lights = line.replaceFirst(INPUT_RX, "$1");
				String buttonString = line.replaceFirst(INPUT_RX, "$2");
				String numsString = line.replaceFirst(INPUT_RX, "$3");
				List<List<Integer>> buttons = new ArrayList<>();
				for (String buttonNumsStr : buttonString.split("[)] *[(]+")) {
					List<Integer> button = new ArrayList<>();
					for (String buttonNumStr : buttonNumsStr.split(",")) {
						button.add(Integer.parseInt(buttonNumStr));
					}
					buttons.add(button);
				}
				List<Integer> nums = new ArrayList<>();
				for (String numStr : numsString.split(",")) {
					nums.add(Integer.parseInt(numStr.trim()));
				}
				return new InputData(lights, buttons, nums);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}
	
	
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		int sum = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			int numButtonPresses = solve(data.lights, data.buttons);
			sum += numButtonPresses;
		}
		System.out.println("SUM: "+sum);
	}



	private static int solve(String targetLightsStr, List<List<Integer>> buttonList) {
		Set<Integer> targetLights = new HashSet<>();
		for (int i = 0; i < targetLightsStr.length(); i++) {
			if (targetLightsStr.charAt(i) == '#') {
				targetLights.add(i);
			}
		}
		List<Set<Integer>> buttons = new ArrayList<>();
		for (List<Integer> buttonNums : buttonList) {
			Set<Integer> button = new HashSet<>(buttonNums);
			buttons.add(button);
		}
		Set<Set<Integer>> currentLightsList = new HashSet<>();
		currentLightsList.add(new HashSet<>());
		int count = 0;
		while (true) {
			count++;
			Set<Set<Integer>> nextLightsList = new HashSet<>();
			for (Set<Integer> currLights : currentLightsList) {
				Set<Set<Integer>> newLightsList = pressAllButtons(currLights, buttons);
				for (Set<Integer> newLights : newLightsList) {
					if (newLights.equals(targetLights)) {
						System.out.println("Found solution in "+count+" steps");
						return count;
					}
					nextLightsList.add(newLights);
				}
			}
			currentLightsList = nextLightsList;
		}
	}



	private static Set<Set<Integer>> pressAllButtons(Set<Integer> currLights, List<Set<Integer>> buttons) {
		Set<Set<Integer>> result = new HashSet<>(); 
		for (Set<Integer> button : buttons) {
			Set<Integer> newLights = new HashSet<>(currLights);
			for (Integer lightNum : button) {
				if (newLights.contains(lightNum)) {
					newLights.remove(lightNum);
				} else {
					newLights.add(lightNum);
				}
			}
			result.add(newLights);
		}
		return result;
	}



	public static void mainPart2(String inputFile) {
		int sum = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			int numButtonPresses = solve2(data.nums, data.buttons);
			sum += numButtonPresses;
		}
		System.out.println("SUM: "+sum);
	}

	

	public static record JoltagesState(int estimate, int count, List<Integer> joltages, int sumJoltage, int sumTargetJoltage, int maxJolatgePerClick, String solution) {
		@Override
		public final int hashCode() {
			return joltages.hashCode();
		}
		@Override
		public final boolean equals(Object other) {
			return joltages.equals(((JoltagesState)other).joltages);
		}
		public JoltagesState clickButons(List<Integer> buttons, int buttonID) {
			int newSumJoltage = sumJoltage + buttons.size();
			List<Integer> newJoltages = new ArrayList<>(joltages);
			for (int button : buttons) {
				newJoltages.set(button, newJoltages.get(button) + 1);
			}
			int newCount = count + 1;
			int newEstimate = newCount*maxJolatgePerClick + sumTargetJoltage-newSumJoltage;
			String newSolution = solution+buttonID;
			return new JoltagesState(
					newEstimate,
					newCount,
					newJoltages,
					newSumJoltage,
					sumTargetJoltage,
					maxJolatgePerClick,
					newSolution
					);
		}
	}
	
	private static int solve2(List<Integer> targetJoltages, List<List<Integer>> buttons) {
		int sumTargetJoltages = 0;
		List<Integer> startJoltages = new ArrayList<>(targetJoltages.size());
		for (Integer joltage : targetJoltages) {
			sumTargetJoltages += joltage;
			startJoltages.add(0);
		}
		buttons.sort((a,b) -> Integer.compare(b.size(), a.size()));
		System.out.println("solve: "+targetJoltages+", buttons: "+buttons);
		int maxJoltagePerClick = buttons.get(0).size();
		JoltagesState initialState = new JoltagesState(
				0,
				0,
				startJoltages,
				0,
				sumTargetJoltages,
				maxJoltagePerClick,
				""
			);
		PriorityQueue<JoltagesState> pq = new PriorityQueue<>(
				(a,b) -> {
					int result = Integer.compare(a.estimate, b.estimate);
					if (result == 0) {
						result = Integer.compare(b.count, a.count);
					}
					return result;
				});
		pq.add(initialState);
		Map<List<Integer>, JoltagesState> bestStates = new HashMap<>();
		long cached = 0;
		long count = 0;
		while (true) {
			count++;
			JoltagesState js = pq.poll();
			if (true || count % 1000000 == 0) {
				System.out.println("At step "+count+", pq size "+pq.size()+", cached "+cached+", "+js);
			}
			
			List<JoltagesState> newStates = pressAllButtons2(js, buttons);
			for (JoltagesState newState : newStates) {
				if (checkLargerThanTarget(newState.joltages, targetJoltages)) {
					continue;
				}
				if (bestStates.containsKey(newState.joltages)) {
					JoltagesState jsCache = bestStates.get(newState.joltages);
					if (jsCache.count > newState.count) {
						throw new RuntimeException("logic error bestCache not best: "+jsCache+" vs "+newState);
					}
					cached++; 
					continue;
				}
				bestStates.put(newState.joltages, newState);
				if (newState.joltages.equals(targetJoltages)) {
					System.out.println("Found solution in "+newState.count+" steps");
					return newState.count;
				}
				pq.add(newState);
			}
		}
	}

	private static List<JoltagesState> pressAllButtons2(JoltagesState js, List<List<Integer>> buttons) {
		List<JoltagesState> result = new ArrayList<>();
		for (int n=0; n<buttons.size(); n++) {
			List<Integer> button = buttons.get(n);
			JoltagesState newState = js.clickButons(button, n);			result.add(newState);
		}
		return result;
	}



	private static boolean checkLargerThanTarget(List<Integer> newJoltages, List<Integer> targetJoltages) {
		for (int i = 0; i < newJoltages.size(); i++) {
			if (newJoltages.get(i) > targetJoltages.get(i)) {
				return true;
			}
		}
		return false;
	}



	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day10/Feri/input-example.txt");
//		mainPart1("exercises/day10/Feri/input.txt");  
		System.out.println("---------------");
//		System.out.println("--- PART II ---");
		mainPart2("exercises/day10/Feri/input-example.txt");
//		mainPart2("exercises/day10/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}
	
}
