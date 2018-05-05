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
	private String mProviriVrijednost;
	private int mDubinaZadnjeProcitanogCvora;
	private int mProcitanoRedova;
	
	public Parser(){
		mUlaz = Utilities.ReadStringFromInput();
	    mLista = Arrays.asList(mUlaz.split("\n"));
	    mIt = mLista.iterator();
	    mProviriVrijednost = mIt.next();
	    mDubinaZadnjeProcitanogCvora = 0;
	    mProcitanoRedova = 0;
	}
	
	public String ParsirajNovuLiniju(){
		String linija = mProviriVrijednost;
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
		++mProcitanoRedova;
		mZadnjeProcitaniCvor = linija;
		mProviriVrijednost = (mIt.hasNext()) ? mIt.next() : null;
		return linija;
	}
	
	public List<String> DohvatiSljedRoditelja(){
		return mSljedRoditeljaTrenutacnogCvora;
	}
	
	public String DohvatiProviriVrijednost(){
		if (mProviriVrijednost != null){
			int brPraznihMjesta = 0;
			for(brPraznihMjesta = 0; mProviriVrijednost.charAt(brPraznihMjesta) == ' '; ++brPraznihMjesta);
			return mProviriVrijednost.substring(brPraznihMjesta, mProviriVrijednost.length());
		}
		else return null;
	}
	
	public int DohvatiProcitanoRedova(){
		return mProcitanoRedova;
	}
}
