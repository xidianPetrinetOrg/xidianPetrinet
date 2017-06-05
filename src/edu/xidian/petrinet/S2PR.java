/**
 * 
 */
package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * this S2PR对象对应的S2P对象, 即不含资源库所(PR)的S2P对象
     */
    protected S2P s2p = null;
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
		// 初始化一个父类对象，S2P对象
		super(resourceNum,p0Token); 
		// this S2PR对象对应的S2P对象
		s2p = new S2P(resourceNum,p0Token); 
				
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
	 * <pre>
	 * 含有资源的简单顺序过程(S2PR-S2P with Resources)是Petri网 N = ({p0} ∪ PA ∪PR, T, F), 使得
     * 1. 由X = PA ∪ {p0} ∪ T 生成的子网是S2P
     * 2. PR ≠ ∅ ;, (PA ∪ {p0}) ∩ PR = ∅ 
     * 3. 任意p∈PA, 任意t1∈p的前置集, 任意t2∈p的后置集, 存在rp ∈PR, t1的前置集  ∩ PR = t2的后置集  ∩ PR = {rp}
     * 4. (a) 任意r∈PR , r的前置集的前置集 ∩ PA = r的后置集的后置集 ∩ PA ≠ ∅; (b) 任意r∈PR, r的前置集 ∩ r后置集 = ∅
     * 5. (p0)的前置集的前置集  ∩ PR = (p0)的后置集的后置集  ∩ PR = ∅。
     * </pre>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isS2PR() {
	   Set<AbstractPNNode> temp1 = new HashSet<>();
	   Set<AbstractPNNode> temp2 = new HashSet<>();
	   /** 1. 由X = PA ∪ {p0} ∪ T 生成的子网是S2P */
       // this对象（S2PR对象）对应的S2P对象
	   if (s2p == null) return false;
       if(!s2p.isS2P()) return false;
       
       /** 2. PR ≠ ∅ ;, (PA ∪ {p0}) ∩ PR = ∅  */
       if (PR.isEmpty()) return false;
       temp1.addAll(PA);
       temp1.add(p0);
       temp1.retainAll(PR);  // 交集
       if (!temp1.isEmpty()) return false;
       
       /** 3. 任意p∈PA, 任意t1∈p的前置集, 任意t2∈p的后置集, 存在rp ∈PR, t1的前置集  ∩ PR = t2的后置集 ∩ PR = {rp} **/
       for (PTPlace p: PA) {
    	   for (AbstractPNNode t1: p.getParents()) {
    		   for (AbstractPNNode t2: p.getChildren()) {
    			   // (1) t1的前置集  ∩ PR
    			   temp1.clear();
    			   temp1.addAll(t1.getParents());
    			   temp1.retainAll(PR); // 交集
    			   // (2) t2的后置集 ∩ PR
    			   temp2.clear();
    			   temp2.addAll(t2.getChildren());
    			   temp2.retainAll(PR); // 交集
    	           // (1) = (2) = {1个PR元素}
    			   if (!temp1.equals(temp2)) return false;
    			   if (temp1.size() != 1) return false;  // 仅有1个rp
    			   for (AbstractPNNode node: temp1) {
    			       if (!PR.contains(node)) return false;
    			   }
    		   }
    	   }
       }
       
       /** 
        * 4. (a) 任意r∈PR , r的前置集的前置集 ∩ PA = r的后置集的后置集 ∩ PA ≠ ∅; 
        *    (b) 任意r∈PR, r的前置集 ∩ r后置集 = ∅ 
        ***/
       for (PTPlace r: PR) {
    	   /////////////// (a)
    	   temp1.clear();
    	   for (AbstractPNNode node: r.getParents()) {
    		   temp1.addAll(node.getParents());  
    	   }
    	   temp1.retainAll(PA);
    	   temp2.clear();
    	   for (AbstractPNNode node: r.getChildren()) {
    		   temp2.addAll(node.getChildren());  
    	   }
    	   temp2.retainAll(PA);
    	   if (!temp1.equals(temp2)) return false;
    	   if (temp1.isEmpty() || temp2.isEmpty()) return false;
    	   ///////////////// (b)
    	   temp1.clear();
    	   temp1.addAll(r.getParents());
    	   temp2.clear();
    	   temp2.addAll(r.getChildren());
    	   temp1.retainAll(temp2);
    	   if (!temp1.isEmpty()) return false;
       }
       
       /** 5. (p0)的前置集的前置集  ∩ PR = (p0)的后置集的后置集  ∩ PR = ∅。 **/
       temp1.clear();
       for (AbstractPNNode node: p0.getParents()) {
		   temp1.addAll(node.getParents());  
	   }
       temp1.retainAll(PR);
       temp2.clear();
       for (AbstractPNNode node: p0.getChildren()) {
		   temp2.addAll(node.getChildren());  
	   }
	   temp2.retainAll(PR);
	   if (!temp1.isEmpty() || !temp2.isEmpty()) return false;
		
	   return true;
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
