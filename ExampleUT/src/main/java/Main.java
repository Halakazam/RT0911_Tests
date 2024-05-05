public class Main {

    public static void main(String[] args) {
        String broker = "tcp://194.57.103.203:1883";
        String clientId = "JavaMQTTClient";
        String topic = "UT";
        String topicResp = "RESP";

        MQTTClient mqttClient = new MQTTClient(broker, clientId);

        // Se connecter au broker MQTT
        mqttClient.seConnecter();

        // S'abonner au topic "UT"
        mqttClient.sAbonnerAUnTopic(topic);
        mqttClient.sAbonnerAUnTopic(topicResp);
        
        mqttClient.publierMessage("top", "DEPART");//donne le top départ
        // Publier périodiquement le message {"id":2} toutes les 10 secondes
        mqttClient.publierMessagePeriodiquement(topic, "{\"id\":2}", 10);

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
