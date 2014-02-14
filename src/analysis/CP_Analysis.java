package analysis;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.Transform;
import soot.jimple.Constant;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

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
				new ConstantPropState(unitGraph);
			}

		}));
		soot.Main.main(args);
	}
}
