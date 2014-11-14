package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.List;

public class ParsingTable {

	private List<PTCell_Action> mCellsA = new ArrayList<PTCell_Action>();
	private List<Integer> mCellsA_Priority = new ArrayList<Integer>();
	private List<PTCell_NewState> mCellsNS = new ArrayList<PTCell_NewState>();
	
	public ParsingTable(Parser p, DFA dfa){
		
		FillActionTable(p, dfa);
		FillNewStateTable(p, dfa);
	}
	
	public List<PTCell_Action> GetCellsAction(){
		return mCellsA;
	}
	
	public List<Integer> GetCellsAction_Priority(){
		return mCellsA_Priority;
	}
	
	public List<PTCell_NewState> GetCellsNewState(){
		return mCellsNS;
	}
	
	public void FillActionTable(Parser p, DFA dfa){
		// Filling the action table (book: page 150)
		// for every input terminal symbol
		for (int i = 0; i < p.GetSymbols().size(); ++i){
			if (!p.GetSymbols().get(i).mIsTerminal) continue;
			// for every state
			for (int j = 0; j < dfa.GetStates().size(); ++j){
				StateDFA tempS = dfa.GetStates().get(j);
				
				// 3.a) from the book
				StateDFA newState = dfa.GetTransition(tempS, i);
				if (newState != null){
					PTCell_Action tempC = new PTCell_Action();
					tempC.mInputIndex = i;
					tempC.mStateIndex = j;
					tempC.mActionType = ActionType.Move;
					tempC.mActionSpecificValue_a = dfa.GetStates().indexOf(newState); // index of a new state
					tempC.mActionSpecificValue_b = -1; // does not have one.
					mCellsA.add(tempC);
					mCellsA_Priority.add(-1); // irrelevant 
					}
				
				// 3.b) from the book
				for (int k = 0; k < tempS.mItems.size(); ++k){
					Item tempI = tempS.mItems.get(k);
					if (tempI.mBEGINS_Set.contains(i) && tempI.mItemDotPosition == tempI.mRightSymbolsIndices.size()){
						PTCell_Action tempC = new PTCell_Action();
						tempC.mInputIndex = i;
						tempC.mStateIndex = j;
						tempC.mActionType = ActionType.Reduce;
						tempC.mActionSpecificValue_a = tempI.mItemDotPosition; // number of symbols to remove from stack
						tempC.mActionSpecificValue_b = tempI.mLeftNonTerminalSymbolIndex; // <-- reduce to this
						mCellsA.add(tempC);
						mCellsA_Priority.add(p.GetProductions().get(tempI.mProductionIndex).mProductionPriority);
						}
					}
				}
			}
		
		// Now extra for EOF symbol
		// for every state
		for (int j = 0; j < dfa.GetStates().size(); ++j){
			StateDFA tempS = dfa.GetStates().get(j);
			
			// 3.b) from the book
			for (int k = 0; k < tempS.mItems.size(); ++k){
				Item tempI = tempS.mItems.get(k);
				if (tempI.mBEGINS_Set.contains(Utilities.EOFTerminalSymbolCode) && 
						tempI.mItemDotPosition == tempI.mRightSymbolsIndices.size() &&
						tempI.mLeftNonTerminalSymbolIndex != p.GetInitialNonTerminalSymbol()){
					PTCell_Action tempC = new PTCell_Action();
					tempC.mInputIndex = Utilities.EOFTerminalSymbolCode;
					tempC.mStateIndex = j;
					tempC.mActionType = ActionType.Reduce;
					tempC.mActionSpecificValue_a = tempI.mItemDotPosition; // number of symbols to remove from stack
					tempC.mActionSpecificValue_b = tempI.mLeftNonTerminalSymbolIndex; // <-- reduce to this
					mCellsA.add(tempC);
					mCellsA_Priority.add(p.GetProductions().get(tempI.mProductionIndex).mProductionPriority);
				}
			}
					
			// 3.c) from the book
			for (int k = 0; k < tempS.mItems.size(); ++k){
				Item tempI = tempS.mItems.get(k);
				if (tempI.mBEGINS_Set.contains(Utilities.EOFTerminalSymbolCode) && 
						tempI.mItemDotPosition == tempI.mRightSymbolsIndices.size() &&
						tempI.mLeftNonTerminalSymbolIndex == p.GetInitialNonTerminalSymbol()){
					PTCell_Action tempC = new PTCell_Action();
					tempC.mInputIndex = Utilities.EOFTerminalSymbolCode;
					tempC.mStateIndex = j;
					tempC.mActionType = ActionType.Accept;
					tempC.mActionSpecificValue_a = -1; // does not have one.
					tempC.mActionSpecificValue_b = -1; // does not have one.
					mCellsA.add(tempC);
					mCellsA_Priority.add(-1); // irrelevant 
				}
			}
		}
	}
	
	public void FillNewStateTable(Parser p, DFA dfa){
		// Filling the action table (book: page 150)
		// for every input non terminal symbol
		for (int i = 0; i < p.GetSymbols().size(); ++i){
			if (p.GetSymbols().get(i).mIsTerminal) continue;
			// for every state
			for (int j = 0; j < dfa.GetStates().size(); ++j){
				StateDFA tempS = dfa.GetStates().get(j);
				
				// 4.a) from the book
				StateDFA newState = dfa.GetTransition(tempS, i);
				if (newState != null){
					PTCell_NewState tempC = new PTCell_NewState();
					tempC.mInputIndex = i;
					tempC.mStateIndex = j;
					tempC.mActionSpecificValue = dfa.GetStates().indexOf(newState); // index of a new state
					mCellsNS.add(tempC);
				}
			}
		}
	}
}
