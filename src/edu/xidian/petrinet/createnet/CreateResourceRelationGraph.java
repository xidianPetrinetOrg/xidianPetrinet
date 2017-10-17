package edu.xidian.petrinet.createnet;

import java.util.HashMap;

import javax.swing.JFrame;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.shape.mxTokenToShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import edu.xidian.petrinet.S3PR.S3PR;

public class CreateResourceRelationGraph extends JFrame {
	
	private static mxGraph graph = new mxGraph();
	private static Object parent = graph.getDefaultParent();
	private static CreateResourceRelationGraph frame;
	static{
		graph.getModel().beginUpdate();
	}
	public static Object insertVertex(String Ri){
		Object v1 = graph.insertVertex(parent, null, 
				new mxTokenToShape(Ri,0), 20, 20, 30, 30,
				mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_TOKEN_ELLIPSE);
		return v1;
	}
	public static void insertEdge(Object vi,Object vj,String ti){
		graph.insertEdge(parent, null,ti,vi,vj);
	}
	public  void create(S3PR s3pr) {	
		HashMap<String, Object> map = new HashMap<>();
		int k = 1;
		Init();
		
		try {
			Object v1 = insertVertex("r1");
			Object v2= insertVertex("r2");
			Object v3 = insertVertex("r3");
			insertEdge(v1, v2, "t2");
			//graph.insertEdge(parent, null, "t2", v1, v2);
			graph.insertEdge(parent, null, "t3", v2, v3);
			graph.insertEdge(parent, null, "t3", v3, v1);
			graph.insertEdge(parent, null, "t3", v3, v1);
		} finally {
			graph.getModel().endUpdate();
		}
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		
		// layout
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.execute(graph.getDefaultParent());
		frame.validate();
	}
	private static void Init() {
		frame = new CreateResourceRelationGraph();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);
		
	}
	public static void main(String[] args) {
		//create(s3pr);
	}
	
	
}
