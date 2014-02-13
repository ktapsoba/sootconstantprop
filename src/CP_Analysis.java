import java.util.HashMap;
import java.util.Map;

import resources.Resource;
import resources.State;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PackManager;
import soot.Transform;

import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class CP_Analysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		(new CP_Analysis()).process(args);
		
	}
	
	public void process(String[] args){
		Map<Local, Constant> out;
		PackManager.v().getPack("jtp")
		.add(new Transform("jtp.myTransform", new BodyTransformer() {

			protected void internalTransform(Body body, String phase, Map options) {
				UnitGraph unitGraph = new ExceptionalUnitGraph(body);
				//constantprop cp_prop = new constantprop(unitGraph);
				constantpropState cp_propState = new constantpropState(unitGraph);
			}

		}));
		soot.Main.main(args);
	}

	public class constantprop extends ForwardFlowAnalysis<Unit, Map<Local, Constant>> {
		private final Local RETURN_LOCAL = new JimpleLocal("@return", null);
		DirectedGraph<Unit> graph;

		public constantprop(DirectedGraph<Unit> graph) {
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
	
	public class constantpropState extends ForwardFlowAnalysis<Unit, Map<Local, State>>{
		private Resource resource;
		public constantpropState(DirectedGraph<Unit> graph) {
			super(graph);
			this.graph = graph;
			resource = new Resource();
			/*Iterator it = ((UnitGraph)graph).iterator();
			while(it.hasNext()){
				
			}*/
			// TODO Auto-generated constructor stub
			doAnalysis();
		}
		
		private void assign(Local lhs, Value rhs, Map<Local, State> input, Map<Local, State> output) {
	       
	     }
		
		@Override
		protected void flowThrough(Map<Local, State> input, Unit unit, Map<Local, State> output) {
			copy(input, output);
			// Only statements assigning locals matter
			Stmt stmt = (Stmt)unit;
			G.v().out.println("stmt------" + stmt.toString());
			if (stmt instanceof AssignStmt){
				Value lhs = ((AssignStmt) stmt).getLeftOp();
				Value rhs = ((AssignStmt) stmt).getRightOp();
				
				if(lhs instanceof Local){
					if(stmt.containsInvokeExpr()){
						InvokeExpr invokeExpr = stmt.getInvokeExpr();
						String methodname = invokeExpr.getMethod().getName();
						if (resource.containsMethod(methodname)){
							//TODO: evaluate method and assign to lhs
							// 
						}
					}
				}
			}
		}

		@Override
		protected void copy(Map<Local, State> input, Map<Local, State> output) {
			// TODO Auto-generated method stub
			for (Local key : input.keySet()){
				output.put(key, input.get(key));
			}
		}

		@Override
		protected Map<Local, State> entryInitialFlow() {
			// TODO Auto-generated method stub
			return new HashMap<Local, State>();
		}

		@Override
		protected void merge(Map<Local, State> input1, Map<Local, State> input2, Map<Local, State> output) {
			// TODO Auto-generated method stub
			//Map<Local, Constant> result;
	         // First add everything in the first operand
	         copy(input1,output);
	         //FlowSet l = null;
	         //l.add(op1);

	         // Then add everything in the second operand, bottoming out the common keys with different values
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
}
