package edu.xidian.petrinet.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
	   
	   System.out.println("经典，Figure 1， P-Invariants:");
	   incidenceM.print("incidence:");
	   incidenceM.print(4, 0);
	   
	   computeInvariant = new ComputeInvariant(incidenceM);
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
   void setUp2() {
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
	    
	    System.out.println("经典，Figure 3， P-Invariants:");
	    incidenceM.print("incidence:");
		incidenceM.print(4, 0);
		
		computeInvariant = new ComputeInvariant(incidenceM);
		
       /**
          1     0     0     1     0     0     1     0     0
          1     0     0     1     0     0     0     1     0
          1     0     0     1     0     0     0     0     1

        */
	}
   
   /**
    * Metabolites, Compute T-Invariants
    */
    void setUp3() {
        // Metabolites
        int incidence[][] = {
        		    {-1,  0,  0,  0,  0,  0,  0,  1,  0},
        		    { 1, -1,  0,  0,  0,  0,  0,  0,  0},
        		    { 1,  0, -1,  0,  0,  0,  0,  0,  0},
        		    { 0,  0,  1,  1, -1, -1,  0,  0,  0},
        		    { 0,  1,  0, -1,  1,  0, -1,  0,  0},
        		    { 0,  0,  0,  0,  0,  0,  1,  0, -1},
        		    { 0,  1,  1,  0,  0,-29,  1,  0,  0},
        		    { 0, -1, -1,  0,  0, 29, -1,  0,  0}
        };
        InvariantMatrix incidenceM = new InvariantMatrix(incidence);
        
        System.out.println("Metabolites,T-Invariants:");
        incidenceM.print("incidence:");
 	    incidenceM.print(4, 0);
 	   
		computeInvariant = new ComputeInvariant(incidenceM.transpose());
    }
    
	// Li，图2.2 Compute P-Invariants
    void setUp4() {
		int incidence[][] = {
  		        /** t1  t2  t3  t4  t5  t6  t7  t8  t9 **/
       /** p1 */  { 0,  0,  1,  0,  0,  0, -1,  0,  1},
       /** p2 */  { 1, -1,  0,  0,  0,  0,  0,  0,  0},
       /** p3 */  { 0,  1, -1,  0,  0,  0,  0,  0,  0},
       /** p4 */  { 0,  0,  0, -1,  0,  1,  0,  0,  0},
       /** p5 */  { 0,  0,  0,  1, -1,  0,  0,  0,  0},
       /** p6 */  { 0,  0,  0,  0,  1, -1,  0,  0,  0},
       /** p7 */  {-1,  1,  0,  0, -1,  1,  0,  0,  0},
       /** p8 */  { 0, -1,  1, -1,  1,  0,  0,  0,  0},
       /** p9 */  {-1,  0,  0,  0,  0,  0,  1, -1,  0},
       /** p10*/  { 0,  0,  0,  0,  0,  0,  0,  1, -1},
       /** p11*/  { 0,  0,  0,  0,  0,  0,  0, -1,  1},
       /** p12*/  { 1,  0,  0,  0,  0,  0, -1,  1,  0}};
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    System.out.println("Li，图2.2 Compute P-Invariants:");
	    incidenceM.print("incidence:");
	    incidenceM.print(4, 0);
 	   
		computeInvariant = new ComputeInvariant(incidenceM);
    }
    
	// Li，图2.2 Compute T-Invariants
    void setUp5() {
		int incidence[][] = {
  		        /** t1  t2  t3  t4  t5  t6  t7  t8  t9 **/
       /** p1 */  { 0,  0,  1,  0,  0,  0, -1,  0,  1},
       /** p2 */  { 1, -1,  0,  0,  0,  0,  0,  0,  0},
       /** p3 */  { 0,  1, -1,  0,  0,  0,  0,  0,  0},
       /** p4 */  { 0,  0,  0, -1,  0,  1,  0,  0,  0},
       /** p5 */  { 0,  0,  0,  1, -1,  0,  0,  0,  0},
       /** p6 */  { 0,  0,  0,  0,  1, -1,  0,  0,  0},
       /** p7 */  {-1,  1,  0,  0, -1,  1,  0,  0,  0},
       /** p8 */  { 0, -1,  1, -1,  1,  0,  0,  0,  0},
       /** p9 */  {-1,  0,  0,  0,  0,  0,  1, -1,  0},
       /** p10*/  { 0,  0,  0,  0,  0,  0,  0,  1, -1},
       /** p11*/  { 0,  0,  0,  0,  0,  0,  0, -1,  1},
       /** p12*/  { 1,  0,  0,  0,  0,  0, -1,  1,  0}};
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	    
	    // Compute T-Invariants
	    System.out.println("Li，图2.2 Compute T-Invariants:");
	    incidenceM.print("incidence:");
	    incidenceM.print(4, 0);
 	   
		computeInvariant = new ComputeInvariant(incidenceM.transpose());
    }
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("setUpBeforeClass()");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("tearDownAfterClass()");
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("setUp()====");
		// 经典,A Simple and Fast Algorithm To Obain All Invariants Of A Generalised Petri Net
		//setUp1();  // 经典 Figure 1, P-Invariants
		setUp2();  // 经典 Figure 3, P-Invariants
		//setUp3();  // Metabolites, T-Invariants
		//setUp4();  // Li，图2.2 Compute P-Invariants
		//setUp5();  // Li，图2.2 Compute T-Invariants
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Ignore
	@Test
	public void test() {
		boolean a=true,b=false,c;
		c = a && b;
		System.out.println("a && b="+c);
		//if(a && (10/0==0)) System.out.println("ok");
		//if(a & (10/0==0)) System.out.println("error");
	}

	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#compute1()}.
	 */
	@Ignore
	@Test
	public void testCompute1() {
		//fail("Not yet implemented");
		System.out.println("testCompute1()");
		computeInvariant.compute1();
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#compute2()}.
	 */
	@Ignore
	@Test
	public void testCompute2() {
		//fail("Not yet implemented");
		System.out.println("testCompute2()");
		System.out.println("compute1()");
		computeInvariant.compute1();
		System.out.println("compute2()");
		computeInvariant.compute2();
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#compute3()}.
	 */
	@Ignore
	@Test
	public void testCompute3() {
		//fail("Not yet implemented");
		System.out.println("testCompute3()");
		
		System.out.println("compute1()");
		computeInvariant.compute1();
		System.out.println("compute2()");
		computeInvariant.compute2();
		System.out.println("compute3()");
		computeInvariant.compute3();
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#compute()}.
	 */
	//@Ignore
	@Test
	public void testCompute() {
		//fail("Not yet implemented");
		System.out.println("testCompute()");
		
		computeInvariant.compute();
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#AnnelCol(ArrayList, ArrayList)}.
	 */
	@Ignore
	@Test
	public void testAnnelCol() {
		//fail("Not yet implemented");
		System.out.println("testAnnelCol()");
		int incidence[][] = {
    		    { 1, -1,  0,  1},
    		    {-1,  1,  1, -1},
    		    { 1, -1, -1,  0},
    		    {-1,  1, -1, -1},
    		    {-1,  1,  1,  1},
        };
		/**
		 * pk,vk:the number of positives and negatives in column k of A(k)
		 * 选择列：pk*vk-(pk+vk) < 0  (1)
		 * 或pk*vk最小                                              (2)
		 * p0=2,v0=3, pk*vk-(pk+vk) = 2*3-(2+3) = 1
		 * p1=3,v1=2, pk*vk-(pk+vk) = 3*2-(3+2) = 1
		 * p2=2,v2=2, pk*vk-(pk+vk) = 2*2-(2+2) = 0  符合条件(2)
		 * p3=2,v3=1, pk*vk-(pk+vk) = 2*2-(2+2) = 0
		 * 选择第3(index)列，为待删除列
		 */
    
       InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	   incidenceM.print("incidence:");
	   incidenceM.print(4, 0);
	   ComputeInvariant computeInvariant = new ComputeInvariant(incidenceM);  
	   
	   ArrayList<Integer> positives = new ArrayList<>();
	   ArrayList<Integer> negatives = new ArrayList<>();
	   int col;
	   col = computeInvariant.AnnelCol(positives, negatives);
	   computeInvariant.println("col="+col); // k
	   computeInvariant.println("positives="+positives);
	   computeInvariant.println("negatives="+negatives);  
	   
	   assertEquals(col,2);
	   assertEquals(positives.size(),2); // pk
	   assertEquals(negatives.size(),2); // vk
	   
	   Object positiveExpecteds[] = {1,4};
	   assertArrayEquals(positiveExpecteds, positives.toArray());
	   
	   Object negativeExpecteds[] = {2,3};
	   assertArrayEquals(negativeExpecteds, negatives.toArray());
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#AnnelCol(ArrayList, ArrayList)}.
	 */
	@Ignore
	@Test
	public void testAnnelCol1() {
		//fail("Not yet implemented");
		System.out.println("testAnnelCol1()");
		int incidence[][] = {
    		    { 1, -1,  0,  1},
    		    {-1,  1,  1,  0},
    		    { 1, -1, -1,  0},
    		    {-1,  1, -1, -1},
    		    {-1,  1,  1,  1},
        };
		/**
		 * pk,vk:the number of positives and negatives in column k of A(k)
		 * 选择列：pk*vk-(pk+vk) < 0  (1)
		 * 或pk*vk最小                                              (2)
		 * p0=2,v0=3, pk*vk-(pk+vk) = 2*3-(2+3) = 1
		 * p1=3,v1=2, pk*vk-(pk+vk) = 3*2-(3+2) = 1
		 * p2=2,v2=2, pk*vk-(pk+vk) = 2*2-(2+2) = 0
		 * p3=2,v3=1, pk*vk-(pk+vk) = 2*1-(2+1) = -1 符合条件(1)
		 * 选择第3(index)列，为待删除列
		 */
    
       InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	   incidenceM.print("incidence:");
	   incidenceM.print(4, 0);
	   ComputeInvariant computeInvariant = new ComputeInvariant(incidenceM);  
	   
	   ArrayList<Integer> positives = new ArrayList<>();
	   ArrayList<Integer> negatives = new ArrayList<>();
	   int col;
	   col = computeInvariant.AnnelCol(positives, negatives);
	   computeInvariant.println("col="+col);  // k
	   computeInvariant.println("positives="+positives);
	   computeInvariant.println("negatives="+negatives);  
	   
	   assertEquals(col,3);
	   assertEquals(positives.size(),2);  // pk
	   assertEquals(negatives.size(),1);  // vk
	   
	   Object positiveExpecteds[] = {0,4};
	   assertArrayEquals(positiveExpecteds, positives.toArray());
	   
	   Object negativeExpecteds[] = {3};
	   assertArrayEquals(negativeExpecteds, negatives.toArray());
	}

	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#AnnelCol(ArrayList, ArrayList)}.
	 */
	@Ignore
	@Test
	public void testAnnelCol2() {
		//fail("Not yet implemented");
		System.out.println("testAnnelCol2()");
		int incidence[][] = {
    		    { 0,  1, -1},
    		    { 0,  1,  1},
    		    { 0, -1, -1},
    		    { 0, -1,  1},
    		    { 0, -1,  1}
        };
		/**
		 * pk,vk:the number of positives and negatives in column k of A(k)
		 * 选择列：pk*vk-(pk+vk) < 0  (1)
		 * 或pk*vk最小                                              (2)  pk或vk至少有1个大于0，即至少有一个正或负元素
		 * p0=2,v0=3, pk*vk-(pk+vk) = 0
		 * p1=3,v1=2, pk*vk-(pk+vk) = 2*3-(2+3) = 1  符合条件(1)
		 * p2=2,v2=2, pk*vk-(pk+vk) = 3*2-(3+2) = 1
		 * 选择第3(index)列，为待删除列
		 */
    
       InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	   incidenceM.print("incidence:");
	   incidenceM.print(4, 0);
	   ComputeInvariant computeInvariant = new ComputeInvariant(incidenceM);  
	   
	   ArrayList<Integer> positives = new ArrayList<>();
	   ArrayList<Integer> negatives = new ArrayList<>();
	   int col;
	   col = computeInvariant.AnnelCol(positives, negatives);
	   computeInvariant.println("col="+col);  // k
	   computeInvariant.println("positives="+positives);
	   computeInvariant.println("negatives="+negatives);  
	   
	   assertEquals(col,1);
	   assertEquals(positives.size(),2);  // pk
	   assertEquals(negatives.size(),3);  // vk
	   
	   Object positiveExpecteds[] = {0,1};
	   assertArrayEquals(positiveExpecteds, positives.toArray());
	   
	   Object negativeExpecteds[] = {2,3,4};
	   assertArrayEquals(negativeExpecteds, negatives.toArray());
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#AnnelCol(ArrayList, ArrayList)}.
	 */
	@Ignore
	@Test
	public void testAnnelCol3() {
		//fail("Not yet implemented");
		System.out.println("testAnnelCol3()");
		int incidence[][] = {
    		    { 1, 0, -1},
    		    { 1, 0,  1},
    		    {-1, 0, -1},
    		    {-1, 0,  1},
    		    {-1, 0,  1}
        };
		/**
		 * pk,vk:the number of positives and negatives in column k of A(k)
		 * 选择列：pk*vk-(pk+vk) < 0  (1)
		 * 或pk*vk最小                                              (2)  pk或vk至少有1个大于0，即至少有一个正或负元素
		 * p0=2,v0=3, pk*vk-(pk+vk) = 0
		 * p1=3,v1=2, pk*vk-(pk+vk) = 2*3-(2+3) = 1  符合条件(1)
		 * p2=2,v2=2, pk*vk-(pk+vk) = 3*2-(3+2) = 1
		 * 选择第3(index)列，为待删除列
		 */
    
       InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	   incidenceM.print("incidence:");
	   incidenceM.print(4, 0);
	   ComputeInvariant computeInvariant = new ComputeInvariant(incidenceM);  
	   
	   ArrayList<Integer> positives = new ArrayList<>();
	   ArrayList<Integer> negatives = new ArrayList<>();
	   int col;
	   col = computeInvariant.AnnelCol(positives, negatives);
	   computeInvariant.println("col="+col);  // k
	   computeInvariant.println("positives="+positives);
	   computeInvariant.println("negatives="+negatives);  
	   
	   assertEquals(col,0);
	   assertEquals(positives.size(),2);  // pk
	   assertEquals(negatives.size(),3);  // vk
	   
	   Object positiveExpecteds[] = {0,1};
	   assertArrayEquals(positiveExpecteds, positives.toArray());
	   
	   Object negativeExpecteds[] = {2,3,4};
	   assertArrayEquals(negativeExpecteds, negatives.toArray());
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.ComputeInvariant#AnnelCol(ArrayList, ArrayList)}.
	 */
	@Ignore
	@Test
	public void testAnnelCol4() {
		//fail("Not yet implemented");
		System.out.println("testAnnelCol4()");
		int incidence[][] = {
    		    { 0, 0},
    		    { 0, 0},
    		    { 0, 0},
    		    { 0, 0},
    		    { 0, 0}
        };
		/**
		 * pk,vk:the number of positives and negatives in column k of A(k)
		 * 选择列：pk*vk-(pk+vk) < 0  (1)
		 * 或pk*vk最小                                              (2)  pk或vk至少有1个大于0，即至少有一个正或负元素
		 * p0=2,v0=3, pk*vk-(pk+vk) = 0
		 * p1=3,v1=2, pk*vk-(pk+vk) = 2*3-(2+3) = 1  符合条件(1)
		 * p2=2,v2=2, pk*vk-(pk+vk) = 3*2-(3+2) = 1
		 * 选择第3(index)列，为待删除列
		 */
    
       InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	   incidenceM.print("incidence:");
	   incidenceM.print(4, 0);
	   ComputeInvariant computeInvariant = new ComputeInvariant(incidenceM);  
	   
	   ArrayList<Integer> positives = new ArrayList<>();
	   ArrayList<Integer> negatives = new ArrayList<>();
	   int col;
	   col = computeInvariant.AnnelCol(positives, negatives);
	   computeInvariant.println("col="+col);  // k
	   computeInvariant.println("positives="+positives);
	   computeInvariant.println("negatives="+negatives);  
	   
	   assertEquals(col,-1);
	}
}
