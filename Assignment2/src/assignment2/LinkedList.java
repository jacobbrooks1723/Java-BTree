package assignment2;


public class LinkedList {
	
	String key;
	int frequency = 0;
	LinkedList next;
	
	public void add(String key){
		if(this.key == null){
			this.key = key;
			frequency++;
		}
		else if(this.key.equals(key)){
			frequency++;
		}
		else if(next == null){
			next = new LinkedList();
			next.add(key);
		}
		else{
			next.add(key);
		}
	}

	public LinkedList get(int i){
		if(i == 0){
			return this;
		}
		else if(next != null){
			return next.get(i-1);
		}
		else{
			return null;
		}
	}
	
	public int size(){
		return calculateSize(1);
	}
	
	private int calculateSize(int s){
		if(next == null){
			return s;
		}
		else{
			return next.calculateSize(s + 1);
		}
	}
	
	public void selectionSort(){
		if (next != null){
			LinkedList minNode = next.findMin();
			if (key.compareTo(minNode.key) > 0){
				String temp = key;
				key = minNode.key;
            	minNode.key = temp;
			}
			next.selectionSort();
		}
	}
	
	private LinkedList findMin(){
        LinkedList minNode;
        if (next == null){
            minNode = this;
        } 
        else {
            minNode = next.findMin();
            if (key.compareTo(minNode.key) < 0){
                minNode = this;
            }
        }
        return minNode;
    }
	
}
