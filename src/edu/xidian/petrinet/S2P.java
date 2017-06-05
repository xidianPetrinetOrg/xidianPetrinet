/**
 * 
 */
package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;

/**
 * 	Li. p65, 定义4.2
 * <pre>
 *  一个简单顺序过程(S2P-Simple Sequential Process)是Petri网N = (PA U {p0},T,F), 这里: 
 * (1) PA是非空库所集合 ;称为工序库所的集合; (2) p0 不属于 PA,称为闲置进程库所或简称闲置库所;
 * (3) N是强连通的状态机;              (4) N的每一条回路包含库所p0。
 * </pre>
 * @author Jiangtao Duan
 *
 */
public class S2P extends PTNet {
	private static final long serialVersionUID = -6989468360233312759L;
	/**
     * A list that contains all state places of state machine.<br>
     * 工序库所（或称为操作库所）集合, 不包含{p0}
     */
    protected List<PTPlace>  PA = new ArrayList<>();
    
    /**
     * Idle state place闲置库所
     */
    protected PTPlace p0 = null;
	
	/**
	 * Place name's prefix, default "p"
	 */
	private String p_prefix = "p";
	
	/**
	 * Transition name's prefix, default "t"
	 */
	private String t_prefix = "t";
	

	/**
	 * last Place name's suffix, start 1
	 */
	private int p_suffix = 1;
	

	/**
	 * last Transition name's suffix, start 1
	 */
	private int t_suffix = 1;
	
	/**
	 * 
	 */
	public S2P() {
		// TODO Auto-generated constructor stub
	}
		
	/**
	 * 根据工序库所的数量，构造S2P对象
	 * PAnum  = 2,
	 * p0 = {p1}
	 * PA = {p2,p3}
	 * 
	 * t1 ----> p2 -----> t2 -----> p3 ----> t3
	 * |                                     |
	 * <------------- p1 <-------------------.
	 * 
	 * @param PAnum   工序库所个数
	 */
	public S2P(int PAnum) {	
		String p1,p2,t1,t2;
		p1 = lastPlaceName();
		addPlace(p1);    
		p0 = getPlace(p1);  // p0 = p1 
		t1 = lastTransitionName(); 
		addTransition(t1);  // t1
		// 记住第一个Transition的name
		String t0 = t1;
		
		for(int i = 0; i < PAnum; i++) {
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
	}
	
	/**
	 * 根据工序库所的数量和闲置库所Token，构造S2P对象
	 * PAnum  = 2,
	 * p0 = {p1}
	 * PA = {p2,p3}
	 * 
	 * t1 ----> p2 -----> t2 -----> p3 ----> t3
	 * |                                     |
	 * <------------- p1 <-------------------.
	 * 
	 * @param PAnum   工序库所个数
	 * @param p0Token 闲置库所的Token
	 */
	public S2P(int PAnum, int p0Token) {	
		this(PAnum);
		// 设置初始标识
		setInitialMarking(p0Token);
	}
	
	/**
	 * 根据参数，构造S2P对象
	 * @param ptnet
	 * @param p0   闲置库所名称
	 * @param PA 工序库所名称集合
	 */
	public S2P(PTNet ptnet, String p0, Collection<String> PA) {
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
        
        this.setP0(this.getPlace(p0));
        for (String pa: PA) {
        	this.PA.add(this.getPlace(pa));
        }
        
        this.setInitialMarking(ptnet.getInitialMarking().clone());
	}
	
	/**
	 * 满足S2P的定义？
	 * Li. p65, 定义4.2
	 * <pre>
	 * 一个简单顺序过程(S2P-Simple Sequential Process)是Petri网N = (PA U {p0},T,F), 这里: 
	 * (1) PA是非空库所集合 ;称为工序库所的集合; (2) p0 不属于 PA,称为闲置进程库所或简称闲置库所;
	 * (3) N是强连通的状态机;              (4) N的每一条回路包含库所p0。
	 * </pre>
	 * @return
	 */
	public boolean isS2P() {
	    // (1) PA是非空库所集合 ;称为工序库所的集合;
	    if (PA.isEmpty()) return false;
	    
	    // (2) p0 不属于 PA,称为闲置进程库所或简称闲置库所;
	    if (PA.contains(p0)) return false;
	    
	    // (3) N是强连通的状态机; 即仅有一个强连通分量，该分量的所有节点是该网的库所和变迁集合
	    Set<Set<AbstractPNNode<?>>> Components = PetriNetTraversalUtils.getStronglyConnectedComponents(this);
	    System.out.println("Components: " + Components.size() + ",\n" + Components);
	    
	    if (Components.size() != 1) return false;
	    if (Components.containsAll(this.getNodes())) return false;
	    
	    // (4) N的每一条回路包含库所p0。
	    int circuits = PetriNetTraversalUtils.dfsCircuits(this, p0);
	    if (circuits <= 0) return false;
	    ///// 以下保证过p0的回路是N中的所有回路
	    int in = p0.getIncomingRelations().size();
	    int out = p0.getOutgoingRelations().size();
	    int maxDegree = Math.max(in, out); // p0的入弧与出弧个数中大者即为回路个数
	    if (maxDegree != circuits) return false;  
	    for (AbstractPNNode<?> node: this.getNodes()) {
	    	if (node.getParents().size() > circuits) return false;
	    	if (node.getChildren().size() > circuits) return false;
	    }
	     
		return true;
	}
		
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
	    String superString = super.toString();
	    str.append("p0: " + p0 + "\n");  // 等效
	    str.append("PA: " + PA.toString() + "\n");
	    return String.format("%s%n%s", superString, str);
	}
	
