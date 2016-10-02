

import java.util.*;

import rangetree.Point;
import rangetree.RangeTree;

public class OldGrid {
	public boolean[][][] grid;
	public ArrayList<int[]> cells = new ArrayList<>();
	private TreeSet<Integer> set = new TreeSet<>();
	private RangeTree rangeTree;
	
	private boolean find(Parser p, double x, double y, double z, double r) {
		Point r1 = new Point(new double[]{x - r, y - r, z - r});
		Point r2 = new Point(new double[]{x + r, y + r, z + r});
		ArrayList<Point> ans = rangeTree.search(r1, r2);
		int len = ans.size();
		for (int i = 0; i < len; i++) {
			Point point = ans.get(i);
			double dx = x - point.coords[0],
				   dy = y - point.coords[1],
				   dz = z - point.coords[2];
			if (dx * dx + dy * dy + dz * dz < r * r) return true;
		}
		return false;
	}
	
	public OldGrid(Parser p, int n, double r, double scale) {
		grid = new boolean[n][n][n];
		
		// ArrayList<Atom> -> ArrayList<Point> conversion
		long t1 = System.nanoTime();
		ArrayList<Point> points = new ArrayList<>();
		for (int i = 0; i < p.atoms.size(); i++) {
			Atom a = p.atoms.get(i);
			Point cur = new Point(new double[]{a.x, a.y, a.z});
			points.add(cur);
		}

		long t2 = System.nanoTime();
		System.out.println((t2 - t1) / 1000000 + "ms");
		//long t1 = System.nanoTime();
		
		//System.out.println("Range-tree construction...");
		
		rangeTree = new RangeTree(points);
		//for (int i = 0; i < n; i++) {
		//	System.out.print("_");
		//}
		
		//System.out.println();
		
		for (int i = 0; i < n; i++) {
			//System.out.print("#");
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					double x = i * scale - r,
					       y = j * scale - r,
						   z = k * scale - r;
					if (!find(p, x, y, z, r)) continue;
					grid[i][j][k] = true;
					int id = n * n * i + n * j + k;
					if (!set.contains(id)) {
						cells.add(new int[]{i, j, k});
						set.add(id);
					}
				}
			}
		}
		//long t2 = System.nanoTime();
		//System.out.println();
		//System.out.println("Time: " + ((t2 - t1) / 1000000) + "ms");
	}
}