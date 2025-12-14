import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/10
 */
public class Y25Day10part2 {


	public static record InputData(int lineNr, String lights, List<List<Integer>> buttons, List<Integer> nums) {}

	public static boolean DEBUG = false;
	public static boolean DEBUG_GAUSS = false;

	public static int[] TEST_VALUES_PER_DEPTH = null;
//	public static int[] TEST_VALUES_PER_DEPTH = {13, 0, 1, 23};
//	public static int[] TEST_VALUES_PER_DEPTH = {1,1};

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
		private int lineNr;
		public InputProcessor(String inputFile) {
			try {
				scanner = new Scanner(new File(inputFile));
				lineNr = 0;
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		@Override public Iterator<InputData> iterator() { return this; }
		@Override public boolean hasNext() { return scanner.hasNext(); }
		@Override public InputData next() {
			String rawLine = scanner.nextLine();
			lineNr++;
			String line = rawLine.trim();
			while (line.length() == 0) {
				line = scanner.nextLine();
				lineNr++;
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
				return new InputData(lineNr, lights, buttons, nums);
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

	
	public static boolean isZero(double d) {
		return Math.abs(d) < 1e-9;
	}

	
	public static record Problem(double[][] matrix, double[] vector) {
		
		public MyLGS.CLDResult getLinearDependencies() {
			return MyLGS.solve(copy(matrix), copy(vector));
		}
		public boolean hasLinearDependencies() {
			return getLinearDependencies().status() == MyLGS.CLD_STATUS.DEPENDENT_ROW;
		}
		public boolean isInconsistent() {
			return getLinearDependencies().status() == MyLGS.CLD_STATUS.INCONSISTENT_SYSTEM;
		}
		
		public double[] solve(int depth) {
			MyLGS.CLDResult lgsResult = MyLGS.solve(copy(matrix), copy(vector));
			if (lgsResult.isInconsistent()) {
				// inconsistent system
				return null;
			}
			if (lgsResult.hasDependentRow()) {
				return solveWithLinearDependencies(depth, lgsResult);
			}
			double[] solution = lgsResult.solution();
			if (solution == null) {
				throw new RuntimeException("solution should be found");
			}
			return solution;
		}
		
		private double[] solveWithLinearDependencies(int depth, MyLGS.CLDResult depCheck) {
			int dependentRow = depCheck.dependentRow();
			int dependentCol = depCheck.dependentCol();
			int maxValue = (int) calcLimit(matrix, vector, dependentCol);
			double[] removedCol = getColumn(dependentCol);
			double[] decCol = vecRemove(removedCol, dependentRow);
			
			double[] minSolution = null;

			int initValue = 0;
			if ((TEST_VALUES_PER_DEPTH != null) && (depth<TEST_VALUES_PER_DEPTH.length)) {
				initValue = TEST_VALUES_PER_DEPTH[depth];
			}
			for (int value=initValue; value<=maxValue; value++) {
				Problem subProblem = remove(dependentRow, dependentCol);
				for (int n=0; n<subProblem.vector.length; n++) {
					subProblem.vector[n] -= decCol[n] * value;
				}
				if (!checkValid(subProblem.vector)) {
					continue;
				}
				if (DEBUG) {
					System.out.println("depth: "+depth+", removing row "+dependentRow+", col "+dependentCol+", setting value "+value+" -> subproblem:\n"+subProblem.toString());
				}
				double[] solution = subProblem.solve(depth+1);  
				if (solution == null) {
					continue;
				}
				solution = vecInsert(solution, dependentCol, value);
				if (checkSolutiont(solution)) {
					if ((minSolution == null) || (sum(solution) < sum(minSolution))) {
						minSolution = solution;
					}
				}
			}
			return minSolution;
		}

		private boolean checkSolutiont(double[] solution) {
			if (!checkValid(solution)) {
				return false;
			}
			double[] calcVector = matrixMult(solution);
			if (!checkEquals(calcVector, vector)) {
				return false;
			}
			return true;
		}
		
		
		private double[] matrixMult(double[] solution) {
			double[] result = new double[matrix.length];
			for (int row=0; row<matrix.length; row++) {
				double calc = 0.0d;
				for (int col=0; col<matrix[row].length; col++) {
					calc += matrix[row][col] * solution[col];
				}
				result[row] = calc;
			}
			return result;
		}
		
		private boolean checkEquals(double[] vector1, double[] vector2) {
			for (int i=0; i<vector1.length; i++) {
				if (!isZero(vector1[i]-vector2[i])) {
					return false;
				}
			}
			return true;
		}
		
		private double[] getColumn(int col) {
			double[] result = new double[matrix.length];
			for (int row=0; row<matrix.length; row++) {
				result[row] = matrix[row][col];
			}
			return result;
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
	
		public Problem removeCol(int colToRemove) {
			double[][] newMatrix = new double[matrix.length][matrix[0].length-1];
			double[] newVector = copy(vector);
			for (int row = 0; row < newMatrix.length; row++) {
				for (int col = 0; col < newMatrix[row].length; col++) {
					int sourceCol = (col < colToRemove) ? col : col + 1;
					newMatrix[row][col] = matrix[row][sourceCol];
				}
			}
			return new Problem(newMatrix, newVector);
		}
	
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			for (int row = 0; row < matrix.length; row++) {
				for (int col = 0; col < matrix[0].length; col++) {
					result.append(toInt(matrix[row][col])).append(";");
				}
				result.append(";;").append(toInt(vector[row])).append("\n");
			}
			return result.toString();
		}
		
		public String toDisplayString() {
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
		Problem problem = new Problem(matrix, vector);
		if (!problem.checkSolutiont(solution)) {
			throw new RuntimeException("invalid solution found: "+tos(solution));
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
			solution = solveMoreButtons(numTargets, numButtons, vector, matrix, 0);
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
				sb.append(toIntNoCheck(solution[i]));
			}
			sb.append("]");
			return sb.toString();
		}
		return null;
	}


	private static double[] solveMinimalDiscreteUsingGauss(double[][] matrix, double[] vector) {
		Problem problem = new Problem(matrix, vector);
		double[] solution = problem.solve(0);
		return solution;	
	}
	

	private static double[] vecInsert(double[] solution, int col, int value) {
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


	private static double[] vecRemove(double[] vector, int n) {
		double[] result = new double[vector.length - 1];
		for (int i = 0; i < vector.length; i++) {
			if (i < n) {
				result[i] = vector[i];
			}
			else if (i > n) {
				result[i-1] = vector[i];
			}
		}
		return result;
	}


	private static int sum(double[] solution) {
		int result = 0;
		for (double v : solution) {
			result += toInt(v);
		}
		return result;
	}


	private static double[] solveMoreTargets(int numTargets, int numButtons, double[] vector, double[][] matrix) {
		Problem problem = new Problem(matrix, vector);
		for (int i = numButtons; i < numTargets; i++) {
			MyLGS.CLDResult depCheck = problem.getLinearDependencies();
			problem = problem.removeRow(depCheck.dependentRow());
		}
		double[] solution = problem.solve(0);
		return solution;
	}


	private static double[] solveMoreButtons(int numTargets, int numButtons, double[] vector, double[][] matrix, int depth) {
		
		List<Integer> minColIndex = sortColumnsByCountOfOnes(matrix);
//		System.out.println("sorted button indices by count of ones: "+minColIndex);
		
		Problem problem = new Problem(matrix, vector);
		int colToRemove = minColIndex.get(0);
		int maxValue = (int) calcLimit(matrix, vector, colToRemove);
		Problem subProblem = problem.removeCol(colToRemove);
		double[] subVector0 = copy(subProblem.vector);
		double[] minSolution = null;
		int initValue = 0;
		if ((TEST_VALUES_PER_DEPTH != null) && (depth<TEST_VALUES_PER_DEPTH.length)) {
			initValue = TEST_VALUES_PER_DEPTH[depth];
		}
		for (int value=initValue; value<=maxValue; value++) {
			for (int n=0; n<subProblem.vector.length; n++) {
				subProblem.vector[n] = subVector0[n] - matrix[n][colToRemove] * value;
			}
			double[] subSolution;
			if (numTargets == numButtons - 1) {
				subSolution = subProblem.solve(depth+1);
			}
			else {
				subSolution = solveMoreButtons(numTargets, numButtons-1, subProblem.vector, subProblem.matrix, depth+1);
			}	
			if (checkValid(subSolution)) {
				double[] solution = vecInsert(subSolution, colToRemove, value);
				if (problem.checkSolutiont(solution)) {
					if ((minSolution == null) || (sum(solution) < sum(minSolution))) {
						minSolution = solution;
					}
				}
			}
		}
		return minSolution;
	}


	private static record CountIndex(int count, int index) {}
	
	private static List<Integer> sortColumnsByCountOfOnes(double[][] mat) {
		List<CountIndex> result = new ArrayList<>();
		for (int col = 0; col < mat[0].length; col++) {
			int count = 0;
			for (int row = 0; row < mat.length; row++) {
				if (!isZero(mat[row][col])) {
					count++;
				}
			}
			result.add(new CountIndex(count, col));
		}
		result.sort((a,b) -> Integer.compare(a.count, b.count));
		return result.stream().map(ci -> ci.index).toList();
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
			if (d+1e-10<0.0) {
				return false;
			}
			int i = (int)(d+1e-10);
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



	private static int toIntNoCheck(double v) {
		return (int) (v+1e-10);
	}

	private static int toInt(double v) {
		int result = (int) (v+1e-10);
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
//		mainPart2("exercises/day10/Feri/input-example-3.txt");
//		mainPart2("exercises/day10/Feri/input-example-4.txt");
//		mainPart2("exercises/day10/Feri/input-example-5.txt");
//		mainPart2("exercises/day10/Feri/input-example-6.txt");
//		mainPart2("exercises/day10/Feri/input-example-7.txt");
//		mainPart2("exercises/day10/Feri/input-example-7b.txt");
//		mainPart2("exercises/day10/Feri/input-example-8.txt");
		mainPart2("exercises/day10/Feri/input.txt");             //  16765
//		System.out.println("---------------");    //  
	}

	
	
	
}
