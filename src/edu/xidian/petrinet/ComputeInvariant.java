package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.List;

import edu.xidian.math.InvariantMatrix;

/**
 * FM(Fourier-Motizkin) method, Computation of p-semiflows(p-invariants):
 * 
   C:    the PN incidence matrix(dimensions m*n, m=|P|,n=|T|)
   I:    identity matrix(dimensions m*m)
   
   1. A = C; Y = I;
   2. For each k, k=1,....,n in this order,execute the following pair of procedures (a) and (b)；
      (a) Insert into B = [A | Y] every new row constructed by a positive coefficient linear combination of any two rows of 
          the original B such that the k-th column element of the resulting vector is zero;
      (b) Delete from B any row whose k-th column element is nonzero,and let B denote the resulting matrix;
      
      Rows finally appeared in the submatrix corresponding Y of B form a class of P-invariants computed by the method.
       
         
   Improving FM:  
   (1) Heuristics for selecting the columns to annul: 
       Annul columns:  F(k)<0 or if there is none, select column with lowest pk*vk
       compute1(): selecting the columns with pk=1 and vk=1;
       compute2(): selecting the columns with pk=1 or vk=1;
       compute3(): F(k)<0 or if there is none, select column with lowest pk*vk
   (2) Deleting any candidate vector V such that |sup(V)| > rank(H)+1. it is non minimal support.
       check |sup(v)| in the compute() function or in the compute1(),compute2(),and compute3().
       the candidate vector V is the new row of Y in the step 2(a).  
     
   pk,vk:the number of positives and negatives in column k of A(k)
   expansion factor: F(k) = pk*vk-(pk+vk)
   The number of new rows to add, by combining pairs of rows, is pk*vk. 
   The pk+vk rows used to annul column k are eliminated at the end of the iteration. 
   The expansion factor is the net number of new rows added in annulling column k. 
   
   Reference：
   [1] MARTINEZ J., SILVA M. A Simple and Fast Algorithm to Obtain all Invariants of a Generalized Petri Net. 
       Second European Workshop on Application Theory of Petri Nets, Bad Honnef, September, pp. 411-422.
   [2] K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants,
       IEICE Trans. Fundamentals, vol.E84-A,no.11,pp.2871-2880,Nov.2001
   [3] M.Silva and J.M. Colom, "CONVEX GEOMETRY AND SEMIFLOWS IN P/T NETS. A COMPARATIVE STUDY OF ALGORITHMS FOR COMPUTATION OF MINIMAL P-SEMIFLOWS," 
       Advances in Petri Nets 1990,Lecture Notes in Computer Science 483,pp.79-112,Springer-Verlag,Berlin,Germany,1991.
   
 * @author Administrator
 *
 */
public class ComputeInvariant {
	/**
	 * Row Dimension of the PN(Petri Net) incidence matrix 
	 */
	private int incidenceRowDimension;
	/**
	 * Column Dimension of the PN(Petri Net) incidence matrix 
	 */
	private int incidenceColumnDimension;
	/**
	 * Initialization is the PN(Petri Net) incidence matrix 
	 */
	private InvariantMatrix A;
	/**
	 * Initialization is the identity matrix(incidenceRowDimension*incidenceRowDimension)
	 */
	private InvariantMatrix Y; 
	
    /**
     * 记录已经消去A矩阵的列index
     */
	private List<Integer> annuledColumns = new ArrayList<Integer>();
	
	/**
	 * 记录原始矩阵A(即未消去任何列的矩阵A),是noMinimalSupport()求非极小支撑的基础
	 */
	private int AA[][];
		
	/**
	 *  enable/disable print debug information
	 */
	private boolean Debug = true;
	
