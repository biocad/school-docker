package ours;

public class Atom extends Point {
	public final static String H = "H";
	public final static String C = "C";
	public final static String N = "N";
	public final static String O = "O";
	public final static String S = "S";
	public final static String P = "P";
	final static float R_H = 1f;
	final static float R_C = 1.3207547170f; 
	final static float R_N = 1.2226415094f; 
	final static float R_O = 1.1320754717f; 
	final static float R_P = 2.4150943396f; 
	final static float R_S = 1.8867924528f;  
	public float x;
	public float y;
	public float z;
	public float radius;
	public float r;
	public float g;
	public float b;
	String name;
	
	public Atom(String name, double x, double y, double z) {
		super(new double[]{x, y, z});
		
		this.name = name;
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
		
		switch (name) {
		case H:
			this.radius = R_H;
			this.r = 1f;
			this.g = 1f;
			this.b = 1f;
			break;
		case C:
			this.radius = R_C;
			this.r = .5f;
			this.g = .5f;
			this.b = .5f;
			break;
		case S:
			this.radius = R_S;
			this.r = 1f;
			this.g = 1f;
			this.b = 0f;
			break;
		case O:
			this.radius = R_O;
			this.r = 1f;
			this.g = 0f;
			this.b = 0f;
			break;
		case P:
			this.radius = R_P;
			this.r = 1f;
			this.g = .5f;
			this.b = 0f;
			break;
		case N:
			this.radius = R_N;
			this.r = 0f;
			this.g = 0f;
			this.b = 1f;
			break;
		default:
			this.radius = 1f;
			this.r = 0f;
			this.g = 1f;
			this.b = 0f;
			break;
		}
	}
}
