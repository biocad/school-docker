package rangetree;


public class Point {
	public int dim;
	public double[] coords;
	
	public Point(int dim) {
		this.dim = dim;
	}
	
	public Point(double[] coords) {
		this.coords = coords;
		this.dim = coords.length;
	}
}