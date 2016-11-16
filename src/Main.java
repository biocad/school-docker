import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

public class Main {
	static Visual visual;
	static Parser sParser = null, mParser = null, p1,  p2;
	static int n = 0;
	
	static boolean ready() {
		return p1 != null && p2 != null && n != 0;
	}
	
	static void start() {
		visual.clear();
		if (p1.atoms.size() > p2.atoms.size()) {
			sParser = p1;
			mParser = p2;
		} else {
			sParser = p2;
			mParser = p1;
		}
		double fullSize = Math.max(sParser.getSize(), mParser.getSize()); 
		double scale = fullSize / n;
		Params params = new Params(n, scale);
		
		visual.shiftMolecules(-sParser.getShift());
		visual.drawMolecule(sParser);
		Grid sGrid = new Grid(sParser, params);
		visual.drawGrid(sGrid, new Cell(0, 0, 0));
		Fourier f = new Fourier(sParser, mParser, params);
		
		visual.showProgressBar();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Answer answer = f.apply();
				visual.hideProgressBar();
				Parser p = mParser.clone();
				p.rotate(answer.ax, answer.ay, answer.az);
				Grid g = new Grid(p, params);
				Utils.placeMolecule(p, answer, params);
				visual.drawMolecule(p);
				visual.drawGrid(g, new Cell(answer.i, answer.j, answer.k));
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					double p = f.getProgress();
					visual.setProgress(p);
					if (p == 1) {
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public static void main(String[] args) throws IOException {
		visual = new Visual(new Listener() {
			@Override
			public void onFileSelect(boolean first, File file, JButton b) {
				try {
					if (first) {
						p1 = new Parser(file);
					} else {
						p2 = new Parser(file);
					}
					b.setText(file.getName());
				} catch (Exception e) {
					visual.msg("Invalid file");
				}
				visual.start.setVisible(ready());
			}

			@Override
			public void onNumEntered(int num) {
				n = num;
			}

			@Override
			public void onStart() {
				start();
			}
		});
	}
}