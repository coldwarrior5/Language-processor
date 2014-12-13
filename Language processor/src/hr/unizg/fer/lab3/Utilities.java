package hr.unizg.fer.lab3;

import java.util.Scanner;

class UniformniZnak{
	String mNaziv;
	int mRedak;
	String mLeksickaJedinka;
	
	public UniformniZnak(String linija){
		linija += " "; // lakse je ovako rascjepati liniju
		mNaziv = linija.substring(0, linija.indexOf(' '));
		linija = linija.substring(linija.indexOf(' ') + 1);
		mRedak = Integer.parseInt(linija.substring(0, linija.indexOf(' ')));
		linija = linija.substring(linija.indexOf(' ') + 1);
		linija = linija.substring(0, linija.indexOf(' '));
		mLeksickaJedinka = linija;
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

class Tip_LIzraz_Const_Niz{
	Tip mTip;	
	TipFunkcija mFun = null; // razlicito od null ako je (mTip == Tip._funkcija).
	Boolean mL_izraz;
	Boolean mConst;
	Boolean mNiz;
	}

class Tip_Const{
	Tip mTip;	
	Boolean mConst;
	}

class Tip_Const_Niz_BrEle_TipFunk{
	Tip mTip;	
	Boolean mConst;
	Boolean mNiz;
	int mBrElemenata; // potrebno ako je niz
	TipFunkcija mTipFunkcija; // null ako nije mTip == Tip._funkcija
	}

class Tip_LIzraz_Const_Niz_Ime{
	Tip_LIzraz_Const_Niz mT;
	String mIme;
	}

public class Utilities {
	
	public static Boolean mWriteToOutputEnabled = true;
	
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
	
	public static Boolean FunkcijeIste(TipFunkcija f1, TipFunkcija f2){
		if (f1.mPov != f2.mPov) return false;
		if (f1.mParam.size() != f2.mParam.size()) return false;
		for (int i = 0; i < f1.mParam.size(); ++i)
			if (f1.mParam.get(i) != f2.mParam.get(i)) return false;
		return true;
	}
	
	public static int VratiBrojZnakovaIz_NIZ_ZNAKOVA(String niz_znakova){
		
		
		return 0;
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
	
	public static void WriteStringLineToOutputAndExit(String str){
		if (!mWriteToOutputEnabled) return;
		System.out.println(str);
		System.exit(0);
	}
	
	public static void WriteStringLineToStdErr(String str){
		System.err.println(str);
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
		
		int index = 0;
		int length = in.length();
		while(true){
			index = in.indexOf('\\', index);
			if (index == (length - 1)) 
				return false; // nesmije '/' biti zadnji znak
			else if (index != -1){
				if ((index != (length - 1) && 
					(in.charAt(index + 1) != 't') &&
					(in.charAt(index + 1) != 'n') &&
					(in.charAt(index + 1) != '0') &&
					(in.charAt(index + 1) != '\'') &&
					(in.charAt(index + 1) != '"') &&
					(in.charAt(index + 1) != '\\'))) return false;
				else
					index += 2; // vazno u slucaju '//'
			} 
			else 
				break;
		}
		
		return true;
	}
}