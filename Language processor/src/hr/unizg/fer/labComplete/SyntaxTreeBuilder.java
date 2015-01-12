package hr.unizg.fer.labComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class TableKey {
    private final int mInputIndex;
    private final int mStateIndex;

    public TableKey(int inputIndex, int stateIndex) {
        this.mInputIndex = inputIndex;
        this.mStateIndex = stateIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableKey)) return false;
        TableKey key = (TableKey) o;
        return mInputIndex == key.mInputIndex && mStateIndex == key.mStateIndex;
    }

    @Override
    public int hashCode() {
        int result = mInputIndex;
        result = 31 * result + mStateIndex;
        return result;
    }
}

class SyntaxTreeNode{
	String mName;
	List<SyntaxTreeNode> mChildren = new ArrayList<SyntaxTreeNode>();
}

class Cell_Action{
	ActionType mActionType;
	int mActionSpecificValue_a;
	int mActionSpecificValue_b;
	int mProductionPriroty;
}

class Cell_NewState{
	int mActionSpecificValue;
}

public class SyntaxTreeBuilder {
	
	private List<String> mSymbols;
	private Map<String, Integer> mSymbolsHashMap = new HashMap<String, Integer>();
	private List<Integer> mSyncronizationSymbols;
	private Map<TableKey, Cell_Action> mCellsA = new HashMap<TableKey, Cell_Action>();
	private Map<TableKey, Cell_NewState> mCellsNS = new HashMap<TableKey, Cell_NewState>();
	private Stack<Integer> mStack = new Stack<Integer>();
	private Stack<SyntaxTreeNode> mSTN_Stack = new Stack<SyntaxTreeNode>(); // syntax tree node stack
	private Boolean mErrorRecoveryMode;
	private int mCurrentLineOfCode;
	private final int mEOFTerminalSymbolCode = -4;
	
	public SyntaxTreeBuilder(List<String> symbols, List<Integer> syncronizationSymbols){
		
		mSymbols = symbols;
		for (int i = 0; i < symbols.size(); ++i) mSymbolsHashMap.put(symbols.get(i), i);
		mSyncronizationSymbols = syncronizationSymbols;
		
		Reset();
	}
	
	public void AddActionCell(int inputIndex, int stateIndex, ActionType actionType, int actionSpecificValue_a, int actionSpecificValue_b, int productionPriroty){
		TableKey tempTK = new TableKey(inputIndex, stateIndex);
		Cell_Action newCA = new Cell_Action();
		newCA.mActionType = actionType;
		newCA.mActionSpecificValue_a = actionSpecificValue_a;
		newCA.mActionSpecificValue_b = actionSpecificValue_b;
		newCA.mProductionPriroty = productionPriroty;
		
		Cell_Action oldCA = mCellsA.get(tempTK);
		if (oldCA == null) {
			mCellsA.put(tempTK, newCA);
			return;
		}
		// Move has advantage over reduce.
		if (newCA.mActionType == ActionType.Move && oldCA.mActionType == ActionType.Reduce){
			mCellsA.put(tempTK, newCA);
			return;
		}
		// If there is contradiction between two reduce actions, use the one with greater priority (smaller number).
		if (newCA.mActionType == ActionType.Reduce && oldCA.mActionType == ActionType.Reduce &&
				newCA.mProductionPriroty < oldCA.mProductionPriroty){
			mCellsA.put(tempTK, newCA);
			return;
		}
	}
	
	public void AddNewStateCell(int inputIndex, int stateIndex, int actionSpecificValue){
		TableKey tempTK = new TableKey(inputIndex, stateIndex);
		Cell_NewState tempCNS = new Cell_NewState();
		tempCNS.mActionSpecificValue = actionSpecificValue;
		mCellsNS.put(tempTK, tempCNS);
	}
	
