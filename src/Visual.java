import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

@SuppressWarnings("serial")
public class Visual extends JFrame {
	private Object3D wrapper = new Object3D();
	private Object3D everything = new Object3D();
	private Object3D content = new Object3D();
	private SimpleUniverse universe;
	private boolean trigger = false;
	public JButton start = new JButton("Start");

	public void msg(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
	
	private void addContent() {
		content = new Object3D();
		content.scale(0.01);
		content.self().setCapability(BranchGroup.ALLOW_DETACH);
		wrapper.add(content);
	}
	
	public void clear() {
		content.self().detach();
		addContent();
	}
	
	public Visual(Listener listener) {
		setLayout(new BorderLayout());
		setSize(600, 600);
		
		// canvas
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		Color3f light1Color = new Color3f(1f, 1f, 1f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		everything.add(light1);

		// axises

		Object3D xAxis = new Object3D();
		xAxis.add(new Cylinder((float) .01, 5));
		xAxis.rot(Math.PI / 2, 0, 0);
		wrapper.add(xAxis);

		Object3D yAxis = new Object3D();
		yAxis.add(new Cylinder((float) .01, 5));
		yAxis.rot(0, Math.PI / 2, 0);
		wrapper.add(yAxis);

		Object3D zAxis = new Object3D();
		zAxis.add(new Cylinder((float) .01, 5));
		zAxis.rot(0, 0, Math.PI / 2);
		wrapper.add(zAxis);

		everything.add(wrapper);

		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(everything.self());
		
		canvas.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_EQUALS:
					content.scale(content.scale() * 1.2);
					break;
				case KeyEvent.VK_MINUS:
					content.scale(content.scale() / 1.2);
					break;
				}
			}
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int w = canvas.getWidth(), h = canvas.getHeight();
				double ay = Math.PI * e.getX() / w * 4;
				double ax = Math.PI * (e.getY() - h / 2) / h * 2;
				ax = Math.min(ax, Math.PI / 2);
				ax = Math.max(ax, -Math.PI / 2);
				wrapper.rot(ax, ay, 0);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});

		add(canvas, BorderLayout.CENTER);

		// interface

		JPanel side = new JPanel();
		side.setLayout(new BorderLayout());
		JPanel control = new JPanel();
		control.setLayout(new GridLayout(4, 1));
		JButton fileButton1 = new JButton("1st molecule");
		JButton fileButton2 = new JButton("2nd molecule");
		ActionListener onChoose = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("data"));
				if (chooser.showDialog(null, "Choose a pdb file") == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					boolean first = e.getSource() == fileButton1;
					listener.onFileSelect(first, file, ((JButton) e.getSource()));
				}
			}
		};
		fileButton1.addActionListener(onChoose);
		fileButton2.addActionListener(onChoose);
		control.add(fileButton1);
		control.add(fileButton2);
		JComboBox<Integer> num = new JComboBox<>(new Integer[]{32, 64, 128, 256});
		num.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.onNumEntered((int) num.getSelectedItem());
			}
		});
		listener.onNumEntered(32);
		control.add(num);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.onStart();
			}
		});
		start.setVisible(false);
		control.add(start);
		side.add(control, BorderLayout.NORTH);

		add(side, BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public Object3D drawMolecule(Parser p) {
		Object3D molecule = new Object3D();
		int len = p.atoms.size();
		for (int i = 0; i < len; i++) {
			Atom atom = p.atoms.get(i);
			Sphere sphere = new Sphere((float) (atom.radius));
			float r = (float) atom.r, g = (float) atom.g, b = (float) atom.b;
			double k = 0.5;
			if (trigger) {
				r = (float) (1 - (1 - r) * k);
				g = (float) (1 - (1 - g) * k);
				b *= k;
			} else {
				r = (float) (1 - (1 - r) * k);
				g *= k;
				b = (float) (1 - (1 - b) * k);
			}
			Appearance ap = new Appearance();
			Color3f black = new Color3f(0, 0, 0);
			Color3f color = new Color3f(r, g, b);
			Material material = new Material(color, black, color, black, 1f);
			ap.setMaterial(material);
			sphere.setAppearance(ap);
			Object3D obj = new Object3D();
			obj.add(sphere);
			obj.pos(atom.x, atom.y, atom.z);
			molecule.add(obj);
		}
		trigger = !trigger;
		content.add(molecule);
		return molecule;
	}
}
