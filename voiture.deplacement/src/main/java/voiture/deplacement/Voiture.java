package voiture.deplacement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.ini4j.*;//Gestion des fichiers ini

//La classe Voiture est pour initialiser la voiture à partir de son fichier de configuration
public class Voiture {
	public static int MODULO_CARTE = 101; // pour les modulos pour la carte
	private String pos;
	private String id;
	private int xDepart;
	private int yDepart;
	private int vitesse;// exprimé en nombre de "saut" par seconde
	private int x;
	private int y;
	private int typeVehicule;
	//ici on va ajouter les chemins spécifiques que suit la voiture : ne peut pas sortir du tronçon
	private int nbHops;
	private ArrayList<Hops> hops;
	private  Carte carte;
	//Plusieurs constructeur : en fonction 
	public Voiture(String pos, String id, int vitesse,int nbHops, ArrayList<Hops> hops, int typeVehicule, Carte carte) {
		setPos(pos);
		setX(Integer.valueOf(pos.split(",")[0]));
		setY(Integer.valueOf(pos.split(",")[1]));
		setId(id);
		setVitesse(vitesse);
		setNbHops(nbHops);
		setHops(hops);
		setTypeVehicule(typeVehicule);
		setCarte(carte);
	}
	public Voiture(int x, int y, String id, int vitesse,int nbHops, ArrayList<Hops> hops, int typeVehicule, Carte carte) {
		setPos(x%MODULO_CARTE+","+y%MODULO_CARTE);
		setX(x);
		setY(y);
		setId(id);
		setVitesse(vitesse);
		setNbHops(nbHops);
		setHops(hops);
		setTypeVehicule(typeVehicule);
		setCarte(carte);
	}
	//initialise une voiture en fonction du nom du fichier : on choisit un fichier .ini, il doit donc être bien formé, on vérifie l'erreur ici
	public Voiture(String nomDuFichier, Carte carte) throws InvalidFileFormatException, FileNotFoundException, IOException {
		Ini ini = new Ini(new FileReader(nomDuFichier));
		Ini.Section section = ini.get("generalInfo");
		setPos(section.get("startPosition"));
		setId(section.get("idCar"));
		setVitesse(Integer.valueOf(section.get("speed")));
		setNbHops(Integer.valueOf(section.get("nbHops")));
		setTypeVehicule(Integer.valueOf(section.get("typeVehicule")));
		setX(Integer.valueOf(getPos().split(",")[0]));
		setxDepart(getX());
		setyDepart(getY());
		setY(Integer.valueOf(getPos().split(",")[1]));
		section = ini.get("trip");
		ArrayList<Hops> hops = new ArrayList<Hops>();
		for(int i=1;i<=getNbHops();i++) {
			String troncon = section.get(String.valueOf(i));
			int xDep,yDep,xArr,yArr;
			xDep = Integer.valueOf(troncon.split(",")[0]);
			yDep = Integer.valueOf(troncon.split(",")[1]);
			xArr = Integer.valueOf(troncon.split(",")[2]);
			yArr = Integer.valueOf(troncon.split(",")[3]);
			Hops hop = new Hops(xDep,yDep,xArr,yArr);
			hops.add(hop);
		}
		setHops(hops);
		setCarte(carte);
	}
	//toString pour le déboggage
	public String toString() {
		String informations="La voiture d'id "+getId()+" se trouve en position ["+getPos()+"] Elle roule à une vitesse "+getVitesse()+"\n";/*+
    					"La voiture va parcourir "+getNbHops()+" tronçons, qui sont : ";
    	for(int i=0; i<getHops().size();i++) {
    		informations += getHops().get(i).toString()+" - ";
    	}
    	informations+="\n";*/
		return(informations);
	}

