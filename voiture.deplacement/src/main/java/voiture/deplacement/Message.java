package voiture.deplacement;
import org.json.JSONObject;

public class Message {
    private String pos;
    private int slot;// pour le temps, en entier : à la seconde slot
    private String id;
    private int x;
    private int y;
    private int heading;//pour la direction : 0 à droite, 1 en haut, 2 à gauche et 3 en bas (sens trigo)
    private int vitesse;
    private int typeVehicule;//0 pour une voiture normale, sinon police etc

    public Message(int x, int y, String id,int heading, int vitesse, int typeVehicule) {
        this.pos = x+","+y;
        this.x=x;
        this.y=y;
        //this.slot = slot; // Timestamp en secondes
        this.id = id; // Générez un ID unique
        if(heading==0 || heading ==1 || heading ==2 || heading == 3) {
        	this.heading=heading;
        }else {
        	this.heading=0;//par défaut
        }
        this.vitesse = vitesse;
        this.typeVehicule = typeVehicule;
    }
    //Un autre constructeur
    public Message(String pos, String id,int heading, int vitesse, int typeVehicule) {
    	this.pos = pos;
    	this.x = Integer.valueOf(pos.split(",")[0]);
    	this.y = Integer.valueOf(pos.split(",")[1]);
        //this.slot = slot; // Timestamp en secondes
        this.id = id; // Générez un ID unique
        this.vitesse = vitesse;
        if(heading==0 || heading ==1 || heading ==2 || heading == 3) {
        	this.heading=heading;
        }else {
        	this.heading=0;//par défaut
        }
        this.typeVehicule = typeVehicule;
    }
    
    // Méthode pour sérialiser un objet Message en JSON : on utilise le format indiqué Pos, slot, id
    public String toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("vtype", typeVehicule);
        json.put("x", x);
        json.put("y", y);
        json.put("dir", heading);
        json.put("speed", vitesse);
        return json.toString();
    }

    // Méthode pour désérialiser un JSON en un objet Message
    public static Message fromJson(String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        int x = json.getInt("x");
        int y = json.getInt("y");
        String id = json.getString("id");
        int heading = json.getInt("dir");
        int vitesse = json.getInt("speed");
        int typeVehicule = json.getInt("vtype");
        return new Message(x,y, id, heading, vitesse, typeVehicule);
    }
    
    // Getters pour les attributs
    public String getId() {
        return id;
    }

	public String getPos() {
		return pos;
	}

	public int getSlot() {
		return slot;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public double getHeading() {
		return heading;
	}
	public int getVitesse() {
		return vitesse;
	}
	public void setVitesse(int vitesse) {
		this.vitesse = vitesse;
	}
	public int getTypeVehicule() {
		return typeVehicule;
	}
	public void setTypeVehicule(int typeVehicule) {
		this.typeVehicule = typeVehicule;
	}

}