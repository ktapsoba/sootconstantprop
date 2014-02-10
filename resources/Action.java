package resources;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

public class Action {
	State state;
	Set<Method> allowed;
	Set<Method> notAllowed;
	
	public Action (State st){
		state = st;
	}
	
	public Action(State st, Set<Method> alwd, Set<Method> ntalwd){
		state = st;
		allowed = alwd;
		notAllowed = ntalwd;
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
	
	public boolean isAllowed(Method method){
		return allowed.contains(method);
	}
	
	public boolean isNotAllowed(Method method){
		return notAllowed.contains(method);
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(state.getName() + "allowed" + "notAllowed");
	}
}