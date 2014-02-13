package resources;

import java.util.Objects;

public class State {
	
	String name;
	
	public static State getTop(){
		return new State("TOP");
	}
	public static State getBottom(){
		return new State("BOTTOM");
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
	
	public boolean equals(State state){
		return this.name.equals(state.getName());
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}
}
