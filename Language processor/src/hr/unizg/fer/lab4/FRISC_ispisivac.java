package hr.unizg.fer.lab4;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FRISC_ispisivac {
	
	private StringBuilder mMC_String;
	private int mKodPos;
	private int mGVariablePos;
	
	/**
	 * Creates a new instance of SourceCodeGenerator.
	 * @param - none
	 */
	public FRISC_ispisivac(){
		mMC_String = new StringBuilder();
		mMC_String.append(
				"\tMOVE 40000, R7\n"
				+ "\tCALL F_MAIN\n"
				+ "\tHALT\n\n\n");
		
		mKodPos = 36;
		mGVariablePos = 37;
	}
	
	public void DodajKod(String naredba){
		DodajKod(null, naredba, null);
	}
	
	public void DodajKod(String labela, String naredba, String komentar){
		String novaLinija = "";
		if (labela != null) novaLinija += labela;
		novaLinija += "\t" + naredba;
		if (komentar != null) novaLinija += "\t;" + komentar;
		novaLinija += "\n";
		
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