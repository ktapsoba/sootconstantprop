package resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Resource {
	
	private Map<String, State> states;
	private Map<String, Set<Transition>> transitions;
	private Map<String, Transition> singleTransitions;
	private Map<State, Action> actions;
	private Map<String, Method> methods;
	
	public Resource(){
		states = new HashMap<String, State>();
		transitions = new HashMap<String, Set<Transition>>();
		singleTransitions = new HashMap<String, Transition>();
		actions = new HashMap<State, Action>();
		methods = new HashMap<String, Method>();
		
		loadResources();
	}
	
	public boolean containsState(String stateName){
		return states.containsKey(stateName);
	}
	public State getState(String name){
		return states.get(name);
	}

	public boolean constainsTransition(String name){
		return singleTransitions.containsKey(name);
	}
	public Transition getSingleTransition(String name) {
		return singleTransitions.get(name);
	}

	public boolean containsMethod(String name){
		return methods.containsKey(name);
	}
	public Method getMethod(String name) {
		return methods.get(name);
	}
	
	public boolean isMethodCallLegal(State state, Method method){
		return (actions.get(state)).isMethodAllowed(method);
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
		State statement = new State("Statement");
		State query = new State("Query");
		State result = new State("Result");

		states.put(notConnected.getName(), notConnected);
		states.put(connected.getName(), connected);
		states.put(statement.getName(), statement);
		states.put(query.getName(), query);
		states.put(result.getName(), result);
	}
	
	private void loadTransitions(){
		Transition transConnected = new Transition(states.get("NotConnected"), states.get("Connected"), methods.get("getConnection"));
		Transition transStatement = new Transition(states.get("Connected"), states.get("Statement"), methods.get("createStatement"));
		Transition transResult = new Transition(states.get("Statement"), states.get("Result"), methods.get("executeQuery"));
		
		singleTransitions.put(transConnected.getName(), transConnected);
		singleTransitions.put(transStatement.getName(), transStatement);
		singleTransitions.put(transResult.getName(), transResult);
	}
	
	private void loadMethods(){
		Method getConnection = new Method("Connection", "getConnection");
		Method createStatement = new Method("Connection", "createStatement");
		Method executeQuery = new Method("Statement", "executeQuery");
		
		methods.put(getConnection.getName(), getConnection);
		methods.put(createStatement.getName(), createStatement);
		methods.put(executeQuery.getName(), executeQuery);
	}
	
	private void loadActions(){
		
	}
}
