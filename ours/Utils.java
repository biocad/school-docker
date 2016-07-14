package ours;
import java.util.ArrayList;
import java.util.Scanner;
import ours.*;

public class Utils {
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
			atoms.get(i).y = (float) (Math.cos(phix) * atoms.get(i).y - Math.sin(phix) * atoms.get(i).z);
			atoms.get(i).z = (float) (Math.sin(phix) * atoms.get(i).y + Math.cos(phix) * atoms.get(i).z);

			atoms.get(i).x = (float) (Math.cos(phiy) * atoms.get(i).x + Math.sin(phiy) * atoms.get(i).z);
			atoms.get(i).z = (float) (-Math.sin(phiy) * atoms.get(i).x + Math.cos(phiy) * atoms.get(i).z);

			atoms.get(i).x = (float) (Math.cos(phiz) * atoms.get(i).x - Math.sin(phiz) * atoms.get(i).y);
			atoms.get(i).y = (float) (Math.sin(phiz) * atoms.get(i).x + Math.cos(phiz) * atoms.get(i).y);
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

	public static void placeMolecule(double[] answer, Parser p, double scale) {
		int n = 32;
		rotation(p.atoms, answer[4], answer[5], answer[6]);
		makeRightPossition(p);
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			p.atoms.get(i).x += answer[1] * scale - n/4*scale;
			p.atoms.get(i).y += answer[2] * scale - n/4*scale;
			p.atoms.get(i).z += answer[3] * scale - n/4*scale;
		}
	}
}
