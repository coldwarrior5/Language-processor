package hr.unizg.fer.lab3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum Tip {_int, _char, _funkcija};

class TipFunkcija{
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
}

public class StablastaTablicaZnakova {

	private Cvor mPocetni;
	private Cvor mTrenutacni;
	private List<ClanTabliceZnakova> mNedefiniraniZnakovi = new ArrayList<ClanTabliceZnakova>();
	
	public StablastaTablicaZnakova(){
		mPocetni = new Cvor();
		mPocetni.mRoditelj = null;
		mTrenutacni = mPocetni;
	}
	
	public void DodajClanUTablicuZnakova(String identifikator, ClanTabliceZnakova clanTablice){
		mTrenutacni.mTablicaZnakova.mTablica.put(identifikator, clanTablice);
		
		// rijesiti nedefinirane
	}
	
	public ClanTabliceZnakova DohvatiClanIzTabliceZnakova(String identifikator){
		return mTrenutacni.mTablicaZnakova.mTablica.get(identifikator);
	}
	
	public void  UdjiUNoviCvor(){
		Cvor noviC = new Cvor();
		noviC.mRoditelj = mTrenutacni;
		mTrenutacni = noviC;
	}
	
	public void IzadjiIzCvora(){
		mTrenutacni = mTrenutacni.mRoditelj;
	}
}
