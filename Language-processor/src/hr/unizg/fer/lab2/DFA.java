package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DFA {

	private class Transition{
		StateDFA mNextState;		// The state NFA will be in after this transition completes
		int mInput; 				// Input symbol index
	}
	
	private List<StateDFA> mStates = new ArrayList<StateDFA>();
	private Parser mParser;
	private NFA mNFA;
	private Map<StateDFA, List<Transition>> mTransitions = new HashMap<StateDFA, List<Transition>>();	
	
	public DFA(Parser parser, NFA nfa){
		
		mParser = parser;
		mNFA = nfa;
		mNFA.Reset();
		StateDFA newState = CreateState(); // First state is also the initial state.
		mStates.add(newState);
		ProcessState(newState);
	}
	
	public List<StateDFA> GetStates(){
		return mStates;
	}
	
	public StateDFA GetTransition(StateDFA state, int input){
		if (mTransitions.containsKey(state)){
			List<Transition> temp = mTransitions.get(state);
			for (int i = 0; i < temp.size(); ++i){
				if (temp.get(i).mInput == input) return temp.get(i).mNextState;
			}
		}
		return null;
	}
	
	// Creates a state populated with items that are currently used by NFA.
	private StateDFA CreateState(){
		StateDFA tempS = new StateDFA();
		
		for (int i = 0; i < mNFA.GetStates().size(); ++i)
			if (mNFA.GetStates().get(i).mUsed){
				tempS.mItems.add(mNFA.GetStates().get(i).mItem);
			}
		
		return tempS;
	}
	
	// Sets the states of the NFA so that it matches 'StateDFA' state.
	private void SetStateForNFA(StateDFA state){
		for (int i = 0; i < mNFA.GetStates().size(); ++i) mNFA.GetStates().get(i).mUsed = false;
		
		for (int i = 0; i < mNFA.GetStates().size(); ++i)
			for (int j = 0; j < state.mItems.size(); ++j)
				if (mNFA.GetStates().get(i).mItem == state.mItems.get(j)) mNFA.GetStates().get(i).mUsed = true;
	}
	
	// Returns -1 if there is no such state.
	private int GetIndexOf(StateDFA state){
		// For each state.
		for (int i = 0; i < mStates.size(); ++i){
			// Both states must have equal number of items.
			if (mStates.get(i).mItems.size() == state.mItems.size()){
				// For each item in a state.
				Boolean match = true;
				for (int j = 0; j < mStates.get(i).mItems.size() && match; ++j){
					Item it1 = state.mItems.get(j);
					Item it2 = mStates.get(i).mItems.get(j);
					if (it1.mItemDotPosition == it2.mItemDotPosition &&
							it1.mLeftNonTerminalSymbolIndex == it2.mLeftNonTerminalSymbolIndex &&
							it1.mBEGINS_Set.size() == it2.mBEGINS_Set.size() &&
							it1.mRightSymbolsIndices.size() == it2.mRightSymbolsIndices.size()){
						
						for (int k = 0; k < it1.mRightSymbolsIndices.size(); ++k){ // check right symbols
							if (it1.mRightSymbolsIndices.get(k) != it2.mRightSymbolsIndices.get(k)){
								match = false;
								break;
							}
						}
						if (!match) continue;
						for (int k = 0; k < it1.mBEGINS_Set.size(); ++k){ // check b_sets symbols
							if (it1.mBEGINS_Set.get(k) != it2.mBEGINS_Set.get(k)){
								match = false;
								break;
							}
						}						
					}
					else{
						match = false;
						break; // it doesn't make sense to check other items
					}
				}
				if (match) return i;
			}
			
		}
		return -1;
	}
	
	private void ProcessState(StateDFA state){
		// For each symbol.
		for (int i = 0; i < mParser.GetSymbols().size(); ++i){
			SetStateForNFA(state);
			mNFA.InputSymbol(i);
			if (!mNFA.UsedStatesExist()) continue;
			
			StateDFA newState = CreateState();
			int newStateIndex;
			if ((newStateIndex = GetIndexOf(newState)) == -1){ // does this state already exist?
				mStates.add(newState); // if not: add it
				ProcessState(newState);
			}
			else newState = mStates.get(newStateIndex); // if yes: get it
			
			// Now create the transition
			if (!mTransitions.containsKey(state)) mTransitions.put(state, new ArrayList<Transition>());
			Transition tempT = new Transition();
			tempT.mInput = i;
			tempT.mNextState = newState;
			mTransitions.get(state).add(tempT);
		}
	}
}
