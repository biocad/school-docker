import java.util.*;
import org.jtransforms.fft.*;

public class Solver {
	private double dangle = Math.PI / 3;
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
					ft[i][j][2 * k] = grid1FT[i][j][2 * k]     * grid2FT[i][j][2 * k] -
					                  grid1FT[i][j][2 * k + 1] * grid2FT[i][j][2 * k + 1];
					
					ft[i][j][2 * k + 1] = grid1FT[i][j][2 * k + 1] * grid2FT[i][j][2 * k] +
					                      grid1FT[i][j][2 * k]     * grid2FT[i][j][2 * k + 1];
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
	
	boolean flag = true;

	private Answer[] findPeaks(double[][][] grid) {
		int N = grid.length, n = N / 2;
		////////////////////////////////////////////////////////////////////////////////////////////////
		if (flag) {
			flag = false;
			Locale.setDefault(new Locale("ru", "RU"));
			double[][] copy = new double[N][N];
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					int ri = i < n ? i + n : i - n;
					int rj = j < n ? j + n : j - n;
					copy[ri][rj] = Math.max(grid[i][j][2], 0);
				}
			}
			try {
				java.io.PrintWriter out = new java.io.PrintWriter(new java.io.File("out.txt"));
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						out.print(copy[i][j] + "\t");
					}
					out.println();
				}
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////
		int len = 3;
		Answer[] localAnswers = new Answer[len];
		for (int i = 0; i < len; i++) {
			localAnswers[i] = new Answer();
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				for (int k = 0; k < N; k++) {
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

	public Solver(Molecule sMolecule, Molecule mMolecule, Params params) {
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

	public Answer apply() throws LockedMoleculeException { // apply()
		int n = params.n;
		fftDo = new DoubleFFT_3D(2 * n, 2 * n, 2 * n);
		//dangle = params.scale / params.d;
		System.out.println(dangle / Math.PI * 180);
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
		Clock clock = new Clock();
		for (double ax = 0; ax < 2 * Math.PI; ax += dangle) {
			for (double ay = 0; ay < 2 * Math.PI; ay += dangle) {
				for (double az = 0; az < Math.PI; az += dangle) {
					clock.stamp();
					Molecule parser = mMolecule.clone();
					parser.rotate(ax, ay, az);
					clock.stamp("rotation");
					
					// generation of grid and gridFT for the smaller molecule
					Grid mGrid = new Grid(parser, params);
					clock.stamp("making grid");
					double[][][] mArr = replaceInnerValues(mGrid, largeNegativeValue);
					clock.stamp("replacing values");
					double[][][] mFT = arrToFT(mArr);
					clock.stamp("fft");

					double[][][] ft = new double[2 * n][2 * n][4 * n];
					multiply(ft, mFT, sFT);
					clock.stamp("multiplying");
					
					makeInversable(ft);
					clock.stamp("making");
					
					fftDo.realInverse(ft, true);
					clock.stamp("inverse");
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
					clock.stamp("finfing peaks");
				}
			}
		}
		System.out.println(clock.results());
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
		for (int cur = 0; cur < len; cur++) {
			double e = 0;
			Answer answer = answers.get(cur);
			Molecule m = mMolecule.clone();
			m.rotate(answer.ax, answer.ay, answer.az);
			int sl = sMolecule.iSize(), ml = mMolecule.iSize();
			double ax = answer.i * params.scale;
			double ay = answer.j * params.scale;
			double az = answer.k * params.scale;
			for (int i = 0; i < sl; i++) {
				Atom sa = sMolecule.iGet(i);
				for (int j = 0; j < ml; j++) {
					Atom ma = mMolecule.iGet(j);
					double dx = sa.x - ma.x - ax;
					double dy = sa.y - ma.y - ay;
					double dz = sa.z - ma.z - az;
					double r = Math.sqrt(dx * dx + dy * dy + dz * dz);
					e += sa.q * ma.q / r;
				}
			}
			if (e < 0) {
				a = cur;
				break;
			}
		}
		Answer finalAnswer = answers.get(a);
		return finalAnswer;
	}
}