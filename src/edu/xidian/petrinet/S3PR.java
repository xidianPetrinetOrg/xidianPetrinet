/**
 * 
 */
package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
	 * Li. p67, 定义4.7
	 * <pre>
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, S<sub>A</sub>表示S中的工序库所集合.
	 * 令[S] = ∪<sub>r∈S<sub>R</sub></sub>(H(r))\S,[S]称为信标S的补集(complementary set of siphon S)
	 * </pre>
     * @param r 资源库所
	 * @return r的持有者集合,是工序库所PA的子集
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<PTPlace> getHr(PTPlace r) {
		Collection<PTPlace> Hr = new HashSet<>();
		Collection<AbstractPNNode<PTFlowRelation>> nodes = r.getParents();
		for (AbstractPNNode node: nodes) {
			Hr.addAll(node.getParents());
		}
		Hr.retainAll(PA);
		return Hr;
	}

	/**
	 * Li. p67, 定义4.7
	 * <pre>
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, S<sub>A</sub>表示S中的工序库所集合.
	 * 令[S] = ∪<sub>r∈S<sub>R</sub></sub>(H(r))\S,[S]称为信标S的补集(complementary set of siphon S)
	 * </pre>
	 * @param SR S<sub>R</sub>表示信标S中的资源库所集合
	 * @return S3PR网, 信标资源库所集合S<sub>R</sub>的所有资源的持有者集合， ∪<sub>r∈S<sub>R</sub></sub>(H(r))，是工序库所的子集
	 */
	public Collection<PTPlace> getHr(Collection<PTPlace> SR) {
		Collection<PTPlace> Hrs = new HashSet<>();
		for (PTPlace r: SR) {
			Hrs.addAll(getHr(r));
		}
		return Hrs;
	}
	
	/**
	 * Li. p67, 定义4.7
	 * <pre>
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, S<sub>A</sub>表示S中的工序库所集合.
	 * 令[S] = ∪<sub>r∈S<sub>R</sub></sub>(H(r))\S,[S]称为信标S的补集(complementary set of siphon S)
	 * </pre>
	 * @param SR S<sub>R</sub>表示信标S中的资源库所集合
	 * @param S 信标(siphon)
	 * @return 信标S的补集[S]. 
	 */
	public Collection<PTPlace> getSiphonCom(Collection<PTPlace> SR, Collection<PTPlace> S) {
		Collection<PTPlace> SiphonCom = new HashSet<>();
		SiphonCom.addAll(getHr(SR));
		SiphonCom.removeAll(S);
		return SiphonCom;
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * 1. 任何 p∈PAi 都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};
     * 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] \ PAi。
     * </pre>
	 */
	public void s3pr4_1() {
		for (S2PR s2pr: s2prSet) {
			System.out.println("s2pr====" + s2pr);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
	    String superString = super.toString();
	    str.append("\nS3PR --------" + "\n");
	    PTPlaceComparator Comparator = new PTPlaceComparator();
	    List<PTPlace> list = new ArrayList<>(P0); 
	    Collections.sort(list,Comparator);
	    str.append("P0: " + list + "\n"); 
	    list.clear(); list.addAll(PA); 
	    Collections.sort(list,Comparator);
	    str.append("PA: " + list + "\n");
	    list.clear(); list.addAll(PR); 
	    Collections.sort(list,Comparator);
	    str.append("PR: " + list + "\n");
	    return String.format("%s%n%s", superString, str);
	}
	
	/**
	 * 按照PTPlace的name排序
	 */
	private class PTPlaceComparator implements Comparator<PTPlace> {
		@Override
		public int compare(PTPlace o1, PTPlace o2) {
			String s1 = o1.getName();
			String s2 = o2.getName();
			return s1.compareTo(s2);
		}
	}
}
 