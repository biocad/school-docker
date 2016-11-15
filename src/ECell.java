import java.util.ArrayList;

public class ECell extends Cell {
	private ArrayList<Atom> atoms = new ArrayList<>();
	private ArrayList<Double> distances = new ArrayList<>();

	public ECell(int i, int j, int k) {
		super(i, j, k);
	}
	
	public void add(Atom a, double r) {
		atoms.add(a);
		distances.add(r);
	}
	
	public int size() {
		return atoms.size();
	}
	
	public Atom getAtom(int i) {
		return atoms.get(i);
	}
	
	public double getDistance(int i) {
		return distances.get(i);
	}
}
