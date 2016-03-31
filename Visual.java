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
	
	public static float randCoord() {
		return (float) (rand.nextDouble() - rand.nextDouble());
	}
	
	public static boolean inRange(int i, int j, int k, int w, int h, int d) {
		return i >= 0 && i < w && j >= 0 && j < h && k >= 0 && k < d;
	}
	
	public Visual(String name) throws IOException {
		Parser p = new Parser(name);
		int len = p.atoms.size();
		double sizex = p.maxx - p.minx,
		       sizey = p.maxy - p.miny,
			   sizez = p.maxz - p.minz;
		int n = 50;
		double size = Math.max(sizex, Math.max(sizey, sizez));
		Grid grid = new Grid(p, n, (n - 1) / size);
		len = grid.cells.size();
		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Color3f black = new Color3f(0, 0, 0);
		float scale = 0.04f;
		for (int i = 0; i < len; i++) {
			/*
			Atom atom = p.atoms.get(i);
			Sphere sphere = new Sphere((float) (atom.radius * scale));
			*/
			int[] coords = grid.cells.get(i);
			
			boolean doNotDraw = true;
			int cx = coords[0] + n / 2,
			    cy = coords[1] + n / 2,
			    cz = coords[2] + n / 2;
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
			Vector3f vector = new Vector3f((float) /*atom.x*/ coords[0] * scale,
			                               (float) /*atom.y*/ coords[1] * scale, 
										   (float) /*atom.z*/ coords[2] * scale);
			transform.setTranslation(vector);
			cur.setTransform(transform);
			/*
			Color3f color = new Color3f(atom.r, atom.g, atom.b);
			Appearance ap = new Appearance();
			Material material = new Material(color, black, color, black, 1f);
			ap.setMaterial(material);
			sphere.setAppearance(ap);
			cur.addChild(sphere);
			*/
			cur.addChild(cube);
			tg.addChild(cur);
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
		Visual visual = new Visual(args[0]);
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