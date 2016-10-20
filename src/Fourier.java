

import java.io.IOException;
import java.util.*;
import org.jtransforms.fft.*;

public class Fourier {
	public int sizeOfAnswer = 7;
	public double dangle = Math.PI;
	public int amountOfPositions = (int) Math.pow(2 * Math.PI / dangle, 3);
	private double largeNegativeValue = -1e6;
	private double smallPositiveValue = 1e-3;
	public Parser sParser;
	public Parser mParser;
	public Params params;
	public Visual visual;

	private double[][][] replaceInnerValues(Grid g, double value) {
		int[][][] grid = g.toArray();
		int n = grid.length;
		double[][][] r = new double[n][n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					double v = grid[i][j][k];
					if (v == Grid.inner) {
						v = value;
					}
					r[i][j][k] = v;
				}
			}
		}
		return r;
	}
	
	private void copy(double[][][] grid, double[][][] gridFT) {
		int n = grid.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					gridFT[i][j][k] = grid[i][j][k];
				}
			}
		}
	}

	private void multiplying(double[][][] newGridC, double[][][] grid1FT, double[][][] grid2FT, int n) {
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

	private void makingInversable(double grid[][][], int n) {
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

	private double[] findingPeak(double[][][] grid, int n) {
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

	private double[] findFinalAnswer(double[][] answer) {
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
		this.sParser = sParser;
		this.mParser = mParser;
		this.params = params;
		this.visual = visual;
	}
	
	public void apply() {
		double[][] answer = new double[amountOfPositions][sizeOfAnswer];
		int n = params.N;
		DoubleFFT_3D fftDo = new DoubleFFT_3D(2 * n, 2 * n, 2 * n);
		// generation of grid and gridFT for the bigger molecule
		Grid sGrid = new Grid(sParser, params);
		double[][][] staticMoleculeGrid = replaceInnerValues(sGrid, smallPositiveValue);
		double[][][] staticMoleculeGridFT = new double[2 * n][2 * n][4 * n];
		copy(staticMoleculeGrid, staticMoleculeGridFT);
		fftDo.realForwardFull(staticMoleculeGridFT);
		// beginning of rotations
		int done = 0;
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
					double[][][] grid1 = replaceInnerValues(g1, largeNegativeValue);
					double[][][] grid1FT = new double[2 * n][2 * n][4 * n];
					double[][][] newGridC = new double[2 * n][2 * n][4 * n];
					copy(grid1, grid1FT);
					fftDo.realForwardFull(grid1FT);
					multiplying(newGridC, grid1FT, staticMoleculeGridFT, 2 * n);
					makingInversable(newGridC, 2 * n);
					fftDo.realInverse(newGridC, true);
					int numberOfAnswer = (int) (i * (2 * Math.PI / dangle) * (2 * Math.PI / dangle)
							+ j * (2 * Math.PI / dangle) + k);
					answer[numberOfAnswer] = findingPeak(newGridC, n);
					answer[numberOfAnswer][4] = phix;
					answer[numberOfAnswer][5] = phiy;
					answer[numberOfAnswer][6] = phiz;
					done++;
					System.out.println(done + "/" + amountOfPositions);
					//break;
				}
				//break;
			}
			//break;
		}
		Parser parser = mParser.clone();
		double[] finalAnswer = findFinalAnswer(answer);
//		for (int i = 1; i < 4; i++){
//			if (finalAnswer[i] < n/2){
//				finalAnswer[i] *= -1;
//			}else{
//				finalAnswer[i] = n - finalAnswer[i];
//			}
//		}
		System.out.println(Arrays.toString(finalAnswer));
		Utils.rotation(parser.atoms, finalAnswer[4], finalAnswer[5], finalAnswer[6]);
		Grid g = new Grid(parser, params);
		visual.drawGrid(sGrid, new Cell(0, 0, 0));
		visual.drawGrid(g, new Cell((int) finalAnswer[1], (int) finalAnswer[2], (int) finalAnswer[3]));
	}
}