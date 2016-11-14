import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	private double minX, minY, minZ, maxX, maxY, maxZ;
	private double size;
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
					double r = atom.radius;
					maxR = Math.max(maxR, r);
					atoms.add(atom);

					minX = Math.min(minX, x - r);
					minY = Math.min(minY, y - r);
					minZ = Math.min(minZ, z - r);

					maxX = Math.max(maxX, x + r);
					maxY = Math.max(maxY, y + r);
					maxZ = Math.max(maxZ, z + r);
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
			size = Math.max(size, ((cx-atom.x)*(cx-atom.x) + (cy-atom.y)*(cy-atom.y)+(cz-atom.z)*(cz-atom.z)));
		}
		size = Math.sqrt(size) + maxR;
		for (int i = 0; i < len; i++) {
			Atom atom = atoms.get(i);
			atom.x -= cx-size;
			atom.y -= cy-size;
			atom.z -= cz-size;
		}
		in.close();
	}
	
	public Parser clone() {
		return new Parser(this);
	}


	public double getSize() {
		return size*2;
	}
}
	