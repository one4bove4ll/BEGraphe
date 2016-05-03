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

	protected HashMap<Noeud, Label> hmNoeudToLabel = new HashMap<Noeud,Label>() ; 

	protected BinaryHeap<Label> labels = new BinaryHeap<Label>(); 

	protected Noeud n_origine ;
	protected Noeud n_destination ;

	protected int choix ; 

	protected static int nb_noeuds_max = 0 ; 
	protected static int nb_noeuds = 0 ; 


	public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) throws Exception {
		super(gr, sortie, readarg);

		this.zoneOrigine = gr.getZone();
		this.origine = readarg.lireInt("Numero du sommet d'origine ? ");

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone();
		this.destination = readarg.lireInt("Numero du sommet destination ? ");

		if(this.origine>gr.getNoeuds().length || this.destination>gr.getNoeuds().length || this.origine<0 || this.destination<0){
			Exception e = new Exception("Un des noeuds n'est pas dans la carte.");
			throw e ;
		}

		System.out.println("Choississez le type de recherche : \n 1 - Plus rapide \n 2 - Plus court") ; 
		this.choix = readarg.lireInt("Votre choix ?");

		Noeud[] noeuds = gr.getNoeuds() ;
		n_origine = noeuds[origine] ; 
		n_destination = noeuds[destination] ; 
		this.hmNoeudToLabel.put(n_origine, new Label(false, 0,null, n_origine)) ;
		this.labels.insert(hmNoeudToLabel.get(n_origine)) ; 

	}


	public Chemin run() throws Exception{ 
		return myrun(this.choix) ; 
	}

	//le choix entre plus court et plus rapide est intégré dans myrun
	public Chemin myrun(int choix) throws Exception{ //choix = 1 => fastest ;;;;; choix = 2 => shortest

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine
				+ " vers " + zoneDestination + ":" + destination);

		Chemin resultat = new Chemin() ;
		boolean dest_found = false ; 
		Date date = new Date() ;

		do {
			Label l = labels.deleteMin();
			l.setMarquage(true) ; //on met le marquage du label a true s'il a déjà été enlevé du tas, cad qu'on connait sa valeur finale
			nb_noeuds--;
			Noeud n = l.getSommetCourant() ; 	
			for (Route r : n.getRoutes()) {

				double new_cost ; 
				if (choix==1) { //fastest
					new_cost = 36.0 * r.getDistance()/(600 * r.getDescripteur().vitesseMax()) + l.getCout() ; //le cout du noeud successeur en passant par le noeud n
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


		if(!dest_found){
			Exception e = new Exception("Il n'existe pas de chemin.");
			throw e ;
		}

		if (choix==1) {
			System.out.println("Le temps de trajet minimal est de " + hmNoeudToLabel.get(n_destination).getCout() + " minutes.") ;
		}
		else {
			System.out.println("La distance de trajet minimal est de " + hmNoeudToLabel.get(n_destination).getCout() + " mètres.") ;
		}
		System.out.println("Nombre maximal de noeuds dans le tas : " + nb_noeuds_max) ; 
		Date date2 = new Date() ;
		long duree = date2.getTime() - date.getTime() ;
		System.out.println("La recherche a duré " + duree/1000.0+" secondes.");

		
		//if(dest_found){ inutile car on le teste au dessus avec l'exception
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
		//} 

	}	
}
