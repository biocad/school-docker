public class Utils {
	public static Cell[] neighbours = new Cell[]{
			new Cell(-1, 0, 0),
			new Cell(1, 0, 0),
			new Cell(0, 1, 0),
			new Cell(0, -1, 0),
			new Cell(0, 0, 1),
			new Cell(0, 0, -1)
	}; 
	
	public static boolean inRange(int i, int j, int k, int w, int h, int d) {
		return i >= 0 && i < w && j >= 0 && j < h && k >= 0 && k < d;
	}

	public static double max(double... a) {
		double ans = Double.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			ans = Math.max(ans, a[i]);
		}
		return ans;
	}

	public static double min(double... a) {
		double ans = Double.MAX_VALUE;
		for (int i = 0; i < a.length; i++) {
			ans = Math.min(ans, a[i]);
		}
		return ans;
	}

	public static void placeMolecule(Parser p, Answer answer, Params params) {
		p.lock();
		double scale = params.scale;
		int len = p.size();
		for (int i = 0; i < len; i++) {
			p.get(i).x += answer.i * scale;
			p.get(i).y += answer.j * scale;
			p.get(i).z += answer.k * scale;
		}
	}
}