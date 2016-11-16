import java.util.*;
import org.jtransforms.fft.*;

public class Fourier {
	private final double dangle = Math.PI / 2;
	private int amountOfPositions = (int) Math.pow(2 * Math.PI / dangle, 3) / 2;
	private ArrayList<Answer> answers = new ArrayList<>();
	private double largeNegativeValue = -1e6;
	private double smallPositiveValue = 1e-3;
	private Parser sParser, mParser;
	private Params params;
	private Visual visual;
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

	private Answer findPeak(double[][][] grid) {
		int n = grid.length;
		Answer answer = new Answer();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					if (answer.fitness < grid[i][j][k]) {
						answer.fitness = grid[i][j][k];
						answer.i = i;
						answer.j = j;
						answer.k = k;
					}
				}
			}
		}
		return answer;
	}

	private Answer findFinalAnswer() {
		Answer finalAnswer = new Answer();
		for (int i = 0; i < answers.size(); i++) {
			if (finalAnswer.fitness < answers.get(i).fitness) {
				finalAnswer = answers.get(i);
			}
		}
		double n = params.N;
		finalAnswer.i -= finalAnswer.i > n ? 2 * n : 0;
		finalAnswer.j -= finalAnswer.j > n ? 2 * n : 0;
		finalAnswer.k -= finalAnswer.k > n ? 2 * n : 0;
		return finalAnswer;
	}
	
	public Fourier(Parser sParser, Parser mParser, Params params, Visual visual) {
		this.sParser = sParser;
		this.mParser = mParser;
		this.params = params;
		this.visual = visual;
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
		int n = params.N;
		DoubleFFT_3D fftDo = new DoubleFFT_3D(2 * n, 2 * n, 2 * n);
		// generation of grid and gridFT for the bigger molecule
		Grid sGrid = new Grid(sParser, params);
		double[][][] sArr = replaceInnerValues(sGrid, smallPositiveValue);
		double[][][] sFT = new double[2 * n][2 * n][4 * n];
		copy(sArr, sFT);
		fftDo.realForwardFull(sFT);
		// beginning of rotations
		int done = 0;
		for (double ax = 0; ax < 2 * Math.PI; ax += dangle) {
			for (double ay = 0; ay < 2 * Math.PI; ay += dangle) {
				for (double az = 0; az < 2 * Math.PI; az += dangle) {
					Parser parser = mParser.clone();
					parser.rotate(ax, ay, az);
					// generation of grid and gridFT for the smaller molecule
					Grid mGrid = new Grid(parser, params);
					double[][][] mArr = replaceInnerValues(mGrid, largeNegativeValue);
					double[][][] mFT = new double[2 * n][2 * n][4 * n];
					double[][][] ft = new double[2 * n][2 * n][4 * n];
					copy(mArr, mFT);
					fftDo.realForwardFull(mFT);
					multiply(ft, mFT, sFT, 2 * n);
					makeInversable(ft, 2 * n);
					fftDo.realInverse(ft, true);
					Answer answer = findPeak(ft);
					answer.ax = ax;
					answer.ay = ay;
					answer.az = az;
					answers.add(answer);
					done++;
					progress = 2. * done / amountOfPositions;
				}
			}
		}
		Parser parser = mParser.clone();
		Answer finalAnswer = findFinalAnswer();
		for (int i = 0; i < answers.size(); i++) {
			System.out.println(answers.get(i));
		}
		//Utils.rotate(parser.atoms, finalAnswer[4], finalAnswer[5], finalAnswer[6], parser.getShift());
		parser.rotate(finalAnswer.ax, finalAnswer.ay, finalAnswer.az);
		Cell d = new Cell(0, 0, 0);
		visual.drawGrid(sGrid, d);
		Grid g = new Grid(parser, params);
		d.i = finalAnswer.i;
		d.j = finalAnswer.j;
		d.k = finalAnswer.k;
		visual.drawGrid(g, d);
		//Utils.placeMolecule(finalAnswer, mParser, scale, n);
		//visual.drawMolecule(mParser);
	}
}