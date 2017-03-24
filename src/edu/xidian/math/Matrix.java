/**
 * copy from PIPEv4.3.0_src\\PIPEv4.3.0_src\\src\\pipe\\utilities\\math\\Matrix.java
 * This support class provides many general matrix manipulation functions, as 
 * well as a number of specialised matrix operations pertaining to Petri net 
 * analysis.
 * @author Manos Papantoniou & Michael Camacho
 * @version February 2004
 *
 * Based on the Jama Matrix class, the PNMatrix class offers a small subset
 * of the operations, and is used for matrices of integers only, as required
 * by the petri net analyser project.
 *
 * <P>
 * This Class provides the fundamental operations of numerical linear algebra.  
 * Various constructors create Matrices from two dimensional arrays of integer 
 * numbers. 
 * Various "gets" and "sets" provide access to submatrices and matrix elements. 
 * Several methods implement basic matrix arithmetic, including matrix addition 
 * and multiplication, and element-by-element array operations.
 * Methods for reading and printing matrices are also included.
 * <P>
 * @author Edwin Chung a new boolean attribute was added (6th Feb 2007)
 * @author Pere Bonet (minor changes)
 */

package edu.xidian.math;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


public class Matrix implements Serializable {
	private static final long serialVersionUID = 1L;

/**
    * Array for internal storage of elements.
    * @serial internal array storage.
    */
   protected int[][] A;
   
   /**
    * Row and column dimensions.
    * @serial m: row dimension.
    * @serial n: column dimension.
    */
   protected int m, n;
   
   /** Used to determine whether the matrixes have been modified */
   public boolean matrixChanged;

   
   /**
    * Construct an m-by-n matrix of zeros.
    * @param m    Number of rows.
    * @param n    Number of colums.
    */
   public Matrix(int m, int n) {
      this();
      this.m = m;
      this.n = n;
      A = new int[m][n];
   }


   /**
    * Construct an m-by-n matrix from another PNMatrix.
    * @param m    Number of rows.
    * @param n    Number of colums.
    * @param b
    */
   public Matrix(Matrix b) {
      this();
      this.m = b.m;
      this.n = b.n;
      A = b.A.clone();
   }

   
   /**
    * Construct a matrix from a 2-D array.
    * @param A Two-dimensional array of integers.
    * @exception IllegalArgumentException All rows must have the same length
    * @see #constructWithCopy
    */
   public Matrix(int[][] A) {
      this();
      if (A != null) {
         m = A.length;  // 行的长度
         if (A.length >= 1) {
            n = A[0].length; // 第1列的长度,n
            for (int i = 0; i < m; i++) {
               if (A[i].length != n) { // 检查第i列的长度是否是n
                  throw new IllegalArgumentException(
                           "All rows must have the same length.");
               }
            }
            this.A = A;
         }
      }
   }

   
   /**
    * Construct a matrix from a one-dimensional packed array
    * @param vals One-dimensional array of integers, packed by columns (ala Fortran).
    *        vals是先列后行的存储方式，采用类似于Fortran的存储方式
    * @param m Number of rows.
    * @exception IllegalArgumentException Array length must be a multiple of m.
    */
   public Matrix(int vals[], int m) {
      this();
      this.m = m;
      n = (m != 0 ? vals.length/m : 0);
      if (m*n != vals.length) {
         throw new IllegalArgumentException("Array length must be a multiple of m.");
      }
      A = new int[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {  // “先列后行”式存储，java,c等，都是按照"先行后列"式存储，即A[i][j] = vals[i*m+j];
            A[i][j] = vals[i+j*m];
         }
      }
   }

   private Matrix() {
      matrixChanged = true;
   }
   
