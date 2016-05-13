package core;

public class ResultCovoiturage extends Result {
	
	private Chemin chemVoiture ; 
	private Chemin chemPieton ; 
	
	
	public ResultCovoiturage(Chemin chem, long tempsCalcul, double cout, int nb_noeuds, Chemin chemVoiture, Chemin chemPieton) {
		super(chem, tempsCalcul, cout, nb_noeuds) ;
		this.chemPieton = chemPieton ; 
		this.chemVoiture = chemVoiture ; 
	}
	
	
	public Chemin getCheminVoiture() {
		return this.chemVoiture ; 
	}
	
	public Chemin getCheminPieton() {
		return this.chemPieton ; 
	}
}
