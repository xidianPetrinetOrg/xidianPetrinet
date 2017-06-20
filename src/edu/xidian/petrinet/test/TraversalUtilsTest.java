/**
 * 
 */
package edu.xidian.petrinet.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.jagal.traverse.TraversalUtils;
import de.uni.freiburg.iig.telematik.jagal.traverse.Traverser;
import de.uni.freiburg.iig.telematik.jagal.traverse.Traverser.TraversalMode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPetriNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import edu.xidian.petrinet.S3PR.S3PR.S2PR;

/**
 * @author Administrator
 *
 */
public class TraversalUtilsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/***
	 * ==与equals的不同，clone()
	 */
	//@Test
	public void testTraverser1() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		
		System.out.println("ptnet1：" + ptnet1);
		
		AbstractPetriNet<PTPlace, PTTransition, PTFlowRelation, PTMarking, Integer> ptnet2 = ptnet1.clone();
		System.out.println("ptnet2: " + ptnet2);
		
		ptnet2.removeTransition("t2");
		System.out.println("ptnet2: " + ptnet2);
		
		// 正确，ptnet2.getPlace("p1"), 表示正确的ptnet2的起点
		Iterator<AbstractPNNode<PTFlowRelation>> iter1 = new Traverser<>(ptnet2, ptnet2.getPlace("p1"), TraversalMode.DEPTHFIRST);
        while (iter1.hasNext()) {
        	 AbstractPNNode<PTFlowRelation> v=iter1.next();
             System.out.println("正确="+v);
        }
        
        // 错误，ptnet1.getPlace("p1"), 本应表示ptnet2的起点，错误的表示成ptnet1的起点，因此从ptnet1开始查找了所有节点（包含已经删除的节点）
        Iterator<AbstractPNNode<PTFlowRelation>> iter2 = new Traverser<>(ptnet2, ptnet1.getPlace("p1"), TraversalMode.DEPTHFIRST);
        while (iter2.hasNext()) {
        	 AbstractPNNode<PTFlowRelation> v=iter2.next();
             System.out.println("错误="+v);
        }
        
        System.out.println("两个网的p1不同,==是 " + (ptnet1.getPlace("p1") == ptnet2.getPlace("p1")));
		System.out.println("两个网的p1名义相同，equals是 " + ptnet1.getPlace("p1").equals(ptnet2.getPlace("p1")));	
	}
	
	/**
	 * ==与equals的不同，clone()
	 */
	// @Test
	public void testTraverser2() {
		class Net extends PTNet {
			private static final long serialVersionUID = 1L;
			public Net() {
				super(); // 产生对象this	
				addPlace("p1");
				addTransition("t1");
				addPlace("p2");
				addTransition("t2");
				addFlowRelationPT("p1", "t1");
				addFlowRelationTP("t1", "p2");
				addFlowRelationPT("p2", "t2");
			}
			public void TestNet() {
				System.out.println("this: " + this);
				AbstractPetriNet<PTPlace, PTTransition, PTFlowRelation, PTMarking, Integer> ptnet2 = this.clone();
				System.out.println("ptnet2: " + ptnet2);
				
				ptnet2.removeTransition("t1");  // 试验删除节点
				System.out.println("ptnet2: " + ptnet2);
				
				// 遍历全部，不管删除节点，错误，this.getPlace("p1")
				Iterator<AbstractPNNode<PTFlowRelation>> iter1 = new Traverser<>(ptnet2, this.getPlace("p1"), TraversalMode.DEPTHFIRST);
		        while (iter1.hasNext()) {
		        	 AbstractPNNode<PTFlowRelation> v=iter1.next();
		             System.out.println("错误="+v);
		        }
		        
		        // 遍历，删除的节点不在，正确，ptnet2.getPlace("p1")
		        Iterator<AbstractPNNode<PTFlowRelation>> iter2 = new Traverser<>(ptnet2, ptnet2.getPlace("p1"), TraversalMode.DEPTHFIRST);
		        while (iter2.hasNext()) {
		        	 AbstractPNNode<PTFlowRelation> v=iter2.next();
		             System.out.println("正确="+v);
		        }
		        
		        System.out.println("两个网的p1不同,==是 " + (this.getPlace("p1") == ptnet2.getPlace("p1")));
				System.out.println("两个网的p1名义相同，equals是 " + this.getPlace("p1").equals(ptnet2.getPlace("p1")));
			}
		}
		
		Net ptnet1 = new Net();
	    ptnet1.TestNet();	
	}
	
	/**
	 * if the given traversable structure is strongly connected.
     * node是否可以到达图中的其它所有节点（不检查是否是封闭的，即检查p0->p1->p2->p3,即p0到其它3个节点都可到达，但是不检查是否p0->p1->p2-p3->p0） 
	 */
	//@Test
	public void isStronglyConnected() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");
		
		System.out.println("ptnet1：" + ptnet1);
		
		AbstractPetriNet<PTPlace, PTTransition, PTFlowRelation, PTMarking, Integer> ptnet2 = ptnet1.clone();
		System.out.println("ptnet2: " + ptnet2);
		
		ptnet2.removeTransition("t1");
		System.out.println("ptnet2: " + ptnet2);
		
		// p1可以到图的其它任何点，但是不检查是否封闭
		System.out.println("强连通：" + TraversalUtils.isStronglyConnected(ptnet1, ptnet1.getPlace("p1")));
		
		// p1不能到图的所有节点
		System.out.println("不是强连通：" + TraversalUtils.isStronglyConnected(ptnet2, ptnet2.getPlace("p1")));
		
		// 错误判断，两个参数都要使用统一的ptnet1或ptnet2
		System.out.println("错误判断：" + TraversalUtils.isStronglyConnected(ptnet2, ptnet1.getPlace("p1")));
		
		System.out.println("两个网的p1不同,==是 " + (ptnet1.getPlace("p1") == ptnet2.getPlace("p1")));
		System.out.println("两个网的p1名义相同，equals是 " + ptnet1.getPlace("p1").equals(ptnet2.getPlace("p1")));
	}
	
	/**
	 * 获取强连通分量
	 */
	//@Test
	public void getStronglyConnectedComponents1() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		Set<Set<AbstractPNNode<PTFlowRelation>>> Components;
		Components = TraversalUtils.getStronglyConnectedComponents(ptnet1);
		
		System.out.println("Components: " + Components);
		System.out.println("size=" + Components.size());
		System.out.println("isEmpty=" + Components.isEmpty());
		
		Iterator<Set<AbstractPNNode<PTFlowRelation>>> iter1 = Components.iterator(); 
		while (iter1.hasNext()) {
		  Set<AbstractPNNode<PTFlowRelation>> nodes = iter1.next();
		  System.out.println("contains p1 = " + nodes.contains(ptnet1.getPlace("p1")));
		  Iterator<AbstractPNNode<PTFlowRelation>> iter2 = nodes.iterator();
		  while(iter2.hasNext()) {
			  AbstractPNNode<PTFlowRelation> node = iter2.next();
			  System.out.print(node + ",");
		  }
		  System.out.println("");
		}  
	}
	
	/**
	 * 获取强连通分量, S2P(simple sequential process)，一个包含全部节点的连通分量，这里是1个含p1的回路
	 */
	// @Test
	public void getStronglyConnectedComponents2() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		Set<Set<AbstractPNNode<PTFlowRelation>>> Components;
		Components = TraversalUtils.getStronglyConnectedComponents(ptnet1);
		
		System.out.println("Components: " + Components); // [[t3[t3], t2[t2], p1[p1], t1[t1], p3[p3], p2[p2]]]
		System.out.println("size=" + Components.size()); // 1
		System.out.println("isEmpty=" + Components.isEmpty()); // false
		
		Iterator<Set<AbstractPNNode<PTFlowRelation>>> iter1 = Components.iterator(); 
		while (iter1.hasNext()) {
		  Set<AbstractPNNode<PTFlowRelation>> nodes = iter1.next();
		  System.out.println("contains p1 = " + nodes.contains(ptnet1.getPlace("p1"))); // true
		  Iterator<AbstractPNNode<PTFlowRelation>> iter2 = nodes.iterator();
		  while(iter2.hasNext()) {
			  AbstractPNNode<PTFlowRelation> node = iter2.next();
			  System.out.print(node + ","); // t3[t3],t2[t2],p1[p1],t1[t1],p3[p3],p2[p2],
		  }
		  System.out.println("");
		}  
		
		// 结论，一个包含全部节点的连通分量
		boolean ok = false;
		if (Components.size() == 1) {
		   Set<AbstractPNNode<PTFlowRelation>> nodes = Components.iterator().next();
		   ok = nodes.size() == ptnet1.nodeCount(); 
		}
		System.out.println("结论，一个包含全部节点的连通分量：" + ok);
	}
	
	/**
	 * 获取强连通分量, S2P(simple sequential process)，一个包含全部节点的连通分量，这里是2个含p1的回路
	 */
	//@Test
	public void getStronglyConnectedComponents3() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");
		
		ptnet1.addTransition("tt1");
		ptnet1.addPlace("pp2");
		ptnet1.addTransition("tt2");
		ptnet1.addPlace("pp3");
		ptnet1.addTransition("tt3");
		
		ptnet1.addFlowRelationPT("p1", "tt1");
		ptnet1.addFlowRelationTP("tt1", "pp2");
		ptnet1.addFlowRelationPT("pp2", "tt2");
		ptnet1.addFlowRelationTP("tt2", "pp3");
		ptnet1.addFlowRelationPT("pp3", "tt3");
		ptnet1.addFlowRelationTP("tt3", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		Set<Set<AbstractPNNode<PTFlowRelation>>> Components;
		Components = TraversalUtils.getStronglyConnectedComponents(ptnet1);
		
		System.out.println("Components: " + Components); // [[t3[t3], t2[t2], p1[p1], t1[t1], p3[p3], p2[p2]]]
		System.out.println("size=" + Components.size()); // 1
		System.out.println("isEmpty=" + Components.isEmpty()); // false
		
		Iterator<Set<AbstractPNNode<PTFlowRelation>>> iter1 = Components.iterator(); 
		while (iter1.hasNext()) {
		  Set<AbstractPNNode<PTFlowRelation>> nodes = iter1.next();
		  System.out.println("contains p1 = " + nodes.contains(ptnet1.getPlace("p1"))); // true
		  Iterator<AbstractPNNode<PTFlowRelation>> iter2 = nodes.iterator();
		  while(iter2.hasNext()) {
			  AbstractPNNode<PTFlowRelation> node = iter2.next();
			  System.out.print(node + ","); // t3[t3],t2[t2],p1[p1],t1[t1],p3[p3],p2[p2],
		  }
		  System.out.println("");
		} 
		
		// 结论，一个包含全部节点的连通分量
		boolean ok = false;
		if (Components.size() == 1) {
		   Set<AbstractPNNode<PTFlowRelation>> nodes = Components.iterator().next();
		   ok = nodes.size() == ptnet1.nodeCount(); 
		}
		System.out.println("结论，一个包含全部节点的连通分量：" + ok);
		
		// p1到p1没有路径
		AbstractPNNode<PTFlowRelation> p1 = ptnet1.getPlace("p1");
		AbstractPNNode<PTFlowRelation> p2 = ptnet1.getPlace("p2");
		try {
			ArrayBlockingQueue<List<AbstractPNNode<PTFlowRelation>>> paths = TraversalUtils.getDirectedPathsFor(ptnet1,p1,p2);
			System.out.println("paths:"+paths);
		} catch (ParameterException e) {
			ok = false;
			e.printStackTrace();
		} catch (VertexNotFoundException e) {
			ok = false;
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取到source到target的所有路径； 
	 * 如果source与target相同，认为没有路径
	 */
	//@Test
	public void getDirectedPathsFor() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");
		
		
		System.out.println("ptnet1：" + ptnet1);
		
		// p1到p2的所有路径，p1到p1没有路径
		AbstractPNNode<PTFlowRelation> p1 = ptnet1.getPlace("p1");
		AbstractPNNode<PTFlowRelation> p2 = ptnet1.getPlace("p2");
		ArrayBlockingQueue<List<AbstractPNNode<PTFlowRelation>>> paths;
		try {
			paths = TraversalUtils.getDirectedPathsFor(ptnet1,p1,p1);
			System.out.println("p1到p1【无路径】:"+paths);
		} catch (ParameterException e) {
			e.printStackTrace();
		} catch (VertexNotFoundException e) {
			e.printStackTrace();
		}
		
		
		try {
			paths = TraversalUtils.getDirectedPathsFor(ptnet1,p2,p1);
			System.out.println("p2到p1【有路径】:"+paths); // [[p2[p2], t2[t2], p3[p3], t3[t3], p1[p1]]]
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			paths = TraversalUtils.getDirectedPathsFor(ptnet1,p1,p2);
			System.out.println("p1到p2的路径:"+paths); // [[p1[p1], t1[t1], p2[p2]]]
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 检查节点是否在一个Cycle中，即该节点既是本身的父节点（或父节点的父节点），也是子节点（或子节点的子节点）
	 */
	@Test
	public void isNodeInCycle() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");
		System.out.println("ptnet1：" + ptnet1);
		
		// 为何没有Cycle？如果各节点双向连接，是Cycle
		try {
			System.out.println("p1 in Cycle:" + TraversalUtils.isNodeInCycle(ptnet1,ptnet1.getPlace("p1")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 是否有Cycle，网中存在节点：该节点既是本身的父节点（或父节点的父节点），也是子节点（或子节点的子节点）
	 */
	@Test
	public void hasCycle() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");
		System.out.println("ptnet1：" + ptnet1);
		
		System.out.println("hasCycle:" + TraversalUtils.hasCycle(ptnet1));
		
	}
	
	/**
	 * queyNode <> baseNode
	 * isPredecessor：检查queyNode是baseNode的前继（包括前继的前继），即queryNode是baseNode父节点或父节点的父节点<br>
	 * isSuccessor: 检查queyNode是baseNode的后继（包括后继的后继），即queryNode是baseNode子节点或子节点的子节点
	 */
	//@Test
	public void isPredecessor_Successor() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");
		
		ptnet1.addPlace("p3");
		ptnet1.addFlowRelationTP("t2", "p3");
				
		System.out.println("ptnet1：" + ptnet1);
		
		// p1（queryNode）是t1（baseNode）的前继和后继
		try {
			System.out.println("p1是t1的前继（父节点）：" + TraversalUtils.isPredecessor(ptnet1, ptnet1.getPlace("p1"), ptnet1.getTransition("t1")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println("p1是t1的后继（子节点的子节点）：" + TraversalUtils.isSuccessor(ptnet1, ptnet1.getPlace("p1"), ptnet1.getTransition("t1")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// p1（queryNode）是t2（baseNode）的前继和后继
		try {
			System.out.println("p1是t2的前继（父节点的父节点）：" + TraversalUtils.isPredecessor(ptnet1, ptnet1.getPlace("p1"), ptnet1.getTransition("t2")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		try {
			System.out.println("p1是t2的后继（子节点的子节点）：" + TraversalUtils.isSuccessor(ptnet1, ptnet1.getPlace("p1"), ptnet1.getTransition("t2")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// p3（queryNode）是t1（baseNode）的关系
		try {
			System.out.println("p3不是t1的前继（父节点或父节点的父节点）：" + TraversalUtils.isPredecessor(ptnet1, ptnet1.getPlace("p3"), ptnet1.getTransition("t1")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		try {
			System.out.println("p3是t1的后继（子节点的子节点）：" + TraversalUtils.isSuccessor(ptnet1, ptnet1.getPlace("p3"), ptnet1.getTransition("t1")));
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * queyNode = baseNode
	 * isPredecessor：检查queyNode是baseNode的前继（包括前继的前继），即queryNode是baseNode父节点或父节点的父节点<br>
	 * isSuccessor: 检查queyNode是baseNode的后继（包括后继的后继），即queryNode是baseNode子节点或子节点的子节点
	 */
	@Test
	public void isPredecessor_Successor1() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");  //ptnet1.addFlowRelationTP("t1", "p1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");
		
				
		System.out.println("ptnet1：" + ptnet1);	
		
		PTPlace p1 = ptnet1.getPlace("p1");
		System.out.println("p1 Parents:" + ptnet1.getParents(p1));  // [t2[t2], t1[t1]]
		System.out.println("p1 Children:" + ptnet1.getChildren(p1)); // [t1[t1]]
		
		// p1（queryNode）是p1（baseNode）的前继和后继
		try {
			System.out.println("p1是p1的前继（父节点）：" + TraversalUtils.isPredecessor(ptnet1, ptnet1.getPlace("p1"), ptnet1.getPlace("p1"))); // false
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// p1（queryNode）是p1（baseNode）的前继和后继
		try {
			System.out.println("p1是p1的后继（子节点）：" + TraversalUtils.isSuccessor(ptnet1, ptnet1.getPlace("p1"), ptnet1.getPlace("p1"))); // false
		} catch (ParameterException | VertexNotFoundException e) {
		    // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取节点所有前继：即父节点或父节点的父节点
	 * 获取节点所有后继：即子节点或子节点的子节点
	 */
	//@Test
	public void getPredecessorsFor_SuccessorsFor() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");
		
		ptnet1.addPlace("p3");
		ptnet1.addFlowRelationTP("t2", "p3");
				
		System.out.println("ptnet1：" + ptnet1);
				
		try {
			System.out.println("p2父节点及父节点的父节点" + TraversalUtils.getPredecessorsFor(ptnet1, ptnet1.getPlace("p2")));
			// [p1[p1], t2[t2], t1[t1]]
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("p2子节点及子节点的子节点" + TraversalUtils.getSuccessorsFor(ptnet1, ptnet1.getPlace("p2")));
			// [t2[t2], p1[p1], t1[t1], p3[p3]]
		} catch (ParameterException | VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
