package core;

public class Result {

	protected Chemin chem ; 

	protected long tempsCalcul ; 

	protected double cout ; 

	protected int nb_noeuds ; 


	public Result(Chemin chem, long tempsCalcul, double cout, int nb_noeuds) {
		this.chem = chem ; 
		this.tempsCalcul = tempsCalcul ; 
		this.cout = cout ; 
		this.nb_noeuds = nb_noeuds ; 
	}

	public Chemin getChemin() {
		return this.chem ;
	}

	public long getTempsCalcul() {
		return this.tempsCalcul ; 
	}

	public double getCout() {
		return this.cout ; 
	}

	public int getNbNoeuds() {
		return this.nb_noeuds ; 
	}

}
