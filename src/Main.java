import java.io.File;
import java.io.IOException;

public class Main {
	static Visual visual;
	static Parser sParser = null, mParser = null;
	static int n = 0;
	
	static boolean ready() {
		return sParser != null && mParser != null && n != 0;
	}
	
	static void start() {
		try {
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
	
	public static void main(String[] args) throws IOException {
		visual = new Visual(new Listener() {
			@Override
			public void onFileSelect(boolean first, File file) {
				visual.updateInfo(visual.err, "");
				try {
					if (first) {
						sParser = new Parser(file);
						visual.updateInfo(visual.f1, "File 1: " + file.getName());
					} else {
						mParser = new Parser(file);
						visual.updateInfo(visual.f2, "File 2: " + file.getName());
					}
				} catch (Exception e) {
					visual.updateInfo(visual.err, "Invalid file");
				}
				if (ready()) {
					start();
				}
			}

			@Override
			public void onNumEntered(String num) {
				int k = Integer.parseInt(num);
				if (k > 0 && (k & (k - 1)) == 0) {
					n = k;
					if (ready()) {
						start();
					}
				} else {
					visual.updateInfo(visual.err, "Must be a power of 2");
				}
			}
		});
	}
}