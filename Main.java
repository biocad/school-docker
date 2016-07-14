package Final;

import java.io.IOException;
import java.util.*;
import org.jtransforms.fft.*;
import ours.*;

public class Main {
	public static int sizeOfAnswer = 7;
	public static double[] finalAnswer = new double[sizeOfAnswer];
	public static double dangle = Math.PI/4;
	public static int amountOfPositions = (int)Math.pow(2*Math.PI/dangle, 3);
	public static double[][] answer = new double[amountOfPositions][sizeOfAnswer] ;
	public static Parser parser1;
	public static Parser parser2;
	public static int n;
	public static Grid staticMolecule;
	public static double[][][] staticMoleculeGrid;
	public static double[][][] staticMoleculeGridFT;
	public static double scale;
	public static double distance = 1.5;
	public static double largeNegativeValue = -100000000;
	public static double smallPositiveValue = 0.0001;
	static Scanner in = new Scanner(System.in);
	
	public static void copyCoordinatesOfParser(Parser p, Parser p1){
		int len = p.atoms.size();
		for (int i = 0; i < len; i++){
			p.atoms.get(i).x = p1.atoms.get(i).x; 
			p.atoms.get(i).y = p1.atoms.get(i).y; 
			p.atoms.get(i).z = p1.atoms.get(i).z; 
		}
	}
	
	public static void rotation(ArrayList<Atom> atoms, double phix, double phiy, double phiz){
		int len = atoms.size();
		for (int i = 0; i < len; i++){
			atoms.get(i).y = (Math.cos(phix)*atoms.get(i).y - Math.sin(phix)*atoms.get(i).z); 
			atoms.get(i).z = (Math.sin(phix)*atoms.get(i).y + Math.cos(phix)*atoms.get(i).z); 
			
			atoms.get(i).x = (Math.cos(phiy)*atoms.get(i).x + Math.sin(phiy)*atoms.get(i).z); 
			atoms.get(i).z = (-Math.sin(phiy)*atoms.get(i).x + Math.cos(phiy)*atoms.get(i).z); 
			
			atoms.get(i).x = (Math.cos(phiz)*atoms.get(i).x - Math.sin(phiz)*atoms.get(i).y); 
			atoms.get(i).y = (Math.sin(phiz)*atoms.get(i).x + Math.cos(phiz)*atoms.get(i).y); 
		}
	}
	
	public static void makeRightPossition(Parser p){
		double minx = 0;
		double miny = 0;
		double minz = 0;
		int len = p.atoms.size();
		for (int i = 0; i < len; i++){
			minx = Math.min(minx, p.atoms.get(i).x);
			miny = Math.min(miny, p.atoms.get(i).y);
			minz = Math.min(minz, p.atoms.get(i).z);
		}
		for (int i = 0; i < len; i++){
			p.atoms.get(i).x -= minx; 
			p.atoms.get(i).y -= miny; 
			p.atoms.get(i).z -= minz; 
		}
	}
	
