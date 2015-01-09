package hr.unizg.fer.lab4;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FRISC_ispisivac {
	
	private StringBuilder mMC_String;
	private int mPreMainKod;
	private int mKodPos;
	private int mGVariablePos;
	private String mSljedecaLabela;
	
	public boolean mDodajFunkcijuMod, mDodajFunkcijuMul, mDodajFunkcijuDiv;
	
	/**
	 * Creates a new instance of SourceCodeGenerator.
	 * @param - none
	 */
	public FRISC_ispisivac(){
		mMC_String = new StringBuilder();
		mMC_String.append(
				"\t`BASE D\t;postavi bazu 10\n"
				+ "\tMOVE 40000, R7\n"
				+ "\tCALL F_MAIN\n"
				+ "\tHALT\n\n\n");
		
		mPreMainKod = 41;
		mKodPos = 61;
		mGVariablePos = 62;
		
		mDodajFunkcijuMod = mDodajFunkcijuMul = mDodajFunkcijuDiv = false;
	}
	
	public void PostaviSljedecuLabelu(String labela){
		mSljedecaLabela = labela;
	}
	
	public void DodajPreMainKod(String naredba){
		DodajPreMainKod(naredba, null);
	}
	
	public void DodajPreMainKod(String naredba, String komentar){
		String novaLinija = "\n";
		novaLinija += "\t" + naredba;
		if (komentar != null) novaLinija += "\t;" + komentar;
		
		mMC_String.insert(mPreMainKod, novaLinija);
		mPreMainKod += novaLinija.length();
		mKodPos += novaLinija.length();
		mGVariablePos += novaLinija.length();
	}
	
	public void DodajKod(String naredba){
		DodajKod(naredba, null);
	}
	
	public void DodajKod(String naredba, String komentar){
		String novaLinija = "";
		if (mSljedecaLabela != null) novaLinija += mSljedecaLabela;
		if (naredba != null) novaLinija += "\t" + naredba;
		if (komentar != null) novaLinija += "\t;" + komentar;
		novaLinija += "\n";
		
		mSljedecaLabela = null;
		mMC_String.insert(mKodPos, novaLinija);
		mKodPos += novaLinija.length();
		mGVariablePos += novaLinija.length();
	}
	
	public void DodajGlobalnuVarijablu(String labela, String naredba){
		DodajGlobalnuVarijablu(labela, naredba, null);
	}
	
	public void DodajGlobalnuVarijablu(String labela, String naredba, String komentar){
		String novaLinija = "";
		if (labela != null) novaLinija += labela;
		novaLinija += "\t" + naredba;
		if (komentar != null) novaLinija += "\t;" + komentar;
		novaLinija += "\n";
		
		mMC_String.insert(mGVariablePos, novaLinija);
		mGVariablePos += novaLinija.length();
	}
	
	public void DodajPotrebneFunkcije(){
		if (mDodajFunkcijuMod){
			DodajKod(null); // radi urednosti
			PostaviSljedecuLabelu("LF_MOD");
			DodajKod("LOAD R1, (R7 + 4)", "dohvacam y");
			DodajKod("LOAD R0, (R7 + 8)", "dohvacam x");
			PostaviSljedecuLabelu("LF_MOD_UVIJET");
			DodajKod("CMP R0, R1");
			DodajKod("JR_SLT LF_MOD_KRAJ");
			DodajKod("SUB R0, R1, R0");
			DodajKod("JR LF_MOD_UVIJET");
			PostaviSljedecuLabelu("LF_MOD_KRAJ");
			DodajKod("ADD R0, 0, R6");
			DodajKod("RET", "vrati x%y");
		}
	}
	
	/**
	 * Writes all generated code to a file named LA.java.
	 * @param dir - directory of the file to write into or create.
	 */
	public void Ispisi(String dir){
		try {
			PrintWriter writer = new PrintWriter(dir + "a" + ".frisc", "UTF-8");
			writer.print(mMC_String.toString());
			writer.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}