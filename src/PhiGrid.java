public class PhiGrid {
	public double[][][] arr;
	
	private double eps(double r){
		if (r <= 6){
			return 4;
		}
		if (r >= 8){
			return 80;
		}
		return 38*r - 224;
	}
	
	public PhiGrid(Molecule m, Params params) {
		int n = params.n;
		double scale = params.scale;
		arr = new double[n][n][n];
		
		int len = m.size();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					for (int cur = 0; cur < len; cur++) {
						Atom a = m.get(cur);
						double dx = a.x - i * scale;
						double dy = a.y - j * scale;
						double dz = a.z - k * scale;
						double r = Math.sqrt(dx * dx + dy * dy + dz * dz);
						arr[i][j][k] += a.q / eps(r) / r;
					}
				}
			}
		}
	}
}
