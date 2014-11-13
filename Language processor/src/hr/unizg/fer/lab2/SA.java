package hr.unizg.fer.lab2;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class SA{

	private static SyntaxTreeBuilder mSTB;

	private static void Initialize(){
		List<String> symbols = new ArrayList<String>();
		List<Integer> syncronizationSymbols = new ArrayList<Integer>();
		symbols.add("<S'>"); //		index: 0
		symbols.add("<A>"); //		index: 1
		symbols.add("<B>"); //		index: 2
		symbols.add("a"); //		index: 3
		symbols.add("b"); //		index: 4
		syncronizationSymbols.add(4);
		mSTB = new SyntaxTreeBuilder(symbols, syncronizationSymbols);
		
		// Now populate action table.
		mSTB.AddActionCell(3, 0, ActionType.Move, 4, -1);
		mSTB.AddActionCell(3, 2, ActionType.Move, 4, -1);
		mSTB.AddActionCell(3, 4, ActionType.Move, 4, -1);
		mSTB.AddActionCell(3, 5, ActionType.Reduce, 2, 2);
		mSTB.AddActionCell(3, 6, ActionType.Reduce, 1, 2);
		mSTB.AddActionCell(4, 0, ActionType.Move, 6, -1);
		mSTB.AddActionCell(4, 2, ActionType.Move, 6, -1);
		mSTB.AddActionCell(4, 4, ActionType.Move, 6, -1);
		mSTB.AddActionCell(4, 5, ActionType.Reduce, 2, 2);
		mSTB.AddActionCell(4, 6, ActionType.Reduce, 1, 2);
		mSTB.AddActionCell(-4, 0, ActionType.Reduce, 0, 1);
		mSTB.AddActionCell(-4, 1, ActionType.Accept, -1, -1);
		mSTB.AddActionCell(-4, 2, ActionType.Reduce, 0, 1);
		mSTB.AddActionCell(-4, 3, ActionType.Reduce, 2, 1);
		mSTB.AddActionCell(-4, 5, ActionType.Reduce, 2, 2);
		mSTB.AddActionCell(-4, 6, ActionType.Reduce, 1, 2);
		
		// Now populate new_state table.
		mSTB.AddNewStateCell(1, 0, 1);
		mSTB.AddNewStateCell(1, 2, 3);
		mSTB.AddNewStateCell(2, 0, 2);
		mSTB.AddNewStateCell(2, 2, 2);
		mSTB.AddNewStateCell(2, 4, 5);
	}

	public static void main(String[] args){
		Initialize();

		String input = Utilities.ReadStringFromInput();
		Scanner scanner = new Scanner(input);
		while (scanner.hasNextLine()) {
		  String line = scanner.nextLine();
		  // input the line until the parser reads it
		  while(!mSTB.InputLine(line));
		}
		scanner.close();

		// Input EOF.
		while(!mSTB.InputLine(null));

		// Write
		String out = mSTB.WriteoutTree();
		System.out.println(out);

	}
}