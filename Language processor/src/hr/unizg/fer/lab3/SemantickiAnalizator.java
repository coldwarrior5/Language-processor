package hr.unizg.fer.lab3;

public class SemantickiAnalizator {

	protected static boolean checkType(String in){
		try{
			Integer.parseInt(in);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {

		
		
	}

}
