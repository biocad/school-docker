import java.util.ArrayList;

public class Clock {
	private long t0;
	private ArrayList<String> keys = new ArrayList<>();
	private ArrayList<Integer> values = new ArrayList<>();
	private int i;
	
	private void reset() {
		t0 = System.currentTimeMillis();
	}
	
	public void stamp() {
		reset();
		i = 0;
	}
	
	public void stamp(String name) {
		int v = (int) (System.currentTimeMillis() - t0);
		if (keys.size() <= i) {
			keys.add(name);
			values.add(v);
		} else {
			keys.set(i, name);
			values.set(i, values.get(i) + v);
		}
		i++;
		reset();
	}
	
	public String results() {
		String r = "";
		int len = keys.size();
		for (int i = 0; i < len; i++) {
			r += keys.get(i) + "\t" + values.get(i) + System.lineSeparator();
		}
		return r;
	}
}
