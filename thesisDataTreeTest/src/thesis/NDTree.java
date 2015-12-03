package thesis;

import java.util.Vector;

/**
 * 
 * An attempt at a dynamic sized data tree.
 * 
 * @author Jordan Parsons
 * @author Michael Jeffers
 * @version 0.1
 * @param <T>
 *            the type of the children in the tree.
 */
public class NDTree<DataType extends Comparable<DataType>> {
	private Node<DataType> root;
	int nCount;

	/**
	 * 
	 * Data tree node, stores: data, parent node ID, and child nodes
	 *
	 * @param <T> the type of data stored in the node.
	 */
	public static class Node<T extends Comparable<T>> implements Comparable<T> {
		Vector<Node<T>> children;
		T data;
		int ID;
		int pID;
		/**
		 * Basic constructor, requires an ID, parent ID, and data.
		 * 
		 * @param id node ID number.
		 * @param pid ID number of the parent node.
		 * @param newData data to be added at the node.
		 */
		Node(int id, int pid, T newData) {
			this.data = newData;
			this.ID = id;
			this.pID = pid;
			children = new Vector<Node<T>>();
		}
		
		/**
		 * Add a child node to a given node.
		 * @param n node to add as a child of this node.
		 */
		public void addChild(Node<T> n) {
			this.children.add(n);
		}

		@Override
		public int compareTo(T arg0) {
			// shorter gaps to the top of a list, making a search faster
			return 0;
		}
	}

	/**
	 * Basic Constructor
	 */
	public NDTree() {
		this.setRoot(null);
		nCount = 0;
	}

	/**
	 * Constructor with data.
	 * @param data Data to start first node out with.
	 */
	public NDTree(DataType data) {
		this.setRoot(new Node<DataType>(1, 0, data));
		nCount = 1;
	}

	/**
	 * Prints out the tree list.
	 * @return Returns a string of the tree structure.
	 */
	public String getTreeOutline() {
		return ""+getTreePrint(root, 1);
	}

	/**
	 * Recursive Function to calculate the tree structure.
	 * @param node Node to check.
	 * @param lvl Depth in the tree
	 * @return Return the structure of that node, and its children.
	 */
	private String getTreePrint(Node<DataType> node, int lvl) {
		String s = node.ID + " has kids: \n";

		for (int i = 0; i < lvl; i++) {
			s += "-";
		}

		if (node.children.size() == 0)
			s += "None \n";

		for (Node<DataType> n : node.children) {
			s += getTreePrint(n, lvl + 1);
		}
		return s;
	}

	/**
	 * Insert an item into the tree, as a child of a certain node.
	 * @param pID Node to insert as a child of.
	 * @param data Data to insert.
	 */
	public void insert(int pID, DataType data) {
		nCount++;
		this.root = insert(pID, this.root, data);

		System.out.println("added a kid as node id: " + nCount + " to parent: " + pID);// this.lookupAndReturn(data).pID
	}

	/**
	 * Recursive function to do the insertion.
	 * @param pID Node to insert as a child of.
	 * @param node Node to check.
	 * @param data Data to insert.
	 * @return Return the node with the data inserted.
	 */
	private Node<DataType> insert(int pID, Node<DataType> node, DataType data) {
		// If the ID matches, add a child
		if (node.ID == pID) {
			node.addChild(new Node<DataType>(nCount, pID, data));
			// p.println("Add a kid");
		} else {
			// Go through every kid & recurse until you can add one, or if it
			// can't
			// be added, no dice.
			for (Node<DataType> n : node.children) {
				n = insert(pID, n, data);
			}

			// If the ID does not match & there are no children, this is not the
			// branch you are looking for, return null
			if (node.ID != pID && node.children.size() == 0) {
				System.out.println("Not added");
				return node;
			}

		}
		return node;
	}

	public boolean lookup(DataType data) {
		return lookup(this.root, data);
	}

	private boolean lookup(Node<DataType> node, DataType data) {
		if (data.equals(node.data))
			return true;

		for (Node<DataType> n : node.children)
			if (lookup(n, data) == false)
				continue;
			else
				return true;

		return false;
	}

	public Node<DataType> lookupAndReturn(DataType data) {
		return lookupAndReturn(this.root, data);
	}

	private Node<DataType> lookupAndReturn(Node<DataType> node, DataType data) {
		if (node == null)
			return null;

		if (data.equals(node.data))
			return node;

		for (Node<DataType> n : node.children)
			if (lookupAndReturn(n, data) == null)
				continue;
			else
				return lookupAndReturn(n, data);

		return null;
	}

	public Node<DataType> getRoot() {
		return root;
	}

	public void setRoot(Node<DataType> root) {
		this.root = root;
	}

}
