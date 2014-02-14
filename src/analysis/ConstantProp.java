package analysis;

import java.util.HashMap;
import java.util.Map;

import soot.G;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.ReturnStmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class ConstantProp extends ForwardFlowAnalysis<Unit, Map<Local, Constant>> {
	private final Local RETURN_LOCAL = new JimpleLocal("@return", null);
	DirectedGraph<Unit> graph;

	public ConstantProp(DirectedGraph<Unit> graph) {
		super(graph);
		this.graph = graph;
		doAnalysis();
	}

	private void assign(Local lhs, Value rhs, Map<Local, Constant> input, Map<Local, Constant> output) {
         // First remove casts, if any.
         if (rhs instanceof CastExpr) {
             rhs = ((CastExpr) rhs).getOp();
         }
         // Then check if the RHS operand is a constant or local
         if (rhs instanceof Constant) {
             // If RHS is a constant, it is a direct gen
             output.put(lhs, (Constant) rhs);
         } else if (rhs instanceof Local) {
             // Copy constant-status of RHS to LHS (indirect gen), if exists
             if(input.containsKey(rhs)) {
                 output.put(lhs, input.get(rhs));
             }
         } else {
             // RHS is some compound expression, then LHS is non-constant (only kill)
             output.put(lhs, null);
         }
     }

	@Override
	protected void flowThrough(Map<Local, Constant> inValue, Unit unit, Map<Local, Constant> outValue) {
		G.v().out.println();
		G.v().out.println("invalue=" + inValue);
		copy(inValue, outValue);
		// Only statements assigning locals matter
		if (unit instanceof AssignStmt) {
			// Get operands
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Value rhsOp = ((AssignStmt) unit).getRightOp();
			if (lhsOp instanceof Local) {
				assign((Local) lhsOp, rhsOp, inValue, outValue);
			}
		} else if (unit instanceof ReturnStmt) {
			// Get operand
			Value rhsOp = ((ReturnStmt) unit).getOp();
			assign(RETURN_LOCAL, rhsOp, inValue, outValue);
		}
		// Return the data flow value at the OUT of the statement
		G.v().out.println("outvalue=" + outValue);
	}

	@Override
     protected void merge(Map<Local, Constant> op1, Map<Local, Constant> op2, Map<Local, Constant> out) {
         //Map<Local, Constant> result;
         // First add everything in the first operand
         copy(op1,out);
         //FlowSet l = null;
         //l.add(op1);

         // Then add everything in the second operand, bottoming out the common keys with different values
         for (Local x : op2.keySet()) {
             if (op1.containsKey(x)) {
                 // Check the values in both operands
                 Constant c1 = op1.get(x);
                 Constant c2 = op2.get(x);
                 if (c1 != null && c1.equals(c2) == false) {
                     // Set to non-constant
                     out.put(x, null);
                 }
             } else {
                 // Only in second operand, so add as-is
                 out.put(x, op2.get(x));
             }
         }

     }

	@Override
	protected void copy(Map<Local, Constant> source, Map<Local, Constant> dest) {
		for (Local key : source.keySet()){
			dest.put(key, source.get(key));
		}
		//dest = new HashMap<Local, Constant>(source);
	}

	@Override
	protected Map<Local, Constant> entryInitialFlow() {
		return new HashMap<Local, Constant>();
	}

	@Override
	protected Map<Local, Constant> newInitialFlow() {
		return new HashMap<Local, Constant>();
	}

}