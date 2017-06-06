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
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.PetriNetTraversalUtils;
import edu.xidian.petrinet.S2PR;
import edu.xidian.petrinet.S3PR;

/**
 * @author Administrator
 *
 */
public class S3PRTest {

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
	 * Test method for {@link edu.xidian.petrinet.S3PR#S3PR(S2PR)}.
	 */
	@Test
	public void testS3PR() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR(2,1,4);
		System.out.println("s2pr:" + s2pr);
		
		S3PR s3pr = new S3PR(s2pr);
		System.out.println("s3pr: " + s3pr);
	}
	
}
