import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	private double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, maxZ = Double.MIN_VALUE;
	private double size, shift;
	public ArrayList<Atom> atoms;

	private Parser(Parser original) {
		size = original.size;
		atoms = new ArrayList<>();
		int len = original.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = original.atoms.get(i);
			atoms.add(new Atom(atom.name, atom.x, atom.y, atom.z));
		}
	}

	public Parser(File file) throws FileNotFoundException {
		atoms = new ArrayList<>();
		Locale.setDefault(Locale.US);
		Scanner in = new Scanner(file);
		String s = in.nextLine();
		double maxR = 0;
		while (!s.substring(0, 3).equals("END")) {
			if (s.substring(0, 4).equals("ATOM")) {
				char c = s.charAt(10);
				if (c >= '0' && c <= '9') {
					double x = Double.parseDouble(s.substring(30, 38).trim());
					double y = Double.parseDouble(s.substring(38, 46).trim());
					double z = Double.parseDouble(s.substring(46, 54).trim());
					String nm = s.substring(76, 78).trim();
					Atom atom = new Atom(nm, x, y, z);
					maxR = Math.max(maxR, atom.radius);
					atoms.add(atom);

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
		shift = 0;
		for (int i = 0; i < len; i++) {
			Atom atom = atoms.get(i);
			double dx = cx - atom.x;
			double dy = cy - atom.y;
			double dz = cz - atom.z;
			shift = Math.max(shift, (dx * dx + dy * dy + dz * dz));
		}
		shift = Math.sqrt(shift) + maxR;
		for (int i = 0; i < len; i++) {
			Atom atom = atoms.get(i);
			atom.x += shift - cx;
			atom.y += shift - cy;
			atom.z += shift - cz;
		}
		size = shift * 2;
		in.close();
	}
	
	public void rotate(double ax, double ay, double az) {
		int len = atoms.size();
		
		for (int i = 0; i < len; i++) {
			Atom a = atoms.get(i);
			double x = a.x - shift;
			double y = a.y - shift;
			double z = a.z - shift;
			 a.y = Math.cos(ax) * y - Math.sin(ax) * z;
			 a.z = Math.sin(ax) * y + Math.cos(ax) * z;

			 x = a.x;
			 y = a.y;
			 z = a.z;
			
			 a.x = Math.cos(ay) * x + Math.sin(ay) * z;
			 a.z = -Math.sin(ay) * x + Math.cos(ay) * z;
			 
			 x = a.x;
			 y = a.y;
			 z = a.z;
			
			 a.x = Math.cos(az) * x - Math.sin(az) * y;
			 a.y = Math.sin(az) * x + Math.cos(az) * y;

			 a.x += shift;
			 a.y += shift;
			 a.z += shift;
		}
	}
	
	public double getShift() {
		return shift;
	}
	
	public Parser clone() {
		return new Parser(this);
	}


	public double getSize() {
		return size;
	}
}
	