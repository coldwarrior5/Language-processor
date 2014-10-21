package hr.unizg.fer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Parser {
	
	public class RegDef{
		String mRegDefName;
		String mRegEx;
		int mPriority;
	}
	
	public class State{
		String mStateName;
	}
	
	public class LexicalToken{
		String mLexicalTokenName;
	}
	
	public class LexicalRule{
		String mLexicalState;
		String mRegEx;
		
		Boolean mDiscardString; // "-" instead of lexical token
		String mLexicalTokenName;
		Boolean mNewLine;
		Boolean mGoToState;
		String mGoToStateName;
		
		// Treci posebni argument je VRATI_SE naZnak
		// koji odreduje da se od procitanih znakova u leksicku jedinku treba grupirati prvih naZnak
		// znakova, a ostali znakovi vracaju se u ulazni niz, kao da nisu ni procitani
		Boolean mReturn;
		int mReturnAt;
	}
	
	private List<RegDef> mRegDefList = new ArrayList<RegDef>();
	private List<State> mStateList = new ArrayList<State>();
	private List<LexicalToken> mLexicalTokenList = new ArrayList<LexicalToken>();
	private List<LexicalRule> mLexicalRuleList = new ArrayList<LexicalRule>();
	
	/**
	 * Creates arrays of data populated by parsing the .lan file.
	 * @param lanFile - is the path of the .lan file that contains all the rules for given language.
	 */
	public Parser(){
		
	    	String input = UtilitiesLA.ReadStringFromInput();
	    	List<String> list = Arrays.asList(input.split("\n"));
	    	Iterator<String> it = list.iterator();
	    	String line=it.next();
	    	
			while(line.substring(0,1).equals("{")){
				FillRegDef(line);
				line = it.next();
			};
			
			if(line.substring(0,2).equals("%X")){
				FillState(line);
				line = it.next();
			}
			
			if(line.substring(0,2).equals("%L")){
				FillLexicalToken(line);
				if(it.hasNext()){
					line = it.next();
				}
				else{
					line=null;
				}
			}
			
			while(line!=null && line.substring(0,1).equals("<")){
				FillLexicalRule(line, it);
				if(it.hasNext()){
					line = it.next();
				}
				else{
					line=null;
				}
			};
			
			if(line!=null){
				//print error:wrong input file
			}

	}
	
	/**
	 * @return the mRegDefList
	 */
	public List<RegDef> GetRegDefList() {
		return mRegDefList;
	}

	/**
	 * @return the mStateList
	 */
	public List<State> GetStateList() {
		return mStateList;
	}

	/**
	 * @return the mLexicalTokenList
	 */
	public List<LexicalToken> GetLexicalTokenList() {
		return mLexicalTokenList;
	}

	/**
	 * @return the mLexicalRuleList
	 */
	public List<LexicalRule> GetLexicalRuleList() {
		return mLexicalRuleList;
	}

	/**
	 * Replaces regular expression definition names with actual regular expressions.
	 * @param regEx - the regular expression that needs processing.
	 */
	private String ProcessRegEx(String regEx){
		int openedCurlyBracketsIndex = -1;
		while ((openedCurlyBracketsIndex = regEx.indexOf("{", openedCurlyBracketsIndex + 1)) >= 0){
			if (openedCurlyBracketsIndex != 0 && regEx.charAt(openedCurlyBracketsIndex - 1) == '\\') continue; // this is special character {
			int closedCurlyBracketsIndex = regEx.indexOf("}", openedCurlyBracketsIndex);
			while (regEx.charAt(closedCurlyBracketsIndex - 1) == '\\'){ // this is special character }
				closedCurlyBracketsIndex = regEx.indexOf("}", closedCurlyBracketsIndex + 1);
			}
			String regDefName = regEx.substring(openedCurlyBracketsIndex + 1, closedCurlyBracketsIndex);
			for (int i = 0; i < mRegDefList.size(); ++i)
				if (mRegDefList.get(i).mRegDefName.equals(regDefName)){
					String replaceString = "{" + regDefName + "}";
					regEx = regEx.replace(replaceString, "(" + mRegDefList.get(i).mRegEx + ")");
				}
		}
		return regEx;
	}
	
	private void FillRegDef(String inputLine){
		RegDef temp = new RegDef();
		temp.mRegDefName = inputLine.substring(1, inputLine.indexOf("} "));
		temp.mRegEx = inputLine.substring(inputLine.indexOf("} ")+2);
		temp.mRegEx = ProcessRegEx(temp.mRegEx);
		temp.mPriority = mRegDefList.size();
		mRegDefList.add(temp);
	}

	private void FillState(String inputLine){
		if(inputLine.indexOf("S_")>0){
			inputLine=inputLine.substring(inputLine.indexOf("S_"));
		}
		else{
			return;
		}
		
		State temp;
		do{
			temp = new State();
			if(inputLine.indexOf(" ")>0){
				temp.mStateName = inputLine.substring(0, inputLine.indexOf(" "));
				inputLine=inputLine.substring(inputLine.indexOf(" ")+1);
			}
			else{
				temp.mStateName = inputLine;
			}
			mStateList.add(temp);
		}while(temp.mStateName != inputLine);
	}
	
	private void FillLexicalToken(String inputLine){
		if(inputLine.length() >= 4){
			inputLine=inputLine.substring(inputLine.indexOf(" ")+1);
		}
		else{
			return;
		}
		
		LexicalToken temp;
		do{
			temp = new LexicalToken();
			if(inputLine.indexOf(" ")>0){
				temp.mLexicalTokenName = inputLine.substring(0, inputLine.indexOf(" "));
				inputLine=inputLine.substring(inputLine.indexOf(" ")+1);
			}
			else{
				temp.mLexicalTokenName = inputLine;
			}
			mLexicalTokenList.add(temp);
		}while(temp.mLexicalTokenName != inputLine);
		
	}
	
	private void FillLexicalRule(String inputLine, Iterator<String> it){
		
			LexicalRule temp = new LexicalRule();
			temp.mLexicalState = inputLine.substring(1, inputLine.indexOf(">"));
			temp.mRegEx = inputLine.substring(inputLine.indexOf(">")+1);
			temp.mRegEx = ProcessRegEx(temp.mRegEx);
			
			temp.mDiscardString = false;
			temp.mNewLine = false;
			temp.mGoToState = false;
			temp.mReturn = false;
			
			String line = it.next(); // skips "{"
			
			// name of the lexical token goes first
			line = it.next();
			if (line.charAt(0) == '-') temp.mDiscardString = true;
			else temp.mLexicalTokenName = line;
			
			// the rest of the arguments
			while(!(line = it.next()).equals("}")){
				
				if (line.indexOf("NOVI_REDAK") >= 0) temp.mNewLine = true;
				if (line.indexOf("UDJI_U_STANJE") >= 0){
					temp.mGoToState = true;
					temp.mGoToStateName = line.substring(line.indexOf(" ") + 1, line.length());
				}
				if (line.indexOf("VRATI_SE") >= 0){
					temp.mReturn = true;
					temp.mReturnAt = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
				}
			}
			mLexicalRuleList.add(temp);	
	}
}
