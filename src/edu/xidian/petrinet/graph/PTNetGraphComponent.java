package edu.xidian.petrinet.graph;


import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxTokenToShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;
/**
 * PTNet表示的图形元素，使用举例：
 * <pre> <code>
 	PTNet ptnet = CreatePetriNet.createPTnet1(); // 创建PTNet对象
	PTNetGraphComponent component = new PTNetGraphComponent(ptnet);
	try {
		component.initialize();
	} catch (Exception e) {
		e.printStackTrace();
	}	
    new DisplayFrame(component,true); // 显示图形元素
    </code></pre>
 * 
 * @author JiangTaoDuan
 *
 */
public class PTNetGraphComponent  extends JPanel {
	
	private static final long serialVersionUID = -6795020261692373388L;
	
	/** PTNet 对象，是本类的数据来源  */
	protected PTNet petriNet = null;

	/** 由petriNet成员，生成的相应图形对象  */
	protected mxGraph visualGraph = null;
	
	private JComponent graphPanel = null;
	
	private mxUndoManager undoManager;
	
	/** vertex of Place style */
	protected static final String PlaceStyle = "PlaceStyle"; 
	
	/** vertex of Transition style */
	protected static final String TransitionStyle = "TransitionStyle"; 
	
	/** width,height of vertex, default 30 */
	protected int vertexWidth = 20, vertexHeight = 20;
		
	/**
	 * key: vertex(Place/Transition) name, value: mxCell对象
	 * for create edge from vertices
	 */
	protected Map<String, mxCell> vertices = new HashMap<String, mxCell>(); 
	
	/** Graph布局 */
	protected mxHierarchicalLayout layout;
	
	protected Object parent;
	
	/** Graph布局朝向 */
	protected int layoutOrientation = SwingConstants.NORTH;	 
	
	/**
	 * 构造PTNet表示的图形元素
	 * @param petriNet  PTNet
	 * @throws ParameterException
	 */
	public PTNetGraphComponent(PTNet petriNet) throws ParameterException  {
		Validate.notNull(petriNet);
		this.petriNet = petriNet;
	}
	
	/**
	 * 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		setupVisualGraph();
		setLayout(new BorderLayout(20, 0));
		add(getGraphPanel(), BorderLayout.CENTER);
		setPreferredSize(getGraphPanel().getPreferredSize());
		
		// undo相关
		this.undoManager = new mxUndoManager();
		// 记录历史，为undo，redo做准备
		visualGraph.getModel().addListener(mxEvent.UNDO, undoHandler);
		visualGraph.getView().addListener(mxEvent.UNDO, undoHandler);
	}
	
	/**
	 * 由petriNet信息，装配visualGraph
	 * insertVextex，isertEdge
	 * @throws Exception
	 */
	protected void setupVisualGraph() throws Exception {	
		visualGraph = new mxGraph();
		parent = visualGraph.getDefaultParent();
		createPlaceStyle();       // vertex of Place style
		createTransitionStyle();  // vertex of Transition style
		setCellsAttribute();      // 设置cells属性

		visualGraph.getModel().beginUpdate();
		try{
			for(AbstractPNNode<PTFlowRelation> node: petriNet.getNodes()) {
				String vertexName = node.getName();    // 唯一
				String vertexLabel = node.getLabel();  // 可能不唯一，用于显示
				mxCell vertex = getVertexCell(parent, vertexName, vertexLabel);
				vertices.put(vertexName, vertex);
			}
			for(PTFlowRelation flow: petriNet.getFlowRelations()) {
				String source = flow.getSource().getName();
				String target = flow.getTarget().getName();	
				String id = source + "-" + target;
				// 有向弧的权值，缺省是1，不显示
				Integer constraint = flow.getConstraint();
				String edge = (constraint == 1) ? "" : constraint.toString();
				
				mxCell insertedEdge = (mxCell) visualGraph.insertEdge(parent, id, edge, vertices.get(source),vertices.get(target));
				Object[] cells = new Object[1];
				cells[0] = insertedEdge;
//				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff", cells);
//				visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP,cells); // 无效
//				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER,cells); // 无效
				//visualGraph.setCellStyles(mxConstants.STYLE_EDGE, elbowEdgeStyle;elbow=horizontalmxConstants.ALIGN_CENTER,cells); // 无效
				//visualGraph.setCellStyles(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW,cells); // 无效
				//visualGraph.setCellStyles(mxConstants.EDGESTYLE_ELBOW, mxConstants.ELBOW_HORIZONTAL,cells); // 无效
				//visualGraph.setCellStyles(mxConstants.EDGESTYLE_ELBOW, mxConstants.ELBOW_VERTICAL,cells); // 无效
		    }
	
		}
		finally{
			visualGraph.getModel().endUpdate();
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
	 * 子类可以重写该方法，定义顶点的形状，如"shape=doubleEllipse";
	 * @param graphVertex
	 * @return
	 */
	protected mxCell getVertexCell(Object parent, String vertexName, String vertexLabel) {
		// Place
		if (petriNet.getPlace(vertexName) != null) {
			return (mxCell) visualGraph.insertVertex(parent, vertexName, toShape(vertexLabel,vertexLabel), 0, 0, vertexWidth, vertexHeight, PlaceStyle);
		}
		//Transition
		return (mxCell) visualGraph.insertVertex(parent, vertexName, vertexLabel, 0, 0, vertexWidth, vertexHeight, TransitionStyle);
	}
	
	private JComponent getGraphPanel() {
		if(graphPanel == null){
			graphPanel = new mxGraphComponent(visualGraph);
		}
		return graphPanel;
	}
	
	/**
	 * undo handle
	 */
	private mxIEventListener undoHandler = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
		}
	};
	
