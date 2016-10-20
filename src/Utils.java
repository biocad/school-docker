
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {
	public static Cell[] neighbours = new Cell[]{
			new Cell(-1, 0, 0),
			new Cell(1, 0, 0),
			new Cell(0, 1, 0),
			new Cell(0, -1, 0),
			new Cell(0, 0, 1),
			new Cell(0, 0, -1)
	}; 
	
	public static boolean inRange(int i, int j, int k, int w, int h, int d) {
		return i >= 0 && i < w && j >= 0 && j < h && k >= 0 && k < d;
	}

	public static double max(double... a) {
		double ans = Double.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			ans = Math.max(ans, a[i]);
		}
		return ans;
	}

	public static double min(double... a) {
		double ans = Double.MAX_VALUE;
		for (int i = 0; i < a.length; i++) {
			ans = Math.min(ans, a[i]);
		}
		return ans;
	}

	public static String requestStr(Scanner sc, String sign) {
		System.out.print(sign + ": ");
		return sc.next();
	}

	public static int requestInt(Scanner sc, String sign) {
		System.out.print(sign + ": ");
		return sc.nextInt();
	}

	public static void rotation(ArrayList<Atom> atoms, double phix, double phiy, double phiz) {
		int len = atoms.size();
		for (int i = 0; i < len; i++) {
			double x = atoms.get(i).x;
			double y = atoms.get(i).y;
			double z = atoms.get(i).z;
			 atoms.get(i).y = (Math.cos(phix) * y -
			 Math.sin(phix) * z);
			 atoms.get(i).z = (Math.sin(phix) * y +
			 Math.cos(phix) * z);

			 x = atoms.get(i).x;
			 y = atoms.get(i).y;
			 z = atoms.get(i).z;
			
			 atoms.get(i).x = (Math.cos(phiy) * x +
			 Math.sin(phiy) * z);
			 atoms.get(i).z = (-Math.sin(phiy) * x +
			 Math.cos(phiy) * z);
			 
			 x = atoms.get(i).x;
			 y = atoms.get(i).y;
			 z = atoms.get(i).z;
			
			 atoms.get(i).x = (Math.cos(phiz) * x -
			 Math.sin(phiz) * y);
			 atoms.get(i).y = (Math.sin(phiz) * x +
			 Math.cos(phiz) * y);
		}
	}
	


	public static void placeMolecule(double[] answer, Parser p, double scale, int n) {
		rotation(p.atoms, answer[4], answer[5], answer[6]);
		int len = p.atoms.size();
		for (int i = 1; i < 4; i++){
			if (answer[i] < n/2){
				answer[i] *= -1;
			}else{
				answer[i] = n - answer[i];
			}
		}
		for (int i = 0; i < len; i++) {
			p.atoms.get(i).x -= (answer[1]) * scale;
			p.atoms.get(i).y -= (answer[2]) * scale;
			p.atoms.get(i).z -= (answer[3]) * scale;
		}
	}
}