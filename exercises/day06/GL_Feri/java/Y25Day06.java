import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/06
 */
public class Y25Day06 {
	
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


	public static void mainPart2(String inputFile) {
		List<String> numbers = new ArrayList<>();
		for (InputData data:new InputProcessor(inputFile)) {
			if (!data.isOpeators()) {
				numbers.add(data.line + "          ");
			} else {
				String operatorsLine = data.line + "X";
				Long result = 0L;
				long colResult = 0L;
				char currentOperator = '?';
				for (int i=0; i<operatorsLine.length(); i++) {
					char operator = operatorsLine.charAt(i);
					if (operator != ' ') {
						System.out.println("colResult reset to " + colResult + " for operator '" + currentOperator + "'");
						result += colResult;
						if (operator == 'X') {
							break;
						}
						currentOperator = operator;
						colResult = operator == '+' ? 0L : 1L;
					}
					String colNum = "";
					for (String numLine:numbers) {
						char numChar = numLine.charAt(i);
						if (numChar != ' ') {
							colNum += numChar;
						}
					}
					if (colNum.length() == 0) {
						continue;
					}
					long num = Long.parseLong(colNum);
					switch (currentOperator) {
					case '+':
						colResult += num;
						break;
					case '*':
						colResult *= num;
						break;
					default:
						throw new RuntimeException("invalid operator '"+currentOperator+"'");
					}
				}
				System.out.println("result: " + result);
			}
		}
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day06/Feri/input-example.txt");
		mainPart1("exercises/day06/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("exercises/day06/Feri/input-example.txt");
		mainPart2("exercises/day06/Feri/input.txt");    // not 31884165731
		System.out.println("---------------");    // 
	}
	
}
