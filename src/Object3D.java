import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

public class Object3D {
	private BranchGroup bg;
	private TransformGroup tg;
	private Transform3D t;
	private double x, y, z, ax, ay, az;
	private double scale = 1;
	private Group parent;

	public Object3D() {
		bg = new BranchGroup();
		tg = new TransformGroup();
		t = new Transform3D();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.setCapability(BranchGroup.ALLOW_DETACH);
		tg.setCapability(BranchGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
		tg.setCapability(BranchGroup.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
		tg.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
		tg.setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);
		tg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		tg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		tg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		tg.setCapability(BranchGroup.ALLOW_COLLIDABLE_READ);
		tg.setCapability(BranchGroup.ALLOW_COLLIDABLE_WRITE);
		tg.setCapability(BranchGroup.ALLOW_COLLISION_BOUNDS_READ);
		tg.setCapability(BranchGroup.ALLOW_COLLISION_BOUNDS_WRITE);
		tg.setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);
		tg.setCapability(BranchGroup.ALLOW_LOCALE_READ);
		tg.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
		tg.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
		bg.addChild(tg);
	}

	public void shift(double x, double y, double z) {
		Transform3D temp = new Transform3D();
		Vector3d v = new Vector3d(x, y, z);
		temp.set(v);
		t.mul(temp);
		tg.setTransform(t);
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public void pos(double x, double y, double z) {
		shift(x - this.x, y - this.y, z - this.z);
	}

	public void rot(double ax, double ay, double az) {
		t.rotX(ax);
		Transform3D temp = new Transform3D();
		temp.rotY(ay);
		t.mul(temp);
		temp = new Transform3D();
		temp.rotZ(az);
		t.mul(temp);
		tg.setTransform(t);
		this.ax = ax;
		this.ay = ay;
		this.az = az;
	}

	public void scale(double scale) {
		t.setScale(scale);
		tg.setTransform(t);
		this.scale = scale;
	}

	public double scale() {
		return scale;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	public double ax() {
		return ax;
	}

	public double ay() {
		return ay;
	}

	public double az() {
		return az;
	}

	public void add(Node node) {
		tg.addChild(node);
	}

	public void add(Object3D obj) {
		tg.addChild(obj.self());
	}

	public BranchGroup self() {
		return bg;
	}
	
	public void clear() {
		tg.removeAllChildren();
	}
	
	public void hide() {
		parent = (Group) (bg.getParent()); 
		parent.removeChild(bg);
	}
	
	public void show() {
		parent.addChild(bg);
	}
}
