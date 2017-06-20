/**
 * 
 */
package edu.xidian.petrinet.S3PR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import edu.xidian.petrinet.Utils.PNNodeComparator;

/**
 * 简单顺序过程(Simple Sequential Process, S2P)
 * 含有资源的简单顺序过程(S2P with resources, S2PR)继承自S2P，增加了资源库所集
 * @author Jiangtao Duan
 *
 */
public class S2PR extends S2P {

	private static final long serialVersionUID = 4178108327705020803L;
	
	/**
	 * String format for plain output.
	 * @see #toString()
	 */
	private static final String toStringFormat = "Petri-Net: %s%n          places: %s %n     transitions: %s %n   flow-relation: %n%s %n initial marking: %s %n  actual marking: %s %n";

	
	/**
     * A list that contains all resource places.<br>
     * 资源库所集合
     */
    protected final Collection<PTPlace>  PR = new HashSet<>();
    
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
	 * <pre>
	 * resourceNum = 2,
	 * p0 = {p1}
	 * PA = {p2,p3}
	 * PR = {p4,p5}, p5,p6可以是同一个资源库所，示意图如下
	 * <------ p5 <-------..<------ p4 <-----. 
	 * |                  ||                 |
	 * t1 ----> p2 -----> t2 -----> p3 ----> t3
	 * |                                     |
	 * <------------- p1 <-------------------.
	 * </pre>
	 * @param name 该网名称
	 * @param resourceNum   资源库所个数
	 * @param resourceToken 缺省资源库所的Token
	 * @param p0Token 缺省闲置库所的Token
	 */
	public S2PR(String name,int resourceNum, int resourceToken, int p0Token) {
		// 初始化一个父类对象，S2P对象
		super(name,resourceNum,p0Token); 
		// this S2PR对象对应的S2P对象
		s2p = new S2P(name,resourceNum,p0Token); 
				
		// Initial Marking
		PTMarking marking = s2p.getInitialMarking();
			
		// PR = { resource places }		
		for (PTPlace pa : PA) {
			String pr = lastPlaceName(); // 资源库所name
			addPlace(pr);
			PR.add(getPlace(pr));
			// this pr's transitions: t1,t2
			for (AbstractPNNode<?> t1 : pa.getParents()) {
				for (AbstractPNNode<?> t2 : pa.getChildren()) {
					addFlowRelationPT(pr, t1.getName(), 1);
					addFlowRelationTP(t2.getName(), pr, 1);
				}
			}
			// resource token
			marking.set(pr, resourceToken);
		}
		
		// Marking
		setInitialMarking(marking);
	}
	
	/**
	 * 根据参数，构造S2PR对象
	 * @param name 该网名称
	 * @param ptnet 符合S2PR定义的Petri网对象
	 * @param p0   闲置库所名称
	 * @param PA 工序库所名称集合
	 * @param PR 资源库所名称集合
	 */
	public S2PR(String name,PTNet ptnet, String p0, Collection<String> PA, Collection<String> PR) {
		// 初始化一个父类对象，S2P对象
		super(name,ptnet,p0,PA); 
		
		// this S2PR对象对应的S2P对象
		s2p = new S2P(name,ptnet,p0,PA); 
		
		for (PTTransition t : ptnet.getTransitions()) {
            this.addTransition(t.getName(), false);
        }
        for (PTPlace p : ptnet.getPlaces()) {
            this.addPlace(p.getName(), false);
        }
        for (PTFlowRelation f : ptnet.getFlowRelations()) {
        	//this.addFlowRelation(f, false);  // 错误的，至少应把f克隆了，
        	if (f.getDirectionPT()) {
        		this.addFlowRelationPT(f.getPlace().getName(), f.getTransition().getName(), false);
        	}
        	else {
            	this.addFlowRelationTP(f.getTransition().getName(), f.getPlace().getName(), false);
        	}
        }
        
        for (String pr: PR) {
        	this.PR.add(this.getPlace(pr));
        }
        
        this.setInitialMarking(ptnet.getInitialMarking().clone());
	}
	
