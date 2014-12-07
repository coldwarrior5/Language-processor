package hr.unizg.fer.lab3;

class PROVJERI_primarni_izraz_RET_TYPE{
	Tip mTip;
	Boolean mL_izraz;
	}
	
public class Izrazi {
	
	public static StablastaTablicaZnakova mSTZ;
	public static Parser mParser;
	
	public static PROVJERI_primarni_izraz_RET_TYPE PROVJERI_primarni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = new UniformniZnak(linija);
		PROVJERI_primarni_izraz_RET_TYPE vrati = new PROVJERI_primarni_izraz_RET_TYPE();
		
		if (uz.mNaziv.equals("IDN")){
			ClanTabliceZnakova cl = mSTZ.DohvatiClanIzTabliceZnakova(uz.mLeksickaJedinka);
			if (cl == null) {
				String greska = "<primarni_izraz> ::= IDN(" + uz.mRedak + "," + uz.mLeksickaJedinka + ")";
				Utilities.WriteStringLineToOutput(greska);
				System.exit(0);
			}			
			vrati.mL_izraz = cl.mL_izraz;
			vrati.mTip = cl.mTip;
			return vrati;
		}
		
		if (uz.mNaziv.equals("BROJ")){
			
			return vrati;
		}
		
		if (uz.mNaziv.equals("ZNAK")){
			
			return vrati;
		}
		
		if (uz.mNaziv.equals("NIZ_ZNAKOVA")){
			
			return vrati;
		}
		
		if (uz.mNaziv.equals("L_ZAGRADA")){
			
			return vrati;
		}
		return vrati;
	}
}
