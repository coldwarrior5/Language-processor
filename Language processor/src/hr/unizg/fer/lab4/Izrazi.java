package hr.unizg.fer.lab4;
	
public class Izrazi {
	
	public static Parser mParser;
	public static FRISC_ispisivac mIspisivac;
	public static int mBrojacLabela = 0;
	
	public static void OBRADI_primarni_izraz(boolean dajAdresu){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = new UniformniZnak(linija);
		int trenLabela = mBrojacLabela++;
		
		if (uz.mNaziv.equals("IDN")){
			if (NaredbenaStrukturaPrograma.mStog != null){ // jesmo li u globalnom djelokrugu ili u funkciji
				int i = NaredbenaStrukturaPrograma.PretraziIme_Lok(uz.mLeksickaJedinka);
				if (i == -1){ // trazena varijabla mora biti u globalnom djelokrugu
					i = NaredbenaStrukturaPrograma.PretraziIme_Glo(uz.mLeksickaJedinka);
					if(dajAdresu) mIspisivac.DodajKod("MOVE G_" + uz.mLeksickaJedinka.toUpperCase() + ", R0", "dohvacam adresu " + uz.mLeksickaJedinka);
					else mIspisivac.DodajKod("LOAD R0, (G_" + uz.mLeksickaJedinka.toUpperCase() + ")", "dohvacam " + uz.mLeksickaJedinka);
					mIspisivac.DodajKod("PUSH R0");
					Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
					novi.mIme = null;
					novi.mAdresa = NaredbenaStrukturaPrograma.mGlobalniDjelokrug.get(i).mAdresa;
					novi.mVelicina = NaredbenaStrukturaPrograma.mGlobalniDjelokrug.get(i).mVelicina;
					NaredbenaStrukturaPrograma.mStog.add(novi);
				}else{ // trazena varijabla je u lokalnom djelokrugu
					int dubina = NaredbenaStrukturaPrograma.DajDubinuOdVrhaStoga(i);
					if(dajAdresu){
						mIspisivac.DodajKod("PUSH R7", "(korak 1) dohvacam adresu: " + uz.mLeksickaJedinka);
						mIspisivac.DodajKod("POP R0", "(korak 2) dohvacam adresu: " + uz.mLeksickaJedinka);
						mIspisivac.DodajKod("ADD R0, 4, R0", "(korak 3) dohvacam adresu: " + uz.mLeksickaJedinka);
						mIspisivac.DodajKod("ADD R0, " + dubina + ", R0", "(korak 4) dohvacam adresu: " + uz.mLeksickaJedinka);
					}
					else mIspisivac.DodajKod("LOAD R0, (R7 + " + dubina + ")", "dohvacam " + uz.mLeksickaJedinka);
					mIspisivac.DodajKod("PUSH R0");
					Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
					novi.mIme = null;
					novi.mAdresa = NaredbenaStrukturaPrograma.mStog.get(i).mAdresa;
					novi.mVelicina = NaredbenaStrukturaPrograma.mStog.get(i).mVelicina;
					NaredbenaStrukturaPrograma.mStog.add(novi);
				}
				return;
			}else{
				// trazena varijabla mora biti u globalnom djelokrugu ako je i trenutno analizirani dio koda
				int i = NaredbenaStrukturaPrograma.PretraziIme_Glo(uz.mLeksickaJedinka);
				if(dajAdresu) mIspisivac.DodajPreMainKod("MOVE G_" + uz.mLeksickaJedinka.toUpperCase() + ", R0", "dohvacam adresu " + uz.mLeksickaJedinka);
				else mIspisivac.DodajPreMainKod("LOAD R0, (G_" + uz.mLeksickaJedinka.toUpperCase() + ")", "dohvacam " + uz.mLeksickaJedinka);
				mIspisivac.DodajPreMainKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = NaredbenaStrukturaPrograma.mGlobalniDjelokrug.get(i).mAdresa;
				novi.mVelicina = NaredbenaStrukturaPrograma.mGlobalniDjelokrug.get(i).mVelicina;
				NaredbenaStrukturaPrograma.mGlobalniDjelokrug.add(novi);
				return;
			}
		}
		
		if (uz.mNaziv.equals("BROJ")){
			int baza = Utilities.VratiBazu(uz.mLeksickaJedinka);
			int broj = Integer.parseInt(uz.mLeksickaJedinka, baza);
			if (NaredbenaStrukturaPrograma.mStog != null){ // jesmo li u globalnom djelokrugu ili u funkciji
				if (broj > 524287 || broj < -524287){
					mIspisivac.DodajGlobalnuVarijablu("TEMP_" + trenLabela, "DW %D " + broj);
					mIspisivac.DodajKod("LOAD R0, (TEMP_" + trenLabela + ")");
					mIspisivac.DodajKod("PUSH R0");
				}
				else{
					mIspisivac.DodajKod("MOVE %D " + uz.mLeksickaJedinka + ", R0");
					mIspisivac.DodajKod("PUSH R0");
				}
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
				return;
			}else{
				if (broj > 524287 || broj < -524287){
					mIspisivac.DodajGlobalnuVarijablu("TEMP_" + trenLabela, "DW %D " + broj);
					mIspisivac.DodajPreMainKod("LOAD R0, (TEMP_" + trenLabela + ")");
					mIspisivac.DodajPreMainKod("PUSH R0");
				}
				else{
					mIspisivac.DodajPreMainKod("MOVE %D " + uz.mLeksickaJedinka + ", R0");
					mIspisivac.DodajPreMainKod("PUSH R0");
				}
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mGlobalniDjelokrug.add(novi);
				return;
			}
		}
		
		if (uz.mNaziv.equals("ZNAK")){
			int broj = Utilities.PretvoriCharUInt(uz.mLeksickaJedinka);
			if (NaredbenaStrukturaPrograma.mStog != null){ // jesmo li u globalnom djelokrugu ili u funkciji
				mIspisivac.DodajKod("MOVE %D " + broj + ", R0");
				mIspisivac.DodajKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
				return;
			}else{
				mIspisivac.DodajPreMainKod("MOVE %D " + broj + ", R0");
				mIspisivac.DodajPreMainKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mGlobalniDjelokrug.add(novi);
				return;
			}
		}
		
		if (uz.mNaziv.equals("NIZ_ZNAKOVA")){
			
			// nije implementirano
			return;
		}
		
		if (uz.mNaziv.equals("L_ZAGRADA")){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <izraz>
			OBRADI_izraz(true);
			linija = mParser.ParsirajNovuLiniju();	// ucitaj D_ZAGRADA
			return;
		}
	}

