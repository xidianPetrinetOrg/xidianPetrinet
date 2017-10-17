package edu.xidian.petrinet.createnet;

import java.util.ArrayList;
import java.util.Collection;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.S3PR.S3PR;

public class CreatePetriNetByFile extends S3PR {

	private handlePNTFile handleFile;
	protected static HandleResourceFile resourceFile;

	private String path = null;
	private PTNet ptNet = null;
	protected static S3PR s3pr = null;
	protected static int[][] incidenceMatrix;

	private Collection<String> P0 = new ArrayList<String>();
	private Collection<String> PA = new ArrayList<String>();
	private Collection<String> PR = new ArrayList<String>();
	
	public CreatePetriNetByFile(){
	}
	
	public HandleResourceFile getResourceFile() {
		return resourceFile;
	}

	public void setResourceFile(HandleResourceFile resourceFile) {
		this.resourceFile = resourceFile;
	}
	
	/**
	 * 根据pnt文件来创建网 path = C:\Users\xd\Desktop\txt1.pnt
	 */
	public PTNet CreatePetriNetByPnt(String path) {
		System.out.println(path);
		handleFile = new handlePNTFile();
		ptNet = handleFile.readPntFile(path);
		incidenceMatrix = handleFile.getIncidenceMatrix();
		this.ptNet = ptNet;
		return ptNet;
	}

	/**
	 * 根据资源流文件来创建网 path = C:\Users\xd\Desktop\ResourceRelationFile1.txt
	 */
	public PTNet CreatePetriNetBytxt(String path) {
		resourceFile = new HandleResourceFile();
		ptNet = resourceFile.ReadResRelFile(path);
		resourceFile.equipmenet(P0, PA, PR);
		this.ptNet = ptNet;
		s3pr = CreateS3PR();
		return ptNet;
	}
	
	//接口，创建生成S3PR网
	public S3PR CreateS3PR() {
		s3pr = new S3PR("S3PR_Resource", ptNet, P0, PA, PR);
		System.out.println("s3PR=="+s3pr);
		return s3pr;
	}
	
	//根据网生成初始资源有向图
	public void CreateResourceGraphByResourceRelation(S3PR s3pr)
	{
		//new CreateResourceRelationGraph().create(s3pr); 
		CreateRRG.Create(s3pr);
	}
	
	public static void main(String[] args) {
		CreatePetriNetByFile createPetriNetByFile = new CreatePetriNetByFile();
		
		//path = C:\Users\xd\Desktop\txt1.pnt
		createPetriNetByFile.CreatePetriNetByPnt("C:\\Users\\xd\\Desktop\\txt1.pnt");
		
		createPetriNetByFile.CreatePetriNetBytxt("C:\\Users\\xd\\Desktop\\ResourceRelationFile.txt");
		//createPetriNetByFile.CreatePetriNetBytxt("C:\\Users\\xd\\Desktop\\ResourceRelationFile1.txt");
		S3PR createS3PR = createPetriNetByFile.CreateS3PR();
		createPetriNetByFile.CreateResourceGraphByResourceRelation(createS3PR);
		
	}
}
