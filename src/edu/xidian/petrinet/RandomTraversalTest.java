package edu.xidian.petrinet;

import de.invation.code.toval.validate.InconsistencyException;
import de.uni.freiburg.iig.telematik.sepia.exception.PNException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.traverse.RandomPTTraverser;
import de.uni.freiburg.iig.telematik.sepia.traversal.PNTraversalUtils;

public class RandomTraversalTest {

	/**
	 * 随机遍历，在使能变迁中，随机选择一个变迁发射
	 */
	static void RandomPTTraverserTest(PTNet ptnet) {		
		System.out.println("===Random Petri Net Traversal====");
		RandomPTTraverser t = new RandomPTTraverser(ptnet);
		for (int i = 1; ptnet.hasEnabledTransitions(); i++) {
			System.out.println(i + ": " + ptnet.getEnabledTransitions().size()); // 1: 2
			                                                                     // 2: 1
			try {
				// 在使能变迁中，随机选择一个变迁
				PTTransition tran = t.chooseNextTransition(ptnet.getEnabledTransitions());
				System.out.println("EnabledTransitions: " + ptnet.getEnabledTransitions());
				System.out.println("chooseNextTransition: " + tran);
				tran.fire();
			} catch (InconsistencyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ptnet);
		}
		System.out.println("no more enabled transitions");
	}
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		System.out.println("ptnet\n" + ptnet);
		
		//RandomPTTraverserTest((PTNet) ptnet.clone());
		RandomPTTraverserTest(ptnet);
		
		System.out.println(ptnet.getEnabledTransitions().size()); 
		
		// simulates the Petri net ptnet 10 times and prints out all distinct traces. Thereby it
		// limits the number of activities per sequence to 100.
		PNTraversalUtils.testTraces(ptnet, 10, 100, true,false,false);
	}
}
