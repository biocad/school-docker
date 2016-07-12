package ours;

public class Node {
	public double value;
	public Node tree;
	public Node left, right;
	public Point p;
	
	public Node() {}
	
	public Node(double value) {
		this.value = value;
	}
	
	public boolean hasLeft() {
		return left != null;
	}
	
	public boolean hasRight() {
		return right != null;
	}
	
	public boolean isLeaf() {
		return !this.hasLeft() && !this.hasRight();
	}
}