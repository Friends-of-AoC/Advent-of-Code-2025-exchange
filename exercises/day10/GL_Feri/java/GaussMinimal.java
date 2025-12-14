/*
 * @author Sebastian Peuser <c-line AT mailbox.tu-berlin DOT de>
 */

public class GaussMinimal {

	
	public static boolean FIX = true;
	
	public static class LinearDependentException extends Exception {
		public int row;
		public LinearDependentException(int row) {
			super("Gleichungssystem nicht eindeutig loesbar! (Zeile " + row + " ist linear abhaengig)");
			this.row = row;
		}
		public int getRow() {
			return row;
		}
	}
	
	public static void main(String[] args) throws GaussMinimal.LinearDependentException {
		double[][] matrix = { { 1, 1, 1 }, { 43, 0, 31 }, { 4, 0, 7 } };
		double[] vector = { 2, 4, 8 };

		/*
		 * "true" oder "false" kann als dritter Parameter uebergeben werden.
		 * "true" -> Zwischenschritte werden auf der Konsole ausgegeben "false"
		 * -> nichts wird ausgegeben
		 */
		printVector(getSolution(matrix, vector, false));
	}

	/*
	 * Gauss-Jordan-Algorithmus nur fuer eindeutige Gleichungssysteme geeignet
	 * (andernfalls wird NULL zurueckgegeben) matrix[row][column]
	 */
	public static double[] getSolution(double[][] matrix, double[] vector, boolean printSteps) throws LinearDependentException {
		// Das Gleichungssystem hat keine eindeutige Loesung!
		if (matrix.length < matrix[0].length) {
			System.out.println("Gleichungssystem nicht eindeutig loesbar!");
			return null;
		}

		int[] origLineNumbers = new int[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			origLineNumbers[i] = i;
		}
		
		// Merken der Spalte, welche eine Zahl ungleich null besitzt
		int tmpColumn = -1;

		// Alle Zeilen durchgehen: Ziel der for-Schleife -> Matrix in
		// Zeilenstufenform bringen!
		// -> Alle Zahlen unterhalb der Diagonale sind null
		for (int line = 0; line < matrix.length; line++) {
			tmpColumn = -1;

			// Umformungsschritt 1: Finden einer Spalte mit einem Wert ungleich
			// null
			for (int column = 0; column < matrix[line].length; column++) {
				for (int row = line; row < matrix.length; row++) {
					if (matrix[row][column] != 0) {
						tmpColumn = column;
						break;
					}
				}

				// Abbruch, zahl ungleich null wurde gefunden
				if (tmpColumn != -1) {
					break;
				}
			}

			// NullZeile(n) entdeckt!
			if (tmpColumn == -1) {
				for (int row = line; row < matrix.length; row++) {
					// Gleichungssystem hat keine Loesung!
					if (!nearlyZero(vector[line])) {
						// Wenn die Zwischenschritte ausgegeben werden sollen
						if (printSteps) {
							printStep(matrix, vector);
						}

						throw new LinearDependentException(origLineNumbers[line]);
//						return null;
					}
				}
				// Nullzeile(n) vorhanden -> Ist das System noch eindeutig
				// loesbar?
				if (matrix[0].length - 1 >= line) {
					
					if (FIX) {
						matrix[line][line] = 1;
						tmpColumn = line;
					}
					else {
					// Wenn die Zwischenschritte ausgegeben werden sollen
					if (printSteps) {
						printStep(matrix, vector);
					}

					// System nicht eindeutig loesbar.
					throw new LinearDependentException(origLineNumbers[line]);
//					return null;
					}					
					
				}
				break;
			}

			// Umformungsschritt 2: Die Zahl matrix[line][tmpColumn] soll
			// UNgleich null sein
			if (matrix[line][tmpColumn] == 0) {
				for (int row = line + 1; row < matrix.length; row++) {
					if (matrix[row][tmpColumn] != 0) {
						// Wenn die Zwischenschritte ausgegeben werden sollen
						if (printSteps) {
							printStep(matrix, vector);
							System.out.println("Zeile " + (line + 1) + " wird mit Zeile " + (row + 1) + " getauscht");
						}

						// Vertauschen von Zeilen -> matrix[line][tmpColumn]
						// wird dann ungleich null
						swapTwoLines(line, row, matrix, vector, origLineNumbers);
						break;
					}
				}
			}

			// Umformungsschritt 3: matrix[line][tmpColumn] soll gleich 1 sein.
			if (matrix[line][tmpColumn] != 0) {
				// Wenn die Zwischenschritte ausgegeben werden sollen
				if (printSteps) {
					printStep(matrix, vector);
					System.out.println("Zeile " + (line + 1) + " wird durch " + matrix[line][tmpColumn] + " geteilt");
				}

				// Division der Zeile mit matrix[line][tmpColumn]
				divideLine(line, matrix[line][tmpColumn], matrix, vector);
			}

			// Wenn die Zwischenschritte ausgegeben werden sollen
			if (printSteps) {
				printStep(matrix, vector);
			}

			// Umformungsschritt 4: Alle Zahlen unter matrix[line][tmpColumn]
			// sollen null sein.
			for (int row = line + 1; row < matrix.length; row++) {
				// Wenn die Zwischenschritte ausgegeben werden sollen
				if (printSteps) {
					System.out.println("Zu Zeile " + (row + 1) + " wird subtrahiert: " + matrix[row][tmpColumn] + " * Zeile " + (line + 1));
				}

				// Subtraktion damit unter der Zahl im Umformungsschritt 3 nur
				// nullen stehen
				removeRowLeadingNumber(matrix[row][tmpColumn], line, row, matrix, vector);
			}
		}

		// Umformungsschritt 6: Matrix in Normalform bringen (Zahlen oberhalb
		// der Diagonale werden ebenfalls zu null)
		for (int column = matrix[0].length - 1; column > 0; column--) {
			// Wenn die Zwischenschritte ausgegeben werden sollen
			if (printSteps) {
				printStep(matrix, vector);
			}

			// Alle Werte oberhalb von "column" werden zu null
			for (int row = column; row > 0; row--) {
				// Wenn die Zwischenschritte ausgegeben werden sollen
				if (printSteps) {
					System.out.println("Zu Zeile " + (row) + " wird subtrahiert: " + matrix[row - 1][column] + " * Zeile " + (column + 1));
				}

				// Dazu wird Subtraktion angewandt
				removeRowLeadingNumber(matrix[row - 1][column], column, row - 1, matrix, vector);
			}
		}

		// Wenn die Zwischenschritte ausgegeben werden sollen
		if (printSteps) {
			printStep(matrix, vector);
		}

		// Unser ehemaliger Loesungsvektor ist jetzt zu unserem Zielvektor
		// geworden :)
		return vector;
	}

