package resources;

import java.util.Objects;

public class Transition {
	
	State state1;
	State state2;
	Method method;
	
	public Transition(State st1, State st2, Method mthd){
		state1 = st1;
		state2 = st2;
		method = mthd;
	}
	
	public String getName(){
		return "From " + state1.toString() + " to " + state2.toString();
	}
	
	public String toString(){
		return "Transition: " + state1.toString() + " to " + state2.toString() + " --> " + method.toString();
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(getName());
	}
}
