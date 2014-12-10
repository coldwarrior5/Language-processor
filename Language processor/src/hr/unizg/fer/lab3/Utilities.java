package hr.unizg.fer.lab3;

import java.util.Scanner;

class UniformniZnak{
	String mNaziv;
	int mRedak;
	String mLeksickaJedinka;
	
	public UniformniZnak(String linija){
		linija += " "; // lakse je ovako rascjepati liniju
		mNaziv = linija.substring(0, linija.indexOf(' '));
		mRedak = Integer.parseInt(linija.substring(linija.indexOf(' '), linija.indexOf(' ', 1)));
		mLeksickaJedinka = linija.substring(linija.indexOf(' ', 1), linija.indexOf(' ', 2));
	}
	
	public String FormatZaIspis(){
		return mNaziv + "(" + mRedak + "," + mLeksickaJedinka + ")";
	}
	
	// Vraca null ako input parametar linija ne zadovoljava oblik uniformnog znaka.
	public static UniformniZnak SigurnoStvaranje(String linija){
		try{
			UniformniZnak novi = new UniformniZnak(linija);
			return novi;
		}catch(IndexOutOfBoundsException | NumberFormatException e){
			return null;
		}
	}
}

public class Utilities {
	
	// l --> d
	public static Boolean ImplicitnaPretvorbaMoguca(Tip l, Tip d){
		if (l == Tip._char && (d == Tip._int || d == Tip._char)) return true;
		if (l == Tip._int && d == Tip._int) return true;
		return false;
	}
	
	public static Boolean JeBrojevniTip(Tip a){
		if (a == Tip._char || a == Tip._int) return true;
		else return false;
	}
	
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

	public static boolean ProvjeriInt(String in){
		try{
			Integer.parseInt(in);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static boolean ProvjeriChar(String in){
		//if (in.charAt(0) != '\'' || in.charAt(in.length() - 1) != '\'') return false;
		if (in.length() > 4) return false;
		if (in.length() == 4){
			if (in.charAt(1) != '\\') return false;
			if (in.charAt(2) != 't' &&
					in.charAt(2) != 'n' &&
					in.charAt(2) != '0' &&
					in.charAt(2) != '\'' &&
					in.charAt(2) != '"' &&
					in.charAt(2) != '\\') return false;
		}
		
		return true;
	}
	
	public static boolean ProvjeriNizConstChar(String in){
		
		int index = -1;
		while(true){
			index = in.indexOf('\\', index + 1);
			if (index != -1){
				if ((index == (in.length() - 1) && 
					(in.charAt(index + 1) != 't') &&
					(in.charAt(index + 1) != 'n') &&
					(in.charAt(index + 1) != '0') &&
					(in.charAt(index + 1) != '\'') &&
					(in.charAt(index + 1) != '"') &&
					(in.charAt(index + 1) != '\\'))) return false;
			}
			else break;
		}
		
		return true;
	}
}