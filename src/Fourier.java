

import java.io.IOException;
import java.util.*;
import org.jtransforms.fft.*;

public class Fourier {
	public int sizeOfAnswer = 7;
	public double[] finalAnswer = new double[sizeOfAnswer];
	public double dangle = Math.PI / 4;
	public int amountOfPositions = (int) Math.pow(2 * Math.PI / dangle, 3);
	public double[][] answer = new double[amountOfPositions][sizeOfAnswer];
	public double largeNegativeValue = -1e6;
	public double smallPositiveValue = 1e-3;

	public double[][][] gridToArray(Grid grid, Params params, double value) {
		int n = params.N;
		double[][][] result = new double[2 * n][2 * n][2 * n];
		int len = grid.cells.size();
		for (int i = 0; i < len; i++) {
			Cell c = grid.cells.get(i);
			result[n / 2 + c.i][n / 2 + c.j][n / 2 + c.k] = value;
		}
		return result;
	}

	public void copyCoordinatesOfParser(Parser p, Parser p1) {
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			p.atoms.get(i).x = p1.atoms.get(i).x;
			p.atoms.get(i).y = p1.atoms.get(i).y;
			p.atoms.get(i).z = p1.atoms.get(i).z;
		}
	}

	
	public void copyingAndFindingSurface(double[][][] grid, double[][][] gridFT, double value) {
		int n = grid.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					if (grid[i][j][k] == value){
						if (grid[i + 1][j][k] == 0 || grid[i][j + 1][k] == 0 || grid[i][j][k + 1] == 0 ||
						    grid[i - 1][j][k] == 0 || grid[i][j - 1][k] == 0 || grid[i][j][k - 1] == 0) {
							grid[i][j][k] = 1;
						}
						gridFT[i][j][k] = grid[i][j][k];
					}
				}
			}
		}
	}

	public void multiplying(double[][][] newGridC, double[][][] grid1FT, double[][][] grid2FT, int n) {
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

	public void makingInversable(double grid[][][], int n) {
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

	public double[] findingPeak(double[][][] grid, int n) {
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

	public double[] findFinalAnswer(double[][] answer) {
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
	
	public Fourier(Parser sParser, Parser mParser, Params params, Visual visual) throws IOException {
		int n = params.N;
		DoubleFFT_3D fftDo = new DoubleFFT_3D(2 * n, 2 * n, 2 * n);
		// generation of grid and gridFT for the bigger molecule
		Grid sGrid = new Grid(sParser, params);
		double[][][] staticMoleculeGrid = gridToArray(sGrid, params, smallPositiveValue);
		double[][][] staticMoleculeGridFT = new double[2 * n][2 * n][4 * n];
		copyingAndFindingSurface(staticMoleculeGrid, staticMoleculeGridFT, smallPositiveValue);
		fftDo.realForwardFull(staticMoleculeGridFT);
		// beginning of rotations
		for (int i = 0; i < Math.PI / dangle; i++) {
			for (int j = 0; j < 2 * Math.PI / dangle; j++) {
				for (int k = 0; k < 2 * Math.PI / dangle; k++) {
					Parser parser = mParser.clone();
					// rotation of molecule
					double phix = dangle * i;
					double phiy = dangle * j;
					double phiz = dangle * k;
					Utils.rotation(parser.atoms, phix, phiy, phiz);
					//parser.dropOnAxisses();
					// generation of grid and gridFT for the smaller molecule
					Grid g1 = new Grid(parser, params);
					double[][][] grid1 = gridToArray(g1, params, largeNegativeValue);
					double[][][] grid1FT = new double[2 * n][2 * n][4 * n];
					double[][][] newGridC = new double[2 * n][2 * n][4 * n];
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
		Parser parser = mParser.clone();
		finalAnswer = findFinalAnswer(answer);
		System.out.println(Arrays.toString(finalAnswer));
		Utils.rotation(parser.atoms, finalAnswer[4], finalAnswer[5], finalAnswer[6]);
		Grid g = new Grid(parser, params);
		visual.drawGrid(sGrid, new Cell(0, 0, 0));
		visual.drawGrid(g, new Cell((int) finalAnswer[1], (int) finalAnswer[2], (int) finalAnswer[3]));
		System.out.println("Next Part");
	}
}