import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

public class Main {
	static Visual visual;
	static Parser sParser = null, mParser = null;
	static int n = 0;
	
	static boolean ready() {
		return sParser != null && mParser != null && n != 0;
	}
	
	static void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					visual.clear();
					if (sParser.atoms.size() < mParser.atoms.size()) {
						Parser t = sParser;
						sParser = mParser;
						mParser = t;
					}
					visual.drawMolecule(sParser);
					
					double fullSize = Math.max(sParser.getSize(), mParser.getSize()); 
					double scale = fullSize / n;
					Params params = new Params(n, scale);
					
					Fourier f = new Fourier(sParser, mParser, params, visual);
					f.apply();
				} catch (Exception e) {
					e.printStackTrace();
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
						sParser = new Parser(file);
					} else {
						mParser = new Parser(file);
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