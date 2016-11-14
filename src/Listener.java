import java.io.File;

import javax.swing.JButton;

public interface Listener {
	public void onFileSelect(boolean first, File file, JButton b);
	public void onNumEntered(int num);
	public void onStart();
}
