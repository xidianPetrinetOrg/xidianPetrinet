package edu.xidian.petrinet;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.Utils.myUtils;

public class CreatePetriNet {
	
	public static int[][] IncMatObjectNet = null;
	
	public static PTNet ptnet = null;
	
	public static  PTNet test11(){
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		//ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");

		System.out.println("ptnet1：" + ptnet1);
		return ptnet1;
	}
	 
	public static PTNet createPTnet1() {
	
	    ptnet = new PTNet();
		IncMatObjectNet = new int[2][3];
		
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addFlowRelationPT("p1", "t1",2);
		IncMatObjectNet[0][0]=1;
		
		ptnet.addFlowRelationTP("t1", "p2",1);
		IncMatObjectNet[0][1]=-1;
		
		ptnet.addFlowRelationPT("p2", "t3",1);
		IncMatObjectNet[1][2]=1;
		
		ptnet.addFlowRelationPT("p1", "t2",1);
		IncMatObjectNet[0][1]=1;
		
		ptnet.addFlowRelationTP("t2", "p2",1);
		IncMatObjectNet[1][2]=-1;
		
		myUtils.print(IncMatObjectNet);
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2);
		ptnet.setInitialMarking(marking);
		
		//marking.set("p2", 1);//散列在hash Map中
		
		
		marking.set("p1", 2); 
		ptnet.setInitialMarking(marking);

		
		return ptnet;
	}
	//自动制造系统建模分析与死锁控制 P165
	public static PTNet createPTnet2()
	{
		PTNet ptnet = new PTNet();
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addPlace("p3");
		ptnet.addPlace("p4");
		ptnet.addPlace("p5");
		ptnet.addPlace("p6");
		ptnet.addPlace("p7");
		ptnet.addPlace("p8");
		ptnet.addPlace("p9");
		ptnet.addPlace("p10");
		ptnet.addPlace("p11");
		
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addTransition("t4");
		ptnet.addTransition("t5");
		ptnet.addTransition("t6");
		ptnet.addTransition("t7");
		ptnet.addTransition("t8");
		
		ptnet.addFlowRelationPT("p1","t1",1);
		ptnet.addFlowRelationTP("t1","p2",1);
		ptnet.addFlowRelationPT("p2","t2",1);
		ptnet.addFlowRelationTP("t2","p5",1);
		ptnet.addFlowRelationPT("p5","t3",1);
		ptnet.addFlowRelationTP("t3","p8",1);
		ptnet.addFlowRelationTP("t4","p1",1);
		
		ptnet.addFlowRelationTP("t8","p11",1);
		ptnet.addFlowRelationTP("t5","p10",1);
		ptnet.addFlowRelationPT("p10","t6",1);
		ptnet.addFlowRelationTP("t6","p7",1);
		ptnet.addFlowRelationPT("p7","t7",1);
		ptnet.addFlowRelationTP("t7","p4",1);
		ptnet.addFlowRelationPT("p4","t8",1);
		
		ptnet.addFlowRelationPT("p3","t1",1);
		ptnet.addFlowRelationPT("p3","t7",1);
		ptnet.addFlowRelationTP("t8","p3",1);
		ptnet.addFlowRelationTP("t2","p3",1);
		
		ptnet.addFlowRelationPT("p6","t2",1);
		ptnet.addFlowRelationPT("p6","t6",1);
		ptnet.addFlowRelationTP("t7","p6",1);
		ptnet.addFlowRelationTP("t3","p6",1);
		
		ptnet.addFlowRelationPT("p9","t3",1);
		ptnet.addFlowRelationPT("p9","t5",1);
		ptnet.addFlowRelationTP("t6","p9",1);
		ptnet.addFlowRelationTP("t4","p9",1);
	
		PTMarking marking = new PTMarking();
		marking.set("p1", 3); //散列在hash Map中
		marking.set("p11", 3);
		marking.set("p3", 1);
		marking.set("p6", 1);
		marking.set("p9", 1);
		ptnet.setInitialMarking(marking);
		return ptnet;
	}
	public static PTNet createPTnet3() {
		PTNet ptnet = new PTNet();  
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addPlace("p3");
		ptnet.addPlace("p4");
		ptnet.addPlace("p5");
		
		
		
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addTransition("t4");
		ptnet.addTransition("t5");
		
		
		ptnet.addFlowRelationPT("p1", "t1",1);
		ptnet.addFlowRelationTP("t1", "p2",1);
		ptnet.addFlowRelationPT("p2", "t2",1);
		ptnet.addFlowRelationTP("t2", "p3",1);
		ptnet.addFlowRelationPT("p3", "t3",1);
		ptnet.addFlowRelationTP("t3", "p1",1);
		ptnet.addFlowRelationTP("t3", "p4",3);
		
		ptnet.addFlowRelationPT("p4", "t2",2);
		ptnet.addFlowRelationPT("p4", "t1",1);
		ptnet.addFlowRelationPT("p4", "t4",2);
		ptnet.addFlowRelationTP("t4", "p4",1);
		ptnet.addFlowRelationTP("t4", "p5",1);
		ptnet.addFlowRelationPT("p5", "t5",1);
		ptnet.addFlowRelationTP("t5", "p4",1);
		
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2); //散列在hash Map中
		ptnet.setInitialMarking(marking);
		return ptnet;
	}
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		//PTNet ptnet = CreatePetriNet.createPTnet2();
		//PTNet ptnet = CreatePetriNet.createPTnet3();
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
