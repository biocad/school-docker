import java.io.*;
import java.util.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import java.io.IOException;
import ours.*;

public class Visual extends Applet implements KeyListener {
	static float hAngle = 0;
	static float vAngle = 0;
	static float scale = 1;
	static TransformGroup tg;
	static Transform3D transform;
	static BranchGroup bg = new BranchGroup();
	static Random rand = new Random();
	static Scanner in = new Scanner(System.in);
	
	public static boolean inRange(int i, int j, int k, int w, int h, int d) {
		return i >= 0 && i < w && j >= 0 && j < h && k >= 0 && k < d;
	}
	
	public Visual(String name) throws IOException {
		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Color3f black = new Color3f(0, 0, 0);
		
		Parser p = new Parser(name);
		int len = p.atoms.size();
		System.out.print("Parsed " + len + " atoms. Enter 1 to show molecule, 2 to show grid: ");
		
		switch (in.next()) {
		case "1":
			for (int i = 0; i < len; i++) {
				Atom atom = p.atoms.get(i);
				Sphere sphere = new Sphere((float) (atom.radius / 20));
				Color3f color = new Color3f(atom.r, atom.g, atom.b);
				Appearance ap = new Appearance();
				Material material = new Material(color, black, color, black, 1f);
				ap.setMaterial(material);
				sphere.setAppearance(ap);
				TransformGroup cur = new TransformGroup();
				transform = new Transform3D();
				Vector3f vector = new Vector3f((float) atom.x / 20,
											   (float) atom.y / 20, 
											   (float) atom.z / 20);
				transform.setTranslation(vector);
				cur.setTransform(transform);
				cur.addChild(sphere);
				tg.addChild(cur);
			}
			break;
		case "2":
			int n = 50;
			double r = 1.5;
			double max = Math.max(Math.max(p.maxx, p.maxy), p.maxz) + 2 * r;
			Grid grid = new Grid(p, n, r, (double) max / n);
			len = grid.cells.size();
			float scale = 0.04f;
			for (int i = 0; i < len; i++) {
				int[] coords = grid.cells.get(i);
				
				boolean doNotDraw = true;
				int cx = coords[0],
					cy = coords[1],
					cz = coords[2];
				int[] dx = new int[]{0, 0, 0, 0, 1, -1},
					  dy = new int[]{0, 0, 1, -1, 0, 0},
					  dz = new int[]{1, -1, 0, 0, 0, 0};
				for (int j = 0; j < 6; j++) {
					int x = cx + dx[j],
						y = cy + dy[j],
						z = cz + dz[j];
					if (!inRange(x, y, z, n, n, n) || !grid.grid[x][y][z]) {
						doNotDraw = false;
						break;
					}
				}
				if (doNotDraw) continue;
				ColorCube cube = new ColorCube(scale * 0.49f);
				
				TransformGroup cur = new TransformGroup();
				transform = new Transform3D();
				Vector3f vector = new Vector3f((float) coords[0] * scale,
											   (float) coords[1] * scale, 
											   (float) coords[2] * scale);
				transform.setTranslation(vector);
				cur.setTransform(transform);
				cur.addChild(cube);
				tg.addChild(cur);
			}
			break;
		}
		bg.addChild(tg);
		
		Color3f light1Color = new Color3f(1f, 1f, 1f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		bg.addChild(light1);
		
		setLayout(new BorderLayout());
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		add("Center", canvas);
		canvas.addKeyListener(this);
		
		SimpleUniverse universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(bg);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.print("Name: ");
		Visual visual = new Visual(in.next());
		visual.addKeyListener(visual);
		new MainFrame(visual, 600, 600); 
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			vAngle -= 0.03;
			break;
		case KeyEvent.VK_DOWN:
			vAngle += 0.03;
			break;
		case KeyEvent.VK_LEFT:
			hAngle -= 0.03;
			break;
		case KeyEvent.VK_RIGHT:
			hAngle += 0.03;
			break;
		case KeyEvent.VK_EQUALS:
			scale *= 1.2;
			break;
		case KeyEvent.VK_MINUS:
			scale /= 1.2;
			break;
		}
		transform.rotX(vAngle);
		Transform3D tempTransform = new Transform3D();
		tempTransform.rotY(hAngle);
		transform.mul(tempTransform);
		tempTransform = new Transform3D();
		tempTransform.setScale(scale);
		transform.mul(tempTransform);
		tg.setTransform(transform);
	}
	public void keyReleased(KeyEvent e){
	}
	public void keyTyped(KeyEvent e){
	}
}