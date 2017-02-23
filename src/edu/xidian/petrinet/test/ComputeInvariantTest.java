package edu.xidian.petrinet.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.xidian.math.InvariantMatrix;
import edu.xidian.petrinet.ComputeInvariant;

public class ComputeInvariantTest {

   ComputeInvariant computeInvariant;
   
   /**
    * 经典 Figure 1
    */
   void setUp1() {
		// 经典,A Simple and Fast Algorithm To Obain All Invariants Of A Generalised Petri Net
		// Figure 1
	   int incidence[][] = {
	   		                 /** t1  t2  t3  t4  t5  t6  t7  t8  t9  t10 **/
	   		          /** p1 */ { 1,  0,  0,  0, -1,  0,  0,  0,  0,  0},
	   		          /** p2 */ { 0,  0,  0, -1,  1,  0,  0,  0,  0,  0},
	   		          /** p3 */ { 1,  0,  0,  0,  0, -1,  0,  0,  0,  0},
	   		          /** p4 */ {-1,  0,  0,  1,  0,  0,  0,  0,  0,  0},
	   		          /** p5 */ {-1,  0,  0,  0,  0,  0,  1,  0,  0,  0},
	   		          /** p6 */ { 0,  1,  0,  0,  0,  0, -1,  0,  0,  0},
	   		          /** p7 */ { 0, -1,  0,  0,  0,  0,  0,  0,  1,  0},
	   		          /** p8 */ { 0,  1,  0,  0,  0,  0,  0, -1,  0,  0},
	   		          /** p9 */ { 0,  0, -1,  0,  0,  0,  0,  1,  0,  0},
	   		          /** p10*/ { 0,  0,  1,  0,  0,  0,  0,  0, -1,  0},
	   		          /** p11*/ { 0,  0,  1,  0,  0,  0,  0,  0,  0, -1},
	   		          /** p12*/ { 0,  0,  0,  0,  0,  0,  0,  0, -1,  1},
	   		          /** p13*/ { 0,  0,  0, -1,  0,  0,  0,  0,  0,  1},
	   		          /** p14*/ { 0, -1,  0,  0,  0,  1,  0,  0,  0,  0}
	   		         };
	   InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	   computeInvariant = new ComputeInvariant(incidenceM);
	   System.out.println("经典，Figure 1， P-Invariants:");
	   // Compute P-Invariants
       /**
          p1    p2    p3    p4    p5    p6    p7    p8    p9    p10  p11   p12   p13   p14
          1     1     0     1     0     0     0     0     0     0     0     0     0     0   一致
          0     0     1     0     1     1     0     0     0     0     0     0     0     1   一致
          0     0     0     0     0     0     1     1     1     1     0     0     0     0   一致
          0     0     0     0     0     0     1     1     1     0     1     1     0     0   一致
          0     0     1     1     0     0     0     1     1     0     1     0     1     1   一致
        */
   }
   
   /**
    * 经典 Figure 3
    */
   void SetUp2() {
	   // 经典,A Simple and Fast Algorithm To Obain All Invariants Of A Generalised Petri Net
	   // Figure 3
       int incidence[][] = {
       		                 /** t1  t2  t3  **/
       		          /** p11*/ { 1,  -1,   0 },
       		          /** p12*/ { 1,  -1,   0 },
       		          /** p13*/ { 1,  -1,   0 },
       		          /** p21*/ { 0,   1,  -1 },
       		          /** p22*/ { 0,   1,  -1 },
       		          /** p23*/ { 0,   1,  -1 },
       		          /** p31*/ {-1,   0,   1 },
       		          /** p32*/ {-1,   0,   1 },
       		          /** p33*/ {-1,   0,   1 }
       		         };
       
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
		computeInvariant = new ComputeInvariant(incidenceM);
		System.out.println("经典，Figure 3， P-Invariants:");
       /**
          1     0     0     1     0     0     1     0     0
          1     0     0     1     0     0     0     1     0
          1     0     0     1     0     0     0     0     1

        */
	}
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//setUp1();  // 经典 Figure 1
		SetUp2();    // 经典 Figure 3
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#compute1()}.
	 */
	//@Ignore
	@Test
	public void testCompute1() {
		//fail("Not yet implemented");
		System.out.println("testCompute1()");
		computeInvariant.compute1();
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#compute2()}.
	 */
	//@Ignore
	@Test
	public void testCompute2() {
		//fail("Not yet implemented");
		System.out.println("testCompute2()");
		computeInvariant.compute2();
	}

}
