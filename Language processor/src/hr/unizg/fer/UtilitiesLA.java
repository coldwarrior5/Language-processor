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
			
			if(scIn.hasNext()==false){		//This works in terminal
				break;						//It expects the stdin to close
			}
/*
 * 			if(read.equals("")){			//This works in Eclipse
				break;						//It expects for user to type another enter
			}

 */
			String read = scIn.nextLine();	//Everything else looks normal
			input += read + "\n";
			
		}
		scIn.close();
		return input;
	}
}
