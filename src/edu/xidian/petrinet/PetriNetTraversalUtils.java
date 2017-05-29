package edu.xidian.petrinet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uni.freiburg.iig.telematik.jagal.traverse.Traversable;
import de.uni.freiburg.iig.telematik.jagal.traverse.algorithms.SCCTarjan;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPetriNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractTransition;

/**
 * 有关PetriNet遍历辅助类
 * @author Administrator
 *
 */
public class PetriNetTraversalUtils {
	/**
	 * 记录已经访问的节点
	 */
	private static final Set<AbstractPNNode<?>> visited = new HashSet<>();
	/**
	 * 含有startNode的回路个数
	 */
	private static int cycleCount = 0;
	/**
	 * 从startNode开始到starNode结束的回路
	 */
	private static AbstractPNNode<?> startNode = null;
	
	/** 获取PetriNet中的所有强连通分量
	 * @param petriNet PetriNet对象 
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set<Set<AbstractPNNode<?>>> getStronglyConnectedComponents(AbstractPetriNet petriNet) {
		Set<Set<AbstractPNNode<?>>> Components = null;
		SCCTarjan<AbstractPNNode<?>> tarjan = new SCCTarjan<>();
		Components = tarjan.execute(petriNet);
		return Components;
	}
	
	/**
	 * 获取place和transition之间的所有弧名称
	 * @param petriNet petri网
	 * @param place  库所
	 * @param transition 变迁
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set<String> getRelations(AbstractPetriNet petriNet,String place,String transition) {
		Set<String> name = new HashSet<>();
		AbstractPlace p = petriNet.getPlace(place);
		AbstractTransition t = petriNet.getTransition(transition);		
		// t --> p
		AbstractFlowRelation f = p.getRelationFrom(t);
		if ( f != null) name.add(f.getName());  
		// p --> t
		f = p.getRelationTo(t);
		if ( f != null) name.add(f.getName());
		
		return name;
	}
	
	/**
	 * 删除place和transition之间的所有弧
	 * @param petriNet petri网
	 * @param place  库所
	 * @param transition 变迁
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeRelations(AbstractPetriNet petriNet,String place,String transition) {
		AbstractPlace p = petriNet.getPlace(place);
		AbstractTransition t = petriNet.getTransition(transition);
		// t --> p
		AbstractFlowRelation f = p.getRelationFrom(t);
		if ( f != null) petriNet.removeFlowRelation(f.getName());
		// p --> t
		f = p.getRelationTo(t);
		if ( f != null) petriNet.removeFlowRelation(f.getName());
	}
	
	/**
	 * 含有sNode的回路
	 * @param petriNet
	 * @param sNode 
	 * @return 含有sNode的回路个数
	 */
	@SuppressWarnings("rawtypes")
	public static int dfsCheckCycles(AbstractPetriNet petriNet, AbstractPNNode sNode) {
		// 首先初始化以下三个静态变量
		visited.clear();
		cycleCount = 0;
		startNode = sNode;
		visited.add(startNode);
		dfsCycles(petriNet,sNode);
		System.out.println("visited: "+ visited.size() + ",nodeCount=" + petriNet.nodeCount());
		return cycleCount;
	}
	
	/**
	 * 深度优先搜素，检查含有startNode的回路，供本类其它函数调用
	 * 已访问的节点记录在本类的静态成员visited
	 * 回路个数记录在本类静态成员cycleCount
	 * 开始节点记录在本类静态成员startNode
	 * 因此调用此递归函数，必须首先初始化这两个静态成员
	 * @param petriNet
	 * @param start
	 * @return 存在包含startNode的回路，返回true；否则返回false
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void dfsCycles(AbstractPetriNet petriNet, AbstractPNNode node) {
		System.out.println("visited " + visited);
		Set<AbstractPNNode> nodes = petriNet.getChildren(node);
		for(AbstractPNNode n: nodes) {
			if (n.equals(startNode)) {
				cycleCount++;
				if (visited.size() < petriNet.nodeCount()) {
					dfsCycles(petriNet,n);
				}
			}
			if (!visited.contains(n)) {
				visited.add(n);
				dfsCycles(petriNet,n);
			}
		}
	}
}
