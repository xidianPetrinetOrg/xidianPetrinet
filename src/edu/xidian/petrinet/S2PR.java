/**
 * 
 */
package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.List;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;

/**
 * 简单顺序过程(Simple Sequential Process, S2P)
 * 含有资源的简单顺序过程(S2P with resources, S2PR)继承自S2P，增加了资源库所集
 * @author Jiangtao Duan
 *
 */
public class S2PR extends S2P {

	private static final long serialVersionUID = 4178108327705020803L;
	
	/**
     * A list that contains all resource places of state machine.<br>
     * 资源库所集合
     */
    protected List<PTPlace>  PR = new ArrayList<>();
    
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
		super(resourceNum,p0Token); // 初始化一个父类对象，S2P对象
				
		// Initial Marking
		PTMarking marking = getInitialMarking();
			
		// PR = { resource places }
		for(int i = 0; i < resourceNum; i++) {
			String pr = lastPlaceName(); // 资源库所name
			addPlace(pr);
			PR.add(getPlace(pr));
			
			// this pr's transitions: t1,t2
			for (AbstractPNNode<?> t1: PA.get(i).getParents()) {
				for (AbstractPNNode<?> t2: PA.get(i).getChildren()) {
					addFlowRelationPT(pr,t1.getName(),1);
					addFlowRelationTP(t2.getName(),pr,1);
				}
			}
			
			// resource token
			marking.set(pr, resourceToken);
		}  
		
		// Marking
		setInitialMarking(marking);
	}
		
	/**
	 * 满足S2PR的定义？
	 * Li. p66, 定义4.3
	 * 含有资源的简单顺序过程(S2PR-S2P with Resources)是Petri网 N = ({p0} ∪ PA ∪PR, T, F), 使得
     * 1. 由X = PA ∪ {p0} ∪T 生成的子网是S2P;
     * 2. PR ≠ ∅ ;, (PA ∪ {p0}) ∩ PR = ∅ ;
     * 3. 任意p∈PA, 任意t∈p的前置集, 存在∪∩∈; &forall;	&#8704;
     * 4. (a) 8r 2 PR, 22r \ PA = r22 \ PA 6= ;; (b) 8r 2 PR, 2r \ r2 = ;;
     * 5. 22(p0) \ PR = (p0)22 \ PR = ;。
	 * @return
	 */
	public boolean isS2PR() {
	   boolean isOk = true;

       if(!isS2P()) return false;
		
	   return isOk;
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
}
