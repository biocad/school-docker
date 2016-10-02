import com.sun.j3d.utils.applet.MainFrame;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		Visual visual = new Visual(600, 600);
		new MainFrame(visual, 600, 600);
		
		// "s" is for "static", "m" is for "movable";
		String sName = Utils.requestStr(sc, "First molecule");
		System.out.println("Parsing...");
		Parser sParser = new Parser(sName);
		int sLen = sParser.atoms.size();
		System.out.println("Parsed " + sLen + " atoms");
		
		String mName = Utils.requestStr(sc, "Second molecule");
		System.out.println("Parsing...");;
		Parser mParser = new Parser(mName);
		int mLen = mParser.atoms.size();
		System.out.println("Parsed " + mLen + " atoms");

		// p1 is movable, p2 is static
		if (sLen < mLen) {
			Parser t = sParser;
			sParser = mParser;
			mParser = t;
		}
		//visual.drawMolecule(sParser);
		
		int n = Utils.requestInt(sc, "Grid edge size");
		double r = 10;
		double fullSize = Math.max(sParser.getSize(), mParser.getSize()) + 2 * r; 
		double scale = fullSize / n;
		Params params = new Params(n, r, scale);
		
		Fourier f = new Fourier(sParser, mParser, params, visual);
		//Utils.placeMolecule(f.finalAnswer, mParser, scale, n);
		//visual.drawMolecule(mParser);
	}
}