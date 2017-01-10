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

	public static void placeMolecule(Molecule m, Answer answer, Params params) {
		m.lock();
		double scale = params.scale;
		int len = m.size();
		for (int i = 0; i < len; i++) {
			m.get(i).x += answer.i * scale;
			m.get(i).y += answer.j * scale;
			m.get(i).z += answer.k * scale;
		}
	}
}