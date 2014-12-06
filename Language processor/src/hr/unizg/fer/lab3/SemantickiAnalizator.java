package hr.unizg.fer.lab3;

public class SemantickiAnalizator {

	protected static int checkType(String in){
		try{
			Integer.parseInt(in);
		}catch(NumberFormatException e){
			if (in.length()==1){
				return 2;
			}
			else{
				return -1;
			}
		}
		return 1;
	}
	
	public static void main(String[] args) {

		int x=checkType("*");
		System.out.println(x);
		
	}

}
