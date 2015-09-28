package artGame.xml;

import artGame.game.Sculpture;

public class SculptureBuilder extends CharacterBuilder {
	
	private String artName;
	private int value;
	
	public SculptureBuilder(int artID){
		super(artID);
	}
	
	@Override
	public void addFeild(String name, String value) {
		super.addFeild(name, value);
		if(name.equals(XMLReader.NAME_ELEMENT)){
			this.artName = value;
		} else if (name.equals(XMLReader.VALUE_ELEMENT)){
			this.value = Integer.parseInt(value);
		} 
	}

	@Override
	public Sculpture buildObject() {
		Sculpture sculpture = new Sculpture(super.getDirection(), super.getID(), value, artName);
		sculpture.setCol(super.getCoord().getX());
		sculpture.setRow(super.getCoord().getY());
		return sculpture;
	}

}
