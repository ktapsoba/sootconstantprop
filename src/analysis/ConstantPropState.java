package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import resources.Resource;
import resources.State;
import soot.G;
import soot.Local;
import soot.PatchingChain;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class ConstantPropState extends ForwardFlowAnalysis<Unit, Map<Local, State>> {
	private final Resource resource = new Resource();
	private Visitor visitor;
	private Map <Unit, Map<Local, State>> statesByUnitsIn;
	private Map <Unit, Map<Local, State>> statesByUnitsOut;
	private Map<Unit, List<String>> errorsByUnits;

	public ConstantPropState(DirectedGraph<Unit> graph) {
		super(graph);
		this.graph = graph;
		
		// Initialize States
		initializeStates();
		doAnalysis();
		printResults();
		//checkUnitsState();
	}
	
	private void initializeStates(){
		UnitGraph unitGraph = (UnitGraph) graph;
		visitor = new Visitor(resource, unitGraph.getBody().getLocals());
		statesByUnitsIn = new HashMap<Unit, Map<Local, State>>();
		statesByUnitsOut = new HashMap<Unit, Map<Local, State>>();
	}

	@Override
	protected void flowThrough(Map<Local, State> input, Unit unit, Map<Local, State> output) {
		Map<Local, State> statesByUnitInLocalMap = getOutput(input);
		statesByUnitsIn.put(unit, statesByUnitInLocalMap);
		
		copy(input, output);
		Stmt stmt = (Stmt) unit;
		visitor.dispatch(stmt, input, output);
		
		Map<Local, State> statesByUnitOutLocalMap = getOutput(output);
		statesByUnitsOut.put(unit, statesByUnitOutLocalMap);
	}

	@Override
	protected void copy(Map<Local, State> input, Map<Local, State> output) {
		for (Local key : input.keySet()) {
			output.put(key, input.get(key));
		}
	}

	@Override
	protected Map<Local, State> entryInitialFlow() {
		// Entry state for the analysis. Should be empty state
		return new HashMap<Local, State>();
	}

	@Override
	protected void merge(Map<Local, State> input1, Map<Local, State> input2, Map<Local, State> output) {
		// First add everything in the first operand
		copy(input1, output);

		// Then add everything in the second operand, bottoming out the common
		// keys with different values
		for (Local x : input2.keySet()) {
			if (input1.containsKey(x)) {
				// Check the values in both operands
				State c1 = input1.get(x);
				State c2 = input2.get(x);
				//they have the same state
				if (c1.equals(c2)){
					output.put(x, c2);
				}
			}
		}
	}

	@Override
	protected Map<Local, State> newInitialFlow() {
		return new HashMap<Local, State>();
	}
	
	private void checkUnitsState(){
		//we get the units in the correct order
		UnitGraph unitGraph = (UnitGraph)this.graph;
		PatchingChain<Unit> units = unitGraph.getBody().getUnits();
		errorsByUnits = new HashMap<Unit, List<String>>();
		for(Unit unit : units){
			//G.v().out.println(unit.toString());
			Map<Local, List<State>> mapOfValidStatesByLocals = resource.getMapOfValidStatesByLocals(statesByUnitsIn.get(unit));
			List<String> errorMessages = new ArrayList<String>();
			//check if each local going from Input to output has valid output State
			for(Local local : mapOfValidStatesByLocals.keySet()){
				boolean isValid = checkStateByLocal(mapOfValidStatesByLocals.get(local), statesByUnitsOut.get(unit).get(local));
				if (!isValid){
					String message = "Error at Unit " + unit.toString() + "\nInvalid State for local " + local;
					message += "\nState found is " + statesByUnitsOut.get(unit).get(local) 
							+ ". It should be one of the following: " + mapOfValidStatesByLocals.get(local) + "!";
					errorMessages.add(message);
				}
			}
			if (!errorMessages.isEmpty()){
				errorsByUnits.put(unit, errorMessages);
			}
		}
		
		if (!errorsByUnits.isEmpty()){
			G.v().out.println("Errors found!");
			for(Unit unit : errorsByUnits.keySet()){
				G.v().out.println("unit: " + unit.toString());
				for(String error: errorsByUnits.get(unit)){
					G.v().out.println(error);
				}
			}
		}
		else {
			G.v().out.println("SUCCESS -- No Errors");
		}
	}
	
	private boolean checkStateByLocal(List<State> validStatesByLocal, State stateOut){
		if (stateOut == null)
			return true;
		return validStatesByLocal.contains(stateOut);
	}
	
	private Map<Local, State> getOutput(Map<Local, State> output){
		Map<Local, State> newMap = new HashMap<Local, State>();
		for(Local local : output.keySet()){
			newMap.put(local, output.get(local));
		}
		return newMap;
	}
	
	private void printResults(){
		UnitGraph unitGraph = (UnitGraph)this.graph;
		PatchingChain<Unit> units = unitGraph.getBody().getUnits();
		for(Unit unit : units){
			G.v().out.println("\nUnit : " + unit);
			G.v().out.println("Before " + getStateByLocal(statesByUnitsIn.get(unit)));
			G.v().out.println("After " + getStateByLocal(statesByUnitsOut.get(unit)));
			/*for(Local local : statesByUnitsOut.get(unit).keySet()){
				G.v().out.println(" [" + local + "->" + statesByUnitsOut.get(unit).get(local) + "]");
			}*/
		}
	}
	
	private String getStateByLocal(Map<Local, State> localByStateMap){
		String ret = "";
		for(Local local : localByStateMap.keySet()){
			ret += "[" + local + "->" + localByStateMap.get(local).toString() + "]";
		}
		return localByStateMap.toString();
	}

}