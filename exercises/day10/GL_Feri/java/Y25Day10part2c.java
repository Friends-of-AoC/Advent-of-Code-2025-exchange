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
public class Y25Day10part2c {

	
	
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
			int numButtonPresses = solve2c(data.nums, data.buttons);
			sum += numButtonPresses;
		}
		System.out.println("SUM: "+sum);
	}

	
	public static record Problem(double[][] matrix, double[] vector) {
		
		public double[] solve() {
			double[] solution = Gauss.getSolution(copy(matrix), copy(vector), false);
			if (solution != null) {
				return solution;
			}
			double[] minResult = null;
			int minResultSum = -1;
			for (int n=0; n<vector[0]; n++) {
				solution = solve(n);
				if (solution != null) {
					if (checkValid(solution)) {
						int resultSum = sum(solution);
						if (minResultSum == -1 || resultSum < minResultSum) {
							minResult = solution;
							minResultSum = resultSum;
						}
					}
				}
			}
			return minResult;
		}
		
		public double[] solve(int value0) {
			double[][] rewriteMatrix = copy(matrix);
			double[] rewriteVector = copy(vector);
			for (int i = 0; i < rewriteMatrix.length; i++) {
				rewriteMatrix[0][i] = i==0 ? 1.0 : 0.0;
			}
			rewriteVector[0] = 1.0*value0;
			return Gauss.getSolution(rewriteMatrix, rewriteVector, false);
		}
	}
	
	
	private static int solve2c(List<Integer> targetJoltages, List<List<Integer>> buttons) {
		System.out.println("solve: targetJoltages="+targetJoltages+", buttons="+buttons);
		int numTargets = targetJoltages.size();
		int numButtons = buttons.size();

		double[] vector = new double[numTargets];
		for (int i = 0; i < numTargets; i++) {
			vector[i] = targetJoltages.get(i);
		}
		double[][] matrix = new double[numTargets][numButtons];
		for (int j = 0; j < numButtons; j++) {
			List<Integer> button = buttons.get(j);
			for (Integer varIndex : button) {
				matrix[varIndex][j] = 1;
			}
		}
		double[] solution = solveMatrix(numTargets, numButtons, vector, matrix);
		if (solution == null) {
			System.out.println("no solution found");
			return 1000000;
		}
		System.out.println("solved: "+tos(solution)+" ("+sum(solution)+")");
		return sum(solution);
	}



	private static double[] solveMatrix(int numTargets, int numButtons, double[] vector, double[][] matrix) {
//		System.out.println("numTargets="+numTargets+", numButtons="+numButtons);
		double[] solution;
		if (numTargets == numButtons) {
			solution = solveUsingGauss(matrix, vector);
		}
		//
		//  targetJoltage: [3,5,4,7]
		//  Buttons:       [[3], [1, 3], [2], [2, 3], [0, 2], [0, 1]]
		//  vector:        [3,5,4,7]
		//  matrix:        [[0,0,0,0,1,1],
		//                  [0,1,0,0,0,1],
		//                  [0,0,1,1,1,0],
		//                  [1,1,0,1,0,0]]
		//
		//  numTargets = 4
		//  numButtons = 6
		//
		else if (numButtons > numTargets) {
			solution = solveMoreButtons(numTargets, numButtons, vector, matrix);
		}
		else {
			solution = solveMoreTargets(numTargets, numButtons, vector, matrix);
		}
//		System.out.println("solution: "+tos(solution));
		return solution;
	}


	private static String tos(double[] solution) {
		if (solution != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < solution.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(toInt(solution[i]));
			}
			sb.append("]");
			return sb.toString();
		}
		return null;
	}



	private static double[] solveUsingGauss(double[][] matrix, double[] vector) {
		Problem problem = new Problem(matrix, vector);
		double[] solution = problem.solve();
		if (solution != null) {
			if (checkValid(solution)) {
				return solution;
			}
			return null;
		}
		return null;
	}



	private static int sum(double[] solution) {
		int result = 0;
		for (double v : solution) {
			result += v;
		}
		return result;
	}


	private static double[] solveMoreTargets(int numTargets, int numButtons, double[] vector, double[][] matrix) {
		for (int target=0; target<numTargets; target++) {
			double[] subVector = new double[numTargets-1];
			double[][] subMatrix = new double[numTargets-1][numButtons];
			for (int i=0; i<target; i++) {
				subVector[i] = vector[i];
				subMatrix[i] = copy(matrix[i]);
			}
			for (int i=target; i<subVector.length; i++) {
				subVector[i] = vector[i+1];
				subMatrix[i] = copy(matrix[i+1]);
			}
			double[] result = solveMatrix(numTargets-1, numButtons, subVector, subMatrix);
			if (result != null) {
				return result;
			}
		}
		return null;
	}


	private static double[] solveMoreButtons(int numTargets, int numButtons, double[] vector, double[][] matrix) {
		double[] result = null;
		int minClicks = -1;
		int fixButtons = numButtons - numTargets;
		double[] startButtons = new double[fixButtons];
		double[][] subMatrix = new double[numTargets][numTargets];
		for (int i = 0; i < numTargets; i++) {
			for (int j = 0; j < numTargets; j++) {
				subMatrix[i][j] = matrix[i][j+fixButtons];
			}
		}
		double[] fixLimits = new double[fixButtons];
		for (int button=0; button<fixButtons; button++) {
			fixLimits[button] = calcLimit(matrix, vector, button);
		}
		
		startButtons[0] = -1;
		while (increment(startButtons, fixLimits)) {
			boolean invalid = false;
			double[] subVector = new double[numTargets];
			int clicks = 0;
			System.arraycopy(vector, 0, subVector, 0, numTargets);
			for (int i = 0; i < fixButtons; i++) {
				double buttonPresses = startButtons[i];
				clicks += buttonPresses;
				for (int j = 0; j < numTargets; j++) {
					subVector[j] -= matrix[j][i] * buttonPresses;
					if (subVector[j] < 0) {
						invalid = true;
						break;
					}
				}
				if (invalid) {
					break;
				}
			}
			if (invalid) {
				continue;
			}
			double[] solution = solveUsingGauss(subMatrix, subVector);
			if (solution != null) {
				if (checkValid(solution)) {
					for (double v : solution) {
						clicks += v;
					}
					if ((minClicks == -1) || (clicks < minClicks)) {
						minClicks = clicks;
						result = concat(startButtons, solution);
					}
				}
			}
		}
		return result;
	}



	private static double[] concat(double[] startButtons, double[] solution) {
		double[] result = new double[startButtons.length + solution.length];
		System.arraycopy(startButtons, 0, result, 0, startButtons.length);
		System.arraycopy(solution, 0, result, startButtons.length, solution.length);
		return result;
	}



	private static double[][] copy(double[][] mat) {
		double[][] result = new double[mat.length][];
		for (int i = 0; i < mat.length; i++) {
			result[i] = copy(mat[i]);
		}
		return result;
	}

	private static double[] copy(double[] vec) {
		double[] result = new double[vec.length];
		System.arraycopy(vec, 0, result, 0, vec.length);
		return result;
	}



	private static boolean checkValid(double[] v) {
		for (double d:v) {
			if (d<0.0) {
				return false;
			}
			int i = (int)d;
			if (Math.abs(d-1.0*i) > 1E-9) {
				return false;
			}
		}
		return true;
	}



	private static boolean increment(double[] fixButtons, double[] fixLimit) {
		int idx = 0;
		while (idx < fixButtons.length) {
			if (fixButtons[idx] == fixLimit[idx]) {
				fixButtons[idx] = 0;
				idx++;
				continue;
			}
			fixButtons[idx]++;
			return true;
		}
		return false;
	}



	private static double calcLimit(double[][] matrix, double[] targetVector, int button) {
		double result = 0;
		for (int targetIndex=0; targetIndex<targetVector.length; targetIndex++) {
			if (matrix[targetIndex][button] != 0.0) {
				if (result == 0) {
					result = matrix[targetIndex][button] * targetVector[targetIndex];
				}
				else {
					result = Math.min(result, matrix[targetIndex][button] * targetVector[targetIndex]);
				}
			}
 		}
		return result;
	}



	private static int toInt(double v) {
		int result = (int)v;
		if (Math.abs(v - 1.0*result) >= 1E-9) {
			throw new RuntimeException("value "+v+" is not integer");
		}
		return result;
	}



	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day10/Feri/input-example.txt");
//		mainPart1("exercises/day10/Feri/input.txt");  
		System.out.println("---------------");
//		System.out.println("--- PART II ---");
//		mainPart2("exercises/day10/Feri/input-example.txt");
//		mainPart2("exercises/day10/Feri/input-example-2.txt");
		mainPart2("exercises/day10/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}

	
	
	
}
