package hr.unizg.fer.lab2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GSA {

	public static int GetNum(ActionType at){
		switch (at){
			case Move: return 0;
			case Reduce: return 1;
			case Accept: return 2;
			default: return -1;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		Parser parser = new Parser();
		BEGINS_sets b_sets = new BEGINS_sets(parser.GetSymbols(), parser.GetProductions());
		EpsilonNFA eNFA = new EpsilonNFA(parser, b_sets);
		NFA nfa = new NFA(parser, eNFA);
		DFA dfa = new DFA(parser, nfa);
		ParsingTable pt = new ParsingTable(parser, dfa);
		
		PrintWriter outSymbols = new PrintWriter("src/hr/unizg/fer/lab2/symbols.txt");
		PrintWriter outSynchronizationSymbols = new PrintWriter("src/hr/unizg/fer/lab2/synchronizationSymbols.txt");
		PrintWriter outActionTable = new PrintWriter("src/hr/unizg/fer/lab2/actionTable.txt");
		PrintWriter outNewStateTable = new PrintWriter("src/hr/unizg/fer/lab2/newStateTable.txt");
		
		for (int i = 0; i < parser.GetSymbols().size(); ++i){
			outSymbols.println(parser.GetSymbols().get(i).mSy);
		}
		for (int i = 0; i < parser.GetSyncronizationSymbols().size(); ++i) {
			outSynchronizationSymbols.println(parser.GetSyncronizationSymbols().get(i));
		}
		for (int i = 0; i < pt.GetCellsAction().size(); ++i){
			outActionTable.println(pt.GetCellsAction().get(i).mInputIndex + " " +
					pt.GetCellsAction().get(i).mStateIndex + " " +
					GetNum(pt.GetCellsAction().get(i).mActionType) + " " +
					pt.GetCellsAction().get(i).mActionSpecificValue_a + " " +
					pt.GetCellsAction().get(i).mActionSpecificValue_b + " " +
					pt.GetCellsAction_Priority().get(i));
		}
		for (int i = 0; i < pt.GetCellsNewState().size(); ++i){
			outNewStateTable.println(pt.GetCellsNewState().get(i).mInputIndex + " " +
					pt.GetCellsNewState().get(i).mStateIndex + " " +
					pt.GetCellsNewState().get(i).mActionSpecificValue);
		}
		
		outSymbols.close();
		outSynchronizationSymbols.close();
		outActionTable.close();
		outNewStateTable.close();
	}

}