	public static void OBRADI_postfiks_izraz(boolean dajAdresu, boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		int trenLabela = mBrojacLabela++;
		
		if (linija.equals("<primarni_izraz>")){
			OBRADI_primarni_izraz(dajAdresu);
			return;
		}
		
		if (linija.equals("<postfiks_izraz>")){
			OBRADI_postfiks_izraz(true, true);
			linija = mParser.ParsirajNovuLiniju();
			UniformniZnak uz1 = new UniformniZnak(linija);
			
			// indeksiranje nizova
			if (uz1.mNaziv.equals("L_UGL_ZAGRADA")){
				linija = mParser.ParsirajNovuLiniju();	// ucitaj <izraz>
				OBRADI_izraz(true);
				linija = mParser.ParsirajNovuLiniju();	// D_UGL_ZAGRADA
				mIspisivac.DodajKod("POP R1", "indeksiranje niza: dohvati pomak");
				mIspisivac.DodajKod("POP R0", "indeksiranje niza: dohvati adresu adrese prvog clana");
				mIspisivac.DodajKod("LOAD R0, (R0)", "indeksiranje niza: dohvati adresu prvog clana");
				mIspisivac.DodajKod("ADD R0, 4, R0", "manje jumpova na ovaj nacin");
				mIspisivac.DodajKod("ADD R1, 1, R1", "manje jumpova na ovaj nacin");
				mIspisivac.PostaviSljedecuLabelu("IN_" + trenLabela);
				mIspisivac.DodajKod("SUB R0, 4, R0", "dodaj pomak");
				mIspisivac.DodajKod("SUB R1, 1, R1", "umanji broj pomaka za jedan");
				mIspisivac.DodajKod("JR_NZ IN_" + trenLabela,
						"dodaj jos pomaka ako je potrebno");
				if (staviNaStog){
					if (dajAdresu) mIspisivac.DodajKod("PUSH R0", "pushaj adresu clana");
					else{			
						mIspisivac.DodajKod("LOAD R1, (R0)", "dohvati clan");
						mIspisivac.DodajKod("PUSH R1", "pushaj clan");
					}
					// maknuli smo dva i stavili jedan pa je razlika sljedeca:
					NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
				}else{
					// maknuli smo dva pa je razlika sljedeca:
					NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
					NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
				}
				return;
			}
			
			// pozivi funkcija
			if (uz1.mNaziv.equals("L_ZAGRADA")){
				linija = mParser.ParsirajNovuLiniju();
				UniformniZnak uz2 = UniformniZnak.SigurnoStvaranje(linija);
				if (uz2 != null && uz2.mNaziv.equals("D_ZAGRADA")){ // void --> pov
					mIspisivac.DodajKod("POP R0", "dohvati adresu adrese funkcije");
					mIspisivac.DodajKod("LOAD R0, (R0)", "dohvati adresu funkcije");
					NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
					mIspisivac.DodajKod("CALL (R0)"); // pozovi adresu
					if (staviNaStog){
						mIspisivac.DodajKod("PUSH R6");
						Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
						novi.mIme = null;
						novi.mAdresa = false;
						novi.mVelicina = 4;
						NaredbenaStrukturaPrograma.mStog.add(novi);
					}
					return;
				}
				if (linija.equals("<lista_argumenata>")){ // lista_argumenata --> pov
					mIspisivac.DodajKod("POP R0", "dohvati adresu adrese funkcije");
					mIspisivac.DodajKod("LOAD R5, (R0)", "dohvati adresu funkcije");
					NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
					int broj_argumenata = OBRADI_lista_argumenata();
					linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA	
					mIspisivac.DodajKod("CALL (R5)"); // pozovi adresu
					while(broj_argumenata-- != 0){ // makni sve stavljene argumente sa stoga
						mIspisivac.DodajKod("POP R0");
						NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
					}
					if (staviNaStog){
						mIspisivac.DodajKod("PUSH R6");
						Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
						novi.mIme = null;
						novi.mAdresa = false;
						novi.mVelicina = 4;
						NaredbenaStrukturaPrograma.mStog.add(novi);
					}
					return;
				}
			}
			
			// ++ i --
			if (uz1.mNaziv.equals("OP_INC") || uz1.mNaziv.equals("OP_DEC")){
				
				mIspisivac.DodajKod("POP R0", "postfiks izraz: dohvati adresu");
				NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
				mIspisivac.DodajKod("LOAD R1, (R0)", "postfiks izraz: dohvati vrijednost");
				if (uz1.mNaziv.equals("OP_DEC")) mIspisivac.DodajKod("SUB R1, 1, R2", "postfiks izraz: umanji za jedan");
				else mIspisivac.DodajKod("ADD R1, 1, R2", "postfiks izraz: uvecaj za jedan");
				mIspisivac.DodajKod("STORE R2, (R0)", "postfiks izraz: spremi");
				if (staviNaStog){
					mIspisivac.DodajKod("PUSH R1", "postfiks izraz: stavi rezultat na stog");
					Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
					novi.mIme = null;
					novi.mAdresa = false;
					novi.mVelicina = 4;
					NaredbenaStrukturaPrograma.mStog.add(novi);
				}
				return;
			}
		}
	}

