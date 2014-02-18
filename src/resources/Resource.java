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
	private Map<Method, State> StateByMethod;
	
	public Resource(){
		states = new HashMap<String, State>();
		transitions = new HashMap<String, Set<Transition>>();
		singleTransitions = new HashMap<String, Transition>();
		actions = new HashMap<State, Action>();
		methods = new HashMap<String, Method>();
		StateByMethod = new HashMap<Method, State>();
		
		loadResources();
	}
	
	public boolean isValidState(State state){
		return containsState(state.getName());
	}
	
	public boolean containsState(String stateName){
		return states.containsKey(stateName);
	}
	
	public State getStateByMethodName(String methodName){
		Method method = methods.get(methodName);
		if (method == null)
			return State.getUnknown();
		
		return StateByMethod.get(method);
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
		loadStates();
		loadMethods();
		loadActions();
		loadTransitions();
	}
	
	private void loadStates(){
		State notConnected = new State("NotConnected");
		State connected = new State("Connected");
		State statement = new State("Statement");
		State query = new State("Query");
		State result = new State("Result");
		State TOP = State.getTop();
		State BOTTOM = State.getBottom();
		State close = new State("Close");

		states.put(notConnected.getName(), notConnected);
		states.put(connected.getName(), connected);
		states.put(statement.getName(), statement);
		states.put(query.getName(), query);
		states.put(result.getName(), result);
		states.put(TOP.getName(), TOP);
		states.put(BOTTOM.getName(), BOTTOM);
		states.put(close.getName(), close);
	}
	
	private void loadTransitions(){
		Transition transNullToConnected = new Transition(State.getNull(), states.get("Connected"), methods.get("getConnection"));
		Transition transConnected = new Transition(states.get("NotConnected"), states.get("Connected"), methods.get("getConnection"));
		Transition transStatement = new Transition(states.get("Connected"), states.get("Statement"), methods.get("createStatement"));
		Transition transResult = new Transition(states.get("Statement"), states.get("Result"), methods.get("executeQuery"));

		singleTransitions.put(transNullToConnected.getName(), transNullToConnected);
		singleTransitions.put(transConnected.getName(), transConnected);
		singleTransitions.put(transStatement.getName(), transStatement);
		singleTransitions.put(transResult.getName(), transResult);
	}
	
	private void loadMethods(){
		Method getConnection = new Method("Connection", "getConnection");
		Method createStatement = new Method("Connection", "createStatement");
		Method executeQuery = new Method("Statement", "executeQuery");
		Method closeConnection = new Method("Connection", "close");
		Method closeStatement = new Method("Statement", "close");
		
		methods.put(getConnection.getName(), getConnection);
		methods.put(createStatement.getName(), createStatement);
		methods.put(executeQuery.getName(), executeQuery);
		methods.put(closeConnection.getName(), closeConnection);
		methods.put(closeStatement.getName(), closeStatement);
		
		StateByMethod.put(getConnection, states.get("Connected"));
		StateByMethod.put(createStatement, states.get("Statement"));
		StateByMethod.put(executeQuery, states.get("Result"));
		StateByMethod.put(closeConnection, states.get("Close"));
		StateByMethod.put(closeStatement, states.get("Close"));
	}
	
	private void loadActions(){
		
	}
}
