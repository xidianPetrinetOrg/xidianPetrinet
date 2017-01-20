/**
 * 
 */
package edu.xidian.math;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class InvariantMatrixTest {

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
	 * Test method for {@link edu.xidian.math.InvariantMatrix#cardinalityOne(int[])}.
	 */
	@Ignore("testCardinalityOneIntArray()")
	@Test
	public void testCardinalityOneIntArray() {
		int incidence[][] = {
    		    {-1,  0,  0,  0,  0,  0,  0,  1,  0},
    		    { 1, -1,  0,  0,  0,  0,  0,  0,  0},
    		    { 1,  0, -1,  0,  0,  0,  0,  0,  0},
    		    { 0,  0,  1,  1, -1, -1,  0,  0,  0},
    		    {-2,  1,  0, -1,  1,  0, -1,  0,  0},
    		    { 0,  0,  0,  0,  0,  0,  1,  0, -1},
    		    { 0,  1,  1,  0,  0,-29,  1,  0,  0},
    		    { 0, -1, -1,  0,  0, 29, -1,  0,  0}
        };
    
        Matrix invariantM = new Matrix(incidence);           
        //InvariantMatrix m = new InvariantMatrix(invariantM.transpose());
        InvariantMatrix m = new InvariantMatrix(invariantM);
        m.print("incidence:");
        m.print(4, 0);
       
        int a[] = {-1,-2};
        int r = m.cardinalityOne(a);
        System.out.println("r="+r+","+a[0]+","+a[1]);
        assertEquals(r,0);
        assertEquals(a[0],3); // row
        assertEquals(a[1],3); // column
        
        ////////////////////////////////////////////////////////////////
		int incidence1[][] = {
    		    {-1,  0,  0,  0},
    		    { 1, -1,  0,  0},
    		    { 1,  1, -1,  0},
    		    { 0, -1,  1,  1},
        };
    
        invariantM = new Matrix(incidence1);           
        m = new InvariantMatrix(invariantM);      
        r = m.cardinalityOne(a);
        System.out.println("r="+r+","+a[0]+","+a[1]);
        assertEquals(r,0);
        assertEquals(a[0],0); // row
        assertEquals(a[1],0); // column
        
		int incidence2[][] = {
    		    {-1,  0,  0,  0},
    		    {-1, -1,  0,  0},
    		    { 1,  1, -1,  0},
    		    { 1, -1,  1,  1},
        };
    
        invariantM = new Matrix(incidence2);           
        m = new InvariantMatrix(invariantM);      
        r = m.cardinalityOne(a);
        System.out.println("r="+r+","+a[0]+","+a[1]);
        assertEquals(r,0);
        assertEquals(a[0],2); // row
        assertEquals(a[1],1); // column
        
		int incidence3[][] = {
    		    {-1,  0, -2,  0},
    		    {-1,  1,  2,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  0},
        };
    
        invariantM = new Matrix(incidence3);           
        m = new InvariantMatrix(invariantM);      
        r = m.cardinalityOne(a);
        System.out.println("r="+r);
        assertEquals(r,-1);  // 未找到
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#linearlyCombine(int i,int k,int j)}.
	 */
	@Ignore
	@Test
	public void testLinearlyCombine() {
		int incidence[][] = {
    		    {-1,  0, -2,  0},
    		    {-1,  1,  2,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  0},
        };
		        
	    InvariantMatrix m = new InvariantMatrix(incidence);
	    m.print("Before linearlyCombine:");
	    m.print(4, 0);
	    
	    m.linearlyCombine(0, 3, 2);
	    m.print("After linearlyCombine(0,3,2):");
	    m.print(4, 0);
	    assertEquals(m.get(3, 2),0); 
	    
	    m.linearlyCombine(2, 3, 0);
	    m.print("After linearlyCombine(2,3,0):");
	    m.print(4, 0);
	    assertEquals(m.get(2, 3),0); 
	    
	    m.linearlyCombine(3, 2, 1);
	    m.print("After linearlyCombine(3,2,1):");
	    m.print(4, 0);
	    assertEquals(m.get(2, 1),0); 
	    
	    m.linearlyCombine(1, 0, 2);
	    m.print("After linearlyCombine(1,0,2):");
	    m.print(4, 0);
	    assertEquals(m.get(0, 2),0); 
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#eliminateRow(int toDelete)}.
	 */
	//@Ignore
	@Test
	public void testEliminateRow() {
		int incidence[][] = {
    		    {-1,  0, -2,  0},
    		    {-1,  1,  2,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  0},
    		    { 2,  3,  4,  5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print("Before Eliminate row 0:");
		m.print(4, 0);
		
		InvariantMatrix m1 = m.eliminateRow(0);
		m1.print("After Eliminate row 0:");
		m1.print(4, 0);
		assertEquals(m.getRowDimension() - m1.getRowDimension(),1);
		
		m1 = m1.eliminateRow(3);
		m1.print("After Eliminate row 3:");
		m1.print(4, 0);
		assertEquals(m1.getRowDimension(),3);
		
		m1 = m1.eliminateRow(1);
		m1.print("After Eliminate row 1:");
		m1.print(4, 0);
		assertEquals(m1.getRowDimension(),2);
	}

	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants() {
		fail("Not yet implemented");
	}

}
