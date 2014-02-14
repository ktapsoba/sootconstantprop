package analysis;

import java.util.HashMap;
import java.util.Map;

import resources.Resource;
import resources.State;
import soot.G;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class ConstantPropState extends ForwardFlowAnalysis<Unit, Map<Local, State>> {
	private final Resource resource = new Resource();

	public ConstantPropState(DirectedGraph<Unit> graph) {
		super(graph);
		this.graph = graph;
		/*
		 * Iterator it = ((UnitGraph)graph).iterator(); while(it.hasNext()){
		 * 
		 * }
		 */
		// TODO Auto-generated constructor stub
		doAnalysis();
	}

	private void assign(Local lhs, Value rhs, Map<Local, State> input, Map<Local, State> output) {

	}

	@Override
	protected void flowThrough(Map<Local, State> input, Unit unit, Map<Local, State> output) {
		copy(input, output);
		// Only statements assigning locals matter
		Stmt stmt = (Stmt) unit;
		G.v().out.println("stmt------" + stmt.toString());
		if (stmt instanceof AssignStmt) {
			Value lhs = ((AssignStmt) stmt).getLeftOp();
			Value rhs = ((AssignStmt) stmt).getRightOp();

			if (lhs instanceof Local) {
				if (stmt.containsInvokeExpr()) {
					InvokeExpr invokeExpr = stmt.getInvokeExpr();
					String methodname = invokeExpr.getMethod().getName();
					if (resource.containsMethod(methodname)) {
						// TODO: evaluate method and assign to lhs
						//
					}
				}
	}
		}
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
		// TODO Auto-generated method stub
		return new HashMap<Local, State>();
	}

	@Override
	protected void merge(Map<Local, State> input1, Map<Local, State> input2,
			Map<Local, State> output) {
		// TODO Auto-generated method stub
		// Map<Local, Constant> result;
		// First add everything in the first operand
		copy(input1, output);
		// FlowSet l = null;
		// l.add(op1);

		// Then add everything in the second operand, bottoming out the common
		// keys with different values
		for (Local x : input2.keySet()) {
			if (input1.containsKey(x)) {
				// Check the values in both operands
				State c1 = input1.get(x);
				State c2 = input2.get(x);
				if (c1 != null && c1.equals(c2) == false) {
					// Set to non-constant
					output.put(x, new State("null"));
				}
			} else {
				// Only in second operand, so add as-is
				output.put(x, input2.get(x));
			}
		}
	}

	@Override
	protected Map<Local, State> newInitialFlow() {
		// TODO Auto-generated method stub
		return new HashMap<Local, State>();
}

}