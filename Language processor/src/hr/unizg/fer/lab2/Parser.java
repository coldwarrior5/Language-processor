package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Parser {
	
	public class Production{
		int mLeftNonTerminalSymbolIndex;
		List<Integer> mRightSymbolsIndices = new ArrayList<Integer>();
		int mProductionPriority; // this is important for Reduce/Reduce contradiction.
	}
	
	public class Symbol{
		String mSy;
		Boolean mIsTerminal;
	}
	
	private List<Symbol> mSymbols = new ArrayList<Symbol>();
	private List<Integer> mSyncronizationSymbols = new ArrayList<Integer>();
	private List<Production> mProductions = new ArrayList<Production>();
	
	public List<Symbol> GetSymbols() {
		return mSymbols;
	}
	
	public List<Integer> GetSyncronizationSymbols() {
		return mSyncronizationSymbols;
	}
	
	public List<Production> GetProductions() {
		return mProductions;
	}
	
	public int GetInitialNonTerminalSymbol(){
		return 0; // First declared symbol is also the initial one in grammar.
	}
	
	public Parser(){
		
	    String input = Utilities.ReadStringFromInput();
	    List<String> list = Arrays.asList(input.split("\n"));
	    Iterator<String> it = list.iterator();
	    String line = it.next();
	    	
	    if(line.substring(0,2).equals("%V")){
			FillNonTerminalSymbols(line);
			line = it.next();
		};
		// Now add a helper initial non terminal symbol at the beginning of the array.
		// <S'>   ->   <S>
		Symbol tempHS = new Symbol();
		tempHS.mIsTerminal = false;
		tempHS.mSy = "<S'>";
		mSymbols.add(0, tempHS);
		
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
		int productionPriority = 1;
		int currentLeftSideSymIndex = 0;
		while(line != null){
			if (line.substring(0,1).equals("<")){ // should we change production's left side
				currentLeftSideSymIndex = FindSymbolIndex(line);
			}else{ // or work on the current one
				temp = new Production();
				temp.mProductionPriority = productionPriority++;
				temp.mLeftNonTerminalSymbolIndex = currentLeftSideSymIndex;
				FillProduction(temp, line);
				mProductions.add(temp);
			}
			
			if(it.hasNext()){
				line = it.next();
			}
			else{
				line=null;
			}
		};
		// Now add a production for the initial non terminal helper symbol.
		// <S'>   ->   <S>
		temp = new Production();
		temp.mLeftNonTerminalSymbolIndex = 0;
		temp.mRightSymbolsIndices = new ArrayList<Integer>();
		temp.mRightSymbolsIndices.add(1);
		temp.mProductionPriority = 0;
		mProductions.add(temp);	
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
			mSyncronizationSymbols.add(FindSymbolIndex(temp));
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
			
			production.mRightSymbolsIndices.add(FindSymbolIndex(temp));
				
			inputLine = inputLine.substring(i);
		}while(inputLine.length() > 0);
	}
	
	private int FindSymbolIndex(String sym){
		
		if (sym.equals("$"))
			return Utilities.ProductionEpsilonCode;
		else{
			for (int i = 0; i < mSymbols.size(); ++i){
				if (mSymbols.get(i).mSy.equals(sym)) return i;
			}
			return Utilities.ProductionErrorCode;
		}
	}
}
