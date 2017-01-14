import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

public class Main {
	static Visual visual;
	static Molecule sMolecule = null, mMolecule = null, m1,  m2;
	
	static boolean ready() {
		return m1 != null && m2 != null;
	}
	
	static void start() throws LockedMoleculeException {
		visual.clear();
		if (m1.size() > m2.size()) {
			sMolecule = m1;
			mMolecule = m2;
		} else {
			sMolecule = m2;
			mMolecule = m1;
		}
		Params params = new Params(sMolecule, mMolecule);
		
		visual.posMolecules(-sMolecule.getShift());
		visual.posGrids(-sMolecule.getShift());
		
		visual.drawMolecule(sMolecule);
		Grid sGrid = new Grid(sMolecule, params);
		visual.drawGrid(sGrid, new Cell(0, 0, 0), params);
		Solver f = new Solver(sMolecule, mMolecule, params);
		
		visual.showProgressBar();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Answer answer = f.apply();
					System.out.println(answer);
					visual.hideProgressBar();
					Molecule m = mMolecule.clone();
					m.rotate(answer.ax, answer.ay, answer.az);
					Grid g = new Grid(m, params);
					Utils.placeMolecule(m, answer, params);
					visual.drawMolecule(m);
					visual.drawGrid(g, new Cell(answer.i, answer.j, answer.k), params);
				} catch (LockedMoleculeException e) {
					e.printStackTrace();
				}
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
						m1 = new Molecule(file);
					} else {
						m2 = new Molecule(file);
					}
					b.setText(file.getName());
				} catch (Exception e) {
					visual.msg("Invalid file");
					e.printStackTrace();
				}
				visual.start.setVisible(ready());
			}

			@Override
			public void onStart() {
				try {
					start();
				} catch (LockedMoleculeException e) {
					e.printStackTrace();
				}
			}
		});
	}
}