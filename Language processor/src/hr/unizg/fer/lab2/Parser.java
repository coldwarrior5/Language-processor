package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Parser {
	
	public class Production{
		int mLeftNonTerminalSymbol;
		List<Integer> mRightSymbols = new ArrayList<Integer>();
	}
	
	public class Symbol{
		String mSy;
		Boolean mIsTerminal;
	}
	
	private List<Symbol> mSymbols = new ArrayList<Symbol>();
	private List<String> mSyncronizationSymbols = new ArrayList<String>();
	private List<Production> mProductions = new ArrayList<Production>();
	
	public Parser(){
		
	    String input = UtilitiesLA.ReadStringFromInput();
	    List<String> list = Arrays.asList(input.split("\n"));
	    Iterator<String> it = list.iterator();
	    String line = it.next();
	    	
	    if(line.substring(0,2).equals("%V")){
			FillNonTerminalSymbols(line);
			line = it.next();
		};
		
		if(line.substring(0,2).equals("%T")){
			FillTerminalSymbols(line);
			line = it.next();
		}
	
		if(line.substring(0,4).equals("%Syn")){
			FillSyncronizationSymbols(line);
			if(it.hasNext()){
				line = it.next();
			}
			else{
				line=null;
			}
		}
		
		Production temp = null;
		int currentLeftSideSymIndex = 0;
		while(line != null){
			if (line.substring(0,1).equals("<")){ // should we change production's left side
				currentLeftSideSymIndex = FindSymbolIndex(line);
			}else{ // or work on the current one
				temp = new Production();
				temp.mLeftNonTerminalSymbol = currentLeftSideSymIndex;
				mProductions.add(temp);
				FillProduction(temp, line);
			}
			
			if(it.hasNext()){
				line = it.next();
			}
			else{
				line=null;
			}
		};
	}
	
	private void FillNonTerminalSymbols(String inputLine){
		int i = 0;
		do{
			i = inputLine.indexOf(">") + 1;
			Symbol temp = new Symbol();
			temp.mIsTerminal = false;
			temp.mSy = inputLine.substring(inputLine.indexOf("<"), i);
			mSymbols.add(temp);
			inputLine = inputLine.substring(i);
		}while(inputLine.length() > 0);
	}
	
	private void FillTerminalSymbols(String inputLine){
		inputLine += " "; // this is needed for the algorithm to know when to stop.
		int i = inputLine.indexOf(" ") + 1; // first skip "%T "
		inputLine = inputLine.substring(i);
		do{
			i = inputLine.indexOf(" ") + 1;
			Symbol temp = new Symbol();
			temp.mIsTerminal = true;
			temp.mSy = inputLine.substring(0, i - 1);
			mSymbols.add(temp);
			inputLine = inputLine.substring(i);
		}while(inputLine.length() > 0);
	}
	
	private void FillSyncronizationSymbols(String inputLine){
		inputLine += " "; // this is needed for the algorithm to know when to stop.
		int i = inputLine.indexOf(" ") + 1; // first skip "%Syn "
		inputLine = inputLine.substring(i);
		do{
			i = inputLine.indexOf(" ") + 1;
			String temp = "";
			temp = inputLine.substring(0, i - 1);
			mSyncronizationSymbols.add(temp);
			inputLine = inputLine.substring(i);
		}while(inputLine.length() > 0);
	}
	
	private void FillProduction(Production production, String inputLine){
		inputLine += " "; // this is needed for the algorithm to know when to stop.
		int i = inputLine.indexOf(" ") + 1; // first skip " "
		inputLine = inputLine.substring(i);
		do{
			i = inputLine.indexOf(" ") + 1;
			String temp = "";
			temp = inputLine.substring(0, i - 1);
			
			production.mRightSymbols.add(FindSymbolIndex(temp));
		
				
			inputLine = inputLine.substring(i);
		}while(inputLine.length() > 0);
	}
	
	private int FindSymbolIndex(String sym){
		for (int i = 0; i < mSymbols.size(); ++i){
			if (mSymbols.get(i).mSy.equals(sym)){
				return i;
			}
		}
		return -1;
	}
}
