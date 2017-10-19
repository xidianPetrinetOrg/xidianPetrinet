package edu.xidian.petrinet.CalculateSMS;


import edu.xidian.petrinet.S3PR.S3PR;

public class SMS {
	/**
	 * 严格极小信标的的计算
	 * @param args
	 */
	public static void main(String[] args) {
		//由资源关系流文件生成pnt文件
		PNT pnt = new PNT();
		//C:/Users/xd/Desktop/ResourceRelationFile1.txt
		String txtPath ="C:/Users/xd/Desktop/ResourceRelationFile1.txt";
		String pntPath = "C://Users//xd//Desktop//net.pnt";
		pnt.createPNT(txtPath, pntPath);
		
		//由pnt文件生成S3PR网对象，计算信标
		PetriNet net = new PetriNet();
		S3PR s3pr = net.getS3PR(pntPath);
		//LS3PR网
		s3pr.deleteN(true);
		//S3PR网
		//s3pr.deleteN_S3PR(true);
	}
}
