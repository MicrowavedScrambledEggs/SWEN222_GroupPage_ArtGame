package artGame.xml.load;

public interface BuildStrategy {
	
	public void addField(String name, Object... values) throws IllegalArgumentException;
	
	public void addToGame();
	
}
