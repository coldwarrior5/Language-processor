package hr.unizg.fer.lab3;

public class SemantickiAnalizator {
	
	public static void main(String[] args) {
		
		Boolean jo = Utilities.ProvjeriNizConstChar("hau\\\"odfh\\nsf\\");
		jo = Utilities.ProvjeriNizConstChar("\"\\a\\b\\c\"");
		
		StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		Parser p = new Parser();
		Izrazi.mSTZ = NaredbenaStrukturaPrograma.mSTZ = DeklaracijeIDefinicije.mSTZ = stz;
		Izrazi.mParser = NaredbenaStrukturaPrograma.mParser = DeklaracijeIDefinicije.mParser = p;
		
		String linija = p.ParsirajNovuLiniju();
		NaredbenaStrukturaPrograma.PROVJERI_prijevodna_jedinica();
		// nakon obilaska stabla
		stz.ProvjeraNakonObilaska();
	}
}