   /**
    * Append a column matrix (vector) to the right of another matrix.
    * @param x Column matrix (vector) to append.
    * @return The matrix with the column vector appended to the right.
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public Matrix appendVector(Matrix x) {
      Matrix r = new Matrix(m, n+1);  // 扩充一列
      // the extended matrix
      r.setMatrix(0, m-1, 0, n-1, this);
      
      try {         
         for (int i = 0; i < m; i++){
            r.set(i, n, x.get(i, 0));
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Row indices incompatible");
      }
      return r;
   }

   
   /**
    * Check if a matrix has a row that satisfies the cardinality condition 1.1.b
    * of the algorithm. 满足算法的基数条件。基数=1，表示存在行，满足条件：仅有一个+ve或-ve
    * @return True if the matrix satisfies the condition and linear combination 
    *              of columns followed by column elimination is required.<br>
    *         >=0 满足算法的基数条件。基数=1：存在行，满足条件：仅有一个+ve或-ve,返回该行的index<br>
    *         <0 没有这样的行
    */
   public int cardinalityCondition(){
      int cardRow = -1; // a value >= 0 means either pPlus or pMinus have 
      // cardinality == 1 and it is the value of the row where this condition 
      // occurs -1 means that both pPlus and pMinus have cardinality != 1
      int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
      int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
      // int m = getRowDimension(), n = getColumnDimension();  // 直接使用本类的m,n即可

       for (int i = 0; i < m; i++){
         countpPlus = 0;
         countpMinus = 0;
         pPlus = getPositiveIndices(i); // get +ve indices of ith row
         pMinus = getNegativeIndices(i); // get -ve indices of ith row
         for (int j = 0; j < n; j++){
            if (pPlus[j] != 0) { // if there is nonzero element count it
               countpPlus++;
            }
         }
         for (int j = 0; j < n; j++){
            if (pMinus[j] != 0) { // if there is nonzero element count it
               countpMinus++;
            }
         }
         if (countpPlus == 1 || countpMinus == 1){
            return i;
         }
      }
      return cardRow;
   }

   
   /**
    * Find the column index of the element in the pPlus or pMinus set, where 
    * pPlus or pMinus has cardinality == 1.
    * 满足算法的基数条件。基数=1，表示存在行，满足条件：仅有一个+ve或-ve。
    * @return The column index(+ve或-ve所在列的index), -1 if unsuccessful (this shouldn't happen under 
    *                    normal operation).<br>
    */
   public int cardinalityOne(){
      int k = -1; // the col index of cardinality == 1 element
      
      int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
      int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
      // int m = getRowDimension(), n = getColumnDimension();  // 直接使用本类的m,n即可

       for (int i = 0; i < m; i++){
         countpPlus = 0;
         countpMinus = 0;
         pPlus = getPositiveIndices(i); // get +ve indices of ith row
         pMinus = getNegativeIndices(i); // get -ve indices of ith row
         for (int j = 0; j < n; j++){
            if (pPlus[j] != 0) { // if there is nonzero element count it
               countpPlus++;
            }
         }
         for (int j = 0; j < n; j++){
            if (pMinus[j] != 0) { // if there is nonzero element count it
               countpMinus++;
            }
         }
         if (countpPlus == 1) {
            return pPlus[0] - 1;
         }
         if (countpMinus == 1) {
            return pMinus[0] - 1;
         }
      }
      
      return k;
   }

   
   /**
    * Check if a matrix satisfies condition 1.1 of the algorithm.
    * 是否满足算法条件：存在非零元素，需要消除列
    * @return True if the matrix satisfies the condition and column elimination 
    *              is required. <br>
    *              存在非零元素，需要消除列。<br>
    *         False: 全0<br>
    *         函数编制太繁琐，仅根据isZeroMatrix()的返回值判断即可。
    */
   public boolean checkCase11(){
      boolean satisfies11 = false; // true means there is an empty set pPlus or pMinus
      // false means that both pPlus and pMinus are non-empty
      boolean pPlusEmpty = true, pMinusEmpty = true;
      int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
      // int m = getRowDimension();  // 直接使用本类的m,n即可
      
      for (int i = 0; i < m; i++){
         pPlusEmpty = true;
         pMinusEmpty = true;
         pPlus = getPositiveIndices(i); // get +ve indices of ith row
         pMinus = getNegativeIndices(i); // get -ve indices of ith row
         int pLength = pPlus.length, mLength = pMinus.length;  // 二者应该是相等的
                  
         for (int j = 0; j < pLength; j++){
            if (pPlus[j] != 0) {
               // if there is nonzero element then false (non-empty set)
               pPlusEmpty = false;
            }
         }
         for (int j = 0; j < mLength; j++){
            if (pMinus[j] != 0) { 
               // if there is nonzero element then false (non-empty set)
               pMinusEmpty = false;
            }
         }
         // if there is an empty set and it is not a zeros-only row then column 
         // elimination is possible
         if ((pPlusEmpty || pMinusEmpty) && !isZeroRow(i)){
            return true;
         }
         
         // reset pPlus and pMinus to 0
         for (int j = 0; j < pLength; j++){
            pPlus[j] = 0;
         }         
         for (int j = 0; j < mLength; j++){
            pMinus[j] = 0;
         }
      }
      return satisfies11;
   }

   
   /**
    * Find the comlumn indices to be changed by linear combination.
    * @return An array of integers, these are the indices increased by 1 each.<br>
    * 存在行，满足算法的基数条件，cardinality ==1; 
    * 如果仅有一个+ve， 返回pMinus[],每个非零元素的值是-ve元素的index+1
    * 如果仅有一个-ve， 返回pPlus[],每个非零元素的值是+ve元素的index+1
    * 否则，不满足算法基数条件，返回null
    */
   public int[] colsToUpdate(){
      int js[] = null; // An array of integers with the comlumn indices to be 
      // changed by linear combination.
      //the col index of cardinality == 1 element
      
      int countpPlus = 0, countpMinus = 0;
      int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
      // int m = getRowDimension();  // 直接使用本类的m,n即可
      // int n = getColumnDimension(); // 直接使用本类的m,n即可

       for (int i = 0; i < m; i++){
         countpPlus = 0;
         countpMinus = 0;
         pPlus = getPositiveIndices(i); // get +ve indices of ith row
         pMinus = getNegativeIndices(i); // get -ve indices of ith row
         
         for (int j = 0; j < n; j++){
            if (pPlus[j] != 0) { // if there is nonzero element count it
               countpPlus++;
            }
         }
         
         for (int j = 0; j < n; j++){
            if (pMinus[j] != 0) { // if there is nonzero element count it
               countpMinus++;
            }
         }
         
         // if pPlus has cardinality ==1 return all the elements in pMinus reduced by 1 each
         if (countpPlus == 1) {
            return pMinus;
         } else if (countpMinus == 1) {
         // if pMinus has cardinality ==1 return all the elements in pPlus reduced by 1 each            
            return pPlus;
         }
      }
      return js;
   }

   
   /**
    * Construct a matrix from a copy of a 2-D array.
    * @param A Two-dimensional array of integers.
    * @return The copied matrix.
    * @exception IllegalArgumentException All rows must have the same length
    */
   public static Matrix constructWithCopy(int[][] A) {
      int m = A.length;
      int n = A[0].length;
      Matrix x = new Matrix(m,n);
      int[][] C = x.getArray();
      for (int i = 0; i < m; i++) {
         if (A[i].length != n) {
            throw new IllegalArgumentException
                     ("All rows must have the same length.");
         }
          System.arraycopy(A[i], 0, C[i], 0, n);
      }
      return x;
   }

   
   /**
    * Make a deep copy of a matrix
    * @return The matrix copy.
    */
   public Matrix copy() {
      Matrix x = new Matrix(m,n);
      int[][] C = x.getArray();
      for (int i = 0; i < m; i++) {
          System.arraycopy(A[i], 0, C[i], 0, n);
      }
      return x;
   }

   
   /**
    * Clone the IntMatrix object.
    * @return  The clone of the current matrix.
    */
   public Object clone() {
      return this.copy();
   }

   
   /**
    * Eliminate a column from the matrix, column index is toDelete
    * @param  toDelete  The column number to delete.
    * @return The matrix with the required row deleted.
    */
   public Matrix eliminateCol(int toDelete) {
      // int m = getRowDimension(), n = getColumnDimension();  // 直接使用本类的m,n即可
      Matrix reduced = new Matrix(m, n);
      int [] cols = new int [n-1]; // array of cols which will not be eliminated
      int count = 0;
      
      // find the col numbers which will not be eliminated
      for (int i = 0; i < n; i++) {
         // if an index will not be eliminated, keep it in the new array cols
         if (i != toDelete) {
            cols[count++] = i;
         }
      }
      // System.out.print("Eliminating column " + toDelete + " from matrix below... keeping columns ");
      // printArray(cols);
      // print(2, 0);
      //  System.out.println("Reduced matrix");
      reduced = getMatrix(0, m-1, cols);
      //  reduced.print(2, 0);
      
      return reduced;
   }

   
   /**
    * Access the internal two-dimensional array.
    * @return Pointer to the two-dimensional array of matrix elements.
    */
   int[][] getArray() {
      return A;
   }

   
   /**
    * Copy the internal two-dimensional array.
    * @return Two-dimensional array copy of matrix elements.
    */
   public int[][] getArrayCopy() {
      int[][] C = new int[m][n];
      for (int i = 0; i < m; i++) {
          System.arraycopy(A[i], 0, C[i], 0, n);
      }
      return C;
   }

   
   /**
    * Make a one-dimensional column packed copy of the internal array.
    * @return Matrix elements packed in a one-dimensional array by columns.
    */
   public int[] getColumnPackedCopy() {
      int[] vals = new int[m*n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            vals[i+j*m] = A[i][j];
         }
      }
      return vals;
   }

   
   /**
    * Make a one-dimensional row packed copy of the internal array.
    * @return Matrix elements packed in a one-dimensional array by rows.
    */
   public int[] getRowPackedCopy() {
      int[] vals = new int[m*n];
      for (int i = 0; i < m; i++) {
          System.arraycopy(A[i], 0, vals, i * n + 0, n);
      }
      return vals;
   }

   
   /**
    * Get row dimension.
    * @return The number of rows.
    */
   public int getRowDimension() {
      return m;
   }

   
   /**
    * Get column dimension.
    * @return The number of columns.
    */
   public int getColumnDimension() {
      return n;
   }

   
   /**
    * Find the first non-zero row of a matrix.
    * @return Row index (starting from 0 for 1st row) of the first row from top 
    *         that is not only zeros, -1 of there is no such row.
    */
   public int firstNonZeroRowIndex() {
      // int m = getRowDimension();  // 直接使用本类的m,n即可
      // int n = getColumnDimension();
      int h = -1;
      
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            if (get(i, j) != 0) {
               return i;
            }
         }
      }
      return h;
   }

   
   /**
    * Form a matrix with columns the row indices of non-zero elements.
    * @return The matrix with columns the row indices of non-zero elements. 
    *         First row has index 1.
    */
   public Matrix nonZeroIndices() {
      Matrix x = new Matrix(m, n);
      
      for (int i = 0; i < m; i++){
         for (int j = 0; j < n; j++){
            if (get(i, j) == 0 ) { 
               x.set(i, j, 0);
            } else {
               x.set(i, j, i+1);
            }
         }
      }
      return x;
   }

   
   /**
    * Find a column with non-minimal support.
    * @return The column index that has non-minimal support, -1 if there is none.
    */
   public int findNonMinimal() {
      int k = -1; // the non-minimal support column index
      // int m = getRowDimension(), n = getColumnDimension();  // 直接使用本类的m,n即可
      
      // x,y,z不用初始化，仅说明即可，例如：Matrix x; 因为以下循环语句中会给它赋值.
      Matrix x = new Matrix(m, 1); // column one, represents first col of comparison
      Matrix y = new Matrix(m, 1); // col two, represents rest columns of comparison
      Matrix z = new Matrix(m, 1); // difference column 1 - column 2
      
      for (int i = 0; i < n ; i++){
         x = getMatrix(0, m-1, i, i);
         for (int j = 0; j < n; j++){
            if (i != j){
               y = getMatrix(0, m-1, j, j);
               z = x.minus(y);
               // if there is at least one -ve element then break inner loop
               // and try another Y vector (because X is minimal with respect to Y)
               // if there is no -ve element then return from the function with index of X
               // to be eliminated, because X is not minimal with respect to Y
               if (!(z.hasNegativeElements())) {
                  return i;
               }
            }
         }
      }
      
      return k;
      // compare each columns' set with the other columns
      
      // if you find during comparison with another another column that it has
      // one more element, stop the comparison (the current col cannot be eliminated
      // based on this comparison) and go to the next comparison
      
      // if you find that the col in question has all the elements of another one
      // then eliminate the col in question
   }

   
   /**
    * Find if a column vector has negative elements.
    * @return True or false.
    */
   boolean hasNegativeElements() {
      boolean hasNegative = false;
      // int m = getRowDimension(); // 直接使用本类的m,n即可
      
      for (int i = 0; i < m ; i++){
         if (get(i, 0) < 0) {
            return true;
         }
      }      
      return hasNegative;
   }

   
   /**
    * Find the column index of the first non zero element of row h.
    * @param h The row to look for the non-zero element in
    * @return Column index (starting from 0 for 1st column) of the first 
    *         non-zero element of row h, -1 if there is no such column.
    */
   public int firstNonZeroElementIndex(int h) {
      // int n = getColumnDimension();  // 直接使用本类的m,n即可
      int k = -1;
      
      for (int j = 0; j < n; j++) {
         if (get(h, j) != 0) {
            return j;
         }
      }
      return k;
   }

   /**
    * Find the column indices of all but the first non zero elements of row h.
    * @param h The row to look for the non-zero element in
    * @return Array of ints of column indices (starting from 0 for 1st column) 
    *         of all but the first non-zero elements of row h.
    */
   public int[] findRemainingNZIndices(int h) {
      // int n = getColumnDimension();  // 直接使用本类的m,n即可
      int[] k = new int[n];
      int count = 0; // increases as we add new indices in the array of ints
      
      for (int j = 1; j < n; j++) {
         if (get(h, j) != 0)
            k[count++] = j;
      }
      return k;
   }

   
   /**
    * Find the coefficients corresponding to column indices of all but the first 
    * non zero elements of row h.
    * @param h The row to look for the non-zero coefficients in
    * @return Array of ints of coefficients of all but the first non-zero 
    *         elements of row h.
    */
   public int[] findRemainingNZCoef(int h) {
      // int n = getColumnDimension();  // 直接使用本类的m,n即可
      int[] k = new int[n];
      int count = 0; // increases as we add new indices in the array of ints
      int anElement; // an element of the matrix
      
      for (int j = 1; j < n; j++) {
         if ((anElement = get(h, j)) != 0) {
            k[count++] = anElement;
         }
      }
      return k;
   }

   
   /**
    * Get a single element.
    * @param i Row index.
    * @param j Column index.
    * @return A(i,j)
    * @exception ArrayIndexOutOfBoundsException
    */
   public int get(int i, int j) {
      return A[i][j];
   }

   
   /** Get a submatrix.
    * @param i0 Initial row index
    * @param i1 Final row index
    * @param j0 Initial column index
    * @param j1 Final column index
    * @return A(i0:i1,j0:j1)
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public Matrix getMatrix(int i0, int i1, int j0, int j1) {
      Matrix x = new Matrix(i1-i0+1,j1-j0+1);
      int[][] B = x.getArray();
      try {
         for (int i = i0; i <= i1; i++) {
             System.arraycopy(A[i], j0, B[i - i0], j0 - j0, j1 + 1 - j0);
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return x;
   }

   
   /**
    * Get a submatrix.
    * @param r Array of row indices.
    * @param c Array of column indices.
    * @return A(r(:),c(:))
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public Matrix getMatrix(int[] r, int[] c) {
      Matrix x = new Matrix(r.length,c.length);
      int[][] B = x.getArray();
      
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
               B[i][j] = A[r[i]][c[j]];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return x;
   }

   
   /**
    * Get a submatrix.
    * @param i0 Initial row index
    * @param i1 Final row index
    * @param c Array of column indices.
    * @return A(i0:i1,c(:))
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   Matrix getMatrix(int i0, int i1, int[] c) {
      Matrix x = new Matrix(i1-i0+1,c.length);
      int[][] B = x.getArray();
      
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = 0; j < c.length; j++) {
               B[i-i0][j] = A[i][c[j]];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return x;
   }

   
   /**
    * Get a submatrix.
    * @param r Array of row indices.
    * @param j0 Initial column index
    * @param j1 Final column index
    * @return A(r(:),j0:j1)
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public Matrix getMatrix(int[] r, int j0, int j1) {
      Matrix x = new Matrix(r.length,j1-j0+1);
      int[][] B = x.getArray();
      
      try {
         for (int i = 0; i < r.length; i++) {
             System.arraycopy(A[r[i]], j0, B[i], j0 - j0, j1 + 1 - j0);
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return x;
   }

   
   /**
    * For row rowNo of the matrix received return the column indices of all the
    * negative elements
    * @param rowNo row iside the Matrix to check for -ve elements
    * @return Integer array of indices of negative elements.
    *         非零元素，代表+ve的index+1  (index=0~(n-1)，n is the number of columns)
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public int[] getNegativeIndices(int rowNo) {
     // int n = getColumnDimension(); // find the number of columns  // 直接使用本类的m,n即可
      
      // create the single row submatrix for the required row
      try {
         Matrix a = new Matrix(1, n);
         a = getMatrix(rowNo, rowNo, 0, n-1);
         
         int count = 0; // index of a negative element in the returned array
         int[] negativesArray = new int[n];
         for (int i = 0; i < n; i++) // initialise to zero
            negativesArray[i] = 0;
         
         for (int i = 0; i < n; i++) {
            if (a.get(0, i) < 0)
               negativesArray[count++] = i + 1;
         }
         
         return negativesArray;
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   
   /**
    * For row rowNo of the matrix received return the column indices of all the 
    * positive elements
    * @param rowNo row inside the Matrix to check for +ve elements
    * @return The integer array of indices of all positive elements.
    *         非零元素，代表+ve的index+1  (index=0~(n-1)，n is the number of columns)
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public int[] getPositiveIndices(int rowNo) {
     // int n = getColumnDimension(); // find the number of columns  // 直接使用本类的m,n即可
      
      // create the single row submatrix for the required row
      try {
         Matrix a = new Matrix(1, n);
         a = getMatrix(rowNo, rowNo, 0, n-1);
         
         int count = 0; // index of a positive element in the returned array
         int[] positivesArray = new int[n];
         for (int i = 0; i < n; i++) // initialise to zero
            positivesArray[i] = 0;
         
         for (int i = 0; i < n; i++) {
            if (a.get(0, i) > 0)
               positivesArray[count++] = i + 1;
         }
         
         return positivesArray;
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   
   /**
    * Check if a matrix is all zeros.
    * @return true if all zeros, false otherwise
    */
   public boolean isZeroMatrix() {
     // int m = getRowDimension();   // 直接使用本类的m,n即可
     // int n = getColumnDimension(); // 直接使用本类的m,n即可

      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            if (get(i, j) != 0) {
               return false;
            }
         }
      }
      return true;
   }

   
   /**
    * isZeroRow returns true if the ith row is all zeros
    * @param r row to check for full zeros.
    * @return true if the row is full of zeros.
    */
   boolean isZeroRow(int r) {
      // TODO: optimize this!
      Matrix a = new Matrix(1, getColumnDimension());
      a = getMatrix(r, r, 0, getColumnDimension()-1);
      return a.isZeroMatrix();
   }

   
   /**
    * Find if a matrix of invariants is covered.
    * @return  true if it is covered, false otherwise.
    */
   public boolean isCovered() {

      // if there is an all-zeros row then it is not covered
      for (int i = 0; i < m; i++){
         if (isZeroRow(i) || this.transpose().hasNegativeElements()) {
            return false;
         }
      }
      return true;
   }

   
   /**
    * Add a linear combination of column k to columns in array j[].
    * @param k Column index to add.
    * @param chk Coefficient of col to add
    * @param j Array of column indices to add to.
    * @param jC Array of coefficients of column indices to add to.
    * @exception ArrayIndexOutOfBoundsException
    */
   public void linearlyCombine(int k, int chk, int[] j, int[] jC){
      // k is column index of coefficient of col to add
      // chj is coefficient of col to add
      int chj = 0; // coefficient of column to add to
      // int m = getRowDimension(); // 直接使用本类的m,n即可
      
      for (int i = 0; i < j.length; i++){  // 列
         if (j[i] != 0){  // 非0元素，才改变
            chj = jC[i];
            // System.out.print("\nchk = " + chk + "\n");
            for (int w = 0; w < m; w++) { // 行
               set(w, j[i]-1, chj*get(w, k)+chk*get(w, j[i]-1));
            }
         }
      }
   }

   
   /**
    * Add a linear combination of column k to columns in array j[].
    * @param k Column index to add.
    * @param alpha Array of coefficients of col to add
    * @param j Array of column indices to add to.
    * @param beta Array of coefficients of column indices to add to.
    * @exception ArrayIndexOutOfBoundsException
    */
   public void linearlyCombine(int k, int[] alpha, int[] j, int[] beta){
      // k is column index of coefficient of col to add
      // a is array of coefficients of col to add
      //int chk = 0; // coefficient of column to add to
      // int m = getRowDimension(); // 直接使用本类的m,n即可
      int n = j.length;
      
      for (int i = 0; i < n; i++){
         if (j[i] != 0){
            //chk = jC[i];
            // System.out.print("\nchk = " + chk + "\n");
            for (int w = 0; w < m; w++) { // for all the elements in a column
               set(w, j[i], alpha[i]*get(w, k)+beta[i]*get(w, j[i]));
            }
         }
      }
   }

   
   /**
    * Find the first row with a negative element in a matrix.
    * @return     Row index (starting from 0 for 1st row) of the first row from top that is has a negative element, -1 of there is no such row.
    */
   public int rowWithNegativeElement() {
      // int m = getRowDimension();   // 直接使用本类的m,n即可
      // int n = getColumnDimension(); // 直接使用本类的m,n即可
      int h = -1;
      
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            if (get(i, j) < 0)
               return i;
         }
      }
      return h;
   }

   
   /**
    * Set a single element.
    * @param i    Row index.
    * @param j    Column index.
    * @param s    A(i,j).
    * @exception  ArrayIndexOutOfBoundsException
    */
   public void set(int i, int j, int s) {
      A[i][j] = s;
   }

   
   /**
    * Set a submatrix.
    * @param i0   Initial row index
    * @param i1   Final row index
    * @param j0   Initial column index
    * @param j1   Final column index
    * @param x    A(i0:i1,j0:j1)
    * @exception  ArrayIndexOutOfBoundsException Submatrix indices
    */
   void setMatrix(int i0, int i1, int j0, int j1, Matrix x) {
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
               A[i][j] = x.get(i-i0,j-j0);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   
   /**
    * Set a submatrix.
    * @param r    Array of row indices.
    * @param c    Array of column indices.
    * @param x    A(r(:),c(:))
    * @exception  ArrayIndexOutOfBoundsException Submatrix indices
    */
   public void setMatrix(int[] r, int[] c, Matrix x) {
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
               A[r[i]][c[j]] = x.get(i,j);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   
   /**
    * Set a submatrix.
    * @param r    Array of row indices.
    * @param j0   Initial column index
    * @param j1   Final column index
    * @param x    A(r(:),j0:j1)
    * @exception  ArrayIndexOutOfBoundsException Submatrix indices
    */
   public void setMatrix(int[] r, int j0, int j1, Matrix x) {
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = j0; j <= j1; j++) {
               A[r[i]][j] = x.get(i,j-j0);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   
   /**
    * Set a submatrix.
    * @param i0 Initial row index
    * @param i1 Final row index
    * @param c Array of column indices.
    * @param x A(i0:i1,c(:))
    * @exception ArrayIndexOutOfBoundsException Submatrix indices
    */
   public void setMatrix(int i0, int i1, int[] c, Matrix x) {
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = 0; j < c.length; j++) {
               A[i][c[j]] = x.get(i-i0,j);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   
   /**
    * Matrix transpose.
    * @return    A'
    */
   public Matrix transpose() {
      Matrix x = new Matrix(n,m);
      int[][] C = x.getArray();
      
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[j][i] = A[i][j];
         }
      }
      return x;
   }

   
   /**
    * Find the greatest common divisor of a column matrix (vector) of integers.
    * @return     The gcd of the column matrix.
    */
   public int gcd() {
      int gcd = A[0][0];
      
      for (int i = 1; i < m; i++){
         if ((A[i][0] != 0) || (gcd != 0)){
            gcd = gcd2(gcd, A[i][0]);
         }
      }
      return gcd; // this should never be zero
   }

   
   /**
    * Find the greatest common divisor of 2 integers.
    * @param a    The first integer.
    * @param b    The second integer.
    * @return     The gcd of a,b, gcd > 0, a=0 && b=0,return 0;
    */
   public int gcd2(int a, int b) {
      int gcd;
      a = Math.abs(a);
      b = Math.abs(b);
      
      // ensure b > a
      if (b <= a){
         int tmp = b;
         b = a;
         a = tmp;
      }
      
      if ( a!=0 ) {
         for ( int tmp ; (b %= a) != 0; ) {
            tmp = b;
            b = a;
            a = tmp;
         }
         gcd = a;
      } else if (b != 0) {
         gcd = b;
      } else {
         // both args == 0, return 0, but this shouldn't happen
         gcd = 0;
      }
      return gcd;
   }

   
   /** uminus()
    * Unary minus
    * @return - A
    */
   public Matrix uminus() {
      Matrix x = new Matrix(m,n);
      int[][] C = x.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = -A[i][j];
         }
      }
      return x;
   }

   
   /**
    * C = A + B
    * @param b another matrix
    * @return A + B
    */
   public Matrix plus(Matrix b) {
      checkMatrixDimensions(b);
      Matrix x = new Matrix(m,n);
      int[][] C = x.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j] + b.A[i][j];
         }
      }
      return x;
   }

   
   /**
    * A = A + B
    *
    * @param b another matrix
    * @return A + B
    */
   public void plusEquals(Matrix b) {
      checkMatrixDimensions(b);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j] + b.A[i][j];
         }
      }
   }

   
   /**
    * C = A - B
    * @param b another matrix
    * @return A - B
    */
   public Matrix minus(Matrix b) {
      checkMatrixDimensions(b);
      int[][] C = new int[m][n]; //= X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j] - b.A[i][j];
         }
      }
       return new Matrix(C);
   }

   
   /**
    * A = A - B
    * @param b another matrix
    * @return A - B
    */
   public Matrix minusEquals(Matrix b) {
      checkMatrixDimensions(b);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j] - b.A[i][j];
         }
      }
      return this;
   }

   
   /**
    * Multiply a matrix by an int in place, A = s*A
    *
    * @param s int multiplier
    * @return replace A by s*A
    */
   public void timesEquals(int s) {
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = s*A[i][j];
         }
      }
   }

   
   /**
    * Multiply a row matrix by a column matrix, A = s*A
    * @param b column vector
    * @return product of row vector A by column vector B
    */
   public int vectorTimes(Matrix b) {
      int product = 0;
      
      for (int j = 0; j < n; j++) {
         product += A[0][j] * b.get(j, 0);
      }
      return product;
   }

   
   /**
    * Divide a matrix by an int in place, A = s*A
    *
    * @param s int divisor
    * @return replace A by A/s
    */
   public void divideEquals(int s) {
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j]/s;
         }
      }
   }

   
   /**
    * Generate identity matrix]
    * @param m Number of rows.
    * @param n Number of colums.
    * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
    */
   public static Matrix identity(int m, int n) {
      Matrix a = new Matrix(m,n);
      
      int[][] X = a.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            X[i][j] = (i == j ? 1 : 0);
         }
      }
      return a;
   }

   
   /**
    * Print the matrix to stdout.   Line the elements up in columns
    * with a Fortran-like 'Fw.d' style format.
    * @param w    Column width.
    * @param d    Number of digits after the decimal.
    */
   public void print(int w, int d) {
      print(new PrintWriter(System.out,true),w,d);
   }

   
   /**
    * Print the matrix to a string.   Line the elements up in columns
    * with a Fortran-like 'Fw.d' style format.
    * @param w    Column width.
    * @param d    Number of digits after the decimal.
    * @return     The formated string to output.
    */
   public String printString(int w, int d) {
      if (isZeroMatrix()) {
         return "\nNone\n\n";
      }
      ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
      
      print(new PrintWriter(arrayStream,true),w,d);
      
      return arrayStream.toString();
   }

   
   /**
    * Print the matrix to the output stream.   Line the elements up in
    * columns with a Fortran-like 'Fw.d' style format.
    * @param output Output stream.
    * @param w Column width.
    * @param d Number of digits after the decimal.
    */
   void print(PrintWriter output, int w, int d) {
      DecimalFormat format = new DecimalFormat();
      format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
      format.setMinimumIntegerDigits(1);
      format.setMaximumFractionDigits(d);
      format.setMinimumFractionDigits(d);
      format.setGroupingUsed(false);
      print(output,format,w+2);
   }

   
   /**
    * Print the matrix to stdout.  Line the elements up in columns.
    * Use the format object, and right justify within columns of width
    * characters.
    * Note that if the matrix is to be read back in, you probably will want
    * to use a NumberFormat that is set to UK Locale.
    * @param format A Formatting object for individual elements.
    * @param width Field width for each column.
    * @see java.text.DecimalFormat#setDecimalFormatSymbols
    */
   public void print(NumberFormat format, int width) {
      print(new PrintWriter(System.out,true),format,width);
   }

   
   // DecimalFormat is a little disappointing coming from Fortran or C's printf.
   // Since it doesn't pad on the left, the elements will come out different
   // widths.  Consequently, we'll pass the desired column width in as an
   // argument and do the extra padding ourselves.
   
   /**
    * Print the matrix to the output stream.  Line the elements up in columns.
    * Use the format object, and right justify within columns of width
    * characters.
    * Note that is the matrix is to be read back in, you probably will want
    * to use a NumberFormat that is set to US Locale.
    * @param output the output stream.
    * @param format A formatting object to format the matrix elements
    * @param width Column width.
    * @see java.text.DecimalFormat#setDecimalFormatSymbols
    */
   void print(PrintWriter output, NumberFormat format, int width) {
      output.println();  // start on new line.
      for (int i = 0; i < m; i++) {
    	 output.print("["+format.format(i)+"]"); // row index
         if (i<10) output.print(' '); 
         
         for (int j = 0; j < n; j++) {
            String s = format.format(A[i][j]); // format the number
            int padding = Math.max(1,width-s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++){
               output.print(' ');
            }               
            output.print(s);
         }
         output.println();
      }
      output.println();   // end with blank line.
   }

   
   /**
    * Throws IllegalArgumentException if dimensions of A and B differ.
    *  @param   b   The matrix to check the dimensions.
    */
   private void checkMatrixDimensions(Matrix b) {
/*      System.out.println("checkMatixDimensions: B.m=" + B.m + ", m=" + m + 
              "; B.n=" + B.n + ", n=" + n);*/
      if (b.m != m || b.n != n) {
         throw new IllegalArgumentException("Matrix dimensions must agree.");
      }
   }

   
   public void setToZero() {
      for(int i = 0 ; i < m ; i++){
         for(int j = 0 ; j < n ; j++){           
            A[i][j] = 0;
         }
      }
   }

   
   /** 
    * size()
    * @param i
    * @return     The size of the matrix.
    */
