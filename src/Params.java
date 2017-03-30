public class Params {
	public final int n; // grid side
	public final double scale = 2;//2.028325411415352; // grid cell size
	public final double d;
	
	public Params(Molecule m1, Molecule m2) {
		double fullSize = Math.max(m1.getSize(), m2.getSize());
		d = 0.001;
		for (int i = 4; ; i *= 2) {
			if (i * scale >= fullSize) {
				n = i;
				break;
			}
		}
		System.out.println(n);
	}
}
