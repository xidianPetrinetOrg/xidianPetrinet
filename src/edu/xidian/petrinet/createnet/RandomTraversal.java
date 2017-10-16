package edu.xidian.petrinet.createnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.validate.InconsistencyException;
import de.uni.freiburg.iig.telematik.sepia.exception.PNException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.traverse.RandomPTTraverser;
import edu.xidian.petrinet.CreatePetriNet;
import edu.xidian.petrinet.graph.PTNetGraphComponent;

public class RandomTraversal {
	/**
	 * 随机遍历，在使能变迁中，随机选择一个变迁发射，可能刚开始的使能变迁好几个，这里只能随机选择一个使能变迁
	 * 进行变迁演示，此处所谓的随机只是随机一部分变迁进行遍历，比如此处的刚开始的使能变迁可以是t1 或t2 而此处的
	 * 选择刚好是t2 作为使能变迁的入口，并没有进行t1 的使能变迁演示，故称之为随机演示一个使能变迁。
	 */
	
	private static ArrayList<PTNetTraversalStepInfo>  ptNetTraversalStepInfos =
			new ArrayList<PTNetTraversalStepInfo>();
	
	//便于外部类的获取
	public static ArrayList<PTNetTraversalStepInfo> getPtNetTraversalStepInfos() {
		return ptNetTraversalStepInfos;
	}

	public static void RandomPTTraverserTest(PTNet ptnet) {		
		System.out.println("===Random Petri Net Traversal====");
		RandomPTTraverser t = new RandomPTTraverser(ptnet);
		for (int i = 1; ptnet.hasEnabledTransitions(); i++) {
			//获得使能变迁的个数：  1 ： 2 说明在初始状态（1）有两个变迁使能，到第二此循环时说明在
			//在状态2 的使能变迁的个数，此时循环完成，只能说明是在初始状态时的一个变迁下的使能连锁反应.
			System.out.println(i + ": " + ptnet.getEnabledTransitions().size()); // 1: 2
			
			PTNetTraversalStepInfo ptNetTraversalStepInfo = new PTNetTraversalStepInfo();
			//<1>
			ArrayList<String>  P = new ArrayList<String>();
			Collection<PTPlace> place = ptnet.initPlace();
			Iterator<PTPlace> it = place.iterator();
			while(it.hasNext())
			{	
				P.add(it.next().getName());
			}

			Map<String, PTTransition> transition = ptnet.initTransition();
			//<2>
			ArrayList<String>  T = new ArrayList<String>();
			Iterator<String> it1 = transition.keySet().iterator();
			while(it1.hasNext())
			{
				T.add(it1.next());
			}
		    //<3>
		    ArrayList<String> M = new ArrayList<String>();		    
			PTMarking string = ptnet.initMarking();
			String[] str5 = string.toString1().split(",");
            for(int k = 0; k < str5.length;k++)
            {
            	M.add(str5[k]);
            }
			//<4>
			String str1 = ptnet.toString3();
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
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		System.out.println("ptnet\n" + ptnet);
		
		//RandomPTTraverserTest((PTNet) ptnet.clone());
		RandomPTTraverserTest(ptnet);
		
		System.out.println(ptnet.getEnabledTransitions().size()); 
	
		for(int i = 0;i < ptNetTraversalStepInfos.size();i++)
		{
			
			//1 创建Petri网
			PTNet ptNet = new PTNet();
			
			//2获取网的信息
			ArrayList<String>  P = ptNetTraversalStepInfos.get(i).getKuSuo();
			ArrayList<String>  T = ptNetTraversalStepInfos.get(i).getBianQian();
			ArrayList<String>  M = ptNetTraversalStepInfos.get(i).getChuShiKuSuo();
			String []       str4 = ptNetTraversalStepInfos.get(i).getLiuGuanXi();
			
			//3 给网中装配获取的信息
			  //3.1 装配Place
			equipmentPlaceTool(ptNet,P);
			  //3.2装配Transition
			equipmentTransitionTool(ptNet,T);
			
			  //3.3装配初始的Marking
			PTMarking marking = new PTMarking();
			  int len2 = M.size()/2;
				String temp_Marking[][] = new String[len2][2];
				int k2 = 0;
				//把一维数组转换为二维数组n行2列，方便后面的对号入座：p1,1
				for(int w = 0 ; w < len2; w++)
				{
					for(int j = 0; j < 2;j++)
					{
						temp_Marking[w][j] = M.get(k2);
						k2++;
					}
				}
		    equipmentMarkingTools(marking,temp_Marking);
		    ptNet.setInitialMarking(marking);
		       //3.4装配流关系
			for(int x= 0; x < str4.length;x = x+3)
			{
				if(P.contains(str4[x]))
				{
					ptNet.addFlowRelationPT(str4[x],str4[x+2],Integer.valueOf(str4[x+1]));
				}
				if(T.contains(str4[x]))
				{
					ptNet.addFlowRelationTP(str4[x],str4[x+2],Integer.valueOf(str4[x+1]));
				}
			}
			//4 重新创建一个线程将装配好的petri网发射
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	PTNetGraphComponent component = new PTNetGraphComponent(ptNet);
	        		try {
	        			component.initialize();  // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}	
	        	    new DisplayFrame(component,true);  // 显示图形元素
	        	}       
	        }); 
		}
	}
}
