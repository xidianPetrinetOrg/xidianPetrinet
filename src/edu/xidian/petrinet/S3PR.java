/**
 * 
 */
package edu.xidian.petrinet;

import java.util.Collection;
import java.util.HashSet;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;

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
 *    PR1 ∩ PR2 = PC ≠ ∅ ;, T1 ∩ T2 = ∅;
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
	 * 
	 */
	public S3PR() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 有一个S2PR对象构造S3PR对象
	 * @param s2pr
	 */
	public S3PR(S2PR s2pr) {
		super(s2pr, s2pr.p0.getName(),s2pr.getPA(),s2pr.getPR());
		s2prSet.add(s2pr);	
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