	//Méthode de déplacement
	//Pas vérification de la possibilité ici, on se déplace
	public void deplacement(int x, int y) {
		setX(getX()+x);
		setY(getY()+y);
		setPos(getX()+","+getY());
	}
	//récupérer les id des feux sur un troncon
	public ArrayList<Integer> feuSurTroncon(Hops troncon){
		ArrayList<Integer> liste = new ArrayList<Integer>();
		int xA = troncon.getxDep();
		int yA = troncon.getyDep();
		int xB = troncon.getxArr();
		int yB = troncon.getyArr(); 
		for(int i=1;i<=carte.getNbTrafficLight();i++) {
			FeuTricolore feu = carte.getTrafficLightPosition().get(String.valueOf(i));
			int xFeu = feu.getX();
			int yFeu = feu.getY();
			//System.out.println("Feu "+i+" : "+xFeu+","+yFeu);
			//on se déplace sur le troncon suivant notre valeur de x et de y : gestion de l'erreur si déjà on ne se trouve pas sur le tronçon
			if(xA!=xB && yA!=yB){
				//On se déplace forcément en ligne dans ce projet, donc xA=xB OU yA=yB
				System.out.println("Cette fonction ne se déplace qu'en ligne, et ici le tronçon est une diagonale.");
			}else if(xA<xB && yFeu==yA) {
				//déplacement horizontal de gauche à droite
				if(xFeu<xA || xFeu>xB) {
					//System.out.println("Le feu ne se trouve pas sur le tronçon allant de xA="+xA+" vers xB="+xB);
				}else {
					//le feu est dedans
					liste.add(i);
				}
			}else if(xB<xA && yFeu==yA) {
				//déplacement horizontal de droite à gauche
				if(xFeu<xB || xFeu>xA) {
					//System.out.println("Le feu ne se trouve pas sur le tronçon allant de xB="+xB+" vers xA="+xA);
				}else {
					//le feu est dedans
					liste.add(i);
				}
			}else if (yA<yB && xFeu==xA) {
				if(yFeu<yA || yFeu>yB){
					//System.out.println("Le feu ne se trouve pas sur le tronçon allant de yA="+yA+" vers yB="+yB);
				}else {
					//le feu est dedans
					liste.add(i);
				}
			}else if (yB<=yA && xFeu==xA) {
				if(yFeu<yB || yFeu>yA){
					//System.out.println("Le feu ne se trouve pas sur le tronçon allant de yB="+yB+" vers yA="+yA);
				}else {
					//le feu est dedans
					liste.add(i);
				}
			}

		}
		if(liste.size()==0)
			liste=null;
		return liste;
	}
	//déplacement sur des tronçons selon la vitesse
	//Un troncon a la forme xA,yA,xB,yB pour dire que le troncon va du point A jusqu'au point B : attention le sens à une importance
	public int deplacementSelonTroncon(int reste,Hops troncon) {
		//Il faut surtout faire un test pour savoir s'il y a un feu sur la route, et si ce feu est vert ou rouge, pour savoir si l'on peut passer ou non
		int resteAFaire = reste;//ce sera getVitesse() au tout début
		int xA = troncon.getxDep();
		int yA = troncon.getyDep();
		int xB = troncon.getxArr();
		int yB = troncon.getyArr();
		boolean feu = false;//pas de feu de base sur la position où l'on se trouve
		boolean vert = true;//initialisé à vert
		ArrayList<Integer> feux = feuSurTroncon(troncon);
		if(feux!=null) {
			System.out.println("Sur ce troncon il y a les feux : "+feux);
			feu = false; //true; //pas de gestion des feux pour le moment
		}else {
			feu = false;
		}
		//on se déplace sur le troncon suivant notre valeur de x et de y : gestion de l'erreur si déjà on ne se trouve pas sur le tronçon
		if(xA!=xB && yA!=yB && resteAFaire!=0){
			//On se déplace forcément en ligne dans ce projet, donc xA=xB OU yA=yB
			System.out.println("Cette fonction ne se déplace qu'en ligne, et ici le tronçon est une diagonale, ou on utilise la fonction pour un reste à 0.");
		}else if(xA<xB) {
			//déplacement horizontal de gauche à droite
			if(getX()<xA || getX()>xB) {
				System.out.println("On ne se trouve pas sur le tronçon allant de xA="+xA+" vers xB="+xB);
			}else {
				//On doit voir si on dépasse xB ou pas et si le feu est vert s'il y en a un et si l'une des coordonnées est concernée
				if(!feu) {
					if(resteAFaire+getX()>xB) {
						resteAFaire=resteAFaire-(xB-getX());
						deplacement(xB-getX(),0);
					}else {
						deplacement(resteAFaire,0);
						resteAFaire=0;
					}
				}else {
					
				}
			}
		}else if(xB<xA) {
			//déplacement horizontal de droite à gauche
			if(getX()<xB || getX()>xA) {
				System.out.println("On ne se trouve pas sur le tronçon allant de xB="+xB+" vers xA="+xA);
			}else {
				//On doit voir si on dépasse xB ou pas
				if(!feu) {
					if(getX()-resteAFaire<xB) {
						resteAFaire=resteAFaire-(getX()-xB);
						deplacement(xB-getX(),0);
					}else {
						deplacement(-resteAFaire,0);
						resteAFaire=0;
					}
				}
			}
		}else if (yA<yB) {
			if((getY()<yA || getY()>yB)){
				System.out.println("On ne se trouve pas sur le tronçon allant de yA="+yA+" vers yB="+yB);
			}else {
				//On doit voir si on dépasse yB ou pas
				if(!feu) {
					if(resteAFaire+getY()>yB) {
						resteAFaire=resteAFaire-(yB-getY());
						deplacement(0,yB-getY());
					}else {
						deplacement(0,resteAFaire);
						resteAFaire=0;
					}
				}
			}
		}else if (yB<=yA) {
			if(getY()<yB || getY()>yA){
				System.out.println("On ne se trouve pas sur le tronçon allant de yB="+yB+" vers yA="+yA);
			}else {
				//On doit voir si on dépasse yB ou pas
				if(!feu) {
					if(getY()-resteAFaire<yB) {
						resteAFaire=resteAFaire-(getY()-yB);
						deplacement(0,yB-getY());
					}else {
						deplacement(0,-resteAFaire);
						resteAFaire=0;
					}
				}
			}
		}
		//System.out.println("Voiture x="+getX()+" y="+getY()+" xA="+xA+" yA="+yA+" xB="+xB+" yB="+yB+" RAF="+resteAFaire);
		return resteAFaire;
	}
	//Fonction pour se déplacer selon une liste de troncon en une seconde (c'est à dire un nombre de case de vitesse)
	public void deplacementComplet(ArrayList<Hops> troncons) {
		int resteAFaire = getVitesse();
		//il faut déjà trouver le tronçon sur lequel on se trouve
		int i=Hops.trouverTronçon(getX(), getY(), troncons);
		if(i==-1) {
			System.out.println("Erreur, la voiture se trouve en ["+getX()+","+getY()+"], qui n'appartient à aucun tronçon de la voiture.\nRetour au point de départ.");
			setX(getxDepart());
			setY(getyDepart());

		}else {
			Hops hop = troncons.get(i);
			resteAFaire=deplacementSelonTroncon(resteAFaire,hop);
			while(resteAFaire!=0 && i!=-1) {
				i=Hops.trouverTronçon(getX(), getY(), troncons);
				if(i!=-1) {
					hop = troncons.get(i);
					resteAFaire=deplacementSelonTroncon(resteAFaire,hop);
				}
			}
		}
	}
	// Getters pour les paramètres
	// on travaille modulo 101 pour avoir une grille torique 
	public String getPos() {
		return pos;
	}
	public String getId() {
		return id;
	}
	public int getVitesse() {
		return vitesse;
	}
	public int getY() {
		return y%MODULO_CARTE;
	}
	public int getX() {
		return x%MODULO_CARTE;
	}
	// Setters pour les paramètres
	public void setPos(String pos) {
		this.pos = pos;
	}

	public void setId(String string) {
		this.id = string;
	}

	public void setVitesse(int vitesse) {
		this.vitesse = vitesse;
	}

	public void setX(int x) {
		this.x = x%MODULO_CARTE;
	}

	public void setY(int y) {
		this.y = y%MODULO_CARTE;
	}
	public int getNbHops() {
		return nbHops;
	}
	public void setNbHops(int nbHops) {
		this.nbHops = nbHops;
	}
	public ArrayList<Hops> getHops() {
		return hops;
	}
	public void setHops(ArrayList<Hops> hops) {
		this.hops = hops;
	}
	public int getTypeVehicule() {
		return typeVehicule;
	}
	public void setTypeVehicule(int typeVehicule) {
		this.typeVehicule = typeVehicule;
	}
	public int getxDepart() {
		return xDepart;
	}
	public void setxDepart(int xDepart) {
		this.xDepart = xDepart;
	}
	public int getyDepart() {
		return yDepart;
	}
	public void setyDepart(int yDepart) {
		this.yDepart = yDepart;
	}
	public Carte getCarte() {
		return carte;
	}
	public void setCarte(Carte carte) {
		this.carte = carte;
	}



}
