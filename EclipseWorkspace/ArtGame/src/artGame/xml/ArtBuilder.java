package artGame.xml;

import artGame.game.Art;

public class ArtBuilder implements ObjectBuilder {
	
	private String artName;
	private int value;
	private int artID;
	
	public ArtBuilder(int artID){
		this.artID = artID;
	}
	
	@Override
	public void addFeild(String name, String value) {
		if(name.equals(XMLReader.NAME_ELEMENT)){
			this.artName = value;
		} else if (name.equals(XMLReader.VALUE_ELEMENT)){
			this.value = Integer.parseInt(value);
		}

	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}
	
	public int getArtID() {
		return artID;
	}

	@Override
	public Art buildObject() {
		return new Art(artName, value, artID);
	}

}
