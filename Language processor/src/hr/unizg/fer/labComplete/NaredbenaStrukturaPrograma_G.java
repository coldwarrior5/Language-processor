package hr.unizg.fer.labComplete;

import java.util.ArrayList;
import java.util.List;

public class NaredbenaStrukturaPrograma_G {
	
	public static Parser_G mParser;
	public static FRISC_ispisivac mIspisivac;
	public static List<Ime_Velicina_Adresa> mStog = null; // kada je nula onda smo u globalnom djelokrugu
	public static List<Ime_Velicina_Adresa> mGlobalniDjelokrug = new ArrayList<Ime_Velicina_Adresa>();
	public static List<Integer> mStanjaStogaDjelokruga = new ArrayList<Integer>();
	public static int mBrojacLabela = 0;
	
	public static int PretraziIme_Lok(String imeVar){
		// pretrazuje stog (lokalni djelokrug)
		for (int i = mStog.size() - 1; i >= 0; --i){
			if (mStog.get(i).mIme != null && mStog.get(i).mIme.equals(imeVar)) return i;
		}
		return -1;
	}
	
	public static int DajDubinuOdVrhaStoga(int indexVarijable){
		int dubina = 0;
		for (int i = mStog.size() - 1; i >= 0 && i != indexVarijable; --i) dubina += mStog.get(i).mVelicina;
		return dubina;
	}
	
	public static int PretraziIme_Glo(String imeVar){
		// pretrazuje globalni djelokrug
		for (int i = mGlobalniDjelokrug.size() - 1; i >= 0; --i){
			if (mGlobalniDjelokrug.get(i).mIme != null && mGlobalniDjelokrug.get(i).mIme.equals(imeVar)) return i;
		}
		return -1;
	}
	
	public static void OBRADI_slozena_naredba(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj L_VIT_ZAGRADA
		linija = mParser.ParsirajNovuLiniju();
		mStanjaStogaDjelokruga.add(mStog.size());
		
		if (linija.equals("<lista_naredbi>")){
			OBRADI_lista_naredbi();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_VIT_ZAGRADA
			//makni sa stoga sav visak tako da ostane samo ono sto je bilo na pocetku ulaza u djelokrug
			for (int i = mStog.size(); i > mStanjaStogaDjelokruga.get(mStanjaStogaDjelokruga.size() - 1); --i){
				mStog.remove(i - 1);
				mIspisivac.DodajKod("POP R0", "ciscenje dijela stoga prije kraja djelokruga");
			}
			mStanjaStogaDjelokruga.remove(mStanjaStogaDjelokruga.size() - 1);
			return;
		}
		
		if (linija.equals("<lista_deklaracija>")){
			DeklaracijeIDefinicije_G.OBRADI_lista_deklaracija();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <lista_naredbi>
			OBRADI_lista_naredbi();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_VIT_ZAGRADA
			//makni sa stoga sav visak tako da ostane samo ono sto je bilo na pocetku ulaza u djelokrug
			for (int i = mStog.size(); i > mStanjaStogaDjelokruga.get(mStanjaStogaDjelokruga.size() - 1); --i){
				mStog.remove(i - 1);
				mIspisivac.DodajKod("POP R0", "ciscenje dijela stoga prije kraja djelokruga");
			}
			mStanjaStogaDjelokruga.remove(mStanjaStogaDjelokruga.size() - 1);
			return;
		}
	}
	
	public static void OBRADI_lista_naredbi(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<naredba>")){
			OBRADI_naredba();
			return;
		}
		
