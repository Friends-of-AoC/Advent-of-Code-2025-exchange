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
public class Y25Day10part2e {


	public static record InputData(String lights, List<List<Integer>> buttons, List<Integer> nums) {}

	public static boolean DEBUG = false;
	
	private static final String INPUT_RX = "^\\[([.#]+)\\] [(]([()0-9, ]+)[)] [{]([0-9,]+)[}]$";

	
	private static String padl(double d, int len) {
		return padl(Double.toString(d), len);
	}
	private static String padl(String txt, int len) {
		if (txt.length() >= len) {
			return txt;
		}
		return " ".repeat(len - txt.length()) + txt;
	}

	
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


	public static void mainPart2(String inputFile) {
		int sum = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			int numButtonPresses = solve2(data.nums, data.buttons);
			sum += numButtonPresses;
		}
		System.out.println("SUM: "+sum);
	}

	
	public static record Problem(double[][] matrix, double[] vector) {
		
		public double[] solve() throws GaussMinimal.LinearDependentException {
			double[] solution;
			solution = GaussMinimal.getSolution(copy(matrix), copy(vector), DEBUG);
			return solution;
		}

		public Problem remove(int rowToRemove, int colToRemove) {
			double[][] newMatrix = new double[matrix.length-1][matrix.length-1];
			double[] newVector = new double[vector.length-1];
			for (int row = 0; row < newMatrix.length; row++) {
				int sourceRow = (row < rowToRemove) ? row : row + 1;
				newVector[row] = vector[sourceRow];
				for (int col = 0; col < newMatrix[0].length; col++) {
					int sourceCol = (col < colToRemove) ? col : col + 1;
					newMatrix[row][col] = matrix[sourceRow][sourceCol];
					
				}
			}
			return new Problem(newMatrix, newVector);
		}
		
		public Problem removeRow(int row) {
			double[][] newMatrix = new double[matrix.length-1][];
			double[] newVector = new double[vector.length-1];
			for (int i = 0; i < matrix.length; i++) {
				if (i < row) {
					newMatrix[i] = copy(matrix[i]);
					newVector[i] = vector[i];
				}
				else if (i > row) {
					newMatrix[i-1] = copy(matrix[i]);
					newVector[i-1] = vector[i];
				}
			}
			return new Problem(newMatrix, newVector);
		}
	
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			for (int row = 0; row < matrix.length; row++) {
				for (int col = 0; col < matrix[0].length; col++) {
					result.append(padl(matrix[row][col], 5));
				}
				result.append(" | ").append(padl(vector[row], 5)).append("\n");
			}
			return result.toString();
		}

	}
	
	
	private static int solve2(List<Integer> targetJoltages, List<List<Integer>> buttons) {
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



	/*
	 * 
	 *  targetJoltage: [3,5,4,7]
	 *  Buttons:       [[3], [1, 3], [2], [2, 3], [0, 2], [0, 1]]
	 *  vector:        [3,5,4,7]
	 *  matrix:        [[0,0,0,0,1,1],
	 *	 *                  [0,1,0,0,0,1],
	 *                  [0,0,1,1,1,0],
	 *                  [1,1,0,1,0,0]]
	 *
	 *  numTargets = 4
	 *  numButtons = 6
	 * 
	 */
	private static double[] solveMatrix(int numTargets, int numButtons, double[] vector, double[][] matrix) {
//		System.out.println("numTargets="+numTargets+", numButtons="+numButtons);
		double[] solution;
		if (numTargets == numButtons) {
			solution = solveMinimalDiscreteUsingGauss(matrix, vector);
		}
		else if (numButtons > numTargets) {
			solution = solveMoreButtons(numTargets, numButtons, vector, matrix);
		}
		else {
			solution = solveMoreTargets(numTargets, numButtons, vector, matrix);
		}
//		System.out.println("solution: "+tos(solution));
		return solution;
	}



	private static double[] solveSquare(double[] vector, double[][] matrix) {
		double[] solution;
		try {
			solution = solveUsingGauss(matrix, vector);
		}
		catch (GaussMinimal.LinearDependentException e) {
			solution = null;
		}
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



	private static double[] solveUsingGauss(double[][] matrix, double[] vector) throws GaussMinimal.LinearDependentException {
		Problem problem = new Problem(matrix, vector);
		double[] solution = problem.solve();
		if (checkValid(solution)) {
			return solution;
		}
		return null;
	}


	private static double[] solveMinimalDiscreteUsingGauss(double[][] matrix, double[] vector) {
		Problem problem = new Problem(matrix, vector);
		try {
			double[] solution = problem.solve();
			if (checkValid(solution)) {
				return solution;
			}
//			return null;
			return solution;
		} catch (GaussMinimal.LinearDependentException e) {
			throw new RuntimeException(e);
		}
	}

	private static double[] insert(double[] solution, int col, int value) {
		double[] result = new double[solution.length + 1];
		for (int i = 0; i < result.length; i++) {
			if (i < col) {
				result[i] = solution[i];
			}
			else if (i == col) {
				result[i] = value;
			}
			else {
				result[i] = solution[i-1];
			}
		}
		return result;
	}


	private static List<Integer> getAllButtonsInRow(double[] row) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < row.length; i++) {
			if (row[i] != 0.0) {
				result.add(i);
			}
		}
		return result;
	}



	private static int sum(double[] solution) {
		int result = 0;
		for (double v : solution) {
			result += v;
		}
		return result;
	}


	private static double[] solveMoreTargets(int numTargets, int numButtons, double[] vector, double[][] matrix) {
		double[][] fullMatrix = new double[numTargets][numTargets];
		for (int row = 0; row < numTargets; row++) {
			for (int col = 0; col < numTargets; col++) {
				int sourceCol = (col < numButtons) ? col : numButtons - 1;
				fullMatrix[row][col] = matrix[row][sourceCol];
			}
		}
		double[] solution = solveMinimalDiscreteUsingGauss(fullMatrix, vector);
		return solution;
	}


	private static double[] solveMoreButtons(int numTargets, int numButtons, double[] vector, double[][] matrix) {
		double[][] fullMatrix = new double[numButtons][numButtons];
		double[] fullVector = new double[numButtons];
		for (int row = 0; row < numButtons; row++) {
			int sourceRow = (row < numTargets) ? row : numTargets - 1;
			for (int col = 0; col < numButtons; col++) {
				fullMatrix[row][col] = matrix[sourceRow][col];
			}
			fullVector[row] = vector[sourceRow];
		}
		double[] solution = solveMinimalDiscreteUsingGauss(fullMatrix, fullVector);
		return solution;
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
		if (v == null) {
			return false;
		}
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
		mainPart2("exercises/day10/Feri/input-example.txt");
//		mainPart2("exercises/day10/Feri/input-example-2.txt");
//		mainPart2("exercises/day10/Feri/input-example-3.txt");
//		mainPart2("exercises/day10/Feri/input-example-4.txt");
//		mainPart2("exercises/day10/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}

	
	
	
}
