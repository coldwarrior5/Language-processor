package hr.unizg.fer.lab3;

import java.util.ArrayList;
import java.util.List;
	
public class Izrazi {
	
	public static StablastaTablicaZnakova mSTZ;
	public static Parser mParser;
	
	// ovo je zapravo vrsta izvedenih svojstava
	public static Boolean mZadnjiPrimarniIzrazJe_NIZ_ZNAKOVA = false;
	public static int mDuljina_NIZ_ZNAKOVA;
	
	public static Tip_LIzraz_Const_Niz PROVJERI_primarni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		UniformniZnak uz = new UniformniZnak(linija);
		Tip_LIzraz_Const_Niz vrati = new Tip_LIzraz_Const_Niz();
		mZadnjiPrimarniIzrazJe_NIZ_ZNAKOVA = false;
		
		if (uz.mNaziv.equals("IDN")){
			ClanTabliceZnakova cl = mSTZ.DohvatiClanIzTabliceZnakova(uz.mLeksickaJedinka);
			if (cl == null) {
				String greska = "<primarni_izraz> ::= " + uz.FormatZaIspis();
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			vrati.mL_izraz = cl.mL_izraz;
			vrati.mTip = cl.mTip;
			vrati.mFun = cl.mTipFunkcija;
			vrati.mConst = cl.mConst;
			vrati.mNiz = cl.mNiz;
			return vrati;
		}
		
		if (uz.mNaziv.equals("BROJ")){
			if (!Utilities.ProvjeriInt(uz.mLeksickaJedinka)) {
				String greska = "<primarni_izraz> ::= " + uz.FormatZaIspis();
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			vrati.mL_izraz = false;
			vrati.mTip = Tip._int;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		if (uz.mNaziv.equals("ZNAK")){
			if (!Utilities.ProvjeriChar(uz.mLeksickaJedinka)) {
				String greska = "<primarni_izraz> ::= " + uz.FormatZaIspis();
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati.mL_izraz = false;
			vrati.mTip = Tip._char;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		if (uz.mNaziv.equals("NIZ_ZNAKOVA")){
			if (!Utilities.ProvjeriNizConstChar(uz.mLeksickaJedinka)) {
				String greska = "<primarni_izraz> ::= " + uz.FormatZaIspis();
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati.mL_izraz = false;
			vrati.mTip = Tip._char;
			vrati.mConst = true;
			vrati.mNiz = true;
			mZadnjiPrimarniIzrazJe_NIZ_ZNAKOVA = true;
			mDuljina_NIZ_ZNAKOVA = Utilities.VratiBrojZnakovaIz_NIZ_ZNAKOVA(uz.mLeksickaJedinka);
			return vrati;
		}
		
		if (uz.mNaziv.equals("L_ZAGRADA")){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <izraz>
			Tip_LIzraz_Const_Niz priv = PROVJERI_izraz();
			linija = mParser.ParsirajNovuLiniju();	// ucitaj D_ZAGRADA
			vrati.mL_izraz = priv.mL_izraz;
			vrati.mTip = priv.mTip;
			vrati.mConst = priv.mConst;
			vrati.mNiz = priv.mNiz;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_postfiks_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati = new Tip_LIzraz_Const_Niz();
		
		if (linija.equals("<primarni_izraz>")){
			vrati = PROVJERI_primarni_izraz();
			return vrati;
		}
		
		if (linija.equals("<postfiks_izraz>")){
			Tip_LIzraz_Const_Niz postfiks_izraz = PROVJERI_postfiks_izraz();
			linija = mParser.ParsirajNovuLiniju();
			UniformniZnak uz1 = new UniformniZnak(linija);
			
			// indeksiranje nizova
			if (uz1.mNaziv.equals("L_UGL_ZAGRADA")){
				Boolean outputEnabledWasTrue = Utilities.mWriteToOutputEnabled;
				Boolean postfiks_izraz_jeOK = true;
				if ((postfiks_izraz.mTip != Tip._int && postfiks_izraz.mTip != Tip._char) || !postfiks_izraz.mNiz) {
					postfiks_izraz_jeOK = false;
					Utilities.mWriteToOutputEnabled = false; // so that PROVJERI_izraz(); does not output anything
				}			
				
				linija = mParser.ParsirajNovuLiniju();	// ucitaj <izraz>
				Tip_LIzraz_Const_Niz izraz = PROVJERI_izraz();
				linija = mParser.ParsirajNovuLiniju();	// D_UGL_ZAGRADA
				UniformniZnak uz2 = new UniformniZnak(linija);
				if (outputEnabledWasTrue) Utilities.mWriteToOutputEnabled = true; // set to true only if it is here that it was set to false (not in a recursive call)
				if (!Utilities.ImplicitnaPretvorbaMoguca(izraz.mTip, Tip._int) || izraz.mNiz || !postfiks_izraz_jeOK){
					String greska = "<postfiks_izraz> ::= <postfiks_izraz> " + uz1.FormatZaIspis() +
									" <izraz> " + uz2.FormatZaIspis();
					Utilities.WriteStringLineToOutputAndExit(greska);
				}
				vrati.mTip = postfiks_izraz.mTip;
				vrati.mL_izraz = !postfiks_izraz.mConst;
				vrati.mConst = postfiks_izraz.mConst;
				vrati.mNiz = false;
				return vrati;
			}
			
			// pozivi funkcija
			if (uz1.mNaziv.equals("L_ZAGRADA")){
				linija = mParser.ParsirajNovuLiniju();
				UniformniZnak uz2 = UniformniZnak.SigurnoStvaranje(linija);
				if (uz2 != null && uz2.mNaziv.equals("D_ZAGRADA")){ // void --> pov
					if(postfiks_izraz.mTip != Tip._funkcija || postfiks_izraz.mFun == null || postfiks_izraz.mFun.mParam.size() != 0){
						String greska = "<postfiks_izraz> ::= <postfiks_izraz> " + uz1.FormatZaIspis() +
								" " + uz2.FormatZaIspis();
						Utilities.WriteStringLineToOutputAndExit(greska);
					}
					vrati.mTip = postfiks_izraz.mFun.mPov;
					vrati.mL_izraz = false;
					vrati.mConst = false;
					vrati.mNiz = false;
					return vrati;
				}
				if (linija.equals("<lista_argumenata>")){ // lista_argumenata --> pov
					List<Tip_LIzraz_Const_Niz> lista_argumenata = PROVJERI_lista_argumenata();
					linija = mParser.ParsirajNovuLiniju(); // ucitaj D_ZAGRADA
					uz2 = UniformniZnak.SigurnoStvaranje(linija);
					
					Boolean arg_Par_OK = true;
					if (postfiks_izraz.mFun.mParam.size() == lista_argumenata.size()){
						for (int i = 0; i < lista_argumenata.size(); ++i) 
							if (!Utilities.ImplicitnaPretvorbaMoguca(lista_argumenata.get(i).mTip, postfiks_izraz.mFun.mParam.get(i).mTip) ||
									(lista_argumenata.get(i).mNiz != postfiks_izraz.mFun.mParam.get(i).mNiz))
								arg_Par_OK = false;
					}else arg_Par_OK = false;
					
					if(postfiks_izraz.mTip != Tip._funkcija || postfiks_izraz.mFun == null || postfiks_izraz.mFun.mParam.size() == 0 || !arg_Par_OK){						
						String greska = "<postfiks_izraz> ::= <postfiks_izraz> " + uz1.FormatZaIspis() +
								" <lista_argumenata> " + uz2.FormatZaIspis();
						Utilities.WriteStringLineToOutputAndExit(greska);
					}
					vrati.mTip = postfiks_izraz.mFun.mPov;
					vrati.mL_izraz = false;
					vrati.mConst = false;
					vrati.mNiz = false;
					return vrati;
				}
			}
			
			// ++ i --
			if (uz1.mNaziv.equals("OP_INC") || uz1.mNaziv.equals("OP_DEC")){
				if (!postfiks_izraz.mL_izraz || postfiks_izraz.mNiz || !Utilities.ImplicitnaPretvorbaMoguca(postfiks_izraz.mTip, Tip._int)){
					String greska = "<postfiks_izraz> ::= <postfiks_izraz> " + uz1.FormatZaIspis();
					Utilities.WriteStringLineToOutputAndExit(greska);
				}
				vrati.mTip = Tip._int;
				vrati.mL_izraz = false;
				vrati.mConst = false;
				vrati.mNiz = false;
				return vrati;
			}
		}
		
		return null;
	}

	public static List<Tip_LIzraz_Const_Niz> PROVJERI_lista_argumenata(){
		String linija = mParser.ParsirajNovuLiniju();
		List<Tip_LIzraz_Const_Niz> vrati;
		
		if (linija.equals("<izraz_pridruzivanja>")){
			vrati = new ArrayList<Tip_LIzraz_Const_Niz>();
			vrati.add(PROVJERI_izraz_pridruzivanja());
			return vrati;
		}
		
		if (linija.equals("<lista_argumenata>")){
			vrati = PROVJERI_lista_argumenata();
			linija = mParser.ParsirajNovuLiniju();	// ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <izraz_pridruzivanja>
			vrati.add(PROVJERI_izraz_pridruzivanja());
			return vrati;
		}
		
		return null;
	}
	
	public static Tip_LIzraz_Const_Niz PROVJERI_unarni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (linija.equals("<postfiks_izraz>")){
			vrati = PROVJERI_postfiks_izraz();
			return vrati;
		}
		
		if (uz != null && (uz.mNaziv.equals("OP_DEC") || uz.mNaziv.equals("OP_INC"))){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <unarni_izraz>
			Tip_LIzraz_Const_Niz unarni_izraz = PROVJERI_unarni_izraz();
			if (!unarni_izraz.mL_izraz || !Utilities.ImplicitnaPretvorbaMoguca(unarni_izraz.mTip, Tip._int) || unarni_izraz.mNiz){
				String greska = "<unarni_izraz> ::= " + uz.FormatZaIspis() + " <unarni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		if (linija.equals("<unarni_operator>")){
			// kaze uputa da <unarni_operator> ne treba provjeravati,
			linija = mParser.ParsirajNovuLiniju();	// ali ipak treba ucitati (PLUS | MINUS | OP_TILDA | OP_NEG)
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <cast_izraz>
			Tip_LIzraz_Const_Niz cast_izraz = PROVJERI_cast_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(cast_izraz.mTip, Tip._int) || cast_izraz.mNiz){
				String greska = "<unarni_izraz> ::= " + "<unarni_operator>" + " <cast_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}
	
	public static Tip_LIzraz_Const_Niz PROVJERI_cast_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (linija.equals("<unarni_izraz>")){
			vrati = PROVJERI_unarni_izraz();
			return vrati;
		}
		
		if (uz != null && uz.mNaziv.equals("L_ZAGRADA")){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <ime_tipa>
			Tip_Const ime_tipa = PROVJERI_ime_tipa();
			linija = mParser.ParsirajNovuLiniju();	// ucitaj D_ZAGRADA
			UniformniZnak uz_dZagrada = UniformniZnak.SigurnoStvaranje(linija);
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <cast_izraz>
			Tip_LIzraz_Const_Niz cast_izraz = PROVJERI_cast_izraz();
			if (!Utilities.JeBrojevniTip(ime_tipa.mTip) || !Utilities.JeBrojevniTip(cast_izraz.mTip) || (cast_izraz.mNiz)){
				String greska = "<cast_izraz> ::= " + uz.FormatZaIspis() + " <ime_tipa> " + 
						uz_dZagrada.FormatZaIspis() + " <cast_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = ime_tipa.mTip;
			vrati.mL_izraz = false;
			vrati.mConst = ime_tipa.mConst;
			vrati.mNiz = false; // <ime_tipa> nemoze proizvesti niz
			return vrati;
		}
		
		return null;
	}
	
	public static Tip_Const PROVJERI_ime_tipa(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_Const vrati;
		UniformniZnak uz = UniformniZnak.SigurnoStvaranje(linija);
		
		if (linija.equals("<specifikator_tipa>")){
			vrati = new Tip_Const();
			vrati.mTip = PROVJERI_specifikator_tipa();
			vrati.mConst = false;
			return vrati;
		}
		
		if (uz.mNaziv.equals("KR_CONST")){
			linija = mParser.ParsirajNovuLiniju();	// ucitaj <specifikator_tipa>
			vrati = new Tip_Const();
			vrati.mTip = PROVJERI_specifikator_tipa();
			if (vrati.mTip == Tip._void){
				String greska = "<ime_tipa> ::= " + uz.FormatZaIspis() + " <specifikator_tipa>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati.mConst = true;
			return vrati;
		}
		
		return null;
	}
	
	public static Tip PROVJERI_specifikator_tipa(){
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
	
	public static Tip_LIzraz_Const_Niz PROVJERI_multiplikativni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<cast_izraz>")){
			vrati = PROVJERI_cast_izraz();
			return vrati;
		}
		
		if (linija.equals("<multiplikativni_izraz>")){
			Tip_LIzraz_Const_Niz multiplikativni_izraz = PROVJERI_multiplikativni_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (OP_PUTA | OP_DIJELI | OP_MOD)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(multiplikativni_izraz.mTip, Tip._int)){
				String greska = "<multiplikativni_izraz> ::= <multiplikativni_izraz> " + uz_operator.FormatZaIspis() + " <cast_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <cast_izraz>
			Tip_LIzraz_Const_Niz cast_izraz = PROVJERI_cast_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(cast_izraz.mTip, Tip._int)){
				String greska = "<multiplikativni_izraz> ::= <multiplikativni_izraz> " + uz_operator.FormatZaIspis() + " <cast_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}
	
	public static Tip_LIzraz_Const_Niz PROVJERI_aditivni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<multiplikativni_izraz>")){
			vrati = PROVJERI_multiplikativni_izraz();
			return vrati;
		}
		
		if (linija.equals("<aditivni_izraz>")){
			Tip_LIzraz_Const_Niz aditivni_izraz = PROVJERI_aditivni_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (PLUS | MINUS)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(aditivni_izraz.mTip, Tip._int)){
				String greska = "<aditivni_izraz> ::= <aditivni_izraz> " + uz_operator.FormatZaIspis() + " <multiplikativni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <multiplikativni_izraz>
			Tip_LIzraz_Const_Niz multiplikativni_izraz = PROVJERI_multiplikativni_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(multiplikativni_izraz.mTip, Tip._int)){
				String greska = "<aditivni_izraz> ::= <aditivni_izraz> " + uz_operator.FormatZaIspis() + " <multiplikativni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_odnosni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<aditivni_izraz>")){
			vrati = PROVJERI_aditivni_izraz();
			return vrati;
		}
		
		if (linija.equals("<odnosni_izraz>")){
			Tip_LIzraz_Const_Niz odnosni_izraz = PROVJERI_odnosni_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (OP_LT | OP_GT | OP_LTE | OP_GTE)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(odnosni_izraz.mTip, Tip._int)){
				String greska = "<odnosni_izraz> ::= <odnosni_izraz> " + uz_operator.FormatZaIspis() + " <aditivni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <aditivni_izraz>
			Tip_LIzraz_Const_Niz aditivni_izraz = PROVJERI_aditivni_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(aditivni_izraz.mTip, Tip._int)){
				String greska = "<odnosni_izraz> ::= <odnosni_izraz> " + uz_operator.FormatZaIspis() + " <aditivni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_jednakosni_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<odnosni_izraz>")){
			vrati = PROVJERI_odnosni_izraz();
			return vrati;
		}
		
		if (linija.equals("<jednakosni_izraz>")){
			Tip_LIzraz_Const_Niz jednakosni_izraz = PROVJERI_jednakosni_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj (OP_EQ | OP_NEQ)
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(jednakosni_izraz.mTip, Tip._int)){
				String greska = "<jednakosni_izraz> ::= <jednakosni_izraz> " + uz_operator.FormatZaIspis() + " <odnosni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <odnosni_izraz>
			Tip_LIzraz_Const_Niz odnosni_izraz = PROVJERI_odnosni_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(odnosni_izraz.mTip, Tip._int)){
				String greska = "<jednakosni_izraz> ::= <jednakosni_izraz> " + uz_operator.FormatZaIspis() + " <odnosni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_bin_i_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<jednakosni_izraz>")){
			vrati = PROVJERI_jednakosni_izraz();
			return vrati;
		}
		
		if (linija.equals("<bin_i_izraz>")){
			Tip_LIzraz_Const_Niz bin_i_izraz = PROVJERI_bin_i_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_BIN_I
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(bin_i_izraz.mTip, Tip._int)){
				String greska = "<bin_i_izraz> ::= <bin_i_izraz> " + uz_operator.FormatZaIspis() + " <jednakosni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <jednakosni_izraz>
			Tip_LIzraz_Const_Niz jednakosni_izraz = PROVJERI_jednakosni_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(jednakosni_izraz.mTip, Tip._int)){
				String greska = "<bin_i_izraz> ::= <bin_i_izraz> " + uz_operator.FormatZaIspis() + " <jednakosni_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_bin_xili_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<bin_i_izraz>")){
			vrati = PROVJERI_bin_i_izraz();
			return vrati;
		}
		
		if (linija.equals("<bin_xili_izraz>")){
			Tip_LIzraz_Const_Niz bin_xili_izraz = PROVJERI_bin_xili_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_BIN_XILI
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(bin_xili_izraz.mTip, Tip._int)){
				String greska = "<bin_xili_izraz> ::= <bin_xili_izraz> " + uz_operator.FormatZaIspis() + " <bin_i_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <bin_i_izraz>
			Tip_LIzraz_Const_Niz bin_i_izraz = PROVJERI_bin_i_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(bin_i_izraz.mTip, Tip._int)){
				String greska = "<bin_xili_izraz> ::= <bin_xili_izraz> " + uz_operator.FormatZaIspis() + " <bin_i_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}
	
	public static Tip_LIzraz_Const_Niz PROVJERI_bin_ili_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<bin_xili_izraz>")){
			vrati = PROVJERI_bin_xili_izraz();
			return vrati;
		}
		
		if (linija.equals("<bin_ili_izraz>")){
			Tip_LIzraz_Const_Niz bin_ili_izraz = PROVJERI_bin_ili_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_BIN_ILI
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(bin_ili_izraz.mTip, Tip._int)){
				String greska = "<bin_ili_izraz> ::= <bin_ili_izraz> " + uz_operator.FormatZaIspis() + " <bin_xili_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <bin_xili_izraz>
			Tip_LIzraz_Const_Niz bin_xili_izraz = PROVJERI_bin_xili_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(bin_xili_izraz.mTip, Tip._int)){
				String greska = "<bin_ili_izraz> ::= <bin_ili_izraz> " + uz_operator.FormatZaIspis() + " <bin_xili_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_log_i_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<bin_ili_izraz>")){
			vrati = PROVJERI_bin_ili_izraz();
			return vrati;
		}
		
		if (linija.equals("<log_i_izraz>")){
			Tip_LIzraz_Const_Niz log_i_izraz = PROVJERI_log_i_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_I
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(log_i_izraz.mTip, Tip._int)){
				String greska = "<log_i_izraz> ::= <log_i_izraz> " + uz_operator.FormatZaIspis() + " <bin_ili_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <bin_ili_izraz>
			Tip_LIzraz_Const_Niz bin_ili_izraz = PROVJERI_bin_ili_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(bin_ili_izraz.mTip, Tip._int)){
				String greska = "<log_i_izraz> ::= <log_i_izraz> " + uz_operator.FormatZaIspis() + " <bin_ili_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_log_ili_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<log_i_izraz>")){
			vrati = PROVJERI_log_i_izraz();
			return vrati;
		}
		
		if (linija.equals("<log_ili_izraz>")){
			Tip_LIzraz_Const_Niz log_ili_izraz = PROVJERI_log_ili_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_ILI
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!Utilities.ImplicitnaPretvorbaMoguca(log_ili_izraz.mTip, Tip._int)){
				String greska = "<log_ili_izraz> ::= <log_ili_izraz> " + uz_operator.FormatZaIspis() + " <log_i_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <log_i_izraz>
			Tip_LIzraz_Const_Niz log_i_izraz = PROVJERI_log_i_izraz();
			if (!Utilities.ImplicitnaPretvorbaMoguca(log_i_izraz.mTip, Tip._int)){
				String greska = "<log_ili_izraz> ::= <log_ili_izraz> " + uz_operator.FormatZaIspis() + " <log_i_izraz>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = Tip._int;
			vrati.mL_izraz = false;
			vrati.mConst = false;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_izraz_pridruzivanja(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<log_ili_izraz>")){
			vrati = PROVJERI_log_ili_izraz();
			return vrati;
		}
		
		if (linija.equals("<postfiks_izraz>")){
			Tip_LIzraz_Const_Niz postfiks_izraz = PROVJERI_postfiks_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj OP_PRIDRUZI
			UniformniZnak uz_operator = UniformniZnak.SigurnoStvaranje(linija);
			if (!postfiks_izraz.mL_izraz){
				String greska = "<izraz_pridruzivanja> ::= <postfiks_izraz> " + uz_operator.FormatZaIspis() + " <izraz_pridruzivanja>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}			
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_pridruzivanja>
			Tip_LIzraz_Const_Niz izraz_pridruzivanja = PROVJERI_izraz_pridruzivanja();
			if (!Utilities.ImplicitnaPretvorbaMoguca(izraz_pridruzivanja.mTip, postfiks_izraz.mTip)){
				String greska = "<izraz_pridruzivanja> ::= <postfiks_izraz> " + uz_operator.FormatZaIspis() + " <izraz_pridruzivanja>";
				Utilities.WriteStringLineToOutputAndExit(greska);
			}
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = postfiks_izraz.mTip;
			vrati.mL_izraz = false;
			vrati.mConst = postfiks_izraz.mConst;
			vrati.mNiz = false;
			return vrati;
		}
		
		return null;
	}

	public static Tip_LIzraz_Const_Niz PROVJERI_izraz(){
		String linija = mParser.ParsirajNovuLiniju();
		Tip_LIzraz_Const_Niz vrati;		
		
		if (linija.equals("<izraz_pridruzivanja>")){
			vrati = PROVJERI_izraz_pridruzivanja();
			return vrati;
		}
		
		if (linija.equals("<izraz>")){
			PROVJERI_izraz();
			linija = mParser.ParsirajNovuLiniju(); // ucitaj ZAREZ
			linija = mParser.ParsirajNovuLiniju(); // ucitaj <izraz_pridruzivanja>
			Tip_LIzraz_Const_Niz izraz_pridruzivanja = PROVJERI_izraz_pridruzivanja();
			
			vrati = new Tip_LIzraz_Const_Niz();
			vrati.mTip = izraz_pridruzivanja.mTip;
			vrati.mL_izraz = false;
			vrati.mConst = izraz_pridruzivanja.mConst;
			vrati.mNiz = izraz_pridruzivanja.mNiz;;
			return vrati;
		}
		
		return null;
	}
}