	/**
	 *  检查本类(S2PR)对应的父类对象(S2P)是否满足S2P的定义
	 * @see edu.xidian.petrinet.S3PR.S2P#isS2P()
	 */
	@Override
	public boolean isS2P() {
		// this对象（S2PR对象）对应的S2P对象
		Validate.notNull(s2p);
		return s2p.isS2P();
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
		Validate.notNull(p0);
		Validate.notEmpty(PA);
		Validate.notEmpty(PR);
	    Collection<AbstractPNNode> temp1 = new HashSet<>();
		Collection<AbstractPNNode> temp2 = new HashSet<>();
		// 一定做此转换，否则，由于集合集的元素类型不同，导致以下retainAll()函数的返回结果错误
		Collection<AbstractPNNode> PAs = new HashSet<>(PA);
		Collection<AbstractPNNode> PRs = new HashSet<>(PR);
		/** 1. 由X = PA ∪ {p0} ∪ T 生成的子网是S2P */
		// this对象（S2PR对象）对应的S2P对象
		Validate.notNull(s2p);
		if (!s2p.isS2P())
			return false;

		/** 2. PR ≠ ∅ ;, (PA ∪ {p0}) ∩ PR = ∅ */
		if (PR.isEmpty())
			return false;
		temp1.clear(); 
		temp1.addAll(PAs);  // 使用PAs不存在类型转换，优于使用PA
		temp1.add(p0);
		temp1.retainAll(PRs); // 交集
		if (!temp1.isEmpty())
			return false;

		/**
		 * 3. 任意p∈PA, 任意t1∈p的前置集, 任意t2∈p的后置集, 
		 * 存在rp ∈PR, t1的前置集  ∩ PR = t2的后置集  ∩ PR = {rp}
		 **/
		for (PTPlace p : PA) {
			for (AbstractPNNode t1 : p.getParents()) {
				for (AbstractPNNode t2 : p.getChildren()) {
					// (1) t1的前置集 ∩ PR
					temp1.clear(); 
  					temp1.addAll(t1.getParents());
					//temp1.retainAll(PR); // 交集, 错误
	                temp1.retainAll(PRs);  // 交集, 正确, temp1和PRs是相同类型的集合
					// (2) t2的后置集 ∩ PR
					temp2.clear();
					temp2.addAll(t2.getChildren());
					temp2.retainAll(PRs); // 交集
					// (1) = (2) = {1个PR元素}
					if (!temp1.equals(temp2) || temp1.size() != 1)
						return false;
				}
			}
		}

		/**
		 * 4. (a) 任意r∈PR , r的前置集的前置集 ∩ PA = r的后置集的后置集 ∩ PA ≠ ∅; 
		 *    (b) 任意r∈PR, r的前置集 ∩ r后置集 = ∅
		 ***/
		for (PTPlace r : PR) {
			/////////////// (a)
			temp1.clear();
			for (AbstractPNNode node : r.getParents()) {
				temp1.addAll(node.getParents());
			}
			temp1.retainAll(PAs); // r的前置集的前置集 ∩ PA
			
			temp2.clear();
			for (AbstractPNNode node : r.getChildren()) {
				temp2.addAll(node.getChildren());
			}
			temp2.retainAll(PAs); // r的后置集的后置集 ∩ PA
			
			if (!temp1.equals(temp2)) return false;
			if (temp1.isEmpty() || temp2.isEmpty()) return false;
			
			///////////////// (b)
			temp1.clear(); temp2.clear();
			temp1.addAll(r.getParents());
			temp2.addAll(r.getChildren());
			temp1.retainAll(temp2);
			if (!temp1.isEmpty()) return false;
		}

		/** 5. (p0)的前置集的前置集 ∩ PR = (p0)的后置集的后置集 ∩ PR = ∅。 **/
		temp1.clear(); temp2.clear();
		for (AbstractPNNode node : p0.getParents()) {
			temp1.addAll(node.getParents());
		}
		temp1.retainAll(PRs);
		
		for (AbstractPNNode node : p0.getChildren()) {
			temp2.addAll(node.getChildren());
		}
		temp2.retainAll(PRs);
		if (!temp1.isEmpty() || !temp2.isEmpty())
			return false;

		return true;
	}

	/**
	 * 是否满足许可初始标识
	 * Li. p66, 定义4.4,
	 * <pre>
	 * 令N = (PA ∪ {p0} ∪  PR, T, F)是一个S2PR。初始标识M0称为N的许可初始标识当且仅当: 
	 * (1) M0(p0) ≥ 1; (2) M0(p) = 0, 任意p∈PA; (3) M0(r) ≥ 1, 任意r ∈ PR。
	 * </pre>
	 * @return
	 */
	public boolean isInintialMarking() {
		Validate.notNull(p0);
		Validate.notEmpty(PA);
		Validate.notEmpty(PR);
		PTMarking marking = getInitialMarking(); 
		if (marking.get(p0.getName()) < 1) return false;
		for (PTPlace p: PA) {
			if (marking.get(p.getName()) != null) {
			   if (marking.get(p.getName()) != 0) return false;  // M(p)=0时, AbstractPTMarking不记录,即M(p)=null
			}
		}
		for (PTPlace p: PR) {
			if (marking.get(p.getName()) < 1) return false;
		}
		return true;
	}
	
	/**
	 * 获取资源库所名字集
	 * @return
	 */
	public Collection<String> getPRnames() {
		Collection<String> pr = new HashSet<>();
		for (PTPlace p: PR) {
			pr.add(p.getName());
		}
		return pr;
	}
	
	/**
	 * 获取资源库所集
	 * @return
	 */
	public Collection<PTPlace> getPR() {
		return PR;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		StringBuilder relationBuilder = new StringBuilder();
		PNNodeComparator Comparator = new PNNodeComparator();
	    List<AbstractPNNode<?>> list1 = new ArrayList<>(places.values()); 
	    List<AbstractPNNode<?>> list2 = new ArrayList<>(transitions.values()); 
	    Collections.sort(list1,Comparator);
	    Collections.sort(list2,Comparator);
		for(PTFlowRelation relation: relations.values()){
			relationBuilder.append("                  ");
			relationBuilder.append(relation);
			relationBuilder.append('\n');
		}
		str.append(String.format(toStringFormat, name, list1, list2, relationBuilder.toString(), initialMarking, marking));
	    
		str.append("\nS2PR --------" + "\n");
	    str.append("p0: " + p0 + "\n");
	    list1.clear(); list1.addAll(PA); 
	    Collections.sort(list1,Comparator);
	    str.append("PA: " + list1 + "\n"); 
	    list1.clear(); list1.addAll(PR); 
	    Collections.sort(list1,Comparator);
	    str.append("PR: " + list1 + "\n");
	    return String.format("%s", str);
	}
}
