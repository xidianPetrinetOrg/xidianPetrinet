/**
 * 
 */
package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	 * String format for plain output.
	 * @see #toString()
	 */
	private static final String toStringFormat = "Petri-Net: %s%n          places: %s %n     transitions: %s %n   flow-relation: %n%s %n initial marking: %s %n  actual marking: %s %n";

	/**
	 * 组成S3PR的各个S2PR对象,以S2PR网名称索引
	 */
	protected Map<String, S2PR> s2prSet = new HashMap<>();
	
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
	 * @param name 该S3PR网名称
	 * @param s2pr_name s2pr网名称
	 * @param s2pr
	 */
	public S3PR(String name, String s2pr_name, S2PR s2pr) {	
		// 调用另一个构造函数，初始化本类
		this(name,s2pr_name,s2pr, s2pr.p0.getName(),s2pr.getPAnames(),s2pr.getPRnames());
	}
	
	/**
	 * 由一个添加符合S2PR定义的Petri网对象构造S3PR对象
	 * @param name 该S3PR网名称
	 * @param s2pr_name S2PR网名称
	 * @param ptnet 符合S2PR定义的Petri网对象
	 * @param p0   闲置库所名称
	 * @param PA 工序库所名称集合
	 * @param PR 资源库所名称集合
	 */
	public S3PR(String name, String s2pr_name, PTNet ptnet, String p0, Collection<String> PA, Collection<String> PR) {
		// 初始化父类对象
		super(name,ptnet, p0, PA, PR);
		S2PR s2pr = new S2PR(s2pr_name,ptnet,p0,PA,PR);
		this.add(s2pr_name,s2pr);
	}
	
	/**
	 * 添加S2PR对象到本S3PR对象中
	 * (1) 把s2pr的闲置库所p0加入到本对象，并且加入到本对象的P0中(P0 = {p01} ∪ {p02})
	 * (2) 把s2pr的工序库所集PA加入到本对象中，并且添加到本对象的PA中(PA = PA1 ∪ PA2)
	 * (3) 把s2pr的Transitions加入到本对象中, T = T1 ∪ T2
	 * (4) 共享资源库所集PC = PR1 ∩ PR2，即 PC = this.PR ∩ s2pr.PR
	 *     s2pr.PR与PC的差集PC1 = s2pr.PR \ PC 
	 *     把PC1加入到本对象中,同时，将其加入到this.PR中。即PR = PR1  ∪  PR2 = this.PR ∪ PC1
	 * (5) 把s2pr的FlowRelations加入到本对象中, F = F1 ∪  F2
	 * (6) 把s2pr对象添加到s2prSet中 
	 * @param s2pr_name S2PR网名称 
	 * @param s2pr
	 */
	public void add(String s2pr_name,S2PR s2pr) {
		// (1) 把s2pr的闲置库所p0加入到本对象，并且加入到本对象的P0中(P0 = {p01} ∪ {p02})
		String pName = s2pr.p0.getName();
		this.addPlace(pName);
		// P0 = {p01} ∪ {p02};
		this.P0.add(this.getPlace(pName)); 
		
		// (2) 把s2pr的工序库所PA加入到本对象中，并且添加到本对象的PA中(PA = PA1 ∪ PA2)
		for (PTPlace p: s2pr.PA) {
			pName = p.getName();
			this.addPlace(pName);
			// PA = PA1 ∪ PA2;  
			this.PA.add(this.getPlace(pName));
		}
		
		// (3) 把s2pr的Transitions加入到本对象中, T = T1 ∪ T2
		for (PTTransition t : s2pr.getTransitions()) {
			this.addTransition(t.getName());
		}
		
		// (4) 共享资源库所集PC = PR1 ∩ PR2，即 PC = this.PR ∩ s2pr.PR
	    //     s2pr.PR与PC的差集PC1 = s2pr.PR \ PC 
		//     把PC1加入到本对象中,同时，将其加入到this.PR中。PR = PR1  ∪  PR2 = this.PR ∪ PC1
		Collection<PTPlace> PC = new HashSet<>();
		PC.addAll(this.PR);
		PC.retainAll(s2pr.PR);  // PC = this.PR ∩ s2pr.PR	
		// s2pr.PR与PC的差集，即s2pr.PR \ PC， 将其加入到本对象中
		Collection<PTPlace> PC1 = new HashSet<>();
		PC1.addAll(s2pr.PR);
		PC1.removeAll(PC);
		for (PTPlace p: PC1) {
			pName = p.getName();
			this.addPlace(pName);
			this.PR.add(this.getPlace(pName));
		}
		
		// (5) 把s2pr的FlowRelations加入到本对象中, F = F1 ∪  F2
		for (PTFlowRelation f : s2pr.getFlowRelations()) {
        	if (f.getDirectionPT()) {
        		this.addFlowRelationPT(f.getPlace().getName(), f.getTransition().getName());
        	}
        	else {
            	this.addFlowRelationTP(f.getTransition().getName(), f.getPlace().getName());
        	}
        }
		
		// (6) 把s2pr对象添加到s2prSet中
		s2prSet.put(s2pr_name, s2pr);	
		//System.out.println("-----"+s2prSet.keySet());
	}
	
	/**
	 * 添加符合S2PR定义的Petri网对象到本S3PR对象中
	 * @param name S2PR网名称
	 * @param ptnet 符合S2PR定义的Petri网对象
	 * @param p0   闲置库所名称
	 * @param PA 工序库所名称集合
	 * @param PR 资源库所名称集合
	 */
	public void add(String name,PTNet ptnet, String p0, Collection<String> PA, Collection<String> PR) {
		S2PR s2pr = new S2PR(name,ptnet,p0,PA,PR);
		add(name,s2pr);
	}
	
	/**
	 * Li. p67, 定义4.7
	 * <pre>
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。
	 * 使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * H(r)必然是places，不是transitions
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, 
	 * S<sub>A</sub>表示S中的工序库所集合.
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
			Hr.addAll(node.getParents()); // H(r)必然是places，不是transitions，因此Hr的类型说明正确
		}
		Hr.retainAll(PA);
		return Hr;
	}

	/**
	 * Li. p67, 定义4.7
	 * <pre>
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。
	 * 使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * H(r)必然是places，不是transitions
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, 
	 * S<sub>A</sub>表示S中的工序库所集合.
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
	 * 令r∈PR是S3PR网N = (P0  ∪ PA ∪ PR,T,F)的资源库所, S是N的严格极小信标。
	 * 使用r的工序库所称为r的持有者, 集合H(r) = ((r的前置集的前置集) ∩ PA) 称为r的持有者的集合.
	 * H(r)必然是places，不是transitions
	 * S = S<sub>R</sub> ∪ S<sub>A</sub>, 其中S<sub>R</sub>表示S中的资源库所集合，且|S<sub>R</sub>|≥2, 
	 * S<sub>A</sub>表示S中的工序库所集合.
	 * 令[S] = ∪<sub>r∈S<sub>R</sub></sub>(H(r))\S,[S]称为信标S的补集(complementary set of siphon S)
	 * 即, [S] = getHr(S<sub>R</sub>)\S, S<sub>R</sub>表示信标S中的资源库所集合
	 * </pre>
	 * @param 
	 * @param S 信标(siphon)
	 * @return 信标S的补集[S]. 
	 */
	public Collection<PTPlace> getSiphonCom(Collection<PTPlace> S) {
		Collection<PTPlace> SR = getSR(S); // S<sub>R</sub>表示信标S中的资源库所集合
		Collection<PTPlace> SiphonCom = new HashSet<>();
		SiphonCom.addAll(getHr(SR));
		SiphonCom.removeAll(S); // S与SiphonCom的类型说明一致，因此removeAll()返回正确的值
		return SiphonCom;
	}
	
	/**
	 * 信标S中资源库所集合
	 * @param S 信标
	 * @return 信标S中资源库所集合
	 */
	public Collection<PTPlace> getSR(Collection<PTPlace> S) {
		Collection<PTPlace> SR = new HashSet<>();
		SR.addAll(S);
		SR.retainAll(PR);
		return SR;
	}
	
	/**
	 * 以名字获取组成S3PR的S2PR对象
	 * @param petriNetName
	 * @return
	 */
	public S2PR getS2pr(String petriNetName) {
		return s2prSet.get(petriNetName);
	}
	
	/**
	 * 组成S3PR的S2PR对象
	 * @param petriNetName
	 * @return
	 */
	public  Map<String, S2PR> getS2pr() {
		return s2prSet;
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * 1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};
     * 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
     * </pre>
	 */
	public void s3pr4_1() {
		Collection<PTPlace> places = new HashSet<>();
		printPNNodes("P0: ",P0);
		printPNNodes("PR: " ,PR);
		
		// 1. 任何 p∈PAi(一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};
		System.out.println("1. 任何 p∈PAi(一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0}");
		int i = 1;
		for (S2PR s2pr: s2prSet.values()) {
			places.clear();
			System.out.println("===S2PR[" + i + "]:");
			printPNNodes("PA: ",s2pr.PA);
			places.addAll(s2pr.PA);
			places.add(s2pr.p0);
			printPNNodes("闲置库所(" + s2pr.p0.getName() + ")对应的极小P-半流: ",places);
			i++;
		}
		// 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
		System.out.println("2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r)");
		for (PTPlace pr: PR) {
			places.clear();
			places.add(pr);
			places.addAll(getHr(pr));
			printPNNodes("资源库所(" + pr.getName() + ")对应的极小P-半流: ",places);
		}
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * <b>1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};</b>
     * 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] \ PAi。
     * </pre>
     * @return S2PR对应的Ip
	 */
	public Collection<PTPlace> getIp(String s2prName) {
		Collection<PTPlace> places = new HashSet<>();
		S2PR s2pr = getS2pr(s2prName);
		places.addAll(s2pr.PA);
		places.add(s2pr.p0);
		return places;
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * <b>1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};</b>
     * 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] \ PAi。
     * </pre>
     * @retun p0对应的Ip
	 */
	public Collection<PTPlace> getIp(PTPlace p0) {
		Collection<PTPlace> places = new HashSet<>();
		for (S2PR s2pr: s2prSet.values()) {
			if ((s2pr.p0).equals(p0)) {
			   places.addAll(s2pr.PA);
			   places.add(s2pr.p0);
			   break;
			}
		}
		return places;
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * <b>1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};</b>
     * 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] \ PAi。
     * </pre>
     * @return 映射集：以p0名字索引的Ip
	 */
	public Map<String,Collection<PTPlace>> getIp() {
		Map<String,Collection<PTPlace>> Ips = new HashMap<>();
		for (S2PR s2pr: s2prSet.values()) {
			Collection<PTPlace> Ip = new HashSet<>();
			Ip.addAll(s2pr.PA);
			Ip.add(s2pr.p0);
			Ips.put((s2pr.p0).getName(),Ip);
		}
		return Ips;
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * 1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};
     * <b>2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r); </b>
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] \ PAi。
     * </pre>
     * @return pr对应的Ir
	 */
	public Collection<PTPlace> getIr(PTPlace pr) {
		Collection<PTPlace> places = new HashSet<>();
		places.add(pr);
		places.addAll(getHr(pr));
		return places;
	}
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * 1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};
     * <b>2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r); </b>
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * 4. [S] ∪ S是N的P-半流的支撑;
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] \ PAi。
     * </pre>
     * @return 映射集: 以资源库所的名字索引的Ir
	 */
	public Map<String, Collection<PTPlace>> getIr() {
		Map<String,Collection<PTPlace>> Irs = new HashMap<>();	
		for (PTPlace pr: PR) {
			Collection<PTPlace> Ir = new HashSet<>();
			Ir.add(pr);
			Ir.addAll(getHr(pr));
			Irs.put(pr.getName(), Ir);
		}
		return Irs;
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
		
	    str.append("\nS3PR --------" + "\n");
	    list1.clear();  list1.addAll(P0); 
	    Collections.sort(list1,Comparator);
	    str.append("P0: " + list1 + "\n"); 
	    list1.clear(); list1.addAll(PA); 
	    Collections.sort(list1,Comparator);
	    str.append("PA: " + list1 + "\n");
	    list1.clear(); list1.addAll(PR); 
	    Collections.sort(list1,Comparator);
	    str.append("PR: " + list1 + "\n");
	    return String.format("%s", str);
	}
}
 