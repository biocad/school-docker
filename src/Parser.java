import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	private double minX, minY, minZ, maxX, maxY, maxZ;
	private double size;
	public ArrayList<Atom> atoms;
	
	private Parser(Parser original) {
		minX = original.minX;
		minY = original.minY;
		minZ = original.minZ;
		maxX = original.maxX;
		maxY = original.maxY;
		maxZ = original.maxZ;
		size = original.size;
		atoms = new ArrayList<>();
		int len = original.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = original.atoms.get(i);
			atoms.add(new Atom(atom.name, atom.x, atom.y, atom.z));
		}
	}
	
	public Parser(String name) {
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
					
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					minZ = Math.min(minZ, z);
					
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
					maxZ = Math.max(maxZ, z);
				}
			}
			s = in.nextLine();
		}
		double cx = (minX + maxX) / 2;
		double cy = (minY + maxY) / 2;
		double cz = (minZ + maxZ) / 2;
		int len = atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = atoms.get(i);
			atom.x -= cx;
			atom.y -= cx;
			atom.z -= cx;
		}
		minX -= cx;
		minY -= cy;
		minZ -= cz;

		maxX -= cx;
		maxY -= cy;
		maxZ -= cz;

		double dx = maxX - minX;
		double dy = maxY - minY;
		double dz = maxZ - minZ;
		size = Math.sqrt(dx * dx + dy * dy + dz * dz);  
		
		in.close();
	}
	
	public Parser clone() {
		return new Parser(this);
	}
	
	public double getMinX() {
		return minX;
	}
	
	public double getMinY() {
		return minY;
	}
	
	public double getMinZ() {
		return minZ;
	}
	
	public double getMaxX() {
		return maxX;
	}
	
	public double getMaxY() {
		return maxY;
	}
	
	public double getMaxZ() {
		return maxZ;
	}
	
	public double getSize() {
		return size;
	}
}