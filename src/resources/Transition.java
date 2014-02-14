package resources;

import java.util.Objects;

public class Transition {
	
	private State state1;
	private State state2;
	private Method method;
	
	public Transition(State st1, State st2, Method mthd){
		state1 = st1;
		state2 = st2;
		method = mthd;
	}
	
	public Method getMethod() {
		return method;
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
