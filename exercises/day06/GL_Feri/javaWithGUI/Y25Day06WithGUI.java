import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/06
 */
public class Y25Day06WithGUI {

	
	static Y25GUIOutput06 output;

	public static record InputData(List<Long> numbers, List<String> operators, String line) {
		public boolean isOpeators() {
			return numbers == null;
		}
	}

	private static final String INPUT_NUMBERS_RX = "^([0-9 ]+)$";
	private static final String INPUT_OPERATORS_RX = "^([+* ]+)$";
	
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
			if (line.matches(INPUT_NUMBERS_RX)) {
				String[] numStrs = line.replaceFirst(INPUT_NUMBERS_RX, "$1").split("[ ]+");
				List<Long> numbers = new ArrayList<>();
				for (String numStr:numStrs) {
					numbers.add(Long.parseLong(numStr));
				}
				return new InputData(numbers, null, rawLine);
			} if (line.matches(INPUT_OPERATORS_RX)) {
				String[] operatorsArray = line.replaceFirst(INPUT_OPERATORS_RX, "$1").split("[ ]+");
				List<String> operators = new ArrayList<>();
				for (String opStr:operatorsArray) {
					operators.add(opStr);
				}
				return new InputData(null, operators, rawLine);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}

	
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		List<List<Long>> allNumbers = new ArrayList<>();
		Long result = 0L;
		for (InputData data:new InputProcessor(inputFile)) {
			if (!data.isOpeators()) {
				allNumbers.add(data.numbers);
			} else {
				System.out.println(data.operators);
				for (int i=0; i<data.operators.size(); i++) {
					String operator = data.operators.get(i);
					long colResult = (operator.equals("+")) ? 0L : 1L;
					for (List<Long> nums:allNumbers) {
						long num = nums.get(i);
						switch (operator) {
						case "+":
							colResult += num;
							break;
						case "*":
							colResult *= num;
							break;
						default:
							throw new RuntimeException("invalid operator '"+operator+"'");
						}
					}
					System.out.println("colResult["+i+"]: " + colResult + " for operator '" + operator + "'");
					result += colResult;
				}
			}
		}
		System.out.println("result: " + result);
	}

	public static String padr(String str, int length, char padChar) {
		StringBuffer sb = new StringBuffer(str);
		while (sb.length() < length) {
			sb.append(' ');
		}
		return sb.toString();
	}
	
	public static String padl(String str, int length, char padChar) {
		if (str.length() >= length) {
			return str;
		}
		return Character.toString(padChar).repeat(length - str.length()) + str;
	}
	
	public static class Visualization {
		List<String> numbers;
		String operatorsLine;
		char[][] matrix;
		int numRows;
		int numCols;
		List<String> typedLines = new ArrayList<>();
		StringBuffer typeLine;
		public Visualization(List<String> numbers, String operatorsLine) {
			this.numbers = numbers;
			this.operatorsLine = operatorsLine+" ";
			this.numRows = numbers.size()+1;
			this.numCols = this.operatorsLine.length();
			this.matrix = new char[numRows][numCols];
			for (int row=0; row<numbers.size(); row++) {
				String numLine = padr(numbers.get(row), numCols, ' ');
				matrix[row] = numLine.toCharArray();
			}
			matrix[numRows-1] = this.operatorsLine.toCharArray();
			this.typedLines = new ArrayList<>();
			this.typedLines.add("");
			this.typeLine = new StringBuffer();
		}
		public void typeMatrix() {
			typeChar(' ', -1, -1);
			typeLine.setLength(0);
			char currentOperator = '?';
			int equalsPos = 0;
			long result = 0L;
			for (int col=0; col<numCols; col++) {
				for (int row=0; row<numRows-1; row++) {
					typeChar(matrix[row][col], row, col);
				}
				char operator = matrix[numRows-1][col];
				char nextOperator = (col<numCols-1) ? matrix[numRows-1][col+1] : 'X';
				char nextNextOperator = (col<numCols-2) ? matrix[numRows-1][col+2] : 'X';
				if (operator != ' ') {
					currentOperator = operator;
				}
				else {
					if (nextNextOperator == ' ') {
						operator = currentOperator;
					}
				}
				if (nextOperator != ' ') {
					operator = '=';
				}
				if (operator != ' ') {
					typeLine.append(' ');
					typeChar(operator, numRows-1, col);
					typeLine.append(' ');
				}
				else {
					typeChar(operator, numRows-1, col);
				}
				if (operator == '=') {
					equalsPos = col;
					long colResult = solve(typeLine.toString(), currentOperator);
					result += colResult;
					typeLine.append(" ").append(padl(Long.toString(colResult), 10, ' '));
					typeChar(' ', numRows-1, col);
					typedLines.add(typeLine.toString());
					typeLine.setLength(0);
				}
			}
			typedLines.add("=".repeat(equalsPos+18));
			typeChar(' ', -1, -1);
			typedLines.add(" ".repeat(equalsPos+8)+padl(Long.toString(result), 10, ' '));
			typeChar(' ', -1, -1);
		}
		private long solve(String term, char operator) {
			String[] parts = term.trim().split("[ *+=]+");
			switch (operator) {
			case '+': {
				long sum = 0L;
				for (String part:parts) {
					if (part.isBlank()) { 
						continue; 
					}
					sum += Long.parseLong(part.trim());
				}
				return sum;
			}
			case '*': {
				long product = 1L;
				for (String part:parts) {
					if (part.isBlank()) { 
						continue; 
					}
					product *= Long.parseLong(part.trim());
				}
				return product;
			}
			default:
				throw new RuntimeException("invalid operator '"+operator+"'");
			}
		}
		public void typeChar(char c, int showRow, int showCol) {
			StringBuffer text = new StringBuffer();
			for (int row=0; row<numRows; row++) {
				for (int col=0; col<numCols; col++) {
					if (row == showRow && col == showCol) {
						text.append("°bor;").append(matrix[row][col]).append("°c0;");
					}
					else {
						text.append(matrix[row][col]);
					}
				}
				text.append("\n");
			}
			
			for (String line:typedLines) {
				text.append(line).append("\n");
			}
			text.append(typeLine.toString()).append("°bor;").append(c).append("°c0;");
			typeLine.append(c);
			output.addStep(text.toString());
		}
	}
	
	
	public static void mainPart2(String inputFile) {

		output = new Y25GUIOutput06("2025 Day 06 Part 1", true);
		
		List<String> numbers = new ArrayList<>();
		for (InputData data:new InputProcessor(inputFile)) {
			if (!data.isOpeators()) {
				numbers.add(data.line + "          ");
			} else {
				
				Visualization vis = new Visualization(numbers, data.line);
				vis.typeMatrix();
			}
		}
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day06/Feri/input-example.txt");
		mainPart1("exercises/day06/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
		mainPart2("exercises/day06/Feri/input-example.txt");
//		mainPart2("exercises/day06/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