	public static void copyingAndFindingSurface (double[][][] grid1, double[][][] grid1FT, int n, double value){
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				for (int k = 0; k < n; k++){
					if (i*j*k != 0 && i != n-1 && j != n-1 && k != n-1){
						if (grid1[i][j][k] == 1 && grid1[i+1][j][k] == 1 && grid1[i][j+1][k] == 1 && grid1[i][j][k+1] == 1 && grid1[i-1][j][k] == 1 && grid1[i][j-1][k] == 1 && grid1[i][j][k-1] == 1){
							grid1[i][j][k] = value;
						}
					}
					grid1FT[i][j][k] = grid1[i][j][k];
				}
			}
		}
	}
	
	public static void multiplying(double[][][] newGridC ,double[][][] grid1FT, double[][][] grid2FT, int n){
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				for (int k = 0; k < n; k++){
						grid1FT[i][j][2*k + 1] *= (-1);
						newGridC[i][j][2*k] = (grid1FT[i][j][2*k]*grid2FT[i][j][2*k] - grid1FT[i][j][2*k + 1]*grid2FT[i][j][2*k + 1]);
						newGridC[i][j][2*k+1] = (grid1FT[i][j][2*k+1]*grid2FT[i][j][2*k] + grid1FT[i][j][2*k]*grid2FT[i][j][2*k + 1]);
				}
			}
		}
	}
	
	public static void makingInversable(double grid[][][], int n){
		for (int i = 0;  i < n ; i++){
        	for (int j = 0 ; j < n ; j++){
          		if (i == 0 || i == n/2){
          			if (j  == 0 || j == n/2){
          				grid[i][j][1] = grid[i][j][n];
          			}
          			if (j>=n/2+1){
          				grid[i][j][0] = grid[i][j][n+1];
          				grid[i][j][1] = grid[i][j][n];
          			}
          		}
          		if (i >= 1 &&  i <= n/2 - 1){
          			if (j>=n/2+1){
          				grid[i][j][0] = grid[i][j][n+1];
          				grid[i][j][1] = grid[i][j][n];
          			}
          		}
          		if (i >= n/2+1){
          			if (j>=n/2+1 || j == 0 || j == n/2){
          				grid[i][j][0] = grid[i][j][n+1];
          				grid[i][j][1] = grid[i][j][n];
          			}
              	}
              	for (int k = n ; k < 2*n ; k++){
              		grid[i][j][k] = 0;
              	}
        	}
        }
	}
	
	public static double[] findingPeak(double[][][] grid, int n){
		double[] answer = new double[sizeOfAnswer];
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				for (int k = 0; k < n; k++){
					if (answer[0] < grid[i][j][k]){
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
	
	public static double[] findFinalAnswer(double[][] answer){
		double[] finalAnswer = new double[sizeOfAnswer];
		for (int i = 0; i < amountOfPositions ; i++){
			if (finalAnswer[0] < answer[i][0]){
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
		//getting information
		System.out.print("Size of the grid (power of 2): ");
		n = in.nextInt();
		DoubleFFT_3D fftDo = new DoubleFFT_3D(n, n, n);
		System.out.print("First molecule name: ");
		String name1 = in.next();
		System.out.print("Second molecule name: ");
		String name2 = in.next();
		//making parsers
		parser1 = new Parser(name1);
		parser2 = new Parser(name2);
		if (parser1.atoms.size() > parser2.atoms.size()){
			Parser temp = parser1;
			parser1 = parser2;
			parser2 = temp;
		}
		//finding scale
		double size1 = Math.max( parser1.maxx, Math.max( parser1.maxy,  parser1.maxz)) + 2 * distance;
		double size2 = Math.max( parser2.maxx, Math.max( parser2.maxy,  parser2.maxz)) + 2 * distance;
		scale = Math.max(size1, size2)/(n);
		//generation of grid and gridFT for the bigger molecule
		staticMolecule = new Grid(parser2, n, distance, scale);
		staticMoleculeGrid = staticMolecule.grid;
		staticMoleculeGridFT = new double[n][n][2*n];
		copyingAndFindingSurface(staticMoleculeGrid,  staticMoleculeGridFT, n, smallPositiveValue);
		fftDo.realForwardFull(staticMoleculeGridFT);
		//beginning of rotations 
		Parser parser = new Parser(name1);;
		for (int i = 0; i < 2*Math.PI/dangle; i++){
			for (int j = 0; j < 2*Math.PI/dangle; j++){
				for (int k = 0; k < 2*Math.PI/dangle; k++){
					copyCoordinatesOfParser(parser, parser1);
					//rotation of molecule
					double phix = dangle*i;
					double phiy = dangle*j;
					double phiz = dangle*k;
					rotation(parser.atoms, phix, phiy, phiz);
					makeRightPossition(parser);
					//generation of grid and gridFT for the smaller molecule
					Grid g1 = new Grid(parser, n, distance, scale);
					double[][][ ]grid1 = g1.grid;
					double[][][ ]grid1FT = new double[n][n][2*n];
					double[][][ ]newGridC = new double[n][n][2*n];
					copyingAndFindingSurface(grid1, grid1FT, n, largeNegativeValue);
					fftDo.realForwardFull(grid1FT);
					multiplying(newGridC, grid1FT, staticMoleculeGridFT, n);
					makingInversable(newGridC, n);
					fftDo.realInverse(newGridC, true);
					int numberOfAnswer = (int)(i*(2*Math.PI/dangle)*(2*Math.PI/dangle)+j*(2*Math.PI/dangle)+k);
					answer[numberOfAnswer] = findingPeak(newGridC, n);
					answer[numberOfAnswer][4] = phix;
					answer[numberOfAnswer][5] = phiy;
					answer[numberOfAnswer][6] = phiz;
					System.out.println(Arrays.toString(answer[numberOfAnswer]));
				}
			}
		}
		finalAnswer = findFinalAnswer(answer);
		System.out.println(Arrays.toString(finalAnswer));
	}
}
