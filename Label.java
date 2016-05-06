package core;

public class Label implements Comparable<Label>{

	private boolean marquage = false ; //rajout du false

	private double cout ;

	private Noeud pere ; 

	private Noeud sommet_courant ;

	private double estimation ;

	public Label(boolean marquage, double cout, Noeud pere, Noeud sommet_courant) {

		this.cout = cout ; 
		this.marquage = marquage ; 
		this.pere = pere ; 
		this.sommet_courant = sommet_courant ; 
		this.estimation = 0 ;
	}

	public Label(boolean marquage, double cout, Noeud pere, Noeud sommet_courant, double estimation) {

		this.cout = cout ; 
		this.marquage = marquage ; 
		this.pere = pere ; 
		this.sommet_courant = sommet_courant ; 
		this.estimation = estimation ;
	}

	public Noeud getSommetCourant() {
		return this.sommet_courant ;
	}

	public double getCout() {
		return this.cout ;
	}
	
	public double getEstimation() {
		return this.estimation ;
	}

	public Noeud getPere() {
		return this.pere ;
	}

	public void setCout(double cout){
		this.cout = cout ;
	}

	public void setPere(Noeud pere){
		this.pere = pere;
	}

	public void setMarquage(boolean B){
		this.marquage = B;
	}

	public boolean getMarquage(){
		return this.marquage ;
	}

	@Override
	public int compareTo(Label o) {
		// TODO Auto-generated method stub
		if(this.cout+this.estimation>o.cout+o.estimation){
			return 1 ;
		}else if (this.cout+this.estimation<o.cout+o.estimation) {
			return -1 ;
		}else {
			return 0 ;
		}

	}

}
