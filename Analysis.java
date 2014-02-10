import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import resources.State;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.infoflow.CallChain;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;

public class Analysis {
	
	static Map<Value, State> env;
	static Map<Stmt, List<State>> cool;
	
	public static void main(String[] args) {
		env = new HashMap<Value, State>();
		cool = new HashMap<Stmt, List<State>>();
		
		PackManager.v().getPack("jtp").add(
				new Transform("jtp.myTransform", new BodyTransformer() {

					protected void internalTransform(Body body, String phase, Map options) {
						UnitGraph unitGraph = new ExceptionalUnitGraph(body);
						
						/*Chain locals = body.getLocals();
						
						Iterator it = locals.iterator();
						
						//Initiate each local variables as TOP
						while(it.hasNext()){
							Local local = (Local)it.next();
							Value value = (Value)local;
							env.put(value, State.getTop());
						}*/
						
						// Conduct Flow Analysis
						FlowAnalysis flowanalysis = new FlowAnalysis(unitGraph);
						
						// Use Flow Graph from Flow Analysis to do Constant Propagation Analysis
						Edge edge = new Edge(body.getMethod(), (Stmt)body.getUnits().getFirst(), body.getMethod());
						
					}
					
				}));
		soot.Main.main(args);
		
		/*G.v().out.println("LOCALS---------------");
		for(Value local : env.keySet()){
			G.v().out.println("" + local.toString() + "---->" + env.get(local));
		}*/
		/*G.v().out.println("STATEMENTS---------------");
		for(Stmt stmt : cool.keySet()){
			G.v().out.println("" + stmt.toString() + "---->" + cool.get(stmt).toString());
		}*/
	}
	
	public static class FlowAnalysis extends ForwardFlowAnalysis{
		FlowSet emptySet = new ArraySparseSet();
		Map<Unit, FlowSet> unitsToPerserve = new HashMap<Unit, FlowSet>();
		Map<Unit, FlowSet> unitsToGenerate = new HashMap<Unit, FlowSet>();

		public FlowAnalysis(UnitGraph unitGraph) {
			super(unitGraph);
			
			//Generate the gen and kill set to perform the analysis
			Iterator it = unitGraph.getBody().getUnits().iterator();
			
			while(it.hasNext()){
				Unit unit = (Unit) it.next();
				
				FlowSet killSet = emptySet.clone();
				FlowSet genSet = emptySet.clone();
				
				Iterator itKillBox = unit.getUseBoxes().iterator();
				
				while(itKillBox.hasNext()){
					ValueBox killBox = (ValueBox)itKillBox.next();
					if (killBox.getValue() instanceof Local){
						killSet.add(killBox.getValue(), killSet);
					}
				}
				//killSet.complement(killSet);
				unitsToPerserve.put(unit, killSet);
				
				Iterator itGenBox = unit.getDefBoxes().iterator();
				
				while(itGenBox.hasNext()){
					ValueBox genBox = (ValueBox)itGenBox.next();
					if (genBox.getValue() instanceof Local){
						genSet.add(genBox.getValue(), genSet);
					}
				}
				//genSet.complement(genSet);
				unitsToGenerate.put(unit, genSet);
			}
			
			doAnalysis();
		}

		@Override
		protected void flowThrough(Object in, Object d, Object out) {
			// TODO Auto-generated method stub
			FlowSet inSet = (FlowSet) in, outSet = (FlowSet) out;
			
			// Perform kill
			inSet.intersection((FlowSet) unitsToPerserve.get(d), outSet);
			
			// Perform generation
			outSet.union((FlowSet) unitsToGenerate.get(d), outSet);
		}

		@Override
		protected void copy(Object source, Object dest) {
			// TODO Auto-generated method stub
			FlowSet sourceSet = (FlowSet)source;
			FlowSet destSet = (FlowSet)dest;
			
			sourceSet.copy(destSet);
		}

		@Override
		protected Object entryInitialFlow() {
			// TODO Auto-generated method stub
			return emptySet.clone();
		}

		@Override
		protected void merge(Object source1, Object source2, Object destination) {
			// TODO Auto-generated method stub
			FlowSet inSet1 = (FlowSet)source1;
			FlowSet inSet2 = (FlowSet)source2;
			FlowSet outSet = (FlowSet)destination;
			
			inSet1.union(inSet2, outSet);
		}

		@Override
		protected Object newInitialFlow() {
			// TODO Auto-generated method stub
			return emptySet.clone();
		}
	}
	
	
}
