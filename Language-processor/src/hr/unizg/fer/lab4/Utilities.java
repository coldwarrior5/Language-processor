package hr.unizg.fer.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Tip {_int, _char, _funkcija, _void};

class UniformniZnak{
	String mNaziv;
	int mRedak;
	String mLeksickaJedinka;
	
	public UniformniZnak(String linija){
		mNaziv = linija.substring(0, linija.indexOf(' '));
		linija = linija.substring(linija.indexOf(' ') + 1);
		mRedak = Integer.parseInt(linija.substring(0, linija.indexOf(' ')));
		linija = linija.substring(linija.indexOf(' ') + 1);
		mLeksickaJedinka = linija;
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

class Ime_Velicina_Adresa{
	String mIme; // identifikator
	int mVelicina; // u byteovima
	boolean mAdresa; // jeli spremljena vrijednost ili adresa
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
	
	public static int VratiBazu(String in){
		int base = 10;
		if (in.charAt(0) == '0'){
			base = 8; // oct, upitno dali se koristi
			if (in.length() >= 3 && (in.charAt(1) == 'x' || in.charAt(1) == 'X')){
				base = 16; // hex
				in = in.substring(2, in.length());
			}
		}
		
		return base;
	}
	
	public static int PretvoriCharUInt(String in){
		if (in.length() == 4){
			// mora biti jedan od ovih
			if (in.charAt(2) != 't') return (int)('\t');
			if (in.charAt(2) != 'n') return (int)('\n');
			if (in.charAt(2) != '0') return (int)('\0');
			if (in.charAt(2) != '\'') return (int)('\'');
			if (in.charAt(2) != '"') return (int)('\"');
			if (in.charAt(2) != '\\') return (int)('\\');
			// ako nije 
			return -1;
		}
		else{ // length mora biti 3
			return in.charAt(1);
		}
	}

	public static List<Character> VratiZnakoveIz_NIZ_ZNAKOVA(String niz_znakova){
		List<Character> vrati = new ArrayList<Character>();
		for (int i = 1; i < niz_znakova.length() - 1; ++i){
			if (niz_znakova.charAt(i) == '\\'){
				++i;
				vrati.add('\\');
				vrati.add(niz_znakova.charAt(i));
			}else{
				vrati.add(niz_znakova.charAt(i));
			}
		}
		
		// dodaj terminal znak
		vrati.add('\0');
		return vrati;
	}
}