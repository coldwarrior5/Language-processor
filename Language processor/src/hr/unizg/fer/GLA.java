package hr.unizg.fer;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
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
			mOutput = new Lexic("LA");
		} catch (FileNotFoundException e) {
			System.err.print("File cannot be created");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.print("File format error non UTF-8");
			e.printStackTrace();
		}
		
		ArrayList<String> inputLines = new ArrayList<String>();
		Scanner input = new Scanner(System.in);
		/**
		 * This while loop is reading whole input and stores it to an array
		 */
		while(true){
			String read=input.nextLine();
			if(read.equals("")){
				break;
			}
			else{
				inputLines.add(read);
			}
		}
		input.close();
		
		mOutput.Import("java.util.Scanner");
		mOutput.StartClass();
		mOutput.Fuction(false, true, "void", "main", "String[] args");
		mOutput.DefineObject("Scanner", "input", "System.in");
		
		mOutput.Input("while(true){\nString read=input.nextLine();\nif(read.equals(\"\"))break;\n}");
		mOutput.close();
		mOutput.close();
		mOutput.EndFile();
	}

}
