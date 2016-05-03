package core;

import java.util.*;

public class Chemin {

	private ArrayList<Noeud> path = new ArrayList<Noeud> ();

	public Chemin(){
	}

	public void addNoeud(Noeud n){
		this.path.add(n);
	}
	
	public void ajouterAuDebut(Noeud n){
		this.path.add(0,n);
	}


	public float tempsDeParcours() { //j'ai mis des float a la place des double, pour avoir des floats dans toutes les classes et pas avoir de problemes de conversion
		float result = 0 ;		
		for(int i = 0 ; i < path.size() - 1 ; i++){
			Route route = path.get(i).findFastestRoute(path.get(i+1));
			result += route.getDistance() / (float) route.getDescripteur().vitesseMax();
		}
		return 36*result/600 ;
	}

	public void afficherChemin (base.Dessin dessin) {
		
		
		if(path.size()!=0){
			
			Noeud n = path.get(0);
			float currLong = n.getlong();
			float currLat = n.getlat() ;
			
			for(int i = 1 ; i< this.path.size() ; i++) {
				n = this.path.get(i);
				dessin.drawLine(currLong, currLat, n.getlong(), n.getlat());
				currLong = n.getlong();
				currLat = n.getlat();
			}
			
		}
	}


}
