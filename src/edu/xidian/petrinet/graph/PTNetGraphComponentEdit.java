package edu.xidian.petrinet.graph;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;

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
public class PTNetGraphComponentEdit extends PTNetGraphComponent {

	/** 图的朝向,如果改变，请注意在createMenuBar()中，修改快捷键，现在是：N,W,S,E */
    //private String[] orientationStr = {"NORTH","WEST","SOUTH","EAST"};
    
    /** 
     * 整合所有快捷键,
     * 除了undo，redo是CTRL组合键，其它按键助记符是Alt的组合键;
     * menu与toolBar同级，不能有重复; 不同menu下的item可以重复.
    */
	private static final Map<String,Integer> keyCode = new HashMap<String,Integer>() {
    	{    		
    		// graph的布局朝向
    		put("north",KeyEvent.VK_N); 
    		put("west",KeyEvent.VK_W); 
    		put("south",KeyEvent.VK_S); 
    		put("east",KeyEvent.VK_E); 
    		
    		// 顶点label的显示位置
    		put("left",KeyEvent.VK_L);  
    		put("right",KeyEvent.VK_R); 
    		put("top",KeyEvent.VK_T); 
    		put("bottom",KeyEvent.VK_B);
    		
       		// undo,redo
    		put("undo",KeyEvent.VK_Z); // CTRL-Z
    		put("redo",KeyEvent.VK_Y); // CTRL-Y
    	}
    };
    
    /** 编辑功能Actions, 图的朝向，顶点Label的显示位置; undo,redo */
    private Action northAction, westAction, southAction, eastAction, 
                   labelLeftAction, labelRightAction, labelTopAction, labelBottomAction,
                   undoHistoryAction, redoHistoryAction;
    
	public PTNetGraphComponentEdit(PTNet petriNet) throws ParameterException {
		super(petriNet);
		
		try {
			initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    createAction();
		
		addBindings();
	}
	
	/**
	 * toolBar,ptnetGraphComponent<br>
	 * 使用BorderLayout布局，其五个区域如下：<br>
	 * BorderLayout.PAGE_START<br>
	 * BorderLayout.LINE_START，BorderLayout.CENTER，BorderLayout.LINE_END<br>
	 * BorderLayout.PAGE_END<br>
	 *  
	 * @return
	 */
    public Container contentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		
		JToolBar toolBar = createToolBar();
		contentPane.add(toolBar, BorderLayout.PAGE_START);
		
		// the ptnetGraphComponent
		JScrollPane netPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		netPane.setViewportView(this);
		contentPane.add(netPane, BorderLayout.CENTER);
								                  
        return contentPane;
	}
	
