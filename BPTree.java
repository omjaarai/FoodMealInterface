package application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 * @author Ethan Root
 * @author Omjaa Rai
 * @author Leo Wu
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

	// Root of the tree
	private Node root;

	// Branching factor is the number of children nodes 
	// for internal nodes of the tree
	private int branchingFactor;


	/**
	 * Public constructor
	 * 
	 * @param branchingFactor 
	 */
	public BPTree(int branchingFactor) {
		if (branchingFactor <= 2) {
			throw new IllegalArgumentException(
					"Illegal branching factor: " + branchingFactor);
		}
		this.branchingFactor = branchingFactor;
		root = new LeafNode(null);
	}


	/**
	 * Inserts the key and value in the appropriate nodes in the tree
	 * 
	 * Note: key-value pairs with duplicate keys can be inserted into the tree.
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void insert(K key, V value) {
		//jumps to appropriate leaf node and inserts
		LeafNode targetLeaf = findApropLeaf(key);
		targetLeaf.insert(key, value);
		//deals with overflow if there is any
		if(targetLeaf.isOverflow()) {
			Node newRoot = targetLeaf.overFlowProcedure();
			if(newRoot != null) {
				root = newRoot;
			}
		}
		
	}


	/**
	 * Gets the values that satisfy the given range 
	 * search arguments.
	 * 
	 * Value of comparator can be one of these: 
	 * "<=", "==", ">="
	 * 
	 * Example:
	 *     If given key = 2.5 and comparator = ">=":
	 *         return all the values with the corresponding 
	 *      keys >= 2.5
	 *      
	 * If key is null or not found, return empty list.
	 * If comparator is null, empty, or not according
	 * to required form, return empty list.
	 * 
	 * @param key to be searched
	 * @param comparator is a string
	 * @return list of values that are the result of the 
	 * range search; if nothing found, return empty list
	 */
	@Override
	public List<V> rangeSearch(K key, String comparator) {
		if (!comparator.contentEquals(">=") && 
				!comparator.contentEquals("==") && 
				!comparator.contentEquals("<=") )
			return new ArrayList<V>();
		LeafNode curNode = findApropLeaf(key);
		List<V> retList = new ArrayList<V>();
		//== case
		if(comparator.equals("==")) {
			while(curNode!=null) {
				//loops through as many nodes as there are that contain the appropriate key
				//and adds the values with appropriate keys
				if(curNode.search(key) == -1) {
					return retList;
				}
				for(int i = curNode.keys.size()-1; i >= 0; i--) {
					if(curNode.keys.get(i).equals(key)) {
						retList.add(curNode.values.get(i));
					}
				}
				curNode = curNode.previous;
			}
		}
		//>= case
		else if(comparator.equals(">=")) {			
			LeafNode firstNode = curNode;
			//collects all values equal to key going left
			while(curNode != null) {
				if(curNode.search(key) == -1) {
					if(curNode.keys.get(curNode.keys.size()-1).compareTo(key) >= 0) {
						for(int i = 0; i < curNode.keys.size(); i++) {
							if(curNode.keys.get(i).compareTo(key) >= 0) {
								retList.add(curNode.values.get(i));
							}
						}
					}
					else {
						break;
					}

				}
				for(int i = curNode.keys.size()-1; i >= 0; i--) {
					if(curNode.keys.get(i).compareTo(key) >= 0) {
						retList.add(curNode.values.get(i));
					}
				}
				curNode = curNode.previous;
			}
			//resets to first node visited and collects all values larger than key going right
			curNode = firstNode.next;
			while(curNode != null) {
				for(int i = 0; i < curNode.keys.size(); i++) {
					retList.add(curNode.values.get(i));
				}
				curNode = curNode.next;
			}
		}
		//<= case
		else if(comparator.equals("<=")) {
			//starts at farthest right node with keys equal to our key. searches through it to add appropriate values
			for(int i = curNode.keys.size()-1; i >= 0; i--) {
				if(curNode.keys.get(i).compareTo(key) <= 0) {
					retList.add(curNode.values.get(i));
				}
			}
			//goes to the node that precedes current node and adds all values, loops this until the end of the leaf nodes
			curNode = curNode.previous;
			while(curNode!=null) {
				for(int i = curNode.keys.size()-1; i >= 0; i--) {
					retList.add(curNode.values.get(i));
				}
				curNode = curNode.previous;				
			}
		}
		return retList;
	}

	
	/**
	 * recursively uses search with key on internal nodes 
	 * until the leaf node in which key should be found is found.
	 * returns that leaf
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	private LeafNode findApropLeaf(K key) {
		Node node = root;
		
		if(root.isLeaf()) {
			return (LeafNode)root;
		}
		while(!node.isLeaf()) {
			node = ((InternalNode)node).children.get(node.search(key));
		}
		return (LeafNode)node;
	}
	

	/**
	 * Returns a string representation for the tree
	 * This method is provided to students in the implementation.
	 * @return a string representation
	 */
	@Override
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof BPTree.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else {
					sb.append('\n');
				}
			}
			queue = nextQueue;
		}
		return sb.toString();
	}



	/**
	 * This abstract class represents any type of node in the tree
	 * This class is a super class of the LeafNode and InternalNode types.
	 * 
	 * @author sapan
	 */
	private abstract class Node {
		// List of keys
		List<K> keys;
		 //parent of current node
		InternalNode parent;

		/**
		 * Package constructor
		 */
		Node(InternalNode p) {
			keys = new ArrayList<K>();
			parent = p;
		}

		/**returns whether the node is a leaf node
		 * 
		 */
		abstract boolean isLeaf();

		/**
		 * Inserts key and value in the appropriate leaf node 
		 * and balances the tree if required by splitting
		 *  
		 * @param key
		 * @param value
		 */
		abstract void insert(K key, V value);

		/**
		 * Gets the first leaf key of the tree
		 * 
		 * @return key
		 */
		abstract K getFirstLeafKey();
		
		/**
		 * returns parent of current node
		 */
		abstract InternalNode getParent();

		/**
		 * Gets the new sibling created after splitting the node
		 * 
		 * @return Node
		 */
		abstract Node split();
		
		/**
		 * searches the keys of this node, finding the index in children or values, or returning -1
		 * @param key
		 * @return
		 */
		abstract int search(K key);
		


		/*
		 * (non-Javadoc)
		 * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
		 */
		abstract List<V> rangeSearch(K key, String comparator);

		/**returns whether the node has too many keys
		 * 
		 * @return boolean
		 */
		abstract boolean isOverflow();
		
		/**
		 * upwards-recursive method to deal with overflow
		 * @return a new root if there is one
		 */
		@SuppressWarnings("unchecked")
		Node overFlowProcedure() {
			int midInd = keys.size()/2;
			K key = keys.get(midInd);
			Node newNode = split();
			//extra code for if overFlowProcedure is being called on the root
			if(getParent() == null) {
				parent = new InternalNode(null);
			}
			//helps set up the second node's fields for parent and previous and next if the node is a leaf
			newNode.parent = parent;
			if(newNode.isLeaf()) {
				((LeafNode)newNode).previous = (LeafNode)this;
				((LeafNode)newNode).next = ((LeafNode)this).next;
				
				if(((LeafNode)this).next != null) {
					((LeafNode)this).next.previous = (LeafNode)newNode;
				}
				((LeafNode)this).next = (LeafNode)newNode;
			}
			//now inserts a new key and pointers for this node and it's new sibling into it's parent
			return this.getParent().upKey(key, this, newNode);
		}
		
		

		public String toString() {
			return keys.toString();
		}
		
		

	} // End of abstract class Node

	/**
	 * This class represents an internal node of the tree.
	 * This class is a concrete sub class of the abstract Node class
	 * and provides implementation of the operations
	 * required for internal (non-leaf) nodes.
	 * 
	 * @author sapan
	 */
	private class InternalNode extends Node {

		// List of children nodes
		List<Node> children;

		/**
		 * Package constructor
		 */
		InternalNode(InternalNode p) {
			super(p);
			children = new ArrayList<Node>();
		}

		boolean isLeaf() {
			return false;
		}

		/**
		 * recursive method to get the first leaf key of this inner node
		 */
		K getFirstLeafKey() {
			if(children.size() != 0) {
				return children.get(0).getFirstLeafKey();
			}
			else  return null;
		}

		/**
		 * returns true if current node is overflowing
		 */
		boolean isOverflow() {
			return keys.size() >= branchingFactor;
		}
		
		/**sets up a new key and children in the current node, and checks for overflow
		 * 
		 * @param key
		 * @param left
		 * @param right
		 * @return either the current node's parent or the result of calling overflowProcedure on this node
		 */
		Node upKey(K key, Node left, Node right) {
			//inserts new key at appropriate position
			int ind = search(key); 
			putAt(ind, key, left, right);
			//deals with overflow, recursing if necessary
			if(isOverflow()) {
				return overFlowProcedure();
			}
			//this part is confusing but basically if a new root has been created
			//it'll return a pointer to it to be set as the new root
			//otherwise it'll return null
			else {
				if(this.getParent() == null) {
					return this;
				}
				else {
					return null;
				}
			}
		}
		
		/**inserts a key and it's left and right children into a given index in the current node
		 * 
		 * @param ind
		 * @param key
		 * @param left
		 * @param right
		 */
		void putAt(int ind, K key, Node left, Node right) { // ind is the index of the second left most key (the index of the left most children) that meet our criteria
			//inserts key in correct place
			if(keys.size() <= ind) {
				keys.add(key);
			}
			else {
				keys.add(ind, key);
			}
			//removes old child and inserts new left child and right child
			if (keys.size() <= ind) {
				if(children.size() != 0) {
					children.remove(ind);
					children.add(ind, left);
					children.add(ind+1, right);
				}
				else {
					children.add(left);
					children.add(right);
				}
			}
			else {
				if(children.size() != 0) {
					children.remove(ind);
					children.add(ind, left);
					children.add(ind+1, right);
				}
				else {
					children.add(left);
					children.add(right);
				}
			}
			left.parent = this;
			right.parent = this;
		}
		
		/**
		 * returns parent of current node
		 */
		InternalNode getParent() {
			return parent;
		}
		
		/**iterates through keys to get the appropriate child pointer where key would be contained
		 * @return the index in children at which key should be found, farthest left if there are duplicates
		 */
		int search(K key) {
			int i;
			for(i = 0; i < keys.size(); i++) {		
				if(key.compareTo(keys.get(i)) < 0) {
					return i;
				}	
			}
			return i;
		}
					
		
		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		void insert(K key, V value) {
			boolean placeFound = false;
			for(int i = 0; i < keys.size(); i++) {
				if(!placeFound) {
					if(key.compareTo(keys.get(i)) < 0) {
						children.get(i).insert(key, value);
						placeFound = true;
					}
				}
			}
			if(!placeFound) {
				if(children.isEmpty()) {
					children.add(new LeafNode(this));
				}
				children.get(children.size()-1).insert(key, value);
			}
		}

		/**
		 * splits current node and returns it's new right sibling
		 */
		Node split() {
			int midInd = keys.size()/2;
			InternalNode secondNode = new InternalNode(getParent());
			
			//moves keys over to secondnode and removes them from left node
			for(int i = midInd+1; i < keys.size(); i++) {
				secondNode.keys.add(keys.get(i));
			}
			for(int i = keys.size()-1; i >= midInd; i--) {
				keys.remove(i);
			}
			//moves children over to secondnode and removes then from left node
			for(int i = midInd+1; i < children.size(); i++) {
				children.get(i).parent = secondNode;
				secondNode.children.add(children.get(i));
			}
			for(int i = children.size()-1; i > midInd; i--) {
				children.remove(i);
			}
			
			return secondNode;
		}

		/**
		 * this method is skipped in our implementation. instead findApropLeaf is used 
		 * to get to the correct leaf node to begin
		 */
		List<V> rangeSearch(K key, String comparator) {
			return null;
		}

	} // End of class InternalNode


	/**
	 * This class represents a leaf node of the tree.
	 * This class is a concrete sub class of the abstract Node class
	 * and provides implementation of the operations that
	 * required for leaf nodes.
	 * 
	 * @author sapan
	 */
	private class LeafNode extends Node {

		// List of values
		List<V> values;

		// Reference to the next leaf node
		LeafNode next;

		// Reference to the previous leaf node
		LeafNode previous;

		/**
		 * Package constructor
		 */
		LeafNode(InternalNode p) {
			super(p);
			values = new ArrayList<V>();
			next = null;
			previous = null;
		}

		//helper method for certain methods in Node and outside of the node classes
		boolean isLeaf() {
			return true;
		}

		/**
		 * returns leftmost key
		 */
		K getFirstLeafKey() {
			if(keys.size() != 0) {
				return keys.get(0);
			}
			else return null;
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			return values.size() >= branchingFactor;
		}
		
		/**
		 * returns parent of current node
		 */
		InternalNode getParent() {
			return parent;
		}
		
		/**iterates through the keys in this node, and returns the index of the first instance of the key if it exists
		 * if key cannot be found, returns -1
		 */
		int search(K key) {
			int i;
			for(i = 0; i < keys.size(); i++) {
				if(key.equals(keys.get(i))) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * inserts a key value pair into the correct position in this node
		 */
		void insert(K key, V value) {
			boolean placeFound = false;
			for(int i=0; i< keys.size();i++) {
				if(!placeFound) {
					if(key.compareTo(keys.get(i))<=0) {
						placeFound=true;
						keys.add(i,key);
						values.add(i,value);
					}
				}
			}
			if(!placeFound) {
				keys.add(key);
				values.add(value);
			}
		}

		/**
		 * splits current node in half and returns it's new sibling
		 */
		Node split() {			
			int midInd = keys.size()/2;
			LeafNode secondNode = new LeafNode(getParent());
			for(int i = midInd; i < keys.size(); i++) {
				secondNode.keys.add(keys.get(i));
				secondNode.values.add(values.get(i));
			}
			for(int i = keys.size()-1; i >= midInd; i--) {
				keys.remove(i);
				values.remove(i);
			}
			return secondNode;
		}

		/**
		 * this method is not used in our implementation, instead check the rangeSearch at the top of BPTree
		 */
		List<V> rangeSearch(K key, String comparator) {
			return null;
		}
	} // End of class LeafNode


	/**
	 * Contains a basic test scenario for a BPTree instance.
	 * It shows a simple example of the use of this class
	 * and its related types.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create empty BPTree with branching factor of 3
		BPTree<Double, Double> bpTree = new BPTree<>(3);

		// create a pseudo random number generator
		Random rnd1 = new Random();

		// some value to add to the BPTree
		Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d, 0.7d, 0.3d};

		// build an ArrayList of those value and add to BPTree also
		// allows for comparing the contents of the ArrayList 
		// against the contents and functionality of the BPTree
		// does not ensure BPTree is implemented correctly
		// just that it functions as a data structure with
		// insert, rangeSearch, and toString() working.
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			Double j = dd[rnd1.nextInt(6)];
			list.add(j);
			bpTree.insert(j, j);
			System.out.println("\n\nTree structure:\n" + bpTree.toString());
		}
		List<Double> filteredValues = bpTree.rangeSearch(0.2d, "<=");
		System.out.println("Filtered values: " + filteredValues.toString());
	}

} // End of class BPTree

