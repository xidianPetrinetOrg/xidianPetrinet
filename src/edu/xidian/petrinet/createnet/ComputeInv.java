package edu.xidian.petrinet.createnet;

import edu.xidian.math.InvariantMatrix;
import edu.xidian.petrinet.ComputeInvariant;
import edu.xidian.petrinet.Utils.myUtils;

public class ComputeInv extends ComputeInvariant{

	public ComputeInv(InvariantMatrix C) {
		super(C);
	}
	//根据关联矩阵计算P-InvariantMatrix
	public static String Cal_P_Inv(int[][] IM){
		InvariantMatrix incidenceM = new InvariantMatrix(IM);   
		ComputeInvariant  computeInvariant = new ComputeInv(incidenceM);
		computeInvariant.compute();  
		InvariantMatrix invariants = computeInvariant.getInvariants();
		int[][] arrayCopy = invariants.getArrayCopy();
		String matrixToFormatString = myUtils.MatrixToFormatString("P-InvariantMatrix", arrayCopy);
		System.out.println(matrixToFormatString);
		return matrixToFormatString;
	}
	//根据关联矩阵计算P-InvariantMatrix
	public static String Cal_T_Inv(int[][] IM){
		InvariantMatrix incidenceM = new InvariantMatrix(IM);   
		ComputeInvariant  computeInvariant = new ComputeInv(incidenceM.transpose());
		computeInvariant.compute();  
		InvariantMatrix invariants = computeInvariant.getInvariants();
		int[][] arrayCopy = invariants.getArrayCopy();
		String matrixToFormatString = myUtils.MatrixToFormatString("T-InvariantMatrix", arrayCopy);
		System.out.println(matrixToFormatString);
		return matrixToFormatString;
	}
}
