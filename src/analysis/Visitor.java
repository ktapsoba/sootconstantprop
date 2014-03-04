package analysis;

import java.util.List;
import java.util.Map;

import resources.Resource;
import resources.State;
import soot.G;
import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.util.Chain;

public class Visitor {
	Map<Local, State> input;
	Map<Local, State> output;
	private Resource resource;
	private Chain<Local> locals;
	
	public Visitor(Resource resource, Chain<Local> locals){
		this.resource = resource;
		this.locals = locals;
	}
	
	public void dispatch(Stmt stmt, Map<Local, State> input, Map<Local, State> output){
		this.input = input;
		this.output = output;
		dispatch(stmt);
	}
	
	private void dispatch(Stmt stmt){
		
		if (stmt instanceof IdentityStmt){
			visit((IdentityStmt) stmt);
		}
		else if (stmt instanceof AssignStmt){
			visit((AssignStmt) stmt);
		}
		else if (stmt instanceof InvokeStmt){
			visit((InvokeStmt) stmt);
		}
		else if (stmt instanceof IfStmt){
			visit((IfStmt) stmt);
		}
		else if (stmt instanceof GotoStmt){
			visit((GotoStmt) stmt);
		}
		else if (stmt instanceof TableSwitchStmt){
			visit((TableSwitchStmt) stmt);
		}
		else if (stmt instanceof LookupSwitchStmt){
			visit((LookupSwitchStmt) stmt);
		}
		else if (stmt instanceof NopStmt){
			visit((NopStmt) stmt);
		}
		else {
			G.v().out.println("Other stmt" + stmt.toString());
		}
	}
	
	private void visit(IdentityStmt stmt){
		G.v().out.println("Identity --> " + stmt.toString());
	}
	
	private void visit(AssignStmt stmt){
		G.v().out.println("Assign --> " + stmt.toString());
		Value lhs = stmt.getLeftOp();
		Value rhs = stmt.getRightOp();
		
		//if input already has rhs evaluated then assign its value to lhs
		if (locals.contains(rhs) && input.containsKey((Local)rhs)){
			output.put((Local) lhs, input.get((Local)rhs));
		}
		else if (stmt.containsInvokeExpr()){
			String methodName = stmt.getInvokeExpr().getMethod().getName();
			State state = resource.getStateByMethodName(methodName);
			if (!state.equals(State.getUnknown()))
				output.put((Local)lhs, state);
		}
	}
	
	private void visit(InvokeStmt stmt){
		G.v().out.println("Invoke --> " + stmt.toString());
		String methodName = stmt.getInvokeExpr().getMethod().getName();
		State state = resource.getStateByMethodName(methodName);
		List<ValueBox> useBoxes = stmt.getUseBoxes();
		
		for(ValueBox valueBox : useBoxes){
			Value value = valueBox.getValue();
			if (locals.contains(value)){
				if (!state.equals(State.getUnknown()))
					output.put((Local) value, state);
			}
		}
	}
	
	private void visit(IfStmt stmt){
		
		G.v().out.println("IfStmt --> " + stmt.toString());
		G.v().out.println("Condition --> " + stmt.getCondition().toString());
	}
	
	private void visit(GotoStmt stmt){
		G.v().out.println("GotoStmt --> " + stmt.toString());
	}
	
	private void visit(TableSwitchStmt stmt){
		G.v().out.println("TableSwitchStmt --> " + stmt.toString());
	}
	
	private void visit(LookupSwitchStmt stmt){
		G.v().out.println("LookupSwitchStmt --> " + stmt.toString());
	}
	
	private void visit(NopStmt stmt){
		G.v().out.println("NopStmt --> " + stmt.toString());
	}
}
