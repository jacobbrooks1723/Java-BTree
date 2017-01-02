package assignment2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Node {
	ArrayList<Entry> entries = new ArrayList<Entry>();
	int maxChildren = 34;
	long id = 0;
	long parent = -1;
	boolean isRoot = true; 
	
	public Node readNode(RandomAccessFile file, long pos) throws IOException{
		file.seek(pos);
		Node n = new Node();
		byte[] b = new byte[64 * maxChildren];
		file.read(b);
		
		int arrayPointer = 0;
		while(arrayPointer < maxChildren * 64){
			byte[] word = new byte[64];
			for(int i = 0; i < word.length; i++){
				word[i] = b[arrayPointer + i];
			}
			String s = new String(word);
			char[] test = s.toCharArray();
			if(Character.isLetterOrDigit(test[0]) && Character.isDefined(test[0])){
				Entry e = new Entry();
				e.key = s;
				n.entries.add(e);
			}
			arrayPointer += 64;
		}
		
		long intPointer = pos + (64 * maxChildren);
		file.seek(intPointer);
		for(int i = 0; i < n.entries.size(); i++){
			int k = file.readInt();
			n.entries.get(i).freq = k;
		}
		
		long childPointer = pos + (64 * maxChildren) + (4 * maxChildren);
		file.seek(childPointer);
		for(int i = 0; i < n.entries.size(); i++){
			n.entries.get(i).child = file.readLong();
		}
		
		long parentPointer = pos + (64 * maxChildren) + (4 * maxChildren) + (8 * maxChildren);
		file.seek(parentPointer);
		n.parent = file.readLong();
		n.isRoot = file.readBoolean();  
		
		return n; 
	}
	

	public void writeNode(RandomAccessFile file) {
		try {
			file.seek(id);
			for(Entry e : entries){
				if(e.key != null){
					byte[] b = e.key.getBytes();
					file.write(b);
				}
			}
			for(int i = 0; i < (maxChildren * 64) - (entries.size() * 64); i++){
				file.writeByte(1);
			}
			for(Entry e: entries){
				if(e.key != null){
					file.writeInt(e.freq);
				}
			}
			for(int i = 0; i < (maxChildren * 4) - (entries.size() * 4); i++){
				file.writeByte(1);
			}
			for(Entry e: entries){
				file.writeLong(e.child);
			}
			for(int i = 0; i < (maxChildren * 8) - (entries.size() * 8); i++){
				file.writeByte(1);
			}
			file.writeLong(parent);
			file.writeBoolean(isRoot);
		}catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void sort() {
		Entry[] e = toEntryArray(entries);
		entrySelectionSort(e, 0);
		for (int i = 0; i < entries.size(); i++) {
			entries.set(i, e[i]);
		}
	}
	
	private Entry[] entrySelectionSort(Entry[] nums, int count) {
		int minIndex = minIndex(nums, count);
		Entry minVal = nums[minIndex];
		Entry temp = nums[count];

		nums[minIndex] = temp;
		nums[count] = minVal;
		count++;

		if (count < nums.length) {
			return entrySelectionSort(nums, count);
		} else {
			return nums;
		}
	}

	private int minIndex(Entry[] n, int count) {
		String currentMin = n[count].key;
		int minIndex = count;
		for (int i = count; i < n.length; i++) {
			if (currentMin.compareTo(n[i].key) > 0) {
				currentMin = n[i].key;
				minIndex = i;
			}
		}
		return minIndex;
	}

	private Entry[] toEntryArray(ArrayList<Entry> entries) {
		Entry[] e = new Entry[entries.size()];
		for (int i = 0; i < e.length; i++) {
			e[i] = entries.get(i);
		}
		return e;
	}

}


