package voiture.deplacement;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTClient {

    private MqttClient client;
    private String broker;
    private String clientId;
    private HashMap<String,Voiture> voitures;//on va ajouter toutes les voitures
    private Carte carte;
    private boolean top;
    private Horloge horloge;

    public MQTTClient(String broker, String clientId,Carte carte,boolean top,Horloge horloge) {
        this.broker = broker;
        this.clientId = clientId;
        this.voitures = new HashMap<String,Voiture>();
        this.carte = carte;
        this.setTop(top);
        this.horloge=horloge;
    }

    public void seConnecter() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(broker, clientId, persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);

            System.out.println("Connecté au broker MQTT: " + broker);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void seDeconnecter() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                System.out.println("Déconnecté du broker MQTT");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void sAbonnerAUnTopic(final String topic) {
        try {
            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connexion MQTT perdue. Reconnexion...");
                    // Logique de reconnexion en cas de perte de connexion : récupère le timestamp actuel et on renvoie notre position
                    publierMessage(topic, null);
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                	String recu = new String(message.getPayload());
                	if(!topic.equals("vehicle") && !topic.equals("top"))
                		System.out.println("Message reçu sur le topic " + topic + ": " + recu);
                    // Personnalisez la logique de traitement du message reçu ici.
                    if(topic.equals("positions")) {
                    	//traitement des positions
                    	JSONObject json = new JSONObject(recu);
                    	for (Iterator<String> iterator = json.keys(); iterator.hasNext();) {
                    		  JSONObject voiturejson = (JSONObject) json.get(iterator.next());
                    		  //System.out.println(voiturejson);
                    		  Object obj_x = voiturejson.opt("x");
                    		  Object obj_y = voiturejson.opt("y");
                    		  Object obj_id = voiturejson.opt("id");
                    		  Object obj_speed = voiturejson.opt("speed");
                    		  Object obj_vtype = voiturejson.opt("vtype");
                    		  Object obj_dir = voiturejson.opt("dir");
                    		  int x,y,speed,vtype,dir;
                    		  String id;
                    		  if (obj_x.getClass().getName().equals("java.lang.Integer")) {x = voiturejson.getInt("x");
                    		  }else {x = Integer.valueOf(voiturejson.getString("x"));}
                    		  if (obj_y.getClass().getName().equals("java.lang.Integer")) {y = voiturejson.getInt("y");
                    		  }else {y = Integer.valueOf(voiturejson.getString("y"));}
                    		  if (obj_id.getClass().getName().equals("java.lang.Integer")) {id = String.valueOf(voiturejson.getInt("id"));
                    		  }else {id = voiturejson.getString("id");}
                    		  if (obj_speed.getClass().getName().equals("java.lang.Integer")) {speed = voiturejson.getInt("speed");
                    		  }else {speed = Integer.valueOf(voiturejson.getString("speed"));}
                    		  if (obj_vtype.getClass().getName().equals("java.lang.Integer")) {vtype = voiturejson.getInt("vtype");
                    		  }else {vtype = Integer.valueOf(voiturejson.getString("vtype"));}
                    		  if (obj_dir.getClass().getName().equals("java.lang.Integer")) {dir = voiturejson.getInt("dir");
                    		  }else {dir = Integer.valueOf(voiturejson.getString("dir"));}
                    		  Voiture voiture = new Voiture(x,y,id,speed,0,null,vtype,carte);
                    		  voitures.put(voiture.getId(), voiture);
                    	}
                    }if(topic.equals("lights")) {
                    	//traitement des feux
                    	JSONObject json = new JSONObject(recu);
                    	for(int i=1; i<carte.getNbTrafficLight();i++) {
                    		if(json.has(Integer.toString(i))) {
                    			String key = Integer.toString(i);
                    			String value = json.getString(key);
                    			String est = value.split(",")[0];boolean estBool = est.equals("0")?false:true;
                    			String nord = value.split(",")[1];boolean nordBool = nord.equals("0")?false:true;
                    			String ouest = value.split(",")[2];boolean ouestBool = ouest.equals("0")?false:true;
                    			String sud = value.split(",")[3];boolean sudBool = sud.equals("0")?false:true;
                    			carte.getTrafficLightPosition().get(key).setEst(estBool);
                    			carte.getTrafficLightPosition().get(key).setNord(nordBool);
                    			carte.getTrafficLightPosition().get(key).setSud(ouestBool);
                    			carte.getTrafficLightPosition().get(key).setOuest(sudBool);
                    		}
                    	}                	
                    }if(topic.equals("top") && !top) {
                    	//on peut démarrer
                    	System.out.println("TOP DEPART");
                    	top = true;
                    	horloge.reset(); //remise à 0 de l'horloge
                    }
                    //Message msg = Message.fromJson(messageText);//TODO
                    
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    //System.out.println("Message publié avec succès");
                }
            });

            client.subscribe(topic);
            System.out.println("Abonné au topic MQTT: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    
    public void publierMessage(String topic, Message message) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.toJson().getBytes()); // Convertir le message en tableau d'octets
                client.publish(topic, mqttMessage);
                System.out.println("Message publié sur le topic " + topic + ": " + message.toJson());
            }
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

	public HashMap<String, Voiture> getVoitures() {
		return voitures;
	}

	public void setVoitures(HashMap<String, Voiture> voitures) {
		this.voitures = voitures;
	}

	public Carte getCarte() {
		return carte;
	}

	public void setCarte(Carte carte) {
		this.carte = carte;
	}

	public boolean getTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}
	
	public MqttClient getClient() {
		return client;
	}

	public Horloge getHorloge() {
		return horloge;
	}

	public void setHorloge(Horloge horloge) {
		this.horloge = horloge;
	}
}
