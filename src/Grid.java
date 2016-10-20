

import java.util.ArrayList;
import java.util.TreeSet;

public class Grid {
	public ArrayList<Cell> cells = new ArrayList<>();
	private TreeSet<Integer> set = new TreeSet<>();
	public int n;
	public static final int outer = 0;
	public static final int surface = 1;
	public static final int inner = 2;
	
	public static boolean inRange(int i, int j, int k, int w, int h, int d) {
		return i >= 0 && i < w && j >= 0 && j < h && k >= 0 && k < d;
	}
	
	public Grid(Parser p, Params params) {
		int n = params.N;
		double scale = params.SCALE;
		this.n = n;
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom a = p.atoms.get(i);
			double r = a.radius;
			double x = a.x - p.getMinX();
			double y = a.y - p.getMinY();
			double z = a.z - p.getMinZ();
			int li = (int) Math.ceil((x - r) / scale);
			int lj = (int) Math.ceil((y - r) / scale);
			int lk = (int) Math.ceil((z - r) / scale);
			int ri = (int) Math.floor((x + r) / scale);
			int rj = (int) Math.floor((y + r) / scale);
			int rk = (int) Math.floor((z + r) / scale);
			for (int ci = li; ci <= ri; ci++) {
				for (int cj = lj; cj <= rj; cj++) {
					for (int ck = lk; ck <= rk; ck++) {
						double dx = ci * scale - x;
						double dy = cj * scale - y;
						double dz = ck * scale - z;
						//if (dx * dx + dy * dy + dz * dz <= r*r) {
							if (inRange(ci, cj, ck, n, n, n)) {
								int id = n * n * ci + n * cj + ck;
								if (!set.contains(id)) {
									cells.add(new Cell(ci, cj, ck));
									set.add(id);
								}
							}
						//}
					}
				}
			}
		}
	}
	
	public int[][][] toArray() {
		int[][][] r = new int[n][n][n];
		int size = cells.size();
		for (int i = 0; i < size; i++) {
			Cell cell = cells.get(i);
			r[cell.i][cell.j][cell.k] = inner;
		}
		return r;
	}
}