package hr.unizg.fer;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
/**
 * 
 * This is the main class
 * here is where we call other classes
 *
 */
public class GLA {

	static Lexic mOutput;
	
	public static void main(String[] args) {
		
		try {
			mOutput = new Lexic();
		} catch (FileNotFoundException e) {
			System.err.print("File cannot be created");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.print("File format error non UTF-8");
			e.printStackTrace();
		}
		mOutput.Import("java.util.Scanner");
		mOutput.StartClass();
		mOutput.Fuction(false, true, "void", "main", "String[] args");
		mOutput.DefineObject("Scanner", "input", "System.in");
		mOutput.close();
		mOutput.close();
		mOutput.EndFile();
	}

}
