package assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Category {
	String fileName;
	BTree b;
	HashTable URLs = new HashTable();
	ArrayList<Entry> wordSet;
	String rootURL;
	
	public Category(String fileName, String rootURL, boolean shouldParse) throws IOException {
		b = new BTree(fileName);
		this.fileName = fileName;
		this.rootURL = rootURL;
		if(shouldParse){
			wikipediaParse(rootURL, 0);
		}
		wordSet = b.getList();
	}

	private void wikipediaParse(String rootURL, int parseCounter) throws IOException {
		ArrayList<String> urlList = new ArrayList<String>();
		Document doc = Jsoup.connect(rootURL).get();
		String text = doc.body().text();
		Scanner stringScanner = new Scanner(text);
		while (stringScanner.hasNext()) {
			String s = stringScanner.next();
			if (isUnicode(s) && !tooBig(s)) {
				b.insert(s);
			}
		}
		Elements links = doc.select("p");
		for (Element e : links) {
			Elements anchor = e.select("a");
			for (Element e1 : anchor) {
				if (e1.hasAttr("href")) {
					String URL = e1.attr("href");
					if (URL.startsWith("/wiki")) {
						String finalURL = "https://en.wikipedia.org" + URL;
						if (URLs.getArray()[URLs.hashFunction(finalURL)] == null) {
							urlList.add(finalURL);
						}
					}
				}
			}
		}
		if (parseCounter < 2) {
			parseCounter++;
			if(urlList.size() >= 10){
				for (int i = 0; i < 10; i++) {
					URLs.insert(urlList.get(i));
					wikipediaParse(urlList.get(i), parseCounter);
				}
			}else{
				for (int i = 0; i < urlList.size(); i++) {
					URLs.insert(urlList.get(i));
					wikipediaParse(urlList.get(i), parseCounter);
				}
			}	
		}
	}

	private boolean tooBig(String key){
		byte[] b = key.getBytes();
		if(b.length > 64){
			return true;
		}
		return false;
	}

	private boolean isUnicode(String s) {
		boolean result = true;
		char[] characters = s.toCharArray();
		for (char c : characters) {
			if (Character.isLetterOrDigit(c) && Character.isDefined(c)) {
				result = true;
			} else {
				return false;
			}
		}
		return result;
	}

}
