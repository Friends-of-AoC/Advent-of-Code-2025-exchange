import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2025/day/01
 */
public class Y25Day01 {
 
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(inputFile));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			System.out.println(line);
		}
	}

	
	public static void mainPart2(String inputFile) {
	}

	

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
		mainPart1("exercises/day01/Feri/input-example.txt");
//		mainPart1("exercises/day01/Feri/input.txt");
		System.out.println("---------------");
		System.out.println("--- PART II ---");
		mainPart2("exercises/day01/Feri/input-example.txt");
//		mainPart2("exercises/day01/Feri/input.txt");     
		System.out.println("---------------");    // 
	}
	
}
