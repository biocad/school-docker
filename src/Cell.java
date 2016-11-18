public class Cell implements Comparable<Cell> {
	public int i;
	public int j;
	public int k;
	public double q = 0;
	
	public Cell(int i, int j, int k) {
		this.i = i;
		this.j = j;
		this.k = k;
	}

	@Override
	public int compareTo(Cell c) {
		int v = Integer.compare(i, c.i);
		if (v != 0) return v;
		v = Integer.compare(j, c.j);
		if (v != 0) return v;
		return Integer.compare(k, c.k);
	}
}
