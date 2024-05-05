package voiture.deplacement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.ini4j.InvalidFileFormatException;

public class Test {
	//classe pour les tests initiaux
	/*public static void main(String[] args) throws InvalidFileFormatException, FileNotFoundException, IOException {
		String pwd = System.getProperty("user.dir");
		String fichierVoiture = pwd + "/ConfigurationVoiture/voiture.ini";
		String fichierCarte = pwd + "/ConfigurationCarte/carte.ini";
		Carte carte = null;
		Voiture voiture = null;
		carte = new Carte(fichierCarte);
		voiture = new Voiture(fichierVoiture,carte);
		System.out.println(voiture.toString());
		boolean boucle = true;
		while(boucle) {
			voiture.deplacementComplet(voiture.getHops());
			int x = voiture.getX();
			int y = voiture.getY();
			int id = Hops.trouverTronçon(x, y, voiture.getHops());
			Hops hop;
			int hearing = 0;
			if (id != -1) {
				hop = voiture.getHops().get(id);
				hearing = hop.directionTroncon();
			}

			Message m=new Message(x, y, voiture.getId(), hearing, voiture.getVitesse(),voiture.getTypeVehicule());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/
}
