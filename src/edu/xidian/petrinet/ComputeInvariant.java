package edu.xidian.petrinet;

import java.util.ArrayList;

import edu.xidian.math.InvariantMatrix;

/**
 * Computation of p-semiflows
   C:    the PN incidence matrix(dimensions m*n)
   I:    identity matrix(dimensions m*m)
   U(k)  matrix [A(k)|Y(k)] where
         for k=0 A(k)=C and Y(k)=I
         for k>0 A(k) is the matrix resulting from annulling k columns of C
                 Y(k) is the matrix which memorizes the coefficients of linear combinations of rows of C
   U(-) the matrix which contains the rows of U k-1 whose k-th column is negative
   U(+) the malrix which contains the rows of U k-1 whose k-th column is positive
   pk,vk:the number of positives and negatives in column k of A(k)
   
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
	 * upper bound of r matrix
	 * Initialization is the PN(Petri Net) incidence matrix，
	 * incidence element[i][j] != 0, B[i][j] = 1, true; 
	 * incidence element[i][j] == 0, B[i][j] = 0, false; 
	 */
	private InvariantMatrix B;
	
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
		
		/**
		 * upper bound of r matrix
		 * Initialization is the PN(Petri Net) incidence matrix，
		 * incidence element[i][j] != 0, B[i][j] = 1, true; 
		 * incidence element[i][j] == 0, B[i][j] = 0, false; 
		 */
		int e;
		B = new InvariantMatrix(incidenceRowDimension, incidenceColumnDimension);
		for(int i = 0; i < incidenceRowDimension; i++) {
			for(int j = 0; j < incidenceColumnDimension;j++) {
				e = (C.get(i, j)!= 0) ? 1 : 0;
				//e = (C.get(i, j)== 0) ? 1 : 0;
				B.set(i, j, e);
			}
		}
		
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
			println("Annul column [" + a[2] + "]");
			// +ve所在的行，加到相应-ve所在的行
			Coefficient = A.linearlyCombine(a[0], a[1], a[2]);
			Y.linearlyCombine(a[0], a[1], Coefficient);
			B.logicalUnion(a[0], a[1]);
			
			A = A.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			Y = Y.eliminateRow(a[0]);
			B = B.eliminateRow(a[0]);
            
			println("唯一的+ve,唯一的-ve：[" + a[0] + "]row, add to [" + a[1] + "]row,at col[" + a[2] + "],eliminate row[" + a[0] + "]");
			printAYB();
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
			println("Annul column [" + a[1] + "]");
			int num = 0;
			for (int i = 0; i < m; i++) {
				if (a[0] == i) continue;  // 不与本行(+ve或-ve所在的行)线性组合
				if (A.get(i, a[1]) == 0) continue; // 不与0元素所在的行线性组合
				
				Coefficient = A.linearlyCombine(a[0], i, a[1]);
				Y.linearlyCombine(a[0], i, Coefficient);
				B.logicalUnion(a[0], i);
				num++;
				println("唯一的+ve或-ve：[" + a[0] + "]row, add to [" + i + "]row,at col[" + a[1] + "]");
			}
			A = A.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			Y = Y.eliminateRow(a[0]);
			B = B.eliminateRow(a[0]);
			m--; // 减掉一行
			println("eliminate row ["+a[0]+ "]，modify invariants number: " + num);
			printAYB();
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
		for (int positiveRow : positives ) {
			for (int negativeRow : negatives) {
				A = A.AppendRowLinearlyCombine(positiveRow, negativeRow, annelCol, Coefficient);
				Y = Y.AppendRowLinearlyCombine(positiveRow, negativeRow, Coefficient);
				B = B.AppendRowlogicalUnion(positiveRow, negativeRow);
				println("Append row is linearlyCombine of row["+positiveRow + "] and row[" +negativeRow+"]:");
				printAYB();
			}
		}
		
		// 删除annelCol列的非0行
		positives.addAll(negatives);  // 并集
		A = A.eliminateRows(positives);
		Y = Y.eliminateRows(positives);
		B = B.eliminateRows(positives);
		
		println("eliminate rows: " + positives);
		printAYB();
	}
	
	public void compute() {
		printAYB();
		while(!A.isZeroMatrix()) {
			// Annul all columns k of U (A | Y) in which pk and vk = 1;
			println("compute1()===");
			compute1();
			println("affter compute1()");
			printAYB();
			
			// Annul all columns k of U (A | Y) in which pk or vk = 1;
			println("compute2()===");
			compute2();
			println("affter compute2()");
			printAYB();
			
			// expansion factor: F(k) = pk*vk-(pk+vk)
			// Annul columns:  F(k)<0 or if there is none, select column with lowest pk*vk
			println("compute3()===");
			compute3();
			println("affter compute3()");
			printAYB();
		}
		
		////////////////////////////////////////////
		// 规范化矩阵Minimal Support matrix Y
		/** Y is called standardized iff
		(1) y1,...,yq 属于整数integers（0,1,2,3，...）
		(2) for the first yi不等于0，yi > 0 and
		(3) y cannot be divided by an element k属于正整数(positive integers), k > 1 without destroying (1)
		**/
		////////////////////////////////////////////
		println("规范化矩阵Minimal Support matrix Y===");
		// 全部由负元素或0组成的行，取正，即负行取正
		while (true) {
			int row = Y.allNegativeOrZeroRow();
			if (row == -1)
				break;
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
		
		//Find a rows with non-minimal support.
		println("n="+Y.findNonMinimalRow());
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
     * print string s + "\n"
     * @param s
     */
    public void println(String s) {
    	if (Debug) A.print(s + "\n");
    }
    
    /**
     * print Matrix A | Y, B 
     */
    private void printAYB() {
    	println("Matrix A | Y, B");
    	if (Debug) {
    		InvariantMatrix.print(A,Y,6,0);
    		B.print(4,0);
    	}
    }
    
    /**
     * print Matrix Y 
     */
    private void printY() {
    	println("Matrix Y");
    	if (Debug) {
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
	
	public static void main(String[] args) {
         	   
	}
}
