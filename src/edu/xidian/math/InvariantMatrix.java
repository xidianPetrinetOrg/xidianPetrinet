package edu.xidian.math;

public class InvariantMatrix extends Matrix {

	/**
	 * 
	 */
	private static final long serialVersionUID = 954754350690844234L;

	public InvariantMatrix(int[][] A) {
		super(A);
		// TODO Auto-generated constructor stub
	}

	/**
     * Transform a matrix to obtain the minimal generating set of vectors.
     * 列对应的不变式，极小向量
     * @return A matrix containing the invariant vectors.
     */
    public Matrix Tinvariants() {
    	
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
        
        InvariantMatrix invariant = new InvariantMatrix(incidence);
        invariant.print("incidence:");
        invariant.print(4, 0);
        invariant.print("incidence transpose:");
        invariant.transpose().print(4, 0);
	}

}
