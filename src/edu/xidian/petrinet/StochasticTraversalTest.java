package edu.xidian.petrinet;

import java.util.List;

import de.uni.freiburg.iig.telematik.sepia.exception.PNException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import de.uni.freiburg.iig.telematik.sepia.traversal.PNTraversalUtils;
import de.uni.freiburg.iig.telematik.sepia.traversal.StochasticPNTraverser;


/**
 * 根据概率选择后一次变迁
 * @author Administrator
 *
 */
public class StochasticTraversalTest {
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		System.out.println("ptnet\n" + ptnet);
		
		System.out.println("===Stochastic Petri Net Traversal====");
		StochasticPNTraverser<PTTransition> t = new StochasticPNTraverser<>(ptnet);
		// t1之后各变迁的概率
		t.addFlowProbability("t1", "t3", 1);
		// t2各变迁的概率
		t.addFlowProbability("t2", "t2", 0.2);
		t.addFlowProbability("t2", "t3", 0.8);
		// t3各变迁的概率
		t.addFlowProbability("t3", "t3", 1);
		
		System.out.println(t.isValid());
		
		if(t.isValid()) {	
			for (int i = 1; ptnet.hasEnabledTransitions(); i++) {
				List<PTTransition> trans = ptnet.getEnabledTransitions();
				System.out.println(i + ": " + trans);
				PTTransition tran = (PTTransition) t.chooseNextTransition(trans);	
				System.out.println("chooseTransition: " + tran);
				try {
					tran.fire();
				} catch (PNException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(ptnet.getEnabledTransitions().size()); 
		
		// simulates the Petri net ptnet 10 times and prints out all distinct traces. Thereby it
		// limits the number of activities per sequence to 100.
		PNTraversalUtils.testTraces(ptnet, 10, 100, true,false,false);
	}
}
