package analysis;

import java.util.HashMap;
import java.util.Map;

import resources.Resource;
import resources.State;
import soot.G;
import soot.Local;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class ConstantPropState extends ForwardFlowAnalysis<Unit, Map<Local, State>> {
	private final Resource resource = new Resource();
	private Visitor visitor;
	private Map <Unit, Map<Local, State>> statesByUnits;

	public ConstantPropState(DirectedGraph<Unit> graph) {
		super(graph);
		this.graph = graph;
		// Initialize States
		
		initializeStates();
		doAnalysis();
		printResults();
	}
	
	private void initializeStates(){
		UnitGraph unitGraph = (UnitGraph) graph;
		visitor = new Visitor(resource, unitGraph.getBody().getLocals());
		statesByUnits = new HashMap<Unit, Map<Local, State>>();
	}

	@Override
	protected void flowThrough(Map<Local, State> input, Unit unit, Map<Local, State> output) {
		copy(input, output);
		Stmt stmt = (Stmt) unit;
		//G.v().out.println("output before visit -- " + output);
		visitor.dispatch(stmt, input, output);
		//G.v().out.println("after before visit -- " + output);
		
		Map<Local, State> statesByUnitLocalMap = getOutput(output);
		statesByUnits.put(unit, statesByUnitLocalMap);
	}

	@Override
	protected void copy(Map<Local, State> input, Map<Local, State> output) {
		// TODO Auto-generated method stub
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
				//G.v().out.printf("c1: {} and c2: {} are in the both inputs", c1.toString(), c2.toString());
				//they have the same state
				if (c1.equals(c2)){
					output.put(x, c2);
				}
				else {
					//G.v().out.printf("c1: {} and c2: {} are not the same", c1.toString(), c2.toString());
				}
			}
		}
	}

	@Override
	protected Map<Local, State> newInitialFlow() {
		// TODO Auto-generated method stub
		return new HashMap<Local, State>();
	}
	
	private Map<Local, State> getOutput(Map<Local, State> output){
		Map<Local, State> newMap = new HashMap<Local, State>();
		for(Local local : output.keySet()){
			newMap.put(local, output.get(local));
		}
		return newMap;
	}
	
	private void printResults(){
		for(Unit unit : statesByUnits.keySet()){
			G.v().out.println("\nUnit : " + unit);
			for(Local local : statesByUnits.get(unit).keySet()){
				G.v().out.println(" [" + local + "->" + statesByUnits.get(unit).get(local) + "]");
			}
		}
	}

}