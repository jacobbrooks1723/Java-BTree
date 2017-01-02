package assignment2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Data {
	private RandomAccessFile data;
	private String userURL;
	private String[] categoryFileNames = new String[20];
	private Category[] categories = new Category[20];
	private Category userCategory;
	private ArrayList<Category> possibleCategories = new ArrayList<Category>();
	private ArrayList<String> topURLs = new ArrayList<String>();
	private String[] categoryURLs = {"https://en.wikipedia.org/wiki/Airplane", 
			"https://en.wikipedia.org/wiki/National_Football_League", 
			"https://en.wikipedia.org/wiki/Napoleonic_Wars",
			"https://en.wikipedia.org/wiki/Europa_(moon)",
			"https://en.wikipedia.org/wiki/Dance",
			"https://en.wikipedia.org/wiki/Tax_evasion",
			"https://en.wikipedia.org/wiki/Humboldt_University_of_Berlin",
			"https://en.wikipedia.org/wiki/Blood",
			"https://en.wikipedia.org/wiki/Paganism",
			"https://en.wikipedia.org/wiki/Monopoly_(game)",
			"https://en.wikipedia.org/wiki/September_11_attacks",
			"https://en.wikipedia.org/wiki/Euphoria",
			"https://en.wikipedia.org/wiki/Avatar_(2009_film)",
			"https://en.wikipedia.org/wiki/Alcubierre_drive",
			"https://en.wikipedia.org/wiki/Tornado",
			"https://en.wikipedia.org/wiki/Sea_turtle",
			"https://en.wikipedia.org/wiki/Hypnic_jerk",
			"https://en.wikipedia.org/wiki/Meiji_Restoration",
			"https://en.wikipedia.org/wiki/Patient_Protection_and_Affordable_Care_Act",
			"https://en.wikipedia.org/wiki/Data_compression"
	};
	
	public Data(String userURL) throws IOException{
		this.userURL = userURL;
		data = new RandomAccessFile(new File("user.txt"), "rw");
		data.setLength(0);
		userCategory = new Category("user.txt", userURL, true);
		createCategories();
		for(int i = 0; i < categories.length; i++){
			possibleCategories.add(categories[i]);
		}
	}
	
	public ArrayList<String> getTopURLs(){
		for(int i = 0; i < 10; i++){
			mostRelated();
		}
		return topURLs;
	}
	
	private void mostRelated(){
		int maxIndex = 0;
		double maxSimilarity = 0;
		for(int i = 0; i < possibleCategories.size(); i++){
			ArrayList<Entry> finalVectorUser = new ArrayList<Entry>();
			ArrayList<Entry> finalCategoryVector = new ArrayList<Entry>();
			processSets(finalVectorUser, finalCategoryVector, userCategory, possibleCategories.get(i));
			double d = cosineSimilarity(entriesToDoubles(finalVectorUser), entriesToDoubles(finalCategoryVector));
			if(d > maxSimilarity){
				maxSimilarity = d;
				maxIndex = i;
			}
		}
		topURLs.add(possibleCategories.get(maxIndex).rootURL);
		possibleCategories.remove(maxIndex);
	}
	
	private void processSets(ArrayList<Entry> userList, ArrayList<Entry> categoryList, Category user, Category c){
		ArrayList<Entry> smaller = whichArray("s", user.wordSet, c.wordSet);
		ArrayList<Entry> bigger = whichArray("b", user.wordSet, c.wordSet);
		for(int i = 0; i < smaller.size(); i++){
			if(hasEntry(smaller.get(i), bigger) > - 1){
				if(hasEntry(smaller.get(i), userList) < 0 && hasEntry(smaller.get(i), categoryList) < 0){
					userList.add(smaller.get(i));
					categoryList.add(bigger.get(hasEntry(smaller.get(i), bigger)));
				}
			}
		}
	}
	
	private int hasEntry(Entry e, ArrayList<Entry> bigger){
		for(int i = 0; i < bigger.size(); i++){
			if(e.key.equals(bigger.get(i).key)){
				return i;
			}
		}
		return -1;
	}
	
	private double[] entriesToDoubles(ArrayList<Entry> e){
		double[] d = new double[e.size()];
		for(int i = 0; i < e.size(); i++){
			int n = e.get(i).freq;
			String s = Integer.toString(n);
			double toDouble = Double.parseDouble(s);
			d[i] = toDouble;
		}
		return d;
	}
	
	private ArrayList<Entry> whichArray(String whichOne, ArrayList<Entry> user, ArrayList<Entry> category){
		if(whichOne.equals("s")){
			if(user.size() < category.size()){
				return user;
			}else{
				return category;
			}
		}else{
			if(user.size() > category.size()){
				return user;
			}else{
				return category;
			}
		}
	}
	
	private void createCategories() throws IOException{
		String prefix = "category";
		for(int i = 1; i < 21; i++){
			String s = Integer.toString(i);
			String fullName = prefix + s + ".txt";
			categoryFileNames[i - 1] = fullName;
		}
		for(int i = 0; i < categories.length; i++){
			categories[i] = new Category(categoryFileNames[i], categoryURLs[i], false);
		}
	}
	
	private double cosineSimilarity(double[] docVector1, double[] docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;
 
        for (int i = 0; i < docVector1.length; i++) {
            dotProduct += docVector1[i] * docVector2[i];  
            magnitude1 += Math.pow(docVector1[i], 2);  
            magnitude2 += Math.pow(docVector2[i], 2); 
        }
 
        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);
 
        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } 
        else {
            return 0.0;
        }
        return cosineSimilarity;
    }
	
}