	/**
	 * 
	 * @param C the PN(Petri Net) incidence matrix for computing P-Invariants; <br>
	 *          the incidence matrix's transpose for computing T-Invariants;
	 */
    public ComputeInvariant(InvariantMatrix C) {
    	incidenceRowDimension = C.getRowDimension(); 
    	incidenceColumnDimension = C.getColumnDimension();
		A = C;
		Y = InvariantMatrix.identity(incidenceRowDimension, incidenceRowDimension);
		AA = A.getArrayCopy();
		
		this.Debug = true;
	}
    
    /**
     * Annul all columns k of U (A | Y) in which pk and vk = 1;
     */
	public void compute1() {
		println("Annul all columns k of U (A | Y) in which pk and vk = 1");
		int a[] = new int[3]; // a[0],a[1],a[2],唯一+ve行,唯一-ve行，列
		int Coefficient[]; // 调整系数
		
		// 符合算法的基数条件,仅有一个+ve和-ve的列, 线性组合+ve行到-ve行，删除+ve行.
		while (A.cardinalityOne1(a) == 0) {
			println("唯一的+ve和唯一的-ve：A[" + a[0] + "][" + a[2] + "],A[" + a[1] + "][" + a[2] + "]");
			println("Annul column [" + a[2] + "]");
			// +ve所在的行，加到相应-ve所在的行
			Coefficient = A.linearlyCombine(a[0], a[1], a[2]);
			Y.linearlyCombine(a[0], a[1], Coefficient);
			
			annuledColumns.add(a[2]); // record annul column
			
			// 如果新添加的候选向量不是极小的，删除之
			int no = noMinimalSupport(a[1], annuledColumns);
		    if (no != -1) {
		      assert(no == a[1]);
			  println("delete non minimal candidate vector:" + no);
			  A = A.eliminateRow(no); Y = Y.eliminateRow(no);
			}
			
			A = A.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			Y = Y.eliminateRow(a[0]);
			
			//println("唯一的+ve,唯一的-ve：[" + a[0] + "]row, add to [" + a[1] + "]row,at col[" + a[2] + "],eliminate row[" + a[0] + "]");
			printAY();
			
			//annuledColumns.add(a[2]); // record annul column
			System.out.println("Annuled columns: " + annuledColumns + "," + 
			              annuledColumns.size() + " of " + incidenceColumnDimension);
		}
    }
	
	/**
     * Annul all columns k of U (A | Y) in which pk or vk = 1;
     */
	public void compute2() {
		println("Annul all columns k of U (A | Y) in which pk or vk = 1");
		
		int a[] = new int[2]; // a[0],a[1]唯一的+ve或-ve所在的行和列
		int Coefficient[]; // 调整系数
		
		int m = A.getRowDimension(); // A的行数
		
		// 符合算法的基数条件,仅有一个+ve或-ve的列, 线性组合到其它行，删除此行.
		while (A.cardinalityOne(a) == 0) {
			// +ve所在的行，加到相应负元素所在的行; -ve所在的行，加到相应正元素所在的行
			println("唯一的+ve或-ve：A[" + a[0] + "][" + a[1] + "]");
			println("Annul column [" + a[1] + "]");
			
			annuledColumns.add(a[1]); // record annul column
			for (int i = 0; i < m; i++) {
				if (a[0] == i) continue;  // 不与本行(+ve或-ve所在的行)线性组合
				if (A.get(i, a[1]) == 0) continue; // 不与0元素所在的行线性组合
				Coefficient = A.linearlyCombine(a[0], i, a[1]);
				Y.linearlyCombine(a[0], i, Coefficient);
				//println("唯一的+ve或-ve：[" + a[0] + "]row, add to [" + i + "]row,at col[" + a[1] + "]");
				
				// 如果新添加的候选向量不是极小的，删除之
				int no = noMinimalSupport(i, annuledColumns);
			    if (no != -1) {
			      assert(no == i);
				  println("delete non minimal candidate vector:" + no);
				  A = A.eliminateRow(no); Y = Y.eliminateRow(no);
				  m--;
				}
			}
			A = A.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			Y = Y.eliminateRow(a[0]);
			m--; // 减掉一行
			
			printAY();
			
			//annuledColumns.add(a[1]); // record annul column
			System.out.println("Annuled columns: " + annuledColumns + "," + 
		              annuledColumns.size() + " of " + incidenceColumnDimension);
		}
    }
	
