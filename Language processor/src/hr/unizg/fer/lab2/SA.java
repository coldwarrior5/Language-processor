package hr.unizg.fer.lab2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class SA{

	private static SyntaxTreeBuilder mSTB;

	private static void ParseTable() throws IOException{
		List<String> symbols = new ArrayList<String>();
		List<Integer> synchronizationSymbols = new ArrayList<Integer>();
		
	    // Symbols
	    BufferedReader br = new BufferedReader(new FileReader("src/hr/unizg/fer/lab2/symbols.txt"));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	symbols.add(line);
	    }
	    br.close();
	    
	    // Synchronization Symbols
	    br = new BufferedReader(new FileReader("src/hr/unizg/fer/lab2/synchronizationSymbols.txt"));
	    while ((line = br.readLine()) != null) {
	    	synchronizationSymbols.add(Integer.parseInt(line));
	    }
	    br.close();
		
		mSTB = new SyntaxTreeBuilder(symbols, synchronizationSymbols);
		
		// Now populate action table.
		br = new BufferedReader(new FileReader("src/hr/unizg/fer/lab2/actionTable.txt"));
	    while ((line = br.readLine()) != null) {
	    	line += " "; // this is needed for the algorithm to know when to stop.
	    	
	    	int i = line.indexOf(" ") + 1;
			int inputIndex = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			i = line.indexOf(" ") + 1;
			int stateIndex = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			i = line.indexOf(" ") + 1;
			int actionTypeInt = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			ActionType actionType;
			switch (actionTypeInt){
				case 0: actionType = ActionType.Move; break;
				case 1: actionType = ActionType.Reduce; break;
				case 2: actionType = ActionType.Accept; break;
				default: actionType = null; break;
			}
			
			i = line.indexOf(" ") + 1;
			int actionSpecificValue_a = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			i = line.indexOf(" ") + 1;
			int actionSpecificValue_b = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			mSTB.AddActionCell(inputIndex, stateIndex, actionType, actionSpecificValue_a, actionSpecificValue_b);
	    }
	    br.close();
		
		// Now populate new_state table.
		br = new BufferedReader(new FileReader("src/hr/unizg/fer/lab2/newStateTable.txt"));
	    while ((line = br.readLine()) != null) {
	    	line += " "; // this is needed for the algorithm to know when to stop.
	    	
	    	int i = line.indexOf(" ") + 1;
			int inputIndex = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			i = line.indexOf(" ") + 1;
			int stateIndex = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			i = line.indexOf(" ") + 1;
			int actionSpecificValue = Integer.parseInt(line.substring(0, i - 1));
			line = line.substring(i);
			
			mSTB.AddNewStateCell(inputIndex, stateIndex, actionSpecificValue);
	    }
	    br.close();
	}

	public static void main(String[] args) throws IOException{
		ParseTable();

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