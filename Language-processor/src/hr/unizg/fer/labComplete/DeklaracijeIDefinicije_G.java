package hr.unizg.fer.labComplete;

import java.util.ArrayList;
import java.util.List;

public class DeklaracijeIDefinicije_G {
	
	public static Parser_G mParser;
	public static FRISC_ispisivac mIspisivac;
	public static List<Ime_Velicina_Adresa> mGlobalneVariable = new ArrayList<Ime_Velicina_Adresa>();
	public static String mLabelaTrenutacneFunkcije = null;
	public static int mBrojParametaraTrenutacneFunkcije = 0;
	
	public static void OBRADI_definicija_funkcije(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj <ime_tipa>
		Izrazi_G.OBRADI_ime_tipa();
		linija = mParser.ParsirajNovuLiniju(); // ucitaj IDN
		UniformniZnak uz_idn = UniformniZnak.SigurnoStvaranje(linija);
		linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
		linija = mParser.ParsirajNovuLiniju();
		mLabelaTrenutacneFunkcije = "F_" + uz_idn.mLeksickaJedinka.toUpperCase();
		mIspisivac.DodajKod(null); // radi urednosti
		mIspisivac.PostaviSljedecuLabelu(mLabelaTrenutacneFunkcije);
		if (NaredbenaStrukturaPrograma_G.PretraziIme_Glo(uz_idn.mLeksickaJedinka) == -1){ // ako nije vec deklarirana
			mIspisivac.DodajGlobalnuVarijablu("G_" + uz_idn.mLeksickaJedinka.toUpperCase(), "DW %D 0");
			Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
			novi.mIme = uz_idn.mLeksickaJedinka;
			novi.mAdresa = true;
			novi.mVelicina = 4;
			NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.add(novi);
		}
		mIspisivac.DodajPreMainKod("MOVE " + mLabelaTrenutacneFunkcije + ", R0", "definicija funkcije " + uz_idn.mLeksickaJedinka);
		mIspisivac.DodajPreMainKod("STORE R0, (G_" + uz_idn.mLeksickaJedinka.toUpperCase() + ")");
		
		// <<<<<<<<<<<<   funkcija koja prima parametre   >>>>>>>>>>>>
		if (linija.equals("<lista_parametara>")){ 
			List<Ime_Velicina_Adresa> lista_parametara = OBRADI_lista_parametara();			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <slozena_naredba>
			NaredbenaStrukturaPrograma_G.mStog = lista_parametara;
			Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa(); // dodaj return vrijednost na vrh stoga
			mBrojParametaraTrenutacneFunkcije = lista_parametara.size() + 1;
			novi.mIme = null;
			novi.mAdresa = false;
			novi.mVelicina = 4;
			NaredbenaStrukturaPrograma_G.mStog.add(novi);
			NaredbenaStrukturaPrograma_G.OBRADI_slozena_naredba();
			
			mLabelaTrenutacneFunkcije = null;
			NaredbenaStrukturaPrograma_G.mStog = null;
			mIspisivac.DodajKod("RET", "dodaj RET u slucaju da nije bio eksplicitno pozvan");
			return;
		}
		
		// <<<<<<<<<<<<   funkcija koja ne prima parametre   >>>>>>>>>>>>
		UniformniZnak uz_void = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_void.mNaziv.equals("KR_VOID")){ 
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
			
			// ulazak u funkciju
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <slozena_naredba>
			List<Ime_Velicina_Adresa> lista_parametara = new ArrayList<Ime_Velicina_Adresa>();
			NaredbenaStrukturaPrograma_G.mStog = lista_parametara;
			Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa(); // dodaj return vrijednost na vrh stoga
			mBrojParametaraTrenutacneFunkcije = lista_parametara.size() + 1;
			novi.mIme = null;
			novi.mAdresa = false;
			novi.mVelicina = 4;
			NaredbenaStrukturaPrograma_G.mStog.add(novi);
			NaredbenaStrukturaPrograma_G.OBRADI_slozena_naredba();
			
			mLabelaTrenutacneFunkcije = null;
			NaredbenaStrukturaPrograma_G.mStog = null;
			mIspisivac.DodajKod("RET", "dodaj RET u slucaju da nije bio eksplicitno pozvan");
			return;
		}
	}

	public static List<Ime_Velicina_Adresa> OBRADI_lista_parametara(){
		String linija = mParser.ParsirajNovuLiniju();
		List<Ime_Velicina_Adresa> vrati;
		
		if (linija.equals("<deklaracija_parametra>")){
			Ime_Velicina_Adresa deklaracija_parametra = OBRADI_deklaracija_parametra();
			vrati = new ArrayList<Ime_Velicina_Adresa>();
			vrati.add(deklaracija_parametra);
			return vrati;
		}
		
		if (linija.equals("<lista_parametara>")){
			List<Ime_Velicina_Adresa> lista_parametara = OBRADI_lista_parametara();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <deklaracija_parametra>
			Ime_Velicina_Adresa deklaracija_parametra = OBRADI_deklaracija_parametra();
			
			lista_parametara.add(deklaracija_parametra);
			vrati = lista_parametara;
			return vrati;
		}
		
		return null;
	}
	
	public static Ime_Velicina_Adresa OBRADI_deklaracija_parametra(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj <ime_tipa>
		Izrazi_G.OBRADI_ime_tipa();
		Ime_Velicina_Adresa vrati;
		linija = mParser.ParsirajNovuLiniju(); // ucitaj IDN
		UniformniZnak uz_idn = UniformniZnak.SigurnoStvaranje(linija);
		
		linija = mParser.DohvatiProviriVrijednost(); // peek-aj L_UGL_ZAGRADA
		Boolean jeNiz = false;
		UniformniZnak uz_l_ugl_zagrada = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_l_ugl_zagrada != null && uz_l_ugl_zagrada.mNaziv.equals("L_UGL_ZAGRADA")){
			jeNiz = true;
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_UGL_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_UGL_ZAGRADA
		}
		
		vrati = new Ime_Velicina_Adresa();
		vrati.mIme = uz_idn.mLeksickaJedinka;
		vrati.mAdresa = jeNiz;
		vrati.mVelicina = 4;
		
		return vrati;
	}
	
	public static void OBRADI_lista_deklaracija(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<deklaracija>")){
			OBRADI_deklaracija();
			return;
		}
		
		if (linija.equals("<lista_deklaracija>")){
			OBRADI_lista_deklaracija();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <deklaracija>
			OBRADI_deklaracija();
			return;
		}
		
		return;
	}
	
	public static void OBRADI_deklaracija(){
		mParser.ParsirajNovuLiniju(); // ucitaj <ime_tipa>
		Izrazi_G.OBRADI_ime_tipa();
		mParser.ParsirajNovuLiniju(); // ucitaj <lista_init_deklaratora>
		OBRADI_lista_init_deklaratora();
		mParser.ParsirajNovuLiniju(); // ucitaj TOCKAZAREZ
		
		return;
	}
	
	public static void OBRADI_lista_init_deklaratora(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<init_deklarator>")){
			OBRADI_init_deklarator();
			return;
		}
		
		if (linija.equals("<lista_init_deklaratora>")){
			OBRADI_lista_init_deklaratora();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <ZAREZ>
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <init_deklarator>
			OBRADI_init_deklarator();
			return;
		}
		
		return;
	}
	
	public static void OBRADI_init_deklarator(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj <izravni_deklarator>
		int trenLabela = Izrazi_G.mBrojacLabela++;
		int stogPrijeIzravnogDek_Lok = (NaredbenaStrukturaPrograma_G.mStog == null) ? 0 : NaredbenaStrukturaPrograma_G.mStog.size();
		int stogPrijeIzravnogDek_Glo = NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.size();
		Ime_Velicina_Adresa izravni_deklarator = OBRADI_izravni_deklarator(trenLabela);
		int stogPoslijeIzravnogDek_Lok = (NaredbenaStrukturaPrograma_G.mStog == null) ? 0 : NaredbenaStrukturaPrograma_G.mStog.size();
		int stogPoslijeIzravnogDek_Glo = NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.size();
		int razlikaStoga_Lok = stogPoslijeIzravnogDek_Lok - stogPrijeIzravnogDek_Lok;
		int razlikaStoga_Glo = stogPoslijeIzravnogDek_Glo - stogPrijeIzravnogDek_Glo;
		linija = mParser.DohvatiProviriVrijednost();
		UniformniZnak uz_op_pridruzi = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_op_pridruzi != null && uz_op_pridruzi.mNaziv.equals("OP_PRIDRUZI")){
			// doslo je do pridruzivanja pa makni nule sa stoga
			if (NaredbenaStrukturaPrograma_G.mStog == null){
				for (int i = 0; i < razlikaStoga_Glo; ++i){
					mIspisivac.DodajPreMainKod("POP R0",
							"deklaracija " + izravni_deklarator.mIme + ": biti ce pridruzivanje pa micem null inicjalizaciju");
					NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.remove(NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.size()-1);
				}
			}else{
				for (int i = 0; i < razlikaStoga_Lok; ++i){
					mIspisivac.DodajKod("POP R0",
							"deklaracija " + izravni_deklarator.mIme + ": biti ce pridruzivanje pa micem null inicjalizaciju");
					NaredbenaStrukturaPrograma_G.mStog.remove(NaredbenaStrukturaPrograma_G.mStog.size()-1);
				}
			}
			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_PRIDRUZI
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <inicijalizator>
			OBRADI_inicijalizator(trenLabela);
		}
		
		if (NaredbenaStrukturaPrograma_G.mStog == null){
			mIspisivac.DodajPreMainKod("POP R0",
					"deklaracija " + izravni_deklarator.mIme + ": dohvacanje vrijednosti");
			mIspisivac.DodajPreMainKod("STORE R0, (G_" + izravni_deklarator.mIme.toUpperCase() + ")",
					"deklaracija " + izravni_deklarator.mIme + ": spremanje na odrediste");
			mIspisivac.DodajGlobalnuVarijablu("G_" + izravni_deklarator.mIme.toUpperCase(), "DW %D 0");
			NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.get(NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.size()-1).mIme = izravni_deklarator.mIme;
		}else{
			NaredbenaStrukturaPrograma_G.mStog.get(NaredbenaStrukturaPrograma_G.mStog.size()-1).mIme = izravni_deklarator.mIme;
		}
		
		return;
	}

	public static Ime_Velicina_Adresa OBRADI_izravni_deklarator(int trenutacnaLabela){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj IDN
		UniformniZnak uz_idn = UniformniZnak.SigurnoStvaranje(linija);
		
		linija = mParser.DohvatiProviriVrijednost();
		UniformniZnak uz_1 = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_1.mNaziv.equals("L_UGL_ZAGRADA")){ // <<<< deklariranje nizova >>>>
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_UGL_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj BROJ
			UniformniZnak uz_broj = UniformniZnak.SigurnoStvaranje(linija);
			int broj = Integer.parseInt(uz_broj.mLeksickaJedinka);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_UGL_ZAGRADA
			
			// neznamo dali ce biti pridruzena vrijednost pri deklaraciji pa inicijaliziramo cijelo polje na nulu
			if (NaredbenaStrukturaPrograma_G.mStog == null){ // u globalnom smo djelokrugu
				for (int i = 0; i < broj; ++i){
					mIspisivac.DodajGlobalnuVarijablu("TEMP_" + (trenutacnaLabela + i), "DW %D 0");
				}
				mIspisivac.DodajPreMainKod("MOVE TEMP_" + (trenutacnaLabela + broj - 1) + ", R0", "inicijalizacija polja: spremanje adrese pocetnog clana");
				mIspisivac.DodajPreMainKod("PUSH R0");
				Ime_Velicina_Adresa vrati = new Ime_Velicina_Adresa();
				vrati.mIme = uz_idn.mLeksickaJedinka;
				vrati.mAdresa = true;
				vrati.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.add(vrati);
				return vrati;
			}else{ // u lokalnom smo djelokrugu
				mIspisivac.DodajKod("MOVE %D 0, R0", "inicijaliziraj polje na nulu");
				for (int i = 0; i < broj; ++i){
					mIspisivac.DodajKod("PUSH R0");
					Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
					novi.mIme = null;
					novi.mAdresa = false;
					novi.mVelicina = 4;
					NaredbenaStrukturaPrograma_G.mStog.add(novi);
				}
				mIspisivac.DodajKod("PUSH R7", "inicijalizacija polja: dohvati adr zadnjeg clana na vrhu stoga (korak 1)");
				mIspisivac.DodajKod("POP R0", "inicijalizacija polja: dohvati adr zadnjeg clana na vrhu stoga (korak 2)");
				mIspisivac.DodajKod("ADD R0, 4, R0", "inicijalizacija polja: dohvati adr zadnjeg clana na vrhu stoga (korak 3)");
				mIspisivac.DodajKod("ADD R0, " + 4*broj + ", R0",
						"inicijalizacija polja: dohvati adr prvog clana");
				mIspisivac.DodajKod("PUSH R0", "inicijalizacija polja: stavi je na stog");
				Ime_Velicina_Adresa vrati = new Ime_Velicina_Adresa();
				vrati.mIme = uz_idn.mLeksickaJedinka;
				vrati.mAdresa = true;
				vrati.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mStog.add(vrati);
				return vrati;
			}
		}
		
		else if (uz_1.mNaziv.equals("L_ZAGRADA")){ // <<<<< deklaracije funkcija >>>>>
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
			linija = mParser.ParsirajNovuLiniju();
			if (linija.equals("<lista_parametara>")){ // -------- funkcija sa parametrima --------
				OBRADI_lista_parametara();
				linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA 
			}
			else{ // --------- funkcija bez parametara ----------
				linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA 
			}
			
			if (NaredbenaStrukturaPrograma_G.mStog == null){ // u globalnom smo djelokrugu
				mIspisivac.DodajPreMainKod("MOVE F_" + uz_idn.mLeksickaJedinka.toUpperCase() + ", R0",
						"deklaracija funkcije " + uz_idn.mLeksickaJedinka);
				mIspisivac.DodajPreMainKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = uz_idn.mLeksickaJedinka;
				novi.mAdresa = true;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.add(novi);
				return novi;
			}else{ // u lokalnom smo djelokrugu
				mIspisivac.DodajKod("MOVE F_" + uz_idn.mLeksickaJedinka.toUpperCase() + ", R0",
						"deklaracija funkcije " + uz_idn.mLeksickaJedinka);
				mIspisivac.DodajKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = uz_idn.mLeksickaJedinka;
				novi.mAdresa = true;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mStog.add(novi);
				return novi;
			}
		}
		
		else{ // <<<<<generiranje varijabli cjelobrojnog tipa>>>>>
			if (NaredbenaStrukturaPrograma_G.mStog != null){ // jesmo li u globalnom djelokrugu ili u funkciji
				mIspisivac.DodajKod("MOVE %D 0, R0", "izravni deklarator cijelobrojne varijable: postavi na nulu");
				mIspisivac.DodajKod("PUSH R0");
				Ime_Velicina_Adresa vrati = new Ime_Velicina_Adresa();
				vrati.mIme = uz_idn.mLeksickaJedinka;
				vrati.mAdresa = false;
				vrati.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mStog.add(vrati);
				return vrati;
			}else{
				mIspisivac.DodajPreMainKod("MOVE %D 0, R0", "izravni deklarator cijelobrojne varijable: postavi na nulu");
				mIspisivac.DodajPreMainKod("PUSH R0");
				Ime_Velicina_Adresa vrati = new Ime_Velicina_Adresa();
				vrati.mIme = uz_idn.mLeksickaJedinka;
				vrati.mAdresa = false;
				vrati.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.add(vrati);
				return vrati;
			}
		}
	}
	
	public static void OBRADI_inicijalizator(int trenutacnaLabela){
		String linija = mParser.ParsirajNovuLiniju();
		boolean jeNizIliLista = false;
		int brojClanova = 0;
		if (linija.equals("<izraz_pridruzivanja>")){
			Izrazi_G.OBRADI_izraz_pridruzivanja(true);
			if (Izrazi_G.mZadnjiPrimarniIzrazJe_NIZ_ZNAKOVA){
				jeNizIliLista = true;
				brojClanova = Izrazi_G.mDuljina_NIZ_ZNAKOVA;
			}
		}else{ // L_VIT_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <lista_izraza_pridruzivanja>
			jeNizIliLista = true;
			brojClanova = OBRADI_lista_izraza_pridruzivanja();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_VIT_ZAGRADA
		}
		
		if (jeNizIliLista){
			if (NaredbenaStrukturaPrograma_G.mStog == null){ // u globalnom smo djelokrugu
				for (int i = brojClanova; i > 0; --i){
					mIspisivac.DodajPreMainKod("POP R0", "inicijalizacija polja: dohvacanje clana br " + i);
					mIspisivac.DodajPreMainKod("STORE R0, (TEMP_" + (trenutacnaLabela + brojClanova - i) + ")", "inicijalizacija polja: spremanje clana br " + i);
					// mIspisivac.DodajGlobalnuVarijablu("TEMP_" + Izrazi_Sem.mBrojacLabela++, "DW %D 0"); // vec dodano u izravnom deklaratoru
					NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.remove(NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.size()-1);
				}
				mIspisivac.DodajPreMainKod("MOVE TEMP_" + (trenutacnaLabela + brojClanova - 1) + ", R0", "inicijalizacija polja: spremanje adrese pocetnog clana");
				mIspisivac.DodajPreMainKod("PUSH R0");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mGlobalniDjelokrug.add(novi);
			}else{ // u lokalnom smo djelokrugu
				mIspisivac.DodajKod("PUSH R7", "inicijalizacija polja: dohvati adr zadnjeg clana na vrhu stoga (korak 1)");
				mIspisivac.DodajKod("POP R0", "inicijalizacija polja: dohvati adr zadnjeg clana na vrhu stoga (korak 2)");
				mIspisivac.DodajKod("ADD R0, 4, R0", "inicijalizacija polja: dohvati adr zadnjeg clana na vrhu stoga (korak 3)");
				mIspisivac.DodajKod("ADD R0, " + 4*(brojClanova - 1) + ", R0",
						"inicijalizacija polja: dohvati adr prvog clana");
				mIspisivac.DodajKod("PUSH R0", "inicijalizacija polja: stavi je na stog");
				Ime_Velicina_Adresa novi = new Ime_Velicina_Adresa();
				novi.mIme = null;
				novi.mAdresa = false;
				novi.mVelicina = 4;
				NaredbenaStrukturaPrograma_G.mStog.add(novi);
			}
		}
	}
	
	public static int OBRADI_lista_izraza_pridruzivanja(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<izraz_pridruzivanja>")){
			Izrazi_G.OBRADI_izraz_pridruzivanja(true);
			return 1;
		}else{ // <lista_izraza_pridruzivanja>
			int brojClanovaDosad = OBRADI_lista_izraza_pridruzivanja();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_pridruzivanja>
			Izrazi_G.OBRADI_izraz_pridruzivanja(true);
			return brojClanovaDosad + 1;
		}
	}
}