	/** 
	 * expansion factor: F(k) = pk*vk-(pk+vk)
	 * Annul columns:  F(k)<0 or if there is none, select column with lowest pk*vk
	 */
	public void compute3() {
		println("Annul columns: expansion factor F(k)<0 or if there is none, select column with lowest pk*vk");
		int Coefficient[] = new int[2]; // 调整系数
		ArrayList<Integer> positives = new ArrayList<Integer>();
		ArrayList<Integer> negatives = new ArrayList<Integer>();
		int annelCol = AnnelCol(positives, negatives);	
		
		println("Annul column [" + annelCol + "]，Append rows = "+positives.size()*negatives.size());
		if (annelCol == -1) return;
		
		annuledColumns.add(annelCol); // record annul column
		for (int positiveRow : positives ) {
			for (int negativeRow : negatives) {
				A = A.AppendRowLinearlyCombine(positiveRow, negativeRow, annelCol, Coefficient);
				Y = Y.AppendRowLinearlyCombine(positiveRow, negativeRow, Coefficient);
				//println("Append row is linearlyCombine of row["+positiveRow + "] and row[" +negativeRow+"]:");
				printAY();
				
				// 如果新添加的候选向量不是极小的，删除之
				int no = noMinimalSupport(Y.getRowDimension()-1, annuledColumns);
			    if (no != -1) {
			      assert(no == Y.getRowDimension()-1);
				  println("delete non minimal candidate vector:" + no);
				  A = A.eliminateRow(no); Y = Y.eliminateRow(no);
				}
			}
		}
		
		// 删除annelCol列的非0行
		positives.addAll(negatives);  // 并集
		A = A.eliminateRows(positives);
		Y = Y.eliminateRows(positives);
		
		//println("eliminate rows: " + positives);
		printAY();
		
		//annuledColumns.add(annelCol); // record annul column
		System.out.println("Annuled columns: " + annuledColumns + "," + 
	              annuledColumns.size() + " of " + incidenceColumnDimension);
	}
	
