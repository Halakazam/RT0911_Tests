package voiture.deplacement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Carte {
	public static int MODULO_CARTE = 101; // pour les modulos pour la carte
	private int nbHops;
	private int nbTrafficLight;
	private ArrayList<Hops> hops;
	private HashMap<String,FeuTricolore> trafficLightPosition;
	
	public Carte(String nomDuFichier) throws InvalidFileFormatException, FileNotFoundException, IOException {
		Ini ini = new Ini(new FileReader(nomDuFichier));
    	Ini.Section section = ini.get("generalInfo");
        setNbHops(Integer.valueOf(section.get("nbHops")));
        setNbTrafficLight(Integer.valueOf(section.get("nbTrafficLight")));
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
    	section = ini.get("trafficLight");
    	HashMap<String,FeuTricolore> feuTricolore = new HashMap<String,FeuTricolore>();
    	for(int i=1;i<=getNbTrafficLight();i++) {
    		String pos = section.get(String.valueOf(i));
    		int x = Integer.valueOf(pos.split(",")[0]);
    		int y = Integer.valueOf(pos.split(",")[1]);
    		feuTricolore.put(Integer.toString(i),new FeuTricolore(x,y,true,true,true,true));//tout est vert pour le moment
    	}
    	setTrafficLightPosition(feuTricolore);
	}
	public String toString() {
		//retourne la carte sous forme de matrice, avec O pour un feu,X pour les routes
		//déclarer la matrice
		char[][] matrice = new char[MODULO_CARTE][MODULO_CARTE];//la carte va de 0 a 100
		//on rempli la matrice
		for (int i = 0; i < MODULO_CARTE; i++) {
			for (int j = 0; j < MODULO_CARTE; j++) {
				//on regarde si un tronçon existe pour ce point
				if(Hops.trouverTronçon(i,j,getHops())!=-1) {
					matrice[j][i] = 'X';//inversion ligne=ordonnées ici et colonne=abscisse
				}else {
					matrice[j][i] = ' ';
				}
			}
		}
		for (int i=1;i<getNbTrafficLight();i++) {
			FeuTricolore pos = getTrafficLightPosition().get(Integer.toString(i));
			int x = pos.getX();
			int y = pos.getY();
			matrice[x][y] = 'O';
		}
		String carteStr="";
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice[0].length; j++) {
				carteStr+=matrice[i][j];
			}
			carteStr+="\n";
		}
		return(carteStr);
	}
	
	
	public int getNbHops() {
		return nbHops;
	}
	public void setNbHops(int nbHops) {
		this.nbHops = nbHops;
	}
	public int getNbTrafficLight() {
		return nbTrafficLight;
	}
	public void setNbTrafficLight(int nbTrafficLight) {
		this.nbTrafficLight = nbTrafficLight;
	}
	public ArrayList<Hops> getHops() {
		return hops;
	}
	public void setHops(ArrayList<Hops> hops) {
		this.hops = hops;
	}
	public HashMap<String, FeuTricolore> getTrafficLightPosition() {
		return trafficLightPosition;
	}
	public void setTrafficLightPosition(HashMap<String, FeuTricolore> trafficLightPosition) {
		this.trafficLightPosition = trafficLightPosition;
	}
	
	
	
}
