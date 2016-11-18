public class QGrid {
	public double[][][] arr;
	
	public QGrid(Parser p, Params params) {
		int n = params.n;
		double scale = params.scale;
		arr = new double[n][n][n];
		
		int len = p.size();
		for (int i = 0; i < len; i++) {
			Atom a = p.get(i);
			double x = a.x / scale;
			double y = a.y / scale;
			double z = a.z / scale;
			int li = (int) Math.floor(x);
			int lj = (int) Math.floor(y);
			int lk = (int) Math.floor(z);
			int ri = (int) Math.ceil(x);
			int rj = (int) Math.ceil(y);
			int rk = (int) Math.ceil(z);
			Cell[] neighbours = new Cell[]{
					new Cell(li, lj, lk),
					new Cell(li, lj, rk),
					new Cell(li, rj, lk),
					new Cell(li, rj, rk),
					new Cell(ri, lj, lk),
					new Cell(ri, lj, rk),
					new Cell(ri, rj, lk),
					new Cell(ri, rj, rk)
			};
			for (int j = 0; j < neighbours.length; j++) {
				Cell c = neighbours[j];
				double dx = c.i - x;
				double dy = c.j - y;
				double dz = c.k - z;
				arr[c.i][c.j][c.k] += (1 - dx) * (1 - dy) * (1 - dz) * a.q; 
			}
		}
	}
}
