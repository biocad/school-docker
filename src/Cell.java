import java.util.ArrayList;

public class Cell {
	public int i;
	public int j;
	public int k;
	public ArrayList<Atom> atoms = new ArrayList<>();
	
	public Cell(int i, int j, int k) {
		this.i = i;
		this.j = j;
		this.k = k;
	}
}
