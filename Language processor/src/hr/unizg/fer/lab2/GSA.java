package hr.unizg.fer.lab2;

public class GSA {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Parser parser = new Parser();
		BEGINS_sets b_sets = new BEGINS_sets(parser.GetSymbols(), parser.GetProductions());
		EpsilonNFA eNFA = new EpsilonNFA(parser, b_sets);
		NFA nfa = new NFA(parser, eNFA);
		DFA dfa = new DFA(parser, nfa);
		ParsingTable pt = new ParsingTable(parser, dfa);
		
		SourceCodeBuilder scb = new SourceCodeBuilder("SA");
		scb.AddImport("java.util.List");
		scb.AddImport("java.util.ArrayList");
		scb.AddImport("java.util.Scanner");
		
		scb.AddVariable("SyntaxTreeBuilder mSTB;");
		
		// Initialize() function
		String fBody = "List<String> symbols = new ArrayList<String>();\n" + 
		"List<Integer> syncronizationSymbols = new ArrayList<Integer>();\n";
		for (int i = 0; i < parser.GetSymbols().size(); ++i)
			fBody += "symbols.add(\"" + parser.GetSymbols().get(i).mSy + "\"); //\t\tindex: " + i + "\n";
		for (int i = 0; i < parser.GetSyncronizationSymbols().size(); ++i)
			fBody += "syncronizationSymbols.add(" + parser.GetSyncronizationSymbols().get(i) + ");\n";
		fBody += "mSTB = new SyntaxTreeBuilder(symbols, syncronizationSymbols);\n\n// Now populate action table.\n";
		for (int i = 0; i < pt.GetCellsAction().size(); ++i){
			fBody += "mSTB.AddActionCell(" + pt.GetCellsAction().get(i).mInputIndex + ", " +
					pt.GetCellsAction().get(i).mStateIndex + ", " +
					"ActionType." + pt.GetCellsAction().get(i).mActionType + ", " +
					pt.GetCellsAction().get(i).mActionSpecificValue_a + ", " +
					pt.GetCellsAction().get(i).mActionSpecificValue_b + ");\n";
		}
		fBody += "\n// Now populate new_state table.\n";
		for (int i = 0; i < pt.GetCellsNewState().size(); ++i){
			fBody += "mSTB.AddNewStateCell(" + pt.GetCellsNewState().get(i).mInputIndex + ", " +
					pt.GetCellsNewState().get(i).mStateIndex + ", " +
					pt.GetCellsNewState().get(i).mActionSpecificValue + ");\n";
		}
		scb.AddFunction("void", "Initialize", "", fBody);
		
		// main
		scb.AddInMain("Initialize();");
		scb.AddEmptyLineInMain();
		scb.AddInMain("String input = Utilities.ReadStringFromInput();");
		scb.AddInMain("Scanner scanner = new Scanner(input);");
		scb.AddInMain("while (scanner.hasNextLine()) {");
		scb.AddInMain("  String line = scanner.nextLine();");
		scb.AddInMain("  // input the line until the parser reads it");
		scb.AddInMain("  while(!mSTB.InputLine(line));");
		scb.AddInMain("}");
		scb.AddInMain("scanner.close();");
		scb.AddEmptyLineInMain();
		scb.AddInMain("// Input EOF.");
		scb.AddInMain("while(!mSTB.InputLine(null));");
		scb.AddEmptyLineInMain();
		scb.AddInMain("// Write");
		scb.AddInMain("String out = mSTB.WriteoutTree();");
		scb.AddInMain("System.out.println(out);");
		
		scb.Write("src/hr/unizg/fer/lab2/");
		
	}

}
