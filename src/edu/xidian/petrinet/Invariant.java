package edu.xidian.petrinet;

import edu.xidian.math.Matrix;
import java.util.Date;
import java.util.LinkedList;

public class Invariant {

	    // private PetriNetView _pnmlData;        // A reference to the Petri Net to be analysed
	    private Matrix _incidenceMatrix;
	    private Matrix _PInvariants;
	    private static final String MODULE_NAME = "Invariant Analysis";
	    
	    public Invariant()
	    {
	    }

	    /**
	     * Invariant analysis was originally only performed on the initial markup
	     * matrix. This is not what the user expects however as when changes are made
	     * to the petri net, the invariant analysis then does not change to reflect
	     * this. The method calls have been changed to pass the current markup matrix
	     * as the parameter for invariant analysis.
	     *
	     * @param pnmlData
	     * @author Nadeem Akharware
	     * @version 1.2
	     * @return
	     */
	    private void analyse()
	    {
	        Date start_time = new Date(); // start timer for program execution
	        
	        int array[][] = {{0,0,1,0,0,0,-1,0,1},
	        		         {1,-1,0,0,0,0,0,0,0},
	        		         {0,1,-1,0,0,0,0,0,0},
	        		         {0,0,0,-1,0,1,0,0,0},
	        		         {0,0,0,1,-1,0,0,0,0},
	        		         {0,0,0,0,1,-1,0,0,0},
	        		         {-1,1,0,0,-1,1,0,0,0},
	        		         {0,-1,1,-1,1,0,0,0,0},
	        		         {-1,0,0,0,0,0,1,-1,0},
	        		         {0,0,0,0,0,0,0,1,-1},
	        		         {0,0,0,0,0,0,0,-1,1},
	                         {1,0,0,0,0,0,-1,1,0}};
	        
	        // 5 place * 4 transition
	        int array1[][] = {{-1,1,0,0},{1,-1,0,0},{-1,1,-1,1},{0,0,-1,1},{0,0,1,-1}};
	        
	        // Metabolites
	        int array2[][] = {{-1,1,1,0,0,0,0,0},
	        		          {0,-1,0,0,1,0,1,-1},
	        		          {0,0,-1,1,0,0,1,-1},
	        		          {0,0,0,1,-1,0,0,0},
	        		          {0,0,0,-1,1,0,0,0},
	        		          {0,0,0,-1,0,0,-29,29},
	        		          {0,0,0,0,-1,1,1,-1},
	        		          {1,0,0,0,0,0,0,0},
	        		          {0,0,0,0,0,-1,0,0}};
	        	        
	      //_incidenceMatrix = new Matrix(array);
	      //_incidenceMatrix = new Matrix(array1);
	      _incidenceMatrix = new Matrix(array2);
	        
	        System.out.println("incidenceMatrix:");
	        _incidenceMatrix.print(4, 0);
	        
	        Matrix vectors = findVectors(_incidenceMatrix.transpose());
	        //Matrix vectors = findVectors(_incidenceMatrix);
	        
	        String output="";
	        Date stop_time = new Date();
	        double etime = (stop_time.getTime() - start_time.getTime()) / 1000.;
	        System.out.println(output + "<br>Analysis time: " + etime + "s");
	        
	        vectors.print(4, 0);
	    }

