package core ;

import java.awt.Color;
import java.io.* ;
import java.util.Date;

import base.Readarg ;

public class PccStar extends Pcc {

	public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) throws Exception {
		super(gr, sortie, readarg) ;
		this.hmNoeudToLabel.put(n_origine, new Label(false, 0,null, n_origine,(float)Graphe.distance(n_origine.getlong(), n_origine.getlat(), n_destination.getlong(), n_destination.getlat()))) ;
	}

	public PccStar(Graphe gr, PrintStream sortie, Readarg readarg, int origine, int destination) throws Exception {
		super(gr,sortie,readarg,origine,destination);
		this.hmNoeudToLabel.put(n_origine, new Label(false, 0,null, n_origine,(float)Graphe.distance(n_origine.getlong(), n_origine.getlat(), n_destination.getlong(), n_destination.getlat()))) ;

	}
	
	public void setOrigine(int ori) {
		this.origine = ori ;
	}

	public Result run() throws Exception {

		System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

		return myrun(this.choix) ; 

	}



	public Result myrun(int choix) throws Exception{ //choix = 1 => fastest ;;;;; choix = 2 => shortest

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
				double estimation ; 
				if (choix==1) { //fastest
					new_cost = 36 * r.getDistance()/(600 * r.getDescripteur().vitesseMax()) + l.getCout() ; //le cout du noeud successeur en passant par le noeud n
					//en minutes  
					estimation = 36 * Graphe.distance(r.getSucc().getlong(), r.getSucc().getlat(), n_destination.getlong(), n_destination.getlat())/(600*130); 
				}
				else { //shortest
					new_cost = r.getDistance() + l.getCout() ;
					estimation = Graphe.distance(r.getSucc().getlong(), r.getSucc().getlat(), n_destination.getlong(), n_destination.getlat()) ; 
				}



				if (!(hmNoeudToLabel.containsKey(r.getSucc()))) { //s'il n'est pas dans la hashmap, il faut créer le label et l'ajouter a la hashmap et dans le tas 
					Label l_succ = new Label(false, new_cost, n, r.getSucc(), estimation) ;  
					hmNoeudToLabel.put(r.getSucc(), l_succ) ; 
					labels.insert(l_succ) ; 
					nb_noeuds++ ; 
					this.graphe.getDessin().setColor(Color.green) ; 
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
		this.duree = (date2.getTime() - date.getTime()) ;
		System.out.println("La recherche a duré " + this.duree/1000.0+" secondes.");


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
		return new Result(resultat, this.duree, hmNoeudToLabel.get(n_destination).getCout(), nb_noeuds_max) ;

	}

}
