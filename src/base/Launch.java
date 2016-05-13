package base ;

/*
 * Ce programme propose de lancer divers algorithmes sur les graphes
 * a partir d'un menu texte, ou a partir de la ligne de commande (ou des deux).
 *
 * A chaque question posee par le programme (par exemple, le nom de la carte), 
 * la reponse est d'abord cherchee sur la ligne de commande.
 *
 * Pour executer en ligne de commande, ecrire les donnees dans l'ordre. Par exemple
 *   "java base.Launch insa 1 1 /tmp/sortie 0"
 * ce qui signifie : charge la carte "insa", calcule les composantes connexes avec une sortie graphique,
 * ecrit le resultat dans le fichier '/tmp/sortie', puis quitte le programme.
 */

import core.* ;

import java.io.* ;

public class Launch {

	private final Readarg readarg ;

	public Launch(String[] args) {
		this.readarg = new Readarg(args) ;
	}

	public void afficherMenu () {
		System.out.println () ;
		System.out.println ("MENU") ;
		System.out.println () ;
		System.out.println ("0 - Quitter") ;
		System.out.println ("1 - Composantes Connexes") ;
		System.out.println ("2 - Plus court chemin standard") ;
		System.out.println ("3 - Plus court chemin A-star") ;
		System.out.println ("4 - Cliquer sur la carte pour obtenir un numero de sommet.") ;
		System.out.println ("5 - Charger un fichier de chemin (.path) et le verifier.") ;
		System.out.println ("6 - Générer un fichier de chemins de test aléatoire.") ;


		System.out.println () ;
	}

	public static void main(String[] args) {
		Launch launch = new Launch(args) ;
		launch.go () ;

	}

	public void go() {

		File f = new File("resultat.txt") ; 
		try {
			FileWriter fw = new FileWriter (f); 
			System.out.println ("**") ;
			System.out.println ("** Programme de test des algorithmes de graphe.");
			System.out.println ("**") ;
			System.out.println () ;

			// On obtient ici le nom de la carte a utiliser.
			String nomcarte = this.readarg.lireString ("Nom du fichier .map a utiliser ? ") ;
			DataInputStream mapdata = Openfile.open (nomcarte) ;

			boolean display = (1 == this.readarg.lireInt ("Voulez-vous une sortie graphique (0 = non, 1 = oui) ? ")) ;	    
			Dessin dessin = (display) ? new DessinVisible(800,600) : new DessinInvisible() ;

			Graphe graphe = new Graphe(nomcarte, mapdata, dessin) ;

			// Boucle principale : le menu est accessible 
			// jusqu'a ce que l'on quitte.
			boolean continuer = true ;
			int choix ;

			while (continuer) {
				this.afficherMenu () ;
				choix = this.readarg.lireInt ("Votre choix ? ") ;


				// Algorithme a executer
				Algo algo = null ;

				// Le choix correspond au numero du menu.
				switch (choix) {
				case 0 : continuer = false ; break ;

				case 1 : algo = new Connexite(graphe, this.fichierSortie (), this.readarg) ; break ;

				case 2 : algo = new Pcc(graphe, this.fichierSortie (), this.readarg) ; break ;

				case 3 : algo = new PccStar(graphe, this.fichierSortie (), this.readarg) ; break ;

				case 4 : graphe.situerClick() ; break ;

				case 5 :
					String nom_chemin = this.readarg.lireString ("Nom du fichier .path contenant le chemin ? ") ;
					graphe.verifierChemin(Openfile.open (nom_chemin), nom_chemin) ;
					break ;
				case 6 : 					
					int  algotest = this.readarg.lireInt("Choix de l'algo ? \n 2 : Djikstra \n 3 : A*\n") ;
					int cheminTest = this.readarg.lireInt ("Choix de la méthode? \n 1 : Plus court \n 2 : Plus rapide\n") ;
					int nb_test = this.readarg.lireInt ("Combien de tests ?\n") ;
					graphe.FileTestCreationRandom(nomcarte, display, algotest, cheminTest, nb_test) ;
					break;
				default:
					System.out.println ("Choix de menu incorrect : " + choix) ;
					System.exit(1) ;
				}
				Result resultat = null ;
				if (algo != null) { 
					try{
						resultat=algo.run() ;
						dessin.setColor(java.awt.Color.magenta);
						dessin.setWidth(4) ;
						fw.write("Duree Recherche : " + String.format("%10d", algo.getDuree() ) + " | Nb de noeuds maximum : " + String.format("%10d", resultat.getNbNoeuds() ) + " | Coût calculé (temps ou distance minimum) : " +  resultat.getCout()) ; fw.write("\r\n") ; 
					if(resultat.getChemin() == null) {
						System.out.println("Le chemin est nul");
					} 
					
					resultat.getChemin().afficherChemin(dessin);
					} catch(Exception e){
						fw.write("Impossible de calculer le chemin") ; fw.write("\r\n") ; 
						System.out.println("Impossible de calculer le chemin : "+ e.getMessage());
					}
				}
			}

			System.out.println ("Programme terminé.") ;
			fw.close() ; 
			System.exit(0) ;


		} catch (Throwable t) {
			t.printStackTrace() ;
			System.exit(1) ;
		}
	}

	// Ouvre un fichier de sortie pour ecrire les reponses
	public PrintStream fichierSortie () {
		PrintStream result = System.out ;

		String nom = this.readarg.lireString ("Nom du fichier de sortie ? ") ;

		if ("".equals(nom)) { nom = "/dev/null" ; }

		try { result = new PrintStream(nom) ; }
		catch (Exception e) {
			System.err.println ("Erreur a l'ouverture du fichier " + nom) ;
			System.exit(1) ;
		}

		return result ;
	}

}
