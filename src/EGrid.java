import java.util.ArrayList;
import java.util.TreeMap;

public class EGrid {
	public ArrayList<ECell> cells = new ArrayList<>();
	private TreeMap<Integer, ECell> map = new TreeMap<>();
	public int n;
	
	public EGrid(Parser p, Params params) {
		double innerR = 2, outerR = 5;
		int n = params.N;
		double scale = params.SCALE;
		this.n = n;
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom a = p.atoms.get(i);
			double x = a.x;
			double y = a.y;
			double z = a.z;
			int li = (int) Math.ceil((x - outerR) / scale);
			int lj = (int) Math.ceil((y - outerR) / scale);
			int lk = (int) Math.ceil((z - outerR) / scale);
			int ri = (int) Math.floor((x + outerR) / scale);
			int rj = (int) Math.floor((y + outerR) / scale);
			int rk = (int) Math.floor((z + outerR) / scale);
			for (int ci = li; ci <= ri; ci++) {
				for (int cj = lj; cj <= rj; cj++) {
					for (int ck = lk; ck <= rk; ck++) {
						double dx = ci * scale - x;
						double dy = cj * scale - y;
						double dz = ck * scale - z;
						double r = dx * dx + dy * dy + dz * dz; 
						if (innerR * innerR <= r && r <= outerR * outerR) {
							if (Utils.inRange(ci, cj, ck, n, n, n)) {
								int id = n * n * ci + n * cj + ck;
								ECell c;
								if (map.get(id) == null) {
									c = new ECell(ci, cj, ck);
									cells.add(c);
									map.put(id, c);
								} else {
									c = map.get(id);
								}
								c.add(a, Math.sqrt(r));
							}
						}
					}
				}
			}
		}
	}
}
