package voiture.deplacement;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class UpperTester {
	private MqttClient client;
    private String broker;
    private String clientId;
	 private Voiture voiture;//on va ajouter toutes les voitures
	 private Horloge horloge;
	 private String topicUTResp;

	 public UpperTester(String broker, String clientId,Horloge horloge,Voiture voiture,String topicUTResp) {
		 this.broker = broker;
	     this.clientId = clientId;
		 this.voiture = voiture;
		 this.horloge = horloge;
		 this.topicUTResp = topicUTResp;
	 }

	    public void seConnecter() {
	        try {
	            MemoryPersistence persistence = new MemoryPersistence();
	            client = new MqttClient(broker, clientId, persistence);

	            MqttConnectOptions connOpts = new MqttConnectOptions();
	            connOpts.setCleanSession(true);

	            client.connect(connOpts);

	            System.out.println("Connecté au broker MQTT - UpperTester : " + broker);
	        } catch (MqttException e) {
	            e.printStackTrace();
	        }
	    }

	    public void seDeconnecter() {
	        try {
	            if (client != null && client.isConnected()) {
	                client.disconnect();
	                System.out.println("Déconnecté du broker MQTT - UpperTester");
	            }
	        } catch (MqttException e) {
	            e.printStackTrace();
	        }
	    }
	 public void sAbonnerAUnTopic(final String topic) {
		 try {
			 client.setCallback(new MqttCallback() {
				 public void connectionLost(Throwable cause) {
					 System.out.println("Connexion MQTT UpperTester perdue. Reconnexion...");
					 // Logique de reconnexion en cas de perte de connexion : récupère le timestamp actuel et on renvoie notre position
				 }

				 public void messageArrived(String topic, MqttMessage message) throws Exception {
					 String recu = new String(message.getPayload());
					 //System.out.println(recu);
					 if(topic.equals("UT")) {
						 //traitement des positions
						 JSONObject json = new JSONObject(recu);
						 int id = json.getInt("id");
						 if(Integer.toString(id).equals(clientId.substring(0, clientId.length() - 2))){
							 //on doit renvoyer nos informations sur la file
							 publierMessage(topicUTResp, horloge, voiture);
							 //suivant ce qui est demandé, on pourrait augmenter la vitesse de la voiture, ralentir, etc

						 }
					 }

				 }

				 public void deliveryComplete(IMqttDeliveryToken token) {
					 //System.out.println("Message publié avec succès");
				 }
			 });

			 client.subscribe(topic);
			 System.out.println("Abonné au topic MQTT - UpperTester : " + topic);
		 } catch (MqttException e) {
			 e.printStackTrace();
		 }
	 }

	 public void publierMessage(String topic, Horloge horloge, Voiture voiture) {
		 try {
			 System.out.println("UpperTester MESSAGE EN COURS ....");
			 if (client != null && client.isConnected()) {
				 MqttMessage mqttMessage = new MqttMessage();
				 //il faut transformer le message que l'on va envoyer en jSON : sous la forme "id", "temps", "position"
				 JSONObject json = new JSONObject();
				 json.put("id", Integer.parseInt(clientId.substring(0, clientId.length() - 2)));
				 json.put("temps", horloge.getCurrentTime());
				 json.put("position", voiture.getX()+","+voiture.getY());
				 mqttMessage.setPayload(json.toString().getBytes()); // Convertir le message en tableau d'octets
				 client.publish(topic, mqttMessage);
				 System.out.println("UpperTester - Message publié sur le topic " + topic + ": " + json.toString());
			 }
			 System.out.println("UpperTester FIN MESSAGE ...");
		 } catch (MqttException e) {
			 e.printStackTrace();
		 }
	 }

	 // Getters et setters pour les propriétés broker et clientId
	 public String getBroker() {
		 return broker;
	 }

	 public void setBroker(String broker) {
		 this.broker = broker;
	 }

	 public String getClientId() {
		 return clientId;
	 }

	 public void setClientId(String clientId) {
		 this.clientId = clientId;
	 }



	 public Voiture getVoiture() {
		 return voiture;
	 }

	 public void setVoiture(Voiture voiture) {
		 this.voiture = voiture;
	 }

	 public Horloge getHorloge() {
		 return horloge;
	 }

	 public void setHorloge(Horloge horloge) {
		 this.horloge = horloge;
	 }
	 public String getTopicUTResp() {
		 return topicUTResp;
	 }
	 public void setTopicUTResp(String topicUTResp) {
		 this.topicUTResp = topicUTResp;
	 }
}
