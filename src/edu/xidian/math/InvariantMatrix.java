package edu.xidian.math;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
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
			if (sign1 == 1) { a[0] = row1; a[1] = col1; return 0; } // 符合基数条件,仅有一个+ve或-ve
			if (sign2 == 1) { a[0] = row2; a[1] = col2; return 0; } // 符合基数条件,仅有一个+ve或-ve
		}
		return -1; // 不符合基数条件
	}
	
	/**
	 * 满足算法的基数条件。基数cardinality=1，表示存在列，满足条件：仅有一个+ve[和]一个-ve<br>
	 * 查找顺序：先列后行，从左到右，自上而下,先+ve,后-ve
	 * @param a 符合条件的行和列Index，+ve行Index：a[0], -ve行Index：a[1], 列Index：a[2]
	 * @return 找到符合条件的列，返回0; 否则，返回-1。
	 */
	public int cardinalityOne1(int a[]) {
		int sign1,sign2; // +ve,-ve number
		int row1,row2,col; // 唯一+ve和-ve的行号和列号
		// 查找顺序：先列后行，从左到右，自上而下
		for (int j = 0; j < n; j++) { // 列
			sign1 = 0; sign2 = 0;
			row1 = -1; row2 = -1; col = -1;
			for (int i = 0; i < m; i++) {  // 行
				if (A[i][j] == 0) continue;
				if (A[i][j] > 0) sign1++;
				else sign2++;
				if (sign1 == 1 && row1 == -1) { row1 = i; col = j; }
				if (sign2 == 1 && row2 == -1) { row2 = i; }
			}
			if (sign1 == 1 && sign2 == 1) { // 符合基数条件, 仅有一个+ve和一个-ve
				a[0] = row1; a[1] = row2; a[2] = col; 
				return 0;
			} 
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
				if (A[i][j] != 0) { // 非0元素
				//if (A[i][j] > 0) { // 正数
					a[0] = i; a[1] = j;
					return 0;
				}
			}
		}
		return -1; // 不符合条件
	}
	
	/**
	 * row(k) = row(i)*c1 + row(k)*c2,  c1*c2<0 <br>
	 * Add a linear combination of i-th row to k-th row for A[k][j]=0
	 * new k row:         A[k][0]           A[k][1]          ....,A[k][j], ...<br>  
	 *            c1*A[i][0]+c2*A[k][0], c1*A[i][1]+c2*A[k][1],...,  0,    ...<br>
	 * @param i  row index
	 * @param k  add i-th row to k-th row
	 *            
	 * @param j  Column index to be add, for new element A[k][j] = 0
	 * @return Coefficient[0],Coefficient[1] is adjusted coefficients for i-th and k-th rows
	 */
	public int[] linearlyCombine(int i, int k, int j) {
		int iRowCoefficient, kRowCoefficient,gcd;
		int Coefficient[] = new int[2];
		iRowCoefficient = Math.abs(A[k][j]);
		kRowCoefficient = Math.abs(A[i][j]);
		gcd = gcd2(iRowCoefficient,kRowCoefficient);
		iRowCoefficient /= gcd;
	    kRowCoefficient /= gcd;
	    if (A[i][j]*A[k][j] > 0) kRowCoefficient = -kRowCoefficient;
		for(int col = 0; col < n; col++) {
		   A[k][col] = iRowCoefficient*A[i][col] + kRowCoefficient*A[k][col];
		}
		Coefficient[0] = iRowCoefficient; Coefficient[1] = kRowCoefficient;
		//print("c=" + iRowCoefficient +","+kRowCoefficient + "\n");
		return Coefficient;
	}
	
	/**
	 * row(k) = row(i)*c1 + row(k)*c2,  c1=Coefficient[0], c2= Coefficient[1]<br>
	 * linear combination of i-th row to k-th row 
	 * 
	 * @param i row index
	 * @param k row index
	 * @param Coefficient Coefficient[0],Coefficient[1] is adjusted coefficients for i-th and k-th rows.
	 */
	public void linearlyCombine(int i, int k, int Coefficient[]) {
		for(int col = 0; col < n; col++) {
			A[k][col] = Coefficient[0]*A[i][col] + Coefficient[1]*A[k][col];
		}
	}
	
	/**
	 * logicalUnion of i-th row and k-th row, resulting is to k-th row 
	 * A矩阵是0和1组成的逻辑矩阵,0:false; 1:true
	 * @param i
	 * @param k
	 */
	public void logicalUnion(int i, int k) {
		boolean a,b,c;
		for(int col = 0; col < n; col++) {
			a = (A[i][col] != 0) ? true : false;
			b = (A[k][col] != 0) ? true : false;
			c = a || b;
			A[k][col] = c ? 1 : 0;
		}
	}
	
	/**
	 * logicalUnion of i-th row and k-th row, the resulting is append to new row 
	 * A矩阵是0和1组成的逻辑矩阵,0:false; 1:true
	 * @param i row index
	 * @param k row index
	 * @return matrix of appended row
	 */
	public InvariantMatrix AppendRowlogicalUnion(int i, int k) {
		boolean a,b,c;
		int row[] = new int[n];
		for(int col = 0; col < n; col++) {
			a = (A[i][col] != 0) ? true : false;
			b = (A[k][col] != 0) ? true : false;
			c = a || b;
			row[col] = c ? 1 : 0;
		}
		return appendRow(row);
	}
	
	/**
	 * append the row of linear combination of i-th row and k-th row 
	 * @param i  row index
	 * @param k  add i-th row to k-th row       
	 * @param j  Column index to be combination, j-th element of append row = 0
	 * @param Coefficient Coefficient[0],Coefficient[1] is adjusted coefficients for i-th and k-th rows
	 * @return matrix of appended row
	 */
	public InvariantMatrix AppendRowLinearlyCombine(int i, int k, int j, int Coefficient[]) {
		int iRowCoefficient, kRowCoefficient,gcd;
		
		iRowCoefficient = Math.abs(A[k][j]);
		kRowCoefficient = Math.abs(A[i][j]);
		gcd = gcd2(iRowCoefficient,kRowCoefficient);
		iRowCoefficient /= gcd;
	    kRowCoefficient /= gcd;
	    if (A[i][j]*A[k][j] > 0) kRowCoefficient = -kRowCoefficient;    
	    Coefficient[0] = iRowCoefficient; Coefficient[1] = kRowCoefficient;
	    
	    // append row of liearlyCombine result
	    int row[] = new int[n];
		for(int col = 0; col < n; col++) {
		   row[col] = iRowCoefficient*A[i][col] + kRowCoefficient*A[k][col];
		}
		
		return appendRow(row);
	}
	
	/**
	 * append the row of linear combination of i-th row and k-th row 
	 * 
	 * @param i row index
	 * @param k row index
	 * @param Coefficient Coefficient[0],Coefficient[1] is adjusted coefficients for i-th and k-th rows.
	 * @return matrix of appended row
	 */
	public InvariantMatrix AppendRowLinearlyCombine(int i, int k, int Coefficient[]) {
		int row[] = new int[n];
		for(int col = 0; col < n; col++) {
			row[col] = Coefficient[0]*A[i][col] + Coefficient[1]*A[k][col];
		}
		return appendRow(row);
	}
	
	/**
	 * Eliminate a row from the matrix, the row index is toDelete
	 * 
	 * @param toDelete The column index to delete.
	 * @return The matrix with the required row deleted.
	 */
	public InvariantMatrix eliminateRow(int toDelete) {
		int ii = 0;
		InvariantMatrix reduced = new InvariantMatrix(m-1, n);
		for(int i = 0; i < m; i++) {
			if (i == toDelete ) continue;
			for(int j = 0; j < n; j++) {
				reduced.set(ii, j, A[i][j]);
			}
			ii++;
		}
		return reduced;
	}
	
	/**
	 * Eliminate rows from the matrix, the row indexes is toDelete
	 * 
	 * @param toDeletes The column indexes to delete.
	 * @return The matrix with the required rows deleted.
	 */
	public InvariantMatrix eliminateRows(ArrayList<Integer> toDeletes) {
		int ii = 0;
		InvariantMatrix reduced = new InvariantMatrix(m-toDeletes.size(), n);
		for(int i = 0; i < m; i++) {
			if (toDeletes.contains(i)) continue;
			for(int j = 0; j < n; j++) {
				reduced.set(ii, j, A[i][j]);
			}
			ii++;
		}
		return reduced;
	}
	
	/**
	 * Has negative element in the column.
	 * @param column index
	 * @return 负元素所在行index
	 *         无负元素，返回-1
	 */
	int hasNegativeElementCol(int col) {
		for(int i = 0; i < m; i++) {
			if (get(i, col) < 0) return i;
		}
		return -1;
	}
	
	/**
	 * 通过与正元素所在行的线性组合，置本列负元素为0
	 * @param col
	 */
	public void negativeToZero(int col) {
		int positive,negative;
		// 找本列的正元素的行，此行与负元素所在的行，线性组合，置负元素为0
		for(positive = 0; positive < m; positive++) {
			if (get(positive,col) > 0) break;
		}
		if (positive == m) return; // 没有正元素，不能通过线性组合置负元素为0
		while (true) {
			negative = hasNegativeElementCol(col);
			if (negative == -1) break;
			linearlyCombine(positive,negative,col);
		}
	}
	
	/**
	 * all elements of the row is negative or 0.
	 * @return 行index，否则返回-1.
	 */
	public int allNegativeOrZeroRow() {
		int cols;
		for (int i = 0; i < m; i++) {
			cols = 0;
			for(int j = 0; j < n; j++) {
			  if (A[i][j] <= 0) cols++; 
			}
			if (cols == n) return i;
		}
		return -1;
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
     * Find the greatest common divisor of a row.
     * @param row index of row
     * @return  gcd of the row,gcd > 0
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
     * Find the greatest common divisor of a column.
     * @param col index of column
     * @return  gcd of the column. gcd >0
     */
    public int gcdCol(int col) {
       int gcd = A[0][col];
       
       for (int i = 0; i < m; i++) {
    	   if ((A[i][col] != 0) || (gcd != 0)){
    		   gcd = gcd2(gcd, A[i][col]);
    	   }
       }
       return gcd; // this should never be zero
    }
    
    /**
     * Append a row vector to the bottom of this matrix.
     * @param row vector for append.
     * @return The matrix with the row vector appended to the bottom.
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    public InvariantMatrix appendRow(int row[]) {
       InvariantMatrix r = new InvariantMatrix(m+1, n);  // 扩充一行
       // the extended matrix
       r.setMatrix(0, m-1, 0, n-1, this);
       
       try {         
          for (int j = 0; j < n; j++){
             r.A[m][j] = row[j];
          }
       } catch(ArrayIndexOutOfBoundsException e) {
          throw new ArrayIndexOutOfBoundsException("Row indices incompatible");
       }
       return r;
    }
    
    /** 
     * 计算特定列中的所有+ve索引的列表
     * @param columnIndex 列index
     * @return 参数表示的列中所有+ve索引的列表
     */
    public ArrayList<Integer> getPositiveList(int columnIndex) {
    	ArrayList<Integer> positive = new ArrayList<Integer>();
    	for(int i = 0; i < m; i++) {
    		if(A[i][columnIndex] > 0) positive.add(i);	
    	}
    	return positive;
    }
    
    /** 
     * 计算特定列中的所有-ve索引的列表
     * @param columnIndex 列index
     * @return 参数表示的列中所有-ve索引的列表
     */
    public ArrayList<Integer> getNegativeList(int columnIndex) {
    	ArrayList<Integer> negative = new ArrayList<Integer>();
    	for(int i = 0; i < m; i++) {
    		if(A[i][columnIndex] < 0) negative.add(i);	
    	}
    	return negative;
    }
    
    // TODO: modify
    /**
     * Find a row with non-minimal support.
     * @return The row index that has non-minimal support, -1 if there is none.
     */
    public int findNonMinimal() {
       int k = -1; // the non-minimal support column index
       
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
     * Find a row with non-minimal support.
     * i-th and k-th row comparison.(i=0,...m-2; k= i+1,...,m-1)
     * if (row i-th = row k-th) return i or k
     * if (row i-th > row k-th) return i; 
     * if (row i-th < row k-th) return k
     * @return The row index of non-minimal row; no exist,return -1 
     */
    public int findNonMinimalRow() {
    	int i,k,j,positive=0,negative = 0,zero = 0;
    	for (i = 0; i < m-1; i++) {
    		for (k = i+1; k < m; k++) {
    		  positive = 0; negative = 0; zero = 0;
    		  for (j = 0; j < n; j++) {
    			 if (A[i][j] - A[k][j] == 0) zero++; 
    			 else {
    				 if (A[i][j] - A[k][j] < 0) negative++; 
    				 else positive++;
    			 }
    		  }
    		  if (zero == n)  return i;            // row i-th = row k-th
    		  if (positive + zero  == n) return i; // row i-th > row k-th
    		  if (negative + zero == n)  return k; // row i-th < row k-th
    	    }
    	}
    	return -1;
    }
    
    /**
     * 是否是线性相关的行，即每个非0元素，均在矩阵A的相应列中存在
     * @param row 待检查的行向量
     * @return true: 是线性相关行，false： 不是线性相关行
     */
    public boolean isDependenceRow(int row[]) {
    	int noZeroCols = 0,ckNoZeroCols = 0; // 非0列数,
    	
    	// 每个非0元素，均在矩阵A的相应列中存在，则为非极小行向量，即该行是线性相关行
    	for (int j = 0; j < n; j++) {
    		if (row[j] == 0) continue;
    		noZeroCols++;
    		for (int i = 0; i < m; i++) {
    			if (A[i][j] != 0) {
    				ckNoZeroCols++;
    				break;
    			}
    		}
    	}
    	if (ckNoZeroCols == noZeroCols) return true;  
    	return false;
    }
    

    
    /** 
     * 计算特定列中的所有+ve/-ve索引的列表
     * @param columnIndex 列index
     * @return 参数表示的列中所有+ve和-ve索引的列表
     * 调用举例：<br>
	 * ArrayList<Integer> positives = new ArrayList<Integer>();
	 * ArrayList<Integer> negatives = new ArrayList<Integer>();
	 * positiveNegativeList(positives, negatives, columnIndex);
     */
    public void positiveNegativeList(ArrayList<Integer> positives,ArrayList<Integer> negatives,int columnIndex) {
    	if (columnIndex < 0 || columnIndex > n) return; // error
    	for(int i = 0; i < m; i++) {
    		if(A[i][columnIndex] > 0) positives.add(i);
    		else if(A[i][columnIndex] < 0) negatives.add(i);	
    	}
    }
    
    /**
     * rank of this matrix,调用rank()
     * 此函数速度优于rankE()
     */
    public int rank(InvariantMatrix a) {
    	int c[][] = a.getArray();
    	return rank(c);
    }
    
    /**
     * rank of this matrix,调用rankE()
     * 此函数速度慢于rank()
     */
    public int rankE(InvariantMatrix a) {
    	int c[][] = a.getArray();
    	return rankE(c);
    }
    
    /**
     * rank of the matrix
     * a matrix is in row echelon form（行阶梯式） if
     * (1) all nonzero rows (rows with at least one nonzero element) are above any rows of all zeroes (all zero rows, if any, belong at the bottom of the matrix), and
     * (2) the leading coefficient (the first nonzero number from the left, also called the pivot) of a nonzero row is always strictly to the right of the leading coefficient of the row above it.
     * ref. https://en.wikipedia.org/wiki/Gaussian_elimination
     **
     * rankE()函数转换为如下行阶梯式求秩, 迭代次数k与正在处理的row或column同，与pivot相同
     * 1 * * * *  k=0  pivot=0;
     * 0 2 * * *  k=1  pivot=1;
     * 0 0 3 * *  k=2  pivot=2;
     * 而rank()函数转换为如下行阶梯式求秩, 迭代次数k与row同，k与pivot不同; rank()计算速度应该比rankE()快
     * 1 * * * * * *   row=0  pivot=0;
     * 0 0 2 * * * *   row=1  pivot=2;
     * 0 0 0 0 3 * *   row=2  pivot=4;
     */
    public int rank(int b[][]) {
    	int m,n,tmp;
    	int row,pivot;  // pivot所在的行和列
    	boolean pivotOk = false;
    	m = b.length;     // row dimension
    	n = b[0].length;  // column dimension
    	
    	// copy,函数内部对b的修改，不能影响到函数外
    	int[][] a = new int[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(b[i], 0, a[i], 0, n);
        }
    	
    	// 最大迭代次数为m,n中的小者,即下面k的最大取值
    	int maxCol;
    	if (m > n) maxCol = n;
    	else maxCol = m;
    	
    	// 变换为行阶梯式
    	/*
    	 * 1 * * * * * *   row=0  pivot = 0;
    	 * 0 0 2 * * * *   row=1  pivot = 2;
    	 * 0 0 0 0 3 * *   row=2  pivot = 4;
    	 */
    	row = 0; pivot = 0;  // start from a[0[0]
    	for (int k = 0; k < maxCol; k++) { // k表示对角线元素序号a[0][0] ==> k=0,a[1][1] ==> k=1,a[2][2] ==> k=2
    		// Find the k-th pivot,使pivot指向row行的最左端的非0元素
    		pivotOk = false;
    		//System.out.println("row,pivot="+row+","+pivot);
    		if (a[row][pivot] == 0) { // 找row行最左端的非0元素 ==> pivot
	        	for (int j = pivot; j < n; j++) {    // column
	        		for (int i = row; i < m; i++) {  // row
		        		if (a[i][j] != 0) { 
		        			if (i != row) { // Row swap: i == >row
		        				for (int c = 0; c < n; c++ ) {
		        					tmp = a[row][c]; a[row][c] = a[i][c]; a[i][c] = tmp;
		        				}
		        			}
		                    pivot = j; pivotOk = true; break; // 为了退出两层for循环，设置pivotOk标志变量
		        		}
	        		}
	        		if (pivotOk) break;
	        	}
	        	if (!pivotOk) {  // pivot无非0指向，即a[row][pivot] = 0, 说明本行及其以下全为0，不用计算非0行数了。 秩就是row
	        		return row;
	        	}
    		}
    		
    		if (a[row][pivot] != 0 && row == m) { // 不用计算非0行数了。 秩就是row
      		  return row;	
      		}
      		
    		  		
	    	// 变换使a[row+1,...][pivot] = 0
    		// a[i][pivot] ==> 0, i= row+1...,m
	    	// L(i)*k1 - L(row)*k2 ==> L(i)
	    	// a[i][:]*k1 - a[row][:]*k2 ==> a[i][:], k1 = a[row][pivot], k2 = a[i][pivot]
	    	for (int i = row + 1; i < m; i++) {
	    		tmp = a[i][pivot]; // first element of this row will be set 0
                if (tmp != 0) { // 如果0，不需线性组合了
		    		for (int j = pivot; j < n; j++) {
		    		   a[i][j] = a[i][j]*a[row][pivot] - a[row][j]*tmp;
		    		}
                }
	    	}
	    	row++;  // next row
	    	pivot++;
	    	if (row >= m) row = m - 1;
	    	if (pivot >= n) pivot = n -1;  // 迭代结束
	    	//printArray(a);
    	}
    	
    	// 计算不全为零的行数，秩
    	boolean zeroFlag = true; // 行全0
    	int noZeroRowNum = 0;
    	for (int i = 0; i < m; i++) {
    		zeroFlag = true; // 行全0
    		for (int j = 0; j < n; j++) {
    			if (a[i][j] != 0) { zeroFlag = false; break; }
    		}
    		if (!zeroFlag) noZeroRowNum++;
    	}
    	
    	return noZeroRowNum;
    }
    
    /**
     * rank of the matrix
     * a matrix is in row echelon form（行阶梯式） if
     * (1) all nonzero rows (rows with at least one nonzero element) are above any rows of all zeroes (all zero rows, if any, belong at the bottom of the matrix), and
     * (2) the leading coefficient (the first nonzero number from the left, also called the pivot) of a nonzero row is always strictly to the right of the leading coefficient of the row above it.
     * ref. https://en.wikipedia.org/wiki/Gaussian_elimination
     **
     * rankE()函数转换为如下行阶梯式求秩, 迭代次数k与正在处理的row或column同，与pivot相同
     * 1 * * * *  k=0  pivot=0;
     * 0 2 * * *  k=1  pivot=1;
     * 0 0 3 * *  k=2  pivot=2;
     * 而rank()函数转换为如下行阶梯式求秩, 迭代次数k与row同，k与pivot不同; rank()计算速度应该比rankE()快
     * 1 * * * * * *   row=0  pivot=0;
     * 0 0 2 * * * *   row=1  pivot=2;
     * 0 0 0 0 3 * *   row=2  pivot=4;
     */
    public int rankE(int b[][]) {
    	int m,n,tmp;
    	m = b.length;     // row dimension
    	n = b[0].length;  // column dimension
    	
    	// copy,函数内部对b的修改，不能影响到函数外
    	int[][] a = new int[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(b[i], 0, a[i], 0, n);
        }
    	
    	// 处理列数为m,n中的小者,即下面k的最大取值
    	int maxCol;
    	if (m > n) maxCol = n;
    	else maxCol = m;
    	
    	// 变换为行阶梯式,正在处理的列k，
    	for (int k = 0; k < maxCol; k++) { // k表示对角线元素序号a[0][0] ==> k=0,a[1][1] ==> k=1,a[2][2] ==> k=2
    		// Find the k-th pivot,变换a使a[k][k] != 0
    		pivot(a,m,n,k); 
    		
    		// 对于非0行，一定不等于0
	    	//assert a[k][k] != 0; // pivot must not be 0
    		if (a[k][k] == 0) { // 说明本行及其以下全为0，不用计算非0行数了。 秩就是k
    		   return k;
    		}
    		
    		if (a[k][k] != 0 && k == maxCol) { // 不用计算非0行数了。 秩就是k
        	   return k;	
        	}
    		
	    	// 变换（k+1）行以下的列k元素为0
    		// a[i][k] ==> 0, i= k+1...,m
	    	// L(i)*k1 - L(k)*k2 ==> L(i), k1 = a[k][k], k2 = a[i][k]
	    	for (int i = k + 1; i < m; i++) {
	    		tmp = a[i][k]; // first element of this row will be set 0 
	    		if (tmp != 0) { // 如果0，不需线性组合了
		    		for (int j = k; j < n; j++) {
		    		   a[i][j] = a[i][j]*a[k][k] - a[k][j]*tmp;
		    		}
	    		}
	    	}
	    	
	    	printArray(a);
    	}
    	
    	// 计算不全为零的行数，秩
    	boolean zeroFlag = true; // 行全0
    	int noZeroRowNum = 0;
    	for (int i = 0; i < m; i++) {
    		zeroFlag = true; // 行全0
    		for (int j = 0; j < n; j++) {
    			if (a[i][j] != 0) { zeroFlag = false; break; }
    		}
    		if (!zeroFlag) noZeroRowNum++;
    	}
    	
    	return noZeroRowNum;
    }
    
    /**
     * Find the k-th(annulCol-th) pivot
     * the leading coefficient (the first nonzero number from the left, also called the pivot)
     * 变换矩阵a，使a[annulCol][annulCol] != 0; 即pivot = annulCol
     * 对于非0行，pivot must not be 0
     * @param a
     * @param m  row dimension
     * @param n  column dimension
     * @param annulCol  processing column index 
     * @return true 矩阵变换已按要求变换好，false 未找到满足条件(a[annulCol][annulCol] != 0)的元素，即a[:][annulCol] = 0
     */
    private void pivot(int a[][],int m, int n,int annulCol) {
    	int tmp;
    	if (a[annulCol][annulCol] != 0) return;
    	
    	/*
    	 * 1 * * * *   第1行pivot: a[0][0]
    	 * 0 0 2 * *   第2行pivot: a[1][2] ==>列变换，第2、3列交换，调整到a[1][1]
    	 * 0 0 0 3 *   第3行pivot: a[2][3] ==>列变换，调整到a[2][2]
    	 * 
    	 */
    	// 行变换，在annulCol的同列中找非零元素，将其交换到annulCol行
    	// 保证：a[annulCol][annulCol] != 0
    	for (int i = annulCol; i < m; i++) {
    		if (a[i][annulCol] != 0) {
    			// Row i swap to row annulCol
    			for (int j = 0; j < n; j++ ) {
    			  tmp = a[annulCol][j]; a[annulCol][j] = a[i][j]; a[i][j] = tmp;
    			}
    			return;
    		}
    	}
    	
    	// 如果行变换没有找到pivot，尝试实行列变换，在annulCol的同行中找非零元素，将其交换到annulCol列
    	for (int j = 0; j < n; j++) {
    		if (a[annulCol][j] != 0) {
    			// column j swap to column annulCol
    			for (int i = 0; i < m; i++ ) {
    			  tmp = a[i][annulCol]; a[i][annulCol] = a[i][j]; a[i][j] = tmp;
    			}
    			return;
    		}
    	}
    	
    	// 对于0行，a[annulCol][annulCol] = 0
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
           output.print("["+format.format(i)+"]"); // row index
           if (i<10) output.print(' ');    
           
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
    
    /**
	 * used to display intermiadiate results for checking
	 *
	 * @param a
	 *            The array to print.
	 */
	public void printArray(int[][] a) {
		System.out.println();
		for (int i = 0; i < a.length; i++)  {
			for (int j = 0; j < a[i].length; j++) {
			  System.out.print(a[i][j] + "   "); 
			}
		    System.out.println();
	   }
	}

}
