package assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class BTree {
	RandomAccessFile data;
	int maxChildren = 34;
	int nodeSize = (maxChildren * 64) + (maxChildren * 4) + (maxChildren * 8) + 9;
	String sentinalKey = pad("zzzzzzzzzzzz");
	ArrayList<Entry> wordList = new ArrayList<Entry>();

	public BTree(String fileName){
		try {
			data = new RandomAccessFile(new File(fileName), "rw");
		} catch (FileNotFoundException e) {
			e.getStackTrace();
		}
	}
	
	public ArrayList<Entry> getList() throws IOException{
		toList(0);
		return wordList;
	}
	
	public void traverse(long pos) throws IOException {
		Node n = new Node();
		Node n1 = n.readNode(data, pos);
		for(int i = 0; i < n1.entries.size() / 2; i++){
			if(n1.entries.get(i).child != -1){
				traverse(n1.entries.get(i).child);
			}
			else{
				break;
			}
		}
		System.out.println();
		for(Entry e : n1.entries){
			System.out.print(e.key + ", " + e.freq + ", ");
		}
		for(int i = n1.entries.size()/2; i < n1.entries.size(); i++){
			if(n1.entries.get(i).child != -1){
				traverse(n1.entries.get(i).child);
			}
			else{
				break;
			}
		}
	}
	
	private void toList(long pos) throws IOException{
		Node n = new Node();
		Node n1 = n.readNode(data, pos);
		for(int i = 0; i < n1.entries.size() / 2; i++){
			if(n1.entries.get(i).child != -1){
				toList(n1.entries.get(i).child);
			}
			else{
				break;
			}
		}
		for(Entry e : n1.entries){
			wordList.add(e);
		}
		for(int i = n1.entries.size()/2; i < n1.entries.size(); i++){
			if(n1.entries.get(i).child != -1){
				toList(n1.entries.get(i).child);
			}
			else{
				break;
			}
		}
	}
	
	private String pad(String key){
		String s = key;
		byte[] array = s.getBytes();
		byte[] space = new byte[64];
		for (int i = 0; i < array.length; i++) {
			space[i] = array[i];
		}
		for (int i = array.length; i < space.length; i++) {
			byte b1 = 1;
			space[i] = b1;
		}
		String finalKey = new String(space);
		return finalKey;
	}

	public void insert(String key) throws IOException {
		hiddenInsert(pad(key), 0);
	}

	private void hiddenInsert(String key, long pos) throws IOException {
		if (data.length() > 0) {
			Node n = new Node();
			Node r = n.readNode(data, pos);
			r.id = pos;
			boolean hasAlready = false;
			for (Entry e : r.entries) {
				if (e.key.equals(key)) {
					e.freq++;
					r.writeNode(data);
					hasAlready = true;			
				}
			}
			if (!hasAlready) {
				if (!hasChildren(r)) {
					if (r.entries.size() < maxChildren - 2) {
						Entry e = new Entry();
						e.key = key;
						e.freq++;
						r.entries.add(e);
						r.sort();
						r.writeNode(data);
					} else if (r.isRoot) {
						rootSplitNoChildren(key);		
					} else if (!r.isRoot) {
						leafSplit(key, r);
						Node preUpdate = new Node();
						Node update = preUpdate.readNode(data, r.id);
						Node preParent = new Node();
						Node parent = preParent.readNode(data, update.parent);
						parent.id = update.parent;
						if (parent.entries.size() == maxChildren) {
							interiorSplit(parent);		
						}
					}
				} else {
					passDown(key, r);
				}
			}
		} else {
			Node n = new Node();
			Entry e = new Entry();
			e.key = key;
			e.freq++;
			n.entries.add(e);
			n.sort();
			n.writeNode(data);	
		}
	}
	
	public long search(String key, long pos) throws IOException{
		Node preNode = new Node();
		Node node = preNode.readNode(data, pos);
		for(Entry e : node.entries){
			if(e.key.equals(key)){
				return pos;
			}
		}
		for(Entry e : node.entries){
			if(key.compareTo(e.key) < 0){
				if(e.child != -1){
					return search(key, e.child);
				}
			}
		}
		return -1;
	}
	
	private void passDown(String key, Node n) throws IOException {
		for (Entry e : n.entries) {
			if (key.compareTo(e.key) < 0) {
				hiddenInsert(key, e.child);
				break;
			}
		}
	}

	private boolean hasChildren(Node n) {
		for (Entry e : n.entries) {
			if (e.child > -1) {
				return true;
			}
		}
		return false;
	}

	private int targetParent(Node p, long id) throws IOException {
		int n = -1;
		for (int i = 0; i < p.entries.size(); i++) {
			if (p.entries.get(i).child == id) {
				n = i;
			}
		}
		return n;
	}

	private void interiorSplit(Node n) throws IOException {
		long fileSize = data.length();
		
		Node preParent = new Node();
		Node parent = preParent.readNode(data, n.parent);
		parent.id = n.parent;

		long sentinalChild = n.entries.get((maxChildren - 2) / 2).child;
		Entry median = n.entries.get((maxChildren - 2) / 2);
		median.child = n.id;
		parent.entries.get(targetParent(parent, n.id)).child = fileSize;
		parent.entries.add(median);
		parent.sort();

		Node left = new Node();
		left.id = n.id;
		left.parent = n.parent;
		left.isRoot = false;
		for (int i = 0; i < (maxChildren - 2) / 2; i++) {
			left.entries.add(n.entries.get(i));
		}
		Entry sentinal = new Entry();
		sentinal.key = sentinalKey;
		sentinal.child = sentinalChild;
		left.entries.add(sentinal);

		Node right = new Node();
		right.id = fileSize;
		right.parent = n.parent;
		right.isRoot = false;
		for (int i = maxChildren / 2; i < maxChildren; i++) {
			right.entries.add(n.entries.get(i));
		}
		for (Entry e : right.entries) {
			Node preChild = new Node();
			Node child = preChild.readNode(data, e.child);
			child.id = e.child;
			child.parent = fileSize;
			child.writeNode(data);
		}

		right.writeNode(data);
		parent.writeNode(data);
		left.writeNode(data);

		Node preParent2 = new Node();
		Node parent2 = preParent2.readNode(data, n.parent);
		parent2.id = n.parent;

		if (parent2.entries.size() == maxChildren) {
			if (parent2.isRoot) {
				rootSplitWithChildren();
				
			} else {
				interiorSplit(parent2);			
			}
		}	
	}

	private void leafSplit(String key, Node n) throws IOException {
		Entry e = new Entry();
		e.key = key;
		e.freq++;
		n.entries.add(e);
		n.sort();
		
		long fileSize = data.length();

		Node preParent = new Node();
		Node parent = preParent.readNode(data, n.parent);
		parent.id = n.parent;

		Entry median = n.entries.get((n.entries.size() - 1) / 2);
		median.child = n.id;
		parent.entries.get(targetParent(parent, n.id)).child = fileSize;
		parent.entries.add(median);
		parent.sort();

		Node right = new Node();
		right.id = fileSize;
		right.isRoot = false;
		right.parent = n.parent;
		for (int i = (n.entries.size() + 1) / 2; i < n.entries.size(); i++) {
			right.entries.add(n.entries.get(i));
		}

		Node left = new Node();
		left.id = n.id;
		left.isRoot = false;
		left.parent = n.parent;
		for (int i = 0; i < (n.entries.size() - 1) / 2; i++) {
			left.entries.add(n.entries.get(i));
		}
		parent.writeNode(data);
		left.writeNode(data);
		right.writeNode(data);

		Node preRoot = new Node();
		Node root = preRoot.readNode(data, 0);
		if (root.entries.size() == maxChildren && root.entries.get(root.entries.size() - 1).key.equals(sentinalKey)) {
			rootSplitWithChildren();	
		}
	}

	private void rootSplitWithChildren() throws IOException {
		Node preRoot = new Node();
		Node root = preRoot.readNode(data, 0);

		Entry median = root.entries.get((maxChildren - 2) / 2);
		long sentChildPointer = root.entries.get((maxChildren - 2) / 2).child;
		median.child = data.length();

		Entry sentinal = new Entry();
		sentinal.key = sentinalKey;
		sentinal.child = data.length() + nodeSize;

		Node left = new Node();
		left.parent = 0;
		left.id = data.length();
		left.isRoot = false;
		for (int i = 0; i < (maxChildren - 2) / 2; i++) {
			left.entries.add(root.entries.get(i));
			long childPointer = root.entries.get(i).child;
			Node preChild = new Node();
			Node child = preChild.readNode(data, childPointer);
			child.id = childPointer;
			child.parent = left.id;
			child.writeNode(data);
		}
		Entry leftSentinal = new Entry();
		leftSentinal.key = sentinalKey;
		Node preChild = new Node();
		Node child = preChild.readNode(data, sentChildPointer);
		child.id = sentChildPointer;
		child.parent = left.id;
		child.writeNode(data);
		leftSentinal.child = sentChildPointer;
		left.entries.add(leftSentinal);

		Node right = new Node();
		right.parent = 0;
		right.id = data.length() + nodeSize;
		right.isRoot = false;
		for (int i = maxChildren / 2; i < maxChildren; i++) {
			long rightChildPointer = root.entries.get(i).child;
			Node preRightChild = new Node();
			Node rightChild = preRightChild.readNode(data, rightChildPointer);
			rightChild.id = rightChildPointer;
			rightChild.parent = right.id;
			rightChild.writeNode(data);
			right.entries.add(root.entries.get(i));
		}

		root.entries = new ArrayList<Entry>();
		root.entries.add(median);
		root.entries.add(sentinal);

		root.writeNode(data);
		left.writeNode(data);
		right.writeNode(data);
	}

	private void rootSplitNoChildren(String key) throws IOException {
		Entry e = new Entry();
		e.key = key;
		e.freq++;

		Node n = new Node();
		Node r = n.readNode(data, 0);

		r.entries.add(e);
		r.sort();

		Entry median = r.entries.get((r.entries.size() - 1) / 2);
		median.child = nodeSize;

		Entry sentinal = new Entry();
		sentinal.key = sentinalKey;
		sentinal.child = nodeSize * 2;

		Node left = new Node();
		left.isRoot = false;
		left.parent = r.id;
		left.id = nodeSize;
		for (int i = 0; i < (r.entries.size() - 1) / 2; i++) {
			left.entries.add(r.entries.get(i));
		}

		Node right = new Node();
		right.isRoot = false;
		right.parent = r.id;
		right.id = nodeSize * 2;
		for (int i = (r.entries.size() + 1) / 2; i < r.entries.size(); i++) {
			right.entries.add(r.entries.get(i));
		}

		r.entries = new ArrayList<Entry>();
		r.entries.add(median);
		r.entries.add(sentinal);
		r.sort();

		r.writeNode(data);
		left.writeNode(data);
		right.writeNode(data);	
	}

}