		if (linija.equals("<lista_naredbi>")){
			OBRADI_lista_naredbi();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
			OBRADI_naredba();
			return;
		}
	}
	
	public static void OBRADI_naredba(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<slozena_naredba>")){
			OBRADI_slozena_naredba();
			return;
		}
		
		if (linija.equals("<izraz_naredba>")){
			OBRADI_izraz_naredba(false);
			return;
		}
		
		if (linija.equals("<naredba_grananja>")){
			OBRADI_naredba_grananja();
			return;
		}
		
		if (linija.equals("<naredba_petlje>")){
			OBRADI_naredba_petlje();
			return;
		}
		
		if (linija.equals("<naredba_skoka>")){
			OBRADI_naredba_skoka();
			return;
		}
	}
	
	public static void OBRADI_izraz_naredba(boolean dodajNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (uz != null && uz.mNaziv.equals("TOCKAZAREZ")){
			if (dodajNaStog){
				mIspisivac.DodajKod("MOVE %D 1 R0");
				mIspisivac.DodajKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mStog.add(novi);
			}
			return;
		}
		
		if (linija.equals("<izraz>")){
			Izrazi_G.OBRADI_izraz(dodajNaStog);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj TOCKAZAREZ
			return;
		}
	}
	
	public static void OBRADI_naredba_grananja(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj KR_IF
		linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
		linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz>
		Izrazi_G.OBRADI_izraz(true);
		linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
		
		mIspisivac.DodajKod("POP R0", "if naredba: skini izraz sa stoga");
		mStog.remove(mStog.size() - 1);
		mIspisivac.DodajKod("CMP R0, 0", "if naredba: provjeri ako je izraz jednak 0");
		int trenLabela = mBrojacLabela++;
		mIspisivac.DodajKod("JR_EQ NG_DALJE_1_" + trenLabela, "if naredba: ako je onda preskoci dio koda");
		
		linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>1
		OBRADI_naredba();
		
		// ima li jos i "else"?
		Boolean elsePostoji = false;
		linija = mParser.DohvatiProviriVrijednost();
		UniformniZnak uz_else = null;
		if (linija != null){
			uz_else = UniformniZnak.SigurnoStvaranje(linija);
			if (uz_else != null && uz_else.mNaziv.equals("KR_ELSE")) elsePostoji = true;
		}
		
		if (elsePostoji){
			mIspisivac.DodajKod("JR NG_DALJE_2_" + trenLabela, "if naredba: preskoci ELSE dio koda");
			mIspisivac.PostaviSljedecuLabelu("NG_DALJE_1_" + trenLabela);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj KR_ELSE (jer smo ga dobavili samo privirkivanjem)
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>2
			OBRADI_naredba();
			mIspisivac.PostaviSljedecuLabelu("NG_DALJE_2_" + trenLabela);
		}else{
			mIspisivac.PostaviSljedecuLabelu("NG_DALJE_1_" + trenLabela);
		}
	}
	
	public static void OBRADI_naredba_petlje(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		int trenLabela = mBrojacLabela++;
		
		// while petlja ??
		if (uz != null && uz.mNaziv.equals("KR_WHILE")){
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz>
			
			mIspisivac.PostaviSljedecuLabelu("NP_UVIJET_" + trenLabela);
			Izrazi_G.OBRADI_izraz(true);
			mIspisivac.DodajKod("POP R0", "while naredba: skini izraz (uvijet) sa stoga");
			mStog.remove(mStog.size() - 1);
			mIspisivac.DodajKod("CMP R0, 0", "while naredba: provjeri ako je izraz jednak 0");
			mIspisivac.DodajKod("JR_EQ NP_KRAJ_" + trenLabela, "while naredba: ako je onda izadji iz petlje");
			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
			OBRADI_naredba();
			mIspisivac.DodajKod("JR NP_UVIJET_" + trenLabela, "while naredba: povratak na uvijet petlje");
			mIspisivac.PostaviSljedecuLabelu("NP_KRAJ_" + trenLabela);
			return;
		}
		
		// for petlja ??
		if (uz != null && uz.mNaziv.equals("KR_FOR")){
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_naredba>1
			OBRADI_izraz_naredba(false);
			
			// provjera
			mIspisivac.PostaviSljedecuLabelu("NP_UVIJET_" + trenLabela);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_naredba>2
			OBRADI_izraz_naredba(true);
			
			mIspisivac.DodajKod("POP R0", "for naredba: skini izraz (uvijet) sa stoga");
			mStog.remove(mStog.size() - 1);
			mIspisivac.DodajKod("CMP R0, 0", "for naredba: provjeri ako je izraz jednak 0");
			mIspisivac.DodajKod("JR_EQ NP_KRAJ_" + trenLabela, "for naredba: ako je onda izadji iz petlje");
			mIspisivac.DodajKod("JR NP_DALJE_" + trenLabela, "for naredba: ako nije onda preskoci dio koda koraka petlje");
			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA ili <izraz>
			Boolean imaKorak;
			if (linija.equals("<izraz>")){
				imaKorak = true;
				mIspisivac.PostaviSljedecuLabelu("NP_KORAK_" + trenLabela);
				Izrazi_G.OBRADI_izraz(false);
				mIspisivac.DodajKod("JR NP_UVIJET_" + trenLabela, "for naredba: nakon koraka skoci na uvijet za provjeru");
				linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
				
				mIspisivac.PostaviSljedecuLabelu("NP_DALJE_" + trenLabela);
				linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
				OBRADI_naredba();
			}
			else{ // it must be D_ZAGRADA
				imaKorak = false;
				mIspisivac.PostaviSljedecuLabelu("NP_DALJE_" + trenLabela);
				linija = mParser.ParsirajNovuLiniju(); // ucitaj <naredba>
				OBRADI_naredba();
			}
			if (imaKorak){
				// povratak na korak petlje
				mIspisivac.DodajKod("JR NP_KORAK_" + trenLabela, "for naredba: povratak na korak petlje");
			}else{
				// povratak na uvijet petlje
				mIspisivac.DodajKod("JR NP_UVIJET_" + trenLabela, "for naredba: povratak na uvijet petlje");
			}
			
			mIspisivac.PostaviSljedecuLabelu("NP_KRAJ_" + trenLabela);
			return;
		}
	}
	
	public static void OBRADI_naredba_skoka(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		// break i continue ??
		if (uz != null && (uz.mNaziv.equals("KR_CONTINUE") || uz.mNaziv.equals("KR_BREAK"))){
			linija = mParser.ParsirajNovuLiniju();
			//UniformniZnak uz_tockaZarez = UniformniZnak.SigurnoStvaranje(linija);
			// nije implementirano
			return;
		}
		
		// return ??
		if (uz != null && uz.mNaziv.equals("KR_RETURN")){
			linija = mParser.ParsirajNovuLiniju();
			if (linija.equals("<izraz>")){
				Izrazi_G.OBRADI_izraz(true);
				linija = mParser.ParsirajNovuLiniju(); // TOCKAZAREZ
				mIspisivac.DodajKod("POP R6", "povratna vrijednost sa stoga");
				mStog.remove(mStog.size()-1);
			}// else -> mora biti tockaZarez
			
			for (int i = mStog.size(); i > DeklaracijeIDefinicije_G.mBrojParametaraTrenutacneFunkcije; --i){// ocisti lokalne varijable koje su ostale na stogu
				//mStog.remove(i - 1); 							// i smece poput spremanja vrijednosti void funkcije
				mIspisivac.DodajKod("POP R0", "ciscenje stoga prije RET"); // <- ne brisemo
			}
			mIspisivac.DodajKod("RET");
			//mStog.remove(mStog.size()-1); // makni return vrijednost sa stoga // <- ne brisemo
			return;
			// Ne micemo sa 'mStog' u komentiranim djelovima jer ovaj RET nece nuzno biti izveden.
			// 'mStog' se cisti u definiciji funkcije
		}
	}
	
	public static void OBRADI_prijevodna_jedinica(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<vanjska_deklaracija>")){
			OBRADI_vanjska_deklaracija();
			return;
		}
		
		if (linija.equals("<prijevodna_jedinica>")){
			OBRADI_prijevodna_jedinica();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <vanjska_deklaracja>
			OBRADI_vanjska_deklaracija();
			return;
		}
	}
	
	public static void OBRADI_vanjska_deklaracija(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<definicija_funkcije>")){
			DeklaracijeIDefinicije_G.OBRADI_definicija_funkcije();
			return;
		}
		
		if (linija.equals("<deklaracija>")){
			DeklaracijeIDefinicije_G.OBRADI_deklaracija();
			return;
		}
	}
}
