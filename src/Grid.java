

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

public class Grid {
	public ArrayList<Cell> cells = new ArrayList<>();
	private TreeSet<Integer> set = new TreeSet<>();
	public int n;
	public static final int outer = 0;
	public static final int surface = 1;
	public static final int inner = 2;
	
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
						//if (dx * dx + dy * dy + dz * dz <= r * r) {
							if (Utils.inRange(ci, cj, ck, n, n, n)) {
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
	
	public boolean exists(int i, int j, int k) {
		int id = i * n * n + j * n + k;
		return set.contains(id);
	}
	
	public int[][][] toArray() {
		int N = n * 2;
		int[][][] r = new int[N][N][N];
		int size = cells.size();
		for (int i = 0; i < size; i++) {
			Cell cell = cells.get(i);
			r[n / 2 + cell.i][n / 2 + cell.j][n / 2 + cell.k] = inner;
		}
		// finding surface using DFS
		Stack<Cell> stack = new Stack<>();
		int corner = n / 2 - 1;
		stack.push(new Cell(corner, corner, corner));
		TreeSet<Integer> set = new TreeSet<>();
		set.add(0);
		System.out.println(N * N * N + " cells");
		int counter = 0;
		while (!stack.isEmpty()) {
			Cell cur = stack.peek();
			boolean pop = true;
			if (r[cur.i][cur.j][cur.k] == inner) {
				r[cur.i][cur.j][cur.k] = surface;
			} else {
				for (int i = 0; i < 6; i++) {
					int di = Utils.neighbours[i].i;
					int dj = Utils.neighbours[i].j;
					int dk = Utils.neighbours[i].k;
					Cell next = new Cell(cur.i + di, cur.j + dj, cur.k + dk);
					if (Utils.inRange(next.i - corner, next.j - corner, next.k - corner, n + 2, n + 2, n + 2)) {
						int id = next.i * N * N + next.j * N + next.k;
						if (!set.contains(id)) {
							stack.push(next);
							set.add(id);
							pop = false;
							counter++;
						}
					}
				}
			}
			if (pop) {
				stack.pop();
			}
		}
		System.out.println(counter + " visited");
		return r;
	}
}