

/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PackManager;
import soot.Transform;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.*;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.FlowUniverse;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;
import soot.util.Switch;

public class MyMain {
	
	static Map<Unit, FlowSet> unitToLocalsAfter = new HashMap<Unit, FlowSet>();
	static Map<Unit, FlowSet> unitToLocalsBefore  = new HashMap<Unit, FlowSet>();
	
	public static void main(String[] args) {
		PackManager.v().getPack("jtp").add(
				new Transform("jtp.myTransform", new BodyTransformer() {

					protected void internalTransform(Body body, String phase, Map options) {

						
						UnitGraph unitGraph = new ExceptionalUnitGraph(body);
						MyAnalysis analysis = new MyAnalysis(unitGraph);
						// use G.v().out instead of System.out so that Soot can
						// redirect this output to the Eclipse console
						
						//Iterator it = unitGraph.iterator();
						
						//while(it.hasNext()){
							
							//Unit unit = (Unit) it.next();
							
							//FlowSet setBefore = (FlowSet) analysis.getFlowBefore(unit);
							
							//unitToLocalsBefore.put(unit, setBefore);
							
							//FlowSet setAfter = (FlowSet) analysis.getFlowAfter(unit);
							
							//unitToLocalsAfter.put(unit, setAfter);
							
						//}

						
						
						//Iterator it = body.getUnits().iterator();
				    	
				    	/*while(it.hasNext()){
				    		Stmt stmt = (Stmt)it.next();
				    		
				    		//TODO: Need to do create Visitor Method that implements Switch
				    		//TODO: stmt.apply(new Switch());
				    		
				    		if (stmt instanceof IdentityStmt){
				    			G.v().out.println("IdentityStmt" + " " + stmt.toString());
				    			if(((IdentityStmt) stmt).getRightOp() instanceof ParameterRef)
				    				G.v().out.println("Right OP ---> " + ((IdentityStmt) stmt).getRightOp().toString());
				    		}
				    		if (stmt instanceof NopStmt){
				    			G.v().out.println("NopStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof AssignStmt){
				    			G.v().out.println("AssignStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof IfStmt){
				    			G.v().out.println("IfStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof GotoStmt){
				    			G.v().out.println("GotoStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof TableSwitchStmt){
				    			G.v().out.println("TableSwitchStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof LookupSwitchStmt){
				    			G.v().out.println("LookupSwithStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof InvokeStmt){
				    			G.v().out.println("InvokeStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof ReturnStmt){
				    			G.v().out.println("ReturnStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof ReturnVoidStmt){
				    			G.v().out.println("ReturnVoidStmt" + " " + stmt.toString());
				    		}
				    		if (stmt instanceof ThrowStmt){
				    			G.v().out.println("ThrowStmt" + " " + stmt.toString());
				    		}
				    	}*/
						//G.v().out.println(body.getMethod());
					}
					
				}));
		
		soot.Main.main(args);
		
		G.v().out.println("BEFORE");
		for(Unit unit : unitToLocalsBefore.keySet()){
			G.v().out.println("UNIT : " + unit.toString() + " ---> " + unitToLocalsBefore.get(unit));
		}
		
		G.v().out.println("AFTER");
		for(Unit unit : unitToLocalsAfter.keySet()){
			G.v().out.println("UNIT : " + unit.toString() + " ---> " + unitToLocalsAfter.get(unit));
		}
	}

	public static class MyAnalysis extends ForwardFlowAnalysis {
		
		FlowSet emptySet = new ArraySparseSet();
		Map<Unit, FlowSet> unitsToPerserve = new HashMap<Unit, FlowSet>();
		Map<Unit, FlowSet> unitsToGenerate = new HashMap<Unit, FlowSet>();
		
		public MyAnalysis(UnitGraph unitGraph) {
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
		protected Object newInitialFlow() {
			
			return emptySet.clone();
		}

		@Override
		protected Object entryInitialFlow() {
			
			return emptySet.clone();
		}

		@Override
		protected void merge(Object in1, Object in2, Object out) {
			// TODO Auto-generated method stub
			FlowSet inSet1 = (FlowSet)in1;
			FlowSet inSet2 = (FlowSet)in2;
			FlowSet outSet = (FlowSet)out;
			
			inSet1.union(inSet2, outSet);
		}

		@Override
		protected void copy(Object source, Object dest) {
			// TODO Auto-generated method stub
			FlowSet sourceSet = (FlowSet)source;
			FlowSet destSet = (FlowSet)dest;
			
			sourceSet.copy(destSet);
		}


	}
}