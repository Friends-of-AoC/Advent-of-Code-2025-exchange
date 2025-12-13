import java.util.HashSet;
import java.util.Set;

public class MyLGS {

	
	public static boolean DEBUG = false;
	
	
	public static boolean isZero(double d) {
		return Math.abs(d) < 1e-9;
	}
	public static boolean nearlyEqual(double d1, double d2) {
		return Math.abs(d2-d1) < 1e-9;
	}
	
	public static int checkLinearDependenies(double[][] matrix, double[] vector) {
		
		showMatrix("Initial matrix", matrix, vector);
		
		int numRows = matrix.length;
		int numCols = matrix[0].length;
		
		// remember swaps to identify dependent row
		int[] origRows = new int[numRows];
		for (int i=0; i<numRows; i++) {
			origRows[i] = i;
		}

		// iterate over columns
		for (int col=0; col<numCols; col++) {
			// swap next row if neccessary to have a value at col
			for (int row=col; row<numRows; row++) {
				if (!isZero(matrix[row][col])) {
					if (col != row) {
						swapRows(col, row, matrix, vector, origRows);
					}
					break;
				}
			}
			int row = col;
			if (isZero(matrix[row][col])) {
				// dependent row found
				// check whether row is all zero
				boolean allZero = true;
				for (int c=col+1; c<numCols; c++) {
					if (!isZero(matrix[row][c])) {
						// found non-zero entry -> not dependent
						allZero = false;
						break;
					}
				}
				if (allZero) {
					if (!isZero(vector[row])) {
						// inconsistent system
						showMatrix("found inconsistent row "+(row+1), matrix, vector);
						return -2;
					}
					showMatrix("found dependent row", matrix, vector);
					return origRows[row];
				}
				return origRows[row];
			}

			// normalize current row to 1.0
			if (!nearlyEqual(matrix[row][col], 1.0d)) {
				double factor = 1.0d/matrix[row][col];
				matrix[row][col] = 1.0d;
				for (int c=col+1; c<numCols; c++) {
					matrix[row][c] = matrix[row][c] * factor;
				}
				vector[row] = vector[row] * factor;
				showMatrix("normalized row "+(row+1)+" by "+factor, matrix, vector);
			}
			
			// eliminate leading number in other rows below
			for (int r=row+1; r<numRows; r++) {
				if (!isZero(matrix[r][col])) {
					// normalize to 1.0 and subtract current row
					double factor = 1.0d/matrix[r][col];
					matrix[r][col] = 0.0d;
					for (int c=col+1; c<numCols; c++) {
						matrix[r][c] = factor * matrix[r][c] - matrix[row][c];
					}
					vector[r] = factor * vector[r] - vector[row];
					showMatrix("eliminate row "+(r+1)+" by "+factor+" and subtraction", matrix, vector);
				}
			}
			
		}
		// more rows than needed -> dependent rows
		if (numCols < numRows) {
			// dependent row found
			showMatrix("more rows than cols", matrix, vector);
			return origRows[numCols];
		}
		return -1;
	}


	private static void swapRows(int row1, int row2, double[][] matrix, double[] vector, int[] origRows) {
		double[] tmpRow = matrix[row1];
		matrix[row1] = matrix[row2];
		matrix[row2] = tmpRow;
		
		double tmpVal = vector[row1];
		vector[row1] = vector[row2];
		vector[row2] = tmpVal;
		
		int tmpOrig = origRows[row1];
		origRows[row1] = origRows[row2];
		origRows[row2] = tmpOrig;

		showMatrix("Swapping row "+(row1+1)+" with row "+(row2+1), matrix, vector);
	}
	
	private static void showMatrix(String title, double[][] matrix, double[] vector) {
		if (!DEBUG) {
			return;
		}
		StringBuffer result = new StringBuffer();
		result.append("\n").append(title+"\n");
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				result.append(String.format("%8.1f", matrix[row][column]));
			}
			result.append(" | ");
			result.append(String.format("%8.1f", vector[row]));
			result.append(" \n");
		}
		System.out.println(result.toString());
	}
			
}