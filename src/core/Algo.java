package core ;

import java.io.* ;
//import java.util.* ; 
import base.* ;

/**
 * Classe abstraite representant un algorithme (connexite, plus court chemin, etc.)
 */
public abstract class Algo {

	protected PrintStream sortie ;
	protected Graphe graphe ;
	protected long duree ; 


	protected Algo(Graphe gr, PrintStream fichierSortie, Readarg readarg) {
		this.graphe = gr ;
		this.sortie = fichierSortie ;	
	}

	public abstract Result run() throws Exception ;

	public long getDuree() {
		return this.duree ;
	}

}
