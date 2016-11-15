

import java.io.IOException;
import java.util.*;
import org.jtransforms.fft.*;

public class Fourier {
	public int sizeOfAnswer = 7;
	public double dangle = Math.PI/2;
	public int amountOfPositions = (int) Math.pow(2 * Math.PI / dangle, 3);
	private double largeNegativeValue = -1e6;
	private double smallPositiveValue = 1e-3;
	public Parser sParser;
	public Parser mParser;
	public Params params;
	public Visual visual;
	public double scale;
	private double progress = 0;
	
	public double getProgress() {
		return progress;
	}

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

	private void multiply(double[][][] newGridC, double[][][] grid1FT, double[][][] grid2FT, int n) {
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

	private void makeInversable(double grid[][][], int n) {
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

	private double[] findPeak(double[][][] grid, int n) {
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
	
	public Fourier(Parser sParser, Parser mParser, Params params, Visual visual) {
		this.sParser = sParser;
		this.mParser = mParser;
		this.params = params;
		this.visual = visual;
		this.scale = params.SCALE;
	}
	
//	public void Approach(double sAx, double sAy, double sAz, Parser mParser, int n, DoubleFFT_3D fftDo, double[][][] staticMoleculeGridFT){
//		int done = 0;
//		for (int i = 0; i < angleSteps; i++) {
//			for (int j = 0; j < angleSteps; j++) {
//				for (int k = 0; k < angleSteps; k++) {
//					Parser parser = mParser.clone();
//					// rotation of molecule
//					double phix = sAx + angle/angleSteps * i;
//					double phiy = sAy + angle/angleSteps * j;
//					double phiz = sAz + angle/angleSteps * k;
//					Utils.rotate(parser.atoms, phix, phiy, phiz, parser.getSize());
//					// generation of grid and gridFT for the smaller molecule
//					Grid mGrid = new Grid(parser, params);
//					double[][][] mgrid = replaceInnerValues(mGrid, largeNegativeValue);
//					double[][][] mgridFT = new double[2 * n][2 * n][4 * n];
//					double[][][] newGridC = new double[2 * n][2 * n][4 * n];
//					copy(mgrid, mgridFT);
//					fftDo.realForwardFull(mgridFT);
//					multiply(newGridC, mgridFT, staticMoleculeGridFT, 2*n);
//					makeInversable(newGridC, 2*n);
//					fftDo.realInverse(newGridC, true);
//					int numberOfAnswer = (int) (i * (angleSteps) * (angleSteps)
//							+ j * (angleSteps) + k);
//					answer[numberOfAnswer] = findPeak(newGridC, 2*n);
//					answer[numberOfAnswer][4] = phix;
//					answer[numberOfAnswer][5] = phiy;
//					answer[numberOfAnswer][6] = phiz;
//					System.out.println(Arrays.toString(answer[numberOfAnswer]));
//					done++;
//					System.out.println(done + "/" + amountOfPositions);
//				}
//			}
//		}
//	}
	
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
					Utils.rotate(parser.atoms, phix, phiy, phiz, parser.getSize());
					//parser.dropOnAxisses();
					// generation of grid and gridFT for the smaller molecule
					Grid g1 = new Grid(parser, params);
					double[][][] grid1 = replaceInnerValues(g1, largeNegativeValue);
					double[][][] grid1FT = new double[2 * n][2 * n][4 * n];
					double[][][] newGridC = new double[2 * n][2 * n][4 * n];
					copy(grid1, grid1FT);
					fftDo.realForwardFull(grid1FT);
					multiply(newGridC, grid1FT, staticMoleculeGridFT, 2 * n);
					makeInversable(newGridC, 2 * n);
					fftDo.realInverse(newGridC, true);
					int numberOfAnswer = (int) (i * (2 * Math.PI / dangle) * (2 * Math.PI / dangle)
							+ j * (2 * Math.PI / dangle) + k);
					answer[numberOfAnswer] = findPeak(newGridC, n);
					answer[numberOfAnswer][4] = phix;
					answer[numberOfAnswer][5] = phiy;
					answer[numberOfAnswer][6] = phiz;
					done++;
					progress = 2. * done / amountOfPositions;
				}
			}
		}
		Parser parser = mParser.clone();
		double[] finalAnswer = findFinalAnswer(answer);
		System.out.println(Arrays.toString(finalAnswer));
		Utils.rotate(parser.atoms, finalAnswer[4], finalAnswer[5], finalAnswer[6], parser.getSize());
		Grid g = new Grid(parser, params);
		//visual.drawGrid(sGrid, new Cell(-n, -n, -n));
		//visual.drawGrid(g, new Cell((int) (finalAnswer[1]-n), (int) (finalAnswer[2]-n), (int) (finalAnswer[3])-n));
		Utils.placeMolecule(finalAnswer, parser, scale, n);
		visual.drawMolecule(parser);
	}
}