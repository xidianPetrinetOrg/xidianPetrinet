package edu.xidian.petrinet.createnet;

import java.util.ArrayList;
import java.util.Collection;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.S3PR.S3PR;

//根据文件创建petri网：可以采用pnt文件，也可以采用资源关系流文件
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
		
	/*	//////////////////////////////////////////////
		System.out.println("++++++++p0++++++++");
		for (String string : P0) {
			System.out.print(string + " ");
		}
		System.out.println();
		System.out.println("++++++++pA++++++++");
		for (String string : PA) {
			System.out.print(string + " ");
		}
		System.out.println();
		System.out.println("++++++++pR++++++++");
		for (String string : PR) {
			System.out.print(string + " ");
		}
		System.out.println();
		/////////////////////////////////////////////
*/		
		this.ptNet = ptNet;
		s3pr = CreateS3PR();
		return ptNet;
	}
	
	//接口，创建生成S3PR网
	public S3PR CreateS3PR() {
		System.out.println("wuuwuuwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
		s3pr = new S3PR("S3PR_Resource", ptNet, P0, PA, PR);
		System.out.println("s3PR=="+s3pr);
		System.out.println("wuuwuuwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
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
		/*Collection<PTPlace> pr2 = createS3PR.getPR();
		 * for (PTPlace ptPlace : pr2) {
			System.out.println("==============================================");
			System.out.println("curentPlace: "+ ptPlace.getName());
			PTPlace place = ptPlace;
			Set<AbstractPNNode<PTFlowRelation>> parents = ptPlace.getParents();
			
			for (PTPlace ptPlace1 : pr2) {
				if(!ptPlace.equals(ptPlace1)){
					System.out.println("OtherPlace: "+ ptPlace1);
					Set<AbstractPNNode<PTFlowRelation>> children = ptPlace1.getChildren();
					Set<AbstractPNNode<PTFlowRelation>> temp = new HashSet<>();
					temp.addAll(parents);
					temp.retainAll(children); //求取交集
					if(!temp.isEmpty()){
						System.out.println(ptPlace+"《-----"+ptPlace1 + "的中间变迁");
						for (AbstractPNNode<PTFlowRelation> abstractPNNode : temp) {
							System.out.println(abstractPNNode);
						}
					}
					
				}
			}
				
				
			}
			
			Set<AbstractPNNode<PTFlowRelation>> children = ptPlace.getChildren();
			
		
			
		    for (AbstractPNNode<PTFlowRelation> abstractPNNode : parents) {
				
		    	System.out.println("---"+abstractPNNode);
				for (AbstractPNNode<PTFlowRelation> abstractPNNode1 : children) {
					System.out.println("++"+abstractPNNode1);
				}
				
			}
			System.out.println("==============================================");
			
			
		}*/
		
		/*System.out.println("================hr==================");
		for (PTPlace ptPlace : pr2) {
			System.out.println("==============================================");
			System.out.println("curentPlace: "+ ptPlace);
			
			Collection<PTPlace> hr = createS3PR.getHr(ptPlace);
			for (PTPlace ptPlace2 : hr) {
				System.out.println(ptPlace2);
			}
		}
		System.out.println("================hr==================");*/
	
	
}
