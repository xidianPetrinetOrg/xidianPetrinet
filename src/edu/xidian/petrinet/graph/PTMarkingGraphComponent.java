package edu.xidian.petrinet.graph;

import java.util.Hashtable;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.sepia.mg.pt.PTMarkingGraph;
import de.uni.freiburg.iig.telematik.sepia.mg.pt.PTMarkingGraphRelation;
import de.uni.freiburg.iig.telematik.sepia.mg.pt.PTMarkingGraphState;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg.MGConstruction;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg.MarkingGraphException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;

public class PTMarkingGraphComponent extends PTNetGraphComponent {

	private static final long serialVersionUID = -7271096146707469553L;
	
	protected PTMarkingGraph markingGraph = null;
	
	/** source顶点的颜色，Source vertexes have no incoming, but at least one outgoing edge. */
	protected static final String sourceNodeColor = "#f6f5b1";
	
	/** Drain顶点的颜色，Drain vertexes have no outgoing, but at least one incoming edge. */
	protected static final String drainNodeColor = "#f2ddf8";
	
	/**
	 * 构造PTNet表示的图形元素
	 * @param petriNet  PTNet
	 * @throws ParameterException
	 */
	public PTMarkingGraphComponent(PTNet petriNet) {
		super(petriNet);
	}
	
	public void setMarkingGraph() {
		try {
			markingGraph = (PTMarkingGraph) MGConstruction.buildMarkingGraph(petriNet);
			int VertexNum = markingGraph.getVertexCount();
			System.out.println("PTMarkingGraph: Vertex Number(states) = " + VertexNum);
			System.out.println(markingGraph);
		} catch (MarkingGraphException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * 由petriNet信息，装配visualGraph
	 * insertVextex，isertEdge
	 * @throws Exception
	 */
	protected void setupVisualGraph() throws Exception {
		visualGraph = new mxGraph();
		parent = visualGraph.getDefaultParent();
		createPlaceStyle();       // 这里顶点为状态
		setCellsAttribute();      // 设置cells属性

		visualGraph.getModel().beginUpdate();
		try{
			for( PTMarkingGraphState node: markingGraph.getVertices()) {
				String vertexName = node.getName();    // 唯一
				String vertexLabel = vertexName;       // 这里，显示的名称就是name
				mxCell vertex = getVertexCell(parent, vertexName, vertexLabel);
				vertices.put(vertexName, vertex);
			}
			for(PTMarkingGraphRelation flow: markingGraph.getEdges()) {
				String source = flow.getSource().getName();
				String target = flow.getTarget().getName();	
				String id = source + "-" + target;
				String edge = flow.getEvent().getName();
				
				mxCell insertedEdge = (mxCell) visualGraph.insertEdge(parent, id, edge, vertices.get(source),vertices.get(target));
				Object[] cells = new Object[1];
				cells[0] = insertedEdge;
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff", cells);
		    }
	
		}
		finally{
			visualGraph.getModel().endUpdate();
		}
		
		
		// 设置source顶点的颜色，Source vertexes have no incoming, but at least one outgoing edge.
		for(PTMarkingGraphState node: markingGraph.getSources()) {
			setNodeColor(sourceNodeColor, node.getName());
		}
		
		// 设置Drain顶点的颜色，Drain vertexes have no outgoing, but at least one incoming edge.
		for(PTMarkingGraphState node: markingGraph.getDrains()) {
			setNodeColor(drainNodeColor, node.getName());
		}
		
		// Graph布局
		layout = new mxHierarchicalLayout(visualGraph);
		// cell的边界是否包含Label，false，利于对其关系仅与几何形状有关，比较整齐。
		// 但是，边界处的label有可能看不见，如，label在vertex的左边时，最左边的label就可能看不见。由下面的平移图形来弥补
		layout.setUseBoundingBox(false);
		// 缺省布局方向
		layout.setOrientation(layoutOrientation);
		// 计算
		layout.execute(parent);
		
		// 保证视图之外的Label能看见，平移图形
		mxRectangle rec = visualGraph.getGraphBounds();  // 包含图形及其Label的边界
		visualGraph.getView().setTranslate(new mxPoint(-rec.getX(), -rec.getY())); // 还原回来，设置为point(0,0)即可
		
	}
	
	/**
	 * 顶点的shape,缺省是"shape=ellipse",
	 * 终止状态是"shape=doubleEllipse";
	 * @param graphVertex
	 * @return
	 */
	@Override
	protected mxCell getVertexCell(Object parent, String vertexName, String vertexLabel) {
		
		if (markingGraph.isEndState(vertexName)) {
			return (mxCell) visualGraph.insertVertex(parent, vertexName, vertexLabel, 0, 0, vertexWidth, vertexHeight, PlaceStyle + ";" +
					"shape=doubleEllipse");
		}
		else {
			return (mxCell) visualGraph.insertVertex(parent, vertexName, vertexLabel, 0, 0, vertexWidth, vertexHeight, PlaceStyle);
		}
	}
	
	/**
	 * 这里设置顶点的Stylesheet
	 * @return styleName
	 */
	@Override
	protected void createPlaceStyle() {
		
		mxStylesheet stylesheet = visualGraph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
		style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the horizontal label position of vertices
		style.put(mxConstants.STYLE_SPACING_RIGHT, 0); // in pixels, added to the right side of a label in a vertex
		style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the vertical label position of vertices
		
		stylesheet.putCellStyle(PlaceStyle, style);
	}
	
	/**
	 * 设置图形的一些属性，比如，不能删除cells; 不能编辑cells; 拖动边时，保证两端顶点是连接状态,不能脱接; cells是可以移动的，不能改变cells的大小;
	 * 不会出现悬挂边; Label【不】可以拖动的
	 */
	@Override
	protected void setCellsAttribute() {
		super.setCellsAttribute();
		visualGraph.setVertexLabelsMovable(false); // Label不可以拖动的
	}
	
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		PTMarkingGraphComponent component = new PTMarkingGraphComponent(ptnet);
		component.setVertexWidth(30);
		component.setVertexHeight(30);
		try {
			component.setMarkingGraph();
			component.initialize();  // 创建PTNet对象
		} catch (Exception e) {
			e.printStackTrace();
		}	
	    new DisplayFrame(component,true);  // 显示图形元素
	}
	
}
	
