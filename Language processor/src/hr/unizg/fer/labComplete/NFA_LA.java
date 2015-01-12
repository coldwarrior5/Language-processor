package hr.unizg.fer.labComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class NFA_LA {
	
	private class Transition{
		State mNextState;			// The state NFA_LA will be in after this transition completes
		char mInputC; 				// Input character (null for epsilon transitions)
		}
	
	private class State{
		Boolean mUsed;				// Flag that indicates if NFA_LA is currently in this state
		Boolean mOld;				// This tells us if this state needs to be set to mUsed=false
									// after all transitions are done.
		int mSwapChainCode;
		HashMap<Character, List<State>> mTransitions = new HashMap<Character, List<State>>();
	}
	
	private Map<State, List<Transition>> mTransitions = new HashMap<State, List<Transition>>();
	private Map<State, List<Transition>> mEpsilonTransitions = new HashMap<State, List<Transition>>();
	private List<State> mStates = new ArrayList<State>();
	
	private State mInitialState, mAcceptableState;
	private Boolean mIsReset;	
	
	private List<Character> mInputCharacters = new ArrayList<Character>();
	private List<State> mSwapChain_a = new LinkedList<State>();
	private List<State> mSwapChain_b = new LinkedList<State>();
	private List<State> mCurrentStates;
	private List<State> mNotCurrentStates;
	private int mSwapChainCounter;
	
	/**
	 * Creates nondeterministic finite automata.
	 * @param regEx - is the input regular expression.
	 */
	public NFA_LA(String regEx){
		mInitialState = AddState();
		mAcceptableState = AddState();
		mIsReset = false;
		mSwapChainCounter = 0;
		
		ProcessRegEx_eNFA(regEx, mInitialState, mAcceptableState);		
		//SetUpNFA();
		//Reset();
		Reset_eNFA();
	}
	
	/**
	 * Resets the NFA_LA to the same state it was in after the constructor was called.
	 * @param - none
	 */
	public void Reset(){
		
		if (mIsReset) return; // Nothing to do here
		mIsReset = true;
		mSwapChain_a.clear();
		mSwapChain_b.clear();
		mSwapChain_a.add(mInitialState);
		mCurrentStates = mSwapChain_a;
		mNotCurrentStates = mSwapChain_b;
	}
	
	public void Reset_eNFA(){
		for (int i = 0; i < mStates.size(); ++i) mStates.get(i).mUsed = false;
		mInitialState.mUsed = true;
		
		DoEpsilonTransitions_eNFA();
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
	
	public void InputString_eNFA(String input){
		for (int i = 0; i < input.length(); ++i){
			InputChar_eNFA(input.charAt(i));
		}
	}
	
	/**
	 * Inputs character into automata and uses transitions to determine new states.
	 * @param - input just one character
	 */
	public void InputChar(char input){
		
		if (mSwapChainCounter == Integer.MAX_VALUE){
			for (int i = 0; i < mStates.size(); ++i){
				mStates.get(i).mSwapChainCode = 0;
			}
			mSwapChainCounter = 1;
		}
		else{
			++mSwapChainCounter;
		}
		
		for (Iterator<State> itr = mCurrentStates.iterator(); itr.hasNext();){
			State temp = itr.next();
			if (temp.mTransitions.containsKey(input)){
				List<State> newStates = temp.mTransitions.get(input);
				for (int j = 0; j < newStates.size(); ++j){
					if (newStates.get(j).mSwapChainCode != mSwapChainCounter){ // already added
						mNotCurrentStates.add(newStates.get(j));
						newStates.get(j).mSwapChainCode = mSwapChainCounter;
					}
				}
			}
			itr.remove(); // remove from mCurrentStates
		}
		
		// swap states
		List<State> temp = mCurrentStates;
		mCurrentStates = mNotCurrentStates;
		mNotCurrentStates = temp;
		mIsReset = false;
	}
	
	public void InputChar_eNFA(char input){
		for (int i = 0; i < mStates.size(); ++i) mStates.get(i).mOld = true;
		/*
		for (int i = 0; i < mTransitions.size(); ++i){
			State state1 = mTransitions.get(i).mCurrentState;
			State state2 = mTransitions.get(i).mNextState;
			if (mTransitions.get(i).mInputC == input &&
				state1.mUsed == true){
				state2.mOld = false;
				state2.mUsed = true;
			}
		}
		*/

		
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mUsed){
				if (mTransitions.containsKey(mStates.get(i))){
					List<Transition> tempT = mTransitions.get(mStates.get(i));
					for (int j = 0; j < tempT.size(); ++j){
							if (tempT.get(j).mInputC == input){
								tempT.get(j).mNextState.mUsed = true;
								tempT.get(j).mNextState.mOld = false;
						}
					}
				}
			}
		
		for (int i = 0; i < mStates.size(); ++i){
			if (mStates.get(i).mOld) mStates.get(i).mUsed = false;
		}
		
		DoEpsilonTransitions_eNFA();
	}
	
	/**
	 * Check whether automata is in acceptable state. Use this after InputString() to see
	 * if your string is in language defined in this automata
	 * @param - none
	 */
	public Boolean IsInAcceptableState(){
		return mCurrentStates.contains(mAcceptableState);
	}
	
	public Boolean IsInAcceptableState_eNFA(){
		return mAcceptableState.mUsed;
	}
	
	/**
	 * Check whether automata has run out of states, meaning the input so far is not in regular
	 * expression defined by this automata.
	 * @param - none
	 */
	public Boolean DoesNotAccept(){
		return (mCurrentStates.size() == 0);
	}
	
	public Boolean DoesNotAccept_eNFA(){
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mUsed)return false;
		
		return true;
	}
	
	private void DoEpsilonTransitions_eNFA(){
		
		/*Boolean goAgain = true;
		while (goAgain){
			goAgain = false;
			for (int i = 0; i < mTransitions.size(); ++i){
				State state1 = mTransitions.get(i).mCurrentState;
				State state2 = mTransitions.get(i).mNextState;
				if (mTransitions.get(i).mIsEpsilon &&
						state1.mUsed == true &&
								state2.mUsed == false){
					goAgain = true;
					state2.mUsed = true;
				}
			}
		}*/	
		
		List<State> tempS = new LinkedList<State>();
		for (int i = 0; i < mStates.size(); ++i)
			if (mStates.get(i).mUsed) tempS.add(mStates.get(i));
		
		for (ListIterator<State> itr = tempS.listIterator(); itr.hasNext();){
			State itrState = itr.next();
			if (mEpsilonTransitions.containsKey(itrState)){
				List<Transition> tempT = mEpsilonTransitions.get(itrState);
			
				for (int j = 0; j < tempT.size(); ++j){
					if (!tempS.contains(tempT.get(j).mNextState)){ // if it is not in tempS then
						tempT.get(j).mNextState.mUsed = true;
						itr.add(tempT.get(j).mNextState);
						itr.previous();
					}
				}
			}
		}
	}
	
	private State AddState(){
		State temp = new State();
		temp.mUsed = false;
		temp.mSwapChainCode = 0;
		mStates.add(temp);
		return temp;
	}
	
	private void AddTransition(State currentState, State nextState, char inputC){
		Transition temp = new Transition();
		temp.mInputC = inputC;
		temp.mNextState = nextState;
		/*mTransitions.add(temp);*/
		
		if (!mTransitions.containsKey(currentState)) 
			mTransitions.put(currentState, new ArrayList<Transition>());
		mTransitions.get(currentState).add(temp);
	}
	
	private void AddEpsilonTransition(State currentState, State nextState){
		Transition temp = new Transition();
		temp.mNextState = nextState;
		/*mTransitions.add(temp);*/
		
		if (!mEpsilonTransitions.containsKey(currentState)) 
			mEpsilonTransitions.put(currentState, new ArrayList<Transition>());
		mEpsilonTransitions.get(currentState).add(temp);
	}
	
	private void ProcessRegEx_eNFA(String regEx, State leftState, State rightState){
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
				State tempLeftState = AddState();
				State tempRightState = AddState();
				AddEpsilonTransition(leftState, tempLeftState);
				AddEpsilonTransition(tempRightState, rightState);
				ProcessRegEx_eNFA(choices.get(i), tempLeftState, tempRightState);
				}
		}
		else{ // no choice operators outside of the brackets.
			// This also mean that choices.get(0) == regEx
			// concatenation
			Boolean prefixFlag = false;
			State lastState = leftState;
			for (int i = 0; i < regEx.length(); ++i){
				State a, b;
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
					
					a = AddState();
					b = AddState();
					AddTransition(a, b, inputC);
					if (!mInputCharacters.contains(inputC)) mInputCharacters.add(inputC);
				}
				else{ // 2
					if (regEx.charAt(i) == '\\'){
						prefixFlag = true;
						continue;
					}
					if (regEx.charAt(i) != '('){ // 2a
						
						a = AddState();
						b = AddState();
						if (regEx.charAt(i) == '$') AddEpsilonTransition(a, b);
						else{
							AddTransition(a, b, regEx.charAt(i));
							if (!mInputCharacters.contains(regEx.charAt(i))) mInputCharacters.add(regEx.charAt(i));
						}
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
						a = AddState();
						b = AddState();
						ProcessRegEx_eNFA(regEx.substring(i+1, j), a, b);
						i = j;						
					}
				}
				
				// Check for Kleen operator
				if ((i+1) < regEx.length() && regEx.charAt(i+1) == '*'){
					State x = a;
					State y = b;
					a = AddState();
					b = AddState();
					
					AddEpsilonTransition(a, x);
					AddEpsilonTransition(y, b);
					AddEpsilonTransition(a, b);
					AddEpsilonTransition(y, x);
					i = i+1;
				}
				
				// connecting this part with the rest of the NFA_LA
				AddEpsilonTransition(lastState, a);
				lastState = b;
			}
			//dodaj_epsilon_prijelaz(automat, lastStateId, desno_stanje)	
			AddEpsilonTransition(lastState, rightState);
		}		
	}
	
	@SuppressWarnings("unused")
	private void SetUpNFA(){
		
		// for every state
		for (int i = 0; i < mStates.size(); ++i){
			
			State st = mStates.get(i);
			for (int j = 0; j < mStates.size(); ++j) mStates.get(j).mUsed = false;
			st.mUsed = true;
			DoEpsilonTransitions_eNFA();
			for (int j = 0; j < mStates.size(); ++j){
				State st_e = mStates.get(j);
				if (st_e.mUsed && mTransitions.containsKey(st_e)){
					List<Transition> ls = mTransitions.get(st_e);
					for (Iterator<Transition> ite = ls.iterator(); ite.hasNext();){
						Transition tr = ite.next();
						
						for (int k = 0; k < mStates.size(); ++k) mStates.get(k).mUsed = false;
						st.mUsed = true;
						DoEpsilonTransitions_eNFA();
						InputChar_eNFA(tr.mInputC);
						
						for (int x = 0; x < mStates.size(); ++x){
							if (mStates.get(x).mUsed && mStates.get(x) != st){
								if (!st.mTransitions.containsKey(tr.mInputC))
									st.mTransitions.put(tr.mInputC, new ArrayList<State>());
								st.mTransitions.get(tr.mInputC).add(mStates.get(x));
							}
						}
					}
				}
			}
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