	public static int OBRADI_lista_argumenata(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<izraz_pridruzivanja>")){
			OBRADI_izraz_pridruzivanja(true);
			return 1;
		}
		
		if (linija.equals("<lista_argumenata>")){
			int vrati = OBRADI_lista_argumenata();
			linija = mParser.ParsirajNovuLiniju();	// ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <izraz_pridruzivanja>
			OBRADI_izraz_pridruzivanja(true);
			++vrati;
			return vrati;
		}
		return -1;
	}
	
	public static void OBRADI_unarni_izraz(boolean dajAdresu, boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (linija.equals("<postfiks_izraz>")){
			OBRADI_postfiks_izraz(dajAdresu, staviNaStog);
			return;
		}
		
		if (uz != null && (uz.mNaziv.equals("OP_DEC") || uz.mNaziv.equals("OP_INC"))){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <unarni_izraz>
			OBRADI_unarni_izraz(true, false); // samo prvi rekurzivni poziv moze na stog stavit
			mIspisivac.DodajKod("POP R0", "unarni izraz: dohvati adresu");
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			mIspisivac.DodajKod("LOAD R1, (R0)", "unarni izraz: dohvati vrijednost");
			if (uz.mNaziv.equals("OP_DEC")) mIspisivac.DodajKod("SUB R1, 1, R1", "unarni izraz: umanji za jedan");
			else mIspisivac.DodajKod("ADD R1, 1, R1", "unarni izraz: uvecaj za jedan");
			mIspisivac.DodajKod("STORE R1, (R0)", "unarni izraz: spremi");
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R1", "unarni izraz: stavi rezultat na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
		
		if (linija.equals("<unarni_operator>")){
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (PLUS | MINUS | OP_TILDA | OP_NEG)
			UniformniZnak znakOp = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <cast_izraz>
			OBRADI_cast_izraz(staviNaStog);
			if (znakOp.mNaziv.equals("MINUS")){
				mIspisivac.DodajKod("POP R0");
				mIspisivac.DodajKod("XOR R0, -1, R0");
				mIspisivac.DodajKod("ADD R0, 1, R0");
				mIspisivac.DodajKod("PUSH R0");
				return;
			}
		}
	}
	
	public static void OBRADI_cast_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (linija.equals("<unarni_izraz>")){
			OBRADI_unarni_izraz(false, staviNaStog);
			return;
		}
		
		if (uz != null && uz.mNaziv.equals("L_ZAGRADA")){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <ime_tipa>
			OBRADI_ime_tipa();
			linija = mParser.ParsirajNovuLiniju();	// ucitaj D_ZAGRADA
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <cast_izraz>
			OBRADI_cast_izraz(staviNaStog);
			return;
		}
	}
	
	public static void OBRADI_ime_tipa(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (linija.equals("<specifikator_tipa>")){
			OBRADI_specifikator_tipa();
			return;
		}
		
		if (uz.mNaziv.equals("KR_CONST")){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <specifikator_tipa>
			OBRADI_specifikator_tipa();
			return;
		}
	}
	
	public static Tip OBRADI_specifikator_tipa(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip vrati;
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (uz.mNaziv.equals("KR_VOID")){
			vrati = Tip._void;
			return vrati;
		}
		
		if (uz.mNaziv.equals("KR_CHAR")){
			vrati = Tip._char;
			return vrati;
		}
		
		if (uz.mNaziv.equals("KR_INT")){
			vrati = Tip._int;
			return vrati;
		}
		
		return null;
	}
	
	public static void OBRADI_multiplikativni_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<cast_izraz>")){
			OBRADI_cast_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<multiplikativni_izraz>")){
			OBRADI_multiplikativni_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (OP_PUTA | OP_DIJELI | OP_MOD)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <cast_izraz>
			OBRADI_cast_izraz(true);
			if (uz_operator.mNaziv.equals("OP_MOD")){
				mIspisivac.mDodajFunkcijuMod = true;
				mIspisivac.DodajKod("CALL LF_MOD", "multiplikativni izraz (MOD):operandi su vec na stogu");
			}
			else{
				// nije jos implementirano
			}
			
			mIspisivac.DodajKod("POP R0", "multiplikativni izraz: skidanje operanada");
			mIspisivac.DodajKod("POP R0", "multiplikativni izraz: skidanje operanada");
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R6", "multiplikativni izraz: potrebno je rezultat staviti na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}
	
	public static void OBRADI_aditivni_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<multiplikativni_izraz>")){
			OBRADI_multiplikativni_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<aditivni_izraz>")){
			OBRADI_aditivni_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (PLUS | MINUS)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <multiplikativni_izraz>
			OBRADI_multiplikativni_izraz(true);
			mIspisivac.DodajKod("POP R1", "aditivni izraz: pocetak");
			mIspisivac.DodajKod("POP R0");
			if (uz_operator.mNaziv.equals("MINUS")) mIspisivac.DodajKod("SUB R0, R1, R0");
			else mIspisivac.DodajKod("ADD R0, R1, R0");
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "aditivni izraz: potrebno je rezultat staviti na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}

	public static void OBRADI_odnosni_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		int trenLabela = mBrojacLabela++;
		
		if (linija.equals("<aditivni_izraz>")){
			OBRADI_aditivni_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<odnosni_izraz>")){
			OBRADI_odnosni_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (OP_LT | OP_GT | OP_LTE | OP_GTE)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <aditivni_izraz>
			OBRADI_aditivni_izraz(true);
			mIspisivac.DodajKod("POP R1", "odnosni izraz " + uz_operator.mLeksickaJedinka + ": pocetak");
			mIspisivac.DodajKod("POP R0");
			if (uz_operator.mNaziv.equals("OP_LT")){
				mIspisivac.DodajKod("CMP R0, R1", "usporedba");
				mIspisivac.DodajKod("JR_SLT OI_ISTINA_" + trenLabela, "skoci na OI_ISTINA_ ako je R0 manji");
			}else if (uz_operator.mNaziv.equals("OP_GT")){
				mIspisivac.DodajKod("CMP R0, R1", "usporedba");
				mIspisivac.DodajKod("JR_SGT OI_ISTINA_" + trenLabela, "skoci na OI_ISTINA_ ako je R0 veci");
			}else if (uz_operator.mNaziv.equals("OP_LTE")){
				mIspisivac.DodajKod("CMP R0, R1", "usporedba");
				mIspisivac.DodajKod("JR_SLE OI_ISTINA_" + trenLabela, "skoci na OI_ISTINA_ ako je R0 manji ili jednak");
			}else if (uz_operator.mNaziv.equals("OP_GTE")){
				mIspisivac.DodajKod("CMP R0, R1", "usporedba");
				mIspisivac.DodajKod("JR_SGE OI_ISTINA_" + trenLabela, "skoci na OI_ISTINA_ ako je R0 veci ili jednak");
			}	
			mIspisivac.DodajKod("MOVE %D 0, R0", "izraz nije istinit");
			mIspisivac.DodajKod("JR OI_DALJE_" + trenLabela, "preskoci postavljanje R0 u 1");
			mIspisivac.PostaviSljedecuLabelu("OI_ISTINA_" + trenLabela);
			mIspisivac.DodajKod("MOVE %D 1, R0", "izraz je istinit");
			mIspisivac.PostaviSljedecuLabelu("OI_DALJE_" + trenLabela);
			
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "odnosni izraz: postavi rezultat na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}
	
	public static void OBRADI_jednakosni_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		int trenLabela = mBrojacLabela++;
		
		if (linija.equals("<odnosni_izraz>")){
			OBRADI_odnosni_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<jednakosni_izraz>")){
			OBRADI_jednakosni_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (OP_EQ | OP_NEQ)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <odnosni_izraz>
			OBRADI_odnosni_izraz(true);
			
			mIspisivac.DodajKod("POP R1", "operator jednakosnog izraza " + uz_operator.mLeksickaJedinka + ": pocetak");
			mIspisivac.DodajKod("POP R0");
			if (uz_operator.mNaziv.equals("OP_EQ")){
				mIspisivac.DodajKod("CMP R0, R1", "usporedba");
				mIspisivac.DodajKod("JR_EQ JI_ISTINA_" + trenLabela, "skoci na JI_ISTINA_ ako je R0 manji");
			}else if (uz_operator.mNaziv.equals("OP_NEQ")){
				mIspisivac.DodajKod("CMP R0, R1", "usporedba");
				mIspisivac.DodajKod("JR_NEQ JI_ISTINA_" + trenLabela, "skoci na JI_ISTINA_ ako je R0 veci");
			}
			mIspisivac.DodajKod("MOVE %D 0, R0", "izraz nije istinit");
			mIspisivac.DodajKod("JR JI_DALJE_" + trenLabela, "preskoci postavljanje R0 u 1");
			mIspisivac.PostaviSljedecuLabelu("JI_ISTINA_" + trenLabela);
			mIspisivac.DodajKod("MOVE %D 1, R0", "izraz je istinit");
			mIspisivac.PostaviSljedecuLabelu("JI_DALJE_" + trenLabela);
			
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "postavi rezultat na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}

	public static void OBRADI_bin_i_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		
		if (linija.equals("<jednakosni_izraz>")){
			OBRADI_jednakosni_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<bin_i_izraz>")){
			OBRADI_bin_i_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_BIN_I
			UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <jednakosni_izraz>
			OBRADI_jednakosni_izraz(true);
			mIspisivac.DodajKod("POP R1", "bin i izraz");
			mIspisivac.DodajKod("POP R0", "bin i izraz");
			mIspisivac.DodajKod("AND R0, R1, R0", "bin i izraz");
			
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "bin i izraz");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}

	public static void OBRADI_bin_xili_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		
		if (linija.equals("<bin_i_izraz>")){
			OBRADI_bin_i_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<bin_xili_izraz>")){
			OBRADI_bin_xili_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_BIN_XILI
			UniformniZnak.SigurnoStvaranje(linija);		
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <bin_i_izraz>
			OBRADI_bin_i_izraz(true);
			mIspisivac.DodajKod("POP R1", "bin xili izraz");
			mIspisivac.DodajKod("POP R0", "bin xili izraz");
			mIspisivac.DodajKod("XOR R0, R1, R0", "bin xili izraz");
		
			// makni 2 sa stoga i stavi novi
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);	
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "bin xili izraz");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}
	
	public static void OBRADI_bin_ili_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		
		if (linija.equals("<bin_xili_izraz>")){
			OBRADI_bin_xili_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<bin_ili_izraz>")){
			OBRADI_bin_ili_izraz(true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_BIN_ILI
			UniformniZnak.SigurnoStvaranje(linija);		
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <bin_xili_izraz>
			OBRADI_bin_xili_izraz(true);
			mIspisivac.DodajKod("POP R1", "bin ili izraz");
			mIspisivac.DodajKod("POP R0", "bin ili izraz");
			mIspisivac.DodajKod("OR R0, R1, R0", "bin ili izraz");
		
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);	
			
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "bin ili izraz");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}

	public static void OBRADI_log_i_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		int trenLabela = mBrojacLabela++;
		
		if (linija.equals("<bin_ili_izraz>")){
			OBRADI_bin_ili_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<log_i_izraz>")){
			OBRADI_log_i_izraz(true);
			mIspisivac.DodajKod("POP R0", "log i izraz: provjeri rani uvjet");
			mIspisivac.DodajKod("CMP R0, 0");
			mIspisivac.DodajKod("JR_EQ LII_KRAJ_" + trenLabela, "log i izraz: ako je rani uvjet zadovoljen preskoci ostatak logickog izraza");
			mIspisivac.DodajKod("PUSH R0", "log i izraz: ako nije vrati R0 na stog za daljnju obradu");
			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_I
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <bin_ili_izraz>
			OBRADI_bin_ili_izraz(true);
			
			mIspisivac.DodajKod("POP R1", "log i izraz");
			mIspisivac.DodajKod("POP R0", "log i izraz");
			mIspisivac.DodajKod("AND R0, R1, R0", "log i izraz");
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			mIspisivac.PostaviSljedecuLabelu("LII_KRAJ_" + trenLabela);
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "log i izraz");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}

	public static void OBRADI_log_ili_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		int trenLabela = mBrojacLabela++;
		
		if (linija.equals("<log_i_izraz>")){
			OBRADI_log_i_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<log_ili_izraz>")){
			OBRADI_log_ili_izraz(true);
			mIspisivac.DodajKod("POP R0", "log ili izraz: provjeri rani uvjet");
			mIspisivac.DodajKod("CMP R0, 1");
			mIspisivac.DodajKod("JR_EQ LIILI_KRAJ_" + trenLabela, "log ili izraz: ako je rani uvjet zadovoljen preskoci ostatak logickog izraza");
			mIspisivac.DodajKod("PUSH R0", "log ili izraz: ako nije vrati R0 na stog za daljnju obradu");
			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_ILI
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <log_i_izraz>
			OBRADI_log_i_izraz(true);
			
			mIspisivac.DodajKod("POP R1", "log ili izraz");
			mIspisivac.DodajKod("POP R0", "log ili izraz");
			mIspisivac.DodajKod("OR R0, R1, R0", "log ili izraz");
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			mIspisivac.PostaviSljedecuLabelu("LIILI_KRAJ_" + trenLabela);
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R0", "log ili izraz");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}			
			return;
		}
	}

	public static void OBRADI_izraz_pridruzivanja(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();	
		
		if (linija.equals("<log_ili_izraz>")){
			OBRADI_log_ili_izraz(staviNaStog);
			return;
		}
		
		if (linija.equals("<postfiks_izraz>")){
			OBRADI_postfiks_izraz(true, true);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_PRIDRUZI			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_pridruzivanja>
			OBRADI_izraz_pridruzivanja(true);
			mIspisivac.DodajKod("POP R1", "izraz pridruzivanja: dohvati vrijednost");
			mIspisivac.DodajKod("POP R0", "izraz pridruzivanja: dohvati adresu");
			mIspisivac.DodajKod("STORE R1, (R0)", "izraz pridruzivanja: spremi");			
			
			// makni 2 sa stoga i stavi novi ako je potrebno
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);
			NaredbenaStrukturaPrograma.mStog.remove(NaredbenaStrukturaPrograma.mStog.size()-1);	
						
			if (staviNaStog){
				mIspisivac.DodajKod("PUSH R1", "izraz pridruzivanja: potrebno je staviti na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma.mStog.add(novi);
			}
			return;
		}
	}

	public static void OBRADI_izraz(boolean staviNaStog){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<izraz_pridruzivanja>")){
			OBRADI_izraz_pridruzivanja(staviNaStog);
			return;
		}
		
		if (linija.equals("<izraz>")){
			OBRADI_izraz(false);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_pridruzivanja>
			OBRADI_izraz_pridruzivanja(staviNaStog);
			return;
		}
	}
}
