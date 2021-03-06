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
		System.out.println ("7 - Organiser un covoiturage. ") ;


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
					int nb_test = this.readarg.lireInt ("Combien de tests ?\n") ;
					//FileTestCreationRandom(nomcarte, display, nb_test) ;
					FileTestCreationRandom(nomcarte, this.neFaitRien(), this.readarg, graphe, display, nb_test) ; 
					break;
				case 7 :
					if (graphe == null)
						System.out.println("graphe null ") ; 
					algo = new Covoiturage(graphe, this.fichierSortie(), this.readarg);
					break ;
				default:
					System.out.println ("Choix de menu incorrect : " + choix) ;
					System.exit(1) ;
				}
				Result resultat = null ;
				if (algo != null) { 
					try{
						resultat=algo.run() ;
						dessin.setColor(java.awt.Color.magenta);
						dessin.setWidth(2) ;
						fw.write("Duree Recherche : " + String.format("%10d", algo.getDuree() ) + " | Nb de noeuds maximum : " + String.format("%10d", resultat.getNbNoeuds() ) + " | Coût calculé (temps ou distance minimum) : " +  resultat.getCout()) ; fw.write("\r\n") ; 
						if(resultat.getChemin() == null) {
							System.out.println("Le chemin est nul");
						} 
						if(resultat instanceof ResultCovoiturage){
							dessin.setWidth(2) ;

							dessin.setColor(java.awt.Color.magenta);
							((ResultCovoiturage) resultat).getChemin().afficherChemin(dessin);
							dessin.setColor(java.awt.Color.blue) ; 
							((ResultCovoiturage) resultat).getCheminPieton().afficherChemin(dessin);
							dessin.setColor(java.awt.Color.pink) ; 
							((ResultCovoiturage) resultat).getCheminVoiture().afficherChemin(dessin);


						}else{
							resultat.getChemin().afficherChemin(dessin);
						}

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

	public PrintStream neFaitRien() {
		PrintStream result = System.out ;

		String nom = "";

		if ("".equals(nom)) { nom = "/dev/null" ; }

		try { result = new PrintStream(nom) ; }
		catch (Exception e) {
			System.err.println ("Erreur a l'ouverture du fichier " + nom) ;
			System.exit(1) ;
		}

		return result ;
	}

	public void FileTestCreationRandom(String nomCarte, PrintStream sortie, Readarg readarg, Graphe gr, boolean affichage, int nb_tests) {
		System.out.print("Création du fichier de test....") ; 
		File f1 = new File ("TestPccSHORT.txt");
		File f2 = new File ("TestPcc*SHORT.txt");
		File f3 = new File ("TestPccFAST.txt");
		File f4 = new File ("TestPcc*FAST.txt");

		try
		{
			FileWriter fw1 = new FileWriter (f1);
			FileWriter fw2 = new FileWriter (f2);
			FileWriter fw3 = new FileWriter (f3);
			FileWriter fw4 = new FileWriter (f4);
			int nb_noeuds = gr.getNoeuds().length ; 

			fw1.write (nomCarte); fw1.write(" ") ; 
			fw2.write (nomCarte); fw2.write(" ") ; 
			fw3.write (nomCarte); fw3.write(" ") ; 
			fw4.write (nomCarte); fw4.write(" ") ; 
			if (affichage) {
				fw1.write (String.valueOf (1));
				fw2.write (String.valueOf (1));
				fw3.write (String.valueOf (1));
				fw4.write (String.valueOf (1));
			}
			else {
				fw1.write (String.valueOf (0));
				fw2.write (String.valueOf (0));
				fw3.write (String.valueOf (0));
				fw4.write (String.valueOf (0));
			}
			fw1.write("\r\n") ; 
			fw2.write("\r\n") ; 
			fw3.write("\r\n") ; 
			fw4.write("\r\n") ; 

			int range = (int) nb_noeuds/10 ;
			System.out.println("range =" + range) ; 

			int i = 0 ; 
			while (i < nb_tests) {

				int n1 = (int)(Math.random()*(nb_noeuds-1));
				int n2 = (int)(Math.random()*(nb_noeuds-1));

				try {

					

					Pcc algo = new Pcc(gr, sortie, readarg, n1, n2) ; 	
					algo.runCovoiturage() ; 
					
					i++ ; 

					fw1.write (String.valueOf (2)); fw1.write(" ") ; 
					fw1.write(String.valueOf (0)) ; fw1.write(" ") ; 

					fw2.write (String.valueOf (3)); fw2.write(" ") ; 
					fw2.write(String.valueOf (0)) ; fw2.write(" ") ;

					fw3.write (String.valueOf (2)); fw3.write(" ") ; 
					fw3.write(String.valueOf (0)) ; fw3.write(" ") ;

					fw4.write (String.valueOf (3)); fw4.write(" ") ; 
					fw4.write(String.valueOf (0)) ; fw4.write(" ") ;					
					
					fw1.write (String.valueOf (String.valueOf(n1) )); fw1.write(" ") ; 
					fw1.write (String.valueOf (String.valueOf(n2) )); fw1.write(" ") ; 

					fw2.write (String.valueOf (String.valueOf(n1) )); fw2.write(" ") ; 
					fw2.write (String.valueOf (String.valueOf(n2) )); fw2.write(" ") ; 

					fw3.write (String.valueOf (String.valueOf(n1) )); fw3.write(" ") ; 
					fw3.write (String.valueOf (String.valueOf(n2) )); fw3.write(" ") ; 

					fw4.write (String.valueOf (String.valueOf(n1) )); fw4.write(" ") ; 
					fw4.write (String.valueOf (String.valueOf(n2) )); fw4.write(" ") ; 

					fw1.write (String.valueOf (2));
					fw1.write("\r\n") ; 

					fw2.write (String.valueOf (2));
					fw2.write("\r\n") ; 

					fw3.write (String.valueOf (1));
					fw3.write("\r\n") ; 

					fw4.write (String.valueOf (1));
					fw4.write("\r\n") ; 

				}
				catch (Exception e) {
					System.out.println("Trajet impossible, points ignorés"); 
				}
				
			}
			
			fw1.write(String.valueOf(0));
			fw1.close();

			fw2.write(String.valueOf(0));
			fw2.close();

			fw3.write(String.valueOf(0));
			fw3.close();

			fw4.write(String.valueOf(0));
			fw4.close();	

		}
		catch (IOException exception)
		{
			System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		}
		System.out.println("Terminée ! ") ; 

	}

}
