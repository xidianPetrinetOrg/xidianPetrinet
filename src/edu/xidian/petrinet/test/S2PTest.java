/**
 * 
 */
package edu.xidian.petrinet.test;

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
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.PetriNetTraversalUtils;
import edu.xidian.petrinet.S2P;

/**
 * @author Administrator
 *
 */
public class S2PTest {

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
	 * Test method for {@link edu.xidian.petrinet.S2P#S2P(int, int)}.
	 */
	//@Test
	public void testS2P() {
		//fail("Not yet implemented");
		S2P s2p = new S2P("s2p_test",2,4);
		System.out.println(s2p);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S2P#S2P(int)}.
	 */
	//@Test
	public void testS2P1() {
		//fail("Not yet implemented");
		S2P s2p = new S2P("s2p_test",3);
		s2p.setInitialMarking(3);
		System.out.println(s2p);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S2P#S2P(PTNet, String, Collection)}.
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
		
		////////////////////////// s2p
		Set<String> pa = new HashSet<String>();
		pa.add("p2"); pa.add("p3"); pa.add("pp2"); pa.add("pp3");
		S2P s2p = new S2P("s2p_test",ptnet1,"p1",pa);
		
		System.out.println(s2p);
		
		/** Test method for {@link edu.xidian.petrinet.S2P#isS2P()}. */
		boolean isS2P = s2p.isS2P();
		System.out.println("满足S2P的定义？ " + isS2P);
		assertTrue(isS2P);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S2P#isS2P()}.
	 */
	@Test
	public void isS2P() {
		//fail("Not yet implemented");
		S2P s2p = new S2P("s2p_test",2,4);
		System.out.println(s2p);
		boolean isS2P = s2p.isS2P();
		System.out.println("满足S2P的定义？ " + isS2P);
		assertTrue(isS2P);
	}
}
