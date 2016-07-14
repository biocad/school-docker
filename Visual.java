import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import ours.*;

@SuppressWarnings("serial")
public class Visual extends Applet implements KeyListener {
	private float xAngle = 0;
	private float yAngle = 0;
	private float scale = 1;
	private TransformGroup tg = new TransformGroup();
	private Transform3D transform;
	private BranchGroup wrapper = new BranchGroup();
	private BranchGroup everything = new BranchGroup();
	private SimpleUniverse universe;
	private boolean dark = false;
	
	final private Color3f black = new Color3f(0, 0, 0);
	
	public void drawMolecule(Parser p) {
		dark = !dark;
		wrapper.detach();
		BranchGroup molecule = new BranchGroup();
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = p.atoms.get(i);
			Sphere sphere = new Sphere((float) (atom.radius / 20));
			float r, g, b;
			if (dark) {
				/*
				r = (float) (atom.r * 0.5);
				g = (float) (atom.g * 0.5);
				b = (float) (atom.b * 0.5);
				*/
				r = 1.f; g = 0; b = 0;
			} else {
				/*
				r = (float) (1 - (1 - atom.r) * 0.5);
				g = (float) (1 - (1 - atom.g) * 0.5);
				b = (float) (1 - (1 - atom.b) * 0.5);
				*/
				r = 0; g = 1.f; b = 0;
			}
			Color3f color = new Color3f(r, g, b);
			Appearance ap = new Appearance();
			Material material = new Material(color, black, color, black, 1f);
			ap.setMaterial(material);
			sphere.setAppearance(ap);
			TransformGroup cur = new TransformGroup();
			transform = new Transform3D();
			Vector3d vector = new Vector3d(atom.x / 20,
										   atom.y / 20, 
										   atom.z / 20);
			transform.setTranslation(vector);
			cur.setTransform(transform);
			cur.addChild(sphere);
			
			BranchGroup curbg = new BranchGroup();
			curbg.addChild(cur);
			
			molecule.addChild(curbg);
		}
		everything.addChild(molecule);
		universe.addBranchGraph(wrapper);
	}
	
	public Visual() {
		setLayout(new BorderLayout());
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		canvas.addKeyListener(this);
		add("Center", canvas);
		
		Color3f light1Color = new Color3f(1f, 1f, 1f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		wrapper.addChild(light1);
		
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		//everything.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND | BranchGroup.ALLOW_CHILDREN_WRITE);
		wrapper.setCapability(BranchGroup.ALLOW_DETACH);
		tg.addChild(everything);
		wrapper.addChild(tg);
		
		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(wrapper);
		/*
			int n = 100;
			double r = 1.5;
			double max = Math.max(Math.max(p.maxx, p.maxy), p.maxz) + 2 * r;
			long t1 = System.nanoTime();
			Grid grid = new Grid(p, n, r, max / n);
			long t2 = System.nanoTime();
			System.out.println((t2 - t1) / 1000000 + "ms");
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
				Vector3d vector = new Vector3d(coords[0] * scale,
											   coords[1] * scale, 
											   coords[2] * scale);
				transform.setTranslation(vector);
				cur.setTransform(transform);
				cur.addChild(cube);
				tg.addChild(cur);
			}
			break;
		}
		*/
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			yAngle -= 0.03;
			break;
		case KeyEvent.VK_DOWN:
			yAngle += 0.03;
			break;
		case KeyEvent.VK_LEFT:
			xAngle -= 0.03;
			break;
		case KeyEvent.VK_RIGHT:
			xAngle += 0.03;
			break;
		case KeyEvent.VK_EQUALS:
			scale *= 1.2;
			break;
		case KeyEvent.VK_MINUS:
			scale /= 1.2;
			break;
		case KeyEvent.VK_TAB:
			break;
		}
		transform.rotX(yAngle);
		Transform3D tempTransform = new Transform3D();
		tempTransform.rotY(xAngle);
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