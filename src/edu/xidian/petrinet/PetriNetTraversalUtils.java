package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	//private static final Set<AbstractPNNode<?>> visited = new HashSet<>();
	private static final List<AbstractPNNode<?>> visited = new ArrayList<>();  // 方便调试，显示顺序与添加书序一致，但是没有HashSet效率高
	/**
	 * 含有startNode的回路个数
	 */
	private static int circuitCount = 0;
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
	 * @return 返回通过的回路个数
	 */
	@SuppressWarnings("rawtypes")
	public static int dfsCircuits(AbstractPetriNet petriNet, AbstractPNNode sNode) {
		// 首先初始化以下三个静态变量
		visited.clear();
		circuitCount = 0;
		startNode = sNode;
		dfsCircuitsRecursive(petriNet,sNode);
		return circuitCount;
	}
	
	/**
	 * 深度优先递归搜索，检查含有startNode的所有回路，供本类其它函数调用
	 * 已访问的节点记录在本类的静态成员visited
	 * 回路个数记录在本类静态成员cycleCount
	 * 开始节点记录在本类静态成员startNode
	 * 因此调用此递归函数，必须首先初始化这两个静态成员
	 * 给定无向图G = (V,E)(或有向图D = (V,E))，设v[0], v[1], · · · , v[m] 属于V，边(或弧)e[1], e[2], · · · , e[m] 属于 E，
	 * 其中v[i-1]和vi是e[i]的端点，交替序列v[0]e[1]v[1]e[2] · · · e[m]v[m]称为连接v[0]到v[m]的路(walk)或链(chain)，通常简记
     * 为v[0]v[1] · · · v[m]。路上边的数目称为该路的长度。当v[0] = v[m]时，称其为回路(circuit)。
     * 
     * 性质：  如果 G=(V,E) 是有向图，那么它是强连通图的必要条件是边的数目大于等于顶点的数目：|E|>=|V|，而反之不成立。
     * 引理：  有向图G无回路当且仅当对G进行深度优先搜索没有得到反向边。
	 * @param petriNet
	 * @param start
	 * @return 存在包含startNode的回路，返回true；否则返回false
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void dfsCircuitsRecursive(AbstractPetriNet petriNet, AbstractPNNode node) {
		visited.add(node);
		System.out.println("visited " + visited);
		Set<AbstractPNNode> nodes = petriNet.getChildren(node);
		for(AbstractPNNode n: nodes) {
			if (n.equals(startNode)) {  // 如果子节点是startNode，找到一个回路
				circuitCount++;
				//return; // 注意直到遍历完所有节点return,因此此语句无意义。未遍历的节点在递归结构的栈中存放
			}
			if (!visited.contains(n)) { // 如果子节点还没有访问，递归访问
				//visited.add(n);
				dfsCircuitsRecursive(petriNet,n);
			}
		}
	}
}