	    /**
	     * Transform a matrix to obtain the minimal generating set of vectors.
	     * 列对应的不变式，极小向量
	     * @param c The matrix to transform.
	     *          关联矩阵： 本函数求T-Invariants;  若是关联矩阵的转置，则本函数求P-Invariants
	     * @return A matrix containing the vectors.
	     */
	    public Matrix findVectors(Matrix c)
	    {
	        /*
	       | Tests Invariant Analysis IModule
	       |
	       |   C          = incidence matrix. 
	                                                                        关联矩阵： 本函数求T-Invariants;  若是关联矩阵的转置，则本函数求P-Invariants
	                        每行通过线性组合，消除非零元素所在的列，最后直到为全零或空。                                                           
	       |   B          = identity matrix with same number of columns as C.
	       |                Becomes the matrix of vectors in the end.
	       |   pPlus      = integer array of +ve indices of a row.
	       |   pMinus     = integer array of -ve indices of a row.
	       |   pPlusMinus = set union of the above integer arrays.
	        */
	    	
	    	/***************************************************
	    	 * Initialisation: C: m*n关联矩阵， B: n*n单位阵 
	    	 * for (i=0;i<m;i++) // 遍历C各行
	    	 *   append列（该列是各行经适当线性组合，使C的第i行为的和0，可以消除该行）
	    	 *   delete
	    	 ***************************************************/
	    	
	    	/** 求列向量对应的不变式。 */
	        int m = c.getRowDimension(), n = c.getColumnDimension();
            
	        System.out.println("findVectors: incidence matrix c");
	        c.print(4, 0);
	        
	        // generate the nxn identity matrix
	        Matrix B = Matrix.identity(n, n);
	        // Matrix B = Matrix.identity(m, m);
	        
	        System.out.println("m*n="+m+"*"+n+",B:identity matrix with same number of columns as C(incidenceMatrix)");
	        B.print(4, 0);

	        // arrays containing the indices of +ve and -ve elements in a row vector
	        // respectively
	        int[] pPlus, pMinus;

	        // while there are no zero elements in C do the steps of phase 
	//--------------------------------------------------------------------------------------
	        // PHASE 1:
	//--------------------------------------------------------------------------------------
	        while(!(c.isZeroMatrix()))
	        {
	        	print("c.checkCase11() = " + c.checkCase11());
	            if(c.checkCase11())
	            {
	                System.out.println("ok,Case11()");
	            	// check each row (case 1.1)
	                for(int i = 0; i < m; i++)  // 遍历各行，
	                {
	                    pPlus = c.getPositiveIndices(i); // get +ve indices of ith row
	                    pMinus = c.getNegativeIndices(i); // get -ve indices of ith row
	                    print(i+" pPlus=");
	                    printArray(pPlus);
	                    print(i+" pMinus=");
	                    printArray(pMinus);
	                    
	                    if(isEmptySet(pPlus) || isEmptySet(pMinus))
	                    { // case-action 1.1.a
	                        // this has to be done for all elements in the union pPlus U pMinus
	                        // so first construct the union
	                        int[] pPlusMinus = uniteSets(pPlus, pMinus);

	                        // eliminate each column corresponding to nonzero elements in pPlusMinus union
	                        for(int j = pPlusMinus.length - 1; j >= 0; j--)
	                        {
	                            if(pPlusMinus[j] != 0)
	                            {
	                                c = c.eliminateCol(pPlusMinus[j] - 1);
	                                B = B.eliminateCol(pPlusMinus[j] - 1);
	                                n--;  // reduce the number of columns since new matrix is smaller
	                            }
	                        }
	                    }
	                    resetArray(pPlus);   // reset pPlus and pMinus to 0
	                    resetArray(pMinus);
	                }
	            }
	            else if(c.cardinalityCondition() >= 0) // >=0: 某行有一个+ve或-ve
	            {
	            	print("c.cardinalityCondition() = " + c.cardinalityCondition());
	            	
	            	while(c.cardinalityCondition() >= 0)
	                {
	                    // while there is a row in the C matrix that satisfies the cardinality condition
	                    // do a linear combination of the appropriate columns and eliminate the appropriate column.
	                    int cardRow = -1; // the row index where cardinality == 1
	                    cardRow = c.cardinalityCondition();  // +ve或-ve的行index
	                    // get the column index of the column to be eliminated
	                    int k = c.cardinalityOne();
	                    
	                    System.out.println("cardRow: " + cardRow);
	                    System.out.println("Column index to be eliminated: " + k );
	                    
	                    if(k == -1)
	                    {
	                        System.out.println("Error");
	                    }

	                    // get the comlumn indices to be changed by linear combination
	                    int j[] = c.colsToUpdate();

	                    // update columns with linear combinations in matrices C and B
	                    // first retrieve the coefficients
	                    int[] jCoef = new int[n];
	                    for(int i = 0; i < j.length; i++)
	                    {
	                        if(j[i] != 0)
	                        {
	                            jCoef[i] = Math.abs(c.get(cardRow, (j[i] - 1)));
	                        }
	                    }

	                    // do the linear combination for C and B
	                    // k is the column to add, j is the array of cols to add to
	                    c.linearlyCombine(k, Math.abs(c.get(cardRow, k)), j, jCoef);
	                    B.linearlyCombine(k, Math.abs(c.get(cardRow, k)), j, jCoef);

	                    // eliminate column of cardinality == 1 in matrices C and B
	                    c = c.eliminateCol(k);
	                    B = B.eliminateCol(k);
	                    // reduce the number of columns since new matrix is smaller
	                    n--;
	                    System.out.println("C and B ---");
		                c.print(4, 0);
		                B.print(4,0);
	                }
	            	System.out.println("C and B");
	                c.print(4, 0);
	                B.print(4,0);
	            }
	            else
	            {
	                // row annihilations (condition 1.1.b.2)
	                // operate only on non-zero rows of C (row index h)
	                // find index of first non-zero row of C (int h)
	                int h = c.firstNonZeroRowIndex();
	                System.out.println("row annihilations (condition 1.1.b.2),non-zero row of C = " +h);
	                while((h = c.firstNonZeroRowIndex()) > -1)
	                {

	                    // the column index of the first non zero element of row h
	                    int k = c.firstNonZeroElementIndex(h);

	                    // find first non-zero element at column k, chk
	                    int chk = c.get(h, k);

	                    // find all the other indices of non-zero elements in that row chj[]
	                    int[] chj = new int[n - 1];
	                    chj = c.findRemainingNZIndices(h);

	                    while(!(isEmptySet(chj)))
	                    {
	                        // chj empty only when there is just one nonzero element in the
	                        // whole row, this should not happen as this case is eliminated
	                        // in the first step, so we would never arrive at this while()
	                        // with just one nonzero element

	                        // find all the corresponding elements in that row (coefficients jCoef[])
	                        int[] jCoef = c.findRemainingNZCoef(h);

	                        // adjust linear combination coefficients according to sign
	                        int[] alpha, beta; // adjusted coefficients for kth and remaining columns respectively
	                        alpha = alphaCoef(chk, jCoef);
	                        beta = betaCoef(chk, jCoef.length);

	                        // linearly combine kth column, coefficient alpha, to jth columns, coefficients beta
	                        c.linearlyCombine(k, alpha, chj, beta);
	                        B.linearlyCombine(k, alpha, chj, beta);

	                        // delete kth column
	                        c = c.eliminateCol(k);
	                        B = B.eliminateCol(k);

	                        chj = c.findRemainingNZIndices(h);
	                    }
	                }
	                // show the result
	                System.out.println("Pseudodiagonal positive basis of Ker C after phase 1:");
	                B.print(2, 0);
	            }
	        }
	        System.out.println("end of phase one");
	        // END OF PHASE ONE, now B contains a pseudodiagonal positive basis of Ker C
	//--------------------------------------------------------------------------------------
	        // PHASE 2:
	//--------------------------------------------------------------------------------------
	        // h is -1 at this point, make it equal to the row index that has a -ve element.
	        // rowWithNegativeElement with return -1 if there is no such row, and we exit the loop.
	        int h;
	        while((h = B.rowWithNegativeElement()) > -1)
	        {

	            pPlus = B.getPositiveIndices(h); // get +ve indices of hth row (1st col = index 1)
	            pMinus = B.getNegativeIndices(h); // get -ve indices of hth row (1st col = index 1)

	            // effective length is the number of non-zero elements
	            int pPlusLength = effectiveSetLength(pPlus);
	            int pMinusLength = effectiveSetLength(pMinus);

	            if(pPlusLength != 0)
	            { // set of positive coef. indices must me non-empty
	                // form the cross product of pPlus and pMinus
	                // for each pair (j, k) in the cross product, operate a linear combination on the columns
	                // of indices j, k, in order to get a new col with a zero at the hth element
	                // The number of new cols obtained = the number of pairs (j, k)
	                for(int j = 0; j < pPlusLength; j++)
	                {
	                    for(int k = 0; k < pMinusLength; k++)
	                    {
	                        // coefficients of row h, cols indexed j, k in pPlus, pMinus
	                        // respectively
	                        int jC = pPlus[j] - 1, kC = pMinus[k] - 1;

	                        // find coeficients for linear combination, just the abs values
	                        // of elements this is more efficient than finding the least
	                        // common multiple and it does not matter since later we will
	                        // find gcd of col and we will normalise with that the col
	                        // elements
	                        int a = -B.get(h, kC), b = B.get(h, jC);

	                        // create the linear combination a*jC-column + b*kC-column, an
	                        // IntMatrix mx1 where m = number of rows of B
	                        m = B.getRowDimension();
	                        Matrix v1 = new Matrix(m, 1); // column vector mx1 of zeros
	                        Matrix v2 = new Matrix(m, 1); // column vector mx1 of zeros
	                        v1 = B.getMatrix(0, m - 1, jC, jC);
	                        v2 = B.getMatrix(0, m - 1, kC, kC);
	                        v1.timesEquals(a);
	                        v2.timesEquals(b);
	                        v2.plusEquals(v1);

	                        // find the gcd of the elements in this new col
	                        int V2gcd = v2.gcd();

	                        // divide all the col elements by their gcd if gcd > 1
	                        if(V2gcd > 1)
	                        {
	                            v2.divideEquals(V2gcd);
	                        }

	                        // append the new col to B
	                        n = B.getColumnDimension();
	                        Matrix f = new Matrix(m, n + 1);
	                        f = B.appendVector(v2);
	                        B = f.copy();
	                    }
	                } // endfor (j,k) operations

	                // delete from B all cols with index in pMinus
	                for(int ww = 0; ww < pMinusLength; ww++)
	                {
	                    B = B.eliminateCol(pMinus[ww] - 1);
	                }

	            } // endif
	        } // end while
	        System.out.println("\nAfter column transformations in phase 2 (non-minimal generating set) B:");
	        B.print(2, 0);

	        // delete from B all cols having non minimal support
	        // k is the index of column to be eliminated, if it is -1 then there is
	        // no col to be eliminated
	        int k = 0;
	        // form a matrix with columns the row indices of non-zero elements
	        Matrix bi = B.nonZeroIndices();

	        while(k > -1)
	        {
	            k = bi.findNonMinimal();

	            if(k != -1)
	            {
	                B = B.eliminateCol(k);
	                bi = B.nonZeroIndices();
	            }
	        }

	        // display the result
	        System.out.println("Minimal generating set (after phase 2):");
	        B.print(2, 0);
	        return B;
	    }

