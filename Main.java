package Final;

import com.sun.j3d.utils.applet.MainFrame;
import java.io.IOException;
import java.util.Scanner;
import ours.*;

public class Main {
	static Scanner sc = new Scanner(System.in);
	static Visual visual;
	final static double R = 1.5;
	
	public static void main(String[] args) throws IOException {
		visual = new Visual(600);
		new MainFrame(visual, 600, 600);
		
		// p1 is movable, p2 is static
		Parser p1 = new Parser(Utils.requestStr(sc, "First molecule"));
		int len1 = p1.atoms.size();
		System.out.println("Parsed " + len1 + " atoms");
		
		Parser p2 = new Parser(Utils.requestStr(sc, "Second molecule"));
		int len2 = p2.atoms.size();
		System.out.println("Parsed " + len2 + " atoms");

		int n = 64;//Utils.requestInt(sc, "Grid edge size");
		
		// swap
		if (len1 > len2) {
			Parser t = p1;
			p1 = p2;
			p2 = t;
		}
		visual.drawMolecule(p2);
		
		Fourier.parser1 = p1;
		Fourier.parser2 = p2;
		Fourier.n = n;
		Fourier.main(new String[]{});
		Utils.placeMolecule(Fourier.finalAnswer, p1, Fourier.scale, n);
		visual.drawMolecule(p1);
		//visual.drawGrid(Fourier.staticMoleculeGrid, Fourier.smallPositiveValue);
	}
}