	public void compute() {
		printAY();
		while(!A.isZeroMatrix()) {
			// Annul all columns k of U (A | Y) in which pk and vk = 1;
			println("compute1()===");
			compute1();
			println("affter compute1()");
			printAY();
			
			// Annul all columns k of U (A | Y) in which pk or vk = 1;
			println("compute2()===");
			compute2();
			println("affter compute2()");
			printAY();
			
			// expansion factor: F(k) = pk*vk-(pk+vk)
			// Annul columns:  F(k)<0 or if there is none, select column with lowest pk*vk
			println("compute3()===");
			compute3();
			println("affter compute3()");
			printAY();
		}
		
		println("矩阵A成为全0矩阵, 矩阵Y的各行是所求不变式的候选向量.");
		//System.out.println("Annuled columns: " + annuledColumns + "," + 
	    //          annuledColumns.size() + " of " + incidenceColumnDimension);
		if ( annuledColumns.size() != incidenceColumnDimension) {
		   System.out.println("Annuled columns: " + annuledColumns);
		   int n = incidenceColumnDimension - annuledColumns.size();
		   System.out.println("算法需要消去A的列数：" + incidenceColumnDimension + ",其中主动消去列数：" + annuledColumns.size() + ",顺带消去列数：" + n);
		}
		
		////////////////////////////////////////////
		// 规范化矩阵Minimal Support matrix Y
		/** Y is called standardized iff
		(1) y1,...,yq 属于整数integers（0,1,2,3，...）
		(2) for the first yi不等于0，yi > 0 and
		(3) y cannot be divided by an element k属于正整数(positive integers), k > 1 without destroying (1)
		**/
		////////////////////////////////////////////
		System.out.println("规范化矩阵Minimal Support Matrix Y===");
		// 全部由负元素或0组成的行，取正，即负行取正
		while (true) {
			int row = Y.allNegativeOrZeroRow();
			if (row == -1) break;
			for (int j = 0; j < incidenceRowDimension; j++) {
				if (Y.get(row, j) < 0)
					Y.set(row, j, Math.abs(Y.get(row, j))); // 负元素取正
			}
		}
		println("全部由负元素或0组成的行，取正，即负行取正.");
		printY();
		
		// 通过与正元素行的线性组合，负元素置0
		for(int col = 0; col < incidenceRowDimension; col++) {
			Y.negativeToZero(col);
		}
		println("通过与正元素行的线性组合，负元素置0.");
		printY();

		// 各行除以该行的最大公约数
		for (int i = 0; i < Y.getRowDimension(); i++) {
			int gcd = Y.gcdRow(i);
			if (gcd > 1) {
				for (int j = 0; j < incidenceRowDimension; j++) {
					Y.set(i, j, Y.get(i, j) / gcd);
				}
			}
		}
		println("各行除以该行的最大公约数.");
		printY();
		
		// Delete the rows of non minimal support in matrix Y
		// 使用以下任意一种方法即可。
		System.out.println("Delete the rows of non minimal support in matrix Y===");
		// method 1:
		/**
		while(true) {
			//Find a row with non-minimal support.
			int row = Y.findNonMinimalRow();
			if (row == -1) break;
			println(row+"-th row of Y, is non minimal support, to be delete.");
			Y = Y.eliminateRow(row);
		}
		
		
		// method 2: 删除非极小候选向量，即 |sup(Y)| > rank(H)+1， #see#noMinimalSupport()函数
		// ref. K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants  
		int n = -1;
		for (int row = 0; row < Y.getRowDimension(); row++) {
			n = noMinimalSupport(row, annuledColumns);
			if (n != -1) {
				println(n+"-th row of Y, is non minimal support, to be delete.");
				Y = Y.eliminateRow(row);
				row--;
			}
		}
		**/
		
		// method 3: 
		// 同method 2， 将其应用于compute1(),compute2(),compute3(),在这些函数中调用noMinimalSupport()处理非极小候选向量
		
		// 最后打印出极小不变式
	    println("minimal support invariants:");
		Y.print(4,0);
	}
	
	/**
	 * 计算后，通过此函数获取计算结果(所求不变式)
	 * @return
	 */
	public InvariantMatrix getInvariants() {
		return Y;
	}
	
