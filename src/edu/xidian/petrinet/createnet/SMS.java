package edu.xidian.petrinet.createnet;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.math.InvariantMatrix;
import edu.xidian.petrinet.S3PR.S3PR;
import edu.xidian.petrinet.Utils.myUtils;

public class SMS extends S3PR{

	protected  static S3PR s3pr;
	protected  static List<Collection<PTPlace>> Siphons ;
	protected  static List<Collection<PTPlace>> SiphonComs;
	protected  static InvariantMatrix CMatrix;
	
	
	/*public ArrayList<String> CalculateSMS()
	{
		
		ArrayList<String> arrayList = new ArrayList<>();
		//由资源关系流文件生成pnt文件
		//PNT pnt = new PNT();
		//C:/Users/xd/Desktop/ResourceRelationFile1.txt
		//C:/Users/xd/Desktop/测试文件/ResourceRelationFile1.txt
		String txtPath ="C:/Users/xd/Desktop/测试文件/ResourceRelationFile1.txt";
		String pntPath = "D:/xidianPetriNet.pnt";
		pnt.createPNT(txtPath, pntPath);
		//由pnt文件生成S3PR网对象，计算信标
		PetriNet net = new PetriNet();
		
		S3PR s3pr = net.getS3PR(pntPath);
		//LS3PR网
		s3pr.deleteN(true);
		//DeleteN(s3pr, true);
		Siphons = s3pr.getSiphons();
		SiphonComs = s3pr.getSiphonComs();
		InvariantMatrix cMatrix = s3pr.CMatrix();
		
		String  stringSiphons = CaculateSiphons(Siphons);
	    String  stringSiphonComs = CalculateSiphoncoms(SiphonComs);
	    //System.out.println("C-Matrix:");
	    //myUtils.MatrixToFormatString("C-Matrix:",cMatrix.getArrayCopy());
	    //cMatrix.print(2, 0);
	    
	    System.out.println("result:\n"+ stringSiphons);
	    System.out.println("result:\n"+ stringSiphonComs);
	    String stringCmatrix = myUtils.MatrixToFormatString("C-Matrix",cMatrix.getArrayCopy());
	    System.out.println(stringCmatrix);
	    
	    arrayList.add(stringSiphons);
	    arrayList.add(stringSiphonComs);
	    arrayList.add(stringCmatrix);
	    
		return arrayList;
		
	}*/
	
	public ArrayList<String> calculateSMS(S3PR s3pr)
	{
		
		ArrayList<String> arrayList = new ArrayList<>();
		//由资源关系流文件生成pnt文件
		//PNT pnt = new PNT();
		//C:/Users/xd/Desktop/ResourceRelationFile1.txt
		//C:/Users/xd/Desktop/测试文件/ResourceRelationFile1.txt
		String txtPath ="C:/Users/xd/Desktop/测试文件/ResourceRelationFile1.txt";
		String pntPath = "D:/xidianPetriNet.pnt";
		//pnt.createPNT(txtPath, pntPath);
		//由pnt文件生成S3PR网对象，计算信标
		CreateS3prByPNT net = new CreateS3prByPNT();
		
		//S3PR s3pr = net.getS3PR(pntPath);
		//LS3PR网
		s3pr.deleteN(true);
		//DeleteN(s3pr, true);
		Siphons = s3pr.getSiphons();
		SiphonComs = s3pr.getSiphonComs();
		InvariantMatrix cMatrix = s3pr.CMatrix();
		
		String  stringSiphons = CaculateSiphons(Siphons);
	    String  stringSiphonComs = CalculateSiphoncoms(SiphonComs);
	    //System.out.println("C-Matrix:");
	    //myUtils.MatrixToFormatString("C-Matrix:",cMatrix.getArrayCopy());
	    //cMatrix.print(2, 0);
	    
	    System.out.println("result:\n"+ stringSiphons);
	    System.out.println("result:\n"+ stringSiphonComs);
	    String stringCmatrix = myUtils.MatrixToFormatString("C-Matrix",cMatrix.getArrayCopy());
	    System.out.println(stringCmatrix);
	    
	    arrayList.add(stringSiphons);
	    arrayList.add(stringSiphonComs);
	    arrayList.add(stringCmatrix);
	    
		return arrayList;
		
	}
	private String CalculateSiphoncoms(List<Collection<PTPlace>> SiphonComs) {
		
		StringBuffer sBuffer = new StringBuffer();
		int i = 1;
		for (Collection<PTPlace> siphonCom: SiphonComs) {
			
			String s1 = "SiphonComs[" + i + "] = ";
			String s2 = siphonCom+"";
			String comb = s1+s2;
			sBuffer.append(comb);
			sBuffer.append("\n");
			
			//printPNNodes("SiphonComs[" + i + "] = ", siphonCom);
			i++;
		}
		//System.out.println("C_+_+_+");
		return sBuffer.toString();
	}

	private String CaculateSiphons(List<Collection<PTPlace>> Siphons) {
		int i = 1;
		StringBuffer sBuffer = new StringBuffer();
		//System.out.println("6661");
		for (Collection<PTPlace> siphon: Siphons) {
			
			String s1 = "siphon[" + i + "] = ";
			String s2 = siphon+"";
			String comb = s1+s2;
			sBuffer.append(comb);
			sBuffer.append("\n");
			
			//printPNNodes("Siphons[" + i + "]    = ", siphon);
			i++;
		}
		//System.out.println("666");
		return sBuffer.toString();
	}

	public static void DeleteN(S3PR s3pr, boolean verbose){
		s3pr.deleteN(verbose);
		int i = 1;
		for (Collection<PTPlace> siphon: Siphons) {
			s3pr.printPNNodes("Siphons[" + i + "]    = ", siphon);
			i++;
		}
	}

	/**
	 * 严格极小信标的的计算
	 * @param args
	 */
	public static void main(String[] args) {
		
		//String txtpath ="C:/Users/xd/Desktop/测试文件/ResourceRelationFile1.txt";
		//new SMS().CalculateSMS();
		//由资源关系流文件生成pnt文件
		//PNT pnt = new PNT();
		//C:/Users/xd/Desktop/ResourceRelationFile1.txt
		//C:/Users/xd/Desktop/测试文件/ResourceRelationFile1.txt
		//String txtPath ="C:/Users/xd/Desktop/测试文件/ResourceRelationFile.txt";
		String pntPath = "D:/xidianPetriNet.pnt";
		//C:/Users/xd/Desktop/测试文件/txt2.pnt
		String pntpath1 = "C:/Users/xd/Desktop/测试文件/c.pnt";
		
		//pnt.createPNT(txtPath, pntPath);
		
		//由pnt文件生成S3PR网对象，计算信标
		//PetriNet net = new PetriNet();
		
		S3PR s3pr = new CreateS3prByPNT().getS3PR(pntpath1);
		//LS3PR网
		s3pr.deleteN(true);
		
		Siphons = s3pr.getSiphons();
		
		SiphonComs = s3pr.getSiphonComs();
		
		//S3PR网
		//s3pr.deleteN_S3PR(true);
		
		
		
		
		
	}
}
