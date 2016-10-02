

public class Atom {
	public final static String H = "H";
	public final static String C = "C";
	public final static String N = "N";
	public final static String O = "O";
	public final static String S = "S";
	public final static String P = "P";
	final static double R_H = 1f;
	final static double R_C = 1.4166666666f; 
	final static double R_N = 1.2916666666f; 
	final static double R_O = 1.2916666666f; 
	final static double R_P = 1.625f; 
	final static double R_S = 1.5f;  
	public double x;
	public double y;
	public double z;
	public double radius;
	public double r;
	public double g;
	public double b;
	String name;
	
	public Atom(String name, double x, double y, double z) {		
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		
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
