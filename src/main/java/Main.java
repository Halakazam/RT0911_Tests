import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ini4j.InvalidFileFormatException;
import org.json.JSONObject;




public class Main {
	//Variable pour garder une trace de l'état actuel
	// Variable pour garder une trace de l'état actuel (utilisation de AtomicBoolean pour garantir la synchronisation)
	static java.util.concurrent.atomic.AtomicInteger etatActuel = new java.util.concurrent.atomic.AtomicInteger(0);


	public static void main(String[] args) throws InvalidFileFormatException, FileNotFoundException, IOException {
		String pwd = System.getProperty("user.dir");
		String fichierCarte = pwd + "/ConfigurationCarte/carte.ini";
		Carte carte = null;
		carte = new Carte(fichierCarte);
		HashMap<String,ArrayList<Boolean>> feux = new HashMap<String,ArrayList<Boolean>>();
		for(int i=1;i<=carte.getNbTrafficLight();i++) {
			//FeuTricolore feu = carte.getTrafficLightPosition().get(String.valueOf(i));
			ArrayList<Boolean> list = new ArrayList<Boolean>();
			list.add(false);//feu.getEst());
			list.add(true);//feu.getNord());
			list.add(false);//feu.getOuest());
			list.add(true);//feu.getSud());
			feux.put(String.valueOf(i), list);
		}
		String broker = "tcp://194.57.103.203:1883";
		String clientId = "JavaMQTTClientLights";
		String topic = "lights";

		MQTTClient mqttClient = new MQTTClient(broker, clientId);

		// Se connecter au broker MQTT
		mqttClient.seConnecter();

		// S'abonner au topic "UT"
		mqttClient.sAbonnerAUnTopic(topic);


		// Créer un service d'exécution planifiée
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);



		// Planifier une tâche pour alterner l'état toutes les 10 secondes
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// Alterner entre 0 et 1
				int nouvelEtat = (etatActuel.get() == 0) ? 1 : 0;
				etatActuel.set(nouvelEtat);

				// Mise à jour des états des feux dans le hashmap
				for (String idFeu : feux.keySet()) {
					ArrayList<Boolean> etatsFeu = feux.get(idFeu);
					for (int i = 0; i < etatsFeu.size(); i++) {
						boolean etatActuel = etatsFeu.get(i); // Récupérer l'état actuel
						int nouvelEtatInt = etatActuel ? 0 : 1; // Inverser l'état actuel (true devient 0 et false devient 1)
						etatsFeu.set(i, nouvelEtatInt == 1); // Mettre à jour l'ArrayList avec le nouvel état
					}
				}

				// Création du message JSON avec les données des feux
				JSONObject json = new JSONObject();
				for (String idFeu : feux.keySet()) {
				    ArrayList<Boolean> etatsFeu = feux.get(idFeu);
				    StringBuilder etatsFeuString = new StringBuilder();
				    for (Boolean etat : etatsFeu) {
				        int etatInt = etat ? 1 : 0; // Convertir true en 1 et false en 0
				        if (etatsFeuString.length() > 0) {
				            etatsFeuString.append(",");
				        }
				        etatsFeuString.append(etatInt);
				    }
				    json.put(idFeu, etatsFeuString.toString());
				}

				// Publier le message avec l'état actuel
				mqttClient.publierMessage(topic, json.toString());
			}
		}, 0, 10, TimeUnit.SECONDS);

		// Attendre indéfiniment (le programme ne quitte pas)
		Object waitObject = new Object();
		synchronized (waitObject) {
			try {
				waitObject.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Se déconnecter du broker MQTT
		mqttClient.seDeconnecter();
	}
}
