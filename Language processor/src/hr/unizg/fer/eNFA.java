package hr.unizg.fer;

import java.util.ArrayList;
import java.util.List;

public class eNFA {
	
	private class Transition{
		int mCurrentStateId;		// Current state
		int mNextStateId;			// The state NFA will be in after this transition completes
		char mInputC; 				// Input character
		Boolean mIsEpsilon;			// Flag that indicates if this is epsilon transition.
		//static final char Epsilon = (char)255;
		}
	
	private class State{
		int mId;					// Unique identifier of the state
		Boolean mUsed;				// Flag that indicates if NFA is currently in this state
		Boolean mOld;				// This tells us if this state needs to be set to mUsed=false
									// after all transitions are done.
		}
	
	List<Transition> mTransitions = new ArrayList<Transition>();
	List<State> mStates = new ArrayList<State>();
	
	private int mInitialStateId, mAcceptableStateId, mStatesNum;
	private Boolean mDoesNotAccept;
	
	/**
	 * Creates nondeterministic finite automata with epsilon transitions.
	 * @param regEx - is the input regular expression.
	 */
	public eNFA(String regEx){
		mStatesNum = 0;
		mInitialStateId = AddState().mId;
		mAcceptableStateId = AddState().mId;
		
		ProcessRegEx(regEx, mInitialStateId, mAcceptableStateId);
		Reset();
	}
	
	/**
	 * Resets the eNFA to the same state it was in after the constructor was called.
	 * @param - none
	 */
	public void Reset(){
		for (int i = 0; i < mStates.size(); ++i){
			if (mInitialStateId == i) mStates.get(i).mUsed = true;
			else mStates.get(i).mUsed = false;
		}
		
		DoEpsilonTransitions();
		mDoesNotAccept = false;
	}
	
	/**
	 * Inputs sequential stream of characters into automata and uses transitions to determine new states.
	 * @param - input string of characters
	 */
	public void InputString(String input){
		for (int i = 0; i < input.length(); ++i){
			InputChar(input.charAt(i));
		}
	}
	
	/**
	 * Inputs character into automata and uses transitions to determine new states.
	 * @param - input just one character
	 */
	public void InputChar(char input){
		for (int i = 0; i < mStates.size(); ++i) mStates.get(i).mOld = true;
		
		for (int i = 0; i < mTransitions.size(); ++i){
			int state1Index = FindState(mTransitions.get(i).mCurrentStateId);
			int state2Index = FindState(mTransitions.get(i).mNextStateId);
			if (mTransitions.get(i).mInputC == input &&
				mStates.get(state1Index).mUsed == true){
				mStates.get(state2Index).mOld = false;
				mStates.get(state2Index).mUsed = true;
			}
		}
		
		for (int i = 0; i < mStates.size(); ++i){
			if (mStates.get(i).mOld) mStates.get(i).mUsed = false;
		}
		
		DoEpsilonTransitions();
		
		mDoesNotAccept = true;
		for (int i = 0; i < mStates.size(); ++i){
			if (mStates.get(i).mUsed){
				mDoesNotAccept = false;
				break;
			}
		}
	}
	
	/**
	 * Check whether automata is in acceptable state. Use this after InputString() to see
	 * if your string is in language defined in this automata
	 * @param - none
	 */
	public Boolean IsInAcceptableState(){
		int stateId = FindState(mAcceptableStateId);
		return mStates.get(stateId).mUsed;
	}
	
	/**
	 * Check whether automata has run out of states, meaning the input so far is not in regular
	 * expression defined by this automata.
	 * @param - none
	 */
	public Boolean DoesNotAccept(){
		return mDoesNotAccept;
	}
	
	private void DoEpsilonTransitions(){
		Boolean goAgain = true;
		while (goAgain){
			goAgain = false;
			for (int i = 0; i < mTransitions.size(); ++i){
				int state1Index = FindState(mTransitions.get(i).mCurrentStateId);
				int state2Index = FindState(mTransitions.get(i).mNextStateId);
				if (mTransitions.get(i).mIsEpsilon &&
						mStates.get(state1Index).mUsed == true &&
						mStates.get(state2Index).mUsed == false){
					goAgain = true;
					mStates.get(state2Index).mUsed = true;
				}
			}
		}
	}
	
	private int FindState(int stateId){
		for (int i = 0; i < mStates.size(); ++i){
			if (mStates.get(i).mId == stateId) return i;
		}
		
		return -1;
	}
	
	private State AddState(){
		State temp = new State();
		temp.mId = mStatesNum++;
		temp.mUsed = false;
		mStates.add(temp);
		return temp;
	}
	
	private void AddTransition(int currentStateId, int nextStateId, char inputC){
		Transition temp = new Transition();
		temp.mInputC = inputC;
		temp.mCurrentStateId = currentStateId;
		temp.mNextStateId = nextStateId;
		temp.mIsEpsilon = false;
		mTransitions.add(temp);
	}
	
