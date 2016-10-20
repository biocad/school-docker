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
		System.out.println("Parsing...");
		Parser mParser = new Parser(mName);
		int mLen = mParser.atoms.size();
		System.out.println("Parsed " + mLen + " atoms");

		if (sLen < mLen) {
			Parser t = sParser;
			sParser = mParser;
			mParser = t;
		}
		//visual.drawMolecule(sParser);
		
		int n = Utils.requestInt(sc, "Grid edge size");
		double fullSize = Math.max(sParser.getSize(), mParser.getSize()); 
		double scale = fullSize / n;
		Params params = new Params(n, scale);
		
		Fourier f = new Fourier(sParser, mParser, params, visual);
		f.apply();
		//Utils.placeMolecule(f.finalAnswer, mParser, scale, n);
		//visual.drawMolecule(mParser);
	}
}