

public class FeuTricolore {
	private int x;
	private int y;
	private boolean nord,sud,est,ouest;
	
	public FeuTricolore(int x, int y, boolean nord,boolean sud, boolean est, boolean ouest) {
		this.x = x;
		this.y = y;
		this.nord = nord;
		this.sud = sud;
		this.est = est;
		this.ouest = ouest;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean getNord() {
		return nord;
	}

	public void setNord(boolean nord) {
		this.nord = nord;
	}

	public boolean getSud() {
		return sud;
	}

	public void setSud(boolean sud) {
		this.sud = sud;
	}

	public boolean getEst() {
		return est;
	}

	public void setEst(boolean est) {
		this.est = est;
	}

	public boolean getOuest() {
		return ouest;
	}

	public void setOuest(boolean ouest) {
		this.ouest = ouest;
	}
}
