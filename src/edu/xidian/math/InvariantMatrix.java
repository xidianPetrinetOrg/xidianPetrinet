package edu.xidian.math;

import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class InvariantMatrix extends Matrix {

	/**
	 * 
	 */
	private static final long serialVersionUID = 954754350690844234L;

	public InvariantMatrix(int[][] A) {
		super(A);
	}
	
	public InvariantMatrix(Matrix b) { 
		super(b);
	}
	
	/**
	 * Construct an m-by-n matrix of zeros.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */
	public InvariantMatrix(int m, int n) {
		super(m, n);
	}
	
	/**
	 * 满足算法的基数条件。基数cardinality=1，表示存在列，满足条件：仅有一个+ve或-ve<br>
	 * 查找顺序：先列后行，从左到右，自上而下,先+ve,后-ve
	 * @param a 符合条件的行和列Index，行Index：a[0]，列Index：a[1]
	 * @return 找到符合条件的列，返回0; 否则，返回-1。
	 */
	public int cardinalityOne(int a[]) {
		int sign1,sign2; // +ve,-ve number
		int row1,col1,row2,col2; // 唯一+ve,-ve的行号和列号
		// 查找顺序：先列后行，从左到右，自上而下
		for (int j = 0; j < n; j++) { // 列
			sign1 = 0; sign2 = 0;
			row1 = -1; col1 = -1; row2 = -1; col2 = -1;
			for (int i = 0; i < m; i++) {  // 行
				if (A[i][j] == 0) continue;
				if (A[i][j] > 0) sign1++;
				else sign2++;
				if (sign1 == 1 && row1 == -1) { row1 = i; col1 = j; }
				if (sign2 == 1 && row2 == -1) { row2 = i; col2 = j; }
			}
			if (sign1 == 1) { a[0] = row1; a[1] = col1; return 0; } // 符合基数条件
			if (sign2 == 1) { a[0] = row2; a[1] = col2; return 0; } // 符合基数条件
		}
		return -1; // 不符合基数条件
	}
	
	/**
	 * 查找第一个非0元素
	 * @param a 符合条件的行和列Index，行Index：a[0]，列Index：a[1]
	 * @return 找到非0元素，返回0; 否则，返回-1。
	 */
	public int firstNonZeroElementIndex(int a[]) {
		// 查找顺序：先列后行，从左到右，自上而下
		for (int j = 0; j < n; j++) { // 列
			for (int i = 0; i < m; i++) {  // 行
				//if (A[i][j] != 0) {
				if (A[i][j] > 0) {
					a[0] = i; a[1] = j;
					return 0;
				}
			}
		}
		return -1; // 不符合条件
	}
	
	/**
	 * Add a linear combination of i-th row to k-th row for A[k][j]=0
	 * new k row:         A[k][0]           A[k][1]          ....,A[k][j], ...<br>  
	 *            c1*A[i][0]+c2*A[k][0], c1*A[i][1]+c2*A[k][1],...,  0,    ...<br>
	 * @param i  
	 * @param k  add i-th row to k-th row
	 *            
	 * @param j  Column index to be add, for new element A[k][j] = 0
	 * @return a[0],a[1] is adjusted coefficients for i-th and k-th rows
	 */
	public int[] linearlyCombine(int i, int k, int j) {
		int iRowCoefficient, kRowCoefficient,gcd;
		int a[] = new int[2];
		iRowCoefficient = Math.abs(A[k][j]);
		kRowCoefficient = Math.abs(A[i][j]);
		gcd = gcd2(iRowCoefficient,kRowCoefficient);
		iRowCoefficient /= gcd;
	    kRowCoefficient /= gcd;
	    if (A[i][j]*A[k][j] > 0) kRowCoefficient = -kRowCoefficient;
		for(int col = 0; col < n; col++) {
		   A[k][col] = iRowCoefficient*A[i][col] + kRowCoefficient*A[k][col];
		}
		a[0] = iRowCoefficient; a[1] = kRowCoefficient;
		//print("c=" + iRowCoefficient +","+kRowCoefficient + "\n");
		return a;
	}
	
	/**
	 * linear combination of i-th row to k-th row 
	 * 
	 * @param i
	 * @param k
	 * @param a a[0],a[1] is adjusted coefficients for i-th and k-th rows.
	 */
	public void linearlyCombine(int i, int k, int a[]) {
		for(int col = 0; col < n; col++) {
			A[k][col] = a[0]*A[i][col] + a[1]*A[k][col];
		}
	}
	
	/**
	 * Eliminate a row from the matrix, the row index is toDelete
	 * 
	 * @param toDelete
	 *            The column number to delete.
	 * @return The matrix with the required row deleted.
	 */
	public InvariantMatrix eliminateRow(int toDelete) {
		int ii = 0;
		InvariantMatrix reduced = new InvariantMatrix(m-1, n);
		for(int i = 0; i < m; i++,ii++) {
			if (i == toDelete ) {
				i++;
				if (i == m) break;  // 最后一行是删除行
			}
			for(int j = 0; j < n; j++) {
				reduced.set(ii, j, A[i][j]);
			}
		}
		return reduced;
	}
	
	/**
	 * Has negative elements.
	 * @param a 负元素所在的行、列index,分别在a[0]和a[1]中
	 * @return True or false.
	 */
	boolean hasNegativeElements(int a[]) {
		for (int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				if (get(i, j) < 0) {
					a[0] = i; a[1] = j;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * all elements is negative or 0 the row.
	 * @return 行index，否则返回-1.
	 */
	int allNegativeOrZeroRow() {
		int cols;
		for (int i = 0; i < m; i++) {
			cols = 0;
			for(int j = 0; j < n; j++) {
			  if (get(i, j) <= 0) cols++; 
			}
			if (cols == n) return i;
		}
		return -1;
	}


	/**
     * Transform a matrix to obtain the minimal generating set of vectors.
     * Initialisation: C = A; D: m*m单位阵 , 
	 * the matrix A: m*n关联矩阵, 本函数求P-Invariants;  若是关联矩阵的转置，则本函数求T-Invariants
	 * m: place number; n: transition number  
	 * for (j=0;j<n;i++) // 遍历C各列，C非全零
	 * {  
	 *   Append to the matrix B = [C:D] every rows resulting from positive linear combinations of row pairs in B that annihilate column j of C.                                                                            
	 *   Eliminate from B the rows in which the j-th row of C is non-zero.
	 * }
	 * B is all zeros
	 * D denotes the invariance matrix.
     * @return A matrix containing the invariant vectors.
     */
	public static Matrix invariants(InvariantMatrix C) {
		int m = C.getRowDimension();  // C的行数

		// generate the mxm identity matrix
		InvariantMatrix B = InvariantMatrix.identity(m, m);
		
		B.print("Matrix C | B");
		print(C,B,4,0);

		int a[] = new int[2]; // a[0],a[1]唯一的+ve或-ve所在的行和列
		int Coefficient[]; // 调整系数
		
		// 符合算法的基数条件,仅有一个+ve或-ve的列, 线性组合到其它行，删除此行.
		while (C.cardinalityOne(a) == 0) {
			int element = C.get(a[0], a[1]); // 唯一的+ve或-ve
			// +ve所在的行，加到相应负元素所在的行; -ve所在的行，加到相应正元素所在的行
			for (int i = 0; i < m; i++) {
				if (C.get(i, a[1]) == 0)
					continue;
				if (a[0] == i)
					continue;
				Coefficient = C.linearlyCombine(a[0], i, a[1]);
				B.linearlyCombine(a[0], i, Coefficient);
			}
			C = C.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			B = B.eliminateRow(a[0]);
			m--; // 减掉一行
			C.print("唯一的+ve/-ve的行列，值：" + a[0] + "," + a[1] + "," + element);
			print(C,B,4,0);
		}
		
		// 第一个非0元素所在的行，线性组合到其它行，消除此行
		while (C.firstNonZeroElementIndex(a) == 0) {
			int element = C.get(a[0], a[1]); // 第一个非0元素
			// 线性组合
			for (int i = 0; i < m; i++) {
				if (C.get(i, a[1]) == 0)
					continue;
				if (a[0] == i)
					continue;
				Coefficient = C.linearlyCombine(a[0], i, a[1]);
				B.linearlyCombine(a[0], i, Coefficient);
			}
			C = C.eliminateRow(a[0]); // 第一个非0元素所在的行
			B = B.eliminateRow(a[0]);
			m--; // 减掉一行
			C.print("第一个非0元素的行列，值：" + a[0] + "," + a[1] + "," + element);
			print(C,B,4,0);
		}

		C.print("C应该是全0" );
		print(C,B,4,0);
		
		// 此时，C应该是全0,
		if (!C.isZeroMatrix()) {
			C.print("==========Error!!!==========\n");
			return null;
		}

		// 规范化B
		// 负元素或0组成的行，取正，即负行取正
		int b_cols = B.getColumnDimension();
		while (true) {
			int row = B.allNegativeOrZeroRow();
			if (row == -1)
				break;
			for (int j = 0; j < b_cols; j++) {
				if (B.get(row, j) < 0)
					B.set(row, j, Math.abs(B.get(row, j))); // 负元素取正
			}
		}
		B.print("matrix B,负行取正：");
		B.print(4, 0);

		// 负元素置0,有不能消除的情况,如：
		// 经典,A Simple and Fast Algorithm To Obain All Invariants Of A Generalised Petri Net
		// Figure 3
		int negative[] = new int[2];
		while (true) {
			boolean b = B.hasNegativeElements(negative);
			if (!b) break;
			for (int i = 0; i < m; i++) {
				if (B.get(i, negative[1]) > 0) {
					B.linearlyCombine(i, negative[0], negative[1]);
					break;
				}
			}
		}
		B.print("matrix B,负元素置0：");
		B.print(4, 0);

		// 各行除以该行的最大公约数
		for (int i = 0; i < m; i++) {
			int gcd = B.gcdRow(i);
			if (gcd > 1) {
				for (int j = 0; j < b_cols; j++) {
					B.set(i, j, B.get(i, j) / gcd);
				}
			}
		}
		B.print("matrix B,各行除以该行的最大公约数：");
		B.print(4, 0);
		return B;
	}
    
    /**
     * Matrix transpose.
     * @return    A'
     */
    public InvariantMatrix transpose() {
       InvariantMatrix x = new InvariantMatrix(n,m);
       int[][] C = x.getArray();
       
       for (int i = 0; i < m; i++) {
          for (int j = 0; j < n; j++) {
             C[j][i] = A[i][j];
          }
       }
       return x;
    }
    
    /**
     * Generate identity matrix]
     * @param m Number of rows.
     * @param n Number of colums.
     * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
     */
    public static InvariantMatrix identity(int m, int n) {
       InvariantMatrix a = new InvariantMatrix(m,n);
       
       int[][] X = a.getArray();
       for (int i = 0; i < m; i++) {
          for (int j = 0; j < n; j++) {
             X[i][j] = (i == j ? 1 : 0);
          }
       }
       return a;
    }
    
    /**
     * Find the greatest common divisor of a row matrix (vector) of integers.
     * @return     The gcd of the column matrix.
     */
    public int gcdRow(int row) {
       int gcd = A[row][0];
       
       for (int j = 0; j < n; j++) {
    	   if ((A[row][j] != 0) || (gcd != 0)){
    		   gcd = gcd2(gcd, A[row][j]);
    	   }
       }
       return gcd; // this should never be zero
    }
    
    /**
     * Print the tow matrix (C | B)to stdout.   
     * @param w    Column width.
     * @param d    Number of digits after the decimal.
     */
    public static void print(Matrix C, Matrix B, int w,int d) {
    	PrintWriter output = new PrintWriter(System.out,true);
    	output.println();  // start on new line.
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        
        int m1,m2,n1,n2;
        m1 = C.getRowDimension();
        m2 = B.getRowDimension();
        if (m1 != m2) {
        	output.println("The row number of two matrix doesnot match!! ");
        	return;
        }
        n1 = C.getColumnDimension();
        n2 = B.getColumnDimension();
        for (int i = 0; i < m1; i++) {
           // matrix C
           for (int j = 0; j < n1; j++) {
              String s = format.format(C.get(i, j)); // format the number
              int padding = Math.max(1,w-s.length()); // At _least_ 1 space
              for (int k = 0; k < padding; k++){
                 output.print(' ');
              }               
              output.print(s);
           }
           output.print(" | ");
           // matrix B
           for (int j = 0; j < n2; j++) {
               String s = format.format(B.get(i, j)); // format the number
               int padding = Math.max(1,w-s.length()); // At _least_ 1 space
               for (int k = 0; k < padding; k++){
                  output.print(' ');
               }               
               output.print(s);
            }
           
           output.println();
        }
        output.println();   // end with blank line.
     }
    
	public static void main(String[] args) {
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
        
        InvariantMatrix invariantM = new InvariantMatrix(incidence);
        // invariantM.print("incidence:");
        // invariantM.print(4, 0);
        // invariantM.print("incidence transpose:");
        // invariantM.transpose().print(4, 0);
        
        // Compute P-Invariants
        // InvariantMatrix.invariants(invariantM);
        
        // Compute T-Invariants
        InvariantMatrix.invariants(invariantM.transpose());
        /**
         t1        t2        t3        t4        t5       t6        t7        t8         t9
         0         0         0         1         1         0         0         0         0
        15        15        15         0        13         2        28        15        28
         */
        
	}

}
