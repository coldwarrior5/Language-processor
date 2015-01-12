package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class LexicalRule{
	int mLexicalState;
	Boolean mDiscardString;
	String mLexicalTokenName;
	Boolean mNewLine;
	Boolean mGoToState;
	int mGoToStateId;
	Boolean mReturn;
	int mReturnAt;
}

class Item{
	int mLeftNonTerminalSymbolIndex;
	int mItemDotPosition; // must be in interval [0, mRightSymbols.size()]
	List<Integer> mRightSymbolsIndices = new ArrayList<Integer>();
	// tells what terminal characters can appear on the right side
	List<Integer> mBEGINS_Set = new ArrayList<Integer>();
	// Index in Parser_Sem.mProductions of this item
	int mProductionIndex;
}

// for EpsilonNFA and NFA
class StateNFA{
	Boolean mIsInitial;
	Item mItem;
	
	// Helper variables for transitions.
	Boolean mUsed;
	Boolean mUsedInNextStep;
}

class StateDFA{
	List<Item> mItems = new ArrayList<Item>();
}

enum ActionType {Move, Reduce, Accept};

// parsing action table cell
class PTCell_Action{
	int mInputIndex;
	int mStateIndex;
	ActionType mActionType;
	int mActionSpecificValue_a;
	int mActionSpecificValue_b;
}

//parsing new state table cell
class PTCell_NewState{
	int mInputIndex;
	int mStateIndex;
	// there is just one action possible -> Put()
	int mActionSpecificValue;
}

class Utilities {
	
	public static int ProductionEpsilonCode = -1;
	public static int ProductionErrorCode = -2;
	//public static int ItemDotCode = -3;
	public static int EOFTerminalSymbolCode = -4;
	
	public static String ReadStringFromInput(){
		String input = "";
		Scanner scIn = new Scanner(System.in);
		while(true){
			
			//if(scIn.hasNext()==false){	//This works in terminal
			//	break;						//It expects the stdin to close
			//}
			
			String read = scIn.nextLine();	//Everything else looks normal
			
			if(read.equals("")){			//This works in Eclipse
				break;						//It expects for user to type another enter
			}

 
			
			input += read + "\n";
			
		}
		scIn.close();
		return input;
	}
}
