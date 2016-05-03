package core;

import java.awt.Color;
import java.io.*;
import base.Readarg;
import java.util.*;

public class Pcc extends Algo {
	// Numero des sommets origine et destination
	protected int zoneOrigine;
	protected int origine;

	protected int zoneDestination;
	protected int destination;

	private HashMap<Noeud, Label> hmNoeudToLabel = new HashMap<Noeud,Label>() ; 

	protected BinaryHeap<Label> labels = new BinaryHeap<Label>(); 
	
	private Noeud n_origine ;
	private Noeud n_destination ;
	
	private int choix ; 
	
	private static int nb_noeuds_max = 0 ; 
	private static int nb_noeuds = 0 ; 
	

	public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg);

		this.zoneOrigine = gr.getZone();
		this.origine = readarg.lireInt("Numero du sommet d'origine ? ");

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone();
		this.destination = readarg.lireInt("Numero du sommet destination ? ");
		
		System.out.println("Choississez le type de recherche : \n 1 - Plus rapide \n 2 - Plus court") ; 
		this.choix = readarg.lireInt("Votre choix ?");
		
		Noeud[] noeuds = gr.getNoeuds() ;
		n_origine = noeuds[origine] ; 
		n_destination = noeuds[destination] ; 
		this.hmNoeudToLabel.put(n_origine, new Label(false, 0,null, n_origine)) ;
		this.labels.insert(hmNoeudToLabel.get(n_origine)) ; 
		
		
		//--------------------------A RAJOUTER SI ON VEUT UTILISER LES ANCIENNES VERSIONS------------//
		//----------------------------A ENLEVER SI ON VEUT UTILISER LA VERSION OPTIMISEE-------------//
		
		//initialisation du tas de labels et remplissage de la HashMap 
		
		/*for(Noeud n : noeuds){ 
			if (n.getId() == this.origine) {
				Label l = new Label(false,0,null,n) ; 
				labels.insert(l);
				hmNoeudToLabel.put(n, l) ; 
				this.n_origine = n ;
			}
			else if (n.getId() == this.destination){
				Label l = new Label(false,Integer.MAX_VALUE,null,n) ; 
				labels.insert(l);
				hmNoeudToLabel.put(n, l) ; 
				this.n_destination = n ;
			}
			else { 
				Label l = new Label(false,Integer.MAX_VALUE,null,n) ; 
				labels.insert(l);
				hmNoeudToLabel.put(n, l) ; 
			}
		}*/

	}
	
	
	public Chemin run() { 
		return myrun(this.choix) ; 
	}
	
	//le choix entre plus court et plus rapide est intégré dans myrun
	public Chemin myrun(int choix) { //choix = 1 => fastest ;;;;; choix = 2 => shortest

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine
				+ " vers " + zoneDestination + ":" + destination);

		Chemin resultat = new Chemin() ;
		boolean dest_found = false ; 
		
			
		do {
			Label l = labels.deleteMin();
			l.setMarquage(true) ; //on met le marquage du label a true s'il a déjà été enlevé du tas, cad qu'on connait sa valeur finale
			nb_noeuds--;
			Noeud n = l.getSommetCourant() ; 	
			for (Route r : n.getRoutes()) {
				
				float new_cost ; 
				if (choix==1) { //fastest
					new_cost = 36 * r.getDistance()/(600 * r.getDescripteur().vitesseMax()) + l.getCout() ; //le cout du noeud successeur en passant par le noeud n
					//en minutes 
				}
				else { //shortest
					new_cost = r.getDistance() + l.getCout() ;
				}
				
				if (!(hmNoeudToLabel.containsKey(r.getSucc()))) { //s'il n'est pas dans la hashmap, il faut créer le label et l'ajouter a la hashmap et dans le tas 
					Label l_succ = new Label(false, new_cost, n, r.getSucc()) ;  
					hmNoeudToLabel.put(r.getSucc(), l_succ) ; 
					labels.insert(l_succ) ; 
					nb_noeuds++ ; 
					this.graphe.getDessin().setColor(Color.cyan) ; 
					this.graphe.getDessin().drawPoint(l.getSommetCourant().getlong(), l.getSommetCourant().getlat(), 2) ;
					
					if (nb_noeuds > nb_noeuds_max) {
						nb_noeuds_max = nb_noeuds ; 
					}
				}
				else { // le noeud est dans la hashmap 
					Label l_succ = hmNoeudToLabel.get(r.getSucc()) ;  
					
					if ((!l_succ.getMarquage()) && (new_cost < l_succ.getCout()) ) { //marquage = faux => le noeud est dans le tas
							l_succ.setCout(new_cost);
							l_succ.setPere(n);
							labels.update(l_succ) ; 
					}
				}
				
				if (n == n_destination) {
					dest_found = true ; 
				}
			}
		} while((!labels.isEmpty()) && !dest_found ) ; 
		
		if (choix==1) {
			System.out.println("Le temps de trajet minimal est de " + hmNoeudToLabel.get(n_destination).getCout() + " minutes.") ;
		}
		else {
			System.out.println("La distance de trajet minimal est de " + hmNoeudToLabel.get(n_destination).getCout() + " mètres.") ;
		}
		System.out.println("Nombre maximal de noeuds dans le tas : " + nb_noeuds_max) ; 
		
		//rajouter un THROW si jamais on est sorti du while parce que le tas était vide mais qu'on avait pas trouvé la destination
		//ce THROW est peut-etre identique à celui qui est dans le else du if(dest_found)...
		if(dest_found){
			Noeud n = n_destination ; 
			resultat.ajouterAuDebut(n);
			boolean go_on = true ;
			 do { 
				if (hmNoeudToLabel.get(n).getPere() != null) {
					resultat.ajouterAuDebut(hmNoeudToLabel.get(n).getPere());
					n = hmNoeudToLabel.get(n).getPere() ; 					
				}
				else { //THROW exception et enlever cette variable go_on
					go_on = false ;
				}
			} while(go_on) ; 
			return resultat ;
		} 
		else { // THROW une exception
			return null ;
		}
	}
	
	//------------------------------CI-APRES : LES ANCIENNES VERSIONS---------------------------------//
	
	public Chemin fastestRunv1() { // plus court en temps

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine
				+ " vers " + zoneDestination + ":" + destination);
		
		Chemin resultat = new Chemin() ;
		boolean dansBoucle = false ;
		Noeud n = null ;
		boolean dest_found = false ; 
			
		do {
			dansBoucle = true ;
			Label l = labels.deleteMin();
			this.graphe.getDessin().setColor(Color.cyan) ; 
			this.graphe.getDessin().drawPoint(l.getSommetCourant().getlong(), l.getSommetCourant().getlat(), 2) ; 
			n = l.getSommetCourant() ;
			for(Route r : n.getRoutes()){
				//ici il faut trouver le label correspondant au Noeud successeur
				Label l_succ = hmNoeudToLabel.get(r.getSucc());
				if( ( r.getDistance()/r.getDescripteur().vitesseMax() + l.getCout() ) < l_succ.getCout()){
					l_succ.setCout(r.getDistance()/r.getDescripteur().vitesseMax() + l.getCout());
					l_succ.setPere(n);
					labels.update(l_succ) ; 
				}
				if (l.getSommetCourant() == n_destination) {
					dest_found = true ; 					
				}
			}

		} while(!labels.isEmpty() && !dest_found) ; 
		
		
		if(dansBoucle==true){
			Noeud n_curr = n_destination ; 
			resultat.ajouterAuDebut(n_curr);
			boolean go_on = true ;
			 do {
				if (hmNoeudToLabel.get(n_curr).getPere() != null) {
					resultat.ajouterAuDebut(hmNoeudToLabel.get(n_curr).getPere());
					n_curr = hmNoeudToLabel.get(n_curr).getPere() ; 
				}
				else {
					go_on = false ;
				}
			} while(go_on) ; 
			 System.out.println("Temps de parcours = " + resultat.tempsDeParcours()) ; 
			return resultat ;
		} else {
			return null ;
		}
	}
	
	public Chemin shortestRunv1() {

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine
				+ " vers " + zoneDestination + ":" + destination);

		// A vous d'implementer la recherche de plus court chemin.
		/*
		 *TANT QUE le tas n'est pas vide
		 *		on enleve le premier 
		 *		pour tous ses successeurs, on modifie les couts si besoin (et le noeud père) et on update a partir du label modifié
		 *fin TANT QUE
		 *Reconstituer le chemin à partir du noeud d'arrivée
		 */

		Chemin resultat = new Chemin() ;
		boolean dansBoucle = false ;
		Noeud n = null ;
		boolean dest_found = false ; 
			
		do {
			dansBoucle = true ;
			Label l = labels.deleteMin();
			this.graphe.getDessin().setColor(Color.cyan) ; 
			this.graphe.getDessin().drawPoint(l.getSommetCourant().getlong(), l.getSommetCourant().getlat(), 2) ; 
			n = l.getSommetCourant() ;
			for(Route r : n.getRoutes()){
				//ici il faut trouver le label correspondant au Noeud successeur
				Label l_succ = hmNoeudToLabel.get(r.getSucc());
				if( ( r.getDistance()+l.getCout() ) < l_succ.getCout()){
					l_succ.setCout(r.getDistance()+l.getCout());
					l_succ.setPere(n);
					labels.update(l_succ) ; 
				}
				if (l.getSommetCourant() == n_destination) {
					dest_found = true ; 					
				}
			}

		} while((!labels.isEmpty()) && !dest_found ) ; 
		
		System.out.println("La distance minimale est : " + hmNoeudToLabel.get(n_destination).getCout()) ; 
		
		if(dansBoucle==true){
			Noeud n_curr = n_destination ; 
			resultat.ajouterAuDebut(n_curr);
			boolean go_on = true ;
			 do { 
				if (hmNoeudToLabel.get(n_curr).getPere() != null) {
					resultat.ajouterAuDebut(hmNoeudToLabel.get(n_curr).getPere());
					n_curr = hmNoeudToLabel.get(n_curr).getPere() ; 
				}
				else {
					go_on = false ;
				}
			} while(go_on) ; 
			return resultat ;
		} else {
			return null ;
		}
	}
	
}
