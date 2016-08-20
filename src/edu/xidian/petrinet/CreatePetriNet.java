package edu.xidian.petrinet;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;

public class CreatePetriNet {
	public static PTNet createPTnet1() {
		PTNet ptnet = new PTNet();
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addFlowRelationPT("p1", "t1",2);
		ptnet.addFlowRelationTP("t1", "p2",1);
		ptnet.addFlowRelationPT("p2", "t3",1);
		ptnet.addFlowRelationPT("p1", "t2",1);
		ptnet.addFlowRelationTP("t2", "p2",1);
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2);
		ptnet.setInitialMarking(marking);
		return ptnet;
	}
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		System.out.println(ptnet);
		/***
	     Petri-Net: PetriNet
          places: [p1[p1], p2[p2]] 
     transitions: [t1[t1], t2[t2], t3[t3]] 
     flow-relation: 
                  t2 -1-> p2
                  t1 -1-> p2
                  p1 -1-> t2
                  p2 -1-> t3
                  p1 -2-> t1
 
      initial marking: p1[2]  
      actual marking: p1[2]  
		 */
	}
}
