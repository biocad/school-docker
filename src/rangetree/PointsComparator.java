package rangetree;


import java.util.*;

public class PointsComparator implements Comparator<Point> {
	public int dim;
	
	public int compare(Point a, Point b) {
		return Double.compare(a.coords[dim], b.coords[dim]);
	}
	
	public PointsComparator() {
	}
}