	    /**
	     * find the number of non-zero elements in a set
	     *
	     * @param pSet The set count the number of non-zero elements.
	     * @return The number of non-zero elements.
	     */
	    private int effectiveSetLength(int[] pSet)
	    {
	        int effectiveLength = 0; // number of non-zero elements
	        int setLength = pSet.length;

	        for(int i = 0; i < setLength; i++)
	        {
	            if(pSet[i] != 0)
	            {
	                effectiveLength++;
	            }
	            else
	            {
	                return effectiveLength;
	            }
	        }
	        return effectiveLength;
	    }

	    /**
	     * adjust linear combination coefficients according to sign
	     * if sign(j) <> sign(k) then alpha = abs(j) beta = abs(k)
	     * if sign(j) == sign(k) then alpha = -abs(j) beta = abs(k)
	     *
	     * @param k The column index of the first coefficient
	     * @param j The column indices of the remaining coefficients
	     * @return The adjusted alpha coefficients
	     */
	    private int[] alphaCoef(int k, int[] j)
	    {
	        int n = j.length; // the length of one row
	        int[] alpha = new int[n];

	        for(int i = 0; i < n; i++)
	        {
	            if((k * j[i]) < 0)
	            {
	                alpha[i] = Math.abs(j[i]);
	            }
	            else
	            {
	                alpha[i] = -Math.abs(j[i]);
	            }
	        }
	        return alpha;
	    }

