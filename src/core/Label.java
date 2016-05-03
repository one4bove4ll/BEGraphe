package core;

public class Label implements Comparable<Label>{
	
	private boolean marquage = false ; //rajout du false
	
	private float cout ;
	
	private Noeud pere ; 
	
	private Noeud sommet_courant ;
	
	public Label(boolean marquage, float cout, Noeud pere, Noeud sommet_courant) {
		
		this.cout = cout ; 
		this.marquage = marquage ; 
		this.pere = pere ; 
		this.sommet_courant = sommet_courant ; 
	}

	public Noeud getSommetCourant() {
		return this.sommet_courant ;
	}
	
	public float getCout() {
		return this.cout ;
	}
	
	public Noeud getPere() {
		return this.pere ;
	}
	
	public void setCout(float cout){
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
		if(this.cout>o.cout){
			return 1 ;
		}else if (this.cout<o.cout) {
			return -1 ;
		}else {
			return 0 ;
		}

	}

}
