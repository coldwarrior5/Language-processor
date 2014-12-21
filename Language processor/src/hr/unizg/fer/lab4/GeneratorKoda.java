package hr.unizg.fer.lab4;

public class GeneratorKoda {

	public static void main(String[] args) {
		
		//StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		//Parser p = new Parser();
		SourceCodeBuilder scb = new SourceCodeBuilder();
		scb.AddInMain("MOVE %D 42, R6");
		scb.Write("src/hr/unizg/fer/lab4/");

	}

}
