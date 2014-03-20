package resource;

import java.util.ArrayList;
import java.util.HashMap;

import soot.Local;
import soot.Value;

abstract class StateType {
	final String name;
	
	StateType(String name){ this.name = name; }
	abstract StateType lub(StateType stateType);
	abstract boolean lessThan(StateType stateType);
	boolean isTop() {return false;}
	boolean isBottom() {return false;}
	boolean isNotConnectd(){ return false; }
	boolean isConnected() { return false; }
	boolean isResult() { return false; }
	boolean isStatement() {return false;}
	
	boolean isLoggedIn() {return false;}
	boolean isLoggedOut() {return false;}
	
	public String toString(){ return name; }
}

class Top extends StateType{
	static private Top top = new Top();
	
	private Top(){ super("top"); }
	static Top getTop() { return top; }
	StateType lub(StateType stateType) { return this; }
	boolean lessThan(StateType stateType) { return this == stateType; }
	boolean isTop() { return true; }

}

class Bottom extends StateType{
	static private Bottom bottom = new Bottom();
	
	private Bottom(){ super("bottom"); }
	static Bottom getBottom() { return bottom; }
	StateType lub(StateType stateType) { return stateType; }
	boolean lessThan(StateType stateType) { return true; }
	boolean isBottom() { return true; }
	
}

class NotConnected extends StateType {
	static private NotConnected notConnected = new NotConnected();
	
	private NotConnected(){ super("not Connected"); }
	static NotConnected getNotConnected() { return notConnected; }
	boolean isNotConnected() { return true; }
	StateType lub(StateType stateType) { 
		if (stateType.isTop() || stateType.isResult() || stateType.isConnected()) return stateType;
		else if (stateType.isBottom()) return this;
		else return this;
	}
	
	boolean lessThan(StateType stateType) { 
		if (stateType.isTop() || stateType.isResult() || stateType.isConnected()) return true;
		else if (stateType.isBottom()) return false;
		return true;
	}
}

class Connected extends StateType {
	static private Connected connected = new Connected();
	
	private Connected(){ super("connected"); }
	static Connected getConnected() { return connected; }
	boolean isConnected() { return true; }
	
	StateType lub(StateType stateType) { 
		if (stateType.isTop() || stateType.isResult()) return stateType;
		else if (stateType.isBottom() || stateType.isNotConnectd()) return this;
		else return this;
	}
	boolean lessThan(StateType stateType) { 
		if (stateType.isTop() || stateType.isResult()) return true;
		else if (stateType.isNotConnectd() || stateType.isBottom()) return false;
		else return true;
	}
}

class Statement extends StateType {
static private Statement statement = new Statement();
	
	private Statement(){ super("statement"); }
	static Statement getStatement() { return statement; }
	boolean isStatement() { return true; }
	
	StateType lub(StateType stateType) { 
		if (stateType.isTop() || stateType.isResult()) return stateType;
		else if (stateType.isBottom() || stateType.isNotConnectd() || stateType.isConnected()) return this;
		else return this;
	}
	boolean lessThan(StateType stateType) { 
		if (stateType.isTop() || stateType.isResult()) return true;
		else if (stateType.isBottom() || stateType.isNotConnectd() || stateType.isConnected()) return false;
		else return true;
	}
}

class Result extends StateType {
	static private Result result = new Result();
	
	private Result(){ super("result"); }
	static Result getResult() { return result; }
	boolean isResult() { return true; }
	
	StateType lub(StateType stateType) { 
		if (stateType.isTop()) return stateType;
		else if (stateType.isBottom() || stateType.isNotConnectd() || stateType.isConnected() || stateType.isStatement()) return this;
		else return this;
	}
	boolean lessThan(StateType stateType) { 
		if (stateType.isTop()) return true;
		else if (stateType.isBottom() || stateType.isNotConnectd() || stateType.isConnected()) return false;
		else return true;
	}
}

class LoggedIn extends StateType{
	static private LoggedIn loggedIn = new LoggedIn();
	
