package assignment2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException{
		BTree b = new BTree("category4.txt");
		for(Entry e: b.getList()){
			if(e.freq == 0){
				System.out.println(e.key + ", " + e.freq);
			}
		}
	}
	
	static int exponentialFactorial(int n){
		if(n == 0)
			return 1;
		return powerOf(n, exponentialFactorial(n-1));
	}
	
	static int factorial(int n){
		if(n == 1)
			return 1;
		return n * factorial(n-1);
	}
	
	static int powerOf(int b, int e){
		if(e == 1)
			return b;
		return b * powerOf(b, e-1);
	}
}
	