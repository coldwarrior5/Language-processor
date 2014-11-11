package hr.unizg.fer.lab2;

import hr.unizg.fer.lab2.Parser.Production;
import hr.unizg.fer.lab2.Parser.Symbol;

import java.util.ArrayList;
import java.util.List;

public class BEGINS_sets {

	// extended symbols (just a helper structure)
	private class SymbolEx{
		Symbol mSym;
		Boolean mIsEmpty; // prazan znak (100. str.)
	}
	
	private Boolean[][] mBeginsWithSymbol;
	private List<SymbolEx> mSymbolsEx = new ArrayList<SymbolEx>();
	
	public BEGINS_sets(List<Symbol> symbols, List<Production> productions){
		
		for (int i = 0; i < symbols.size(); ++i){
			SymbolEx temp = new SymbolEx();
			temp.mSym = symbols.get(i);
			temp.mIsEmpty = false;
			mSymbolsEx.add(temp);
		}
		
		
		Process_IsEmpty(productions);
		Boolean[][] beginsDirectlyWithSymbol = new Boolean[mSymbolsEx.size()][mSymbolsEx.size()];
		Process_BeginsDirectlyWithSymbol(productions, beginsDirectlyWithSymbol);
		mBeginsWithSymbol = new Boolean[mSymbolsEx.size()][mSymbolsEx.size()];
		Process_BeginsWithSymbol(beginsDirectlyWithSymbol);
	}
	
	private void Process_IsEmpty(List<Production> productions){
		// First step is to select symbols that are on the left side of the epsilon production
		// as empty symbols.
		for (int i = 0; i < productions.size(); ++i){
			if (productions.get(i).mRightSymbolsIndices.get(0) == Utilities.ProductionEpsilonCode)
				mSymbolsEx.get(productions.get(i).mLeftNonTerminalSymbolIndex).mIsEmpty = true;
		}
		
		// Now we do the same for the symbols that are on the left side of a productions that has 
		// all empty symbols on the right side.
		Boolean goAgain = true;
		while (goAgain){
			goAgain = false;
			for (int i = 0; i < productions.size(); ++i){
				Boolean allEmptyOnTheRightSide = true;
				for (int j = 0; j < productions.get(i).mRightSymbolsIndices.size(); ++j){
					if (productions.get(i).mRightSymbolsIndices.get(j) != Utilities.ProductionEpsilonCode &&
							mSymbolsEx.get(productions.get(i).mRightSymbolsIndices.get(j)).mIsEmpty == false){
						allEmptyOnTheRightSide = false;
						break;
					}
				}
				if (allEmptyOnTheRightSide &&
						mSymbolsEx.get(productions.get(i).mLeftNonTerminalSymbolIndex).mIsEmpty == false){
					goAgain = true;
					mSymbolsEx.get(productions.get(i).mLeftNonTerminalSymbolIndex).mIsEmpty = true;
				}
			}
		}
	}
	
	private void Process_BeginsDirectlyWithSymbol(List<Production> productions, Boolean[][] beginsDirectlyWithSymbol){
		// First set all pairs to false.
		for (int i = 0; i < mSymbolsEx.size(); ++i){
			for (int j = 0; j < mSymbolsEx.size(); ++j){ // for each pair of symbols
				beginsDirectlyWithSymbol[i][j] = false;
			}
		}
		// Then set to true only the appropriate pairs.
		for (int i = 0; i < mSymbolsEx.size(); ++i){
			SymbolEx firstSym = mSymbolsEx.get(i);
			if (firstSym.mSym.mIsTerminal) continue; // only nonterminal symbols can be processed
			for (int j = 0; j < mSymbolsEx.size(); ++j){
				SymbolEx secondSym = mSymbolsEx.get(j);
				for (int k = 0; k < productions.size(); ++k){
					if (firstSym == mSymbolsEx.get(productions.get(k).mLeftNonTerminalSymbolIndex)){
						// now we go through all of the productions
						for (int l = 0; l < productions.get(k).mRightSymbolsIndices.size(); ++l){
							int productionSymIndex = productions.get(k).mRightSymbolsIndices.get(l);
							if (productionSymIndex != Utilities.ProductionEpsilonCode){
								if (mSymbolsEx.get(productionSymIndex) == secondSym){
									beginsDirectlyWithSymbol[i][j] = true;
									break;// we found our 'secondSym' in production right side symbols
								}else if (mSymbolsEx.get(productionSymIndex).mIsEmpty){
									continue;// so far there are only empty symbols in this production
								}else{
									break;// because whatever comes next won't be in 'beginsDirectlyWithSymbol' relation.
								}
							}
							else break;
						}
					}
				}
			}
		}
	}
	
	private void Process_BeginsWithSymbol(Boolean[][] beginsDirectlyWithSymbol){
		// First set all pairs equal to the ones from 'beginsDirectlyWithSymbol'.
		for (int i = 0; i < mSymbolsEx.size(); ++i){
			for (int j = 0; j < mSymbolsEx.size(); ++j){ // for each pair of symbols
				if (i != j) mBeginsWithSymbol[i][j] = beginsDirectlyWithSymbol[i][j];
				else mBeginsWithSymbol[i][j] = true; // it is a reflective relation
			}
		}
		// Now update transitive relations
		Boolean goAgain = true;
		while (goAgain){
			goAgain = false;
			// first we pick a column in the table (page 101)
			for (int i = 0; i < mSymbolsEx.size(); ++i){
				// now we pick a relation ->  r(j, i)
				for (int j = 0; j < mSymbolsEx.size(); ++j){
					if (mBeginsWithSymbol[j][i]){
						// now find this relation -> r(k, j)
						for (int k = 0; k < mSymbolsEx.size(); ++k){
							if (mBeginsWithSymbol[k][j] && mBeginsWithSymbol[k][i] == false){
								mBeginsWithSymbol[k][i] = true; // update transition
								goAgain = true;
							}
						}
					}
				}
			}
		}
	}

	public List<Integer> Get_BEGINS(int symbolIndex){		
		List<Integer> tempList = new ArrayList<Integer>();
		// go through whole row
		for (int i = 0; i < mSymbolsEx.size(); ++i){
			if (mBeginsWithSymbol[symbolIndex][i] 
					&& mSymbolsEx.get(i).mSym.mIsTerminal) // must be a terminal symbol
				tempList.add(i);
		}
		
		return tempList;
	}
	
	public List<Integer> Get_BEGINS(List<Integer> symbolIndexList){
		List<Integer> tempList = new ArrayList<Integer>();
		// go through whole row
		for (int i = 0; i < symbolIndexList.size(); ++i){	
			List<Integer> retList = Get_BEGINS(symbolIndexList.get(i));
			for (int j = 0; j < retList.size(); ++j){
				if (!tempList.contains(retList.get(j))) tempList.add(retList.get(j));
			}
			if (!mSymbolsEx.get(symbolIndexList.get(i)).mIsEmpty) break;
		}
		
		return tempList;
	}

	public Boolean IsEmpty(int symbolIndex){
		return mSymbolsEx.get(symbolIndex).mIsEmpty;
	}
	
	public Boolean AreEmpty(List<Integer> symbolIndexList){
		for(int i = 0; i < symbolIndexList.size(); ++i){
			if (mSymbolsEx.get(symbolIndexList.get(i)).mIsEmpty == false) return false;
		}
		return true;
	}
}
