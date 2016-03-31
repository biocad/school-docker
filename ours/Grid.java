package ours;

import java.util.*;

public class Grid {
	public static boolean[][][] grid;
	static TreeSet<Integer> set = new TreeSet<>();
	public static ArrayList<int[]> cells = new ArrayList<>();
	static boolean find(Parser p, double x, double y, double z, double r) {
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = p.atoms.get(i);
			double dx = x - atom.x,
			       dy = y - atom.y,
				   dz = z - atom.z;
			if (dx * dx + dy * dy + dz * dz < r * r) return true;
		}
		return false;
	}
	public Grid(Parser p, int n, double scale) {
		grid = new boolean[n][n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					double x = i / scale + p.minx,
					       y = j / scale + p.miny,
						   z = k / scale + p.minz;
					if (!find(p, x, y, z, 1.5)) continue;
					grid[i][j][k] = true;
					int id = n * n * i + n * j + k;
					if (!set.contains(id)) {
						cells.add(new int[]{i - n / 2, j - n / 2, k - n / 2});
						set.add(id);
					}
				}
			}
		}
	}
}