	    /**
	     * adjust linear combination coefficients according to sign
	     * if sign(j) <> sign(k) then alpha = abs(j) beta = abs(k)
	     * if sign(j) == sign(k) then alpha = -abs(j) beta = abs(k)
	     *
	     * @param chk The first coefficient
	     * @param n   The length of one row
	     * @return The adjusted beta coefficients
	     */
	    private int[] betaCoef(int chk, int n)
	    {
	        int[] beta = new int[n];
	        int abschk = Math.abs(chk);

	        for(int i = 0; i < n; i++)
	        {
	            beta[i] = abschk;
	        }
	        return beta;
	    }

	    private void resetArray(int[] a)
	    {
	        for(int i = 0; i < a.length; i++)
	        {
	            a[i] = 0;
	        }
	    }

	    /**
	     * Unite two sets (arrays of integers) so that if there is a common entry in
	     * the arrays it appears only once, and all the entries of each array appear
	     * in the union. The resulting array size is the same as the 2 arrays and
	     * they are both equal. We are only interested in non-zero elements. One of
	     * the 2 input arrays is always full of zeros.
	     *
	     * @param A The first set to unite.
	     * @param B The second set to unite.
	     * @return The union of the two input sets.
	     */
	    private int[] uniteSets(int[] A, int[] B)
	    {
	        int[] union = new int[A.length];

	        if(isEmptySet(A))
	        {
	            union = B;
	        }
	        else
	        {
	            union = A;
	        }
	        return union;
	    }

