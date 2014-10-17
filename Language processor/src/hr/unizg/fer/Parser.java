package hr.unizg.fer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
		String mRegDef;
		List<String> mArguments = new ArrayList<String>();
	}
	
	private List<RegDef> mRegDefList = new ArrayList<RegDef>();
	private List<State> mStateList = new ArrayList<State>();
	private List<LexicalToken> mLexicalTokenList = new ArrayList<LexicalToken>();
	private List<LexicalRule> mLexicalRuleList = new ArrayList<LexicalRule>();
	
	/**
	 * Creates arrays of data populated by parsing the .lan file.
	 * @author Kristijan
	 * @param lanFile - is the path of the .lan file that contains all the rules for given language.
	 */
	public Parser(String lanFile){
		
		BufferedReader br;
	    try {
	    	br = new BufferedReader(new FileReader(lanFile));
	    	
	    	String line;
			line = br.readLine();
			
			while(line.substring(0,1).equals("{")){
				FillRegDef(line);
				line = br.readLine();
			};
			
			if(line.substring(0,2).equals("%X")){
				FillState(line);
				line = br.readLine();
			}
			
			if(line.substring(0,2).equals("%L")){
				FillLexicalToken(line);
				line = br.readLine();
			}
			
			while(line!=null && line.substring(0,1).equals("<")){
				FillLexicalRule(line, br);
				line = br.readLine();
			};
			
			if(line!=null){
				//print error:wrong input file
			}
	    	
	        br.close();
	        
	    } 
	    catch (IOException e) {
			// TOO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the mRegDefList
	 */
	public List<RegDef> GetmRegDefList() {
		return mRegDefList;
	}

	/**
	 * @return the mStateList
	 */
	public List<State> GetmStateList() {
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
	 * @author Bojan
	 * @param regEx - the regular expression that needs processing.
	 */
	private String ProcessRegEx(String regEx){
		while (regEx.indexOf("{") >= 0){
			String regDefName = regEx.substring(regEx.indexOf("{") + 1, regEx.indexOf("}"));
			for (int i = 0; i < mRegDefList.size(); ++i)
				if (mRegDefList.get(i).mRegDefName.equals(regDefName)){
					String replaceString = "{" + regDefName + "}";
					regEx = regEx.replace(replaceString, mRegDefList.get(i).mRegEx);
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
	
	private void FillLexicalRule(String inputLine, BufferedReader br){
		try {
			LexicalRule temp = new LexicalRule();
			temp.mLexicalState = inputLine.substring(1, inputLine.indexOf(">"));
			temp.mRegDef = inputLine.substring(inputLine.indexOf(">")+1);
			temp.mRegDef = ProcessRegEx(temp.mRegDef);
			
			String line = br.readLine(); // skips "{"
			while(!(line = br.readLine()).equals("}")){
				temp.mArguments.add(line);
			}
			mLexicalRuleList.add(temp);	
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
