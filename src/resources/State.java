package resources;


public class State {
	
	private String name;
	static State top = new State("TOP"), bot = new State("BOTTOM"), NULL = new State("NULL"),
			unknown  = new State("UNKNOWN");
	
	
	public static State getTop(){
		return top;
	}
	public static State getBottom(){
		return bot;
	}
	
	public static State getNull(){
		return NULL;
	}
	
	public static State getUnknown(){
		return unknown;
	}
	
	public State(String name){
		this.name = name;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object state){
		boolean ret = false;
		
		if (state instanceof State) {
			ret =  this.name.compareTo(((State) state).getName()) == 0;
		}
		
		return ret;
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
}
