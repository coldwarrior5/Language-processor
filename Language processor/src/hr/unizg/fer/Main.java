package hr.unizg.fer;

import java.util.Scanner;

public class Main {
	/**
	 * @author Bojan
	 * @param args
	 * 
	 */
	public static void main(String[] args) {	
		
		eNFA enfa = new eNFA("array[(0|1|2|3|4|5|6|7|8|9)*]");
		Scanner reader = new Scanner(System.in);
		
		while (true){
			String output=reader.next();
			enfa.Reset();
			enfa.InputString(output);
			System.out.println(enfa.IsInAcceptableState());
		}
		
		//reader.close();
	}
}
