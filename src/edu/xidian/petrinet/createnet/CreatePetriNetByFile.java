package edu.xidian.petrinet.createnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.petrinet.S3PR.S3PR;
/**
 * 根据文件创建petri网对象
 *
 */
public class CreatePetriNetByFile extends S3PR {

	private handlePNTFile handleFile;
	protected static HandleResourceFile resourceFile;

	private String path = null;
	private PTNet ptNet = null;
	protected static S3PR s3pr = null;
	protected static int[][] incidenceMatrix;
	private boolean flag = false;

	private Collection<String> P0 = new ArrayList<String>();
	private Collection<String> PA = new ArrayList<String>();
	private Collection<String> PR = new ArrayList<String>();
	
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
		
		System.out.println("路径："+path);
		handleFile = new handlePNTFile();
		ptNet = handleFile.readPntFile(path);
		
		
		incidenceMatrix = handleFile.getIncidenceMatrix();
		this.ptNet = ptNet;
		this.path = path;
		
		s3pr = CreateS3PR();
		return ptNet;
	}

	/**
	 * 根据资源流文件来创建网 path = C:\Users\xd\Desktop\ResourceRelationFile1.txt
	 */
	public PTNet CreatePetriNetBytxt(String path) {
		
		flag = true;
		resourceFile = new HandleResourceFile();
		ptNet = resourceFile.ReadResRelFile(path);
		resourceFile.equipmenet(P0, PA, PR);
		this.ptNet = ptNet;
		s3pr = CreateS3PR();
		return ptNet;
	}
	
	//接口，创建生成S3PR网
	public S3PR CreateS3PR() {
		if(flag){
			s3pr = new S3PR("S3PR_Resource", ptNet, P0, PA, PR);
		}else{
			//System.out.println("lujing"+path);
			s3pr = new CreateS3prByPNT().getS3PR(path);
		}
		
		return s3pr;
	}
	
	//根据网生成初始资源有向图
	public void CreateResourceGraphByResourceRelation(S3PR s3pr)
	{
		CreateRRG.Create(s3pr);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<PTPlace> getPreAndPre(PTPlace p){
		Collection<PTPlace> prePre = new HashSet<>();
		Collection<AbstractPNNode<PTFlowRelation>> nodes = p.getParents();
		for (AbstractPNNode node: nodes) {
			prePre.addAll(node.getParents()); // H(r)必然是places，不是transitions，因此Hr的类型说明正确
		}
		return prePre;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<PTPlace> getPostPost(PTPlace p){
		Collection<PTPlace> postPost = new HashSet<>();
		Collection<AbstractPNNode<PTFlowRelation>> nodes = p.getChildren();
		for (AbstractPNNode node: nodes) {
			postPost.addAll(node.getChildren()); // H(r)必然是places，不是transitions，因此Hr的类型说明正确
		}
		return postPost;
	}
}