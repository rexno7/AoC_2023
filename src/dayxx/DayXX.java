package dayxx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DayXX {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day16/resources/test.txt"));
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			System.out.println(line);
		}
	}
}
