package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpsilonNFA {
	
	private class Transition{
		StateNFA mNextState;			// The state NFA will be in after this transition completes
		int mInput; 				// Input symbol index
	}

	private class EpsilonTransition{
		StateNFA mNextState;			// The state NFA will be in after this transition completes
	}
	
	private List<StateNFA> mStates = new ArrayList<StateNFA>();
	private Map<StateNFA, List<Transition>> mTransitions = new HashMap<StateNFA, List<Transition>>();
	private Map<StateNFA, List<EpsilonTransition>> mEpsilonTransitions = new HashMap<StateNFA, List<EpsilonTransition>>();
	private Parser mParser;
	private BEGINS_sets mB_Sets;
	
	public EpsilonNFA(Parser p, BEGINS_sets b_sets){
		mParser = p;
		mB_Sets = b_sets;
		// The following is implementation of the algorithm presented on the page 148.
		
		// This is 4th step in the algorithm
		for (int i = 0; i < p.GetProductions().size(); ++i){
			// case 4.a), we need only the initial productions
			if (p.GetProductions().get(i).mLeftNonTerminalSymbolIndex == p.GetInitialNonTerminalSymbol()){
				List<Integer> b_set = new ArrayList<Integer>();
				b_set.add(Utilities.EOFTerminalSymbolCode);
				StateNFA s = CreateState(p.GetProductions().get(i).mLeftNonTerminalSymbolIndex,
						p.GetProductions().get(i).mRightSymbolsIndices, true, 0, b_set);
				mStates.add(s);
			}
		}
		// process newly added initial states
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mIsInitial)
				ProcessState(mStates.get(i));
		
		DoEpsilonTransitions();
	}
	
	public List<StateNFA> GetStates(){
		return mStates;
	}
	
	public void Reset(){
		for (int i = 0; i < mStates.size(); ++i)
			mStates.get(i).mUsed = mStates.get(i).mIsInitial;
		
		DoEpsilonTransitions();
	}
	
	public void InputSymbol(int input){
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mUsed && mTransitions.containsKey(mStates.get(i))){
				List<Transition> temp = mTransitions.get(mStates.get(i));
				for (int j = 0; j < temp.size(); ++j){
					if (temp.get(j).mInput == input){
						temp.get(j).mNextState.mUsedInNextStep = true;
					}
				}
			}
		for (int i = 0; i < mStates.size(); ++i){
			mStates.get(i).mUsed = mStates.get(i).mUsedInNextStep;
			mStates.get(i).mUsedInNextStep = false;
		}
		
		DoEpsilonTransitions();
	}
	
	public void DoEpsilonTransitions(){
		Boolean goAgain = true;
		while (goAgain){
			goAgain = false;
			for (int i = 0; i < mStates.size(); ++i)
				if (mStates.get(i).mUsed && mEpsilonTransitions.containsKey(mStates.get(i))){
					List<EpsilonTransition> temp = mEpsilonTransitions.get(mStates.get(i));
					for (int j = 0; j < temp.size(); ++j){
						if (!temp.get(j).mNextState.mUsed){
							temp.get(j).mNextState.mUsed = true;
							goAgain = true;
						}
					}
				}
		}
	}
	
	private StateNFA CreateState(int leftNonTerminalSymbol, List<Integer> rightSymbols, Boolean isInitial, int itemDotIndex, List<Integer> b_set){
		// States are actually items from the productions from the grammar. 
		StateNFA s = new StateNFA();
		s.mIsInitial = isInitial;
		s.mItem = new Item();
		s.mItem.mLeftNonTerminalSymbolIndex = leftNonTerminalSymbol;
		s.mItem.mItemDotPosition = itemDotIndex;
		// copy mRightSymbols array
		for (int i = 0; i < rightSymbols.size(); ++i){
			s.mItem.mRightSymbolsIndices.add(rightSymbols.get(i));
		}
		if (s.mItem.mRightSymbolsIndices.size() == 1 && // epsilon productions have s.mItem.mRightSymbolsIndices.size == 0
				s.mItem.mRightSymbolsIndices.get(0) == Utilities.ProductionEpsilonCode) 
			s.mItem.mRightSymbolsIndices.clear();
		// copy mBEGINS_Set array
		for (int i = 0; i < b_set.size(); ++i){
			s.mItem.mBEGINS_Set.add(b_set.get(i));
		}
		
		s.mUsed = isInitial;
		s.mUsedInNextStep = false;
		return s;
	}
	
	private int GetIndexOf(StateNFA sIn){		
		// Check if there is already a state just like this one.
		for (int i = 0; i < mStates.size(); ++i){
			if (mStates.get(i).mItem.mLeftNonTerminalSymbolIndex == sIn.mItem.mLeftNonTerminalSymbolIndex &&
					mStates.get(i).mIsInitial == sIn.mIsInitial &&
					mStates.get(i).mItem.mItemDotPosition == sIn.mItem.mItemDotPosition &&
					mStates.get(i).mItem.mRightSymbolsIndices.size() == sIn.mItem.mRightSymbolsIndices.size() &&
					mStates.get(i).mItem.mBEGINS_Set.size() == sIn.mItem.mBEGINS_Set.size()){
				Boolean match = true;
				for (int j = 0; j < mStates.get(i).mItem.mRightSymbolsIndices.size(); ++j){ // check right symbols
					if (mStates.get(i).mItem.mRightSymbolsIndices.get(j) != sIn.mItem.mRightSymbolsIndices.get(j)){
						match = false;
						break;
					}
				}
				if (!match) continue;
				for (int j = 0; j < mStates.get(i).mItem.mBEGINS_Set.size(); ++j){ // check b_sets symbols
					if (mStates.get(i).mItem.mBEGINS_Set.get(j) != sIn.mItem.mBEGINS_Set.get(j)){
						match = false;
						break;
					}
				}
				if (match) return i;
			}
		}
		return -1;
	}
	
	private void ProcessState(StateNFA state){
		// This is basically implementation of 4.b) and 4.c)
		// It uses recursion
		
		// first 4.b)
		if (state.mItem.mRightSymbolsIndices.size() != state.mItem.mItemDotPosition){ // Does this state represent a complete item.
			// create state, create transition, recursive call
			StateNFA newState = CreateState(state.mItem.mLeftNonTerminalSymbolIndex, state.mItem.mRightSymbolsIndices,
					false, state.mItem.mItemDotPosition + 1, state.mItem.mBEGINS_Set);
			int newStateIndex;
			if ((newStateIndex = GetIndexOf(newState)) == -1){ // does this state already exist?
				mStates.add(newState); // if not: add it
				ProcessState(newState);
			}
			else newState = mStates.get(newStateIndex); // if yes: get it
			
			// Now create the transition
			if (!mTransitions.containsKey(state)) mTransitions.put(state, new ArrayList<Transition>());
			Transition tempT = new Transition();
			tempT.mInput = state.mItem.mRightSymbolsIndices.get(state.mItem.mItemDotPosition);
			tempT.mNextState = newState;
			mTransitions.get(state).add(tempT);
		}
		// now 4.c)
		if (state.mItem.mRightSymbolsIndices.size() != state.mItem.mItemDotPosition &&
				mParser.GetSymbols().get(state.mItem.mRightSymbolsIndices.get(state.mItem.mItemDotPosition)).mIsTerminal == false){
			int symbolIndex = state.mItem.mRightSymbolsIndices.get(state.mItem.mItemDotPosition);
			// create state, create transition, recursive call
			for (int i = 0; i < mParser.GetProductions().size(); ++i){
				if (mParser.GetProductions().get(i).mLeftNonTerminalSymbolIndex == symbolIndex){
					// This is beta from the book.
					List<Integer> beta = new ArrayList<Integer>();
					for (int j = state.mItem.mItemDotPosition + 1; j < state.mItem.mRightSymbolsIndices.size(); ++j) 
						beta.add(state.mItem.mRightSymbolsIndices.get(j)); // fill beta with everything right of the dot
					List<Integer> b_set = mB_Sets.Get_BEGINS(beta);
					// check for rule ii)
					if (mB_Sets.AreEmpty(beta)){
						for (int j = 0; j < state.mItem.mBEGINS_Set.size(); ++j)
							if (!b_set.contains(state.mItem.mBEGINS_Set.get(j))) b_set.add(state.mItem.mBEGINS_Set.get(j));
					}
					StateNFA newState = CreateState(symbolIndex, mParser.GetProductions().get(i).mRightSymbolsIndices,
							(symbolIndex == mParser.GetInitialNonTerminalSymbol()), 0, b_set);
					int newStateIndex;
					if ((newStateIndex = GetIndexOf(newState)) == -1){ // does this state already exist?
						mStates.add(newState); // if not: add it
						ProcessState(newState);
					}
					else newState = mStates.get(newStateIndex); // if yes: get it
					
					// Now create the epsilon transition
					if (!mEpsilonTransitions.containsKey(state)) mEpsilonTransitions.put(state, new ArrayList<EpsilonTransition>());
					EpsilonTransition tempT = new EpsilonTransition();
					tempT.mNextState = newState;
					mEpsilonTransitions.get(state).add(tempT);
				}
			}
		}
		
	}
}
