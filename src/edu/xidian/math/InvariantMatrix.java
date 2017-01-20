package edu.xidian.math;

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
	 * Add a linear combination of i-th row to k-th row for A[k][j]=0
	 * @param i  
	 * @param k  add i-th row to k-th row
	 *            
	 * @param j  Column index to be add, for new element A[k][j] = 0<br>
	 * new k row:         A[k][0]           A[k][1]          ....,A[k][j], ...<br>
	 * if (A[i][j]*A[k][j] <= 0)<br>    
	 *            c1*A[i][0]+c2*A[k][0], c1*A[i][1]+c2*A[k][1],...,  0,   ...<br>
	 * if (A[i][j]*A[k][j] > 0)<br>    
	 *            c1*A[i][0]-c2*A[k][0], c1*A[i][1]-c2*A[k][1],...,  0,   ...
	 */
	public void linearlyCombine(int i, int k, int j) {
		int iRowCoefficient, kRowCoefficient,gcd;
		iRowCoefficient = Math.abs(A[k][j]);
		kRowCoefficient = Math.abs(A[i][j]);
		gcd = gcd2(iRowCoefficient,kRowCoefficient);
		iRowCoefficient /= gcd;
	    kRowCoefficient /= gcd;
		//print("c=" + iRowCoefficient +","+kRowCoefficient + "\n");
		if (A[i][j]*A[k][j] <= 0) {  
			for(int col = 0; col < n; col++) {
				A[k][col] = iRowCoefficient*A[i][col] + kRowCoefficient*A[k][col];
			}
		}
		else {
			for(int col = 0; col < n; col++) {
				A[k][col] = iRowCoefficient*A[i][col] - kRowCoefficient*A[k][col];
			}
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
    public static Matrix invariants(Matrix C) {
    	C.print("Matrix C:");
        C.print(4, 0);
		return null;	
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
        
        Matrix invariantM = new Matrix(incidence);
        invariantM.print("incidence:");
        invariantM.print(4, 0);
        invariantM.print("incidence transpose:");
        invariantM.transpose().print(4, 0);
        
        // Compute P-Invariants
        //InvariantMatrix.invariants(invariantM);
        
        // Compute T-Invariants
        InvariantMatrix.invariants(invariantM.transpose());
        
        //InvariantMatrix m = new InvariantMatrix(invariantM.transpose());
        InvariantMatrix m = new InvariantMatrix(invariantM);
        int a[] = {-1,-2};
        int r = m.cardinalityOne(a);
        System.out.println("r="+r+","+a[0]+","+a[1]);
	}

}
