import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2025/day/10
 */
public class Y25Day10 {

	
	
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

	

	private static int solve2(List<Integer> targetJoltages, List<List<Integer>> buttonsList) {
		List<Set<Integer>> buttons = new ArrayList<>();
		for (List<Integer> buttonNums : buttonsList) {
			Set<Integer> button = new HashSet<>(buttonNums);
			buttons.add(button);
		}
		List<Integer> startJoltages = new ArrayList<>();
		for (int i = 0; i < targetJoltages.size(); i++) {
			startJoltages.add(0);
		}
		Set<List<Integer>> currentJoltagesList = new HashSet<>();
		currentJoltagesList.add(startJoltages);
		int count = 0;
		while (true) {
			count++;
			Set<List<Integer>> nextJoltagesList = new HashSet<>();
			for (List<Integer> currJoltages : currentJoltagesList) {
				Set<List<Integer>> newJoltagesList = pressAllButtons2(currJoltages, buttons);
				for (List<Integer> newJoltages : newJoltagesList) {
					if (newJoltages.equals(targetJoltages)) {
						System.out.println("Found solution in "+count+" steps");
						return count;
					}
					if (checkLargerThanTarget(newJoltages, targetJoltages)) {
						continue;
					}
					nextJoltagesList.add(newJoltages);
				}
			}
			currentJoltagesList = nextJoltagesList;
		}
	}

	private static Set<List<Integer>> pressAllButtons2(List<Integer> currJoltages, List<Set<Integer>> buttons) {
		Set<List<Integer>> result = new HashSet<>();
		for (Set<Integer> button : buttons) {
			List<Integer> newJoltages = new ArrayList<>(currJoltages);
			for (Integer index : button) {
				newJoltages.set(index, newJoltages.get(index)+1);
			}
			result.add(newJoltages);
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
		mainPart1("exercises/day10/Feri/input.txt");  
		System.out.println("---------------");
//		System.out.println("--- PART II ---");
//		mainPart2("exercises/day10/Feri/input-example.txt");
		mainPart2("exercises/day10/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}
	
}
