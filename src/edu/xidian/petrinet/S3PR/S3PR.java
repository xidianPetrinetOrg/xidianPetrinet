/**
 * 
 */
package edu.xidian.petrinet.S3PR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import edu.xidian.math.InvariantMatrix;
import edu.xidian.petrinet.S3PR.RGraph.REdge;
import edu.xidian.petrinet.S3PR.RGraph.RGraph;
import edu.xidian.petrinet.Utils.PNNodeComparator;

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
     * 该网对应的资源有向图
     */
    protected final RGraph Rgraph = new RGraph("Resource Directed Graph");
    
    /**
     * 信标集合，为了与补集对应，必须是有序列表
     */
    protected final List<Collection<PTPlace>> Siphons = new ArrayList<>();
    
    /**
     * 信标的补集集合，为了与信标对应，必须是有序列表
     */
    protected final List<Collection<PTPlace>> SiphonComs = new ArrayList<>();


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
	 * 即, [S] = getHr(S<sub>R</sub>)\S, S<sub>R</sub>表示信标S中的资源库所集合
	 * 信标的补集是由工序库所组成的，具有明确的物理含义。信标补集中的库所和信标中的工序库所竞争信标中的资源
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
	 * 即, [S] = getHr(S<sub>R</sub>)\S, S<sub>R</sub>表示信标S中的资源库所集合
	 * 信标的补集是由工序库所组成的，具有明确的物理含义。信标补集中的库所和信标中的工序库所竞争信标中的资源
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
	 * 信标的补集是由工序库所组成的，具有明确的物理含义。信标补集中的库所和信标中的工序库所竞争信标中的资源。
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
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。
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
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
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
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
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
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
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
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
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
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
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
	
	/**
	 * Li. p68, 性质4.1
	 * <pre>
	 * 令N = O<sub>i=1</sub><sup style="margin-left:-5px">n</sup>N<sub>i</sub> = (P0  ∪ PA  ∪ PR, T, F)是包含n个简单顺序过程的S3PR。
     * 1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0};
     * 2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r);
     * 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\{r}, p ∉ H(r1);
     * <b>4. [S] ∪ S是N的P-半流的支撑; 
     *    小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则
     *    [S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。 </b>
     * 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
     * </pre>
     * @param SR 信标S中资源库所集合
     * @return Is = ∑<sub>r∈SR</sub>Ir
	 */
	public Collection<PTPlace> getIs(Collection<PTPlace> SR) {
		Collection<PTPlace> Is = new HashSet<>();	
		for (PTPlace r: SR) {
			Is.addAll(getIr(r));
		}
		return Is;
	}
	
	/**
	 * 获取该网的资源有向图
	 * Wang. p88, 定理4.4 初始资源有向图D0 = （V0，E0）
	 * @param verbose 是否打印输出
	 * @return
	 */
	public RGraph getRgraph(boolean verbose) {
		// 如果已经生成资源有向图, 直接返回, 否则生成之。
		if (Rgraph.nodeCount() != 0) return Rgraph;
		
		for (PTPlace pr_i: PR) {
			for (PTPlace pr_j: PR) {
				if (pr_i.equals(pr_j)) continue;
				// pr_j的前置集
				Collection<AbstractPNNode<PTFlowRelation>> prePr_j = pr_j.getParents();
				// pr_i的后置集
				Collection<AbstractPNNode<PTFlowRelation>> postPr_i = pr_i.getChildren();
				// 二者的交集
				Collection<AbstractPNNode<PTFlowRelation>> transitions = new HashSet<>();
				transitions.addAll(prePr_j);
				transitions.retainAll(postPr_i);
				if (!transitions.isEmpty()) {
					for (AbstractPNNode<PTFlowRelation> t: transitions) {
						// Rgraph.addVertex(pr_i.getName()); // 使用此函数，在graph中无vertex对象
						Rgraph.addVertex(pr_i.getName(),pr_i);
						Rgraph.addVertex(pr_j.getName(),pr_j);
						try {
							Rgraph.addREdge(t.getName(),pr_i.getName(), pr_j.getName());
						} catch (VertexNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		if (verbose) {
			System.out.println(Rgraph);
		}
		return Rgraph;
	}
	
	
	/**
	 * <pre>
	 * 计算严格极小信标（SMS）
	 * Wang p91, 定理4.7： 设N是一个LS3PR网，D0[Ω] = (Ω,E)
	 * 信标的补集：式（4-7） [S] = {p | {p}=(t的前置集) ∩ PA ∧ e<sub>t</sub> ∈ E}
	 * 其中：PA是工序库所集合
	 * 信标：式（4-2） S = ‖Is‖ \ [S]
	 * 其中：Is = ∑<sub>r∈SR</sub>Ir. SR是S中资源库所集合，SR = Ω;
	 * Is由getIs(SR)函数求取。
	 * 定理4.9：D0[Ω]=(V,E)是D0的Ω导出子图，S是D0[Ω]对应的信标。S是一个SMS当且仅当D0[Ω]强连通且|Ω|>=2.
	 * 定理4.11：C-矩阵中的非全0列数α是由D0中的所有强分图-D0<sup>1</sup>,D0<sup>2</sup>,...,D0<sup>k</sup>共同确定的，且满足以下关系式：
	 * α = ∑|E(D0<sup>i</sup>)|,i=1,2,...k
	 * C-矩阵中互不相同的非全0列的个数记作δ（delta），rank([C])<=δ<=α
	 * <b>算法4.1：删除0点法（确定α）
	 *    计算信标及其补集，记录至Siphons和SiphonComs成员，外部可以通过getSiphons()和getSiphonsComs()获取
	 *    [C]-矩阵(信标补集矩阵)，alpha，delta可以通过rank_alpha_delta()获取
	 * 算法4.2：删除1点法（确定δ）
	 * </pre>
	 * @param verbose 是否打印输出
	 */
	public void algorithm4_1(boolean verbose) {
		// [C]-矩阵中的非全0列数α, 与rank_alpha_delta(Cmatrix)的返回值一致
		// int alpha = 0;
		// 获取资源有向图
		getRgraph(verbose);
		// 计算强连通分量
		Collection<RGraph> components = Rgraph.getStronglyConnectedComponentGraphs(verbose);
		
		for (RGraph com: components) {
			Collection<PTPlace> S = new HashSet<>();  // 信标
			Collection<PTPlace> SCom = new HashSet<>(); // 信标补集
			if (Siphon_Com(com,S,SCom)) {
				Siphons.add(S);
				SiphonComs.add(SCom);
				if (verbose) {
					printPNNodes("Siphon:      ", S);
					printPNNodes("SiphonCom:   ", SCom);
				}
				// [C]-矩阵中的非全0列数α, 与rank_alpha_delta(Cmatrix)的返回值一致
				// alpha += com.getEdgeCount() + com.getParallelEdges().size();
			}
		}
		
		if (verbose) {
			// 信标补集矩阵
			InvariantMatrix Cmatrix = CMatrix();
			int rank_alpha_delta[] = rank_alpha_delta(Cmatrix);
			System.out.println("C-Matrix:");
			Cmatrix.print(2, 0);
			System.out.println("Cmatrix rank,alpha,delta = " + 
			   rank_alpha_delta[0] + "," + rank_alpha_delta[1] + "," + rank_alpha_delta[2]);
		}
	}
	
	/**
	 * <pre>
	 * 计算严格极小信标（SMS）
	 * Wang p91, 定理4.7： 设N是一个LS3PR网，D0[Ω] = (Ω,E)
	 * 信标的补集：式（4-7） [S] = {p | {p}=(t的前置集) ∩ PA ∧ e<sub>t</sub> ∈ E}
	 * 其中：PA是工序库所集合
	 * 信标：式（4-2） S = ‖Is‖ \ [S]
	 * 其中：Is = ∑<sub>r∈SR</sub>Ir. SR是S中资源库所集合，SR = Ω;
	 * Is由getIs(SR)函数求取。
	 * 定理4.9：D0[Ω]=(V,E)是D0的Ω导出子图，S是D0[Ω]对应的信标。S是一个SMS当且仅当D0[Ω]强连通且|Ω|>=2.
	 * 定理4.11：C-矩阵中的非全0列数α是由D0中的所有强分图-D0<sup>1</sup>,D0<sup>2</sup>,...,D0<sup>k</sup>共同确定的，且满足以下关系式：
	 * α = ∑|E(D0<sup>i</sup>)|,i=1,2,...k
	 * C-矩阵中互不相同的非全0列的个数记作δ（delta），rank([C])<=δ<=α
	 * 算法4.1：删除0点法（确定α）
	 * 算法4.2：删除1点法（确定δ）
	 * </pre>
	 * @param verbose 是否打印输出
	 */
	public void algorithm4_2(boolean verbose) {
		// 算法4.1：删除0点法（确定α）
		System.out.println(" 算法4.1：删除0点法（确定α）");
		Collection<RGraph> components = Component(getRgraph(verbose),verbose);
		
		// 算法4.2：删除1点法（确定δ）
		System.out.println(" 算法4.2：删除1点法（确定δ）");
		for (RGraph com : components) {
			if (com.getVertexCount() >= 2) {
				for (String v: com.getVertexNames()) {
					RGraph cloneCom = com.clone();
					try {
						cloneCom.removeVertex(v); // 删除1点
						Component(cloneCom,verbose);
					} catch (VertexNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		if (verbose) {
			System.out.println("=======================");
			int i = 1;
			for (Collection<PTPlace> siphon: Siphons) {
				printPNNodes("Siphons[" + i + "]    = ", siphon);
				i++;
			}
			i = 1;
			for (Collection<PTPlace> siphonCom: SiphonComs) {
				printPNNodes("SiphonComs[" + i + "] = ", siphonCom);
				i++;
			}
		}
	}
	
	/**
	 * 计算资源有向图或他的强联通分量的信标及其补集
	 * @param component 资源有向图或他的强联通分量
	 * @param verbose 是否打印输出
	 * @return 强联通分量
	 */
	protected Collection<RGraph> Component(RGraph component, boolean verbose) {
		Collection<RGraph> components = component.getStronglyConnectedComponentGraphs(verbose);
		for (RGraph com : components) {
			Collection<PTPlace> S = new HashSet<>(); // 信标
			Collection<PTPlace> SCom = new HashSet<>(); // 信标补集
			if (Siphon_Com(com, S, SCom)) {
				Siphons.add(S);
				SiphonComs.add(SCom);
				if (verbose) {
					printPNNodes("Siphon:      ", S);
					printPNNodes("SiphonCom:   ", SCom);
				}
			}
		}
		return components;
	}

	/**
	 * 计算资源有向图的强连通分量对应的信标及其补集，每个强连通分量对应一个信标补集和信标, 顶点集是SR(信标S中的资源库所集合)
	 * @param component 强连通分量
	 * @param S 返回信标
	 * @param SCom 返回信标的补集
	 * @return true: 有信标及其补集; false：无，强连通分量的的顶点个数<2
	 */
	@SuppressWarnings("rawtypes")
	protected boolean Siphon_Com(RGraph component, Collection<PTPlace> S, Collection<PTPlace> SCom) {		 
		int num = component.getVertexCount();
		if (num < 2) return false; // 非SMS,至少资源库所个数>=2 
	
		Collection<PTPlace> intersection = new HashSet<>();
		// 边集，包含平行边
		Collection<REdge> allEdges = new ArrayList<>(component.getEdges());
		allEdges.addAll(component.getParallelEdges()); // 平行边
		for (REdge edge: allEdges) {
			for(AbstractPNNode p: getTransition(edge.getName()).getParents()) {
				intersection.add((PTPlace) p);
			}
			intersection.retainAll(PA);  
			SCom.addAll(intersection);  // wang (4-7)，信标补集
		}
		// 参数是强连通分量的顶点集，构成信标S中资源库所集合SR
		Collection<PTPlace> Is = getIs(component.getElementSet());
		S.addAll(Is);
		S.removeAll(SCom); // wang (4-2)，信标
		return true;
	}
		
	/**
	 * 信标集合
	 * @return
	 */
	public List<Collection<PTPlace>> getSiphons() {
		return Siphons;
	}

	/**
	 * 信标的补集集合
	 * @return
	 */
	public List<Collection<PTPlace>> getSiphonComs() {
		return SiphonComs;
	}
	
	/**
	 * <pre>
	 * 补集矩阵（C矩阵，[C]矩阵
	 * Li. p44, 定义3.1  令S（是P的子集）是Petri网N = (P,T,F,W)的库所子集。
	 * P-向量λs称为S的特征P-向量，当且仅当任意p∈S, λs(p) = 1, 否则λs(p) = 0。
	 * 定义3.2  令S（是P的子集）是Petri网N = (P,T,F,W)的库所子集。
	 * [N]<sup>T</sup>是Petri网关联矩阵[N]的转置。
	 * η<sub>s</sub>=[N]<sup>T</sup>λs称为S的特征T-向量。
	 * wang. p83, 定义4.3: 设N = 是一个S3PR网，Π是网N的严格极小信标集合，则称
	 * [C]<sub>|Π|×|P|</sub>= [λs1|λs2|...]<sup>T</sup>
	 * 是网N的严格极小信标的补集的特征P−向量矩阵，简称为补集矩阵，或[C]−矩阵，其中P = P0∪PA∪PR。
	 * 
	 * 根据algorithm4_1()或algorithm4_2计算所得的SiphonComs，生成[C]矩阵
	 * </pre>
	 */
	public InvariantMatrix CMatrix() {
		int m,n; // 行，列，|Π|，|P|
		m = SiphonComs.size(); 
		n = getPlaceCount();
		InvariantMatrix cmatrix = new InvariantMatrix(m,n);
		int i = 0;
		for(Collection<PTPlace> sCom: SiphonComs) {
			for (PTPlace p: sCom) {
				int lambda = toInt(p.getName().toString()) - 1; // toString()形成，如p20[p20]
				cmatrix.set(i, lambda, 1);
			}
			i++;
		}
		return cmatrix;
	}
	
	/**
	 *  C-矩阵中的非全0列数α(alpha), C-矩阵中互不相同的非全0列的个数记作δ(delta),
	 *  rank([C])<=δ<=α
	 *  @return result[0],result[1],result[2]代表矩阵a的秩,alpha,delta;
	 */
	public int[] rank_alpha_delta(InvariantMatrix a) {
		int result[] = new int[3];
		result[0] = a.rank();
		int m = a.getRowDimension();    
		int n = a.getColumnDimension();
		
		// 标记全0列，不参与比较
		int zeroColumn[] = new int[n];
		for (int j = 0; j < n; j++) { zeroColumn[j] = 0; }
		
		// alpha
		result[1] = 0;  
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < m; i++) {
				if (a.get(i,j) != 0) {
					result[1]++;
					zeroColumn[j] = 1;  // 置为非全0列
					break;
				}
			}
		}
		
		// delta初值 = alpha
		result[2] = result[1];  
		for (int j = 0; j < n; j++) {
			if (zeroColumn[j] == 0) continue; // 全0列不参与比较
			for (int j1 = j + 1; j1 < n; j1++) {
				if (zeroColumn[j1] == 0) continue; // 全0列不参与比较
				boolean same = true;  // 相同列
				for (int i = 0; i < m; i++) {
					if (a.get(i,j) != a.get(i, j1)) {
						same = false; // 非相同列
						break;
					}
				}
				if (same) { // 相同列
					zeroColumn[j1] = 0;  // 标记为全0列，目的是下一次该列不作为j,参与上层循环再进行比较。
					result[2]--;         // 相同列delta--
				}  
			}
		}
		return result;
	}
	
	/**
	 * '['以前的字符串转int，例如："p20[p20]" ==> 20 
	 * @param s
	 * @return
	 */
	private int toInt(String s) {
		char c;
		StringBuilder str = new StringBuilder(); 
        for(int i = 0; i<s.length(); i++){
        	c = s.charAt(i);
        	if (c == '[' || c == '(' || c == '\r' || c == '\n') break;
        	if (c >= '0' && c <= '9') str.append(c);
        }
        try {
			return Integer.parseInt(str.toString());
		} catch (NumberFormatException e) {
			//e.printStackTrace();
			return 0;
		}
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
 