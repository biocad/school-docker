package ours;

import java.io.IOException;
import java.util.*;
import org.jtransforms.fft.*;

public class Fourier {
	public static int sizeOfAnswer = 7;
	public static double[] finalAnswer = new double[sizeOfAnswer];
	public static double dangle = Math.PI / 4;
	public static int amountOfPositions = (int) Math.pow(2 * Math.PI / dangle, 3);
	public static double[][] answer = new double[amountOfPositions][sizeOfAnswer];
	public static Parser parser1;
	public static Parser parser2;
	public static int n;
	public static Grid staticMolecule;
	public static double[][][] staticMoleculeGrid;
	public static double[][][] staticMoleculeGridFT;
	public static double scale;
	public static double distance = 5;
	public static double largeNegativeValue = -100000000;
	public static double smallPositiveValue = 0.0001;
	static Scanner in = new Scanner(System.in);

	public static double[][][] gridToArray(Grid grid, double value) {
		double[][][] result = new double[n][n][n];
		int len = grid.cells.size();
		for (int i = 0; i < len; i++) {
			Cell c = grid.cells.get(i);
			result[n / 4 + c.i][n / 4 + c.j][n / 4 + c.k] = value;
		}
		return result;
	}

	public static void copyCoordinatesOfParser(Parser p, Parser p1) {
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			p.atoms.get(i).x = p1.atoms.get(i).x;
			p.atoms.get(i).y = p1.atoms.get(i).y;
			p.atoms.get(i).z = p1.atoms.get(i).z;
		}
	}

	
	public static void copyingAndFindingSurface(double[][][] grid, double[][][] gridFT, double value) {
		int n = grid.length;
		int a = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					if (grid[i][j][k] == value){
						if (grid[i + 1][j][k] == 0 || grid[i][j + 1][k] == 0 || grid[i][j][k + 1] == 0 ||
						    grid[i - 1][j][k] == 0 || grid[i][j - 1][k] == 0 || grid[i][j][k - 1] == 0) {
							grid[i][j][k] = 1;
							a++;
						}
						gridFT[i][j][k] = grid[i][j][k];
					}
				}
			}
		}
	}

	public static void multiplying(double[][][] newGridC, double[][][] grid1FT, double[][][] grid2FT, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					grid1FT[i][j][2 * k + 1] *= (-1);
					newGridC[i][j][2 * k] = (grid1FT[i][j][2 * k] * grid2FT[i][j][2 * k]
							- grid1FT[i][j][2 * k + 1] * grid2FT[i][j][2 * k + 1]);
					newGridC[i][j][2 * k + 1] = (grid1FT[i][j][2 * k + 1] * grid2FT[i][j][2 * k]
							+ grid1FT[i][j][2 * k] * grid2FT[i][j][2 * k + 1]);
				}
			}
		}
	}

	public static void makingInversable(double grid[][][], int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == 0 || i == n / 2) {
					if (j == 0 || j == n / 2) {
						grid[i][j][1] = grid[i][j][n];
					}
					if (j >= n / 2 + 1) {
						grid[i][j][0] = grid[i][j][n + 1];
						grid[i][j][1] = grid[i][j][n];
					}
				}
				if (i >= 1 && i <= n / 2 - 1) {
					if (j >= n / 2 + 1) {
						grid[i][j][0] = grid[i][j][n + 1];
						grid[i][j][1] = grid[i][j][n];
					}
				}
				if (i >= n / 2 + 1) {
					if (j >= n / 2 + 1 || j == 0 || j == n / 2) {
						grid[i][j][0] = grid[i][j][n + 1];
						grid[i][j][1] = grid[i][j][n];
					}
				}
				for (int k = n; k < 2 * n; k++) {
					grid[i][j][k] = 0;
				}
			}
		}
	}

	public static double[] findingPeak(double[][][] grid, int n) {
		double[] answer = new double[sizeOfAnswer];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					if (answer[0] < grid[i][j][k]) {
						answer[0] = grid[i][j][k];
						answer[1] = i;
						answer[2] = j;
						answer[3] = k;
					}
				}
			}
		}
		return answer;
	}

	public static double[] findFinalAnswer(double[][] answer) {
		double[] finalAnswer = new double[sizeOfAnswer];
		for (int i = 0; i < amountOfPositions; i++) {
			if (finalAnswer[0] < answer[i][0]) {
				finalAnswer[0] = answer[i][0];
				finalAnswer[1] = answer[i][1];
				finalAnswer[2] = answer[i][2];
				finalAnswer[3] = answer[i][3];
				finalAnswer[4] = answer[i][4];
				finalAnswer[5] = answer[i][5];
				finalAnswer[6] = answer[i][6];
			}
		}
		return finalAnswer;
	}
	
	public static void main(String[] s) throws IOException {
//		 n = in.nextInt();
//		 Parser parser1 = new Parser(in.next());
//		 Parser parser2 = new Parser(in.next());
		DoubleFFT_3D fftDo = new DoubleFFT_3D(n, n, n);
		// finding scale
		double size1 = parser1.size + 2 * distance;
		double size2 = parser2.size + 2 * distance;
		scale = Math.max(size1, size2) / (n / 2);
		// generation of grid and gridFT for the bigger molecule
		staticMolecule = new Grid(parser2, n / 2, distance, scale);
		staticMoleculeGrid = gridToArray(staticMolecule, smallPositiveValue);
		staticMoleculeGridFT = new double[n][n][2 * n];
		copyingAndFindingSurface(staticMoleculeGrid, staticMoleculeGridFT, smallPositiveValue);
		fftDo.realForwardFull(staticMoleculeGridFT);
		// beginning of rotations
		Parser parser = new Parser(parser1.name);
		for (int i = 0; i < Math.PI / dangle; i++) {
			for (int j = 0; j < 2 * Math.PI / dangle; j++) {
				for (int k = 0; k < 2 * Math.PI / dangle; k++) {
					copyCoordinatesOfParser(parser, parser1);
					// rotation of molecule
					double phix = dangle * i;
					double phiy = dangle * j;
					double phiz = dangle * k;
					Utils.rotation(parser.atoms, phix, phiy, phiz);
					parser.centration();
					//parser.dropOnAxisses();
					// generation of grid and gridFT for the smaller molecule
					Grid g1 = new Grid(parser, n / 2, distance, scale);
					double[][][] grid1 = gridToArray(g1, largeNegativeValue);
					double[][][] grid1FT = new double[n][n][2 * n];
					double[][][] newGridC = new double[n][n][2 * n];
					copyingAndFindingSurface(grid1, grid1FT, largeNegativeValue);
					fftDo.realForwardFull(grid1FT);
					multiplying(newGridC, grid1FT, staticMoleculeGridFT, n);
					makingInversable(newGridC, n);
					fftDo.realInverse(newGridC, true);
					int numberOfAnswer = (int) (i * (2 * Math.PI / dangle) * (2 * Math.PI / dangle)
							+ j * (2 * Math.PI / dangle) + k);
					answer[numberOfAnswer] = findingPeak(newGridC, n);
					answer[numberOfAnswer][4] = phix;
					answer[numberOfAnswer][5] = phiy;
					answer[numberOfAnswer][6] = phiz;
					System.out.println(Arrays.toString(answer[numberOfAnswer]));
					//break;
				}
				//break;
			}
			//break;
		}
		finalAnswer = findFinalAnswer(answer);
		 System.out.println(Arrays.toString(finalAnswer));
		System.out.println("Next Part");
	}
}
