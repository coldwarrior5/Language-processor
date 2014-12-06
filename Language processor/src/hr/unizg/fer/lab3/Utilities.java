package hr.unizg.fer.lab3;

import java.util.Scanner;

class UniformniZnak{
	String mNaziv;
	int mRedak;
	String mLeksickaJedinka;
	
	public UniformniZnak(String linija){
		linija += " "; // lakse je onda rascjepati liniju
		mNaziv = linija.substring(0, linija.indexOf(' '));
		mRedak = Integer.parseInt(linija.substring(linija.indexOf(' '), linija.indexOf(' ', 1)));
		mLeksickaJedinka = linija.substring(linija.indexOf(' ', 1), linija.indexOf(' ', 2));
	}
}

public class Utilities {
	
	public static String ReadStringFromInput(){
		String input = "";
		Scanner scIn = new Scanner(System.in);
		while(true){
			
			//if(scIn.hasNext()==false){		//This works in terminal
			//	break;						//It expects the stdin to close
			//}
			
			String read = scIn.nextLine();	//Everything else looks normal
			
			if(read.equals("")){			//This works in Eclipse
				break;						//It expects for user to type another enter
			}

 
			
			input += read + "\n";
			
		}
		scIn.close();
		return input;
	}
	
	public static void WriteStringLineToOutput(String str){
		System.out.println(str);
	}
}
