package hr.unizg.fer;

import java.util.Scanner;

public class Main {
	/**
	 * @author Bojan
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		//Scanner reader = new Scanner(System.in);
		//String output=reader.next();
		//reader.close();
		//System.out.print(output);
		// komentar by bojan
		// moj komentar		
		
		Covjek bojan = new Covjek(178.35f, 35, "Bojan Lovrovic");
		
		System.out.println(bojan.mName);
	}
	/**
	 * @author Bojan
	 * @param
	 * @return
	 * This function takes two variables and multiplies them
	 */
	public static int count(int a, int b){
		return a*b;
		
	}

}