package hr.unizg.fer.lab3;

import java.util.ArrayList;
import java.util.List;

public class DeklaracijeIDefinicije {
	
	public static StablastaTablicaZnakova mSTZ;
	public static Parser mParser;
	
	public static void PROVJERI_definicija_funkcije(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj <ime_tipa>
		Tip_Const ime_tipa = Izrazi.PROVJERI_ime_tipa();
		Boolean writeToOutputEnabledWasTrue = Utilities.mWriteToOutputEnabled;
		Boolean bezGreskeZasad = true;
		if (ime_tipa.mTip != Tip._void && ime_tipa.mTip != Tip._int && ime_tipa.mTip != Tip._char){
			Utilities.mWriteToOutputEnabled = false;
			bezGreskeZasad = false;
		}
		
		linija = mParser.ParsirajNovuLiniju(); // ucitaj IDN
		UniformniZnak uz_idn = UniformniZnak.SigurnoStvaranje(linija);
		ClanTabliceZnakova clTablice = mSTZ.DohvatiClanIzTabliceZnakova(uz_idn.mLeksickaJedinka);
		if (clTablice != null && clTablice.mDefinirano == true){
			Utilities.mWriteToOutputEnabled = false;
			bezGreskeZasad = false;
		}
		
		linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
		UniformniZnak uz_l_zagrada = UniformniZnak.SigurnoStvaranje(linija);
		linija = mParser.ParsirajNovuLiniju();
		
		// <<<<<<<<<<<<   funkcija koja nije f(void), vec prima parametre   >>>>>>>>>>>>
		if (linija.equals("<lista_parametara>")){ 
			List<Tip_LIzraz_Const_Niz_Ime> lista_parametara = PROVJERI_lista_parametara();
			
			if (clTablice != null){
				if ((clTablice.mTipFunkcija.mPov != ime_tipa.mTip)) bezGreskeZasad = false;
				if (lista_parametara.size() != clTablice.mTipFunkcija.mParam.size()) bezGreskeZasad = false;
				else {
					for (int i = 0; i < lista_parametara.size(); ++i)
						if (lista_parametara.get(i).mT.mTip != clTablice.mTipFunkcija.mParam.get(i).mTip ||
								lista_parametara.get(i).mT.mNiz != clTablice.mTipFunkcija.mParam.get(i).mNiz ||
								lista_parametara.get(i).mT.mConst != clTablice.mTipFunkcija.mParam.get(i).mConst ||
								lista_parametara.get(i).mT.mNiz){
							bezGreskeZasad = false;
							break;
						}
				}
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
			UniformniZnak uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
			if (writeToOutputEnabledWasTrue) Utilities.mWriteToOutputEnabled = true;
			if (!bezGreskeZasad){
				String greska = "<definicija_funkcije> ::= <ime_tipa> " + uz_idn.FormatZaIspis() + " " + uz_l_zagrada.FormatZaIspis() +
						" <lista_parametara> " + uz_d_zagrada.FormatZaIspis() + " <slozena_naredba>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			// definiraj funkciju
			ClanTabliceZnakova clTab = new ClanTabliceZnakova();
			clTab.mTip = Tip._funkcija;
			clTab.mConst = false;
			clTab.mL_izraz = false;
			clTab.mNiz = false;
			clTab.mDefinirano = true;
			clTab.mTipFunkcija = new TipFunkcija();
			clTab.mTipFunkcija.mPov = ime_tipa.mTip;
			for (int i = 0; i < lista_parametara.size(); ++i){
				Tip_Const_Niz tcn = new Tip_Const_Niz();
				tcn.mTip = lista_parametara.get(i).mT.mTip;
				tcn.mConst = lista_parametara.get(i).mT.mConst;
				tcn.mNiz = lista_parametara.get(i).mT.mNiz;
				clTab.mTipFunkcija.mParam.add(tcn);
			}
			mSTZ.DodajClanUTablicuZnakova(uz_idn.mLeksickaJedinka, clTab);
			mSTZ.Definiran(uz_idn.mLeksickaJedinka);
			
			// ulazak u funkciju i deklaracija parametara u novom cvoru STZ
			mSTZ.UdjiUNoviCvor(clTab);
			for (int i = 0; i < lista_parametara.size(); ++i){
				ClanTabliceZnakova clTabPara = new ClanTabliceZnakova();
				clTabPara.mTip = lista_parametara.get(i).mT.mTip;
				clTabPara.mConst = lista_parametara.get(i).mT.mConst;
				clTabPara.mL_izraz = lista_parametara.get(i).mT.mL_izraz;
				clTabPara.mNiz = lista_parametara.get(i).mT.mNiz;
				clTabPara.mDefinirano = true;
				clTabPara.mTipFunkcija = null;
				mSTZ.DodajClanUTablicuZnakova(lista_parametara.get(i).mIme, clTabPara);
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <slozena_naredba>
			NaredbenaStrukturaPrograma.PROVJERI_slozena_naredba();
			mSTZ.IzadjiIzCvora();
			return;
		}
		
		// <<<<<<<<<<<<   funkcija koja ne prima parametre   >>>>>>>>>>>>
		UniformniZnak uz_void = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_void.mNaziv.equals("KR_VOID")){ 
			if (clTablice != null){
				if ((clTablice.mTipFunkcija.mPov != ime_tipa.mTip)) bezGreskeZasad = false;
				if (clTablice.mTipFunkcija.mParam.size() != 0) bezGreskeZasad = false;
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
			UniformniZnak uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
			if (writeToOutputEnabledWasTrue) Utilities.mWriteToOutputEnabled = true;
			if (!bezGreskeZasad){
				String greska = "<definicija_funkcije> ::= <ime_tipa> " + uz_idn.FormatZaIspis() + " " + uz_l_zagrada.FormatZaIspis() +
						" " + uz_void.FormatZaIspis() + " " + uz_d_zagrada.FormatZaIspis() + " <slozena_naredba>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			// definiraj funkciju
			ClanTabliceZnakova clTab = new ClanTabliceZnakova();
			clTab.mTip = Tip._funkcija;
			clTab.mConst = false;
			clTab.mL_izraz = false;
			clTab.mNiz = false;
			clTab.mDefinirano = true;
			clTab.mTipFunkcija = new TipFunkcija();
			clTab.mTipFunkcija.mPov = ime_tipa.mTip;
			// clTab.mTipFunkcija.mParam ostavim na 0
			mSTZ.DodajClanUTablicuZnakova(uz_idn.mLeksickaJedinka, clTab);
			mSTZ.Definiran(uz_idn.mLeksickaJedinka);
			
			// ulazak u funkciju
			mSTZ.UdjiUNoviCvor(clTab);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <slozena_naredba>
			NaredbenaStrukturaPrograma.PROVJERI_slozena_naredba();
			mSTZ.IzadjiIzCvora();
			return;
		}
	}

	public static List<Tip_LIzraz_Const_Niz_Ime> PROVJERI_lista_parametara(){
		String linija = mParser.ParsirajNovuLiniju();
		List<Tip_LIzraz_Const_Niz_Ime> vrati;
		
		if (linija.equals("<deklaracija_parametra>")){
			Tip_LIzraz_Const_Niz_Ime deklaracija_parametra = PROVJERI_deklaracija_parametra();
			vrati = new ArrayList<Tip_LIzraz_Const_Niz_Ime>();
			vrati.add(deklaracija_parametra);
			return vrati;
		}
		
		if (linija.equals("<lista_parametara>")){
			List<Tip_LIzraz_Const_Niz_Ime> lista_parametara = PROVJERI_lista_parametara();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj ZAREZ
			UniformniZnak uz_zarez = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <deklaracija_parametra>
			Tip_LIzraz_Const_Niz_Ime deklaracija_parametra = PROVJERI_deklaracija_parametra();
			
			Boolean ponovljeniIdentifikator = false;
			for (int i = 0; i < lista_parametara.size(); ++i){
				if (lista_parametara.get(i).mIme.equals(deklaracija_parametra.mIme)){
					ponovljeniIdentifikator = true;
					break;
				}				
			}
			if (ponovljeniIdentifikator){
				String greska = "<lista_parametara> ::= <lista_parametara> " + uz_zarez.FormatZaIspis() + " <deklaracija_parametra>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			lista_parametara.add(deklaracija_parametra);
			vrati = lista_parametara;
			return vrati;
		}
		
		return null;
	}
	
	public static Tip_LIzraz_Const_Niz_Ime PROVJERI_deklaracija_parametra(){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj <ime_tipa>
		Tip_Const ime_tipa = Izrazi.PROVJERI_ime_tipa();
		Tip_LIzraz_Const_Niz_Ime vrati;
		linija = mParser.ParsirajNovuLiniju(); // ucitaj IDN
		UniformniZnak uz_idn = UniformniZnak.SigurnoStvaranje(linija);
		
		linija = mParser.DohvatiProviriVrijednost(); // peek-aj L_UGL_ZAGRADA
		Boolean jeNiz = false;
		UniformniZnak uz_l_ugl_zagrada = UniformniZnak.SigurnoStvaranje(linija);
		UniformniZnak uz_d_ugl_zagrada = null;
		if (uz_l_ugl_zagrada != null && uz_l_ugl_zagrada.mLeksickaJedinka.equals("L_UGL_ZAGRADA")){
			jeNiz = true;
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_UGL_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_UGL_ZAGRADA
			uz_d_ugl_zagrada = UniformniZnak.SigurnoStvaranje(linija);
		}
		
		if (ime_tipa.mTip == Tip._void){
			String greska = "<deklaracija_parametra> ::= <ime_tipa> " + uz_idn.FormatZaIspis();
			if (jeNiz) greska += " " + uz_l_ugl_zagrada.FormatZaIspis() + " " + uz_d_ugl_zagrada.FormatZaIspis();
			Utilities.WriteStringLineToOutputAndExit(greska);
		}
		
		vrati = new Tip_LIzraz_Const_Niz_Ime();
		vrati.mIme = uz_idn.mLeksickaJedinka;
		vrati.mT = new Tip_LIzraz_Const_Niz();
		vrati.mT.mConst = ime_tipa.mConst;
		vrati.mT.mFun = null;
		vrati.mT.mL_izraz = !ime_tipa.mConst;
		vrati.mT.mNiz = jeNiz;
		vrati.mT.mTip = ime_tipa.mTip;
		
		return vrati;
	}
	
	public static void PROVJERI_lista_deklaracija(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<deklaracija>")){
			PROVJERI_deklaracija();
			return;
		}
		
		if (linija.equals("<lista_deklaracija>")){
			PROVJERI_lista_deklaracija();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <deklaracija>
			PROVJERI_deklaracija();
			return;
		}
		
		return;
	}
	
	public static void PROVJERI_deklaracija(){
		mParser.ParsirajNovuLiniju(); // ucitaj <ime_tipa>
		Tip_Const ime_tipa = Izrazi.PROVJERI_ime_tipa();
		mParser.ParsirajNovuLiniju(); // ucitaj <lista_init_deklaratora>
		PROVJERI_lista_init_deklaratora(ime_tipa);
		mParser.ParsirajNovuLiniju(); // ucitaj TOCKAZAREZ
		
		return;
	}
	
	public static void PROVJERI_lista_init_deklaratora(Tip_Const ime_tipa){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<init_deklarator>")){
			PROVJERI_init_deklarator(ime_tipa);
			return;
		}
		
		if (linija.equals("<lista_init_deklaratora>")){
			PROVJERI_lista_init_deklaratora(ime_tipa);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <ZAREZ>
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <init_deklarator>
			PROVJERI_init_deklarator(ime_tipa);
			return;
		}
		
		return;
	}
	
	public static void PROVJERI_init_deklarator(Tip_Const ime_tipa){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj <izravni_deklarator>
		Tip_Const_Niz_BrEle_TipFunk izravni_deklarator = PROVJERI_izravni_deklarator(ime_tipa);
		
		linija = mParser.DohvatiProviriVrijednost();
		Boolean pogreska = false, pridruzivanje = false;
		UniformniZnak uz_op_pridruzi = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_op_pridruzi != null && uz_op_pridruzi.mNaziv.equals("OP_PRIDRUZI")){
			pridruzivanje = true;
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_PRIDRUZI
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <inicijalizator>
			List<Tip_LIzraz_Const_Niz> inicijalizator = PROVJERI_inicijalizator();
			
			if (Utilities.JeBrojevniTip(izravni_deklarator.mTip) && !izravni_deklarator.mNiz){ // nije niz
				if (!Utilities.ImplicitnaPretvorbaMoguca(inicijalizator.get(0).mTip, izravni_deklarator.mTip)) pogreska = true;
			}else if (Utilities.JeBrojevniTip(izravni_deklarator.mTip) && izravni_deklarator.mNiz){ // je niz
				if (izravni_deklarator.mBrElemenata < inicijalizator.size()) pogreska = true;
				 // svaki clan u polju iniccijalizacije se mora implicitno moc pretvorit u tip izravnog_deklaratora
				for (int i = 0; i < inicijalizator.size(); ++i)
					if (!Utilities.ImplicitnaPretvorbaMoguca(inicijalizator.get(i).mTip, izravni_deklarator.mTip) ||
							inicijalizator.get(i).mNiz != izravni_deklarator.mNiz){
						pogreska = true;
						break;
					}
			}else pogreska = true;
		}
		
		if (!pridruzivanje && izravni_deklarator.mConst) pogreska = true; // nesmije biti const ako nema pridruzivanja
		
		if (pogreska){
			String greska = "<init_deklarator> ::= <izravni_deklarator>";
			if (pridruzivanje) greska += " " + uz_op_pridruzi.FormatZaIspis() + " <inicijalizator>";
			Utilities.WriteStringLineToOutputAndExit(greska);
		}
		
		return;
	}

	public static Tip_Const_Niz_BrEle_TipFunk PROVJERI_izravni_deklarator(Tip_Const ime_tipa){
		String linija = mParser.ParsirajNovuLiniju(); // ucitaj IDN
		UniformniZnak uz_idn = UniformniZnak.SigurnoStvaranje(linija);
		// morat ce bit null
		ClanTabliceZnakova clT = mSTZ.DohvatiClanIzTabliceZnakovaSamoTrenutacnogDjelokruga(uz_idn.mLeksickaJedinka);
		
		linija = mParser.DohvatiProviriVrijednost();
		UniformniZnak uz_1 = UniformniZnak.SigurnoStvaranje(linija);
		if (uz_1.mNaziv.equals("L_UGL_ZAGRADA")){ // <<<< deklariranje nizova >>>>
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_UGL_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj BROJ
			UniformniZnak uz_broj = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_UGL_ZAGRADA
			UniformniZnak uz_d_ugl_zagrada = UniformniZnak.SigurnoStvaranje(linija);
			
			Tip_Const_Niz_BrEle_TipFunk vrati = new Tip_Const_Niz_BrEle_TipFunk();
			ClanTabliceZnakova noviCl = new ClanTabliceZnakova();
			noviCl.mTip = vrati.mTip = ime_tipa.mTip;
			noviCl.mL_izraz = noviCl.mConst = vrati.mConst = ime_tipa.mConst;
			vrati.mBrElemenata = Integer.parseInt(uz_broj.mLeksickaJedinka);
			vrati.mTipFunkcija = null;
			noviCl.mNiz = vrati.mNiz = true;
			noviCl.mDefinirano = true;
			noviCl.mTipFunkcija = null;
			
			if (ime_tipa.mTip == Tip._void || clT != null || vrati.mBrElemenata < 0 || vrati.mBrElemenata > 1024){
				String greska = "<izravni_deklarator> ::= " + uz_idn.FormatZaIspis() + " " + uz_1.FormatZaIspis() + " " +
						uz_broj.FormatZaIspis() + " " + uz_d_ugl_zagrada.FormatZaIspis();
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			mSTZ.DodajClanUTablicuZnakova(uz_idn.mLeksickaJedinka, noviCl);
			return vrati;
		}
		
		else if (uz_1.mNaziv.equals("L_ZAGRADA")){ // <<<<< deklaracije funkcija >>>>>
			linija = mParser.ParsirajNovuLiniju(); // ucitaj L_ZAGRADA
			linija = mParser.ParsirajNovuLiniju();
			if (linija.equals("<lista_parametara>")){ // -------- funkcija sa parametrima --------
				List<Tip_LIzraz_Const_Niz_Ime> lista_parametara = PROVJERI_lista_parametara();
				linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA 
				UniformniZnak uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
				Boolean vecPostojecaDeklaracijaOdgovara = true;

				Tip_Const_Niz_BrEle_TipFunk vrati = new Tip_Const_Niz_BrEle_TipFunk();
				ClanTabliceZnakova noviCl = new ClanTabliceZnakova();
				noviCl.mTip = vrati.mTip = Tip._funkcija;
				noviCl.mL_izraz = noviCl.mConst = vrati.mConst = ime_tipa.mConst;
				vrati.mBrElemenata = -1; // nije polje
				noviCl.mTipFunkcija = vrati.mTipFunkcija = new TipFunkcija();
				vrati.mTipFunkcija.mPov = ime_tipa.mTip;
				for (int i = 0; i < lista_parametara.size(); ++i){
					Tip_Const_Niz tcn = new Tip_Const_Niz();
					tcn.mTip = lista_parametara.get(i).mT.mTip;
					tcn.mConst = lista_parametara.get(i).mT.mConst;
					tcn.mNiz = lista_parametara.get(i).mT.mNiz;
					vrati.mTipFunkcija.mParam.add(tcn);
				}
				noviCl.mNiz = vrati.mNiz = false;
				noviCl.mDefinirano = mSTZ.JeliFunkcijaDefinirana(uz_idn.mLeksickaJedinka, noviCl.mTipFunkcija);
				
				if (clT != null){
					if (clT.mTipFunkcija.mPov != ime_tipa.mTip || clT.mTipFunkcija.mParam.size() != 0)
					if (Utilities.FunkcijeIste(clT.mTipFunkcija, noviCl.mTipFunkcija))
						vecPostojecaDeklaracijaOdgovara = false;
				}				
				if (!vecPostojecaDeklaracijaOdgovara){
					String greska = "<izravni_deklarator> ::= " + uz_idn.FormatZaIspis() + " " + uz_1.FormatZaIspis() +
							" <lista_parametara> " + uz_d_zagrada.FormatZaIspis();
					Utilities.WriteStringLineToOutputAndExit(greska);
				}
				mSTZ.DodajClanUTablicuZnakova(uz_idn.mLeksickaJedinka, noviCl);
				return vrati;
			}
			else{ // --------- funkcija bez parametara ----------
				UniformniZnak uz_void = UniformniZnak.SigurnoStvaranje(linija);
				linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA 
				UniformniZnak uz_d_zagrada = UniformniZnak.SigurnoStvaranje(linija);
				Boolean vecPostojecaDeklaracijaOdgovara = true;

				Tip_Const_Niz_BrEle_TipFunk vrati = new Tip_Const_Niz_BrEle_TipFunk();
				ClanTabliceZnakova noviCl = new ClanTabliceZnakova();
				noviCl.mTip = vrati.mTip = Tip._funkcija;
				noviCl.mL_izraz = noviCl.mConst = vrati.mConst = ime_tipa.mConst;
				vrati.mBrElemenata = -1; // nije polje
				noviCl.mTipFunkcija = vrati.mTipFunkcija = new TipFunkcija();
				vrati.mTipFunkcija.mPov = ime_tipa.mTip;
				noviCl.mNiz = vrati.mNiz = false;
				noviCl.mDefinirano = mSTZ.JeliFunkcijaDefinirana(uz_idn.mLeksickaJedinka, noviCl.mTipFunkcija);
								
				if (clT != null){
					if (clT.mTipFunkcija.mPov != ime_tipa.mTip || clT.mTipFunkcija.mParam.size() != 0)
						vecPostojecaDeklaracijaOdgovara = false;
				}
				if (!vecPostojecaDeklaracijaOdgovara){
					String greska = "<izravni_deklarator> ::= " + uz_idn.FormatZaIspis() + " " + uz_1.FormatZaIspis() + " " +
							uz_void.FormatZaIspis() + " " + uz_d_zagrada.FormatZaIspis();
					Utilities.WriteStringLineToOutputAndExit(greska);
				}
				mSTZ.DodajClanUTablicuZnakova(uz_idn.mLeksickaJedinka, noviCl);
				return vrati;
			}
		}
		
		else{ // <<<<<generiranje varijabli cjelobrojnog tipa>>>>>
			Tip_Const_Niz_BrEle_TipFunk vrati = new Tip_Const_Niz_BrEle_TipFunk();
			ClanTabliceZnakova noviCl = new ClanTabliceZnakova();
			noviCl.mTip = vrati.mTip = ime_tipa.mTip;
			noviCl.mConst = vrati.mConst = ime_tipa.mConst;
			noviCl.mL_izraz = !noviCl.mConst;
			vrati.mBrElemenata = -1; // nije polje
			noviCl.mTipFunkcija = vrati.mTipFunkcija = null;
			noviCl.mNiz = vrati.mNiz = false;
			noviCl.mDefinirano = true;
			
			if (ime_tipa.mTip == Tip._void || clT != null){
				String greska = "<izravni_deklarator> ::= " + uz_idn.FormatZaIspis();
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			mSTZ.DodajClanUTablicuZnakova(uz_idn.mLeksickaJedinka, noviCl);
			return vrati;
		}
	}
	
	public static List<Tip_LIzraz_Const_Niz> PROVJERI_inicijalizator(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<izraz_pridruzivanja>")){
			Tip_LIzraz_Const_Niz izraz_pridruzivanja = Izrazi.PROVJERI_izraz_pridruzivanja();
			List<Tip_LIzraz_Const_Niz> vrati = new ArrayList<Tip_LIzraz_Const_Niz>();
			if (Izrazi.mZadnjiPrimarniIzrazJe_NIZ_ZNAKOVA){
				for (int i = 0; i < Izrazi.mDuljina_NIZ_ZNAKOVA + 1; ++i){
					Tip_LIzraz_Const_Niz priv = new Tip_LIzraz_Const_Niz();
					priv.mConst = true;
					priv.mFun = null;
					priv.mTip = Tip._char;
					priv.mNiz = false;
					priv.mL_izraz = false;
					vrati.add(priv);
				}
				return vrati;
			}else{ // nije niz znakova
				vrati.add(izraz_pridruzivanja);
				return vrati;
			}
		}else{ // L_VIT_ZAGRADA
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <lista_izraza_pridruzivanja>
			List<Tip_LIzraz_Const_Niz> lista_izraza_pridruzivanja = PROVJERI_lista_izraza_pridruzivanja();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj D_VIT_ZAGRADA
			return lista_izraza_pridruzivanja;
		}
	}
	
	public static List<Tip_LIzraz_Const_Niz> PROVJERI_lista_izraza_pridruzivanja(){
		String linija = mParser.ParsirajNovuLiniju();
		
		if (linija.equals("<izraz_pridruzivanja>")){
			Tip_LIzraz_Const_Niz izraz_pridruzivanja = Izrazi.PROVJERI_izraz_pridruzivanja();
			List<Tip_LIzraz_Const_Niz> vrati = new ArrayList<Tip_LIzraz_Const_Niz>();
			vrati.add(izraz_pridruzivanja);
			return vrati;
		}else{ // <lista_izraza_pridruzivanja>
			List<Tip_LIzraz_Const_Niz> lista_izraza_pridruzivanja = PROVJERI_lista_izraza_pridruzivanja();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_pridruzivanja>
			Tip_LIzraz_Const_Niz izraz_pridruzivanja = Izrazi.PROVJERI_izraz_pridruzivanja();
			lista_izraza_pridruzivanja.add(izraz_pridruzivanja);
			
			return lista_izraza_pridruzivanja;
		}
	}
}
