package edu.xidian.petrinet.createnet;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.shape.mxTokenToShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.petrinet.S3PR.S3PR;

/**
 * 图形化显示资源关系流<br>
 *
 */
public class CreateRRG extends JFrame
{
	private static final long serialVersionUID = 5483575278950465873L;
	
	private static mxGraph graph = new mxGraph();
	private static Object parent = graph.getDefaultParent();
	private static CreateRRG frame;
	static{
		graph.getModel().beginUpdate();
	}
	
	public CreateRRG()
	{
		super("ResourceRelationGraph");
	}
	public Object insertVertex(String Ri){
		Object v1 = graph.insertVertex(parent, null, 
				new mxTokenToShape(Ri,0), 50, 50, 40, 40,
				mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_TOKEN_ELLIPSE);
		return v1;
	}
	public void insertEdge(Object vi,Object vj,String ti){
		graph.insertEdge(parent, null,ti,vi,vj);
	}
	public void test2() {
		try
		{
			Object v1 = insertVertex("r1");
			Object v2= insertVertex("r2");
			Object v3 = insertVertex("r3");
			
			insertEdge(v1, v2, "t2");
			insertEdge(v2, v3, "t3");
			insertEdge(v3, v1, "t3");
			insertEdge(v3, v1, "t3");
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		
		// layout
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.execute(graph.getDefaultParent());
	}

	public static void Create()
	{
		frame = new CreateRRG();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.setSize(300,300);
		frame.setVisible(true);
		frame.test2();
		frame.validate(); 
		
		
		frame.addWindowListener(new WindowAdapter() {
		   public void windowClosing(WindowEvent e) {
		   int value=JOptionPane.showConfirmDialog(null, "确定要关闭吗？");
		      if (value==JOptionPane.OK_OPTION) {
		          frame.dispose();
		    	  //System.exit(0);
		      }
		   }
	   });
	}
	
	
	public static void main(String[] args) {
		Create();
	}
	public static void Create(S3PR s3pr) {
		frame = new CreateRRG();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setVisible(true);
		frame.CreateS3PRGraph(s3pr);
		frame.validate();
		
	}
	/**
	 *   初始资源有向图算法：
	 *   输入：S3PR网
	 *   输出：初始资源有向图
	 * 1  根据S3PR网获取资源库所集；
	 * 2 while（遍历所有的资源库所集）do  
	 * 3   	获取当前遍历到的库所的parent变迁； 
	 * 4    	while(遍历除当前的资源库所的库所集)do
	 * 5        	获取当前遍历到的库所的children变迁
	 * 6            if（parent变迁   ∩ children变迁）
	 * 7                                            获取公共的变迁
	 * 8            end if
	 * 9         end while
	 * 10 end while
	 * 
	 * 
	 * @param s3pr
	 */
	private void CreateS3PRGraph(S3PR s3pr) {	
		HashMap<String, Object> map = new HashMap<>();
		int k = 1;
		try
		{
			Collection<PTPlace> pr2 = s3pr.getPR(); 
			for (PTPlace ptPlace : pr2) {
				Object insertVertex = insertVertex(ptPlace.getName());
				String name = ptPlace.getName();
				map.put(name, insertVertex);
				k++;
			}
			for (PTPlace ptPlace : pr2) {
				PTPlace place = ptPlace;
				Set<AbstractPNNode<PTFlowRelation>> parents = ptPlace.getParents();
				for (PTPlace ptPlace1 : pr2) {
					if(!ptPlace.equals(ptPlace1)){
						//System.out.println("OtherPlace: "+ ptPlace1);
						Set<AbstractPNNode<PTFlowRelation>> children = ptPlace1.getChildren();
						Set<AbstractPNNode<PTFlowRelation>> temp = new HashSet<>();
						temp.addAll(parents);
						temp.retainAll(children); //求取交集
						if(!temp.isEmpty()){
							for (AbstractPNNode<PTFlowRelation> abstractPNNode : temp) {
								String key1 = ptPlace1.getName();
								Object value1 = map.get(key1);
								String key = ptPlace.getName();
								Object value = map.get(key);
								insertEdge(value1, value, abstractPNNode.getName().toString());
							}
							
						}
					}
				}
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		// layout
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.execute(graph.getDefaultParent());
	}
}
