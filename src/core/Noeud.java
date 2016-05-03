package core;

import java.util.*;

public class Noeud {

	private float latitude;
	private float longitude;

	private int nb_succ;

	private int id;

	private ArrayList<Route> routes = new ArrayList<Route> ();
	
	public Noeud(int id, float longitude, float latitude) {
		this.id = id;
		this.nb_succ = 0 ;
		this.longitude = longitude;
		this.latitude = latitude ;
				
	}

	public void addSuccesseur(Route route){
		routes.add(route);
		this.nb_succ ++ ;
	}
	
	public int getId() { return this.id ; }
	
	public float getlong() {
		return this.longitude ;
	}
	
	public float getlat() {
		return this.latitude ;
	}
	
	public void setnbsucc(int succ){
		this.nb_succ = succ ;
	}
	
	public String toString() {
		return "Je suis un Noeud n°"+id+" j'ai "+nb_succ+" successeurs et mes coordonnées sont "+longitude+"long et "+latitude+"lat.";
	}
	
	public int getnbsucc() {
		return nb_succ;
	}
	
	public ArrayList<Route> getRoutes() {
		return routes ;
	}
	
	public ArrayList<Noeud> getSucc() {
		
		ArrayList<Noeud> result = new ArrayList<Noeud>() ;
		
		for(Route r : this.routes){
			result.add(r.getSucc());
		}
		
		return result ;
	}
	
	public Route findFastestRoute(Noeud dest){
		double min_time = Float.MAX_VALUE; 
		Route resultat = null ;
		
		for (Route current : routes) {
			double cur_time = current.getDistance() / (float) current.getDescripteur().vitesseMax();
			if(current.getSucc() == dest && cur_time <= min_time){
				min_time = cur_time ;
				resultat = current ;
			}
		}
		
		return resultat;
		
	}
	
	public Route findShortestRoute(Noeud dest){
		double distance = Float.MAX_VALUE; 
		Route resultat = null ;
		
		for (Route current : routes) {
			if(current.getSucc() == dest && current.getDistance() <= distance){
				distance = current.getDistance() ;
				resultat = current ;
			}
		}
		
		return resultat;
		
	}
	
}
