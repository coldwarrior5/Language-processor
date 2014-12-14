package hr.unizg.fer.lab3;

public class SemantickiAnalizator {
	
	public static void main(String[] args) {
		
		StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		Parser p = new Parser();
		Izrazi.mSTZ = NaredbenaStrukturaPrograma.mSTZ = DeklaracijeIDefinicije.mSTZ = stz;
		Izrazi.mParser = NaredbenaStrukturaPrograma.mParser = DeklaracijeIDefinicije.mParser = p;
		
		p.ParsirajNovuLiniju(); // ucitaj <prijevodna_jedinica>
		NaredbenaStrukturaPrograma.PROVJERI_prijevodna_jedinica();
		// nakon obilaska stabla
		stz.ProvjeraNakonObilaska();
	}
}
