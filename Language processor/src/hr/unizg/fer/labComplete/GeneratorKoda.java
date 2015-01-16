package hr.unizg.fer.labComplete;

import java.util.ArrayList;
import java.util.List;

class Ime_Velicina_Adresa{
	String mIme; // identifikator
	int mVelicina; // u byteovima
	boolean mAdresa; // jeli spremljena vrijednost ili adresa
	}

class Utilities {
	
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

public class GeneratorKoda {

	public void GenerirajKod(String saOutput) {
		
		Parser_G p = new Parser_G(saOutput);
		FRISC_ispisivac frisc_i = new FRISC_ispisivac();
		//frisc_i.DodajKod("F_MAIN", "MOVE %D 42, R6", "nesto");
		//frisc_i.DodajGlobalnuVarijablu("G_BROJ", "DW %D 12");
		//frisc_i.DodajKod("RET");
		//frisc_i.DodajGlobalnuVarijablu("G_Y", "DW %D 1243");
		
		Izrazi_G.mParser = NaredbenaStrukturaPrograma_G.mParser = DeklaracijeIDefinicije_G.mParser = p;
		Izrazi_G.mIspisivac = NaredbenaStrukturaPrograma_G.mIspisivac = DeklaracijeIDefinicije_G.mIspisivac = frisc_i;
		
		p.ParsirajNovuLiniju(); // ucitaj <prijevodna_jedinica>
		NaredbenaStrukturaPrograma_G.OBRADI_prijevodna_jedinica();
		
		frisc_i.DodajPotrebneFunkcije();
		frisc_i.Ispisi("src/hr/unizg/fer/labComplete/");
	}

}
