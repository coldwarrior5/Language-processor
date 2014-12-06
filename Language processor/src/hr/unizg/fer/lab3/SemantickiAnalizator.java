package hr.unizg.fer.lab3;

public class SemantickiAnalizator {
	
	public static void main(String[] args) {
		
		StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		Parser p = new Parser();
		Izrazi.mSTZ = NaredbenaStrukturaPrograma.mSTZ = DeklaracijeIDefinicije.mSTZ = stz;
		Izrazi.mParser = NaredbenaStrukturaPrograma.mParser = DeklaracijeIDefinicije.mParser = p;
		
		
		while(true) {
			p.ParsirajNovuLiniju();
			int a;
			a = 4;
		}
	}

}
