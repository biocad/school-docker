public class Answer {
	public double fitness = Double.MIN_VALUE;
	public int i, j, k;
	public double ax, ay, az;
	
	public String toString() {
		return "i: " + i + ", j: " + j + ", k: " + k + ", ax: " + ax + ", ay: " + ay + ", az: " + az + " (fitness: " + fitness + ")";
	}
}
