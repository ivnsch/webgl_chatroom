

public class Player {
	public int id;
	public String name;
	public float x;
	public float y;
	public float z;
	
	public String serialize() {
		return id + " " + name + " " + x + " " + y + " " +	z;
	}

	public Player(int id, String name, float x, float y, float z) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}