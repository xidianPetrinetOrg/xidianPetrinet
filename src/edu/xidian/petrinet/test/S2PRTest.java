/**
 * 
 */
package edu.xidian.petrinet.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.xidian.petrinet.S2PR;

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
	 * Test method for {@link edu.xidian.petrinet.S2PR#S2PR(int, int, int)}.
	 */
	//@Test
	public void testS2PR() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR(2,1,4);
		System.out.println(s2pr);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S2P#isS2P()}.
	 */
	@Test
	public void testS2PR1() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR(2,1,4);
		System.out.println(s2pr);
		
		boolean isS2P = s2pr.isS2P();
		System.out.println("由于PR的影响,S2PR不一定满足S2P的定义？ " + isS2P);
		assertTrue(isS2P);
	}
}
