import java.util.ArrayList;
import java.util.Stack;

public class Grid {
	private ArrayList<Cell> cells = new ArrayList<>();
	public double[][][] arr;
	public static final int outer = 0;
	public static final int surface = 1;
	public static final int inner = 2;
	
	public Grid(Molecule m, Params params) throws LockedMoleculeException {
		if (m.isLocked()) {
			throw new LockedMoleculeException();
		}
		int n = params.n, N = 2 * n;
		arr = new double[N][N][N];
		double scale = params.scale;
		int len = m.size();
		for (int i = 0; i < len; i++) {
			Atom a = m.get(i);
			double r = a.radius;
			double x = a.x;
			double y = a.y;
			double z = a.z;
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
						if (dx * dx + dy * dy + dz * dz <= r * r) {
							arr[n/2 + ci][n/2 + cj][n/2 + ck] = inner;
						}
					}
				}
			}
		}
		boolean[] visited = new boolean[N * N * N];
		// finding surface using DFS
		Stack<Cell> stack = new Stack<>();
		int corner = n/2 - 1;
		stack.push(new Cell(corner, corner, corner));
		while (!stack.isEmpty()) {
			Cell cur = stack.peek();
			boolean pop = true;
			if (arr[cur.i][cur.j][cur.k] == inner) {
				arr[cur.i][cur.j][cur.k] = surface;
				cells.add(new Cell(cur.i - n/2, cur.j - n/2, cur.k - n/2));
			} else {
				for (int i = 0; i < 6; i++) {
					int di = Utils.neighbours[i].i;
					int dj = Utils.neighbours[i].j;
					int dk = Utils.neighbours[i].k;
					Cell next = new Cell(cur.i + di, cur.j + dj, cur.k + dk);
					if (Utils.inRange(next.i - corner, next.j - corner, next.k - corner, n + 2, n + 2, n + 2)) {
						int id = next.i * N * N + next.j * N + next.k;
						if (!visited[id]) {
							stack.push(next);
							visited[id] = true;
							pop = false;
						}
					}
				}
			}
			if (pop) {
				stack.pop();
			}
		}
	}
	
	public int size() {
		return cells.size();
	}
	
	public Cell get(int i) {
		return cells.get(i);
	}
}