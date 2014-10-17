package hr.unizg.fer;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class Parser {
	
	private class regDef{
		
		String regDefName;
		String regExpression;
	
	}
	
	private class state{
		
		String stateName;
		
	}
	
	private class lexicalToken{
		
		String lexicalTokenName;
		
	}
	
	private class lexicalRule{
		
		String lexicalState;
		String regDef;
		List<String> rule = new ArrayList<String>();
		int numberOfRules=0;
	}
	
	public List<regDef> regDefList = new ArrayList<regDef>();
	public List<state> stateList = new ArrayList<state>();
	public List<lexicalToken> lexicalTokenList = new ArrayList<lexicalToken>();
	public List<lexicalRule> lexicalRuleList = new ArrayList<lexicalRule>();
	

	
	public void parseInput(){
		
		String personalBuffer="";
		personalBuffer=System.console().readLine();
		
		while(personalBuffer.substring(0,1)=="{"){
			fillRegDef(personalBuffer);
			personalBuffer=System.console().readLine();
		};
		
		if(personalBuffer.substring(0,2)=="%X"){
			fillState(personalBuffer);
			personalBuffer=System.console().readLine();
		}
		
		if(personalBuffer.substring(0,2)=="%L"){
			fillLexicalToken(personalBuffer);
			personalBuffer=System.console().readLine();
		}
		
		while(personalBuffer!=null && personalBuffer.substring(0,1)=="<"){
			fillLexicalRule(personalBuffer);
			personalBuffer=System.console().readLine();
		};
		
		if(personalBuffer!=null){
			//print error:wrong input file
		}
	}
	
	private void fillRegDef(String inputLine){
		regDef temp = new regDef();
		temp.regDefName = inputLine.substring(1, inputLine.indexOf("} "));
		temp.regExpression = inputLine.substring(inputLine.indexOf("} ")+2);
		regDefList.add(temp);
	}

	private void fillState(String inputLine){
		state temp = new state();
		if(inputLine.indexOf("S_")>0){
			inputLine=inputLine.substring(inputLine.indexOf("S_"));
		}
		else{
			return;
		}
		do{
			if(inputLine.indexOf(" ")>0){
				temp.stateName = inputLine.substring(0, inputLine.indexOf(" "));
				inputLine=inputLine.substring(inputLine.indexOf(" ")+1);
			}
			else{
				temp.stateName = inputLine;
			}
			stateList.add(temp);
		}while(temp.stateName != inputLine);
	}
	
	private void fillLexicalToken(String inputLine){
		lexicalToken temp = new lexicalToken();
		if(inputLine.length()>4){
			inputLine=inputLine.substring(inputLine.indexOf(" ")+1);
		}
		else{
			return;
		}
		do{
			if(inputLine.indexOf(" ")>0){
				temp.lexicalTokenName = inputLine.substring(0, inputLine.indexOf(" "));
				inputLine=inputLine.substring(inputLine.indexOf(" ")+1);
			}
			else{
				temp.lexicalTokenName = inputLine;
			}
			lexicalTokenList.add(temp);
		}while(temp.lexicalTokenName != inputLine);
		
	}
	
	private void fillLexicalRule(String inputLine){
		lexicalRule temp = new lexicalRule();
		temp.lexicalState = inputLine.substring(1, inputLine.indexOf(">"));
		temp.regDef = inputLine.substring(inputLine.indexOf(">")+1);
		inputLine = System.console().readLine();                   // preskaèe "{"
		while((inputLine = System.console().readLine()) != "}"){
			temp.rule.add(inputLine);
			temp.numberOfRules = temp.numberOfRules + 1;
		}
		lexicalRuleList.add(temp);
	}

}