	/**
	 * 选取待消除的列的启发式策略，Heuristics for selecting the columns to annul
	 * The number of new rows to add, by combining pairs of rows, is pk*vk. 
	 * The pk+vk rows used to annul column k are eliminated at the end of the iteration. 
	 * The expansion factor is the net number of new rows added in annulling column k: 
	 * F(k) = pk*vk-(pk+vk)
	 * 生成新行数：pk*vk; 线性组合后，删除的行数：pk+vk
	 * @param positives 返回选定列中正元素(+ve)index列表
	 * @param negatives 返回选定列中负元素(-ve)index列表
	 * @return 选定列的Index，如果没有符合条件的列，返回-1
	 * 因此,有如下策略，选取待删除的列：<br>
	 * （1）不产生新行的情况，即f(k) < 0, 返回将要删除的列index; <br>
	 * （2）如果（1）不成立，选择pk*vk最小的(pk或vk至少有1个大于0，即至少有一个正或负元素),即生成的新行数最少,返回将要删除的列index; <br>
	 * <br>
	 * 调用举例：<br>
	 * ArrayList<Integer> positives = new ArrayList<Integer>();
	 * ArrayList<Integer> negatives = new ArrayList<Integer>();
	 * int col = AnnelCol(positives, negatives);
	 */
	public int AnnelCol(ArrayList<Integer> positives, ArrayList<Integer> negatives) {
		int pk,vk; // the number of positives and negatives in column k of A;
		int fk; // expansion factor,F(k) = pk*vk-(pk+vk)
		int newRowsNum[] = new int[incidenceColumnDimension]; // pk*vk
		for(int j = 0;j < incidenceColumnDimension;j++) {
		  positives.clear();negatives.clear();  // 一定要清零，初始化
		  A.positiveNegativeList(positives, negatives, j);
		  pk = positives.size();
		  vk = negatives.size();
		  fk = pk*vk-(pk+vk);
		  if (fk < 0) return j;
		  newRowsNum[j] = pk*vk;
		}
		
		// 生成的新行数最少
		int minValue = -1;
		int col = 0, minCol = -1;
		for (int value : newRowsNum) {
			// 初始化，要保证pk*vk>0
			if (minValue == -1) {
				if (value != 0) { minValue = value; minCol = col; }
				col++;
				continue;
			}
			
			if (value != 0 && value < minValue ) {
				minValue = value; 
				minCol = col;
			}
			col++;
		}
		
		// 获取该行的+ve和-ve index list
		positives.clear();negatives.clear();  // 一定要清零，初始化
		if (minCol >= 0) A.positiveNegativeList(positives, negatives, minCol);
		
		return minCol;
	}
	
	/**
	 * 非极小支撑
	 * The row index of non minimal support in matrix Y
	 * non minimal support: any candidate vector such that |sup(Y)| > rank(H)+1
	 * @param row index of row for matrix Y
	 * @param annuledColumns  已经消去的矩阵A的列
	 * @return the row index of non minimal support in matrix Y, 
	 *         if not, return -1;
	 * ref. K. Takano., Experimental Evaluation of Two Algorithms for Computing Petri Net Invariants  
	 */
	public int noMinimalSupport(int row, List<Integer> annuledColumns) {
		List<Integer> noZeroes = new ArrayList<Integer>(); // Y矩阵第row行，非0元素，构成|sup（Y）|
		
		// sup(Y)
		for (int j = 0; j < incidenceRowDimension; j++) { // Y的行数和列数相同
			if (Y.get(row, j) != 0) noZeroes.add(j);
		}
		
		// construct H, 来自AA的子矩阵： 行：noZeroes; 列：annuledColumns
		int m = noZeroes.size();  // row
	    int n = annuledColumns.size(); // column
	    int r = 0, c = 0; // row,column index
		int H[][] = new int[m][n];
		for (Integer i : noZeroes) {
			c = 0;
			for (Integer j : annuledColumns) {
			  H[r][c] = AA[i][j]; c++; 
			}
			r++;
		}
		
		int rank = Y.rank(H); 
		//println("rank:"+noZeroes.size()+","+rank);
		if (noZeroes.size() > rank+1) return row;
		else return -1;
	}
	
	/**
     * print string s + "\n"
     * @param s
     */
    public void println(String s) {
    	if (Debug) A.print(s + "\n");
    }
    
    /**
     * print Matrix A | Y
     */
    private void printAY() {
    	if (Debug) {
    	    println("Matrix A | Y");
    		InvariantMatrix.print(A,Y,6,0);
    	}
    }
    
    /**
     * print Matrix Y 
     */
    private void printY() {
    	if (Debug) {
    		println("Matrix Y");
    		Y.print(4,0);
    	}
    }
    
    /**
     * enable/disable print debug information
     * @param debug true enable print debug information; false disable print debug information
     */
    public void setDebug(boolean debug) {
    	this.Debug = debug;
    }
    
    /**
     * @return  enable/disable print debug information
     */
    public boolean getDebug() {
    	return this.Debug;
    }
	
//	public static void main(String[] args) {
//         	   
//	}
}
