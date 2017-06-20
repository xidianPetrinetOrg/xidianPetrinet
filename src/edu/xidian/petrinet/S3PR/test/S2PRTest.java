/**
 * 
 */
package edu.xidian.petrinet.S3PR.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.petrinet.S3PR.S2PR;
import edu.xidian.petrinet.Utils.PetriNetTraversalUtils;

/**
 * @author Administrator
 *
 */
public class S2PRTest {

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

	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S2PR#S2PR(int, int, int)}.
	 */
	//@Test
	public void testS2PR() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR("s2pr_test",2,1,4);
		System.out.println(s2pr);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S2PR#isS2P()}.
	 */
	//@Test
	public void isS2P() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR("s2pr_test",2,1,4);
		System.out.println(s2pr);
		
		boolean isS2P = s2pr.isS2P();
		System.out.println("S2PR中的S2P满足S2P的定义？ " + isS2P);
		assertTrue(isS2P);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S2PR#isS2PR()}.
	 */
	@Test
	public void isS2PR() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR("s2pr_test",2,1,4);
		System.out.println(s2pr);
		
		boolean isS2PR = s2pr.isS2PR();
		System.out.println("S2PR满足S2PR的定义？ " + isS2PR);
		assertTrue(isS2PR);
	}
	
	////////////////////// Error retainAll()
	//@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setTest1() {
		S2PR s2pr = new S2PR("s2pr_test",2,1,4);
		System.out.println(s2pr);
		
		Collection<AbstractPNNode> temp1 = new HashSet<>();
		Collection<AbstractPNNode> temp2 = new HashSet<>();
		System.out.println("PR:"+s2pr.getPR());
		for (PTPlace p : s2pr.getPA()) {
			for (AbstractPNNode t1 : p.getParents()) {
					temp1.clear(); temp2.clear();
					temp1.addAll(t1.getParents());
					System.out.println("---temp1:"+temp1);
					// temp2.addAll(s2pr.getPR());
					// temp1.retainAll(temp2); // 正确        // temp1和temp2元素类型必须相同
					temp1.retainAll(s2pr.getPR()); // 错误，temp1与s2pr.getPR()元素类型不同
					System.out.println("---temp1:"+temp1);
			}
		}
	}
	
    //////////////////////OK retainAll()
	// 集合运算，temp1.retainAll(temp2); temp1和temp2元素类型必须相同
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setTest2() {
		S2PR s2pr = new S2PR("s2pr_test",2,1,4);
		System.out.println(s2pr);
		
		Collection<AbstractPNNode> temp1 = new HashSet<>();
		Collection<AbstractPNNode> temp2 = new HashSet<>();
		System.out.println("PR:"+s2pr.getPR());
		for (PTPlace p : s2pr.getPA()) {
			for (AbstractPNNode t1 : p.getParents()) {
				temp1.clear();
				temp2.clear();
				temp1.addAll(t1.getParents());
				System.out.println("---temp1:" + temp1);

				temp2.addAll(s2pr.getPR());
				temp1.retainAll(temp2); // 正确 // temp1和temp2元素类型必须相同
				/// temp1.retainAll(s2pr.getPR()); //
				/// 错误，temp1与s2pr.getPR()元素类型不同
				System.out.println("---temp1:" + temp1);
			}
		}
		
		System.out.println("---------------");
		System.out.println("PA"+s2pr.getPA());
		System.out.println("PR:"+s2pr.getPR());
		temp1.clear(); temp2.clear();
		temp1.addAll(s2pr.getPA());
		temp1.add(s2pr.getPlace("p4"));
		System.out.println("---temp1:"+temp1);
		temp1.retainAll(s2pr.getPR()); // 交集, 错误
		System.out.println("---temp1:"+temp1);
		
		System.out.println("---------------");
		temp1.clear(); temp2.clear();
		temp1.addAll(s2pr.getPA());
		temp1.add(s2pr.getPlace("p4"));
		System.out.println("---temp1:"+temp1);
		temp2.addAll(s2pr.getPR());
		temp1.retainAll(temp2);  // 正确
		//temp1.retainAll(s2pr.getPR()); // 交集, 错误
		System.out.println("---temp1:"+temp1);
		
		System.out.println("==============");
		temp1.clear(); temp2.clear();
		temp1.addAll(s2pr.getPA());
		temp1.add(s2pr.getPlace("p4"));
		System.out.println("---temp1:"+temp1);
		temp2.addAll(s2pr.getPR());
		//temp1.removeAll(temp2);  // 正确
		temp1.removeAll(s2pr.getPR()); // 差集, 结果也是正确的，似乎差集集合元素的类型可以不一致
		System.out.println("---temp1:"+temp1);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S2PR#isInintialMarking()}.
	 */
	//@Test
	public void isInintialMarking() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR("s2pr_test",2,1,4);
		System.out.println(s2pr);
		
		boolean isInintial = s2pr.isInintialMarking();
		System.out.println("S2PR满足M0？ " + isInintial);
		assertTrue(isInintial);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S2PR#S2PR(PTNet, String, Collection, Collection)}.
	 */
	//@Test
	public void testS2P2() {
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
		
		System.out.println("含p1的回路：");
		int result = PetriNetTraversalUtils.dfsCircuits(ptnet1,ptnet1.getPlace("p1"));
		System.out.println("回路个数：" + result);
		assertEquals(2, result); // 回路个数
		
		Set<Set<AbstractPNNode<?>>> Components = PetriNetTraversalUtils.getStronglyConnectedComponents(ptnet1);
		System.out.println("Components: " + Components.size() + ",\n" + Components);
		
		///////////PR
		ptnet1.addPlace("pr1");
		ptnet1.addPlace("pr2");
		ptnet1.addFlowRelationTP("t2", "pr1");
		ptnet1.addFlowRelationPT("pr1", "t1");
		ptnet1.addFlowRelationTP("tt2", "pr1");
		ptnet1.addFlowRelationPT("pr1", "tt1");
		ptnet1.addFlowRelationTP("t3", "pr2");
		ptnet1.addFlowRelationPT("pr2", "t2");
		ptnet1.addFlowRelationTP("tt3", "pr2");
		ptnet1.addFlowRelationPT("pr2", "tt2");
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 4);
		marking.set("pr1", 2);
		marking.set("pr2", 3);
		ptnet1.setInitialMarking(marking);
		
		////////////////////////// s2pr
		Set<String> pa = new HashSet<String>();
		pa.add("p2"); pa.add("p3"); pa.add("pp2"); pa.add("pp3");
		Set<String> pr = new HashSet<String>();
		pr.add("pr1"); pr.add("pr2");
		S2PR s2pr = new S2PR("s2pr_test",ptnet1,"p1",pa,pr);
		
		System.out.println("s2pr: " + s2pr);
		
		/** Test method for {@link edu.xidian.petrinet.S2PR#isS2PR()}. */
		boolean isS2PR = s2pr.isS2PR();
		System.out.println("满足S2PR的定义？ " + isS2PR);
		assertTrue(isS2PR);
		
		boolean isInintial = s2pr.isInintialMarking();
		System.out.println("S2PR满足M0？ " + isInintial);
		assertTrue(isInintial);
	}
	
}
