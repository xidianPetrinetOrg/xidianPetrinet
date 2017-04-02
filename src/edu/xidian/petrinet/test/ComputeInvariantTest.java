package edu.xidian.petrinet.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.invation.code.toval.time.TimeScale;
import de.invation.code.toval.time.TimeValue;
import edu.xidian.math.InvariantMatrix;
import edu.xidian.petrinet.ComputeInvariant;

public class ComputeInvariantTest {

   ComputeInvariant computeInvariant;
   long start;
   
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
    * 经典 Figure 3 （1）
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
	    
	    System.out.println("经典，Figure 3 (1)， P-Invariants:");
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
    * 经典 Figure 3 （2）program net
    * @param k
    * @param t |T|, |P|=k*t
    */
   void setUp22(int k, int t) {
	   // 经典,A Simple and Fast Algorithm To Obain All Invariants Of A Generalised Petri Net
	   // Figure 3
       int incidence1111[][] = {
       		                 /** t1   t2   t3  **/
       		     /**pp0   p11*/ { 1,  -1,   0 },  /** k个 **/
       		          /** p12*/ { 1,  -1,   0 },
       		          /** p13*/ { 1,  -1,   0 },
       		    
       		     /**pp1   p21*/ { 0,   1,  -1 },  /** k个 **/
       		          /** p22*/ { 0,   1,  -1 },
       		          /** p23*/ { 0,   1,  -1 },
       		          
       		     /**pp2   p31*/ {-1,   0,   1 },  /** k个 **/
       		          /** p32*/ {-1,   0,   1 },
       		          /** p33*/ {-1,   0,   1 }
       		         };  // pp: |T|个
        
        //int k = 4;
        //int t = 5;    // |T|
        int p = k*t;  // |P|
        int incidence[][] = new int[p][t];
        
        int v = 0;
        for (int pp = 0; pp < t; pp++) {
	        for (int j = 0; j < t; j++) {
	        	if ( pp == t-1 ) { // 最后一个pp
	        		if (j == 0) v = -1;
	        		else if (j == t-1) v = 1;
	        		else v = 0;
	        	}
	        	else {  // 不是最后一个pp
	        		if (j == pp) v = 1;
	        		else if (j == pp+1) v = -1;
	        		else v = 0;
	        	}
	        	
	        	for (int m = 0; m < k; m++) incidence[pp*k+m][j] = v; 
	        }
        }
   
        
        
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	    
	    System.out.println("经典，Figure 3 (2)， P-Invariants:");
	    incidenceM.print("incidence:");
		incidenceM.print(4, 0);
		
		System.out.println("|p|="+t*k+",|T|="+t+",Invariants="+Math.pow(k, t));
		
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
		
		/**
		 [0]     15    15    15     0    13     2    28    15    28
         [1]      0     0     0     1     1     0     0     0     0
         [2]    435   435   435    28   405    58   812   435   812
		 Y3 = 29*Y1+28*Y2 
		 */
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
    
	// Fritz., Mathematics Methods for Calculate Invariants in Petri Nets, Compute P-Invariants
    void setUp6() {
		int incidence[][] = {
  		        /** t1  t2  t3  t4  t5  t6  t7 **/
       /** p1 */  { 1,  0,  0, 14,  0,  0, -3},
       /** p2 */  { 0,  0,  7,  0, -1, -2,  1},
       /** p3 */  { 0,  0, -1,  0, -2,  1,  0},
       /** p4 */  { 0,  0, -1, -1,  1,  0,  0},
       /** p5 */  { 1,  0,  0, -1,  0,  0,  0},
       /** p6 */  {-1,  0,  0,  1,  0,  0,  0},
       /** p7 */  { 0, -7,  0,  7,  0,  0,  0},
       /** p8 */  {-1,  1,  0,  0,  0,  0,  0}};
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    System.out.println("Fritz. Compute P-Invariants:");
	    incidenceM.print("incidence:");
	    incidenceM.print(4, 0);
 	   
		computeInvariant = new ComputeInvariant(incidenceM);
    }
	
    // (1) K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants
    void setUp7() {
		int incidence[][] = {
  		        /** t1  t2  t3  t4  t5  t6 **/
       /** p1 */  { 0,  0,  0, -1,  1,  0},
       /** p2 */  { 1,  0,  0,  0, -1,  0},
       /** p3 */  {-1,  0,  0,  1,  0,  0},
       /** p4 */  { 1, -1,  0,  0,  0,  0},
       /** p5 */  {-1,  1,  0,  0,  0,  0},
       /** p6 */  { 0,  1,  0,  0,  0, -1},
       /** p7 */  { 0, -1,  1,  0,  0,  0},
       /** p8 */  { 0,  0, -1,  0,  0,  1},
       /** p9 */  { 0,  0,  1, -1,  0,  0}};
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    System.out.println("(1) K. Takano. Compute P-Invariants:");
	    incidenceM.print("incidence:");
	    incidenceM.print(4, 0);
 	   
		computeInvariant = new ComputeInvariant(incidenceM);
    }
    
    // (2) K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants
    void setUp8() {
		int incidence[][] = {
  		        /** t1  t2  t3  t4  t5  t6 t7  t8 t9 **/
       /** p1 */  { 1, -1,  0,  0,  0,  0,  0,  0,  0},
       /** p2 */  {-1,  0,  1,  0,  0,  0,  0,  0,  0},
       /** p3 */  { 0, -1,  0, -1,  0,  0,  0,  0,  0},
       /** p4 */  { 0,  1, -1,  0,  0,  0,  0,  0,  0},
       /** p5 */  { 0,  0,  0,  1, -1,  1,  0,  0,  0},
       /** p6 */  { 0,  0,  1,  0,  1,  0,  0,  0, -1},
       /** p7 */  { 0,  0,  0,  0,  0,  1, -1,  0,  0},
       /** p8 */  { 0,  0,  0,  0,  0, -1,  0,  1,  0},
       /** p9 */  { 0,  0,  0,  0,  0,  0,  1, -1,  0},
       /** p10*/  { 0,  0,  0,  0,  0,  0,  1, -1,  1}};
	    InvariantMatrix incidenceM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    System.out.println("(2) K. Takano. Compute P-Invariants:");
	    incidenceM.print("incidence:");
	    incidenceM.print(4, 0);
 	   
		computeInvariant = new ComputeInvariant(incidenceM);
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
		//setUp2();  // 经典 Figure 3 (1), P-Invariants,|T|=3,|P|=3*3,
		//setUp22(3,2);  // 经典 Figure 3 (2), P-Invariants，|T|=2,|P|=3*2,9Invariants
		//setUp22(3,3);  // 经典 Figure 3 (2), P-Invariants，|T|=3,|P|=3*3,27Invariants
		
		// 经典 Figure 3 (2), P-Invariants，|T|=4,|P|=4*4,256Invariants
		// Debug: 2.56 seconds done; setDebug(false)不显示中间过程：58 milliseconds done.
		//setUp22(4,4); 
		
		// 经典 Figure 3 (2), P-Invariants，|T|=4,|P|=4*5,1024Invariants
		// Debug: 1.68 minutes done; setDebug(false)不显示中间过程：342 milliseconds done.
		//setUp22(4,5);  
		
		// 经典 Figure 3 (2), P-Invariants，|T|=5,|P|=5*5,3125Invariants, 
		// Debug：16.39 minutes done; setDebug(false)不显示中间过程：2.05 seconds done.
		//setUp22(5,5); 
		
		// 经典 Figure 3 (2), P-Invariants，|T|=6,|P|=6*6,46656Invariants
		// Debug：? minutes done; setDebug(false)不显示中间过程：9.81 minutes done.
		//setUp22(6,6); 
		
		// has minimal support invariants
		setUp3();  // Metabolites, T-Invariants
		
		
		//setUp4();  // Li，图2.2 Compute P-Invariants
		//setUp5();  // Li，图2.2 Compute T-Invariants
		
		// Fritz., Mathematics Methods for Calculate Invariants in Petri Nets, Compute P-Invariants
		//setUp6();
		
		// (1) K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants
		//setUp7();
		
		// (2) K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants
		//setUp8();
		
		//computeInvariant.setDebug(false);
		start = System.currentTimeMillis();
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()");
		
		InvariantMatrix invariants = computeInvariant.getInvariants();
		System.out.println("minimal support invariants:");
		invariants.print(4, 0);
		
		TimeValue runtime = new TimeValue(System.currentTimeMillis() - start, TimeScale.MILLISECONDS);
		runtime.adjustScale();
		
		System.out.println(runtime + " done.");
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
