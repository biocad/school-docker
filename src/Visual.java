import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

@SuppressWarnings("serial")
public class Visual extends Applet implements KeyListener, MouseMotionListener {
	private int w, h;
	private float xAngle = 0;
	private float yAngle = 0;
	private float scale = 0.01f;
	private TransformGroup tg = new TransformGroup();
	private BranchGroup wrapper = new BranchGroup();
	private BranchGroup everything = new BranchGroup();
	private SimpleUniverse universe;
	private boolean dark = false;
	
	final private Color3f black = new Color3f(0, 0, 0);
	
	private void pos(TransformGroup tg, double x, double y, double z) {
		Transform3D tr = new Transform3D();
		Vector3d v = new Vector3d(x, y, z);
		tr.setTranslation(v);
		tg.setTransform(tr);
	}
	
	public void drawMolecule(Parser p) {
		dark = !dark;
		wrapper.detach();
		BranchGroup molecule = new BranchGroup();
		molecule.setCapability(BranchGroup.ALLOW_DETACH);
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = p.atoms.get(i);
			Sphere sphere = new Sphere((float) (atom.radius));
			float r, g, b;
			if (dark) {
				r = 1.f; g = 0; b = 0;
			} else {
				r = 0; g = 1.f; b = 0;
			}
			Color3f color = new Color3f(r, g, b);
			Appearance ap = new Appearance();
			Material material = new Material(color, black, color, black, 1f);
			ap.setMaterial(material);
			sphere.setAppearance(ap);
			TransformGroup cur = new TransformGroup();
			cur.addChild(sphere);
			pos(cur, atom.x, atom.y, atom.z);
			molecule.addChild(cur);
		}
		everything.addChild(molecule);
		universe.addBranchGraph(wrapper);
	}
	
	public void drawGrid(Grid grid, Cell offset) {
		dark = !dark;
		wrapper.detach();
		BranchGroup g = new BranchGroup();
		int len = grid.cells.size();
		for (int i = 0; i < len; i++) {
			Cell cell = grid.cells.get(i);
			ColorCube cube = new ColorCube();
			Color3f color = new Color3f(0, 0, 1);
			if (dark) {
				color = new Color3f(1, 1, 0);
			}
			Appearance ap = new Appearance();
			Material material = new Material(color, black, color, black, 1f);
			ap.setMaterial(material);
			cube.setAppearance(ap);
			TransformGroup cur = new TransformGroup();
			Transform3D transform = new Transform3D();
			Vector3d vector = new Vector3d(cell.i + offset.i,
										   cell.j + offset.j, 
										   cell.k + offset.k);
			transform.setTranslation(vector);
			cur.setTransform(transform);
			cur.addChild(cube);
			g.addChild(cur);
		}
		everything.addChild(g);
		universe.addBranchGraph(wrapper);
	}
	
	public void transform() {
		Transform3D tfx = new Transform3D();
		tfx.rotX(yAngle);
		Transform3D tfy = new Transform3D();
		tfy.rotY(xAngle);
		tfx.mul(tfy);
		Transform3D tfs = new Transform3D();
		tfs.setScale(scale);
		tfx.mul(tfs);
		tg.setTransform(tfx);
	}
	
	public Visual(int w, int h) {
		this.w = w;
		this.h = h;
		setLayout(new BorderLayout());
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		canvas.addKeyListener(this);
		canvas.addMouseMotionListener(this);
		add("Center", canvas);
		
		Color3f light1Color = new Color3f(1f, 1f, 1f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		wrapper.addChild(light1);
		
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		// axises
		Cylinder xAxis = new Cylinder(1, 300);
		everything.addChild(xAxis);
		
		Cylinder yAxis = new Cylinder(1, 300);
		TransformGroup yAxisTG = new TransformGroup();
		yAxisTG.addChild(yAxis);
		Transform3D yAxisTr = new Transform3D();
		yAxisTr.rotZ((float) (Math.PI / 2));
		yAxisTG.setTransform(yAxisTr);
		everything.addChild(yAxisTG);
		
		Cylinder zAxis = new Cylinder(1, 300);
		TransformGroup zAxisTG = new TransformGroup();
		zAxisTG.addChild(zAxis);
		Transform3D zAxisTr = new Transform3D();
		zAxisTr.rotX((float) (Math.PI / 2));
		zAxisTG.setTransform(zAxisTr);
		everything.addChild(zAxisTG);
		
		wrapper.setCapability(BranchGroup.ALLOW_DETACH);
		tg.addChild(everything);
		wrapper.addChild(tg);
		
		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(wrapper);
		
		transform();
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
		transform();
	}
	
	public void keyReleased(KeyEvent e){
	}
	
	public void keyTyped(KeyEvent e){
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		xAngle = (float) (e.getX() - w / 2) / 50;
		
		yAngle = (float) ((float) (e.getY() - h / 2) / (h / 3) * Math.PI / 2);
		yAngle = (float) Math.min(yAngle, Math.PI / 2);
		yAngle = (float) Math.max(yAngle, -Math.PI / 2);
		transform();
	}
}