package edu.xidian.petrinet.createnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.validate.InconsistencyException;
import de.uni.freiburg.iig.telematik.sepia.exception.PNException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.traverse.RandomPTTraverser;
import edu.xidian.petrinet.CreatePetriNet;
import edu.xidian.petrinet.graph.PTNetGraphComponent;
/**
 *  重写打印 
 *
 */
public class DecoratePrint extends PTNet{
	
	private static ArrayList<PTNetTraversalStepInfo>  ptNetTraversalStepInfos =
			new ArrayList<PTNetTraversalStepInfo>();
	//static  PTNet ptNet = new PTNet();
	private static PTNet  ptNet = null;
	//便于外部类的获取
	public static ArrayList<PTNetTraversalStepInfo> getPtNetTraversalStepInfos() {
		
		return ptNetTraversalStepInfos;
	}

	@Override
	public String toString(){
		String placeFormat = "%s,%s,";
		StringBuilder builder = new StringBuilder();
		
		List<String> placeNamesSorted = new ArrayList<String>(ptNet.getMarking().places());
		Collections.sort(placeNamesSorted);
		for(String pla: placeNamesSorted){
			builder.append(String.format(placeFormat, pla, ptNet.getMarking().get(pla)));
		}
		return builder.toString();
	}
	public static String toPrint(PTNet ptnet){
		 StringBuilder relationBuilder = new StringBuilder();
		 Collection<PTFlowRelation> flowRelations = ptnet.getFlowRelations();
		 for (PTFlowRelation relation : flowRelations) {
			 relationBuilder.append("");
			 relationBuilder.append(relation);
			 relationBuilder.append(',');
		}
		
		return String.format("%s %n", relationBuilder.toString());
	}
	public static void RandomPTTraverserTest(PTNet ptnet) {		
		
		ptNet = ptnet;
		System.out.println("===Random Petri Net Traversal====");
		RandomPTTraverser t = new RandomPTTraverser(ptnet);
		for (int i = 1; ptnet.hasEnabledTransitions(); i++) {
			//获得使能变迁的个数：  1 ： 2 说明在初始状态（1）有两个变迁使能，到第二此循环时说明在
			//在状态2 的使能变迁的个数，此时循环完成，只能说明是在初始状态时的一个变迁下的使能连锁反应.
			System.out.println(i + ": " + ptnet.getEnabledTransitions().size()); // 1: 2
			
			PTNetTraversalStepInfo ptNetTraversalStepInfo = new PTNetTraversalStepInfo();
			//<1>
			ArrayList<String>  P = new ArrayList<String>();
			
			Collection<PTPlace> place = ptnet.getPlaces();
			
			Iterator<PTPlace> it = place.iterator();
			while(it.hasNext())
			{	
				P.add(it.next().getName());
			}

			//<2>
			Collection<PTTransition> transitions2 = ptnet.getTransitions();
			ArrayList<String>  T = new ArrayList<String>();
			Iterator<PTTransition> iterator = transitions2.iterator();
			for (PTTransition ptTransition : transitions2) {
				
				String s1 = ptTransition.toString();
				String[] split = s1.split("\\[");
				T.add(split[0]);
			
			}
		    //<3>
		    ArrayList<String> M = new ArrayList<String>();		    
			PTMarking string = ptnet.getInitialMarking();
			String[] str5 = string.toString().split(",");
		
            for(int k = 0; k < str5.length;k++)
            {
            	M.add(str5[k]);
            }
			//<4>
			String str1 = toPrint(ptnet);
			String str2 = str1.replaceAll("-> ", ",");
			String str3 = str2.replaceAll(" -", ",");
			String[] str4 = str3.split(",");			
		
			ptNetTraversalStepInfo.setKuSuo(P);
			ptNetTraversalStepInfo.setBianQian(T);
			ptNetTraversalStepInfo.setChuShiKuSuo(M);
			ptNetTraversalStepInfo.setLiuGuanXi(str4);
			ptNetTraversalStepInfos.add(ptNetTraversalStepInfo);			
			try {
				// 在使能变迁中，随机选择一个变迁
				PTTransition tran = t.chooseNextTransition(ptnet.getEnabledTransitions());
				System.out.println("EnabledTransitions: " + ptnet.getEnabledTransitions());
				System.out.println("chooseNextTransition: " + tran);
				
				String eTString = String.valueOf(ptnet.getEnabledTransitions());
				String rtString = String.valueOf(tran);
				System.out.println("此步可以发射的使能变迁：" + eTString);
				
				System.out.println("选择发射的使能变迁：" + rtString);
				
				tran.fire();
			} catch (InconsistencyException e) {
				e.printStackTrace();
			} catch (PNException e) {
				e.printStackTrace();
			}
			System.out.println(ptnet);
			
			//test
		}
		
		System.out.println("no more enabled transitions");
	}
	public static void equipmentPlaceTool(PTNet ptNet ,ArrayList<String> P)
	{
		if(ptNet == null)
		{
			ptNet = new PTNet();
		}
	    Iterator<String> it=P.iterator();
		while(it.hasNext())
		{
			ptNet.addPlace(it.next());
	    }
	}

	public static void equipmentTransitionTool(PTNet ptNet,ArrayList<String> T)
	{
		if(ptNet == null)
		{
			ptNet = new PTNet();
		}
		Iterator<String> it = T.iterator();
		while(it.hasNext())
		{
			ptNet.addTransition(it.next());
		}
	}	
	 public static void equipmentMarkingTools( PTMarking marking,String [][] temp_Marking)
	 {
		 for(int i = 0; i < temp_Marking.length;i++)
		 {
			 marking.set(temp_Marking[i][0], Integer.valueOf(temp_Marking[i][1])); //散列在hash Map中
		 }
	 }
	
	
}