	private void AddEpsilonTransition(int currentStateId, int nextStateId){
		Transition temp = new Transition();
		temp.mCurrentStateId = currentStateId;
		temp.mNextStateId = nextStateId;
		temp.mIsEpsilon = true;
		mTransitions.add(temp);
	}
	
	private void ProcessRegEx(String regEx, int leftStateId, int rightStateId){
		//regEx = RemoveUnnecessaryBrackets(regEx);
		// first we separate the regEx by '|' operator into separate regular expressions
		List<String> choices = new ArrayList<String>();
		int bracketsNum = 0;
		int lastSubstringEndIndex = 0;
		for (int i = 0; i < regEx.length(); ++i){
			if (regEx.charAt(i) == '(' && IsOperator(regEx, i)) ++bracketsNum;
			else if (regEx.charAt(i) == ')' && IsOperator(regEx, i)) --bracketsNum;
			else if (bracketsNum == 0 && regEx.charAt(i) == '|' && IsOperator(regEx, i)){
				String s = regEx.substring(lastSubstringEndIndex, i);
				lastSubstringEndIndex = i+1;
				choices.add(s);
			}
		}
		// add the last choice (this could also be the only one)
		String s = regEx.substring(lastSubstringEndIndex, regEx.length());
		choices.add(s);
		
		if (choices.size() >= 2){ // at least one choice operator ('|') 
			// has been found outside of the brackets
			for (int i = 0; i < choices.size(); ++i){
				// add epsilon transitions
				int tempLeftStateId = AddState().mId;
				int tempRightStateId = AddState().mId;
				AddEpsilonTransition(leftStateId, tempLeftStateId);
				AddEpsilonTransition(tempRightStateId, rightStateId);
				ProcessRegEx(choices.get(i), tempLeftStateId, tempRightStateId);
				}
		}
		else{ // no choice operators outside of the brackets.
			// This also mean that choices.get(0) == regEx
			// concatenation
			Boolean prefixFlag = false;
			int lastStateId = leftStateId;
			for (int i = 0; i < regEx.length(); ++i){
				int a, b;
				if (prefixFlag){ // 1
					prefixFlag = false;
					char inputC;
					// jos dodati za * $ | \ <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					if (regEx.charAt(i) == '*') inputC = '*';
					if (regEx.charAt(i) == '$') inputC = '$';
					if (regEx.charAt(i) == '|') inputC = '|';
					if (regEx.charAt(i) == '\\') inputC = '\\';
					
					if (regEx.charAt(i) == 't') inputC = '\t';
					else if (regEx.charAt(i) == 'n') inputC = '\n';
					else if (regEx.charAt(i) == '_') inputC = ' ';
					else inputC = regEx.charAt(i);
					
					a = AddState().mId;
					b = AddState().mId;
					AddTransition(a, b, inputC);
				}
				else{ // 2
					if (regEx.charAt(i) == '\\'){
						prefixFlag = true;
						continue;
					}
					if (regEx.charAt(i) != '('){ // 2a
						
						a = AddState().mId;
						b = AddState().mId;
						if (regEx.charAt(i) == '$') AddEpsilonTransition(a, b);
						else AddTransition(a, b, regEx.charAt(i));
					}
					else{ // 2b
						// first find the appropriate closed bracket character index
						int j = -1;
						int bracketsCount = 0;
						for (int k = i + 1; k < regEx.length(); ++k){
							if (bracketsCount == 0 && regEx.charAt(k) == ')' && IsOperator(regEx, k)){
								j = k;
								break;
							}
							else if (regEx.charAt(k) == '(' && IsOperator(regEx, k)) ++bracketsCount;
							else if (regEx.charAt(k) == ')' && IsOperator(regEx, k)) --bracketsCount;
						}
						a = AddState().mId;
						b = AddState().mId;
						ProcessRegEx(regEx.substring(i+1, j), a, b);
						i = j;						
					}
				}
				
				// Check for Kleen operator
				if ((i+1) < regEx.length() && regEx.charAt(i+1) == '*'){
					int x = a;
					int y = b;
					a = AddState().mId;
					b = AddState().mId;
					
					AddEpsilonTransition(a, x);
					AddEpsilonTransition(y, b);
					AddEpsilonTransition(a, b);
					AddEpsilonTransition(y, x);
					i = i+1;
				}
				
				// connecting this part with the rest of the NFA
				AddEpsilonTransition(lastStateId, a);
				lastStateId = b;
			}
			//dodaj_epsilon_prijelaz(automat, lastStateId, desno_stanje)	
			AddEpsilonTransition(lastStateId, rightStateId);
		}		
	}
	
	private Boolean IsOperator(String regEx, int i){
		int br = 0;
		while (i-1 >= 0 && regEx.charAt(i-1) == '\\') { // one \, like in C
			br = br + 1;
			i = i - 1;
		}
		return br%2 == 0;
	}
}
