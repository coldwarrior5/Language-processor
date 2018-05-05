package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFA {
	
	private class Transition{
		List<StateNFA> mNextStates;	// The states NFA will be in after this transition completes
		int mInput; 				// Input symbol index
	}
	
	private List<StateNFA> mStates = new ArrayList<StateNFA>();
	private Map<StateNFA, List<Transition>> mTransitions = new HashMap<StateNFA, List<Transition>>();
	
	public NFA(Parser p, EpsilonNFA eNFA){
		// Populate new array and figure out which states are initial for NFA
		eNFA.Reset();
		for (int i = 0; i < eNFA.GetStates().size(); ++i){
			StateNFA temp = new StateNFA();
			temp.mIsInitial = eNFA.GetStates().get(i).mUsed;
			temp.mItem = eNFA.GetStates().get(i).mItem;
			temp.mUsed = temp.mUsedInNextStep = false;
			mStates.add(temp);
		}
		
		// for each symbol
		for (int i = 0; i < p.GetSymbols().size(); ++i){
			// for each state
			for (int j = 0; j < mStates.size(); ++j){
				
				// set all .mUsed to false except the current one
				for (int k = 0; k < mStates.size(); ++k) eNFA.GetStates().get(k).mUsed = false;
				eNFA.GetStates().get(j).mUsed = true;
				eNFA.DoEpsilonTransitions();
				eNFA.InputSymbol(i);
				
				// save in the new transition
				Transition tempT = new Transition();
				tempT.mInput = i;
				tempT.mNextStates = new ArrayList<StateNFA>();
				for (int k = 0; k < mStates.size(); ++k)
					if (eNFA.GetStates().get(k).mUsed) 
						tempT.mNextStates.add(mStates.get(k)); // add the copy of a state in 'NFA.mStates'
				
				if (!mTransitions.containsKey(mStates.get(j))) 
					mTransitions.put(mStates.get(j), new ArrayList<Transition>());
				mTransitions.get(mStates.get(j)).add(tempT);
			}
		}
		
		Reset();
	}
	
	public List<StateNFA> GetStates(){
		return mStates;
	}
	
	public Boolean UsedStatesExist(){
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mUsed) return true;
		
		return false;
	}
	
	public void Reset(){
		for (int i = 0; i < mStates.size(); ++i)
			mStates.get(i).mUsed = mStates.get(i).mIsInitial;
	}
	
	public void InputSymbol(int input){
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mUsed && mTransitions.containsKey(mStates.get(i))){
				List<Transition> temp = mTransitions.get(mStates.get(i));
				for (int j = 0; j < temp.size(); ++j){
					if (temp.get(j).mInput == input){
						List<StateNFA> tempNS = temp.get(j).mNextStates;
						for (int k = 0; k < tempNS.size(); ++k){
							tempNS.get(k).mUsedInNextStep = true;
						}
					}
				}
			}
		
		for (int i = 0; i < mStates.size(); ++i){
			mStates.get(i).mUsed = mStates.get(i).mUsedInNextStep;
			mStates.get(i).mUsedInNextStep = false;
		}
	}
}
