package artGame.xml.load;

public class ObjectBuilder {
	
	private BuildStrategy buildStrat;
	
	public ObjectBuilder(BuildStrategy bs){
		buildStrat = bs;
	}
	
	public void addField(String name, Object... values) throws IllegalArgumentException{
		buildStrat.addField(name, values);
	}
	
	public void addToGame(){
		buildStrat.addToGame();
	}
	
	public BuildStrategy getBuildStrategy(){
		return buildStrat;
	}
	
}
