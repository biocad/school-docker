package ours;

import java.util.ArrayList;
import java.util.TreeSet;

public class Grid {
	public ArrayList<Cell> cells = new ArrayList<>();
	private TreeSet<Integer> set = new TreeSet<>();
	
	public static boolean inRange(int i, int j, int k, int w, int h, int d) {
		return i >= 0 && i < w && j >= 0 && j < h && k >= 0 && k < d;
	}
	
	public Grid(Parser p, int n, double r, double scale) {
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom a = p.atoms.get(i);
			double x = a.x + r;
			double y = a.y + r;
			double z = a.z + r;
			int li = (int) Math.ceil((x - r) / scale);
			int lj = (int) Math.ceil((y - r) / scale);
			int lk = (int) Math.ceil((z - r) / scale);
			int ri = (int) Math.ceil((x + r) / scale);
			int rj = (int) Math.ceil((y + r) / scale);
			int rk = (int) Math.ceil((z + r) / scale);
			for (int ci = li; ci <= ri; ci++) {
				for (int cj = lj; cj <= rj; cj++) {
					for (int ck = lk; ck <= rk; ck++) {
						double dx = ci * scale - x;
						double dy = cj * scale - y;
						double dz = ck * scale - z;
						if (dx*dx + dy*dy + dz*dz <= r*r) {
							if (inRange(ci, cj, ck, n, n, n)) {
								int id = n * n * ci + n * cj + ck;
								if (!set.contains(id)) {
									cells.add(new Cell(ci, cj, ck));
									set.add(id);
								}
							}
						}
					}
				}
			}
		}
	}
}