	private static boolean nearlyZero(double d) {
		return Math.abs(d) < 1e-9;
	}

	/*
	 * Hier werden einfach zwei Zeilen vertrauscht
	 */
	private static void swapTwoLines(int rowOne, int rowTwo, double[][] matrix, double[] vector, int[] origLineNumbers) {
		double[] tmpLine;
		double tmpVar;

		tmpLine = matrix[rowOne];
		tmpVar = vector[rowOne];

		matrix[rowOne] = matrix[rowTwo];
		vector[rowOne] = vector[rowTwo];

		matrix[rowTwo] = tmpLine;
		vector[rowTwo] = tmpVar;
		
		int tmpOrig = origLineNumbers[rowOne];
		origLineNumbers[rowOne] = origLineNumbers[rowTwo];
		origLineNumbers[rowTwo] = tmpOrig;
	}

	/*
	 * eine Zeile wird durch "div" geteilt. "div" darf nicht null sein
	 */
	private static void divideLine(int row, double div, double[][] matrix, double[] vector) {
		for (int column = 0; column < matrix[row].length; column++) {
			matrix[row][column] = matrix[row][column] / div;
		}
		vector[row] = vector[row] / div;
	}

	/*
	 * Eine Zeile (row) wird mit einem entsprechendem vielfachen (factor) von
	 * einer anderen Zeile (rowRoot) subtrahiert.
	 */
	private static void removeRowLeadingNumber(double factor, int rowRoot, int row, double[][] matrix, double[] vector) {
		for (int column = 0; column < matrix[row].length; column++) {
			matrix[row][column] = matrix[row][column] - factor * matrix[rowRoot][column];
		}
		vector[row] = vector[row] - factor * vector[rowRoot];
	}

	/*
	 * Ein Vector wird auf der Konsole ausgegeben (transponiert)
	 */
	public static void printVector(double[] vector) {
		if (vector == null) {
			return;
		}
		System.out.println();
		System.out.print("Loesungsvektor ist: (");
		for (int i = 0; i < vector.length; i++) {
			if (i != 0) {
				System.out.print(",");
			}
			System.out.print(vector[i]);
		}
		System.out.println(")^T");
	}

	/*
	 * Eine Matrix wird auf der Konsole ausgegeben matrix[row][column]
	 */
	public static void printMatrix(double[][] matrix) {
		if (matrix == null) {
			return;
		}
		for (int row = 0; row < matrix.length; row++) {
			System.out.print("(");
			for (int column = 0; column < matrix[row].length; column++) {
				if (column != 0) {
					System.out.print(",");
				}
				System.out.print(matrix[row][column]);
			}
			System.out.println(")");
		}
	}

	/*
	 * Diese Methode zeigt die Zwischenschritte der Berechnung auf der Konsole
	 * an. Fuer die Aufgabe nicht weiter relevant (unbekannte Konzepte werden
	 * verwendet!)
	 */
	private static void printStep(double[][] matrix, double[] vector) {
		System.out.println();

		// Werte werden fuer die Ausgabe auf ein bestimmtes Format gebracht
		// -> Damit die Ausgabe auch immer schick aussieht
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				if (matrix[row][column] >= 0) {
					System.out.print("+");
				}
				System.out.print(df.format(matrix[row][column]) + "    ");
			}
			System.out.print("|    ");
			if (vector[row] >= 0) {
				System.out.print("+");
			}
			System.out.println(df.format(vector[row]));
		}
	}
}