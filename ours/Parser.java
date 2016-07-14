package ours;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	double BN = 100000000;
	private double minx = BN;
	private double miny = BN;
	private double minz = BN;
	public double maxx = -BN;
	public double maxy = -BN;
	public double maxz = -BN;
	public double size;
	public ArrayList<Atom> atoms;
	public String name;
	
	public Parser(String name) {
		this.name = name;
		atoms = new ArrayList<>();
		Locale.setDefault(Locale.US);
		Scanner in = null;
		try {
			in = new Scanner(new File("data/" + name + ".pdb"));
		} catch (Exception e) {
			System.out.println("not found");
			System.exit(0);
		}
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
		for (int i = 0; i < len; i++){
			atoms.get(i).x -= minx;
			atoms.get(i).y -= miny;
			atoms.get(i).z -= minz;
		}
		maxx -= minx;
		maxy -= miny;
		maxz -= minz;
		size = Utils.max(maxx, maxy, maxz);
		in.close();
	}
}