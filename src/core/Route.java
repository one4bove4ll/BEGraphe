package core;

public class Route {

	// attributs
	///private int num_zone;
	private float distance;
	private base.Descripteur descripteur;
	private Noeud successeur;
	
	public Route (float distance, base.Descripteur descripteur , Noeud succ){
		
		//num_zone = 0 ;
		this.distance = distance ; //quelle unité ? 
		this.descripteur = descripteur ;
		this.successeur = succ ;
		
	}
	
	public float getDistance() {
		return distance ;
	}
	
	public Noeud getSucc() {
		return successeur ;
	}
	
	public base.Descripteur getDescripteur () {
		return descripteur ;
	}
	

}