	/** 
	 * 生成工具条 ，顶点Label显示位置，undo，redo
	 */
	private JToolBar createToolBar() {
		JButton button = null;

		// Create the toolBar.
		JToolBar toolBar = new JToolBar();
		
		///////////////// Graph orientation
		button = new JButton("North");
		button.setAction(northAction);
		toolBar.add(button);
		
		button = new JButton("West");
		button.setAction(westAction);
		toolBar.add(button);
		
		button = new JButton("South");
		button.setAction(southAction);
		toolBar.add(button);
		
		button = new JButton("East");
		button.setAction(eastAction);
		toolBar.add(button);
		
		toolBar.addSeparator();
	    
		///////////////// Label position
		// labelLeftAction button
		button = new JButton(labelLeftAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);

		// labelRightAction button
		button = new JButton(labelRightAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);

		// labelTopAction button
		button = new JButton(labelTopAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		// labelBottomAction button
		button = new JButton(labelBottomAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		toolBar.addSeparator();
		
		//////////// undo,redo
		button = new JButton(undoHistoryAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		button = new JButton(redoHistoryAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		return toolBar;
	}
	

	
	/**
	 * Create the actions shared by the toolBar and menu.
	 * toolBar和menu共用的动作在这里生成,
	 * 按键助记符是Alt的组合键
	 * undo,redo快捷按键（CTRL-Z/CTRL-Y）在addBindings()中定义
	 */
	private void createAction() {

		// 图的朝向
		northAction = new OrientationAction("North", "图的朝向: North", new Integer(keyCode.get("north")));
		southAction = new OrientationAction("South", "图的朝向: South", new Integer(keyCode.get("south")));
		westAction = new OrientationAction("West", "图的朝向: West", new Integer(keyCode.get("west")));
		eastAction = new OrientationAction("East", "图的朝向: East", new Integer(keyCode.get("east")));
		
		// 顶点label位置
		labelLeftAction = new LabelLeftAction("Left", createNavigationIcon("left"), "left label",
				new Integer(keyCode.get("left")));

		labelRightAction = new LabelRightAction("Right", createNavigationIcon("right"), "right label",
				new Integer(keyCode.get("right")));

		labelTopAction = new LabelTopAction("Top", createNavigationIcon("top"), "top label",
				new Integer(keyCode.get("top")));

		labelBottomAction = new LabelBottomAction("Bottom", createNavigationIcon("bottom"), "bottom label",
				new Integer(keyCode.get("bottom")));
		
		// Undo,Redo
		undoHistoryAction = new HistoryAction("Undo", createNavigationIcon("undo"), "undo", true);
		redoHistoryAction = new HistoryAction("Redo", createNavigationIcon("redo"), "redo", false);
		
		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	private static ImageIcon createNavigationIcon(String imageName) {
		String imgLocation = "images/" + imageName + ".gif";
		java.net.URL imageURL = PTNetGraphComponentEdit.class.getResource(imgLocation);

		if (imageURL == null) {
			System.err.println("Resource not found: " + imgLocation);
			return null;
		} else {
			return new ImageIcon(imageURL);
		}
	}
	
	/**
	 * Add a couple of emacs key bindings for undo,redo.
	 * undo,redo快捷按键（CTRL-Z/CTRL-Y）在addBindings()中定义
	 * toolBar和menu共用的动作在这里生成,按键助记符是Alt的组合键,在createAction()中定义
	 */
    private void addBindings() {
    	// The component has the keyboard focus
    	//InputMap inputMap = getInputMap();  // 无效
    	
    	// The component has the keyboard focus
    	//InputMap inputMap = getInputMap( JComponent.WHEN_FOCUSED ); // 无效，相当于无参的形式，组件获得焦点时
    	
    	// The component contains (or is) the component that has the focus. 
    	InputMap inputMap = getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT); // 有效，当父组件获得焦点时
    	
    	// The component's window either has the focus or contains the component that has the focus. 
    	//InputMap inputMap = getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW); // 有效，尽量不用，因为窗口的子组件太多
    	
    	ActionMap actionMap = getActionMap();
    	
    	actionMap.put("undo", undoHistoryAction);
    	actionMap.put("redo", redoHistoryAction);
    	
    	//CTRL-Z to go undo
    	KeyStroke key = KeyStroke.getKeyStroke(keyCode.get("undo"), Event.CTRL_MASK);
    	inputMap.put(key, "undo");
    	
    	//CTRL-Y to go undo
    	key = KeyStroke.getKeyStroke(keyCode.get("redo"), Event.CTRL_MASK);
    	inputMap.put(key, "redo");
    }
	
	/** 图的朝向 */
	public class OrientationAction extends AbstractAction {
		// 图的朝向
		private int orientation = SwingConstants.NORTH;
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, "North",or,"South","West","East", default is "North"
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public OrientationAction(String name, String desc, Integer mnemonic) {
			super(name);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
			
			orientation = SwingConstants.NORTH;
	    	if (name.equalsIgnoreCase("WEST")) orientation = SwingConstants.WEST;
	    	else if (name.equalsIgnoreCase("EAST")) orientation = SwingConstants.EAST;
	    	else if (name.equalsIgnoreCase("SOUTH")) orientation = SwingConstants.SOUTH;
		}
		
		public void actionPerformed(ActionEvent e) {
	    	setOrientation(orientation); // 改变图的朝向
		}
	}
	
	/** Label显示在顶点的左边 */
	public class LabelLeftAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelLeftAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			changeLabelPosition(SwingConstants.LEFT);
		}
	}
	

	/** Label显示在顶点的右边 */
	public class LabelRightAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelRightAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			 changeLabelPosition(SwingConstants.RIGHT);
		}
	}
	

	/** Label显示在顶点的上边 */
	public class LabelTopAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelTopAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			changeLabelPosition(SwingConstants.TOP);
		}
	}
	
	/** Label显示在顶点的下边 */
	public class LabelBottomAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelBottomAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			changeLabelPosition(SwingConstants.BOTTOM);
		}
	}
	
	/** 历史动作 */
	public class HistoryAction extends AbstractAction {
		protected boolean undo;

		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param undo true,undo; false,redo
		 */
		public HistoryAction(String name, ImageIcon icon, String desc,boolean undo) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			//putValue(MNEMONIC_KEY, mnemonic); // 在addBindings()定义了control-z and control-y
			this.undo = undo;
		}
		
		public HistoryAction(boolean undo) {
			this.undo = undo;
		}

		public void actionPerformed(ActionEvent e) {
			if (visualGraph != null) {
				if (undo) {
					undoManager.undo();
				} else {
					undoManager.redo();
				}
			}
		}
	}
	
	/** Keeps the selection in sync with the command history */
	private mxIEventListener undoHandler = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
			visualGraph.setSelectionCells(visualGraph.getSelectionCellsForChanges(changes));
		}
	};

	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		PTNetGraphComponentEdit component = new PTNetGraphComponentEdit(ptnet);
//		try {
//			component.initialize();  // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
//	    new DisplayFrame(component,true);  // 显示图形元素
	    
	    JFrame frame = new JFrame("PTNet and MarkingGraph");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	//Create and set up the content pane.
    	//frame.setJMenuBar(ptgraph.createMenuBar());
    	
    	frame.setContentPane(component.contentPane());
    	
    	//Display the window.
    	//frame.setSize(450, 260);
    	frame.pack();
    	frame.setVisible(true);
	}

}
