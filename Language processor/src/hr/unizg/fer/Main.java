package hr.unizg.fer;

public class Main {
	/**
	 * @author Bojan
	 * @param args
	 * 
	 */
	public static void main(String[] args) {	
		
		//eNFA enfa = new eNFA("(A|B|C|D)(a|b|c|d|_)*(.|?|!)");
		//Scanner reader = new Scanner(System.in);
		//while (true){
		//	String output=reader.next();
		//	enfa.Reset();
		//	enfa.InputString(output);
		//	System.out.println(enfa.IsInAcceptableState());
		//}
		//reader.close();
		
		//Parser parser = new Parser("D:\\FER\\PPJ\\Lab1\\01_nadji_x\\test.lan");
		LAG lag = new LAG();
		lag.AddVariable("int", "mCurrentLine");
		lag.AddVariable("String", "mCurrentState");
		lag.AddFunction("f1");
		lag.AddFunction("f2");
		lag.AddFunction("f3");
		lag.Write("src/hr/unizg/fer/");
	}
}
