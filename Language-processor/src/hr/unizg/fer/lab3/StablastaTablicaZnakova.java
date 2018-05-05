package hr.unizg.fer.lab3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

enum Tip {_int, _char, _funkcija, _void};

class TipFunkcija{
	Tip mPov; // povratna vrijednost funkcije
	List<Tip_Const_Niz> mParam = new ArrayList<Tip_Const_Niz>(); // parametri funkcije
}

class ClanTabliceZnakova{
	Tip mTip;
	Boolean mL_izraz;
	Boolean mConst;
	Boolean mNiz;
	Boolean mDefinirano;
	TipFunkcija mTipFunkcija; // null ako nije mTip == Tip._funkcija
}

class TablicaZnakova{
	Map<String, ClanTabliceZnakova> mTablica = new HashMap<String, ClanTabliceZnakova>();
}

class Cvor{ // odredjuje djelokrug deklaracija
	TablicaZnakova mTablicaZnakova = new TablicaZnakova();
	Cvor mRoditelj; // cvor koji predstavlja ugnijezdujuci blok (ako takav postoji)
	ClanTabliceZnakova mFunkcija; // funkcija kojoj je dodijeljen djelokrug. Ako je djelokrug sam za sebe tada mFunkcija = null
}

public class StablastaTablicaZnakova {

	private Cvor mPocetni;
	private Cvor mTrenutacni;
	private List<ClanTabliceZnakova> mNedefiniraniZnakovi_ct = new ArrayList<ClanTabliceZnakova>();
	private List<String> mNedefiniraniZnakovi_idn = new ArrayList<String>();
	
	public StablastaTablicaZnakova(){
		mPocetni = new Cvor();
		mPocetni.mRoditelj = null;
		mTrenutacni = mPocetni;
	}
	
	public void ProvjeraNakonObilaska(){
		// 1.
		TipFunkcija f = new TipFunkcija();
		f.mPov = Tip._int;
		// f.mParam mora  ostat na nula clanova
		if (!JeliFunkcijaDefinirana("main", f))
			Utilities.WriteStringLineToOutputAndExit("main");
		
		// 2.
		if (mNedefiniraniZnakovi_ct.size() != 0)
			Utilities.WriteStringLineToOutputAndExit("funkcija");
	}
	
	public void DodajClanUTablicuZnakova(String identifikator, ClanTabliceZnakova clanTablice){
		mTrenutacni.mTablicaZnakova.mTablica.put(identifikator, clanTablice);
		
		// dodati u nedefinirane ako je potrebno
		if (clanTablice.mDefinirano == false){
			mNedefiniraniZnakovi_ct.add(clanTablice);
			mNedefiniraniZnakovi_idn.add(identifikator);
		}
	}
	
	public void UpravoDefiniranaFunkcija(String identifikator, ClanTabliceZnakova clTablice){
		Iterator<String> it1;
		Iterator<ClanTabliceZnakova> it2;
		for (it1 = mNedefiniraniZnakovi_idn.iterator(), it2 = mNedefiniraniZnakovi_ct.iterator(); it2.hasNext();){
			String itIdn = it1.next();
			ClanTabliceZnakova itClT = it2.next();
			
			if (itIdn.equals(identifikator) && Utilities.FunkcijeIste(clTablice.mTipFunkcija, itClT.mTipFunkcija)){
				it1.remove();
				it2.remove();
			}
		}
	}
	
	public ClanTabliceZnakova DohvatiClanIzTabliceZnakova(String identifikator){
		Cvor privCvor = mTrenutacni;
		ClanTabliceZnakova privClan = privCvor.mTablicaZnakova.mTablica.get(identifikator);
		// pretrazuj roditelje sve dok ne dodjes do globalnog djelokruga
		while (privClan == null){
			if (privCvor.mRoditelj == null) return null;
			privCvor = privCvor.mRoditelj;
			privClan = privCvor.mTablicaZnakova.mTablica.get(identifikator);
		}
		return privClan;
	}
	
	public ClanTabliceZnakova DohvatiClanIzTabliceZnakovaSamoTrenutacnogDjelokruga(String identifikator){
		return mTrenutacni.mTablicaZnakova.mTablica.get(identifikator);
	}
	
	// vraca deklaraciju funkcije u kojoj se STZ trenutacno nalazi
	public TipFunkcija VratiDeklaracijuFunkcijeDjelokruga(){
		Cvor priv = mTrenutacni;
		while (priv.mFunkcija == null) priv = priv.mRoditelj;
		// kad smo nasli onda:
		if (priv.mFunkcija.mTip != Tip._funkcija) 
			Utilities.WriteStringLineToStdErr("Greska u: VratiDeklaracijuFunkcije();");
		return priv.mFunkcija.mTipFunkcija;
	}
	
	public Boolean JeliFunkcijaDefinirana(String identifikator, TipFunkcija tipFun){
		ClanTabliceZnakova cl = mPocetni.mTablicaZnakova.mTablica.get(identifikator); // definicija moze biti samo u globalnom djelokrugu
		if (cl == null) return false;
		else if (cl.mTip != Tip._funkcija) return false;
		else if (!Utilities.FunkcijeIste(tipFun, cl.mTipFunkcija)) return false;
		else return true;
	}
	
	public void  UdjiUNoviCvor(ClanTabliceZnakova funkcija){
		Cvor noviC = new Cvor();
		noviC.mRoditelj = mTrenutacni;
		noviC.mFunkcija = funkcija;
		mTrenutacni = noviC;
	}
	
	public void IzadjiIzCvora(){
		mTrenutacni = mTrenutacni.mRoditelj;
	}
}
