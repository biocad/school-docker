package ours;

import java.util.*;

public class RangeTree {
	private PointsComparator pointsComparator = new PointsComparator();
	public Node root;
	final private int SPLIT_SEARCH = 0;
	final private int REPORT_LEFT = 1;
	final private int REPORT_RIGHT = 2;
	
	public RangeTree(ArrayList<Point> points) {
		this.root = make(points, points.get(0).dim - 1);
	}
	
	private Node make(ArrayList<Point> points, int dim) {
		pointsComparator.dim = dim;
		Collections.sort(points, pointsComparator);
		int n = points.size();
		Node tree = make__helper(points, dim, 0, n - 1);
		if (dim == 0) return tree;
		
		ArrayList<Node> nodes = subtreeNodes(tree);
		int len = nodes.size();
		for (int i = 0; i < len; i++) {
			Node node = nodes.get(i);
			node.tree = make(subtreePoints(node), dim - 1);
		}
		return tree;
	}
	
	private Node make__helper(ArrayList<Point> points, int dim, int l, int r) {
		if (l == r) {
			Point p = points.get(l);
			Node leaf = new Node(p.coords[dim]);
			leaf.p = p;
			return leaf;
		}
		int ml = (l + r) / 2, mr = ml + 1;
		Node cur = new Node((points.get(ml).coords[dim] + points.get(mr).coords[dim]) / 2);
		cur.left = make__helper(points, dim, l, ml);
		cur.right = make__helper(points, dim, mr, r);
		return cur;
	}
	
	private ArrayList<Point> subtreePoints(Node root) {
		ArrayList<Point> ans = new ArrayList<>();
		subtreePoints__helper(root, ans);
		return ans;
	}
	
	private void subtreePoints__helper(Node node, ArrayList<Point> ans) {
		if (node == null) return;
		if (node.isLeaf()) {
			ans.add(node.p);
		}
		subtreePoints__helper(node.left, ans);
		subtreePoints__helper(node.right, ans);
	}
	
	private ArrayList<Node> subtreeNodes(Node root) {
		ArrayList<Node> ans = new ArrayList<>();
		subtreeNodes__helper(root, ans);
		return ans;
	}
	
	private void subtreeNodes__helper(Node node, ArrayList<Node> ans) {
		if (node == null) return;
		ans.add(node);
		subtreeNodes__helper(node.left, ans);
		subtreeNodes__helper(node.right, ans);
	}
	
	public ArrayList<Point> search(Point r1, Point r2) {
		ArrayList<Point> ans = new ArrayList<>();
		search__helper(r1, r2, ans, r1.dim - 1, this.root, SPLIT_SEARCH);
		return ans;
	}
	
	private void search__helper(Point r1, Point r2, ArrayList<Point> ans, int dim, Node cur, int mode) {
		if (cur == null) return;
		
		// strange bugfix
		if (cur.isLeaf() && cur.p.coords[dim] != cur.value) {
			//System.out.println("FATAL: wanted " + cur.p.coords[dim] + ", got " + cur.value + "; coords" + Arrays.toString(cur.p.coords) + " (dim " + dim + ")");
			cur.value = cur.p.coords[dim];
		}
		
		switch (mode) {
		case SPLIT_SEARCH:
			if (r1.coords[dim] <= cur.value && cur.value <= r2.coords[dim]) {
				if (cur.isLeaf()) {
					if (dim == 0) {
						ans.add(cur.p);
					} else {
						search__helper(r1, r2, ans, dim - 1, cur, SPLIT_SEARCH);
					}
				} else {
					search__helper(r1, r2, ans, dim, cur.left, REPORT_LEFT);
					search__helper(r1, r2, ans, dim, cur.right, REPORT_RIGHT);
				}
			} else {
				if (cur.value < r1.coords[dim]) {
					search__helper(r1, r2, ans, dim, cur.right, SPLIT_SEARCH);
				} else {
					search__helper(r1, r2, ans, dim, cur.left, SPLIT_SEARCH);
				}
			}
			break;
		case REPORT_LEFT:
			if (r1.coords[dim] <= cur.value) {
				if (cur.isLeaf()) {
					if (dim == 0) {
						ans.add(cur.p);
					} else {
						search__helper(r1, r2, ans, dim - 1, cur, SPLIT_SEARCH);
					}
				} else {
					if (dim == 0) {
						subtreePoints__helper(cur.right, ans);
					} else {
						if (cur.hasRight()) {
							search__helper(r1, r2, ans, dim - 1, cur.right.tree, SPLIT_SEARCH);
						}
					}
				}
				search__helper(r1, r2, ans, dim, cur.left, REPORT_LEFT);
			} else {
				search__helper(r1, r2, ans, dim, cur.right, REPORT_LEFT);
			}
			break;
		case REPORT_RIGHT:
			if (cur.value <= r2.coords[dim]) {
				if (cur.isLeaf()) {
					if (dim == 0) {
						ans.add(cur.p);
					} else {
						search__helper(r1, r2, ans, dim - 1, cur, SPLIT_SEARCH);
					}
				} else {
					if (dim == 0) {
						subtreePoints__helper(cur.left, ans);
					} else {
						if (cur.hasLeft()) {
							search__helper(r1, r2, ans, dim - 1, cur.left.tree, SPLIT_SEARCH);
						}
					}
				}
				search__helper(r1, r2, ans, dim, cur.right, REPORT_RIGHT);
			} else {
				search__helper(r1, r2, ans, dim, cur.left, REPORT_RIGHT);
			}
			break;
		}
	}
	
	private Stack<Node> s = new Stack<>();
	private Scanner in = new Scanner(System.in);
	public void go() {
		Node current = root;
		if (!s.empty()) {
			current = s.peek();
		}
		if (current == null) {
			System.out.println("null");
		} else {
			System.out.println(current.value);
			if (current.hasLeft()) {
				System.out.println("left: " + current.left.value);
			}
			if (current.hasRight()) {
				System.out.println("right: " + current.right.value);
			}
		}
		switch (in.next()) {
		case "l":
			s.push(current.left);
			break;
		case "r":
			s.push(current.right);
			break;
		case "p":
			s.pop();
			break;
		case "t":
			s = new Stack<>();
			s.push(current.tree);
			break;
		case "exit":
			return;
		}
		go();
	}
}