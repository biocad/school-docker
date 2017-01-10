import java.util.*;
import org.jtransforms.fft.*;

public class Fourier {
	private final double dangle = Math.PI / 3;
	private ArrayList<Answer> answers = new ArrayList<>();
	private DoubleFFT_3D fftDo;
	private double largeNegativeValue = -1e12;
	private double smallPositiveValue = 1e-6;
	private Molecule sMolecule, mMolecule;
	private Params params;
	private double progress = 0;

	public double getProgress() {
		return progress;
	}

	private double[][][] replaceInnerValues(Grid g, double value) {
		int n = g.arr.length;
		double[][][] r = new double[n][n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					double v = g.arr[i][j][k];
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
	
	private double[][][] arrToFT(double[][][] _Arr) {
		int n = params.n;
		double[][][] _FT = new double[2 * n][2 * n][4 * n];
		copy(_Arr, _FT);
		fftDo.realForwardFull(_FT);
		return _FT;
	}

	private void multiply(double[][][] ft, double[][][] grid1FT, double[][][] grid2FT) {
		int n = ft.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					grid1FT[i][j][2 * k + 1] *= -1;
					ft[i][j][2 * k] = grid1FT[i][j][2 * k] *     grid2FT[i][j][2 * k] -
					                        grid1FT[i][j][2 * k + 1] * grid2FT[i][j][2 * k + 1];
					
					ft[i][j][2 * k + 1] = grid1FT[i][j][2 * k + 1] * grid2FT[i][j][2 * k] +
					                            grid1FT[i][j][2 * k] *     grid2FT[i][j][2 * k + 1];
				}
			}
		}
	}

	private void makeInversable(double grid[][][]) {
		int n = grid.length;
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

	private Answer[] findPeaks(double[][][] grid) {
		int len = 3;
		Answer[] localAnswers = new Answer[len];
		for (int i = 0; i < len; i++) {
			localAnswers[i] = new Answer();
		}
		int n = grid.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					double v = grid[i][j][k];
					if (v > localAnswers[0].fitness) {
						Answer a = new Answer();
						a.i = i;
						a.j = j;
						a.k = k;
						a.fitness = v;
						localAnswers[0] = a;
						for (int cur = 0; cur < len - 1; cur++) {
							if (localAnswers[cur].fitness > localAnswers[cur + 1].fitness) {
								Answer t = localAnswers[cur];
								localAnswers[cur] = localAnswers[cur + 1];
								localAnswers[cur + 1] = t;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		return localAnswers;
	}

	public Fourier(Molecule sMolecule, Molecule mMolecule, Params params) {
		this.sMolecule = sMolecule;
		this.mMolecule = mMolecule;
		this.params = params;
	}

	// public void Approach(double sAx, double sAy, double sAz, Parser mParser,
	// int n, DoubleFFT_3D fftDo, double[][][] staticMoleculeGridFT){
	// int done = 0;
	// for (int i = 0; i < angleSteps; i++) {
	// for (int j = 0; j < angleSteps; j++) {
	// for (int k = 0; k < angleSteps; k++) {
	// Parser parser = mParser.clone();
	// // rotation of molecule
	// double phix = sAx + angle/angleSteps * i;
	// double phiy = sAy + angle/angleSteps * j;
	// double phiz = sAz + angle/angleSteps * k;
	// Utils.rotate(parser.atoms, phix, phiy, phiz, parser.getSize());
	// // generation of grid and gridFT for the smaller molecule
	// Grid mGrid = new Grid(parser, params);
	// double[][][] mgrid = replaceInnerValues(mGrid, largeNegativeValue);
	// double[][][] mgridFT = new double[2 * n][2 * n][4 * n];
	// double[][][] newGridC = new double[2 * n][2 * n][4 * n];
	// copy(mgrid, mgridFT);
	// fftDo.realForwardFull(mgridFT);
	// multiply(newGridC, mgridFT, staticMoleculeGridFT, 2*n);
	// makeInversable(newGridC, 2*n);
	// fftDo.realInverse(newGridC, true);
	// int numberOfAnswer = (int) (i * (angleSteps) * (angleSteps)
	// + j * (angleSteps) + k);
	// answer[numberOfAnswer] = findPeak(newGridC, 2*n);
	// answer[numberOfAnswer][4] = phix;
	// answer[numberOfAnswer][5] = phiy;
	// answer[numberOfAnswer][6] = phiz;
	// System.out.println(Arrays.toString(answer[numberOfAnswer]));
	// done++;
	// System.out.println(done + "/" + amountOfPositions);
	// }
	// }
	// }
	// }

	public Answer apply() throws LockedMoleculeException {
		int n = params.n;
		fftDo = new DoubleFFT_3D(2 * n, 2 * n, 2 * n);
		
		// generating grid and gridFT for the bigger molecule
		Grid sGrid = new Grid(sMolecule, params);
		double[][][] sArr = replaceInnerValues(sGrid, smallPositiveValue);
		double[][][] sFT = arrToFT(sArr);
		
		int done = 0;
		int total = 0;
		for (double ax = 0; ax < 2 * Math.PI; ax += dangle) {
			for (double ay = 0; ay < 2 * Math.PI; ay += dangle) {
				for (double az = 0; az < Math.PI; az += dangle) {
					total++;
				}
			}
		}
		
		for (double ax = 0; ax < 2 * Math.PI; ax += dangle) {
			for (double ay = 0; ay < 2 * Math.PI; ay += dangle) {
				for (double az = 0; az < Math.PI; az += dangle) {
					Molecule parser = mMolecule.clone();
					parser.rotate(ax, ay, az);
					
					// generation of grid and gridFT for the smaller molecule
					Grid mGrid = new Grid(parser, params);
					double[][][] mArr = replaceInnerValues(mGrid, largeNegativeValue);
					double[][][] mFT = arrToFT(mArr);

					double[][][] ft = new double[2 * n][2 * n][4 * n];
					multiply(ft, mFT, sFT);
					makeInversable(ft);
					fftDo.realInverse(ft, true);
					Answer[] localAnswers = findPeaks(ft);
					for (int i = 0; i < localAnswers.length; i++) {
						Answer answer = localAnswers[i];
						answer.ax = ax;
						answer.ay = ay;
						answer.az = az;
						answers.add(answer);
					}
					done++;
					progress = 1. * done / total;
				}
			}
		}
		progress = 1;
		answers.sort(new Comparator<Answer>() {
			@Override
			public int compare(Answer o1, Answer o2) {
				return -Double.compare(o1.fitness, o2.fitness);
			}
		});
		int len = answers.size();
		for (int i = 0; i < len; i++) {
			// !important
			Answer answer = answers.get(i);
			answer.i -= answer.i > n ? 2 * n : 0;
			answer.j -= answer.j > n ? 2 * n : 0;
			answer.k -= answer.k > n ? 2 * n : 0;
		}
		int a = 0;
		PhiGrid phiGrid = new PhiGrid(sMolecule, params);
		for (int cur = 0; cur < len; cur++) {
			Answer answer = answers.get(cur);
			System.out.println(answer.fitness);
			Molecule m = mMolecule.clone();
			m.rotate(answer.ax, answer.ay, answer.az);
			QGrid qGrid = new QGrid(m, params);
			double summ = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					for (int k = 0; k < n; k++) {
						int ci = answer.i + i;
						int cj = answer.j + j;
						int ck = answer.k + k;
						if (Utils.inRange(ci, cj, ck, n, n, n)) {
							summ += phiGrid.arr[ci][cj][ck] * qGrid.arr[i][j][k];
						}
					}
				}
			}
			System.out.println(summ);
			if (summ < 0) {
				a = cur;
				break;
			}
		}
		Answer finalAnswer = answers.get(a);
		return finalAnswer;
	}
}