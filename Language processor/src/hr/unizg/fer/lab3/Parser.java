package hr.unizg.fer.lab3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Parser {

	private String mUlaz;
	private List<String> mLista;
	private Iterator<String> mIt;
	
	private List<String> mSljedRoditeljaTrenutacnogCvora = new ArrayList<String>();
	private String mZadnjeProcitaniCvor;
	private int mDubinaZadnjeProcitanogCvora;
	
	public Parser(){
		mUlaz = Utilities.ReadStringFromInput();
	    mLista = Arrays.asList(mUlaz.split("\n"));
	    mIt = mLista.iterator();
	    
	    mDubinaZadnjeProcitanogCvora = 0;
	}
	
	public String ParsirajNovuLiniju(){
		String linija = mIt.next();
		int brPraznihMjesta = 0; // ili dubina cvora u stablu
		for(brPraznihMjesta = 0; linija.charAt(brPraznihMjesta) == ' '; ++brPraznihMjesta);
		
		linija = linija.substring(brPraznihMjesta, linija.length());
		if (mDubinaZadnjeProcitanogCvora < brPraznihMjesta)
			mSljedRoditeljaTrenutacnogCvora.add(mZadnjeProcitaniCvor);
		if (mDubinaZadnjeProcitanogCvora > brPraznihMjesta){
			int razlikaUDubini = mDubinaZadnjeProcitanogCvora - brPraznihMjesta;
			while (razlikaUDubini-- != 0)
				mSljedRoditeljaTrenutacnogCvora.remove(mSljedRoditeljaTrenutacnogCvora.size() - 1);
		}
		
		mDubinaZadnjeProcitanogCvora = brPraznihMjesta;
		mZadnjeProcitaniCvor = linija;
		return linija;
	}
	
	public List<String> DohvatiSljedRoditelja(){
		return mSljedRoditeljaTrenutacnogCvora;
	}
}
