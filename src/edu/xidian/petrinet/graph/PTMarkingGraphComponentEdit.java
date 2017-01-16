package edu.xidian.petrinet.graph;

import javax.swing.JFrame;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxUndoManager;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;
import edu.xidian.petrinet.graph.PTMarkingGraphComponent.IMarkingGraphReady;

/**
 * PTNet表示的图形元素(组件)，带有编辑功能：图的朝向，顶点的显示位置。
 * 使用举例：
 <pre><code>
   JFrame frame = new JFrame("PTNet and MarkingGraph");
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.setContentPane(component.contentPane());
   frame.pack();
   frame.setVisible(true);
</code></pre>
 * @author JiangtaoDuan
 *
 */
@SuppressWarnings("serial")
public class PTMarkingGraphComponentEdit extends PTNetGraphComponentEdit {
    
	public PTMarkingGraphComponentEdit() {
		
	}
	
	public PTMarkingGraphComponentEdit(PTNet petriNet) throws ParameterException {
		//super(petriNet);
		Validate.notNull(petriNet);
		this.petriNet = petriNet;
		PTMarkingGraphComponent component = new PTMarkingGraphComponent(petriNet);
		IMarkingGraphReady ready = new IMarkingGraphReady() {

			@Override
			public void graph(boolean isReady) {
				if (isReady) {
					//new DisplayFrame(component, true); // 显示图形元素
					// undo相关
					undoManager = new mxUndoManager();
					
					try {
						component.initialize();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					visualGraph = component.getGraph();
					// 记录历史，为undo，redo做准备
					visualGraph.getModel().addListener(mxEvent.UNDO, undoHandler);
					visualGraph.getView().addListener(mxEvent.UNDO, undoHandler);
				    
			        createAction();
					
					addBindings();
					
//					try {
//						initialize();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
				    JFrame frame = new JFrame("PTNet and MarkingGraph");
			    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    	
			    	//Create and set up the content pane.
			    	//frame.setJMenuBar(ptgraph.createMenuBar());
			    	
			    	frame.setContentPane(contentPane());
			    	
			    	//Display the window.
			    	//frame.setSize(450, 260);
			    	frame.pack();
			    	frame.setVisible(true);
				}
			}
		};
	    component.markingGraphReady(ready);
	}
	

	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		PTMarkingGraphComponentEdit component = new PTMarkingGraphComponentEdit(ptnet);
//		try {
//			component.initialize();  // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
//	    new DisplayFrame(component,true);  // 显示图形元素
	    
//	    JFrame frame = new JFrame("PTNet and MarkingGraph");
//    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    	
//    	//Create and set up the content pane.
//    	//frame.setJMenuBar(ptgraph.createMenuBar());
//    	
//    	frame.setContentPane(component.contentPane());
//    	
//    	//Display the window.
//    	//frame.setSize(450, 260);
//    	frame.pack();
//    	frame.setVisible(true);
	}

}
