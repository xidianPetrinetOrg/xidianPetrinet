package edu.xidian.petrinet.graph;


import de.invation.code.toval.graphic.component.DisplayFrame;
import de.uni.freiburg.iig.telematik.jagal.visualization.flexible.AbstractLabeledTransitionSystemComponent;
import de.uni.freiburg.iig.telematik.sepia.generator.PNGenerator;
import de.uni.freiburg.iig.telematik.sepia.mg.pt.PTMarkingGraph;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg.MGConstruction;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg.MarkingGraphException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;

public class MarkingGraphTest {
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();       // states: 6
		//PTNet ptnet = PNGenerator.sharedResource(2, 1);  // states: 15
		//PTNet ptnet = PNGenerator.producerConsumer(10, 1);  // states: 1860
		System.out.println(ptnet);
		
		System.out.println("Caculator PTMarkingGraph....");
		PTMarkingGraph mg = null;
		try {
			mg = (PTMarkingGraph) MGConstruction.buildMarkingGraph(ptnet);
			int VertexNum = mg.getVertexCount();
			System.out.println("PTMarkingGraph: Vertex Number(states) = " + VertexNum);
			System.out.println(mg);
		} catch (MarkingGraphException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/***
		  TS = {S, E, T, S_start, S_end}
		  S       = [s3, s4, s5, s0, s1, s2]
		  S_start = [s0]
		  S_end   = [s5]
		  E       = [t1, t2, t3]
		  T       = (s0, t2, s1)
		            (s0, t1, s2)
		            (s1, t2, s3)
		            (s1, t3, s4)
		            (s2, t3, s5)
		            (s3, t3, s2)
		            (s4, t2, s2)
		 ***/
		
		/*** Display Marking Graph */
		try {
			AbstractLabeledTransitionSystemComponent<PTMarkingGraph, ?, ?, ?, ?> component = new AbstractLabeledTransitionSystemComponent<>(mg);
			component.initialize();
			new DisplayFrame(component,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
