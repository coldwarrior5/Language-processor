package hr.unizg.fer;

import java.util.Scanner;

class LexicalRule{
	String mLexicalState;
	eNFA mENFA;
	Boolean mDiscardString;
	String mLexicalTokenName;
	Boolean mNewLine;
	Boolean mGoToState;
	String mGoToStateName;
	Boolean mReturn;
	int mReturnAt;
}

class LA_OutputElement{
	String mUniformToken;
	int mLine;
	String mLexicUint;
}

class UtilitiesLA {
	
	public static String ReadStringFromInput(){
		String input = "";
		Scanner scIn = new Scanner(System.in);
		while(true){
			String read = scIn.nextLine();
			if(read.equals("")){
				break;
			}
			else{
				input += read + "\n";
			}
		}
		scIn.close();
		return input;
	}
}
