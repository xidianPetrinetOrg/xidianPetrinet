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
	 * 
	 * @param C the PN(Petri Net) incidence matrix for computing P-Invariants; <br>
	 *          the incidence matrix's transpose for computing T-Invariants;
	 */
    public ComputeInvariant(InvariantMatrix C) {
    	incidenceRowDimension = C.getRowDimension(); 
    	incidenceColumnDimension = C.getColumnDimension();
		A = C;
		Y = InvariantMatrix.identity(incidenceRowDimension, incidenceRowDimension);
	}
    
    public void print(String s) {
    	A.print(s);
    }

    /**
     * Annul all columns k of U (A | Y) in which pk and vk = 1;
     */
	public void compute1() {
		//print("Matrix A | Y");
		//InvariantMatrix.print(A,Y,4,0);

		int a[] = new int[3]; // a[0],a[1],a[2],唯一+ve行,唯一-ve行，列
		int Coefficient[]; // 调整系数
		
		// 符合算法的基数条件,仅有一个+ve和-ve的列, 线性组合+ve行到-ve行，删除+ve行.
		while (A.cardinalityOne1(a) == 0) {
			// +ve所在的行，加到相应-ve所在的行
			Coefficient = A.linearlyCombine(a[0], a[1], a[2]);
			Y.linearlyCombine(a[0], a[1], Coefficient);
			
			A = A.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			Y = Y.eliminateRow(a[0]);
		
			print("唯一的+ve行,唯一的-ve的行,列：" + a[0] + "," + a[1] + "," + a[2]);
			InvariantMatrix.print(A,Y,4,0);
		}
    }
	
	/**
     * Annul all columns k of U (A | Y) in which pk or vk = 1;
     */
	public void compute2() {
		//print("Matrix A | Y");
		//InvariantMatrix.print(A,Y,4,0);

		int a[] = new int[2]; // a[0],a[1]唯一的+ve或-ve所在的行和列
		int Coefficient[]; // 调整系数
		
		int m = A.getRowDimension(); // A的行数
		
		// 符合算法的基数条件,仅有一个+ve或-ve的列, 线性组合到其它行，删除此行.
		while (A.cardinalityOne(a) == 0) {
			int element = A.get(a[0], a[1]); // 唯一的+ve或-ve
			// +ve所在的行，加到相应负元素所在的行; -ve所在的行，加到相应正元素所在的行
			for (int i = 0; i < m; i++) {
				if (a[0] == i) continue;  // 不与本行(+ve或-ve所在的行)线性组合
				if (A.get(i, a[1]) == 0) continue; // 不与0元素所在的行线性组合
				
				Coefficient = A.linearlyCombine(a[0], i, a[1]);
				Y.linearlyCombine(a[0], i, Coefficient);
			}
			A = A.eliminateRow(a[0]); // 删除唯一的+ve或-ve所在的行
			Y = Y.eliminateRow(a[0]);
			m--; // 减掉一行
			print("唯一的+ve/-ve的行列，值：" + a[0] + "," + a[1] + "," + element);
			InvariantMatrix.print(A,Y,4,0);
		}
    }
	
	/** 
	 * F(k) = pk*vk-(pk+vk)
	 * 删除F(k)<0或pk*vk最小的列k
	 */
	public void compute3() {
		//print("Matrix A | Y");
		//InvariantMatrix.print(A,Y,4,0);
		int Coefficient[] = new int[2]; // 调整系数
		ArrayList<Integer> positives = new ArrayList<Integer>();
		ArrayList<Integer> negatives = new ArrayList<Integer>();
		int annelCol = AnnelCol(positives, negatives);
		print("annelColumn:" + annelCol + "\n");
		print("positives:" + positives + "\n");
		print("negatives:" + negatives + "\n");
		
		for (int positiveRow : positives ) {
			for (int negativeRow : negatives) {
				A = A.AppendRowLinearlyCombine(positiveRow, negativeRow, annelCol, Coefficient);
				Y = Y.AppendRowLinearlyCombine(positiveRow, negativeRow, Coefficient);
			}
			InvariantMatrix.print(A,Y,4,0);
		}
		
		// 删除annelCol列的非0行
		positives.addAll(negatives);  // 并集
		A = A.eliminateRows(positives);
		Y = Y.eliminateRows(positives);
		
		InvariantMatrix.print(A,Y,4,0);
	}
	
	public void compute() {
		print("Matrix A | Y");
		InvariantMatrix.print(A,Y,4,0);
		while(!A.isZeroMatrix()) {
			// Annul all columns k of U (A | Y) in which pk and vk = 1;
			print("compute1()===\n");
			compute1();
			print("affter compute1() Matrix A | Y");
			InvariantMatrix.print(A,Y,4,0);
			// Annul all columns k of U (A | Y) in which pk or vk = 1;
			print("compute2()===\n");
			compute2();
			print("affter compute2() Matrix A | Y");
			InvariantMatrix.print(A,Y,4,0);
			// F(k) = pk*vk-(pk+vk); 删除 F(k)<0或pk*vk最小的列k
			print("compute3()===\n");
			compute3();
			print("affter compute3() Matrix A | Y");
			InvariantMatrix.print(A,Y,4,0);
		}
	}
	
	/**
	 * The number of new rows to add, by combining pairs of rows, is pk*vk. 
	 * The pk+vk rows used to annul column k are eliminated at the end of the iteration. 
	 * The expansion factor is the net number of new rows added in annulling column k: 
	 * F(k) = pk*vk-(pk+vk)
	 * 生成新行数：pk*vk; 线性组合后，删除的行数：pk+vk
	 * @param positives 返回选定列中正元素(+ve)index列表
	 * @param negatives 返回选定列中负元素(-ve)index列表
	 * @return
	 * 因此,有如下策略，选取要删除的列：<br>
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
		int col = 0, minCol = 0;
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
		A.positiveNegativeList(positives, negatives, minCol);
		
		return minCol;
	}
	
	public static void main(String[] args) {
         	   
	}
}
