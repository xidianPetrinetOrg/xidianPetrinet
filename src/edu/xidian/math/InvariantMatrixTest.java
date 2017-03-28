/**
 * 
 */
package edu.xidian.math;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.misc.CollectionUtils;
import jdk.nashorn.internal.ir.LiteralNode.ArrayLiteralNode.ArrayUnit;

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
		System.out.println("testCardinalityOneIntArray()");
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
	 * Test method for {@link edu.xidian.math.InvariantMatrix#cardinalityOne1(int[])}.
	 */
	@Ignore("testCardinalityOne1IntArray()")
	@Test
	public void testCardinalityOne1IntArray() {
		System.out.println("testCardinalityOne1IntArray()");
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
       
        int a[] = {-1,-2,-3};
        int r = m.cardinalityOne1(a);
        System.out.println("r="+r+","+a[0]+","+a[1]+","+a[2]);
        assertEquals(r,0);
        int[] expecteds = {3,4,3};
        assertArrayEquals(expecteds, a);
        
        ////////////////////////////////////////////////////////////////
		int incidence1[][] = {
    		    {-1,  0,  0,  0},
    		    { 1, -1,  0,  0},
    		    { 1,  1, -1,  0},
    		    { 0, -1,  1,  1},
        };
    
        invariantM = new Matrix(incidence1);           
        m = new InvariantMatrix(invariantM);      
        r = m.cardinalityOne1(a);
        System.out.println("r="+r+","+a[0]+","+a[1]+","+a[2]);
        assertEquals(r,0);
        expecteds[0] = 3; expecteds[1] = 2; expecteds[2] = 2;
        assertArrayEquals(expecteds, a);
        
		int incidence2[][] = {
    		    {-1,  0,  0,  0},
    		    {-1, -1,  0,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  1},
        };
    
        invariantM = new Matrix(incidence2);           
        m = new InvariantMatrix(invariantM);      
        r = m.cardinalityOne1(a);
        System.out.println("r="+r+","+a[0]+","+a[1]+","+a[2]);
        assertEquals(r,0);
        expecteds[0] = 3; expecteds[1] = 1; expecteds[2] = 1;
        assertArrayEquals(expecteds, a);
        
		int incidence3[][] = {
    		    {-1,  0, -2,  0},
    		    {-1,  1,  2,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  0},
        };
    
        invariantM = new Matrix(incidence3);           
        m = new InvariantMatrix(invariantM);      
        r = m.cardinalityOne1(a);
        System.out.println("r="+r);
        assertEquals(r,-1);  // 未找到
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#linearlyCombine(int i,int k,int j)}.
	 */
	@Ignore
	@Test
	public void testLinearlyCombine() {
		System.out.println("testLinearlyCombine()");
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
	 * Test method for {@link edu.xidian.math.InvariantMatrix#logicalUnion(int, int)}.
	 */
	@Ignore
	@Test
	public void testLogicalUnion() {
		System.out.println("testLogicalUnion()");
		int incidence[][] = {
    		    { 1,  0,  1,  0},
    		    { 1,  1,  1,  1},
    		    { 1,  0,  1,  0},
    		    { 0,  0,  1,  1},
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print("Before logicalUnion:");
		m.print(4, 0);
		
		int row1, row2;
		row1=3; row2=0;
		m.logicalUnion(row1,row2);
		m.print("logicalUnion rows:"+row1+" to "+row2);
		m.print(4,0);
		Object a[] = {1,0,1,1};
		Object b[] = new Object[4];
		for(int i = 0; i<4;i++) b[i]=m.get(row2, i);
		assertArrayEquals(a, b);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#AppendRowlogicalUnion(int, int)}.
	 */
	@Ignore
	@Test
	public void testAppendRowLogicalUnion() {
		System.out.println("testLogicalUnion()");
		int incidence[][] = {
    		    { 1,  0,  1,  0},
    		    { 1,  1,  1,  1},
    		    { 1,  0,  1,  0},
    		    { 0,  0,  1,  1},
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print("Before logicalUnion:");
		m.print(4, 0);
		
		int row1, row2;
		row1=3; row2=0;
		InvariantMatrix m1 = m.AppendRowlogicalUnion(row1,row2);
		m1.print("append logicalUnion rows:"+row1+","+row2);
		m1.print(4,0);
		int rows = m1.getRowDimension();
		assertEquals(rows,m.getRowDimension()+1);
		
		Object a[] = {1,0,1,1};
		Object b[] = new Object[4];
		for(int i = 0; i<4;i++) b[i]=m1.get(rows-1, i);
		assertArrayEquals(a, b);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#eliminateRow(int toDelete)}.
	 */
	@Ignore
	@Test
	public void testEliminateRow() {
		System.out.println("testEliminateRow()");
		int incidence[][] = {
    		    {-1,  0, -2,  0},
    		    {-1,  1,  2,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  0},
    		    { 2,  3,  4,  5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print("Before Eliminate row:");
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
	 * Test method for {@link edu.xidian.math.InvariantMatrix#eliminateRows(ArrayList)}.
	 */
	@Ignore
	@Test
	public void testEliminateRows() {
		System.out.println("testEliminateRows()");
		int incidence[][] = {
    		    {-1,  0, -2,  0},
    		    {-1,  1,  2,  0},
    		    { 1,  0, -1,  0},
    		    { 1,  1,  1,  0},
    		    { 2,  3,  4,  5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print("Before Eliminate rows:");
		m.print(4, 0);
		
		ArrayList<Integer> rows = new ArrayList<>();
		rows.add(0);rows.add(2);
		InvariantMatrix m1 = m.eliminateRows(rows);
		m1.print("After Eliminate row 0,2:");
		m1.print(4, 0);
		assertEquals(m.getRowDimension() - m1.getRowDimension(),2);
		
		rows.clear();
		rows.add(1); rows.add(2);
		m1 = m1.eliminateRows(rows);
		m1.print("After Eliminate row 1,2:");
		m1.print(4, 0);
		assertEquals(m1.getRowDimension(),1);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#negativeToZero(int col)}.
	 */
	@Ignore
	@Test
	public void testNegativeToZero() {
		System.out.println("testNegativeToZero()");
		int incidence[][] = {
    		    {-21,  0,  2,  0},
    		    {  1,  1,  2,  0},
    		    {  1,  0, -1,  0},
    		    { -1,  1,  1,  0},
    		    {  2,  3,  4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		for(int i=0;i<m.getColumnDimension();i++) {
		   m.negativeToZero(i);
		}
		assertEquals(incidence[0][0],0);
		assertEquals(incidence[0][3],0);
		m.print(4,0);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#gcdRow(int row)}.
	 */
	@Ignore
	@Test
	public void testGcdRow() {
		System.out.println("testGcdRow()");
		int incidence[][] = {
    		    { 4,  0,  2,  0},
    		    { 1,  1,  2,  0},
    		    { 1,  0,  1,  0},
    		    {-1,  1,  1,  0},
    		    {20, 30, 40,-50}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		assertEquals(m.gcdRow(0), 2);
		m.print("gcd="+m.gcdRow(4));
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#allNegativeOrZeroRow()}.
	 */
	@Ignore
	@Test
	public void testAllNegativeOrZeroRow() {
		System.out.println("testAllNegativeOrZeroRow()");
		int incidence[][] = {
    		    { 1,  0, -2,  0},
    		    { 1,  1,  2,  0},
    		    { 1,  0,  1,  0},
    		    {-1,  0, -4, -2},
    		    { 0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		while(true) {
		  int row = m.allNegativeOrZeroRow();
		  if (row == -1) break;
		  for (int j = 0; j < m.getColumnDimension(); j++)
		    if (m.get(row, j) < 0) m.set(row, j, Math.abs(m.get(row, j)));
		  m.print(4,0);
		}
		//assertEquals(a,-1);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#appendRow(int[])}.
	 */
	@Ignore
	@Test
	public void testAppendRow() {
		System.out.println("testAppendRow()");
		int incidence[][] = {
    		    { 1,  0, -2,  0},
    		    { 1,  1,  2,  0},
    		    { 1,  0,  1,  0},
    		    {-1,  0, -4, -2},
    		    { 0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		
		m.print("append1:");
		int row[] = {1,2,3,4};
		InvariantMatrix a = m.appendRow(row);
		a.print(4,0);
		assertEquals(a.m,6); // proteted成员a.m为何能在此访问?  因为与原类在同一包package edu.xidian.math;
		assertEquals(a.getRowDimension(),6);
		
		m.print("append2:");
		int row1[] = {1,2,3,4};
		InvariantMatrix b = a.appendRow(row1);
		b.print(4,0);
		assertEquals(b.m,7);
		assertEquals(b.getRowDimension(),7);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#getPositiveList(int)}.
	 */
	@Ignore
	@Test
	public void testGetPostiveList() {
		System.out.println("testGetPostiveList()");
		int incidence[][] = {
    		    {  1,  0, -2,  0},
    		    {  1,  1,  2,  0},
    		    {  1,  0,  1,  0},
    		    { -1,  0, -4, -2},
    		    {  0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);

		ArrayList<Integer> positives = m.getPositiveList(2);
		assertEquals(positives.size(),2);
		Object positiveExpecteds[] = {1,2};
		assertArrayEquals(positiveExpecteds, positives.toArray());
		m.print(positives+"\n");
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#getgetNegativeList(int)}.
	 */
	@Ignore
	@Test
	public void testGetNegativeList() {
		System.out.println("testGetNegativeList()");
		int incidence[][] = {
    		    {  1,  0, -2,  0},
    		    {  1,  1,  2,  0},
    		    {  1,  0,  1,  0},
    		    { -1,  0, -4, -2},
    		    {  0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);

		ArrayList<Integer> negatives = m.getNegativeList(2);
		assertEquals(negatives.size(),3);
		Object negativeExpecteds[] = {0,3,4};
		assertArrayEquals(negativeExpecteds, negatives.toArray());
		m.print(negatives+"\n");
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#positiveNegativeList(ArrayList, ArrayList, int)}.
	 */
	@Ignore
	@Test
	public void testPositiveNegativeList() {
		System.out.println("testPositiveNegativeList()");
		int incidence[][] = {
    		    {  1,  0, -2,  0},
    		    {  1,  1,  2,  0},
    		    {  1,  0,  1,  0},
    		    { -1,  0, -4, -2},
    		    {  0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);

		ArrayList<Integer> positives = new ArrayList<>();
		ArrayList<Integer> negatives = new ArrayList<>();
		m.positiveNegativeList(positives, negatives, 2);
		
		// +ve
		assertEquals(positives.size(),2);
		Object positiveExpecteds[] = {1,2};
		assertArrayEquals(positiveExpecteds, positives.toArray());
		
		// -ve
		assertEquals(negatives.size(),3);
		Object negativeExpecteds[] = {0,3,4};
		assertArrayEquals(negativeExpecteds, negatives.toArray());
		
		m.print("positives:"+positives+"\n");
		m.print("negatives"+negatives+"\n");
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#findNonMinimalRow()}.
	 */
	@Ignore
	@Test
	public void testFindNonMinimalRow() {
		System.out.println("testFindNonMinimalRow()");
		int incidence[][] = {
    		    {  1,  0, -2,  0},
    		    {  1,  1,  2,  0},
    		    {  1,  0,  1,  1},
    		    { -1,  0, -4, -2},
    		    {  0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		int nonMinmal = m.findNonMinimalRow();  
		m.print("nonMinmal=" + nonMinmal); /** 0-th row - 1-th row < 0 */
		assertEquals(nonMinmal,1);
		
		int incidence1[][] = {
    		    {  1,  0,  2,  0},
    		    {  1,  0,  0,  1},
    		    {  1,  1,  1,  0},
        };
		m = new InvariantMatrix(incidence1);
		m.print(4, 0);
		nonMinmal = m.findNonMinimalRow();  
		m.print("nonMinmal=" + nonMinmal); /** No exist i-th row - k-th row < 0; >0; 0*/
		assertEquals(nonMinmal,-1);
		
		int incidence3[][] = {
				 {  1,  0,  2,  0},
	    		 {  1,  0,  0,  1},
	    		 {  1,  1,  1,  2},
        };
		m = new InvariantMatrix(incidence3);
		m.print(4, 0);
		nonMinmal = m.findNonMinimalRow();  
		m.print("nonMinmal=" + nonMinmal); /** 1-th row - 2-th row < 0 */
		assertEquals(nonMinmal,2);
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#isDependenceRow(int[])}.
	 */
	@Ignore
	@Test
	public void testIsDependenceRow() {
		System.out.println("testIsDependenceRow()");
		int incidence[][] = {
    		    {  1,  0, -2,  0},
    		    {  1,  1,  2,  0},
    		    {  1,  0,  1,  1},
    		    { -1,  0, -4, -2},
    		    {  0,  0, -4, -5}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		
		int row[] = {1,2,3,4};
		assertTrue(m.isDependenceRow(row));
		int row1[] = {1,0,0,4};
		assertTrue(m.isDependenceRow(row1));
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#rank(InvariantMatrix)}.
	 */
	//@Ignore
	@Test
	public void testRank() {
		System.out.println("testRank()");
		int incidence[][] = {
				{3, 2, 2},
				{5, 3, 7},
				{8, 5, 9}
        };
		InvariantMatrix m = new InvariantMatrix(incidence);
		m.print(4, 0);
		
		int r;
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,2);
		
		///////////////////////////////////////
		int incidence1[][] = {
				{4, 3, -5, 6},
				{6, 2,  0, 3},
				{8, 5,-12, 5}
        };
		m = new InvariantMatrix(incidence1);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,3);
		
		///////////////////////////////////////
		int incidence2[][] = {
				{1, 1, 1, 1},
				{0, 0, 1, 1},
				{0, 0, 1, 1}
        };
		m = new InvariantMatrix(incidence2);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,2);
		
		///////////////////////////////////////
		int incidence3[][] = {
		{1, 3, 1, 9},
		{1, 1,-1, 1},
		{3,11, 5, 35}
		};
		m = new InvariantMatrix(incidence3);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,2);
		
		///////////////////////////////////////
		int incidence4[][] = {
		{1,  1, 0, 2},
		{-1,-1, 0, -2}
		};
		m = new InvariantMatrix(incidence4);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,1);
		
		///////////////////////////////////////
		int incidence5[][] = {
		{ 1, 0, 1},
		{-2,-3, 1},
		{ 3, 0, 3}
		};
		m = new InvariantMatrix(incidence5);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,2);
		
		///////////////////////////////////////
		int incidence6[][] = {
		{1,  1, 0, 0, 2},
		{0,  0, 0, 0, 0},
		{0,  0, 0, 0, 0},
		{1,  0, 0, 0, -2}
		};
		m = new InvariantMatrix(incidence6);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,2);
		
		///////////////////////////////////////
		int incidence7[][] = {
		{1,  1, 0, 0, 2},
		{0,  0, 0, 1, 0},
		{0,  1, 0, 1, 0},
		{1,  0, 0, 1, 2}
		};
		m = new InvariantMatrix(incidence7);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,3);
		
		///////////////////////////////////////
		int incidence8[][] = {
		{1,  1},
		{0,  1},
		{0,  1},
		{1,  0}
		};
		m = new InvariantMatrix(incidence8);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		assertEquals(r,2);
		
		///////////////////////////////////////
		int incidence9[][] = {
		{1,1,1,1,1,1,1,1,1},
		{0,0,2,1,1,1,1,1,1},
		{0,0,0,3,1,1,1,1,1},
		{0,0,0,0,0,0,4,1,1},
		{0,0,0,0,0,0,0,0,5},
		{0,0,0,0,0,0,0,0,0}
		};
		//https://en.wikipedia.org/wiki/Gaussian_elimination#Computing_ranks_and_bases
		m = new InvariantMatrix(incidence9);
		m.print(4, 0);
		
		r = m.rank(m);
		System.out.println("rank=" + r);
		//assertEquals(r,6);
		
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants1() {
		//fail("Not yet implemented");
		System.out.println("testInvariants1()");
		 // Li，图2.2
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
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    // Compute P-Invariants
        InvariantMatrix.invariants(invariantM);
        /***
          p1    p2    p3    p4    p5    p6    p7    p8    p9   p10   p11   p12
          1     1     1     0     0     0     0     0     1     1     0     0  (1)一致
          0     0     0     1     1     1     0     0     0     0     0     0  (2)一致
          0     1     0     0     0     1     1     0     0     0     0     0  (3)一致
          0     0     1     0     1     0     0     1     0     0     0     0   (4) 书没有
          0     0     0     0     0     0     0     0     0     1     1     0   (5) 书没有
          0     0     0     0     0     0     0     0     1     0     0     1   (6) 书没有
                            1     1     1                -1                -1    (2)-(6)一致
         */
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants2() {
		System.out.println("testInvariants2()");
		 // Li，图2.2
        int incidence[][] = {
        		      /** t1  t2  t3  t4  t5  t6  t7  t8  t9 **/
             /** p1 */  { 0,  0,  1,  0,  0,  0, -1,  0,  1},
             /** p2 */  { 1, -1,  0,  0,  0,  0,  0,  0,  0},
             /** p3 */  { 0,  1, -1,  0,  0,  0,  0,  0,  0},
             /** p4 */  { 0,  0,  0, -1,  0,  1,  0,  0,  0},
             /** p5 */  { 0,  0,  0,  1, -1,  0,  0,  0,  0},
             /** p6 */  { 0,  0,  0,  0,  1, -1,  0,  0,  0},
             /** p7 */	{-1,  1,  0,  0, -1,  1,  0,  0,  0},
             /** p8 */	{ 0, -1,  1, -1,  1,  0,  0,  0,  0},
             /** p9 */	{-1,  0,  0,  0,  0,  0,  1, -1,  0},
             /** p10*/	{ 0,  0,  0,  0,  0,  0,  0,  1, -1},
             /** p11*/	{ 0,  0,  0,  0,  0,  0,  0, -1,  1},
             /** p12*/  { 1,  0,  0,  0,  0,  0, -1,  1,  0}};
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    // Compute T-Invariants
        InvariantMatrix.invariants(invariantM.transpose());
       /**
         t1        t2        t3        t4        t5        t6        t7        t8        t9
         1         1         1         0         0         0         1         0         0     一致
         0         0         0         1         1         1         0         0         0     一致
         0         0         0         0         0         0         1         1         1     一致
        */
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants3() {
		System.out.println("testInvariants3()");
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
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    // Compute P-Invariants
        InvariantMatrix.invariants(invariantM);
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
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants4() {
		System.out.println("testInvariants4()");
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
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    // Compute P-Invariants
        InvariantMatrix.invariants(invariantM);
        /**
           1     0     0     1     0     0     1     0     0
           1     0     0     1     0     0     0     1     0
           1     0     0     1     0     0     0     0     1

         */
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants5() {
		System.out.println("testInvariants5()");
		// PIPEv4.3.0  5 place * 4 transition
        int incidence[][] = {
        		                 /**  t0  t1  t2  t3  **/
        		          /** p0 */ { -1,  1,  0,  0 },
        		          /** p1 */ {  1, -1,  0,  0 },
        		          /** p2 */ { -1,  1, -1,  1 },
        		          /** p3 */ {  0,  0, -1,  1 },
        		          /** p4 */ {  0,  0,  1, -1 }
        		         };
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    invariantM.print("Compute P-Invariants\n");
        InvariantMatrix.invariants(invariantM);
        /** p0    p1    p2    p3   p4  // 原文档：Invariant Analysis.htm
         	1     1     0     0     0  // M(p0)+M(p1) = 5
     		0     1     1     0     1  // M(p1)+M(p2)+3M(p4) = 3
     		0     0     0     1     1  // M(p3)+M(p4) = 2
         */
        
        // Compute T-Invariants
        invariantM.print("Compute T-Invariants\n");
        InvariantMatrix.invariants(invariantM.transpose());
        /**t0     t1    t2    t3  // 原文档：Invariant Analysis.htm
            1     1     0     0   // 1 1 0 0
     		0     0     1     1   // 0 0 3 3
         */
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants6() {
		System.out.println("testInvariants6()");
		// 袁崇义p52，
        int incidence[][] = {
        		                 /**  t1  t2  t3  t4  **/
        		          /** s1 */ { -1,  1,  0,  0 },
        		          /** s2 */ {  1, -1, -1,  1 },
        		          /** s3 */ {  0,  0,  1, -1 }
        		         };
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    invariantM.print("Compute P-Invariants\n");
        InvariantMatrix.invariants(invariantM);
        /** s1    s2    s3
         	1     1     1
         */
        
        // Compute T-Invariants
        invariantM.print("Compute T-Invariants\n");
        InvariantMatrix.invariants(invariantM.transpose());
        /**t1     t2    t3    t4
            1     1     0     0
     		0     0     1     1
         */
	}
	
	/**
	 * Test method for {@link edu.xidian.math.InvariantMatrix#invariants(edu.xidian.math.Matrix)}.
	 */
	@Ignore
	@Test
	public void testInvariants7() {
		System.out.println("testInvariants7()");
		// 袁崇义p52，
        int incidence[][] = {
        		                 /**  t1  t2  t3 **/
        		          /** s1 */ {  1, -1,  0 },
        		          /** s2 */ { -1,  1,  0 },
        		          /** s3 */ {  0,  1, -1 },
        		          /** s4 */ {  0, -1,  1 }
        		         };
	    InvariantMatrix invariantM = new InvariantMatrix(incidence);
	    
	    // Compute P-Invariants
	    invariantM.print("Compute P-Invariants\n");
        InvariantMatrix.invariants(invariantM);
        /** s1    s2    s3    s4
         	1     1     0     0
     		0     0     1     1
         */
        
        // Compute T-Invariants
        invariantM.print("Compute T-Invariants\n");
        InvariantMatrix.invariants(invariantM.transpose());
        /**t1     t2    t3
            1     1     1
         */
	}
}