	    /**
	     * check if an array is empty (only zeros)
	     *
	     * @param pSet The set to check if it is empty.
	     * @return True if the set is empty.
	     */
	    private boolean isEmptySet(int[] pSet)
	    {
	        int setLength = pSet.length;

	        for(int i = 0; i < setLength; i++)
	        {
	            if(pSet[i] != 0)
	            {
	                return false;
	            }
	        }
	        return true;
	    }
	    
		/**
		 * used to display intermiadiate results for checking
		 *
		 * @param a
		 *            The array to print.
		 */
		public void printArray(int[] a) {
			int n = a.length;
	
			for (int i = 0; i < n; i++)
				System.out.print(a[i] + " ");
			System.out.println();
		}
	
		/**
		 * Shorten spelling of print.
		 * 
		 * @param s
		 *            The string to print.
		 */
		private void print(String s) {
			System.out.println(s);
		}
		  
	    /** Format double with Fw.d.
	     *
	     * @param x  The format of the string.
	     * @param w  The length of the string.
	     * @param d  The number of fraction digits.
	     * @return The string to print.
	     * */
	//  public String fixedWidthDoubletoString (double x, int w, int d) {
//	    java.text.DecimalFormat fmt = new java.text.DecimalFormat();
//	    fmt.setMaximumFractionDigits(d);
//	    fmt.setMinimumFractionDigits(d);
//	    fmt.setGroupingUsed(false);
//	    String s = fmt.format(x);
//	    while (s.length() < w) {
//	      s = " " + s;
//	    }
//	    return s;
	//  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Invariant vectors = new Invariant();
		vectors.analyse();
	}

}
