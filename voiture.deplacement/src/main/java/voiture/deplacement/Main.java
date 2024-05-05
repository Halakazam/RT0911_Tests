package voiture.deplacement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.ini4j.InvalidFileFormatException;

public class Main {
	private volatile static Horloge horloge = null;//il faut le préciser en volatile pour l'acces entre chaque thread 
	static String clientId = "2"; // Identifiant unique de votre client MQTT
	static String topicVehicle = "vehicle"; // on écrit dedans 
	static String topicPositions = "positions"; //on lit les positions de tout le monde
	static String topicLights = "lights";//infos sur les feux
	static String topicTop = "top";//top départ pour commencer
	static String topicUT = "UT"; //UPPERTESTER pour pouvoir récupérer la position et l'horloge
	static String topicUTResp = "RESP";//réponse de l'uppertester
	static String broker = "tcp://194.57.103.203:1883";
	static String pwd = System.getProperty("user.dir");
	static String fichierVoiture = pwd + "/ConfigurationVoiture/voiture.ini";
	static String fichierCarte = pwd + "/ConfigurationCarte/carte.ini";
	private volatile static Carte carte = null;
	private volatile static Voiture voiture = null;
	
	public static void main(String[] args) throws InvalidFileFormatException, FileNotFoundException, IOException {
		horloge = new Horloge();
		carte = new Carte(fichierCarte);
		voiture = new Voiture(fichierVoiture,carte);
		final MQTTClient mqttClient = new MQTTClient(broker, clientId, carte, false,horloge);
		final UpperTester upperTester = new UpperTester(broker, clientId+"UT", horloge, voiture, topicUTResp);
		
		// Créer et démarrer MQTTClient dans un thread
		Thread mqttThread = new Thread(new Runnable() {
			public void run() {
				mqttClient.seConnecter();
				mqttClient.sAbonnerAUnTopic(topicPositions);
				mqttClient.sAbonnerAUnTopic(topicLights);
				mqttClient.sAbonnerAUnTopic(topicTop);
			}
		});
		mqttThread.start();

		// Créer et démarrer UpperTester dans un autre thread
		Thread upperTesterThread = new Thread(new Runnable() {
			public void run() {
				upperTester.seConnecter();
				upperTester.sAbonnerAUnTopic(topicUT);
			}
		});
		upperTesterThread.start();
		
		//attente du top départ dans la file, quand il arrive on quitte cette boucle infini et on débute l'horloge à 0 (fait à la réception du message par MQTTClient)
		while(!mqttClient.getTop()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Etape après la connexion MQTT ici... : on se déplace selon un parcours défini dans un fichier .ini   
		System.out.println(voiture.toString());
		boolean boucle = true;
		while(boucle) {
			//on fait un déplacement complet selon la vitesse
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
			mqttClient.publierMessage(topicVehicle, m);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(mqttClient.getVoitures());
		mqttClient.seDeconnecter();
	}

}
