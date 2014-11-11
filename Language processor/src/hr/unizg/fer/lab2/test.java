package hr.unizg.fer.lab2;

import java.util.ArrayList;
import java.util.List;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Parser parser = new Parser();
		BEGINS_sets b_sets = new BEGINS_sets(parser.GetSymbols(), parser.GetProductions());
		EpsilonNFA eNFA = new EpsilonNFA(parser, b_sets);
		
		NFA nfa = new NFA(parser, eNFA);
		
		DFA dfa = new DFA(parser, nfa);
		
		ParsingTable pt = new ParsingTable(parser, dfa);
		
		int a = 4;
		a = 3;
		
	}

}
