package edu.xidian.petrinet;

import java.util.ArrayList;

import edu.xidian.math.InvariantMatrix;

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
	 * @param C the PN(Petri Net) incidence matrix
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
     * Annul all columns k of U (A | Y) in which pk or vk = 1;
     */
	public void compute1() {
		print("Matrix A | Y");
		InvariantMatrix.print(A,Y,4,0);

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
	
	public void compute2() {
		int pk,vk; // the number of positives and negatives in column k of C;
		ArrayList<Integer> positives,negatives;
		for(int j=0;j<A.getColumnDimension();j++) {
		  positives = A.getPositiveList(j);
		  negatives = A.getNegativeList(j);
		  pk = positives.size();
		  vk = negatives.size();
		}
	}
}
