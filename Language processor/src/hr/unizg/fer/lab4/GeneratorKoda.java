package hr.unizg.fer.lab4;

public class GeneratorKoda {

	public static void main(String[] args) {
		
		StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		Parser p = new Parser();
		FRISC_ispisivac frisc_i = new FRISC_ispisivac();
		//frisc_i.DodajKod("F_MAIN", "MOVE %D 42, R6", "nesto");
		//frisc_i.DodajGlobalnuVarijablu("G_BROJ", "DW %D 12");
		//frisc_i.DodajKod("RET");
		//frisc_i.DodajGlobalnuVarijablu("G_Y", "DW %D 1243");
		
		Izrazi.mSTZ = NaredbenaStrukturaPrograma.mSTZ = DeklaracijeIDefinicije.mSTZ = stz;
		Izrazi.mParser = NaredbenaStrukturaPrograma.mParser = DeklaracijeIDefinicije.mParser = p;
		Izrazi.mIspisivac = NaredbenaStrukturaPrograma.mIspisivac = DeklaracijeIDefinicije.mIspisivac = frisc_i;
		
		p.ParsirajNovuLiniju(); // ucitaj <prijevodna_jedinica>
		NaredbenaStrukturaPrograma.OBRADI_prijevodna_jedinica();
		// nakon obilaska stabla
		stz.ProvjeraNakonObilaska();
		

		frisc_i.Ispisi("src/hr/unizg/fer/lab4/");
	}

}
