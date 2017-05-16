/**
 * 
 */
package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;

/**
 * 简单顺序过程(Simple Sequential Process, S2P)
 * 含有资源的简单顺序过程(S2P with resources, S2PR)
 * @author Jiangtao Duan
 *
 */
public class S2PR extends PTNet {

	private static final long serialVersionUID = 4178108327705020803L;
	
	/**
     * A list that contains all state places of state machine.<br>
     * 工序库所（或称为操作库所）集合, 不包含{p0}
     */
    protected List<PTPlace>  PA = new ArrayList<>();
    
	/**
     * A list that contains all resource places of state machine.<br>
     * 资源库所集合
     */
    protected List<PTPlace>  PR = new ArrayList<>();
    
    /**
     * Idle state place闲置库所
     */
    protected PTPlace p0 = null;
	
	/**
	 * Place name's prefix
	 */
	private String p_prefix = "p";
	
	/**
	 * Transition name's prefix
	 */
	private String t_prefix = "t";
	

	/**
	 * last Place name's suffix
	 */
	private int p_suffix = 1;
	

	/**
	 * last Transition name's suffix 
	 */
	private int t_suffix = 1;
	
	/**
	 * 
	 */
	public S2PR() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 根据资源库所的数量，构造S2PR对象
	 * resourceNum = 2,
	 * p0 = {p1}
	 * PA = {p2,p3}
	 * PR = {p4,p5}, p5,p6可以是同一个资源库所，示意图如下
	 * <------ p4 <-------..<------ p5 <-----. 
	 * |                  ||                 |
	 * t1 ----> p2 -----> t2 -----> p3 ----> t3
	 * |                                     |
	 * <------------- p1 <-------------------.
	 * @param resourceNum   资源库所个数
	 * @param resourceToken 缺省资源库所的Token
	 * @param p0Token 缺省闲置库所的Token
	 */
	public S2PR(int resourceNum, int resourceToken, int p0Token) {
		super(); // 产生对象this	
		
		String p1,p2,t1,t2;
		p1 = lastPlaceName();
		addPlace(p1);    
		p0 = getPlace(p1);  // p0 = p1 
		t1 = lastTransitionName(); 
		addTransition(t1);  // t1
		// 记住第一个Transition的name
		String t0 = t1;
		
		for(int i = 0; i < resourceNum; i++) {
			p2 = lastPlaceName();   
			t2 = lastTransitionName();	
			addPlace(p2);       // p2
			addTransition(t2);  // t2
			// PA = {p2,...}
			PA.add(getPlace(p2));  
			// t1-->p2-->t2
			addFlowRelationTP(t1,p2,1);
			addFlowRelationPT(p2,t2,1);
			// 准备下一循环的t1
			t1 = t2;
		}
		
		// endTransition(t1) --> p0 --> firstTransition(t0)
		addFlowRelationTP(t1,p0.getName(),1);
		addFlowRelationPT(p0.getName(),t0,1);	
		
		// Marking
		PTMarking marking = new PTMarking();
		marking.set(p0.getName(), p0Token);
			
		// PR = { resource places }
		for(int i = 0; i < resourceNum; i++) {
			String pr = lastPlaceName();
			addPlace(pr);
			PR.add(getPlace(pr));
			
			// this pr's transitions
			int sufix = i + 1;
			String pr_t1 = t_prefix + sufix++;
			String pr_t2 = t_prefix + sufix;
			addFlowRelationPT(pr,pr_t1,1);
			addFlowRelationTP(pr_t2,pr,1);
			
			// resource token
			marking.set(pr, resourceToken);
		}
		
		// Marking
		setInitialMarking(marking);
	}
	
	/**
	 * 是否满足许可初始标识
	 * Li. p66, 定义4.4,
	 * @return
	 */
	public boolean isInintialMarking() {
		PTPlace pr1 = PR.get(0);
		PTPlace pr2 = PR.get(1);
		pr1 = pr2;
		boolean isOk = false;
		if (!PR.isEmpty()) isOk = true;
		return isOk;
	}
	
	/**
	 * 
	 */
	public void SetResourceSame() {
		PTPlace pr1 = PR.get(0);
		PTPlace pr2 = PR.get(1);
		pr1 = pr2;  // 不能这样
		System.out.println("pr1:" + pr1 + ",pr2:" + pr2);
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
	    String superString = super.toString();
	    //str.append("p0: " + p0.toString() + "\n"); 
	    str.append("p0: " + p0 + "\n");  // 等效
	    str.append("PA: " + PA.toString() + "\n");
	    str.append("PR: " + PR.toString() + "\n");
	    return String.format("%s%n%s", superString, str);
	}
	
	/**
	 * last Place Name: p_prefix + p_suffix++
	 */
	private String lastPlaceName() {
		return p_prefix + p_suffix++;
	}
	
	/**
	 * last Transition Name: t_prefix + t_suffix++
	 */
	private String lastTransitionName() {
		return t_prefix + t_suffix++;
	}
	
	/**
	 * current Place Name: p_prefix + p_suffix
	 */
	private String currentPlaceName() {
		return p_prefix+p_suffix;
	}
	
	/**
	 * current Transition Name: t_prefix + t_suffix
	 */
	private String currentTransitionName() {
		return t_prefix + t_suffix;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		S2PR ptnet = new S2PR();
		
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
