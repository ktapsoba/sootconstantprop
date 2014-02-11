package resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Resource {
	
	Map<String, State> states;
	Map<String, Set<Transition>> transitions;
	Map<State, Action> actions;
	Map<String, Method> methods;
	
	public Resource(){
		states = new HashMap<String, State>();
		transitions = new HashMap<String, Set<Transition>>();
		actions = new HashMap<State, Action>();
		methods = new HashMap<String, Method>();
		
		loadResources();
	}
	
	private void loadResources(){
		loadMethods();
		loadStates();
		loadActions();
		loadTransitions();
	}
	
	private void loadStates(){
		State notConnected = new State("NotConnected");
		State connected = new State("Connected");
		State query = new State("Query");
		State result = new State("Result");

		states.put(notConnected.getName(), notConnected);
		states.put(connected.getName(), connected);
		states.put(query.getName(), query);
		states.put(result.getName(), result);
	}
	
	private void loadTransitions(){
		
	}
	
	private void loadMethods(){
		
	}
	
	private void loadActions(){
		
	}
}