	/**
	 * last Place Name: p_prefix + p_suffix++
	 */
	protected String lastPlaceName() {
		return p_prefix + p_suffix++;
	}
	
	/**
	 * last Transition Name: t_prefix + t_suffix++
	 */
	protected String lastTransitionName() {
		return t_prefix + t_suffix++;
	}
	
	/**
	 * current Place Name: p_prefix + p_suffix
	 */
	public String currentPlaceName() {
		return p_prefix + p_suffix;
	}
	
	/**
	 * current Transition Name: t_prefix + t_suffix
	 */
	public String currentTransitionName() {
		return t_prefix + t_suffix;
	}
	
    /**
     * 获取工序库所集
     * @return
     */
	public List<PTPlace> getPA() {
		return PA;
	}

	/**
	 * 设置工序库所集
	 * @param pA
	 */
	public void setPA(List<PTPlace> pA) {
		PA = pA;
	}

	/**
	 * 获取闲置库所p0
	 * @return
	 */
	public PTPlace getP0() {
		return p0;
	}

	/**
	 * 设置闲置库所p0
	 * @param p0
	 */
	public void setP0(PTPlace p0) {
		this.p0 = p0;
	}
	
	/**
	 * 获取P0初始标识，M0(p0)
	 * @return
	 */
	public int getP0Token() {
		PTMarking marking = this.getMarking();
		return marking.get(p0.getName());
	}
	
	/**
	 * 设置初始标识
	 * M0(p0) = p0Token; 
	 * M0(p) = 0, p属于PA
	 * @param p0Token
	 */
	public void setInitialMarking(int p0Token) {
		// Marking
		PTMarking marking = new PTMarking();
		// M0(p0) = p0Token; 
		marking.set(p0.getName(), p0Token);
		// M0(p) = 0, p属于PA
		for(PTPlace p: PA) {
			marking.set(p.getName(), 0);
		}
		// Marking
		setInitialMarking(marking);
	}

	/**
	 * get place name's prefix {@linkplain #p_prefix}
	 * @return
	 */
	public String getP_prefix() {
		return p_prefix;
	}

	/**
	 * set place name's prefix {@linkplain #p_prefix}
	 * @param p_prefix
	 */
	public void setP_prefix(String p_prefix) {
		this.p_prefix = p_prefix;
	}

	/**
	 * get transition name's prefix {@linkplain #t_prefix}
	 * @param p_prefix
	 */
	public String getT_prefix() {
		return t_prefix;
	}

	/**
	 * set transition name's prefix {@linkplain #t_prefix}
	 * @param p_prefix
	 */
	public void setT_prefix(String t_prefix) {
		this.t_prefix = t_prefix;
	}

	/**
	 * get last Place name's suffix {@linkplain #p_suffix}
	 * @return
	 */
	public int getP_suffix() {
		return p_suffix;
	}

	/**
	 * set last Place name's suffix {@linkplain #p_suffix}
	 * @return
	 */
	public void setP_suffix(int p_suffix) {
		this.p_suffix = p_suffix;
	}

	/**
	 * get last Transition name's suffix {@linkplain #t_suffix}
	 * @return
	 */
	public int getT_suffix() {
		return t_suffix;
	}

	/**
	 * set last Transition name's suffix {@linkplain #t_suffix}
	 * @param t_suffix
	 */
	public void setT_suffix(int t_suffix) {
		this.t_suffix = t_suffix;
	}
}
