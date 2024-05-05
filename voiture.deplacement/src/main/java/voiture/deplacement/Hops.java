package voiture.deplacement;

import java.util.ArrayList;

//un hop (saut) est représenté par un couple de coordonnées de départ, et un double de coordonnées d'arrivée
public class Hops {
	private int xDep;
	private int yDep;
	private int xArr;
	private int yArr;
	
	public Hops(int xDep,int yDep,int xArr,int yArr) {
		if(xDep>=0 && xDep<=100)
			setxDep(xDep);
		if(yDep>=0 && yDep<=100)
			setyDep(yDep);
		if(xArr>=0 && xArr<=100)
			setxArr(xArr);
		if(yArr>=0 && yArr<=100)
			setyArr(yArr);
	}
	public int directionTroncon() {
		//on va retourner 0,1,2,3 pour la direction du troncon
		int direction=0;
		int xA = getxDep();
		int yA = getyDep();
		int xB = getxArr();
		int yB = getyArr();
		if(xB>xA) {
			direction=0;//vers l'est
		}else if(xB<xA) {
			direction=2;//vers l'ouest
		}else if(yB>yA) {
			direction=1;//vers le nord
		}else if(yB<yA) {
			direction=3;//vers le sud
		}
		return(direction);
	}
	public boolean estSurExtremiteTroncon(int x, int y) {
		boolean result = false;
		if(estSurTroncon(x,y,this)) {
			if(x==getxArr() && y==getyArr()) {
				result=true;
			}
		}
		return(result);
	}
	public static boolean estSurTroncon(int x, int y, Hops hop) {
		boolean result = false;
		//Attention, parfois le troncon va de bas en haut ou de droite a gauche
		if(x>=hop.getxDep() && x<=hop.getxArr() && y>=hop.getyDep() && y<=hop.getyArr()) {
			//bas vers haut ou gauche vers droite
			result = true;
		}else if(x<=hop.getxDep() && x>=hop.getxArr() && y<=hop.getyDep() && y>=hop.getyArr()){
			//haut vers bas ou droite vers gauche
			result = true;
		}else {
			result = false;
		}
		return(result);
	}
	//le but est de trouver l'id du tronçon sur lequel on se trouve : le bearing est pour savoir dans quelle direction on va
	public static int trouverTronçon(int x, int y, ArrayList<Hops> hops) {
		int taille = hops.size();
		int id=0;
		while(id<taille && (!estSurTroncon(x,y,hops.get(id)))) {
			id++;
		}
		if(id==taille) {
			id=-1;//cas d'erreur
		}else {
			//on vérifie que l'on est pas à une extrémité gênante, auquel cas il faut un autre troncon
			Hops hop = hops.get(id);
			if(hop.estSurExtremiteTroncon(x, y)) {
				//il faut prendre le prochain troncon que l'on rencontre
				id ++;
				while(id<taille && (!estSurTroncon(x,y,hops.get(id)))) {
					id++;
				}
				if(id==taille) {
					id=-1;//cas d'erreur
				}
			}
		}
		return id;
	}
	public String toString() {
		return("["+xDep+","+yDep+"];["+xArr+","+yArr+"]");
	}

	public int getxDep() {
		return xDep;
	}

	public void setxDep(int xDep) {
		this.xDep = xDep;
	}

	public int getyDep() {
		return yDep;
	}

	public void setyDep(int yDep) {
		this.yDep = yDep;
	}

	public int getxArr() {
		return xArr;
	}

	public void setxArr(int xArr) {
		this.xArr = xArr;
	}

	public int getyArr() {
		return yArr;
	}

	public void setyArr(int yArr) {
		this.yArr = yArr;
	}
}
