package voiture.deplacement;

import java.util.concurrent.locks.ReentrantLock;

public class Horloge {
	// Point de départ (en millisecondes)
    private static long startTime;
    private final static ReentrantLock lock = new ReentrantLock(); // pour placer un lock sur la variable
    
    public Horloge() {
        // Initialiser le point de départ au moment où vous le souhaitez
        startTimer();
    }

    // Fonction pour démarrer le timer (définir le point de départ)
    private static synchronized void startTimer() {
    	lock.lock();
        try {
            // Opérations sur la variable partagée
        	startTime = System.currentTimeMillis();
        } finally {
            lock.unlock();
        }
    }

    // Fonction pour obtenir l'heure actuelle
    public long getCurrentTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    public void reset() {
        startTimer();
    }
}
