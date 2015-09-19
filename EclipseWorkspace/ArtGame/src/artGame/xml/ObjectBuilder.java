package artGame.xml;

public interface ObjectBuilder {
	
	public void addFeild(String name, String value);
	
	public void addFeild(String name, Object value) throws IllegalArgumentException;
	
	public <T> T buildObject();
}
