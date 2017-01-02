package assignment2;

public class HashTable {
	
	private LinkedList[] links = new LinkedList[2503];
	
	public HashTable(){
		for(int i = 0; i < links.length; i++){
			links[i] = new LinkedList();
		}
	}
	
	public int hashFunction(String key){
		int value = key.charAt(0);
		for(int i = 1; i < key.length(); i++){
			value = (value % 127) * key.charAt(i);
		}
		return value % links.length;
	}
	
	public int find(String key){
		return hashFunction(key);
	}
	
	public void insert(String key){
		links[hashFunction(key)].add(key);
	}
	
	public LinkedList[] getArray(){
		return links;
	}
}

