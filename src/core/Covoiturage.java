package core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import base.Readarg;

public class Covoiturage extends Algo{


	private int departVoiture ;
	private int departPieton ;
	private int destination ;

	private int rencontre ;

	private Noeud n_departVoiture ;
	private Noeud n_departPieton ;
	private Noeud n_destination ;	

	private Noeud n_rencontre ;

	private Chemin pieton_rencontre ;
	private Chemin voiture_rencontre ;
	private Chemin rencontre_destination ;

	private Pcc pccVoiture ;
	private PccPieton pccPieton ;
	private PccStar covoit ;

	private base.Readarg readarg ;

	public Covoiturage(Graphe gr, PrintStream sortie, Readarg readarg) throws Exception {
		super(gr,sortie,readarg);

		this.departVoiture = readarg.lireInt("Numero du sommet d'origine de la voiture ? ");
		this.departPieton = readarg.lireInt("Numero du sommet d'origine du piéton ? ");
		this.destination = readarg.lireInt("Numero du sommet de destination ? ");
		this.readarg = readarg ; 

		pccVoiture = new Pcc(this.graphe,this.sortie,this.readarg,this.departVoiture,this.destination); //lève une exception
		pccPieton = new PccPieton(this.graphe,this.sortie,this.readarg,this.departPieton,this.destination);		
		covoit = new PccStar(this.graphe,this.sortie,this.readarg,0,this.destination);	
	}


	public Result run() throws Exception{

		
		ArrayList<Label> listeFusion = new ArrayList<Label>();	
		Result rVoiture = pccVoiture.runCovoiturage();
		Result rPieton = pccPieton.run();

		HashMap<Noeud, Label> hmPieton = pccPieton.getHM() ;
		HashMap<Noeud, Label> hmVoiture = pccVoiture.getHM() ;

		for(Noeud n : hmVoiture.keySet()){ 
			if(hmPieton.containsKey(n)&& n.getId()!=departPieton && n.getId()!=departVoiture){			
				listeFusion.add(new Label(true,hmVoiture.get(n).getCout()+hmPieton.get(n).getCout(),null,n));
			}
		}
		


		Label labelMin = new Label(true, Double.MAX_VALUE, null,null) ;		
		System.out.println(listeFusion.size());
		for(Label l : listeFusion){
			if(l.getCout()<labelMin.getCout() && l.getSommetCourant().getId()!=departVoiture){
				labelMin = l ;
				System.out.println("je suis dans le if pour le noeud " + l.getSommetCourant().getId()) ; 
			}
		}
		
		n_rencontre = labelMin.getSommetCourant() ;
		rencontre = n_rencontre.getId() ;
		System.out.println("Le noeud de rencontre a pour identifiant : " + rencontre);
		covoit.setOrigine(rencontre) ;


		Result rCovoit = (new PccStar(this.graphe,this.sortie,this.readarg,rencontre,destination)).run() ;
		
		rPieton = (new PccStar(this.graphe,this.sortie,this.readarg,this.departPieton,rencontre)).run();
		rVoiture= (new PccStar(this.graphe,this.sortie,this.readarg,this.departVoiture,rencontre)).run();

		return new ResultCovoiturage(rCovoit.getChemin(), 0, 0, 0, rVoiture.getChemin(), rPieton.getChemin()) ; 
	}

}