	private LoggedIn() {super("logged in");}
	static LoggedIn getLoggedIn() { return loggedIn; }
	boolean isLoggedIn() { return true;}
	
	StateType lub(StateType stateType){
		if (stateType.isTop()) return stateType;
		else return this;
	}
	boolean lessThan(StateType stateType){
		if(stateType.isTop()) return true;
		else if (stateType.isBottom() || stateType.isNotConnectd() || stateType.isConnected()) return false;
		else return true;
	}
}

class LoggedOut extends StateType {
	static private LoggedOut loggedOut = new LoggedOut();
	
	private LoggedOut() { super("logged out"); }
	static LoggedOut getLoggedOut() { return loggedOut; }
	boolean isLoggedOut() {return true;}
	
	StateType lub(StateType stateType){
		if(stateType.isTop() || stateType.isLoggedIn()) return stateType;
		else return this;
	}
	boolean lessThan(StateType stateType){
		if(stateType.isTop() || stateType.isLoggedIn()) return true;
		else return false;
	}
}

class InvalidState extends StateType{
	static private InvalidState invalidState = new InvalidState();
	
	private InvalidState(){ super("invalid State"); }
	static InvalidState getInvalidState() { return invalidState; }
	boolean isInvalidState() { return true; }
	
	StateType lub(StateType stateType) { 
		if (stateType.isTop() || stateType.isBottom() || stateType.isNotConnectd() || stateType.isConnected() || stateType.isStatement()) return stateType;
		else return this;
	}
	boolean lessThan(StateType stateType) { 
		return true;
	}
}


public class State extends HashMap<Local, StateType> {
	private static final long serialVersionUID = 1L;
	
	final StateType stateType;
	static StateType getTopState() { return Top.getTop(); }
	static StateType getBottomState() { return Bottom.getBottom(); }
	static StateType getInvalidState() { return InvalidState.getInvalidState(); }
	
	private State(State state) { 
		super(state);
		this.stateType = state.stateType;
	}
	
	public State(){
		super();
		this.stateType = getBottomState();
	}
	
	public State lub(State state){
		ArrayList<Local> locals = new ArrayList<Local>();
		locals.addAll(this.keySet());
		locals.addAll(state.keySet());
		State newState = new State(this);
		
		for(Local local : locals){
			newState.put(local, this.get(local).lub(state.get(local)));
		}
		return newState;
	}
	
	StateType getDefault() { return stateType; }
	
	public String toString() {
		String value = "[";
		for(Local local : keySet()){
			value += local.toString() + "->" + get(local).toString() + ", ";
		}
		value +="]";
		return value;
	}
	
	
	public StateType put(Local local, StateType stateType){
		return super.put(local, stateType);
	}
	
	public StateType get(Object object){
		StateType type = stateType;
		if (object instanceof Local){
			Local local = (Local)object;
			if(this.containsKey(local)){
				type = super.get(local);
			}
		}
		return type;
	}
	
	@Override
	public boolean containsKey(Object object){
		if(object instanceof Value){
			return containsLocal((Value)object);
		}
		return false;
	}
	
	private boolean containsLocal(Value value){
		for(Local local : this.keySet()){
			if(local.getName().equals(value.toString()))
				return true;
		}
		return false;
	}
	 
}

class ContextualState extends HashMap<Local, HashMap<Integer, StateType>>{
	private static final long serialVersionUID = 1L;
	
	
	final StateType stateType;
	static StateType getTopState() { return Top.getTop(); }
	static StateType getBottomState() { return Bottom.getBottom(); }
	static StateType getInvalidState() { return InvalidState.getInvalidState(); }
	
	private ContextualState(ContextualState ctxState) { 
		super(ctxState);
		this.stateType = ctxState.stateType;
	}
	
	public ContextualState(){
		super();
		this.stateType = getBottomState();
	}
	
	public HashMap<Integer, StateType> put(Local local, StateType stateType, int ctx){
		HashMap<Integer, StateType> newStateType = new HashMap<>();
		newStateType.put(ctx, stateType);
		return super.put(local, newStateType);
	}
}
