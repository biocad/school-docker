package ours;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	public double minx, miny, minz, maxx, maxy, maxz;
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
					atoms.add(new Atom(nm, x, y, z));
				}
			}
			s = in.nextLine();
		}
		centration();
//		dropOnAxisses();
		size = Utils.max(maxx - minx, maxy - miny, maxz - minz);
		in.close();
	}
	
	public void centration(){
		minx = miny = minz = Double.MAX_VALUE;
		maxx = maxy = maxz = Double.MIN_VALUE;
		int len = atoms.size();
		for (int i = 0; i < len; i++){
			minx = Math.min(minx, atoms.get(i).x);
			miny = Math.min(miny, atoms.get(i).y);
			minz = Math.min(minz, atoms.get(i).z);
			
			maxx = Math.max(maxx, atoms.get(i).x);
			maxy = Math.max(maxy, atoms.get(i).y);
			maxz = Math.max(maxz, atoms.get(i).z);
		}
		double cx = (minx + maxx) / 2;
		double cy = (miny + maxy) / 2;
		double cz = (minz + maxz) / 2;
		for (int i = 0; i < len; i++){
			atoms.get(i).x -= cx; 
			atoms.get(i).y -= cy; 
			atoms.get(i).z -= cz; 
		}
		minx -= cx;
		miny -= cy;
		minz -= cz;
		maxx -= cx;
		maxy -= cy;
		maxz -= cz;
		size = Utils.max(maxx - minx, maxy - miny, maxz - minz);
	}
	
	public void dropOnAxisses(){
		minx = miny = minz = Double.MAX_VALUE;
		int len = atoms.size();
		for (int i = 0; i < len; i++){
			minx = Math.min(minx, atoms.get(i).x);
			miny = Math.min(miny, atoms.get(i).y);
			minz = Math.min(minz, atoms.get(i).z);
		}
		for (int i = 0; i < len; i++){
			atoms.get(i).x -= minx; 
			atoms.get(i).y -= miny; 
			atoms.get(i).z -= minz; 
		}
		maxx -= minx;
		maxy -= miny;
		maxz -= minz;
		minx = 0;
		miny = 0;
		minz = 0;
		size = Utils.max(maxx - minx, maxy - miny, maxz - minz);
	}
}