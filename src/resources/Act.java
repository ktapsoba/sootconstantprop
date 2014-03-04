package resources;

import java.util.Objects;
import java.util.Set;

public class Act {
	//This will be now a map
	private State state;
	private Set<Method> allowed;
	private Set<Method> notAllowed;
	
	public Act (State st){
		state = st;
	}
	
	public Act(State state, Set<Method> alwd, Set<Method> ntalwd){
		this.state = state;
		allowed = alwd;
		notAllowed = ntalwd;
	}
	
	public boolean isMethodAllowed(State state, Method method){
		return allowed.contains(method);
	}
	public boolean isMethodNotAllowed(Method method){
		return notAllowed.contains(method);
	}
	
	public String toString(){
		return "State: " + state.toString() + " Allowed: " + allowed.toString() + " Not Allowed:" + notAllowed.toString();
	}
	
	public void AddAllowed(Method method){
		allowed.add(method);
	}
	
	public void AddNotAllowed(Method method){
		notAllowed.add(method);
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(state.getName() + "allowed" + "notAllowed");
	}
}