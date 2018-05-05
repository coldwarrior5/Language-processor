package hr.unizg.fer.labComplete;

import java.util.ArrayList;
import java.util.List;

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
class LISTA_Tip_LIzraz_Const_Niz{
	List<Tip_LIzraz_Const_Niz> mLista = new ArrayList<Tip_LIzraz_Const_Niz>();
	Boolean mJeNiz;
}

class Tip_Const{
	Tip mTip;	
	Boolean mConst;
	}

class Tip_Const_Niz{
	Tip mTip;	
	Boolean mConst;
	Boolean mNiz;
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

class Utilities_Sem {
	
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
			if (f1.mParam.get(i).mConst != f2.mParam.get(i).mConst ||
			f1.mParam.get(i).mNiz != f2.mParam.get(i).mNiz ||
			f1.mParam.get(i).mTip != f2.mParam.get(i).mTip) return false;
		return true;
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
		
		int base = 10;
		if (in.charAt(0) == '0'){
			base = 8; // oct, upitno dali se koristi
			if (in.length() >= 3 && (in.charAt(1) == 'x' || in.charAt(1) == 'X')){
				base = 16; // hex
				in = in.substring(2, in.length());
			}
		}
		
		try{
			Integer.parseInt(in, base);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static boolean ProvjeriChar(String in){
		if (in.length() > 4) 
			return false;
		if (in.length() == 4){
			if (in.charAt(1) != '\\') 
				return false;
			if (in.charAt(2) != 't' &&
					in.charAt(2) != 'n' &&
					in.charAt(2) != '0' &&
					in.charAt(2) != '\'' &&
					in.charAt(2) != '"' &&
					in.charAt(2) != '\\')
				return false;
		}
		else{ // length mora biti 3
			if (in.charAt(1) == '\\') 
				return false;
		}
		
		return true;
	}
		
	public static int VratiBrojZnakovaIz_NIZ_ZNAKOVA(String niz_znakova){
		int broj = 0;
		for (int i = 0; i < niz_znakova.length(); ++i){
			if (niz_znakova.charAt(i) == '\\') ++i;
			++broj;
		}
		return broj - 2; // zbog navodnika - 2
	}
	
	public static boolean ProvjeriNizConstChar(String in){
		Boolean prefiksiran = false;
		for (int i = 1; i < in.length() - 1; ++i){ // idemo od 1 do length() - 1 jer navodnici su osigurani leksikom
			char c = in.charAt(i);
			if (prefiksiran){
				if (c != 't' && c != 'n' && c != '0' && c != '\'' && c != '"' && c != '\\')
					return false;
				else{
					prefiksiran = false;
					continue;
				}
			}
			else{
				if (c == '"') 
					return false;
			}
			if (c == '\\'){
				prefiksiran = !prefiksiran;
			}
		}
		// nesmije zavrsit prefiksiran
		if (prefiksiran) return false;
		else return true;
	}
}

public class SemantickiAnalizator {
	
	public void Check(String saOutput) {
		
		StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		Parser_Sem p = new Parser_Sem(saOutput);
		Izrazi_Sem.mSTZ = NaredbenaStrukturaPrograma_Sem.mSTZ = DeklaracijeIDefinicije_Sem.mSTZ = stz;
		Izrazi_Sem.mParser = NaredbenaStrukturaPrograma_Sem.mParser = DeklaracijeIDefinicije_Sem.mParser = p;
		
		p.ParsirajNovuLiniju(); // ucitaj <prijevodna_jedinica>
		NaredbenaStrukturaPrograma_Sem.PROVJERI_prijevodna_jedinica();
		// nakon obilaska stabla
		stz.ProvjeraNakonObilaska();
	}
}
