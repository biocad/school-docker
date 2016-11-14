import java.io.File;

public interface Listener {
	public void onFileSelect(boolean first, File file);
	public void onNumEntered(String num);
}