//   public int size() {
//      return A.length;
//   }
   
   
   public int[] getColumn(int i){
      int[] r = new int[this.getColumnDimension()];

       System.arraycopy(A[i], 0, r, 0, this.getColumnDimension());
      return r;
   }   
   
   
   public int[] getRow(int i){
      int[] r = new int[this.getRowDimension()];
      
      for (int j = 0; j < this.getRowDimension(); j++) {
         r[j] = A[j][i];         
      }
      return r;
   }      

   
   /**
    * @param place
    */
   public void clearColumn(int place) {
      for (int j = 0; j < this.getColumnDimension(); j++) {
         A[place][j] = 0;
      }
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
	public void print(String s) {
		System.out.print(s);
	}
   
   public static void main(String[] args) {
	   //int a[] = new int[6];
	   int a[] = {1,2,3,4,5,6};
	   Matrix b = new Matrix(a,2);  // a --> b
	   b.print(4, 1);  // 1.0   3.0   5.0
	                   // 2.0   4.0   6.0
	   
	   int c[][] = {{1,2,3},{4,5,6}};
	   Matrix cc = new Matrix(c);
	   cc.print(4,1);   //  1.0   2.0   3.0
	                    //  4.0   5.0   6.0
	   System.out.println("changed = " + cc.matrixChanged);
	  
	  // 在c2的右端补一列c1
	  int c1[][] = {{10},{30}};
	  Matrix c2 = new Matrix(c1);
	  Matrix c3 = cc.appendVector(c2);
	  c3.print(4, 1); //  1.0   2.0   3.0  10.0
	                  //  4.0   5.0   6.0  20.0
	  
	  
   }

}
