package hr.unizg.fer.labComplete;

public class NaredbenaStrukturaPrograma_Sem {
	
	public static StablastaTablicaZnakova mSTZ;
	public static Parser_Sem mParser;
	
	public static void PROVJERI_slozena_naredba(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj L_VIT_ZAGRADA
		linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<lista_naredbi>")){
			PROVJERI_lista_naredbi();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_VIT_ZAGRADA
			return;
		}
		
		if (linija.equals("<lista_deklaracija>")){
			DeklaracijeIDefinicije_Sem.PROVJERI_lista_deklaracija();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <lista_naredbi>
			PROVJERI_lista_naredbi();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_VIT_ZAGRADA
			return;
		}
	}
	
	public static void PROVJERI_lista_naredbi(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<naredba>")){
			PROVJERI_naredba();
			return;
		}
		
		if (linija.equals("<lista_naredbi>")){
			PROVJERI_lista_naredbi();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
			PROVJERI_naredba();
			return;
		}
	}
	
	public static void PROVJERI_naredba(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<slozena_naredba>")){
			mSTZ.UdjiUNoviCvor(null);
			PROVJERI_slozena_naredba();
			mSTZ.IzadjiIzCvora();
			return;
		}
		
		if (linija.equals("<izraz_naredba>")){
			PROVJERI_izraz_naredba();
			return;
		}
		
		if (linija.equals("<naredba_grananja>")){
			PROVJERI_naredba_grananja();
			return;
		}
		
		if (linija.equals("<naredba_petlje>")){
			PROVJERI_naredba_petlje();
			return;
		}
		
		if (linija.equals("<naredba_skoka>")){
			PROVJERI_naredba_skoka();
			return;
		}
	}
	
	public static Tip_LIzraz_Const_Niz PROVJERI_izraz_naredba(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		Tip_LIzraz_Const_Niz vrati;
		
		if (uz != null && uz.mNaziv.equals("TOCKAZAREZ")){
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mConst = false;
			vrati.mNiz = false;
			vrati.mL_izraz = false;
			return vrati;
		}
		
		if (linija.equals("<izraz>")){
			vrati = Izrazi_Sem.PROVJERI_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj TOCKAZAREZ
			return vrati;
		}
		
		return null;
	}
	
	public static void PROVJERI_naredba_grananja(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj KR_IF
		UniformniZnak uz_kr_if = UniformniZnak.SigurnoStvaranje(linija);
		linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
		UniformniZnak uz_l_zagrada = UniformniZnak.SigurnoStvaranje(linija);
		linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz>
		Tip_LIzraz_Const_Niz izraz = Izrazi_Sem.PROVJERI_izraz();
		linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
		UniformniZnak uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
		linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>1
		PROVJERI_naredba();
		
		// ima li jos i "else"?
		Boolean elseExists = false;
		linija = mParser.DohvatiProviriVrijednost();
		UniformniZnak uz_else = null;
		if (linija != null){
			uz_else = UniformniZnak.SigurnoStvaranje(linija);
			if (uz_else != null && uz_else.mNaziv.equals("KR_ELSE")) elseExists = true;
		}
		
		// ispis u slucaju pogreske
		if (!Utilities_Sem.ImplicitnaPretvorbaMoguca(izraz.mTip, Tip._int) || izraz.mNiz){
			String greska = "<naredba_grananja> ::= " + uz_kr_if.FormatZaIspis() + " " +
					uz_l_zagrada.FormatZaIspis() + " <izraz> " + uz_d_zagrada.FormatZaIspis() + " <naredba>";
			if (elseExists) greska += " " + uz_else.FormatZaIspis() + " <naredba>";
			Utilities_Sem.WriteStringLineToOutputAndExit(greska);
		}
		
		if (elseExists){
			linija = mParser.ParsirajNovuLiniju(); // ucitaj KR_ELSE (jer smo ga dobavili samo privirkivanjem)
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>2
			PROVJERI_naredba();
			return;
		}
	}
	
	public static void PROVJERI_naredba_petlje(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		// while petlja ??
		if (uz != null && uz.mNaziv.equals("KR_WHILE")){
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
			UniformniZnak uz_l_zagrada = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz>
			Tip_LIzraz_Const_Niz izraz = Izrazi_Sem.PROVJERI_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
			UniformniZnak uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities_Sem.ImplicitnaPretvorbaMoguca(izraz.mTip, Tip._int) || izraz.mNiz){
				String greska = "<naredba_petlje> ::= " + uz.FormatZaIspis() + " " +
						uz_l_zagrada.FormatZaIspis() + " <izraz> " + uz_d_zagrada.FormatZaIspis() + " <naredba>";
				Utilities_Sem.WriteStringLineToOutputAndExit(greska);
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
			PROVJERI_naredba();
			return;
		}
		
		// for petlja ??
		if (uz != null && uz.mNaziv.equals("KR_FOR")){
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
			UniformniZnak uz_l_zagrada = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_naredba>1
			PROVJERI_izraz_naredba();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_naredba>2			
			Tip_LIzraz_Const_Niz izraz_naredba2 = PROVJERI_izraz_naredba();
			
			Boolean writeToOutputEnabledWasTrue = Utilities_Sem.mWriteToOutputEnabled;
			Boolean izraz_naredba2_jeOK = true;
			if (!Utilities_Sem.ImplicitnaPretvorbaMoguca(izraz_naredba2.mTip, Tip._int) || izraz_naredba2.mNiz){
				Utilities_Sem.mWriteToOutputEnabled = false;
				izraz_naredba2_jeOK = false;
			}
			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA ili <izraz>
			UniformniZnak uz_d_zagrada;
			Boolean imaIzraz;
			if (linija.equals("<izraz>")){
				imaIzraz = true;
				Izrazi_Sem.PROVJERI_izraz();
				linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
				uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
				linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
				PROVJERI_naredba();
			}
			else{ // it must be D_ZAGRADA
				imaIzraz = false;
				uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
				linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
				PROVJERI_naredba();
			}
			
			if (writeToOutputEnabledWasTrue) Utilities_Sem.mWriteToOutputEnabled = true;
			if (!izraz_naredba2_jeOK){
				String greska = "<naredba_petlje> ::= " + uz.FormatZaIspis() + " " +
						uz_l_zagrada.FormatZaIspis() + " <izraz_naredba> <izraz_naredba>";
				if (imaIzraz) greska += " <izraz>";
				greska += " " + uz_d_zagrada.FormatZaIspis() + " <naredba>";
				Utilities_Sem.WriteStringLineToOutputAndExit(greska);
			}
			return;
		}
	}
	
	public static void PROVJERI_naredba_skoka(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		// break i continue ??
		if (uz != null && (uz.mNaziv.equals("KR_CONTINUE") || uz.mNaziv.equals("KR_BREAK"))){
			linija = mParser.ParsirajNovuLiniju();
			UniformniZnak uz_tockaZarez = UniformniZnak.SigurnoStvaranje(linija);
			if (!mParser.DohvatiSljedRoditelja().contains("<naredba_petlje>")){
				String greska = "<naredba_skoka> ::= " + uz.FormatZaIspis() + " " + uz_tockaZarez.FormatZaIspis();
				Utilities_Sem.WriteStringLineToOutputAndExit(greska);
			}
			return;
		}
		
		// return ??
		if (uz != null && uz.mNaziv.equals("KR_RETURN")){
			linija = mParser.ParsirajNovuLiniju();
			UniformniZnak uz_tockaZarez;
			if (linija.equals("<izraz>")){
				Tip_LIzraz_Const_Niz izraz = Izrazi_Sem.PROVJERI_izraz();
				linija = mParser.ParsirajNovuLiniju(); // TOCKAZAREZ
				uz_tockaZarez = UniformniZnak.SigurnoStvaranje(linija);
				TipFunkcija tipFunk = mSTZ.VratiDeklaracijuFunkcijeDjelokruga();
				if (tipFunk == null || !Utilities_Sem.ImplicitnaPretvorbaMoguca(izraz.mTip, tipFunk.mPov) || izraz.mNiz){
					String greska = "<naredba_skoka> ::= " + uz.FormatZaIspis() + " <izraz> " + uz_tockaZarez.FormatZaIspis();
					Utilities_Sem.WriteStringLineToOutputAndExit(greska);
				}				
			}else { // mora biti tockaZarez
				uz_tockaZarez = UniformniZnak.SigurnoStvaranje(linija);
				TipFunkcija tipFunk = mSTZ.VratiDeklaracijuFunkcijeDjelokruga();
				if (tipFunk == null || tipFunk.mPov != Tip._void){
					String greska = "<naredba_skoka> ::= " + uz.FormatZaIspis() + " " + uz_tockaZarez.FormatZaIspis();
					Utilities_Sem.WriteStringLineToOutputAndExit(greska);
				}
			}
			return;
		}
	}
	
	public static void PROVJERI_prijevodna_jedinica(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<vanjska_deklaracija>")){
			PROVJERI_vanjska_deklaracija();
			return;
		}
		
		if (linija.equals("<prijevodna_jedinica>")){
			PROVJERI_prijevodna_jedinica();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <vanjska_deklaracja>
			PROVJERI_vanjska_deklaracija();
			return;
		}
	}
	
	public static void PROVJERI_vanjska_deklaracija(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<definicija_funkcije>")){
			DeklaracijeIDefinicije_Sem.PROVJERI_definicija_funkcije();
			return;
		}
		
		if (linija.equals("<deklaracija>")){
			DeklaracijeIDefinicije_Sem.PROVJERI_deklaracija();
			return;
		}
	}
}