	// Returns true if reading head should be moved to next input symbol.
	public Boolean InputLine(String line){
		int symbolIndex;
		if (line != null) {
			String symbol = line.substring(0, line.indexOf(' '));
			String temp = line.substring(line.indexOf(' ') + 1);
			mCurrentLineOfCode = Integer.parseInt(temp.substring(0, temp.indexOf(' ')));
			symbolIndex = mSymbolsHashMap.get(symbol);
		}
		else symbolIndex = mEOFTerminalSymbolCode;
		
		if (mErrorRecoveryMode){
			// discard everything up until we get a synchronization symbol.
			if (!mSyncronizationSymbols.contains(symbolIndex)) return true;
			else{
				Cell_Action tempCA = mCellsA.get(new TableKey(symbolIndex, mStack.peek()));
				while (tempCA == null) {
					mStack.pop();
					mStack.pop();
					mSTN_Stack.pop();
					tempCA = mCellsA.get(new TableKey(symbolIndex, mStack.peek()));
				}
				mErrorRecoveryMode = false;
			}
		}
		
		Cell_Action tempCA = mCellsA.get(new TableKey(symbolIndex, mStack.peek()));
		if (tempCA == null) {
			mErrorRecoveryMode = true;
			System.err.println("Syntax error in line: " + mCurrentLineOfCode);
			return false;
		}
		
		SyntaxTreeNode newNode;
		switch (tempCA.mActionType){
		case Move:
			mStack.push(symbolIndex);
			mStack.push(tempCA.mActionSpecificValue_a); // index of a new state
			// syntax tree generation
			newNode = new SyntaxTreeNode();
			newNode.mName = line;
			mSTN_Stack.push(newNode);
			return true;
			
		case Reduce:
			// how many to pop (* 2 because state indexes need to be removed too)
			int popC1 = tempCA.mActionSpecificValue_a * 2;
			while (popC1-- > 0) mStack.pop();
			
			int currentStateIndex = mStack.peek();
			int nonTerminalSymbolIndex = tempCA.mActionSpecificValue_b;
			Cell_NewState tempNS = mCellsNS.get(new TableKey(nonTerminalSymbolIndex, currentStateIndex));
			int newStateIndex = tempNS.mActionSpecificValue;
			mStack.push(nonTerminalSymbolIndex); // <-- reduce to this
			mStack.push(newStateIndex);	
			
			// syntax tree generation
			newNode = new SyntaxTreeNode();
			newNode.mName = mSymbols.get(nonTerminalSymbolIndex);
			int popC2 = tempCA.mActionSpecificValue_a;
			if (popC2 == 0) { // was this epsilon production?
				SyntaxTreeNode epsilon = new SyntaxTreeNode();
				epsilon.mName = "$";
				newNode.mChildren.add(epsilon);
			}else{
				while (popC2-- > 0){
					newNode.mChildren.add(0, mSTN_Stack.pop());
				}
			}
			
			mSTN_Stack.push(newNode);
			return false;
			
		case Accept:
			// syntax tree generation
			newNode = new SyntaxTreeNode();
			newNode.mName = mSymbols.get(0); // index = 0 because the first defined symbol is always the initial one.
			int popC = mSTN_Stack.size();
			while (popC-- > 0){ // empty the stack
				newNode.mChildren.add(0, mSTN_Stack.pop());
			}
			mSTN_Stack.push(newNode); // at the end there must be just one node on the stack
			return true;
			
		default:
			return false;
		}
	}
	
	public void Reset(){
		mStack.clear();
		mStack.push(0);
		mSTN_Stack.clear();
		mErrorRecoveryMode = false;
		mCurrentLineOfCode = 0;
	}
	
	public String WriteoutTree(){
		SyntaxTreeNode initialNode = mSTN_Stack.peek();
		StringBuilder output = new StringBuilder();
		// output.append(initialNode.mName + "\n"); we don't need to write first node because it is a helper one we added.
		for (int i = 0; i < initialNode.mChildren.size(); ++i){
			WriteNode(output, initialNode.mChildren.get(i), 0);
		}
		
		return output.toString();
	}
	
	private void WriteNode(StringBuilder output, SyntaxTreeNode node, int depth){
		int tempD = depth;
		while (tempD-- > 0) output.append(" ");
		output.append(node.mName + "\n");
		for (int i = 0; i < node.mChildren.size(); ++i){
			WriteNode(output, node.mChildren.get(i), depth + 1);
		}
	}
}
