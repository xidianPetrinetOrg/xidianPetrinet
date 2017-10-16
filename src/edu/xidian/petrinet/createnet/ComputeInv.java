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
	
	public static void main(String[] args) {
		 int incidence[][] = {
                 /** t1  t2  t3  t4  t5  t6  t7  t8  t9  t10 **/
          /** p1 */ { 1,  0,  0,  0, -1,  0,  0,  0,  0,  0},
          /** p2 */ { 0,  0,  0, -1,  1,  0,  0,  0,  0,  0},
          /** p3 */ { 1,  0,  0,  0,  0, -1,  0,  0,  0,  0},
          /** p4 */ {-1,  0,  0,  1,  0,  0,  0,  0,  0,  0},
          /** p5 */ {-1,  0,  0,  0,  0,  0,  1,  0,  0,  0},
          /** p6 */ { 0,  1,  0,  0,  0,  0, -1,  0,  0,  0},
          /** p7 */ { 0, -1,  0,  0,  0,  0,  0,  0,  1,  0},
          /** p8 */ { 0,  1,  0,  0,  0,  0,  0, -1,  0,  0},
          /** p9 */ { 0,  0, -1,  0,  0,  0,  0,  1,  0,  0},
          /** p10*/ { 0,  0,  1,  0,  0,  0,  0,  0, -1,  0},
          /** p11*/ { 0,  0,  1,  0,  0,  0,  0,  0,  0, -1},
          /** p12*/ { 0,  0,  0,  0,  0,  0,  0,  0, -1,  1},
          /** p13*/ { 0,  0,  0, -1,  0,  0,  0,  0,  0,  1},
          /** p14*/ { 0, -1,  0,  0,  0,  1,  0,  0,  0,  0}
         };
		 ComputeInv.Cal_P_Inv(incidence);
		 int incidence1[][] = {
	  		        /** t1  t2  t3  t4  t5  t6  t7  t8  t9 **/
	       /** p1 */  { 0,  0,  1,  0,  0,  0, -1,  0,  1},
	       /** p2 */  { 1, -1,  0,  0,  0,  0,  0,  0,  0},
	       /** p3 */  { 0,  1, -1,  0,  0,  0,  0,  0,  0},
	       /** p4 */  { 0,  0,  0, -1,  0,  1,  0,  0,  0},
	       /** p5 */  { 0,  0,  0,  1, -1,  0,  0,  0,  0},
	       /** p6 */  { 0,  0,  0,  0,  1, -1,  0,  0,  0},
	       /** p7 */  {-1,  1,  0,  0, -1,  1,  0,  0,  0},
	       /** p8 */  { 0, -1,  1, -1,  1,  0,  0,  0,  0},
	       /** p9 */  {-1,  0,  0,  0,  0,  0,  1, -1,  0},
	       /** p10*/  { 0,  0,  0,  0,  0,  0,  0,  1, -1},
	       /** p11*/  { 0,  0,  0,  0,  0,  0,  0, -1,  1},
	       /** p12*/  { 1,  0,  0,  0,  0,  0, -1,  1,  0}};
		 ComputeInv.Cal_T_Inv(incidence1);
	}
}
