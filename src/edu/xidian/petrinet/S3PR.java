/**
 * 
 */
package edu.xidian.petrinet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;

/**
 * <pre>
 * 简单顺序过程(Simple Sequential Process, S2P)
 * 含有资源的简单顺序过程(S2P with resources, S2PR)继承自S2P，增加了资源库所集
 * S3PR： System of Simple Sequential Process with resources
 * Li. p67 定义4.5
 * 一个S3PR可递归定义如下:
 * 1. 一个S2PR是一个S3PR;
 * 2. 令Ni = (PAi ∪ {p0i} ∪ PRi , Ti, Fi), i ∈  {1,2}是两个S3PR, 使得
 *    (PA1 ∪ {p01}) ∩ (PA2 ∪ {p02}) = ∅,
 *    PR1 ∩ PR2 = PC ≠ ∅; T1 ∩ T2 = ∅;
 *    那么, 由N1和N2通过PC复合而成的Petri网N = (PA ∪ P0 ∪ PR, T, F)仍是S3PR, 定义为: 
 *    (1) PA = PA1 ∪ PA2; (2) P0 = {p01} ∪ {p02}; (3) PR = PR1 ∪  PR2; (4) T = T1 ∪ T2, F = F1 ∪  F2。
 * (P0)后置集中的变迁称为源变迁, 它们表示工件进入系统的输入点。(P0)前置集中的变迁称为汇变迁, 它们表示工件移出系统的输出点。
 * </pre>
 * @author Jiangtao Duan
 *
 */
public class S3PR extends S2PR {

	private static final long serialVersionUID = 589520113153525821L;
	
	/**
	 * 组成S3PR的各个S2PR对象
	 */
	protected final Collection<S2PR> s2prSet = new HashSet<>();
	
	/**
     * 闲置库所集合
     */
    protected final Collection<PTPlace>  P0 = new HashSet<>();

	/**
	 * 
	 */
	public S3PR() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 由一个S2PR对象构造S3PR对象
	 * @param s2pr
	 */
	public S3PR(S2PR s2pr) {
		super(s2pr, s2pr.p0.getName(),s2pr.getPA(),s2pr.getPR());
		s2prSet.add(s2pr);
		P0.add(s2pr.p0);
	}
	
	/**
	 * 由一个添加符合S2PR定义的Petri网对象构造S3PR对象
	 * @param ptnet 符合S2PR定义的Petri网对象
	 * @param p0   闲置库所名称
	 * @param PA 工序库所名称集合
	 * @param PR 资源库所名称集合
	 */
	public S3PR(PTNet ptnet, String p0, Collection<String> PA, Collection<String> PR) {
		S2PR s2pr = new S2PR(ptnet,p0,PA,PR);
		this.add(s2pr);
	}
	
	/**
	 * 添加S2PR对象到本S3PR对象中
	 * @param s2pr
	 */
	public void add(S2PR s2pr) {
		// 把s2pr的闲置库所p0加入到本对象
		this.addPlace(s2pr.p0.getName());
		for (PTPlace p: s2pr.PA) {
			this.addPlace(p.getName());
		}
		
		// 共享资源库所，已经在本对象中，因此不用添加
		Collection<PTPlace> PC = new HashSet<>();
		PC.addAll(this.PR);
		PC.retainAll(s2pr.PR);
		
		// s2pr.PR与PC的差集，即s2pr.PR \ PC， 将其加入到本对象中
		Collection<PTPlace> PC1 = new HashSet<>();
		PC1.addAll(s2pr.PR);
		PC1.removeAll(PC);
		for (PTPlace p: PC1) {
			this.addPlace(p.getName());
		}
		
		// T = T1 ∪ T2, 把s2pr的Transitions加入到本对象
		for (PTTransition t: s2pr.getTransitions()) {
			this.addTransition(t.getName());
		}
		
		// F = F1 ∪  F2, 把s2pr的FlowRelations加入到本对象
		for (PTFlowRelation f : s2pr.getFlowRelations()) {
        	if (f.getDirectionPT()) {
        		this.addFlowRelationPT(f.getPlace().getName(), f.getTransition().getName());
        	}
        	else {
            	this.addFlowRelationTP(f.getTransition().getName(), f.getPlace().getName());
        	}
        }
		
		// PA = PA1 ∪ PA2;
		PA.addAll(s2pr.PA); // 不会有重复元素，(PA1 ∪ {p01}) ∩ (PA2 ∪ {p02}) = ∅,
		
		// PR = PR1 ∪  PR2;
		PR.addAll(s2pr.PR); // 不会有重复元素，因为Set<>中重复元素添加不进去 
		
		// P0 = {p01} ∪ {p02};
		P0.add(s2pr.p0);    // 不会有重复元素，(PA1 ∪ {p01}) ∩ (PA2 ∪ {p02}) = ∅,
		s2prSet.add(s2pr);	
	}
	
	/**
	 * 添加符合S2PR定义的Petri网对象到本S3PR对象中
	 * @param ptnet 符合S2PR定义的Petri网对象
	 * @param p0   闲置库所名称
	 * @param PA 工序库所名称集合
	 * @param PR 资源库所名称集合
	 */
	public void add(PTNet ptnet, String p0, Collection<String> PA, Collection<String> PR) {
		S2PR s2pr = new S2PR(ptnet,p0,PA,PR);
		add(s2pr);
	}
	
	/**
	 * r∈PR, 使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
     * @param r 资源库所
	 * @return r的持有者集合
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<PTPlace> getHr(PTPlace r) {
		Set<PTPlace> Hr = new HashSet<>();
		Set<AbstractPNNode<PTFlowRelation>> nodes = r.getParents();
		for (AbstractPNNode node: nodes) {
			Hr.addAll(node.getParents());
		}
		Hr.retainAll(PA);
		return Hr;
	}

	/**
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, S<sub>A</sub>表示S中的工序库所集合.
	 * @return S3PR网所有资源的持有者集合， ∪<sub>r∈S<sub>R</sub></sub>(H(r))
	 */
	public Set<PTPlace> getHr(Set<PTPlace> SR) {
		Set<PTPlace> Hrs = new HashSet<>();
		for (PTPlace r: SR) {
			Hrs.addAll(getHr(r));
		}
		return Hrs;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
	    String superString = super.toString();
	    str.append("S3PR --------" + "\n");
	    str.append("P0: " + P0 + "\n"); 
	    str.append("PA: " + PA.toString() + "\n");
	    str.append("PR: " + PR.toString() + "\n");
	    return String.format("%s%n%s", superString, str);
	}
}
 