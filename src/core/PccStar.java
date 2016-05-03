package core ;

import java.io.* ;
import base.Readarg ;

public class PccStar extends Pcc {

    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) throws Exception {
	super(gr, sortie, readarg) ;
    }

    public Chemin run() {

	System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;
	
	return null ; 

	// A vous d'implementer la recherche de plus court chemin A*
    }

}