	/**
	 * @return mxUndoManager对象
	 */
	public mxUndoManager getUndoManager() {
		return this.undoManager;
	}
	
	/**
	 * @return mxGraph对象，表示由PTNet生成的图形对象
	 */
	public mxGraph getGraph() {
		return this.visualGraph;
	}
	
	/** 
	 * 设置顶点的颜色
	 * @param color
	 * @param nodeNames
	 */
	public void setNodeColor(String color, String... nodeNames){
		Object[] cells = new Object[nodeNames.length];
		for(int i=0; i<nodeNames.length; i++)
			cells[i] = vertices.get(nodeNames[i]);
		visualGraph.setCellStyles(mxConstants.STYLE_FILLCOLOR, color, cells);
	}
	
	/**
	 * 获取 actual marking 的Places token number
	 * @param placeName
	 * @return token number
	 */
	private int getPlaceTokenNumber(String placeName) {
		PTMarking marking = petriNet.getMarking();
		Integer tokenNum = marking.get(placeName);
		if (tokenNum == null) tokenNum = 0;
		return tokenNum; 
	}
	
	/** 向顶点形状传递 place label(库所显示标识)和token number */
	private mxTokenToShape toShape(String placeName,String placeLabel) {
		int tokenNumber = getPlaceTokenNumber(placeName);	
		return new mxTokenToShape(placeLabel,tokenNumber);
	}
	
