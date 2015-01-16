package hr.unizg.fer.labComplete;

public class CompilerPPJ {
	
	public static void main(String[] args) {
		LA la = new LA();
		String laOutput = la.GetOutput();
		SA sa = new SA();
		String saOutput = sa.GetOutput(laOutput);
		SemantickiAnalizator s = new SemantickiAnalizator();
		s.Check(saOutput);
		GeneratorKoda gk = new GeneratorKoda();
		gk.GenerirajKod(saOutput);
		
	}

}
