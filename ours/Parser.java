package ours;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	double BN = 10000;
	public double maxx = -BN;
	public double minx = BN;
	public double maxy = -BN;
	public double miny = BN;
	public double maxz = -BN;
	public double minz = BN;
	public ArrayList<Atom> atoms;
	public Parser(String name) throws IOException{
		atoms = new ArrayList<>();
		Locale.setDefault(Locale.US);
		Scanner in = new Scanner(new File("data/" + name + ".pdb"));
		String s = in.nextLine(); 
		while (!s.substring(0, 3).equals("END")){
			if (s.substring(0, 4).equals("ATOM")){
				char c = s.charAt(10);
				if (c >= '0' && c <= '9'){
					double x = Double.parseDouble(s.substring(30, 38).trim());
					double y = Double.parseDouble(s.substring(38, 46).trim());
					double z = Double.parseDouble(s.substring(46, 54).trim());
					String nm = s.substring(76, 78).trim();
					maxx = Math.max(maxx, x);
					minx = Math.min(minx, x);
					maxy = Math.max(maxy, y);
					miny = Math.min(miny, y);
					maxz = Math.max(maxz, z);
					minz = Math.min(minz, z);
					atoms.add(new Atom(nm, x, y, z));
				}
			}
			s = in.nextLine();
		}
		int len = atoms.size();
		double dx = (minx + maxx) / 2,
		       dy = (miny + maxy) / 2,
		       dz = (minz + maxz) / 2;
		for (int i = 0; i < len; i++){
			atoms.get(i).x -= dx;
			atoms.get(i).y -= dy;
			atoms.get(i).z -= dz;
		}
		minx -= dx;
		miny -= dy;
		minz -= dz;
		maxx -= dx;
		maxy -= dy;
		maxz -= dz;
		in.close();
	}
}