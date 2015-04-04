import java.io.Serializable;

@SuppressWarnings("serial")
public class Waypoint implements Serializable {
	private int posX, posY, id, normalizer;
	private String floor, name;
	
	public Waypoint(int x, int y, int id, String floor, String name, int normalizer){
		posX = x;
		posY = y;
		this.id = id;
		this.floor = floor;
		this.name = name;
		this.normalizer = normalizer;
	}
	
	public int getX(){
		return posX;
	}
	
	public int getY(){
		return posY;
	}
	
	public int getId(){
		return id;
	}
	
	public String getFloor(){
		return floor;
	}
	
	public String getName(){
		return name;
	}
	
	public int getNormalizer(){
		return normalizer;
	}

}
