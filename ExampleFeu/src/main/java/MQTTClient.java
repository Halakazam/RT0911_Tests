import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MQTTClient {

    private MqttClient client;
    private String broker;
    private String clientId;

    public MQTTClient(String broker, String clientId) {
        this.broker = broker;
        this.clientId = clientId;
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
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String recu = new String(message.getPayload());
                    System.out.println("Message reçu sur le topic " + topic + ": " + recu);
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            client.subscribe(topic);
            System.out.println("Abonné au topic MQTT: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publierMessage(String topic, String message) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                client.publish(topic, mqttMessage);
                System.out.println("Message publié sur le topic " + topic + ": " + message);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publierMessagePeriodiquement(final String topic, final String message, int intervalSeconds) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                publierMessage(topic, message);
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }
}