	/**
	 * vertex of place style
	 * @return styleName
	 */
	protected void createPlaceStyle() {
		// defaultVertex={shape=rectangle, fontColor=#774400, strokeColor=#6482B9, fillColor=#C3D9FF, align=center, verticalAlign=middle}
		// defaultEdge={endArrow=classic, shape=connector, fontColor=#446299, strokeColor=#6482B9, align=center, verticalAlign=middle}, 
		mxStylesheet stylesheet = visualGraph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_TOKEN_ELLIPSE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
		if (layoutOrientation == SwingConstants.NORTH || layoutOrientation == SwingConstants.SOUTH) {
			style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT); //the horizontal label position of vertices
			style.put(mxConstants.STYLE_SPACING_RIGHT, -10); // in pixels, added to the right side of a label in a vertex
		}
		else {
			style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the horizontal label position of vertices
			style.put(mxConstants.STYLE_SPACING_RIGHT, 0); // in pixels, added to the right side of a label in a vertex
			style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP); //the vertical label position of vertices
		}
		stylesheet.putCellStyle(PlaceStyle, style);
	}
	
	/**
	 * vertex of Transition style
	 * @return styleName
	 */
	protected void createTransitionStyle() {
		mxStylesheet stylesheet = visualGraph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
		if (layoutOrientation == SwingConstants.NORTH || layoutOrientation == SwingConstants.SOUTH) {
			style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT); //the horizontal label position of vertices
			style.put(mxConstants.STYLE_SPACING_LEFT, 0); // in pixels, added to the left side of a label in a vertex
			style.put(mxConstants.STYLE_SPACING_RIGHT, -10); // in pixels, added to the right side of a label in a vertex
			style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the vertical label position of vertices
			style.put(mxConstants.STYLE_SPACING_TOP, 0); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
			style.put(mxConstants.STYLE_SPACING_BOTTOM, 0); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
		}
		else {
			style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the horizontal label position of vertices
			style.put(mxConstants.STYLE_SPACING_LEFT, 0); // in pixels, added to the left side of a label in a vertex
			style.put(mxConstants.STYLE_SPACING_RIGHT, 0); // in pixels, added to the right side of a label in a vertex
			style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP); //the vertical label position of vertices
			style.put(mxConstants.STYLE_SPACING_TOP, 0); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
			style.put(mxConstants.STYLE_SPACING_BOTTOM, -10); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
		}
		stylesheet.putCellStyle(TransitionStyle, style);
	}
	
	/**
	 * 设置图形的一些属性，比如，不能删除cells; 不能编辑cells; 拖动边时，保证两端顶点是连接状态,不能脱接; cells是可以移动的，不能改变cells的大小;
	 * 不会出现悬挂边; Label是可以拖动的
	 */
	protected void setCellsAttribute() {
		visualGraph.setCellsDeletable(false);
		visualGraph.setCellsEditable(false);
		visualGraph.setCellsDisconnectable(false);  // 不会拖动边，而使两端顶点不连接
		visualGraph.setCellsMovable(true);
		//visualGraph.setCellsLocked(true);
		visualGraph.setCellsResizable(false);
		visualGraph.setAllowDanglingEdges(false); // 设置true: 鼠标掠过，按住左键，可以拖出一条悬挂边;	
		visualGraph.setVertexLabelsMovable(true); // Label是可以拖动的
		visualGraph.setEdgeLabelsMovable(true);
	}
	
	/**
	 * 根据Graph朝向，调整vertex label的显示位置
	 * @param orientation
	 */
	private void labelPosition(int orientation) {
		int i = 0;
		Object[] cells = new Object[petriNet.getNodes().size()];
		for(AbstractPNNode<PTFlowRelation> node: petriNet.getNodes()) {
			String vertexName = node.getName();    
		    cells[i] = vertices.get(vertexName);
		    i++;
		}
		if (layoutOrientation == SwingConstants.NORTH || layoutOrientation == SwingConstants.SOUTH) {
			visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT, cells); //the horizontal label position of vertices
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_LEFT, "0", cells); // in pixels, added to the left side of a label in a vertex
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_RIGHT, "-10", cells); // in pixels, added to the right side of a label in a vertex
			visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER, cells); //the vertical label position of vertices
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_TOP, "0" , cells); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_BOTTOM, "0" , cells); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
		}
		else {
			visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER, cells); //the horizontal label position of vertices
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_LEFT, "0", cells); // in pixels, added to the left side of a label in a vertex
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_RIGHT, "0", cells); // in pixels, added to the right side of a label in a vertex
			visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP, cells); //the vertical label position of vertices
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_TOP, "0" , cells); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
			visualGraph.setCellStyles(mxConstants.STYLE_SPACING_BOTTOM, "-10" , cells); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
		}
	}
	
	/** 设置Graph布局方向（朝向，东、西、南、北），缺省：北
	 * @param orientation: SwingConstants.NORTH，SOUTH，WEST，EAST
	 */
	public void setOrientation(int orientation) {
		this.layoutOrientation = orientation;
		
		// 调整vertex label的显示位置
		labelPosition(orientation);
				
		// 首先，把在setupVisualGraph()或上一次setOrientation（）中平移图形的坐标还原过来，即设置translate为point(0,0)
		visualGraph.getView().setTranslate(new mxPoint(0, 0));
		
		layout.setOrientation(orientation);
		// 计算
		layout.execute(parent);
		
		// 保证视图之外的Label能看见,平移图形
		mxRectangle rec = visualGraph.getGraphBounds(); // 包含图形及其Label的边界
		visualGraph.getView().setTranslate(new mxPoint(-rec.getX(), -rec.getY())); // 还原回来，设置为point(0,0)即可
	}
	
	
	/**
	 * 改变选中vertex的label的显示位置
	 * @param position SwingConstants.LEFT,SwingConstants.RIGHT，SwingConstants.TOP，SwingConstants.BOTTOM
	 */
	public void changeLabelPosition(int position) {
		
		// 首先，还原别的地方引起的平移图形，即设置translate为point(0,0)
	    visualGraph.getView().setTranslate(new mxPoint(0, 0));
	    
	    // 如果人为移动过该顶点的label（鼠标选取label,拖动）,恢复其offset。 否则，由于offset的存在，使得本函数中改变的label相对于顶点的相对位置，表现不尽人意
	    Object[] vertices = visualGraph.getSelectionCells();
	    for (Object vertex: vertices) {
		    if (((mxCell)vertex).isVertex()) {
		    	mxGeometry geo = ((mxCell)vertex).getGeometry();
		    	if (geo.getOffset() != null) { // 该顶点的label被人为移动过（鼠标选取label,拖动）
		    		geo.setOffset(null);  // 恢复其offset
		    	}
		    }
	    }
	    
		switch(position) {
			case SwingConstants.LEFT:
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT); //the horizontal label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_LEFT, "0"); // in pixels, added to the left side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_RIGHT, "-10"); // in pixels, added to the right side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the vertical label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_TOP, "0"); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_BOTTOM, "0"); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
				break;
			case SwingConstants.RIGHT:
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_RIGHT); //the horizontal label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_LEFT, "-10"); // in pixels, added to the left side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_RIGHT, "0"); // in pixels, added to the right side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the vertical label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_TOP, "0"); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_BOTTOM, "0"); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
				break;
			case SwingConstants.TOP:
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the horizontal label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_LEFT, "0"); // in pixels, added to the left side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_RIGHT, "0"); // in pixels, added to the right side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP); //the vertical label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_TOP, "0"); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_BOTTOM, "-10"); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
				break;
			case SwingConstants.BOTTOM:
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER); //the horizontal label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_LEFT, "0"); // in pixels, added to the left side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_RIGHT, "0"); // in pixels, added to the right side of a label in a vertex
				visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM); //the vertical label position of vertices
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_TOP, "-10"); // The value represents the spacing, in pixels, added to the top side of a label in a vertex (style applies to vertices only).
				visualGraph.setCellStyles(mxConstants.STYLE_SPACING_BOTTOM, "0"); // The value represents the spacing, in pixels, added to the bottom side of a label in a vertex (style applies to vertices only).
				break;
		}
		// 保证视图之外的Label能看见,平移图形
		mxRectangle rec = visualGraph.getGraphBounds(); // 包含图形及其Label的边界
		visualGraph.getView().setTranslate(new mxPoint(-rec.getX(), -rec.getY())); // 还原回来，设置为point(0,0)即可
	}
	
	
	/**
	 * get width of vertex,{@link #vertexWidth}
	 */
	public int getVertexWidth() {
		return this.vertexWidth;
	}

	/**
	 * set width of vertex,{@link #vertexWidth}
	 * @param width
	 */
	public void setVertexWidth(int width) {
		this.vertexWidth = width;
	}

	/**
	 * get height of vertex,{@link #vertexWidth}
	 */
	public int getVertexHeight() {
		return this.vertexHeight;
	}

	/**
	 * set height of vertex,{@link #vertexWidth}
	 * @param height
	 */
	public void setVertexHeight(int height) {
		this.vertexHeight = height;
	}
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		PTNetGraphComponent component = new PTNetGraphComponent(ptnet);
		try {
			component.initialize();  // 创建PTNet对象
		} catch (Exception e) {
			e.printStackTrace();
		}	
	    new DisplayFrame(component,true);  // 显示图形元素
	}

}
